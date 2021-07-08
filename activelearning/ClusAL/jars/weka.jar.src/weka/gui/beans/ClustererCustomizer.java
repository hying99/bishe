package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorManager;
import javax.swing.JPanel;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.CostMatrix;
import weka.clusterers.Clusterer;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.gui.CostMatrixEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertySheetPanel;
import weka.gui.SelectedTagEditor;

public class ClustererCustomizer extends JPanel implements Customizer {
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private Clusterer m_dsClusterer;
  
  private PropertySheetPanel m_ClustererEditor = new PropertySheetPanel();
  
  static Class array$Ljava$lang$Object;
  
  public ClustererCustomizer() {
    setLayout(new BorderLayout());
    add((Component)this.m_ClustererEditor, "Center");
  }
  
  public void setObject(Object paramObject) {
    this.m_dsClusterer = (Clusterer)paramObject;
    this.m_ClustererEditor.setTarget(this.m_dsClusterer.getClusterer());
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
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(CostMatrix.class, CostMatrixEditor.class);
    PropertyEditorManager.registerEditor(Clusterer.class, GenericObjectEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClustererCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */