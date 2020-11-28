package br.pucrio.opus.smells.ui.util;

import java.util.concurrent.ThreadLocalRandom;

import br.pucrio.opus.smells.patterns.model.PatternKind;

public class DegradationInfoProvider {
	
	public static String getInfoFor(PatternKind patternKind) {
		switch (patternKind) {
		case CONCERN_OVERLOAD:
			return "This class may be impacted by a degradation problem called Concern Overload. " + System.lineSeparator() + 
					"A Concern Overload occurs when a source code element is overloaded with too many responsibilities.";
		case FAT_INTERFACE:
			return "This code element may be the root of a degradation problem called Fat Interface. " + System.lineSeparator() + 
					"A Fat Interface occurs when an class/interface exposes " + 
					"multiple funcionalities and many of those functionalities are " + 
					"not related to each other.";			
		case INCOMPLETE_ABSTRACTION:
			return "This class may be impacted by a degradation problem called Incomplete Abstraction. " + System.lineSeparator() +
					"An Incomplete Abstraction occurs when a source code element " + 
					"does not support complementary or interrelated methods completely."; 
		case SCATTERED_CONCERN:
			return "This class may be related to a degradation problem called Scattered Concern. " + System.lineSeparator() +
					"A Scattered Concern occurs when code elements are responsible for the same functionality, " + 
					"but some of them cross-cut different components of the system.";
		case UNUSED_ABSTRACTION:
			return "This class may be impacted by a degradation problem called Unused Abstraction. " + System.lineSeparator() +
					"An Unused Abstraction occurs when the code element " + 
					"is not directly used or is unreachable.";
		case UNWANTED_DEPENDENCY:
			return "This class may be related to a degradation problem called Unwanted Dependency. " + System.lineSeparator() +
					"An Unwanted Dependency occurs when a dependency violates " + 
					"a rule defined on the system intended architectural design.";
		}
		
		return "";
	}

	public static String getRandomInfo() {
		PatternKind[] kinds = PatternKind.values();
		int randomPosition = ThreadLocalRandom.current().nextInt(0, kinds.length);
		if (randomPosition == kinds.length)
			randomPosition--;
		return getInfoFor(kinds[randomPosition]);
	}
}
