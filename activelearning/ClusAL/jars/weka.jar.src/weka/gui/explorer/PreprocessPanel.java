package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.C45Loader;
import weka.core.converters.CSVLoader;
import weka.core.converters.Loader;
import weka.core.converters.Saver;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.AttributeSelectionPanel;
import weka.gui.AttributeSummaryPanel;
import weka.gui.AttributeVisualizationPanel;
import weka.gui.ExtensionFileFilter;
import weka.gui.FileEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.InstancesSummaryPanel;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.PropertyDialog;
import weka.gui.PropertyPanel;
import weka.gui.SelectedTagEditor;
import weka.gui.SysErrLog;
import weka.gui.TaskLogger;
import weka.gui.ViewerDialog;
import weka.gui.beans.AttributeSummarizer;

public class PreprocessPanel extends JPanel {
  protected InstancesSummaryPanel m_InstSummaryPanel = new InstancesSummaryPanel();
  
  protected JButton m_OpenFileBut = new JButton("Open file...");
  
  protected JButton m_OpenURLBut = new JButton("Open URL...");
  
  protected JButton m_OpenDBBut = new JButton("Open DB...");
  
  protected GenericObjectEditor m_DatabaseQueryEditor = new GenericObjectEditor();
  
  protected JButton m_UndoBut = new JButton("Undo");
  
  protected JButton m_EditBut = new JButton("Edit...");
  
  protected JButton m_SaveBut = new JButton("Save...");
  
  protected AttributeSelectionPanel m_AttPanel = new AttributeSelectionPanel();
  
  JButton m_RemoveButton = new JButton("Remove");
  
  protected AttributeSummaryPanel m_AttSummaryPanel = new AttributeSummaryPanel();
  
  protected GenericObjectEditor m_FilterEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_FilterPanel = new PropertyPanel((PropertyEditor)this.m_FilterEditor);
  
  protected JButton m_ApplyFilterBut = new JButton("Apply");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected ExtensionFileFilter m_bsiFileFilter = new ExtensionFileFilter(Instances.SERIALIZED_OBJ_FILE_EXTENSION, "Binary serialized instances");
  
  protected ExtensionFileFilter m_c45FileFilter = new ExtensionFileFilter(C45Loader.FILE_EXTENSION, "C45 names files");
  
  protected ExtensionFileFilter m_csvFileFilter = new ExtensionFileFilter(CSVLoader.FILE_EXTENSION, "CSV data files");
  
  protected ExtensionFileFilter m_arffFileFilter = new ExtensionFileFilter(Instances.FILE_EXTENSION, "Arff data files");
  
  protected String m_LastURL = "http://";
  
  protected String m_SQLQ = new String("SELECT * FROM ?");
  
  protected Instances m_Instances;
  
  protected AttributeVisualizationPanel m_AttVisualizePanel = new AttributeVisualizationPanel();
  
  protected File[] m_tempUndoFiles = new File[20];
  
  protected int m_tempUndoIndex = 0;
  
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
  
  protected Thread m_IOThread;
  
  protected Logger m_Log = (Logger)new SysErrLog();
  
  public PreprocessPanel() {
    try {
      this.m_DatabaseQueryEditor.setClassType(InstanceQuery.class);
      this.m_DatabaseQueryEditor.setValue(new InstanceQuery());
      ((GenericObjectEditor.GOEPanel)this.m_DatabaseQueryEditor.getCustomEditor()).addOkListener(new ActionListener(this) {
            private final PreprocessPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.setInstancesFromDBQ();
            }
          });
    } catch (Exception exception) {}
    this.m_FilterEditor.setClassType(Filter.class);
    this.m_OpenFileBut.setToolTipText("Open a set of instances from a file");
    this.m_OpenURLBut.setToolTipText("Open a set of instances from a URL");
    this.m_OpenDBBut.setToolTipText("Open a set of instances from a database");
    this.m_UndoBut.setToolTipText("Undo the last change to the dataset");
    this.m_EditBut.setToolTipText("Open the current dataset in a Viewer for editing");
    this.m_SaveBut.setToolTipText("Save the working relation to a file");
    this.m_ApplyFilterBut.setToolTipText("Apply the current filter to the data");
    this.m_FileChooser.addChoosableFileFilter((FileFilter)this.m_bsiFileFilter);
    this.m_FileChooser.addChoosableFileFilter((FileFilter)this.m_c45FileFilter);
    this.m_FileChooser.addChoosableFileFilter((FileFilter)this.m_csvFileFilter);
    this.m_FileChooser.addChoosableFileFilter((FileFilter)this.m_arffFileFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_OpenURLBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setInstancesFromURLQ();
          }
        });
    this.m_OpenDBBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            PropertyDialog propertyDialog = new PropertyDialog((PropertyEditor)this.this$0.m_DatabaseQueryEditor, 100, 100);
          }
        });
    this.m_OpenFileBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setInstancesFromFileQ();
          }
        });
    this.m_UndoBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.undo();
          }
        });
    this.m_EditBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.edit();
          }
        });
    this.m_SaveBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveWorkingInstancesToFileQ();
          }
        });
    this.m_ApplyFilterBut.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.applyFilter((Filter)this.this$0.m_FilterEditor.getValue());
          }
        });
    this.m_AttPanel.getSelectionModel().addListSelectionListener(new ListSelectionListener(this) {
          private final PreprocessPanel this$0;
          
          public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
            if (!param1ListSelectionEvent.getValueIsAdjusting()) {
              ListSelectionModel listSelectionModel = (ListSelectionModel)param1ListSelectionEvent.getSource();
              for (int i = param1ListSelectionEvent.getFirstIndex(); i <= param1ListSelectionEvent.getLastIndex(); i++) {
                if (listSelectionModel.isSelectedIndex(i)) {
                  this.this$0.m_AttSummaryPanel.setAttribute(i);
                  this.this$0.m_AttVisualizePanel.setAttribute(i);
                  break;
                } 
              } 
            } 
          }
        });
    this.m_InstSummaryPanel.setBorder(BorderFactory.createTitledBorder("Current relation"));
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createTitledBorder("Attributes"));
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add((Component)this.m_AttPanel, "Center");
    this.m_RemoveButton.setEnabled(false);
    this.m_RemoveButton.setToolTipText("Remove selected attributes.");
    this.m_RemoveButton.addActionListener(new ActionListener(this) {
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            try {
              Remove remove = new Remove();
              int[] arrayOfInt = this.this$0.m_AttPanel.getSelectedAttributes();
              if (arrayOfInt.length == 0)
                return; 
              if (arrayOfInt.length == this.this$0.m_Instances.numAttributes()) {
                JOptionPane.showMessageDialog(this.this$0, "Can't remove all attributes from data!\n", "Remove Attributes", 0);
                this.this$0.m_Log.logMessage("Can't remove all attributes from data!");
                this.this$0.m_Log.statusMessage("Problem removing attributes");
                return;
              } 
              remove.setAttributeIndicesArray(arrayOfInt);
              this.this$0.applyFilter((Filter)remove);
            } catch (Exception exception) {
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskFinished(); 
              JOptionPane.showMessageDialog(this.this$0, "Problem filtering instances:\n" + exception.getMessage(), "Remove Attributes", 0);
              this.this$0.m_Log.logMessage("Problem removing attributes: " + exception.getMessage());
              this.this$0.m_Log.statusMessage("Problem removing attributes");
            } 
          }
        });
    JPanel jPanel2 = new JPanel();
    jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel2.setLayout(new BorderLayout());
    jPanel2.add(this.m_RemoveButton, "Center");
    jPanel1.add(jPanel2, "South");
    this.m_AttSummaryPanel.setBorder(BorderFactory.createTitledBorder("Selected attribute"));
    this.m_UndoBut.setEnabled(false);
    this.m_EditBut.setEnabled(false);
    this.m_SaveBut.setEnabled(false);
    this.m_ApplyFilterBut.setEnabled(false);
    JPanel jPanel3 = new JPanel();
    jPanel3.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel3.setLayout(new GridLayout(1, 6, 5, 5));
    jPanel3.add(this.m_OpenFileBut);
    jPanel3.add(this.m_OpenURLBut);
    jPanel3.add(this.m_OpenDBBut);
    jPanel3.add(this.m_UndoBut);
    jPanel3.add(this.m_EditBut);
    jPanel3.add(this.m_SaveBut);
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new BorderLayout());
    jPanel4.add(jPanel1, "Center");
    JPanel jPanel5 = new JPanel();
    jPanel5.setBorder(BorderFactory.createTitledBorder("Filter"));
    jPanel5.setLayout(new BorderLayout());
    jPanel5.add((Component)this.m_FilterPanel, "Center");
    jPanel5.add(this.m_ApplyFilterBut, "East");
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new GridLayout(2, 1));
    jPanel6.add((Component)this.m_AttSummaryPanel);
    JComboBox jComboBox = this.m_AttVisualizePanel.getColorBox();
    jComboBox.setToolTipText("The chosen attribute will also be used as the class attribute when a filter is applied.");
    JButton jButton = new JButton("Visualize All");
    jButton.addActionListener(new ActionListener(this, jButton) {
          private final JButton val$visAllBut;
          
          private final PreprocessPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_Instances != null)
              try {
                AttributeSummarizer attributeSummarizer = new AttributeSummarizer();
                attributeSummarizer.setColoringIndex(this.this$0.m_AttVisualizePanel.getColoringIndex());
                attributeSummarizer.setInstances(this.this$0.m_Instances);
                JFrame jFrame = new JFrame();
                jFrame.getContentPane().setLayout(new BorderLayout());
                jFrame.getContentPane().add((Component)attributeSummarizer, "Center");
                jFrame.addWindowListener((WindowListener)new Object(this, jFrame));
                jFrame.setSize(830, 600);
                jFrame.setVisible(true);
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
          }
        });
    JPanel jPanel7 = new JPanel();
    jPanel7.setLayout(new BorderLayout());
    jPanel7.add((Component)this.m_AttVisualizePanel, "Center");
    JPanel jPanel8 = new JPanel();
    jPanel8.setLayout(new BorderLayout());
    jPanel8.add(jComboBox, "Center");
    jPanel8.add(jButton, "East");
    jPanel7.add(jPanel8, "North");
    jPanel6.add(jPanel7);
    JPanel jPanel9 = new JPanel();
    jPanel9.setLayout(new BorderLayout());
    jPanel9.add((Component)this.m_InstSummaryPanel, "North");
    jPanel9.add(jPanel4, "Center");
    JPanel jPanel10 = new JPanel();
    jPanel10.setLayout(new BorderLayout());
    jPanel10.add(jPanel6, "Center");
    JPanel jPanel11 = new JPanel();
    jPanel11.setLayout(new GridLayout(1, 2));
    jPanel11.add(jPanel9);
    jPanel11.add(jPanel10);
    JPanel jPanel12 = new JPanel();
    jPanel12.setLayout(new BorderLayout());
    jPanel12.add(jPanel5, "North");
    jPanel12.add(jPanel11, "Center");
    setLayout(new BorderLayout());
    add(jPanel3, "North");
    add(jPanel12, "Center");
  }
  
  public void setLog(Logger paramLogger) {
    this.m_Log = paramLogger;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    try {
      Runnable runnable = new Runnable(this) {
          private final PreprocessPanel this$0;
          
          public void run() {
            this.this$0.m_InstSummaryPanel.setInstances(this.this$0.m_Instances);
            this.this$0.m_AttPanel.setInstances(this.this$0.m_Instances);
            this.this$0.m_RemoveButton.setEnabled(true);
            this.this$0.m_AttSummaryPanel.setInstances(this.this$0.m_Instances);
            this.this$0.m_AttVisualizePanel.setInstances(this.this$0.m_Instances);
            this.this$0.m_AttPanel.getSelectionModel().setSelectionInterval(0, 0);
            this.this$0.m_AttSummaryPanel.setAttribute(0);
            this.this$0.m_AttVisualizePanel.setAttribute(0);
            this.this$0.m_ApplyFilterBut.setEnabled(true);
            this.this$0.m_Log.logMessage("Base relation is now " + this.this$0.m_Instances.relationName() + " (" + this.this$0.m_Instances.numInstances() + " instances)");
            this.this$0.m_SaveBut.setEnabled(true);
            this.this$0.m_EditBut.setEnabled(true);
            this.this$0.m_Log.statusMessage("OK");
            this.this$0.m_Support.firePropertyChange("", (Object)null, (Object)null);
          }
        };
      if (SwingUtilities.isEventDispatchThread()) {
        runnable.run();
      } else {
        SwingUtilities.invokeAndWait(runnable);
      } 
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(this, "Problem setting base instances:\n" + exception.getMessage(), "Instances", 0);
    } 
  }
  
  public Instances getInstances() {
    return this.m_Instances;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  protected void applyFilter(Filter paramFilter) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramFilter) {
          private final Filter val$filter;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              if (this.val$filter != null) {
                if (this.this$0.m_Log instanceof TaskLogger)
                  ((TaskLogger)this.this$0.m_Log).taskStarted(); 
                this.this$0.m_Log.statusMessage("Passing dataset through filter " + this.val$filter.getClass().getName());
                int i = this.this$0.m_AttVisualizePanel.getColoringIndex();
                if (i < 0 && this.val$filter instanceof weka.filters.SupervisedFilter)
                  throw new IllegalArgumentException("Class (colour) needs to be set for supervised filter."); 
                Instances instances1 = new Instances(this.this$0.m_Instances);
                instances1.setClassIndex(i);
                this.val$filter.setInputFormat(instances1);
                Instances instances2 = Filter.useFilter(instances1, this.val$filter);
                if (instances2 == null || instances2.numAttributes() < 1)
                  throw new Exception("Dataset is empty."); 
                this.this$0.m_Log.statusMessage("Saving undo information");
                this.this$0.addUndoPoint();
                this.this$0.m_AttVisualizePanel.setColoringIndex(instances1.classIndex());
                this.this$0.m_Instances = instances2;
                this.this$0.setInstances(this.this$0.m_Instances);
                if (this.this$0.m_Log instanceof TaskLogger)
                  ((TaskLogger)this.this$0.m_Log).taskFinished(); 
              } 
            } catch (Exception exception) {
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskFinished(); 
              JOptionPane.showMessageDialog(this.this$0, "Problem filtering instances:\n" + exception.getMessage(), "Apply Filter", 0);
              this.this$0.m_Log.logMessage("Problem filtering instances: " + exception.getMessage());
              this.this$0.m_Log.statusMessage("Problem filtering instances");
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't apply filter at this time,\ncurrently busy with other IO", "Apply Filter", 2);
    } 
  }
  
  public void saveWorkingInstancesToFileQ() {
    if (this.m_IOThread == null) {
      this.m_FileChooser.setAcceptAllFileFilterUsed(false);
      int i = this.m_FileChooser.showSaveDialog(this);
      if (i == 0) {
        File file = this.m_FileChooser.getSelectedFile();
        if (this.m_FileChooser.getFileFilter() == this.m_arffFileFilter) {
          if (!file.getName().toLowerCase().endsWith(Instances.FILE_EXTENSION))
            file = new File(file.getParent(), file.getName() + Instances.FILE_EXTENSION); 
          File file1 = file;
          saveInstancesToFile(file1, this.m_Instances, true);
        } else if (this.m_FileChooser.getFileFilter() == this.m_csvFileFilter) {
          if (!file.getName().toLowerCase().endsWith(CSVLoader.FILE_EXTENSION))
            file = new File(file.getParent(), file.getName() + CSVLoader.FILE_EXTENSION); 
          File file1 = file;
          saveInstancesToFile(file1, this.m_Instances, false);
        } else if (this.m_FileChooser.getFileFilter() == this.m_c45FileFilter) {
          File file1 = file;
          saveInstancesToC45File(file1, this.m_Instances);
        } else if (this.m_FileChooser.getFileFilter() == this.m_bsiFileFilter) {
          if (!file.getName().toLowerCase().endsWith(Instances.SERIALIZED_OBJ_FILE_EXTENSION))
            file = new File(file.getParent(), file.getName() + Instances.SERIALIZED_OBJ_FILE_EXTENSION); 
          File file1 = file;
          saveSerializedInstancesToFile(file1, this.m_Instances);
        } 
      } 
      FileFilter fileFilter = this.m_FileChooser.getFileFilter();
      this.m_FileChooser.setAcceptAllFileFilterUsed(true);
      this.m_FileChooser.setFileFilter(fileFilter);
    } else {
      JOptionPane.showMessageDialog(this, "Can't save at this time,\ncurrently busy with other IO", "Save Instances", 2);
    } 
  }
  
  public void setInstancesFromFileQ() {
    if (this.m_IOThread == null) {
      int i = this.m_FileChooser.showOpenDialog(this);
      if (i == 0) {
        File file = this.m_FileChooser.getSelectedFile();
        try {
          addUndoPoint();
        } catch (Exception exception) {}
        setInstancesFromFile(file);
      } 
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  public void setInstancesFromDBQ() {
    if (this.m_IOThread == null) {
      try {
        InstanceQuery instanceQuery = (InstanceQuery)this.m_DatabaseQueryEditor.getValue();
        instanceQuery.connectToDatabase();
        try {
          addUndoPoint();
        } catch (Exception exception) {}
        setInstancesFromDB(instanceQuery);
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(this, "Problem connecting to database:\n" + exception.getMessage(), "Load Instances", 0);
      } 
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  public void setInstancesFromURLQ() {
    if (this.m_IOThread == null) {
      try {
        String str = (String)JOptionPane.showInputDialog(this, "Enter the source URL", "Load Instances", 3, null, null, this.m_LastURL);
        if (str != null) {
          this.m_LastURL = str;
          URL uRL = new URL(str);
          try {
            addUndoPoint();
          } catch (Exception exception) {}
          setInstancesFromURL(uRL);
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
        JOptionPane.showMessageDialog(this, "Problem with URL:\n" + exception.getMessage(), "Load Instances", 0);
      } 
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  protected void saveInstancesToC45File(File paramFile, Instances paramInstances) {
    if (this.m_IOThread == null) {
      int i = this.m_AttVisualizePanel.getColoringIndex();
      if (paramInstances.attribute(i).isNumeric()) {
        JOptionPane.showMessageDialog(this, "Can't save in C45 format,\nas the selected class is numeric.", "Save Instances", 0);
        return;
      } 
      this.m_IOThread = new Thread(this, paramFile, paramInstances, i) {
          private final File val$f;
          
          private final Instances val$inst;
          
          private final int val$classIndex;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              this.this$0.m_Log.statusMessage("Saving to file...");
              String str = this.val$f.getAbsolutePath();
              if (str.lastIndexOf('.') != -1)
                str = str.substring(0, str.lastIndexOf('.')); 
              File file1 = new File(str + ".data");
              File file2 = new File(str + ".names");
              BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter(file2));
              BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(file1));
              byte b;
              for (b = 0; b < this.val$inst.attribute(this.val$classIndex).numValues(); b++) {
                bufferedWriter1.write(this.val$inst.attribute(this.val$classIndex).value(b));
                if (b < this.val$inst.attribute(this.val$classIndex).numValues() - 1) {
                  bufferedWriter1.write(",");
                } else {
                  bufferedWriter1.write(".\n");
                } 
              } 
              for (b = 0; b < this.val$inst.numAttributes(); b++) {
                if (b != this.val$classIndex) {
                  bufferedWriter1.write(this.val$inst.attribute(b).name() + ": ");
                  if (this.val$inst.attribute(b).isNumeric() || this.val$inst.attribute(b).isDate()) {
                    bufferedWriter1.write("continuous.\n");
                  } else {
                    Attribute attribute = this.val$inst.attribute(b);
                    for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
                      bufferedWriter1.write(attribute.value(b1));
                      if (b1 < attribute.numValues() - 1) {
                        bufferedWriter1.write(",");
                      } else {
                        bufferedWriter1.write(".\n");
                      } 
                    } 
                  } 
                } 
              } 
              bufferedWriter1.close();
              for (b = 0; b < this.val$inst.numInstances(); b++) {
                Instance instance = this.val$inst.instance(b);
                for (byte b1 = 0; b1 < this.val$inst.numAttributes(); b1++) {
                  if (b1 != this.val$classIndex)
                    if (instance.isMissing(b1)) {
                      bufferedWriter2.write("?,");
                    } else if (this.val$inst.attribute(b1).isNominal() || this.val$inst.attribute(b1).isString()) {
                      bufferedWriter2.write(this.val$inst.attribute(b1).value((int)instance.value(b1)) + ",");
                    } else {
                      bufferedWriter2.write("" + instance.value(b1) + ",");
                    }  
                } 
                if (instance.isMissing(this.val$classIndex)) {
                  bufferedWriter2.write("?");
                } else {
                  bufferedWriter2.write(this.val$inst.attribute(this.val$classIndex).value((int)instance.value(this.val$classIndex)));
                } 
                bufferedWriter2.write("\n");
              } 
              bufferedWriter2.close();
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              exception.printStackTrace();
              this.this$0.m_Log.logMessage(exception.getMessage());
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't save at this time,\ncurrently busy with other IO", "Save c45 format", 2);
    } 
  }
  
  protected void saveSerializedInstancesToFile(File paramFile, Instances paramInstances) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramFile, paramInstances) {
          private final File val$f;
          
          private final Instances val$inst;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              this.this$0.m_Log.statusMessage("Saving to file...");
              ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.val$f)));
              objectOutputStream.writeObject(this.val$inst);
              objectOutputStream.flush();
              objectOutputStream.close();
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              exception.printStackTrace();
              this.this$0.m_Log.logMessage(exception.getMessage());
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't save at this time,\ncurrently busy with other IO", "Save binary serialized instances", 2);
    } 
  }
  
  protected void saveInstancesToFile(File paramFile, Instances paramInstances, boolean paramBoolean) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramFile, paramBoolean, paramInstances) {
          private final File val$f;
          
          private final boolean val$saveHeader;
          
          private final Instances val$inst;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              this.this$0.m_Log.statusMessage("Saving to file...");
              BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.val$f));
              if (this.val$saveHeader) {
                Instances instances = new Instances(this.val$inst, 0);
                bufferedWriter.write(instances.toString());
                bufferedWriter.write("\n");
              } else {
                for (byte b1 = 0; b1 < this.val$inst.numAttributes(); b1++) {
                  bufferedWriter.write(this.val$inst.attribute(b1).name());
                  if (b1 < this.val$inst.numAttributes() - 1)
                    bufferedWriter.write(","); 
                } 
                bufferedWriter.write("\n");
              } 
              for (byte b = 0; b < this.val$inst.numInstances(); b++) {
                bufferedWriter.write(this.val$inst.instance(b).toString());
                bufferedWriter.write("\n");
              } 
              bufferedWriter.close();
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              exception.printStackTrace();
              this.this$0.m_Log.logMessage(exception.getMessage());
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't save at this time,\ncurrently busy with other IO", "Save Instances", 2);
    } 
  }
  
  private void converterQuery(File paramFile) {
    GenericObjectEditor genericObjectEditor = new GenericObjectEditor(true);
    try {
      genericObjectEditor.setClassType(Loader.class);
      genericObjectEditor.setValue(new CSVLoader());
      ((GenericObjectEditor.GOEPanel)genericObjectEditor.getCustomEditor()).addOkListener(new ActionListener(this, genericObjectEditor, paramFile) {
            private final GenericObjectEditor val$convEd;
            
            private final File val$f;
            
            private final PreprocessPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.tryConverter((Loader)this.val$convEd.getValue(), this.val$f);
            }
          });
    } catch (Exception exception) {}
    PropertyDialog propertyDialog = new PropertyDialog((PropertyEditor)genericObjectEditor, 100, 100);
  }
  
  private void tryConverter(Loader paramLoader, File paramFile) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramLoader, paramFile) {
          private final Loader val$cnv;
          
          private final File val$f;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              this.val$cnv.setSource(this.val$f);
              Instances instances = this.val$cnv.getDataSet();
              this.this$0.setInstances(instances);
            } catch (Exception exception) {
              this.this$0.m_Log.statusMessage(this.val$cnv.getClass().getName() + " failed to load " + this.val$f.getName());
              JOptionPane.showMessageDialog(this.this$0, this.val$cnv.getClass().getName() + " failed to load '" + this.val$f.getName() + "'.\n" + "Reason:\n" + exception.getMessage(), "Convert File", 0);
              this.this$0.m_IOThread = null;
              this.this$0.converterQuery(this.val$f);
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } 
  }
  
  public void setInstancesFromFile(File paramFile) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramFile) {
          private final File val$f;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            String str = this.val$f.getName();
            try {
              this.this$0.m_Log.statusMessage("Reading from file...");
              if (this.val$f.getName().toLowerCase().endsWith(Instances.FILE_EXTENSION)) {
                str = "arff";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(this.val$f));
                this.this$0.setInstances(new Instances(bufferedReader));
                bufferedReader.close();
              } else if (this.val$f.getName().toLowerCase().endsWith(CSVLoader.FILE_EXTENSION)) {
                str = "csv";
                CSVLoader cSVLoader = new CSVLoader();
                cSVLoader.setSource(this.val$f);
                Instances instances = cSVLoader.getDataSet();
                this.this$0.setInstances(instances);
              } else if (this.val$f.getName().toLowerCase().endsWith(C45Loader.FILE_EXTENSION)) {
                str = "C45 names";
                C45Loader c45Loader = new C45Loader();
                c45Loader.setSource(this.val$f);
                Instances instances = c45Loader.getDataSet();
                this.this$0.setInstances(instances);
              } else if (this.val$f.getName().toLowerCase().endsWith(Instances.SERIALIZED_OBJ_FILE_EXTENSION) || this.val$f.getName().toLowerCase().endsWith(".tmp")) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(this.val$f)));
                this.this$0.setInstances((Instances)objectInputStream.readObject());
                objectInputStream.close();
              } else {
                throw new Exception("Unrecognized file type");
              } 
            } catch (Exception exception) {
              this.this$0.m_Log.statusMessage("File '" + this.val$f.getName() + "' not recognised as an " + str + " file.");
              this.this$0.m_IOThread = null;
              if (JOptionPane.showOptionDialog(this.this$0, "File '" + this.val$f.getName() + "' not recognised as an " + str + " file.\n" + "Reason:\n" + exception.getMessage(), "Load Instances", 0, 0, null, (Object[])new String[] { "OK", "Use Converter" }, null) == 1)
                this.this$0.converterQuery(this.val$f); 
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  public void setInstancesFromDB(InstanceQuery paramInstanceQuery) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramInstanceQuery) {
          private final InstanceQuery val$iq;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              this.this$0.m_Log.statusMessage("Reading from database...");
              Instances instances = this.val$iq.retrieveInstances();
              SwingUtilities.invokeAndWait((Runnable)new Object(this, instances));
              this.val$iq.disconnectFromDatabase();
            } catch (Exception exception) {
              this.this$0.m_Log.statusMessage("Problem executing DB query " + this.this$0.m_SQLQ);
              JOptionPane.showMessageDialog(this.this$0, "Couldn't read from database:\n" + exception.getMessage(), "Load Instances", 0);
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  public void setInstancesFromURL(URL paramURL) {
    if (this.m_IOThread == null) {
      this.m_IOThread = new Thread(this, paramURL) {
          private final URL val$u;
          
          private final PreprocessPanel this$0;
          
          public void run() {
            try {
              this.this$0.m_Log.statusMessage("Reading from URL...");
              BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.val$u.openStream()));
              this.this$0.setInstances(new Instances(bufferedReader));
              bufferedReader.close();
            } catch (Exception exception) {
              exception.printStackTrace();
              this.this$0.m_Log.statusMessage("Problem reading " + this.val$u);
              JOptionPane.showMessageDialog(this.this$0, "Couldn't read from URL:\n" + this.val$u + "\n" + exception.getMessage(), "Load Instances", 0);
            } 
            this.this$0.m_IOThread = null;
          }
        };
      this.m_IOThread.setPriority(1);
      this.m_IOThread.start();
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  public void addUndoPoint() throws Exception {
    if (this.m_Instances != null) {
      File file = File.createTempFile("weka", null);
      file.deleteOnExit();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
      objectOutputStream.writeObject(this.m_Instances);
      objectOutputStream.flush();
      objectOutputStream.close();
      if (this.m_tempUndoFiles[this.m_tempUndoIndex] != null)
        this.m_tempUndoFiles[this.m_tempUndoIndex].delete(); 
      this.m_tempUndoFiles[this.m_tempUndoIndex] = file;
      if (++this.m_tempUndoIndex >= this.m_tempUndoFiles.length)
        this.m_tempUndoIndex = 0; 
      this.m_UndoBut.setEnabled(true);
    } 
  }
  
  public void undo() {
    if (--this.m_tempUndoIndex < 0)
      this.m_tempUndoIndex = this.m_tempUndoFiles.length - 1; 
    if (this.m_tempUndoFiles[this.m_tempUndoIndex] != null) {
      setInstancesFromFile(this.m_tempUndoFiles[this.m_tempUndoIndex]);
      this.m_tempUndoFiles[this.m_tempUndoIndex] = null;
    } 
    int i = this.m_tempUndoIndex - 1;
    if (i < 0)
      i = this.m_tempUndoFiles.length - 1; 
    this.m_UndoBut.setEnabled((this.m_tempUndoFiles[i] != null));
  }
  
  public void edit() {
    ViewerDialog viewerDialog = new ViewerDialog(null);
    int i = viewerDialog.showDialog(this.m_Instances);
    if (i == 0) {
      try {
        addUndoPoint();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      setInstances(viewerDialog.getInstances());
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Explorer: Preprocess");
      jFrame.getContentPane().setLayout(new BorderLayout());
      PreprocessPanel preprocessPanel = new PreprocessPanel();
      jFrame.getContentPane().add(preprocessPanel, "Center");
      LogPanel logPanel = new LogPanel();
      preprocessPanel.setLog((Logger)logPanel);
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
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    PropertyEditorManager.registerEditor(File.class, FileEditor.class);
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASSearch.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASEvaluation.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(InstanceQuery.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(Loader.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(Saver.class, GenericObjectEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\explorer\PreprocessPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */