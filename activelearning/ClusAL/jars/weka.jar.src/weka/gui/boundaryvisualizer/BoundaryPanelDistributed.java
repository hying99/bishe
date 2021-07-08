package weka.gui.boundaryvisualizer;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.rmi.Naming;
import java.util.Vector;
import javax.swing.JFrame;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Queue;
import weka.core.Utils;
import weka.experiment.Compute;
import weka.experiment.RemoteExperimentEvent;
import weka.experiment.RemoteExperimentListener;
import weka.experiment.TaskStatusInfo;

public class BoundaryPanelDistributed extends BoundaryPanel {
  protected Vector m_listeners = new Vector();
  
  protected Vector m_remoteHosts = new Vector();
  
  private Queue m_remoteHostsQueue = new Queue();
  
  private int[] m_remoteHostsStatus;
  
  private int[] m_remoteHostFailureCounts;
  
  protected static final int AVAILABLE = 0;
  
  protected static final int IN_USE = 1;
  
  protected static final int CONNECTION_FAILED = 2;
  
  protected static final int SOME_OTHER_FAILURE = 3;
  
  protected static final int MAX_FAILURES = 3;
  
  private boolean m_plottingAborted = false;
  
  private int m_removedHosts;
  
  private int m_failedCount;
  
  private int m_finishedCount;
  
  private Queue m_subExpQueue = new Queue();
  
  private int m_minTaskPollTime = 1000;
  
  private int[] m_hostPollingTime;
  
  public BoundaryPanelDistributed(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
  }
  
  public void setRemoteHosts(Vector paramVector) {
    this.m_remoteHosts = paramVector;
  }
  
  public void addRemoteExperimentListener(RemoteExperimentListener paramRemoteExperimentListener) {
    this.m_listeners.addElement(paramRemoteExperimentListener);
  }
  
  protected void initialize() {
    super.initialize();
    this.m_plottingAborted = false;
    this.m_finishedCount = 0;
    this.m_failedCount = 0;
    this.m_remoteHostsStatus = new int[this.m_remoteHosts.size()];
    this.m_remoteHostFailureCounts = new int[this.m_remoteHosts.size()];
    this.m_remoteHostsQueue = new Queue();
    if (this.m_remoteHosts.size() == 0) {
      System.err.println("No hosts specified!");
      System.exit(1);
    } 
    this.m_hostPollingTime = new int[this.m_remoteHosts.size()];
    byte b;
    for (b = 0; b < this.m_remoteHosts.size(); b++) {
      this.m_remoteHostsQueue.push(new Integer(b));
      this.m_hostPollingTime[b] = this.m_minTaskPollTime;
    } 
    this.m_subExpQueue = new Queue();
    for (b = 0; b < this.m_panelHeight; b++)
      this.m_subExpQueue.push(new Integer(b)); 
    try {
      this.m_classifier.buildClassifier(this.m_trainingData);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.exit(1);
    } 
    boolean[] arrayOfBoolean = new boolean[this.m_trainingData.numAttributes()];
    arrayOfBoolean[this.m_xAttribute] = true;
    arrayOfBoolean[this.m_yAttribute] = true;
    this.m_dataGenerator.setWeightingDimensions(arrayOfBoolean);
    try {
      this.m_dataGenerator.buildGenerator(this.m_trainingData);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.exit(1);
    } 
  }
  
  public void start() throws Exception {
    this.m_stopReplotting = true;
    if (this.m_trainingData == null)
      throw new Exception("No training data set (BoundaryPanel)"); 
    if (this.m_classifier == null)
      throw new Exception("No classifier set (BoundaryPanel)"); 
    if (this.m_dataGenerator == null)
      throw new Exception("No data generator set (BoundaryPanel)"); 
    if (this.m_trainingData.attribute(this.m_xAttribute).isNominal() || this.m_trainingData.attribute(this.m_yAttribute).isNominal())
      throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)"); 
    computeMinMaxAtts();
    initialize();
    int i = this.m_remoteHostsQueue.size();
    for (byte b = 0; b < i; b++) {
      availableHost(-1);
      Thread.sleep(70L);
    } 
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
      this.m_plottingAborted = true;
      notifyListeners(false, true, true, "Plotting aborted! Max failures exceeded on all remote hosts.");
      return;
    } 
    if (this.m_subExpQueue.size() == 0 && this.m_remoteHosts.size() == this.m_remoteHostsQueue.size() + this.m_removedHosts) {
      if (this.m_plotTrainingData)
        plotTrainingData(); 
      notifyListeners(false, true, true, "Plotting completed successfully.");
      return;
    } 
    if (checkForAllFailedHosts())
      return; 
    if (this.m_plottingAborted && this.m_remoteHostsQueue.size() + this.m_removedHosts == this.m_remoteHosts.size())
      notifyListeners(false, true, true, "Plotting aborted. All remote tasks finished."); 
    if (!this.m_subExpQueue.empty() && !this.m_plottingAborted && !this.m_remoteHostsQueue.empty())
      try {
        int i = ((Integer)this.m_remoteHostsQueue.pop()).intValue();
        int j = ((Integer)this.m_subExpQueue.pop()).intValue();
        launchNext(j, i);
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
  
  private synchronized void notifyListeners(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString) {
    if (this.m_listeners.size() > 0) {
      for (byte b = 0; b < this.m_listeners.size(); b++) {
        RemoteExperimentListener remoteExperimentListener = this.m_listeners.elementAt(b);
        remoteExperimentListener.remoteExperimentStatus(new RemoteExperimentEvent(paramBoolean1, paramBoolean2, paramBoolean3, paramString));
      } 
    } else {
      System.err.println(paramString);
    } 
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
      this.m_plottingAborted = true;
      notifyListeners(false, true, true, "Plotting aborted! All connections to remote hosts failed.");
    } 
    return bool;
  }
  
  protected synchronized void incrementFinished() {
    this.m_finishedCount++;
  }
  
  protected synchronized void incrementFailed(int paramInt) {
    this.m_failedCount++;
    this.m_remoteHostFailureCounts[paramInt] = this.m_remoteHostFailureCounts[paramInt] + 1;
  }
  
  protected synchronized void waitingTask(int paramInt) {
    this.m_subExpQueue.push(new Integer(paramInt));
  }
  
  protected void launchNext(int paramInt1, int paramInt2) {
    Thread thread = new Thread(this, paramInt2, paramInt1) {
        private final int val$ah;
        
        private final int val$wtask;
        
        private final BoundaryPanelDistributed this$0;
        
        public void run() {
          this.this$0.m_remoteHostsStatus[this.val$ah] = 1;
          RemoteBoundaryVisualizerSubTask remoteBoundaryVisualizerSubTask = new RemoteBoundaryVisualizerSubTask();
          remoteBoundaryVisualizerSubTask.setXAttribute(this.this$0.m_xAttribute);
          remoteBoundaryVisualizerSubTask.setYAttribute(this.this$0.m_yAttribute);
          remoteBoundaryVisualizerSubTask.setRowNumber(this.val$wtask);
          remoteBoundaryVisualizerSubTask.setPanelWidth(this.this$0.m_panelWidth);
          remoteBoundaryVisualizerSubTask.setPanelHeight(this.this$0.m_panelHeight);
          remoteBoundaryVisualizerSubTask.setPixHeight(this.this$0.m_pixHeight);
          remoteBoundaryVisualizerSubTask.setPixWidth(this.this$0.m_pixWidth);
          remoteBoundaryVisualizerSubTask.setClassifier(this.this$0.m_classifier);
          remoteBoundaryVisualizerSubTask.setDataGenerator(this.this$0.m_dataGenerator);
          remoteBoundaryVisualizerSubTask.setInstances(this.this$0.m_trainingData);
          remoteBoundaryVisualizerSubTask.setMinMaxX(this.this$0.m_minX, this.this$0.m_maxX);
          remoteBoundaryVisualizerSubTask.setMinMaxY(this.this$0.m_minY, this.this$0.m_maxY);
          remoteBoundaryVisualizerSubTask.setNumSamplesPerRegion(this.this$0.m_numOfSamplesPerRegion);
          remoteBoundaryVisualizerSubTask.setGeneratorSamplesBase(this.this$0.m_samplesBase);
          try {
            String str = "//" + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah) + "/RemoteEngine";
            Compute compute = (Compute)Naming.lookup(str);
            this.this$0.notifyListeners(false, true, false, "Starting row " + this.val$wtask + " on host " + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah));
            Object object = compute.executeTask(remoteBoundaryVisualizerSubTask);
            boolean bool = false;
            TaskStatusInfo taskStatusInfo = null;
            long l = System.currentTimeMillis();
            while (!bool) {
              try {
                Thread.sleep(Math.max(this.this$0.m_minTaskPollTime, this.this$0.m_hostPollingTime[this.val$ah]));
                TaskStatusInfo taskStatusInfo1 = (TaskStatusInfo)compute.checkStatus(object);
                if (taskStatusInfo1.getExecutionStatus() == 3) {
                  long l1 = System.currentTimeMillis() - l;
                  l1 /= 4L;
                  if (l1 < 1000L)
                    l1 = 1000L; 
                  this.this$0.m_hostPollingTime[this.val$ah] = (int)l1;
                  RemoteResult remoteResult1 = (RemoteResult)taskStatusInfo1.getTaskResult();
                  double[][] arrayOfDouble = remoteResult1.getProbabilities();
                  for (byte b = 0; b < this.this$0.m_panelWidth; b++) {
                    this.this$0.m_probabilityCache[this.val$wtask][b] = arrayOfDouble[b];
                    if (b < this.this$0.m_panelWidth - 1) {
                      this.this$0.plotPoint(b, this.val$wtask, arrayOfDouble[b], false);
                    } else {
                      this.this$0.plotPoint(b, this.val$wtask, arrayOfDouble[b], true);
                    } 
                  } 
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
                  this.this$0.notifyListeners(false, true, false, "Row " + this.val$wtask + " " + taskStatusInfo1.getStatusMessage() + ". Scheduling for execution on another host.");
                  this.this$0.incrementFailed(this.val$ah);
                  this.this$0.waitingTask(this.val$wtask);
                  this.this$0.availableHost(this.val$ah);
                  bool = true;
                  continue;
                } 
                if (taskStatusInfo == null) {
                  taskStatusInfo = taskStatusInfo1;
                  this.this$0.notifyListeners(false, true, false, taskStatusInfo1.getStatusMessage());
                  continue;
                } 
                RemoteResult remoteResult = (RemoteResult)taskStatusInfo1.getTaskResult();
                if (remoteResult != null) {
                  int i = remoteResult.getPercentCompleted();
                  String str1 = "";
                  if (i > 0 && i < 100) {
                    double d1 = System.currentTimeMillis() - l;
                    double d2 = (100.0D - i) / i * d1;
                    if (d2 < this.this$0.m_hostPollingTime[this.val$ah])
                      this.this$0.m_hostPollingTime[this.val$ah] = (int)d2; 
                    String str2 = "seconds";
                    d2 /= 1000.0D;
                    if (d2 > 60.0D) {
                      str2 = "minutes";
                      d2 /= 60.0D;
                    } 
                    if (d2 > 60.0D) {
                      str2 = "hours";
                      d2 /= 60.0D;
                    } 
                    str1 = " (approx. time remaining " + Utils.doubleToString(d2, 1) + " " + str2 + ")";
                  } 
                  if (i < 25) {
                    if (i > 0) {
                      this.this$0.m_hostPollingTime[this.val$ah] = (int)(25.0D / i * this.this$0.m_hostPollingTime[this.val$ah]);
                    } else {
                      this.this$0.m_hostPollingTime[this.val$ah] = this.this$0.m_hostPollingTime[this.val$ah] * 2;
                    } 
                    if (this.this$0.m_hostPollingTime[this.val$ah] > 60000)
                      this.this$0.m_hostPollingTime[this.val$ah] = 60000; 
                  } 
                  this.this$0.notifyListeners(false, true, false, "Row " + this.val$wtask + " " + i + "% complete" + str1 + ".");
                } else {
                  this.this$0.notifyListeners(false, true, false, "Row " + this.val$wtask + " queued on " + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah));
                  if (this.this$0.m_hostPollingTime[this.val$ah] < 60000)
                    this.this$0.m_hostPollingTime[this.val$ah] = this.this$0.m_hostPollingTime[this.val$ah] * 2; 
                } 
                taskStatusInfo = taskStatusInfo1;
              } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
              } 
            } 
          } catch (Exception exception) {
            this.this$0.m_remoteHostsStatus[this.val$ah] = 2;
            this.this$0.m_removedHosts++;
            System.err.println(exception);
            exception.printStackTrace();
            this.this$0.notifyListeners(false, true, false, "Connection to " + (String)this.this$0.m_remoteHosts.elementAt(this.val$ah) + " failed. Scheduling row " + this.val$wtask + " for execution on another host.");
            this.this$0.checkForAllFailedHosts();
            this.this$0.waitingTask(this.val$wtask);
          } finally {
            if (isInterrupted())
              System.err.println("Sub exp Interupted!"); 
          } 
        }
      };
    thread.setPriority(1);
    thread.start();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length < 8) {
        System.err.println("Usage : BoundaryPanelDistributed <dataset> <class col> <xAtt> <yAtt> <base> <# loc/pixel> <kernel bandwidth> <display width> <display height> <classifier [classifier options]>");
        System.exit(1);
      } 
      Vector vector = new Vector();
      try {
        BufferedReader bufferedReader1 = new BufferedReader(new FileReader("hosts.vis"));
        for (String str1 = bufferedReader1.readLine(); str1 != null; str1 = bufferedReader1.readLine()) {
          System.out.println("Adding host " + str1);
          vector.add(str1);
        } 
        bufferedReader1.close();
      } catch (Exception exception) {
        System.err.println("No hosts.vis file - create this file in the current directory with one host name per line, or use BoundaryPanel instead.");
        System.exit(1);
      } 
      JFrame jFrame = new JFrame("Weka classification boundary visualizer");
      jFrame.getContentPane().setLayout(new BorderLayout());
      System.err.println("Loading instances from : " + paramArrayOfString[0]);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      Instances instances = new Instances(bufferedReader);
      instances.setClassIndex(Integer.parseInt(paramArrayOfString[1]));
      int i = Integer.parseInt(paramArrayOfString[2]);
      int j = Integer.parseInt(paramArrayOfString[3]);
      int k = Integer.parseInt(paramArrayOfString[4]);
      int m = Integer.parseInt(paramArrayOfString[5]);
      int n = Integer.parseInt(paramArrayOfString[6]);
      int i1 = Integer.parseInt(paramArrayOfString[7]);
      int i2 = Integer.parseInt(paramArrayOfString[8]);
      String str = paramArrayOfString[9];
      BoundaryPanelDistributed boundaryPanelDistributed = new BoundaryPanelDistributed(i1, i2);
      boundaryPanelDistributed.addRemoteExperimentListener(new RemoteExperimentListener(str, boundaryPanelDistributed, instances, i, j) {
            private final String val$classifierName;
            
            private final BoundaryPanelDistributed val$bv;
            
            private final Instances val$i;
            
            private final int val$xatt;
            
            private final int val$yatt;
            
            public void remoteExperimentStatus(RemoteExperimentEvent param1RemoteExperimentEvent) {
              if (param1RemoteExperimentEvent.m_experimentFinished) {
                String str = this.val$classifierName.substring(this.val$classifierName.lastIndexOf('.') + 1, this.val$classifierName.length());
                this.val$bv.saveImage(str + "_" + this.val$i.relationName() + "_X" + this.val$xatt + "_Y" + this.val$yatt + ".jpg");
              } else {
                System.err.println(param1RemoteExperimentEvent.m_messageString);
              } 
            }
          });
      boundaryPanelDistributed.setRemoteHosts(vector);
      jFrame.getContentPane().add(boundaryPanelDistributed, "Center");
      jFrame.setSize(boundaryPanelDistributed.getMinimumSize());
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      boundaryPanelDistributed.repaint();
      String[] arrayOfString = null;
      if (paramArrayOfString.length > 10) {
        arrayOfString = new String[paramArrayOfString.length - 10];
        for (byte b = 10; b < paramArrayOfString.length; b++)
          arrayOfString[b - 10] = paramArrayOfString[b]; 
      } 
      Classifier classifier = Classifier.forName(paramArrayOfString[9], arrayOfString);
      KDDataGenerator kDDataGenerator = new KDDataGenerator();
      kDDataGenerator.setKernelBandwidth(n);
      boundaryPanelDistributed.setDataGenerator(kDDataGenerator);
      boundaryPanelDistributed.setNumSamplesPerRegion(m);
      boundaryPanelDistributed.setGeneratorSamplesBase(k);
      boundaryPanelDistributed.setClassifier(classifier);
      boundaryPanelDistributed.setTrainingData(instances);
      boundaryPanelDistributed.setXAttribute(i);
      boundaryPanelDistributed.setYAttribute(j);
      try {
        FileInputStream fileInputStream = new FileInputStream("colors.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        FastVector fastVector = (FastVector)objectInputStream.readObject();
        boundaryPanelDistributed.setColors(fastVector);
      } catch (Exception exception) {
        System.err.println("No color map file");
      } 
      boundaryPanelDistributed.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\BoundaryPanelDistributed.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */