package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.persist.NotationLibrary;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class NotationLibraryBuilderTest {


	@Test
	public void canPersistAndReadJavaLibrary() throws IOException, JAXBException {

		final NotationExtractor notationExtractor = new CentralCouncilXmlLibraryNotationExtractor();
		final NotationLibrary notations = notationExtractor.extractNotationLibrary();

		NotationLibraryBuilder.writeNotationLibrary(notations);
	}


//	private void run() {
//		long start = System.currentTimeMillis();
//
//
//		final SerializableNotationList notationsRead = readNotationLibraryFromDeflated();
//
//		checkState(notations.getSerializableNotation().size() == notationsRead.getSerializableNotation().size());
//		for (int i=0;i<notations.getSerializableNotation().size();i++) {
//			SerializableNotation serializableNotation = notations.getSerializableNotation().get(i);
//			SerializableNotation serializableNotationRead = notationsRead.getSerializableNotation().get(i);
//			checkState(serializableNotation.getName().equals(serializableNotationRead.getName()));
//		}
//
//		log.info("Finished. [{}ms]", System.currentTimeMillis() - start);
//	}

}