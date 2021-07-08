package weka.gui.beans;

import java.util.EventObject;
import weka.core.Instances;

public class DataSetEvent extends EventObject {
  private Instances m_dataSet;
  
  private boolean m_structureOnly;
  
  public DataSetEvent(Object paramObject, Instances paramInstances) {
    super(paramObject);
    this.m_dataSet = paramInstances;
    if (this.m_dataSet != null && this.m_dataSet.numInstances() == 0)
      this.m_structureOnly = true; 
  }
  
  public Instances getDataSet() {
    return this.m_dataSet;
  }
  
  public boolean isStructureOnly() {
    return this.m_structureOnly;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\DataSetEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */