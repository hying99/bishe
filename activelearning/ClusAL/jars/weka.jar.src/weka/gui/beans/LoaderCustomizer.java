package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorManager;
import java.io.File;
import javax.swing.JButton;
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
import weka.core.converters.DatabaseLoader;
import weka.core.converters.FileSourcedConverter;
import weka.core.converters.Loader;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.ExtensionFileFilter;
import weka.gui.FileEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertySheetPanel;
import weka.gui.SelectedTagEditor;

public class LoaderCustomizer extends JPanel implements Customizer, CustomizerCloseRequester {
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private Loader m_dsLoader;
  
  private PropertySheetPanel m_LoaderEditor = new PropertySheetPanel();
  
  private JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  private JFrame m_parentFrame;
  
  private JTextField m_dbaseURLText;
  
  private JTextField m_userNameText;
  
  private JTextField m_queryText;
  
  private JTextField m_keyText;
  
  private JPasswordField m_passwordText;
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public LoaderCustomizer() {
    try {
      this.m_LoaderEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
            private final LoaderCustomizer this$0;
            
            public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
              this.this$0.repaint();
              if (this.this$0.m_dsLoader != null) {
                System.err.println("Property change!!");
                this.this$0.m_dsLoader.setLoader(this.this$0.m_dsLoader.getLoader());
              } 
            }
          });
      repaint();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    setLayout(new BorderLayout());
    this.m_fileChooser.setDialogType(0);
    this.m_fileChooser.addActionListener(new ActionListener(this) {
          private final LoaderCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (param1ActionEvent.getActionCommand().equals("ApproveSelection"))
              try {
                ((FileSourcedConverter)this.this$0.m_dsLoader.getLoader()).setFile(this.this$0.m_fileChooser.getSelectedFile());
                this.this$0.m_dsLoader.setLoader(this.this$0.m_dsLoader.getLoader());
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
    add((Component)this.m_LoaderEditor, "Center");
    validate();
    repaint();
  }
  
  private void setUpDatabase() {
    removeAll();
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new GridLayout(6, 1));
    this.m_dbaseURLText = new JTextField(((DatabaseConverter)this.m_dsLoader.getLoader()).getUrl(), 50);
    JLabel jLabel1 = new JLabel(" Database URL:", 2);
    jLabel1.setFont(new Font("Monospaced", 0, 12));
    this.m_userNameText = new JTextField(((DatabaseConverter)this.m_dsLoader.getLoader()).getUser(), 50);
    JLabel jLabel2 = new JLabel(" Username:    ", 2);
    jLabel2.setFont(new Font("Monospaced", 0, 12));
    this.m_passwordText = new JPasswordField(50);
    JLabel jLabel3 = new JLabel(" Password:    ", 2);
    jLabel3.setFont(new Font("Monospaced", 0, 12));
    this.m_queryText = new JTextField(((DatabaseLoader)this.m_dsLoader.getLoader()).getQuery(), 50);
    JLabel jLabel4 = new JLabel(" Query:       ", 2);
    jLabel4.setFont(new Font("Monospaced", 0, 12));
    this.m_keyText = new JTextField(((DatabaseLoader)this.m_dsLoader.getLoader()).getKeys(), 50);
    JLabel jLabel5 = new JLabel(" Key columns: ", 2);
    jLabel5.setFont(new Font("Monospaced", 0, 12));
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
    jPanel5.add(jLabel4);
    jPanel5.add(this.m_queryText);
    jPanel1.add(jPanel5);
    JPanel jPanel6 = new JPanel();
    jPanel6.setLayout(new FlowLayout(0));
    jPanel6.add(jLabel5);
    jPanel6.add(this.m_keyText);
    jPanel1.add(jPanel6);
    JPanel jPanel7 = new JPanel();
    jPanel7.setLayout(new FlowLayout());
    JButton jButton1;
    jPanel7.add(jButton1 = new JButton("OK"));
    JButton jButton2;
    jPanel7.add(jButton2 = new JButton("Cancel"));
    jButton1.addActionListener(new ActionListener(this) {
          private final LoaderCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            ((DatabaseLoader)this.this$0.m_dsLoader.getLoader()).resetStructure();
            ((DatabaseConverter)this.this$0.m_dsLoader.getLoader()).setUrl(this.this$0.m_dbaseURLText.getText());
            ((DatabaseConverter)this.this$0.m_dsLoader.getLoader()).setUser(this.this$0.m_userNameText.getText());
            ((DatabaseConverter)this.this$0.m_dsLoader.getLoader()).setPassword(new String(this.this$0.m_passwordText.getPassword()));
            ((DatabaseLoader)this.this$0.m_dsLoader.getLoader()).setQuery(this.this$0.m_queryText.getText());
            ((DatabaseLoader)this.this$0.m_dsLoader.getLoader()).setKeys(this.this$0.m_keyText.getText());
            try {
              this.this$0.m_dsLoader.notifyStructureAvailable(((DatabaseLoader)this.this$0.m_dsLoader.getLoader()).getStructure());
              this.this$0.m_dsLoader.setDB(true);
            } catch (Exception exception) {}
            if (this.this$0.m_parentFrame != null)
              this.this$0.m_parentFrame.dispose(); 
          }
        });
    jButton2.addActionListener(new ActionListener(this) {
          private final LoaderCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_parentFrame != null)
              this.this$0.m_parentFrame.dispose(); 
          }
        });
    jPanel1.add(jPanel7);
    JPanel jPanel8 = this.m_LoaderEditor.getAboutPanel();
    if (jPanel8 != null)
      add(jPanel8, "North"); 
    add(jPanel1, "South");
  }
  
  public void setUpFile() {
    removeAll();
    this.m_fileChooser.setSelectedFile(((FileSourcedConverter)this.m_dsLoader.getLoader()).retrieveFile());
    ExtensionFileFilter extensionFileFilter = new ExtensionFileFilter(((FileSourcedConverter)this.m_dsLoader.getLoader()).getFileExtension(), ((FileSourcedConverter)this.m_dsLoader.getLoader()).getFileDescription());
    this.m_fileChooser.addChoosableFileFilter((FileFilter)extensionFileFilter);
    JPanel jPanel = this.m_LoaderEditor.getAboutPanel();
    if (jPanel != null)
      add(jPanel, "North"); 
    add(this.m_fileChooser, "Center");
  }
  
  public void setObject(Object paramObject) {
    this.m_dsLoader = (Loader)paramObject;
    this.m_LoaderEditor.setTarget(this.m_dsLoader.getLoader());
    if (this.m_dsLoader.getLoader() instanceof FileSourcedConverter) {
      setUpFile();
    } else if (this.m_dsLoader.getLoader() instanceof DatabaseConverter) {
      setUpDatabase();
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\LoaderCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */