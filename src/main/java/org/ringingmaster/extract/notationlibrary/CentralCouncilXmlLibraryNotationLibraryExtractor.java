package org.ringingmaster.extract.notationlibrary;


import com.google.common.base.Preconditions;
import org.ringingmaster.engine.NumberOfBells;
import org.ringingmaster.persist.generated.v1.LibraryNotationPersist;
import org.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.cccbr.methods.schemas._2007._05.methods.CollectionType;
import uk.org.cccbr.methods.schemas._2007._05.methods.MethodSetType;
import uk.org.cccbr.methods.schemas._2007._05.methods.MethodType;
import uk.org.cccbr.methods.schemas._2007._05.methods.SymmetryType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Central Council XML library format extract.
 */
public class CentralCouncilXmlLibraryNotationLibraryExtractor implements NotationLibraryExtractor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Path libraryLocation;
	public  CentralCouncilXmlLibraryNotationLibraryExtractor(Path inputLibraryLocation) {
		this.libraryLocation = inputLibraryLocation;
	}

	@Override
	public NotationLibraryPersist extractNotationLibrary() {

		log.info("Reading Central Council library from [{}]", libraryLocation);

		int unimportedMethodCount = 0;

		final NotationLibraryPersist notationLibrary = new NotationLibraryPersist();
		final List<LibraryNotationPersist> persistableNotations = notationLibrary.getNotation();

		try {
			final JAXBContext jc = JAXBContext.newInstance("uk.org.cccbr.methods.schemas._2007._05.methods");

			final Unmarshaller unmarshaller = jc.createUnmarshaller();


			InputStream ccbrStream = new FileInputStream(libraryLocation.toString());
			Preconditions.checkNotNull(ccbrStream);

			@SuppressWarnings("unchecked")
			final JAXBElement<CollectionType> unmarshallled = (JAXBElement<CollectionType>) unmarshaller.unmarshal(ccbrStream);

			final CollectionType collection = unmarshallled.getValue();

            collection.getDecisionsYear();
            notationLibrary.setNotes(
                    "Collection Name [" + Objects.toString(collection.getCollectionName()) + "] " +
                    "Notes [" + Objects.toString(collection.getNotes().getContent() ) + "] " +
                    "Creation Date [" + Objects.toString(collection.getDate()) + "] " +
                    "Decision Year [" + Objects.toString(collection.getDecisionsYear() + "] ")
            );

            final List<MethodSetType> methodSets = collection.getMethodSet();


			for (final MethodSetType methodSet : methodSets) {
				final BigInteger stage = methodSet.getProperties().getStage();
				final List<MethodType> methods = methodSet.getMethod();
				final List<SymmetryType> symmetryTypes = methodSet.getProperties().getSymmetry();
				final boolean palindromic = symmetryTypes.contains(SymmetryType.PALINDROMIC);
				final BigInteger leadLength = methodSet.getProperties().getLengthOfLead();

				for (final MethodType method : methods) {
					final LibraryNotationPersist notationType = extractNotationFromMethodSet(method, stage, palindromic, leadLength, unimportedMethodCount);
					if (notationType != null) {
						persistableNotations.add(notationType);
					}
					else {
						unimportedMethodCount++;
					}
				}
			}

			log.info("Extracted [" + persistableNotations.size() + "]. Methods not imported [" + unimportedMethodCount + "]");

		} catch (final JAXBException e) {
			log.error("Exception extracting methods from allmethods.xml", e);
		} catch (FileNotFoundException e) {
			log.error("Cant find file", e);
		}

		return notationLibrary;
	}

	private LibraryNotationPersist extractNotationFromMethodSet(final MethodType method, final BigInteger stage, boolean palindromic, BigInteger leadLength, final int unimportedMethodCount) {

		final NumberOfBells numberOfBells = NumberOfBells.valueOf(stage.intValue());
		method.getName().getValue();
		final String titleOriginal = method.getTitle().getValue();
		String title = method.getTitle().getValue().trim();
		final String id = method.getId();
		final String notationShorthand = method.getNotation().getValue();
		final String leadHead = (method.getLeadHeadCode() != null)?method.getLeadHeadCode():method.getLeadHead();

		// do we have a method based palindromic set
		final List<SymmetryType> symmetryTypes = method.getSymmetry();
		if (symmetryTypes.contains(SymmetryType.PALINDROMIC)) {
			palindromic = true;
		}

		if (numberOfBells == null) {
			log.debug("[{}.{}] Not extracting [{}] as stage [{}]", unimportedMethodCount, id, titleOriginal, stage);
			return null;
		}

		if (!palindromic && notationShorthand.contains(",")) {
			log.error("[{}.{}] Not extracting [{}] as not palindromic but contains comma for folded", unimportedMethodCount, id, titleOriginal);
			return null;
		}

		//remove the number of bells from the title - will take up less room on the database.
		int replacedNumberOfBellsInTitle = 0;
		for (final NumberOfBells nobTest : NumberOfBells.values()) {
			final String nobName = nobTest.getName();
			if (title.indexOf(nobName, title.length() - nobName.length()) != -1) {
				title = title.replace(nobName, "").trim();
				replacedNumberOfBellsInTitle++;
			}
		}

		if (replacedNumberOfBellsInTitle != 1) {
			log.error("[{}.{}] Not extracting [{}] as replaced [{}] numberOfBells names in title [{}]", unimportedMethodCount, id, titleOriginal, replacedNumberOfBellsInTitle, title );
			return null;
		}

		final LibraryNotationPersist persistableNotation = new LibraryNotationPersist();
		persistableNotation.setName(title);
		persistableNotation.setNumberOfWorkingBells(numberOfBells.getBellCount());

		// are we palindromic AND folded?
		final String[] split = notationShorthand.split(",");
		if (palindromic && notationShorthand.contains(",")) {

			persistableNotation.setFoldedPalindrome(true);
			persistableNotation.setNotation(split[0]);

			if (split.length > 1) {
				persistableNotation.setNotation2(split[1]);
			}

			if (split.length > 2) {
				log.error("[{}.{}] Not extracting [{}] as palindromic but contain [{}] comma(s).", unimportedMethodCount, id, titleOriginal, split.length - 1);
				return null;
			}

		} else {
			if (split.length != 1) {
				log.error("[{}.{}] Not extracting [{}] as not palindromic but contain [{}] comma(s).", unimportedMethodCount, id, titleOriginal, split.length-1);
				return null;
			}
			persistableNotation.setNotation(notationShorthand);
		}


		persistableNotation.setLeadHead(leadHead);

		if (method.getLengthOfLead() != null) {
			leadLength = method.getLengthOfLead();
		}
		persistableNotation.setLeadLength(leadLength.intValue());

		return persistableNotation;
	}
}