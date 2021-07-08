package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Randomizable;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class Bagging extends RandomizableIteratedSingleClassifierEnhancer implements WeightedInstancesHandler, AdditionalMeasureProducer {
  protected int m_BagSizePercent = 100;
  
  protected boolean m_CalcOutOfBag = false;
  
  protected double m_OutOfBagError;
  
  public String globalInfo() {
    return "Class for bagging a classifier to reduce variance. Can do classification and regression depending on the base learner. For more information, see\n\nLeo Breiman (1996). \"Bagging predictors\". Machine Learning, 24(2):123-140.";
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.REPTree";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSize of each bag, as a percentage of the\n\ttraining set size. (default 100)", "P", 1, "-P"));
    vector.addElement(new Option("\tCalculate the out of bag error.", "O", 0, "-O"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0) {
      setBagSizePercent(Integer.parseInt(str));
    } else {
      setBagSizePercent(100);
    } 
    setCalcOutOfBag(Utils.getFlag('O', paramArrayOfString));
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 3];
    int i = 0;
    arrayOfString2[i++] = "-P";
    arrayOfString2[i++] = "" + getBagSizePercent();
    if (getCalcOutOfBag())
      arrayOfString2[i++] = "-O"; 
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String bagSizePercentTipText() {
    return "Size of each bag, as a percentage of the training set size.";
  }
  
  public int getBagSizePercent() {
    return this.m_BagSizePercent;
  }
  
  public void setBagSizePercent(int paramInt) {
    this.m_BagSizePercent = paramInt;
  }
  
  public String calcOutOfBagTipText() {
    return "Whether the out-of-bag error is calculated.";
  }
  
  public void setCalcOutOfBag(boolean paramBoolean) {
    this.m_CalcOutOfBag = paramBoolean;
  }
  
  public boolean getCalcOutOfBag() {
    return this.m_CalcOutOfBag;
  }
  
  public double measureOutOfBagError() {
    return this.m_OutOfBagError;
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureOutOfBagError");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.equalsIgnoreCase("measureOutOfBagError"))
      return measureOutOfBagError(); 
    throw new IllegalArgumentException(paramString + " not supported (Bagging)");
  }
  
  public final Instances resampleWithWeights(Instances paramInstances, Random paramRandom, boolean[] paramArrayOfboolean) {
    double[] arrayOfDouble1 = new double[paramInstances.numInstances()];
    for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
      arrayOfDouble1[b1] = paramInstances.instance(b1).weight(); 
    Instances instances = new Instances(paramInstances, paramInstances.numInstances());
    if (paramInstances.numInstances() == 0)
      return instances; 
    double[] arrayOfDouble2 = new double[paramInstances.numInstances()];
    double d1 = 0.0D;
    double d2 = Utils.sum(arrayOfDouble1);
    byte b2;
    for (b2 = 0; b2 < paramInstances.numInstances(); b2++) {
      d1 += paramRandom.nextDouble();
      arrayOfDouble2[b2] = d1;
    } 
    Utils.normalize(arrayOfDouble2, d1 / d2);
    arrayOfDouble2[paramInstances.numInstances() - 1] = d2;
    b2 = 0;
    byte b3 = 0;
    d1 = 0.0D;
    while (b2 < paramInstances.numInstances() && b3 < paramInstances.numInstances()) {
      if (arrayOfDouble1[b3] < 0.0D)
        throw new IllegalArgumentException("Weights have to be positive."); 
      d1 += arrayOfDouble1[b3];
      while (b2 < paramInstances.numInstances() && arrayOfDouble2[b2] <= d1) {
        instances.add(paramInstances.instance(b3));
        paramArrayOfboolean[b3] = true;
        instances.instance(b2).setWeight(1.0D);
        b2++;
      } 
      b3++;
    } 
    return instances;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    super.buildClassifier(paramInstances);
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (this.m_CalcOutOfBag && this.m_BagSizePercent != 100)
      throw new IllegalArgumentException("Bag size needs to be 100% if out-of-bag error is to be calculated!"); 
    double d1 = 0.0D;
    double d2 = 0.0D;
    int i = paramInstances.numInstances() * this.m_BagSizePercent / 100;
    Random random = new Random(this.m_Seed);
    for (byte b = 0; b < this.m_Classifiers.length; b++) {
      Instances instances = null;
      boolean[] arrayOfBoolean = null;
      if (this.m_CalcOutOfBag) {
        arrayOfBoolean = new boolean[paramInstances.numInstances()];
        instances = resampleWithWeights(paramInstances, random, arrayOfBoolean);
      } else {
        instances = paramInstances.resampleWithWeights(random);
        if (i < paramInstances.numInstances()) {
          instances.randomize(random);
          Instances instances1 = new Instances(instances, 0, i);
          instances = instances1;
        } 
      } 
      if (this.m_Classifier instanceof Randomizable)
        ((Randomizable)this.m_Classifiers[b]).setSeed(random.nextInt()); 
      this.m_Classifiers[b].buildClassifier(instances);
      if (this.m_CalcOutOfBag)
        for (byte b1 = 0; b1 < arrayOfBoolean.length; b1++) {
          if (!arrayOfBoolean[b1]) {
            Instance instance = paramInstances.instance(b1);
            d1 += instance.weight();
            if (paramInstances.classAttribute().isNumeric()) {
              d2 += instance.weight() * Math.abs(this.m_Classifiers[b].classifyInstance(instance) - instance.classValue());
            } else if (this.m_Classifiers[b].classifyInstance(instance) != instance.classValue()) {
              d2 += instance.weight();
            } 
          } 
        }  
    } 
    this.m_OutOfBagError = d2 / d1;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    for (byte b = 0; b < this.m_NumIterations; b++) {
      if (paramInstance.classAttribute().isNumeric() == true) {
        arrayOfDouble[0] = arrayOfDouble[0] + this.m_Classifiers[b].classifyInstance(paramInstance);
      } else {
        double[] arrayOfDouble1 = this.m_Classifiers[b].distributionForInstance(paramInstance);
        for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
          arrayOfDouble[b1] = arrayOfDouble[b1] + arrayOfDouble1[b1]; 
      } 
    } 
    if (paramInstance.classAttribute().isNumeric() == true) {
      arrayOfDouble[0] = arrayOfDouble[0] / this.m_NumIterations;
      return arrayOfDouble;
    } 
    if (Utils.eq(Utils.sum(arrayOfDouble), 0.0D))
      return arrayOfDouble; 
    Utils.normalize(arrayOfDouble);
    return arrayOfDouble;
  }
  
  public String toString() {
    if (this.m_Classifiers == null)
      return "Bagging: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("All the base classifiers: \n\n");
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      stringBuffer.append(this.m_Classifiers[b].toString() + "\n\n"); 
    if (this.m_CalcOutOfBag)
      stringBuffer.append("Out of bag error: " + Utils.doubleToString(this.m_OutOfBagError, 4) + "\n\n"); 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new Bagging(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\Bagging.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */