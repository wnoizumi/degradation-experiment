package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.patterns.model.PatternModel;

public class MultipleSmellsPatternCase extends Case {

	private PatternModel pattern;

	public MultipleSmellsPatternCase(PatternModel pattern, int caseNumber) {
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
		//MSP = Multiple Smells Pattern case
		return "Case Type: MSP" + System.lineSeparator();
	}
	
	@Override
	public String getDegradationInfo() {
		return DegradationInfoProvider.getInfoFor(this.pattern.getKind());
	}
}
