package weka.gui.visualize;

import weka.core.FastVector;
import weka.core.Instances;

public class VisualizePanelEvent {
  public static int NONE = 0;
  
  public static int RECTANGLE = 1;
  
  public static int OVAL = 2;
  
  public static int POLYGON = 3;
  
  public static int LINE = 4;
  
  public static int VLINE = 5;
  
  public static int HLINE = 6;
  
  private FastVector m_values;
  
  private Instances m_inst;
  
  private Instances m_inst2;
  
  private int m_attrib1;
  
  private int m_attrib2;
  
  public VisualizePanelEvent(FastVector paramFastVector, Instances paramInstances1, Instances paramInstances2, int paramInt1, int paramInt2) {
    this.m_values = paramFastVector;
    this.m_inst = paramInstances1;
    this.m_inst2 = paramInstances2;
    this.m_attrib1 = paramInt1;
    this.m_attrib2 = paramInt2;
  }
  
  public FastVector getValues() {
    return this.m_values;
  }
  
  public Instances getInstances1() {
    return this.m_inst;
  }
  
  public Instances getInstances2() {
    return this.m_inst2;
  }
  
  public int getAttribute1() {
    return this.m_attrib1;
  }
  
  public int getAttribute2() {
    return this.m_attrib2;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\VisualizePanelEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */