package br.pucrio.opus.smells.patterns.model;

import java.util.HashSet;
import java.util.Set;

import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.resources.Type;

public class PatternModel implements Comparable<PatternModel> {

	private PatternKind pattern;
	private Type rootType;
	private float patternCompleteness;
	private HashSet<Type> relatedTypes;

	public PatternModel(Type rootType, PatternKind pattern) {
		this.rootType = rootType;
		this.pattern = pattern;
		this.patternCompleteness = 1;
		this.relatedTypes = new HashSet<>();
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
		return Float.compare(this.patternCompleteness, o.patternCompleteness);
	}

	public void addRelatedTypes(HashSet<Type> relatedTypes) {
		this.relatedTypes.addAll(relatedTypes);
	}

	public HashSet<Type> getRelatedTypes() {
		return relatedTypes;
	}

	public Set<Smell> getAllSmells() {
		Set<Smell> allSmells = new HashSet<>();
		allSmells.addAll(this.rootType.getAllSmells());
		for (Type t : relatedTypes) {
			allSmells.addAll(t.getAllSmells());
		}
		
		return allSmells;
	}
}
