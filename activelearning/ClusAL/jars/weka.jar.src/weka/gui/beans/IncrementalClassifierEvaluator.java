package weka.gui.beans;

import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class IncrementalClassifierEvaluator extends AbstractEvaluator implements IncrementalClassifierListener, EventConstraints {
  private transient Evaluation m_eval;
  
  private transient Classifier m_classifier;
  
  private Vector m_listeners = new Vector();
  
  private Vector m_textListeners = new Vector();
  
  private Vector m_dataLegend = new Vector();
  
  private ChartEvent m_ce = new ChartEvent(this);
  
  private double[] m_dataPoint = new double[1];
  
  private boolean m_reset = false;
  
  private double m_min = Double.MAX_VALUE;
  
  private double m_max = Double.MIN_VALUE;
  
  public IncrementalClassifierEvaluator() {
    this.m_visual.loadIcons("weka/gui/beans/icons/IncrementalClassifierEvaluator.gif", "weka/gui/beans/icons/IncrementalClassifierEvaluator_animated.gif");
    this.m_visual.setText("IncrementalClassifierEvaluator");
  }
  
  public String globalInfo() {
    return "Evaluate the performance of incrementally trained classifiers.";
  }
  
  public void acceptClassifier(IncrementalClassifierEvent paramIncrementalClassifierEvent) {
    try {
      if (paramIncrementalClassifierEvent.getStatus() == 0) {
        this.m_eval = new Evaluation(paramIncrementalClassifierEvent.getStructure());
        this.m_dataLegend = new Vector();
        this.m_reset = true;
        this.m_dataPoint = new double[0];
        Instances instances = paramIncrementalClassifierEvent.getStructure();
        System.err.println("NEW BATCH");
      } else {
        Instance instance = paramIncrementalClassifierEvent.getCurrentInstance();
        double[] arrayOfDouble = paramIncrementalClassifierEvent.getClassifier().distributionForInstance(instance);
        double d = 0.0D;
        if (!instance.isMissing(instance.classIndex())) {
          this.m_eval.evaluateModelOnce(arrayOfDouble, instance);
        } else {
          d = paramIncrementalClassifierEvent.getClassifier().classifyInstance(instance);
        } 
        if (instance.classIndex() >= 0) {
          if (instance.attribute(instance.classIndex()).isNominal()) {
            if (!instance.isMissing(instance.classIndex())) {
              if (this.m_dataPoint.length < 2) {
                this.m_dataPoint = new double[2];
                this.m_dataLegend.addElement("Accuracy");
                this.m_dataLegend.addElement("RMSE (prob)");
              } 
              this.m_dataPoint[1] = this.m_eval.rootMeanSquaredError();
            } else if (this.m_dataPoint.length < 1) {
              this.m_dataPoint = new double[1];
              this.m_dataLegend.addElement("Confidence");
            } 
            double d1 = 0.0D;
            if (!instance.isMissing(instance.classIndex())) {
              d1 = 1.0D - this.m_eval.errorRate();
            } else {
              d1 = arrayOfDouble[Utils.maxIndex(arrayOfDouble)];
            } 
            this.m_dataPoint[0] = d1;
            this.m_ce.setLegendText(this.m_dataLegend);
            this.m_ce.setMin(0.0D);
            this.m_ce.setMax(1.0D);
            this.m_ce.setDataPoint(this.m_dataPoint);
            this.m_ce.setReset(this.m_reset);
            this.m_reset = false;
          } else {
            if (this.m_dataPoint.length < 1) {
              this.m_dataPoint = new double[1];
              if (instance.isMissing(instance.classIndex())) {
                this.m_dataLegend.addElement("Prediction");
              } else {
                this.m_dataLegend.addElement("RMSE");
              } 
            } 
            if (!instance.isMissing(instance.classIndex())) {
              double d1;
              if (!instance.isMissing(instance.classIndex())) {
                d1 = this.m_eval.rootMeanSquaredError();
              } else {
                d1 = d;
              } 
              this.m_dataPoint[0] = d1;
              if (d1 > this.m_max)
                this.m_max = d1; 
              if (d1 < this.m_min)
                this.m_min = d1; 
            } 
            this.m_ce.setLegendText(this.m_dataLegend);
            this.m_ce.setMin(instance.isMissing(instance.classIndex()) ? this.m_min : 0.0D);
            this.m_ce.setMax(this.m_max);
            this.m_ce.setDataPoint(this.m_dataPoint);
            this.m_ce.setReset(this.m_reset);
            this.m_reset = false;
          } 
          notifyChartListeners(this.m_ce);
          if (paramIncrementalClassifierEvent.getStatus() == 2 && this.m_textListeners.size() > 0) {
            String str1 = paramIncrementalClassifierEvent.getClassifier().getClass().getName();
            str1 = str1.substring(str1.lastIndexOf('.') + 1, str1.length());
            String str2 = "=== Performance information ===\n\nScheme:   " + str1 + "\n" + "Relation: " + instance.dataset().relationName() + "\n\n" + this.m_eval.toSummaryString();
            str1 = "Results: " + str1;
            TextEvent textEvent = new TextEvent(this, str2, str1);
            notifyTextListeners(textEvent);
          } 
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public boolean eventGeneratable(String paramString) {
    return (this.m_listenee == null) ? false : (!(this.m_listenee instanceof EventConstraints && !((EventConstraints)this.m_listenee).eventGeneratable("incrementalClassifier")));
  }
  
  public void stop() {}
  
  private void notifyChartListeners(ChartEvent paramChartEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_listeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((ChartListener)vector.elementAt(b)).acceptDataPoint(paramChartEvent);  
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
  
  public synchronized void addChartListener(ChartListener paramChartListener) {
    this.m_listeners.addElement(paramChartListener);
  }
  
  public synchronized void removeChartListener(ChartListener paramChartListener) {
    this.m_listeners.remove(paramChartListener);
  }
  
  public synchronized void addTextListener(TextListener paramTextListener) {
    this.m_textListeners.addElement(paramTextListener);
  }
  
  public synchronized void removeTextListener(TextListener paramTextListener) {
    this.m_textListeners.remove(paramTextListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\IncrementalClassifierEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */