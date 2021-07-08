package weka.experiment;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.Queue;

public class RemoteEngine extends UnicastRemoteObject implements Compute {
  private String m_HostName = "local";
  
  private Queue m_TaskQueue = new Queue();
  
  private Queue m_TaskIdQueue = new Queue();
  
  private Hashtable m_TaskStatus = new Hashtable();
  
  private boolean m_TaskRunning = false;
  
  public RemoteEngine(String paramString) throws RemoteException {
    this.m_HostName = paramString;
    Thread thread = new Thread(this) {
        private final RemoteEngine this$0;
        
        public void run() {
          while (true) {
            try {
              Thread.sleep(3600000L);
            } catch (InterruptedException interruptedException) {}
            if (this.this$0.m_TaskStatus.size() > 0) {
              this.this$0.purge();
              continue;
            } 
            System.err.println("RemoteEngine : purge - no tasks to check.");
          } 
        }
      };
    thread.setPriority(1);
    thread.start();
  }
  
  public synchronized Object executeTask(Task paramTask) throws RemoteException {
    String str = "" + System.currentTimeMillis() + ":";
    str = str + paramTask.hashCode();
    addTaskToQueue(paramTask, str);
    return str;
  }
  
  public Object checkStatus(Object paramObject) throws Exception {
    TaskStatusInfo taskStatusInfo1 = (TaskStatusInfo)this.m_TaskStatus.get(paramObject);
    if (taskStatusInfo1 == null)
      throw new Exception("RemoteEngine (" + this.m_HostName + ") : Task not found."); 
    TaskStatusInfo taskStatusInfo2 = new TaskStatusInfo();
    taskStatusInfo2.setExecutionStatus(taskStatusInfo1.getExecutionStatus());
    taskStatusInfo2.setStatusMessage(taskStatusInfo1.getStatusMessage());
    taskStatusInfo2.setTaskResult(taskStatusInfo1.getTaskResult());
    if (taskStatusInfo1.getExecutionStatus() == 3 || taskStatusInfo1.getExecutionStatus() == 2) {
      System.err.println("Finished/failed Task id : " + paramObject + " checked by client. Removing.");
      taskStatusInfo1.setTaskResult(null);
      taskStatusInfo1 = null;
      this.m_TaskStatus.remove(paramObject);
    } 
    taskStatusInfo1 = null;
    return taskStatusInfo2;
  }
  
  private synchronized void addTaskToQueue(Task paramTask, String paramString) {
    TaskStatusInfo taskStatusInfo = paramTask.getTaskStatus();
    if (taskStatusInfo == null)
      taskStatusInfo = new TaskStatusInfo(); 
    this.m_TaskQueue.push(paramTask);
    this.m_TaskIdQueue.push(paramString);
    taskStatusInfo.setStatusMessage("RemoteEngine (" + this.m_HostName + ") : task queued at postion: " + this.m_TaskQueue.size());
    this.m_TaskStatus.put(paramString, taskStatusInfo);
    System.err.println("Task id : " + paramString + "Queued.");
    if (!this.m_TaskRunning)
      startTask(); 
  }
  
  private void startTask() {
    if (!this.m_TaskRunning && this.m_TaskQueue.size() > 0) {
      Thread thread = new Thread(this) {
          private final RemoteEngine this$0;
          
          public void run() {
            this.this$0.m_TaskRunning = true;
            Task task = (Task)this.this$0.m_TaskQueue.pop();
            String str = (String)this.this$0.m_TaskIdQueue.pop();
            TaskStatusInfo taskStatusInfo = (TaskStatusInfo)this.this$0.m_TaskStatus.get(str);
            taskStatusInfo.setExecutionStatus(1);
            taskStatusInfo.setStatusMessage("RemoteEngine (" + this.this$0.m_HostName + ") : task running...");
            try {
              System.err.println("Launching task id : " + str + "...");
              task.execute();
              TaskStatusInfo taskStatusInfo1 = task.getTaskStatus();
              taskStatusInfo.setExecutionStatus(taskStatusInfo1.getExecutionStatus());
              taskStatusInfo.setStatusMessage("RemoteExperiment (" + this.this$0.m_HostName + ") " + taskStatusInfo1.getStatusMessage());
              taskStatusInfo.setTaskResult(taskStatusInfo1.getTaskResult());
            } catch (Exception exception) {
              taskStatusInfo.setExecutionStatus(2);
              taskStatusInfo.setStatusMessage("RemoteEngine (" + this.this$0.m_HostName + ") : task failed.");
              System.err.println("Task id " + str + "Failed!");
            } finally {
              if (this.this$0.m_TaskStatus.size() == 0)
                this.this$0.purgeClasses(); 
              this.this$0.m_TaskRunning = false;
              this.this$0.startTask();
            } 
          }
        };
      thread.setPriority(1);
      thread.start();
    } 
  }
  
  private void purgeClasses() {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      URLClassLoader uRLClassLoader = URLClassLoader.newInstance(new URL[] { new URL("file:.") }, classLoader);
      Thread.currentThread().setContextClassLoader(uRLClassLoader);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void purge() {
    Enumeration enumeration = this.m_TaskStatus.keys();
    long l = System.currentTimeMillis();
    System.err.println("RemoteEngine purge. Current time : " + l);
    while (enumeration.hasMoreElements()) {
      String str1 = enumeration.nextElement();
      System.err.print("Examining task id : " + str1 + "...");
      String str2 = str1.substring(0, str1.indexOf(':'));
      long l1 = Long.valueOf(str2).longValue();
      if (l - l1 > 3600000L) {
        TaskStatusInfo taskStatusInfo = null;
        taskStatusInfo = (TaskStatusInfo)this.m_TaskStatus.get(taskStatusInfo);
        if (taskStatusInfo != null && (taskStatusInfo.getExecutionStatus() == 3 || taskStatusInfo.getExecutionStatus() == 2)) {
          System.err.println("\nTask id : " + str1 + " has gone stale. Removing.");
          this.m_TaskStatus.remove(str1);
          taskStatusInfo.setTaskResult(null);
          taskStatusInfo = null;
        } 
        continue;
      } 
      System.err.println("ok.");
    } 
    if (this.m_TaskStatus.size() == 0)
      purgeClasses(); 
  }
  
  public static void main(String[] paramArrayOfString) {
    String str;
    if (System.getSecurityManager() == null)
      System.setSecurityManager(new RMISecurityManager()); 
    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getLocalHost();
      System.err.println("Host name : " + inetAddress.getHostName());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    if (inetAddress != null) {
      str = "//" + inetAddress.getHostName() + "/RemoteEngine";
    } else {
      str = "//localhost/RemoteEngine";
    } 
    try {
      RemoteEngine remoteEngine = new RemoteEngine(str);
      Naming.rebind(str, remoteEngine);
      System.out.println("RemoteEngine bound in RMI registry");
    } catch (Exception exception) {
      System.err.println("RemoteEngine exception: " + exception.getMessage());
      try {
        System.err.println("Attempting to start rmi registry...");
        LocateRegistry.createRegistry(1099);
        RemoteEngine remoteEngine = new RemoteEngine(str);
        Naming.rebind(str, remoteEngine);
        System.out.println("RemoteEngine bound in RMI registry");
      } catch (Exception exception1) {
        exception1.printStackTrace();
      } 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RemoteEngine.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */