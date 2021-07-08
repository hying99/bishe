package weka.gui.beans;

import java.util.EventObject;
import weka.classifiers.Classifier;

public class BatchClassifierEvent extends EventObject {
  protected Classifier m_classifier;
  
  protected DataSetEvent m_testSet;
  
  protected int m_setNumber;
  
  protected int m_maxSetNumber;
  
  public BatchClassifierEvent(Object paramObject, Classifier paramClassifier, DataSetEvent paramDataSetEvent, int paramInt1, int paramInt2) {
    super(paramObject);
    this.m_classifier = paramClassifier;
    this.m_testSet = paramDataSetEvent;
    this.m_setNumber = paramInt1;
    this.m_maxSetNumber = paramInt2;
  }
  
  public Classifier getClassifier() {
    return this.m_classifier;
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
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\BatchClassifierEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */