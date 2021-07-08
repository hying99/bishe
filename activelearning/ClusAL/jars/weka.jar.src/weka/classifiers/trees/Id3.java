package weka.classifiers.trees;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class Id3 extends Classifier {
  private Id3[] m_Successors;
  
  private Attribute m_Attribute;
  
  private double m_ClassValue;
  
  private double[] m_Distribution;
  
  private Attribute m_ClassAttribute;
  
  public String globalInfo() {
    return "Class for constructing an unpruned decision tree based on the ID3 algorithm. Can only deal with nominal attributes. No missing values allowed. Empty leaves may result in unclassified instances. For more information see: \n\n R. Quinlan (1986). \"Induction of decision trees\". Machine Learning. Vol.1, No.1, pp. 81-106";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Id3: nominal class, please."); 
    Enumeration enumeration = paramInstances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      if (!((Attribute)enumeration.nextElement()).isNominal())
        throw new UnsupportedAttributeTypeException("Id3: only nominal attributes, please."); 
    } 
    Enumeration enumeration1 = paramInstances.enumerateInstances();
    while (enumeration1.hasMoreElements()) {
      if (((Instance)enumeration1.nextElement()).hasMissingValue())
        throw new NoSupportForMissingValuesException("Id3: no missing values, please."); 
    } 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    makeTree(paramInstances);
  }
  
  private void makeTree(Instances paramInstances) throws Exception {
    if (paramInstances.numInstances() == 0) {
      this.m_Attribute = null;
      this.m_ClassValue = Instance.missingValue();
      this.m_Distribution = new double[paramInstances.numClasses()];
      return;
    } 
    double[] arrayOfDouble = new double[paramInstances.numAttributes()];
    Enumeration enumeration = paramInstances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      Attribute attribute = enumeration.nextElement();
      arrayOfDouble[attribute.index()] = computeInfoGain(paramInstances, attribute);
    } 
    this.m_Attribute = paramInstances.attribute(Utils.maxIndex(arrayOfDouble));
    if (Utils.eq(arrayOfDouble[this.m_Attribute.index()], 0.0D)) {
      this.m_Attribute = null;
      this.m_Distribution = new double[paramInstances.numClasses()];
      Enumeration enumeration1 = paramInstances.enumerateInstances();
      while (enumeration1.hasMoreElements()) {
        Instance instance = enumeration1.nextElement();
        this.m_Distribution[(int)instance.classValue()] = this.m_Distribution[(int)instance.classValue()] + 1.0D;
      } 
      Utils.normalize(this.m_Distribution);
      this.m_ClassValue = Utils.maxIndex(this.m_Distribution);
      this.m_ClassAttribute = paramInstances.classAttribute();
    } else {
      Instances[] arrayOfInstances = splitData(paramInstances, this.m_Attribute);
      this.m_Successors = new Id3[this.m_Attribute.numValues()];
      for (byte b = 0; b < this.m_Attribute.numValues(); b++) {
        this.m_Successors[b] = new Id3();
        this.m_Successors[b].makeTree(arrayOfInstances[b]);
      } 
    } 
  }
  
  public double classifyInstance(Instance paramInstance) throws NoSupportForMissingValuesException {
    if (paramInstance.hasMissingValue())
      throw new NoSupportForMissingValuesException("Id3: no missing values, please."); 
    return (this.m_Attribute == null) ? this.m_ClassValue : this.m_Successors[(int)paramInstance.value(this.m_Attribute)].classifyInstance(paramInstance);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws NoSupportForMissingValuesException {
    if (paramInstance.hasMissingValue())
      throw new NoSupportForMissingValuesException("Id3: no missing values, please."); 
    return (this.m_Attribute == null) ? this.m_Distribution : this.m_Successors[(int)paramInstance.value(this.m_Attribute)].distributionForInstance(paramInstance);
  }
  
  public String toString() {
    return (this.m_Distribution == null && this.m_Successors == null) ? "Id3: No model built yet." : ("Id3\n\n" + toString(0));
  }
  
  private double computeInfoGain(Instances paramInstances, Attribute paramAttribute) throws Exception {
    double d = computeEntropy(paramInstances);
    Instances[] arrayOfInstances = splitData(paramInstances, paramAttribute);
    for (byte b = 0; b < paramAttribute.numValues(); b++) {
      if (arrayOfInstances[b].numInstances() > 0)
        d -= arrayOfInstances[b].numInstances() / paramInstances.numInstances() * computeEntropy(arrayOfInstances[b]); 
    } 
    return d;
  }
  
  private double computeEntropy(Instances paramInstances) throws Exception {
    double[] arrayOfDouble = new double[paramInstances.numClasses()];
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      arrayOfDouble[(int)instance.classValue()] = arrayOfDouble[(int)instance.classValue()] + 1.0D;
    } 
    double d = 0.0D;
    for (byte b = 0; b < paramInstances.numClasses(); b++) {
      if (arrayOfDouble[b] > 0.0D)
        d -= arrayOfDouble[b] * Utils.log2(arrayOfDouble[b]); 
    } 
    d /= paramInstances.numInstances();
    return d + Utils.log2(paramInstances.numInstances());
  }
  
  private Instances[] splitData(Instances paramInstances, Attribute paramAttribute) {
    Instances[] arrayOfInstances = new Instances[paramAttribute.numValues()];
    for (byte b1 = 0; b1 < paramAttribute.numValues(); b1++)
      arrayOfInstances[b1] = new Instances(paramInstances, paramInstances.numInstances()); 
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      arrayOfInstances[(int)instance.value(paramAttribute)].add(instance);
    } 
    for (byte b2 = 0; b2 < arrayOfInstances.length; b2++)
      arrayOfInstances[b2].compactify(); 
    return arrayOfInstances;
  }
  
  private String toString(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_Attribute == null) {
      if (Instance.isMissingValue(this.m_ClassValue)) {
        stringBuffer.append(": null");
      } else {
        stringBuffer.append(": " + this.m_ClassAttribute.value((int)this.m_ClassValue));
      } 
    } else {
      for (byte b = 0; b < this.m_Attribute.numValues(); b++) {
        stringBuffer.append("\n");
        for (byte b1 = 0; b1 < paramInt; b1++)
          stringBuffer.append("|  "); 
        stringBuffer.append(this.m_Attribute.name() + " = " + this.m_Attribute.value(b));
        stringBuffer.append(this.m_Successors[b].toString(paramInt + 1));
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new Id3(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\Id3.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */