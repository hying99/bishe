package weka.gui.beans;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JPanel;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.SwapValues;
import weka.gui.Logger;

public class ClassValuePicker extends JPanel implements Visible, DataSourceListener, BeanCommon, EventConstraints, Serializable {
  private int m_classValueIndex = 0;
  
  private Instances m_connectedFormat;
  
  private Object m_dataProvider;
  
  private Vector m_dataListeners = new Vector();
  
  private Vector m_dataFormatListeners = new Vector();
  
  protected transient Logger m_logger = null;
  
  protected BeanVisual m_visual = new BeanVisual("ClassValuePicker", "weka/gui/beans/icons/ClassValuePicker.gif", "weka/gui/beans/icons/ClassValuePicker_animated.gif");
  
  public String globalInfo() {
    return "Designate which class value is to be considered the \"positive\" class value (useful for ROC style curves).";
  }
  
  public ClassValuePicker() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  public Instances getConnectedFormat() {
    if (this.m_connectedFormat == null)
      System.err.println("Is null!!!!!!"); 
    return this.m_connectedFormat;
  }
  
  public void setClassValueIndex(int paramInt) {
    this.m_classValueIndex = paramInt;
    if (this.m_connectedFormat != null)
      notifyDataFormatListeners(); 
  }
  
  public int getClassValueIndex() {
    return this.m_classValueIndex;
  }
  
  public void acceptDataSet(DataSetEvent paramDataSetEvent) {
    if (paramDataSetEvent.isStructureOnly() && (this.m_connectedFormat == null || !this.m_connectedFormat.equalHeaders(paramDataSetEvent.getDataSet()))) {
      this.m_connectedFormat = new Instances(paramDataSetEvent.getDataSet(), 0);
      notifyDataFormatListeners();
    } 
    Instances instances1 = paramDataSetEvent.getDataSet();
    Instances instances2 = assignClassValue(instances1);
    paramDataSetEvent = new DataSetEvent(this, instances2);
    notifyDataListeners(paramDataSetEvent);
  }
  
  private Instances assignClassValue(Instances paramInstances) {
    if (paramInstances.classIndex() < 0) {
      if (this.m_logger != null)
        this.m_logger.logMessage("No class attribute defined in data set (ClassValuePicker)"); 
      return paramInstances;
    } 
    if (paramInstances.classAttribute().isNumeric()) {
      if (this.m_logger != null)
        this.m_logger.logMessage("Class attribute must be nominal (ClassValuePicker)"); 
      return paramInstances;
    } 
    if (this.m_classValueIndex != 0)
      try {
        SwapValues swapValues = new SwapValues();
        swapValues.setAttributeIndex("" + (paramInstances.classIndex() + 1));
        swapValues.setFirstValueIndex("first");
        swapValues.setSecondValueIndex("" + (this.m_classValueIndex + 1));
        swapValues.setInputFormat(paramInstances);
        Instances instances = Filter.useFilter(paramInstances, (Filter)swapValues);
        instances.setRelationName(paramInstances.relationName());
        return instances;
      } catch (Exception exception) {
        if (this.m_logger != null)
          this.m_logger.logMessage("Unable to swap class attibute values (ClassValuePicker)"); 
      }  
    return paramInstances;
  }
  
  protected void notifyDataListeners(DataSetEvent paramDataSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_dataListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        System.err.println("Notifying data listeners (ClassValuePicker)");
        ((DataSourceListener)vector.elementAt(b)).acceptDataSet(paramDataSetEvent);
      }  
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
  
  public synchronized void addDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataListeners.addElement(paramDataSourceListener);
  }
  
  public synchronized void removeDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataListeners.removeElement(paramDataSourceListener);
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
    this.m_visual.loadIcons("weka/gui/beans/icons/ClassValuePicker.gif", "weka/gui/beans/icons/ClassValuePicker_animated.gif");
  }
  
  public boolean connectionAllowed(String paramString) {
    return !(paramString.compareTo("dataSet") == 0 && this.m_dataProvider != null);
  }
  
  public synchronized void connectionNotification(String paramString, Object paramObject) {
    if (connectionAllowed(paramString) && paramString.compareTo("dataSet") == 0)
      this.m_dataProvider = paramObject; 
  }
  
  public synchronized void disconnectionNotification(String paramString, Object paramObject) {
    if (paramString.compareTo("dataSet") == 0 && this.m_dataProvider == paramObject)
      this.m_dataProvider = null; 
  }
  
  public void setLog(Logger paramLogger) {
    this.m_logger = paramLogger;
  }
  
  public void stop() {}
  
  public boolean eventGeneratable(String paramString) {
    if (paramString.compareTo("dataSet") != 0)
      return false; 
    if (paramString.compareTo("dataSet") == 0) {
      if (this.m_dataProvider == null) {
        this.m_connectedFormat = null;
        notifyDataFormatListeners();
        return false;
      } 
      if (this.m_dataProvider instanceof EventConstraints && !((EventConstraints)this.m_dataProvider).eventGeneratable("dataSet")) {
        this.m_connectedFormat = null;
        notifyDataFormatListeners();
        return false;
      } 
    } 
    return true;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\ClassValuePicker.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */