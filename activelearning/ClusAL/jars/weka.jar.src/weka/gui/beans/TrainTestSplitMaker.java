package weka.gui.beans;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instances;

public class TrainTestSplitMaker extends AbstractTrainAndTestSetProducer implements DataSourceListener, TrainingSetListener, TestSetListener, UserRequestAcceptor, EventConstraints, Serializable {
  private int m_trainPercentage = 66;
  
  private int m_randomSeed = 1;
  
  private Thread m_splitThread = null;
  
  public TrainTestSplitMaker() {
    this.m_visual.loadIcons("weka/gui/beans/icons/TrainTestSplitMaker.gif", "weka/gui/beans/icons/TrainTestSplittMaker_animated.gif");
    this.m_visual.setText("TrainTestSplitMaker");
  }
  
  public String globalInfo() {
    return "Split an incoming data set into separate train and test sets.";
  }
  
  public String trainPercentTipText() {
    return "The percentage of data to go into the training set";
  }
  
  public void setTrainPercent(int paramInt) {
    this.m_trainPercentage = paramInt;
  }
  
  public int getTrainPercent() {
    return this.m_trainPercentage;
  }
  
  public String seedTipText() {
    return "The randomization seed";
  }
  
  public void setSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public int getSeed() {
    return this.m_randomSeed;
  }
  
  public void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    Instances instances = paramTrainingSetEvent.getTrainingSet();
    DataSetEvent dataSetEvent = new DataSetEvent(this, instances);
    acceptDataSet(dataSetEvent);
  }
  
  public void acceptTestSet(TestSetEvent paramTestSetEvent) {
    Instances instances = paramTestSetEvent.getTestSet();
    DataSetEvent dataSetEvent = new DataSetEvent(this, instances);
    acceptDataSet(dataSetEvent);
  }
  
  public void acceptDataSet(DataSetEvent paramDataSetEvent) {
    if (this.m_splitThread == null) {
      Instances instances = new Instances(paramDataSetEvent.getDataSet());
      this.m_splitThread = new Thread(this, instances) {
          private final Instances val$dataSet;
          
          private final TrainTestSplitMaker this$0;
          
          public void run() {
            try {
              this.val$dataSet.randomize(new Random(this.this$0.m_randomSeed));
              int i = this.val$dataSet.numInstances() * this.this$0.m_trainPercentage / 100;
              int j = this.val$dataSet.numInstances() - i;
              Instances instances1 = new Instances(this.val$dataSet, 0, i);
              Instances instances2 = new Instances(this.val$dataSet, i, j);
              TrainingSetEvent trainingSetEvent = new TrainingSetEvent(this.this$0, instances1);
              trainingSetEvent.m_setNumber = 1;
              trainingSetEvent.m_maxSetNumber = 1;
              if (this.this$0.m_splitThread != null)
                this.this$0.notifyTrainingSetProduced(trainingSetEvent); 
              TestSetEvent testSetEvent = new TestSetEvent(this.this$0, instances2);
              testSetEvent.m_setNumber = 1;
              testSetEvent.m_maxSetNumber = 1;
              if (this.this$0.m_splitThread != null) {
                this.this$0.notifyTestSetProduced(testSetEvent);
              } else if (this.this$0.m_logger != null) {
                this.this$0.m_logger.logMessage("Split has been canceled!");
                this.this$0.m_logger.statusMessage("OK");
              } 
            } catch (Exception exception) {
              exception.printStackTrace();
            } finally {
              if (isInterrupted())
                System.err.println("Split maker interrupted"); 
              this.this$0.block(false);
            } 
          }
        };
      this.m_splitThread.setPriority(1);
      this.m_splitThread.start();
      block(true);
      this.m_splitThread = null;
    } 
  }
  
  protected void notifyTestSetProduced(TestSetEvent paramTestSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_testListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying test listeners (cross validation fold maker)");
        ((TestSetListener)vector.elementAt(b)).acceptTestSet(paramTestSetEvent);
      }  
  }
  
  protected void notifyTrainingSetProduced(TrainingSetEvent paramTrainingSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_trainingListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying training listeners (cross validation fold maker)");
        ((TrainingSetListener)vector.elementAt(b)).acceptTrainingSet(paramTrainingSetEvent);
      }  
  }
  
  private synchronized void block(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        if (this.m_splitThread.isAlive())
          wait(); 
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  public void stop() {
    if (this.m_listenee instanceof BeanCommon) {
      System.err.println("Listener is BeanCommon");
      ((BeanCommon)this.m_listenee).stop();
    } 
    if (this.m_splitThread != null) {
      Thread thread = this.m_splitThread;
      this.m_splitThread = null;
      thread.interrupt();
    } 
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_splitThread != null)
      vector.addElement("Stop"); 
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Stop") == 0) {
      stop();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (TrainTestSplitMaker)");
    } 
  }
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : ((this.m_listenee instanceof EventConstraints) ? ((((EventConstraints)this.m_listenee).eventGeneratable("dataSet") || ((EventConstraints)this.m_listenee).eventGeneratable("trainingSet") || ((EventConstraints)this.m_listenee).eventGeneratable("testSet"))) : true);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\TrainTestSplitMaker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */