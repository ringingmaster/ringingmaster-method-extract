package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryPersist;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class PersistableNotationLibraryBuilderTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void canExtractCentralCouncilLibrary() throws IOException, JAXBException {

		final NotationLibraryExtractor notationLibraryExtractor = new CentralCouncilXmlLibraryNotationLibraryExtractor(NotationLibraryBuilder.CCBR_XML);
		final NotationLibraryPersist notationLibrary = notationLibraryExtractor.extractNotationLibrary();

		assertTrue(notationLibrary.getNotation().size() > 19100);
		assertTrue(notationLibrary.getNotation().size() < 21000);

	}

}