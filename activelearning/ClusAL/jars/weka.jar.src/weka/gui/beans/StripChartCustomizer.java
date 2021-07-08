package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import weka.gui.PropertySheetPanel;

public class StripChartCustomizer extends JPanel implements Customizer {
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private PropertySheetPanel m_cvEditor = new PropertySheetPanel();
  
  public StripChartCustomizer() {
    setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    setLayout(new BorderLayout());
    add((Component)this.m_cvEditor, "Center");
    add(new JLabel("StripChartCustomizer"), "North");
  }
  
  public void setObject(Object paramObject) {
    this.m_cvEditor.setTarget(paramObject);
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\StripChartCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */