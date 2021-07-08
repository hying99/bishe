package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.xml.XMLClassifier;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.experiment.Experiment;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.ExtensionFileFilter;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyDialog;
import weka.gui.SelectedTagEditor;

public class AlgorithmListPanel extends JPanel implements ActionListener {
  protected Experiment m_Exp;
  
  protected JList m_List = new JList();
  
  protected JButton m_AddBut = new JButton("Add new...");
  
  protected JButton m_EditBut = new JButton("Edit selected...");
  
  protected JButton m_DeleteBut = new JButton("Delete selected");
  
  protected JButton m_LoadOptionsBut = new JButton("Load options...");
  
  protected JButton m_SaveOptionsBut = new JButton("Save options...");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected FileFilter m_XMLFilter = (FileFilter)new ExtensionFileFilter(".xml", "Experiment configuration files (*.xml)");
  
  protected boolean m_Editing = false;
  
  protected GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor(true);
  
  protected PropertyDialog m_PD;
  
  protected DefaultListModel m_AlgorithmListModel = new DefaultListModel();
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public AlgorithmListPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public AlgorithmListPanel() {
    this.m_ClassifierEditor.setClassType(Classifier.class);
    this.m_ClassifierEditor.setValue(new ZeroR());
    this.m_ClassifierEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final AlgorithmListPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.repaint();
          }
        });
    ((GenericObjectEditor.GOEPanel)this.m_ClassifierEditor.getCustomEditor()).addOkListener(new ActionListener(this) {
          private final AlgorithmListPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            Classifier classifier = (Classifier)this.this$0.copyObject(this.this$0.m_ClassifierEditor.getValue());
            this.this$0.addNewAlgorithm(classifier);
          }
        });
    this.m_DeleteBut.setEnabled(false);
    this.m_DeleteBut.addActionListener(this);
    this.m_AddBut.setEnabled(false);
    this.m_AddBut.addActionListener(this);
    this.m_EditBut.setEnabled(false);
    this.m_EditBut.addActionListener(this);
    this.m_LoadOptionsBut.setEnabled(false);
    this.m_LoadOptionsBut.addActionListener(this);
    this.m_SaveOptionsBut.setEnabled(false);
    this.m_SaveOptionsBut.addActionListener(this);
    this.m_List.addListSelectionListener(new ListSelectionListener(this) {
          private final AlgorithmListPanel this$0;
          
          public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
            this.this$0.setButtons(param1ListSelectionEvent);
          }
        });
    this.m_FileChooser.addChoosableFileFilter(this.m_XMLFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Algorithms"));
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
    jPanel1.add(this.m_AddBut, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_EditBut, gridBagConstraints);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel1.add(this.m_DeleteBut, gridBagConstraints);
    JPanel jPanel2 = new JPanel();
    gridBagLayout = new GridBagLayout();
    gridBagConstraints = new GridBagConstraints();
    jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel2.setLayout(gridBagLayout);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel2.add(this.m_LoadOptionsBut, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel2.add(this.m_SaveOptionsBut, gridBagConstraints);
    add(jPanel1, "North");
    add(new JScrollPane(this.m_List), "Center");
    add(jPanel2, "South");
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_Exp = paramExperiment;
    this.m_AddBut.setEnabled(true);
    this.m_List.setModel(this.m_AlgorithmListModel);
    this.m_List.setCellRenderer(new ObjectCellRenderer(this));
    this.m_AlgorithmListModel.removeAllElements();
    if (this.m_Exp.getPropertyArray() instanceof Classifier[]) {
      Classifier[] arrayOfClassifier = (Classifier[])this.m_Exp.getPropertyArray();
      for (byte b = 0; b < arrayOfClassifier.length; b++)
        this.m_AlgorithmListModel.addElement(arrayOfClassifier[b]); 
    } 
    this.m_EditBut.setEnabled((this.m_AlgorithmListModel.size() > 0));
    this.m_DeleteBut.setEnabled((this.m_AlgorithmListModel.size() > 0));
    this.m_LoadOptionsBut.setEnabled((this.m_AlgorithmListModel.size() > 0));
    this.m_SaveOptionsBut.setEnabled((this.m_AlgorithmListModel.size() > 0));
  }
  
  private void addNewAlgorithm(Classifier paramClassifier) {
    if (!this.m_Editing) {
      this.m_AlgorithmListModel.addElement(paramClassifier);
    } else {
      this.m_AlgorithmListModel.setElementAt(paramClassifier, this.m_List.getSelectedIndex());
    } 
    Classifier[] arrayOfClassifier = new Classifier[this.m_AlgorithmListModel.size()];
    for (byte b = 0; b < arrayOfClassifier.length; b++)
      arrayOfClassifier[b] = this.m_AlgorithmListModel.elementAt(b); 
    this.m_Exp.setPropertyArray(arrayOfClassifier);
    this.m_Editing = false;
  }
  
  private void setButtons(ListSelectionEvent paramListSelectionEvent) {
    if (paramListSelectionEvent.getSource() == this.m_List) {
      this.m_DeleteBut.setEnabled((this.m_List.getSelectedIndex() > -1));
      this.m_AddBut.setEnabled(true);
      this.m_EditBut.setEnabled(((this.m_List.getSelectedIndices()).length == 1));
      this.m_LoadOptionsBut.setEnabled(((this.m_List.getSelectedIndices()).length == 1));
      this.m_SaveOptionsBut.setEnabled(((this.m_List.getSelectedIndices()).length == 1));
    } 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (paramActionEvent.getSource() == this.m_AddBut) {
      this.m_Editing = false;
      if (this.m_PD == null) {
        int i = (getLocationOnScreen()).x;
        int j = (getLocationOnScreen()).y;
        this.m_PD = new PropertyDialog((PropertyEditor)this.m_ClassifierEditor, i, j);
      } else {
        this.m_PD.setVisible(true);
      } 
    } else if (paramActionEvent.getSource() == this.m_EditBut) {
      if (this.m_List.getSelectedValue() != null) {
        this.m_Editing = true;
        if (this.m_PD == null) {
          int i = (getLocationOnScreen()).x;
          int j = (getLocationOnScreen()).y;
          this.m_PD = new PropertyDialog((PropertyEditor)this.m_ClassifierEditor, i, j);
        } else {
          this.m_PD.setVisible(true);
        } 
        this.m_PD.getEditor().setValue(this.m_List.getSelectedValue());
      } 
    } else if (paramActionEvent.getSource() == this.m_DeleteBut) {
      int[] arrayOfInt = this.m_List.getSelectedIndices();
      if (arrayOfInt != null)
        for (int i = arrayOfInt.length - 1; i >= 0; i--) {
          int j = arrayOfInt[i];
          this.m_AlgorithmListModel.removeElementAt(j);
          if (this.m_Exp.getDatasets().size() > j) {
            this.m_List.setSelectedIndex(j);
          } else {
            this.m_List.setSelectedIndex(j - 1);
          } 
        }  
      if (this.m_List.getSelectedIndex() == -1) {
        this.m_EditBut.setEnabled(false);
        this.m_DeleteBut.setEnabled(false);
        this.m_LoadOptionsBut.setEnabled(false);
        this.m_SaveOptionsBut.setEnabled(false);
      } 
      Classifier[] arrayOfClassifier = new Classifier[this.m_AlgorithmListModel.size()];
      for (byte b = 0; b < arrayOfClassifier.length; b++)
        arrayOfClassifier[b] = this.m_AlgorithmListModel.elementAt(b); 
      this.m_Exp.setPropertyArray(arrayOfClassifier);
    } else if (paramActionEvent.getSource() == this.m_LoadOptionsBut) {
      if (this.m_List.getSelectedValue() != null) {
        int i = this.m_FileChooser.showOpenDialog(this);
        if (i == 0)
          try {
            File file = this.m_FileChooser.getSelectedFile();
            if (!file.getAbsolutePath().toLowerCase().endsWith(".xml"))
              file = new File(file.getAbsolutePath() + ".xml"); 
            XMLClassifier xMLClassifier = new XMLClassifier();
            Classifier classifier = (Classifier)xMLClassifier.read(file);
            this.m_AlgorithmListModel.setElementAt(classifier, this.m_List.getSelectedIndex());
          } catch (Exception exception) {
            exception.printStackTrace();
          }  
      } 
    } else if (paramActionEvent.getSource() == this.m_SaveOptionsBut && this.m_List.getSelectedValue() != null) {
      int i = this.m_FileChooser.showSaveDialog(this);
      if (i == 0)
        try {
          File file = this.m_FileChooser.getSelectedFile();
          if (!file.getAbsolutePath().toLowerCase().endsWith(".xml"))
            file = new File(file.getAbsolutePath() + ".xml"); 
          XMLClassifier xMLClassifier = new XMLClassifier();
          xMLClassifier.write(file, this.m_List.getSelectedValue());
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
    } 
  }
  
  protected Object copyObject(Object paramObject) {
    Object object = null;
    try {
      SerializedObject serializedObject = new SerializedObject(paramObject);
      object = serializedObject.getObject();
    } catch (Exception exception) {
      System.err.println("AlgorithmListPanel: Problem copying object");
      System.err.println(exception);
    } 
    return object;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Algorithm List Editor");
      jFrame.getContentPane().setLayout(new BorderLayout());
      AlgorithmListPanel algorithmListPanel = new AlgorithmListPanel();
      jFrame.getContentPane().add(algorithmListPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
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
      algorithmListPanel.setExperiment(new Experiment());
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
  
  public class ObjectCellRenderer extends DefaultListCellRenderer {
    private final AlgorithmListPanel this$0;
    
    public ObjectCellRenderer(AlgorithmListPanel this$0) {
      this.this$0 = this$0;
    }
    
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      Component component = super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      String str = param1Object.getClass().getName();
      int i = str.lastIndexOf('.');
      if (i != -1)
        str = str.substring(i + 1); 
      if (param1Object instanceof OptionHandler)
        str = str + " " + Utils.joinOptions(((OptionHandler)param1Object).getOptions()); 
      setText(str);
      return component;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\AlgorithmListPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */