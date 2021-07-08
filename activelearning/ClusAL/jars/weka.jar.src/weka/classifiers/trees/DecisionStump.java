package weka.classifiers.trees;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.Sourcable;
import weka.core.Attribute;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class DecisionStump extends Classifier implements WeightedInstancesHandler, Sourcable {
  private int m_AttIndex;
  
  private double m_SplitPoint;
  
  private double[][] m_Distribution;
  
  private Instances m_Instances;
  
  public String globalInfo() {
    return "Class for building and using a decision stump. Usually used in conjunction with a boosting algorithm. Does regression (based on mean-squared error) or classification (based on entropy). Missing is treated as a separate value.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    boolean bool1;
    double d1 = Double.MAX_VALUE;
    double d2 = -1.7976931348623157E308D;
    byte b = -1;
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    double[][] arrayOfDouble = new double[3][paramInstances.numClasses()];
    this.m_Instances = new Instances(paramInstances);
    this.m_Instances.deleteWithMissingClass();
    if (this.m_Instances.numInstances() == 0)
      throw new IllegalArgumentException("No instances without missing class values in training file!"); 
    if (paramInstances.numAttributes() == 1)
      throw new IllegalArgumentException("Attribute missing. Need at least one attribute other than class attribute!"); 
    if (this.m_Instances.classAttribute().isNominal()) {
      bool1 = this.m_Instances.numClasses();
    } else {
      bool1 = true;
    } 
    boolean bool2 = true;
    byte b1;
    for (b1 = 0; b1 < this.m_Instances.numAttributes(); b1++) {
      if (b1 != this.m_Instances.classIndex()) {
        double d;
        this.m_Distribution = new double[3][bool1];
        if (this.m_Instances.attribute(b1).isNominal()) {
          d = findSplitNominal(b1);
        } else {
          d = findSplitNumeric(b1);
        } 
        if (bool2 || d < d1) {
          d1 = d;
          b = b1;
          d2 = this.m_SplitPoint;
          for (byte b2 = 0; b2 < 3; b2++)
            System.arraycopy(this.m_Distribution[b2], 0, arrayOfDouble[b2], 0, bool1); 
        } 
        bool2 = false;
      } 
    } 
    this.m_AttIndex = b;
    this.m_SplitPoint = d2;
    this.m_Distribution = arrayOfDouble;
    if (this.m_Instances.classAttribute().isNominal())
      for (b1 = 0; b1 < this.m_Distribution.length; b1++) {
        double d = Utils.sum(this.m_Distribution[b1]);
        if (d == 0.0D) {
          System.arraycopy(this.m_Distribution[2], 0, this.m_Distribution[b1], 0, (this.m_Distribution[2]).length);
          Utils.normalize(this.m_Distribution[b1]);
        } else {
          Utils.normalize(this.m_Distribution[b1], d);
        } 
      }  
    this.m_Instances = new Instances(this.m_Instances, 0);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_Distribution[whichSubset(paramInstance)];
  }
  
  public String toSource(String paramString) throws Exception {
    StringBuffer stringBuffer = new StringBuffer("class ");
    Attribute attribute = this.m_Instances.classAttribute();
    stringBuffer.append(paramString).append(" {\n  public static double classify(Object [] i) {\n");
    stringBuffer.append("    /* " + this.m_Instances.attribute(this.m_AttIndex).name() + " */\n");
    stringBuffer.append("    if (i[").append(this.m_AttIndex);
    stringBuffer.append("] == null) { return ");
    stringBuffer.append(sourceClass(attribute, this.m_Distribution[2])).append(";");
    if (this.m_Instances.attribute(this.m_AttIndex).isNominal()) {
      stringBuffer.append(" } else if (((String)i[").append(this.m_AttIndex);
      stringBuffer.append("]).equals(\"");
      stringBuffer.append(this.m_Instances.attribute(this.m_AttIndex).value((int)this.m_SplitPoint));
      stringBuffer.append("\")");
    } else {
      stringBuffer.append(" } else if (((Double)i[").append(this.m_AttIndex);
      stringBuffer.append("]).doubleValue() <= ").append(this.m_SplitPoint);
    } 
    stringBuffer.append(") { return ");
    stringBuffer.append(sourceClass(attribute, this.m_Distribution[0])).append(";");
    stringBuffer.append(" } else { return ");
    stringBuffer.append(sourceClass(attribute, this.m_Distribution[1])).append(";");
    stringBuffer.append(" }\n  }\n}\n");
    return stringBuffer.toString();
  }
  
  private String sourceClass(Attribute paramAttribute, double[] paramArrayOfdouble) {
    return paramAttribute.isNominal() ? Integer.toString(Utils.maxIndex(paramArrayOfdouble)) : Double.toString(paramArrayOfdouble[0]);
  }
  
  public String toString() {
    if (this.m_Instances == null)
      return "Decision Stump: No model built yet."; 
    try {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("Decision Stump\n\n");
      stringBuffer.append("Classifications\n\n");
      Attribute attribute = this.m_Instances.attribute(this.m_AttIndex);
      if (attribute.isNominal()) {
        stringBuffer.append(attribute.name() + " = " + attribute.value((int)this.m_SplitPoint) + " : ");
        stringBuffer.append(printClass(this.m_Distribution[0]));
        stringBuffer.append(attribute.name() + " != " + attribute.value((int)this.m_SplitPoint) + " : ");
        stringBuffer.append(printClass(this.m_Distribution[1]));
      } else {
        stringBuffer.append(attribute.name() + " <= " + this.m_SplitPoint + " : ");
        stringBuffer.append(printClass(this.m_Distribution[0]));
        stringBuffer.append(attribute.name() + " > " + this.m_SplitPoint + " : ");
        stringBuffer.append(printClass(this.m_Distribution[1]));
      } 
      stringBuffer.append(attribute.name() + " is missing : ");
      stringBuffer.append(printClass(this.m_Distribution[2]));
      if (this.m_Instances.classAttribute().isNominal()) {
        stringBuffer.append("\nClass distributions\n\n");
        if (attribute.isNominal()) {
          stringBuffer.append(attribute.name() + " = " + attribute.value((int)this.m_SplitPoint) + "\n");
          stringBuffer.append(printDist(this.m_Distribution[0]));
          stringBuffer.append(attribute.name() + " != " + attribute.value((int)this.m_SplitPoint) + "\n");
          stringBuffer.append(printDist(this.m_Distribution[1]));
        } else {
          stringBuffer.append(attribute.name() + " <= " + this.m_SplitPoint + "\n");
          stringBuffer.append(printDist(this.m_Distribution[0]));
          stringBuffer.append(attribute.name() + " > " + this.m_SplitPoint + "\n");
          stringBuffer.append(printDist(this.m_Distribution[1]));
        } 
        stringBuffer.append(attribute.name() + " is missing\n");
        stringBuffer.append(printDist(this.m_Distribution[2]));
      } 
      return stringBuffer.toString();
    } catch (Exception exception) {
      return "Can't print decision stump classifier!";
    } 
  }
  
  private String printDist(double[] paramArrayOfdouble) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_Instances.classAttribute().isNominal()) {
      byte b;
      for (b = 0; b < this.m_Instances.numClasses(); b++)
        stringBuffer.append(this.m_Instances.classAttribute().value(b) + "\t"); 
      stringBuffer.append("\n");
      for (b = 0; b < this.m_Instances.numClasses(); b++)
        stringBuffer.append(paramArrayOfdouble[b] + "\t"); 
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  private String printClass(double[] paramArrayOfdouble) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_Instances.classAttribute().isNominal()) {
      stringBuffer.append(this.m_Instances.classAttribute().value(Utils.maxIndex(paramArrayOfdouble)));
    } else {
      stringBuffer.append(paramArrayOfdouble[0]);
    } 
    return stringBuffer.toString() + "\n";
  }
  
  private double findSplitNominal(int paramInt) throws Exception {
    return this.m_Instances.classAttribute().isNominal() ? findSplitNominalNominal(paramInt) : findSplitNominalNumeric(paramInt);
  }
  
  private double findSplitNominalNominal(int paramInt) throws Exception {
    double d = Double.MAX_VALUE;
    double[][] arrayOfDouble1 = new double[this.m_Instances.attribute(paramInt).numValues() + 1][this.m_Instances.numClasses()];
    double[] arrayOfDouble = new double[this.m_Instances.numClasses()];
    double[][] arrayOfDouble2 = new double[3][this.m_Instances.numClasses()];
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < this.m_Instances.numInstances(); b2++) {
      Instance instance = this.m_Instances.instance(b2);
      if (instance.isMissing(paramInt)) {
        b1++;
        arrayOfDouble1[this.m_Instances.attribute(paramInt).numValues()][(int)instance.classValue()] = arrayOfDouble1[this.m_Instances.attribute(paramInt).numValues()][(int)instance.classValue()] + instance.weight();
      } else {
        arrayOfDouble1[(int)instance.value(paramInt)][(int)instance.classValue()] = arrayOfDouble1[(int)instance.value(paramInt)][(int)instance.classValue()] + instance.weight();
      } 
    } 
    for (b2 = 0; b2 < this.m_Instances.attribute(paramInt).numValues(); b2++) {
      for (byte b = 0; b < this.m_Instances.numClasses(); b++)
        arrayOfDouble[b] = arrayOfDouble[b] + arrayOfDouble1[b2][b]; 
    } 
    System.arraycopy(arrayOfDouble1[this.m_Instances.attribute(paramInt).numValues()], 0, this.m_Distribution[2], 0, this.m_Instances.numClasses());
    for (b2 = 0; b2 < this.m_Instances.attribute(paramInt).numValues(); b2++) {
      byte b;
      for (b = 0; b < this.m_Instances.numClasses(); b++) {
        this.m_Distribution[0][b] = arrayOfDouble1[b2][b];
        this.m_Distribution[1][b] = arrayOfDouble[b] - arrayOfDouble1[b2][b];
      } 
      double d1 = ContingencyTables.entropyConditionedOnRows(this.m_Distribution);
      if (d1 < d) {
        d = d1;
        this.m_SplitPoint = b2;
        for (b = 0; b < 3; b++)
          System.arraycopy(this.m_Distribution[b], 0, arrayOfDouble2[b], 0, this.m_Instances.numClasses()); 
      } 
    } 
    if (b1 == 0)
      System.arraycopy(arrayOfDouble, 0, arrayOfDouble2[2], 0, this.m_Instances.numClasses()); 
    this.m_Distribution = arrayOfDouble2;
    return d;
  }
  
  private double findSplitNominalNumeric(int paramInt) throws Exception {
    double d1 = Double.MAX_VALUE;
    double[] arrayOfDouble1 = new double[this.m_Instances.attribute(paramInt).numValues()];
    double[] arrayOfDouble2 = new double[this.m_Instances.attribute(paramInt).numValues()];
    double[] arrayOfDouble3 = new double[this.m_Instances.attribute(paramInt).numValues()];
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    double[] arrayOfDouble4 = new double[3];
    double[] arrayOfDouble5 = new double[3];
    double[][] arrayOfDouble = new double[3][1];
    byte b;
    for (b = 0; b < this.m_Instances.numInstances(); b++) {
      Instance instance = this.m_Instances.instance(b);
      if (instance.isMissing(paramInt)) {
        this.m_Distribution[2][0] = this.m_Distribution[2][0] + instance.classValue() * instance.weight();
        arrayOfDouble4[2] = arrayOfDouble4[2] + instance.classValue() * instance.classValue() * instance.weight();
        arrayOfDouble5[2] = arrayOfDouble5[2] + instance.weight();
      } else {
        arrayOfDouble3[(int)instance.value(paramInt)] = arrayOfDouble3[(int)instance.value(paramInt)] + instance.weight();
        arrayOfDouble2[(int)instance.value(paramInt)] = arrayOfDouble2[(int)instance.value(paramInt)] + instance.classValue() * instance.weight();
        arrayOfDouble1[(int)instance.value(paramInt)] = arrayOfDouble1[(int)instance.value(paramInt)] + instance.classValue() * instance.classValue() * instance.weight();
      } 
      d5 += instance.weight();
      d6 += instance.classValue() * instance.weight();
    } 
    if (d5 <= 0.0D)
      return d1; 
    for (b = 0; b < this.m_Instances.attribute(paramInt).numValues(); b++) {
      d4 += arrayOfDouble3[b];
      d2 += arrayOfDouble1[b];
      d3 += arrayOfDouble2[b];
    } 
    for (b = 0; b < this.m_Instances.attribute(paramInt).numValues(); b++) {
      this.m_Distribution[0][0] = arrayOfDouble2[b];
      arrayOfDouble4[0] = arrayOfDouble1[b];
      arrayOfDouble5[0] = arrayOfDouble3[b];
      this.m_Distribution[1][0] = d3 - arrayOfDouble2[b];
      arrayOfDouble4[1] = d2 - arrayOfDouble1[b];
      arrayOfDouble5[1] = d4 - arrayOfDouble3[b];
      double d = variance(this.m_Distribution, arrayOfDouble4, arrayOfDouble5);
      if (d < d1) {
        d1 = d;
        this.m_SplitPoint = b;
        for (byte b1 = 0; b1 < 3; b1++) {
          if (arrayOfDouble5[b1] > 0.0D) {
            arrayOfDouble[b1][0] = this.m_Distribution[b1][0] / arrayOfDouble5[b1];
          } else {
            arrayOfDouble[b1][0] = d6 / d5;
          } 
        } 
      } 
    } 
    this.m_Distribution = arrayOfDouble;
    return d1;
  }
  
  private double findSplitNumeric(int paramInt) throws Exception {
    return this.m_Instances.classAttribute().isNominal() ? findSplitNumericNominal(paramInt) : findSplitNumericNumeric(paramInt);
  }
  
  private double findSplitNumericNominal(int paramInt) throws Exception {
    double d = Double.MAX_VALUE;
    byte b1 = 0;
    double[] arrayOfDouble = new double[this.m_Instances.numClasses()];
    double[][] arrayOfDouble1 = new double[3][this.m_Instances.numClasses()];
    byte b2;
    for (b2 = 0; b2 < this.m_Instances.numInstances(); b2++) {
      Instance instance = this.m_Instances.instance(b2);
      if (!instance.isMissing(paramInt)) {
        this.m_Distribution[1][(int)instance.classValue()] = this.m_Distribution[1][(int)instance.classValue()] + instance.weight();
      } else {
        this.m_Distribution[2][(int)instance.classValue()] = this.m_Distribution[2][(int)instance.classValue()] + instance.weight();
        b1++;
      } 
    } 
    System.arraycopy(this.m_Distribution[1], 0, arrayOfDouble, 0, this.m_Instances.numClasses());
    for (b2 = 0; b2 < 3; b2++)
      System.arraycopy(this.m_Distribution[b2], 0, arrayOfDouble1[b2], 0, this.m_Instances.numClasses()); 
    this.m_Instances.sort(paramInt);
    for (b2 = 0; b2 < this.m_Instances.numInstances() - b1 + 1; b2++) {
      Instance instance1 = this.m_Instances.instance(b2);
      Instance instance2 = this.m_Instances.instance(b2 + 1);
      this.m_Distribution[0][(int)instance1.classValue()] = this.m_Distribution[0][(int)instance1.classValue()] + instance1.weight();
      this.m_Distribution[1][(int)instance1.classValue()] = this.m_Distribution[1][(int)instance1.classValue()] - instance1.weight();
      if (instance1.value(paramInt) < instance2.value(paramInt)) {
        double d2 = (instance1.value(paramInt) + instance2.value(paramInt)) / 2.0D;
        double d1 = ContingencyTables.entropyConditionedOnRows(this.m_Distribution);
        if (d1 < d) {
          this.m_SplitPoint = d2;
          d = d1;
          for (byte b = 0; b < 3; b++)
            System.arraycopy(this.m_Distribution[b], 0, arrayOfDouble1[b], 0, this.m_Instances.numClasses()); 
        } 
      } 
    } 
    if (b1 == 0)
      System.arraycopy(arrayOfDouble, 0, arrayOfDouble1[2], 0, this.m_Instances.numClasses()); 
    this.m_Distribution = arrayOfDouble1;
    return d;
  }
  
  private double findSplitNumericNumeric(int paramInt) throws Exception {
    double d1 = Double.MAX_VALUE;
    byte b1 = 0;
    double[] arrayOfDouble1 = new double[3];
    double[] arrayOfDouble2 = new double[3];
    double[][] arrayOfDouble = new double[3][1];
    double d2 = 0.0D;
    double d3 = 0.0D;
    byte b2;
    for (b2 = 0; b2 < this.m_Instances.numInstances(); b2++) {
      Instance instance = this.m_Instances.instance(b2);
      if (!instance.isMissing(paramInt)) {
        this.m_Distribution[1][0] = this.m_Distribution[1][0] + instance.classValue() * instance.weight();
        arrayOfDouble1[1] = arrayOfDouble1[1] + instance.classValue() * instance.classValue() * instance.weight();
        arrayOfDouble2[1] = arrayOfDouble2[1] + instance.weight();
      } else {
        this.m_Distribution[2][0] = this.m_Distribution[2][0] + instance.classValue() * instance.weight();
        arrayOfDouble1[2] = arrayOfDouble1[2] + instance.classValue() * instance.classValue() * instance.weight();
        arrayOfDouble2[2] = arrayOfDouble2[2] + instance.weight();
        b1++;
      } 
      d3 += instance.weight();
      d2 += instance.classValue() * instance.weight();
    } 
    if (d3 <= 0.0D)
      return d1; 
    this.m_Instances.sort(paramInt);
    for (b2 = 0; b2 < this.m_Instances.numInstances() - b1 + 1; b2++) {
      Instance instance1 = this.m_Instances.instance(b2);
      Instance instance2 = this.m_Instances.instance(b2 + 1);
      this.m_Distribution[0][0] = this.m_Distribution[0][0] + instance1.classValue() * instance1.weight();
      arrayOfDouble1[0] = arrayOfDouble1[0] + instance1.classValue() * instance1.classValue() * instance1.weight();
      arrayOfDouble2[0] = arrayOfDouble2[0] + instance1.weight();
      this.m_Distribution[1][0] = this.m_Distribution[1][0] - instance1.classValue() * instance1.weight();
      arrayOfDouble1[1] = arrayOfDouble1[1] - instance1.classValue() * instance1.classValue() * instance1.weight();
      arrayOfDouble2[1] = arrayOfDouble2[1] - instance1.weight();
      if (instance1.value(paramInt) < instance2.value(paramInt)) {
        double d5 = (instance1.value(paramInt) + instance2.value(paramInt)) / 2.0D;
        double d4 = variance(this.m_Distribution, arrayOfDouble1, arrayOfDouble2);
        if (d4 < d1) {
          this.m_SplitPoint = d5;
          d1 = d4;
          for (byte b = 0; b < 3; b++) {
            if (arrayOfDouble2[b] > 0.0D) {
              arrayOfDouble[b][0] = this.m_Distribution[b][0] / arrayOfDouble2[b];
            } else {
              arrayOfDouble[b][0] = d2 / d3;
            } 
          } 
        } 
      } 
    } 
    this.m_Distribution = arrayOfDouble;
    return d1;
  }
  
  private double variance(double[][] paramArrayOfdouble, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      if (paramArrayOfdouble2[b] > 0.0D)
        d += paramArrayOfdouble1[b] - paramArrayOfdouble[b][0] * paramArrayOfdouble[b][0] / paramArrayOfdouble2[b]; 
    } 
    return d;
  }
  
  private int whichSubset(Instance paramInstance) throws Exception {
    return paramInstance.isMissing(this.m_AttIndex) ? 2 : (paramInstance.attribute(this.m_AttIndex).isNominal() ? (((int)paramInstance.value(this.m_AttIndex) == this.m_SplitPoint) ? 0 : 1) : ((paramInstance.value(this.m_AttIndex) <= this.m_SplitPoint) ? 0 : 1));
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      DecisionStump decisionStump = new DecisionStump();
      System.out.println(Evaluation.evaluateModel(decisionStump, paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\DecisionStump.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */