package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
import weka.classifiers.Sourcable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class AdaBoostM1 extends RandomizableIteratedSingleClassifierEnhancer implements WeightedInstancesHandler, Sourcable {
  private static int MAX_NUM_RESAMPLING_ITERATIONS = 10;
  
  protected double[] m_Betas;
  
  protected int m_NumIterationsPerformed;
  
  protected int m_WeightThreshold = 100;
  
  protected boolean m_UseResampling;
  
  protected int m_NumClasses;
  
  public String globalInfo() {
    return "Class for boosting a nominal class classifier using the Adaboost M1 method. Only nominal class problems can be tackled. Often dramatically improves performance, but sometimes overfits. For more information, see\n\nYoav Freund and Robert E. Schapire (1996). \"Experiments with a new boosting algorithm\".  Proc International Conference on Machine Learning, pages 148-156, Morgan Kaufmann, San Francisco.";
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.DecisionStump";
  }
  
  protected Instances selectWeightQuantile(Instances paramInstances, double paramDouble) {
    int i = paramInstances.numInstances();
    Instances instances = new Instances(paramInstances, i);
    double[] arrayOfDouble = new double[i];
    double d1 = 0.0D;
    for (byte b = 0; b < i; b++) {
      arrayOfDouble[b] = paramInstances.instance(b).weight();
      d1 += arrayOfDouble[b];
    } 
    double d2 = d1 * paramDouble;
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    d1 = 0.0D;
    for (int j = i - 1; j >= 0; j--) {
      Instance instance = (Instance)paramInstances.instance(arrayOfInt[j]).copy();
      instances.add(instance);
      d1 += arrayOfDouble[arrayOfInt[j]];
      if (d1 > d2 && j > 0 && arrayOfDouble[arrayOfInt[j]] != arrayOfDouble[arrayOfInt[j - 1]])
        break; 
    } 
    if (this.m_Debug)
      System.err.println("Selected " + instances.numInstances() + " out of " + i); 
    return instances;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tPercentage of weight mass to base training on.\n\t(default 100, reduce to around 90 speed up)", "P", 1, "-P <num>"));
    vector.addElement(new Option("\tUse resampling for boosting.", "Q", 0, "-Q"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0) {
      setWeightThreshold(Integer.parseInt(str));
    } else {
      setWeightThreshold(100);
    } 
    setUseResampling(Utils.getFlag('Q', paramArrayOfString));
    if (this.m_UseResampling && str.length() != 0)
      throw new Exception("Weight pruning with resamplingnot allowed."); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    byte b = 0;
    if (getUseResampling()) {
      arrayOfString2[b++] = "-Q";
    } else {
      arrayOfString2[b++] = "-P";
      arrayOfString2[b++] = "" + getWeightThreshold();
    } 
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String weightThresholdTipText() {
    return "Weight threshold for weight pruning.";
  }
  
  public void setWeightThreshold(int paramInt) {
    this.m_WeightThreshold = paramInt;
  }
  
  public int getWeightThreshold() {
    return this.m_WeightThreshold;
  }
  
  public String useResamplingTipText() {
    return "Whether resampling is used instead of reweighting.";
  }
  
  public void setUseResampling(boolean paramBoolean) {
    this.m_UseResampling = paramBoolean;
  }
  
  public boolean getUseResampling() {
    return this.m_UseResampling;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    super.buildClassifier(paramInstances);
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new Exception("No train instances without class missing!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("AdaBoostM1 can't handle a numeric class!"); 
    this.m_NumClasses = paramInstances.numClasses();
    if (!this.m_UseResampling && this.m_Classifier instanceof WeightedInstancesHandler) {
      buildClassifierWithWeights(paramInstances);
    } else {
      buildClassifierUsingResampling(paramInstances);
    } 
  }
  
  protected void buildClassifierUsingResampling(Instances paramInstances) throws Exception {
    double d1 = 0.0D;
    int i = paramInstances.numInstances();
    Random random = new Random(this.m_Seed);
    byte b1 = 0;
    this.m_Betas = new double[this.m_Classifiers.length];
    this.m_NumIterationsPerformed = 0;
    Instances instances = new Instances(paramInstances, 0, i);
    double d2 = instances.sumOfWeights();
    for (byte b2 = 0; b2 < instances.numInstances(); b2++)
      instances.instance(b2).setWeight(instances.instance(b2).weight() / d2); 
    this.m_NumIterationsPerformed = 0;
    while (this.m_NumIterationsPerformed < this.m_Classifiers.length) {
      Instances instances1;
      double d3;
      if (this.m_Debug)
        System.err.println("Training classifier " + (this.m_NumIterationsPerformed + 1)); 
      if (this.m_WeightThreshold < 100) {
        instances1 = selectWeightQuantile(instances, this.m_WeightThreshold / 100.0D);
      } else {
        instances1 = new Instances(instances);
      } 
      b1 = 0;
      double[] arrayOfDouble = new double[instances1.numInstances()];
      for (byte b = 0; b < arrayOfDouble.length; b++)
        arrayOfDouble[b] = instances1.instance(b).weight(); 
      do {
        Instances instances2 = instances1.resampleWithWeights(random, arrayOfDouble);
        this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(instances2);
        Evaluation evaluation = new Evaluation(paramInstances);
        evaluation.evaluateModel(this.m_Classifiers[this.m_NumIterationsPerformed], instances);
        d3 = evaluation.errorRate();
        b1++;
      } while (Utils.eq(d3, 0.0D) && b1 < MAX_NUM_RESAMPLING_ITERATIONS);
      if (Utils.grOrEq(d3, 0.5D) || Utils.eq(d3, 0.0D)) {
        if (this.m_NumIterationsPerformed == 0)
          this.m_NumIterationsPerformed = 1; 
        break;
      } 
      this.m_Betas[this.m_NumIterationsPerformed] = d1 = Math.log((1.0D - d3) / d3);
      double d4 = (1.0D - d3) / d3;
      if (this.m_Debug)
        System.err.println("\terror rate = " + d3 + "  beta = " + this.m_Betas[this.m_NumIterationsPerformed]); 
      setWeights(instances, d4);
      this.m_NumIterationsPerformed++;
    } 
  }
  
  protected void setWeights(Instances paramInstances, double paramDouble) throws Exception {
    double d1 = paramInstances.sumOfWeights();
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (!Utils.eq(this.m_Classifiers[this.m_NumIterationsPerformed].classifyInstance(instance), instance.classValue()))
        instance.setWeight(instance.weight() * paramDouble); 
    } 
    double d2 = paramInstances.sumOfWeights();
    enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      instance.setWeight(instance.weight() * d1 / d2);
    } 
  }
  
  protected void buildClassifierWithWeights(Instances paramInstances) throws Exception {
    double d = 0.0D;
    int i = paramInstances.numInstances();
    this.m_Betas = new double[this.m_Classifiers.length];
    this.m_NumIterationsPerformed = 0;
    Instances instances = new Instances(paramInstances, 0, i);
    this.m_NumIterationsPerformed = 0;
    while (this.m_NumIterationsPerformed < this.m_Classifiers.length) {
      Instances instances1;
      if (this.m_Debug)
        System.err.println("Training classifier " + (this.m_NumIterationsPerformed + 1)); 
      if (this.m_WeightThreshold < 100) {
        instances1 = selectWeightQuantile(instances, this.m_WeightThreshold / 100.0D);
      } else {
        instances1 = new Instances(instances, 0, i);
      } 
      this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(instances1);
      Evaluation evaluation = new Evaluation(paramInstances);
      evaluation.evaluateModel(this.m_Classifiers[this.m_NumIterationsPerformed], instances);
      double d1 = evaluation.errorRate();
      if (Utils.grOrEq(d1, 0.5D) || Utils.eq(d1, 0.0D)) {
        if (this.m_NumIterationsPerformed == 0)
          this.m_NumIterationsPerformed = 1; 
        break;
      } 
      this.m_Betas[this.m_NumIterationsPerformed] = d = Math.log((1.0D - d1) / d1);
      double d2 = (1.0D - d1) / d1;
      if (this.m_Debug)
        System.err.println("\terror rate = " + d1 + "  beta = " + this.m_Betas[this.m_NumIterationsPerformed]); 
      setWeights(instances, d2);
      this.m_NumIterationsPerformed++;
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_NumIterationsPerformed == 0)
      throw new Exception("No model built"); 
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    if (this.m_NumIterationsPerformed == 1)
      return this.m_Classifiers[0].distributionForInstance(paramInstance); 
    for (byte b = 0; b < this.m_NumIterationsPerformed; b++)
      arrayOfDouble[(int)this.m_Classifiers[b].classifyInstance(paramInstance)] = arrayOfDouble[(int)this.m_Classifiers[b].classifyInstance(paramInstance)] + this.m_Betas[b]; 
    return Utils.logs2probs(arrayOfDouble);
  }
  
  public String toSource(String paramString) throws Exception {
    if (this.m_NumIterationsPerformed == 0)
      throw new Exception("No model built yet"); 
    if (!(this.m_Classifiers[0] instanceof Sourcable))
      throw new Exception("Base learner " + this.m_Classifier.getClass().getName() + " is not Sourcable"); 
    StringBuffer stringBuffer = new StringBuffer("class ");
    stringBuffer.append(paramString).append(" {\n\n");
    stringBuffer.append("  public static double classify(Object [] i) {\n");
    if (this.m_NumIterationsPerformed == 1) {
      stringBuffer.append("    return " + paramString + "_0.classify(i);\n");
    } else {
      stringBuffer.append("    double [] sums = new double [" + this.m_NumClasses + "];\n");
      for (byte b1 = 0; b1 < this.m_NumIterationsPerformed; b1++)
        stringBuffer.append("    sums[(int) " + paramString + '_' + b1 + ".classify(i)] += " + this.m_Betas[b1] + ";\n"); 
      stringBuffer.append("    double maxV = sums[0];\n    int maxI = 0;\n    for (int j = 1; j < " + this.m_NumClasses + "; j++) {\n" + "      if (sums[j] > maxV) { maxV = sums[j]; maxI = j; }\n" + "    }\n    return (double) maxI;\n");
    } 
    stringBuffer.append("  }\n}\n");
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      stringBuffer.append(((Sourcable)this.m_Classifiers[b]).toSource(paramString + '_' + b)); 
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_NumIterationsPerformed == 0) {
      stringBuffer.append("AdaBoostM1: No model built yet.\n");
    } else if (this.m_NumIterationsPerformed == 1) {
      stringBuffer.append("AdaBoostM1: No boosting possible, one classifier used!\n");
      stringBuffer.append(this.m_Classifiers[0].toString() + "\n");
    } else {
      stringBuffer.append("AdaBoostM1: Base classifiers and their weights: \n\n");
      for (byte b = 0; b < this.m_NumIterationsPerformed; b++) {
        stringBuffer.append(this.m_Classifiers[b].toString() + "\n\n");
        stringBuffer.append("Weight: " + Utils.roundDouble(this.m_Betas[b], 2) + "\n\n");
      } 
      stringBuffer.append("Number of performed Iterations: " + this.m_NumIterationsPerformed + "\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new AdaBoostM1(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\AdaBoostM1.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */