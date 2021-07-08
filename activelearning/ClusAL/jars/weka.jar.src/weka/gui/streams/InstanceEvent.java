package weka.gui.streams;

import java.util.EventObject;

public class InstanceEvent extends EventObject {
  public static final int FORMAT_AVAILABLE = 1;
  
  public static final int INSTANCE_AVAILABLE = 2;
  
  public static final int BATCH_FINISHED = 3;
  
  private int m_ID;
  
  public InstanceEvent(Object paramObject, int paramInt) {
    super(paramObject);
    this.m_ID = paramInt;
  }
  
  public int getID() {
    return this.m_ID;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */