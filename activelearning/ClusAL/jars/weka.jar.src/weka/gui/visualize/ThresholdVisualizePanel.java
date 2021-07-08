package weka.gui.visualize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import weka.core.Instances;

public class ThresholdVisualizePanel extends VisualizePanel {
  private String m_ROCString = "";
  
  private String m_savePanelBorderText;
  
  public void setROCString(String paramString) {
    this.m_ROCString = paramString;
  }
  
  public String getROCString() {
    return this.m_ROCString;
  }
  
  public void setUpComboBoxes(Instances paramInstances) {
    super.setUpComboBoxes(paramInstances);
    this.m_XCombo.addActionListener(new ActionListener(this) {
          private final ThresholdVisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setBorderText();
          }
        });
    this.m_YCombo.addActionListener(new ActionListener(this) {
          private final ThresholdVisualizePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setBorderText();
          }
        });
    TitledBorder titledBorder = (TitledBorder)this.m_plotSurround.getBorder();
    this.m_savePanelBorderText = titledBorder.getTitle();
    setBorderText();
  }
  
  private void setBorderText() {
    String str1 = this.m_XCombo.getSelectedItem().toString();
    String str2 = this.m_YCombo.getSelectedItem().toString();
    if (str1.equals("X: False Positive Rate (Num)") && str2.equals("Y: True Positive Rate (Num)")) {
      this.m_plotSurround.setBorder(BorderFactory.createTitledBorder(this.m_savePanelBorderText + " " + this.m_ROCString));
    } else {
      this.m_plotSurround.setBorder(BorderFactory.createTitledBorder(this.m_savePanelBorderText));
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\ThresholdVisualizePanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */