package weka.gui.boundaryvisualizer;

import java.util.Random;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.experiment.Task;
import weka.experiment.TaskStatusInfo;

public class RemoteBoundaryVisualizerSubTask implements Task {
  private TaskStatusInfo m_status = new TaskStatusInfo();
  
  private RemoteResult m_result;
  
  private int m_rowNumber;
  
  private int m_panelHeight;
  
  private int m_panelWidth;
  
  private Classifier m_classifier;
  
  private DataGenerator m_dataGenerator;
  
  private Instances m_trainingData;
  
  private int m_xAttribute;
  
  private int m_yAttribute;
  
  private double m_pixHeight;
  
  private double m_pixWidth;
  
  private double m_minX;
  
  private double m_minY;
  
  private double m_maxX;
  
  private double m_maxY;
  
  private int m_numOfSamplesPerRegion = 2;
  
  private int m_numOfSamplesPerGenerator;
  
  private double m_samplesBase = 2.0D;
  
  private Random m_random;
  
  private double[] m_weightingAttsValues;
  
  private boolean[] m_attsToWeightOn;
  
  private double[] m_vals;
  
  private double[] m_dist;
  
  private Instance m_predInst;
  
  public void setRowNumber(int paramInt) {
    this.m_rowNumber = paramInt;
  }
  
  public void setPanelWidth(int paramInt) {
    this.m_panelWidth = paramInt;
  }
  
  public void setPanelHeight(int paramInt) {
    this.m_panelHeight = paramInt;
  }
  
  public void setPixHeight(double paramDouble) {
    this.m_pixHeight = paramDouble;
  }
  
  public void setPixWidth(double paramDouble) {
    this.m_pixWidth = paramDouble;
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_classifier = paramClassifier;
  }
  
  public void setDataGenerator(DataGenerator paramDataGenerator) {
    this.m_dataGenerator = paramDataGenerator;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_trainingData = paramInstances;
  }
  
  public void setMinMaxX(double paramDouble1, double paramDouble2) {
    this.m_minX = paramDouble1;
    this.m_maxX = paramDouble2;
  }
  
  public void setMinMaxY(double paramDouble1, double paramDouble2) {
    this.m_minY = paramDouble1;
    this.m_maxY = paramDouble2;
  }
  
  public void setXAttribute(int paramInt) {
    this.m_xAttribute = paramInt;
  }
  
  public void setYAttribute(int paramInt) {
    this.m_yAttribute = paramInt;
  }
  
  public void setNumSamplesPerRegion(int paramInt) {
    this.m_numOfSamplesPerRegion = paramInt;
  }
  
  public void setGeneratorSamplesBase(double paramDouble) {
    this.m_samplesBase = paramDouble;
  }
  
  public void execute() {
    this.m_random = new Random((this.m_rowNumber * 11));
    this.m_dataGenerator.setSeed(this.m_rowNumber * 11);
    this.m_result = new RemoteResult(this.m_rowNumber, this.m_panelWidth);
    this.m_status.setTaskResult(this.m_result);
    this.m_status.setExecutionStatus(1);
    try {
      this.m_numOfSamplesPerGenerator = (int)Math.pow(this.m_samplesBase, (this.m_trainingData.numAttributes() - 3));
      if (this.m_trainingData == null)
        throw new Exception("No training data set (BoundaryPanel)"); 
      if (this.m_classifier == null)
        throw new Exception("No classifier set (BoundaryPanel)"); 
      if (this.m_dataGenerator == null)
        throw new Exception("No data generator set (BoundaryPanel)"); 
      if (this.m_trainingData.attribute(this.m_xAttribute).isNominal() || this.m_trainingData.attribute(this.m_yAttribute).isNominal())
        throw new Exception("Visualization dimensions must be numeric (RemoteBoundaryVisualizerSubTask)"); 
      this.m_attsToWeightOn = new boolean[this.m_trainingData.numAttributes()];
      this.m_attsToWeightOn[this.m_xAttribute] = true;
      this.m_attsToWeightOn[this.m_yAttribute] = true;
      this.m_weightingAttsValues = new double[this.m_attsToWeightOn.length];
      this.m_vals = new double[this.m_trainingData.numAttributes()];
      this.m_predInst = new Instance(1.0D, this.m_vals);
      this.m_predInst.setDataset(this.m_trainingData);
      System.err.println("Executing row number " + this.m_rowNumber);
      for (byte b = 0; b < this.m_panelWidth; b++) {
        double[] arrayOfDouble = calculateRegionProbs(b, this.m_rowNumber);
        this.m_result.setLocationProbs(b, arrayOfDouble);
        this.m_result.setPercentCompleted((int)(100.0D * b / this.m_panelWidth));
      } 
    } catch (Exception exception) {
      this.m_status.setExecutionStatus(2);
      this.m_status.setStatusMessage("Row " + this.m_rowNumber + " failed.");
      System.err.print(exception);
      return;
    } 
    this.m_status.setExecutionStatus(3);
    this.m_status.setStatusMessage("Row " + this.m_rowNumber + " completed successfully.");
  }
  
  private double[] calculateRegionProbs(int paramInt1, int paramInt2) throws Exception {
    double[] arrayOfDouble1 = new double[this.m_trainingData.classAttribute().numValues()];
    for (byte b = 0; b < this.m_numOfSamplesPerRegion; b++) {
      double[] arrayOfDouble3 = new double[this.m_trainingData.classAttribute().numValues()];
      this.m_weightingAttsValues[this.m_xAttribute] = getRandomX(paramInt1);
      this.m_weightingAttsValues[this.m_yAttribute] = getRandomY(this.m_panelHeight - paramInt2 - 1);
      this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
      double[] arrayOfDouble4 = this.m_dataGenerator.getWeights();
      double d1 = Utils.sum(arrayOfDouble4);
      int[] arrayOfInt1 = Utils.sort(arrayOfDouble4);
      int[] arrayOfInt2 = new int[arrayOfInt1.length];
      double d2 = 0.0D;
      double d3 = 0.99D * d1;
      int i = arrayOfDouble4.length - 1;
      byte b1 = 0;
      int j;
      for (j = arrayOfDouble4.length - 1; j >= 0; j--) {
        arrayOfInt2[i--] = arrayOfInt1[j];
        d2 += arrayOfDouble4[arrayOfInt1[j]];
        b1++;
        if (d2 > d3)
          break; 
      } 
      arrayOfInt1 = new int[b1];
      System.arraycopy(arrayOfInt2, i + 1, arrayOfInt1, 0, b1);
      for (j = 0; j < this.m_numOfSamplesPerGenerator; j++) {
        this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
        double[][] arrayOfDouble = this.m_dataGenerator.generateInstances(arrayOfInt1);
        for (byte b2 = 0; b2 < arrayOfDouble.length; b2++) {
          if (arrayOfDouble[b2] != null) {
            System.arraycopy(arrayOfDouble[b2], 0, this.m_vals, 0, this.m_vals.length);
            this.m_vals[this.m_xAttribute] = this.m_weightingAttsValues[this.m_xAttribute];
            this.m_vals[this.m_yAttribute] = this.m_weightingAttsValues[this.m_yAttribute];
            this.m_dist = this.m_classifier.distributionForInstance(this.m_predInst);
            for (byte b3 = 0; b3 < arrayOfDouble3.length; b3++)
              arrayOfDouble3[b3] = arrayOfDouble3[b3] + this.m_dist[b3] * arrayOfDouble4[b2]; 
          } 
        } 
      } 
      for (j = 0; j < arrayOfDouble1.length; j++)
        arrayOfDouble1[j] = arrayOfDouble1[j] + arrayOfDouble3[j] * d1; 
    } 
    Utils.normalize(arrayOfDouble1);
    double[] arrayOfDouble2 = new double[arrayOfDouble1.length];
    System.arraycopy(arrayOfDouble1, 0, arrayOfDouble2, 0, arrayOfDouble1.length);
    return arrayOfDouble2;
  }
  
  private double getRandomX(int paramInt) {
    double d = this.m_minX + paramInt * this.m_pixWidth;
    return d + this.m_random.nextDouble() * this.m_pixWidth;
  }
  
  private double getRandomY(int paramInt) {
    double d = this.m_minY + paramInt * this.m_pixHeight;
    return d + this.m_random.nextDouble() * this.m_pixHeight;
  }
  
  public TaskStatusInfo getTaskStatus() {
    return this.m_status;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\boundaryvisualizer\RemoteBoundaryVisualizerSubTask.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */