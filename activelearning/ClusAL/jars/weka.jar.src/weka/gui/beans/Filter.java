package weka.gui.beans;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JPanel;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.gui.Logger;

public class Filter extends JPanel implements BeanCommon, Visible, WekaWrapper, Serializable, UserRequestAcceptor, TrainingSetListener, TestSetListener, TrainingSetProducer, TestSetProducer, DataSource, DataSourceListener, InstanceListener, EventConstraints {
  protected BeanVisual m_visual = new BeanVisual("Filter", "weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
  
  private static int IDLE = 0;
  
  private static int FILTERING_TRAINING = 1;
  
  private static int FILTERING_TEST = 2;
  
  private int m_state = IDLE;
  
  protected Thread m_filterThread = null;
  
  private transient Instances m_trainingSet;
  
  private transient Instances m_testingSet;
  
  protected String m_globalInfo;
  
  private Hashtable m_listenees = new Hashtable();
  
  private Vector m_trainingListeners = new Vector();
  
  private Vector m_testListeners = new Vector();
  
  private Vector m_instanceListeners = new Vector();
  
  private Vector m_dataListeners = new Vector();
  
  private weka.filters.Filter m_Filter = (weka.filters.Filter)new AllFilter();
  
  private InstanceEvent m_ie = new InstanceEvent(this);
  
  private transient Logger m_log = null;
  
  private boolean m_structurePassedOn = false;
  
  public String globalInfo() {
    return this.m_globalInfo;
  }
  
  public Filter() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
    setFilter(this.m_Filter);
  }
  
  public void setFilter(weka.filters.Filter paramFilter) {
    boolean bool = true;
    if (paramFilter.getClass().getName().compareTo(this.m_Filter.getClass().getName()) == 0)
      bool = false; 
    this.m_Filter = paramFilter;
    String str = paramFilter.getClass().toString();
    str = str.substring(str.indexOf('.') + 1, str.length());
    if (bool && !this.m_visual.loadIcons("weka/gui/beans/icons/" + str + ".gif", "weka/gui/beans/icons/" + str + "_animated.gif"))
      useDefaultVisual(); 
    this.m_visual.setText(str.substring(str.lastIndexOf('.') + 1, str.length()));
    if (!(this.m_Filter instanceof weka.filters.StreamableFilter) && this.m_listenees.containsKey("instance") && this.m_log != null)
      this.m_log.logMessage("WARNING : " + this.m_Filter.getClass().getName() + " is not an incremental filter"); 
    this.m_globalInfo = KnowledgeFlow.getGlobalInfo(this.m_Filter);
  }
  
  public weka.filters.Filter getFilter() {
    return this.m_Filter;
  }
  
  public void setWrappedAlgorithm(Object paramObject) {
    if (!(paramObject instanceof weka.filters.Filter))
      throw new IllegalArgumentException(paramObject.getClass() + " : incorrect " + "type of algorithm (Filter)"); 
    setFilter((weka.filters.Filter)paramObject);
  }
  
  public Object getWrappedAlgorithm() {
    return getFilter();
  }
  
  public void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    processTrainingOrDataSourceEvents(paramTrainingSetEvent);
  }
  
  public void acceptInstance(InstanceEvent paramInstanceEvent) {
    if (this.m_filterThread != null) {
      String str = "Filter is currently batch processing!";
      if (this.m_log != null) {
        this.m_log.logMessage(str);
      } else {
        System.err.println(str);
      } 
      return;
    } 
    if (!(this.m_Filter instanceof weka.filters.StreamableFilter)) {
      if (this.m_log != null)
        this.m_log.logMessage("ERROR : " + this.m_Filter.getClass().getName() + "can't process streamed instances; can't continue"); 
      return;
    } 
    if (paramInstanceEvent.getStatus() == 0) {
      try {
        Instances instances = paramInstanceEvent.getStructure();
        if (this.m_Filter instanceof weka.filters.SupervisedFilter && instances.classIndex() < 0)
          instances.setClassIndex(instances.numAttributes() - 1); 
        this.m_Filter.setInputFormat(instances);
        this.m_structurePassedOn = false;
        try {
          if (this.m_Filter.isOutputFormatDefined()) {
            this.m_ie.setStructure(this.m_Filter.getOutputFormat());
            notifyInstanceListeners(this.m_ie);
            this.m_structurePassedOn = true;
          } 
        } catch (Exception exception) {
          System.err.println("Error in obtaining post-filter structure: Filter.java");
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      return;
    } 
    try {
      if (!this.m_Filter.input(paramInstanceEvent.getInstance())) {
        if (this.m_log != null)
          this.m_log.logMessage("ERROR : filter not ready to output instance"); 
        return;
      } 
      Instance instance = this.m_Filter.output();
      if (instance == null)
        return; 
      if (!this.m_structurePassedOn) {
        this.m_ie.setStructure(new Instances(instance.dataset(), 0));
        notifyInstanceListeners(this.m_ie);
        this.m_structurePassedOn = true;
      } 
      this.m_ie.setInstance(instance);
      this.m_ie.setStatus(paramInstanceEvent.getStatus());
      notifyInstanceListeners(this.m_ie);
    } catch (Exception exception) {
      if (this.m_log != null)
        this.m_log.logMessage(exception.toString()); 
      exception.printStackTrace();
    } 
  }
  
  private void processTrainingOrDataSourceEvents(EventObject paramEventObject) {
    boolean bool = false;
    if (paramEventObject instanceof DataSetEvent) {
      bool = ((DataSetEvent)paramEventObject).isStructureOnly();
      if (bool)
        notifyDataOrTrainingListeners(paramEventObject); 
    } 
    if (paramEventObject instanceof TrainingSetEvent) {
      bool = ((TrainingSetEvent)paramEventObject).isStructureOnly();
      if (bool)
        notifyDataOrTrainingListeners(paramEventObject); 
    } 
    if (bool && !(this.m_Filter instanceof weka.filters.StreamableFilter))
      return; 
    if (this.m_filterThread == null)
      try {
        if (this.m_state == IDLE) {
          synchronized (this) {
            this.m_state = FILTERING_TRAINING;
          } 
          this.m_trainingSet = (paramEventObject instanceof TrainingSetEvent) ? ((TrainingSetEvent)paramEventObject).getTrainingSet() : ((DataSetEvent)paramEventObject).getDataSet();
          String str = this.m_visual.getText();
          this.m_filterThread = new Thread(this, str, paramEventObject) {
              private final String val$oldText;
              
              private final EventObject val$e;
              
              private final Filter this$0;
              
              public void run() {
                try {
                  if (this.this$0.m_trainingSet != null) {
                    DataSetEvent dataSetEvent;
                    this.this$0.m_visual.setAnimated();
                    this.this$0.m_visual.setText("Filtering training data...");
                    if (this.this$0.m_log != null)
                      this.this$0.m_log.statusMessage("Filter : filtering training data (" + this.this$0.m_trainingSet.relationName()); 
                    this.this$0.m_Filter.setInputFormat(this.this$0.m_trainingSet);
                    Instances instances = weka.filters.Filter.useFilter(this.this$0.m_trainingSet, this.this$0.m_Filter);
                    this.this$0.m_visual.setText(this.val$oldText);
                    this.this$0.m_visual.setStatic();
                    if (this.val$e instanceof TrainingSetEvent) {
                      TrainingSetEvent trainingSetEvent = new TrainingSetEvent(this.this$0, instances);
                      trainingSetEvent.m_setNumber = ((TrainingSetEvent)this.val$e).m_setNumber;
                      trainingSetEvent.m_maxSetNumber = ((TrainingSetEvent)this.val$e).m_maxSetNumber;
                    } else {
                      dataSetEvent = new DataSetEvent(this.this$0, instances);
                    } 
                    this.this$0.notifyDataOrTrainingListeners(dataSetEvent);
                  } 
                } catch (Exception exception) {
                  exception.printStackTrace();
                } finally {
                  this.this$0.m_visual.setText(this.val$oldText);
                  this.this$0.m_visual.setStatic();
                  this.this$0.m_state = Filter.IDLE;
                  if (isInterrupted()) {
                    this.this$0.m_trainingSet = null;
                    if (this.this$0.m_log != null) {
                      this.this$0.m_log.logMessage("Filter training set interrupted!");
                      this.this$0.m_log.statusMessage("OK");
                    } 
                  } 
                  this.this$0.block(false);
                } 
              }
            };
          this.m_filterThread.setPriority(1);
          this.m_filterThread.start();
          block(true);
          this.m_filterThread = null;
          this.m_state = IDLE;
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  public void acceptTestSet(TestSetEvent paramTestSetEvent) {
    if (paramTestSetEvent.isStructureOnly())
      notifyTestListeners(paramTestSetEvent); 
    if (this.m_trainingSet != null && this.m_trainingSet.equalHeaders(paramTestSetEvent.getTestSet()) && this.m_filterThread == null)
      try {
        if (this.m_state == IDLE)
          this.m_state = FILTERING_TEST; 
        this.m_testingSet = paramTestSetEvent.getTestSet();
        String str = this.m_visual.getText();
        this.m_filterThread = new Thread(this, str, paramTestSetEvent) {
            private final String val$oldText;
            
            private final TestSetEvent val$e;
            
            private final Filter this$0;
            
            public void run() {
              try {
                if (this.this$0.m_testingSet != null) {
                  this.this$0.m_visual.setAnimated();
                  this.this$0.m_visual.setText("Filtering test data...");
                  if (this.this$0.m_log != null)
                    this.this$0.m_log.statusMessage("Filter : filtering test data (" + this.this$0.m_testingSet.relationName()); 
                  Instances instances = weka.filters.Filter.useFilter(this.this$0.m_testingSet, this.this$0.m_Filter);
                  this.this$0.m_visual.setText(this.val$oldText);
                  this.this$0.m_visual.setStatic();
                  TestSetEvent testSetEvent = new TestSetEvent(this.this$0, instances);
                  testSetEvent.m_setNumber = this.val$e.m_setNumber;
                  testSetEvent.m_maxSetNumber = this.val$e.m_maxSetNumber;
                  this.this$0.notifyTestListeners(testSetEvent);
                } 
              } catch (Exception exception) {
                exception.printStackTrace();
              } finally {
                this.this$0.m_visual.setText(this.val$oldText);
                this.this$0.m_visual.setStatic();
                this.this$0.m_state = Filter.IDLE;
                if (isInterrupted()) {
                  this.this$0.m_trainingSet = null;
                  if (this.this$0.m_log != null) {
                    this.this$0.m_log.logMessage("Filter test set interrupted!");
                    this.this$0.m_log.statusMessage("OK");
                  } 
                } 
                this.this$0.block(false);
              } 
            }
          };
        this.m_filterThread.setPriority(1);
        this.m_filterThread.start();
        block(true);
        this.m_filterThread = null;
        this.m_state = IDLE;
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  public void acceptDataSet(DataSetEvent paramDataSetEvent) {
    processTrainingOrDataSourceEvents(paramDataSetEvent);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultFilter.gif", "weka/gui/beans/icons/DefaultFilter_animated.gif");
  }
  
  public synchronized void addTrainingSetListener(TrainingSetListener paramTrainingSetListener) {
    this.m_trainingListeners.addElement(paramTrainingSetListener);
  }
  
  public synchronized void removeTrainingSetListener(TrainingSetListener paramTrainingSetListener) {
    this.m_trainingListeners.removeElement(paramTrainingSetListener);
  }
  
  public synchronized void addTestSetListener(TestSetListener paramTestSetListener) {
    this.m_testListeners.addElement(paramTestSetListener);
  }
  
  public synchronized void removeTestSetListener(TestSetListener paramTestSetListener) {
    this.m_testListeners.removeElement(paramTestSetListener);
  }
  
  public synchronized void addDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataListeners.addElement(paramDataSourceListener);
  }
  
  public synchronized void removeDataSourceListener(DataSourceListener paramDataSourceListener) {
    this.m_dataListeners.remove(paramDataSourceListener);
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    this.m_instanceListeners.addElement(paramInstanceListener);
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    this.m_instanceListeners.removeElement(paramInstanceListener);
  }
  
  private void notifyDataOrTrainingListeners(EventObject paramEventObject) {
    Vector vector;
    synchronized (this) {
      vector = (paramEventObject instanceof TrainingSetEvent) ? (Vector)this.m_trainingListeners.clone() : (Vector)this.m_dataListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++) {
        if (paramEventObject instanceof TrainingSetEvent) {
          ((TrainingSetListener)vector.elementAt(b)).acceptTrainingSet((TrainingSetEvent)paramEventObject);
        } else {
          ((DataSourceListener)vector.elementAt(b)).acceptDataSet((DataSetEvent)paramEventObject);
        } 
      }  
  }
  
  private void notifyTestListeners(TestSetEvent paramTestSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_testListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((TestSetListener)vector.elementAt(b)).acceptTestSet(paramTestSetEvent);  
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
  
  public boolean connectionAllowed(String paramString) {
    return this.m_listenees.containsKey(paramString) ? false : ((this.m_listenees.containsKey("dataSet") && (paramString.compareTo("trainingSet") == 0 || paramString.compareTo("testSet") == 0 || paramString.compareTo("instance") == 0)) ? false : (((this.m_listenees.containsKey("trainingSet") || this.m_listenees.containsKey("testSet")) && (paramString.compareTo("dataSet") == 0 || paramString.compareTo("instance") == 0)) ? false : ((this.m_listenees.containsKey("instance") && (paramString.compareTo("trainingSet") == 0 || paramString.compareTo("testSet") == 0 || paramString.compareTo("dataSet") == 0)) ? false : (!(paramString.compareTo("instance") == 0 && !(this.m_Filter instanceof weka.filters.StreamableFilter))))));
  }
  
  public synchronized void connectionNotification(String paramString, Object paramObject) {
    if (connectionAllowed(paramString))
      this.m_listenees.put(paramString, paramObject); 
  }
  
  public synchronized void disconnectionNotification(String paramString, Object paramObject) {
    this.m_listenees.remove(paramString);
  }
  
  private synchronized void block(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        if (this.m_filterThread.isAlive() && this.m_state != IDLE)
          wait(); 
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  public void stop() {
    Enumeration enumeration = this.m_listenees.keys();
    while (enumeration.hasMoreElements()) {
      Object object = this.m_listenees.get(enumeration.nextElement());
      if (object instanceof BeanCommon) {
        System.err.println("Listener is BeanCommon");
        ((BeanCommon)object).stop();
      } 
    } 
  }
  
  public void setLog(Logger paramLogger) {
    this.m_log = paramLogger;
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_filterThread != null)
      vector.addElement("Stop"); 
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Stop") == 0) {
      stop();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (Filter)");
    } 
  }
  
  public boolean eventGeneratable(String paramString) {
    if (!this.m_listenees.containsKey(paramString))
      return false; 
    Object object = this.m_listenees.get(paramString);
    return (object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable(paramString)) ? false : (!(paramString.compareTo("instance") == 0 && !(this.m_Filter instanceof weka.filters.StreamableFilter)));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\Filter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */