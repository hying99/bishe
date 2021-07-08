package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import weka.classifiers.Classifier;
import weka.core.xml.KOML;
import weka.experiment.CSVResultListener;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.InstancesResultListener;
import weka.experiment.PropertyNode;
import weka.experiment.RandomSplitResultProducer;
import weka.experiment.RegressionSplitEvaluator;
import weka.experiment.ResultListener;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;
import weka.experiment.xml.XMLExperiment;
import weka.gui.DatabaseConnectionDialog;
import weka.gui.ExtensionFileFilter;

public class SimpleSetupPanel extends JPanel {
  protected Experiment m_Exp;
  
  protected SetupModePanel m_modePanel = null;
  
  protected String m_destinationDatabaseURL;
  
  protected String m_destinationFilename = "";
  
  protected int m_numFolds = 10;
  
  protected double m_trainPercent = 66.0D;
  
  protected int m_numRepetitions = 10;
  
  protected boolean m_userHasBeenAskedAboutConversion;
  
  protected ExtensionFileFilter m_csvFileFilter = new ExtensionFileFilter(".csv", "Comma separated value files");
  
  protected ExtensionFileFilter m_arffFileFilter = new ExtensionFileFilter(".arff", "ARFF files");
  
  protected JButton m_OpenBut = new JButton("Open...");
  
  protected JButton m_SaveBut = new JButton("Save...");
  
  protected JButton m_NewBut = new JButton("New");
  
  protected FileFilter m_ExpFilter = (FileFilter)new ExtensionFileFilter(Experiment.FILE_EXTENSION, "Experiment configuration files (*" + Experiment.FILE_EXTENSION + ")");
  
  protected FileFilter m_KOMLFilter = (FileFilter)new ExtensionFileFilter(".koml", "Experiment configuration files (*.koml)");
  
  protected FileFilter m_XMLFilter = (FileFilter)new ExtensionFileFilter(".xml", "Experiment configuration files (*.xml)");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected JFileChooser m_DestFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected JComboBox m_ResultsDestinationCBox = new JComboBox();
  
  protected JLabel m_ResultsDestinationPathLabel = new JLabel("Filename:");
  
  protected JTextField m_ResultsDestinationPathTField = new JTextField();
  
  protected JButton m_BrowseDestinationButton = new JButton("Browse...");
  
  protected JComboBox m_ExperimentTypeCBox = new JComboBox();
  
  protected JLabel m_ExperimentParameterLabel = new JLabel("Number of folds:");
  
  protected JTextField m_ExperimentParameterTField = new JTextField();
  
  protected JRadioButton m_ExpClassificationRBut = new JRadioButton("Classification");
  
  protected JRadioButton m_ExpRegressionRBut = new JRadioButton("Regression");
  
  protected JTextField m_NumberOfRepetitionsTField = new JTextField();
  
  protected JRadioButton m_OrderDatasetsFirstRBut = new JRadioButton("Data sets first");
  
  protected JRadioButton m_OrderAlgorithmsFirstRBut = new JRadioButton("Algorithms first");
  
  protected static String DEST_DATABASE_TEXT = "JDBC database";
  
  protected static String DEST_ARFF_TEXT = "ARFF file";
  
  protected static String DEST_CSV_TEXT = "CSV file";
  
  protected static String TYPE_CROSSVALIDATION_TEXT = "Cross-validation";
  
  protected static String TYPE_RANDOMSPLIT_TEXT = "Train/Test Percentage Split (data randomized)";
  
  protected static String TYPE_FIXEDSPLIT_TEXT = "Train/Test Percentage Split (order preserved)";
  
  protected DatasetListPanel m_DatasetListPanel = new DatasetListPanel();
  
  protected AlgorithmListPanel m_AlgorithmListPanel = new AlgorithmListPanel();
  
  protected JButton m_NotesButton = new JButton("Notes");
  
  protected JFrame m_NotesFrame = new JFrame("Notes");
  
  protected JTextArea m_NotesText = new JTextArea(null, 10, 0);
  
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
  
  public SimpleSetupPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public SimpleSetupPanel() {
    this.m_ResultsDestinationCBox.setEnabled(false);
    this.m_ResultsDestinationPathLabel.setEnabled(false);
    this.m_ResultsDestinationPathTField.setEnabled(false);
    this.m_BrowseDestinationButton.setEnabled(false);
    this.m_ExperimentTypeCBox.setEnabled(false);
    this.m_ExperimentParameterLabel.setEnabled(false);
    this.m_ExperimentParameterTField.setEnabled(false);
    this.m_ExpClassificationRBut.setEnabled(false);
    this.m_ExpRegressionRBut.setEnabled(false);
    this.m_NumberOfRepetitionsTField.setEnabled(false);
    this.m_OrderDatasetsFirstRBut.setEnabled(false);
    this.m_OrderAlgorithmsFirstRBut.setEnabled(false);
    try {
      this.m_destinationDatabaseURL = (new DatabaseResultListener()).getDatabaseURL();
    } catch (Exception exception) {}
    this.m_NewBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            Experiment experiment = new Experiment();
            CrossValidationResultProducer crossValidationResultProducer = new CrossValidationResultProducer();
            crossValidationResultProducer.setNumFolds(10);
            crossValidationResultProducer.setSplitEvaluator((SplitEvaluator)new ClassifierSplitEvaluator());
            experiment.setResultProducer((ResultProducer)crossValidationResultProducer);
            experiment.setPropertyArray(new Classifier[0]);
            experiment.setUsePropertyIterator(true);
            this.this$0.setExperiment(experiment);
          }
        });
    this.m_SaveBut.setEnabled(false);
    this.m_SaveBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveExperiment();
          }
        });
    this.m_OpenBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.openExperiment();
          }
        });
    this.m_FileChooser.addChoosableFileFilter(this.m_ExpFilter);
    if (KOML.isPresent())
      this.m_FileChooser.addChoosableFileFilter(this.m_KOMLFilter); 
    this.m_FileChooser.addChoosableFileFilter(this.m_XMLFilter);
    this.m_FileChooser.setFileFilter(this.m_ExpFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_DestFileChooser.setFileSelectionMode(0);
    this.m_BrowseDestinationButton.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_ResultsDestinationCBox.getSelectedItem() == SimpleSetupPanel.DEST_DATABASE_TEXT) {
              this.this$0.chooseURLUsername();
            } else {
              this.this$0.chooseDestinationFile();
            } 
          }
        });
    this.m_ExpClassificationRBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.expTypeChanged();
          }
        });
    this.m_ExpRegressionRBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.expTypeChanged();
          }
        });
    this.m_OrderDatasetsFirstRBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_Exp != null) {
              this.this$0.m_Exp.setAdvanceDataSetFirst(true);
              this.this$0.m_Support.firePropertyChange("", (Object)null, (Object)null);
            } 
          }
        });
    this.m_OrderAlgorithmsFirstRBut.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_Exp != null) {
              this.this$0.m_Exp.setAdvanceDataSetFirst(false);
              this.this$0.m_Support.firePropertyChange("", (Object)null, (Object)null);
            } 
          }
        });
    this.m_ResultsDestinationPathTField.getDocument().addDocumentListener(new DocumentListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void insertUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.destinationAddressChanged();
          }
          
          public void removeUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.destinationAddressChanged();
          }
          
          public void changedUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.destinationAddressChanged();
          }
        });
    this.m_ExperimentParameterTField.getDocument().addDocumentListener(new DocumentListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void insertUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.expParamChanged();
          }
          
          public void removeUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.expParamChanged();
          }
          
          public void changedUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.expParamChanged();
          }
        });
    this.m_NumberOfRepetitionsTField.getDocument().addDocumentListener(new DocumentListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void insertUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.numRepetitionsChanged();
          }
          
          public void removeUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.numRepetitionsChanged();
          }
          
          public void changedUpdate(DocumentEvent param1DocumentEvent) {
            this.this$0.numRepetitionsChanged();
          }
        });
    this.m_NotesFrame.addWindowListener(new WindowAdapter(this) {
          private final SimpleSetupPanel this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.this$0.m_NotesButton.setEnabled(true);
          }
        });
    this.m_NotesFrame.getContentPane().add(new JScrollPane(this.m_NotesText));
    this.m_NotesFrame.setSize(600, 400);
    this.m_NotesButton.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_NotesButton.setEnabled(false);
            this.this$0.m_NotesFrame.setVisible(true);
          }
        });
    this.m_NotesButton.setEnabled(false);
    this.m_NotesText.setEditable(true);
    this.m_NotesText.addKeyListener(new KeyAdapter(this) {
          private final SimpleSetupPanel this$0;
          
          public void keyReleased(KeyEvent param1KeyEvent) {
            this.this$0.m_Exp.setNotes(this.this$0.m_NotesText.getText());
          }
        });
    this.m_NotesText.addFocusListener(new FocusAdapter(this) {
          private final SimpleSetupPanel this$0;
          
          public void focusLost(FocusEvent param1FocusEvent) {
            this.this$0.m_Exp.setNotes(this.this$0.m_NotesText.getText());
          }
        });
    JPanel jPanel1 = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel1.setLayout(gridBagLayout);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel1.add(this.m_OpenBut, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_SaveBut, gridBagConstraints);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_NewBut, gridBagConstraints);
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BorderLayout(5, 5));
    jPanel2.add(this.m_ResultsDestinationPathLabel, "West");
    jPanel2.add(this.m_ResultsDestinationPathTField, "Center");
    this.m_ResultsDestinationCBox.addItem(DEST_ARFF_TEXT);
    this.m_ResultsDestinationCBox.addItem(DEST_CSV_TEXT);
    this.m_ResultsDestinationCBox.addItem(DEST_DATABASE_TEXT);
    this.m_ResultsDestinationCBox.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.destinationTypeChanged();
          }
        });
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BorderLayout(5, 5));
    jPanel3.add(this.m_ResultsDestinationCBox, "West");
    jPanel3.add(jPanel2, "Center");
    jPanel3.add(this.m_BrowseDestinationButton, "East");
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new BorderLayout());
    jPanel4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Results Destination"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel4.add(jPanel3, "North");
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new BorderLayout(5, 5));
    jPanel5.add(this.m_ExperimentParameterLabel, "West");
    jPanel5.add(this.m_ExperimentParameterTField, "Center");
    ButtonGroup buttonGroup1 = new ButtonGroup();
    buttonGroup1.add(this.m_ExpClassificationRBut);
    buttonGroup1.add(this.m_ExpRegressionRBut);
    this.m_ExpClassificationRBut.setSelected(true);
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new GridLayout(1, 0));
    jPanel6.add(this.m_ExpClassificationRBut);
    jPanel6.add(this.m_ExpRegressionRBut);
    this.m_ExperimentTypeCBox.addItem(TYPE_CROSSVALIDATION_TEXT);
    this.m_ExperimentTypeCBox.addItem(TYPE_RANDOMSPLIT_TEXT);
    this.m_ExperimentTypeCBox.addItem(TYPE_FIXEDSPLIT_TEXT);
    this.m_ExperimentTypeCBox.addActionListener(new ActionListener(this) {
          private final SimpleSetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.expTypeChanged();
          }
        });
    JPanel jPanel7 = new JPanel();
    jPanel7.setLayout(new GridLayout(0, 1));
    jPanel7.add(this.m_ExperimentTypeCBox);
    jPanel7.add(jPanel5);
    jPanel7.add(jPanel6);
    JPanel jPanel8 = new JPanel();
    jPanel8.setLayout(new BorderLayout());
    jPanel8.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Experiment Type"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel8.add(jPanel7, "North");
    ButtonGroup buttonGroup2 = new ButtonGroup();
    buttonGroup2.add(this.m_OrderDatasetsFirstRBut);
    buttonGroup2.add(this.m_OrderAlgorithmsFirstRBut);
    this.m_OrderDatasetsFirstRBut.setSelected(true);
    JPanel jPanel9 = new JPanel();
    jPanel9.setLayout(new BorderLayout(5, 5));
    jPanel9.add(new JLabel("Number of repetitions:"), "West");
    jPanel9.add(this.m_NumberOfRepetitionsTField, "Center");
    JPanel jPanel10 = new JPanel();
    jPanel10.setLayout(new GridLayout(0, 1));
    jPanel10.add(jPanel9);
    jPanel10.add(this.m_OrderDatasetsFirstRBut);
    jPanel10.add(this.m_OrderAlgorithmsFirstRBut);
    JPanel jPanel11 = new JPanel();
    jPanel11.setLayout(new BorderLayout());
    jPanel11.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Iteration Control"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel11.add(jPanel10, "North");
    JPanel jPanel12 = new JPanel();
    jPanel12.setLayout(new GridLayout(1, 0));
    jPanel12.add(jPanel8);
    jPanel12.add(jPanel11);
    JPanel jPanel13 = new JPanel();
    jPanel13.setLayout(new BorderLayout());
    jPanel13.add(this.m_NotesButton, "Center");
    JPanel jPanel14 = new JPanel();
    jPanel14.setLayout(new BorderLayout());
    jPanel14.add(jPanel4, "North");
    jPanel14.add(jPanel12, "Center");
    JPanel jPanel15 = new JPanel();
    jPanel15.setLayout(new BorderLayout());
    jPanel15.add(jPanel1, "North");
    jPanel15.add(jPanel14, "Center");
    JPanel jPanel16 = new JPanel();
    jPanel16.setLayout(new BorderLayout());
    jPanel16.add(this.m_DatasetListPanel, "Center");
    JPanel jPanel17 = new JPanel();
    jPanel17.setLayout(new BorderLayout());
    jPanel17.add(this.m_AlgorithmListPanel, "Center");
    JPanel jPanel18 = new JPanel();
    jPanel18.setLayout(new GridLayout(1, 0));
    jPanel18.add(jPanel16);
    jPanel18.add(jPanel17);
    setLayout(new BorderLayout());
    add(jPanel15, "North");
    add(jPanel18, "Center");
    add(jPanel13, "South");
  }
  
  protected void removeNotesFrame() {
    this.m_NotesFrame.setVisible(false);
  }
  
  private boolean userWantsToConvert() {
    if (this.m_userHasBeenAskedAboutConversion)
      return true; 
    this.m_userHasBeenAskedAboutConversion = true;
    return (JOptionPane.showConfirmDialog(this, "This experiment has settings that are too advanced\nto be represented in the simple setup mode.\nDo you want the experiment to be converted,\nlosing some of the advanced settings?\n", "Confirm conversion", 0, 2) == 0);
  }
  
  public void setModePanel(SetupModePanel paramSetupModePanel) {
    this.m_modePanel = paramSetupModePanel;
  }
  
  public boolean setExperiment(Experiment paramExperiment) {
    this.m_userHasBeenAskedAboutConversion = false;
    this.m_Exp = null;
    this.m_SaveBut.setEnabled(true);
    if (paramExperiment.getResultListener() instanceof DatabaseResultListener) {
      this.m_ResultsDestinationCBox.setSelectedItem(DEST_DATABASE_TEXT);
      this.m_ResultsDestinationPathLabel.setText("URL:");
      this.m_destinationDatabaseURL = ((DatabaseResultListener)paramExperiment.getResultListener()).getDatabaseURL();
      this.m_ResultsDestinationPathTField.setText(this.m_destinationDatabaseURL);
      this.m_BrowseDestinationButton.setEnabled(true);
    } else if (paramExperiment.getResultListener() instanceof InstancesResultListener) {
      this.m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
      this.m_ResultsDestinationPathLabel.setText("Filename:");
      this.m_destinationFilename = ((InstancesResultListener)paramExperiment.getResultListener()).outputFileName();
      this.m_ResultsDestinationPathTField.setText(this.m_destinationFilename);
      this.m_BrowseDestinationButton.setEnabled(true);
    } else if (paramExperiment.getResultListener() instanceof CSVResultListener) {
      this.m_ResultsDestinationCBox.setSelectedItem(DEST_CSV_TEXT);
      this.m_ResultsDestinationPathLabel.setText("Filename:");
      this.m_destinationFilename = ((CSVResultListener)paramExperiment.getResultListener()).outputFileName();
      this.m_ResultsDestinationPathTField.setText(this.m_destinationFilename);
      this.m_BrowseDestinationButton.setEnabled(true);
    } else {
      System.out.println("SimpleSetup incompatibility: unrecognised result destination");
      if (userWantsToConvert()) {
        this.m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
        this.m_ResultsDestinationPathLabel.setText("Filename:");
        this.m_destinationFilename = "";
        this.m_ResultsDestinationPathTField.setText(this.m_destinationFilename);
        this.m_BrowseDestinationButton.setEnabled(true);
      } else {
        return false;
      } 
    } 
    this.m_ResultsDestinationCBox.setEnabled(true);
    this.m_ResultsDestinationPathLabel.setEnabled(true);
    this.m_ResultsDestinationPathTField.setEnabled(true);
    if (paramExperiment.getResultProducer() instanceof CrossValidationResultProducer) {
      CrossValidationResultProducer crossValidationResultProducer = (CrossValidationResultProducer)paramExperiment.getResultProducer();
      this.m_numFolds = crossValidationResultProducer.getNumFolds();
      this.m_ExperimentParameterTField.setText("" + this.m_numFolds);
      if (crossValidationResultProducer.getSplitEvaluator() instanceof ClassifierSplitEvaluator) {
        this.m_ExpClassificationRBut.setSelected(true);
        this.m_ExpRegressionRBut.setSelected(false);
      } else if (crossValidationResultProducer.getSplitEvaluator() instanceof RegressionSplitEvaluator) {
        this.m_ExpClassificationRBut.setSelected(false);
        this.m_ExpRegressionRBut.setSelected(true);
      } else {
        System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
        if (userWantsToConvert()) {
          this.m_ExpClassificationRBut.setSelected(true);
          this.m_ExpRegressionRBut.setSelected(false);
        } else {
          return false;
        } 
      } 
      this.m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
    } else if (paramExperiment.getResultProducer() instanceof RandomSplitResultProducer) {
      RandomSplitResultProducer randomSplitResultProducer = (RandomSplitResultProducer)paramExperiment.getResultProducer();
      if (randomSplitResultProducer.getRandomizeData()) {
        this.m_ExperimentTypeCBox.setSelectedItem(TYPE_RANDOMSPLIT_TEXT);
      } else {
        this.m_ExperimentTypeCBox.setSelectedItem(TYPE_FIXEDSPLIT_TEXT);
      } 
      if (randomSplitResultProducer.getSplitEvaluator() instanceof ClassifierSplitEvaluator) {
        this.m_ExpClassificationRBut.setSelected(true);
        this.m_ExpRegressionRBut.setSelected(false);
      } else if (randomSplitResultProducer.getSplitEvaluator() instanceof RegressionSplitEvaluator) {
        this.m_ExpClassificationRBut.setSelected(false);
        this.m_ExpRegressionRBut.setSelected(true);
      } else {
        System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
        if (userWantsToConvert()) {
          this.m_ExpClassificationRBut.setSelected(true);
          this.m_ExpRegressionRBut.setSelected(false);
        } else {
          return false;
        } 
      } 
      this.m_trainPercent = randomSplitResultProducer.getTrainPercent();
      this.m_ExperimentParameterTField.setText("" + this.m_trainPercent);
    } else {
      System.out.println("SimpleSetup incompatibility: unrecognised resultProducer");
      if (userWantsToConvert()) {
        this.m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
        this.m_ExpClassificationRBut.setSelected(true);
        this.m_ExpRegressionRBut.setSelected(false);
      } else {
        return false;
      } 
    } 
    this.m_ExperimentTypeCBox.setEnabled(true);
    this.m_ExperimentParameterLabel.setEnabled(true);
    this.m_ExperimentParameterTField.setEnabled(true);
    this.m_ExpClassificationRBut.setEnabled(true);
    this.m_ExpRegressionRBut.setEnabled(true);
    if (paramExperiment.getRunLower() == 1) {
      this.m_numRepetitions = paramExperiment.getRunUpper();
      this.m_NumberOfRepetitionsTField.setText("" + this.m_numRepetitions);
    } else {
      System.out.println("SimpleSetup incompatibility: runLower is not 1");
      if (userWantsToConvert()) {
        paramExperiment.setRunLower(1);
        if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_FIXEDSPLIT_TEXT) {
          paramExperiment.setRunUpper(1);
          this.m_NumberOfRepetitionsTField.setEnabled(false);
          this.m_NumberOfRepetitionsTField.setText("1");
        } else {
          paramExperiment.setRunUpper(10);
          this.m_numRepetitions = 10;
          this.m_NumberOfRepetitionsTField.setText("" + this.m_numRepetitions);
        } 
      } else {
        return false;
      } 
    } 
    this.m_NumberOfRepetitionsTField.setEnabled(true);
    this.m_OrderDatasetsFirstRBut.setSelected(paramExperiment.getAdvanceDataSetFirst());
    this.m_OrderAlgorithmsFirstRBut.setSelected(!paramExperiment.getAdvanceDataSetFirst());
    this.m_OrderDatasetsFirstRBut.setEnabled(true);
    this.m_OrderAlgorithmsFirstRBut.setEnabled(true);
    this.m_NotesText.setText(paramExperiment.getNotes());
    this.m_NotesButton.setEnabled(true);
    if (!paramExperiment.getUsePropertyIterator() || !(paramExperiment.getPropertyArray() instanceof Classifier[])) {
      System.out.println("SimpleSetup incompatibility: unrecognised property iteration");
      if (userWantsToConvert()) {
        paramExperiment.setPropertyArray(new Classifier[0]);
        paramExperiment.setUsePropertyIterator(true);
      } else {
        return false;
      } 
    } 
    this.m_DatasetListPanel.setExperiment(paramExperiment);
    this.m_AlgorithmListPanel.setExperiment(paramExperiment);
    this.m_Exp = paramExperiment;
    expTypeChanged();
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
    return true;
  }
  
  public Experiment getExperiment() {
    return this.m_Exp;
  }
  
  private void openExperiment() {
    int i = this.m_FileChooser.showOpenDialog(this);
    if (i != 0)
      return; 
    File file = this.m_FileChooser.getSelectedFile();
    if (this.m_FileChooser.getFileFilter() == this.m_ExpFilter) {
      if (!file.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION))
        file = new File(file.getParent(), file.getName() + Experiment.FILE_EXTENSION); 
    } else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter) {
      if (!file.getName().toLowerCase().endsWith(".koml"))
        file = new File(file.getParent(), file.getName() + ".koml"); 
    } else if (this.m_FileChooser.getFileFilter() == this.m_XMLFilter && !file.getName().toLowerCase().endsWith(".xml")) {
      file = new File(file.getParent(), file.getName() + ".xml");
    } 
    try {
      Experiment experiment;
      if (KOML.isPresent() && file.getAbsolutePath().toLowerCase().endsWith(".koml")) {
        experiment = (Experiment)KOML.read(file.getAbsolutePath());
      } else if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
        XMLExperiment xMLExperiment = new XMLExperiment();
        experiment = (Experiment)xMLExperiment.read(file);
      } else {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        experiment = (Experiment)objectInputStream.readObject();
        objectInputStream.close();
      } 
      if (!setExperiment(experiment) && this.m_modePanel != null)
        this.m_modePanel.switchToAdvanced(experiment); 
      System.err.println("Opened experiment:\n" + experiment);
    } catch (Exception exception) {
      exception.printStackTrace();
      JOptionPane.showMessageDialog(this, "Couldn't open experiment file:\n" + file + "\nReason:\n" + exception.getMessage(), "Open Experiment", 0);
    } 
  }
  
  private void saveExperiment() {
    int i = this.m_FileChooser.showSaveDialog(this);
    if (i != 0)
      return; 
    File file = this.m_FileChooser.getSelectedFile();
    if (this.m_FileChooser.getFileFilter() == this.m_ExpFilter) {
      if (!file.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION))
        file = new File(file.getParent(), file.getName() + Experiment.FILE_EXTENSION); 
    } else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter) {
      if (!file.getName().toLowerCase().endsWith(".koml"))
        file = new File(file.getParent(), file.getName() + ".koml"); 
    } else if (this.m_FileChooser.getFileFilter() == this.m_XMLFilter && !file.getName().toLowerCase().endsWith(".xml")) {
      file = new File(file.getParent(), file.getName() + ".xml");
    } 
    try {
      if (KOML.isPresent() && file.getAbsolutePath().toLowerCase().endsWith(".koml")) {
        KOML.write(file.getAbsolutePath(), this.m_Exp);
      } else if (file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
        XMLExperiment xMLExperiment = new XMLExperiment();
        xMLExperiment.write(file, this.m_Exp);
      } else {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
        objectOutputStream.writeObject(this.m_Exp);
        objectOutputStream.close();
      } 
      System.err.println("Saved experiment:\n" + this.m_Exp);
    } catch (Exception exception) {
      exception.printStackTrace();
      JOptionPane.showMessageDialog(this, "Couldn't save experiment file:\n" + file + "\nReason:\n" + exception.getMessage(), "Save Experiment", 0);
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  private void destinationTypeChanged() {
    if (this.m_Exp == null)
      return; 
    String str = "";
    if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT) {
      this.m_ResultsDestinationPathLabel.setText("URL:");
      str = this.m_destinationDatabaseURL;
      this.m_BrowseDestinationButton.setEnabled(true);
      this.m_BrowseDestinationButton.setText("User...");
    } else {
      this.m_ResultsDestinationPathLabel.setText("Filename:");
      if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
        int i = this.m_destinationFilename.lastIndexOf(".csv");
        if (i > -1)
          this.m_destinationFilename = this.m_destinationFilename.substring(0, i) + ".arff"; 
      } 
      if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
        int i = this.m_destinationFilename.lastIndexOf(".arff");
        if (i > -1)
          this.m_destinationFilename = this.m_destinationFilename.substring(0, i) + ".csv"; 
      } 
      str = this.m_destinationFilename;
      if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
        int i = str.lastIndexOf(".csv");
        if (i > -1)
          str = str.substring(0, i) + ".arff"; 
      } 
      if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
        int i = str.lastIndexOf(".arff");
        if (i > -1)
          str = str.substring(0, i) + ".csv"; 
      } 
      this.m_BrowseDestinationButton.setEnabled(true);
      this.m_BrowseDestinationButton.setText("Browse...");
    } 
    if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT) {
      DatabaseResultListener databaseResultListener = null;
      try {
        databaseResultListener = new DatabaseResultListener();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      databaseResultListener.setDatabaseURL(this.m_destinationDatabaseURL);
      this.m_Exp.setResultListener((ResultListener)databaseResultListener);
    } else if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
      InstancesResultListener instancesResultListener = new InstancesResultListener();
      if (!this.m_destinationFilename.equals(""))
        instancesResultListener.setOutputFile(new File(this.m_destinationFilename)); 
      this.m_Exp.setResultListener((ResultListener)instancesResultListener);
    } else if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
      CSVResultListener cSVResultListener = new CSVResultListener();
      if (!this.m_destinationFilename.equals(""))
        cSVResultListener.setOutputFile(new File(this.m_destinationFilename)); 
      this.m_Exp.setResultListener((ResultListener)cSVResultListener);
    } 
    this.m_ResultsDestinationPathTField.setText(str);
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  private void destinationAddressChanged() {
    if (this.m_Exp == null)
      return; 
    if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT) {
      this.m_destinationDatabaseURL = this.m_ResultsDestinationPathTField.getText();
      if (this.m_Exp.getResultListener() instanceof DatabaseResultListener)
        ((DatabaseResultListener)this.m_Exp.getResultListener()).setDatabaseURL(this.m_destinationDatabaseURL); 
    } else {
      File file = null;
      this.m_destinationFilename = this.m_ResultsDestinationPathTField.getText();
      if (this.m_destinationFilename.equals("")) {
        try {
          if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT)
            file = File.createTempFile("weka_experiment", ".arff"); 
          if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT)
            file = File.createTempFile("weka_experiment", ".csv"); 
          file.deleteOnExit();
        } catch (Exception exception) {
          System.err.println("Cannot create temp file, writing to standard out.");
          file = new File("-");
        } 
      } else {
        if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT && !this.m_destinationFilename.endsWith(".arff"))
          this.m_destinationFilename += ".arff"; 
        if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT && !this.m_destinationFilename.endsWith(".csv"))
          this.m_destinationFilename += ".csv"; 
        file = new File(this.m_destinationFilename);
      } 
      ((CSVResultListener)this.m_Exp.getResultListener()).setOutputFile(file);
      ((CSVResultListener)this.m_Exp.getResultListener()).setOutputFileName(this.m_destinationFilename);
    } 
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  private void expTypeChanged() {
    RegressionSplitEvaluator regressionSplitEvaluator;
    if (this.m_Exp == null)
      return; 
    if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
      this.m_ExperimentParameterLabel.setText("Number of folds:");
      this.m_ExperimentParameterTField.setText("" + this.m_numFolds);
    } else {
      this.m_ExperimentParameterLabel.setText("Train percentage:");
      this.m_ExperimentParameterTField.setText("" + this.m_trainPercent);
    } 
    if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_FIXEDSPLIT_TEXT) {
      this.m_NumberOfRepetitionsTField.setEnabled(false);
      this.m_NumberOfRepetitionsTField.setText("1");
      this.m_Exp.setRunLower(1);
      this.m_Exp.setRunUpper(1);
    } else {
      this.m_NumberOfRepetitionsTField.setText("" + this.m_numRepetitions);
      this.m_NumberOfRepetitionsTField.setEnabled(true);
      this.m_Exp.setRunLower(1);
      this.m_Exp.setRunUpper(this.m_numRepetitions);
    } 
    ClassifierSplitEvaluator classifierSplitEvaluator = null;
    Classifier classifier = null;
    if (this.m_ExpClassificationRBut.isSelected()) {
      classifierSplitEvaluator = new ClassifierSplitEvaluator();
      classifier = classifierSplitEvaluator.getClassifier();
    } else {
      regressionSplitEvaluator = new RegressionSplitEvaluator();
      classifier = regressionSplitEvaluator.getClassifier();
    } 
    if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
      CrossValidationResultProducer crossValidationResultProducer = new CrossValidationResultProducer();
      crossValidationResultProducer.setNumFolds(this.m_numFolds);
      crossValidationResultProducer.setSplitEvaluator((SplitEvaluator)regressionSplitEvaluator);
      PropertyNode[] arrayOfPropertyNode = new PropertyNode[2];
      try {
        arrayOfPropertyNode[0] = new PropertyNode(regressionSplitEvaluator, new PropertyDescriptor("splitEvaluator", CrossValidationResultProducer.class), CrossValidationResultProducer.class);
        arrayOfPropertyNode[1] = new PropertyNode(classifier, new PropertyDescriptor("classifier", regressionSplitEvaluator.getClass()), regressionSplitEvaluator.getClass());
      } catch (IntrospectionException introspectionException) {
        introspectionException.printStackTrace();
      } 
      this.m_Exp.setResultProducer((ResultProducer)crossValidationResultProducer);
      this.m_Exp.setPropertyPath(arrayOfPropertyNode);
    } else {
      RandomSplitResultProducer randomSplitResultProducer = new RandomSplitResultProducer();
      randomSplitResultProducer.setRandomizeData((this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_RANDOMSPLIT_TEXT));
      randomSplitResultProducer.setTrainPercent(this.m_trainPercent);
      randomSplitResultProducer.setSplitEvaluator((SplitEvaluator)regressionSplitEvaluator);
      PropertyNode[] arrayOfPropertyNode = new PropertyNode[2];
      try {
        arrayOfPropertyNode[0] = new PropertyNode(regressionSplitEvaluator, new PropertyDescriptor("splitEvaluator", RandomSplitResultProducer.class), RandomSplitResultProducer.class);
        arrayOfPropertyNode[1] = new PropertyNode(classifier, new PropertyDescriptor("classifier", regressionSplitEvaluator.getClass()), regressionSplitEvaluator.getClass());
      } catch (IntrospectionException introspectionException) {
        introspectionException.printStackTrace();
      } 
      this.m_Exp.setResultProducer((ResultProducer)randomSplitResultProducer);
      this.m_Exp.setPropertyPath(arrayOfPropertyNode);
    } 
    this.m_Exp.setUsePropertyIterator(true);
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  private void expParamChanged() {
    if (this.m_Exp == null)
      return; 
    if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
      try {
        this.m_numFolds = Integer.parseInt(this.m_ExperimentParameterTField.getText());
      } catch (NumberFormatException numberFormatException) {
        return;
      } 
    } else {
      try {
        this.m_trainPercent = Double.parseDouble(this.m_ExperimentParameterTField.getText());
      } catch (NumberFormatException numberFormatException) {
        return;
      } 
    } 
    if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
      if (this.m_Exp.getResultProducer() instanceof CrossValidationResultProducer) {
        CrossValidationResultProducer crossValidationResultProducer = (CrossValidationResultProducer)this.m_Exp.getResultProducer();
        crossValidationResultProducer.setNumFolds(this.m_numFolds);
      } else {
        return;
      } 
    } else if (this.m_Exp.getResultProducer() instanceof RandomSplitResultProducer) {
      RandomSplitResultProducer randomSplitResultProducer = (RandomSplitResultProducer)this.m_Exp.getResultProducer();
      randomSplitResultProducer.setRandomizeData((this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_RANDOMSPLIT_TEXT));
      randomSplitResultProducer.setTrainPercent(this.m_trainPercent);
    } else {
      return;
    } 
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  private void numRepetitionsChanged() {
    if (this.m_Exp == null || !this.m_NumberOfRepetitionsTField.isEnabled())
      return; 
    try {
      this.m_numRepetitions = Integer.parseInt(this.m_NumberOfRepetitionsTField.getText());
    } catch (NumberFormatException numberFormatException) {
      return;
    } 
    this.m_Exp.setRunLower(1);
    this.m_Exp.setRunUpper(this.m_numRepetitions);
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  private void chooseURLUsername() {
    String str1 = ((DatabaseResultListener)this.m_Exp.getResultListener()).getDatabaseURL();
    String str2 = ((DatabaseResultListener)this.m_Exp.getResultListener()).getUsername();
    DatabaseConnectionDialog databaseConnectionDialog = new DatabaseConnectionDialog(null, str1, str2);
    databaseConnectionDialog.setVisible(true);
    if (databaseConnectionDialog.getReturnValue() == -1)
      return; 
    ((DatabaseResultListener)this.m_Exp.getResultListener()).setUsername(databaseConnectionDialog.getUsername());
    ((DatabaseResultListener)this.m_Exp.getResultListener()).setPassword(databaseConnectionDialog.getPassword());
    ((DatabaseResultListener)this.m_Exp.getResultListener()).setDatabaseURL(databaseConnectionDialog.getURL());
  }
  
  private void chooseDestinationFile() {
    ExtensionFileFilter extensionFileFilter = null;
    if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
      extensionFileFilter = this.m_csvFileFilter;
    } else {
      extensionFileFilter = this.m_arffFileFilter;
    } 
    this.m_DestFileChooser.setFileFilter((FileFilter)extensionFileFilter);
    int i = this.m_DestFileChooser.showSaveDialog(this);
    if (i != 0)
      return; 
    this.m_ResultsDestinationPathTField.setText(this.m_DestFileChooser.getSelectedFile().toString());
  }
  
  public static void main(String[] paramArrayOfString) {}
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\SimpleSetupPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */