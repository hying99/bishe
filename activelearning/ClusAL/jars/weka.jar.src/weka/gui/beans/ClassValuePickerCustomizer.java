package weka.gui.beans;

import java.awt.BorderLayout;
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

public class ClassValuePickerCustomizer extends JPanel implements Customizer, CustomizerClosingListener, DataFormatListener {
  private boolean m_displayValNames = false;
  
  private ClassValuePicker m_classValuePicker;
  
  private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
  
  private JComboBox m_ClassValueCombo = new JComboBox();
  
  private JPanel m_holderP = new JPanel();
  
  private JLabel m_messageLabel = new JLabel("No customization possible at present.");
  
  public ClassValuePickerCustomizer() {
    setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    setLayout(new BorderLayout());
    add(new JLabel("ClassValuePickerCustomizer"), "North");
    this.m_holderP.setLayout(new BorderLayout());
    this.m_holderP.setBorder(BorderFactory.createTitledBorder("Choose class value"));
    this.m_holderP.add(this.m_ClassValueCombo, "Center");
    this.m_ClassValueCombo.addActionListener(new ActionListener(this) {
          private final ClassValuePickerCustomizer this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_classValuePicker != null)
              this.this$0.m_classValuePicker.setClassValueIndex(this.this$0.m_ClassValueCombo.getSelectedIndex()); 
          }
        });
    add(this.m_messageLabel, "Center");
  }
  
  private void setUpNoCustPossible() {
    if (this.m_displayValNames == true) {
      remove(this.m_holderP);
      add(this.m_messageLabel, "Center");
      this.m_displayValNames = false;
    } 
    validate();
    repaint();
  }
  
  private void setUpValueSelection(Instances paramInstances) {
    if (paramInstances.classIndex() < 0 || paramInstances.classAttribute().isNumeric())
      return; 
    if (!this.m_displayValNames)
      remove(this.m_messageLabel); 
    int i = this.m_classValuePicker.getClassValueIndex();
    String[] arrayOfString = new String[paramInstances.classAttribute().numValues()];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = paramInstances.classAttribute().value(b); 
    this.m_ClassValueCombo.setModel(new DefaultComboBoxModel(arrayOfString));
    if (arrayOfString.length > 0 && i < arrayOfString.length)
      this.m_ClassValueCombo.setSelectedIndex(i); 
    if (!this.m_displayValNames) {
      add(this.m_holderP, "Center");
      this.m_displayValNames = true;
    } 
    validate();
    repaint();
  }
  
  public void setObject(Object paramObject) {
    if (this.m_classValuePicker != (ClassValuePicker)paramObject) {
      if (this.m_classValuePicker != null)
        this.m_classValuePicker.removeDataFormatListener(this); 
      this.m_classValuePicker = (ClassValuePicker)paramObject;
      this.m_classValuePicker.addDataFormatListener(this);
      if (this.m_classValuePicker.getConnectedFormat() != null)
        setUpValueSelection(this.m_classValuePicker.getConnectedFormat()); 
    } 
  }
  
  public void customizerClosing() {
    if (this.m_classValuePicker != null) {
      System.err.println("Customizer deregistering with class value picker");
      this.m_classValuePicker.removeDataFormatListener(this);
    } 
  }
  
  public void newDataFormat(DataSetEvent paramDataSetEvent) {
    if (paramDataSetEvent.getDataSet() != null) {
      setUpValueSelection(this.m_classValuePicker.getConnectedFormat());
    } else {
      setUpNoCustPossible();
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_pcSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassValuePickerCustomizer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */