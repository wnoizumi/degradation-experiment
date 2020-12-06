package br.pucrio.opus.smells.ui.controllers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JOptionPane;

import br.pucrio.opus.smells.ui.util.Case;
import br.pucrio.opus.smells.ui.util.ExperimentalData;
import br.pucrio.opus.smells.ui.windows.ShowPatternCase;

public class CasesEvaluationController {

	private static Object lock = new Object();

	public void startEvaluations(ExperimentalData data) throws InterruptedException {
		ArrayList<Case> selectedCases = data.getSelectedCases();
		Collections.shuffle(selectedCases, new Random());
		for (Case c : selectedCases) {
			System.out.println("Started Analyzing Case #" + c.getCaseNumber());
			showPatternCase(c);
			saveCaseData(c);
			System.out.println("Finished Analyzing Case #" + c.getCaseNumber());
		}
		System.out.println("Finished all cases. Thank you!!!");
	}

	private void saveCaseData(Case c) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("case_" + c.getCaseNumber()));
			writer.write(c.getCaseDescription());
			writer.close();
		} catch (IOException e) {
			System.out.println("Failed to save case data file:");
			System.out.println(c.getCaseDescription());
		}
	}

	private void showPatternCase(Case caseToShow) throws InterruptedException {
		ShowPatternCase frame = new ShowPatternCase(caseToShow);
		frame.setVisible(true);

		Thread t = new Thread() {
			public void run() {
				synchronized (lock) {
					while (frame.isVisible())
						try {
							lock.wait();
						} catch (InterruptedException e) {
							System.out.println(e.getMessage());
						}
				}
			}
		};
		t.start();

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				synchronized (lock) {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(caseToShow.getCaseDescription()), null);
					JOptionPane.showMessageDialog(null, "Case data copied to clipboard!");
					frame.setVisible(false);
					lock.notify();
				}
			}

		});

		t.join();
	}
}
