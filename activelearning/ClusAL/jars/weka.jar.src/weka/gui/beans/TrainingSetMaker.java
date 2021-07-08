package weka.gui.beans;

import java.io.Serializable;
import java.util.Vector;

public class TrainingSetMaker extends AbstractTrainingSetProducer implements DataSourceListener, EventConstraints, Serializable {
  public TrainingSetMaker() {
    this.m_visual.loadIcons("weka/gui/beans/icons/TrainingSetMaker.gif", "weka/gui/beans/icons/TrainingSetMaker_animated.gif");
    this.m_visual.setText("TrainingSetMaker");
  }
  
  public String globalInfo() {
    return "Designate an incoming data set as a training set.";
  }
  
  public void acceptDataSet(DataSetEvent paramDataSetEvent) {
    System.err.println("In accept data set");
    TrainingSetEvent trainingSetEvent = new TrainingSetEvent(this, paramDataSetEvent.getDataSet());
    trainingSetEvent.m_setNumber = 1;
    trainingSetEvent.m_maxSetNumber = 1;
    notifyTrainingSetProduced(trainingSetEvent);
  }
  
  protected void notifyTrainingSetProduced(TrainingSetEvent paramTrainingSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_listeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying listeners (training set maker)");
        ((TrainingSetListener)vector.elementAt(b)).acceptTrainingSet(paramTrainingSetEvent);
      }  
  }
  
  public void stop() {}
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : (!(this.m_listenee instanceof EventConstraints && !((EventConstraints)this.m_listenee).eventGeneratable("dataSet")));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\TrainingSetMaker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */