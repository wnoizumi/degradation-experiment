package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.collector.Smell;

public class RefactoringsSuggestionProvider {
	
	public static String getInfoFor(Smell smell) {
		switch (smell.getName()) {
		case BrainClass:
			return "For removing a Brain Class one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Class, Move Method, and/or Move Attribute.";
		case BrainMethod:
			return "For removing a Brain Method one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method.";
		case ClassDataShouldBePrivate:
			return "For removing a Class Data Should be Private one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Encapsulate Attribute.";
		case ComplexClass:
			return "For removing a Complex Class one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method, Replace Conditional with Polymorphism, or Replace Type code with State/Strategy.";
		case DataClass:
			return "For removing a Data Class one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Inline Class or Collapse Hierarchy.";
		case DispersedCoupling:
			return "For removing a Dispersed Coupling one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method.";
		case FeatureEnvy:
			return "For removing a Feature Envy one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method and Move Method.";
		case GodClass:
			return "For removing a God Class one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Class, Move Method, and/or Move Attribute.";
		case IntensiveCoupling:
			return "For removing an Intensive Coupling one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method and Move Method.";
		case LazyClass:
			return "For removing a Lazy Class one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Inline Class or Collapse Hierarchy.";
		case LongMethod:
			return "For removing a Long Method one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method.";
		case LongParameterList:
			return "For removing a Long Parameter List one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Replace Parameter with Method Call, Preserve Whole Object, or Introduce Parameter Object.";
		case MessageChain:
			return "For removing a Message Chain one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Hide Delegate, or Extract Method followed by Move Method.";
		case RefusedBequest:
			return "For removing a Refused Bequest one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Replace Inheritance with Delegation or Extract Superclass.";
		case ShotgunSurgery:
			return "For removing a Shotgun Surgery one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Move Method and Move Attribute, or Inline Class.";
		case SpaghettiCode:
			return "For removing a Spaghetti Code one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Extract Method, Extract Class, Move Method, and/or Move Attribute.";
		case SpeculativeGenerality:
			return "For removing a Speculative Generality one or multiple of the following refactorings may be suitable:" + System.lineSeparator() +
					"Collapse Hierarchy, Inline Class, Remove Parameter, and/or Rename Method";
		}
		
		return "";
	}
}
