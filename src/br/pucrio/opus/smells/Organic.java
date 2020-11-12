package br.pucrio.opus.smells;

import java.util.Scanner;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import br.pucrio.opus.smells.ui.controllers.ExperimentController;

public class Organic implements IApplication {

	ExperimentController controller = new ExperimentController();

	@Override
	public Object start(IApplicationContext context) throws Exception {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Main window = new Main();
//					window.getFrmSoftwareMaintainabilityExperiment().setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		
		System.out.println("OPUS Research Group");
		System.out.println("Source Code Degradation Experiment");
		System.out.println("Please provide the path to the source code folder (without test folder):");
		Scanner scanner = new Scanner(System.in);
		String sourcePath = scanner.nextLine();
		
		controller.startExperiment(sourcePath);
		
		scanner.close();
		return EXIT_OK;
	}

	@Override
	public void stop() {

	}

}
