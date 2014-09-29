package com.ringingmaster.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.cccbr.generated.method.CollectionType;
import com.cccbr.generated.method.MethodSetType;
import com.cccbr.generated.method.MethodType;
import com.cccbr.generated.method.SymmetryType;
import com.concurrentperformance.ringingmaster.engine.NumberOfBells;
import com.concurrentperformance.ringingmaster.generated.notation.persist.ObjectFactory;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotationList;

/**
 * Builds the compressed method data
 * 
 * @author Stephen Lake
 */
public class MethodExtraxtor {
	private static Logger log = Logger.getLogger(MethodExtraxtor.class);

	private static final String CCBR_XML = "./resources/allmeths.xml";
	private static final String ALL_NOTATIONS_DATA_OUTPUT = "../android-ui/assets/all_notations.xml.deflated";

	private int unimportedMethodCount = 0;


	public static void main(final String[] args) {
		new MethodExtraxtor().run();
	}

	private void run() {
		final SerializableNotationList notations = extractNotations();
		writeNotations(notations);
		final SerializableNotationList notationsRead = readNotations();
		final boolean match = notations.equals(notationsRead);
		if (match) {
			log.info("Round trip persistance success");
		}
		else{

			log.error("Round trip persistance fail - THIS IS FAILING BECAUSE WE CANT COMPARE TEH JAX OBJECTS");
		}
	}

	private SerializableNotationList extractNotations() {

		final SerializableNotationList serializableNotationList = new SerializableNotationList();
		final List<SerializableNotation> notations = serializableNotationList.getSerializableNotation();

		try {
			final JAXBContext jc = JAXBContext.newInstance("com.cccbr.generated.method");

			final Unmarshaller unmarshaller = jc.createUnmarshaller();

			final JAXBElement<CollectionType> unmarshallled = (JAXBElement<CollectionType>) unmarshaller.unmarshal(new File(CCBR_XML));

			final CollectionType collection = unmarshallled.getValue();

			final List<MethodSetType> methodSets  = collection.getMethodSet();

			for (final MethodSetType methodSet : methodSets) {
				final BigInteger stage = methodSet.getProperties().getStage();
				final List<MethodType> methods = methodSet.getMethod();
				final List<SymmetryType> symmetryTypes = methodSet.getProperties().getSymmetry();
				final boolean palindromic = symmetryTypes.contains(SymmetryType.PALINDROMIC);

				for (final MethodType method : methods) {
					final SerializableNotation notation = extractNotation(method, stage, palindromic);
					if (notation != null) {
						notations.add(notation);
					}
				}
			}

			log.info("Extracted [" + notations.size() + "] methods not imported [" + unimportedMethodCount + "/87]" );

		} catch (final JAXBException e) {
			log.error("Exception extracting methods from allmethods.xml", e);
		}

		return serializableNotationList;
	}

	private SerializableNotation extractNotation(final MethodType method, final BigInteger stage, boolean parentPalindromic) {

		final NumberOfBells numberOfBells = NumberOfBells.valueOf(stage.intValue());
		method.getName().getValue();
		final String titleOriginal = method.getTitle().getValue();
		String title = method.getTitle().getValue();
		final String id = method.getId();

		// do we have a method based palindromic set
		final List<SymmetryType> symmetryTypes = method.getSymmetry();
		if (symmetryTypes.contains(SymmetryType.PALINDROMIC) ) {
			parentPalindromic = true;
		}

		if (numberOfBells == null) {
			log.debug("[" + unimportedMethodCount++ + "." + id +"] Not extracting [" + titleOriginal + "] as stage [" + stage +"]");
			return null;
		}

		final String notationShorthand = method.getNotation().getValue();

		if (!parentPalindromic && notationShorthand.contains(",")) {
			log.error("[" + unimportedMethodCount++ + "." + id +"] Not extracting [" + titleOriginal + "] as not palindromic but contains comma for folded");
			return null;
		}

		//remove the number of bells from the title - will take up less room on the database.

		int replacedNumberOfBellsInTitle = 0;
		for (final NumberOfBells nobTest : NumberOfBells.values()) {
			final String nobName = nobTest.getName();
			if (title.contains(nobName)) {
				title = title.replace(nobName, "");
				replacedNumberOfBellsInTitle++;
			}
		}
		title = title.trim();

		if (replacedNumberOfBellsInTitle != 1) {
			log.error("[" + unimportedMethodCount++ + "." + id +"] Not extracting [" + titleOriginal + "] as replaced [" + replacedNumberOfBellsInTitle +"] numberOfBells names in title [" + title + "]");
			return null;
		}

		final SerializableNotation notation = new SerializableNotation();
		notation.setName(title);
		notation.setStage(numberOfBells.getBellCount());

		// are we palindromic AND folded?
		if (parentPalindromic && notationShorthand.contains(",")) {
			final String[] split = notationShorthand.split(",");
			if (split.length != 2) {
				log.error("[" + unimportedMethodCount++ + "." + id +"] Not extracting [" + titleOriginal + "] as palindromic but contain [" + split.length + "]comma.");
				return null;
			}
			notation.setNotation(split[0]);
			notation.setLeadEnd(split[1]);

		}
		else {
			notation.setNotation(notationShorthand); //TODO need to handle commas.
		}

		return notation;
	}


	private static void writeNotations(final SerializableNotationList notations) {
		log.info("serialising to [" + ALL_NOTATIONS_DATA_OUTPUT + "]");

		FileOutputStream fos = null;
		DeflaterOutputStream dos = null;
		try {
			fos = new FileOutputStream(ALL_NOTATIONS_DATA_OUTPUT);
			dos = new DeflaterOutputStream(fos);

			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.notation.persist" );
			final Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE );

			m.marshal(new ObjectFactory().createSerializableNotationList(notations), dos);

			// Also to Std out for debug.
			m.marshal(new ObjectFactory().createSerializableNotationList(notations), System.out);

		} catch (final IOException e) {
			log.error("Exception serializing notations", e);
		} catch (final JAXBException e) {
			log.error("Exception marshalling notations", e);
		}
		finally {
			try {
				if (dos != null) {
					dos.close();
				}
			} catch (final IOException e) {}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (final IOException e) {}
		}
	}

	private SerializableNotationList readNotations() {

		log.info("de-serialising from [" + ALL_NOTATIONS_DATA_OUTPUT + "]");

		SerializableNotationList notations = null;

		FileInputStream fis = null;
		InflaterInputStream iis = null;
		try {
			fis = new FileInputStream(ALL_NOTATIONS_DATA_OUTPUT);
			iis = new InflaterInputStream(fis);

			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.notation.persist" );
			final Unmarshaller unmarshaller = jc.createUnmarshaller();
			final JAXBElement<SerializableNotationList> unmarshallled = (JAXBElement<SerializableNotationList>) unmarshaller.unmarshal(iis);

			notations = unmarshallled.getValue();

		} catch (final IOException e) {
			log.error("Exception deserializing notations", e);
		} catch (final JAXBException e) {
			log.error("Exception unmarshalling notations", e);
		}
		finally {
			try {
				if (iis != null) {
					iis.close();
				}
			} catch (final IOException e) {}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {}
		}

		return notations;
	}

}