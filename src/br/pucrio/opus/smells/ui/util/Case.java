package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.Type;

public class Case {

	private Type type;
	private int caseNumber;
	private String degradationInfo;

	public Case(Type type, int caseNumber) {
		this.type = type;
		this.caseNumber = caseNumber;
		this.degradationInfo = DegradationInfoProvider.getDefaultInfo();
	}

	public Type getType() {
		return type;
	}
	
	public String getCaseDescription() {
		String caseNumberStr = "Case #" + this.getCaseNumber() + System.lineSeparator();
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
		
		String providedDegradationInfo = "Degradation Info: None.";
		if (this.caseNumber % 2 == 0)
			providedDegradationInfo = "Degradation Info: " + this.degradationInfo;
		providedDegradationInfo += System.lineSeparator();
		
		return caseNumberStr + caseType + className + classSmells + methodSmells + providedDegradationInfo;
	}

	protected String getCaseType() {
		//NP = Non-Pattern case
		return "Case Type: NP" + System.lineSeparator();
	}

	public int getCaseNumber() {
		return caseNumber;
	}
	
	public String getDegradationInfo() {
		return this.degradationInfo;
	}
}
