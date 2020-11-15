package br.pucrio.opus.smells.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.collector.SmellName;
import br.pucrio.opus.smells.patterns.model.PatternModel;
import br.pucrio.opus.smells.patterns.model.SmellsOfPatterns;
import br.pucrio.opus.smells.resources.Type;

public class SmellPatternsFinder {
	
	private List<PatternModel> multipleSmellsPatterns = new ArrayList<PatternModel>();
	private List<PatternModel> singleSmellsPatterns = new ArrayList<PatternModel>();
	
	
	public void findPatterns(List<Type> allTypes, SmellyGraph graph) {
		detectMultipleSmellsPatterns(allTypes, graph);
		detectSingleSmellPatterns(allTypes);
	}

	private void detectSingleSmellPatterns(List<Type> allTypes) {
		detecIncompleteAbstraction(allTypes);
		detectUnusedAbstraction(allTypes);
	}

	private void detectUnusedAbstraction(List<Type> allTypes) {
		for (Type type : allTypes) {
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.UNUSED_ABSTRACTION.contains(smell.getName())) {
					singleSmellsPatterns.add(new PatternModel(type));
					break;
				}
			}
		}
	}

	private void detecIncompleteAbstraction(List<Type> allTypes) {
		for (Type type : allTypes) {
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.INCOMPLETE_ABSTRACTION.contains(smell.getName())) {
					singleSmellsPatterns.add(new PatternModel(type));
					break;
				}
			}
		}
	}

	private void detectMultipleSmellsPatterns(List<Type> allTypes, SmellyGraph graph) {
		detectFatInterface(allTypes);
		detectConcernOverload(allTypes);
		detectScatteredConcern(allTypes);
		detectUnwantedDependency(allTypes);
	}

	private void detectFatInterface(List<Type> allTypes) {
		for (Type type : allTypes) {
			for (Smell smell : type.getSmells()) {
				if (type.isInterface()) {
					if (smell.getName().equals(SmellName.ShotgunSurgery)) {
						multipleSmellsPatterns.add(new PatternModel(type));
					}
				} else {
					//TODO check clients and implementations
				}
			}
		}
	}

	private void detectScatteredConcern(List<Type> allTypes) {
		for (Type type : allTypes) {
			HashSet<SmellName> mandatorySmells = new HashSet<>();
			HashSet<SmellName> complementarySmells = new HashSet<>();
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.SCATTERED_CONCERN_MANDATORY.contains(smell.getName())) {
					mandatorySmells.add(smell.getName());
				} else if (SmellsOfPatterns.SCATTERED_CONCERN_COMPLEMENT.contains(smell.getName())) {
					complementarySmells.add(smell.getName());
				}
			}
			
			if (mandatorySmells.size() >= 1 && complementarySmells.size() >= 1) {
				multipleSmellsPatterns.add(new PatternModel(type));
			}
		}
	}

	private void detectConcernOverload(List<Type> allTypes) {
		for (Type type : allTypes) {
			HashSet<SmellName> smellsFound = new HashSet<>();
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.CONCERN_OVERLOAD.contains(smell.getName())) {
					smellsFound.add(smell.getName());
				}
			}
			
			if (smellsFound.size() > 1) {
				multipleSmellsPatterns.add(new PatternModel(type));
			}
		}
	}

	private void detectUnwantedDependency(List<Type> allTypes) {
		for (Type type : allTypes) {
			HashSet<SmellName> smellsFound = new HashSet<>();
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.UNWANTED_DEPENDENCY.contains(smell.getName())) {
					smellsFound.add(smell.getName());
				}
			}
			
			if (smellsFound.size() > 1) {
				multipleSmellsPatterns.add(new PatternModel(type));
			}
		}
	}
}
