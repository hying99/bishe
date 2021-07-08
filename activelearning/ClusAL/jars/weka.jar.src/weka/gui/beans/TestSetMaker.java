package weka.gui.beans;

import java.io.Serializable;
import java.util.Vector;

public class TestSetMaker extends AbstractTestSetProducer implements DataSourceListener, EventConstraints, Serializable {
  public TestSetMaker() {
    this.m_visual.loadIcons("weka/gui/beans/icons/TestSetMaker.gif", "weka/gui/beans/icons/TestSetMaker_animated.gif");
    this.m_visual.setText("TestSetMaker");
  }
  
  public String globalInfo() {
    return "Designate an incoming data set as a test set.";
  }
  
  public void acceptDataSet(DataSetEvent paramDataSetEvent) {
    TestSetEvent testSetEvent = new TestSetEvent(this, paramDataSetEvent.getDataSet());
    testSetEvent.m_setNumber = 1;
    testSetEvent.m_maxSetNumber = 1;
    notifyTestSetProduced(testSetEvent);
  }
  
  protected void notifyTestSetProduced(TestSetEvent paramTestSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_listeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((TestSetListener)vector.elementAt(b)).acceptTestSet(paramTestSetEvent);  
  }
  
  public void stop() {}
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : (!(this.m_listenee instanceof EventConstraints && !((EventConstraints)this.m_listenee).eventGeneratable("dataSet")));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\TestSetMaker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */