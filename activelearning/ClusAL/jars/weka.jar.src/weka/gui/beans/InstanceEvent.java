package weka.gui.beans;

import java.util.EventObject;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceEvent extends EventObject {
  public static final int FORMAT_AVAILABLE = 0;
  
  public static final int INSTANCE_AVAILABLE = 1;
  
  public static final int BATCH_FINISHED = 2;
  
  private Instances m_structure;
  
  private Instance m_instance;
  
  private int m_status;
  
  public InstanceEvent(Object paramObject, Instance paramInstance, int paramInt) {
    super(paramObject);
    this.m_instance = paramInstance;
    this.m_status = paramInt;
  }
  
  public InstanceEvent(Object paramObject, Instances paramInstances) {
    super(paramObject);
    this.m_structure = paramInstances;
    this.m_status = 0;
  }
  
  public InstanceEvent(Object paramObject) {
    super(paramObject);
  }
  
  public Instance getInstance() {
    return this.m_instance;
  }
  
  public void setInstance(Instance paramInstance) {
    this.m_instance = paramInstance;
  }
  
  public int getStatus() {
    return this.m_status;
  }
  
  public void setStatus(int paramInt) {
    this.m_status = paramInt;
  }
  
  public void setStructure(Instances paramInstances) {
    this.m_structure = paramInstances;
    this.m_instance = null;
    this.m_status = 0;
  }
  
  public Instances getStructure() {
    return this.m_structure;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\InstanceEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */