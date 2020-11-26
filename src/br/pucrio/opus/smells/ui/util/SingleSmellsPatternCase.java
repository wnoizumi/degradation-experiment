package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.patterns.model.PatternModel;

public class SingleSmellsPatternCase extends Case {

	private PatternModel pattern;

	public SingleSmellsPatternCase(PatternModel pattern, int caseNumber) {
		super(pattern.getRootType(), caseNumber);
		this.pattern = pattern;
	}

	public PatternModel getPattern() {
		return pattern;
	}
	
	@Override
	public String getCaseDescription() {
		String patternKind = "Kind: " + pattern.getKind();
		return super.getCaseDescription() + patternKind;
	}
	
	@Override
	protected String getCaseType() {
		//SSP = Single Smell Pattern case
		return "Case Type: SSP" + System.lineSeparator();
	}
}
