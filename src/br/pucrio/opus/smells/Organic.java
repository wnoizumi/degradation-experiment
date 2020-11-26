package br.pucrio.opus.smells;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import br.pucrio.opus.smells.ui.controllers.CasesEvaluationController;
import br.pucrio.opus.smells.ui.controllers.DataCollectionController;

public class Organic {

	DataCollectionController dataCollectionController = new DataCollectionController();
	CasesEvaluationController evaluationController = new CasesEvaluationController();

	private static Organic instance = null;
	
	private Organic(){}

	public static Organic getInstance(){
		if (instance == null){
			instance = new Organic();
		}
		return instance;
	}
	
	public void start() throws IOException, InterruptedException {
		Scanner scanner = new Scanner(System.in);

		System.out.println("OPUS Research Group");
		System.out.println("Source Code Degradation Experiment");
		System.out.println("Please provide the path to the source code folder (without test folder):");
		String sourcePath = scanner.nextLine();
		while (!new File(sourcePath).isDirectory()) {
			System.out.println("Unable to read the provided path.");
			System.out.println("Please provide the path to the source code folder (without test folder):");
			sourcePath = scanner.nextLine();
		} 
		dataCollectionController.collectData(sourcePath);
		evaluationController.startEvaluations(dataCollectionController.getExperimentalData());
		
		scanner.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Organic instance = Organic.getInstance();
		instance.start();
	}
}
