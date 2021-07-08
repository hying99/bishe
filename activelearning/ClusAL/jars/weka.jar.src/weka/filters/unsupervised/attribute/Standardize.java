package weka.filters.unsupervised.attribute;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class Standardize extends PotentialClassIgnorer implements UnsupervisedFilter {
  private double[] m_Means;
  
  private double[] m_StdDevs;
  
  public String globalInfo() {
    return "Standardizes all numeric attributes in the given dataset to have zero mean and unit variance (apart from the class attribute, if set).";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    this.m_Means = this.m_StdDevs = null;
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_Means == null) {
      bufferInput(paramInstance);
      return false;
    } 
    convertInstance(paramInstance);
    return true;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_Means == null) {
      Instances instances = getInputFormat();
      this.m_Means = new double[instances.numAttributes()];
      this.m_StdDevs = new double[instances.numAttributes()];
      byte b;
      for (b = 0; b < instances.numAttributes(); b++) {
        if (instances.attribute(b).isNumeric() && instances.classIndex() != b) {
          this.m_Means[b] = instances.meanOrMode(b);
          this.m_StdDevs[b] = Math.sqrt(instances.variance(b));
        } 
      } 
      for (b = 0; b < instances.numInstances(); b++)
        convertInstance(instances.instance(b)); 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  private void convertInstance(Instance paramInstance) {
    Instance instance;
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      double[] arrayOfDouble1 = new double[paramInstance.numAttributes()];
      int[] arrayOfInt1 = new int[paramInstance.numAttributes()];
      double[] arrayOfDouble2 = paramInstance.toDoubleArray();
      byte b1 = 0;
      for (byte b2 = 0; b2 < paramInstance.numAttributes(); b2++) {
        if (paramInstance.attribute(b2).isNumeric() && !Instance.isMissingValue(arrayOfDouble2[b2]) && getInputFormat().classIndex() != b2) {
          double d;
          if (this.m_StdDevs[b2] > 0.0D) {
            d = (arrayOfDouble2[b2] - this.m_Means[b2]) / this.m_StdDevs[b2];
          } else {
            d = arrayOfDouble2[b2] - this.m_Means[b2];
          } 
          if (d != 0.0D) {
            arrayOfDouble1[b1] = d;
            arrayOfInt1[b1] = b2;
            b1++;
          } 
        } else {
          double d = arrayOfDouble2[b2];
          if (d != 0.0D) {
            arrayOfDouble1[b1] = d;
            arrayOfInt1[b1] = b2;
            b1++;
          } 
        } 
      } 
      double[] arrayOfDouble3 = new double[b1];
      int[] arrayOfInt2 = new int[b1];
      System.arraycopy(arrayOfDouble1, 0, arrayOfDouble3, 0, b1);
      System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b1);
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble3, arrayOfInt2, paramInstance.numAttributes());
    } else {
      double[] arrayOfDouble = paramInstance.toDoubleArray();
      for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
        if (paramInstance.attribute(b).isNumeric() && !Instance.isMissingValue(arrayOfDouble[b]) && getInputFormat().classIndex() != b)
          if (this.m_StdDevs[b] > 0.0D) {
            arrayOfDouble[b] = (arrayOfDouble[b] - this.m_Means[b]) / this.m_StdDevs[b];
          } else {
            arrayOfDouble[b] = arrayOfDouble[b] - this.m_Means[b];
          }  
      } 
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    instance.setDataset(paramInstance.dataset());
    push(instance);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Standardize(), paramArrayOfString);
      } else {
        Filter.filterFile(new Standardize(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Standardize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */