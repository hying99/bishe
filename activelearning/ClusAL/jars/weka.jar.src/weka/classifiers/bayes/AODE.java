package weka.classifiers.bayes;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class AODE extends Classifier implements OptionHandler, WeightedInstancesHandler, UpdateableClassifier {
  private double[][][] m_CondiCounts;
  
  private double[] m_ClassCounts;
  
  private double[][] m_SumForCounts;
  
  private int m_NumClasses;
  
  private int m_NumAttributes;
  
  private int m_NumInstances;
  
  private int m_ClassIndex;
  
  private Instances m_Instances;
  
  private int m_TotalAttValues;
  
  private int[] m_StartAttIndex;
  
  private int[] m_NumAttValues;
  
  private double[] m_Frequencies;
  
  private double m_SumInstances;
  
  private int m_Limit;
  
  private boolean m_Debug = false;
  
  public String globalInfo() {
    return "AODE achieves highly accurate classification by averaging over all of a small space of alternative naive-Bayes-like models that have weaker (and hence less detrimental) independence assumptions than naive Bayes. The resulting algorithm is computationally efficient while delivering highly accurate classification on many learning tasks.\n\nFor more information, see\n\nG. Webb, J. Boughton & Z. Wang (2004). Not So Naive Bayes. To be published in Machine Learning. G. Webb, J. Boughton & Z. Wang (2002). <i>Averaged One-Dependence Estimators: Preliminary Results. AI2002 Data Mining Workshop, Canberra.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_SumInstances = 0.0D;
    this.m_NumClasses = paramInstances.numClasses();
    if (this.m_NumClasses < 2)
      throw new Exception("Dataset has no class attribute"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new Exception("AODE: Class is numeric!"); 
    if (paramInstances.checkForStringAttributes())
      throw new Exception("AODE: String attributes are not allowed."); 
    this.m_ClassIndex = paramInstances.classIndex();
    this.m_NumAttributes = paramInstances.numAttributes();
    byte b;
    for (b = 0; b < this.m_NumAttributes; b++) {
      Attribute attribute = paramInstances.attribute(b);
      if (!attribute.isNominal())
        throw new Exception("Attributes must be nominal.  Discretize dataset with FilteredClassifer."); 
    } 
    this.m_Instances = paramInstances;
    this.m_NumInstances = this.m_Instances.numInstances();
    this.m_StartAttIndex = new int[this.m_NumAttributes];
    this.m_NumAttValues = new int[this.m_NumAttributes];
    this.m_TotalAttValues = 0;
    for (b = 0; b < this.m_NumAttributes; b++) {
      if (b != this.m_ClassIndex) {
        this.m_StartAttIndex[b] = this.m_TotalAttValues;
        this.m_NumAttValues[b] = this.m_Instances.attribute(b).numValues();
        this.m_TotalAttValues += this.m_NumAttValues[b] + 1;
      } else {
        this.m_NumAttValues[b] = this.m_NumClasses;
      } 
    } 
    this.m_CondiCounts = new double[this.m_NumClasses][this.m_TotalAttValues][this.m_TotalAttValues];
    this.m_ClassCounts = new double[this.m_NumClasses];
    this.m_SumForCounts = new double[this.m_NumClasses][this.m_NumAttributes];
    this.m_Frequencies = new double[this.m_TotalAttValues];
    for (b = 0; b < this.m_NumInstances; b++)
      addToCounts(this.m_Instances.instance(b)); 
    this.m_Instances = new Instances(this.m_Instances, 0);
  }
  
  public void updateClassifier(Instance paramInstance) {
    addToCounts(paramInstance);
  }
  
  private void addToCounts(Instance paramInstance) {
    if (paramInstance.classIsMissing())
      return; 
    int i = (int)paramInstance.classValue();
    int j = (int)paramInstance.weight();
    this.m_ClassCounts[i] = this.m_ClassCounts[i] + j;
    this.m_SumInstances += j;
    int[] arrayOfInt = new int[this.m_NumAttributes];
    byte b;
    for (b = 0; b < this.m_NumAttributes; b++) {
      if (b == this.m_ClassIndex) {
        arrayOfInt[b] = -1;
      } else if (paramInstance.isMissing(b)) {
        arrayOfInt[b] = this.m_StartAttIndex[b] + this.m_NumAttValues[b];
      } else {
        arrayOfInt[b] = this.m_StartAttIndex[b] + (int)paramInstance.value(b);
      } 
    } 
    for (b = 0; b < this.m_NumAttributes; b++) {
      if (arrayOfInt[b] != -1) {
        this.m_Frequencies[arrayOfInt[b]] = this.m_Frequencies[arrayOfInt[b]] + j;
        if (!paramInstance.isMissing(b))
          this.m_SumForCounts[i][b] = this.m_SumForCounts[i][b] + j; 
        double[] arrayOfDouble = this.m_CondiCounts[i][arrayOfInt[b]];
        for (byte b1 = 0; b1 < this.m_NumAttributes; b1++) {
          if (arrayOfInt[b1] != -1)
            arrayOfDouble[arrayOfInt[b1]] = arrayOfDouble[arrayOfInt[b1]] + j; 
        } 
      } 
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[this.m_NumClasses];
    int[] arrayOfInt = new int[this.m_NumAttributes];
    byte b;
    for (b = 0; b < this.m_NumAttributes; b++) {
      if (paramInstance.isMissing(b) || b == this.m_ClassIndex) {
        arrayOfInt[b] = -1;
      } else {
        arrayOfInt[b] = this.m_StartAttIndex[b] + (int)paramInstance.value(b);
      } 
    } 
    for (b = 0; b < this.m_NumClasses; b++) {
      arrayOfDouble[b] = 0.0D;
      double d = 0.0D;
      byte b1 = 0;
      double[][] arrayOfDouble1 = this.m_CondiCounts[b];
      for (byte b2 = 0; b2 < this.m_NumAttributes; b2++) {
        if (arrayOfInt[b2] != -1) {
          int i = arrayOfInt[b2];
          if (this.m_Frequencies[i] >= this.m_Limit) {
            double[] arrayOfDouble2 = arrayOfDouble1[i];
            arrayOfInt[b2] = -1;
            b1++;
            double d1 = arrayOfDouble2[i];
            double d2 = this.m_Frequencies[this.m_StartAttIndex[b2] + this.m_NumAttValues[b2]];
            d = (d1 + 1.0D) / (this.m_SumInstances - d2 + (this.m_NumClasses * this.m_NumAttValues[b2]));
            for (byte b3 = 0; b3 < this.m_NumAttributes; b3++) {
              if (arrayOfInt[b3] != -1) {
                double d3 = arrayOfDouble2[this.m_StartAttIndex[b3] + this.m_NumAttValues[b3]];
                d *= (arrayOfDouble2[arrayOfInt[b3]] + 1.0D) / (d1 - d3 + this.m_NumAttValues[b3]);
              } 
            } 
            arrayOfDouble[b] = arrayOfDouble[b] + d;
            arrayOfInt[b2] = i;
          } 
        } 
      } 
      if (b1 < 1) {
        arrayOfDouble[b] = NBconditionalProb(paramInstance, b);
      } else {
        arrayOfDouble[b] = arrayOfDouble[b] / b1;
      } 
    } 
    Utils.normalize(arrayOfDouble);
    return arrayOfDouble;
  }
  
  public double NBconditionalProb(Instance paramInstance, int paramInt) {
    double d = (this.m_ClassCounts[paramInt] + 1.0D) / (this.m_SumInstances + this.m_NumClasses);
    double[][] arrayOfDouble = this.m_CondiCounts[paramInt];
    for (byte b = 0; b < this.m_NumAttributes; b++) {
      if (b != this.m_ClassIndex && !paramInstance.isMissing(b)) {
        int i = this.m_StartAttIndex[b] + (int)paramInstance.value(b);
        d *= (arrayOfDouble[i][i] + 1.0D) / (this.m_SumForCounts[paramInt][b] + this.m_NumAttValues[b]);
      } 
    } 
    return d;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tOutput debugging information\n", "D", 0, "-D"));
    vector.addElement(new Option("\tImpose a frequency limit for superParents\n\t(default is 30)", "F", 1, "-F"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    this.m_Debug = Utils.getFlag('D', paramArrayOfString);
    String str = Utils.getOption('F', paramArrayOfString);
    if (str.length() != 0) {
      this.m_Limit = Integer.parseInt(str);
    } else {
      this.m_Limit = 30;
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[3];
    byte b = 0;
    if (this.m_Debug)
      arrayOfString[b++] = "-D"; 
    arrayOfString[b++] = "-F " + this.m_Limit;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("The AODE Classifier");
    if (this.m_Instances == null) {
      stringBuffer.append(": No model built yet.");
    } else {
      try {
        for (byte b = 0; b < this.m_NumClasses; b++)
          stringBuffer.append("\nClass " + this.m_Instances.classAttribute().value(b) + ": Prior probability = " + Utils.doubleToString((this.m_ClassCounts[b] + 1.0D) / (this.m_SumInstances + this.m_NumClasses), 4, 2) + "\n\n"); 
        stringBuffer.append("Dataset: " + this.m_Instances.relationName() + "\n" + "Instances: " + this.m_NumInstances + "\n" + "Attributes: " + this.m_NumAttributes + "\n" + "Frequency limit for superParents: " + this.m_Limit + "\n");
      } catch (Exception exception) {
        stringBuffer.append(exception.getMessage());
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new AODE(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\AODE.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */