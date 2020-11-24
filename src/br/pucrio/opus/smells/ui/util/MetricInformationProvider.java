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
			return "Max Call Chain measures the largest chain of method calls (i.e., successive method calls in the same expression) in the method.";
		case ParameterCount:
			return "Parameter Count measures the number of parameters in the method.";
		case OverrideRatio:
			return "Override Ratio measures the ratio of overridden methods divided by the number of methods in the super class.";
		case PublicFieldCount:
			return "Public Field Count measures the number of non-static public attributes in the class.";
		case TCC:
			return "Tight Class Cohesion (TCC) measures the relative number of directly connected public methods in the class. " + System.lineSeparator() +
					" Two visible methods are directly connected, if they are accessing the same instance variables of the class.";
		case MaxNesting:
			return "Max Nesting measures the method's max nesting level.";
		case NOAV:
			return "Number of Accessed Variables (NOAV) measures how many attributes the method accesses.";
		case NOAM:
			return "Number of Accessor Methods (NOAM) measures how many assessors methods (getters and setters) exist in the class.";
		case WMC:
			return "Weighted Method Count (WMC) measures the sum of Cyclomatic Complexity (CC) of all methods declared in the class.";
		case WOC:
			return "Weigh of Class (WOC) measures the number of non-assessors (i.e., methods that are not getters nor setters) public methods divided by the total number of public methods in the class.";
		case CINT:
			return "Coupling Intensity measures the number of calls for distinct methods performed by the class.";
		case CDISP:
			return "Coupling Dispersion measures the dispersion of calls for distinct methods (provided by different classes) performed by the class.";
		case ChangingClasses:
			return "Changing Classes measures the number of different classes calling methods of this class.";
		case ChangingMethods:
			return "Changing Methods measures the number of different methods calling this method.";
		case LCOM2:
			return "LCOM2 measures the lack of cohesion in methods of a class. " + System.lineSeparator() + 
					"The equation to calculate the LCOM2 is:" + System.lineSeparator() +
					"		m = #declaredMethods(C)" + System.lineSeparator() +
					"		a = #declaredAttributes(C)" + System.lineSeparator() +
					"		m(A) = # of methods in C that reference attribute A" + System.lineSeparator() + 
					"		s = sum(m(A)) for A in declaredAttributes(C)" + System.lineSeparator() +
					"		LCOM2(C) = 1 - s/(m * a)";
		case LCOM3:
			return "LCOM3 measures the lack of cohesion in methods of a class. " + System.lineSeparator() + 
					"The equation to calculate the LCOM3 is:" + System.lineSeparator() + 
					"		m = #declaredMethods(C)" + System.lineSeparator() + 
					"		a = #declaredAttributes(C)" + System.lineSeparator() + 
					"		m(A) = # of methods in C that reference attribute A" + System.lineSeparator() + 
					"		s = sum(m(A)) for A in declaredAttributes(C)" + System.lineSeparator() + 
					"		LCOM3(C) = (m - s/a)/(m - 1)";
		}
		
		return "";
	}
}
