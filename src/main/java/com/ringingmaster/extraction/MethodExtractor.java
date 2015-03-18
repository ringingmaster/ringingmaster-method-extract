package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;

import java.util.stream.Stream;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface MethodExtractor {
	Stream<SerializableNotation> extractNotations();
}
