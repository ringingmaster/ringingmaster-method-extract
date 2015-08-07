package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.persist.NotationLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Builds the file version of the notation data in both compressed an uncompressed form.
 * 
 * @author Stephen Lake
 */
public class NotationLibraryBuilder {
	private final static Logger log = LoggerFactory.getLogger(NotationLibraryBuilder.class);

	private static final String ARTIFACT_FOLDER = "./ringingmaster-method-extraction/artifacts/";
	private static final String NOTATION_LIBRARY_FILENAME = "notation_library.xml";



	public static void main(final String[] args) {

		final NotationExtractor notationExtractor = new CentralCouncilXmlLibraryNotationExtractor();
		final NotationLibrary notations = notationExtractor.extractNotationLibrary();

		try {
			writeNotationLibrary(notations);
		} catch (IOException e) {
			log.error("", e);
		} catch (JAXBException e) {
			log.error("", e);
		}

	}

	public static void writeNotationLibrary(final NotationLibrary notationLibrary) throws IOException, JAXBException {

		FileOutputStream fos = null;
		try {
			Path path = Paths.get(ARTIFACT_FOLDER, NOTATION_LIBRARY_FILENAME).toAbsolutePath().normalize();
			log.info("serialising to [" + path + "]");

			fos = new FileOutputStream(path.toFile());

			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.persist" );
			final Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

			m.marshal(notationLibrary, fos);

			fos.flush();

			// Also to Std out for debug.
			//m.marshal(notationLibrary, System.out);

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


	private NotationLibrary readNotationLibraryFromDeflated() {

//		SerializableNotationList notations = null;
//
//		FileInputStream fis = null;
//		InflaterInputStream iis = null;
//		try {
//			Path path = Paths.get(ARTIFACT_FOLDER, ALL_NOTATIONS_DEFLATED_OUTPUT).toAbsolutePath().normalize();
//			log.info("de-serialising from [{}]", path);
//			fis = new FileInputStream(path.toFile());
//			iis = new InflaterInputStream(fis);
//
//			final JAXBContext jc = JAXBContext.newInstance( "com.concurrentperformance.ringingmaster.generated.notation.persist" );
//			final Unmarshaller unmarshaller = jc.createUnmarshaller();
//			final JAXBElement<SerializableNotationList> unmarshallled = (JAXBElement<SerializableNotationList>) unmarshaller.unmarshal(iis);
//
//			notations = unmarshallled.getValue();
//
//		} catch (final IOException e) {
//			log.error("Exception deserializing notations", e);
//		} catch (final JAXBException e) {
//			log.error("Exception unmarshalling notations", e);
//		}
//		finally {
//			try {
//				if (iis != null) {
//					iis.close();
//				}
//			} catch (final IOException e) {}
//			try {
//				if (fis != null) {
//					fis.close();
//				}
//			} catch (final IOException e) {}
//		}
//
//		return notations;

		return null;
	}

}