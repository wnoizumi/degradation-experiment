package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.metrics.MetricName;

public class MetricInformationProvider {
	
	public static String getInfoFor(MetricName metric) {
		switch (metric) {
		case CLOC:
			return "Class Lines of Code (CLOC) measures the number of lines of code in a class. It does not consider blank lines and code comments.";
		case MLOC:
			return "Method Lines of Code (MLOC) measures the number of lines of code in a method. It does not consider blank lines and code comments.";
		case CC:
			return "Cyclomatic Complexity (CC) measures the complexity of a method. " + System.lineSeparator() + 
					"It is a quantitative measure of independent paths in the source code of the method. " + System.lineSeparator() + 
					"The higher the CC value, the more complex the method will be.";
		case MaxCallChain:
			return "";
		case ParameterCount:
			return "";
		case OverrideRatio:
			return "";
		case PublicFieldCount:
			return "";
		case TCC:
			return "";
		case MaxNesting:
			return "";
		case NOAV:
			return "";
		case NOAM:
			return "";
		case WMC:
			return "";
		case WOC:
			return "";
		case CINT:
			return "";
		case CDISP:
			return "";
		case ChangingClasses:
			return "";
		case ChangingMethods:
			return "";
		case LCOM:
			return "";
		case LCOM2:
			return "";
		case LCOM3:
			return "";
		}
		
		return "";
	}

}
