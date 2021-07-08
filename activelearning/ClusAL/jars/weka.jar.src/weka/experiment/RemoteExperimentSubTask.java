package weka.experiment;

import java.io.File;

public class RemoteExperimentSubTask implements Task {
  private TaskStatusInfo m_result = new TaskStatusInfo();
  
  private Experiment m_experiment;
  
  public RemoteExperimentSubTask() {
    this.m_result.setStatusMessage("Not running.");
    this.m_result.setExecutionStatus(0);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_experiment = paramExperiment;
  }
  
  public Experiment getExperiment() {
    return this.m_experiment;
  }
  
  public void execute() {
    String str2;
    this.m_result = new TaskStatusInfo();
    this.m_result.setStatusMessage("Running...");
    String str1 = "(sub)experiment completed successfully";
    if (this.m_experiment.getRunLower() != this.m_experiment.getRunUpper()) {
      str2 = "(datataset " + ((File)this.m_experiment.getDatasets().elementAt(0)).getName();
    } else {
      str2 = "(exp run # " + this.m_experiment.getRunLower();
    } 
    try {
      System.err.println("Initializing " + str2 + ")...");
      this.m_experiment.initialize();
      System.err.println("Iterating " + str2 + ")...");
      this.m_experiment.runExperiment();
      System.err.println("Postprocessing " + str2 + ")...");
      this.m_experiment.postProcess();
    } catch (Exception exception) {
      exception.printStackTrace();
      String str = "(sub)experiment " + str2 + ") failed : " + exception.toString();
      this.m_result.setExecutionStatus(2);
      this.m_result.setStatusMessage(str);
      this.m_result.setTaskResult("Failed");
      return;
    } 
    this.m_result.setExecutionStatus(3);
    this.m_result.setStatusMessage(str1 + " " + str2 + ").");
    this.m_result.setTaskResult("No errors");
  }
  
  public TaskStatusInfo getTaskStatus() {
    return this.m_result;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\RemoteExperimentSubTask.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */