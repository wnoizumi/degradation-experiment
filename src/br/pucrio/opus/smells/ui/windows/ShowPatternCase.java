package br.pucrio.opus.smells.ui.windows;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.metrics.MetricName;
import br.pucrio.opus.smells.patterns.model.PatternModel;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.Resource;

public class ShowPatternCase extends JFrame {

	private JPanel contentPane;
	private JTree classesTree;
	private JTree smellsTree;
	private JTextArea informationTextArea;
	private JTextArea refactoringTextArea;
	private JTree metricsTree;
	private JButton btnFinishCase;
	private JScrollPane sourceScrollPane;
	private JTextArea sourceTextArea;

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

	public ShowPatternCase(PatternModel model) throws IOException {
		this();

		fillClassesTree(model.getRootType());
		fillSourceCode(model.getRootType().getAbsoluteFilePath());
	}

	private void fillSourceCode(String absoluteFilePath) throws IOException {
		List<String> allLines = Files.readAllLines(Paths.get(absoluteFilePath));
		StringBuilder builder = new StringBuilder();
		for (String line : allLines) {
			builder.append(line + System.lineSeparator());
		}
		sourceTextArea.setText(builder.toString());
	}

	private void fillClassesTree(br.pucrio.opus.smells.resources.Type type) {
		DefaultMutableTreeNode topClassesNode = new DefaultMutableTreeNode("Degradaded Elements");
		DefaultTreeModel classesTreeModel = new DefaultTreeModel(topClassesNode);
		classesTree.setModel(classesTreeModel);
		DefaultMutableTreeNode rootClassNode = new DefaultMutableTreeNode(type);
		topClassesNode.add(rootClassNode);
		for (Method method : type.getMethods()) {
			DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method);
			rootClassNode.add(methodNode);
		}

		classesTree.expandRow(0);

		classesTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) classesTree.getLastSelectedPathComponent();

				if (node == null)
					return;

				Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof br.pucrio.opus.smells.resources.Type) {
					fillAllClassInformation((br.pucrio.opus.smells.resources.Type) nodeInfo);
				} else if (nodeInfo instanceof Method) {
					fillAllMethodInformation((Method) nodeInfo);
					selectMethodLines((Method) nodeInfo);
				}

			}
		});
	}

	protected void selectMethodLines(Method method) {
		//TODO select the method in the source code text area
	}

	protected void fillAllMethodInformation(Method method) {
		fillSmellsTree(method);
		fillMetricsTree(method);
	}

	protected void fillAllClassInformation(br.pucrio.opus.smells.resources.Type type) {
		fillSmellsTree(type);
		fillMetricsTree(type);
	}

	private void fillMetricsTree(Resource resource) {
		String topNodeDescription = "";
		if (resource instanceof br.pucrio.opus.smells.resources.Type) {
			topNodeDescription = "Class Metrics";
		} else {
			topNodeDescription = "Method Metrics";
		}
		DefaultMutableTreeNode topMetricsNode = new DefaultMutableTreeNode(topNodeDescription);
		DefaultTreeModel metricsTreeModel = new DefaultTreeModel(topMetricsNode);
		metricsTree.setModel(metricsTreeModel);

		for (MetricName metricName : MetricName.values()) {
			Double metricValue = resource.getMetricValue(metricName);
			if (metricValue != null) {
				DefaultMutableTreeNode metricNode = new DefaultMutableTreeNode(
						metricName.getLabel() + " : " + metricValue);
				topMetricsNode.add(metricNode);
			}
		}

		metricsTree.expandRow(0);
	}

	private void fillSmellsTree(Resource resource) {
		String topNodeDescription = "";
		if (resource instanceof br.pucrio.opus.smells.resources.Type) {
			topNodeDescription = "Class Smells";
		} else {
			topNodeDescription = "Method Smells";
		}
		DefaultMutableTreeNode topSmellsNode = new DefaultMutableTreeNode(topNodeDescription);
		DefaultTreeModel smellsTreeModel = new DefaultTreeModel(topSmellsNode);
		smellsTree.setModel(smellsTreeModel);

		for (Smell smell : resource.getSmells()) {
			DefaultMutableTreeNode smellNode = new DefaultMutableTreeNode(smell);
			topSmellsNode.add(smellNode);
		}

		if (resource.getSmells().size() > 0)
			smellsTree.expandRow(0);
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
		JScrollPane classesScrollPane = new JScrollPane(classesTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		classestabbedPane.addTab("Classes", null, classesScrollPane, null);

		JTabbedPane smellsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		smellsTabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));

		smellsTree = new JTree();
		JScrollPane smellsScrollPane = new JScrollPane(smellsTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		smellsTabbedPane.addTab("Smells", null, smellsScrollPane, null);

		JTabbedPane additionalInfoTabbedPane = new JTabbedPane(JTabbedPane.TOP);

		metricsTree = new JTree();
		JScrollPane metricsScrollPane = new JScrollPane(metricsTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		additionalInfoTabbedPane.addTab("Metrics", null, metricsScrollPane, null);

		refactoringTextArea = new JTextArea();
		JScrollPane refactoringScrollPane = new JScrollPane(refactoringTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		additionalInfoTabbedPane.addTab("Refactorings Suggestion", null, refactoringScrollPane, null);

		JTabbedPane informationTabbedPane = new JTabbedPane(JTabbedPane.TOP);

		informationTextArea = new JTextArea();
		informationTextArea.setEditable(false);
		informationTabbedPane.addTab("Information", null, informationTextArea, null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		btnFinishCase = new JButton("Finish Case");
		btnFinishCase.setActionCommand("Finish Case");

		sourceScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup()
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
						.createSequentialGroup()
						.addComponent(additionalInfoTabbedPane, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
						.addGap(5)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(informationTabbedPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup().addGap(178).addComponent(tabbedPane,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(btnFinishCase,
								GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
										.addComponent(smellsTabbedPane).addComponent(classestabbedPane,
												GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(sourceScrollPane, GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)))
				.addGap(9)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(classestabbedPane, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
										.addGap(5)
										.addComponent(smellsTabbedPane, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
								.addComponent(sourceScrollPane, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
						.addGap(5)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(additionalInfoTabbedPane, GroupLayout.PREFERRED_SIZE, 198,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(informationTabbedPane, GroupLayout.PREFERRED_SIZE, 198,
										GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup().addGap(96).addComponent(tabbedPane,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)))
						.addGap(5)
						.addComponent(btnFinishCase, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
						.addGap(8)));

		sourceTextArea = new JTextArea();
		sourceTextArea.setEditable(false);
		sourceTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		sourceScrollPane.setViewportView(sourceTextArea);

		JTextArea degradationInfoTextArea = new JTextArea();
		degradationInfoTextArea.setEditable(false);
		additionalInfoTabbedPane.addTab("Degradation Info", null, degradationInfoTextArea, null);
		contentPane.setLayout(gl_contentPane);
	}
}
