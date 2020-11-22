package br.pucrio.opus.smells.ui.controllers;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.pucrio.opus.smells.agglomeration.SmellyGraph;
import br.pucrio.opus.smells.agglomeration.SmellyGraphBuilder;
import br.pucrio.opus.smells.collector.ClassLevelSmellDetector;
import br.pucrio.opus.smells.collector.MethodLevelSmellDetector;
import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.gson.ObservableExclusionStrategy;
import br.pucrio.opus.smells.metrics.MethodMetricValueCollector;
import br.pucrio.opus.smells.metrics.TypeMetricValueCollector;
import br.pucrio.opus.smells.patterns.SmellPatternsFinder;
import br.pucrio.opus.smells.patterns.model.PatternModel;
import br.pucrio.opus.smells.resources.JavaFilesFinder;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.SourceFile;
import br.pucrio.opus.smells.resources.SourceFilesLoader;
import br.pucrio.opus.smells.resources.Type;
import br.pucrio.opus.smells.ui.windows.ShowPatternCase;

public class ExperimentController {

	private static Object lock = new Object();
	
	private List<Type> allTypes = new ArrayList<>();
	private SmellyGraph graph = new SmellyGraph();
	SmellPatternsFinder patternsFinder = new SmellPatternsFinder();

	public void collectData(String sourcePath) throws IOException, InterruptedException {
		loadAllTypes(sourcePath);
		collectTypeMetrics();
		detectSmells();
		buildGraph();
		findSmellPatterns();
		startExperiment();
	}

	private void startExperiment() throws InterruptedException {
		PatternModel patternToShow = null;
		if (patternsFinder.getMultipleSmellsPatterns().size() > 0) {
			patternToShow = patternsFinder.getMultipleSmellsPatterns().get(0);
		} else {
			patternToShow = patternsFinder.getSingleSmellPatterns().get(0);
		}
		
		ShowPatternCase frame = new ShowPatternCase(patternToShow);
		frame.setVisible(true);
		
		Thread t = new Thread() {
	        public void run() {
	            synchronized(lock) {
	                while (frame.isVisible())
	                    try {
	                        lock.wait();
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                System.out.println("Started Experiment");
	            }
	        }
	    };
	    t.start();

	    frame.addWindowListener(new WindowAdapter() {

	        @Override
	        public void windowClosing(WindowEvent arg0) {
	            synchronized (lock) {
	                frame.setVisible(false);
	                lock.notify();
	            }
	        }

	    });

	    t.join();
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
