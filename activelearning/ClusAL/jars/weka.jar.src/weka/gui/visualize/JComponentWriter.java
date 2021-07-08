package weka.gui.visualize;

import java.io.File;
import javax.swing.JComponent;

public abstract class JComponentWriter {
  protected static final boolean DEBUG = false;
  
  private JComponent component;
  
  private File outputFile;
  
  protected double m_xScale;
  
  protected double m_yScale;
  
  protected boolean m_ScalingEnabled;
  
  public JComponentWriter() {
    this(null);
  }
  
  public JComponentWriter(JComponent paramJComponent) {
    this(paramJComponent, null);
  }
  
  public JComponentWriter(JComponent paramJComponent, File paramFile) {
    this.component = paramJComponent;
    this.outputFile = paramFile;
    initialize();
  }
  
  protected void initialize() {
    this.m_xScale = 1.0D;
    this.m_yScale = 1.0D;
    this.m_ScalingEnabled = true;
  }
  
  public void setComponent(JComponent paramJComponent) {
    this.component = paramJComponent;
  }
  
  public JComponent getComponent() {
    return this.component;
  }
  
  public void setFile(File paramFile) {
    this.outputFile = paramFile;
  }
  
  public File getFile() {
    return this.outputFile;
  }
  
  public String getDescription() {
    return null;
  }
  
  public String getExtension() {
    return null;
  }
  
  public boolean getScalingEnabled() {
    return this.m_ScalingEnabled;
  }
  
  public void setScalingEnabled(boolean paramBoolean) {
    this.m_ScalingEnabled = paramBoolean;
  }
  
  public void setScale(double paramDouble1, double paramDouble2) {
    if (getScalingEnabled()) {
      this.m_xScale = paramDouble1;
      this.m_yScale = paramDouble2;
    } else {
      this.m_xScale = 1.0D;
      this.m_yScale = 1.0D;
    } 
  }
  
  public double getXScale() {
    return this.m_xScale;
  }
  
  public double getYScale() {
    return this.m_xScale;
  }
  
  public void toOutput() throws Exception {
    if (getFile() == null)
      throw new Exception("The file is not set!"); 
    if (getComponent() == null)
      throw new Exception("The component is not set!"); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\JComponentWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */