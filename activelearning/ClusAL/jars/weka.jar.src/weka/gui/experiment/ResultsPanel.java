package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.CSVLoader;
import weka.experiment.CSVResultListener;
import weka.experiment.DatabaseResultListener;
import weka.experiment.Experiment;
import weka.experiment.InstanceQuery;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.PairedTTester;
import weka.gui.DatabaseConnectionDialog;
import weka.gui.ExtensionFileFilter;
import weka.gui.ListSelectorDialog;
import weka.gui.ResultHistoryPanel;
import weka.gui.SaveBuffer;

public class ResultsPanel extends JPanel {
  protected static final String NO_SOURCE = "No source";
  
  protected JButton m_FromFileBut = new JButton("File...");
  
  protected JButton m_FromDBaseBut = new JButton("Database...");
  
  protected JButton m_FromExpBut = new JButton("Experiment");
  
  protected JLabel m_FromLab = new JLabel("No source");
  
  private static String[] FOR_JFC_1_1_DCBM_BUG = new String[] { "" };
  
  protected DefaultComboBoxModel m_DatasetModel = new DefaultComboBoxModel(FOR_JFC_1_1_DCBM_BUG);
  
  protected DefaultComboBoxModel m_CompareModel = new DefaultComboBoxModel(FOR_JFC_1_1_DCBM_BUG);
  
  protected DefaultListModel m_TestsModel = new DefaultListModel();
  
  protected DefaultListModel m_DisplayedModel = new DefaultListModel();
  
  protected JLabel m_DatasetKeyLabel = new JLabel("Row", 4);
  
  protected JButton m_DatasetKeyBut = new JButton("Select");
  
  protected DefaultListModel m_DatasetKeyModel = new DefaultListModel();
  
  protected JList m_DatasetKeyList = new JList(this.m_DatasetKeyModel);
  
  protected JLabel m_ResultKeyLabel = new JLabel("Column", 4);
  
  protected JButton m_ResultKeyBut = new JButton("Select");
  
  protected DefaultListModel m_ResultKeyModel = new DefaultListModel();
  
  protected JList m_ResultKeyList = new JList(this.m_ResultKeyModel);
  
  protected JButton m_TestsButton = new JButton("Select base...");
  
  protected JButton m_DisplayedButton = new JButton("Select");
  
  protected JList m_TestsList = new JList(this.m_TestsModel);
  
  protected JList m_DisplayedList = new JList(this.m_DisplayedModel);
  
  protected JComboBox m_CompareCombo = new JComboBox(this.m_CompareModel);
  
  protected JTextField m_SigTex = new JTextField("0.05");
  
  protected JCheckBox m_ShowStdDevs = new JCheckBox("");
  
  protected JButton m_OutputFormatButton = new JButton("Select");
  
  protected JButton m_PerformBut = new JButton("Perform test");
  
  protected JButton m_SaveOutBut = new JButton("Save output");
  
  SaveBuffer m_SaveOut = new SaveBuffer(null, this);
  
  protected JTextArea m_OutText = new JTextArea();
  
  protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected ExtensionFileFilter m_csvFileFilter = new ExtensionFileFilter(CSVLoader.FILE_EXTENSION, "CSV data files");
  
  protected ExtensionFileFilter m_arffFileFilter = new ExtensionFileFilter(Instances.FILE_EXTENSION, "Arff data files");
  
  protected PairedTTester m_TTester = (PairedTTester)new PairedCorrectedTTester();
  
  protected Instances m_Instances;
  
  protected InstanceQuery m_InstanceQuery;
  
  protected Thread m_LoadThread;
  
  protected Experiment m_Exp;
  
  private Dimension COMBO_SIZE = new Dimension(150, (this.m_ResultKeyBut.getPreferredSize()).height);
  
  protected boolean m_latexOutput = false;
  
  protected boolean m_csvOutput = false;
  
  protected int m_MeanPrec = 2;
  
  protected int m_StdDevPrec = 2;
  
  public ResultsPanel() {
    this.m_FileChooser.addChoosableFileFilter((FileFilter)this.m_csvFileFilter);
    this.m_FileChooser.addChoosableFileFilter((FileFilter)this.m_arffFileFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_FromExpBut.setEnabled(false);
    this.m_FromExpBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_LoadThread == null) {
              this.this$0.m_LoadThread = (Thread)new Object(this);
              this.this$0.m_LoadThread.start();
            } 
          }
        });
    this.m_FromDBaseBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_LoadThread == null) {
              this.this$0.m_LoadThread = (Thread)new Object(this);
              this.this$0.m_LoadThread.start();
            } 
          }
        });
    this.m_FromFileBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            int i = this.this$0.m_FileChooser.showOpenDialog(this.this$0);
            if (i == 0) {
              File file = this.this$0.m_FileChooser.getSelectedFile();
              if (this.this$0.m_LoadThread == null) {
                this.this$0.m_LoadThread = (Thread)new Object(this, file);
                this.this$0.m_LoadThread.start();
              } 
            } 
          }
        });
    setComboSizes();
    this.m_DatasetKeyBut.setEnabled(false);
    this.m_DatasetKeyBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setDatasetKeyFromDialog();
          }
        });
    this.m_DatasetKeyList.setSelectionMode(2);
    this.m_ResultKeyBut.setEnabled(false);
    this.m_ResultKeyBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setResultKeyFromDialog();
          }
        });
    this.m_ResultKeyList.setSelectionMode(2);
    this.m_CompareCombo.setEnabled(false);
    this.m_SigTex.setEnabled(false);
    this.m_TestsButton.setEnabled(false);
    this.m_TestsButton.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setTestBaseFromDialog();
          }
        });
    this.m_DisplayedButton.setEnabled(false);
    this.m_DisplayedButton.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setDisplayedFromDialog();
          }
        });
    this.m_ShowStdDevs.setEnabled(false);
    this.m_OutputFormatButton.setEnabled(false);
    this.m_OutputFormatButton.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setOutputFormatFromDialog();
          }
        });
    this.m_PerformBut.setEnabled(false);
    this.m_PerformBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.performTest();
            this.this$0.m_SaveOutBut.setEnabled(true);
          }
        });
    this.m_PerformBut.setToolTipText("Performs test using corrected resampled t-test statistic (Nadeau and Bengio)");
    this.m_SaveOutBut.setEnabled(false);
    this.m_SaveOutBut.addActionListener(new ActionListener(this) {
          private final ResultsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveBuffer();
          }
        });
    this.m_OutText.setFont(new Font("Monospaced", 0, 12));
    this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_OutText.setEditable(false);
    this.m_History.setBorder(BorderFactory.createTitledBorder("Result list"));
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createTitledBorder("Source"));
    JPanel jPanel2 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
    jPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
    jPanel2.setLayout(gridBagLayout1);
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.weightx = 5.0D;
    gridBagConstraints1.fill = 2;
    gridBagConstraints1.gridwidth = 1;
    gridBagConstraints1.gridheight = 1;
    gridBagConstraints1.insets = new Insets(0, 2, 0, 2);
    jPanel2.add(this.m_FromFileBut, gridBagConstraints1);
    gridBagConstraints1.gridx = 1;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.weightx = 5.0D;
    gridBagConstraints1.gridwidth = 1;
    gridBagConstraints1.gridheight = 1;
    jPanel2.add(this.m_FromDBaseBut, gridBagConstraints1);
    gridBagConstraints1.gridx = 2;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.weightx = 5.0D;
    gridBagConstraints1.gridwidth = 1;
    gridBagConstraints1.gridheight = 1;
    jPanel2.add(this.m_FromExpBut, gridBagConstraints1);
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add(this.m_FromLab, "Center");
    jPanel1.add(jPanel2, "East");
    JPanel jPanel3 = new JPanel();
    jPanel3.setBorder(BorderFactory.createTitledBorder("Configure test"));
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    jPanel3.setLayout(gridBagLayout2);
    GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 0;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(this.m_DatasetKeyLabel, gridBagConstraints2);
    jPanel3.add(this.m_DatasetKeyLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.gridy = 0;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_DatasetKeyBut, gridBagConstraints2);
    jPanel3.add(this.m_DatasetKeyBut);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 2;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(this.m_ResultKeyLabel, gridBagConstraints2);
    jPanel3.add(this.m_ResultKeyLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.gridy = 2;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_ResultKeyBut, gridBagConstraints2);
    jPanel3.add(this.m_ResultKeyBut);
    JLabel jLabel = new JLabel("Comparison field", 4);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 3;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
    jPanel3.add(jLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.gridy = 3;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_CompareCombo, gridBagConstraints2);
    jPanel3.add(this.m_CompareCombo);
    jLabel = new JLabel("Significance", 4);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 4;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
    jPanel3.add(jLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.gridy = 4;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagLayout2.setConstraints(this.m_SigTex, gridBagConstraints2);
    jPanel3.add(this.m_SigTex);
    jLabel = new JLabel("Test base", 4);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 5;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
    jPanel3.add(jLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.fill = 2;
    gridBagConstraints2.gridy = 5;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_TestsButton, gridBagConstraints2);
    jPanel3.add(this.m_TestsButton);
    jLabel = new JLabel("Displayed Columns", 4);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 6;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
    jPanel3.add(jLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.fill = 2;
    gridBagConstraints2.gridy = 6;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_DisplayedButton, gridBagConstraints2);
    jPanel3.add(this.m_DisplayedButton);
    jLabel = new JLabel("Show std. deviations", 4);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 7;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
    jPanel3.add(jLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 17;
    gridBagConstraints2.gridy = 7;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_ShowStdDevs, gridBagConstraints2);
    jPanel3.add(this.m_ShowStdDevs);
    jLabel = new JLabel("Output Format", 4);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 13;
    gridBagConstraints2.gridy = 8;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(2, 10, 2, 10);
    gridBagLayout2.setConstraints(jLabel, gridBagConstraints2);
    jPanel3.add(jLabel);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 17;
    gridBagConstraints2.gridy = 8;
    gridBagConstraints2.gridx = 1;
    gridBagConstraints2.weightx = 100.0D;
    gridBagConstraints2.insets = new Insets(5, 0, 5, 0);
    gridBagLayout2.setConstraints(this.m_OutputFormatButton, gridBagConstraints2);
    jPanel3.add(this.m_OutputFormatButton);
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new BorderLayout());
    jPanel4.setBorder(BorderFactory.createTitledBorder("Test output"));
    jPanel4.add(new JScrollPane(this.m_OutText), "Center");
    JPanel jPanel5 = new JPanel();
    gridBagLayout2 = new GridBagLayout();
    jPanel5.setLayout(gridBagLayout2);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.gridy = 0;
    gridBagConstraints2.gridx = 0;
    gridBagLayout2.setConstraints(jPanel3, gridBagConstraints2);
    jPanel5.add(jPanel3);
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new GridLayout(1, 2, 5, 5));
    jPanel6.add(this.m_PerformBut);
    jPanel6.add(this.m_SaveOutBut);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.anchor = 11;
    gridBagConstraints2.fill = 2;
    gridBagConstraints2.gridy = 1;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
    gridBagLayout2.setConstraints(jPanel6, gridBagConstraints2);
    jPanel5.add(jPanel6);
    gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.fill = 1;
    gridBagConstraints2.gridy = 2;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.weightx = 0.0D;
    gridBagConstraints2.weighty = 100.0D;
    gridBagLayout2.setConstraints((Component)this.m_History, gridBagConstraints2);
    jPanel5.add((Component)this.m_History);
    JSplitPane jSplitPane = new JSplitPane(1, jPanel5, jPanel4);
    jSplitPane.setOneTouchExpandable(true);
    setLayout(new BorderLayout());
    add(jPanel1, "North");
    add(jSplitPane, "Center");
  }
  
  protected void setComboSizes() {
    this.m_DatasetKeyBut.setPreferredSize(this.COMBO_SIZE);
    this.m_ResultKeyBut.setPreferredSize(this.COMBO_SIZE);
    this.m_CompareCombo.setPreferredSize(this.COMBO_SIZE);
    this.m_SigTex.setPreferredSize(this.COMBO_SIZE);
    this.m_DatasetKeyBut.setMaximumSize(this.COMBO_SIZE);
    this.m_ResultKeyBut.setMaximumSize(this.COMBO_SIZE);
    this.m_CompareCombo.setMaximumSize(this.COMBO_SIZE);
    this.m_SigTex.setMaximumSize(this.COMBO_SIZE);
    this.m_DatasetKeyBut.setMinimumSize(this.COMBO_SIZE);
    this.m_ResultKeyBut.setMinimumSize(this.COMBO_SIZE);
    this.m_CompareCombo.setMinimumSize(this.COMBO_SIZE);
    this.m_SigTex.setMinimumSize(this.COMBO_SIZE);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_Exp = paramExperiment;
    this.m_FromExpBut.setEnabled((paramExperiment != null));
  }
  
  protected void setInstancesFromDBaseQuery() {
    try {
      if (this.m_InstanceQuery == null)
        this.m_InstanceQuery = new InstanceQuery(); 
      String str1 = this.m_InstanceQuery.getDatabaseURL();
      String str2 = this.m_InstanceQuery.getUsername();
      String str3 = this.m_InstanceQuery.getPassword();
      DatabaseConnectionDialog databaseConnectionDialog = new DatabaseConnectionDialog(null, str1, str2);
      databaseConnectionDialog.setVisible(true);
      if (databaseConnectionDialog.getReturnValue() == -1) {
        this.m_FromLab.setText("Cancelled");
        return;
      } 
      str1 = databaseConnectionDialog.getURL();
      str2 = databaseConnectionDialog.getUsername();
      str3 = databaseConnectionDialog.getPassword();
      this.m_InstanceQuery.setDatabaseURL(str1);
      this.m_InstanceQuery.setUsername(str2);
      this.m_InstanceQuery.setPassword(str3);
      this.m_InstanceQuery.connectToDatabase();
      if (!this.m_InstanceQuery.experimentIndexExists()) {
        System.err.println("not found");
        this.m_FromLab.setText("No experiment index");
        return;
      } 
      System.err.println("found");
      this.m_FromLab.setText("Getting experiment index");
      Instances instances = this.m_InstanceQuery.retrieveInstances("SELECT * FROM Experiment_index");
      if (instances.numInstances() == 0) {
        this.m_FromLab.setText("No experiments available");
        return;
      } 
      this.m_FromLab.setText("Got experiment index");
      DefaultListModel defaultListModel = new DefaultListModel();
      for (byte b = 0; b < instances.numInstances(); b++)
        defaultListModel.addElement(instances.instance(b).toString()); 
      JList jList = new JList(defaultListModel);
      jList.setSelectedIndex(0);
      ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, jList);
      int i = listSelectorDialog.showDialog();
      if (i != 0) {
        this.m_FromLab.setText("Cancelled");
        return;
      } 
      Instance instance = instances.instance(jList.getSelectedIndex());
      Attribute attribute = instances.attribute("Result_table");
      String str4 = "Results" + instance.toString(attribute);
      setInstancesFromDatabaseTable(str4);
    } catch (Exception exception) {
      exception.printStackTrace();
      this.m_FromLab.setText("Problem reading database: '" + exception.getMessage() + "'");
    } 
  }
  
  protected void setInstancesFromExp(Experiment paramExperiment) {
    if (paramExperiment.getResultListener() instanceof CSVResultListener) {
      File file = ((CSVResultListener)paramExperiment.getResultListener()).getOutputFile();
      if (file == null) {
        this.m_FromLab.setText("No result file");
      } else {
        setInstancesFromFile(file);
      } 
    } else if (paramExperiment.getResultListener() instanceof DatabaseResultListener) {
      String str = ((DatabaseResultListener)paramExperiment.getResultListener()).getDatabaseURL();
      try {
        if (this.m_InstanceQuery == null)
          this.m_InstanceQuery = new InstanceQuery(); 
        this.m_InstanceQuery.setDatabaseURL(str);
        this.m_InstanceQuery.connectToDatabase();
        String str1 = this.m_InstanceQuery.getResultsTableName(paramExperiment.getResultProducer());
        setInstancesFromDatabaseTable(str1);
      } catch (Exception exception) {
        this.m_FromLab.setText("Problem reading database");
      } 
    } else {
      this.m_FromLab.setText("Can't get results from experiment");
    } 
  }
  
  protected void setInstancesFromDatabaseTable(String paramString) {
    try {
      this.m_FromLab.setText("Reading from database, please wait...");
      Instances instances = this.m_InstanceQuery.retrieveInstances("SELECT * FROM " + paramString);
      SwingUtilities.invokeAndWait(new Runnable(this, instances) {
            private final Instances val$i;
            
            private final ResultsPanel this$0;
            
            public void run() {
              this.this$0.setInstances(this.val$i);
            }
          });
      this.m_InstanceQuery.disconnectFromDatabase();
    } catch (Exception exception) {
      this.m_FromLab.setText(exception.getMessage());
    } 
  }
  
  protected void setInstancesFromFile(File paramFile) {
    String str = paramFile.getName();
    try {
      this.m_FromLab.setText("Reading from file...");
      if (paramFile.getName().toLowerCase().endsWith(Instances.FILE_EXTENSION)) {
        str = "arff";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramFile));
        setInstances(new Instances(bufferedReader));
        bufferedReader.close();
      } else if (paramFile.getName().toLowerCase().endsWith(CSVLoader.FILE_EXTENSION)) {
        str = "csv";
        CSVLoader cSVLoader = new CSVLoader();
        cSVLoader.setSource(paramFile);
        Instances instances = cSVLoader.getDataSet();
        setInstances(instances);
      } else {
        throw new Exception("Unrecognized file type");
      } 
    } catch (Exception exception) {
      this.m_FromLab.setText("File '" + paramFile.getName() + "' not recognised as an " + str + " file.");
      if (JOptionPane.showOptionDialog(this, "File '" + paramFile.getName() + "' not recognised as an " + str + " file.\n" + "Reason:\n" + exception.getMessage(), "Load Instances", 0, 0, null, (Object[])new String[] { "OK" }, null) == 1);
    } 
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    this.m_TTester.setInstances(this.m_Instances);
    this.m_FromLab.setText("Got " + this.m_Instances.numInstances() + " results");
    this.m_DatasetKeyModel.removeAllElements();
    this.m_ResultKeyModel.removeAllElements();
    this.m_CompareModel.removeAllElements();
    byte b = -1;
    String str1 = "";
    String str2 = "";
    for (byte b1 = 0; b1 < this.m_Instances.numAttributes(); b1++) {
      String str = this.m_Instances.attribute(b1).name();
      if (str.toLowerCase().startsWith("key_", 0)) {
        this.m_DatasetKeyModel.addElement(str.substring(4));
        this.m_ResultKeyModel.addElement(str.substring(4));
        this.m_CompareModel.addElement(str.substring(4));
      } else {
        this.m_DatasetKeyModel.addElement(str);
        this.m_ResultKeyModel.addElement(str);
        this.m_CompareModel.addElement(str);
      } 
      if (str.toLowerCase().equals("key_dataset")) {
        this.m_DatasetKeyList.addSelectionInterval(b1, b1);
        str2 = str2 + "," + (b1 + 1);
      } else if (str.toLowerCase().equals("key_run")) {
        this.m_TTester.setRunColumn(b1);
      } else if (str.toLowerCase().equals("key_fold")) {
        this.m_TTester.setFoldColumn(b1);
      } else if (str.toLowerCase().equals("key_scheme") || str.toLowerCase().equals("key_scheme_options") || str.toLowerCase().equals("key_scheme_version_id")) {
        this.m_ResultKeyList.addSelectionInterval(b1, b1);
        str1 = str1 + "," + (b1 + 1);
      } else if (str.toLowerCase().indexOf("percent_correct") != -1) {
        this.m_CompareCombo.setSelectedIndex(b1);
      } else if (str.toLowerCase().indexOf("root_relative_squared_error") != -1 && this.m_CompareCombo.getSelectedIndex() < 0) {
        this.m_CompareCombo.setSelectedIndex(b1);
      } 
    } 
    this.m_DatasetKeyBut.setEnabled(true);
    this.m_ResultKeyBut.setEnabled(true);
    this.m_CompareCombo.setEnabled(true);
    Range range = new Range();
    if (str1.length() != 0)
      try {
        range.setRanges(str1);
      } catch (Exception exception) {
        exception.printStackTrace();
        System.err.println(exception.getMessage());
      }  
    this.m_TTester.setResultsetKeyColumns(range);
    range = new Range();
    if (str2.length() != 0)
      try {
        range.setRanges(str2);
      } catch (Exception exception) {
        exception.printStackTrace();
        System.err.println(exception.getMessage());
      }  
    this.m_TTester.setDatasetKeyColumns(range);
    this.m_SigTex.setEnabled(true);
    setTTester();
  }
  
  protected void setTTester() {
    this.m_TTester.setDisplayedResultsets(null);
    String str = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date()) + "Available resultsets";
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Available resultsets\n" + this.m_TTester.resultsetKey() + "\n\n");
    this.m_History.addResult(str, stringBuffer);
    this.m_History.setSingle(str);
    this.m_TestsModel.removeAllElements();
    byte b;
    for (b = 0; b < this.m_TTester.getNumResultsets(); b++) {
      String str1 = this.m_TTester.getResultsetName(b);
      this.m_TestsModel.addElement(str1);
    } 
    this.m_DisplayedModel.removeAllElements();
    for (b = 0; b < this.m_TestsModel.size(); b++)
      this.m_DisplayedModel.addElement(this.m_TestsModel.elementAt(b)); 
    this.m_TestsModel.addElement("Summary");
    this.m_TestsModel.addElement("Ranking");
    this.m_TestsList.setSelectedIndex(0);
    this.m_DisplayedList.setSelectionInterval(0, this.m_DisplayedModel.size() - 1);
    this.m_TestsButton.setEnabled(true);
    this.m_DisplayedButton.setEnabled(true);
    this.m_ShowStdDevs.setEnabled(true);
    this.m_OutputFormatButton.setEnabled(true);
    this.m_PerformBut.setEnabled(true);
  }
  
  protected void performTest() {
    String str1 = this.m_SigTex.getText();
    if (str1.length() != 0) {
      this.m_TTester.setSignificanceLevel((new Double(str1)).doubleValue());
    } else {
      this.m_TTester.setSignificanceLevel(0.05D);
    } 
    this.m_TTester.setShowStdDevs(this.m_ShowStdDevs.isSelected());
    int i = this.m_CompareCombo.getSelectedIndex();
    int j = this.m_TestsList.getSelectedIndex();
    String str2 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date()) + (String)this.m_CompareCombo.getSelectedItem() + " - " + (String)this.m_TestsList.getSelectedValue();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.m_TTester.header(i));
    stringBuffer.append("\n");
    this.m_History.addResult(str2, stringBuffer);
    this.m_History.setSingle(str2);
    this.m_TTester.setDisplayedResultsets(this.m_DisplayedList.getSelectedIndices());
    this.m_TTester.setMeanPrec(this.m_MeanPrec);
    this.m_TTester.setStdDevPrec(this.m_StdDevPrec);
    this.m_TTester.setProduceLatex(this.m_latexOutput);
    this.m_TTester.setProduceCSV(this.m_csvOutput);
    try {
      if (j < this.m_TTester.getNumResultsets()) {
        stringBuffer.append(this.m_TTester.multiResultsetFull(j, i));
      } else if (j == this.m_TTester.getNumResultsets()) {
        stringBuffer.append(this.m_TTester.multiResultsetSummary(i));
      } else {
        stringBuffer.append(this.m_TTester.multiResultsetRanking(i));
      } 
      stringBuffer.append("\n");
    } catch (Exception exception) {
      stringBuffer.append(exception.getMessage() + "\n");
    } 
    this.m_History.updateResult(str2);
  }
  
  public void setResultKeyFromDialog() {
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, this.m_ResultKeyList);
    int i = listSelectorDialog.showDialog();
    if (i == 0) {
      int[] arrayOfInt = this.m_ResultKeyList.getSelectedIndices();
      String str = "";
      for (byte b = 0; b < arrayOfInt.length; b++)
        str = str + "," + (arrayOfInt[b] + 1); 
      Range range = new Range();
      if (str.length() != 0)
        try {
          range.setRanges(str);
        } catch (Exception exception) {
          exception.printStackTrace();
          System.err.println(exception.getMessage());
        }  
      this.m_TTester.setResultsetKeyColumns(range);
      setTTester();
    } 
  }
  
  public void setDatasetKeyFromDialog() {
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, this.m_DatasetKeyList);
    int i = listSelectorDialog.showDialog();
    if (i == 0) {
      int[] arrayOfInt = this.m_DatasetKeyList.getSelectedIndices();
      String str = "";
      for (byte b = 0; b < arrayOfInt.length; b++)
        str = str + "," + (arrayOfInt[b] + 1); 
      Range range = new Range();
      if (str.length() != 0)
        try {
          range.setRanges(str);
        } catch (Exception exception) {
          exception.printStackTrace();
          System.err.println(exception.getMessage());
        }  
      this.m_TTester.setDatasetKeyColumns(range);
      setTTester();
    } 
  }
  
  public void setTestBaseFromDialog() {
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, this.m_TestsList);
    listSelectorDialog.showDialog();
  }
  
  public void setDisplayedFromDialog() {
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, this.m_DisplayedList);
    listSelectorDialog.showDialog();
  }
  
  public void setOutputFormatFromDialog() {
    OutputFormatDialog outputFormatDialog = new OutputFormatDialog(null);
    outputFormatDialog.setProduceLatex(this.m_latexOutput);
    outputFormatDialog.setProduceCSV(this.m_csvOutput);
    outputFormatDialog.setMeanPrec(this.m_MeanPrec);
    outputFormatDialog.setStdDevPrec(this.m_StdDevPrec);
    if (outputFormatDialog.showDialog() == 0) {
      this.m_latexOutput = outputFormatDialog.getProduceLatex();
      this.m_csvOutput = outputFormatDialog.getProduceCSV();
      this.m_MeanPrec = outputFormatDialog.getMeanPrec();
      this.m_StdDevPrec = outputFormatDialog.getStdDevPrec();
    } 
  }
  
  protected void saveBuffer() {
    StringBuffer stringBuffer = this.m_History.getSelectedBuffer();
    if (stringBuffer != null) {
      if (this.m_SaveOut.save(stringBuffer))
        JOptionPane.showMessageDialog(this, "File saved", "Results", 1); 
    } else {
      this.m_SaveOutBut.setEnabled(false);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Experiment: Results Analysis");
      jFrame.getContentPane().setLayout(new BorderLayout());
      ResultsPanel resultsPanel = new ResultsPanel();
      jFrame.getContentPane().add(resultsPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setSize(700, 550);
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\ResultsPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */