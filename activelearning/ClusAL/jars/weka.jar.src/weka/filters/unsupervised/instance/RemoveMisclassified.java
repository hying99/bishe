package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class RemoveMisclassified extends Filter implements UnsupervisedFilter, OptionHandler {
  protected Classifier m_cleansingClassifier = (Classifier)new ZeroR();
  
  protected int m_classIndex = -1;
  
  protected int m_numOfCrossValidationFolds = 0;
  
  protected int m_numOfCleansingIterations = 0;
  
  protected double m_numericClassifyThreshold = 0.1D;
  
  protected boolean m_invertMatching = false;
  
  protected boolean m_firstBatchFinished = false;
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    this.m_firstBatchFinished = false;
    return true;
  }
  
  private Instances cleanseTrain(Instances paramInstances) throws Exception {
    Instances instances1 = new Instances(paramInstances);
    Instances instances2 = new Instances(paramInstances, paramInstances.numInstances());
    Instances instances3 = new Instances(paramInstances, paramInstances.numInstances());
    int i = 0;
    byte b = 0;
    int j = this.m_classIndex;
    if (j < 0)
      j = paramInstances.classIndex(); 
    if (j < 0)
      j = paramInstances.numAttributes() - 1; 
    while (i != instances1.numInstances()) {
      if (this.m_numOfCleansingIterations > 0 && ++b > this.m_numOfCleansingIterations)
        break; 
      i = instances1.numInstances();
      instances1.setClassIndex(j);
      this.m_cleansingClassifier.buildClassifier(instances1);
      instances2 = new Instances(instances1, instances1.numInstances());
      for (byte b1 = 0; b1 < instances1.numInstances(); b1++) {
        Instance instance = instances1.instance(b1);
        double d = this.m_cleansingClassifier.classifyInstance(instance);
        if (instances1.classAttribute().isNumeric()) {
          if (d >= instance.classValue() - this.m_numericClassifyThreshold && d <= instance.classValue() + this.m_numericClassifyThreshold) {
            instances2.add(instance);
          } else if (this.m_invertMatching) {
            instances3.add(instance);
          } 
        } else if (d == instance.classValue()) {
          instances2.add(instance);
        } else if (this.m_invertMatching) {
          instances3.add(instance);
        } 
      } 
      instances1 = instances2;
    } 
    if (this.m_invertMatching) {
      instances3.setClassIndex(paramInstances.classIndex());
      return instances3;
    } 
    instances1.setClassIndex(paramInstances.classIndex());
    return instances1;
  }
  
  private Instances cleanseCross(Instances paramInstances) throws Exception {
    Instances instances1 = new Instances(paramInstances);
    Instances instances2 = new Instances(paramInstances, paramInstances.numInstances());
    Instances instances3 = new Instances(paramInstances, paramInstances.numInstances());
    int i = 0;
    byte b = 0;
    int j = this.m_classIndex;
    if (j < 0)
      j = paramInstances.classIndex(); 
    if (j < 0)
      j = paramInstances.numAttributes() - 1; 
    while (i != instances1.numInstances() && instances1.numInstances() >= this.m_numOfCrossValidationFolds) {
      i = instances1.numInstances();
      if (this.m_numOfCleansingIterations > 0 && ++b > this.m_numOfCleansingIterations)
        break; 
      instances1.setClassIndex(j);
      if (instances1.classAttribute().isNominal())
        instances1.stratify(this.m_numOfCrossValidationFolds); 
      instances2 = new Instances(instances1, instances1.numInstances());
      for (byte b1 = 0; b1 < this.m_numOfCrossValidationFolds; b1++) {
        Instances instances4 = instances1.trainCV(this.m_numOfCrossValidationFolds, b1);
        this.m_cleansingClassifier.buildClassifier(instances4);
        Instances instances5 = instances1.testCV(this.m_numOfCrossValidationFolds, b1);
        for (byte b2 = 0; b2 < instances5.numInstances(); b2++) {
          Instance instance = instances5.instance(b2);
          double d = this.m_cleansingClassifier.classifyInstance(instance);
          if (instances1.classAttribute().isNumeric()) {
            if (d >= instance.classValue() - this.m_numericClassifyThreshold && d <= instance.classValue() + this.m_numericClassifyThreshold) {
              instances2.add(instance);
            } else if (this.m_invertMatching) {
              instances3.add(instance);
            } 
          } else if (d == instance.classValue()) {
            instances2.add(instance);
          } else if (this.m_invertMatching) {
            instances3.add(instance);
          } 
        } 
      } 
      instances1 = instances2;
    } 
    if (this.m_invertMatching) {
      instances3.setClassIndex(paramInstances.classIndex());
      return instances3;
    } 
    instances1.setClassIndex(paramInstances.classIndex());
    return instances1;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (inputFormatPeek() == null)
      throw new NullPointerException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    if (this.m_firstBatchFinished) {
      push(paramInstance);
      return true;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (!this.m_firstBatchFinished) {
      Instances instances;
      if (this.m_numOfCrossValidationFolds < 2) {
        instances = cleanseTrain(getInputFormat());
      } else {
        instances = cleanseCross(getInputFormat());
      } 
      for (byte b = 0; b < instances.numInstances(); b++)
        push(instances.instance(b)); 
      this.m_firstBatchFinished = true;
      flushInput();
    } 
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tFull class name of classifier to use, followed\n\tby scheme options. (required)\n\teg: \"weka.classifiers.bayes.NaiveBayes -D\"", "W", 1, "-W <classifier specification>"));
    vector.addElement(new Option("\tAttribute on which misclassifications are based.\n\tIf < 0 will use any current set class or default to the last attribute.", "C", 1, "-C <class index>"));
    vector.addElement(new Option("\tThe number of folds to use for cross-validation cleansing.\n\t(<2 = no cross-validation - default).", "F", 1, "-F <number of folds>"));
    vector.addElement(new Option("\tThreshold for the max error when predicting numeric class.\n\t(Value should be >= 0, default = 0.1).", "T", 1, "-T <threshold>"));
    vector.addElement(new Option("\tThe maximum number of cleansing iterations to perform.\n\t(<1 = until fully cleansed - default)", "I", 1, "-I"));
    vector.addElement(new Option("\tInvert the match so that correctly classified instances are discarded.\n", "V", 0, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('W', paramArrayOfString);
    if (str1.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    String[] arrayOfString = Utils.splitOptions(str1);
    if (arrayOfString.length == 0)
      throw new Exception("Invalid classifier specification string"); 
    String str2 = arrayOfString[0];
    arrayOfString[0] = "";
    setClassifier(Classifier.forName(str2, arrayOfString));
    String str3 = Utils.getOption('C', paramArrayOfString);
    if (str3.length() != 0) {
      setClassIndex((new Double(str3)).intValue());
    } else {
      setClassIndex(-1);
    } 
    String str4 = Utils.getOption('F', paramArrayOfString);
    if (str4.length() != 0) {
      setNumFolds((new Double(str4)).intValue());
    } else {
      setNumFolds(0);
    } 
    String str5 = Utils.getOption('T', paramArrayOfString);
    if (str5.length() != 0) {
      setThreshold((new Double(str5)).doubleValue());
    } else {
      setThreshold(0.1D);
    } 
    String str6 = Utils.getOption('I', paramArrayOfString);
    if (str5.length() != 0) {
      setMaxIterations((new Double(str5)).intValue());
    } else {
      setMaxIterations(0);
    } 
    if (Utils.getFlag('V', paramArrayOfString)) {
      setInvert(true);
    } else {
      setInvert(false);
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[15];
    byte b = 0;
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + getClassifierSpec();
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getClassIndex();
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + getNumFolds();
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + getThreshold();
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + getMaxIterations();
    if (getInvert())
      arrayOfString[b++] = "-V"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "A filter that removes instances which are incorrectly classified. Useful for removing outliers.";
  }
  
  public String classifierTipText() {
    return "The classifier upon which to base the misclassifications.";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_cleansingClassifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_cleansingClassifier;
  }
  
  protected String getClassifierSpec() {
    Classifier classifier = getClassifier();
    return (classifier instanceof OptionHandler) ? (classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions())) : classifier.getClass().getName();
  }
  
  public String classIndexTipText() {
    return "Index of the class upon which to base the misclassifications. If < 0 will use any current set class or default to the last attribute.";
  }
  
  public void setClassIndex(int paramInt) {
    this.m_classIndex = paramInt;
  }
  
  public int getClassIndex() {
    return this.m_classIndex;
  }
  
  public String numFoldsTipText() {
    return "The number of cross-validation folds to use. If < 2 then no cross-validation will be performed.";
  }
  
  public void setNumFolds(int paramInt) {
    this.m_numOfCrossValidationFolds = paramInt;
  }
  
  public int getNumFolds() {
    return this.m_numOfCrossValidationFolds;
  }
  
  public String thresholdTipText() {
    return "Threshold for the max allowable error when predicting a numeric class. Should be >= 0.";
  }
  
  public void setThreshold(double paramDouble) {
    this.m_numericClassifyThreshold = paramDouble;
  }
  
  public double getThreshold() {
    return this.m_numericClassifyThreshold;
  }
  
  public String maxIterationsTipText() {
    return "The maximum number of iterations to perform. < 1 means filter will go until fully cleansed.";
  }
  
  public void setMaxIterations(int paramInt) {
    this.m_numOfCleansingIterations = paramInt;
  }
  
  public int getMaxIterations() {
    return this.m_numOfCleansingIterations;
  }
  
  public String invertTipText() {
    return "Whether or not to invert the selection. If true, correctly classified instances will be discarded.";
  }
  
  public void setInvert(boolean paramBoolean) {
    this.m_invertMatching = paramBoolean;
  }
  
  public boolean getInvert() {
    return this.m_invertMatching;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new RemoveMisclassified(), paramArrayOfString);
      } else {
        Filter.filterFile(new RemoveMisclassified(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\RemoveMisclassified.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */