package weka.gui.beans;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JPanel;
import weka.core.Instances;
import weka.gui.Logger;

public class ClassAssigner extends JPanel implements Visible, DataSourceListener, TrainingSetListener, TestSetListener, DataSource, TrainingSetProducer, TestSetProducer, BeanCommon, EventConstraints, Serializable, InstanceListener {
  private String m_classColumn = "last";
  
  private Instances m_connectedFormat;
  
  private Object m_trainingProvider;
  
  private Object m_testProvider;
  
  private Object m_dataProvider;
  
  private Object m_instanceProvider;
  
  private Vector m_trainingListeners = new Vector();
  
  private Vector m_testListeners = new Vector();
  
  private Vector m_dataListeners = new Vector();
  
  private Vector m_instanceListeners = new Vector();
  
  private Vector m_dataFormatListeners = new Vector();
  
  protected transient Logger m_logger = null;
  
  protected BeanVisual m_visual = new BeanVisual("ClassAssigner", "weka/gui/beans/icons/ClassAssigner.gif", "weka/gui/beans/icons/ClassAssigner_animated.gif");
  
  public String globalInfo() {
    return "Designate which column is to be considered the class column in incoming data.";
  }
  
  public ClassAssigner() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  public String classColumnTipText() {
    return "Specify the number of the column that contains the class attribute";
  }
  
  public Instances getConnectedFormat() {
    return this.m_connectedFormat;
  }
  
  public void setClassColumn(String paramString) {
    this.m_classColumn = paramString;
    if (this.m_connectedFormat != null)
      assignClass(this.m_connectedFormat); 
  }
  
  public String getClassColumn() {
    return this.m_classColumn;
  }
  
  public void acceptDataSet(DataSetEvent paramDataSetEvent) {
    Instances instances = paramDataSetEvent.getDataSet();
    assignClass(instances);
    notifyDataListeners(paramDataSetEvent);
    if (paramDataSetEvent.isStructureOnly()) {
      this.m_connectedFormat = paramDataSetEvent.getDataSet();
      notifyDataFormatListeners();
    } 
  }
  
  public void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    Instances instances = paramTrainingSetEvent.getTrainingSet();
    assignClass(instances);
    notifyTrainingListeners(paramTrainingSetEvent);
  }
  
  public void acceptTestSet(TestSetEvent paramTestSetEvent) {
    Instances instances = paramTestSetEvent.getTestSet();
    assignClass(instances);
    notifyTestListeners(paramTestSetEvent);
  }
  
  public void acceptInstance(InstanceEvent paramInstanceEvent) {
    if (paramInstanceEvent.getStatus() == 0) {
      this.m_connectedFormat = paramInstanceEvent.getStructure();
      assignClass(this.m_connectedFormat);
      notifyInstanceListeners(paramInstanceEvent);
      System.err.println("Notifying customizer...");
      notifyDataFormatListeners();
    } else {
      notifyInstanceListeners(paramInstanceEvent);
    } 
  }
  
  private void assignClass(Instances paramInstances) {
    int i = -1;
    if (this.m_classColumn.toLowerCase().compareTo("last") == 0) {
      paramInstances.setClassIndex(paramInstances.numAttributes() - 1);
    } else if (this.m_classColumn.toLowerCase().compareTo("first") == 0) {
      paramInstances.setClassIndex(0);
    } else {
      i = Integer.parseInt(this.m_classColumn) - 1;
      if (i < 0 || i > paramInstances.numAttributes() - 1) {
        if (this.m_logger != null)
          this.m_logger.logMessage("Class column outside range of data (ClassAssigner)"); 
      } else {
        paramInstances.setClassIndex(i);
      } 
    } 
  }
  
  protected void notifyTestListeners(TestSetEvent paramTestSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_testListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying test listeners (ClassAssigner)");
        ((TestSetListener)vector.elementAt(b)).acceptTestSet(paramTestSetEvent);
      }  
  }
  
  protected void notifyTrainingListeners(TrainingSetEvent paramTrainingSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_trainingListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying training listeners (ClassAssigner)");
        ((TrainingSetListener)vector.elementAt(b)).acceptTrainingSet(paramTrainingSetEvent);
      }  
  }
  
  protected void notifyDataListeners(DataSetEvent paramDataSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_dataListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying data listeners (ClassAssigner)");
        ((DataSourceListener)vector.elementAt(b)).acceptDataSet(paramDataSetEvent);
      }  
  }
  
  protected void notifyInstanceListeners(InstanceEvent paramInstanceEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_instanceListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((InstanceListener)vector.elementAt(b)).acceptInstance(paramInstanceEvent);  
  }
  
  protected void notifyDataFormatListeners() {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_dataFormatListeners.clone();
    } 
    if (vector.size() > 0) {
      DataSetEvent dataSetEvent = new DataSetEvent(this, this.m_connectedFormat);
      for (byte b = 0; b < vector.size(); b++)
        ((DataFormatListener)vector.elementAt(b)).newDataFormat(dataSetEvent); 
    } 
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    this.m_instanceListeners.addElement(paramInstanceListener);
    if (this.m_connectedFormat != null) {
      InstanceEvent instanceEvent = new InstanceEvent(this, this.m_connectedFormat);
      paramInstanceListener.acceptInstance(instanceEvent);
    } 
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    this.m_instanceListeners.removeElement(paramInstanceListener);
  }
  
  public synchronized void addDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataListeners.addElement(paramDataSourceListener);
    if (this.m_connectedFormat != null) {
      DataSetEvent dataSetEvent = new DataSetEvent(this, this.m_connectedFormat);
      paramDataSourceListener.acceptDataSet(dataSetEvent);
    } 
  }
  
  public synchronized void removeDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataListeners.removeElement(paramDataSourceListener);
  }
  
  public synchronized void addTrainingSetListener(TrainingSetListener paramTrainingSetListener) {
    this.m_trainingListeners.addElement(paramTrainingSetListener);
    if (this.m_connectedFormat != null) {
      TrainingSetEvent trainingSetEvent = new TrainingSetEvent(this, this.m_connectedFormat);
      paramTrainingSetListener.acceptTrainingSet(trainingSetEvent);
    } 
  }
  
  public synchronized void removeTrainingSetListener(TrainingSetListener paramTrainingSetListener) {
    this.m_trainingListeners.removeElement(paramTrainingSetListener);
  }
  
  public synchronized void addTestSetListener(TestSetListener paramTestSetListener) {
    this.m_testListeners.addElement(paramTestSetListener);
    if (this.m_connectedFormat != null) {
      TestSetEvent testSetEvent = new TestSetEvent(this, this.m_connectedFormat);
      paramTestSetListener.acceptTestSet(testSetEvent);
    } 
  }
  
  public synchronized void removeTestSetListener(TestSetListener paramTestSetListener) {
    this.m_testListeners.removeElement(paramTestSetListener);
  }
  
  public synchronized void addDataFormatListener(DataFormatListener paramDataFormatListener) {
    this.m_dataFormatListeners.addElement(paramDataFormatListener);
  }
  
  public synchronized void removeDataFormatListener(DataFormatListener paramDataFormatListener) {
    this.m_dataFormatListeners.removeElement(paramDataFormatListener);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/ClassAssigner.gif", "weka/gui/beans/icons/ClassAssigner_animated.gif");
  }
  
  public boolean connectionAllowed(String paramString) {
    return (paramString.compareTo("trainingSet") == 0 && (this.m_trainingProvider != null || this.m_dataProvider != null || this.m_instanceProvider != null)) ? false : ((paramString.compareTo("testSet") == 0 && this.m_testProvider != null) ? false : (!((paramString.compareTo("instance") == 0 && this.m_instanceProvider != null) || this.m_trainingProvider != null || this.m_dataProvider != null)));
  }
  
  public synchronized void connectionNotification(String paramString, Object paramObject) {
    if (connectionAllowed(paramString))
      if (paramString.compareTo("trainingSet") == 0) {
        this.m_trainingProvider = paramObject;
      } else if (paramString.compareTo("testSet") == 0) {
        this.m_testProvider = paramObject;
      } else if (paramString.compareTo("dataSet") == 0) {
        this.m_dataProvider = paramObject;
      } else if (paramString.compareTo("instance") == 0) {
        this.m_instanceProvider = paramObject;
      }  
  }
  
  public synchronized void disconnectionNotification(String paramString, Object paramObject) {
    if (paramString.compareTo("trainingSet") == 0 && this.m_trainingProvider == paramObject)
      this.m_trainingProvider = null; 
    if (paramString.compareTo("testSet") == 0 && this.m_testProvider == paramObject)
      this.m_testProvider = null; 
    if (paramString.compareTo("dataSet") == 0 && this.m_dataProvider == paramObject)
      this.m_dataProvider = null; 
    if (paramString.compareTo("instance") == 0 && this.m_instanceProvider == paramObject)
      this.m_instanceProvider = null; 
  }
  
  public void setLog(Logger paramLogger) {
    this.m_logger = paramLogger;
  }
  
  public void stop() {}
  
  public boolean eventGeneratable(String paramString) {
    if (paramString.compareTo("trainingSet") == 0) {
      if (this.m_trainingProvider == null)
        return false; 
      if (this.m_trainingProvider instanceof EventConstraints && !((EventConstraints)this.m_trainingProvider).eventGeneratable("trainingSet"))
        return false; 
    } 
    if (paramString.compareTo("dataSet") == 0) {
      if (this.m_dataProvider == null) {
        if (this.m_instanceProvider == null) {
          this.m_connectedFormat = null;
          notifyDataFormatListeners();
        } 
        return false;
      } 
      if (this.m_dataProvider instanceof EventConstraints && !((EventConstraints)this.m_dataProvider).eventGeneratable("dataSet")) {
        this.m_connectedFormat = null;
        notifyDataFormatListeners();
        return false;
      } 
    } 
    if (paramString.compareTo("instance") == 0) {
      if (this.m_instanceProvider == null) {
        if (this.m_dataProvider == null) {
          this.m_connectedFormat = null;
          notifyDataFormatListeners();
        } 
        return false;
      } 
      if (this.m_instanceProvider instanceof EventConstraints && !((EventConstraints)this.m_instanceProvider).eventGeneratable("instance")) {
        this.m_connectedFormat = null;
        notifyDataFormatListeners();
        return false;
      } 
    } 
    if (paramString.compareTo("testSet") == 0) {
      if (this.m_testProvider == null)
        return false; 
      if (this.m_testProvider instanceof EventConstraints && !((EventConstraints)this.m_testProvider).eventGeneratable("testSet"))
        return false; 
    } 
    return true;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassAssigner.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */