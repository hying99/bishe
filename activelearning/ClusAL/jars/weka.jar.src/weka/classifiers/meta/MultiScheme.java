package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableMultipleClassifiersCombiner;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class MultiScheme extends RandomizableMultipleClassifiersCombiner {
  protected Classifier m_Classifier;
  
  protected int m_ClassifierIndex;
  
  protected int m_NumXValFolds;
  
  public String globalInfo() {
    return "Class for selecting a classifier from among several using cross validation on the training data or the performance on the training data. Performance is measured based on percent correct (classification) or mean-squared error (regression).";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tUse cross validation for model selection using the\n\tgiven number of folds. (default 0, is to\n\tuse training error)", "X", 1, "-X <number of folds>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('X', paramArrayOfString);
    if (str.length() != 0) {
      setNumFolds(Integer.parseInt(str));
    } else {
      setNumFolds(0);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    byte b = 0;
    arrayOfString2[b++] = "-X";
    arrayOfString2[b++] = "" + getNumFolds();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String classifiersTipText() {
    return "The classifiers to be chosen from.";
  }
  
  public void setClassifiers(Classifier[] paramArrayOfClassifier) {
    this.m_Classifiers = paramArrayOfClassifier;
  }
  
  public Classifier[] getClassifiers() {
    return this.m_Classifiers;
  }
  
  public Classifier getClassifier(int paramInt) {
    return this.m_Classifiers[paramInt];
  }
  
  protected String getClassifierSpec(int paramInt) {
    if (this.m_Classifiers.length < paramInt)
      return ""; 
    Classifier classifier = getClassifier(paramInt);
    return (classifier instanceof weka.core.OptionHandler) ? (classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions())) : classifier.getClass().getName();
  }
  
  public String seedTipText() {
    return "The seed used for randomizing the data for cross-validation.";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public String numFoldsTipText() {
    return "The number of folds used for cross-validation (if 0, performance on training data will be used).";
  }
  
  public int getNumFolds() {
    return this.m_NumXValFolds;
  }
  
  public void setNumFolds(int paramInt) {
    this.m_NumXValFolds = paramInt;
  }
  
  public String debugTipText() {
    return "Whether debug information is output to console.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_Classifiers.length == 0)
      throw new Exception("No base classifiers have been set!"); 
    Instances instances1 = new Instances(paramInstances);
    instances1.deleteWithMissingClass();
    Random random = new Random(this.m_Seed);
    instances1.randomize(random);
    if (instances1.classAttribute().isNominal() && this.m_NumXValFolds > 1)
      instances1.stratify(this.m_NumXValFolds); 
    Instances instances2 = instances1;
    Instances instances3 = instances1;
    Classifier classifier = null;
    byte b = -1;
    double d = Double.NaN;
    int i = this.m_Classifiers.length;
    for (byte b1 = 0; b1 < i; b1++) {
      Evaluation evaluation;
      Classifier classifier1 = getClassifier(b1);
      if (this.m_NumXValFolds > 1) {
        evaluation = new Evaluation(instances1);
        for (byte b2 = 0; b2 < this.m_NumXValFolds; b2++) {
          instances2 = instances1.trainCV(this.m_NumXValFolds, b2, new Random(1L));
          instances3 = instances1.testCV(this.m_NumXValFolds, b2);
          classifier1.buildClassifier(instances2);
          evaluation.setPriors(instances2);
          evaluation.evaluateModel(classifier1, instances3);
        } 
      } else {
        classifier1.buildClassifier(instances2);
        evaluation = new Evaluation(instances2);
        evaluation.evaluateModel(classifier1, instances3);
      } 
      double d1 = evaluation.errorRate();
      if (this.m_Debug)
        System.err.println("Error rate: " + Utils.doubleToString(d1, 6, 4) + " for classifier " + classifier1.getClass().getName()); 
      if (b1 == 0 || d1 < d) {
        classifier = classifier1;
        d = d1;
        b = b1;
      } 
    } 
    this.m_ClassifierIndex = b;
    if (this.m_NumXValFolds > 1)
      classifier.buildClassifier(instances1); 
    this.m_Classifier = classifier;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_Classifier.distributionForInstance(paramInstance);
  }
  
  public String toString() {
    if (this.m_Classifier == null)
      return "MultiScheme: No model built yet."; 
    null = "MultiScheme selection using";
    if (this.m_NumXValFolds > 1) {
      null = null + " cross validation error";
    } else {
      null = null + " error on training data";
    } 
    null = null + " from the following:\n";
    for (byte b = 0; b < this.m_Classifiers.length; b++)
      null = null + '\t' + getClassifierSpec(b) + '\n'; 
    return null + "Selected scheme: " + getClassifierSpec(this.m_ClassifierIndex) + "\n\n" + this.m_Classifier.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new MultiScheme(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\MultiScheme.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */