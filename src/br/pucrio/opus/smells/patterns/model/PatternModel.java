package br.pucrio.opus.smells.patterns.model;

import br.pucrio.opus.smells.resources.Type;

public class PatternModel implements Comparable<PatternModel> {

	private PatternKind pattern;
	private Type rootType;
	private float patternCompleteness;

	public PatternModel(Type rootType, PatternKind pattern) {
		this.rootType = rootType;
		this.pattern = pattern;
		this.patternCompleteness = 1;
	}
	
	public Type getRootType() {
		return this.rootType;
	}
	
	public PatternKind getKind() {
		return this.pattern;
	}
	
	public void setPatternCompleteness(float patternCompleteness) {
		this.patternCompleteness = patternCompleteness;
	}

	@Override
	public int compareTo(PatternModel o) {
		int completenessComparison = Float.compare(this.patternCompleteness, o.patternCompleteness);
		if (completenessComparison == 0) {
			int thisNumSmells = this.rootType.getAllSmells().size();
			int oNumSmells = o.rootType.getAllSmells().size();
			return Integer.compare(thisNumSmells, oNumSmells);
		}
		return completenessComparison;
	}
}
