package br.pucrio.opus.smells.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.pucrio.opus.smells.agglomeration.SmellyEdge;
import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.agglomeration.SmellyNode;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.collector.SmellName;
import br.pucrio.opus.smells.patterns.model.PatternKind;
import br.pucrio.opus.smells.patterns.model.PatternModel;
import br.pucrio.opus.smells.patterns.model.SmellsOfPatterns;
import br.pucrio.opus.smells.resources.Resource;
import br.pucrio.opus.smells.resources.Type;

public class SmellPatternsFinder {

	private List<PatternModel> multipleSmellsPatterns = new ArrayList<PatternModel>();
	private List<PatternModel> singleSmellPatterns = new ArrayList<PatternModel>();

	public List<PatternModel> getMultipleSmellsPatterns() {
		return this.multipleSmellsPatterns;
	}

	public List<PatternModel> getSingleSmellPatterns() {
		return this.singleSmellPatterns;
	}

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
			for (Smell smell : type.getAllSmells()) {
				if (SmellsOfPatterns.UNUSED_ABSTRACTION.contains(smell.getName())) {
					singleSmellPatterns.add(new PatternModel(type, PatternKind.UNUSED_ABSTRACTION));
					break;
				}
			}
		}
	}

	private void detecIncompleteAbstraction(List<Type> allTypes) {
		for (Type type : allTypes) {
			for (Smell smell : type.getAllSmells()) {
				if (SmellsOfPatterns.INCOMPLETE_ABSTRACTION.contains(smell.getName())) {
					singleSmellPatterns.add(new PatternModel(type, PatternKind.INCOMPLETE_ABSTRACTION));
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
				PatternModel pattern = null;
				for (Smell smell : type.getAllSmells()) {
					if (smell.getName().equals(SmellName.ShotgunSurgery)) {
						pattern = new PatternModel(type, PatternKind.FAT_INTERFACE);
					}
				}

				HashSet<SmellName> fatInterfaceSmells = new HashSet<>();
				HashSet<Type> relatedTypes = new HashSet<>();
				SmellyNode node = getNodeOfType(type, graph);
				if (node != null) {
					for (SmellyEdge edge : node.getIncomingEdges()) {
						Set<Smell> smellsToCheck = null;
						Resource resource = edge.getOrigin().getResource();
						if (resource instanceof Type && !resource.equals(type)) {
							smellsToCheck = ((Type) resource).getAllSmells();

							for (Smell smell : smellsToCheck) {
								if (SmellsOfPatterns.FAT_INTERFACE.contains(smell.getName())) {
									fatInterfaceSmells.add(smell.getName());
									relatedTypes.add((Type) resource);
								}
							}
						}
					}
					if (fatInterfaceSmells.size() > 1) {
						if (pattern == null) {
							pattern = new PatternModel(type, PatternKind.FAT_INTERFACE);
							float completeness = fatInterfaceSmells.size()
									/ ((float) SmellsOfPatterns.FAT_INTERFACE.size());
							pattern.setPatternCompleteness(completeness);
						}
						pattern.addRelatedTypes(relatedTypes);
					}
				}
				
				if (pattern != null) {
					multipleSmellsPatterns.add(pattern);
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
			for (Smell smell : type.getAllSmells()) {
				if (SmellsOfPatterns.SCATTERED_CONCERN_MANDATORY.contains(smell.getName())) {
					mandatorySmells.add(smell.getName());
				} else if (SmellsOfPatterns.SCATTERED_CONCERN_COMPLEMENT.contains(smell.getName())) {
					complementarySmells.add(smell.getName());
				}
			}

			if (mandatorySmells.size() >= 1 && complementarySmells.size() >= 1) {
				PatternModel pattern = new PatternModel(type, PatternKind.SCATTERED_CONCERN);
				float completeness = complementarySmells.size()
						/ ((float) SmellsOfPatterns.SCATTERED_CONCERN_COMPLEMENT.size());
				pattern.setPatternCompleteness(completeness);
				multipleSmellsPatterns.add(pattern);
			}
		}
	}

	private void detectConcernOverload(List<Type> allTypes) {
		for (Type type : allTypes) {
			HashSet<SmellName> smellsFound = new HashSet<>();
			for (Smell smell : type.getAllSmells()) {
				if (SmellsOfPatterns.CONCERN_OVERLOAD.contains(smell.getName())) {
					smellsFound.add(smell.getName());
				}
			}

			if (smellsFound.size() > 1) {
				PatternModel pattern = new PatternModel(type, PatternKind.CONCERN_OVERLOAD);
				float completeness = smellsFound.size() / ((float) SmellsOfPatterns.CONCERN_OVERLOAD.size());
				pattern.setPatternCompleteness(completeness);
				multipleSmellsPatterns.add(pattern);
			}
		}
	}

	private void detectUnwantedDependency(List<Type> allTypes) {
		for (Type type : allTypes) {
			HashSet<SmellName> smellsFound = new HashSet<>();
			for (Smell smell : type.getAllSmells()) {
				if (SmellsOfPatterns.UNWANTED_DEPENDENCY.contains(smell.getName())) {
					smellsFound.add(smell.getName());
				}
			}

			if (smellsFound.size() > 1) {
				PatternModel pattern = new PatternModel(type, PatternKind.UNWANTED_DEPENDENCY);
				float completeness = smellsFound.size() / ((float) SmellsOfPatterns.UNWANTED_DEPENDENCY.size());
				pattern.setPatternCompleteness(completeness);
				multipleSmellsPatterns.add(pattern);
			}
		}
	}
}
