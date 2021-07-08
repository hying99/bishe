package weka.gui.beans;

import java.util.EventObject;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class IncrementalClassifierEvent extends EventObject {
  public static final int NEW_BATCH = 0;
  
  public static final int WITHIN_BATCH = 1;
  
  public static final int BATCH_FINISHED = 2;
  
  private Instances m_structure;
  
  private int m_status;
  
  protected Classifier m_classifier;
  
  protected Instance m_currentInstance;
  
  public IncrementalClassifierEvent(Object paramObject, Classifier paramClassifier, Instance paramInstance, int paramInt) {
    super(paramObject);
    this.m_classifier = paramClassifier;
    this.m_currentInstance = paramInstance;
    this.m_status = paramInt;
  }
  
  public IncrementalClassifierEvent(Object paramObject, Classifier paramClassifier, Instances paramInstances) {
    super(paramObject);
    this.m_structure = paramInstances;
    this.m_status = 0;
    this.m_classifier = paramClassifier;
  }
  
  public IncrementalClassifierEvent(Object paramObject) {
    super(paramObject);
  }
  
  public Classifier getClassifier() {
    return this.m_classifier;
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_classifier = paramClassifier;
  }
  
  public Instance getCurrentInstance() {
    return this.m_currentInstance;
  }
  
  public void setCurrentInstance(Instance paramInstance) {
    this.m_currentInstance = paramInstance;
  }
  
  public int getStatus() {
    return this.m_status;
  }
  
  public void setStatus(int paramInt) {
    this.m_status = paramInt;
  }
  
  public void setStructure(Instances paramInstances) {
    this.m_structure = paramInstances;
    this.m_currentInstance = null;
    this.m_status = 0;
  }
  
  public Instances getStructure() {
    return this.m_structure;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\IncrementalClassifierEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */