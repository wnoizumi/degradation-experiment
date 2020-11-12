package br.pucrio.opus.smells.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.agglomeration.SmellyGraphBuilder;
import br.pucrio.opus.smells.collector.ClassLevelSmellDetector;
import br.pucrio.opus.smells.collector.MethodLevelSmellDetector;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.metrics.MethodMetricValueCollector;
import br.pucrio.opus.smells.metrics.TypeMetricValueCollector;
import br.pucrio.opus.smells.patterns.SmellPatternsFinder;
import br.pucrio.opus.smells.resources.JavaFilesFinder;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.SourceFile;
import br.pucrio.opus.smells.resources.SourceFilesLoader;
import br.pucrio.opus.smells.resources.Type;

public class ExperimentController {
	
	private List<Type> allTypes = new ArrayList<>();
	private SmellyGraph graph = new SmellyGraph();
	SmellPatternsFinder patternsFinder = new SmellPatternsFinder();

	public void startExperiment(String sourcePath) throws IOException {
		loadAllTypes(sourcePath);
		collectTypeMetrics();
		detectSmells();
		buildGraph();
		collectNumberOfChanges();
		collectDeveloperAffinity();
		findSmellPatterns();
	}

	private void findSmellPatterns() {
		patternsFinder.findPatterns(allTypes, graph);
	}

	private void collectDeveloperAffinity() {
		
	}

	private void collectNumberOfChanges() {
		
	}

	private void buildGraph() {
		SmellyGraphBuilder builder = new SmellyGraphBuilder();
		builder.addTypeAndItsMethods(allTypes);
		graph = builder.build();
	}
	
	private void collectMethodMetrics(Type type) {
		for (Method method: type.getMethods()) {
			MethodMetricValueCollector methodCollector = new MethodMetricValueCollector(type.getNodeAsTypeDeclaration());
			methodCollector.collect(method);
		}
	}

	private void collectTypeMetrics() throws IOException {
		for (Type type : allTypes) {
			TypeMetricValueCollector typeCollector = new TypeMetricValueCollector();
			typeCollector.collect(type);
			this.collectMethodMetrics(type);
		}
	}

	private void detectSmells() {
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

	private void loadAllTypes(String sourcePath) throws IOException {
		allTypes = new ArrayList<>();
		JavaFilesFinder sourceLoader = new JavaFilesFinder(sourcePath);
		SourceFilesLoader compUnitLoader = new SourceFilesLoader(sourceLoader);
		List<SourceFile> sourceFiles = compUnitLoader.getLoadedSourceFiles();
		for (SourceFile sourceFile : sourceFiles) {
			for (Type type : sourceFile.getTypes()) {
				allTypes.add(type);
			}
		}
	}

}
