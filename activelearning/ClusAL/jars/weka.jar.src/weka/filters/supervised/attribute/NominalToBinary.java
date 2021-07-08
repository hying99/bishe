package weka.filters.supervised.attribute;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.UnassignedClassException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class NominalToBinary extends Filter implements SupervisedFilter, OptionHandler {
  private int[][] m_Indices = (int[][])null;
  
  private boolean m_Numeric = true;
  
  private boolean m_TransformAll = false;
  
  public String globalInfo() {
    return "Converts all nominal attributes into binary numeric attributes. An attribute with k values is transformed into k binary attributes if the class is nominal (using the one-attribute-per-value approach). Binary attributes are left binary, if option '-A' is not given.If the class is numeric, k - 1 new binary attributes are generated in the manner described in \"Classification and Regression Trees\" by Breiman et al. (i.e. taking the average class value associated with each attribute value into account)";
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    if (paramInstances.classIndex() < 0)
      throw new UnassignedClassException("No class has been assigned to the instances"); 
    setOutputFormat();
    this.m_Indices = (int[][])null;
    return paramInstances.classAttribute().isNominal();
  }
  
  public boolean input(Instance paramInstance) {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_Indices != null || getInputFormat().classAttribute().isNominal()) {
      convertInstance(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_Indices == null && getInputFormat().classAttribute().isNumeric()) {
      computeAverageClassValues();
      setOutputFormat();
      for (byte b = 0; b < getInputFormat().numInstances(); b++)
        convertInstance(getInputFormat().instance(b)); 
    } 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSets if binary attributes are to be coded as nominal ones.", "N", 0, "-N"));
    vector.addElement(new Option("\tFor each nominal value a new attribute is created, \nnot only if there are more than 2 values.", "A", 0, "-A"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setBinaryAttributesNominal(Utils.getFlag('N', paramArrayOfString));
    setTransformAllValues(Utils.getFlag('A', paramArrayOfString));
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[1];
    byte b = 0;
    if (getBinaryAttributesNominal())
      arrayOfString[b++] = "-N"; 
    if (getTransformAllValues())
      arrayOfString[b++] = "-A"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String binaryAttributesNominalTipText() {
    return "Whether resulting binary attributes will be nominal.";
  }
  
  public boolean getBinaryAttributesNominal() {
    return !this.m_Numeric;
  }
  
  public void setBinaryAttributesNominal(boolean paramBoolean) {
    this.m_Numeric = !paramBoolean;
  }
  
  public String transformAllValuesTipText() {
    return "Whether all nominal values are turned into new attributes, not only if there are more than 2.";
  }
  
  public boolean getTransformAllValues() {
    return this.m_TransformAll;
  }
  
  public void setTransformAllValues(boolean paramBoolean) {
    this.m_TransformAll = paramBoolean;
  }
  
  private void computeAverageClassValues() {
    double[][] arrayOfDouble = new double[getInputFormat().numAttributes()][0];
    this.m_Indices = new int[getInputFormat().numAttributes()][0];
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (attribute.isNominal()) {
        arrayOfDouble[b] = new double[attribute.numValues()];
        double[] arrayOfDouble1 = new double[attribute.numValues()];
        byte b1;
        for (b1 = 0; b1 < getInputFormat().numInstances(); b1++) {
          Instance instance = getInputFormat().instance(b1);
          if (!instance.classIsMissing() && !instance.isMissing(b)) {
            arrayOfDouble1[(int)instance.value(b)] = arrayOfDouble1[(int)instance.value(b)] + instance.weight();
            arrayOfDouble[b][(int)instance.value(b)] = arrayOfDouble[b][(int)instance.value(b)] + instance.weight() * instance.classValue();
          } 
        } 
        double d2 = Utils.sum(arrayOfDouble[b]);
        double d1 = Utils.sum(arrayOfDouble1);
        if (Utils.gr(d1, 0.0D))
          for (b1 = 0; b1 < attribute.numValues(); b1++) {
            if (Utils.gr(arrayOfDouble1[b1], 0.0D)) {
              arrayOfDouble[b][b1] = arrayOfDouble[b][b1] / arrayOfDouble1[b1];
            } else {
              arrayOfDouble[b][b1] = d2 / d1;
            } 
          }  
        this.m_Indices[b] = Utils.sort(arrayOfDouble[b]);
      } 
    } 
  }
  
  private void setOutputFormat() {
    if (getInputFormat().classAttribute().isNominal()) {
      setOutputFormatNominal();
    } else {
      setOutputFormatNumeric();
    } 
  }
  
  private void convertInstance(Instance paramInstance) {
    if (getInputFormat().classAttribute().isNominal()) {
      convertInstanceNominal(paramInstance);
    } else {
      convertInstanceNumeric(paramInstance);
    } 
  }
  
  private void setOutputFormatNominal() {
    int i = getInputFormat().classIndex();
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (!attribute.isNominal() || b == getInputFormat().classIndex()) {
        fastVector.addElement(attribute.copy());
      } else if (attribute.numValues() <= 2 && !this.m_TransformAll) {
        if (this.m_Numeric) {
          fastVector.addElement(new Attribute(attribute.name()));
        } else {
          fastVector.addElement(attribute.copy());
        } 
      } else {
        if (b < getInputFormat().classIndex())
          i += attribute.numValues() - 1; 
        for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
          StringBuffer stringBuffer = new StringBuffer(attribute.name() + "=");
          stringBuffer.append(attribute.value(b1));
          if (this.m_Numeric) {
            fastVector.addElement(new Attribute(stringBuffer.toString()));
          } else {
            FastVector fastVector1 = new FastVector(2);
            fastVector1.addElement("f");
            fastVector1.addElement("t");
            fastVector.addElement(new Attribute(stringBuffer.toString(), fastVector1));
          } 
        } 
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(i);
    setOutputFormat(instances);
  }
  
  private void setOutputFormatNumeric() {
    if (this.m_Indices == null) {
      setOutputFormat(null);
      return;
    } 
    int i = getInputFormat().classIndex();
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (!attribute.isNominal() || b == getInputFormat().classIndex()) {
        fastVector.addElement(attribute.copy());
      } else {
        if (b < getInputFormat().classIndex())
          i += attribute.numValues() - 2; 
        for (byte b1 = 1; b1 < attribute.numValues(); b1++) {
          StringBuffer stringBuffer = new StringBuffer(attribute.name() + "=");
          for (byte b2 = b1; b2 < attribute.numValues(); b2++) {
            if (b2 > b1)
              stringBuffer.append(','); 
            stringBuffer.append(attribute.value(this.m_Indices[b][b2]));
          } 
          if (this.m_Numeric) {
            fastVector.addElement(new Attribute(stringBuffer.toString()));
          } else {
            FastVector fastVector1 = new FastVector(2);
            fastVector1.addElement("f");
            fastVector1.addElement("t");
            fastVector.addElement(new Attribute(stringBuffer.toString(), fastVector1));
          } 
        } 
      } 
    } 
    Instances instances = new Instances(getInputFormat().relationName(), fastVector, 0);
    instances.setClassIndex(i);
    setOutputFormat(instances);
  }
  
  private void convertInstanceNominal(Instance paramInstance) {
    Instance instance;
    double[] arrayOfDouble = new double[outputFormatPeek().numAttributes()];
    int i = 0;
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (!attribute.isNominal() || b == getInputFormat().classIndex()) {
        arrayOfDouble[i] = paramInstance.value(b);
        i++;
      } else if (attribute.numValues() <= 2 && !this.m_TransformAll) {
        arrayOfDouble[i] = paramInstance.value(b);
        i++;
      } else {
        if (paramInstance.isMissing(b)) {
          for (byte b1 = 0; b1 < attribute.numValues(); b1++)
            arrayOfDouble[i + b1] = paramInstance.value(b); 
        } else {
          for (byte b1 = 0; b1 < attribute.numValues(); b1++) {
            if (b1 == (int)paramInstance.value(b)) {
              arrayOfDouble[i + b1] = 1.0D;
            } else {
              arrayOfDouble[i + b1] = 0.0D;
            } 
          } 
        } 
        i += attribute.numValues();
      } 
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    copyStringValues(instance, false, paramInstance.dataset(), getInputStringIndex(), getOutputFormat(), getOutputStringIndex());
    instance.setDataset(getOutputFormat());
    push(instance);
  }
  
  private void convertInstanceNumeric(Instance paramInstance) {
    Instance instance;
    double[] arrayOfDouble = new double[outputFormatPeek().numAttributes()];
    int i = 0;
    for (byte b = 0; b < getInputFormat().numAttributes(); b++) {
      Attribute attribute = getInputFormat().attribute(b);
      if (!attribute.isNominal() || b == getInputFormat().classIndex()) {
        arrayOfDouble[i] = paramInstance.value(b);
        i++;
      } else {
        if (paramInstance.isMissing(b)) {
          for (byte b1 = 0; b1 < attribute.numValues() - 1; b1++)
            arrayOfDouble[i + b1] = paramInstance.value(b); 
        } else {
          byte b1;
          for (b1 = 0; (int)paramInstance.value(b) != this.m_Indices[b][b1]; b1++)
            arrayOfDouble[i + b1] = 1.0D; 
          while (b1 < attribute.numValues() - 1) {
            arrayOfDouble[i + b1] = 0.0D;
            b1++;
          } 
        } 
        i += attribute.numValues() - 1;
      } 
    } 
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    copyStringValues(instance, false, paramInstance.dataset(), getInputStringIndex(), getOutputFormat(), getOutputStringIndex());
    instance.setDataset(getOutputFormat());
    push(instance);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new NominalToBinary(), paramArrayOfString);
      } else {
        Filter.filterFile(new NominalToBinary(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\attribute\NominalToBinary.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */