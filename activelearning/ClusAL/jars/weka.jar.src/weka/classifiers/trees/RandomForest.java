package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class RandomForest extends Classifier implements OptionHandler, Randomizable, WeightedInstancesHandler, AdditionalMeasureProducer {
  protected int m_numTrees = 10;
  
  protected int m_numFeatures = 0;
  
  protected int m_randomSeed = 1;
  
  protected int m_KValue = 0;
  
  protected Bagging m_bagger = null;
  
  public String globalInfo() {
    return "Class for constructing a forest of random trees. For more information see: \n\nLeo Breiman. \"Random Forests\". Machine Learning 45 (1):5-32, October 2001.";
  }
  
  public String numTreesTipText() {
    return "The number of trees to be generated.";
  }
  
  public int getNumTrees() {
    return this.m_numTrees;
  }
  
  public void setNumTrees(int paramInt) {
    this.m_numTrees = paramInt;
  }
  
  public String numFeaturesTipText() {
    return "The number of attributes to be used in random selection (see RandomTree).";
  }
  
  public int getNumFeatures() {
    return this.m_numFeatures;
  }
  
  public void setNumFeatures(int paramInt) {
    this.m_numFeatures = paramInt;
  }
  
  public String seedTipText() {
    return "The random number seed to be used.";
  }
  
  public void setSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public int getSeed() {
    return this.m_randomSeed;
  }
  
  public double measureOutOfBagError() {
    return (this.m_bagger != null) ? this.m_bagger.measureOutOfBagError() : Double.NaN;
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureOutOfBagError");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.equalsIgnoreCase("measureOutOfBagError"))
      return measureOutOfBagError(); 
    throw new IllegalArgumentException(paramString + " not supported (RandomForest)");
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tNumber of trees to build.", "I", 1, "-I <number of trees>"));
    vector.addElement(new Option("\tNumber of features to consider (<1=int(logM+1)).", "K", 1, "-K <number of features>"));
    vector.addElement(new Option("\tSeed for random number generator.\n\t(default 1)", "S", 1, "-S"));
    return vector.elements();
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + getNumTrees();
    arrayOfString[b++] = "-K";
    arrayOfString[b++] = "" + getNumFeatures();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('I', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_numTrees = Integer.parseInt(str1);
    } else {
      this.m_numTrees = 10;
    } 
    String str2 = Utils.getOption('K', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_numFeatures = Integer.parseInt(str2);
    } else {
      this.m_numFeatures = 0;
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      setSeed(Integer.parseInt(str3));
    } else {
      setSeed(1);
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_bagger = new Bagging();
    RandomTree randomTree = new RandomTree();
    this.m_KValue = this.m_numFeatures;
    if (this.m_KValue < 1)
      this.m_KValue = (int)Utils.log2(paramInstances.numAttributes()) + 1; 
    randomTree.setKValue(this.m_KValue);
    this.m_bagger.setClassifier(randomTree);
    this.m_bagger.setSeed(this.m_randomSeed);
    this.m_bagger.setNumIterations(this.m_numTrees);
    this.m_bagger.setCalcOutOfBag(true);
    this.m_bagger.buildClassifier(paramInstances);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_bagger.distributionForInstance(paramInstance);
  }
  
  public String toString() {
    return (this.m_bagger == null) ? "Random forest not built yet" : ("Random forest of " + this.m_numTrees + " trees, each constructed while considering " + this.m_KValue + " random feature" + ((this.m_KValue == 1) ? "" : "s") + ".\n" + "Out of bag error: " + Utils.doubleToString(this.m_bagger.measureOutOfBagError(), 4) + "\n\n");
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new RandomForest(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\RandomForest.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */