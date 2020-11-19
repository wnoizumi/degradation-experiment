package br.pucrio.opus.smells;

import java.io.IOException;
import java.util.Scanner;

import br.pucrio.opus.smells.ui.controllers.ExperimentController;

public class Organic {

	ExperimentController controller = new ExperimentController();

	private static Organic instance = null;
	
	private Organic(){}

	public static Organic getInstance(){
		if (instance == null){
			instance = new Organic();
		}
		return instance;
	}
	
	public void start() throws IOException {
		System.out.println("OPUS Research Group");
		System.out.println("Source Code Degradation Experiment");
		System.out.println("Please provide the path to the source code folder (without test folder):");
		Scanner scanner = new Scanner(System.in);
		String sourcePath = scanner.nextLine();
		
		controller.startExperiment(sourcePath);
		
		scanner.close();
	}

	public static void main(String[] args) throws IOException {
		Organic instance = Organic.getInstance();
		instance.start();
	}
}
