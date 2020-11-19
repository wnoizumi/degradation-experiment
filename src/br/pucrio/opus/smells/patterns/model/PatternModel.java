package br.pucrio.opus.smells.patterns.model;

import br.pucrio.opus.smells.resources.Type;

public class PatternModel {

	private PatternKind pattern;
	private Type rootType;

	public PatternModel(Type rootType, PatternKind pattern) {
		this.rootType = rootType;
		this.pattern = pattern;
	}
	
	public Type getRootType() {
		return this.rootType;
	}
	
	public PatternKind getKind() {
		return this.pattern;
	}
		
}
