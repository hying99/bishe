package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import weka.core.Instances;
import weka.gui.PropertySheetPanel;

public class ClassAssignerCustomizer extends JPanel implements Customizer, CustomizerClosingListener, DataFormatListener {
  private boolean m_displayColNames = false;
  
  private ClassAssigner m_classAssigner;
  
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private PropertySheetPanel m_caEditor = new PropertySheetPanel();
  
  private JComboBox m_ClassCombo = new JComboBox();
  
  private JPanel m_holderP = new JPanel();
  
  public ClassAssignerCustomizer() {
    setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    setLayout(new BorderLayout());
    add(new JLabel("ClassAssignerCustomizer"), "North");
    this.m_holderP.setLayout(new BorderLayout());
    this.m_holderP.setBorder(BorderFactory.createTitledBorder("Choose class attribute"));
    this.m_holderP.add(this.m_ClassCombo, "Center");
    this.m_ClassCombo.addActionListener(new ActionListener(this) {
          private final ClassAssignerCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_classAssigner != null && this.this$0.m_displayColNames == true)
              this.this$0.m_classAssigner.setClassColumn("" + (this.this$0.m_ClassCombo.getSelectedIndex() + 1)); 
          }
        });
    add((Component)this.m_caEditor, "Center");
  }
  
  private void setUpStandardSelection() {
    if (this.m_displayColNames == true) {
      remove(this.m_holderP);
      this.m_caEditor.setTarget(this.m_classAssigner);
      add((Component)this.m_caEditor, "Center");
      this.m_displayColNames = false;
    } 
    validate();
    repaint();
  }
  
  private void setUpColumnSelection(Instances paramInstances) {
    if (!this.m_displayColNames)
      remove((Component)this.m_caEditor); 
    int i = paramInstances.classIndex();
    if (i < 0)
      i = 0; 
    String[] arrayOfString = new String[paramInstances.numAttributes()];
    for (byte b = 0; b < arrayOfString.length; b++) {
      String str = "";
      switch (paramInstances.attribute(b).type()) {
        case 1:
          str = "(Nom) ";
          break;
        case 0:
          str = "(Num) ";
          break;
        case 2:
          str = "(Str) ";
          break;
        default:
          str = "(???) ";
          break;
      } 
      arrayOfString[b] = str + paramInstances.attribute(b).name();
    } 
    this.m_ClassCombo.setModel(new DefaultComboBoxModel(arrayOfString));
    if (arrayOfString.length > 0)
      this.m_ClassCombo.setSelectedIndex(i); 
    if (!this.m_displayColNames) {
      add(this.m_holderP, "Center");
      this.m_displayColNames = true;
    } 
    validate();
    repaint();
  }
  
  public void setObject(Object paramObject) {
    if (this.m_classAssigner != (ClassAssigner)paramObject) {
      if (this.m_classAssigner != null)
        this.m_classAssigner.removeDataFormatListener(this); 
      this.m_classAssigner = (ClassAssigner)paramObject;
      this.m_classAssigner.addDataFormatListener(this);
      this.m_caEditor.setTarget(this.m_classAssigner);
      if (this.m_classAssigner.getConnectedFormat() != null)
        setUpColumnSelection(this.m_classAssigner.getConnectedFormat()); 
    } 
  }
  
  public void customizerClosing() {
    if (this.m_classAssigner != null) {
      System.err.println("Customizer deregistering with class assigner");
      this.m_classAssigner.removeDataFormatListener(this);
    } 
  }
  
  public void newDataFormat(DataSetEvent paramDataSetEvent) {
    if (paramDataSetEvent.getDataSet() != null) {
      setUpColumnSelection(this.m_classAssigner.getConnectedFormat());
    } else {
      setUpStandardSelection();
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassAssignerCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */