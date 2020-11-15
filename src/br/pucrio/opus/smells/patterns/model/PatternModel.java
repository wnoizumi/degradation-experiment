package br.pucrio.opus.smells.patterns.model;

import br.pucrio.opus.smells.resources.Type;

public class PatternModel {

	private Type rootType;

	public PatternModel(Type rootType) {
		this.rootType = rootType;
	}
	
	public Type getRootType() {
		return this.rootType;
	}
		
}
