package br.pucrio.opus.smells.ui.util;

import br.pucrio.opus.smells.collector.Smell;

public class SmellInformationProvider {

	public static String getInfoFor(Smell smell) {
		switch (smell.getName()) {
		case BrainClass:
			return "The Brain Class smell occurs when the class is complex" + System.lineSeparator() + 
					" and tend to accumulate an excessive amount of intelligence.";
		case BrainMethod:
			return "The Brain Method smell occurs when the method centralizes the functionality of a class.";
		case ClassDataShouldBePrivate:
			return "The Class Data Should be Private smell occurs when a class exposes attributes that should be encapsulated.";
		case ComplexClass:
			return "The Complex Class smell occurs when a class has at least one method with high Cyclomatic Complexity (CC).";
		case DataClass:
			return "The Data Class smell occurs when the class is a \"dumb\" data holder, without complex functionality but other classes strongly rely on it.";
		case DispersedCoupling:
			return "The Dispersed Coupling smell occurs when the method is excessively tied to many other methods in the system " + System.lineSeparator() + 
					" and additionally these provider methods are dispersed among multiple classes.";
		case FeatureEnvy:
			return "The Feature Envy smell occurs when the method seem more interested in the data of other classes than that of its own class. " + System.lineSeparator() +
					"Such method access directly or via accessor methods a lot of data of other classes.";
		case GodClass:
			return "The God Class smell occurs when the class tend to centralize the intelligence of the system. " + System.lineSeparator() +
					"A God Class performs too much work on its own, delegating only minor details to a set of trivial classes and using the data from other classes.";
		case IntensiveCoupling:
			return "The Intensive Coupling smell occurs when a method is tied to many other methods in the system, " + System.lineSeparator() +
					"whereby these provider methods are dispersed only into one or a few classes.";
		case LazyClass:
			return "The Lazy Class smell occurs when the class is too small in terms of source lines of code.";
		case LongMethod:
			return "The Long Method smell occurs when the method is too long in terms of source lines of code.";
		case LongParameterList:
			return "The Long Parameter List smell occurs when the method has too many parameters.";
		case MessageChain:
			return "The message chain smell occurs when an implementation requests another object, that object requests yet another one, and so on. " + System.lineSeparator() +
					"These chains mean that the client is dependent on navigation along multiple class structures.";
		case RefusedBequest:
			return "The Refused Bequest smell occurs when the (sub)class uses only some of the methods and attributes inherited from its parents.";
		case ShotgunSurgery:
			return "The Shotgun Surgery smell occurs when a change in the method may imply many (small) changes to a lot of different methods and classes.";
		case SpaghettiCode:
			return "The Spaghetti Code smell occurs when the class implements at least two long methods, " + System.lineSeparator() +
					"which interact between them through method calls or shared attributes.";
		case SpeculativeGenerality:
			return "The Speculative Generality smell occurs when the class is declared as abstract, having less than three children classes using its methods.";
		}
		return "";
	}
}
