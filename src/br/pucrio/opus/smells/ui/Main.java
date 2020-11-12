package br.pucrio.opus.smells.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import br.pucrio.opus.smells.ui.controllers.ExperimentController;

public class Main {

	private JFrame frmSoftwareMaintainabilityExperiment;
	private JTextField fieldSourcePath;
	private ExperimentController experimentController;

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		experimentController = new ExperimentController();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSoftwareMaintainabilityExperiment = new JFrame();
		frmSoftwareMaintainabilityExperiment.setTitle("Structural Degradation Experiment");
		frmSoftwareMaintainabilityExperiment.setBounds(100, 100, 800, 600);
		frmSoftwareMaintainabilityExperiment.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmSoftwareMaintainabilityExperiment.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("Source Path:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		frmSoftwareMaintainabilityExperiment.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		fieldSourcePath = new JTextField();
		GridBagConstraints gbc_fieldSourcePath = new GridBagConstraints();
		gbc_fieldSourcePath.insets = new Insets(0, 0, 5, 0);
		gbc_fieldSourcePath.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldSourcePath.gridx = 2;
		gbc_fieldSourcePath.gridy = 1;
		frmSoftwareMaintainabilityExperiment.getContentPane().add(fieldSourcePath, gbc_fieldSourcePath);
		fieldSourcePath.setColumns(10);
		
		JButton btnStartExperiment = new JButton("Start Experiment");
		btnStartExperiment.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String sourcePath = fieldSourcePath.getText();
					experimentController.startExperiment(sourcePath);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
			}
		});
		btnStartExperiment.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_btnStartExperiment = new GridBagConstraints();
		gbc_btnStartExperiment.gridx = 2;
		gbc_btnStartExperiment.gridy = 3;
		frmSoftwareMaintainabilityExperiment.getContentPane().add(btnStartExperiment, gbc_btnStartExperiment);
	}
	
	public JFrame getFrmSoftwareMaintainabilityExperiment() {
		return this.frmSoftwareMaintainabilityExperiment;
	}

}
