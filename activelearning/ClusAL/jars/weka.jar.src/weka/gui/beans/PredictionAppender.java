package weka.gui.beans;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JPanel;
import weka.classifiers.Classifier;
import weka.clusterers.Clusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.gui.Logger;

public class PredictionAppender extends JPanel implements DataSource, Visible, BeanCommon, EventConstraints, BatchClassifierListener, IncrementalClassifierListener, BatchClustererListener, Serializable {
  protected Vector m_dataSourceListeners = new Vector();
  
  protected Vector m_instanceListeners = new Vector();
  
  protected Object m_listenee = null;
  
  protected Instances m_format;
  
  protected BeanVisual m_visual = new BeanVisual("PredictionAppender", "weka/gui/beans/icons/PredictionAppender.gif", "weka/gui/beans/icons/PredictionAppender_animated.gif");
  
  protected boolean m_appendProbabilities;
  
  protected transient Logger m_logger;
  
  protected InstanceEvent m_instanceEvent;
  
  protected double[] m_instanceVals;
  
  public String globalInfo() {
    return "Accepts batch or incremental classifier events and produces a new data set with classifier predictions appended.";
  }
  
  public PredictionAppender() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  public String appendPredictedProbabilitiesTipText() {
    return "append probabilities rather than labels for discrete class predictions";
  }
  
  public boolean getAppendPredictedProbabilities() {
    return this.m_appendProbabilities;
  }
  
  public void setAppendPredictedProbabilities(boolean paramBoolean) {
    this.m_appendProbabilities = paramBoolean;
  }
  
  public synchronized void addDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataSourceListeners.addElement(paramDataSourceListener);
    if (this.m_format != null) {
      DataSetEvent dataSetEvent = new DataSetEvent(this, this.m_format);
      paramDataSourceListener.acceptDataSet(dataSetEvent);
    } 
  }
  
  public synchronized void removeDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataSourceListeners.remove(paramDataSourceListener);
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    this.m_instanceListeners.addElement(paramInstanceListener);
    if (this.m_format != null) {
      InstanceEvent instanceEvent = new InstanceEvent(this, this.m_format);
      paramInstanceListener.acceptInstance(instanceEvent);
    } 
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    this.m_instanceListeners.remove(paramInstanceListener);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/PredictionAppender.gif", "weka/gui/beans/icons/PredictionAppender_animated.gif");
  }
  
  public void acceptClassifier(IncrementalClassifierEvent paramIncrementalClassifierEvent) {
    Classifier classifier = paramIncrementalClassifierEvent.getClassifier();
    Instance instance = paramIncrementalClassifierEvent.getCurrentInstance();
    int i = paramIncrementalClassifierEvent.getStatus();
    int j = 0;
    if (i == 0) {
      j = paramIncrementalClassifierEvent.getStructure().numAttributes();
    } else {
      j = instance.dataset().numAttributes();
    } 
    if (i == 0) {
      this.m_instanceEvent = new InstanceEvent(this, null, 0);
      Instances instances = new Instances(paramIncrementalClassifierEvent.getStructure(), 0);
      String str = "_with predictions";
      if (!this.m_appendProbabilities || instances.classAttribute().isNumeric()) {
        try {
          this.m_format = makeDataSetClass(instances, classifier, str);
          this.m_instanceVals = new double[this.m_format.numAttributes()];
        } catch (Exception exception) {
          exception.printStackTrace();
          return;
        } 
      } else if (this.m_appendProbabilities) {
        try {
          this.m_format = makeDataSetProbabilities(instances, classifier, str);
          this.m_instanceVals = new double[this.m_format.numAttributes()];
        } catch (Exception exception) {
          exception.printStackTrace();
          return;
        } 
      } 
      this.m_instanceEvent.setStructure(this.m_format);
      notifyInstanceAvailable(this.m_instanceEvent);
      return;
    } 
    try {
      for (byte b = 0; b < j; b++)
        this.m_instanceVals[b] = instance.value(b); 
      if (!this.m_appendProbabilities || instance.dataset().classAttribute().isNumeric()) {
        double d = classifier.classifyInstance(instance);
        this.m_instanceVals[this.m_instanceVals.length - 1] = d;
      } else if (this.m_appendProbabilities) {
        double[] arrayOfDouble = classifier.distributionForInstance(instance);
        for (int k = j; k < this.m_instanceVals.length; k++)
          this.m_instanceVals[k] = arrayOfDouble[k - j]; 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    } finally {
      Instance instance1 = new Instance(instance.weight(), this.m_instanceVals);
      instance1.setDataset(this.m_format);
      this.m_instanceEvent.setInstance(instance1);
      this.m_instanceEvent.setStatus(i);
      notifyInstanceAvailable(this.m_instanceEvent);
    } 
    if (i == 2) {
      this.m_instanceVals = null;
      this.m_instanceEvent = null;
    } 
  }
  
  public void acceptClassifier(BatchClassifierEvent paramBatchClassifierEvent) {
    if (this.m_dataSourceListeners.size() > 0) {
      Instances instances = paramBatchClassifierEvent.getTestSet().getDataSet();
      Classifier classifier = paramBatchClassifierEvent.getClassifier();
      String str = "_set_" + paramBatchClassifierEvent.getSetNumber() + "_of_" + paramBatchClassifierEvent.getMaxSetNumber();
      if (!this.m_appendProbabilities || instances.classAttribute().isNumeric())
        try {
          Instances instances1 = makeDataSetClass(instances, classifier, str);
          notifyDataSetAvailable(new DataSetEvent(this, new Instances(instances1, 0)));
          if (paramBatchClassifierEvent.getTestSet().isStructureOnly())
            this.m_format = instances1; 
          for (byte b = 0; b < instances.numInstances(); b++) {
            double d = classifier.classifyInstance(instances.instance(b));
            instances1.instance(b).setValue(instances1.numAttributes() - 1, d);
          } 
          notifyDataSetAvailable(new DataSetEvent(this, instances1));
          return;
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
      if (this.m_appendProbabilities)
        try {
          Instances instances1 = makeDataSetProbabilities(instances, classifier, str);
          notifyDataSetAvailable(new DataSetEvent(this, new Instances(instances1, 0)));
          if (paramBatchClassifierEvent.getTestSet().isStructureOnly())
            this.m_format = instances1; 
          for (byte b = 0; b < instances.numInstances(); b++) {
            double[] arrayOfDouble = classifier.distributionForInstance(instances.instance(b));
            for (byte b1 = 0; b1 < instances.classAttribute().numValues(); b1++)
              instances1.instance(b).setValue(instances.numAttributes() + b1, arrayOfDouble[b1]); 
          } 
          notifyDataSetAvailable(new DataSetEvent(this, instances1));
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
    } 
  }
  
  public void acceptClusterer(BatchClustererEvent paramBatchClustererEvent) {
    if (this.m_dataSourceListeners.size() > 0) {
      String str1;
      if (paramBatchClustererEvent.getTestSet().isStructureOnly())
        return; 
      Instances instances = paramBatchClustererEvent.getTestSet().getDataSet();
      Clusterer clusterer = paramBatchClustererEvent.getClusterer();
      if (paramBatchClustererEvent.getTestOrTrain() == 0) {
        str1 = "test";
      } else {
        str1 = "training";
      } 
      String str2 = "_" + str1 + "_" + paramBatchClustererEvent.getSetNumber() + "_of_" + paramBatchClustererEvent.getMaxSetNumber();
      if (!this.m_appendProbabilities || !(clusterer instanceof weka.clusterers.DensityBasedClusterer)) {
        if (this.m_appendProbabilities && !(clusterer instanceof weka.clusterers.DensityBasedClusterer)) {
          System.err.println("Only density based clusterers can append probabilities. Instead cluster will be assigned for each instance.");
          if (this.m_logger != null)
            this.m_logger.logMessage("Only density based clusterers can append probabilities. Instead cluster will be assigned for each instance."); 
        } 
        try {
          Instances instances1 = makeClusterDataSetClass(instances, clusterer, str2);
          notifyDataSetAvailable(new DataSetEvent(this, new Instances(instances1, 0)));
          for (byte b = 0; b < instances.numInstances(); b++) {
            double d = clusterer.clusterInstance(instances.instance(b));
            instances1.instance(b).setValue(instances1.numAttributes() - 1, d);
          } 
          notifyDataSetAvailable(new DataSetEvent(this, instances1));
          return;
        } catch (Exception exception) {
          exception.printStackTrace();
        } 
      } else {
        try {
          Instances instances1 = makeClusterDataSetProbabilities(instances, clusterer, str2);
          notifyDataSetAvailable(new DataSetEvent(this, new Instances(instances1, 0)));
          for (byte b = 0; b < instances.numInstances(); b++) {
            double[] arrayOfDouble = clusterer.distributionForInstance(instances.instance(b));
            for (byte b1 = 0; b1 < clusterer.numberOfClusters(); b1++)
              instances1.instance(b).setValue(instances.numAttributes() + b1, arrayOfDouble[b1]); 
          } 
          notifyDataSetAvailable(new DataSetEvent(this, instances1));
        } catch (Exception exception) {
          exception.printStackTrace();
        } 
      } 
    } 
  }
  
  private Instances makeDataSetProbabilities(Instances paramInstances, Classifier paramClassifier, String paramString) throws Exception {
    int i = paramInstances.numAttributes();
    Instances instances = new Instances(paramInstances);
    for (byte b = 0; b < paramInstances.classAttribute().numValues(); b++) {
      Add add = new Add();
      add.setAttributeIndex("last");
      add.setAttributeName("prob_" + paramInstances.classAttribute().value(b));
      add.setInputFormat(instances);
      instances = Filter.useFilter(instances, (Filter)add);
    } 
    instances.setRelationName(paramInstances.relationName() + paramString);
    return instances;
  }
  
  private Instances makeDataSetClass(Instances paramInstances, Classifier paramClassifier, String paramString) throws Exception {
    Add add = new Add();
    add.setAttributeIndex("last");
    String str = paramClassifier.getClass().getName();
    str = str.substring(str.lastIndexOf('.') + 1, str.length());
    add.setAttributeName("class_predicted_by: " + str);
    if (paramInstances.classAttribute().isNominal()) {
      String str1 = "";
      Enumeration enumeration = paramInstances.classAttribute().enumerateValues();
      for (str1 = str1 + (String)enumeration.nextElement(); enumeration.hasMoreElements(); str1 = str1 + "," + (String)enumeration.nextElement());
      add.setNominalLabels(str1);
    } 
    add.setInputFormat(paramInstances);
    Instances instances = Filter.useFilter(paramInstances, (Filter)add);
    instances.setRelationName(paramInstances.relationName() + paramString);
    return instances;
  }
  
  private Instances makeClusterDataSetProbabilities(Instances paramInstances, Clusterer paramClusterer, String paramString) throws Exception {
    int i = paramInstances.numAttributes();
    Instances instances = new Instances(paramInstances);
    for (byte b = 0; b < paramClusterer.numberOfClusters(); b++) {
      Add add = new Add();
      add.setAttributeIndex("last");
      add.setAttributeName("prob_cluster" + b);
      add.setInputFormat(instances);
      instances = Filter.useFilter(instances, (Filter)add);
    } 
    instances.setRelationName(paramInstances.relationName() + paramString);
    return instances;
  }
  
  private Instances makeClusterDataSetClass(Instances paramInstances, Clusterer paramClusterer, String paramString) throws Exception {
    Add add = new Add();
    add.setAttributeIndex("last");
    String str1 = paramClusterer.getClass().getName();
    str1 = str1.substring(str1.lastIndexOf('.') + 1, str1.length());
    add.setAttributeName("assigned_cluster: " + str1);
    String str2 = "0";
    for (byte b = 1; b <= paramClusterer.numberOfClusters() - 1; b++)
      str2 = str2 + "," + b; 
    add.setNominalLabels(str2);
    add.setInputFormat(paramInstances);
    Instances instances = Filter.useFilter(paramInstances, (Filter)add);
    instances.setRelationName(paramInstances.relationName() + paramString);
    return instances;
  }
  
  protected void notifyInstanceAvailable(InstanceEvent paramInstanceEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_instanceListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((InstanceListener)vector.elementAt(b)).acceptInstance(paramInstanceEvent);  
  }
  
  protected void notifyDataSetAvailable(DataSetEvent paramDataSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_dataSourceListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((DataSourceListener)vector.elementAt(b)).acceptDataSet(paramDataSetEvent);  
  }
  
  public void setLog(Logger paramLogger) {
    this.m_logger = paramLogger;
  }
  
  public void stop() {}
  
  public boolean connectionAllowed(String paramString) {
    return (this.m_listenee == null);
  }
  
  public synchronized void connectionNotification(String paramString, Object paramObject) {
    if (connectionAllowed(paramString))
      this.m_listenee = paramObject; 
  }
  
  public synchronized void disconnectionNotification(String paramString, Object paramObject) {
    if (this.m_listenee == paramObject) {
      this.m_listenee = null;
      this.m_format = null;
    } 
  }
  
  public boolean eventGeneratable(String paramString) {
    if (this.m_listenee == null)
      return false; 
    if (this.m_listenee instanceof EventConstraints) {
      if (paramString.equals("instance") && !((EventConstraints)this.m_listenee).eventGeneratable("incrementalClassifier"))
        return false; 
      if (paramString.equals("dataSet") && !((EventConstraints)this.m_listenee).eventGeneratable("batchClassifier") && !((EventConstraints)this.m_listenee).eventGeneratable("batchClusterer"))
        return false; 
    } 
    return true;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\PredictionAppender.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */