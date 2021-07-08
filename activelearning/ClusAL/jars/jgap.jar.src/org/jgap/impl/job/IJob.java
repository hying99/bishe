package org.jgap.impl.job;

import java.io.Serializable;

public interface IJob extends Serializable, Runnable {
  public static final String CVS_REVISION = "$Revision: 1.7 $";
  
  JobResult execute(JobData paramJobData) throws Exception;
  
  JobData getJobData();
  
  void run();
  
  boolean isFinished();
  
  JobResult getResult();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\IJob.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */