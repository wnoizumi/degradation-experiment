package br.pucrio.opus.smells.patterns;

import br.pucrio.opus.smells.resources.Type;

public class ArchitecturalProblem {
	
	private Type rootClass;
	
	public ArchitecturalProblem(Type rootClass) {
		this.rootClass = rootClass;
	}
	
	public Type getRootClass() {
		return rootClass;
	}
}
