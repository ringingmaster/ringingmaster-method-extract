package com.ringingmaster.extraction;

import com.cccbr.generated.method.CollectionType;
import com.cccbr.generated.method.MethodSetType;
import com.cccbr.generated.method.MethodType;
import com.cccbr.generated.method.SymmetryType;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotationList;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

public class CentralCouncilMethodExtractor implements MethodExtractor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String CCBR_XML = "/allmeths.xml";

	@Override
	public Stream<SerializableNotation> extractNotationsToStream() {
		return extractNotations().getSerializableNotation().stream();
	}

	@Override
	public SerializableNotationList extractNotations() {

		int unimportedMethodCount = 0;

		final SerializableNotationList serializableNotationList = new SerializableNotationList();
		final List<SerializableNotation> notations = serializableNotationList.getSerializableNotation();

		try {
			final JAXBContext jc = JAXBContext.newInstance("com.cccbr.generated.method");

			final Unmarshaller unmarshaller = jc.createUnmarshaller();

			InputStream ccbrStream = CentralCouncilMethodExtractor.class.getResourceAsStream(CCBR_XML);
			Preconditions.checkNotNull(ccbrStream);

			final JAXBElement<CollectionType> unmarshallled = (JAXBElement<CollectionType>) unmarshaller.unmarshal(ccbrStream);

			final CollectionType collection = unmarshallled.getValue();

			final List<MethodSetType> methodSets = collection.getMethodSet();

			for (final MethodSetType methodSet : methodSets) {
				final BigInteger stage = methodSet.getProperties().getStage();
				final List<MethodType> methods = methodSet.getMethod();
				final List<SymmetryType> symmetryTypes = methodSet.getProperties().getSymmetry();
				final boolean palindromic = symmetryTypes.contains(SymmetryType.PALINDROMIC);
				final BigInteger leadLength = methodSet.getProperties().getLengthOfLead();

				for (final MethodType method : methods) {
					final SerializableNotation notation = extractNotationFromMethodSet(method, stage, palindromic, leadLength, unimportedMethodCount);
					if (notation != null) {
						notations.add(notation);
					}
					else {
						unimportedMethodCount++;
					}
				}
			}

			log.info("Extracted [" + notations.size() + "]. Methods not imported [" + unimportedMethodCount + "]");

		} catch (final JAXBException e) {
			log.error("Exception extracting methods from allmethods.xml", e);
		}

		return serializableNotationList;
	}

	private SerializableNotation extractNotationFromMethodSet(final MethodType method, final BigInteger stage, boolean palindromic, BigInteger leadLength, final int unimportedMethodCount) {

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
			log.debug("[{}.{}] Not extracting [{}] as stage [" + stage + "]", unimportedMethodCount, id, titleOriginal);
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

		final SerializableNotation notation = new SerializableNotation();
		notation.setName(title);
		notation.setStage(numberOfBells.getBellCount());

		// are we palindromic AND folded?
		final String[] split = notationShorthand.split(",");
		if (palindromic && notationShorthand.contains(",")) {

			notation.setFoldedPalindrome(true);
			notation.setNotation(split[0]);

			if (split.length > 1) {
				notation.setNotation2(split[1]);
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
			notation.setNotation(notationShorthand);
		}


		notation.setLeadHead(leadHead);

		if (method.getLengthOfLead() != null) {
			leadLength = method.getLengthOfLead();
		}
		notation.setLeadLength(leadLength.intValue());

		return notation;
	}
}