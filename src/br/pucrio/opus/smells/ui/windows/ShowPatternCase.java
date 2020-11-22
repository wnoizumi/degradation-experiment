package br.pucrio.opus.smells.ui.windows;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import br.pucrio.opus.smells.patterns.model.PatternModel;

public class ShowPatternCase extends JFrame {

	private JPanel contentPane;
	private JTree classesTree;
	private JTree smellsTree;
	private JTextArea informationTextArea;
	private JTextArea refactoringTextArea;
	private JTree metricsTree;
	private JTextArea sourceTextArea;
	private JButton btnFinishCase;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowPatternCase frame = new ShowPatternCase();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ShowPatternCase(PatternModel model) {
		this();
		DefaultMutableTreeNode topClassesNode = new DefaultMutableTreeNode("Degradaded Elements");
		DefaultTreeModel classesTreeModel = new DefaultTreeModel(topClassesNode);
		classesTree.setModel(classesTreeModel);
		topClassesNode.add(new DefaultMutableTreeNode(model.getRootType().getFullyQualifiedName()));
	}

	/**
	 * Create the frame.
	 */
	public ShowPatternCase() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1003, 736);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JTabbedPane classestabbedPane = new JTabbedPane(JTabbedPane.TOP);
		classestabbedPane.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		classesTree = new JTree();
		JScrollPane classesScrollPane = new JScrollPane(classesTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		classestabbedPane.addTab("Classes", null, classesScrollPane, null);
		
		sourceTextArea = new JTextArea();
		sourceTextArea.setEditable(false);
		
		JTabbedPane smellsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		smellsTabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		smellsTree = new JTree();
		smellsTabbedPane.addTab("Smells", null, smellsTree, null);
		
		JTabbedPane additionalInfoTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		metricsTree = new JTree();
		additionalInfoTabbedPane.addTab("Metrics", null, metricsTree, null);
		
		refactoringTextArea = new JTextArea();
		additionalInfoTabbedPane.addTab("Refactorings Suggestion", null, refactoringTextArea, null);
		
		JTabbedPane informationTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		informationTextArea = new JTextArea();
		informationTextArea.setEditable(false);
		informationTabbedPane.addTab("Information", null, informationTextArea, null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		btnFinishCase = new JButton("Finish Case");
		btnFinishCase.setActionCommand("Finish Case");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(smellsTabbedPane, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
								.addComponent(classestabbedPane, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sourceTextArea, GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(additionalInfoTabbedPane, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
							.addGap(5)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(informationTabbedPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(178)
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnFinishCase, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)))
					.addGap(9))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(classestabbedPane, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
							.addGap(5)
							.addComponent(smellsTabbedPane, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
						.addComponent(sourceTextArea, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
					.addGap(5)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(additionalInfoTabbedPane, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
						.addComponent(informationTabbedPane, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(96)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(5)
					.addComponent(btnFinishCase, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
		);
		
		JTextArea degradationInfoTextArea = new JTextArea();
		degradationInfoTextArea.setEditable(false);
		additionalInfoTabbedPane.addTab("Degradation Info", null, degradationInfoTextArea, null);
		contentPane.setLayout(gl_contentPane);
	}
}
