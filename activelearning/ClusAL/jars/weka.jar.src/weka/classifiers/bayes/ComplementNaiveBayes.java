package weka.classifiers.bayes;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class ComplementNaiveBayes extends Classifier implements OptionHandler, WeightedInstancesHandler {
  private double[][] wordWeights;
  
  private double smoothingParameter = 1.0D;
  
  private boolean m_normalizeWordWeights = false;
  
  private int numClasses;
  
  private Instances header;
  
  public Enumeration listOptions() {
    FastVector fastVector = new FastVector(2);
    fastVector.addElement(new Option("\tNormalize the word weights for each class\n", "N", 0, "-N"));
    fastVector.addElement(new Option("\tSmoothing value to avoid zero WordGivenClass probabilities (default=1.0).\n", "S", 1, "-S"));
    return fastVector.elements();
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    if (getNormalizeWordWeights())
      arrayOfString[b++] = "-N"; 
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = Double.toString(this.smoothingParameter);
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setNormalizeWordWeights(Utils.getFlag('N', paramArrayOfString));
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setSmoothingParameter(Double.parseDouble(str)); 
  }
  
  public boolean getNormalizeWordWeights() {
    return this.m_normalizeWordWeights;
  }
  
  public void setNormalizeWordWeights(boolean paramBoolean) {
    this.m_normalizeWordWeights = paramBoolean;
  }
  
  public String normalizeWordWeightsTipText() {
    return "Normalizes the word weights for each class.";
  }
  
  public double getSmoothingParameter() {
    return this.smoothingParameter;
  }
  
  public void setSmoothingParameter(double paramDouble) {
    this.smoothingParameter = paramDouble;
  }
  
  public String smoothingParameterTipText() {
    return "Sets the smoothing parameter to avoid zero WordGivenClass probabilities (default=1.0).";
  }
  
  public String globalInfo() {
    return "Class for building and using a Complement class Naive Bayes classifier. For more information see, \nICML-2003 \"Tackling the poor assumptions of Naive Bayes Text Classifiers\" \nP.S.: TF, IDF and length normalization transforms, as described in the paper, can be performed through weka.filters.unsupervised.StringToWordVector.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.numClasses = paramInstances.numClasses();
    int i = paramInstances.numAttributes();
    for (byte b1 = 0; b1 < this.numClasses; b1++) {
      for (byte b = 0; b < i; b++) {
        if (paramInstances.classIndex() == b) {
          if (!paramInstances.attribute(b).isNominal())
            throw new Exception("ComplementNaiveBayes cannot handle non-nominal class attribute"); 
        } else if (!paramInstances.attribute(b).isNumeric()) {
          throw new Exception("Attribute " + paramInstances.attribute(b).name() + " is not numeric! " + "ComplementNaiveBayes requires " + "that all attributes (except the " + "class attribute) are numeric.");
        } 
      } 
    } 
    this.header = new Instances(paramInstances, 0);
    double[][] arrayOfDouble = new double[this.numClasses][i];
    this.wordWeights = new double[this.numClasses][i];
    double[] arrayOfDouble1 = new double[this.numClasses];
    double d1 = 0.0D;
    double d2 = (i - 1) * this.smoothingParameter;
    int j = paramInstances.instance(0).classIndex();
    Enumeration enumeration = paramInstances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      int k = (int)instance.value(j);
      for (byte b = 0; b < instance.numValues(); b++) {
        if (instance.index(b) != instance.classIndex() && !instance.isMissing(b)) {
          double d = instance.valueSparse(b) * instance.weight();
          if (d < 0.0D)
            throw new Exception("Numeric attribute values must all be greater or equal to zero."); 
          d1 += d;
          arrayOfDouble1[k] = arrayOfDouble1[k] + d;
          arrayOfDouble[k][instance.index(b)] = arrayOfDouble[k][instance.index(b)] + d;
          this.wordWeights[0][instance.index(b)] = this.wordWeights[0][instance.index(b)] + d;
        } 
      } 
    } 
    byte b2;
    for (b2 = 1; b2 < this.numClasses; b2++) {
      double d = d1 - arrayOfDouble1[b2];
      for (byte b = 0; b < i; b++) {
        if (b != j) {
          double d3 = this.wordWeights[0][b] - arrayOfDouble[b2][b];
          this.wordWeights[b2][b] = Math.log((d3 + this.smoothingParameter) / (d + d2));
        } 
      } 
    } 
    for (b2 = 0; b2 < i; b2++) {
      if (b2 != j) {
        double d3 = this.wordWeights[0][b2] - arrayOfDouble[0][b2];
        double d4 = d1 - arrayOfDouble1[0];
        this.wordWeights[0][b2] = Math.log((d3 + this.smoothingParameter) / (d4 + d2));
      } 
    } 
    if (this.m_normalizeWordWeights == true)
      for (b2 = 0; b2 < this.numClasses; b2++) {
        double d = 0.0D;
        byte b;
        for (b = 0; b < i; b++) {
          if (b != j)
            d += Math.abs(this.wordWeights[b2][b]); 
        } 
        for (b = 0; b < i; b++) {
          if (b != j)
            this.wordWeights[b2][b] = this.wordWeights[b2][b] / d; 
        } 
      }  
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (this.wordWeights == null)
      throw new Exception("Error. The classifier has not been built properly."); 
    double[] arrayOfDouble = new double[this.numClasses];
    double d = 0.0D;
    byte b1;
    for (b1 = 0; b1 < this.numClasses; b1++) {
      double d1 = 0.0D;
      for (byte b = 0; b < paramInstance.numValues(); b++) {
        if (paramInstance.index(b) != paramInstance.classIndex()) {
          double d2 = paramInstance.valueSparse(b);
          d1 += d2 * this.wordWeights[b1][paramInstance.index(b)];
        } 
      } 
      arrayOfDouble[b1] = d1;
      d += arrayOfDouble[b1];
    } 
    b1 = 0;
    for (byte b2 = 0; b2 < this.numClasses; b2++) {
      if (arrayOfDouble[b2] < arrayOfDouble[b1])
        b1 = b2; 
    } 
    return b1;
  }
  
  public String toString() {
    if (this.wordWeights == null)
      return "The classifier hasn't been built yet."; 
    int i = this.header.numAttributes();
    StringBuffer stringBuffer = new StringBuffer("The word weights for each class are: \n------------------------------------\n\t");
    byte b;
    for (b = 0; b < this.numClasses; b++)
      stringBuffer.append(this.header.classAttribute().value(b)).append("\t"); 
    stringBuffer.append("\n");
    for (b = 0; b < i; b++) {
      stringBuffer.append(this.header.attribute(b).name()).append("\t");
      for (byte b1 = 0; b1 < this.numClasses; b1++)
        stringBuffer.append(Double.toString(this.wordWeights[b1][b])).append("\t"); 
      stringBuffer.append("\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new ComplementNaiveBayes(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\ComplementNaiveBayes.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */