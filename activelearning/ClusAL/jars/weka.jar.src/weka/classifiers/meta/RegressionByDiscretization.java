package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class RegressionByDiscretization extends SingleClassifierEnhancer {
  protected Discretize m_Discretizer = new Discretize();
  
  protected int m_NumBins = 10;
  
  protected double[] m_ClassMeans;
  
  public String globalInfo() {
    return "A regression scheme that employs any classifier on a copy of the data that has the class attribute (equal-width) discretized. The predicted value is the expected value of the mean class value for each discretized interval (based on the predicted probabilities for each interval).";
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.J48";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class attribute has to be numeric"); 
    this.m_Discretizer.setIgnoreClass(true);
    this.m_Discretizer.setAttributeIndices("" + (paramInstances.classIndex() + 1));
    this.m_Discretizer.setBins(getNumBins());
    this.m_Discretizer.setInputFormat(paramInstances);
    Instances instances = Filter.useFilter(paramInstances, (Filter)this.m_Discretizer);
    int i = instances.numClasses();
    this.m_ClassMeans = new double[i];
    int[] arrayOfInt = new int[i];
    byte b;
    for (b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = instances.instance(b);
      if (!instance.classIsMissing()) {
        int j = (int)instance.classValue();
        arrayOfInt[j] = arrayOfInt[j] + 1;
        this.m_ClassMeans[j] = this.m_ClassMeans[j] + paramInstances.instance(b).classValue();
      } 
    } 
    for (b = 0; b < i; b++) {
      if (arrayOfInt[b] > 0)
        this.m_ClassMeans[b] = this.m_ClassMeans[b] / arrayOfInt[b]; 
    } 
    if (this.m_Debug) {
      System.out.println("Bin Means");
      System.out.println("==========");
      for (b = 0; b < this.m_ClassMeans.length; b++)
        System.out.println(this.m_ClassMeans[b]); 
      System.out.println();
    } 
    this.m_Classifier.buildClassifier(instances);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (this.m_Discretizer.numPendingOutput() > 0)
      throw new Exception("Discretize output queue not empty"); 
    if (this.m_Discretizer.input(paramInstance)) {
      this.m_Discretizer.batchFinished();
      Instance instance = this.m_Discretizer.output();
      double[] arrayOfDouble = this.m_Classifier.distributionForInstance(instance);
      double d1 = 0.0D;
      double d2 = 0.0D;
      for (byte b = 0; b < arrayOfDouble.length; b++) {
        d1 += arrayOfDouble[b] * this.m_ClassMeans[b];
        d2 += arrayOfDouble[b];
      } 
      return d1 / d2;
    } 
    throw new Exception("Discretize didn't make the test instance immediately available");
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tNumber of bins for equal-width discretization\n\t(default 10).\n", "B", 1, "-B <int>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('B', paramArrayOfString);
    if (str.length() != 0) {
      setNumBins(Integer.parseInt(str));
    } else {
      setNumBins(10);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    byte b = 0;
    arrayOfString2[b++] = "-I";
    arrayOfString2[b++] = "" + getNumBins();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String numBinsTipText() {
    return "Number of bins for discretization.";
  }
  
  public int getNumBins() {
    return this.m_NumBins;
  }
  
  public void setNumBins(int paramInt) {
    this.m_NumBins = paramInt;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Regression by discretization");
    if (this.m_ClassMeans == null) {
      stringBuffer.append(": No model built yet.");
    } else {
      stringBuffer.append("\n\nClass attribute discretized into " + this.m_ClassMeans.length + " values\n");
      stringBuffer.append("\nClassifier spec: " + getClassifierSpec() + "\n");
      stringBuffer.append(this.m_Classifier.toString());
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new RegressionByDiscretization(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\RegressionByDiscretization.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */