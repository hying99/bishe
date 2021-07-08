package weka.attributeSelection;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class OneRAttributeEval extends AttributeEvaluator implements OptionHandler {
  private Instances m_trainInstances;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private int m_randomSeed;
  
  private int m_folds;
  
  private boolean m_evalUsingTrainingData;
  
  private int m_minBucketSize;
  
  public String globalInfo() {
    return "OneRAttributeEval :\n\nEvaluates the worth of an attribute by using the OneR classifier.\n";
  }
  
  public String seedTipText() {
    return "Set the seed for use in cross validation.";
  }
  
  public void setSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public int getSeed() {
    return this.m_randomSeed;
  }
  
  public String foldsTipText() {
    return "Set the number of folds for cross validation.";
  }
  
  public void setFolds(int paramInt) {
    this.m_folds = paramInt;
    if (this.m_folds < 2)
      this.m_folds = 2; 
  }
  
  public int getFolds() {
    return this.m_folds;
  }
  
  public String evalUsingTrainingDataTipText() {
    return "Use the training data to evaluate attributes rather than cross validation.";
  }
  
  public void setEvalUsingTrainingData(boolean paramBoolean) {
    this.m_evalUsingTrainingData = paramBoolean;
  }
  
  public String minimumBucketSizeTipText() {
    return "The minimum number of objects in a bucket (passed to OneR).";
  }
  
  public void setMinimumBucketSize(int paramInt) {
    this.m_minBucketSize = paramInt;
  }
  
  public int getMinimumBucketSize() {
    return this.m_minBucketSize;
  }
  
  public boolean getEvalUsingTrainingData() {
    return this.m_evalUsingTrainingData;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tRandom number seed for cross validation (default = 1)", "S", 1, "-S <seed>"));
    vector.addElement(new Option("\tNumber of folds for cross validation (default = 10)", "F", 1, "-F <folds>"));
    vector.addElement(new Option("\tUse training data for evaluation rather than cross validaton", "D", 0, "-D"));
    vector.addElement(new Option("\tMinimum number of objects in a bucket (passed on to OneR, default = 6)", "B", 1, "-B <minimum bucket size>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
    str = Utils.getOption('F', paramArrayOfString);
    if (str.length() != 0)
      setFolds(Integer.parseInt(str)); 
    str = Utils.getOption('B', paramArrayOfString);
    if (str.length() != 0)
      setMinimumBucketSize(Integer.parseInt(str)); 
    setEvalUsingTrainingData(Utils.getFlag('D', paramArrayOfString));
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[7];
    byte b = 0;
    if (getEvalUsingTrainingData())
      arrayOfString[b++] = "-D"; 
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + getFolds();
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + getMinimumBucketSize();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public OneRAttributeEval() {
    resetOptions();
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    this.m_trainInstances = paramInstances;
    if (this.m_trainInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_classIndex = this.m_trainInstances.classIndex();
    this.m_numAttribs = this.m_trainInstances.numAttributes();
    this.m_numInstances = this.m_trainInstances.numInstances();
    if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric())
      throw new Exception("Class must be nominal!"); 
  }
  
  protected void resetOptions() {
    this.m_trainInstances = null;
    this.m_randomSeed = 1;
    this.m_folds = 10;
    this.m_evalUsingTrainingData = false;
    this.m_minBucketSize = 6;
  }
  
  public double evaluateAttribute(int paramInt) throws Exception {
    int[] arrayOfInt = new int[2];
    Remove remove = new Remove();
    remove.setInvertSelection(true);
    Instances instances = new Instances(this.m_trainInstances);
    arrayOfInt[0] = paramInt;
    arrayOfInt[1] = instances.classIndex();
    remove.setAttributeIndicesArray(arrayOfInt);
    remove.setInputFormat(instances);
    instances = Filter.useFilter(instances, (Filter)remove);
    Evaluation evaluation = new Evaluation(instances);
    String[] arrayOfString = { "-B", "" + getMinimumBucketSize() };
    Classifier classifier = Classifier.forName("weka.classifiers.rules.OneR", arrayOfString);
    if (this.m_evalUsingTrainingData) {
      classifier.buildClassifier(instances);
      evaluation.evaluateModel(classifier, instances);
    } else {
      evaluation.crossValidateModel(classifier, instances, this.m_folds, new Random(this.m_randomSeed));
    } 
    double d = evaluation.errorRate();
    return (1.0D - d) * 100.0D;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainInstances == null) {
      stringBuffer.append("\tOneR feature evaluator has not been built yet");
    } else {
      stringBuffer.append("\tOneR feature evaluator.\n\n");
      stringBuffer.append("\tUsing ");
      if (this.m_evalUsingTrainingData) {
        stringBuffer.append("training data for evaluation of attributes.");
      } else {
        stringBuffer.append("" + getFolds() + " fold cross validation for evaluating " + "attributes.");
      } 
      stringBuffer.append("\n\tMinimum bucket size for OneR: " + getMinimumBucketSize());
    } 
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new OneRAttributeEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\OneRAttributeEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */