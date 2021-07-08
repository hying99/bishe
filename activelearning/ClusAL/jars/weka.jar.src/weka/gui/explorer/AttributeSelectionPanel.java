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
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.AttributeTransformer;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.gui.FileEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.PropertyPanel;
import weka.gui.ResultHistoryPanel;
import weka.gui.SaveBuffer;
import weka.gui.SelectedTagEditor;
import weka.gui.SysErrLog;
import weka.gui.TaskLogger;
import weka.gui.visualize.MatrixPanel;

public class AttributeSelectionPanel extends JPanel {
  protected GenericObjectEditor m_AttributeEvaluatorEditor = new GenericObjectEditor();
  
  protected GenericObjectEditor m_AttributeSearchEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_AEEPanel = new PropertyPanel((PropertyEditor)this.m_AttributeEvaluatorEditor);
  
  protected PropertyPanel m_ASEPanel = new PropertyPanel((PropertyEditor)this.m_AttributeSearchEditor);
  
  protected JTextArea m_OutText = new JTextArea(20, 40);
  
  protected Logger m_Log = (Logger)new SysErrLog();
  
  SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
  
  protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
  
  protected JComboBox m_ClassCombo = new JComboBox();
  
  protected JRadioButton m_CVBut = new JRadioButton("Cross-validation");
  
  protected JRadioButton m_TrainBut = new JRadioButton("Use full training set");
  
  protected JLabel m_CVLab = new JLabel("Folds", 4);
  
  protected JTextField m_CVText = new JTextField("10");
  
  protected JLabel m_SeedLab = new JLabel("Seed", 4);
  
  protected JTextField m_SeedText = new JTextField("1");
  
  ActionListener m_RadioListener = new ActionListener(this) {
      private final AttributeSelectionPanel this$0;
      
      public void actionPerformed(ActionEvent param1ActionEvent) {
        this.this$0.updateRadioLinks();
      }
    };
  
  protected JButton m_StartBut = new JButton("Start");
  
  protected JButton m_StopBut = new JButton("Stop");
  
  private Dimension COMBO_SIZE = new Dimension(150, (this.m_StartBut.getPreferredSize()).height);
  
  protected Instances m_Instances;
  
  protected Thread m_RunThread;
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public AttributeSelectionPanel() {
    this.m_OutText.setEditable(false);
    this.m_OutText.setFont(new Font("Monospaced", 0, 12));
    this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_OutText.addMouseListener(new MouseAdapter(this) {
          private final AttributeSelectionPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16)
              this.this$0.m_OutText.selectAll(); 
          }
        });
    this.m_History.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
    this.m_AttributeEvaluatorEditor.setClassType(ASEvaluation.class);
    this.m_AttributeEvaluatorEditor.setValue(new CfsSubsetEval());
    this.m_AttributeEvaluatorEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.updateRadioLinks();
            this.this$0.repaint();
          }
        });
    this.m_AttributeSearchEditor.setClassType(ASSearch.class);
    this.m_AttributeSearchEditor.setValue(new BestFirst());
    this.m_AttributeSearchEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.repaint();
          }
        });
    this.m_ClassCombo.setToolTipText("Select the attribute to use as the class");
    this.m_TrainBut.setToolTipText("select attributes using the full training dataset");
    this.m_CVBut.setToolTipText("Perform a n-fold cross-validation");
    this.m_StartBut.setToolTipText("Starts attribute selection");
    this.m_StopBut.setToolTipText("Stops a attribute selection task");
    this.m_ClassCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_ClassCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_ClassCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_History.setPreferredSize(this.COMBO_SIZE);
    this.m_History.setMaximumSize(this.COMBO_SIZE);
    this.m_History.setMinimumSize(this.COMBO_SIZE);
    this.m_ClassCombo.setEnabled(false);
    this.m_TrainBut.setSelected(true);
    updateRadioLinks();
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_TrainBut);
    buttonGroup.add(this.m_CVBut);
    this.m_TrainBut.addActionListener(this.m_RadioListener);
    this.m_CVBut.addActionListener(this.m_RadioListener);
    this.m_StartBut.setEnabled(false);
    this.m_StopBut.setEnabled(false);
    this.m_StartBut.addActionListener(new ActionListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.startAttributeSelection();
          }
        });
    this.m_StopBut.addActionListener(new ActionListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.stopAttributeSelection();
          }
        });
    this.m_History.setHandleRightClicks(false);
    this.m_History.getList().addMouseListener(new MouseAdapter(this) {
          private final AttributeSelectionPanel this$0;
          
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
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Attribute Evaluator"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add((Component)this.m_AEEPanel, "North");
    JPanel jPanel2 = new JPanel();
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search Method"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel2.setLayout(new BorderLayout());
    jPanel2.add((Component)this.m_ASEPanel, "North");
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BorderLayout());
    jPanel3.add(jPanel1, "North");
    jPanel3.add(jPanel2, "Center");
    JPanel jPanel4 = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    jPanel4.setLayout(gridBagLayout);
    jPanel4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Attribute Selection Mode"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(this.m_TrainBut, gridBagConstraints);
    jPanel4.add(this.m_TrainBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(this.m_CVBut, gridBagConstraints);
    jPanel4.add(this.m_CVBut);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new Insets(2, 10, 2, 10);
    gridBagLayout.setConstraints(this.m_CVLab, gridBagConstraints);
    jPanel4.add(this.m_CVLab);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.ipadx = 20;
    gridBagLayout.setConstraints(this.m_CVText, gridBagConstraints);
    jPanel4.add(this.m_CVText);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new Insets(2, 10, 2, 10);
    gridBagLayout.setConstraints(this.m_SeedLab, gridBagConstraints);
    jPanel4.add(this.m_SeedLab);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.ipadx = 20;
    gridBagLayout.setConstraints(this.m_SeedText, gridBagConstraints);
    jPanel4.add(this.m_SeedText);
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new GridLayout(2, 2));
    jPanel5.add(this.m_ClassCombo);
    this.m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    JPanel jPanel6 = new JPanel();
    jPanel6.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jPanel6.setLayout(new GridLayout(1, 2, 5, 5));
    jPanel6.add(this.m_StartBut);
    jPanel6.add(this.m_StopBut);
    jPanel5.add(jPanel6);
    JPanel jPanel7 = new JPanel();
    jPanel7.setBorder(BorderFactory.createTitledBorder("Attribute selection output"));
    jPanel7.setLayout(new BorderLayout());
    JScrollPane jScrollPane = new JScrollPane(this.m_OutText);
    jPanel7.add(jScrollPane, "Center");
    jScrollPane.getViewport().addChangeListener(new ChangeListener(this) {
          private int lastHeight;
          
          private final AttributeSelectionPanel this$0;
          
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
    JPanel jPanel8 = new JPanel();
    gridBagLayout = new GridBagLayout();
    jPanel8.setLayout(gridBagLayout);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.0D;
    gridBagLayout.setConstraints(jPanel4, gridBagConstraints);
    jPanel8.add(jPanel4);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 11;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.0D;
    gridBagLayout.setConstraints(jPanel5, gridBagConstraints);
    jPanel8.add(jPanel5);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.weighty = 100.0D;
    gridBagLayout.setConstraints((Component)this.m_History, gridBagConstraints);
    jPanel8.add((Component)this.m_History);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.weighty = 100.0D;
    gridBagLayout.setConstraints(jPanel7, gridBagConstraints);
    jPanel8.add(jPanel7);
    setLayout(new BorderLayout());
    add(jPanel3, "North");
    add(jPanel8, "Center");
  }
  
  protected void updateRadioLinks() {
    this.m_CVBut.setEnabled(true);
    this.m_CVText.setEnabled(this.m_CVBut.isSelected());
    this.m_CVLab.setEnabled(this.m_CVBut.isSelected());
    this.m_SeedText.setEnabled(this.m_CVBut.isSelected());
    this.m_SeedLab.setEnabled(this.m_CVBut.isSelected());
    if (this.m_AttributeEvaluatorEditor.getValue() instanceof AttributeTransformer) {
      this.m_CVBut.setSelected(false);
      this.m_CVBut.setEnabled(false);
      this.m_CVText.setEnabled(false);
      this.m_CVLab.setEnabled(false);
      this.m_SeedText.setEnabled(false);
      this.m_SeedLab.setEnabled(false);
      this.m_TrainBut.setSelected(true);
    } 
  }
  
  public void setLog(Logger paramLogger) {
    this.m_Log = paramLogger;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    String[] arrayOfString = new String[this.m_Instances.numAttributes()];
    for (byte b = 0; b < arrayOfString.length; b++) {
      String str1 = "";
      switch (this.m_Instances.attribute(b).type()) {
        case 1:
          str1 = "(Nom) ";
          break;
        case 0:
          str1 = "(Num) ";
          break;
        case 2:
          str1 = "(Str) ";
          break;
        default:
          str1 = "(???) ";
          break;
      } 
      String str2 = this.m_Instances.attribute(b).name();
      arrayOfString[b] = str1 + str2;
    } 
    this.m_StartBut.setEnabled((this.m_RunThread == null));
    this.m_StopBut.setEnabled((this.m_RunThread != null));
    this.m_ClassCombo.setModel(new DefaultComboBoxModel(arrayOfString));
    this.m_ClassCombo.setSelectedIndex(arrayOfString.length - 1);
    this.m_ClassCombo.setEnabled(true);
  }
  
  protected void startAttributeSelection() {
    if (this.m_RunThread == null) {
      this.m_StartBut.setEnabled(false);
      this.m_StopBut.setEnabled(true);
      this.m_RunThread = new Thread(this) {
          private final AttributeSelectionPanel this$0;
          
          public void run() {
            this.this$0.m_Log.statusMessage("Setting up...");
            Instances instances = new Instances(this.this$0.m_Instances);
            boolean bool = false;
            int i = 10;
            int j = 1;
            int k = this.this$0.m_ClassCombo.getSelectedIndex();
            ASEvaluation aSEvaluation = (ASEvaluation)this.this$0.m_AttributeEvaluatorEditor.getValue();
            ASSearch aSSearch = (ASSearch)this.this$0.m_AttributeSearchEditor.getValue();
            StringBuffer stringBuffer = new StringBuffer();
            String str1 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
            String str2 = aSSearch.getClass().getName();
            if (str2.startsWith("weka.attributeSelection.")) {
              str1 = str1 + str2.substring("weka.attributeSelection.".length());
            } else {
              str1 = str1 + str2;
            } 
            String str3 = aSEvaluation.getClass().getName();
            if (str3.startsWith("weka.attributeSelection.")) {
              str1 = str1 + " + " + str3.substring("weka.attributeSelection.".length());
            } else {
              str1 = str1 + " + " + str3;
            } 
            AttributeSelection attributeSelection = null;
            try {
              Random random;
              byte b;
              if (this.this$0.m_CVBut.isSelected()) {
                bool = true;
                i = Integer.parseInt(this.this$0.m_CVText.getText());
                j = Integer.parseInt(this.this$0.m_SeedText.getText());
                if (i <= 1)
                  throw new Exception("Number of folds must be greater than 1"); 
              } 
              instances.setClassIndex(k);
              this.this$0.m_Log.logMessage("Started " + str3);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskStarted(); 
              stringBuffer.append("=== Run information ===\n\n");
              stringBuffer.append("Evaluator:    " + str3);
              if (aSEvaluation instanceof OptionHandler) {
                String[] arrayOfString = ((OptionHandler)aSEvaluation).getOptions();
                stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
              } 
              stringBuffer.append("\nSearch:       " + str2);
              if (aSSearch instanceof OptionHandler) {
                String[] arrayOfString = ((OptionHandler)aSSearch).getOptions();
                stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
              } 
              stringBuffer.append("\n");
              stringBuffer.append("Relation:     " + instances.relationName() + '\n');
              stringBuffer.append("Instances:    " + instances.numInstances() + '\n');
              stringBuffer.append("Attributes:   " + instances.numAttributes() + '\n');
              if (instances.numAttributes() < 100) {
                for (byte b1 = 0; b1 < instances.numAttributes(); b1++)
                  stringBuffer.append("              " + instances.attribute(b1).name() + '\n'); 
              } else {
                stringBuffer.append("              [list of attributes omitted]\n");
              } 
              stringBuffer.append("Evaluation mode:    ");
              switch (bool) {
                case false:
                  stringBuffer.append("evaluate on all training data\n");
                  break;
                case true:
                  stringBuffer.append("" + i + "-fold cross-validation\n");
                  break;
              } 
              stringBuffer.append("\n");
              this.this$0.m_History.addResult(str1, stringBuffer);
              this.this$0.m_History.setSingle(str1);
              this.this$0.m_Log.statusMessage("Doing feature selection...");
              this.this$0.m_History.updateResult(str1);
              attributeSelection = new AttributeSelection();
              attributeSelection.setEvaluator(aSEvaluation);
              attributeSelection.setSearch(aSSearch);
              attributeSelection.setFolds(i);
              attributeSelection.setSeed(j);
              if (bool == true)
                attributeSelection.setXval(true); 
              switch (bool) {
                case false:
                  this.this$0.m_Log.statusMessage("Evaluating on training data...");
                  attributeSelection.SelectAttributes(instances);
                  break;
                case true:
                  this.this$0.m_Log.statusMessage("Randomizing instances...");
                  random = new Random(j);
                  instances.randomize(random);
                  if (instances.attribute(k).isNominal()) {
                    this.this$0.m_Log.statusMessage("Stratifying instances...");
                    instances.stratify(i);
                  } 
                  for (b = 0; b < i; b++) {
                    this.this$0.m_Log.statusMessage("Creating splits for fold " + (b + 1) + "...");
                    Instances instances1 = instances.trainCV(i, b, random);
                    this.this$0.m_Log.statusMessage("Selecting attributes using all but fold " + (b + 1) + "...");
                    attributeSelection.selectAttributesCVSplit(instances1);
                  } 
                  break;
                default:
                  throw new Exception("Test mode not implemented");
              } 
              if (!bool) {
                stringBuffer.append(attributeSelection.toResultsString());
              } else {
                stringBuffer.append(attributeSelection.CVResultsString());
              } 
              stringBuffer.append("\n");
              this.this$0.m_History.updateResult(str1);
              this.this$0.m_Log.logMessage("Finished " + str3 + " " + str2);
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              this.this$0.m_Log.logMessage(exception.getMessage());
              this.this$0.m_Log.statusMessage("See error log");
            } finally {
              if (aSEvaluation instanceof AttributeTransformer) {
                try {
                  Instances instances1 = ((AttributeTransformer)aSEvaluation).transformedData();
                  instances1.setRelationName("AT: " + instances1.relationName());
                  FastVector fastVector = new FastVector();
                  fastVector.addElement(instances1);
                  this.this$0.m_History.addObject(str1, fastVector);
                } catch (Exception exception) {
                  System.err.println(exception);
                  exception.printStackTrace();
                } 
              } else if (!bool) {
                try {
                  Instances instances1 = attributeSelection.reduceDimensionality(instances);
                  FastVector fastVector = new FastVector();
                  fastVector.addElement(instances1);
                  this.this$0.m_History.addObject(str1, fastVector);
                } catch (Exception exception) {
                  exception.printStackTrace();
                } 
              } 
              if (isInterrupted()) {
                this.this$0.m_Log.logMessage("Interrupted " + str3 + " " + str2);
                this.this$0.m_Log.statusMessage("See error log");
              } 
              this.this$0.m_RunThread = null;
              this.this$0.m_StartBut.setEnabled(true);
              this.this$0.m_StopBut.setEnabled(false);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskFinished(); 
            } 
          }
        };
      this.m_RunThread.setPriority(1);
      this.m_RunThread.start();
    } 
  }
  
  protected void stopAttributeSelection() {
    if (this.m_RunThread != null) {
      this.m_RunThread.interrupt();
      this.m_RunThread.stop();
    } 
  }
  
  protected void saveBuffer(String paramString) {
    StringBuffer stringBuffer = this.m_History.getNamedBuffer(paramString);
    if (stringBuffer != null && this.m_SaveOut.save(stringBuffer))
      this.m_Log.logMessage("Save succesful."); 
  }
  
  protected void visualizeTransformedData(Instances paramInstances) {
    if (paramInstances != null) {
      MatrixPanel matrixPanel = new MatrixPanel();
      matrixPanel.setInstances(paramInstances);
      String str = paramInstances.relationName();
      JFrame jFrame = new JFrame("Weka Attribute Selection Visualize: " + str);
      jFrame.setSize(800, 600);
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add((Component)matrixPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final AttributeSelectionPanel this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
            }
          });
      jFrame.setVisible(true);
    } 
  }
  
  protected void visualize(String paramString, int paramInt1, int paramInt2) {
    String str = paramString;
    JPopupMenu jPopupMenu = new JPopupMenu();
    JMenuItem jMenuItem1 = new JMenuItem("View in main window");
    if (str != null) {
      jMenuItem1.addActionListener(new ActionListener(this, str) {
            private final String val$selectedName;
            
            private final AttributeSelectionPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_History.setSingle(this.val$selectedName);
            }
          });
    } else {
      jMenuItem1.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem1);
    JMenuItem jMenuItem2 = new JMenuItem("View in separate window");
    if (str != null) {
      jMenuItem2.addActionListener(new ActionListener(this, str) {
            private final String val$selectedName;
            
            private final AttributeSelectionPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_History.openFrame(this.val$selectedName);
            }
          });
    } else {
      jMenuItem2.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem2);
    JMenuItem jMenuItem3 = new JMenuItem("Save result buffer");
    if (str != null) {
      jMenuItem3.addActionListener(new ActionListener(this, str) {
            private final String val$selectedName;
            
            private final AttributeSelectionPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.saveBuffer(this.val$selectedName);
            }
          });
    } else {
      jMenuItem3.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem3);
    FastVector fastVector = null;
    if (str != null)
      fastVector = (FastVector)this.m_History.getNamedObject(str); 
    Instances instances1 = null;
    if (fastVector != null)
      for (byte b = 0; b < fastVector.size(); b++) {
        Object object = fastVector.elementAt(b);
        if (object instanceof Instances)
          instances1 = (Instances)object; 
      }  
    Instances instances2 = instances1;
    JMenuItem jMenuItem4 = null;
    if (instances2 != null) {
      if (instances2.relationName().startsWith("AT:")) {
        jMenuItem4 = new JMenuItem("Visualize transformed data");
      } else {
        jMenuItem4 = new JMenuItem("Visualize reduced data");
      } 
      jPopupMenu.addSeparator();
    } 
    if (instances2 != null && jMenuItem4 != null)
      jMenuItem4.addActionListener(new ActionListener(this, instances2) {
            private final Instances val$ti;
            
            private final AttributeSelectionPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.visualizeTransformedData(this.val$ti);
            }
          }); 
    if (jMenuItem4 != null)
      jPopupMenu.add(jMenuItem4); 
    jPopupMenu.show(this.m_History.getList(), paramInt1, paramInt2);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Explorer: Select attributes");
      jFrame.getContentPane().setLayout(new BorderLayout());
      AttributeSelectionPanel attributeSelectionPanel = new AttributeSelectionPanel();
      jFrame.getContentPane().add(attributeSelectionPanel, "Center");
      LogPanel logPanel = new LogPanel();
      attributeSelectionPanel.setLog((Logger)logPanel);
      jFrame.getContentPane().add((Component)logPanel, "South");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      if (paramArrayOfString.length == 1) {
        System.err.println("Loading instances from " + paramArrayOfString[0]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
        Instances instances = new Instances(bufferedReader);
        attributeSelectionPanel.setInstances(instances);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    PropertyEditorManager.registerEditor(File.class, FileEditor.class);
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor((array$Lweka$classifiers$Classifier == null) ? (array$Lweka$classifiers$Classifier = class$("[Lweka.classifiers.Classifier;")) : array$Lweka$classifiers$Classifier, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASEvaluation.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASSearch.class, GenericObjectEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\explorer\AttributeSelectionPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */