package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.EvaluationUtils;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;

public class ThresholdSelector extends Classifier implements OptionHandler, Drawable {
  public static final int RANGE_NONE = 0;
  
  public static final int RANGE_BOUNDS = 1;
  
  public static final Tag[] TAGS_RANGE = new Tag[] { new Tag(0, "No range correction"), new Tag(1, "Correct based on min/max observed") };
  
  public static final int EVAL_TRAINING_SET = 2;
  
  public static final int EVAL_TUNED_SPLIT = 1;
  
  public static final int EVAL_CROSS_VALIDATION = 0;
  
  public static final Tag[] TAGS_EVAL = new Tag[] { new Tag(2, "Entire training set"), new Tag(1, "Single tuned fold"), new Tag(0, "N-Fold cross validation") };
  
  public static final int OPTIMIZE_0 = 0;
  
  public static final int OPTIMIZE_1 = 1;
  
  public static final int OPTIMIZE_LFREQ = 2;
  
  public static final int OPTIMIZE_MFREQ = 3;
  
  public static final int OPTIMIZE_POS_NAME = 4;
  
  public static final Tag[] TAGS_OPTIMIZE = new Tag[] { new Tag(0, "First class value"), new Tag(1, "Second class value"), new Tag(2, "Least frequent class value"), new Tag(3, "Most frequent class value"), new Tag(4, "Class value named: \"yes\", \"pos(itive)\",\"1\"") };
  
  protected Classifier m_Classifier = (Classifier)new Logistic();
  
  protected double m_HighThreshold = 1.0D;
  
  protected double m_LowThreshold = 0.0D;
  
  protected double m_BestThreshold = -1.7976931348623157E308D;
  
  protected double m_BestValue = -1.7976931348623157E308D;
  
  protected int m_NumXValFolds = 3;
  
  protected int m_Seed = 1;
  
  protected int m_DesignatedClass = 0;
  
  protected int m_ClassMode = 4;
  
  protected int m_EvalMode = 1;
  
  protected int m_RangeMode = 0;
  
  protected static final double MIN_VALUE = 0.05D;
  
  protected FastVector getPredictions(Instances paramInstances, int paramInt1, int paramInt2) throws Exception {
    Instances instances1;
    Instances instances2;
    Instances instances3;
    Random random;
    byte b;
    EvaluationUtils evaluationUtils = new EvaluationUtils();
    evaluationUtils.setSeed(this.m_Seed);
    switch (paramInt1) {
      case 1:
        instances1 = null;
        instances2 = null;
        instances3 = new Instances(paramInstances);
        random = new Random(this.m_Seed);
        instances3.randomize(random);
        instances3.stratify(paramInt2);
        for (b = 0; b < paramInt2; b++) {
          instances1 = instances3.trainCV(paramInt2, b, random);
          instances2 = instances3.testCV(paramInt2, b);
          if (checkForInstance(instances1) && checkForInstance(instances2))
            break; 
        } 
        return evaluationUtils.getTrainTestPredictions(this.m_Classifier, instances1, instances2);
      case 2:
        return evaluationUtils.getTrainTestPredictions(this.m_Classifier, paramInstances, paramInstances);
      case 0:
        return evaluationUtils.getCVPredictions(this.m_Classifier, paramInstances, paramInt2);
    } 
    throw new RuntimeException("Unrecognized evaluation mode");
  }
  
  protected void findThreshold(FastVector paramFastVector) {
    Instances instances = (new ThresholdCurve()).getCurve(paramFastVector, this.m_DesignatedClass);
    double d1 = 1.0D;
    double d2 = 0.0D;
    if (instances.numInstances() > 0) {
      Instance instance = instances.instance(0);
      int i = instances.attribute("FMeasure").index();
      int j = instances.attribute("Threshold").index();
      for (byte b = 1; b < instances.numInstances(); b++) {
        Instance instance1 = instances.instance(b);
        if (instance1.value(i) > instance.value(i))
          instance = instance1; 
        if (this.m_RangeMode == 1) {
          double d = instance1.value(j);
          if (d < d1)
            d1 = d; 
          if (d > d2)
            d2 = d; 
        } 
      } 
      if (instance.value(i) > 0.05D) {
        this.m_BestThreshold = instance.value(j);
        this.m_BestValue = instance.value(i);
      } 
      if (this.m_RangeMode == 1) {
        this.m_LowThreshold = d1;
        this.m_HighThreshold = d2;
      } 
    } 
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tThe class for which threshold is determined. Valid values are:\n\t1, 2 (for first and second classes, respectively), 3 (for whichever\n\tclass is least frequent), and 4 (for whichever class value is most\n\tfrequent), and 5 (for the first class named any of \"yes\",\"pos(itive)\"\n\t\"1\", or method 3 if no matches). (default 5).", "C", 1, "-C <integer>"));
    vector.addElement(new Option("\tFull name of classifier to perform parameter selection on.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <classifier class name>"));
    vector.addElement(new Option("\tNumber of folds used for cross validation. If just a\n\thold-out set is used, this determines the size of the hold-out set\n\t(default 3).", "X", 1, "-X <number of folds>"));
    vector.addElement(new Option("\tSets whether confidence range correction is applied. This\n\tcan be used to ensure the confidences range from 0 to 1.\n\tUse 0 for no range correction, 1 for correction based on\n\tthe min/max values seen during threshold selection\n\t(default 0).", "R", 1, "-R <integer>"));
    vector.addElement(new Option("\tSets the random number seed (default 1).", "S", 1, "-S <random number seed>"));
    vector.addElement(new Option("\tSets the evaluation mode. Use 0 for\n\tevaluation using cross-validation,\n\t1 for evaluation using hold-out set,\n\tand 2 for evaluation on the\n\ttraining data (default 1).", "E", 1, "-E <integer>"));
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to sub-classifier " + this.m_Classifier.getClass().getName() + ":\n(use -- to signal start of sub-classifier options)"));
      Enumeration enumeration = this.m_Classifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      setDesignatedClass(new SelectedTag(Integer.parseInt(str1) - 1, TAGS_OPTIMIZE));
    } else {
      setDesignatedClass(new SelectedTag(2, TAGS_OPTIMIZE));
    } 
    String str2 = Utils.getOption('E', paramArrayOfString);
    if (str2.length() != 0) {
      setEvaluationMode(new SelectedTag(Integer.parseInt(str2), TAGS_EVAL));
    } else {
      setEvaluationMode(new SelectedTag(1, TAGS_EVAL));
    } 
    String str3 = Utils.getOption('R', paramArrayOfString);
    if (str3.length() != 0) {
      setRangeCorrection(new SelectedTag(Integer.parseInt(str3) - 1, TAGS_RANGE));
    } else {
      setRangeCorrection(new SelectedTag(0, TAGS_RANGE));
    } 
    String str4 = Utils.getOption('X', paramArrayOfString);
    if (str4.length() != 0) {
      setNumXValFolds(Integer.parseInt(str4));
    } else {
      setNumXValFolds(3);
    } 
    String str5 = Utils.getOption('S', paramArrayOfString);
    if (str5.length() != 0) {
      setSeed(Integer.parseInt(str5));
    } else {
      setSeed(1);
    } 
    String str6 = Utils.getOption('W', paramArrayOfString);
    if (str6.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str6, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    int i = 0;
    String[] arrayOfString2 = new String[arrayOfString1.length + 13];
    arrayOfString2[i++] = "-C";
    arrayOfString2[i++] = "" + (this.m_DesignatedClass + 1);
    arrayOfString2[i++] = "-X";
    arrayOfString2[i++] = "" + getNumXValFolds();
    arrayOfString2[i++] = "-S";
    arrayOfString2[i++] = "" + getSeed();
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "-E";
    arrayOfString2[i++] = "" + this.m_EvalMode;
    arrayOfString2[i++] = "-R";
    arrayOfString2[i++] = "" + this.m_RangeMode;
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.numClasses() > 2)
      throw new UnsupportedClassTypeException("Only works for two-class datasets!"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("Class attribute must be nominal!"); 
    AttributeStats attributeStats = paramInstances.attributeStats(paramInstances.classIndex());
    this.m_BestThreshold = 0.5D;
    this.m_BestValue = 0.05D;
    this.m_HighThreshold = 1.0D;
    this.m_LowThreshold = 0.0D;
    if (attributeStats.distinctCount != 2) {
      System.err.println("Couldn't find examples of both classes. No adjustment.");
      this.m_Classifier.buildClassifier(paramInstances);
    } else {
      Attribute attribute;
      boolean bool;
      byte b;
      switch (this.m_ClassMode) {
        case 0:
          this.m_DesignatedClass = 0;
          break;
        case 1:
          this.m_DesignatedClass = 1;
          break;
        case 4:
          attribute = paramInstances.classAttribute();
          bool = false;
          for (b = 0; b < attribute.numValues() && !bool; b++) {
            String str = attribute.value(b).toLowerCase();
            if (str.startsWith("yes") || str.equals("1") || str.startsWith("pos")) {
              bool = true;
              this.m_DesignatedClass = b;
            } 
          } 
          if (bool)
            break; 
        case 2:
          this.m_DesignatedClass = (attributeStats.nominalCounts[0] > attributeStats.nominalCounts[1]) ? 1 : 0;
          break;
        case 3:
          this.m_DesignatedClass = (attributeStats.nominalCounts[0] > attributeStats.nominalCounts[1]) ? 0 : 1;
          break;
        default:
          throw new Exception("Unrecognized class value selection mode");
      } 
      if (attributeStats.nominalCounts[this.m_DesignatedClass] == 1) {
        System.err.println("Only 1 positive found: optimizing on training data");
        findThreshold(getPredictions(paramInstances, 2, 0));
      } else {
        int i = Math.min(this.m_NumXValFolds, attributeStats.nominalCounts[this.m_DesignatedClass]);
        findThreshold(getPredictions(paramInstances, this.m_EvalMode, i));
        if (this.m_EvalMode != 2)
          this.m_Classifier.buildClassifier(paramInstances); 
      } 
    } 
  }
  
  private boolean checkForInstance(Instances paramInstances) throws Exception {
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      if ((int)paramInstances.instance(b).classValue() == this.m_DesignatedClass)
        return true; 
    } 
    return false;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = this.m_Classifier.distributionForInstance(paramInstance);
    double d = arrayOfDouble[this.m_DesignatedClass];
    if (d > this.m_BestThreshold) {
      d = 0.5D + (d - this.m_BestThreshold) / (this.m_HighThreshold - this.m_BestThreshold) * 2.0D;
    } else {
      d = (d - this.m_LowThreshold) / (this.m_BestThreshold - this.m_LowThreshold) * 2.0D;
    } 
    if (d < 0.0D) {
      d = 0.0D;
    } else if (d > 1.0D) {
      d = 1.0D;
    } 
    arrayOfDouble[this.m_DesignatedClass] = d;
    if (arrayOfDouble.length == 2)
      arrayOfDouble[(this.m_DesignatedClass + 1) % 2] = 1.0D - d; 
    return arrayOfDouble;
  }
  
  public String globalInfo() {
    return "A metaclassifier that selecting a mid-point threshold on the probability output by a Classifier. The midpoint threshold is set so that a given performance measure is optimized. Currently this is the F-measure. Performance is measured either on the training data, a hold-out set or using cross-validation. In addition, the probabilities returned by the base learner can have their range expanded so that the output probabilities will reside between 0 and 1 (this is useful if the scheme normally produces probabilities in a very narrow range).";
  }
  
  public String designatedClassTipText() {
    return "Sets the class value for which the optimization is performed. The options are: pick the first class value; pick the second class value; pick whichever class is least frequent; pick whichever class value is most frequent; pick the first class named any of \"yes\",\"pos(itive)\", \"1\", or the least frequent if no matches).";
  }
  
  public SelectedTag getDesignatedClass() {
    return new SelectedTag(this.m_ClassMode, TAGS_OPTIMIZE);
  }
  
  public void setDesignatedClass(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_OPTIMIZE)
      this.m_ClassMode = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String evaluationModeTipText() {
    return "Sets the method used to determine the threshold/performance curve. The options are: perform optimization based on the entire training set (may result in overfitting); perform an n-fold cross-validation (may be time consuming); perform one fold of an n-fold cross-validation (faster but likely less accurate).";
  }
  
  public void setEvaluationMode(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_EVAL)
      this.m_EvalMode = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getEvaluationMode() {
    return new SelectedTag(this.m_EvalMode, TAGS_EVAL);
  }
  
  public String rangeCorrectionTipText() {
    return "Sets the type of prediction range correction performed. The options are: do not do any range correction; expand predicted probabilities so that the minimum probability observed during the optimization maps to 0, and the maximum maps to 1 (values outside this range are clipped to 0 and 1).";
  }
  
  public void setRangeCorrection(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_RANGE)
      this.m_RangeMode = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getRangeCorrection() {
    return new SelectedTag(this.m_RangeMode, TAGS_RANGE);
  }
  
  public String seedTipText() {
    return "Sets the seed used for randomization. This is used when randomizing the data during optimization.";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public String numXValFoldsTipText() {
    return "Sets the number of folds used during full cross-validation and tuned fold evaluation. This number will be automatically reduced if there are insufficient positive examples.";
  }
  
  public int getNumXValFolds() {
    return this.m_NumXValFolds;
  }
  
  public void setNumXValFolds(int paramInt) {
    if (paramInt < 2)
      throw new IllegalArgumentException("Number of folds must be greater than 1"); 
    this.m_NumXValFolds = paramInt;
  }
  
  public String classifierTipText() {
    return "Sets the base Classifier to which the optimization will be made.";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  protected String getClassifierSpec() {
    Classifier classifier = getClassifier();
    return (classifier instanceof OptionHandler) ? (classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions())) : classifier.getClass().getName();
  }
  
  public int graphType() {
    return (this.m_Classifier instanceof Drawable) ? ((Drawable)this.m_Classifier).graphType() : 0;
  }
  
  public String graph() throws Exception {
    if (this.m_Classifier instanceof Drawable)
      return ((Drawable)this.m_Classifier).graph(); 
    throw new Exception("Classifier: " + getClassifierSpec() + " cannot be graphed");
  }
  
  public String toString() {
    if (this.m_BestValue == -1.7976931348623157E308D)
      return "ThresholdSelector: No model built yet."; 
    null = "Threshold Selector.\nClassifier: " + this.m_Classifier.getClass().getName() + "\n";
    null = null + "Index of designated class: " + this.m_DesignatedClass + "\n";
    null = null + "Evaluation mode: ";
    switch (this.m_EvalMode) {
      case 0:
        null = null + this.m_NumXValFolds + "-fold cross-validation";
        break;
      case 1:
        null = null + "tuning on 1/" + this.m_NumXValFolds + " of the data";
        break;
      default:
        null = null + "tuning on the training data";
        break;
    } 
    null = null + "\n";
    null = null + "Threshold: " + this.m_BestThreshold + "\n";
    null = null + "Best value: " + this.m_BestValue + "\n";
    if (this.m_RangeMode == 1)
      null = null + "Expanding range [" + this.m_LowThreshold + "," + this.m_HighThreshold + "] to [0, 1]\n"; 
    return null + this.m_Classifier.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new ThresholdSelector(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\ThresholdSelector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */