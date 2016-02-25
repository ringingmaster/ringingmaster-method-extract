package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import com.ringingmaster.extraction.notationlibrary.NotationLibraryExtractor;
import com.ringingmaster.extraction.notationlibrary.NotationLibraryFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author Lake
 */
public class PersistableNotationLibraryBuilderTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void canExtractCentralCouncilLibrary() throws IOException, JAXBException {

		final NotationLibraryExtractor notationLibraryExtractor = NotationLibraryFactory.buildCentralCouncilLibraryForClasspathResource("/allmeths.xml");
		final NotationLibraryPersist notationLibrary = notationLibraryExtractor.extractNotationLibrary();

		// NOTE: This number will probably go up each time a new version is taken from the CC site.
		assertTrue(notationLibrary.getNotation().size() > 19100);
		assertTrue(notationLibrary.getNotation().size() < 21000);

	}

}