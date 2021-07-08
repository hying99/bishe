package weka.gui.beans;

import java.awt.BorderLayout;
import java.beans.EventSetDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JPanel;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Drawable;
import weka.core.Instances;
import weka.gui.Logger;

public class Classifier extends JPanel implements BeanCommon, Visible, WekaWrapper, EventConstraints, Serializable, UserRequestAcceptor, TrainingSetListener, TestSetListener, InstanceListener {
  protected BeanVisual m_visual = new BeanVisual("Classifier", "weka/gui/beans/icons/DefaultClassifier.gif", "weka/gui/beans/icons/DefaultClassifier_animated.gif");
  
  private static int IDLE = 0;
  
  private static int BUILDING_MODEL = 1;
  
  private static int CLASSIFYING = 2;
  
  private int m_state = IDLE;
  
  private Thread m_buildThread = null;
  
  protected String m_globalInfo;
  
  private Hashtable m_listenees = new Hashtable();
  
  private Vector m_batchClassifierListeners = new Vector();
  
  private Vector m_incrementalClassifierListeners = new Vector();
  
  private Vector m_graphListeners = new Vector();
  
  private Vector m_textListeners = new Vector();
  
  private Instances m_trainingSet;
  
  private transient Instances m_testingSet;
  
  private weka.classifiers.Classifier m_Classifier = (weka.classifiers.Classifier)new ZeroR();
  
  private IncrementalClassifierEvent m_ie = new IncrementalClassifierEvent(this);
  
  private boolean m_updateIncrementalClassifier = true;
  
  private transient Logger m_log = null;
  
  private InstanceEvent m_incrementalEvent;
  
  private Double m_dummy = new Double(0.0D);
  
  public String globalInfo() {
    return this.m_globalInfo;
  }
  
  public Classifier() {
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
    setClassifier(this.m_Classifier);
  }
  
  public void setClassifier(weka.classifiers.Classifier paramClassifier) {
    boolean bool = true;
    if (paramClassifier.getClass().getName().compareTo(this.m_Classifier.getClass().getName()) == 0) {
      bool = false;
    } else {
      this.m_trainingSet = null;
    } 
    this.m_Classifier = paramClassifier;
    String str = paramClassifier.getClass().toString();
    str = str.substring(str.lastIndexOf('.') + 1, str.length());
    if (bool && !this.m_visual.loadIcons("weka/gui/beans/icons/" + str + ".gif", "weka/gui/beans/icons/" + str + "_animated.gif"))
      useDefaultVisual(); 
    this.m_visual.setText(str);
    if (!(this.m_Classifier instanceof UpdateableClassifier) && this.m_listenees.containsKey("instance") && this.m_log != null)
      this.m_log.logMessage("WARNING : " + this.m_Classifier.getClass().getName() + " is not an incremental classifier (Classifier)"); 
    this.m_globalInfo = KnowledgeFlow.getGlobalInfo(this.m_Classifier);
  }
  
  public boolean hasIncomingStreamInstances() {
    return (this.m_listenees.size() == 0) ? false : (this.m_listenees.containsKey("instance"));
  }
  
  public boolean hasIncomingBatchInstances() {
    return (this.m_listenees.size() == 0) ? false : ((this.m_listenees.containsKey("trainingSet") || this.m_listenees.containsKey("testSet")));
  }
  
  public weka.classifiers.Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public void setWrappedAlgorithm(Object paramObject) {
    if (!(paramObject instanceof weka.classifiers.Classifier))
      throw new IllegalArgumentException(paramObject.getClass() + " : incorrect " + "type of algorithm (Classifier)"); 
    setClassifier((weka.classifiers.Classifier)paramObject);
  }
  
  public Object getWrappedAlgorithm() {
    return getClassifier();
  }
  
  public boolean getUpdateIncrementalClassifier() {
    return this.m_updateIncrementalClassifier;
  }
  
  public void setUpdateIncrementalClassifier(boolean paramBoolean) {
    this.m_updateIncrementalClassifier = paramBoolean;
  }
  
  public void acceptInstance(InstanceEvent paramInstanceEvent) {
    this.m_incrementalEvent = paramInstanceEvent;
    handleIncrementalEvent();
  }
  
  private void handleIncrementalEvent() {
    if (this.m_buildThread != null) {
      String str = "Classifier is currently batch training!";
      if (this.m_log != null) {
        this.m_log.logMessage(str);
      } else {
        System.err.println(str);
      } 
      return;
    } 
    if (this.m_incrementalEvent.getStatus() == 0) {
      Instances instances = this.m_incrementalEvent.getStructure();
      if (instances.classIndex() < 0)
        instances.setClassIndex(instances.numAttributes() - 1); 
      try {
        if (this.m_trainingSet == null || !instances.equalHeaders(this.m_trainingSet)) {
          if (!(this.m_Classifier instanceof UpdateableClassifier)) {
            if (this.m_log != null) {
              String str = (this.m_trainingSet == null) ? ("ERROR : " + this.m_Classifier.getClass().getName() + " has not been batch " + "trained; can't process instance events.") : "ERROR : instance event's structure is different from the data that was used to batch train this classifier; can't continue.";
              this.m_log.logMessage(str);
            } 
            return;
          } 
          if (this.m_trainingSet != null && !instances.equalHeaders(this.m_trainingSet)) {
            if (this.m_log != null)
              this.m_log.logMessage("Warning : structure of instance events differ from data used in batch training this classifier. Resetting classifier..."); 
            this.m_trainingSet = null;
          } 
          if (this.m_trainingSet == null) {
            this.m_trainingSet = new Instances(instances, 0);
            this.m_Classifier.buildClassifier(this.m_trainingSet);
          } 
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      System.err.println("NOTIFYING NEW BATCH");
      this.m_ie.setStructure(instances);
      this.m_ie.setClassifier(this.m_Classifier);
      notifyIncrementalClassifierListeners(this.m_ie);
      return;
    } 
    if (this.m_trainingSet == null)
      return; 
    try {
      byte b = 1;
      if (this.m_incrementalEvent.getStatus() == 2)
        b = 2; 
      this.m_ie.setStatus(b);
      this.m_ie.setClassifier(this.m_Classifier);
      this.m_ie.setCurrentInstance(this.m_incrementalEvent.getInstance());
      notifyIncrementalClassifierListeners(this.m_ie);
      if (this.m_Classifier instanceof UpdateableClassifier && this.m_updateIncrementalClassifier == true && !this.m_incrementalEvent.getInstance().isMissing(this.m_incrementalEvent.getInstance().dataset().classIndex()))
        ((UpdateableClassifier)this.m_Classifier).updateClassifier(this.m_incrementalEvent.getInstance()); 
      if (this.m_incrementalEvent.getStatus() == 2 && this.m_textListeners.size() > 0) {
        String str1 = this.m_Classifier.toString();
        String str2 = this.m_Classifier.getClass().getName();
        str2 = str2.substring(str2.lastIndexOf('.') + 1, str2.length());
        str1 = "=== Classifier model ===\n\nScheme:   " + str2 + "\n" + "Relation: " + this.m_trainingSet.relationName() + "\n\n" + str1;
        str2 = "Model: " + str2;
        TextEvent textEvent = new TextEvent(this, str1, str2);
        notifyTextListeners(textEvent);
      } 
    } catch (Exception exception) {
      if (this.m_log != null)
        this.m_log.logMessage(exception.toString()); 
      exception.printStackTrace();
    } 
  }
  
  private void startIncrementalHandler() {
    if (this.m_buildThread == null) {
      this.m_buildThread = new Thread(this) {
          private final Classifier this$0;
          
          public void run() {
            while (true) {
              synchronized (this.this$0.m_dummy) {
                try {
                  this.this$0.m_dummy.wait();
                } catch (InterruptedException interruptedException) {
                  return;
                } 
              } 
              this.this$0.handleIncrementalEvent();
              this.this$0.m_state = Classifier.IDLE;
              this.this$0.block(false);
            } 
          }
        };
      this.m_buildThread.setPriority(1);
      this.m_buildThread.start();
      try {
        Thread.sleep(500L);
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  public void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    if (paramTrainingSetEvent.isStructureOnly()) {
      BatchClassifierEvent batchClassifierEvent = new BatchClassifierEvent(this, this.m_Classifier, new DataSetEvent(this, paramTrainingSetEvent.getTrainingSet()), paramTrainingSetEvent.getSetNumber(), paramTrainingSetEvent.getMaxSetNumber());
      notifyBatchClassifierListeners(batchClassifierEvent);
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
              
              private final Classifier this$0;
              
              public void run() {
                try {
                  if (this.this$0.m_trainingSet != null) {
                    if (this.this$0.m_trainingSet.classIndex() < 0) {
                      this.this$0.m_trainingSet.setClassIndex(this.this$0.m_trainingSet.numAttributes() - 1);
                      if (this.this$0.m_log != null)
                        this.this$0.m_log.logMessage("Classifier : assuming last column is the class"); 
                    } 
                    this.this$0.m_visual.setAnimated();
                    this.this$0.m_visual.setText("Building model...");
                    if (this.this$0.m_log != null)
                      this.this$0.m_log.statusMessage("Classifier : building model..."); 
                    this.this$0.buildClassifier();
                    if (this.this$0.m_Classifier instanceof Drawable && this.this$0.m_graphListeners.size() > 0) {
                      String str1 = ((Drawable)this.this$0.m_Classifier).graph();
                      int i = ((Drawable)this.this$0.m_Classifier).graphType();
                      String str2 = this.this$0.m_Classifier.getClass().getName();
                      str2 = str2.substring(str2.lastIndexOf('.') + 1, str2.length());
                      str2 = "Set " + this.val$e.getSetNumber() + " (" + this.val$e.getTrainingSet().relationName() + ") " + str2;
                      GraphEvent graphEvent = new GraphEvent(this.this$0, str1, str2, i);
                      this.this$0.notifyGraphListeners(graphEvent);
                    } 
                    if (this.this$0.m_textListeners.size() > 0) {
                      String str1 = this.this$0.m_Classifier.toString();
                      String str2 = this.this$0.m_Classifier.getClass().getName();
                      str2 = str2.substring(str2.lastIndexOf('.') + 1, str2.length());
                      str1 = "=== Classifier model ===\n\nScheme:   " + str2 + "\n" + "Relation: " + this.this$0.m_trainingSet.relationName() + ((this.val$e.getMaxSetNumber() > 1) ? ("\nTraining Fold: " + this.val$e.getSetNumber()) : "") + "\n\n" + str1;
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
                  this.this$0.m_state = Classifier.IDLE;
                  if (isInterrupted()) {
                    this.this$0.m_trainingSet = null;
                    if (this.this$0.m_log != null) {
                      this.this$0.m_log.logMessage("Build classifier interrupted!");
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
            this.m_state = CLASSIFYING;
          } 
          this.m_testingSet = paramTestSetEvent.getTestSet();
          if (this.m_testingSet != null && this.m_testingSet.classIndex() < 0)
            this.m_testingSet.setClassIndex(this.m_testingSet.numAttributes() - 1); 
          if (this.m_trainingSet.equalHeaders(this.m_testingSet)) {
            BatchClassifierEvent batchClassifierEvent = new BatchClassifierEvent(this, this.m_Classifier, new DataSetEvent(this, paramTestSetEvent.getTestSet()), paramTestSetEvent.getSetNumber(), paramTestSetEvent.getMaxSetNumber());
            notifyBatchClassifierListeners(batchClassifierEvent);
          } 
          this.m_state = IDLE;
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  private void buildClassifier() throws Exception {
    this.m_Classifier.buildClassifier(this.m_trainingSet);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultClassifier.gif", "weka/gui/beans/icons/DefaultClassifier_animated.gif");
  }
  
  public synchronized void addBatchClassifierListener(BatchClassifierListener paramBatchClassifierListener) {
    this.m_batchClassifierListeners.addElement(paramBatchClassifierListener);
  }
  
  public synchronized void removeBatchClassifierListener(BatchClassifierListener paramBatchClassifierListener) {
    this.m_batchClassifierListeners.remove(paramBatchClassifierListener);
  }
  
  private void notifyBatchClassifierListeners(BatchClassifierEvent paramBatchClassifierEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_batchClassifierListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((BatchClassifierListener)vector.elementAt(b)).acceptClassifier(paramBatchClassifierEvent);  
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
  
  public synchronized void addIncrementalClassifierListener(IncrementalClassifierListener paramIncrementalClassifierListener) {
    this.m_incrementalClassifierListeners.add(paramIncrementalClassifierListener);
  }
  
  public synchronized void removeIncrementalClassifierListener(IncrementalClassifierListener paramIncrementalClassifierListener) {
    this.m_incrementalClassifierListeners.remove(paramIncrementalClassifierListener);
  }
  
  private void notifyIncrementalClassifierListeners(IncrementalClassifierEvent paramIncrementalClassifierEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_incrementalClassifierListeners.clone();
    } 
    if (vector.size() > 0)
      for (byte b = 0; b < vector.size(); b++)
        ((IncrementalClassifierListener)vector.elementAt(b)).acceptClassifier(paramIncrementalClassifierEvent);  
  }
  
  public boolean connectionAllowed(String paramString) {
    return !this.m_listenees.containsKey(paramString);
  }
  
  public synchronized void connectionNotification(String paramString, Object paramObject) {
    if (paramString.compareTo("instance") == 0 && !(this.m_Classifier instanceof UpdateableClassifier) && this.m_log != null)
      this.m_log.logMessage("Warning : " + this.m_Classifier.getClass().getName() + " is not an updateable classifier. This " + "classifier will only be evaluated on incoming " + "instance events and not trained on them."); 
    if (connectionAllowed(paramString))
      this.m_listenees.put(paramString, paramObject); 
  }
  
  public synchronized void disconnectionNotification(String paramString, Object paramObject) {
    this.m_listenees.remove(paramString);
    if (paramString.compareTo("instance") == 0)
      stop(); 
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
      if (!(this.m_Classifier instanceof Drawable))
        return false; 
      if (!this.m_listenees.containsKey("trainingSet"))
        return false; 
      Object object = this.m_listenees.get("trainingSet");
      if (object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("trainingSet"))
        return false; 
    } 
    if (paramString.compareTo("batchClassifier") == 0) {
      if (!this.m_listenees.containsKey("testSet") || !this.m_listenees.containsKey("trainingSet"))
        return false; 
      Object object = this.m_listenees.get("testSet");
      if (object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("testSet"))
        return false; 
      object = this.m_listenees.get("trainingSet");
      if (object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("trainingSet"))
        return false; 
    } 
    if (paramString.compareTo("text") == 0) {
      if (!this.m_listenees.containsKey("trainingSet") && !this.m_listenees.containsKey("instance"))
        return false; 
      Object object = this.m_listenees.get("trainingSet");
      if (object != null && object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("trainingSet"))
        return false; 
      object = this.m_listenees.get("instance");
      if (object != null && object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("instance"))
        return false; 
    } 
    if (paramString.compareTo("incrementalClassifier") == 0) {
      if (!this.m_listenees.containsKey("instance"))
        return false; 
      Object object = this.m_listenees.get("instance");
      if (object instanceof EventConstraints && !((EventConstraints)object).eventGeneratable("instance"))
        return false; 
    } 
    return true;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\Classifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */