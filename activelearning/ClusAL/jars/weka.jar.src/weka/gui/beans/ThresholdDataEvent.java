package weka.gui.beans;

import java.util.EventObject;
import weka.gui.visualize.PlotData2D;

public class ThresholdDataEvent extends EventObject {
  private PlotData2D m_dataSet;
  
  public ThresholdDataEvent(Object paramObject, PlotData2D paramPlotData2D) {
    super(paramObject);
    this.m_dataSet = paramPlotData2D;
  }
  
  public PlotData2D getDataSet() {
    return this.m_dataSet;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ThresholdDataEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */