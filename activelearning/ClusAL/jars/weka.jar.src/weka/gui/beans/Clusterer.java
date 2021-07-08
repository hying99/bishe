package weka.gui.beans;

import java.awt.BorderLayout;
import java.beans.EventSetDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JPanel;
import weka.clusterers.EM;
import weka.core.Drawable;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.Logger;

public class Clusterer extends JPanel implements BeanCommon, Visible, WekaWrapper, EventConstraints, Serializable, UserRequestAcceptor, TrainingSetListener, TestSetListener {
  protected BeanVisual m_visual = new BeanVisual("Clusterer", "weka/gui/beans/icons/DefaultClusterer.gif", "weka/gui/beans/icons/DefaultClusterer_animated.gif");
  
  private static int IDLE = 0;
  
  private static int BUILDING_MODEL = 1;
  
  private static int CLUSTERING = 2;
  
  private int m_state = IDLE;
  
  private Thread m_buildThread = null;
  
  protected String m_globalInfo;
  
  private Hashtable m_listenees = new Hashtable();
  
  private Vector m_batchClustererListeners = new Vector();
  
  private Vector m_graphListeners = new Vector();
  
  private Vector m_textListeners = new Vector();
  
  private Instances m_trainingSet;
  
  private transient Instances m_testingSet;
  
  private weka.clusterers.Clusterer m_Clusterer = (weka.clusterers.Clusterer)new EM();
  
  private transient Logger m_log = null;
  
  private Double m_dummy = new Double(0.0D);
  
  public String globalInfo() {
    return this.m_globalInfo;
  }
  
  public Clusterer() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
    setClusterer(this.m_Clusterer);
  }
  
  public void setClusterer(weka.clusterers.Clusterer paramClusterer) {
    boolean bool = true;
    if (paramClusterer.getClass().getName().compareTo(this.m_Clusterer.getClass().getName()) == 0) {
      bool = false;
    } else {
      this.m_trainingSet = null;
    } 
    this.m_Clusterer = paramClusterer;
    String str = paramClusterer.getClass().toString();
    str = str.substring(str.lastIndexOf('.') + 1, str.length());
    if (bool && !this.m_visual.loadIcons("weka/gui/beans/icons/" + str + ".gif", "weka/gui/beans/icons/" + str + "_animated.gif"))
      useDefaultVisual(); 
    this.m_visual.setText(str);
    this.m_globalInfo = KnowledgeFlow.getGlobalInfo(this.m_Clusterer);
  }
  
  public boolean hasIncomingBatchInstances() {
    return (this.m_listenees.size() == 0) ? false : ((this.m_listenees.containsKey("trainingSet") || this.m_listenees.containsKey("testSet") || this.m_listenees.containsKey("dataSet")));
  }
  
  public weka.clusterers.Clusterer getClusterer() {
    return this.m_Clusterer;
  }
  
  public void setWrappedAlgorithm(Object paramObject) {
    if (!(paramObject instanceof weka.clusterers.Clusterer))
      throw new IllegalArgumentException(paramObject.getClass() + " : incorrect " + "type of algorithm (Clusterer)"); 
    setClusterer((weka.clusterers.Clusterer)paramObject);
  }
  
  public Object getWrappedAlgorithm() {
    return getClusterer();
  }
  
  public void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    if (paramTrainingSetEvent.isStructureOnly()) {
      BatchClustererEvent batchClustererEvent = new BatchClustererEvent(this, this.m_Clusterer, new DataSetEvent(this, paramTrainingSetEvent.getTrainingSet()), paramTrainingSetEvent.getSetNumber(), paramTrainingSetEvent.getMaxSetNumber(), 1);
      notifyBatchClustererListeners(batchClustererEvent);
      return;
    } 
    if (this.m_buildThread == null)
      try {
        if (this.m_state == IDLE) {
          synchronized (this) {
            this.m_state = BUILDING_MODEL;
          } 
          this.m_trainingSet = paramTrainingSetEvent.getTrainingSet();
          String str = this.m_visual.getText();
          this.m_buildThread = new Thread(this, paramTrainingSetEvent, str) {
              private final TrainingSetEvent val$e;
              
              private final String val$oldText;
              
              private final Clusterer this$0;
              
              public void run() {
                try {
                  if (this.this$0.m_trainingSet != null) {
                    this.this$0.m_visual.setAnimated();
                    this.this$0.m_visual.setText("Building clusters...");
                    if (this.this$0.m_log != null)
                      this.this$0.m_log.statusMessage("Clusterer : building clusters..."); 
                    this.this$0.buildClusterer();
                    if (this.this$0.m_batchClustererListeners.size() > 0) {
                      BatchClustererEvent batchClustererEvent = new BatchClustererEvent(this, this.this$0.m_Clusterer, new DataSetEvent(this, this.val$e.getTrainingSet()), this.val$e.getSetNumber(), this.val$e.getMaxSetNumber(), 1);
                      this.this$0.notifyBatchClustererListeners(batchClustererEvent);
                    } 
                    if (this.this$0.m_Clusterer instanceof Drawable && this.this$0.m_graphListeners.size() > 0) {
                      String str1 = ((Drawable)this.this$0.m_Clusterer).graph();
                      int i = ((Drawable)this.this$0.m_Clusterer).graphType();
                      String str2 = this.this$0.m_Clusterer.getClass().getName();
                      str2 = str2.substring(str2.lastIndexOf('.') + 1, str2.length());
                      str2 = "Set " + this.val$e.getSetNumber() + " (" + this.val$e.getTrainingSet().relationName() + ") " + str2;
                      GraphEvent graphEvent = new GraphEvent(this.this$0, str1, str2, i);
                      this.this$0.notifyGraphListeners(graphEvent);
                    } 
                    if (this.this$0.m_textListeners.size() > 0) {
                      String str1 = this.this$0.m_Clusterer.toString();
                      String str2 = this.this$0.m_Clusterer.getClass().getName();
                      str2 = str2.substring(str2.lastIndexOf('.') + 1, str2.length());
                      str1 = "=== Clusterer model ===\n\nScheme:   " + str2 + "\n" + "Relation: " + this.this$0.m_trainingSet.relationName() + ((this.val$e.getMaxSetNumber() > 1) ? ("\nTraining Fold: " + this.val$e.getSetNumber()) : "") + "\n\n" + str1;
                      str2 = "Model: " + str2;
                      TextEvent textEvent = new TextEvent(this.this$0, str1, str2);
                      this.this$0.notifyTextListeners(textEvent);
                    } 
                  } 
                } catch (Exception exception) {
                  exception.printStackTrace();
                } finally {
                  this.this$0.m_visual.setText(this.val$oldText);
                  this.this$0.m_visual.setStatic();
                  this.this$0.m_state = Clusterer.IDLE;
                  if (isInterrupted()) {
                    this.this$0.m_trainingSet = null;
                    if (this.this$0.m_log != null) {
                      this.this$0.m_log.logMessage("Build clusterer interrupted!");
                      this.this$0.m_log.statusMessage("OK");
                    } 
                  } else {
                    this.this$0.m_trainingSet = new Instances(this.this$0.m_trainingSet, 0);
                  } 
                  if (this.this$0.m_log != null)
                    this.this$0.m_log.statusMessage("OK"); 
                  this.this$0.block(false);
                } 
              }
            };
          this.m_buildThread.setPriority(1);
          this.m_buildThread.start();
          block(true);
          this.m_buildThread = null;
          this.m_state = IDLE;
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  public void acceptTestSet(TestSetEvent paramTestSetEvent) {
    if (this.m_trainingSet != null)
      try {
        if (this.m_state == IDLE) {
          synchronized (this) {
            this.m_state = CLUSTERING;
          } 
          this.m_testingSet = paramTestSetEvent.getTestSet();
          if (this.m_trainingSet.equalHeaders(this.m_testingSet)) {
            BatchClustererEvent batchClustererEvent = new BatchClustererEvent(this, this.m_Clusterer, new DataSetEvent(this, paramTestSetEvent.getTestSet()), paramTestSetEvent.getSetNumber(), paramTestSetEvent.getMaxSetNumber(), 0);
            notifyBatchClustererListeners(batchClustererEvent);
          } 
          this.m_state = IDLE;
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  private void buildClusterer() throws Exception {
    if (this.m_trainingSet.classIndex() < 0) {
      this.m_Clusterer.buildClusterer(this.m_trainingSet);
    } else {
      Remove remove = new Remove();
      remove.setAttributeIndices("" + (this.m_trainingSet.classIndex() + 1));
      remove.setInvertSelection(false);
      remove.setInputFormat(this.m_trainingSet);
      Instances instances = Filter.useFilter(this.m_trainingSet, (Filter)remove);
      this.m_Clusterer.buildClusterer(instances);
    } 
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultClusterer.gif", "weka/gui/beans/icons/DefaultClusterer_animated.gif");
  }
  
  public synchronized void addBatchClustererListener(BatchClustererListener paramBatchClustererListener) {
    this.m_batchClustererListeners.addElement(paramBatchClustererListener);
  }
  
  public synchronized void removeBatchClustererListener(BatchClustererListener paramBatchClustererListener) {
    this.m_batchClustererListeners.remove(paramBatchClustererListener);
  }
  
  private void notifyBatchClustererListeners(BatchClustererEvent paramBatchClustererEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_batchClustererListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((BatchClustererListener)vector.elementAt(b)).acceptClusterer(paramBatchClustererEvent);  
  }
  
  public synchronized void addGraphListener(GraphListener paramGraphListener) {
    this.m_graphListeners.addElement(paramGraphListener);
  }
  
  public synchronized void removeGraphListener(GraphListener paramGraphListener) {
    this.m_graphListeners.remove(paramGraphListener);
  }
  
  private void notifyGraphListeners(GraphEvent paramGraphEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_graphListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((GraphListener)vector.elementAt(b)).acceptGraph(paramGraphEvent);  
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
  
  public boolean connectionAllowed(String paramString) {
    return !this.m_listenees.containsKey(paramString);
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
        if (this.m_buildThread.isAlive() && this.m_state != IDLE)
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
    if (this.m_buildThread != null) {
      this.m_buildThread.interrupt();
      this.m_buildThread.stop();
      this.m_buildThread = null;
      this.m_visual.setStatic();
    } 
  }
  
  public void setLog(Logger paramLogger) {
    this.m_log = paramLogger;
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    if (this.m_buildThread != null)
      vector.addElement("Stop"); 
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Stop") == 0) {
      stop();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (Classifier)");
    } 
  }
  
  public boolean eventGeneratable(EventSetDescriptor paramEventSetDescriptor) {
    String str = paramEventSetDescriptor.getName();
    return eventGeneratable(str);
  }
  
  public boolean eventGeneratable(String paramString) {
    if (paramString.compareTo("graph") == 0) {
      if (!(this.m_Clusterer instanceof Drawable))
        return false; 
      if (!this.m_listenees.containsKey("trainingSet"))
        return false; 
      Object object = this.m_listenees.get("trainingSet");
      if (object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("trainingSet"))
        return false; 
    } 
    if (paramString.compareTo("batchClusterer") == 0) {
      if (!this.m_listenees.containsKey("trainingSet"))
        return false; 
      Object object = this.m_listenees.get("trainingSet");
      if (object != null && object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("trainingSet"))
        return false; 
    } 
    if (paramString.compareTo("text") == 0) {
      if (!this.m_listenees.containsKey("trainingSet"))
        return false; 
      Object object = this.m_listenees.get("trainingSet");
      if (object != null && object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("trainingSet"))
        return false; 
    } 
    return (paramString.compareTo("batchClassifier") == 0) ? false : (!(paramString.compareTo("incrementalClassifier") == 0));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\Clusterer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */