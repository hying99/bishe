package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorManager;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertySheetPanel;
import weka.gui.SelectedTagEditor;

public class ClassifierCustomizer extends JPanel implements Customizer {
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private Classifier m_dsClassifier;
  
  private PropertySheetPanel m_ClassifierEditor = new PropertySheetPanel();
  
  private JPanel m_incrementalPanel = new JPanel();
  
  private JCheckBox m_updateIncrementalClassifier = new JCheckBox("Update classifier on incoming instance stream");
  
  private boolean m_panelVisible = false;
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public ClassifierCustomizer() {
    this.m_updateIncrementalClassifier.setToolTipText("Train the classifier on each individual incoming streamed instance.");
    this.m_updateIncrementalClassifier.addActionListener(new ActionListener(this) {
          private final ClassifierCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_dsClassifier != null)
              this.this$0.m_dsClassifier.setUpdateIncrementalClassifier(this.this$0.m_updateIncrementalClassifier.isSelected()); 
          }
        });
    this.m_incrementalPanel.add(this.m_updateIncrementalClassifier);
    setLayout(new BorderLayout());
    add((Component)this.m_ClassifierEditor, "Center");
  }
  
  private void checkOnClassifierType() {
    Classifier classifier = this.m_dsClassifier.getClassifier();
    if (classifier instanceof weka.classifiers.UpdateableClassifier && this.m_dsClassifier.hasIncomingStreamInstances()) {
      if (!this.m_panelVisible) {
        add(this.m_incrementalPanel, "South");
        this.m_panelVisible = true;
      } 
    } else if (this.m_panelVisible) {
      remove(this.m_incrementalPanel);
      this.m_panelVisible = false;
    } 
  }
  
  public void setObject(Object paramObject) {
    this.m_dsClassifier = (Classifier)paramObject;
    this.m_ClassifierEditor.setTarget(this.m_dsClassifier.getClassifier());
    this.m_updateIncrementalClassifier.setSelected(this.m_dsClassifier.getUpdateIncrementalClassifier());
    checkOnClassifierType();
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  static {
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASSearch.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(ASEvaluation.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor((array$Lweka$classifiers$Classifier == null) ? (array$Lweka$classifiers$Classifier = class$("[Lweka.classifiers.Classifier;")) : array$Lweka$classifiers$Classifier, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(CostMatrix.class, CostMatrixEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassifierCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */