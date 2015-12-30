package com.ringingmaster.extraction.notationlibrary;

import com.concurrentperformance.ringingmaster.persist.generated.v1.NotationLibraryPersist;

/**
 * Extract notations from a third party notation library into our standard persistent format
 *
 * @author Lake
 */
public interface NotationLibraryExtractor {
	NotationLibraryPersist extractNotationLibrary();
}
