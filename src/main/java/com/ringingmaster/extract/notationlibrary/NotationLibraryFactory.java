package com.ringingmaster.extract.notationlibrary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationLibraryFactory {

	private static final Logger log = LoggerFactory.getLogger(NotationLibraryFactory.class);

	public static NotationLibraryExtractor buildCentralCouncilLibraryForClasspathResource(String centralCouncilXmlClasspath) {
		URL resource = NotationLibraryFactory.class.getResource(centralCouncilXmlClasspath);
		Path centralCouncilXmlPath = null;
		try {
			centralCouncilXmlPath = Paths.get(resource.toURI());
		} catch (URISyntaxException e) {
			log.error("TODO", e);
		}
		return new CentralCouncilXmlLibraryNotationLibraryExtractor(centralCouncilXmlPath);
	}
}
