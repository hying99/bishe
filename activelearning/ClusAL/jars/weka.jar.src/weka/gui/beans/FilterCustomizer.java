package weka.gui.beans;

import java.awt.BorderLayout;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorManager;
import javax.swing.JPanel;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.gui.CostMatrixEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.SelectedTagEditor;

public class FilterCustomizer extends JPanel implements Customizer {
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private Filter m_filter;
  
  private GenericObjectEditor m_filterEditor = new GenericObjectEditor(true);
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public FilterCustomizer() {
    try {
      this.m_filterEditor.setClassType(Filter.class);
      this.m_filterEditor.setValue(new Add());
      this.m_filterEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
            private final FilterCustomizer this$0;
            
            public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
              this.this$0.repaint();
              if (this.this$0.m_filter != null) {
                Filter filter = (Filter)this.this$0.m_filterEditor.getValue();
                this.this$0.m_filter.setFilter(filter);
              } 
            }
          });
      repaint();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    setLayout(new BorderLayout());
    add(this.m_filterEditor.getCustomEditor(), "Center");
  }
  
  public void setObject(Object paramObject) {
    this.m_filter = (Filter)paramObject;
    this.m_filterEditor.setValue(this.m_filter.getFilter());
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\FilterCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */