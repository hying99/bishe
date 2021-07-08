package weka.gui.beans;

import java.util.EventObject;
import java.util.Vector;

public class ChartEvent extends EventObject {
  private Vector m_legendText;
  
  private double m_max;
  
  private double m_min;
  
  private boolean m_reset;
  
  private double[] m_dataPoint;
  
  public ChartEvent(Object paramObject, Vector paramVector, double paramDouble1, double paramDouble2, double[] paramArrayOfdouble, boolean paramBoolean) {
    super(paramObject);
    this.m_legendText = paramVector;
    this.m_max = paramDouble2;
    this.m_min = paramDouble1;
    this.m_dataPoint = paramArrayOfdouble;
    this.m_reset = paramBoolean;
  }
  
  public ChartEvent(Object paramObject) {
    super(paramObject);
  }
  
  public Vector getLegendText() {
    return this.m_legendText;
  }
  
  public void setLegendText(Vector paramVector) {
    this.m_legendText = paramVector;
  }
  
  public double getMin() {
    return this.m_min;
  }
  
  public void setMin(double paramDouble) {
    this.m_min = paramDouble;
  }
  
  public double getMax() {
    return this.m_max;
  }
  
  public void setMax(double paramDouble) {
    this.m_max = paramDouble;
  }
  
  public double[] getDataPoint() {
    return this.m_dataPoint;
  }
  
  public void setDataPoint(double[] paramArrayOfdouble) {
    this.m_dataPoint = paramArrayOfdouble;
  }
  
  public void setReset(boolean paramBoolean) {
    this.m_reset = paramBoolean;
  }
  
  public boolean getReset() {
    return this.m_reset;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ChartEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */