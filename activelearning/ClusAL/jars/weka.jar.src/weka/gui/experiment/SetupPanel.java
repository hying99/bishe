package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.UnsupervisedSubsetEvaluator;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.clusterers.Clusterer;
import weka.clusterers.DensityBasedClusterer;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.xml.KOML;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.experiment.RemoteExperiment;
import weka.experiment.ResultListener;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;
import weka.experiment.xml.XMLExperiment;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.ExtensionFileFilter;
import weka.gui.FileEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;
import weka.gui.SelectedTagEditor;

public class SetupPanel extends JPanel {
  protected Experiment m_Exp;
  
  protected JButton m_OpenBut = new JButton("Open...");
  
  protected JButton m_SaveBut = new JButton("Save...");
  
  protected JButton m_NewBut = new JButton("New");
  
  protected FileFilter m_ExpFilter = (FileFilter)new ExtensionFileFilter(Experiment.FILE_EXTENSION, "Experiment configuration files (*" + Experiment.FILE_EXTENSION + ")");
  
  protected FileFilter m_KOMLFilter = (FileFilter)new ExtensionFileFilter(".koml", "Experiment configuration files (*.koml)");
  
  protected FileFilter m_XMLFilter = (FileFilter)new ExtensionFileFilter(".xml", "Experiment configuration files (*.xml)");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected GenericObjectEditor m_RPEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_RPEditorPanel = new PropertyPanel((PropertyEditor)this.m_RPEditor);
  
  protected GenericObjectEditor m_RLEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_RLEditorPanel = new PropertyPanel((PropertyEditor)this.m_RLEditor);
  
  protected GeneratorPropertyIteratorPanel m_GeneratorPropertyPanel = new GeneratorPropertyIteratorPanel();
  
  protected RunNumberPanel m_RunNumberPanel = new RunNumberPanel();
  
  protected DistributeExperimentPanel m_DistributeExperimentPanel = new DistributeExperimentPanel();
  
  protected DatasetListPanel m_DatasetListPanel = new DatasetListPanel();
  
  protected JButton m_NotesButton = new JButton("Notes");
  
  protected JFrame m_NotesFrame = new JFrame("Notes");
  
  protected JTextArea m_NotesText = new JTextArea(null, 10, 0);
  
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
  
  protected JRadioButton m_advanceDataSetFirst = new JRadioButton("Data sets first");
  
  protected JRadioButton m_advanceIteratorFirst = new JRadioButton("Custom generator first");
  
  ActionListener m_RadioListener = new ActionListener(this) {
      private final SetupPanel this$0;
      
      public void actionPerformed(ActionEvent param1ActionEvent) {
        this.this$0.updateRadioLinks();
      }
    };
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public SetupPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public SetupPanel() {
    this.m_DistributeExperimentPanel.addCheckBoxActionListener(new ActionListener(this) {
          private final SetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_DistributeExperimentPanel.distributedExperimentSelected()) {
              if (!(this.this$0.m_Exp instanceof RemoteExperiment))
                try {
                  RemoteExperiment remoteExperiment = new RemoteExperiment(this.this$0.m_Exp);
                  this.this$0.setExperiment((Experiment)remoteExperiment);
                } catch (Exception exception) {
                  exception.printStackTrace();
                }  
            } else if (this.this$0.m_Exp instanceof RemoteExperiment) {
              this.this$0.setExperiment(((RemoteExperiment)this.this$0.m_Exp).getBaseExperiment());
            } 
          }
        });
    this.m_NewBut.addActionListener(new ActionListener(this) {
          private final SetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setExperiment(new Experiment());
          }
        });
    this.m_SaveBut.setEnabled(false);
    this.m_SaveBut.addActionListener(new ActionListener(this) {
          private final SetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveExperiment();
          }
        });
    this.m_OpenBut.addActionListener(new ActionListener(this) {
          private final SetupPanel this$0;
          
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
    this.m_GeneratorPropertyPanel.addActionListener(new ActionListener(this) {
          private final SetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.updateRadioLinks();
          }
        });
    this.m_RPEditor.setClassType(ResultProducer.class);
    this.m_RPEditor.setEnabled(false);
    this.m_RPEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final SetupPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.m_Exp.setResultProducer((ResultProducer)this.this$0.m_RPEditor.getValue());
            this.this$0.m_Exp.setUsePropertyIterator(false);
            this.this$0.m_Exp.setPropertyArray(null);
            this.this$0.m_Exp.setPropertyPath(null);
            this.this$0.m_GeneratorPropertyPanel.setExperiment(this.this$0.m_Exp);
            this.this$0.repaint();
          }
        });
    this.m_RLEditor.setClassType(ResultListener.class);
    this.m_RLEditor.setEnabled(false);
    this.m_RLEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final SetupPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.m_Exp.setResultListener((ResultListener)this.this$0.m_RLEditor.getValue());
            this.this$0.m_Support.firePropertyChange("", (Object)null, (Object)null);
            this.this$0.repaint();
          }
        });
    this.m_NotesFrame.addWindowListener(new WindowAdapter(this) {
          private final SetupPanel this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.this$0.m_NotesButton.setEnabled(true);
          }
        });
    this.m_NotesFrame.getContentPane().add(new JScrollPane(this.m_NotesText));
    this.m_NotesFrame.setSize(600, 400);
    this.m_NotesButton.addActionListener(new ActionListener(this) {
          private final SetupPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_NotesButton.setEnabled(false);
            this.this$0.m_NotesFrame.setVisible(true);
          }
        });
    this.m_NotesButton.setEnabled(false);
    this.m_NotesText.setEditable(true);
    this.m_NotesText.addKeyListener(new KeyAdapter(this) {
          private final SetupPanel this$0;
          
          public void keyReleased(KeyEvent param1KeyEvent) {
            this.this$0.m_Exp.setNotes(this.this$0.m_NotesText.getText());
          }
        });
    this.m_NotesText.addFocusListener(new FocusAdapter(this) {
          private final SetupPanel this$0;
          
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
    jPanel2.setLayout(new BorderLayout());
    jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Result generator"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel2.add((Component)this.m_RPEditorPanel, "North");
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BorderLayout());
    jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Destination"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel3.add((Component)this.m_RLEditorPanel, "North");
    this.m_advanceDataSetFirst.setEnabled(false);
    this.m_advanceIteratorFirst.setEnabled(false);
    this.m_advanceDataSetFirst.setToolTipText("Advance data set before custom generator");
    this.m_advanceIteratorFirst.setToolTipText("Advance custom generator before data set");
    this.m_advanceDataSetFirst.setSelected(true);
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_advanceDataSetFirst);
    buttonGroup.add(this.m_advanceIteratorFirst);
    this.m_advanceDataSetFirst.addActionListener(this.m_RadioListener);
    this.m_advanceIteratorFirst.addActionListener(this.m_RadioListener);
    JPanel jPanel4 = new JPanel();
    jPanel4.setBorder(BorderFactory.createTitledBorder("Iteration control"));
    jPanel4.setLayout(new GridLayout(1, 2));
    jPanel4.add(this.m_advanceDataSetFirst);
    jPanel4.add(this.m_advanceIteratorFirst);
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new BorderLayout());
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new GridBagLayout());
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel6.add(this.m_RunNumberPanel, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 2;
    jPanel6.add(this.m_DistributeExperimentPanel, gridBagConstraints);
    JPanel jPanel7 = new JPanel();
    jPanel7.setLayout(new GridBagLayout());
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel7.add(jPanel6, gridBagConstraints);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel7.add(jPanel4, gridBagConstraints);
    jPanel5.add(jPanel7, "North");
    jPanel5.add(this.m_DatasetListPanel, "Center");
    JPanel jPanel8 = new JPanel();
    jPanel8.setLayout(new GridLayout(1, 2));
    jPanel8.add(jPanel5);
    jPanel8.add(this.m_GeneratorPropertyPanel);
    JPanel jPanel9 = new JPanel();
    jPanel9.setLayout(new GridLayout(2, 1));
    jPanel9.add(jPanel3);
    jPanel9.add(jPanel2);
    JPanel jPanel10 = new JPanel();
    jPanel10.setLayout(new BorderLayout());
    jPanel10.add(this.m_NotesButton, "Center");
    JPanel jPanel11 = new JPanel();
    jPanel11.setLayout(new BorderLayout());
    jPanel11.add(jPanel8, "Center");
    jPanel11.add(jPanel10, "South");
    JPanel jPanel12 = new JPanel();
    jPanel12.setLayout(new BorderLayout());
    jPanel12.add(jPanel1, "North");
    jPanel12.add(jPanel9, "South");
    setLayout(new BorderLayout());
    add(jPanel12, "North");
    add(jPanel11, "Center");
  }
  
  protected void removeNotesFrame() {
    this.m_NotesFrame.setVisible(false);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    boolean bool = paramExperiment.getUsePropertyIterator();
    Object object = paramExperiment.getPropertyArray();
    PropertyNode[] arrayOfPropertyNode = paramExperiment.getPropertyPath();
    this.m_Exp = paramExperiment;
    this.m_SaveBut.setEnabled(true);
    this.m_RPEditor.setValue(this.m_Exp.getResultProducer());
    this.m_RPEditor.setEnabled(true);
    this.m_RPEditorPanel.repaint();
    this.m_RLEditor.setValue(this.m_Exp.getResultListener());
    this.m_RLEditor.setEnabled(true);
    this.m_RLEditorPanel.repaint();
    this.m_NotesText.setText(paramExperiment.getNotes());
    this.m_NotesButton.setEnabled(true);
    this.m_advanceDataSetFirst.setSelected(this.m_Exp.getAdvanceDataSetFirst());
    this.m_advanceIteratorFirst.setSelected(!this.m_Exp.getAdvanceDataSetFirst());
    this.m_advanceDataSetFirst.setEnabled(true);
    this.m_advanceIteratorFirst.setEnabled(true);
    paramExperiment.setPropertyPath(arrayOfPropertyNode);
    paramExperiment.setPropertyArray(object);
    paramExperiment.setUsePropertyIterator(bool);
    this.m_GeneratorPropertyPanel.setExperiment(this.m_Exp);
    this.m_RunNumberPanel.setExperiment(this.m_Exp);
    this.m_DatasetListPanel.setExperiment(this.m_Exp);
    this.m_DistributeExperimentPanel.setExperiment(this.m_Exp);
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
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
      setExperiment(experiment);
      System.err.println("Opened experiment:\n" + this.m_Exp);
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
  
  private void updateRadioLinks() {
    this.m_advanceDataSetFirst.setEnabled(this.m_GeneratorPropertyPanel.getEditorActive());
    this.m_advanceIteratorFirst.setEnabled(this.m_GeneratorPropertyPanel.getEditorActive());
    if (this.m_Exp != null)
      if (!this.m_GeneratorPropertyPanel.getEditorActive()) {
        this.m_Exp.setAdvanceDataSetFirst(true);
      } else {
        this.m_Exp.setAdvanceDataSetFirst(this.m_advanceDataSetFirst.isSelected());
      }  
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      boolean bool1 = Utils.getFlag('l', paramArrayOfString);
      boolean bool2 = Utils.getFlag('s', paramArrayOfString);
      String str = Utils.getOption('f', paramArrayOfString);
      if ((bool1 || bool2) && str.length() == 0)
        throw new Exception("A filename must be given with the -f option"); 
      Experiment experiment = null;
      if (bool1) {
        FileInputStream fileInputStream = new FileInputStream(str);
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        experiment = (Experiment)objectInputStream.readObject();
        objectInputStream.close();
      } else {
        experiment = new Experiment();
      } 
      System.err.println("Initial Experiment:\n" + experiment.toString());
      JFrame jFrame = new JFrame("Weka Experiment Setup");
      jFrame.getContentPane().setLayout(new BorderLayout());
      SetupPanel setupPanel = new SetupPanel();
      jFrame.getContentPane().add(setupPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(setupPanel, bool2, str, jFrame) {
            private final SetupPanel val$sp;
            
            private final boolean val$writeExp;
            
            private final String val$expFile;
            
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              System.err.println("\nFinal Experiment:\n" + this.val$sp.m_Exp.toString());
              if (this.val$writeExp)
                try {
                  FileOutputStream fileOutputStream = new FileOutputStream(this.val$expFile);
                  ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
                  objectOutputStream.writeObject(this.val$sp.m_Exp);
                  objectOutputStream.close();
                } catch (Exception exception) {
                  exception.printStackTrace();
                  System.err.println("Couldn't write experiment to: " + this.val$expFile + '\n' + exception.getMessage());
                }  
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      System.err.println("Short nap");
      Thread.currentThread();
      Thread.sleep(3000L);
      System.err.println("Done");
      setupPanel.setExperiment(experiment);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    System.err.println("---Registering Weka Editors---");
    PropertyEditorManager.registerEditor(File.class, FileEditor.class);
    PropertyEditorManager.registerEditor(ResultListener.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ResultProducer.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(SplitEvaluator.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor((array$Lweka$classifiers$Classifier == null) ? (array$Lweka$classifiers$Classifier = class$("[Lweka.classifiers.Classifier;")) : array$Lweka$classifiers$Classifier, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASEvaluation.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASSearch.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(UnsupervisedSubsetEvaluator.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(Clusterer.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(DensityBasedClusterer.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(CostMatrix.class, CostMatrixEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\SetupPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */