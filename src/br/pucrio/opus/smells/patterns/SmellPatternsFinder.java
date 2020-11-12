package br.pucrio.opus.smells.patterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.collector.SmellName;
import br.pucrio.opus.smells.resources.Type;

public class SmellPatternsFinder {
	
	private static final List<SmellName> unwantedDependencySmellTypes = Arrays.asList(SmellName.FeatureEnvy, SmellName.LongMethod, SmellName.ShotgunSurgery);
	
	private List<ArchitecturalProblem> architecturalProblems = new ArrayList<ArchitecturalProblem>();

	
	
	public void findPatterns(List<Type> allTypes, SmellyGraph graph) {
		detectUnwantedDependency(allTypes, graph);
	}

	private void detectUnwantedDependency(List<Type> allTypes, SmellyGraph graph) {
		for (Type type : allTypes) {
			HashSet<SmellName> smellsFound = new HashSet<>();
			for (Smell smell : type.getSmells()) {
				if (unwantedDependencySmellTypes.contains(smell.getName())) {
					smellsFound.add(smell.getName());
				}
			}
			
			if (smellsFound.size() > 1) {
				architecturalProblems.add(new ArchitecturalProblem(type));
			}
		}
	}


}
