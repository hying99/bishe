package weka.gui.streams;

import java.io.Serializable;
import java.util.Vector;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceJoiner implements Serializable, InstanceProducer, SerialInstanceListener {
  private Vector listeners = new Vector();
  
  private boolean b_Debug = false;
  
  protected Instances m_InputFormat = null;
  
  private Instance m_OutputInstance = null;
  
  private boolean b_FirstInputFinished = false;
  
  private boolean b_SecondInputFinished = false;
  
  public boolean inputFormat(Instances paramInstances) {
    this.m_InputFormat = new Instances(paramInstances, 0);
    notifyInstanceProduced(new InstanceEvent(this, 1));
    this.b_FirstInputFinished = false;
    this.b_SecondInputFinished = false;
    return true;
  }
  
  public Instances outputFormat() throws Exception {
    if (this.m_InputFormat == null)
      throw new Exception("No output format defined."); 
    return new Instances(this.m_InputFormat, 0);
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (this.m_InputFormat == null)
      throw new Exception("No input instance format defined"); 
    if (paramInstance != null) {
      this.m_OutputInstance = (Instance)paramInstance.copy();
      notifyInstanceProduced(new InstanceEvent(this, 2));
      return true;
    } 
    return false;
  }
  
  public void batchFinished() throws Exception {
    if (this.m_InputFormat == null)
      throw new Exception("No input instance format defined"); 
    notifyInstanceProduced(new InstanceEvent(this, 3));
  }
  
  public Instance outputPeek() throws Exception {
    if (this.m_InputFormat == null)
      throw new Exception("No output instance format defined"); 
    return (this.m_OutputInstance == null) ? null : (Instance)this.m_OutputInstance.copy();
  }
  
  public void setDebug(boolean paramBoolean) {
    this.b_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.b_Debug;
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    this.listeners.addElement(paramInstanceListener);
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    this.listeners.removeElement(paramInstanceListener);
  }
  
  protected void notifyInstanceProduced(InstanceEvent paramInstanceEvent) {
    if (this.listeners.size() > 0) {
      Vector vector;
      if (this.b_Debug)
        System.err.println(getClass().getName() + "::notifyInstanceProduced()"); 
      synchronized (this) {
        vector = (Vector)this.listeners.clone();
      } 
      for (byte b = 0; b < vector.size(); b++)
        ((InstanceListener)vector.elementAt(b)).instanceProduced(paramInstanceEvent); 
      try {
        if (paramInstanceEvent.getID() == 2)
          this.m_OutputInstance = null; 
      } catch (Exception exception) {
        System.err.println("Problem: notifyInstanceProduced() was\ncalled with INSTANCE_AVAILABLE, but output()\nthrew an exception: " + exception.getMessage());
      } 
    } 
  }
  
  public void instanceProduced(InstanceEvent paramInstanceEvent) {
    Object object = paramInstanceEvent.getSource();
    if (object instanceof InstanceProducer) {
      try {
        InstanceProducer instanceProducer = (InstanceProducer)object;
        switch (paramInstanceEvent.getID()) {
          case 1:
            if (this.b_Debug)
              System.err.println(getClass().getName() + "::firstInstanceProduced() - Format available"); 
            inputFormat(instanceProducer.outputFormat());
            return;
          case 2:
            if (this.b_Debug)
              System.err.println(getClass().getName() + "::firstInstanceProduced() - Instance available"); 
            input(instanceProducer.outputPeek());
            return;
          case 3:
            if (this.b_Debug)
              System.err.println(getClass().getName() + "::firstInstanceProduced() - End of instance batch"); 
            batchFinished();
            this.b_FirstInputFinished = true;
            return;
        } 
        System.err.println(getClass().getName() + "::firstInstanceProduced() - unknown event type");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else {
      System.err.println(getClass().getName() + "::firstInstanceProduced() - Unknown source object type");
    } 
  }
  
  public void secondInstanceProduced(InstanceEvent paramInstanceEvent) {
    Object object = paramInstanceEvent.getSource();
    if (object instanceof InstanceProducer) {
      try {
        if (!this.b_FirstInputFinished)
          throw new Exception(getClass().getName() + "::secondInstanceProduced() - Input received from" + " second stream before first stream finished"); 
        InstanceProducer instanceProducer = (InstanceProducer)object;
        switch (paramInstanceEvent.getID()) {
          case 1:
            if (this.b_Debug)
              System.err.println(getClass().getName() + "::secondInstanceProduced() - Format available"); 
            if (!instanceProducer.outputFormat().equalHeaders(outputFormat()))
              throw new Exception(getClass().getName() + "::secondInstanceProduced() - incompatible instance streams"); 
            return;
          case 2:
            if (this.b_Debug)
              System.err.println(getClass().getName() + "::secondInstanceProduced() - Instance available"); 
            input(instanceProducer.outputPeek());
            return;
          case 3:
            if (this.b_Debug)
              System.err.println(getClass().getName() + "::secondInstanceProduced() - End of instance batch"); 
            batchFinished();
            return;
        } 
        System.err.println(getClass().getName() + "::secondInstanceProduced() - unknown event type");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else {
      System.err.println(getClass().getName() + "::secondInstanceProduced() - Unknown source object type");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceJoiner.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */