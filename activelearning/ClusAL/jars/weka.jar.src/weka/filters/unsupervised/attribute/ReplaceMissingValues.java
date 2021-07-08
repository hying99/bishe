package weka.filters.unsupervised.attribute;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class ReplaceMissingValues extends PotentialClassIgnorer implements UnsupervisedFilter {
  private double[] m_ModesAndMeans = null;
  
  public String globalInfo() {
    return "Replaces all missing values for nominal and numeric attributes in a dataset with the modes and means from the training data.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    this.m_ModesAndMeans = null;
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_ModesAndMeans == null) {
      bufferInput(paramInstance);
      return false;
    } 
    convertInstance(paramInstance);
    return true;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_ModesAndMeans == null) {
      double d = getInputFormat().sumOfWeights();
      double[][] arrayOfDouble = new double[getInputFormat().numAttributes()][];
      for (byte b1 = 0; b1 < getInputFormat().numAttributes(); b1++) {
        if (getInputFormat().attribute(b1).isNominal()) {
          arrayOfDouble[b1] = new double[getInputFormat().attribute(b1).numValues()];
          arrayOfDouble[b1][0] = d;
        } 
      } 
      double[] arrayOfDouble1 = new double[getInputFormat().numAttributes()];
      for (byte b2 = 0; b2 < arrayOfDouble1.length; b2++)
        arrayOfDouble1[b2] = d; 
      double[] arrayOfDouble2 = new double[getInputFormat().numAttributes()];
      byte b3;
      for (b3 = 0; b3 < getInputFormat().numInstances(); b3++) {
        Instance instance = getInputFormat().instance(b3);
        for (byte b = 0; b < instance.numValues(); b++) {
          if (!instance.isMissingSparse(b)) {
            double d1 = instance.valueSparse(b);
            if (instance.attributeSparse(b).isNominal()) {
              arrayOfDouble[instance.index(b)][(int)d1] = arrayOfDouble[instance.index(b)][(int)d1] + instance.weight();
              arrayOfDouble[instance.index(b)][0] = arrayOfDouble[instance.index(b)][0] - instance.weight();
            } else if (instance.attributeSparse(b).isNumeric()) {
              arrayOfDouble2[instance.index(b)] = arrayOfDouble2[instance.index(b)] + instance.weight() * instance.valueSparse(b);
            } 
          } else if (instance.attributeSparse(b).isNominal()) {
            arrayOfDouble[instance.index(b)][0] = arrayOfDouble[instance.index(b)][0] - instance.weight();
          } else if (instance.attributeSparse(b).isNumeric()) {
            arrayOfDouble1[instance.index(b)] = arrayOfDouble1[instance.index(b)] - instance.weight();
          } 
        } 
      } 
      this.m_ModesAndMeans = new double[getInputFormat().numAttributes()];
      for (b3 = 0; b3 < getInputFormat().numAttributes(); b3++) {
        if (getInputFormat().attribute(b3).isNominal()) {
          this.m_ModesAndMeans[b3] = Utils.maxIndex(arrayOfDouble[b3]);
        } else if (getInputFormat().attribute(b3).isNumeric() && Utils.gr(arrayOfDouble1[b3], 0.0D)) {
          this.m_ModesAndMeans[b3] = arrayOfDouble2[b3] / arrayOfDouble1[b3];
        } 
      } 
      for (b3 = 0; b3 < getInputFormat().numInstances(); b3++)
        convertInstance(getInputFormat().instance(b3)); 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  private void convertInstance(Instance paramInstance) {
    Instance instance;
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      double[] arrayOfDouble = new double[paramInstance.numValues()];
      int[] arrayOfInt = new int[paramInstance.numValues()];
      byte b1 = 0;
      for (byte b2 = 0; b2 < paramInstance.numValues(); b2++) {
        if (paramInstance.isMissingSparse(b2) && getInputFormat().classIndex() != paramInstance.index(b2) && (paramInstance.attributeSparse(b2).isNominal() || paramInstance.attributeSparse(b2).isNumeric())) {
          if (this.m_ModesAndMeans[paramInstance.index(b2)] != 0.0D) {
            arrayOfDouble[b1] = this.m_ModesAndMeans[paramInstance.index(b2)];
            arrayOfInt[b1] = paramInstance.index(b2);
            b1++;
          } 
        } else {
          arrayOfDouble[b1] = paramInstance.valueSparse(b2);
          arrayOfInt[b1] = paramInstance.index(b2);
          b1++;
        } 
      } 
      if (b1 == paramInstance.numValues()) {
        sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble, arrayOfInt, paramInstance.numAttributes());
      } else {
        double[] arrayOfDouble1 = new double[b1];
        int[] arrayOfInt1 = new int[b1];
        System.arraycopy(arrayOfDouble, 0, arrayOfDouble1, 0, b1);
        System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, b1);
        sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble1, arrayOfInt1, paramInstance.numAttributes());
      } 
    } else {
      double[] arrayOfDouble = new double[getInputFormat().numAttributes()];
      for (byte b = 0; b < paramInstance.numAttributes(); b++) {
        if (paramInstance.isMissing(b) && getInputFormat().classIndex() != b && (getInputFormat().attribute(b).isNominal() || getInputFormat().attribute(b).isNumeric())) {
          arrayOfDouble[b] = this.m_ModesAndMeans[b];
        } else {
          arrayOfDouble[b] = paramInstance.value(b);
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
        Filter.batchFilterFile(new ReplaceMissingValues(), paramArrayOfString);
      } else {
        Filter.filterFile(new ReplaceMissingValues(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\ReplaceMissingValues.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */