package com.ringingmaster.extraction;

import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotation;
import com.concurrentperformance.ringingmaster.generated.notation.persist.SerializableNotationList;

import java.util.stream.Stream;

/**
 * TODO Comments
 *
 * @author Lake
 */
public interface MethodExtractor {
	Stream<SerializableNotation> extractNotationsToStream();

	SerializableNotationList extractNotations();
}
