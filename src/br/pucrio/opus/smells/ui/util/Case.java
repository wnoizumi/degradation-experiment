package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.Type;

public class Case {

	private Type type;
	private int caseNumber;

	public Case(Type type, int caseNumber) {
		this.type = type;
		this.caseNumber = caseNumber;
	}

	public Type getType() {
		return type;
	}
	
	public String getCaseDescription() {
		String caseNumber = "Case #" + this.getCaseNumber() + System.lineSeparator();
		String caseType = getCaseType();
		String className = "Root Class: " + type.getFullyQualifiedName() + System.lineSeparator();
		String classSmells = "Class Smells:" + type.getSmells()
										.stream()
										.map(a -> a.getName().toString())
										.reduce("", (a, b) -> a + System.lineSeparator() + b) + System.lineSeparator();
		String methodSmells = "Method Smells:" + System.lineSeparator();
		for (Method method : type.getMethods()) {
			for (Smell smell : method.getSmells()) {
				methodSmells += smell.getName().toString() + "->" + method.getFullyQualifiedName() + System.lineSeparator();
			}
		}
		
		return caseNumber + caseType + className + classSmells + methodSmells;
	}

	protected String getCaseType() {
		//NP = Non-Pattern case
		return "Case Type: NP" + System.lineSeparator();
	}

	public int getCaseNumber() {
		return caseNumber;
	}
}
