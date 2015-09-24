package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.NotationLibraryUsage;
import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
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

	private static final String ARTIFACT_FOLDER = "./ringingmaster-engine/src/test/resource";
	private static final String NOTATION_LIBRARY_FILENAME = "notationlibrary.xml";

	protected static final String CCBR_XML = "/allmeths.xml";


	public static void main(final String[] args) {
		extractAndPersistNotationLibrary();
	}

	private static void extractAndPersistNotationLibrary() {

		final NotationLibraryExtractor notationLibraryExtractor = new CentralCouncilXmlLibraryNotationLibraryExtractor(CCBR_XML);
		final NotationLibraryType notations = notationLibraryExtractor.extractNotationLibrary();

		try {
			Path path = Paths.get(ARTIFACT_FOLDER, NOTATION_LIBRARY_FILENAME);
			new DocumentPersist().writeNotationLibrary(notations, path, NotationLibraryUsage.CC_LIBRARY);
		} catch (IOException e) {
			log.error("", e);
		} catch (JAXBException e) {
			log.error("", e);
		}
	}

}