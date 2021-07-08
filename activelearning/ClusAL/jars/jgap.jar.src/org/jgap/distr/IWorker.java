package org.jgap.distr;

public interface IWorker {
  public static final String CVS_REVISION = "$Revision: 1.5 $";
  
  String getDisplayName();
  
  Object getStatus();
  
  Object pause();
  
  Object stop();
  
  Object resume();
  
  Object sendCommand(WorkerCommand paramWorkerCommand);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\IWorker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */