package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.persist.DocumentPersist;
import com.concurrentperformance.ringingmaster.persist.NotationLibraryUsage;
import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import com.ringingmaster.extraction.notationlibrary.NotationLibraryExtractor;
import com.ringingmaster.extraction.notationlibrary.NotationLibraryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Builds the file version of the notation data in both compressed an uncompressed form.
 * 
 * @author Stephen Lake
 */
public class NotationLibraryBuilder {
	private final static Logger log = LoggerFactory.getLogger(NotationLibraryBuilder.class);


	public static void main(final String[] args) {
		checkArgument(args.length == 2, "Wrong number of args. usage: [inputCentralCouncilXmlPathClasspathPath] [outputCentralCouncilXmlPath]");
		String inputCentralCouncilXmlPathClasspathPath = args[0];
		String outputCentralCouncilXmlPath = checkNotNull(args[1]);
		extractAndPersistNotationLibrary(inputCentralCouncilXmlPathClasspathPath, outputCentralCouncilXmlPath);
	}

	private static void extractAndPersistNotationLibrary(String inputCentralCouncilXmlPathClasspathPath, String outputCentralCouncilXmlPathAbsolutePath) {
		log.info("Extracting Notation library. Args: [{}], [{}]", inputCentralCouncilXmlPathClasspathPath, outputCentralCouncilXmlPathAbsolutePath);

		final NotationLibraryExtractor notationLibraryExtractor = NotationLibraryFactory.buildCentralCouncilLibraryForClasspathResource(inputCentralCouncilXmlPathClasspathPath);

		final NotationLibraryPersist notations = notationLibraryExtractor.extractNotationLibrary();

		try {
			Path path = Paths.get(outputCentralCouncilXmlPathAbsolutePath).toAbsolutePath().normalize();
			log.info("Writing notation library to [{}]", path);
			new DocumentPersist().writeNotationLibrary(notations, path, NotationLibraryUsage.CC_LIBRARY);
		} catch (IOException | JAXBException e) {
			log.error("", e);
		}
	}

}