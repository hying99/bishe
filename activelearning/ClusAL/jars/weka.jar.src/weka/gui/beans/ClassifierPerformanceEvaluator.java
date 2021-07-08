package weka.gui.beans;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.explorer.ClassifierPanel;
import weka.gui.visualize.PlotData2D;

public class ClassifierPerformanceEvaluator extends AbstractEvaluator implements BatchClassifierListener, Serializable, UserRequestAcceptor, EventConstraints {
  private transient Evaluation m_eval;
  
  private transient Classifier m_classifier;
  
  private transient Thread m_evaluateThread = null;
  
  private Vector m_textListeners = new Vector();
  
  private Vector m_thresholdListeners = new Vector();
  
  private boolean m_rocListenersConnected = false;
  
  private Instances m_predInstances = null;
  
  private FastVector m_preds = null;
  
  private FastVector m_plotShape = null;
  
  private FastVector m_plotSize = null;
  
  public ClassifierPerformanceEvaluator() {
    this.m_visual.loadIcons("weka/gui/beans/icons/ClassifierPerformanceEvaluator.gif", "weka/gui/beans/icons/ClassifierPerformanceEvaluator_animated.gif");
    this.m_visual.setText("ClassifierPerformanceEvaluator");
  }
  
  public String globalInfo() {
    return "Evaluate the performance of batch trained classifiers.";
  }
  
  public void acceptClassifier(BatchClassifierEvent paramBatchClassifierEvent) {
    if (paramBatchClassifierEvent.getTestSet().isStructureOnly())
      return; 
    try {
      if (this.m_evaluateThread == null) {
        this.m_evaluateThread = new Thread(this, paramBatchClassifierEvent) {
            private final BatchClassifierEvent val$ce;
            
            private final ClassifierPerformanceEvaluator this$0;
            
            public void run() {
              String str = this.this$0.m_visual.getText();
              try {
                if (this.val$ce.getSetNumber() == 1 || this.val$ce.getClassifier() != this.this$0.m_classifier) {
                  this.this$0.m_eval = new Evaluation(this.val$ce.getTestSet().getDataSet());
                  this.this$0.m_classifier = this.val$ce.getClassifier();
                  this.this$0.m_predInstances = ClassifierPanel.setUpVisualizableInstances(new Instances(this.val$ce.getTestSet().getDataSet()));
                  this.this$0.m_preds = new FastVector();
                  this.this$0.m_plotShape = new FastVector();
                  this.this$0.m_plotSize = new FastVector();
                } 
                if (this.val$ce.getSetNumber() <= this.val$ce.getMaxSetNumber()) {
                  this.this$0.m_visual.setText("Evaluating (" + this.val$ce.getSetNumber() + ")...");
                  if (this.this$0.m_logger != null)
                    this.this$0.m_logger.statusMessage("ClassifierPerformaceEvaluator : evaluating (" + this.val$ce.getSetNumber() + ")..."); 
                  this.this$0.m_visual.setAnimated();
                  for (byte b = 0; b < this.val$ce.getTestSet().getDataSet().numInstances(); b++) {
                    Instance instance = this.val$ce.getTestSet().getDataSet().instance(b);
                    ClassifierPanel.processClassifierPrediction(instance, this.val$ce.getClassifier(), this.this$0.m_eval, this.this$0.m_preds, this.this$0.m_predInstances, this.this$0.m_plotShape, this.this$0.m_plotSize);
                  } 
                } 
                if (this.val$ce.getSetNumber() == this.val$ce.getMaxSetNumber()) {
                  System.err.println(this.this$0.m_eval.toSummaryString());
                  String str1 = this.this$0.m_classifier.getClass().getName();
                  str1 = str1.substring(str1.lastIndexOf('.') + 1, str1.length());
                  String str2 = "=== Evaluation result ===\n\nScheme: " + str1 + "\n" + "Relation: " + this.val$ce.getTestSet().getDataSet().relationName() + "\n\n" + this.this$0.m_eval.toSummaryString();
                  TextEvent textEvent = new TextEvent(this.this$0, str2, str1);
                  this.this$0.notifyTextListeners(textEvent);
                  if (this.val$ce.getTestSet().getDataSet().classAttribute().isNominal()) {
                    ThresholdCurve thresholdCurve = new ThresholdCurve();
                    Instances instances = thresholdCurve.getCurve(this.this$0.m_preds, 0);
                    instances.setRelationName(this.val$ce.getTestSet().getDataSet().relationName());
                    PlotData2D plotData2D = new PlotData2D(instances);
                    plotData2D.setPlotName(str1 + " (" + this.val$ce.getTestSet().getDataSet().classAttribute().value(0) + ")");
                    boolean[] arrayOfBoolean = new boolean[instances.numInstances()];
                    for (byte b = 1; b < arrayOfBoolean.length; b++)
                      arrayOfBoolean[b] = true; 
                    plotData2D.setConnectPoints(arrayOfBoolean);
                    ThresholdDataEvent thresholdDataEvent = new ThresholdDataEvent(this.this$0, plotData2D);
                    this.this$0.notifyThresholdListeners(thresholdDataEvent);
                  } 
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
      throw new IllegalArgumentException(paramString + " not supported (ClassifierPerformanceEvaluator)");
    } 
  }
  
  public synchronized void addTextListener(TextListener paramTextListener) {
    this.m_textListeners.addElement(paramTextListener);
  }
  
  public synchronized void removeTextListener(TextListener paramTextListener) {
    this.m_textListeners.remove(paramTextListener);
  }
  
  public synchronized void addThresholdDataListener(ThresholdDataListener paramThresholdDataListener) {
    this.m_thresholdListeners.addElement(paramThresholdDataListener);
  }
  
  public synchronized void removeThresholdDataListener(ThresholdDataListener paramThresholdDataListener) {
    this.m_thresholdListeners.remove(paramThresholdDataListener);
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
  
  private void notifyThresholdListeners(ThresholdDataEvent paramThresholdDataEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_thresholdListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((ThresholdDataListener)vector.elementAt(b)).acceptDataSet(paramThresholdDataEvent);  
  }
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : (!(this.m_listenee instanceof EventConstraints && !((EventConstraints)this.m_listenee).eventGeneratable("batchClassifier")));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassifierPerformanceEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */