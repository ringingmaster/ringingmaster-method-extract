package com.ringingmaster.extraction.notationlibrary;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationLibraryFactory {

	public static NotationLibraryExtractor buildCentralCouncilLibraryForClasspathResource(String centralCouncilXmlClasspath) {
		URL resource = NotationLibraryFactory.class.getResource(centralCouncilXmlClasspath);
		Path centralCouncilXmlPath = Paths.get(resource.getPath());
		return new CentralCouncilXmlLibraryNotationLibraryExtractor(centralCouncilXmlPath);
	}
}
