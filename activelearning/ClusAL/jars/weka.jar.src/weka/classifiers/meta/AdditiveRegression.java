package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.IteratedSingleClassifierEnhancer;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.DecisionStump;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class AdditiveRegression extends IteratedSingleClassifierEnhancer implements OptionHandler, AdditionalMeasureProducer, WeightedInstancesHandler {
  private int m_classIndex;
  
  protected double m_shrinkage = 1.0D;
  
  protected int m_NumIterationsPerformed;
  
  protected ZeroR m_zeroR;
  
  public String globalInfo() {
    return " Meta classifier that enhances the performance of a regression base classifier. Each iteration fits a model to the residuals left by the classifier on the previous iteration. Prediction is accomplished by adding the predictions of each classifier. Reducing the shrinkage (learning rate) parameter helps prevent overfitting and has a smoothing effect but increases the learning time.  For more information see: Friedman, J.H. (1999). Stochastic Gradient Boosting. Technical Report Stanford University. http://www-stat.stanford.edu/~jhf/ftp/stobst.ps.";
  }
  
  public AdditiveRegression() {
    this((Classifier)new DecisionStump());
  }
  
  public AdditiveRegression(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.DecisionStump";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify shrinkage rate. (default = 1.0, ie. no shrinkage)\n", "S", 1, "-S"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setShrinkage(double_.doubleValue());
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    int i = 0;
    arrayOfString2[i++] = "-S";
    arrayOfString2[i++] = "" + getShrinkage();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String shrinkageTipText() {
    return "Shrinkage rate. Smaller values help prevent overfitting and have a smoothing effect (but increase learning time). Default = 1.0, ie. no shrinkage.";
  }
  
  public void setShrinkage(double paramDouble) {
    this.m_shrinkage = paramDouble;
  }
  
  public double getShrinkage() {
    return this.m_shrinkage;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    super.buildClassifier(paramInstances);
    if (paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Class must be numeric!"); 
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    this.m_classIndex = instances.classIndex();
    double d1 = 0.0D;
    double d2 = 0.0D;
    this.m_zeroR = new ZeroR();
    this.m_zeroR.buildClassifier(instances);
    instances = residualReplace(instances, (Classifier)this.m_zeroR, false);
    byte b;
    for (b = 0; b < instances.numInstances(); b++)
      d1 += instances.instance(b).weight() * instances.instance(b).classValue() * instances.instance(b).classValue(); 
    if (this.m_Debug)
      System.err.println("Sum of squared residuals (predicting the mean) : " + d1); 
    this.m_NumIterationsPerformed = 0;
    do {
      d2 = d1;
      this.m_Classifiers[this.m_NumIterationsPerformed].buildClassifier(instances);
      instances = residualReplace(instances, this.m_Classifiers[this.m_NumIterationsPerformed], true);
      d1 = 0.0D;
      for (b = 0; b < instances.numInstances(); b++)
        d1 += instances.instance(b).weight() * instances.instance(b).classValue() * instances.instance(b).classValue(); 
      if (this.m_Debug)
        System.err.println("Sum of squared residuals : " + d1); 
      this.m_NumIterationsPerformed++;
    } while (d2 - d1 > Utils.SMALL && this.m_NumIterationsPerformed < this.m_Classifiers.length);
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    double d = this.m_zeroR.classifyInstance(paramInstance);
    for (byte b = 0; b < this.m_NumIterationsPerformed; b++) {
      double d1 = this.m_Classifiers[b].classifyInstance(paramInstance);
      d1 *= getShrinkage();
      d += d1;
    } 
    return d;
  }
  
  private Instances residualReplace(Instances paramInstances, Classifier paramClassifier, boolean paramBoolean) throws Exception {
    Instances instances = new Instances(paramInstances);
    for (byte b = 0; b < instances.numInstances(); b++) {
      double d1 = paramClassifier.classifyInstance(instances.instance(b));
      if (paramBoolean)
        d1 *= getShrinkage(); 
      double d2 = instances.instance(b).classValue() - d1;
      instances.instance(b).setClassValue(d2);
    } 
    return instances;
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureNumIterations");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.compareToIgnoreCase("measureNumIterations") == 0)
      return measureNumIterations(); 
    throw new IllegalArgumentException(paramString + " not supported (AdditiveRegression)");
  }
  
  public double measureNumIterations() {
    return this.m_NumIterationsPerformed;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_NumIterations == 0)
      return "Classifier hasn't been built yet!"; 
    stringBuffer.append("Additive Regression\n\n");
    stringBuffer.append("ZeroR model\n\n" + this.m_zeroR + "\n\n");
    stringBuffer.append("Base classifier " + getClassifier().getClass().getName() + "\n\n");
    stringBuffer.append("" + this.m_NumIterationsPerformed + " models generated.\n");
    for (byte b = 0; b < this.m_NumIterationsPerformed; b++)
      stringBuffer.append("\nModel number " + b + "\n\n" + this.m_Classifiers[b] + "\n"); 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new AdditiveRegression(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\AdditiveRegression.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */