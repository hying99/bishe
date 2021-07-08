package weka.filters.unsupervised.attribute;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class Normalize extends PotentialClassIgnorer implements UnsupervisedFilter {
  private double[] m_MinArray;
  
  private double[] m_MaxArray;
  
  public String globalInfo() {
    return "Normalizes all numeric values in the given dataset (apart from the class attribute, if set). The resulting values are in [0,1] for the data used to compute the normalization intervals. ";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    this.m_MinArray = this.m_MaxArray = null;
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_MinArray == null) {
      bufferInput(paramInstance);
      return false;
    } 
    convertInstance(paramInstance);
    return true;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_MinArray == null) {
      Instances instances = getInputFormat();
      this.m_MinArray = new double[instances.numAttributes()];
      this.m_MaxArray = new double[instances.numAttributes()];
      byte b;
      for (b = 0; b < instances.numAttributes(); b++)
        this.m_MinArray[b] = Double.NaN; 
      for (b = 0; b < instances.numInstances(); b++) {
        double[] arrayOfDouble = instances.instance(b).toDoubleArray();
        for (byte b1 = 0; b1 < instances.numAttributes(); b1++) {
          if (instances.attribute(b1).isNumeric() && instances.classIndex() != b1 && !Instance.isMissingValue(arrayOfDouble[b1])) {
            this.m_MaxArray[b1] = arrayOfDouble[b1];
            this.m_MinArray[b1] = arrayOfDouble[b1];
            if (arrayOfDouble[b1] < this.m_MinArray[b1])
              this.m_MinArray[b1] = arrayOfDouble[b1]; 
            if (arrayOfDouble[b1] > this.m_MaxArray[b1])
              this.m_MaxArray[b1] = arrayOfDouble[b1]; 
          } 
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
          if (Double.isNaN(this.m_MinArray[b2]) || this.m_MaxArray[b2] == this.m_MinArray[b2]) {
            d = 0.0D;
          } else {
            d = (arrayOfDouble2[b2] - this.m_MinArray[b2]) / (this.m_MaxArray[b2] - this.m_MinArray[b2]);
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
          if (Double.isNaN(this.m_MinArray[b]) || this.m_MaxArray[b] == this.m_MinArray[b]) {
            arrayOfDouble[b] = 0.0D;
          } else {
            arrayOfDouble[b] = (arrayOfDouble[b] - this.m_MinArray[b]) / (this.m_MaxArray[b] - this.m_MinArray[b]);
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
        Filter.batchFilterFile(new Normalize(), paramArrayOfString);
      } else {
        Filter.filterFile(new Normalize(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\Normalize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */