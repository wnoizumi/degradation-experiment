package br.pucrio.opus.smells.ui.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.agglomeration.SmellyGraphBuilder;
import br.pucrio.opus.smells.collector.ClassLevelSmellDetector;
import br.pucrio.opus.smells.collector.MethodLevelSmellDetector;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.filechanges.FileChangesManager;
import br.pucrio.opus.smells.gson.ObservableExclusionStrategy;
import br.pucrio.opus.smells.metrics.MethodMetricValueCollector;
import br.pucrio.opus.smells.metrics.TypeMetricValueCollector;
import br.pucrio.opus.smells.patterns.SmellPatternsFinder;
import br.pucrio.opus.smells.resources.JavaFilesFinder;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.SourceFile;
import br.pucrio.opus.smells.resources.SourceFilesLoader;
import br.pucrio.opus.smells.resources.Type;
import br.pucrio.opus.smells.ui.util.ExperimentalData;

public class DataCollectionController {

	private List<Type> allTypes = new ArrayList<>();
	private SmellyGraph graph = new SmellyGraph();
	private SmellPatternsFinder patternsFinder = new SmellPatternsFinder();
	FileChangesManager changesManager = null;
	private ExperimentalData experimentalData = null;

	public void collectData(String sourcePath) throws IOException, InterruptedException {
		loadAllTypes(sourcePath);
		collectTypeMetrics();
		detectSmells();
		buildGraph();
		findSmellPatterns();
		loadChangeData(sourcePath);
		setExperimentalData();
	}

	private void loadChangeData(String sourcePath) {
		changesManager = new FileChangesManager(allTypes);
		ProcessBuilder builder = new ProcessBuilder("git", "log", "--all", "--name-only", "--pretty=\"format:\"",
				"*.java");
		builder.directory(new File(sourcePath));
		builder.redirectErrorStream(true);
		Process process;
		try {
			process = builder.start();
			List<String> changedFiles = getResults(process);
			for (String filePath : changedFiles) {
				changesManager.incrementChangesOf(filePath);
			}
		} catch (IOException e) {
			System.out.println("Unable to collect source code change data from git.");
			System.out.println(e.getMessage());
		}
	}

	private List<String> getResults(Process process) throws IOException {
		List<String> resultingLines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			resultingLines.add(line);
		}
		return resultingLines;
	}

	private void setExperimentalData() {
		experimentalData = new ExperimentalData(allTypes, patternsFinder.getSingleSmellPatterns(),
				patternsFinder.getMultipleSmellsPatterns(), changesManager.getMapOfFileChanges());
	}

	public ExperimentalData getExperimentalData() {
		return this.experimentalData;
	}

	private void findSmellPatterns() {
		patternsFinder.findPatterns(allTypes, graph);
	}

	private void buildGraph() {
		SmellyGraphBuilder builder = new SmellyGraphBuilder();
		builder.addTypeAndItsMethods(allTypes);
		graph = builder.build();
	}

	private void collectMethodMetrics(Type type) {
		for (Method method : type.getMethods()) {
			MethodMetricValueCollector methodCollector = new MethodMetricValueCollector(
					type.getNodeAsTypeDeclaration());
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
			// It is important for some detectors that method-level smells are collected
			// first
			for (Method method : type.getMethods()) {
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

	private <T> void exportToJson(List<T> data, String fileName) throws IOException {
		String workingDir = System.getProperty("user.dir");
		File smellsFile = new File(workingDir + File.separator + fileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(smellsFile));

		GsonBuilder builder = new GsonBuilder();
		builder.addSerializationExclusionStrategy(new ObservableExclusionStrategy());
		builder.disableHtmlEscaping();
		builder.setPrettyPrinting();
		builder.serializeNulls();

		Gson gson = builder.create();
		gson.toJson(data, writer);
		writer.close();
	}

}
