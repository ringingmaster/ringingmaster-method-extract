package org.ringingmaster.extract;

import org.ringingmaster.persist.NotationLibraryPersister;
import org.ringingmaster.persist.NotationLibraryUsage;
import org.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import org.ringingmaster.extract.notationlibrary.NotationLibraryExtractor;
import org.ringingmaster.extract.notationlibrary.NotationLibraryFactory;
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
			new NotationLibraryPersister().writeNotationLibrary(notations, path, NotationLibraryUsage.CC_LIBRARY);
		} catch (IOException | JAXBException e) {
			log.error("", e);
		}
	}

}