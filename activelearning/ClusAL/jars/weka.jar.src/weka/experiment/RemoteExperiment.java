package weka.experiment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Naming;
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import weka.core.FastVector;
import weka.core.Option;
import weka.core.Queue;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.core.xml.KOML;
import weka.core.xml.XMLOptions;
import weka.experiment.xml.XMLExperiment;

public class RemoteExperiment extends Experiment {
  private FastVector m_listeners = new FastVector();
  
  protected DefaultListModel m_remoteHosts = new DefaultListModel();
  
  private Queue m_remoteHostsQueue = new Queue();
  
  private int[] m_remoteHostsStatus;
  
  private int[] m_remoteHostFailureCounts;
  
  protected static final int AVAILABLE = 0;
  
  protected static final int IN_USE = 1;
  
  protected static final int CONNECTION_FAILED = 2;
  
  protected static final int SOME_OTHER_FAILURE = 3;
  
  protected static final int MAX_FAILURES = 3;
  
  private boolean m_experimentAborted = false;
  
  private int m_removedHosts;
  
  private int m_failedCount;
  
  private int m_finishedCount;
  
  private Experiment m_baseExperiment = null;
  
  protected Experiment[] m_subExperiments;
  
  private Queue m_subExpQueue = new Queue();
  
  protected int[] m_subExpComplete;
  
  protected boolean m_splitByDataSet = true;
  
  public boolean getSplitByDataSet() {
    return this.m_splitByDataSet;
  }
  
  public void setSplitByDataSet(boolean paramBoolean) {
    this.m_splitByDataSet = paramBoolean;
  }
  
  public RemoteExperiment() throws Exception {
    this(new Experiment());
  }
  
  public RemoteExperiment(Experiment paramExperiment) throws Exception {
    setBaseExperiment(paramExperiment);
  }
  
  public void addRemoteExperimentListener(RemoteExperimentListener paramRemoteExperimentListener) {
    this.m_listeners.addElement(paramRemoteExperimentListener);
  }
  
  public Experiment getBaseExperiment() {
    return this.m_baseExperiment;
  }
  
  public void setBaseExperiment(Experiment paramExperiment) throws Exception {
    if (paramExperiment == null)
      throw new Exception("Base experiment is null!"); 
    this.m_baseExperiment = paramExperiment;
    setRunLower(this.m_baseExperiment.getRunLower());
    setRunUpper(this.m_baseExperiment.getRunUpper());
    setResultListener(this.m_baseExperiment.getResultListener());
    setResultProducer(this.m_baseExperiment.getResultProducer());
    setDatasets(this.m_baseExperiment.getDatasets());
    setUsePropertyIterator(this.m_baseExperiment.getUsePropertyIterator());
    setPropertyPath(this.m_baseExperiment.getPropertyPath());
    setPropertyArray(this.m_baseExperiment.getPropertyArray());
    setNotes(this.m_baseExperiment.getNotes());
    this.m_ClassFirst = this.m_baseExperiment.m_ClassFirst;
    this.m_AdvanceDataSetFirst = this.m_baseExperiment.m_AdvanceDataSetFirst;
  }
  
  public void setNotes(String paramString) {
    super.setNotes(paramString);
    this.m_baseExperiment.setNotes(paramString);
  }
  
  public void setRunLower(int paramInt) {
    super.setRunLower(paramInt);
    this.m_baseExperiment.setRunLower(paramInt);
  }
  
  public void setRunUpper(int paramInt) {
    super.setRunUpper(paramInt);
    this.m_baseExperiment.setRunUpper(paramInt);
  }
  
  public void setResultListener(ResultListener paramResultListener) {
    super.setResultListener(paramResultListener);
    this.m_baseExperiment.setResultListener(paramResultListener);
  }
  
  public void setResultProducer(ResultProducer paramResultProducer) {
    super.setResultProducer(paramResultProducer);
    this.m_baseExperiment.setResultProducer(paramResultProducer);
  }
  
  public void setDatasets(DefaultListModel paramDefaultListModel) {
    super.setDatasets(paramDefaultListModel);
    this.m_baseExperiment.setDatasets(paramDefaultListModel);
  }
  
  public void setUsePropertyIterator(boolean paramBoolean) {
    super.setUsePropertyIterator(paramBoolean);
    this.m_baseExperiment.setUsePropertyIterator(paramBoolean);
  }
  
  public void setPropertyPath(PropertyNode[] paramArrayOfPropertyNode) {
    super.setPropertyPath(paramArrayOfPropertyNode);
    this.m_baseExperiment.setPropertyPath(paramArrayOfPropertyNode);
  }
  
  public void setPropertyArray(Object paramObject) {
    super.setPropertyArray(paramObject);
    this.m_baseExperiment.setPropertyArray(paramObject);
  }
  
  public void initialize() throws Exception {
    if (this.m_baseExperiment == null)
      throw new Exception("No base experiment specified!"); 
    this.m_experimentAborted = false;
    this.m_finishedCount = 0;
    this.m_failedCount = 0;
    this.m_RunNumber = getRunLower();
    this.m_DatasetNumber = 0;
    this.m_PropertyNumber = 0;
    this.m_CurrentProperty = -1;
    this.m_CurrentInstances = null;
    this.m_Finished = false;
    if (this.m_remoteHosts.size() == 0)
      throw new Exception("No hosts specified!"); 
    this.m_remoteHostsStatus = new int[this.m_remoteHosts.size()];
    this.m_remoteHostFailureCounts = new int[this.m_remoteHosts.size()];
    this.m_remoteHostsQueue = new Queue();
    int i;
    for (i = 0; i < this.m_remoteHosts.size(); i++)
      this.m_remoteHostsQueue.push(new Integer(i)); 
    this.m_subExpQueue = new Queue();
    if (getSplitByDataSet()) {
      i = this.m_baseExperiment.getDatasets().size();
    } else {
      i = getRunUpper() - getRunLower() + 1;
    } 
    this.m_subExperiments = new Experiment[i];
    this.m_subExpComplete = new int[i];
    SerializedObject serializedObject = new SerializedObject(this.m_baseExperiment);
    if (getSplitByDataSet()) {
      for (byte b = 0; b < this.m_baseExperiment.getDatasets().size(); b++) {
        this.m_subExperiments[b] = (Experiment)serializedObject.getObject();
        DefaultListModel defaultListModel = new DefaultListModel();
        defaultListModel.addElement(this.m_baseExperiment.getDatasets().elementAt(b));
        this.m_subExperiments[b].setDatasets(defaultListModel);
        this.m_subExpQueue.push(new Integer(b));
      } 
    } else {
      for (int j = getRunLower(); j <= getRunUpper(); j++) {
        this.m_subExperiments[j - getRunLower()] = (Experiment)serializedObject.getObject();
        this.m_subExperiments[j - getRunLower()].setRunLower(j);
        this.m_subExperiments[j - getRunLower()].setRunUpper(j);
        this.m_subExpQueue.push(new Integer(j - getRunLower()));
      } 
    } 
  }
  
  private synchronized void notifyListeners(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString) {
    if (this.m_listeners.size() > 0) {
      for (byte b = 0; b < this.m_listeners.size(); b++) {
        RemoteExperimentListener remoteExperimentListener = (RemoteExperimentListener)this.m_listeners.elementAt(b);
        remoteExperimentListener.remoteExperimentStatus(new RemoteExperimentEvent(paramBoolean1, paramBoolean2, paramBoolean3, paramString));
      } 
    } else {
      System.err.println(paramString);
    } 
  }
  
  public void abortExperiment() {
    this.m_experimentAborted = true;
  }
  
  protected synchronized void incrementFinished() {
    this.m_finishedCount++;
  }
  
  protected synchronized void incrementFailed(int paramInt) {
    this.m_failedCount++;
    this.m_remoteHostFailureCounts[paramInt] = this.m_remoteHostFailureCounts[paramInt] + 1;
  }
  
  protected synchronized void waitingExperiment(int paramInt) {
    this.m_subExpQueue.push(new Integer(paramInt));
  }
  
  private boolean checkForAllFailedHosts() {
    boolean bool = true;
    for (byte b = 0; b < this.m_remoteHostsStatus.length; b++) {
      if (this.m_remoteHostsStatus[b] != 2) {
        bool = false;
        break;
      } 
    } 
    if (bool) {
      abortExperiment();
      notifyListeners(false, true, true, "Experiment aborted! All connections to remote hosts failed.");
    } 
    return bool;
  }
  
  private String postExperimentInfo() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.m_finishedCount + (this.m_splitByDataSet ? " data sets" : " runs") + " completed successfully. " + this.m_failedCount + " failures during running.\n");
    System.err.print(stringBuffer.toString());
    return stringBuffer.toString();
  }
  
  protected synchronized void availableHost(int paramInt) {
    if (paramInt >= 0)
      if (this.m_remoteHostFailureCounts[paramInt] < 3) {
        this.m_remoteHostsQueue.push(new Integer(paramInt));
      } else {
        notifyListeners(false, true, false, "Max failures exceeded for host " + (String)this.m_remoteHosts.elementAt(paramInt) + ". Removed from host list.");
        this.m_removedHosts++;
      }  
    if (this.m_failedCount == 3 * this.m_remoteHosts.size()) {
      abortExperiment();
      notifyListeners(false, true, true, "Experiment aborted! Max failures exceeded on all remote hosts.");
      return;
    } 
    if ((getSplitByDataSet() && this.m_baseExperiment.getDatasets().size() == this.m_finishedCount) || (!getSplitByDataSet() && getRunUpper() - getRunLower() + 1 == this.m_finishedCount)) {
      notifyListeners(false, true, false, "Experiment completed successfully.");
      notifyListeners(false, true, true, postExperimentInfo());
      return;
    } 
    if (checkForAllFailedHosts())
      return; 
    if (this.m_experimentAborted && this.m_remoteHostsQueue.size() + this.m_removedHosts == this.m_remoteHosts.size())
      notifyListeners(false, true, true, "Experiment aborted. All remote tasks finished."); 
    if (!this.m_subExpQueue.empty() && !this.m_experimentAborted && !this.m_remoteHostsQueue.empty())
      try {
        int i = ((Integer)this.m_remoteHostsQueue.pop()).intValue();
        int j = ((Integer)this.m_subExpQueue.pop()).intValue();
        launchNext(j, i);
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  public void launchNext(int paramInt1, int paramInt2) {
    Thread thread = new Thread(this, paramInt2, paramInt1) {
        private final int val$ah;
        
        private final int val$wexp;
        
        private final RemoteExperiment this$0;
        
        public void run() {
          this.this$0.m_remoteHostsStatus[this.val$ah] = 1;
          this.this$0.m_subExpComplete[this.val$wexp] = 1;
          RemoteExperimentSubTask remoteExperimentSubTask = new RemoteExperimentSubTask();
          remoteExperimentSubTask.setExperiment(this.this$0.m_subExperiments[this.val$wexp]);
          String str = this.this$0.getSplitByDataSet() ? ("dataset :" + ((File)this.this$0.m_subExperiments[this.val$wexp].getDatasets().elementAt(0)).getName()) : ("run :" + this.this$0.m_subExperiments[this.val$wexp].getRunLower());
          try {
            String str1 = "//" + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah) + "/RemoteEngine";
            Compute compute = (Compute)Naming.lookup(str1);
            this.this$0.notifyListeners(false, true, false, "Starting " + str + " on host " + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah));
            Object object = compute.executeTask(remoteExperimentSubTask);
            boolean bool = false;
            TaskStatusInfo taskStatusInfo = null;
            while (!bool) {
              try {
                Thread.sleep(2000L);
                TaskStatusInfo taskStatusInfo1 = (TaskStatusInfo)compute.checkStatus(object);
                if (taskStatusInfo1.getExecutionStatus() == 3) {
                  this.this$0.notifyListeners(false, true, false, taskStatusInfo1.getStatusMessage());
                  this.this$0.m_remoteHostsStatus[this.val$ah] = 0;
                  this.this$0.incrementFinished();
                  this.this$0.availableHost(this.val$ah);
                  bool = true;
                  continue;
                } 
                if (taskStatusInfo1.getExecutionStatus() == 2) {
                  this.this$0.notifyListeners(false, true, false, taskStatusInfo1.getStatusMessage());
                  this.this$0.m_remoteHostsStatus[this.val$ah] = 3;
                  this.this$0.m_subExpComplete[this.val$wexp] = 2;
                  this.this$0.notifyListeners(false, true, false, str + " " + taskStatusInfo1.getStatusMessage() + ". Scheduling for execution on another host.");
                  this.this$0.incrementFailed(this.val$ah);
                  this.this$0.waitingExperiment(this.val$wexp);
                  this.this$0.availableHost(this.val$ah);
                  bool = true;
                  continue;
                } 
                if (taskStatusInfo == null) {
                  taskStatusInfo = taskStatusInfo1;
                  this.this$0.notifyListeners(false, true, false, taskStatusInfo1.getStatusMessage());
                  continue;
                } 
                if (taskStatusInfo1.getStatusMessage().compareTo(taskStatusInfo.getStatusMessage()) != 0)
                  this.this$0.notifyListeners(false, true, false, taskStatusInfo1.getStatusMessage()); 
                taskStatusInfo = taskStatusInfo1;
              } catch (InterruptedException interruptedException) {}
            } 
          } catch (Exception exception) {
            this.this$0.m_remoteHostsStatus[this.val$ah] = 2;
            this.this$0.m_subExpComplete[this.val$wexp] = 0;
            System.err.println(exception);
            exception.printStackTrace();
            this.this$0.notifyListeners(false, true, false, "Connection to " + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah) + " failed. Scheduling " + str + " for execution on another host.");
            this.this$0.checkForAllFailedHosts();
            this.this$0.waitingExperiment(this.val$wexp);
          } finally {
            if (isInterrupted())
              System.err.println("Sub exp Interupted!"); 
          } 
        }
      };
    thread.setPriority(1);
    thread.start();
  }
  
  public void nextIteration() throws Exception {}
  
  public void advanceCounters() {}
  
  public void postProcess() {}
  
  public void addRemoteHost(String paramString) {
    this.m_remoteHosts.addElement(paramString);
  }
  
  public DefaultListModel getRemoteHosts() {
    return this.m_remoteHosts;
  }
  
  public void setRemoteHosts(DefaultListModel paramDefaultListModel) {
    this.m_remoteHosts = paramDefaultListModel;
  }
  
  public String toString() {
    String str = this.m_baseExperiment.toString();
    str = str + "\nRemote Hosts:\n";
    for (byte b = 0; b < this.m_remoteHosts.size(); b++)
      str = str + (String)this.m_remoteHosts.elementAt(b) + '\n'; 
    return str;
  }
  
  public void runExperiment() {
    int i = this.m_remoteHostsQueue.size();
    for (byte b = 0; b < i; b++)
      availableHost(-1); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      RemoteExperiment remoteExperiment = null;
      String str1 = Utils.getOption("xml", paramArrayOfString);
      if (!str1.equals(""))
        paramArrayOfString = (new XMLOptions(str1)).toArray(); 
      Experiment experiment = null;
      String str2 = Utils.getOption('l', paramArrayOfString);
      String str3 = Utils.getOption('s', paramArrayOfString);
      boolean bool = Utils.getFlag('r', paramArrayOfString);
      FastVector fastVector = new FastVector();
      String str4 = " ";
      while (str4.length() != 0) {
        str4 = Utils.getOption('h', paramArrayOfString);
        if (str4.length() != 0)
          fastVector.addElement(str4); 
      } 
      if (str2.length() == 0) {
        experiment = new Experiment();
        try {
          experiment.setOptions(paramArrayOfString);
          Utils.checkForRemainingOptions(paramArrayOfString);
        } catch (Exception exception) {
          exception.printStackTrace();
          String str = "Usage:\n\n-l <exp file>\n\tLoad experiment from file (default use cli options)\n-s <exp file>\n\tSave experiment to file after setting other options\n\t(default don't save)\n-h <remote host name>\n\tHost to run experiment on (may be specified more than once\n\tfor multiple remote hosts)\n-r \n\tRun experiment on (default don't run)\n-xml <filename | xml-string>\n\tget options from XML-Data instead from parameters\n\n";
          Enumeration enumeration = experiment.listOptions();
          while (enumeration.hasMoreElements()) {
            Option option = enumeration.nextElement();
            str = str + option.synopsis() + "\n";
            str = str + option.description() + "\n";
          } 
          throw new Exception(str + "\n" + exception.getMessage());
        } 
      } else {
        Object object;
        if (KOML.isPresent() && str2.toLowerCase().endsWith(".koml")) {
          object = KOML.read(str2);
        } else if (str2.toLowerCase().endsWith(".xml")) {
          XMLExperiment xMLExperiment = new XMLExperiment();
          object = xMLExperiment.read(str2);
        } else {
          FileInputStream fileInputStream = new FileInputStream(str2);
          ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
          object = objectInputStream.readObject();
          objectInputStream.close();
        } 
        if (object instanceof RemoteExperiment) {
          remoteExperiment = (RemoteExperiment)object;
        } else {
          experiment = (Experiment)object;
        } 
      } 
      if (experiment != null)
        remoteExperiment = new RemoteExperiment(experiment); 
      for (byte b = 0; b < fastVector.size(); b++)
        remoteExperiment.addRemoteHost((String)fastVector.elementAt(b)); 
      System.err.println("Experiment:\n" + remoteExperiment.toString());
      if (str3.length() != 0)
        if (KOML.isPresent() && str3.toLowerCase().endsWith(".koml")) {
          KOML.write(str3, remoteExperiment);
        } else if (str3.toLowerCase().endsWith(".xml")) {
          XMLExperiment xMLExperiment = new XMLExperiment();
          xMLExperiment.write(str3, remoteExperiment);
        } else {
          FileOutputStream fileOutputStream = new FileOutputStream(str3);
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
          objectOutputStream.writeObject(remoteExperiment);
          objectOutputStream.close();
        }  
      if (bool) {
        System.err.println("Initializing...");
        remoteExperiment.initialize();
        System.err.println("Iterating...");
        remoteExperiment.runExperiment();
        System.err.println("Postprocessing...");
        remoteExperiment.postProcess();
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RemoteExperiment.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */