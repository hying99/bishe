package weka.gui.beans;

import java.util.EventObject;
import weka.clusterers.Clusterer;

public class BatchClustererEvent extends EventObject {
  protected Clusterer m_clusterer;
  
  protected DataSetEvent m_testSet;
  
  protected int m_setNumber;
  
  protected int m_testOrTrain;
  
  protected int m_maxSetNumber;
  
  private static int TEST = 0;
  
  private static int TRAINING = 1;
  
  public BatchClustererEvent(Object paramObject, Clusterer paramClusterer, DataSetEvent paramDataSetEvent, int paramInt1, int paramInt2, int paramInt3) {
    super(paramObject);
    this.m_clusterer = paramClusterer;
    this.m_testSet = paramDataSetEvent;
    this.m_setNumber = paramInt1;
    this.m_maxSetNumber = paramInt2;
    if (paramInt3 == 0) {
      this.m_testOrTrain = TEST;
    } else {
      this.m_testOrTrain = TRAINING;
    } 
  }
  
  public Clusterer getClusterer() {
    return this.m_clusterer;
  }
  
  public DataSetEvent getTestSet() {
    return this.m_testSet;
  }
  
  public int getSetNumber() {
    return this.m_setNumber;
  }
  
  public int getMaxSetNumber() {
    return this.m_maxSetNumber;
  }
  
  public int getTestOrTrain() {
    return this.m_testOrTrain;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\BatchClustererEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */