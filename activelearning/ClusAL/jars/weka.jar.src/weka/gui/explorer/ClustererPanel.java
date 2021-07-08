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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import weka.attributeSelection.UnsupervisedSubsetEvaluator;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.ExtensionFileFilter;
import weka.gui.GenericObjectEditor;
import weka.gui.InstancesSummaryPanel;
import weka.gui.ListSelectorDialog;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.PropertyPanel;
import weka.gui.ResultHistoryPanel;
import weka.gui.SaveBuffer;
import weka.gui.SelectedTagEditor;
import weka.gui.SetInstancesPanel;
import weka.gui.SysErrLog;
import weka.gui.TaskLogger;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

public class ClustererPanel extends JPanel {
  public static String MODEL_FILE_EXTENSION = ".model";
  
  protected GenericObjectEditor m_ClustererEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_CLPanel = new PropertyPanel((PropertyEditor)this.m_ClustererEditor);
  
  protected JTextArea m_OutText = new JTextArea(20, 40);
  
  protected Logger m_Log = (Logger)new SysErrLog();
  
  SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
  
  protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
  
  protected JRadioButton m_PercentBut = new JRadioButton("Percentage split");
  
  protected JRadioButton m_TrainBut = new JRadioButton("Use training set");
  
  protected JRadioButton m_TestSplitBut = new JRadioButton("Supplied test set");
  
  protected JRadioButton m_ClassesToClustersBut = new JRadioButton("Classes to clusters evaluation");
  
  protected JComboBox m_ClassCombo = new JComboBox();
  
  protected JLabel m_PercentLab = new JLabel("%", 4);
  
  protected JTextField m_PercentText = new JTextField("66");
  
  protected JButton m_SetTestBut = new JButton("Set...");
  
  protected JFrame m_SetTestFrame;
  
  protected JButton m_ignoreBut = new JButton("Ignore attributes");
  
  protected DefaultListModel m_ignoreKeyModel = new DefaultListModel();
  
  protected JList m_ignoreKeyList = new JList(this.m_ignoreKeyModel);
  
  ActionListener m_RadioListener = new ActionListener(this) {
      private final ClustererPanel this$0;
      
      public void actionPerformed(ActionEvent param1ActionEvent) {
        this.this$0.updateRadioLinks();
      }
    };
  
  protected JButton m_StartBut = new JButton("Start");
  
  private Dimension COMBO_SIZE = new Dimension(250, (this.m_StartBut.getPreferredSize()).height);
  
  protected JButton m_StopBut = new JButton("Stop");
  
  protected Instances m_Instances;
  
  protected Instances m_TestInstances;
  
  protected Instances m_TestInstancesCopy;
  
  protected VisualizePanel m_CurrentVis = null;
  
  protected JCheckBox m_StorePredictionsBut = new JCheckBox("Store clusters for visualization");
  
  protected Thread m_RunThread;
  
  protected InstancesSummaryPanel m_Summary;
  
  protected FileFilter m_ModelFilter = (FileFilter)new ExtensionFileFilter(MODEL_FILE_EXTENSION, "Model object files");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  public ClustererPanel() {
    this.m_OutText.setEditable(false);
    this.m_OutText.setFont(new Font("Monospaced", 0, 12));
    this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_OutText.addMouseListener(new MouseAdapter(this) {
          private final ClustererPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16)
              this.this$0.m_OutText.selectAll(); 
          }
        });
    this.m_History.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
    this.m_ClustererEditor.setClassType(Clusterer.class);
    this.m_ClustererEditor.setValue(new EM());
    this.m_ClustererEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final ClustererPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.repaint();
          }
        });
    this.m_TrainBut.setToolTipText("Cluster the same set that the clusterer is trained on");
    this.m_PercentBut.setToolTipText("Train on a percentage of the data and cluster the remainder");
    this.m_TestSplitBut.setToolTipText("Cluster a user-specified dataset");
    this.m_ClassesToClustersBut.setToolTipText("Evaluate clusters with respect to a class");
    this.m_ClassCombo.setToolTipText("Select the class attribute for class based evaluation");
    this.m_StartBut.setToolTipText("Starts the clustering");
    this.m_StopBut.setToolTipText("Stops a running clusterer");
    this.m_StorePredictionsBut.setToolTipText("Store predictions in the result list for later visualization");
    this.m_ignoreBut.setToolTipText("Ignore attributes during clustering");
    this.m_FileChooser.setFileFilter(this.m_ModelFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_ClassCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_ClassCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_ClassCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_ClassCombo.setEnabled(false);
    this.m_TrainBut.setSelected(true);
    this.m_StorePredictionsBut.setSelected(true);
    updateRadioLinks();
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_TrainBut);
    buttonGroup.add(this.m_PercentBut);
    buttonGroup.add(this.m_TestSplitBut);
    buttonGroup.add(this.m_ClassesToClustersBut);
    this.m_TrainBut.addActionListener(this.m_RadioListener);
    this.m_PercentBut.addActionListener(this.m_RadioListener);
    this.m_TestSplitBut.addActionListener(this.m_RadioListener);
    this.m_ClassesToClustersBut.addActionListener(this.m_RadioListener);
    this.m_SetTestBut.addActionListener(new ActionListener(this) {
          private final ClustererPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setTestSet();
          }
        });
    this.m_StartBut.setEnabled(false);
    this.m_StopBut.setEnabled(false);
    this.m_ignoreBut.setEnabled(false);
    this.m_StartBut.addActionListener(new ActionListener(this) {
          private final ClustererPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.startClusterer();
          }
        });
    this.m_StopBut.addActionListener(new ActionListener(this) {
          private final ClustererPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.stopClusterer();
          }
        });
    this.m_ignoreBut.addActionListener(new ActionListener(this) {
          private final ClustererPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setIgnoreColumns();
          }
        });
    this.m_History.setHandleRightClicks(false);
    this.m_History.getList().addMouseListener(new MouseAdapter(this) {
          private final ClustererPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16 || param1MouseEvent.isAltDown()) {
              int i = this.this$0.m_History.getList().locationToIndex(param1MouseEvent.getPoint());
              if (i != -1) {
                String str = this.this$0.m_History.getNameAtIndex(i);
                this.this$0.visualizeClusterer(str, param1MouseEvent.getX(), param1MouseEvent.getY());
              } else {
                this.this$0.visualizeClusterer((String)null, param1MouseEvent.getX(), param1MouseEvent.getY());
              } 
            } 
          }
        });
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Clusterer"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add((Component)this.m_CLPanel, "North");
    JPanel jPanel2 = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    jPanel2.setLayout(gridBagLayout);
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Cluster mode"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
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
    gridBagLayout.setConstraints(this.m_PercentBut, gridBagConstraints);
    jPanel2.add(this.m_PercentBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new Insets(2, 10, 2, 10);
    gridBagLayout.setConstraints(this.m_PercentLab, gridBagConstraints);
    jPanel2.add(this.m_PercentLab);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.ipadx = 20;
    gridBagLayout.setConstraints(this.m_PercentText, gridBagConstraints);
    jPanel2.add(this.m_PercentText);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagLayout.setConstraints(this.m_ClassesToClustersBut, gridBagConstraints);
    jPanel2.add(this.m_ClassesToClustersBut);
    this.m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagLayout.setConstraints(this.m_ClassCombo, gridBagConstraints);
    jPanel2.add(this.m_ClassCombo);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagLayout.setConstraints(this.m_StorePredictionsBut, gridBagConstraints);
    jPanel2.add(this.m_StorePredictionsBut);
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new GridLayout(2, 1));
    JPanel jPanel4 = new JPanel();
    jPanel4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jPanel4.setLayout(new GridLayout(1, 2, 5, 5));
    jPanel4.add(this.m_StartBut);
    jPanel4.add(this.m_StopBut);
    JPanel jPanel5 = new JPanel();
    jPanel5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jPanel5.setLayout(new GridLayout(1, 1, 5, 5));
    jPanel5.add(this.m_ignoreBut);
    jPanel3.add(jPanel5);
    jPanel3.add(jPanel4);
    JPanel jPanel6 = new JPanel();
    jPanel6.setBorder(BorderFactory.createTitledBorder("Clusterer output"));
    jPanel6.setLayout(new BorderLayout());
    JScrollPane jScrollPane = new JScrollPane(this.m_OutText);
    jPanel6.add(jScrollPane, "Center");
    jScrollPane.getViewport().addChangeListener(new ChangeListener(this) {
          private int lastHeight;
          
          private final ClustererPanel this$0;
          
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
    JPanel jPanel7 = new JPanel();
    gridBagLayout = new GridBagLayout();
    jPanel7.setLayout(gridBagLayout);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jPanel2, gridBagConstraints);
    jPanel7.add(jPanel2);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 11;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jPanel3, gridBagConstraints);
    jPanel7.add(jPanel3);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.0D;
    gridBagLayout.setConstraints((Component)this.m_History, gridBagConstraints);
    jPanel7.add((Component)this.m_History);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.weighty = 100.0D;
    gridBagLayout.setConstraints(jPanel6, gridBagConstraints);
    jPanel7.add(jPanel6);
    setLayout(new BorderLayout());
    add(jPanel1, "North");
    add(jPanel7, "Center");
  }
  
  protected void updateRadioLinks() {
    this.m_SetTestBut.setEnabled(this.m_TestSplitBut.isSelected());
    if (this.m_SetTestFrame != null && !this.m_TestSplitBut.isSelected())
      this.m_SetTestFrame.setVisible(false); 
    this.m_PercentText.setEnabled(this.m_PercentBut.isSelected());
    this.m_PercentLab.setEnabled(this.m_PercentBut.isSelected());
    this.m_ClassCombo.setEnabled(this.m_ClassesToClustersBut.isSelected());
  }
  
  public void setLog(Logger paramLogger) {
    this.m_Log = paramLogger;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    this.m_ignoreKeyModel.removeAllElements();
    String[] arrayOfString = new String[this.m_Instances.numAttributes()];
    for (byte b = 0; b < this.m_Instances.numAttributes(); b++) {
      String str1 = this.m_Instances.attribute(b).name();
      this.m_ignoreKeyModel.addElement(str1);
      String str2 = "";
      switch (this.m_Instances.attribute(b).type()) {
        case 1:
          str2 = "(Nom) ";
          break;
        case 0:
          str2 = "(Num) ";
          break;
        case 2:
          str2 = "(Str) ";
          break;
        default:
          str2 = "(???) ";
          break;
      } 
      String str3 = this.m_Instances.attribute(b).name();
      arrayOfString[b] = str2 + str3;
    } 
    this.m_StartBut.setEnabled((this.m_RunThread == null));
    this.m_StopBut.setEnabled((this.m_RunThread != null));
    this.m_ignoreBut.setEnabled(true);
    this.m_ClassCombo.setModel(new DefaultComboBoxModel(arrayOfString));
    this.m_ClassCombo.setSelectedIndex(arrayOfString.length - 1);
    updateRadioLinks();
  }
  
  protected void setTestSet() {
    if (this.m_SetTestFrame == null) {
      SetInstancesPanel setInstancesPanel = new SetInstancesPanel();
      this.m_Summary = setInstancesPanel.getSummary();
      if (this.m_TestInstancesCopy != null)
        setInstancesPanel.setInstances(this.m_TestInstances); 
      setInstancesPanel.addPropertyChangeListener(new PropertyChangeListener(this, setInstancesPanel) {
            private final SetInstancesPanel val$sp;
            
            private final ClustererPanel this$0;
            
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
  
  public static PlotData2D setUpVisualizableInstances(Instances paramInstances, ClusterEvaluation paramClusterEvaluation) throws Exception {
    int i = paramClusterEvaluation.getNumClusters();
    double[] arrayOfDouble = paramClusterEvaluation.getClusterAssignments();
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    byte b1;
    for (b1 = 0; b1 < i; b1++)
      fastVector2.addElement("cluster" + b1); 
    Attribute attribute = new Attribute("Cluster", fastVector2);
    for (b1 = 0; b1 < paramInstances.numAttributes(); b1++)
      fastVector1.addElement(paramInstances.attribute(b1).copy()); 
    fastVector1.addElement(attribute);
    Instances instances = new Instances(paramInstances.relationName() + "_clustered", fastVector1, paramInstances.numInstances());
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    if (paramInstances.classIndex() >= 0) {
      arrayOfInt2 = paramClusterEvaluation.getClassesToClusters();
      arrayOfInt1 = new int[paramInstances.numInstances()];
      for (byte b = 0; b < paramInstances.numInstances(); b++)
        arrayOfInt1[b] = -1; 
    } 
    for (byte b2 = 0; b2 < paramInstances.numInstances(); b2++) {
      double[] arrayOfDouble1 = new double[instances.numAttributes()];
      byte b;
      for (b = 0; b < paramInstances.numAttributes(); b++)
        arrayOfDouble1[b] = paramInstances.instance(b2).value(b); 
      arrayOfDouble1[b] = arrayOfDouble[b2];
      instances.add(new Instance(1.0D, arrayOfDouble1));
      if (arrayOfInt1 != null && (int)paramInstances.instance(b2).classValue() != arrayOfInt2[(int)arrayOfDouble[b2]])
        arrayOfInt1[b2] = 1000; 
    } 
    PlotData2D plotData2D = new PlotData2D(instances);
    if (arrayOfInt1 != null)
      plotData2D.setShapeType(arrayOfInt1); 
    plotData2D.addInstanceNumberAttribute();
    return plotData2D;
  }
  
  protected void startClusterer() {
    if (this.m_RunThread == null) {
      this.m_StartBut.setEnabled(false);
      this.m_StopBut.setEnabled(true);
      this.m_ignoreBut.setEnabled(false);
      this.m_RunThread = new Thread(this) {
          private final ClustererPanel this$0;
          
          public void run() {
            this.this$0.m_Log.statusMessage("Setting up...");
            Instances instances1 = new Instances(this.this$0.m_Instances);
            instances1.setClassIndex(-1);
            Instances instances2 = null;
            PlotData2D plotData2D = null;
            if (this.this$0.m_TestInstances != null)
              instances2 = new Instances(this.this$0.m_TestInstancesCopy); 
            boolean bool = this.this$0.m_StorePredictionsBut.isSelected();
            String str1 = null;
            int[] arrayOfInt = null;
            byte b = 0;
            int i = 66;
            Clusterer clusterer1 = (Clusterer)this.this$0.m_ClustererEditor.getValue();
            Clusterer clusterer2 = null;
            StringBuffer stringBuffer = new StringBuffer();
            String str2 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
            String str3 = clusterer1.getClass().getName();
            if (str3.startsWith("weka.clusterers.")) {
              str2 = str2 + str3.substring("weka.clusterers.".length());
            } else {
              str2 = str2 + str3;
            } 
            try {
              int j;
              int k;
              Instances instances4;
              Instances instances5;
              Instances instances6;
              Instances instances7;
              this.this$0.m_Log.logMessage("Started " + str3);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskStarted(); 
              if (this.this$0.m_PercentBut.isSelected()) {
                b = 2;
                i = Integer.parseInt(this.this$0.m_PercentText.getText());
                if (i <= 0 || i >= 100)
                  throw new Exception("Percentage must be between 0 and 100"); 
              } else if (this.this$0.m_TrainBut.isSelected()) {
                b = 3;
              } else if (this.this$0.m_TestSplitBut.isSelected()) {
                b = 4;
                if (instances2 == null)
                  throw new Exception("No user test set has been opened"); 
                if (!instances1.equalHeaders(instances2))
                  throw new Exception("Train and test set are not compatible"); 
              } else if (this.this$0.m_ClassesToClustersBut.isSelected()) {
                b = 5;
              } else {
                throw new Exception("Unknown test mode");
              } 
              Instances instances3 = new Instances(instances1);
              if (this.this$0.m_ClassesToClustersBut.isSelected()) {
                instances3.setClassIndex(this.this$0.m_ClassCombo.getSelectedIndex());
                instances1.setClassIndex(this.this$0.m_ClassCombo.getSelectedIndex());
                if (instances1.classAttribute().isNumeric())
                  throw new Exception("Class must be nominal for class based evaluation!"); 
              } 
              if (!this.this$0.m_ignoreKeyList.isSelectionEmpty())
                instances3 = this.this$0.removeIgnoreCols(instances3); 
              stringBuffer.append("=== Run information ===\n\n");
              stringBuffer.append("Scheme:       " + str3);
              if (clusterer1 instanceof OptionHandler) {
                String[] arrayOfString = ((OptionHandler)clusterer1).getOptions();
                stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
              } 
              stringBuffer.append("\n");
              stringBuffer.append("Relation:     " + instances1.relationName() + '\n');
              stringBuffer.append("Instances:    " + instances1.numInstances() + '\n');
              stringBuffer.append("Attributes:   " + instances1.numAttributes() + '\n');
              if (instances1.numAttributes() < 100) {
                boolean[] arrayOfBoolean = new boolean[instances1.numAttributes()];
                byte b1;
                for (b1 = 0; b1 < instances1.numAttributes(); b1++)
                  arrayOfBoolean[b1] = true; 
                if (!this.this$0.m_ignoreKeyList.isSelectionEmpty()) {
                  int[] arrayOfInt1 = this.this$0.m_ignoreKeyList.getSelectedIndices();
                  for (byte b2 = 0; b2 < arrayOfInt1.length; b2++)
                    arrayOfBoolean[arrayOfInt1[b2]] = false; 
                } 
                if (this.this$0.m_ClassesToClustersBut.isSelected())
                  arrayOfBoolean[this.this$0.m_ClassCombo.getSelectedIndex()] = false; 
                for (b1 = 0; b1 < instances1.numAttributes(); b1++) {
                  if (arrayOfBoolean[b1])
                    stringBuffer.append("              " + instances1.attribute(b1).name() + '\n'); 
                } 
                if (!this.this$0.m_ignoreKeyList.isSelectionEmpty() || this.this$0.m_ClassesToClustersBut.isSelected()) {
                  stringBuffer.append("Ignored:\n");
                  for (b1 = 0; b1 < instances1.numAttributes(); b1++) {
                    if (!arrayOfBoolean[b1])
                      stringBuffer.append("              " + instances1.attribute(b1).name() + '\n'); 
                  } 
                } 
              } else {
                stringBuffer.append("              [list of attributes omitted]\n");
              } 
              if (!this.this$0.m_ignoreKeyList.isSelectionEmpty())
                arrayOfInt = this.this$0.m_ignoreKeyList.getSelectedIndices(); 
              if (this.this$0.m_ClassesToClustersBut.isSelected())
                if (arrayOfInt == null) {
                  arrayOfInt = new int[1];
                  arrayOfInt[0] = this.this$0.m_ClassCombo.getSelectedIndex();
                } else {
                  int[] arrayOfInt1 = new int[arrayOfInt.length + 1];
                  System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, arrayOfInt.length);
                  arrayOfInt1[arrayOfInt.length] = this.this$0.m_ClassCombo.getSelectedIndex();
                  arrayOfInt = arrayOfInt1;
                }  
              stringBuffer.append("Test mode:    ");
              switch (b) {
                case 3:
                  stringBuffer.append("evaluate on training data\n");
                  break;
                case 2:
                  stringBuffer.append("split " + i + "% train, remainder test\n");
                  break;
                case 4:
                  stringBuffer.append("user supplied test set: " + instances2.numInstances() + " instances\n");
                  break;
                case 5:
                  stringBuffer.append("Classes to clusters evaluation on training data");
                  break;
              } 
              stringBuffer.append("\n");
              this.this$0.m_History.addResult(str2, stringBuffer);
              this.this$0.m_History.setSingle(str2);
              this.this$0.m_Log.statusMessage("Building model on training data...");
              clusterer1.buildClusterer(this.this$0.removeClass(instances3));
              if (b == 2) {
                stringBuffer.append("\n=== Clustering model (full training set) ===\n\n");
                stringBuffer.append(clusterer1.toString() + '\n');
              } 
              this.this$0.m_History.updateResult(str2);
              if (clusterer1 instanceof Drawable)
                try {
                  str1 = ((Drawable)clusterer1).graph();
                } catch (Exception exception) {} 
              SerializedObject serializedObject = new SerializedObject(clusterer1);
              clusterer2 = (Clusterer)serializedObject.getObject();
              ClusterEvaluation clusterEvaluation = new ClusterEvaluation();
              clusterEvaluation.setClusterer(clusterer1);
              switch (b) {
                case 3:
                case 5:
                  this.this$0.m_Log.statusMessage("Clustering training data...");
                  clusterEvaluation.evaluateClusterer(instances3);
                  plotData2D = ClustererPanel.setUpVisualizableInstances(instances1, clusterEvaluation);
                  stringBuffer.append("=== Model and evaluation on training set ===\n\n");
                  break;
                case 2:
                  this.this$0.m_Log.statusMessage("Randomizing instances...");
                  instances1.randomize(new Random(1L));
                  instances3.randomize(new Random(1L));
                  j = instances3.numInstances() * i / 100;
                  k = instances3.numInstances() - j;
                  instances4 = new Instances(instances3, 0, j);
                  instances5 = new Instances(instances3, j, k);
                  instances6 = new Instances(instances1, j, k);
                  this.this$0.m_Log.statusMessage("Building model on training split...");
                  clusterer1.buildClusterer(instances4);
                  this.this$0.m_Log.statusMessage("Evaluating on test split...");
                  clusterEvaluation.evaluateClusterer(instances5);
                  plotData2D = ClustererPanel.setUpVisualizableInstances(instances6, clusterEvaluation);
                  stringBuffer.append("=== Model and evaluation on test split ===\n");
                  break;
                case 4:
                  this.this$0.m_Log.statusMessage("Evaluating on test data...");
                  instances7 = new Instances(instances2);
                  if (!this.this$0.m_ignoreKeyList.isSelectionEmpty())
                    instances7 = this.this$0.removeIgnoreCols(instances7); 
                  clusterEvaluation.evaluateClusterer(instances7);
                  plotData2D = ClustererPanel.setUpVisualizableInstances(instances2, clusterEvaluation);
                  stringBuffer.append("=== Model and evaluation on test set ===\n");
                  break;
                default:
                  throw new Exception("Test mode not implemented");
              } 
              stringBuffer.append(clusterEvaluation.clusterResultsToString());
              stringBuffer.append("\n");
              this.this$0.m_History.updateResult(str2);
              this.this$0.m_Log.logMessage("Finished " + str3);
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              exception.printStackTrace();
              this.this$0.m_Log.logMessage(exception.getMessage());
              JOptionPane.showMessageDialog(this.this$0, "Problem evaluating clusterer:\n" + exception.getMessage(), "Evaluate clusterer", 0);
              this.this$0.m_Log.statusMessage("Problem evaluating clusterer");
            } finally {
              if (plotData2D != null) {
                this.this$0.m_CurrentVis = new VisualizePanel();
                this.this$0.m_CurrentVis.setName(str2 + " (" + instances1.relationName() + ")");
                this.this$0.m_CurrentVis.setLog(this.this$0.m_Log);
                plotData2D.setPlotName(str2 + " (" + instances1.relationName() + ")");
                try {
                  this.this$0.m_CurrentVis.addPlot(plotData2D);
                } catch (Exception exception) {
                  System.err.println(exception);
                } 
                FastVector fastVector = new FastVector();
                fastVector.addElement(clusterer2);
                Instances instances = new Instances(this.this$0.m_Instances, 0);
                fastVector.addElement(instances);
                if (arrayOfInt != null)
                  fastVector.addElement(arrayOfInt); 
                if (bool) {
                  fastVector.addElement(this.this$0.m_CurrentVis);
                  if (str1 != null)
                    fastVector.addElement(str1); 
                } 
                this.this$0.m_History.addObject(str2, fastVector);
              } 
              if (isInterrupted()) {
                this.this$0.m_Log.logMessage("Interrupted " + str3);
                this.this$0.m_Log.statusMessage("See error log");
              } 
              this.this$0.m_RunThread = null;
              this.this$0.m_StartBut.setEnabled(true);
              this.this$0.m_StopBut.setEnabled(false);
              this.this$0.m_ignoreBut.setEnabled(true);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskFinished(); 
            } 
          }
        };
      this.m_RunThread.setPriority(1);
      this.m_RunThread.start();
    } 
  }
  
  private Instances removeClass(Instances paramInstances) {
    Remove remove = new Remove();
    Instances instances = null;
    try {
      if (paramInstances.classIndex() < 0) {
        instances = paramInstances;
      } else {
        remove.setAttributeIndices("" + (paramInstances.classIndex() + 1));
        remove.setInvertSelection(false);
        remove.setInputFormat(paramInstances);
        instances = Filter.useFilter(paramInstances, (Filter)remove);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return instances;
  }
  
  private Instances removeIgnoreCols(Instances paramInstances) {
    if (this.m_ClassesToClustersBut.isSelected()) {
      int i = this.m_ClassCombo.getSelectedIndex();
      if (this.m_ignoreKeyList.isSelectedIndex(i))
        this.m_ignoreKeyList.removeSelectionInterval(i, i); 
    } 
    int[] arrayOfInt = this.m_ignoreKeyList.getSelectedIndices();
    Remove remove = new Remove();
    Instances instances = null;
    try {
      remove.setAttributeIndicesArray(arrayOfInt);
      remove.setInvertSelection(false);
      remove.setInputFormat(paramInstances);
      instances = Filter.useFilter(paramInstances, (Filter)remove);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return instances;
  }
  
  private Instances removeIgnoreCols(Instances paramInstances, int[] paramArrayOfint) {
    Remove remove = new Remove();
    Instances instances = null;
    try {
      remove.setAttributeIndicesArray(paramArrayOfint);
      remove.setInvertSelection(false);
      remove.setInputFormat(paramInstances);
      instances = Filter.useFilter(paramInstances, (Filter)remove);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return instances;
  }
  
  protected void stopClusterer() {
    if (this.m_RunThread != null) {
      this.m_RunThread.interrupt();
      this.m_RunThread.stop();
    } 
  }
  
  protected void visualizeTree(String paramString1, String paramString2) {
    JFrame jFrame = new JFrame("Weka Classifier Tree Visualizer: " + paramString2);
    jFrame.setSize(500, 400);
    jFrame.getContentPane().setLayout(new BorderLayout());
    TreeVisualizer treeVisualizer = new TreeVisualizer(null, paramString1, (NodePlace)new PlaceNode2());
    jFrame.getContentPane().add((Component)treeVisualizer, "Center");
    jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
          private final JFrame val$jf;
          
          private final ClustererPanel this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.val$jf.dispose();
          }
        });
    jFrame.setVisible(true);
    treeVisualizer.fitToScreen();
  }
  
  protected void visualizeClusterAssignments(VisualizePanel paramVisualizePanel) {
    if (paramVisualizePanel != null) {
      String str = paramVisualizePanel.getName();
      JFrame jFrame = new JFrame("Weka Clusterer Visualize: " + str);
      jFrame.setSize(500, 400);
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add((Component)paramVisualizePanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final ClustererPanel this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
            }
          });
      jFrame.setVisible(true);
    } 
  }
  
  protected void visualizeClusterer(String paramString, int paramInt1, int paramInt2) {
    String str1 = paramString;
    JPopupMenu jPopupMenu = new JPopupMenu();
    JMenuItem jMenuItem1 = new JMenuItem("View in main window");
    if (str1 != null) {
      jMenuItem1.addActionListener(new ActionListener(this, str1) {
            private final String val$selectedName;
            
            private final ClustererPanel this$0;
            
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
            
            private final ClustererPanel this$0;
            
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
            
            private final ClustererPanel this$0;
            
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
          private final ClustererPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.loadClusterer();
          }
        });
    jPopupMenu.add(jMenuItem4);
    FastVector fastVector = null;
    if (str1 != null)
      fastVector = (FastVector)this.m_History.getNamedObject(str1); 
    VisualizePanel visualizePanel1 = null;
    String str2 = null;
    Clusterer clusterer1 = null;
    Instances instances1 = null;
    int[] arrayOfInt1 = null;
    if (fastVector != null)
      for (byte b = 0; b < fastVector.size(); b++) {
        Object object = fastVector.elementAt(b);
        if (object instanceof Clusterer) {
          clusterer1 = (Clusterer)object;
        } else if (object instanceof Instances) {
          instances1 = (Instances)object;
        } else if (object instanceof int[]) {
          arrayOfInt1 = (int[])object;
        } else if (object instanceof VisualizePanel) {
          visualizePanel1 = (VisualizePanel)object;
        } else if (object instanceof String) {
          str2 = (String)object;
        } 
      }  
    VisualizePanel visualizePanel2 = visualizePanel1;
    String str3 = str2;
    Clusterer clusterer2 = clusterer1;
    Instances instances2 = instances1;
    int[] arrayOfInt2 = arrayOfInt1;
    JMenuItem jMenuItem5 = new JMenuItem("Save model");
    if (clusterer2 != null) {
      jMenuItem5.addActionListener(new ActionListener(this, str1, clusterer2, instances2, arrayOfInt2) {
            private final String val$selectedName;
            
            private final Clusterer val$clusterer;
            
            private final Instances val$trainHeader;
            
            private final int[] val$ignoreAtts;
            
            private final ClustererPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.saveClusterer(this.val$selectedName, this.val$clusterer, this.val$trainHeader, this.val$ignoreAtts);
            }
          });
    } else {
      jMenuItem5.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem5);
    JMenuItem jMenuItem6 = new JMenuItem("Re-evaluate model on current test set");
    if (clusterer2 != null && this.m_TestInstances != null) {
      jMenuItem6.addActionListener(new ActionListener(this, str1, clusterer2, instances2, arrayOfInt2) {
            private final String val$selectedName;
            
            private final Clusterer val$clusterer;
            
            private final Instances val$trainHeader;
            
            private final int[] val$ignoreAtts;
            
            private final ClustererPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.reevaluateModel(this.val$selectedName, this.val$clusterer, this.val$trainHeader, this.val$ignoreAtts);
            }
          });
    } else {
      jMenuItem6.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem6);
    jPopupMenu.addSeparator();
    JMenuItem jMenuItem7 = new JMenuItem("Visualize cluster assignments");
    if (visualizePanel2 != null) {
      jMenuItem7.addActionListener(new ActionListener(this, visualizePanel2) {
            private final VisualizePanel val$vp;
            
            private final ClustererPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.visualizeClusterAssignments(this.val$vp);
            }
          });
    } else {
      jMenuItem7.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem7);
    JMenuItem jMenuItem8 = new JMenuItem("Visualize tree");
    if (str3 != null) {
      jMenuItem8.addActionListener(new ActionListener(this, visualizePanel2, str1, str3) {
            private final VisualizePanel val$vp;
            
            private final String val$selectedName;
            
            private final String val$grph;
            
            private final ClustererPanel this$0;
            
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
    } else {
      jMenuItem8.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem8);
    jPopupMenu.show(this.m_History.getList(), paramInt1, paramInt2);
  }
  
  protected void saveBuffer(String paramString) {
    StringBuffer stringBuffer = this.m_History.getNamedBuffer(paramString);
    if (stringBuffer != null && this.m_SaveOut.save(stringBuffer))
      this.m_Log.logMessage("Save successful."); 
  }
  
  private void setIgnoreColumns() {
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, this.m_ignoreKeyList);
    int i = listSelectorDialog.showDialog();
    if (i != 0)
      this.m_ignoreKeyList.clearSelection(); 
  }
  
  protected void saveClusterer(String paramString, Clusterer paramClusterer, Instances paramInstances, int[] paramArrayOfint) {
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
        objectOutputStream.writeObject(paramClusterer);
        if (paramInstances != null)
          objectOutputStream.writeObject(paramInstances); 
        if (paramArrayOfint != null)
          objectOutputStream.writeObject(paramArrayOfint); 
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
  
  protected void loadClusterer() {
    int i = this.m_FileChooser.showOpenDialog(this);
    if (i == 0) {
      File file = this.m_FileChooser.getSelectedFile();
      Clusterer clusterer = null;
      Instances instances = null;
      int[] arrayOfInt = null;
      this.m_Log.statusMessage("Loading model from file...");
      try {
        GZIPInputStream gZIPInputStream;
        FileInputStream fileInputStream = new FileInputStream(file);
        if (file.getName().endsWith(".gz"))
          gZIPInputStream = new GZIPInputStream(fileInputStream); 
        ObjectInputStream objectInputStream = new ObjectInputStream(gZIPInputStream);
        clusterer = (Clusterer)objectInputStream.readObject();
        try {
          instances = (Instances)objectInputStream.readObject();
          arrayOfInt = (int[])objectInputStream.readObject();
        } catch (Exception exception) {}
        objectInputStream.close();
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(null, exception, "Load Failed", 0);
      } 
      this.m_Log.statusMessage("OK");
      if (clusterer != null) {
        this.m_Log.logMessage("Loaded model from file '" + file.getName() + "'");
        String str1 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
        String str2 = clusterer.getClass().getName();
        if (str2.startsWith("weka.clusterers."))
          str2 = str2.substring("weka.clusterers.".length()); 
        str1 = str1 + str2 + " from file '" + file.getName() + "'";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("=== Model information ===\n\n");
        stringBuffer.append("Filename:     " + file.getName() + "\n");
        stringBuffer.append("Scheme:       " + str2);
        if (clusterer instanceof OptionHandler) {
          String[] arrayOfString = ((OptionHandler)clusterer).getOptions();
          stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
        } 
        stringBuffer.append("\n");
        if (instances != null) {
          stringBuffer.append("Relation:     " + instances.relationName() + '\n');
          stringBuffer.append("Attributes:   " + instances.numAttributes() + '\n');
          if (instances.numAttributes() < 100) {
            boolean[] arrayOfBoolean = new boolean[instances.numAttributes()];
            byte b;
            for (b = 0; b < instances.numAttributes(); b++)
              arrayOfBoolean[b] = true; 
            if (arrayOfInt != null)
              for (b = 0; b < arrayOfInt.length; b++)
                arrayOfBoolean[arrayOfInt[b]] = false;  
            for (b = 0; b < instances.numAttributes(); b++) {
              if (arrayOfBoolean[b])
                stringBuffer.append("              " + instances.attribute(b).name() + '\n'); 
            } 
            if (arrayOfInt != null) {
              stringBuffer.append("Ignored:\n");
              for (b = 0; b < arrayOfInt.length; b++)
                stringBuffer.append("              " + instances.attribute(arrayOfInt[b]).name() + '\n'); 
            } 
          } else {
            stringBuffer.append("              [list of attributes omitted]\n");
          } 
        } else {
          stringBuffer.append("\nTraining data unknown\n");
        } 
        stringBuffer.append("\n=== Clustering model ===\n\n");
        stringBuffer.append(clusterer.toString() + "\n");
        this.m_History.addResult(str1, stringBuffer);
        this.m_History.setSingle(str1);
        FastVector fastVector = new FastVector();
        fastVector.addElement(clusterer);
        if (instances != null)
          fastVector.addElement(instances); 
        if (arrayOfInt != null)
          fastVector.addElement(arrayOfInt); 
        String str3 = null;
        if (clusterer instanceof Drawable)
          try {
            str3 = ((Drawable)clusterer).graph();
          } catch (Exception exception) {} 
        if (str3 != null)
          fastVector.addElement(str3); 
        this.m_History.addObject(str1, fastVector);
      } 
    } 
  }
  
  protected void reevaluateModel(String paramString, Clusterer paramClusterer, Instances paramInstances, int[] paramArrayOfint) {
    StringBuffer stringBuffer = this.m_History.getNamedBuffer(paramString);
    Instances instances = null;
    PlotData2D plotData2D = null;
    if (this.m_TestInstances != null)
      instances = new Instances(this.m_TestInstancesCopy); 
    boolean bool = this.m_StorePredictionsBut.isSelected();
    Object object = null;
    try {
      if (instances == null)
        throw new Exception("No user test set has been opened"); 
      if (paramInstances != null && !paramInstances.equalHeaders(instances))
        throw new Exception("Train and test set are not compatible"); 
      this.m_Log.statusMessage("Evaluating on test data...");
      this.m_Log.logMessage("Re-evaluating clusterer (" + paramString + ") on test set");
      ClusterEvaluation clusterEvaluation = new ClusterEvaluation();
      clusterEvaluation.setClusterer(paramClusterer);
      Instances instances1 = new Instances(instances);
      if (paramArrayOfint != null)
        instances1 = removeIgnoreCols(instances1, paramArrayOfint); 
      clusterEvaluation.evaluateClusterer(instances1);
      plotData2D = setUpVisualizableInstances(instances, clusterEvaluation);
      stringBuffer.append("\n=== Re-evaluation on test set ===\n\n");
      stringBuffer.append("User supplied test set\n");
      stringBuffer.append("Relation:     " + instances.relationName() + '\n');
      stringBuffer.append("Instances:    " + instances.numInstances() + '\n');
      stringBuffer.append("Attributes:   " + instances.numAttributes() + "\n\n");
      if (paramInstances == null)
        stringBuffer.append("NOTE - if test set is not compatible then results are unpredictable\n\n"); 
      stringBuffer.append(clusterEvaluation.clusterResultsToString());
      stringBuffer.append("\n");
      this.m_History.updateResult(paramString);
      this.m_Log.logMessage("Finished re-evaluation");
      this.m_Log.statusMessage("OK");
    } catch (Exception exception) {
      exception.printStackTrace();
      this.m_Log.logMessage(exception.getMessage());
      JOptionPane.showMessageDialog(this, "Problem evaluating clusterer:\n" + exception.getMessage(), "Evaluate clusterer", 0);
      this.m_Log.statusMessage("Problem evaluating clusterer");
    } finally {
      if (plotData2D != null) {
        this.m_CurrentVis = new VisualizePanel();
        this.m_CurrentVis.setName(paramString + " (" + instances.relationName() + ")");
        this.m_CurrentVis.setLog(this.m_Log);
        plotData2D.setPlotName(paramString + " (" + instances.relationName() + ")");
        try {
          this.m_CurrentVis.addPlot(plotData2D);
        } catch (Exception exception) {
          System.err.println(exception);
        } 
        FastVector fastVector = new FastVector();
        fastVector.addElement(paramClusterer);
        if (paramInstances != null)
          fastVector.addElement(paramInstances); 
        if (paramArrayOfint != null)
          fastVector.addElement(paramArrayOfint); 
        if (bool) {
          fastVector.addElement(this.m_CurrentVis);
          if (object != null)
            fastVector.addElement(object); 
        } 
        this.m_History.addObject(paramString, fastVector);
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Explorer: Cluster");
      jFrame.getContentPane().setLayout(new BorderLayout());
      ClustererPanel clustererPanel = new ClustererPanel();
      jFrame.getContentPane().add(clustererPanel, "Center");
      LogPanel logPanel = new LogPanel();
      clustererPanel.setLog((Logger)logPanel);
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
        clustererPanel.setInstances(instances);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Clusterer.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(DensityBasedClusterer.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(UnsupervisedSubsetEvaluator.class, GenericObjectEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\explorer\ClustererPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */