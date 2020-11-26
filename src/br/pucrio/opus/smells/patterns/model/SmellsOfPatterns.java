package br.pucrio.opus.smells.patterns.model;

import java.util.Arrays;
import java.util.List;

import br.pucrio.opus.smells.collector.SmellName;

public class SmellsOfPatterns {

	//Multiple Smells Patterns
	public static final List<SmellName> CONCERN_OVERLOAD = Arrays.asList(SmellName.BrainClass, SmellName.SpaghettiCode, SmellName.ComplexClass, SmellName.FeatureEnvy, SmellName.GodClass, SmellName.IntensiveCoupling, SmellName.LongMethod, SmellName.ShotgunSurgery);
	public static final List<SmellName> FAT_INTERFACE = Arrays.asList(SmellName.DispersedCoupling, SmellName.FeatureEnvy);
	public static final List<SmellName> UNWANTED_DEPENDENCY = Arrays.asList(SmellName.FeatureEnvy, SmellName.LongMethod, SmellName.ShotgunSurgery);
	public static final List<SmellName> SCATTERED_CONCERN_COMPLEMENT = Arrays.asList(SmellName.DispersedCoupling, SmellName.FeatureEnvy, SmellName.IntensiveCoupling, SmellName.ShotgunSurgery);
	public static final List<SmellName> SCATTERED_CONCERN_MANDATORY = Arrays.asList(SmellName.GodClass, SmellName.ComplexClass, SmellName.BrainClass, SmellName.SpaghettiCode);
	
	//Single Smell Patterns
	public static final List<SmellName> INCOMPLETE_ABSTRACTION = Arrays.asList(SmellName.LazyClass);
	public static final List<SmellName> UNUSED_ABSTRACTION = Arrays.asList(SmellName.SpeculativeGenerality);
}
