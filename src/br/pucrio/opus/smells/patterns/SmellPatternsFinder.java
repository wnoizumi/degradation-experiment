package br.pucrio.opus.smells.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.pucrio.opus.smells.agglomeration.SmellyEdge;
import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.agglomeration.SmellyNode;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.collector.SmellName;
import br.pucrio.opus.smells.patterns.model.PatternModel;
import br.pucrio.opus.smells.patterns.model.SmellsOfPatterns;
import br.pucrio.opus.smells.resources.Type;

public class SmellPatternsFinder {

	private List<PatternModel> multipleSmellsPatterns = new ArrayList<PatternModel>();
	private List<PatternModel> singleSmellPatterns = new ArrayList<PatternModel>();

	public void findPatterns(List<Type> allTypes, SmellyGraph graph) {
		detectMultipleSmellsPatterns(allTypes, graph);
		detectSingleSmellPatterns(allTypes);
		System.out.println("Multiple smells patterns:" + multipleSmellsPatterns.size());
		System.out.println("Single smell patterns:" + singleSmellPatterns.size());
	}

	private void detectSingleSmellPatterns(List<Type> allTypes) {
		detecIncompleteAbstraction(allTypes);
		detectUnusedAbstraction(allTypes);
	}

	private void detectUnusedAbstraction(List<Type> allTypes) {
		for (Type type : allTypes) {
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.UNUSED_ABSTRACTION.contains(smell.getName())) {
					singleSmellPatterns.add(new PatternModel(type));
					break;
				}
			}
		}
	}

	private void detecIncompleteAbstraction(List<Type> allTypes) {
		for (Type type : allTypes) {
			for (Smell smell : type.getSmells()) {
				if (SmellsOfPatterns.INCOMPLETE_ABSTRACTION.contains(smell.getName())) {
					singleSmellPatterns.add(new PatternModel(type));
					break;
				}
			}
		}
	}

	private void detectMultipleSmellsPatterns(List<Type> allTypes, SmellyGraph graph) {
		detectFatInterface(allTypes, graph);
		detectConcernOverload(allTypes);
		detectScatteredConcern(allTypes);
		detectUnwantedDependency(allTypes);
	}

	private void detectFatInterface(List<Type> allTypes, SmellyGraph graph) {
		for (Type type : allTypes) {
			if (type.isInterface()) {
				boolean foundFatInterface = false;
				for (Smell smell : type.getSmells()) {
					if (smell.getName().equals(SmellName.ShotgunSurgery)) {
						multipleSmellsPatterns.add(new PatternModel(type));
						foundFatInterface = true;
					}
				}
				
				if (!foundFatInterface) {
					List<Smell> fatInterfaceSmells = new ArrayList<>();
					SmellyNode node = getNodeOfType(type, graph);
					for (SmellyEdge edge : node.getIncomingEdges()) {
						for (Smell smell : edge.getOrigin().getResource().getSmells()) {
							if (SmellsOfPatterns.FAT_INTERFACE.contains(smell.getName())) {
								fatInterfaceSmells.add(smell);
							}
						}
					}
					if (fatInterfaceSmells.size() > 1) {
						multipleSmellsPatterns.add(new PatternModel(type));
					}
				}
			} 
		}
	}

	private SmellyNode getNodeOfType(Type type, SmellyGraph graph) {
		for (SmellyNode node : graph.getNodes()) {
			if (node.getResource().equals(type)) {
				return node;
			}
		}
		return null;
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
