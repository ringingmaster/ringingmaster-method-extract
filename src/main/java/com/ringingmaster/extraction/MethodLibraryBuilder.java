package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.notation.persist.ObjectFactory;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


/**
 * Builds the compressed method data
 * 
 * @author Stephen Lake
 */
public class MethodLibraryBuilder {
	private final static Logger log = LoggerFactory.getLogger(MethodLibraryBuilder.class);

	private static final String ALL_NOTATIONS_OUTPUT_FOLDER = "./ringingmaster-method-extraction/artifacts/";
	private static final String ALL_NOTATIONS_DEFLATED_OUTPUT = "all_notations.xml.deflated";
	private static final String ALL_NOTATIONS_OUTPUT = "all_notations.xml";

	private final MethodExtractor methodExtractor = new CentralCouncilMethodExtractor();


	public static void main(final String[] args) {
		new MethodLibraryBuilder().run();
	}

	private void run() {
		long start = System.currentTimeMillis();

		final SerializableNotationList notations = methodExtractor.extractNotations();
		writeNotations(notations);
//TODO	    final SerializableNotationList notationsRead = readNotations();
//TODO	    final boolean match = notations.equals(notationsRead);
//TODO		if (match) {
//TODO			log.info("Round trip persistence success");
//TODO		}
//TODO		else{
//TODO
//TODO			log.error("Round trip persistence fail - THIS IS FAILING BECAUSE WE CANT COMPARE THE JAX OBJECTS");
//TODO		}
		log.info("Finished. [{}ms]", System.currentTimeMillis() - start);
	}

	private static void writeNotations(final SerializableNotationList notations) {
		log.info("serialising to [" + ALL_NOTATIONS_DEFLATED_OUTPUT + "]");

		FileOutputStream fos = null;
		DeflaterOutputStream dos = null;
		try {
			dos = new DeflaterOutputStream(fos);
			fos = new FileOutputStream(ALL_NOTATIONS_OUTPUT_FOLDER + ALL_NOTATIONS_OUTPUT);



			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.notation.persist" );
			final Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE );

			m.marshal(new ObjectFactory().createSerializableNotationList(notations), fos);

			// Also to Std out for debug.
			//m.marshal(new ObjectFactory().createSerializableNotationList(notations), System.out);

		} catch (final IOException e) {
			log.error("Exception serializing notations", e);
		} catch (final JAXBException e) {
			log.error("Exception marshalling notations", e);
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
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

		log.info("de-serialising from [" + ALL_NOTATIONS_DEFLATED_OUTPUT + "]");

		SerializableNotationList notations = null;

		FileInputStream fis = null;
		InflaterInputStream iis = null;
		try {
			fis = new FileInputStream(ALL_NOTATIONS_DEFLATED_OUTPUT);
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