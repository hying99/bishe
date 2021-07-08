package weka.gui.beans;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;

public class ClustererPerformanceEvaluator extends AbstractEvaluator implements BatchClustererListener, Serializable, UserRequestAcceptor, EventConstraints {
  private transient ClusterEvaluation m_eval;
  
  private transient Clusterer m_clusterer;
  
  private transient Thread m_evaluateThread = null;
  
  private Vector m_textListeners = new Vector();
  
  public ClustererPerformanceEvaluator() {
    this.m_visual.loadIcons("weka/gui/beans/icons/ClustererPerformanceEvaluator.gif", "weka/gui/beans/icons/ClustererPerformanceEvaluator_animated.gif");
    this.m_visual.setText("ClustererPerformanceEvaluator");
  }
  
  public String globalInfo() {
    return "Evaluate the performance of batch trained clusterers.";
  }
  
  public void acceptClusterer(BatchClustererEvent paramBatchClustererEvent) {
    if (paramBatchClustererEvent.getTestSet().isStructureOnly())
      return; 
    try {
      if (this.m_evaluateThread == null) {
        this.m_evaluateThread = new Thread(this, paramBatchClustererEvent) {
            private final BatchClustererEvent val$ce;
            
            private final ClustererPerformanceEvaluator this$0;
            
            public void run() {
              boolean bool = false;
              String str = this.this$0.m_visual.getText();
              try {
                if (this.val$ce.getSetNumber() == 1 || this.val$ce.getClusterer() != this.this$0.m_clusterer) {
                  this.this$0.m_eval = new ClusterEvaluation();
                  this.this$0.m_clusterer = this.val$ce.getClusterer();
                  this.this$0.m_eval.setClusterer(this.this$0.m_clusterer);
                } 
                if (this.val$ce.getSetNumber() <= this.val$ce.getMaxSetNumber()) {
                  this.this$0.m_visual.setText("Evaluating (" + this.val$ce.getSetNumber() + ")...");
                  if (this.this$0.m_logger != null)
                    this.this$0.m_logger.statusMessage("ClustererPerformaceEvaluator : evaluating (" + this.val$ce.getSetNumber() + ")..."); 
                  this.this$0.m_visual.setAnimated();
                  if (this.val$ce.getTestSet().getDataSet().classIndex() != -1 && this.val$ce.getTestSet().getDataSet().classAttribute().isNumeric()) {
                    bool = true;
                    this.val$ce.getTestSet().getDataSet().setClassIndex(-1);
                  } 
                  this.this$0.m_eval.evaluateClusterer(this.val$ce.getTestSet().getDataSet());
                } 
                if (this.val$ce.getSetNumber() == this.val$ce.getMaxSetNumber()) {
                  String str2;
                  String str1 = this.this$0.m_clusterer.getClass().getName();
                  str1 = str1.substring(str1.lastIndexOf('.') + 1, str1.length());
                  if (this.val$ce.getTestOrTrain() == 0) {
                    str2 = "test";
                  } else {
                    str2 = "training";
                  } 
                  String str3 = "=== Evaluation result for " + str2 + " instances ===\n\n" + "Scheme: " + str1 + "\n" + "Relation: " + this.val$ce.getTestSet().getDataSet().relationName() + "\n\n" + this.this$0.m_eval.clusterResultsToString();
                  if (bool)
                    str3 = str3 + "\n\nNo class based evaluation possible. Class attribute has to be nominal."; 
                  TextEvent textEvent = new TextEvent(this.this$0, str3, str1);
                  this.this$0.notifyTextListeners(textEvent);
                  if (this.this$0.m_logger != null)
                    this.this$0.m_logger.statusMessage("Done."); 
                } 
              } catch (Exception exception) {
                exception.printStackTrace();
              } finally {
                this.this$0.m_visual.setText(str);
                this.this$0.m_visual.setStatic();
                this.this$0.m_evaluateThread = null;
                if (isInterrupted() && this.this$0.m_logger != null) {
                  this.this$0.m_logger.logMessage("Evaluation interrupted!");
                  this.this$0.m_logger.statusMessage("OK");
                } 
                this.this$0.block(false);
              } 
            }
          };
        this.m_evaluateThread.setPriority(1);
        this.m_evaluateThread.start();
        block(true);
        this.m_evaluateThread = null;
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public void stop() {
    if (this.m_listenee instanceof BeanCommon) {
      System.err.println("Listener is BeanCommon");
      ((BeanCommon)this.m_listenee).stop();
    } 
    if (this.m_evaluateThread != null) {
      this.m_evaluateThread.interrupt();
      this.m_evaluateThread.stop();
    } 
  }
  
  private synchronized void block(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        if (this.m_evaluateThread != null && this.m_evaluateThread.isAlive())
          wait(); 
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_evaluateThread != null)
      vector.addElement("Stop"); 
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Stop") == 0) {
      stop();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (ClustererPerformanceEvaluator)");
    } 
  }
  
  public synchronized void addTextListener(TextListener paramTextListener) {
    this.m_textListeners.addElement(paramTextListener);
  }
  
  public synchronized void removeTextListener(TextListener paramTextListener) {
    this.m_textListeners.remove(paramTextListener);
  }
  
  private void notifyTextListeners(TextEvent paramTextEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_textListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((TextListener)vector.elementAt(b)).acceptText(paramTextEvent);  
  }
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : (!(this.m_listenee instanceof EventConstraints && !((EventConstraints)this.m_listenee).eventGeneratable("batchClusterer")));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClustererPerformanceEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */