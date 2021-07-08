package weka.filters.unsupervised.attribute;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class NumericToBinary extends PotentialClassIgnorer implements UnsupervisedFilter, StreamableFilter {
  public String globalInfo() {
    return "Converts all numeric attributes into binary attributes (apart from the class attribute, if set): if the value of the numeric attribute is exactly zero, the value of the new attribute will be zero. If the value of the numeric attribute is missing, the value of the new attribute will be missing. Otherwise, the value of the new attribute will be one. The new attributes will nominal.";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat();
    return true;
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    convertInstance(paramInstance);
    return true;
  }
  
  private void setOutputFormat() {
    int i = getInputFormat().classIndex();
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (b == i || !attribute.isNumeric()) {
        fastVector.addElement(attribute.copy());
      } else {
        StringBuffer stringBuffer = new StringBuffer(attribute.name() + "_binarized");
        FastVector fastVector1 = new FastVector(2);
        fastVector1.addElement("0");
        fastVector1.addElement("1");
        fastVector.addElement(new Attribute(stringBuffer.toString(), fastVector1));
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(i);
    setOutputFormat(instances);
  }
  
  private void convertInstance(Instance paramInstance) {
    Instance instance;
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      double[] arrayOfDouble = new double[paramInstance.numValues()];
      int[] arrayOfInt = new int[paramInstance.numValues()];
      for (byte b = 0; b < paramInstance.numValues(); b++) {
        Attribute attribute = getInputFormat().attribute(paramInstance.index(b));
        if (!attribute.isNumeric() || paramInstance.index(b) == getInputFormat().classIndex()) {
          arrayOfDouble[b] = paramInstance.valueSparse(b);
        } else if (paramInstance.isMissingSparse(b)) {
          arrayOfDouble[b] = paramInstance.valueSparse(b);
        } else {
          arrayOfDouble[b] = 1.0D;
        } 
        arrayOfInt[b] = paramInstance.index(b);
      } 
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble, arrayOfInt, outputFormatPeek().numAttributes());
    } else {
      double[] arrayOfDouble = new double[outputFormatPeek().numAttributes()];
      for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
        Attribute attribute = getInputFormat().attribute(b);
        if (!attribute.isNumeric() || b == getInputFormat().classIndex()) {
          arrayOfDouble[b] = paramInstance.value(b);
        } else if (paramInstance.isMissing(b) || paramInstance.value(b) == 0.0D) {
          arrayOfDouble[b] = paramInstance.value(b);
        } else {
          arrayOfDouble[b] = 1.0D;
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
        Filter.batchFilterFile(new NumericToBinary(), paramArrayOfString);
      } else {
        Filter.filterFile(new NumericToBinary(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\NumericToBinary.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */