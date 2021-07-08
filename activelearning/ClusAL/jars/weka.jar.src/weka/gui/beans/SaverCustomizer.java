package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorManager;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.core.SelectedTag;
import weka.core.converters.DatabaseConverter;
import weka.core.converters.DatabaseSaver;
import weka.core.converters.Loader;
import weka.core.converters.Saver;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.FileEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertySheetPanel;
import weka.gui.SelectedTagEditor;

public class SaverCustomizer extends JPanel implements Customizer, CustomizerCloseRequester {
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private Saver m_dsSaver;
  
  private PropertySheetPanel m_SaverEditor = new PropertySheetPanel();
  
  private JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  private JFrame m_parentFrame;
  
  private JTextField m_dbaseURLText;
  
  private JTextField m_userNameText;
  
  private JPasswordField m_passwordText;
  
  private JTextField m_tableText;
  
  private JComboBox m_idBox;
  
  private JComboBox m_tabBox;
  
  private JTextField m_prefixText;
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public SaverCustomizer() {
    try {
      this.m_SaverEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
            private final SaverCustomizer this$0;
            
            public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
              this.this$0.repaint();
              if (this.this$0.m_dsSaver != null) {
                System.err.println("Property change!!");
                this.this$0.m_dsSaver.setSaver(this.this$0.m_dsSaver.getSaver());
              } 
            }
          });
      repaint();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    setLayout(new BorderLayout());
    this.m_fileChooser.setDialogType(1);
    this.m_fileChooser.setFileSelectionMode(1);
    this.m_fileChooser.setApproveButtonText("Select directory and prefix");
    this.m_fileChooser.addActionListener(new ActionListener(this) {
          private final SaverCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (param1ActionEvent.getActionCommand().equals("ApproveSelection"))
              try {
                this.this$0.m_dsSaver.getSaver().setFilePrefix(this.this$0.m_prefixText.getText());
                this.this$0.m_dsSaver.getSaver().setDir(this.this$0.m_fileChooser.getSelectedFile().getAbsolutePath());
                this.this$0.m_dsSaver.setSaver(this.this$0.m_dsSaver.getSaver());
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
            if (this.this$0.m_parentFrame != null)
              this.this$0.m_parentFrame.dispose(); 
          }
        });
  }
  
  public void setParentFrame(JFrame paramJFrame) {
    this.m_parentFrame = paramJFrame;
  }
  
  private void setUpOther() {
    removeAll();
    add((Component)this.m_SaverEditor, "Center");
    validate();
    repaint();
  }
  
  private void setUpDatabase() {
    removeAll();
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new GridLayout(7, 1));
    this.m_dbaseURLText = new JTextField(((DatabaseConverter)this.m_dsSaver.getSaver()).getUrl(), 50);
    JLabel jLabel1 = new JLabel(" Database URL:          ", 2);
    jLabel1.setFont(new Font("Monospaced", 0, 12));
    this.m_userNameText = new JTextField(((DatabaseConverter)this.m_dsSaver.getSaver()).getUser(), 50);
    JLabel jLabel2 = new JLabel(" Username:              ", 2);
    jLabel2.setFont(new Font("Monospaced", 0, 12));
    this.m_passwordText = new JPasswordField(50);
    JLabel jLabel3 = new JLabel(" Password:              ", 2);
    jLabel3.setFont(new Font("Monospaced", 0, 12));
    this.m_tableText = new JTextField(((DatabaseSaver)this.m_dsSaver.getSaver()).getTableName(), 50);
    this.m_tableText.setEditable(!((DatabaseSaver)this.m_dsSaver.getSaver()).getRelationForTableName());
    JLabel jLabel4 = new JLabel(" Table Name:            ", 2);
    jLabel4.setFont(new Font("Monospaced", 0, 12));
    this.m_tabBox = new JComboBox();
    this.m_tabBox.addItem(new Boolean(true));
    this.m_tabBox.addItem(new Boolean(false));
    if (!((DatabaseSaver)this.m_dsSaver.getSaver()).getRelationForTableName()) {
      this.m_tabBox.setSelectedIndex(1);
    } else {
      this.m_tabBox.setSelectedIndex(0);
    } 
    this.m_tabBox.addItemListener(new ItemListener(this) {
          private final SaverCustomizer this$0;
          
          public void itemStateChanged(ItemEvent param1ItemEvent) {
            this.this$0.m_tableText.setEditable(!((Boolean)this.this$0.m_tabBox.getSelectedItem()).booleanValue());
          }
        });
    JLabel jLabel5 = new JLabel(" Use relation name:     ", 2);
    jLabel5.setFont(new Font("Monospaced", 0, 12));
    this.m_idBox = new JComboBox();
    this.m_idBox.addItem(new Boolean(true));
    this.m_idBox.addItem(new Boolean(false));
    if (!((DatabaseSaver)this.m_dsSaver.getSaver()).getAutoKeyGeneration()) {
      this.m_idBox.setSelectedIndex(1);
    } else {
      this.m_idBox.setSelectedIndex(0);
    } 
    JLabel jLabel6 = new JLabel(" Automatic primary key: ", 2);
    jLabel6.setFont(new Font("Monospaced", 0, 12));
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new FlowLayout(0));
    jPanel2.add(jLabel1);
    jPanel2.add(this.m_dbaseURLText);
    jPanel1.add(jPanel2);
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new FlowLayout(0));
    jPanel3.add(jLabel2);
    jPanel3.add(this.m_userNameText);
    jPanel1.add(jPanel3);
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new FlowLayout(0));
    jPanel4.add(jLabel3);
    jPanel4.add(this.m_passwordText);
    jPanel1.add(jPanel4);
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new FlowLayout(0));
    jPanel5.add(jLabel5);
    jPanel5.add(this.m_tabBox);
    jPanel1.add(jPanel5);
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new FlowLayout(0));
    jPanel6.add(jLabel4);
    jPanel6.add(this.m_tableText);
    jPanel1.add(jPanel6);
    JPanel jPanel7 = new JPanel();
    jPanel7.setLayout(new FlowLayout(0));
    jPanel7.add(jLabel6);
    jPanel7.add(this.m_idBox);
    jPanel1.add(jPanel7);
    JPanel jPanel8 = new JPanel();
    jPanel8.setLayout(new FlowLayout());
    JButton jButton1;
    jPanel8.add(jButton1 = new JButton("OK"));
    JButton jButton2;
    jPanel8.add(jButton2 = new JButton("Cancel"));
    jButton1.addActionListener(new ActionListener(this) {
          private final SaverCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            ((DatabaseSaver)this.this$0.m_dsSaver.getSaver()).resetStructure();
            ((DatabaseConverter)this.this$0.m_dsSaver.getSaver()).setUrl(this.this$0.m_dbaseURLText.getText());
            ((DatabaseConverter)this.this$0.m_dsSaver.getSaver()).setUser(this.this$0.m_userNameText.getText());
            ((DatabaseConverter)this.this$0.m_dsSaver.getSaver()).setPassword(new String(this.this$0.m_passwordText.getPassword()));
            if (!((Boolean)this.this$0.m_tabBox.getSelectedItem()).booleanValue())
              ((DatabaseSaver)this.this$0.m_dsSaver.getSaver()).setTableName(this.this$0.m_tableText.getText()); 
            ((DatabaseSaver)this.this$0.m_dsSaver.getSaver()).setAutoKeyGeneration(((Boolean)this.this$0.m_idBox.getSelectedItem()).booleanValue());
            ((DatabaseSaver)this.this$0.m_dsSaver.getSaver()).setRelationForTableName(((Boolean)this.this$0.m_tabBox.getSelectedItem()).booleanValue());
            if (this.this$0.m_parentFrame != null)
              this.this$0.m_parentFrame.dispose(); 
          }
        });
    jButton2.addActionListener(new ActionListener(this) {
          private final SaverCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_parentFrame != null)
              this.this$0.m_parentFrame.dispose(); 
          }
        });
    jPanel1.add(jPanel8);
    JPanel jPanel9 = this.m_SaverEditor.getAboutPanel();
    if (jPanel9 != null)
      add(jPanel9, "North"); 
    add(jPanel1, "South");
  }
  
  public void setUpFile() {
    removeAll();
    this.m_fileChooser.setFileFilter(new FileFilter(this) {
          private final SaverCustomizer this$0;
          
          public boolean accept(File param1File) {
            return param1File.isDirectory();
          }
          
          public String getDescription() {
            return "Directory";
          }
        });
    this.m_fileChooser.setAcceptAllFileFilterUsed(false);
    try {
      if (!this.m_dsSaver.getSaver().retrieveDir().equals(""))
        this.m_fileChooser.setCurrentDirectory(new File(this.m_dsSaver.getSaver().retrieveDir())); 
    } catch (Exception exception) {
      System.out.println(exception);
    } 
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new BorderLayout());
    try {
      this.m_prefixText = new JTextField(this.m_dsSaver.getSaver().filePrefix(), 25);
      JLabel jLabel = new JLabel(" Prefix for file name:", 2);
      JPanel jPanel = new JPanel();
      jPanel.setLayout(new FlowLayout(0));
      jPanel.add(jLabel);
      jPanel.add(this.m_prefixText);
      jPanel1.add(jPanel, "South");
    } catch (Exception exception) {}
    JPanel jPanel2 = this.m_SaverEditor.getAboutPanel();
    if (jPanel2 != null)
      jPanel1.add(jPanel2, "North"); 
    add(jPanel1, "North");
    add(this.m_fileChooser, "Center");
  }
  
  public void setObject(Object paramObject) {
    this.m_dsSaver = (Saver)paramObject;
    this.m_SaverEditor.setTarget(this.m_dsSaver.getSaver());
    if (this.m_dsSaver.getSaver() instanceof DatabaseConverter) {
      setUpDatabase();
    } else if (this.m_dsSaver.getSaver() instanceof weka.core.converters.FileSourcedConverter) {
      setUpFile();
    } else {
      setUpOther();
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  static {
    PropertyEditorManager.registerEditor(Saver.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(Loader.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASSearch.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASEvaluation.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor((array$Lweka$classifiers$Classifier == null) ? (array$Lweka$classifiers$Classifier = class$("[Lweka.classifiers.Classifier;")) : array$Lweka$classifiers$Classifier, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(CostMatrix.class, CostMatrixEditor.class);
    PropertyEditorManager.registerEditor(File.class, FileEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\SaverCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */