package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.notation.persist.ObjectFactory;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static com.google.common.base.Preconditions.checkState;


/**
 * Builds the file version of the method data in both compressed an uncompressed form.
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

		SerializableNotation serializableNotation1 = notations.getSerializableNotation().get(0);
		notations.getSerializableNotation().clear();
		notations.getSerializableNotation().add(serializableNotation1);

		writeNotations(notations);
		writeNotationsDeflated(notations);

	    final SerializableNotationList notationsRead = readNotationsFromDeflated();

		checkState(notations.getSerializableNotation().size() == notationsRead.getSerializableNotation().size());
		for (int i=0;i<notations.getSerializableNotation().size();i++) {
			SerializableNotation serializableNotation = notations.getSerializableNotation().get(i);
			SerializableNotation serializableNotationRead = notationsRead.getSerializableNotation().get(i);
			checkState(serializableNotation.getName().equals(serializableNotationRead.getName()));
		}

		log.info("Finished. [{}ms]", System.currentTimeMillis() - start);
	}

	private static void writeNotations(final SerializableNotationList notations) {

		FileOutputStream fos = null;
		try {
			Path path = Paths.get(ALL_NOTATIONS_OUTPUT_FOLDER, ALL_NOTATIONS_OUTPUT).toAbsolutePath().normalize();
			log.info("serialising to [" + path + "]");

			fos = new FileOutputStream(path.toFile());

			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.notation.persist" );
			final Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

			m.marshal(new ObjectFactory().createSerializableNotationList(notations), fos);

			fos.flush();

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

	private static void writeNotationsDeflated(final SerializableNotationList notations) {
		FileOutputStream fos = null;
		DeflaterOutputStream dos = null;
		try {
			Path path = Paths.get(ALL_NOTATIONS_OUTPUT_FOLDER, ALL_NOTATIONS_DEFLATED_OUTPUT).toAbsolutePath().normalize();
			log.info("serialising deflated to [" + path + "]");

			fos = new FileOutputStream(path.toFile());
			dos = new DeflaterOutputStream(fos);

			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.notation.persist" );
			final Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

			m.marshal(new ObjectFactory().createSerializableNotationList(notations), dos);

			dos.flush();
			fos.flush();

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
				if (dos != null) {
					dos.close();
				}
			} catch (final IOException e) {}
		}
	}

	private SerializableNotationList readNotationsFromDeflated() {

		SerializableNotationList notations = null;

		FileInputStream fis = null;
		InflaterInputStream iis = null;
		try {
			Path path = Paths.get(ALL_NOTATIONS_OUTPUT_FOLDER, ALL_NOTATIONS_DEFLATED_OUTPUT).toAbsolutePath().normalize();
			log.info("de-serialising from [{}]", path);
			fis = new FileInputStream(path.toFile());
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