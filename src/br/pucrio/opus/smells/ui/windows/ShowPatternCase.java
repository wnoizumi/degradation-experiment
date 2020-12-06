package br.pucrio.opus.smells.ui.windows;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import br.pucrio.opus.smells.collector.Smell;
import br.pucrio.opus.smells.metrics.MetricName;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.Resource;
import br.pucrio.opus.smells.ui.util.Case;
import br.pucrio.opus.smells.ui.util.MetricInformationProvider;
import br.pucrio.opus.smells.ui.util.MetricValueTuple;
import br.pucrio.opus.smells.ui.util.MultipleSmellsPatternCase;
import br.pucrio.opus.smells.ui.util.RefactoringsSuggestionProvider;
import br.pucrio.opus.smells.ui.util.SmellInformationProvider;

public class ShowPatternCase extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTree classesTree;
	private JTree smellsTree;
	private JTextArea informationTextArea;
	private JTextArea refactoringTextArea;
	private JTree metricsTree;
	private JButton btnFinishCase;
	private RTextScrollPane sourceScrollPane;
	private RSyntaxTextArea sourceTextArea;
	private JTextArea degradationInfoTextArea;
	private JTabbedPane informationTabbedPane;
	private Map<Method, br.pucrio.opus.smells.resources.Type> typesOfMethods = new  HashMap<>();
	private Map<br.pucrio.opus.smells.resources.Type, String> sourcesOfTypes = new HashMap<>();

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

	public ShowPatternCase(Case caseToShow) {
		this();
		this.setTitle("Case #" + caseToShow.getCaseNumber());
		retrieveAllSources(caseToShow);
		cleanAllDynamicInformation();
		fillClassesTree(caseToShow);
		fillDegradationInformation(caseToShow);
	}

	private void fillDegradationInformation(Case caseToShow) {
		//We only show degradation information in even cases.
		if (caseToShow.getCaseNumber() % 2 == 0) {
			degradationInfoTextArea.setText(caseToShow.getDegradationInfo());
		} else {
			degradationInfoTextArea.setText("No degradation information.");
		}
	}

	private void cleanAllDynamicInformation() {
		DefaultTreeModel emptyTreeModel = new DefaultTreeModel(null);
		metricsTree.setModel(emptyTreeModel);
		smellsTree.setModel(emptyTreeModel);
		informationTextArea.setText("");
		refactoringTextArea.setText("");

		Highlighter h = sourceTextArea.getHighlighter();
		h.removeAllHighlights();
	}

	private void fillSourceCode(Resource type) {
		String source = sourcesOfTypes.get(type);
		if (source != null) {
			sourceTextArea.setText(source);
		} else {
			sourceTextArea.setText("");
		}
	}
	
	private void retrieveAllSources(Case caseToShow) {
		String source = retrieveSource(caseToShow.getType());
		sourcesOfTypes.put(caseToShow.getType(), source);
		
		if (caseToShow instanceof MultipleSmellsPatternCase) {
			HashSet<br.pucrio.opus.smells.resources.Type> relatedTypes = ((MultipleSmellsPatternCase)caseToShow).getPattern().getRelatedTypes();
			for (br.pucrio.opus.smells.resources.Type type : relatedTypes) {
				source = retrieveSource(type);
				sourcesOfTypes.put(type, source);
			}
		}
	}

	private String retrieveSource(Resource type) {
		List<String> allLines = new ArrayList<>();
		try {
			allLines = Files.readAllLines(Paths.get(type.getAbsoluteFilePath()));
		} catch (IOException e) {
			System.out.println("Failed to load the source code.");
			System.out.println(e.getStackTrace());
		}
		StringBuilder builder = new StringBuilder();
		for (String line : allLines) {
			builder.append(line + System.lineSeparator());
		}
		
		return builder.toString();
	}

	private void fillClassesTree(Case caseToShow) {
		br.pucrio.opus.smells.resources.Type rootType = caseToShow.getType();
		DefaultMutableTreeNode topClassesNode = new DefaultMutableTreeNode("Degradaded Elements");
		DefaultTreeModel classesTreeModel = new DefaultTreeModel(topClassesNode);
		classesTree.setModel(classesTreeModel);
		DefaultMutableTreeNode rootClassNode = new DefaultMutableTreeNode(rootType);
		topClassesNode.add(rootClassNode);
		for (Method method : rootType.getMethods()) {
			if (method.getSmells().size() > 0) {
				DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method);
				rootClassNode.add(methodNode);
				typesOfMethods.put(method, rootType);
			}
		}
		
		if (caseToShow instanceof MultipleSmellsPatternCase) {
			MultipleSmellsPatternCase multipleSmellsCase = (MultipleSmellsPatternCase)caseToShow;
			for (br.pucrio.opus.smells.resources.Type t : multipleSmellsCase.getPattern().getRelatedTypes()) {
				DefaultMutableTreeNode relatedNode = new DefaultMutableTreeNode(t);
				topClassesNode.add(relatedNode);
				
				for (Method m : t.getMethods()) {
					if (m.getSmells().size() > 0) {
						DefaultMutableTreeNode mNode = new DefaultMutableTreeNode(m);
						relatedNode.add(mNode);
						typesOfMethods.put(m, t);
					}
				}
			}
		}
		
		classesTree.expandRow(0);
		
		fillSourceCode(caseToShow.getType());

		classesTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) classesTree.getLastSelectedPathComponent();

				if (node == null)
					return;

				cleanAllDynamicInformation();
				Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof br.pucrio.opus.smells.resources.Type) {
					fillSourceCode(((Resource) nodeInfo));
					focusOnTopOfSourceCode();
					fillAllClassInformation((Resource) nodeInfo);
				} else if (nodeInfo instanceof Method) {
					Resource type = typesOfMethods.get(nodeInfo);
					fillSourceCode(type);
					fillAllMethodInformation((Method) nodeInfo);
					selectMethodLines((Method) nodeInfo);
				}
			}
		});
	}

	protected void focusOnTopOfSourceCode() {
		sourceTextArea.requestFocusInWindow();
		Highlighter h = sourceTextArea.getHighlighter();
		h.removeAllHighlights();
		sourceTextArea.setCaretPosition(0);
	}

	protected void selectMethodLines(Method method) {
		char lineSeparator = '\n';
		String text = sourceTextArea.getText();
		int lineCount = 1;
		int startPosition = 0;
		int endPosition = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == lineSeparator) {
				lineCount++;
			}
			if (lineCount == method.getStartLineNumber() - 1) {
				startPosition = i;
			}
			if (lineCount == method.getEndLineNumber() + 1) {
				endPosition = i;
				break;
			}
		}

		sourceTextArea.requestFocusInWindow();

		Highlighter h = sourceTextArea.getHighlighter();
		h.removeAllHighlights();
		try {
			h.addHighlight(startPosition, endPosition, DefaultHighlighter.DefaultPainter);
		} catch (BadLocationException e) {
			System.out.println("Failed to highlight source code position");
			System.out.println(e.getMessage());
		}

		int focusPosition = (startPosition + endPosition) / 2;
		sourceTextArea.setCaretPosition(focusPosition);
	}

	protected void fillAllMethodInformation(Method method) {
		fillSmellsTree(method);
		fillMetricsTree(method);
	}

	protected void fillAllClassInformation(Resource type) {
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
			if (metricValue != null && metricName != MetricName.IsAbstract) {
				DefaultMutableTreeNode metricNode = new DefaultMutableTreeNode(
						new MetricValueTuple(metricName, metricValue));
				topMetricsNode.add(metricNode);
			}
		}

		metricsTree.expandRow(0);

		metricsTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) metricsTree.getLastSelectedPathComponent();

				if (node == null)
					return;

				Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof MetricValueTuple) {
					fillMetricInformation((MetricValueTuple) nodeInfo);
				}
			}
		});
	}

	protected void fillMetricInformation(MetricValueTuple tuple) {
		informationTextArea.setText(MetricInformationProvider.getInfoFor(tuple.getMetricName()));
		informationTabbedPane.setSelectedIndex(0);
	}

	private void fillSmellsTree(Resource resource) {
		if (resource.getSmells().size() > 0) {
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

			smellsTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) smellsTree.getLastSelectedPathComponent();

					if (node == null)
						return;

					Object nodeInfo = node.getUserObject();
					if (nodeInfo instanceof Smell) {
						fillSmellInformation((Smell) nodeInfo);
					}
				}
			});
		} else {
			smellsTree.setModel(null);
		}
	}

	protected void fillSmellInformation(Smell smell) {
		refactoringTextArea.setText(RefactoringsSuggestionProvider.getInfoFor(smell));
		informationTextArea.setText(SmellInformationProvider.getInfoFor(smell));
		informationTabbedPane.setSelectedIndex(0);
	}

	/**
	 * Create the frame.
	 */
	public ShowPatternCase() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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

		degradationInfoTextArea = new JTextArea();
		degradationInfoTextArea.setLineWrap(true);
		degradationInfoTextArea.setWrapStyleWord(true);
		degradationInfoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		degradationInfoTextArea.setEditable(false);
		JScrollPane degradationInfoScrollPane = new JScrollPane(degradationInfoTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		additionalInfoTabbedPane.addTab("Degradation Info", null, degradationInfoScrollPane, null);
		
		metricsTree = new JTree();
		JScrollPane metricsScrollPane = new JScrollPane(metricsTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		additionalInfoTabbedPane.addTab("Metrics", null, metricsScrollPane, null);

		informationTabbedPane = new JTabbedPane(JTabbedPane.TOP);

		informationTextArea = new JTextArea();
		informationTextArea.setLineWrap(true);
		informationTextArea.setWrapStyleWord(true);
		informationTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		informationTextArea.setEditable(false);
		JScrollPane informationScrollPane = new JScrollPane(informationTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		informationTabbedPane.addTab("Smell/Metric Information", null, informationScrollPane, null);

		refactoringTextArea = new JTextArea();
		refactoringTextArea.setWrapStyleWord(true);
		refactoringTextArea.setLineWrap(true);
		refactoringTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		JScrollPane refactoringScrollPane = new JScrollPane(refactoringTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		informationTabbedPane.addTab("Refactorings Suggestion", null, refactoringScrollPane, null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		btnFinishCase = new JButton("Copy Data and Open Next Case");
		btnFinishCase.addActionListener(finishCaseAction());
		btnFinishCase.setActionCommand("Finish Case");

		sourceScrollPane = new RTextScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(additionalInfoTabbedPane, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
							.addGap(5)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(informationTabbedPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(178)
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(smellsTabbedPane)
								.addComponent(classestabbedPane, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(sourceScrollPane, GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)))
					.addGap(9))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(661, Short.MAX_VALUE)
					.addComponent(btnFinishCase, GroupLayout.PREFERRED_SIZE, 308, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(classestabbedPane, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
							.addGap(5)
							.addComponent(smellsTabbedPane, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
						.addComponent(sourceScrollPane, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
					.addGap(5)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(additionalInfoTabbedPane, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
						.addComponent(informationTabbedPane, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(96)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(13)
					.addComponent(btnFinishCase, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
		);

		sourceTextArea = new RSyntaxTextArea();
		sourceTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		sourceTextArea.setCodeFoldingEnabled(true);
		sourceTextArea.setEditable(false);
		sourceScrollPane.setViewportView(sourceTextArea);

		contentPane.setLayout(gl_contentPane);
	}

	private ActionListener finishCaseAction() {
		return e -> {
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		};
	}
}
