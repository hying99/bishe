package weka.classifiers.rules;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class OneR extends Classifier implements OptionHandler {
  private OneRRule m_rule;
  
  private int m_minBucketSize = 6;
  
  public String globalInfo() {
    return "Class for building and using a 1R classifier; in other words, uses the minimum-error attribute for prediction, discretizing numeric attributes. For more information, see\n\n:R.C. Holte (1993). \"Very simple classification rules perform well on most commonly used datasets\". Machine Learning, Vol. 11, pp. 63-91.";
  }
  
  public double classifyInstance(Instance paramInstance) {
    int i = 0;
    if (paramInstance.isMissing(this.m_rule.m_attr))
      return (this.m_rule.m_missingValueClass != -1) ? this.m_rule.m_missingValueClass : 0.0D; 
    if (this.m_rule.m_attr.isNominal()) {
      i = (int)paramInstance.value(this.m_rule.m_attr);
    } else {
      while (i < this.m_rule.m_breakpoints.length && paramInstance.value(this.m_rule.m_attr) >= this.m_rule.m_breakpoints[i])
        i++; 
    } 
    return this.m_rule.m_classifications[i];
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    boolean bool = true;
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Can't handle numeric class!"); 
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    if (instances.numInstances() == 0)
      throw new Exception("No instances with a class value!"); 
    Enumeration enumeration = paramInstances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      try {
        OneRRule oneRRule = newRule(enumeration.nextElement(), instances);
        if (bool || oneRRule.m_correct > this.m_rule.m_correct)
          this.m_rule = oneRRule; 
        bool = false;
      } catch (Exception exception) {}
    } 
  }
  
  public OneRRule newRule(Attribute paramAttribute, Instances paramInstances) throws Exception {
    OneRRule oneRRule;
    int[] arrayOfInt = new int[paramInstances.classAttribute().numValues()];
    if (paramAttribute.isNominal()) {
      oneRRule = newNominalRule(paramAttribute, paramInstances, arrayOfInt);
    } else {
      oneRRule = newNumericRule(paramAttribute, paramInstances, arrayOfInt);
    } 
    oneRRule.m_missingValueClass = Utils.maxIndex(arrayOfInt);
    if (arrayOfInt[oneRRule.m_missingValueClass] == 0) {
      oneRRule.m_missingValueClass = -1;
    } else {
      oneRRule.m_correct += arrayOfInt[oneRRule.m_missingValueClass];
    } 
    return oneRRule;
  }
  
  public OneRRule newNominalRule(Attribute paramAttribute, Instances paramInstances, int[] paramArrayOfint) throws Exception {
    int[][] arrayOfInt = new int[paramAttribute.numValues()][paramInstances.classAttribute().numValues()];
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (instance.isMissing(paramAttribute)) {
        paramArrayOfint[(int)instance.classValue()] = paramArrayOfint[(int)instance.classValue()] + 1;
        continue;
      } 
      arrayOfInt[(int)instance.value(paramAttribute)][(int)instance.classValue()] = arrayOfInt[(int)instance.value(paramAttribute)][(int)instance.classValue()] + 1;
    } 
    OneRRule oneRRule = new OneRRule(this, paramInstances, paramAttribute);
    for (byte b = 0; b < paramAttribute.numValues(); b++) {
      int i = Utils.maxIndex(arrayOfInt[b]);
      oneRRule.m_classifications[b] = i;
      oneRRule.m_correct += arrayOfInt[b][i];
    } 
    return oneRRule;
  }
  
  public OneRRule newNumericRule(Attribute paramAttribute, Instances paramInstances, int[] paramArrayOfint) throws Exception {
    int[] arrayOfInt1 = new int[paramInstances.numInstances()];
    double[] arrayOfDouble = new double[paramInstances.numInstances()];
    int[] arrayOfInt2 = new int[paramInstances.classAttribute().numValues()];
    int i = 0;
    int j = paramInstances.numInstances();
    paramInstances.sort(paramAttribute);
    while (j > 0 && paramInstances.instance(j - 1).isMissing(paramAttribute))
      paramArrayOfint[(int)paramInstances.instance(--j).classValue()] = paramArrayOfint[(int)paramInstances.instance(--j).classValue()] + 1; 
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b1 < j; b2++) {
      int k;
      byte b;
      for (b = 0; b < arrayOfInt2.length; b++)
        arrayOfInt2[b] = 0; 
      do {
        k = (int)paramInstances.instance(b1++).classValue();
        arrayOfInt2[k] = arrayOfInt2[k] + 1;
      } while (arrayOfInt2[k] < this.m_minBucketSize && b1 < j);
      while (b1 < j && (int)paramInstances.instance(b1).classValue() == k) {
        arrayOfInt2[k] = arrayOfInt2[k] + 1;
        b1++;
      } 
      while (b1 < j && paramInstances.instance(b1 - 1).value(paramAttribute) == paramInstances.instance(b1).value(paramAttribute))
        arrayOfInt2[(int)paramInstances.instance(b1++).classValue()] = arrayOfInt2[(int)paramInstances.instance(b1++).classValue()] + 1; 
      for (b = 0; b < arrayOfInt2.length; b++) {
        if (arrayOfInt2[b] > arrayOfInt2[k])
          k = b; 
      } 
      if (b2) {
        if (arrayOfInt2[arrayOfInt1[b2 - 1]] == arrayOfInt2[k])
          k = arrayOfInt1[b2 - 1]; 
        if (k == arrayOfInt1[b2 - 1])
          b2--; 
      } 
      i += arrayOfInt2[k];
      arrayOfInt1[b2] = k;
      if (b1 < j)
        arrayOfDouble[b2] = (paramInstances.instance(b1 - 1).value(paramAttribute) + paramInstances.instance(b1).value(paramAttribute)) / 2.0D; 
    } 
    if (b2 == 0)
      throw new Exception("Only missing values in the training data!"); 
    OneRRule oneRRule = new OneRRule(this, paramInstances, paramAttribute, b2);
    oneRRule.m_correct = i;
    for (byte b3 = 0; b3 < b2; b3++) {
      oneRRule.m_classifications[b3] = arrayOfInt1[b3];
      if (b3 < b2 - 1)
        oneRRule.m_breakpoints[b3] = arrayOfDouble[b3]; 
    } 
    return oneRRule;
  }
  
  public Enumeration listOptions() {
    String str = "\tThe minimum number of objects in a bucket (default: 6).";
    Vector vector = new Vector(1);
    vector.addElement(new Option(str, "B", 1, "-B <minimum bucket size>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('B', paramArrayOfString);
    if (str.length() != 0) {
      this.m_minBucketSize = Integer.parseInt(str);
    } else {
      this.m_minBucketSize = 6;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + this.m_minBucketSize;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    return (this.m_rule == null) ? "OneR: No model built yet." : this.m_rule.toString();
  }
  
  public String minBucketSizeTipText() {
    return "The minimum bucket size used for discretizing numeric attributes.";
  }
  
  public int getMinBucketSize() {
    return this.m_minBucketSize;
  }
  
  public void setMinBucketSize(int paramInt) {
    this.m_minBucketSize = paramInt;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new OneR(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  private class OneRRule implements Serializable {
    private Attribute m_class;
    
    private int m_numInst;
    
    private Attribute m_attr;
    
    private int m_correct;
    
    private int[] m_classifications;
    
    private int m_missingValueClass;
    
    private double[] m_breakpoints;
    
    private final OneR this$0;
    
    public OneRRule(OneR this$0, Instances param1Instances, Attribute param1Attribute) throws Exception {
      this.this$0 = this$0;
      this.m_missingValueClass = -1;
      this.m_class = param1Instances.classAttribute();
      this.m_numInst = param1Instances.numInstances();
      this.m_attr = param1Attribute;
      this.m_correct = 0;
      this.m_classifications = new int[this.m_attr.numValues()];
    }
    
    public OneRRule(OneR this$0, Instances param1Instances, Attribute param1Attribute, int param1Int) throws Exception {
      this.this$0 = this$0;
      this.m_missingValueClass = -1;
      this.m_class = param1Instances.classAttribute();
      this.m_numInst = param1Instances.numInstances();
      this.m_attr = param1Attribute;
      this.m_correct = 0;
      this.m_classifications = new int[param1Int];
      this.m_breakpoints = new double[param1Int - 1];
    }
    
    public String toString() {
      try {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.m_attr.name() + ":\n");
        for (byte b = 0; b < this.m_classifications.length; b++) {
          stringBuffer.append("\t");
          if (this.m_attr.isNominal()) {
            stringBuffer.append(this.m_attr.value(b));
          } else if (b < this.m_breakpoints.length) {
            stringBuffer.append("< " + this.m_breakpoints[b]);
          } else if (b > 0) {
            stringBuffer.append(">= " + this.m_breakpoints[b - 1]);
          } else {
            stringBuffer.append("not ?");
          } 
          stringBuffer.append("\t-> " + this.m_class.value(this.m_classifications[b]) + "\n");
        } 
        if (this.m_missingValueClass != -1)
          stringBuffer.append("\t?\t-> " + this.m_class.value(this.m_missingValueClass) + "\n"); 
        stringBuffer.append("(" + this.m_correct + "/" + this.m_numInst + " instances correct)\n");
        return stringBuffer.toString();
      } catch (Exception exception) {
        return "Can't print OneR classifier!";
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\rules\OneR.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */