package weka.attributeSelection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ClassifierSubsetEval extends HoldOutSubsetEvaluator implements OptionHandler, ErrorBasedMeritEvaluator {
  private Instances m_trainingInstances;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private Classifier m_Classifier = (Classifier)new ZeroR();
  
  private Evaluation m_Evaluation;
  
  private File m_holdOutFile = new File("Click to set hold out or test instances");
  
  private Instances m_holdOutInstances = null;
  
  private boolean m_useTraining = true;
  
  public String globalInfo() {
    return "Evaluates attribute subsets on training data or a seperate hold out testing set";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tclass name of the classifier to use for\n\taccuracy estimation. Place any\n\tclassifier options LAST on the\n\tcommand line following a \"--\".\n\teg. -C weka.classifiers.bayes.NaiveBayes ... -- -K", "B", 1, "-B <classifier>"));
    vector.addElement(new Option("\tUse the training data to estimate accuracy.", "T", 0, "-T"));
    vector.addElement(new Option("\tName of the hold out/test set to \n\testimate accuracy on.", "H", 1, "-H <filename>"));
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to scheme " + this.m_Classifier.getClass().getName() + ":"));
      Enumeration enumeration = this.m_Classifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('B', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A classifier must be specified with -B option"); 
    setClassifier(Classifier.forName(str, Utils.partitionOptions(paramArrayOfString)));
    str = Utils.getOption('H', paramArrayOfString);
    if (str.length() != 0)
      setHoldOutFile(new File(str)); 
    setUseTraining(Utils.getFlag('T', paramArrayOfString));
  }
  
  public String classifierTipText() {
    return "Classifier to use for estimating the accuracy of subsets";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public String holdOutFileTipText() {
    return "File containing hold out/test instances.";
  }
  
  public File getHoldOutFile() {
    return this.m_holdOutFile;
  }
  
  public void setHoldOutFile(File paramFile) {
    this.m_holdOutFile = paramFile;
  }
  
  public String useTrainingTipText() {
    return "Use training data instead of hold out/test instances.";
  }
  
  public boolean getUseTraining() {
    return this.m_useTraining;
  }
  
  public void setUseTraining(boolean paramBoolean) {
    this.m_useTraining = paramBoolean;
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[6 + arrayOfString1.length];
    int i = 0;
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-B";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    if (getUseTraining())
      arrayOfString2[i++] = "-T"; 
    arrayOfString2[i++] = "-H";
    arrayOfString2[i++] = getHoldOutFile().getPath();
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_trainingInstances = paramInstances;
    this.m_classIndex = this.m_trainingInstances.classIndex();
    this.m_numAttribs = this.m_trainingInstances.numAttributes();
    this.m_numInstances = this.m_trainingInstances.numInstances();
    if (!this.m_useTraining && !getHoldOutFile().getPath().startsWith("Click to set")) {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(getHoldOutFile().getPath()));
      this.m_holdOutInstances = new Instances(bufferedReader);
      this.m_holdOutInstances.setClassIndex(this.m_trainingInstances.classIndex());
      if (!this.m_trainingInstances.equalHeaders(this.m_holdOutInstances))
        throw new Exception("Hold out/test set is not compatable with training data."); 
    } 
  }
  
  public double evaluateSubset(BitSet paramBitSet) throws Exception {
    double d = 0.0D;
    byte b3 = 0;
    Instances instances1 = null;
    Instances instances2 = null;
    Remove remove = new Remove();
    remove.setInvertSelection(true);
    instances1 = new Instances(this.m_trainingInstances);
    if (!this.m_useTraining) {
      if (this.m_holdOutInstances == null)
        throw new Exception("Must specify a set of hold out/test instances with -H"); 
      instances2 = new Instances(this.m_holdOutInstances);
    } 
    byte b1;
    for (b1 = 0; b1 < this.m_numAttribs; b1++) {
      if (paramBitSet.get(b1))
        b3++; 
    } 
    int[] arrayOfInt = new int[b3 + 1];
    b1 = 0;
    byte b2 = 0;
    while (b1 < this.m_numAttribs) {
      if (paramBitSet.get(b1))
        arrayOfInt[b2++] = b1; 
      b1++;
    } 
    arrayOfInt[b2] = this.m_classIndex;
    remove.setAttributeIndicesArray(arrayOfInt);
    remove.setInputFormat(instances1);
    instances1 = Filter.useFilter(instances1, (Filter)remove);
    if (!this.m_useTraining)
      instances2 = Filter.useFilter(instances2, (Filter)remove); 
    this.m_Classifier.buildClassifier(instances1);
    this.m_Evaluation = new Evaluation(instances1);
    if (!this.m_useTraining) {
      this.m_Evaluation.evaluateModel(this.m_Classifier, instances2);
    } else {
      this.m_Evaluation.evaluateModel(this.m_Classifier, instances1);
    } 
    if (this.m_trainingInstances.classAttribute().isNominal()) {
      d = this.m_Evaluation.errorRate();
    } else {
      d = this.m_Evaluation.meanAbsoluteError();
    } 
    this.m_Evaluation = null;
    return -d;
  }
  
  public double evaluateSubset(BitSet paramBitSet, Instances paramInstances) throws Exception {
    double d;
    byte b3 = 0;
    Instances instances1 = null;
    Instances instances2 = null;
    if (!this.m_trainingInstances.equalHeaders(paramInstances))
      throw new Exception("evaluateSubset : Incompatable instance types."); 
    Remove remove = new Remove();
    remove.setInvertSelection(true);
    instances1 = new Instances(this.m_trainingInstances);
    instances2 = new Instances(paramInstances);
    byte b1;
    for (b1 = 0; b1 < this.m_numAttribs; b1++) {
      if (paramBitSet.get(b1))
        b3++; 
    } 
    int[] arrayOfInt = new int[b3 + 1];
    b1 = 0;
    byte b2 = 0;
    while (b1 < this.m_numAttribs) {
      if (paramBitSet.get(b1))
        arrayOfInt[b2++] = b1; 
      b1++;
    } 
    arrayOfInt[b2] = this.m_classIndex;
    remove.setAttributeIndicesArray(arrayOfInt);
    remove.setInputFormat(instances1);
    instances1 = Filter.useFilter(instances1, (Filter)remove);
    instances2 = Filter.useFilter(instances2, (Filter)remove);
    this.m_Classifier.buildClassifier(instances1);
    this.m_Evaluation = new Evaluation(instances1);
    this.m_Evaluation.evaluateModel(this.m_Classifier, instances2);
    if (this.m_trainingInstances.classAttribute().isNominal()) {
      d = this.m_Evaluation.errorRate();
    } else {
      d = this.m_Evaluation.meanAbsoluteError();
    } 
    this.m_Evaluation = null;
    return -d;
  }
  
  public double evaluateSubset(BitSet paramBitSet, Instance paramInstance, boolean paramBoolean) throws Exception {
    double d1;
    double d2;
    byte b3 = 0;
    Instances instances = null;
    Instance instance = null;
    if (!this.m_trainingInstances.equalHeaders(paramInstance.dataset()))
      throw new Exception("evaluateSubset : Incompatable instance types."); 
    Remove remove = new Remove();
    remove.setInvertSelection(true);
    instances = new Instances(this.m_trainingInstances);
    instance = (Instance)paramInstance.copy();
    byte b1;
    for (b1 = 0; b1 < this.m_numAttribs; b1++) {
      if (paramBitSet.get(b1))
        b3++; 
    } 
    int[] arrayOfInt = new int[b3 + 1];
    b1 = 0;
    byte b2 = 0;
    while (b1 < this.m_numAttribs) {
      if (paramBitSet.get(b1))
        arrayOfInt[b2++] = b1; 
      b1++;
    } 
    arrayOfInt[b2] = this.m_classIndex;
    remove.setAttributeIndicesArray(arrayOfInt);
    remove.setInputFormat(instances);
    if (paramBoolean) {
      instances = Filter.useFilter(instances, (Filter)remove);
      this.m_Classifier.buildClassifier(instances);
    } 
    remove.input(instance);
    instance = remove.output();
    double[] arrayOfDouble = this.m_Classifier.distributionForInstance(instance);
    if (this.m_trainingInstances.classAttribute().isNominal()) {
      d2 = arrayOfDouble[(int)instance.classValue()];
    } else {
      d2 = arrayOfDouble[0];
    } 
    if (this.m_trainingInstances.classAttribute().isNominal()) {
      d1 = 1.0D - d2;
    } else {
      d1 = instance.classValue() - d2;
    } 
    return -d1;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainingInstances == null) {
      stringBuffer.append("\tClassifier subset evaluator has not been built yet\n");
    } else {
      stringBuffer.append("\tClassifier Subset Evaluator\n");
      stringBuffer.append("\tLearning scheme: " + getClassifier().getClass().getName() + "\n");
      stringBuffer.append("\tScheme options: ");
      String[] arrayOfString = new String[0];
      if (this.m_Classifier instanceof OptionHandler) {
        arrayOfString = this.m_Classifier.getOptions();
        for (byte b = 0; b < arrayOfString.length; b++)
          stringBuffer.append(arrayOfString[b] + " "); 
      } 
      stringBuffer.append("\n");
      stringBuffer.append("\tHold out/test set: ");
      if (!this.m_useTraining) {
        if (getHoldOutFile().getPath().startsWith("Click to set")) {
          stringBuffer.append("none\n");
        } else {
          stringBuffer.append(getHoldOutFile().getPath() + '\n');
        } 
      } else {
        stringBuffer.append("Training data\n");
      } 
      if (this.m_trainingInstances.attribute(this.m_classIndex).isNumeric()) {
        stringBuffer.append("\tAccuracy estimation: MAE\n");
      } else {
        stringBuffer.append("\tAccuracy estimation: classification error\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  protected void resetOptions() {
    this.m_trainingInstances = null;
    this.m_Evaluation = null;
    this.m_Classifier = (Classifier)new ZeroR();
    this.m_holdOutFile = new File("Click to set hold out or test instances");
    this.m_holdOutInstances = null;
    this.m_useTraining = false;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new ClassifierSubsetEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\ClassifierSubsetEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */