package weka.experiment;

import java.io.Serializable;

public interface Task extends Serializable {
  void execute();
  
  TaskStatusInfo getTaskStatus();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\Task.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */