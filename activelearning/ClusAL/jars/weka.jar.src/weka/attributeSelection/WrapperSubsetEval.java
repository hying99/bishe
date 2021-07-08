package weka.attributeSelection;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class WrapperSubsetEval extends SubsetEvaluator implements OptionHandler {
  private Instances m_trainInstances;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private Evaluation m_Evaluation;
  
  private Classifier m_BaseClassifier;
  
  private int m_folds;
  
  private int m_seed;
  
  private double m_threshold;
  
  public String globalInfo() {
    return "WrapperSubsetEval:\n\nEvaluates attribute sets by using a learning scheme. Cross validation is used to estimate the accuracy of the learning scheme for a set of attributes.\n";
  }
  
  public WrapperSubsetEval() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tclass name of base learner to use for\n\taccuracy estimation. Place any\n\tclassifier options LAST on the\n\tcommand line following a \"--\".\n\teg. -B weka.classifiers.bayes.NaiveBayes ... -- -K", "B", 1, "-B <base learner>"));
    vector.addElement(new Option("\tnumber of cross validation folds to use\n\tfor estimating accuracy.\n\t(default=5)", "F", 1, "-F <num>"));
    vector.addElement(new Option("\tSeed for cross validation accuracy \n\testimation.\n\t(default = 1)", "R", 1, "-R <seed>"));
    vector.addElement(new Option("\tthreshold by which to execute another cross validation\n\t(standard deviation---expressed as a percentage of the mean).\n\t(default=0.01(1%))", "T", 1, "-T <num>"));
    if (this.m_BaseClassifier != null && this.m_BaseClassifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific toscheme " + this.m_BaseClassifier.getClass().getName() + ":"));
      Enumeration enumeration = this.m_BaseClassifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('B', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A learning scheme must be specified with-B option"); 
    setClassifier(Classifier.forName(str, Utils.partitionOptions(paramArrayOfString)));
    str = Utils.getOption('F', paramArrayOfString);
    if (str.length() != 0)
      setFolds(Integer.parseInt(str)); 
    str = Utils.getOption('R', paramArrayOfString);
    if (str.length() != 0)
      setSeed(Integer.parseInt(str)); 
    str = Utils.getOption('T', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = Double.valueOf(str);
      setThreshold(double_.doubleValue());
    } 
  }
  
  public String thresholdTipText() {
    return "Repeat xval if stdev of mean exceeds this value.";
  }
  
  public void setThreshold(double paramDouble) {
    this.m_threshold = paramDouble;
  }
  
  public double getThreshold() {
    return this.m_threshold;
  }
  
  public String foldsTipText() {
    return "Number of xval folds to use when estimating subset accuracy.";
  }
  
  public void setFolds(int paramInt) {
    this.m_folds = paramInt;
  }
  
  public int getFolds() {
    return this.m_folds;
  }
  
  public String seedTipText() {
    return "Seed to use for randomly generating xval splits.";
  }
  
  public void setSeed(int paramInt) {
    this.m_seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_seed;
  }
  
  public String classifierTipText() {
    return "Classifier to use for estimating the accuracy of subsets";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_BaseClassifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_BaseClassifier;
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_BaseClassifier != null && this.m_BaseClassifier instanceof OptionHandler)
      arrayOfString1 = this.m_BaseClassifier.getOptions(); 
    String[] arrayOfString2 = new String[9 + arrayOfString1.length];
    int i = 0;
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-B";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "-F";
    arrayOfString2[i++] = "" + getFolds();
    arrayOfString2[i++] = "-T";
    arrayOfString2[i++] = "" + getThreshold();
    arrayOfString2[i++] = "-R";
    arrayOfString2[i++] = "" + getSeed();
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  protected void resetOptions() {
    this.m_trainInstances = null;
    this.m_Evaluation = null;
    this.m_BaseClassifier = (Classifier)new ZeroR();
    this.m_folds = 5;
    this.m_seed = 1;
    this.m_threshold = 0.01D;
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_trainInstances = paramInstances;
    this.m_classIndex = this.m_trainInstances.classIndex();
    this.m_numAttribs = this.m_trainInstances.numAttributes();
    this.m_numInstances = this.m_trainInstances.numInstances();
  }
  
  public double evaluateSubset(BitSet paramBitSet) throws Exception {
    double d = 0.0D;
    double[] arrayOfDouble = new double[5];
    boolean bool = true;
    byte b1 = 0;
    Random random = new Random(this.m_seed);
    Remove remove = new Remove();
    remove.setInvertSelection(true);
    Instances instances = new Instances(this.m_trainInstances);
    byte b2;
    for (b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    int[] arrayOfInt = new int[b1 + 1];
    b2 = 0;
    byte b3 = 0;
    while (b2 < this.m_numAttribs) {
      if (paramBitSet.get(b2))
        arrayOfInt[b3++] = b2; 
      b2++;
    } 
    arrayOfInt[b3] = this.m_classIndex;
    remove.setAttributeIndicesArray(arrayOfInt);
    remove.setInputFormat(instances);
    instances = Filter.useFilter(instances, (Filter)remove);
    for (b2 = 0; b2 < 5; b2++) {
      this.m_Evaluation = new Evaluation(instances);
      this.m_Evaluation.crossValidateModel(this.m_BaseClassifier, instances, this.m_folds, random);
      arrayOfDouble[b2] = this.m_Evaluation.errorRate();
      if (!repeat(arrayOfDouble, b2 + 1))
        break; 
    } 
    for (b3 = 0; b3 < b2; b3++)
      d += arrayOfDouble[b3]; 
    d /= b2;
    this.m_Evaluation = null;
    return -d;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainInstances == null) {
      stringBuffer.append("\tWrapper subset evaluator has not been built yet\n");
    } else {
      stringBuffer.append("\tWrapper Subset Evaluator\n");
      stringBuffer.append("\tLearning scheme: " + getClassifier().getClass().getName() + "\n");
      stringBuffer.append("\tScheme options: ");
      String[] arrayOfString = new String[0];
      if (this.m_BaseClassifier instanceof OptionHandler) {
        arrayOfString = this.m_BaseClassifier.getOptions();
        for (byte b = 0; b < arrayOfString.length; b++)
          stringBuffer.append(arrayOfString[b] + " "); 
      } 
      stringBuffer.append("\n");
      if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric()) {
        stringBuffer.append("\tAccuracy estimation: RMSE\n");
      } else {
        stringBuffer.append("\tAccuracy estimation: classification error\n");
      } 
      stringBuffer.append("\tNumber of folds for accuracy estimation: " + this.m_folds + "\n");
    } 
    return stringBuffer.toString();
  }
  
  private boolean repeat(double[] paramArrayOfdouble, int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    if (paramInt == 1)
      return true; 
    byte b;
    for (b = 0; b < paramInt; b++)
      d1 += paramArrayOfdouble[b]; 
    d1 /= paramInt;
    for (b = 0; b < paramInt; b++)
      d2 += (paramArrayOfdouble[b] - d1) * (paramArrayOfdouble[b] - d1); 
    d2 /= paramInt;
    if (d2 > 0.0D)
      d2 = Math.sqrt(d2); 
    return (d2 / d1 > this.m_threshold);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new WrapperSubsetEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\WrapperSubsetEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */