package br.pucrio.opus.smells.ui.windows;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import br.pucrio.opus.smells.patterns.model.PatternKind;
import br.pucrio.opus.smells.resources.Method;
import br.pucrio.opus.smells.resources.Resource;
import br.pucrio.opus.smells.ui.util.Case;
import br.pucrio.opus.smells.ui.util.DegradationInfoProvider;
import br.pucrio.opus.smells.ui.util.MetricInformationProvider;
import br.pucrio.opus.smells.ui.util.MetricValueTuple;
import br.pucrio.opus.smells.ui.util.MultipleSmellsPatternCase;
import br.pucrio.opus.smells.ui.util.RefactoringsSuggestionProvider;
import br.pucrio.opus.smells.ui.util.SingleSmellsPatternCase;
import br.pucrio.opus.smells.ui.util.SmellInformationProvider;

public class ShowPatternCase extends JFrame {

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
	private Case caseToShow;

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

	public ShowPatternCase(Case caseToShow) throws IOException {
		this();
		this.caseToShow = caseToShow;
		this.setTitle("Case #" + caseToShow.getCaseNumber());
		cleanAllDynamicInformation();
		fillClassesTree(caseToShow.getType());
		fillSourceCode(caseToShow.getType().getAbsoluteFilePath());
		fillDegradationInformation(caseToShow);

	}

	private void fillDegradationInformation(Case caseToShow) {
		if (caseToShow instanceof MultipleSmellsPatternCase) {
			PatternKind kind = ((MultipleSmellsPatternCase) caseToShow).getPattern().getKind();
			degradationInfoTextArea.setText(DegradationInfoProvider.getInfoFor(kind));
		} else if (caseToShow instanceof SingleSmellsPatternCase) {
			PatternKind kind = ((SingleSmellsPatternCase) caseToShow).getPattern().getKind();
			degradationInfoTextArea.setText(DegradationInfoProvider.getInfoFor(kind));
		} else {
			degradationInfoTextArea.setText("No degradation information available.");
		}
	}

	private void cleanAllDynamicInformation() {
		DefaultMutableTreeNode topEmptyNode = new DefaultMutableTreeNode("");
		DefaultTreeModel emptyTreeModel = new DefaultTreeModel(topEmptyNode);
		metricsTree.setModel(emptyTreeModel);
		smellsTree.setModel(emptyTreeModel);
		informationTextArea.setText("");
		refactoringTextArea.setText("");

		Highlighter h = sourceTextArea.getHighlighter();
		h.removeAllHighlights();
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
			if (method.getSmells().size() > 0) {
				DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method);
				rootClassNode.add(methodNode);
			}
		}

		classesTree.expandRow(0);

		classesTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) classesTree.getLastSelectedPathComponent();

				if (node == null)
					return;

				cleanAllDynamicInformation();
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
		refactoringTextArea.setText("");
		informationTextArea.setText(MetricInformationProvider.getInfoFor(tuple.getMetricName()));
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
	}

	protected void fillSmellInformation(Smell smell) {
		refactoringTextArea.setText(RefactoringsSuggestionProvider.getInfoFor(smell));
		informationTextArea.setText(SmellInformationProvider.getInfoFor(smell));
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

		metricsTree = new JTree();
		JScrollPane metricsScrollPane = new JScrollPane(metricsTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		additionalInfoTabbedPane.addTab("Metrics", null, metricsScrollPane, null);

		JTabbedPane informationTabbedPane = new JTabbedPane(JTabbedPane.TOP);

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

		btnFinishCase = new JButton("Copy case data to clipboard");
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

		degradationInfoTextArea = new JTextArea();
		degradationInfoTextArea.setLineWrap(true);
		degradationInfoTextArea.setWrapStyleWord(true);
		degradationInfoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		degradationInfoTextArea.setEditable(false);
		JScrollPane degradationInfoScrollPane = new JScrollPane(degradationInfoTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		additionalInfoTabbedPane.addTab("Degradation Info", null, degradationInfoScrollPane, null);
		contentPane.setLayout(gl_contentPane);
	}

	private ActionListener finishCaseAction() {
		return e -> {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(caseToShow.getCaseDescription()), null);
				JOptionPane.showMessageDialog(this, "Case data copied to clipboard!");
		};
	}
}
