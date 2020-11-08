package br.pucrio.opus.smells.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.agglomeration.SmellyGraphBuilder;
import br.pucrio.opus.smells.collector.ClassLevelSmellDetector;
import br.pucrio.opus.smells.collector.MethodLevelSmellDetector;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.collector.SmellName;
import br.pucrio.opus.smells.metrics.MethodMetricValueCollector;
import br.pucrio.opus.smells.metrics.TypeMetricValueCollector;
import br.pucrio.opus.smells.resources.JavaFilesFinder;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.SourceFile;
import br.pucrio.opus.smells.resources.SourceFilesLoader;
import br.pucrio.opus.smells.resources.Type;

public class SmellPatternsFinder {
	
	private static final List<SmellName> unwantedDependencySmellTypes = Arrays.asList(SmellName.FeatureEnvy, SmellName.LongMethod, SmellName.ShotgunSurgery);
	
	private List<String> sourcePaths;
	
	private List<ArchitecturalProblem> architecturalProblems = new ArrayList<ArchitecturalProblem>();

	public SmellPatternsFinder(List<String> sourcePaths) {
		this.sourcePaths = sourcePaths;
	}
	
	public void findPatterns() throws IOException {
		List<Type> allTypes = loadAllTypes(sourcePaths);
		collectTypeMetrics(allTypes);
		detectSmells(allTypes);
		SmellyGraph graph = this.buildGraph(allTypes);
		detectPatterns(allTypes, graph);
	}

	private void detectPatterns(List<Type> allTypes, SmellyGraph graph) {
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

	private SmellyGraph buildGraph(List<Type> allTypes) {
		SmellyGraphBuilder builder = new SmellyGraphBuilder();
		builder.addTypeAndItsMethods(allTypes);
		return builder.build();
	}
	
	private void collectMethodMetrics(Type type) {
		for (Method method: type.getMethods()) {
			MethodMetricValueCollector methodCollector = new MethodMetricValueCollector(type.getNodeAsTypeDeclaration());
			methodCollector.collect(method);
		}
	}

	private void collectTypeMetrics(List<Type> types) throws IOException {
		for (Type type : types) {
			TypeMetricValueCollector typeCollector = new TypeMetricValueCollector();
			typeCollector.collect(type);
			this.collectMethodMetrics(type);
		}
	}

	private void detectSmells(List<Type> allTypes) {
		for (Type type : allTypes) {
			// It is important for some detectors that method-level smells are collected first
			for (Method method: type.getMethods()) {
				MethodLevelSmellDetector methodSmellDetector = new MethodLevelSmellDetector();
				List<Smell> smells = methodSmellDetector.detect(method);
				method.addAllSmells(smells);
			}
			// some class-level detectors use method-level smells in their algorithms
			ClassLevelSmellDetector classSmellDetector = new ClassLevelSmellDetector();
			List<Smell> smells = classSmellDetector.detect(type);
			type.addAllSmells(smells);
		}
	}

	private List<Type> loadAllTypes(List<String> sourcePaths) throws IOException {
		List<Type> allTypes = new ArrayList<>();
		JavaFilesFinder sourceLoader = new JavaFilesFinder(sourcePaths);
		SourceFilesLoader compUnitLoader = new SourceFilesLoader(sourceLoader);
		List<SourceFile> sourceFiles = compUnitLoader.getLoadedSourceFiles();
		for (SourceFile sourceFile : sourceFiles) {
			for (Type type : sourceFile.getTypes()) {
				allTypes.add(type);
			}
		}
		return allTypes;
	}

}
