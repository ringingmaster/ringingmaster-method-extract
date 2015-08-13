package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.persist.generated.v1.PersistableNotationLibrary;

/**
 * Extract notations from a third party notation library into our standard persistent format
 *
 * @author Lake
 */
public interface NotationLibraryExtractor {
	PersistableNotationLibrary extractNotationLibrary();
}
