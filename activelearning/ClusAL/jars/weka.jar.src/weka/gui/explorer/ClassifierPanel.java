package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.CostCurve;
import weka.classifiers.evaluation.MarginCurve;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.ExtensionFileFilter;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.InstancesSummaryPanel;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.PropertyDialog;
import weka.gui.PropertyPanel;
import weka.gui.ResultHistoryPanel;
import weka.gui.SaveBuffer;
import weka.gui.SelectedTagEditor;
import weka.gui.SetInstancesPanel;
import weka.gui.SysErrLog;
import weka.gui.TaskLogger;
import weka.gui.graphvisualizer.BIFFormatException;
import weka.gui.graphvisualizer.GraphVisualizer;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;
import weka.gui.visualize.VisualizePanel;

public class ClassifierPanel extends JPanel {
  public static String MODEL_FILE_EXTENSION = ".model";
  
  protected GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_CEPanel = new PropertyPanel((PropertyEditor)this.m_ClassifierEditor);
  
  protected JTextArea m_OutText = new JTextArea(20, 40);
  
  protected Logger m_Log = (Logger)new SysErrLog();
  
  SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
  
  protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
  
  protected JComboBox m_ClassCombo = new JComboBox();
  
  protected JRadioButton m_CVBut = new JRadioButton("Cross-validation");
  
  protected JRadioButton m_PercentBut = new JRadioButton("Percentage split");
  
  protected JRadioButton m_TrainBut = new JRadioButton("Use training set");
  
  protected JRadioButton m_TestSplitBut = new JRadioButton("Supplied test set");
  
  protected JCheckBox m_StorePredictionsBut = new JCheckBox("Store predictions for visualization");
  
  protected JCheckBox m_OutputModelBut = new JCheckBox("Output model");
  
  protected JCheckBox m_OutputPerClassBut = new JCheckBox("Output per-class stats");
  
  protected JCheckBox m_OutputConfusionBut = new JCheckBox("Output confusion matrix");
  
  protected JCheckBox m_OutputEntropyBut = new JCheckBox("Output entropy evaluation measures");
  
  protected JCheckBox m_OutputPredictionsTextBut = new JCheckBox("Output predictions");
  
  protected JCheckBox m_EvalWRTCostsBut = new JCheckBox("Cost-sensitive evaluation");
  
  protected JButton m_SetCostsBut = new JButton("Set...");
  
  protected JLabel m_CVLab = new JLabel("Folds", 4);
  
  protected JTextField m_CVText = new JTextField("10");
  
  protected JLabel m_PercentLab = new JLabel("%", 4);
  
  protected JTextField m_PercentText = new JTextField("66");
  
  protected JButton m_SetTestBut = new JButton("Set...");
  
  protected JFrame m_SetTestFrame;
  
  protected PropertyDialog m_SetCostsFrame;
  
  ActionListener m_RadioListener = new ActionListener(this) {
      private final ClassifierPanel this$0;
      
      public void actionPerformed(ActionEvent param1ActionEvent) {
        this.this$0.updateRadioLinks();
      }
    };
  
  JButton m_MoreOptions = new JButton("More options...");
  
  protected JTextField m_RandomSeedText = new JTextField("1      ");
  
  protected JLabel m_RandomLab = new JLabel("Random seed for XVal / % Split", 4);
  
  protected JButton m_StartBut = new JButton("Start");
  
  protected JButton m_StopBut = new JButton("Stop");
  
  private Dimension COMBO_SIZE = new Dimension(150, (this.m_StartBut.getPreferredSize()).height);
  
  protected CostMatrixEditor m_CostMatrixEditor = new CostMatrixEditor();
  
  protected Instances m_Instances;
  
  protected Instances m_TestInstances;
  
  protected Thread m_RunThread;
  
  protected VisualizePanel m_CurrentVis = null;
  
  protected InstancesSummaryPanel m_Summary = null;
  
  protected FileFilter m_ModelFilter = (FileFilter)new ExtensionFileFilter(MODEL_FILE_EXTENSION, "Model object files");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public ClassifierPanel() {
    this.m_OutText.setEditable(false);
    this.m_OutText.setFont(new Font("Monospaced", 0, 12));
    this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_OutText.addMouseListener(new MouseAdapter(this) {
          private final ClassifierPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16)
              this.this$0.m_OutText.selectAll(); 
          }
        });
    this.m_History.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
    this.m_ClassifierEditor.setClassType(Classifier.class);
    this.m_ClassifierEditor.setValue(new ZeroR());
    this.m_ClassifierEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final ClassifierPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.repaint();
          }
        });
    this.m_ClassCombo.setToolTipText("Select the attribute to use as the class");
    this.m_TrainBut.setToolTipText("Test on the same set that the classifier is trained on");
    this.m_CVBut.setToolTipText("Perform a n-fold cross-validation");
    this.m_PercentBut.setToolTipText("Train on a percentage of the data and test on the remainder");
    this.m_TestSplitBut.setToolTipText("Test on a user-specified dataset");
    this.m_StartBut.setToolTipText("Starts the classification");
    this.m_StopBut.setToolTipText("Stops a running classification");
    this.m_StorePredictionsBut.setToolTipText("Store predictions in the result list for later visualization");
    this.m_OutputModelBut.setToolTipText("Output the model obtained from the full training set");
    this.m_OutputPerClassBut.setToolTipText("Output precision/recall & true/false positives for each class");
    this.m_OutputConfusionBut.setToolTipText("Output the matrix displaying class confusions");
    this.m_OutputEntropyBut.setToolTipText("Output entropy-based evaluation measures");
    this.m_EvalWRTCostsBut.setToolTipText("Evaluate errors with respect to a cost matrix");
    this.m_OutputPredictionsTextBut.setToolTipText("Include the predictions in the output buffer");
    this.m_FileChooser.setFileFilter(this.m_ModelFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_StorePredictionsBut.setSelected(true);
    this.m_OutputModelBut.setSelected(true);
    this.m_OutputPerClassBut.setSelected(true);
    this.m_OutputConfusionBut.setSelected(true);
    this.m_ClassCombo.setEnabled(false);
    this.m_ClassCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_ClassCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_ClassCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_CVBut.setSelected(true);
    updateRadioLinks();
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_TrainBut);
    buttonGroup.add(this.m_CVBut);
    buttonGroup.add(this.m_PercentBut);
    buttonGroup.add(this.m_TestSplitBut);
    this.m_TrainBut.addActionListener(this.m_RadioListener);
    this.m_CVBut.addActionListener(this.m_RadioListener);
    this.m_PercentBut.addActionListener(this.m_RadioListener);
    this.m_TestSplitBut.addActionListener(this.m_RadioListener);
    this.m_SetTestBut.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setTestSet();
          }
        });
    this.m_EvalWRTCostsBut.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_SetCostsBut.setEnabled(this.this$0.m_EvalWRTCostsBut.isSelected());
            if (this.this$0.m_SetCostsFrame != null && !this.this$0.m_EvalWRTCostsBut.isSelected())
              this.this$0.m_SetCostsFrame.setVisible(false); 
          }
        });
    this.m_CostMatrixEditor.setValue(new CostMatrix(1));
    this.m_SetCostsBut.setEnabled(this.m_EvalWRTCostsBut.isSelected());
    this.m_SetCostsBut.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_SetCostsBut.setEnabled(false);
            if (this.this$0.m_SetCostsFrame == null) {
              this.this$0.m_SetCostsFrame = new PropertyDialog((PropertyEditor)this.this$0.m_CostMatrixEditor, 100, 100);
              this.this$0.m_SetCostsFrame.addWindowListener((WindowListener)new Object(this));
            } 
            this.this$0.m_SetCostsFrame.setVisible(true);
          }
        });
    this.m_StartBut.setEnabled(false);
    this.m_StopBut.setEnabled(false);
    this.m_StartBut.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.startClassifier();
          }
        });
    this.m_StopBut.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.stopClassifier();
          }
        });
    this.m_ClassCombo.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = this.this$0.m_ClassCombo.getSelectedIndex();
            if (i != -1) {
              boolean bool = this.this$0.m_Instances.attribute(i).isNominal();
              this.this$0.m_OutputPerClassBut.setEnabled(bool);
              this.this$0.m_OutputConfusionBut.setEnabled(bool);
            } 
          }
        });
    this.m_History.setHandleRightClicks(false);
    this.m_History.getList().addMouseListener(new MouseAdapter(this) {
          private final ClassifierPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16 || param1MouseEvent.isAltDown()) {
              int i = this.this$0.m_History.getList().locationToIndex(param1MouseEvent.getPoint());
              if (i != -1) {
                String str = this.this$0.m_History.getNameAtIndex(i);
                this.this$0.visualize(str, param1MouseEvent.getX(), param1MouseEvent.getY());
              } else {
                this.this$0.visualize((String)null, param1MouseEvent.getX(), param1MouseEvent.getY());
              } 
            } 
          }
        });
    this.m_MoreOptions.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_MoreOptions.setEnabled(false);
            JPanel jPanel1 = new JPanel();
            jPanel1.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
            jPanel1.setLayout(new GridLayout(8, 1));
            jPanel1.add(this.this$0.m_OutputModelBut);
            jPanel1.add(this.this$0.m_OutputPerClassBut);
            jPanel1.add(this.this$0.m_OutputEntropyBut);
            jPanel1.add(this.this$0.m_OutputConfusionBut);
            jPanel1.add(this.this$0.m_StorePredictionsBut);
            jPanel1.add(this.this$0.m_OutputPredictionsTextBut);
            JPanel jPanel2 = new JPanel();
            jPanel2.setLayout(new BorderLayout());
            jPanel2.add(this.this$0.m_EvalWRTCostsBut, "West");
            jPanel2.add(this.this$0.m_SetCostsBut, "East");
            jPanel1.add(jPanel2);
            JPanel jPanel3 = new JPanel();
            jPanel3.setLayout(new BorderLayout());
            jPanel3.add(this.this$0.m_RandomLab, "West");
            jPanel3.add(this.this$0.m_RandomSeedText, "East");
            jPanel1.add(jPanel3);
            JPanel jPanel4 = new JPanel();
            jPanel4.setLayout(new BorderLayout());
            JButton jButton = new JButton("OK");
            JPanel jPanel5 = new JPanel();
            jPanel5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            jPanel5.setLayout(new GridLayout(1, 1, 5, 5));
            jPanel5.add(jButton);
            jPanel4.add(jPanel1, "Center");
            jPanel4.add(jPanel5, "South");
            JFrame jFrame = new JFrame("Classifier evaluation options");
            jFrame.getContentPane().setLayout(new BorderLayout());
            jFrame.getContentPane().add(jPanel4, "Center");
            jFrame.addWindowListener((WindowListener)new Object(this, jFrame));
            jButton.addActionListener((ActionListener)new Object(this, jFrame));
            jFrame.pack();
            jFrame.setLocation(this.this$0.m_MoreOptions.getLocationOnScreen());
            jFrame.setVisible(true);
          }
        });
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Classifier"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add((Component)this.m_CEPanel, "North");
    JPanel jPanel2 = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    jPanel2.setLayout(gridBagLayout);
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Test options"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(this.m_TrainBut, gridBagConstraints);
    jPanel2.add(this.m_TrainBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(this.m_TestSplitBut, gridBagConstraints);
    jPanel2.add(this.m_TestSplitBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.insets = new Insets(2, 10, 2, 0);
    gridBagLayout.setConstraints(this.m_SetTestBut, gridBagConstraints);
    jPanel2.add(this.m_SetTestBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(this.m_CVBut, gridBagConstraints);
    jPanel2.add(this.m_CVBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new Insets(2, 10, 2, 10);
    gridBagLayout.setConstraints(this.m_CVLab, gridBagConstraints);
    jPanel2.add(this.m_CVLab);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.ipadx = 20;
    gridBagLayout.setConstraints(this.m_CVText, gridBagConstraints);
    jPanel2.add(this.m_CVText);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(this.m_PercentBut, gridBagConstraints);
    jPanel2.add(this.m_PercentBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new Insets(2, 10, 2, 10);
    gridBagLayout.setConstraints(this.m_PercentLab, gridBagConstraints);
    jPanel2.add(this.m_PercentLab);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.ipadx = 20;
    gridBagLayout.setConstraints(this.m_PercentText, gridBagConstraints);
    jPanel2.add(this.m_PercentText);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.insets = new Insets(3, 0, 1, 0);
    gridBagLayout.setConstraints(this.m_MoreOptions, gridBagConstraints);
    jPanel2.add(this.m_MoreOptions);
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new GridLayout(2, 2));
    jPanel3.add(this.m_ClassCombo);
    this.m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    JPanel jPanel4 = new JPanel();
    jPanel4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jPanel4.setLayout(new GridLayout(1, 2, 5, 5));
    jPanel4.add(this.m_StartBut);
    jPanel4.add(this.m_StopBut);
    jPanel3.add(jPanel4);
    JPanel jPanel5 = new JPanel();
    jPanel5.setBorder(BorderFactory.createTitledBorder("Classifier output"));
    jPanel5.setLayout(new BorderLayout());
    JScrollPane jScrollPane = new JScrollPane(this.m_OutText);
    jPanel5.add(jScrollPane, "Center");
    jScrollPane.getViewport().addChangeListener(new ChangeListener(this) {
          private int lastHeight;
          
          private final ClassifierPanel this$0;
          
          public void stateChanged(ChangeEvent param1ChangeEvent) {
            JViewport jViewport = (JViewport)param1ChangeEvent.getSource();
            int i = (jViewport.getViewSize()).height;
            if (i != this.lastHeight) {
              this.lastHeight = i;
              int j = i - (jViewport.getExtentSize()).height;
              jViewport.setViewPosition(new Point(0, j));
            } 
          }
        });
    JPanel jPanel6 = new JPanel();
    gridBagLayout = new GridBagLayout();
    jPanel6.setLayout(gridBagLayout);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jPanel2, gridBagConstraints);
    jPanel6.add(jPanel2);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 11;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jPanel3, gridBagConstraints);
    jPanel6.add(jPanel3);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.0D;
    gridBagLayout.setConstraints((Component)this.m_History, gridBagConstraints);
    jPanel6.add((Component)this.m_History);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.weighty = 100.0D;
    gridBagLayout.setConstraints(jPanel5, gridBagConstraints);
    jPanel6.add(jPanel5);
    setLayout(new BorderLayout());
    add(jPanel1, "North");
    add(jPanel6, "Center");
  }
  
  protected void updateRadioLinks() {
    this.m_SetTestBut.setEnabled(this.m_TestSplitBut.isSelected());
    if (this.m_SetTestFrame != null && !this.m_TestSplitBut.isSelected())
      this.m_SetTestFrame.setVisible(false); 
    this.m_CVText.setEnabled(this.m_CVBut.isSelected());
    this.m_CVLab.setEnabled(this.m_CVBut.isSelected());
    this.m_PercentText.setEnabled(this.m_PercentBut.isSelected());
    this.m_PercentLab.setEnabled(this.m_PercentBut.isSelected());
  }
  
  public void setLog(Logger paramLogger) {
    this.m_Log = paramLogger;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    String[] arrayOfString = new String[this.m_Instances.numAttributes()];
    for (byte b = 0; b < arrayOfString.length; b++) {
      String str = "";
      switch (this.m_Instances.attribute(b).type()) {
        case 1:
          str = "(Nom) ";
          break;
        case 0:
          str = "(Num) ";
          break;
        case 2:
          str = "(Str) ";
          break;
        default:
          str = "(???) ";
          break;
      } 
      arrayOfString[b] = str + this.m_Instances.attribute(b).name();
    } 
    this.m_ClassCombo.setModel(new DefaultComboBoxModel(arrayOfString));
    if (arrayOfString.length > 0) {
      this.m_ClassCombo.setSelectedIndex(arrayOfString.length - 1);
      this.m_ClassCombo.setEnabled(true);
      this.m_StartBut.setEnabled((this.m_RunThread == null));
      this.m_StopBut.setEnabled((this.m_RunThread != null));
    } else {
      this.m_StartBut.setEnabled(false);
      this.m_StopBut.setEnabled(false);
    } 
  }
  
  protected void setTestSet() {
    if (this.m_SetTestFrame == null) {
      SetInstancesPanel setInstancesPanel = new SetInstancesPanel();
      this.m_Summary = setInstancesPanel.getSummary();
      if (this.m_TestInstances != null)
        setInstancesPanel.setInstances(this.m_TestInstances); 
      setInstancesPanel.addPropertyChangeListener(new PropertyChangeListener(this, setInstancesPanel) {
            private final SetInstancesPanel val$sp;
            
            private final ClassifierPanel this$0;
            
            public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
              this.this$0.m_TestInstances = this.val$sp.getInstances();
            }
          });
      this.m_SetTestFrame = new JFrame("Test Instances");
      this.m_SetTestFrame.getContentPane().setLayout(new BorderLayout());
      this.m_SetTestFrame.getContentPane().add((Component)setInstancesPanel, "Center");
      this.m_SetTestFrame.pack();
    } 
    this.m_SetTestFrame.setVisible(true);
  }
  
  public static void processClassifierPrediction(Instance paramInstance, Classifier paramClassifier, Evaluation paramEvaluation, FastVector paramFastVector1, Instances paramInstances, FastVector paramFastVector2, FastVector paramFastVector3) {
    try {
      double d;
      if (paramFastVector1 != null) {
        Instance instance = (Instance)paramInstance.copy();
        instance.setDataset(paramInstance.dataset());
        instance.setClassMissing();
        Classifier classifier = paramClassifier;
        double[] arrayOfDouble1 = classifier.distributionForInstance(instance);
        d = paramEvaluation.evaluateModelOnce(arrayOfDouble1, paramInstance);
        paramFastVector1.addElement(new NominalPrediction(paramInstance.classValue(), arrayOfDouble1, paramInstance.weight()));
      } else {
        d = paramEvaluation.evaluateModelOnce(paramClassifier, paramInstance);
      } 
      double[] arrayOfDouble = new double[paramInstances.numAttributes()];
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (b < paramInstance.classIndex()) {
          arrayOfDouble[b] = paramInstance.value(b);
        } else if (b == paramInstance.classIndex()) {
          arrayOfDouble[b] = d;
          arrayOfDouble[b + 1] = paramInstance.value(b);
          b++;
        } else {
          arrayOfDouble[b] = paramInstance.value(b - 1);
        } 
      } 
      paramInstances.add(new Instance(1.0D, arrayOfDouble));
      if (paramInstance.classAttribute().isNominal()) {
        if (paramInstance.isMissing(paramInstance.classIndex()) || Instance.isMissingValue(d)) {
          paramFastVector2.addElement(new Integer(2000));
        } else if (d != paramInstance.classValue()) {
          paramFastVector2.addElement(new Integer(1000));
        } else {
          paramFastVector2.addElement(new Integer(-1));
        } 
        paramFastVector3.addElement(new Integer(2));
      } else {
        Double double_ = null;
        if (!paramInstance.isMissing(paramInstance.classIndex()) && !Instance.isMissingValue(d)) {
          double_ = new Double(d - paramInstance.classValue());
          paramFastVector2.addElement(new Integer(-1));
        } else {
          paramFastVector2.addElement(new Integer(2000));
        } 
        paramFastVector3.addElement(double_);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void postProcessPlotInfo(FastVector paramFastVector) {
    byte b1 = 20;
    double d1 = Double.NEGATIVE_INFINITY;
    double d2 = Double.POSITIVE_INFINITY;
    byte b2;
    for (b2 = 0; b2 < paramFastVector.size(); b2++) {
      Double double_ = (Double)paramFastVector.elementAt(b2);
      if (double_ != null) {
        double d = Math.abs(double_.doubleValue());
        if (d < d2)
          d2 = d; 
        if (d > d1)
          d1 = d; 
      } 
    } 
    for (b2 = 0; b2 < paramFastVector.size(); b2++) {
      Double double_ = (Double)paramFastVector.elementAt(b2);
      if (double_ != null) {
        double d = Math.abs(double_.doubleValue());
        if (d1 - d2 > 0.0D) {
          double d3 = (d - d2) / (d1 - d2) * b1;
          paramFastVector.setElementAt(new Integer((int)d3), b2);
        } else {
          paramFastVector.setElementAt(new Integer(1), b2);
        } 
      } else {
        paramFastVector.setElementAt(new Integer(1), b2);
      } 
    } 
  }
  
  public static Instances setUpVisualizableInstances(Instances paramInstances) {
    Attribute attribute1;
    FastVector fastVector = new FastVector();
    Attribute attribute2 = paramInstances.attribute(paramInstances.classIndex());
    if (attribute2.isNominal()) {
      FastVector fastVector1 = new FastVector();
      for (byte b1 = 0; b1 < attribute2.numValues(); b1++)
        fastVector1.addElement(attribute2.value(b1)); 
      attribute1 = new Attribute("predicted" + attribute2.name(), fastVector1);
    } else {
      attribute1 = new Attribute("predicted" + attribute2.name());
    } 
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (b == paramInstances.classIndex())
        fastVector.addElement(attribute1); 
      fastVector.addElement(paramInstances.attribute(b).copy());
    } 
    return new Instances(paramInstances.relationName() + "_predicted", fastVector, paramInstances.numInstances());
  }
  
  protected void startClassifier() {
    if (this.m_RunThread == null) {
      synchronized (this) {
        this.m_StartBut.setEnabled(false);
        this.m_StopBut.setEnabled(true);
      } 
      this.m_RunThread = new Thread(this) {
          private final ClassifierPanel this$0;
          
          public void run() {
            this.this$0.m_Log.statusMessage("Setting up...");
            CostMatrix costMatrix = null;
            Instances instances1 = new Instances(this.this$0.m_Instances);
            Instances instances2 = null;
            FastVector fastVector1 = new FastVector();
            FastVector fastVector2 = new FastVector();
            Instances instances3 = null;
            FastVector fastVector3 = null;
            long l1 = 0L;
            long l2 = 0L;
            if (this.this$0.m_TestInstances != null)
              instances2 = new Instances(this.this$0.m_TestInstances); 
            if (this.this$0.m_EvalWRTCostsBut.isSelected())
              costMatrix = new CostMatrix((CostMatrix)this.this$0.m_CostMatrixEditor.getValue()); 
            boolean bool1 = this.this$0.m_OutputModelBut.isSelected();
            boolean bool2 = this.this$0.m_OutputConfusionBut.isSelected();
            boolean bool3 = this.this$0.m_OutputPerClassBut.isSelected();
            boolean bool = true;
            boolean bool4 = this.this$0.m_OutputEntropyBut.isSelected();
            boolean bool5 = this.this$0.m_StorePredictionsBut.isSelected();
            boolean bool6 = this.this$0.m_OutputPredictionsTextBut.isSelected();
            String str1 = null;
            byte b = 0;
            int i = 10;
            int j = 66;
            int k = this.this$0.m_ClassCombo.getSelectedIndex();
            Classifier classifier1 = (Classifier)this.this$0.m_ClassifierEditor.getValue();
            Classifier classifier2 = null;
            try {
              classifier2 = Classifier.makeCopy(classifier1);
            } catch (Exception exception) {
              this.this$0.m_Log.logMessage("Problem copying classifier: " + exception.getMessage());
            } 
            Classifier classifier3 = null;
            StringBuffer stringBuffer = new StringBuffer();
            String str2 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
            String str3 = classifier1.getClass().getName();
            if (str3.startsWith("weka.classifiers.")) {
              str2 = str2 + str3.substring("weka.classifiers.".length());
            } else {
              str2 = str2 + str3;
            } 
            try {
              int m;
              Random random;
              int n;
              int i1;
              Instances instances4;
              Instances instances5;
              Classifier classifier;
              byte b1;
              if (this.this$0.m_CVBut.isSelected()) {
                b = 1;
                i = Integer.parseInt(this.this$0.m_CVText.getText());
                if (i <= 1)
                  throw new Exception("Number of folds must be greater than 1"); 
              } else if (this.this$0.m_PercentBut.isSelected()) {
                b = 2;
                j = Integer.parseInt(this.this$0.m_PercentText.getText());
                if (j <= 0 || j >= 100)
                  throw new Exception("Percentage must be between 0 and 100"); 
              } else if (this.this$0.m_TrainBut.isSelected()) {
                b = 3;
              } else if (this.this$0.m_TestSplitBut.isSelected()) {
                b = 4;
                if (instances2 == null)
                  throw new Exception("No user test set has been opened"); 
                if (!instances1.equalHeaders(instances2))
                  throw new Exception("Train and test set are not compatible"); 
                instances2.setClassIndex(k);
              } else {
                throw new Exception("Unknown test mode");
              } 
              instances1.setClassIndex(k);
              instances3 = ClassifierPanel.setUpVisualizableInstances(instances1);
              instances3.setClassIndex(instances1.classIndex() + 1);
              if (instances1.classAttribute().isNominal())
                fastVector3 = new FastVector(); 
              this.this$0.m_Log.logMessage("Started " + str3);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskStarted(); 
              stringBuffer.append("=== Run information ===\n\n");
              stringBuffer.append("Scheme:       " + str3);
              if (classifier1 instanceof weka.core.OptionHandler) {
                String[] arrayOfString = classifier1.getOptions();
                stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
              } 
              stringBuffer.append("\n");
              stringBuffer.append("Relation:     " + instances1.relationName() + '\n');
              stringBuffer.append("Instances:    " + instances1.numInstances() + '\n');
              stringBuffer.append("Attributes:   " + instances1.numAttributes() + '\n');
              if (instances1.numAttributes() < 100) {
                for (byte b2 = 0; b2 < instances1.numAttributes(); b2++)
                  stringBuffer.append("              " + instances1.attribute(b2).name() + '\n'); 
              } else {
                stringBuffer.append("              [list of attributes omitted]\n");
              } 
              stringBuffer.append("Test mode:    ");
              switch (b) {
                case 3:
                  stringBuffer.append("evaluate on training data\n");
                  break;
                case 1:
                  stringBuffer.append("" + i + "-fold cross-validation\n");
                  break;
                case 2:
                  stringBuffer.append("split " + j + "% train, remainder test\n");
                  break;
                case 4:
                  stringBuffer.append("user supplied test set: " + instances2.numInstances() + " instances\n");
                  break;
              } 
              if (costMatrix != null)
                stringBuffer.append("Evaluation cost matrix:\n").append(costMatrix.toString()).append("\n"); 
              stringBuffer.append("\n");
              this.this$0.m_History.addResult(str2, stringBuffer);
              this.this$0.m_History.setSingle(str2);
              if (bool1 || b == 3 || b == 4) {
                this.this$0.m_Log.statusMessage("Building model on training data...");
                l1 = System.currentTimeMillis();
                classifier1.buildClassifier(instances1);
                l2 = System.currentTimeMillis() - l1;
              } 
              if (bool1) {
                stringBuffer.append("=== Classifier model (full training set) ===\n\n");
                stringBuffer.append(classifier1.toString() + "\n");
                stringBuffer.append("\nTime taken to build model: " + Utils.doubleToString(l2 / 1000.0D, 2) + " seconds\n\n");
                this.this$0.m_History.updateResult(str2);
                if (classifier1 instanceof Drawable) {
                  str1 = null;
                  try {
                    str1 = ((Drawable)classifier1).graph();
                  } catch (Exception exception) {}
                } 
                SerializedObject serializedObject = new SerializedObject(classifier1);
                classifier3 = (Classifier)serializedObject.getObject();
              } 
              Evaluation evaluation = null;
              switch (b) {
                case 3:
                  this.this$0.m_Log.statusMessage("Evaluating on training data...");
                  evaluation = new Evaluation(instances1, costMatrix);
                  if (bool6) {
                    stringBuffer.append("=== Predictions on training set ===\n\n");
                    stringBuffer.append(" inst#,    actual, predicted, error");
                    if (instances1.classAttribute().isNominal())
                      stringBuffer.append(", probability distribution"); 
                    stringBuffer.append("\n");
                  } 
                  for (m = 0; m < instances1.numInstances(); m++) {
                    ClassifierPanel.processClassifierPrediction(instances1.instance(m), classifier1, evaluation, fastVector3, instances3, fastVector1, fastVector2);
                    if (bool6)
                      stringBuffer.append(this.this$0.predictionText(classifier1, instances1.instance(m), m + 1)); 
                    if (m % 100 == 0)
                      this.this$0.m_Log.statusMessage("Evaluating on training data. Processed " + m + " instances..."); 
                  } 
                  if (bool6)
                    stringBuffer.append("\n"); 
                  stringBuffer.append("=== Evaluation on training set ===\n");
                  break;
                case 1:
                  this.this$0.m_Log.statusMessage("Randomizing instances...");
                  m = 1;
                  try {
                    m = Integer.parseInt(this.this$0.m_RandomSeedText.getText().trim());
                  } catch (Exception exception) {
                    this.this$0.m_Log.logMessage("Trouble parsing random seed value");
                    m = 1;
                  } 
                  random = new Random(m);
                  instances1.randomize(random);
                  if (instances1.attribute(k).isNominal()) {
                    this.this$0.m_Log.statusMessage("Stratifying instances...");
                    instances1.stratify(i);
                  } 
                  evaluation = new Evaluation(instances1, costMatrix);
                  if (bool6) {
                    stringBuffer.append("=== Predictions on test data ===\n\n");
                    stringBuffer.append(" inst#,    actual, predicted, error");
                    if (instances1.classAttribute().isNominal())
                      stringBuffer.append(", probability distribution"); 
                    stringBuffer.append("\n");
                  } 
                  for (n = 0; n < i; n++) {
                    this.this$0.m_Log.statusMessage("Creating splits for fold " + (n + 1) + "...");
                    Instances instances6 = instances1.trainCV(i, n, random);
                    evaluation.setPriors(instances6);
                    this.this$0.m_Log.statusMessage("Building model for fold " + (n + 1) + "...");
                    Classifier classifier4 = null;
                    try {
                      classifier4 = Classifier.makeCopy(classifier2);
                    } catch (Exception exception) {
                      this.this$0.m_Log.logMessage("Problem copying classifier: " + exception.getMessage());
                    } 
                    classifier4.buildClassifier(instances6);
                    Instances instances7 = instances1.testCV(i, n);
                    this.this$0.m_Log.statusMessage("Evaluating model for fold " + (n + 1) + "...");
                    for (byte b2 = 0; b2 < instances7.numInstances(); b2++) {
                      ClassifierPanel.processClassifierPrediction(instances7.instance(b2), classifier4, evaluation, fastVector3, instances3, fastVector1, fastVector2);
                      if (bool6)
                        stringBuffer.append(this.this$0.predictionText(classifier4, instances7.instance(b2), b2 + 1)); 
                    } 
                  } 
                  if (bool6)
                    stringBuffer.append("\n"); 
                  if (instances1.attribute(k).isNominal()) {
                    stringBuffer.append("=== Stratified cross-validation ===\n");
                    break;
                  } 
                  stringBuffer.append("=== Cross-validation ===\n");
                  break;
                case 2:
                  this.this$0.m_Log.statusMessage("Randomizing instances...");
                  try {
                    m = Integer.parseInt(this.this$0.m_RandomSeedText.getText().trim());
                  } catch (Exception exception) {
                    this.this$0.m_Log.logMessage("Trouble parsing random seed value");
                    m = 1;
                  } 
                  instances1.randomize(new Random(m));
                  n = instances1.numInstances() * j / 100;
                  i1 = instances1.numInstances() - n;
                  instances4 = new Instances(instances1, 0, n);
                  instances5 = new Instances(instances1, n, i1);
                  this.this$0.m_Log.statusMessage("Building model on training split...");
                  classifier = null;
                  try {
                    classifier = Classifier.makeCopy(classifier2);
                  } catch (Exception exception) {
                    this.this$0.m_Log.logMessage("Problem copying classifier: " + exception.getMessage());
                  } 
                  classifier.buildClassifier(instances4);
                  evaluation = new Evaluation(instances4, costMatrix);
                  this.this$0.m_Log.statusMessage("Evaluating on test split...");
                  if (bool6) {
                    stringBuffer.append("=== Predictions on test split ===\n\n");
                    stringBuffer.append(" inst#,    actual, predicted, error");
                    if (instances1.classAttribute().isNominal())
                      stringBuffer.append(", probability distribution"); 
                    stringBuffer.append("\n");
                  } 
                  for (b1 = 0; b1 < instances5.numInstances(); b1++) {
                    ClassifierPanel.processClassifierPrediction(instances5.instance(b1), classifier, evaluation, fastVector3, instances3, fastVector1, fastVector2);
                    if (bool6)
                      stringBuffer.append(this.this$0.predictionText(classifier, instances5.instance(b1), b1 + 1)); 
                    if (b1 % 100 == 0)
                      this.this$0.m_Log.statusMessage("Evaluating on test split. Processed " + b1 + " instances..."); 
                  } 
                  if (bool6)
                    stringBuffer.append("\n"); 
                  stringBuffer.append("=== Evaluation on test split ===\n");
                  break;
                case 4:
                  this.this$0.m_Log.statusMessage("Evaluating on test data...");
                  evaluation = new Evaluation(instances1, costMatrix);
                  if (bool6) {
                    stringBuffer.append("=== Predictions on test set ===\n\n");
                    stringBuffer.append(" inst#,    actual, predicted, error");
                    if (instances1.classAttribute().isNominal())
                      stringBuffer.append(", probability distribution"); 
                    stringBuffer.append("\n");
                  } 
                  for (b1 = 0; b1 < instances2.numInstances(); b1++) {
                    ClassifierPanel.processClassifierPrediction(instances2.instance(b1), classifier1, evaluation, fastVector3, instances3, fastVector1, fastVector2);
                    if (bool6)
                      stringBuffer.append(this.this$0.predictionText(classifier1, instances2.instance(b1), b1 + 1)); 
                    if (b1 % 100 == 0)
                      this.this$0.m_Log.statusMessage("Evaluating on test data. Processed " + b1 + " instances..."); 
                  } 
                  if (bool6)
                    stringBuffer.append("\n"); 
                  stringBuffer.append("=== Evaluation on test set ===\n");
                  break;
                default:
                  throw new Exception("Test mode not implemented");
              } 
              if (bool)
                stringBuffer.append(evaluation.toSummaryString(bool4) + "\n"); 
              if (instances1.attribute(k).isNominal()) {
                if (bool3)
                  stringBuffer.append(evaluation.toClassDetailsString() + "\n"); 
                if (bool2)
                  stringBuffer.append(evaluation.toMatrixString() + "\n"); 
              } 
              this.this$0.m_History.updateResult(str2);
              this.this$0.m_Log.logMessage("Finished " + str3);
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              exception.printStackTrace();
              this.this$0.m_Log.logMessage(exception.getMessage());
              JOptionPane.showMessageDialog(this.this$0, "Problem evaluating classifier:\n" + exception.getMessage(), "Evaluate classifier", 0);
              this.this$0.m_Log.statusMessage("Problem evaluating classifier");
            } finally {
              try {
                if (instances3 != null && instances3.numInstances() > 0) {
                  if (instances3.attribute(instances3.classIndex()).isNumeric())
                    this.this$0.postProcessPlotInfo(fastVector2); 
                  this.this$0.m_CurrentVis = new VisualizePanel();
                  this.this$0.m_CurrentVis.setName(str2 + " (" + instances1.relationName() + ")");
                  this.this$0.m_CurrentVis.setLog(this.this$0.m_Log);
                  PlotData2D plotData2D = new PlotData2D(instances3);
                  plotData2D.setShapeSize(fastVector2);
                  plotData2D.setShapeType(fastVector1);
                  plotData2D.setPlotName(str2 + " (" + instances1.relationName() + ")");
                  plotData2D.addInstanceNumberAttribute();
                  this.this$0.m_CurrentVis.addPlot(plotData2D);
                  this.this$0.m_CurrentVis.setColourIndex(instances3.classIndex() + 1);
                  if (bool5) {
                    FastVector fastVector = new FastVector();
                    if (bool1) {
                      fastVector.addElement(classifier3);
                      Instances instances = new Instances(this.this$0.m_Instances, 0);
                      instances.setClassIndex(k);
                      fastVector.addElement(instances);
                    } 
                    fastVector.addElement(this.this$0.m_CurrentVis);
                    if (str1 != null)
                      fastVector.addElement(str1); 
                    if (fastVector3 != null) {
                      fastVector.addElement(fastVector3);
                      fastVector.addElement(instances1.classAttribute());
                    } 
                    this.this$0.m_History.addObject(str2, fastVector);
                  } else if (bool1) {
                    FastVector fastVector = new FastVector();
                    fastVector.addElement(classifier3);
                    Instances instances = new Instances(this.this$0.m_Instances, 0);
                    instances.setClassIndex(k);
                    fastVector.addElement(instances);
                    this.this$0.m_History.addObject(str2, fastVector);
                  } 
                } 
              } catch (Exception exception) {
                exception.printStackTrace();
              } 
              if (isInterrupted()) {
                this.this$0.m_Log.logMessage("Interrupted " + str3);
                this.this$0.m_Log.statusMessage("Interrupted");
              } 
              synchronized (this) {
                this.this$0.m_StartBut.setEnabled(true);
                this.this$0.m_StopBut.setEnabled(false);
                this.this$0.m_RunThread = null;
              } 
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskFinished(); 
            } 
          }
        };
      this.m_RunThread.setPriority(1);
      this.m_RunThread.start();
    } 
  }
  
  protected String predictionText(Classifier paramClassifier, Instance paramInstance, int paramInt) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(Utils.padLeft("" + paramInt, 6) + " ");
    if (paramInstance.classAttribute().isNominal()) {
      double d;
      if (paramInstance.classIsMissing()) {
        stringBuffer.append(Utils.padLeft("?", 10) + " ");
      } else {
        stringBuffer.append(Utils.padLeft("" + ((int)paramInstance.classValue() + 1) + ":" + paramInstance.stringValue(paramInstance.classAttribute()), 10) + " ");
      } 
      double[] arrayOfDouble = null;
      if (paramInstance.classAttribute().isNominal()) {
        arrayOfDouble = paramClassifier.distributionForInstance(paramInstance);
        d = Utils.maxIndex(arrayOfDouble);
        if (arrayOfDouble[(int)d] <= 0.0D)
          d = Instance.missingValue(); 
      } else {
        d = paramClassifier.classifyInstance(paramInstance);
      } 
      stringBuffer.append(Utils.padLeft(Instance.isMissingValue(d) ? "?" : (((int)d + 1) + ":" + paramInstance.classAttribute().value((int)d)), 10) + " ");
      if (d == paramInstance.classValue()) {
        stringBuffer.append(Utils.padLeft(" ", 6) + " ");
      } else {
        stringBuffer.append(Utils.padLeft("+", 6) + " ");
      } 
      if (paramInstance.classAttribute().type() == 1)
        for (byte b = 0; b < arrayOfDouble.length; b++) {
          if (b == (int)d) {
            stringBuffer.append(" *");
          } else {
            stringBuffer.append("  ");
          } 
          stringBuffer.append(Utils.doubleToString(arrayOfDouble[b], 5, 3));
        }  
    } else {
      if (paramInstance.classIsMissing()) {
        stringBuffer.append(Utils.padLeft("?", 10) + " ");
      } else {
        stringBuffer.append(Utils.doubleToString(paramInstance.classValue(), 10, 3) + " ");
      } 
      double d = paramClassifier.classifyInstance(paramInstance);
      if (Instance.isMissingValue(d)) {
        stringBuffer.append(Utils.padLeft("?", 10) + " ");
      } else {
        stringBuffer.append(Utils.doubleToString(d, 10, 3) + " ");
      } 
      if (!paramInstance.classIsMissing() && !Instance.isMissingValue(d))
        stringBuffer.append(Utils.doubleToString(d - paramInstance.classValue(), 10, 3)); 
    } 
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
  
  protected void visualize(String paramString, int paramInt1, int paramInt2) {
    String str1 = paramString;
    JPopupMenu jPopupMenu = new JPopupMenu();
    JMenuItem jMenuItem1 = new JMenuItem("View in main window");
    if (str1 != null) {
      jMenuItem1.addActionListener(new ActionListener(this, str1) {
            private final String val$selectedName;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_History.setSingle(this.val$selectedName);
            }
          });
    } else {
      jMenuItem1.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem1);
    JMenuItem jMenuItem2 = new JMenuItem("View in separate window");
    if (str1 != null) {
      jMenuItem2.addActionListener(new ActionListener(this, str1) {
            private final String val$selectedName;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_History.openFrame(this.val$selectedName);
            }
          });
    } else {
      jMenuItem2.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem2);
    JMenuItem jMenuItem3 = new JMenuItem("Save result buffer");
    if (str1 != null) {
      jMenuItem3.addActionListener(new ActionListener(this, str1) {
            private final String val$selectedName;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.saveBuffer(this.val$selectedName);
            }
          });
    } else {
      jMenuItem3.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem3);
    jPopupMenu.addSeparator();
    JMenuItem jMenuItem4 = new JMenuItem("Load model");
    jMenuItem4.addActionListener(new ActionListener(this) {
          private final ClassifierPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.loadClassifier();
          }
        });
    jPopupMenu.add(jMenuItem4);
    FastVector fastVector1 = null;
    if (str1 != null)
      fastVector1 = (FastVector)this.m_History.getNamedObject(str1); 
    VisualizePanel visualizePanel1 = null;
    String str2 = null;
    FastVector fastVector2 = null;
    Attribute attribute1 = null;
    Classifier classifier1 = null;
    Instances instances1 = null;
    if (fastVector1 != null)
      for (byte b = 0; b < fastVector1.size(); b++) {
        Object object = fastVector1.elementAt(b);
        if (object instanceof Classifier) {
          classifier1 = (Classifier)object;
        } else if (object instanceof Instances) {
          instances1 = (Instances)object;
        } else if (object instanceof VisualizePanel) {
          visualizePanel1 = (VisualizePanel)object;
        } else if (object instanceof String) {
          str2 = (String)object;
        } else if (object instanceof FastVector) {
          fastVector2 = (FastVector)object;
        } else if (object instanceof Attribute) {
          attribute1 = (Attribute)object;
        } 
      }  
    VisualizePanel visualizePanel2 = visualizePanel1;
    String str3 = str2;
    FastVector fastVector3 = fastVector2;
    Attribute attribute2 = attribute1;
    Classifier classifier2 = classifier1;
    Instances instances2 = instances1;
    JMenuItem jMenuItem5 = new JMenuItem("Save model");
    if (classifier2 != null) {
      jMenuItem5.addActionListener(new ActionListener(this, str1, classifier2, instances2) {
            private final String val$selectedName;
            
            private final Classifier val$classifier;
            
            private final Instances val$trainHeader;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.saveClassifier(this.val$selectedName, this.val$classifier, this.val$trainHeader);
            }
          });
    } else {
      jMenuItem5.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem5);
    JMenuItem jMenuItem6 = new JMenuItem("Re-evaluate model on current test set");
    if (classifier2 != null && this.m_TestInstances != null) {
      jMenuItem6.addActionListener(new ActionListener(this, str1, classifier2, instances2) {
            private final String val$selectedName;
            
            private final Classifier val$classifier;
            
            private final Instances val$trainHeader;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.reevaluateModel(this.val$selectedName, this.val$classifier, this.val$trainHeader);
            }
          });
    } else {
      jMenuItem6.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem6);
    jPopupMenu.addSeparator();
    JMenuItem jMenuItem7 = new JMenuItem("Visualize classifier errors");
    if (visualizePanel2 != null) {
      jMenuItem7.addActionListener(new ActionListener(this, visualizePanel2) {
            private final VisualizePanel val$vp;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.visualizeClassifierErrors(this.val$vp);
            }
          });
    } else {
      jMenuItem7.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem7);
    JMenuItem jMenuItem8 = new JMenuItem("Visualize tree");
    if (str3 != null) {
      if (((Drawable)classifier1).graphType() == 1) {
        jMenuItem8.addActionListener(new ActionListener(this, visualizePanel2, str1, str3) {
              private final VisualizePanel val$vp;
              
              private final String val$selectedName;
              
              private final String val$grph;
              
              private final ClassifierPanel this$0;
              
              public void actionPerformed(ActionEvent param1ActionEvent) {
                String str;
                if (this.val$vp != null) {
                  str = this.val$vp.getName();
                } else {
                  str = this.val$selectedName;
                } 
                this.this$0.visualizeTree(this.val$grph, str);
              }
            });
      } else if (((Drawable)classifier1).graphType() == 2) {
        jMenuItem8.setText("Visualize graph");
        jMenuItem8.addActionListener(new ActionListener(this, str3, str1) {
              private final String val$grph;
              
              private final String val$selectedName;
              
              private final ClassifierPanel this$0;
              
              public void actionPerformed(ActionEvent param1ActionEvent) {
                Object object = new Object(this);
                object.start();
              }
            });
      } else {
        jMenuItem8.setEnabled(false);
      } 
    } else {
      jMenuItem8.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem8);
    JMenuItem jMenuItem9 = new JMenuItem("Visualize margin curve");
    if (fastVector3 != null) {
      jMenuItem9.addActionListener(new ActionListener(this, fastVector3) {
            private final FastVector val$preds;
            
            private final ClassifierPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              try {
                MarginCurve marginCurve = new MarginCurve();
                Instances instances = marginCurve.getCurve(this.val$preds);
                VisualizePanel visualizePanel = new VisualizePanel();
                visualizePanel.setName(instances.relationName());
                visualizePanel.setLog(this.this$0.m_Log);
                PlotData2D plotData2D = new PlotData2D(instances);
                plotData2D.setPlotName(instances.relationName());
                plotData2D.addInstanceNumberAttribute();
                visualizePanel.addPlot(plotData2D);
                this.this$0.visualizeClassifierErrors(visualizePanel);
              } catch (Exception exception) {
                exception.printStackTrace();
              } 
            }
          });
    } else {
      jMenuItem9.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem9);
    JMenu jMenu1 = new JMenu("Visualize threshold curve");
    if (fastVector3 != null && attribute2 != null) {
      for (byte b = 0; b < attribute2.numValues(); b++) {
        JMenuItem jMenuItem = new JMenuItem(attribute2.value(b));
        byte b1 = b;
        jMenuItem.addActionListener(new ActionListener(this, fastVector3, b1, attribute2) {
              private final FastVector val$preds;
              
              private final int val$classValue;
              
              private final Attribute val$classAtt;
              
              private final ClassifierPanel this$0;
              
              public void actionPerformed(ActionEvent param1ActionEvent) {
                try {
                  ThresholdCurve thresholdCurve = new ThresholdCurve();
                  Instances instances = thresholdCurve.getCurve(this.val$preds, this.val$classValue);
                  ThresholdVisualizePanel thresholdVisualizePanel = new ThresholdVisualizePanel();
                  thresholdVisualizePanel.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(instances), 4) + ")");
                  thresholdVisualizePanel.setLog(this.this$0.m_Log);
                  thresholdVisualizePanel.setName(instances.relationName() + ". (Class value " + this.val$classAtt.value(this.val$classValue) + ")");
                  PlotData2D plotData2D = new PlotData2D(instances);
                  plotData2D.setPlotName(instances.relationName());
                  plotData2D.addInstanceNumberAttribute();
                  thresholdVisualizePanel.addPlot(plotData2D);
                  this.this$0.visualizeClassifierErrors((VisualizePanel)thresholdVisualizePanel);
                } catch (Exception exception) {
                  exception.printStackTrace();
                } 
              }
            });
        jMenu1.add(jMenuItem);
      } 
    } else {
      jMenu1.setEnabled(false);
    } 
    jPopupMenu.add(jMenu1);
    JMenu jMenu2 = new JMenu("Visualize cost curve");
    if (fastVector3 != null && attribute2 != null) {
      for (byte b = 0; b < attribute2.numValues(); b++) {
        JMenuItem jMenuItem = new JMenuItem(attribute2.value(b));
        byte b1 = b;
        jMenuItem.addActionListener(new ActionListener(this, fastVector3, b1, attribute2) {
              private final FastVector val$preds;
              
              private final int val$classValue;
              
              private final Attribute val$classAtt;
              
              private final ClassifierPanel this$0;
              
              public void actionPerformed(ActionEvent param1ActionEvent) {
                try {
                  CostCurve costCurve = new CostCurve();
                  Instances instances = costCurve.getCurve(this.val$preds, this.val$classValue);
                  VisualizePanel visualizePanel = new VisualizePanel();
                  visualizePanel.setLog(this.this$0.m_Log);
                  visualizePanel.setName(instances.relationName() + ". (Class value " + this.val$classAtt.value(this.val$classValue) + ")");
                  PlotData2D plotData2D = new PlotData2D(instances);
                  plotData2D.m_displayAllPoints = true;
                  plotData2D.setPlotName(instances.relationName());
                  boolean[] arrayOfBoolean = new boolean[instances.numInstances()];
                  for (byte b = 1; b < arrayOfBoolean.length; b += 2)
                    arrayOfBoolean[b] = true; 
                  plotData2D.setConnectPoints(arrayOfBoolean);
                  visualizePanel.addPlot(plotData2D);
                  this.this$0.visualizeClassifierErrors(visualizePanel);
                } catch (Exception exception) {
                  exception.printStackTrace();
                } 
              }
            });
        jMenu2.add(jMenuItem);
      } 
    } else {
      jMenu2.setEnabled(false);
    } 
    jPopupMenu.add(jMenu2);
    jPopupMenu.show(this.m_History.getList(), paramInt1, paramInt2);
  }
  
  protected void visualizeTree(String paramString1, String paramString2) {
    JFrame jFrame = new JFrame("Weka Classifier Tree Visualizer: " + paramString2);
    jFrame.setSize(500, 400);
    jFrame.getContentPane().setLayout(new BorderLayout());
    TreeVisualizer treeVisualizer = new TreeVisualizer(null, paramString1, (NodePlace)new PlaceNode2());
    jFrame.getContentPane().add((Component)treeVisualizer, "Center");
    jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
          private final JFrame val$jf;
          
          private final ClassifierPanel this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.val$jf.dispose();
          }
        });
    jFrame.setVisible(true);
    treeVisualizer.fitToScreen();
  }
  
  protected void visualizeBayesNet(String paramString1, String paramString2) {
    JFrame jFrame = new JFrame("Weka Classifier Graph Visualizer: " + paramString2);
    jFrame.setSize(500, 400);
    jFrame.getContentPane().setLayout(new BorderLayout());
    GraphVisualizer graphVisualizer = new GraphVisualizer();
    try {
      graphVisualizer.readBIF(paramString1);
    } catch (BIFFormatException bIFFormatException) {
      System.err.println("unable to visualize BayesNet");
      bIFFormatException.printStackTrace();
    } 
    graphVisualizer.layoutGraph();
    jFrame.getContentPane().add((Component)graphVisualizer, "Center");
    jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
          private final JFrame val$jf;
          
          private final ClassifierPanel this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.val$jf.dispose();
          }
        });
    jFrame.setVisible(true);
  }
  
  protected void visualizeClassifierErrors(VisualizePanel paramVisualizePanel) {
    if (paramVisualizePanel != null) {
      String str = paramVisualizePanel.getName();
      JFrame jFrame = new JFrame("Weka Classifier Visualize: " + str);
      jFrame.setSize(500, 400);
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add((Component)paramVisualizePanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final ClassifierPanel this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
            }
          });
      jFrame.setVisible(true);
    } 
  }
  
  protected void saveBuffer(String paramString) {
    StringBuffer stringBuffer = this.m_History.getNamedBuffer(paramString);
    if (stringBuffer != null && this.m_SaveOut.save(stringBuffer))
      this.m_Log.logMessage("Save successful."); 
  }
  
  protected void stopClassifier() {
    if (this.m_RunThread != null) {
      this.m_RunThread.interrupt();
      this.m_RunThread.stop();
    } 
  }
  
  protected void saveClassifier(String paramString, Classifier paramClassifier, Instances paramInstances) {
    File file = null;
    boolean bool = true;
    int i = this.m_FileChooser.showSaveDialog(this);
    if (i == 0) {
      file = this.m_FileChooser.getSelectedFile();
      if (!file.getName().toLowerCase().endsWith(MODEL_FILE_EXTENSION))
        file = new File(file.getParent(), file.getName() + MODEL_FILE_EXTENSION); 
      this.m_Log.statusMessage("Saving model to file...");
      try {
        GZIPOutputStream gZIPOutputStream;
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        if (file.getName().endsWith(".gz"))
          gZIPOutputStream = new GZIPOutputStream(fileOutputStream); 
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(gZIPOutputStream);
        objectOutputStream.writeObject(paramClassifier);
        if (paramInstances != null)
          objectOutputStream.writeObject(paramInstances); 
        objectOutputStream.flush();
        objectOutputStream.close();
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(null, exception, "Save Failed", 0);
        bool = false;
      } 
      if (bool)
        this.m_Log.logMessage("Saved model (" + paramString + ") to file '" + file.getName() + "'"); 
      this.m_Log.statusMessage("OK");
    } 
  }
  
  protected void loadClassifier() {
    int i = this.m_FileChooser.showOpenDialog(this);
    if (i == 0) {
      File file = this.m_FileChooser.getSelectedFile();
      Classifier classifier = null;
      Instances instances = null;
      this.m_Log.statusMessage("Loading model from file...");
      try {
        GZIPInputStream gZIPInputStream;
        FileInputStream fileInputStream = new FileInputStream(file);
        if (file.getName().endsWith(".gz"))
          gZIPInputStream = new GZIPInputStream(fileInputStream); 
        ObjectInputStream objectInputStream = new ObjectInputStream(gZIPInputStream);
        classifier = (Classifier)objectInputStream.readObject();
        try {
          instances = (Instances)objectInputStream.readObject();
        } catch (Exception exception) {}
        objectInputStream.close();
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(null, exception, "Load Failed", 0);
      } 
      this.m_Log.statusMessage("OK");
      if (classifier != null) {
        this.m_Log.logMessage("Loaded model from file '" + file.getName() + "'");
        String str1 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
        String str2 = classifier.getClass().getName();
        if (str2.startsWith("weka.classifiers."))
          str2 = str2.substring("weka.classifiers.".length()); 
        str1 = str1 + str2 + " from file '" + file.getName() + "'";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("=== Model information ===\n\n");
        stringBuffer.append("Filename:     " + file.getName() + "\n");
        stringBuffer.append("Scheme:       " + str2);
        if (classifier instanceof weka.core.OptionHandler) {
          String[] arrayOfString = classifier.getOptions();
          stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
        } 
        stringBuffer.append("\n");
        if (instances != null) {
          stringBuffer.append("Relation:     " + instances.relationName() + '\n');
          stringBuffer.append("Attributes:   " + instances.numAttributes() + '\n');
          if (instances.numAttributes() < 100) {
            for (byte b = 0; b < instances.numAttributes(); b++)
              stringBuffer.append("              " + instances.attribute(b).name() + '\n'); 
          } else {
            stringBuffer.append("              [list of attributes omitted]\n");
          } 
        } else {
          stringBuffer.append("\nTraining data unknown\n");
        } 
        stringBuffer.append("\n=== Classifier model ===\n\n");
        stringBuffer.append(classifier.toString() + "\n");
        this.m_History.addResult(str1, stringBuffer);
        this.m_History.setSingle(str1);
        FastVector fastVector = new FastVector();
        fastVector.addElement(classifier);
        if (instances != null)
          fastVector.addElement(instances); 
        String str3 = null;
        if (classifier instanceof Drawable)
          try {
            str3 = ((Drawable)classifier).graph();
          } catch (Exception exception) {} 
        if (str3 != null)
          fastVector.addElement(str3); 
        this.m_History.addObject(str1, fastVector);
      } 
    } 
  }
  
  protected void reevaluateModel(String paramString, Classifier paramClassifier, Instances paramInstances) {
    StringBuffer stringBuffer = this.m_History.getNamedBuffer(paramString);
    Instances instances1 = null;
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    Instances instances2 = null;
    FastVector fastVector3 = null;
    CostMatrix costMatrix = null;
    if (this.m_EvalWRTCostsBut.isSelected())
      costMatrix = new CostMatrix((CostMatrix)this.m_CostMatrixEditor.getValue()); 
    boolean bool1 = this.m_OutputConfusionBut.isSelected();
    boolean bool2 = this.m_OutputPerClassBut.isSelected();
    boolean bool = true;
    boolean bool3 = this.m_OutputEntropyBut.isSelected();
    boolean bool4 = this.m_StorePredictionsBut.isSelected();
    boolean bool5 = this.m_OutputPredictionsTextBut.isSelected();
    String str = null;
    try {
      if (this.m_TestInstances != null)
        instances1 = new Instances(this.m_TestInstances); 
      if (instances1 == null)
        throw new Exception("No user test set has been opened"); 
      if (paramInstances != null) {
        if (paramInstances.classIndex() > instances1.numAttributes() - 1)
          throw new Exception("Train and test set are not compatible"); 
        instances1.setClassIndex(paramInstances.classIndex());
        if (!paramInstances.equalHeaders(instances1))
          throw new Exception("Train and test set are not compatible"); 
      } else {
        instances1.setClassIndex(instances1.numAttributes() - 1);
      } 
      this.m_Log.statusMessage("Evaluating on test data...");
      this.m_Log.logMessage("Re-evaluating classifier (" + paramString + ") on test set");
      Evaluation evaluation = new Evaluation(instances1, costMatrix);
      instances2 = setUpVisualizableInstances(instances1);
      instances2.setClassIndex(instances1.classIndex() + 1);
      if (instances1.classAttribute().isNominal())
        fastVector3 = new FastVector(); 
      stringBuffer.append("\n=== Re-evaluation on test set ===\n\n");
      stringBuffer.append("User supplied test set\n");
      stringBuffer.append("Relation:     " + instances1.relationName() + '\n');
      stringBuffer.append("Instances:    " + instances1.numInstances() + '\n');
      stringBuffer.append("Attributes:   " + instances1.numAttributes() + "\n\n");
      if (paramInstances == null)
        stringBuffer.append("NOTE - if test set is not compatible then results are unpredictable\n\n"); 
      if (bool5) {
        stringBuffer.append("=== Predictions on test set ===\n\n");
        stringBuffer.append(" inst#,    actual, predicted, error");
        if (instances1.classAttribute().isNominal())
          stringBuffer.append(", probability distribution"); 
        stringBuffer.append("\n");
      } 
      for (byte b = 0; b < instances1.numInstances(); b++) {
        processClassifierPrediction(instances1.instance(b), paramClassifier, evaluation, fastVector3, instances2, fastVector1, fastVector2);
        if (bool5)
          stringBuffer.append(predictionText(paramClassifier, instances1.instance(b), b + 1)); 
        if (b % 100 == 0)
          this.m_Log.statusMessage("Evaluating on test data. Processed " + b + " instances..."); 
      } 
      if (bool5)
        stringBuffer.append("\n"); 
      if (bool)
        stringBuffer.append(evaluation.toSummaryString(bool3) + "\n"); 
      if (instances1.classAttribute().isNominal()) {
        if (bool2)
          stringBuffer.append(evaluation.toClassDetailsString() + "\n"); 
        if (bool1)
          stringBuffer.append(evaluation.toMatrixString() + "\n"); 
      } 
      this.m_History.updateResult(paramString);
      this.m_Log.logMessage("Finished re-evaluation");
      this.m_Log.statusMessage("OK");
    } catch (Exception exception) {
      exception.printStackTrace();
      this.m_Log.logMessage(exception.getMessage());
      this.m_Log.statusMessage("See error log");
      exception.printStackTrace();
      this.m_Log.logMessage(exception.getMessage());
      JOptionPane.showMessageDialog(this, "Problem evaluationg classifier:\n" + exception.getMessage(), "Evaluate classifier", 0);
      this.m_Log.statusMessage("Problem evaluating classifier");
    } finally {
      try {
        if (instances2 != null && instances2.numInstances() > 0) {
          if (instances2.attribute(instances2.classIndex()).isNumeric())
            postProcessPlotInfo(fastVector2); 
          this.m_CurrentVis = new VisualizePanel();
          this.m_CurrentVis.setName(paramString + " (" + instances1.relationName() + ")");
          this.m_CurrentVis.setLog(this.m_Log);
          PlotData2D plotData2D = new PlotData2D(instances2);
          plotData2D.setShapeSize(fastVector2);
          plotData2D.setShapeType(fastVector1);
          plotData2D.setPlotName(paramString + " (" + instances1.relationName() + ")");
          plotData2D.addInstanceNumberAttribute();
          this.m_CurrentVis.addPlot(plotData2D);
          this.m_CurrentVis.setColourIndex(instances2.classIndex() + 1);
          if (paramClassifier instanceof Drawable)
            try {
              str = ((Drawable)paramClassifier).graph();
            } catch (Exception exception) {} 
          if (bool4) {
            FastVector fastVector = new FastVector();
            fastVector.addElement(paramClassifier);
            if (paramInstances != null)
              fastVector.addElement(paramInstances); 
            fastVector.addElement(this.m_CurrentVis);
            if (str != null)
              fastVector.addElement(str); 
            if (fastVector3 != null) {
              fastVector.addElement(fastVector3);
              fastVector.addElement(instances1.classAttribute());
            } 
            this.m_History.addObject(paramString, fastVector);
          } else {
            FastVector fastVector = new FastVector();
            fastVector.addElement(paramClassifier);
            if (paramInstances != null)
              fastVector.addElement(paramInstances); 
            this.m_History.addObject(paramString, fastVector);
          } 
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Explorer: Classifier");
      jFrame.getContentPane().setLayout(new BorderLayout());
      ClassifierPanel classifierPanel = new ClassifierPanel();
      jFrame.getContentPane().add(classifierPanel, "Center");
      LogPanel logPanel = new LogPanel();
      classifierPanel.setLog((Logger)logPanel);
      jFrame.getContentPane().add((Component)logPanel, "South");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
      if (paramArrayOfString.length == 1) {
        System.err.println("Loading instances from " + paramArrayOfString[0]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
        Instances instances = new Instances(bufferedReader);
        classifierPanel.setInstances(instances);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor((array$Lweka$classifiers$Classifier == null) ? (array$Lweka$classifiers$Classifier = class$("[Lweka.classifiers.Classifier;")) : array$Lweka$classifiers$Classifier, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(CostMatrix.class, CostMatrixEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\explorer\ClassifierPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */