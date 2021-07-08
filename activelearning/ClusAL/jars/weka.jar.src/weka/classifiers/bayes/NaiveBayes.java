package weka.classifiers.bayes;

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
import weka.core.WeightedInstancesHandler;
import weka.estimators.DiscreteEstimator;
import weka.estimators.Estimator;
import weka.estimators.KernelEstimator;
import weka.estimators.NormalEstimator;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class NaiveBayes extends Classifier implements OptionHandler, WeightedInstancesHandler {
  protected Estimator[][] m_Distributions;
  
  protected Estimator m_ClassDistribution;
  
  protected boolean m_UseKernelEstimator = false;
  
  protected boolean m_UseDiscretization = false;
  
  protected int m_NumClasses;
  
  protected Instances m_Instances;
  
  protected static final double DEFAULT_NUM_PRECISION = 0.01D;
  
  protected Discretize m_Disc = null;
  
  public String globalInfo() {
    return "Class for a Naive Bayes classifier using estimator classes. Numeric estimator precision values are chosen based on analysis of the  training data. For this reason, the classifier is not an UpdateableClassifier (which in typical usage are initialized with zero training instances) -- if you need the UpdateableClassifier functionality, use the NaiveBayesUpdateable classifier. The NaiveBayesUpdateable classifier will  use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances.\n\nFor more information on Naive Bayes classifiers, see\n\nGeorge H. John and Pat Langley (1995). Estimating Continuous Distributions in Bayesian Classifiers. Proceedings of the Eleventh Conference on Uncertainty in Artificial Intelligence. pp. 338-345. Morgan Kaufmann, San Mateo.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Naive Bayes: Class is numeric!"); 
    this.m_NumClasses = paramInstances.numClasses();
    if (this.m_NumClasses < 0)
      throw new Exception("Dataset has no class attribute"); 
    this.m_Instances = new Instances(paramInstances);
    if (this.m_UseDiscretization) {
      this.m_Disc = new Discretize();
      this.m_Disc.setInputFormat(this.m_Instances);
      this.m_Instances = Filter.useFilter(this.m_Instances, (Filter)this.m_Disc);
    } else {
      this.m_Disc = null;
    } 
    this.m_Distributions = new Estimator[this.m_Instances.numAttributes() - 1][this.m_Instances.numClasses()];
    this.m_ClassDistribution = (Estimator)new DiscreteEstimator(this.m_Instances.numClasses(), true);
    byte b = 0;
    Enumeration enumeration = this.m_Instances.enumerateAttributes();
    while (enumeration.hasMoreElements()) {
      Attribute attribute = enumeration.nextElement();
      double d = 0.01D;
      if (attribute.type() == 0) {
        this.m_Instances.sort(attribute);
        if (this.m_Instances.numInstances() > 0 && !this.m_Instances.instance(0).isMissing(attribute)) {
          double d1 = this.m_Instances.instance(0).value(attribute);
          double d2 = 0.0D;
          byte b2 = 0;
          for (byte b3 = 1; b3 < this.m_Instances.numInstances(); b3++) {
            Instance instance = this.m_Instances.instance(b3);
            if (instance.isMissing(attribute))
              break; 
            double d3 = instance.value(attribute);
            if (d3 != d1) {
              d2 += d3 - d1;
              d1 = d3;
              b2++;
            } 
          } 
          if (b2 > 0)
            d = d2 / b2; 
        } 
      } 
      for (byte b1 = 0; b1 < this.m_Instances.numClasses(); b1++) {
        switch (attribute.type()) {
          case 0:
            if (this.m_UseKernelEstimator) {
              this.m_Distributions[b][b1] = (Estimator)new KernelEstimator(d);
              break;
            } 
            this.m_Distributions[b][b1] = (Estimator)new NormalEstimator(d);
            break;
          case 1:
            this.m_Distributions[b][b1] = (Estimator)new DiscreteEstimator(attribute.numValues(), true);
            break;
          default:
            throw new Exception("Attribute type unknown to NaiveBayes");
        } 
      } 
      b++;
    } 
    Enumeration enumeration1 = this.m_Instances.enumerateInstances();
    while (enumeration1.hasMoreElements()) {
      Instance instance = enumeration1.nextElement();
      updateClassifier(instance);
    } 
    this.m_Instances = new Instances(this.m_Instances, 0);
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    if (!paramInstance.classIsMissing()) {
      Enumeration enumeration = this.m_Instances.enumerateAttributes();
      for (byte b = 0; enumeration.hasMoreElements(); b++) {
        Attribute attribute = enumeration.nextElement();
        if (!paramInstance.isMissing(attribute))
          this.m_Distributions[b][(int)paramInstance.classValue()].addValue(paramInstance.value(attribute), paramInstance.weight()); 
      } 
      this.m_ClassDistribution.addValue(paramInstance.classValue(), paramInstance.weight());
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_UseDiscretization) {
      this.m_Disc.input(paramInstance);
      paramInstance = this.m_Disc.output();
    } 
    double[] arrayOfDouble = new double[this.m_NumClasses];
    for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
      arrayOfDouble[b1] = this.m_ClassDistribution.getProbability(b1); 
    Enumeration enumeration = paramInstance.enumerateAttributes();
    for (byte b2 = 0; enumeration.hasMoreElements(); b2++) {
      Attribute attribute = enumeration.nextElement();
      if (!paramInstance.isMissing(attribute)) {
        double d = 0.0D;
        byte b;
        for (b = 0; b < this.m_NumClasses; b++) {
          double d1 = Math.max(1.0E-75D, this.m_Distributions[b2][b].getProbability(paramInstance.value(attribute)));
          arrayOfDouble[b] = arrayOfDouble[b] * d1;
          if (arrayOfDouble[b] > d)
            d = arrayOfDouble[b]; 
          if (Double.isNaN(arrayOfDouble[b]))
            throw new Exception("NaN returned from estimator for attribute " + attribute.name() + ":\n" + this.m_Distributions[b2][b].toString()); 
        } 
        if (d > 0.0D && d < 1.0E-75D)
          for (b = 0; b < this.m_NumClasses; b++)
            arrayOfDouble[b] = arrayOfDouble[b] * 1.0E75D;  
      } 
    } 
    Utils.normalize(arrayOfDouble);
    return arrayOfDouble;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tUse kernel density estimator rather than normal\n\tdistribution for numeric attributes", "K", 0, "-K"));
    vector.addElement(new Option("\tUse supervised discretization to process numeric attributes\n", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    boolean bool1 = Utils.getFlag('K', paramArrayOfString);
    boolean bool2 = Utils.getFlag('D', paramArrayOfString);
    if (bool1 && bool2)
      throw new IllegalArgumentException("Can't use both kernel density estimation and discretization!"); 
    setUseSupervisedDiscretization(bool2);
    setUseKernelEstimator(bool1);
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    if (this.m_UseKernelEstimator)
      arrayOfString[b++] = "-K"; 
    if (this.m_UseDiscretization)
      arrayOfString[b++] = "-D"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Naive Bayes Classifier");
    if (this.m_Instances == null) {
      stringBuffer.append(": No model built yet.");
    } else {
      try {
        for (byte b = 0; b < (this.m_Distributions[0]).length; b++) {
          stringBuffer.append("\n\nClass " + this.m_Instances.classAttribute().value(b) + ": Prior probability = " + Utils.doubleToString(this.m_ClassDistribution.getProbability(b), 4, 2) + "\n\n");
          Enumeration enumeration = this.m_Instances.enumerateAttributes();
          for (byte b1 = 0; enumeration.hasMoreElements(); b1++) {
            Attribute attribute = enumeration.nextElement();
            stringBuffer.append(attribute.name() + ":  " + this.m_Distributions[b1][b]);
          } 
        } 
      } catch (Exception exception) {
        stringBuffer.append(exception.getMessage());
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String useKernelEstimatorTipText() {
    return "Use a kernel estimator for numeric attributes rather than a normal distribution.";
  }
  
  public boolean getUseKernelEstimator() {
    return this.m_UseKernelEstimator;
  }
  
  public void setUseKernelEstimator(boolean paramBoolean) {
    this.m_UseKernelEstimator = paramBoolean;
    if (paramBoolean)
      setUseSupervisedDiscretization(false); 
  }
  
  public String useSupervisedDiscretizationTipText() {
    return "Use supervised discretization to convert numeric attributes to nominal ones.";
  }
  
  public boolean getUseSupervisedDiscretization() {
    return this.m_UseDiscretization;
  }
  
  public void setUseSupervisedDiscretization(boolean paramBoolean) {
    this.m_UseDiscretization = paramBoolean;
    if (paramBoolean)
      setUseKernelEstimator(false); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new NaiveBayes(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\NaiveBayes.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */