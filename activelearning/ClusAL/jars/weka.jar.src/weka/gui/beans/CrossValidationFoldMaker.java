package weka.gui.beans;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instances;

public class CrossValidationFoldMaker extends AbstractTrainAndTestSetProducer implements DataSourceListener, TrainingSetListener, TestSetListener, UserRequestAcceptor, EventConstraints, Serializable {
  private int m_numFolds = 10;
  
  private int m_randomSeed = 1;
  
  private Thread m_foldThread = null;
  
  public CrossValidationFoldMaker() {
    this.m_visual.loadIcons("weka/gui/beans/icons/CrossValidationFoldMaker.gif", "weka/gui/beans/icons/CrossValidationFoldMaker_animated.gif");
    this.m_visual.setText("CrossValidationFoldMaker");
  }
  
  public String globalInfo() {
    return "Split an incoming data set into cross validation folds. Separate train and test sets are produced for each of the k folds.";
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
    if (paramDataSetEvent.isStructureOnly()) {
      TrainingSetEvent trainingSetEvent = new TrainingSetEvent(this, paramDataSetEvent.getDataSet());
      TestSetEvent testSetEvent = new TestSetEvent(this, paramDataSetEvent.getDataSet());
      notifyTrainingSetProduced(trainingSetEvent);
      notifyTestSetProduced(testSetEvent);
      return;
    } 
    if (this.m_foldThread == null) {
      Instances instances = new Instances(paramDataSetEvent.getDataSet());
      this.m_foldThread = new Thread(this, instances) {
          private final Instances val$dataSet;
          
          private final CrossValidationFoldMaker this$0;
          
          public void run() {
            try {
              Random random = new Random(this.this$0.getSeed());
              this.val$dataSet.randomize(random);
              if (this.val$dataSet.classIndex() >= 0 && this.val$dataSet.attribute(this.val$dataSet.classIndex()).isNominal()) {
                this.val$dataSet.stratify(this.this$0.getFolds());
                if (this.this$0.m_logger != null)
                  this.this$0.m_logger.logMessage("CrossValidationFoldMaker : stratifying data"); 
              } 
              for (byte b = 0; b < this.this$0.getFolds(); b++) {
                if (this.this$0.m_foldThread == null) {
                  if (this.this$0.m_logger != null) {
                    this.this$0.m_logger.logMessage("Cross validation has been canceled!");
                    this.this$0.m_logger.statusMessage("OK");
                  } 
                  break;
                } 
                Instances instances1 = this.val$dataSet.trainCV(this.this$0.getFolds(), b, random);
                Instances instances2 = this.val$dataSet.testCV(this.this$0.getFolds(), b);
                TrainingSetEvent trainingSetEvent = new TrainingSetEvent(this, instances1);
                trainingSetEvent.m_setNumber = b + 1;
                trainingSetEvent.m_maxSetNumber = this.this$0.getFolds();
                if (this.this$0.m_foldThread != null)
                  this.this$0.notifyTrainingSetProduced(trainingSetEvent); 
                TestSetEvent testSetEvent = new TestSetEvent(this, instances2);
                testSetEvent.m_setNumber = b + 1;
                testSetEvent.m_maxSetNumber = this.this$0.getFolds();
                if (this.this$0.m_foldThread != null)
                  this.this$0.notifyTestSetProduced(testSetEvent); 
              } 
            } catch (Exception exception) {
              exception.printStackTrace();
            } finally {
              if (isInterrupted())
                System.err.println("Cross validation interrupted"); 
              this.this$0.block(false);
            } 
          }
        };
      this.m_foldThread.setPriority(1);
      this.m_foldThread.start();
      block(true);
      this.m_foldThread = null;
    } 
  }
  
  private void notifyTestSetProduced(TestSetEvent paramTestSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_testListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((TestSetListener)vector.elementAt(b)).acceptTestSet(paramTestSetEvent);  
  }
  
  protected void notifyTrainingSetProduced(TrainingSetEvent paramTrainingSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_trainingListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((TrainingSetListener)vector.elementAt(b)).acceptTrainingSet(paramTrainingSetEvent);  
  }
  
  public void setFolds(int paramInt) {
    this.m_numFolds = paramInt;
  }
  
  public int getFolds() {
    return this.m_numFolds;
  }
  
  public String foldsTipText() {
    return "The number of train and test splits to produce";
  }
  
  public void setSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public int getSeed() {
    return this.m_randomSeed;
  }
  
  public String seedTipText() {
    return "The randomization seed";
  }
  
  public void stop() {
    if (this.m_listenee instanceof BeanCommon) {
      System.err.println("Listener is BeanCommon");
      ((BeanCommon)this.m_listenee).stop();
    } 
    if (this.m_foldThread != null) {
      Thread thread = this.m_foldThread;
      this.m_foldThread = null;
      thread.interrupt();
    } 
  }
  
  private synchronized void block(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        if (this.m_foldThread.isAlive())
          wait(); 
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_foldThread != null)
      vector.addElement("Stop"); 
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Stop") == 0) {
      stop();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (CrossValidation)");
    } 
  }
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : ((this.m_listenee instanceof EventConstraints) ? ((((EventConstraints)this.m_listenee).eventGeneratable("dataSet") || ((EventConstraints)this.m_listenee).eventGeneratable("trainingSet") || ((EventConstraints)this.m_listenee).eventGeneratable("testSet"))) : true);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\CrossValidationFoldMaker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */