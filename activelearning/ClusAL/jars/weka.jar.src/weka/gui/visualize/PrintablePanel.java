package weka.gui.visualize;

import java.util.Hashtable;
import javax.swing.JPanel;

public class PrintablePanel extends JPanel implements PrintableHandler {
  protected PrintableComponent m_Printer = null;
  
  public PrintablePanel() {
    this.m_Printer = new PrintableComponent(this);
  }
  
  public Hashtable getWriters() {
    return this.m_Printer.getWriters();
  }
  
  public JComponentWriter getWriter(String paramString) {
    return this.m_Printer.getWriter(paramString);
  }
  
  public void setSaveDialogTitle(String paramString) {
    this.m_Printer.setSaveDialogTitle(paramString);
  }
  
  public String getSaveDialogTitle() {
    return this.m_Printer.getSaveDialogTitle();
  }
  
  public void setScale(double paramDouble1, double paramDouble2) {
    this.m_Printer.setScale(paramDouble1, paramDouble2);
  }
  
  public double getXScale() {
    return this.m_Printer.getXScale();
  }
  
  public double getYScale() {
    return this.m_Printer.getYScale();
  }
  
  public void saveComponent() {
    this.m_Printer.saveComponent();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\PrintablePanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */