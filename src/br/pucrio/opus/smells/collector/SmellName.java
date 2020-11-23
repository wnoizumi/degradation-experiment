package br.pucrio.opus.smells.collector;

public enum SmellName {
	ClassDataShouldBePrivate("Class Data Should be Private"),
	ComplexClass("Complex Class"),
	FeatureEnvy("Feature Envy"),
	GodClass("God Class"),
	LazyClass("Lazy Class"),
	LongMethod("Long Method"),
	LongParameterList("Long Parameter List"),
	MessageChain("Message Chain"),
	RefusedBequest("Refused Bequest"),
	SpeculativeGenerality("Speculative Generality"),
	SpaghettiCode("Spaghetti Code"),
	DispersedCoupling("Dispersed Coupling"),
	IntensiveCoupling("Intensive Coupling"),
	BrainClass("Brain Class"),
	ShotgunSurgery("Shotgun Surgery"),
	BrainMethod("Brain Method"),
	DataClass("Data Class"); 

	private String readableName;
	
	SmellName(String readableName) {
		this.readableName = readableName;
	}
	
	public String getReadableName() {
		return this.readableName;
	}
}
