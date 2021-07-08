package weka.gui.beans;

import java.util.EventObject;
import weka.core.Instances;

public class TrainingSetEvent extends EventObject {
  protected Instances m_trainingSet;
  
  private boolean m_structureOnly;
  
  protected int m_setNumber;
  
  protected int m_maxSetNumber;
  
  public TrainingSetEvent(Object paramObject, Instances paramInstances) {
    super(paramObject);
    this.m_trainingSet = paramInstances;
    if (this.m_trainingSet != null && this.m_trainingSet.numInstances() == 0)
      this.m_structureOnly = true; 
  }
  
  public Instances getTrainingSet() {
    return this.m_trainingSet;
  }
  
  public int getSetNumber() {
    return this.m_setNumber;
  }
  
  public int getMaxSetNumber() {
    return this.m_maxSetNumber;
  }
  
  public boolean isStructureOnly() {
    return this.m_structureOnly;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\TrainingSetEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */