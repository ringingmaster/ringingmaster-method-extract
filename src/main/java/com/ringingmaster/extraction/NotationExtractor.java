package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.persist.Notation;
import com.concurrentperformance.ringingmaster.generated.persist.NotationLibrary;

import java.util.stream.Stream;

/**
 * Extract notations from a third party notation library into our standard persistent format
 *
 * @author Lake
 */
public interface NotationExtractor {
	Stream<Notation> extractNotationLibraryToStream();

	NotationLibrary extractNotationLibrary();
}
