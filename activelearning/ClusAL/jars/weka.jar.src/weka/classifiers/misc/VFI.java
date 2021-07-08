package weka.classifiers.misc;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class VFI extends Classifier implements OptionHandler, WeightedInstancesHandler {
  protected int m_ClassIndex;
  
  protected int m_NumClasses;
  
  protected Instances m_Instances = null;
  
  protected double[][][] m_counts;
  
  protected double[] m_globalCounts;
  
  protected double[][] m_intervalBounds;
  
  protected double m_maxEntrop;
  
  protected boolean m_weightByConfidence = true;
  
  protected double m_bias = -0.6D;
  
  private double TINY = 1.0E-11D;
  
  public String globalInfo() {
    return "Classification by voting feature intervals. Intervals are constucted around each class for each attribute (basically discretization). Class counts are recorded for each interval on each attribute. Classification is by voting. For more info see Demiroz, G. and Guvenir, A. (1997) \"Classification by voting feature intervals\", ECML-97.\n\nHave added a simple attribute weighting scheme. Higher weight is assigned to more confident intervals, where confidence is a function of entropy:\nweight (att_i) = (entropy of class distrib att_i / max uncertainty)^-bias";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tDon't weight voting intervals by confidence", "C", 0, "-C"));
    vector.addElement(new Option("\tSet exponential bias towards confident intervals\n\t(default = 1.0)", "B", 1, "-B <bias>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setWeightByConfidence(!Utils.getFlag('C', paramArrayOfString));
    String str = Utils.getOption('B', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = new Double(str);
      setBias(double_.doubleValue());
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String weightByConfidenceTipText() {
    return "Weight feature intervals by confidence";
  }
  
  public void setWeightByConfidence(boolean paramBoolean) {
    this.m_weightByConfidence = paramBoolean;
  }
  
  public boolean getWeightByConfidence() {
    return this.m_weightByConfidence;
  }
  
  public String biasTipText() {
    return "Strength of bias towards more confident features";
  }
  
  public void setBias(double paramDouble) {
    this.m_bias = -paramDouble;
  }
  
  public double getBias() {
    return -this.m_bias;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[3];
    byte b = 0;
    if (!getWeightByConfidence())
      arrayOfString[b++] = "-C"; 
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + getBias();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!this.m_weightByConfidence)
      this.TINY = 0.0D; 
    if (paramInstances.classIndex() == -1)
      throw new Exception("No class attribute assigned"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("VFI: class attribute needs to be nominal!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    this.m_ClassIndex = paramInstances.classIndex();
    this.m_NumClasses = paramInstances.numClasses();
    this.m_globalCounts = new double[this.m_NumClasses];
    this.m_maxEntrop = Math.log(this.m_NumClasses) / Math.log(2.0D);
    this.m_Instances = new Instances(paramInstances, 0);
    this.m_intervalBounds = new double[paramInstances.numAttributes()][2 + 2 * this.m_NumClasses];
    byte b;
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < this.m_NumClasses * 2 + 2; b1++) {
        if (b1 == 0) {
          this.m_intervalBounds[b][b1] = Double.NEGATIVE_INFINITY;
        } else if (b1 == this.m_NumClasses * 2 + 1) {
          this.m_intervalBounds[b][b1] = Double.POSITIVE_INFINITY;
        } else if (bool) {
          this.m_intervalBounds[b][b1] = Double.NEGATIVE_INFINITY;
          bool = false;
        } else {
          this.m_intervalBounds[b][b1] = Double.POSITIVE_INFINITY;
          bool = true;
        } 
      } 
    } 
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != this.m_ClassIndex && paramInstances.attribute(b).isNumeric())
        for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++) {
          Instance instance = paramInstances.instance(b1);
          if (!instance.isMissing(b)) {
            if (instance.value(b) < this.m_intervalBounds[b][(int)instance.classValue() * 2 + 1])
              this.m_intervalBounds[b][(int)instance.classValue() * 2 + 1] = instance.value(b); 
            if (instance.value(b) > this.m_intervalBounds[b][(int)instance.classValue() * 2 + 2])
              this.m_intervalBounds[b][(int)instance.classValue() * 2 + 2] = instance.value(b); 
          } 
        }  
    } 
    this.m_counts = new double[paramInstances.numAttributes()][][];
    for (b = 0; b < paramInstances.numAttributes(); b++) {
      if (paramInstances.attribute(b).isNumeric()) {
        int[] arrayOfInt = Utils.sort(this.m_intervalBounds[b]);
        byte b1 = 1;
        for (byte b2 = 1; b2 < arrayOfInt.length; b2++) {
          if (this.m_intervalBounds[b][arrayOfInt[b2]] != this.m_intervalBounds[b][arrayOfInt[b2 - 1]])
            b1++; 
        } 
        double[] arrayOfDouble = new double[b1];
        b1 = 1;
        arrayOfDouble[0] = this.m_intervalBounds[b][arrayOfInt[0]];
        for (byte b3 = 1; b3 < arrayOfInt.length; b3++) {
          if (this.m_intervalBounds[b][arrayOfInt[b3]] != this.m_intervalBounds[b][arrayOfInt[b3 - 1]]) {
            arrayOfDouble[b1] = this.m_intervalBounds[b][arrayOfInt[b3]];
            b1++;
          } 
        } 
        this.m_intervalBounds[b] = arrayOfDouble;
        this.m_counts[b] = new double[b1][this.m_NumClasses];
      } else if (b != this.m_ClassIndex) {
        this.m_counts[b] = new double[paramInstances.attribute(b).numValues()][this.m_NumClasses];
      } 
    } 
    for (b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.instance(b);
      this.m_globalCounts[(int)paramInstances.instance(b).classValue()] = this.m_globalCounts[(int)paramInstances.instance(b).classValue()] + instance.weight();
      for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (!instance.isMissing(b1) && b1 != this.m_ClassIndex)
          if (paramInstances.attribute(b1).isNumeric()) {
            double d = instance.value(b1);
            boolean bool = false;
            for (int i = (this.m_intervalBounds[b1]).length - 1; i >= 0; i--) {
              if (d > this.m_intervalBounds[b1][i]) {
                bool = true;
                this.m_counts[b1][i][(int)instance.classValue()] = this.m_counts[b1][i][(int)instance.classValue()] + instance.weight();
                break;
              } 
              if (d == this.m_intervalBounds[b1][i]) {
                bool = true;
                this.m_counts[b1][i][(int)instance.classValue()] = this.m_counts[b1][i][(int)instance.classValue()] + instance.weight() / 2.0D;
                this.m_counts[b1][i - 1][(int)instance.classValue()] = this.m_counts[b1][i - 1][(int)instance.classValue()] + instance.weight() / 2.0D;
                break;
              } 
            } 
          } else {
            this.m_counts[b1][(int)instance.value(b1)][(int)instance.classValue()] = this.m_counts[b1][(int)instance.value(b1)][(int)instance.classValue()] + instance.weight();
          }  
      } 
    } 
  }
  
  public String toString() {
    if (this.m_Instances == null)
      return "FVI: Classifier not built yet!"; 
    StringBuffer stringBuffer = new StringBuffer("Voting feature intervals classifier\n");
    return stringBuffer.toString();
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = new double[this.m_NumClasses];
    double[] arrayOfDouble2 = new double[this.m_NumClasses];
    double d1 = 0.0D;
    double d2 = 1.0D;
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (b != this.m_ClassIndex && !paramInstance.isMissing(b)) {
        double d4 = paramInstance.value(b);
        boolean bool = false;
        if (paramInstance.attribute(b).isNumeric()) {
          for (int i = (this.m_intervalBounds[b]).length - 1; i >= 0; i--) {
            if (d4 > this.m_intervalBounds[b][i]) {
              for (byte b2 = 0; b2 < this.m_NumClasses; b2++) {
                if (this.m_globalCounts[b2] > 0.0D)
                  arrayOfDouble2[b2] = (this.m_counts[b][i][b2] + this.TINY) / (this.m_globalCounts[b2] + this.TINY); 
              } 
              bool = true;
              break;
            } 
            if (d4 == this.m_intervalBounds[b][i]) {
              for (byte b2 = 0; b2 < this.m_NumClasses; b2++) {
                if (this.m_globalCounts[b2] > 0.0D) {
                  arrayOfDouble2[b2] = (this.m_counts[b][i][b2] + this.m_counts[b][i - 1][b2]) / 2.0D + this.TINY;
                  arrayOfDouble2[b2] = arrayOfDouble2[b2] / (this.m_globalCounts[b2] + this.TINY);
                } 
              } 
              bool = true;
              break;
            } 
          } 
          if (!bool)
            throw new Exception("This shouldn't happen"); 
        } else {
          bool = true;
          for (byte b2 = 0; b2 < this.m_NumClasses; b2++) {
            if (this.m_globalCounts[b2] > 0.0D)
              arrayOfDouble2[b2] = (this.m_counts[b][(int)d4][b2] + this.TINY) / (this.m_globalCounts[b2] + this.TINY); 
          } 
        } 
        double d5 = Utils.sum(arrayOfDouble2);
        if (d5 <= 0.0D) {
          for (byte b2 = 0; b2 < arrayOfDouble2.length; b2++)
            arrayOfDouble2[b2] = 1.0D / arrayOfDouble2.length; 
        } else {
          Utils.normalize(arrayOfDouble2, d5);
        } 
        if (this.m_weightByConfidence) {
          d2 = ContingencyTables.entropy(arrayOfDouble2);
          d2 = Math.pow(d2, this.m_bias);
          if (d2 < 1.0D)
            d2 = 1.0D; 
        } 
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
          arrayOfDouble1[b1] = arrayOfDouble1[b1] + arrayOfDouble2[b1] * d2; 
      } 
    } 
    double d3 = Utils.sum(arrayOfDouble1);
    if (d3 <= 0.0D) {
      for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
        arrayOfDouble1[b1] = 1.0D / arrayOfDouble1.length; 
      return arrayOfDouble1;
    } 
    Utils.normalize(arrayOfDouble1, d3);
    return arrayOfDouble1;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new VFI(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\misc\VFI.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */