package weka.classifiers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.Summarizable;
import weka.core.Utils;
import weka.estimators.Estimator;
import weka.estimators.KernelEstimator;

public class Evaluation implements Summarizable {
  protected int m_NumClasses;
  
  protected int m_NumFolds;
  
  protected double m_Incorrect;
  
  protected double m_Correct;
  
  protected double m_Unclassified;
  
  protected double m_MissingClass;
  
  protected double m_WithClass;
  
  protected double[][] m_ConfusionMatrix;
  
  protected String[] m_ClassNames;
  
  protected boolean m_ClassIsNominal;
  
  protected double[] m_ClassPriors;
  
  protected double m_ClassPriorsSum;
  
  protected CostMatrix m_CostMatrix;
  
  protected double m_TotalCost;
  
  protected double m_SumErr;
  
  protected double m_SumAbsErr;
  
  protected double m_SumSqrErr;
  
  protected double m_SumClass;
  
  protected double m_SumSqrClass;
  
  protected double m_SumPredicted;
  
  protected double m_SumSqrPredicted;
  
  protected double m_SumClassPredicted;
  
  protected double m_SumPriorAbsErr;
  
  protected double m_SumPriorSqrErr;
  
  protected double m_SumKBInfo;
  
  protected static int k_MarginResolution = 500;
  
  protected double[] m_MarginCounts;
  
  protected int m_NumTrainClassVals;
  
  protected double[] m_TrainClassVals;
  
  protected double[] m_TrainClassWeights;
  
  protected Estimator m_PriorErrorEstimator;
  
  protected Estimator m_ErrorEstimator;
  
  protected static final double MIN_SF_PROB = 4.9E-324D;
  
  protected double m_SumPriorEntropy;
  
  protected double m_SumSchemeEntropy;
  
  public Evaluation(Instances paramInstances) throws Exception {
    this(paramInstances, null);
  }
  
  public Evaluation(Instances paramInstances, CostMatrix paramCostMatrix) throws Exception {
    this.m_NumClasses = paramInstances.numClasses();
    this.m_NumFolds = 1;
    this.m_ClassIsNominal = paramInstances.classAttribute().isNominal();
    if (this.m_ClassIsNominal) {
      this.m_ConfusionMatrix = new double[this.m_NumClasses][this.m_NumClasses];
      this.m_ClassNames = new String[this.m_NumClasses];
      for (byte b = 0; b < this.m_NumClasses; b++)
        this.m_ClassNames[b] = paramInstances.classAttribute().value(b); 
    } 
    this.m_CostMatrix = paramCostMatrix;
    if (this.m_CostMatrix != null) {
      if (!this.m_ClassIsNominal)
        throw new Exception("Class has to be nominal if cost matrix given!"); 
      if (this.m_CostMatrix.size() != this.m_NumClasses)
        throw new Exception("Cost matrix not compatible with data!"); 
    } 
    this.m_ClassPriors = new double[this.m_NumClasses];
    setPriors(paramInstances);
    this.m_MarginCounts = new double[k_MarginResolution + 1];
  }
  
  public double[][] confusionMatrix() {
    double[][] arrayOfDouble = new double[this.m_ConfusionMatrix.length][0];
    for (byte b = 0; b < this.m_ConfusionMatrix.length; b++) {
      arrayOfDouble[b] = new double[(this.m_ConfusionMatrix[b]).length];
      System.arraycopy(this.m_ConfusionMatrix[b], 0, arrayOfDouble[b], 0, (this.m_ConfusionMatrix[b]).length);
    } 
    return arrayOfDouble;
  }
  
  public void crossValidateModel(Classifier paramClassifier, Instances paramInstances, int paramInt, Random paramRandom) throws Exception {
    paramInstances = new Instances(paramInstances);
    paramInstances.randomize(paramRandom);
    if (paramInstances.classAttribute().isNominal())
      paramInstances.stratify(paramInt); 
    for (byte b = 0; b < paramInt; b++) {
      Instances instances1 = paramInstances.trainCV(paramInt, b, paramRandom);
      setPriors(instances1);
      Classifier classifier = Classifier.makeCopy(paramClassifier);
      classifier.buildClassifier(instances1);
      Instances instances2 = paramInstances.testCV(paramInt, b);
      evaluateModel(classifier, instances2);
    } 
    this.m_NumFolds = paramInt;
  }
  
  public void crossValidateModel(String paramString, Instances paramInstances, int paramInt, String[] paramArrayOfString, Random paramRandom) throws Exception {
    crossValidateModel(Classifier.forName(paramString, paramArrayOfString), paramInstances, paramInt, paramRandom);
  }
  
  public static String evaluateModel(String paramString, String[] paramArrayOfString) throws Exception {
    Classifier classifier;
    try {
      classifier = (Classifier)Class.forName(paramString).newInstance();
    } catch (Exception exception) {
      throw new Exception("Can't find class with name " + paramString + '.');
    } 
    return evaluateModel(classifier, paramArrayOfString);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("The first argument must be the class name of a classifier"); 
      String str = paramArrayOfString[0];
      paramArrayOfString[0] = "";
      System.out.println(evaluateModel(str, paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  public static String evaluateModel(Classifier paramClassifier, String[] paramArrayOfString) throws Exception {
    String str1;
    String str2;
    String str3;
    String str4;
    String str5;
    Instances instances1 = null;
    Instances instances2 = null;
    Instances instances3 = null;
    int i = 1;
    int j = 10;
    int k = -1;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = true;
    boolean bool5 = false;
    boolean bool6 = false;
    boolean bool7 = false;
    boolean bool8 = false;
    boolean bool9 = false;
    StringBuffer stringBuffer1 = new StringBuffer();
    BufferedReader bufferedReader1 = null;
    BufferedReader bufferedReader2 = null;
    ObjectInputStream objectInputStream = null;
    CostMatrix costMatrix = null;
    StringBuffer stringBuffer2 = null;
    Range range = null;
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    try {
      String str9;
      String str6 = Utils.getOption('c', paramArrayOfString);
      if (str6.length() != 0)
        k = Integer.parseInt(str6); 
      str1 = Utils.getOption('t', paramArrayOfString);
      str4 = Utils.getOption('l', paramArrayOfString);
      str5 = Utils.getOption('d', paramArrayOfString);
      str2 = Utils.getOption('T', paramArrayOfString);
      if (str1.length() == 0) {
        if (str4.length() == 0)
          throw new Exception("No training file and no object input file given."); 
        if (str2.length() == 0)
          throw new Exception("No training file and no test file given."); 
      } else if (str4.length() != 0 && (!(paramClassifier instanceof UpdateableClassifier) || str2.length() == 0)) {
        throw new Exception("Classifier not incremental, or no test file provided: can't use both train and model file.");
      } 
      try {
        if (str1.length() != 0)
          bufferedReader1 = new BufferedReader(new FileReader(str1)); 
        if (str2.length() != 0)
          bufferedReader2 = new BufferedReader(new FileReader(str2)); 
        if (str4.length() != 0) {
          GZIPInputStream gZIPInputStream;
          FileInputStream fileInputStream = new FileInputStream(str4);
          if (str4.endsWith(".gz"))
            gZIPInputStream = new GZIPInputStream(fileInputStream); 
          objectInputStream = new ObjectInputStream(gZIPInputStream);
        } 
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + '.');
      } 
      if (str2.length() != 0) {
        instances3 = instances2 = new Instances(bufferedReader2, 1);
        if (k != -1) {
          instances2.setClassIndex(k - 1);
        } else {
          instances2.setClassIndex(instances2.numAttributes() - 1);
        } 
        if (k > instances2.numAttributes())
          throw new Exception("Index of class attribute too large."); 
      } 
      if (str1.length() != 0) {
        if (paramClassifier instanceof UpdateableClassifier && str2.length() != 0) {
          instances1 = new Instances(bufferedReader1, 1);
        } else {
          instances1 = new Instances(bufferedReader1);
        } 
        instances3 = instances1;
        if (k != -1) {
          instances1.setClassIndex(k - 1);
        } else {
          instances1.setClassIndex(instances1.numAttributes() - 1);
        } 
        if (str2.length() != 0 && !instances2.equalHeaders(instances1))
          throw new IllegalArgumentException("Train and test file not compatible!"); 
        if (k > instances1.numAttributes())
          throw new Exception("Index of class attribute too large."); 
      } 
      if (instances3 == null)
        throw new Exception("No actual dataset provided to use as template"); 
      String str7 = Utils.getOption('s', paramArrayOfString);
      if (str7.length() != 0)
        i = Integer.parseInt(str7); 
      String str8 = Utils.getOption('x', paramArrayOfString);
      if (str8.length() != 0)
        j = Integer.parseInt(str8); 
      costMatrix = handleCostOption(Utils.getOption('m', paramArrayOfString), instances3.numClasses());
      bool8 = Utils.getFlag('i', paramArrayOfString);
      bool2 = Utils.getFlag('o', paramArrayOfString);
      bool4 = !Utils.getFlag('v', paramArrayOfString) ? true : false;
      bool6 = Utils.getFlag('k', paramArrayOfString);
      bool5 = Utils.getFlag('r', paramArrayOfString);
      bool7 = Utils.getFlag('g', paramArrayOfString);
      str3 = Utils.getOption('z', paramArrayOfString);
      bool9 = (str3.length() != 0) ? true : false;
      try {
        str9 = Utils.getOption('p', paramArrayOfString);
      } catch (Exception exception) {
        throw new Exception(exception.getMessage() + "\nNOTE: the -p option has changed. " + "It now expects a parameter specifying a range of attributes " + "to list with the predictions. Use '-p 0' for none.");
      } 
      if (str9.length() != 0) {
        bool3 = true;
        if (!str9.equals("0"))
          range = new Range(str9); 
      } 
      if (str4.length() != 0) {
        Utils.checkForRemainingOptions(paramArrayOfString);
      } else if (paramClassifier instanceof weka.core.OptionHandler) {
        for (byte b = 0; b < paramArrayOfString.length; b++) {
          if (paramArrayOfString[b].length() != 0) {
            if (stringBuffer2 == null)
              stringBuffer2 = new StringBuffer(); 
            if (paramArrayOfString[b].indexOf(' ') != -1) {
              stringBuffer2.append('"' + paramArrayOfString[b] + "\" ");
            } else {
              stringBuffer2.append(paramArrayOfString[b] + " ");
            } 
          } 
        } 
        paramClassifier.setOptions(paramArrayOfString);
      } 
      Utils.checkForRemainingOptions(paramArrayOfString);
    } catch (Exception exception) {
      throw new Exception("\nWeka exception: " + exception.getMessage() + makeOptionString(paramClassifier));
    } 
    Evaluation evaluation1 = new Evaluation(new Instances(instances3, 0), costMatrix);
    Evaluation evaluation2 = new Evaluation(new Instances(instances3, 0), costMatrix);
    if (str4.length() != 0) {
      paramClassifier = (Classifier)objectInputStream.readObject();
      objectInputStream.close();
    } 
    if (paramClassifier instanceof UpdateableClassifier && str2.length() != 0 && costMatrix == null && str1.length() != 0) {
      evaluation1.setPriors(instances1);
      evaluation2.setPriors(instances1);
      l1 = System.currentTimeMillis();
      if (str4.length() == 0)
        paramClassifier.buildClassifier(instances1); 
      while (instances1.readInstance(bufferedReader1)) {
        evaluation1.updatePriors(instances1.instance(0));
        evaluation2.updatePriors(instances1.instance(0));
        ((UpdateableClassifier)paramClassifier).updateClassifier(instances1.instance(0));
        instances1.delete(0);
      } 
      l2 = System.currentTimeMillis() - l1;
      bufferedReader1.close();
    } else if (str4.length() == 0) {
      Instances instances = new Instances(instances1);
      evaluation1.setPriors(instances);
      evaluation2.setPriors(instances);
      l1 = System.currentTimeMillis();
      paramClassifier.buildClassifier(instances);
      l2 = System.currentTimeMillis() - l1;
    } 
    if (str5.length() != 0) {
      GZIPOutputStream gZIPOutputStream;
      FileOutputStream fileOutputStream = new FileOutputStream(str5);
      if (str5.endsWith(".gz"))
        gZIPOutputStream = new GZIPOutputStream(fileOutputStream); 
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(gZIPOutputStream);
      objectOutputStream.writeObject(paramClassifier);
      objectOutputStream.flush();
      objectOutputStream.close();
    } 
    if (paramClassifier instanceof Drawable && bool7)
      return ((Drawable)paramClassifier).graph(); 
    if (paramClassifier instanceof Sourcable && bool9)
      return wekaStaticWrapper((Sourcable)paramClassifier, str3); 
    if (bool3)
      return printClassifications(paramClassifier, new Instances(instances3, 0), str2, k, range); 
    if (!bool2 && !bool5) {
      if (paramClassifier instanceof weka.core.OptionHandler && stringBuffer2 != null) {
        stringBuffer1.append("\nOptions: " + stringBuffer2);
        stringBuffer1.append("\n");
      } 
      stringBuffer1.append("\n" + paramClassifier.toString() + "\n");
    } 
    if (!bool5 && costMatrix != null)
      stringBuffer1.append("\n=== Evaluation Cost Matrix ===\n\n").append(costMatrix.toString()); 
    if (bool4 && str1.length() != 0) {
      if (paramClassifier instanceof UpdateableClassifier && str2.length() != 0 && costMatrix == null) {
        bufferedReader1 = new BufferedReader(new FileReader(str1));
        instances1 = new Instances(bufferedReader1, 1);
        if (k != -1) {
          instances1.setClassIndex(k - 1);
        } else {
          instances1.setClassIndex(instances1.numAttributes() - 1);
        } 
        l3 = System.currentTimeMillis();
        while (instances1.readInstance(bufferedReader1)) {
          evaluation1.evaluateModelOnce(paramClassifier, instances1.instance(0));
          instances1.delete(0);
        } 
        l4 = System.currentTimeMillis() - l3;
        bufferedReader1.close();
      } else {
        l3 = System.currentTimeMillis();
        evaluation1.evaluateModel(paramClassifier, instances1);
        l4 = System.currentTimeMillis() - l3;
      } 
      if (bool5)
        return evaluation1.toCumulativeMarginDistributionString(); 
      stringBuffer1.append("\nTime taken to build model: " + Utils.doubleToString(l2 / 1000.0D, 2) + " seconds");
      stringBuffer1.append("\nTime taken to test model on training data: " + Utils.doubleToString(l4 / 1000.0D, 2) + " seconds");
      stringBuffer1.append(evaluation1.toSummaryString("\n\n=== Error on training data ===\n", bool6));
      if (instances3.classAttribute().isNominal()) {
        if (bool8)
          stringBuffer1.append("\n\n" + evaluation1.toClassDetailsString()); 
        stringBuffer1.append("\n\n" + evaluation1.toMatrixString());
      } 
    } 
    if (str2.length() != 0) {
      while (instances2.readInstance(bufferedReader2)) {
        evaluation2.evaluateModelOnce(paramClassifier, instances2.instance(0));
        instances2.delete(0);
      } 
      bufferedReader2.close();
      stringBuffer1.append("\n\n" + evaluation2.toSummaryString("=== Error on test data ===\n", bool6));
    } else if (str1.length() != 0) {
      Random random = new Random(i);
      evaluation2.crossValidateModel(paramClassifier, instances1, j, random);
      if (instances3.classAttribute().isNumeric()) {
        stringBuffer1.append("\n\n\n" + evaluation2.toSummaryString("=== Cross-validation ===\n", bool6));
      } else {
        stringBuffer1.append("\n\n\n" + evaluation2.toSummaryString("=== Stratified cross-validation ===\n", bool6));
      } 
    } 
    if (instances3.classAttribute().isNominal()) {
      if (bool8)
        stringBuffer1.append("\n\n" + evaluation2.toClassDetailsString()); 
      stringBuffer1.append("\n\n" + evaluation2.toMatrixString());
    } 
    return stringBuffer1.toString();
  }
  
  protected static CostMatrix handleCostOption(String paramString, int paramInt) throws Exception {
    if (paramString != null && paramString.length() != 0) {
      System.out.println("NOTE: The behaviour of the -m option has changed between WEKA 3.0 and WEKA 3.1. -m now carries out cost-sensitive *evaluation* only. For cost-sensitive *prediction*, use one of the cost-sensitive metaschemes such as weka.classifiers.meta.CostSensitiveClassifier or weka.classifiers.meta.MetaCost");
      BufferedReader bufferedReader = null;
      try {
        bufferedReader = new BufferedReader(new FileReader(paramString));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + '.');
      } 
      try {
        return new CostMatrix(bufferedReader);
      } catch (Exception exception) {
        try {
          try {
            bufferedReader.close();
            bufferedReader = new BufferedReader(new FileReader(paramString));
          } catch (Exception exception1) {
            throw new Exception("Can't open file " + exception1.getMessage() + '.');
          } 
          CostMatrix costMatrix = new CostMatrix(paramInt);
          costMatrix.readOldFormat(bufferedReader);
          return costMatrix;
        } catch (Exception exception1) {
          throw exception;
        } 
      } 
    } 
    return null;
  }
  
  public double[] evaluateModel(Classifier paramClassifier, Instances paramInstances) throws Exception {
    double[] arrayOfDouble = new double[paramInstances.numInstances()];
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      arrayOfDouble[b] = evaluateModelOnce(paramClassifier, paramInstances.instance(b)); 
    return arrayOfDouble;
  }
  
  public double evaluateModelOnce(Classifier paramClassifier, Instance paramInstance) throws Exception {
    Instance instance = (Instance)paramInstance.copy();
    double d = 0.0D;
    instance.setDataset(paramInstance.dataset());
    instance.setClassMissing();
    if (this.m_ClassIsNominal) {
      double[] arrayOfDouble = paramClassifier.distributionForInstance(instance);
      d = Utils.maxIndex(arrayOfDouble);
      if (arrayOfDouble[(int)d] <= 0.0D)
        d = Instance.missingValue(); 
      updateStatsForClassifier(arrayOfDouble, paramInstance);
    } else {
      d = paramClassifier.classifyInstance(instance);
      updateStatsForPredictor(d, paramInstance);
    } 
    return d;
  }
  
  public double evaluateModelOnce(double[] paramArrayOfdouble, Instance paramInstance) throws Exception {
    double d;
    if (this.m_ClassIsNominal) {
      d = Utils.maxIndex(paramArrayOfdouble);
      if (paramArrayOfdouble[(int)d] <= 0.0D)
        d = Instance.missingValue(); 
      updateStatsForClassifier(paramArrayOfdouble, paramInstance);
    } else {
      d = paramArrayOfdouble[0];
      updateStatsForPredictor(d, paramInstance);
    } 
    return d;
  }
  
  public void evaluateModelOnce(double paramDouble, Instance paramInstance) throws Exception {
    if (this.m_ClassIsNominal) {
      updateStatsForClassifier(makeDistribution(paramDouble), paramInstance);
    } else {
      updateStatsForPredictor(paramDouble, paramInstance);
    } 
  }
  
  protected static String wekaStaticWrapper(Sourcable paramSourcable, String paramString) throws Exception {
    String str = paramSourcable.toSource(paramString);
    return "package weka.classifiers;\nimport weka.core.Attribute;\nimport weka.core.Instance;\nimport weka.core.Instances;\nimport weka.classifiers.Classifier;\n\npublic class WekaWrapper extends Classifier {\n\n  public void buildClassifier(Instances i) throws Exception {\n  }\n\n  public double classifyInstance(Instance i) throws Exception {\n\n    Object [] s = new Object [i.numAttributes()];\n    for (int j = 0; j < s.length; j++) {\n      if (!i.isMissing(j)) {\n        if (i.attribute(j).type() == Attribute.NOMINAL) {\n          s[j] = i.attribute(j).value((int) i.value(j));\n        } else if (i.attribute(j).type() == Attribute.NUMERIC) {\n          s[j] = new Double(i.value(j));\n        }\n      }\n    }\n    return " + paramString + ".classify(s);\n" + "  }\n\n" + "}\n\n" + str;
  }
  
  public final double numInstances() {
    return this.m_WithClass;
  }
  
  public final double incorrect() {
    return this.m_Incorrect;
  }
  
  public final double pctIncorrect() {
    return 100.0D * this.m_Incorrect / this.m_WithClass;
  }
  
  public final double totalCost() {
    return this.m_TotalCost;
  }
  
  public final double avgCost() {
    return this.m_TotalCost / this.m_WithClass;
  }
  
  public final double correct() {
    return this.m_Correct;
  }
  
  public final double pctCorrect() {
    return 100.0D * this.m_Correct / this.m_WithClass;
  }
  
  public final double unclassified() {
    return this.m_Unclassified;
  }
  
  public final double pctUnclassified() {
    return 100.0D * this.m_Unclassified / this.m_WithClass;
  }
  
  public final double errorRate() {
    return !this.m_ClassIsNominal ? Math.sqrt(this.m_SumSqrErr / this.m_WithClass) : ((this.m_CostMatrix == null) ? (this.m_Incorrect / this.m_WithClass) : avgCost());
  }
  
  public final double kappa() {
    double[] arrayOfDouble1 = new double[this.m_ConfusionMatrix.length];
    double[] arrayOfDouble2 = new double[this.m_ConfusionMatrix.length];
    double d1 = 0.0D;
    for (byte b1 = 0; b1 < this.m_ConfusionMatrix.length; b1++) {
      for (byte b = 0; b < this.m_ConfusionMatrix.length; b++) {
        arrayOfDouble1[b1] = arrayOfDouble1[b1] + this.m_ConfusionMatrix[b1][b];
        arrayOfDouble2[b] = arrayOfDouble2[b] + this.m_ConfusionMatrix[b1][b];
        d1 += this.m_ConfusionMatrix[b1][b];
      } 
    } 
    double d2 = 0.0D;
    double d3 = 0.0D;
    for (byte b2 = 0; b2 < this.m_ConfusionMatrix.length; b2++) {
      d3 += arrayOfDouble1[b2] * arrayOfDouble2[b2];
      d2 += this.m_ConfusionMatrix[b2][b2];
    } 
    d3 /= d1 * d1;
    d2 /= d1;
    return (d3 < 1.0D) ? ((d2 - d3) / (1.0D - d3)) : 1.0D;
  }
  
  public final double correlationCoefficient() throws Exception {
    if (this.m_ClassIsNominal)
      throw new Exception("Can't compute correlation coefficient: class is nominal!"); 
    double d1 = 0.0D;
    double d2 = this.m_SumSqrClass - this.m_SumClass * this.m_SumClass / this.m_WithClass;
    double d3 = this.m_SumSqrPredicted - this.m_SumPredicted * this.m_SumPredicted / this.m_WithClass;
    double d4 = this.m_SumClassPredicted - this.m_SumClass * this.m_SumPredicted / this.m_WithClass;
    if (Utils.smOrEq(d2 * d3, 0.0D)) {
      d1 = 0.0D;
    } else {
      d1 = d4 / Math.sqrt(d2 * d3);
    } 
    return d1;
  }
  
  public final double meanAbsoluteError() {
    return this.m_SumAbsErr / this.m_WithClass;
  }
  
  public final double meanPriorAbsoluteError() {
    return this.m_SumPriorAbsErr / this.m_WithClass;
  }
  
  public final double relativeAbsoluteError() throws Exception {
    return 100.0D * meanAbsoluteError() / meanPriorAbsoluteError();
  }
  
  public final double rootMeanSquaredError() {
    return Math.sqrt(this.m_SumSqrErr / this.m_WithClass);
  }
  
  public final double rootMeanPriorSquaredError() {
    return Math.sqrt(this.m_SumPriorSqrErr / this.m_WithClass);
  }
  
  public final double rootRelativeSquaredError() {
    return 100.0D * rootMeanSquaredError() / rootMeanPriorSquaredError();
  }
  
  public final double priorEntropy() throws Exception {
    if (!this.m_ClassIsNominal)
      throw new Exception("Can't compute entropy of class prior: class numeric!"); 
    double d = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++)
      d -= this.m_ClassPriors[b] / this.m_ClassPriorsSum * Utils.log2(this.m_ClassPriors[b] / this.m_ClassPriorsSum); 
    return d;
  }
  
  public final double KBInformation() throws Exception {
    if (!this.m_ClassIsNominal)
      throw new Exception("Can't compute K&B Info score: class numeric!"); 
    return this.m_SumKBInfo;
  }
  
  public final double KBMeanInformation() throws Exception {
    if (!this.m_ClassIsNominal)
      throw new Exception("Can't compute K&B Info score: class numeric!"); 
    return this.m_SumKBInfo / this.m_WithClass;
  }
  
  public final double KBRelativeInformation() throws Exception {
    if (!this.m_ClassIsNominal)
      throw new Exception("Can't compute K&B Info score: class numeric!"); 
    return 100.0D * KBInformation() / priorEntropy();
  }
  
  public final double SFPriorEntropy() {
    return this.m_SumPriorEntropy;
  }
  
  public final double SFMeanPriorEntropy() {
    return this.m_SumPriorEntropy / this.m_WithClass;
  }
  
  public final double SFSchemeEntropy() {
    return this.m_SumSchemeEntropy;
  }
  
  public final double SFMeanSchemeEntropy() {
    return this.m_SumSchemeEntropy / this.m_WithClass;
  }
  
  public final double SFEntropyGain() {
    return this.m_SumPriorEntropy - this.m_SumSchemeEntropy;
  }
  
  public final double SFMeanEntropyGain() {
    return (this.m_SumPriorEntropy - this.m_SumSchemeEntropy) / this.m_WithClass;
  }
  
  public String toCumulativeMarginDistributionString() throws Exception {
    if (!this.m_ClassIsNominal)
      throw new Exception("Class must be nominal for margin distributions"); 
    String str = "";
    double d = 0.0D;
    for (byte b = 0; b <= k_MarginResolution; b++) {
      if (this.m_MarginCounts[b] != 0.0D) {
        d += this.m_MarginCounts[b];
        double d1 = b * 2.0D / k_MarginResolution - 1.0D;
        str = str + Utils.doubleToString(d1, 7, 3) + ' ' + Utils.doubleToString(d * 100.0D / this.m_WithClass, 7, 3) + '\n';
      } else if (b == 0) {
        str = Utils.doubleToString(-1.0D, 7, 3) + ' ' + Utils.doubleToString(0.0D, 7, 3) + '\n';
      } 
    } 
    return str;
  }
  
  public String toSummaryString() {
    return toSummaryString("", false);
  }
  
  public String toSummaryString(boolean paramBoolean) {
    return toSummaryString("=== Summary ===\n", paramBoolean);
  }
  
  public String toSummaryString(String paramString, boolean paramBoolean) {
    double d = 0.0D;
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramString + "\n");
    try {
      if (this.m_WithClass > 0.0D) {
        if (this.m_ClassIsNominal) {
          stringBuffer.append("Correctly Classified Instances     ");
          stringBuffer.append(Utils.doubleToString(correct(), 12, 4) + "     " + Utils.doubleToString(pctCorrect(), 12, 4) + " %\n");
          stringBuffer.append("Incorrectly Classified Instances   ");
          stringBuffer.append(Utils.doubleToString(incorrect(), 12, 4) + "     " + Utils.doubleToString(pctIncorrect(), 12, 4) + " %\n");
          stringBuffer.append("Kappa statistic                    ");
          stringBuffer.append(Utils.doubleToString(kappa(), 12, 4) + "\n");
          if (this.m_CostMatrix != null) {
            stringBuffer.append("Total Cost                         ");
            stringBuffer.append(Utils.doubleToString(totalCost(), 12, 4) + "\n");
            stringBuffer.append("Average Cost                       ");
            stringBuffer.append(Utils.doubleToString(avgCost(), 12, 4) + "\n");
          } 
          if (paramBoolean) {
            stringBuffer.append("K&B Relative Info Score            ");
            stringBuffer.append(Utils.doubleToString(KBRelativeInformation(), 12, 4) + " %\n");
            stringBuffer.append("K&B Information Score              ");
            stringBuffer.append(Utils.doubleToString(KBInformation(), 12, 4) + " bits");
            stringBuffer.append(Utils.doubleToString(KBMeanInformation(), 12, 4) + " bits/instance\n");
          } 
        } else {
          stringBuffer.append("Correlation coefficient            ");
          stringBuffer.append(Utils.doubleToString(correlationCoefficient(), 12, 4) + "\n");
        } 
        if (paramBoolean) {
          stringBuffer.append("Class complexity | order 0         ");
          stringBuffer.append(Utils.doubleToString(SFPriorEntropy(), 12, 4) + " bits");
          stringBuffer.append(Utils.doubleToString(SFMeanPriorEntropy(), 12, 4) + " bits/instance\n");
          stringBuffer.append("Class complexity | scheme          ");
          stringBuffer.append(Utils.doubleToString(SFSchemeEntropy(), 12, 4) + " bits");
          stringBuffer.append(Utils.doubleToString(SFMeanSchemeEntropy(), 12, 4) + " bits/instance\n");
          stringBuffer.append("Complexity improvement     (Sf)    ");
          stringBuffer.append(Utils.doubleToString(SFEntropyGain(), 12, 4) + " bits");
          stringBuffer.append(Utils.doubleToString(SFMeanEntropyGain(), 12, 4) + " bits/instance\n");
        } 
        stringBuffer.append("Mean absolute error                ");
        stringBuffer.append(Utils.doubleToString(meanAbsoluteError(), 12, 4) + "\n");
        stringBuffer.append("Root mean squared error            ");
        stringBuffer.append(Utils.doubleToString(rootMeanSquaredError(), 12, 4) + "\n");
        stringBuffer.append("Relative absolute error            ");
        stringBuffer.append(Utils.doubleToString(relativeAbsoluteError(), 12, 4) + " %\n");
        stringBuffer.append("Root relative squared error        ");
        stringBuffer.append(Utils.doubleToString(rootRelativeSquaredError(), 12, 4) + " %\n");
      } 
      if (Utils.gr(unclassified(), 0.0D)) {
        stringBuffer.append("UnClassified Instances             ");
        stringBuffer.append(Utils.doubleToString(unclassified(), 12, 4) + "     " + Utils.doubleToString(pctUnclassified(), 12, 4) + " %\n");
      } 
      stringBuffer.append("Total Number of Instances          ");
      stringBuffer.append(Utils.doubleToString(this.m_WithClass, 12, 4) + "\n");
      if (this.m_MissingClass > 0.0D) {
        stringBuffer.append("Ignored Class Unknown Instances            ");
        stringBuffer.append(Utils.doubleToString(this.m_MissingClass, 12, 4) + "\n");
      } 
    } catch (Exception exception) {
      System.err.println("Arggh - Must be a bug in Evaluation class");
    } 
    return stringBuffer.toString();
  }
  
  public String toMatrixString() throws Exception {
    return toMatrixString("=== Confusion Matrix ===\n");
  }
  
  public String toMatrixString(String paramString) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    char[] arrayOfChar = { 
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
        'u', 'v', 'w', 'x', 'y', 'z' };
    boolean bool = false;
    if (!this.m_ClassIsNominal)
      throw new Exception("Evaluation: No confusion matrix possible!"); 
    double d = 0.0D;
    byte b;
    for (b = 0; b < this.m_NumClasses; b++) {
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
        double d1 = this.m_ConfusionMatrix[b][b1];
        if (d1 < 0.0D)
          d1 *= -10.0D; 
        if (d1 > d)
          d = d1; 
        double d2 = d1 - Math.rint(d1);
        if (!bool && Math.log(d2) / Math.log(10.0D) >= -2.0D)
          bool = true; 
      } 
    } 
    int i = 1 + Math.max((int)(Math.log(d) / Math.log(10.0D) + (bool ? 3 : false)), (int)(Math.log(this.m_NumClasses) / Math.log(arrayOfChar.length)));
    stringBuffer.append(paramString).append("\n");
    for (b = 0; b < this.m_NumClasses; b++) {
      if (bool) {
        stringBuffer.append(" ").append(num2ShortID(b, arrayOfChar, i - 3)).append("   ");
      } else {
        stringBuffer.append(" ").append(num2ShortID(b, arrayOfChar, i));
      } 
    } 
    stringBuffer.append("   <-- classified as\n");
    for (b = 0; b < this.m_NumClasses; b++) {
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
        stringBuffer.append(" ").append(Utils.doubleToString(this.m_ConfusionMatrix[b][b1], i, bool ? 2 : 0)); 
      stringBuffer.append(" | ").append(num2ShortID(b, arrayOfChar, i)).append(" = ").append(this.m_ClassNames[b]).append("\n");
    } 
    return stringBuffer.toString();
  }
  
  public String toClassDetailsString() throws Exception {
    return toClassDetailsString("=== Detailed Accuracy By Class ===\n");
  }
  
  public String toClassDetailsString(String paramString) throws Exception {
    if (!this.m_ClassIsNominal)
      throw new Exception("Evaluation: No confusion matrix possible!"); 
    StringBuffer stringBuffer = new StringBuffer(paramString + "\nTP Rate   FP Rate" + "   Precision   Recall" + "  F-Measure   Class\n");
    for (byte b = 0; b < this.m_NumClasses; b++) {
      stringBuffer.append(Utils.doubleToString(truePositiveRate(b), 7, 3)).append("   ");
      stringBuffer.append(Utils.doubleToString(falsePositiveRate(b), 7, 3)).append("    ");
      stringBuffer.append(Utils.doubleToString(precision(b), 7, 3)).append("   ");
      stringBuffer.append(Utils.doubleToString(recall(b), 7, 3)).append("   ");
      stringBuffer.append(Utils.doubleToString(fMeasure(b), 7, 3)).append("    ");
      stringBuffer.append(this.m_ClassNames[b]).append('\n');
    } 
    return stringBuffer.toString();
  }
  
  public double numTruePositives(int paramInt) {
    double d = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b == paramInt)
        d += this.m_ConfusionMatrix[paramInt][b]; 
    } 
    return d;
  }
  
  public double truePositiveRate(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b == paramInt)
        d1 += this.m_ConfusionMatrix[paramInt][b]; 
      d2 += this.m_ConfusionMatrix[paramInt][b];
    } 
    return (d2 == 0.0D) ? 0.0D : (d1 / d2);
  }
  
  public double numTrueNegatives(int paramInt) {
    double d = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b != paramInt)
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (b1 != paramInt)
            d += this.m_ConfusionMatrix[b][b1]; 
        }  
    } 
    return d;
  }
  
  public double trueNegativeRate(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b != paramInt)
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (b1 != paramInt)
            d1 += this.m_ConfusionMatrix[b][b1]; 
          d2 += this.m_ConfusionMatrix[b][b1];
        }  
    } 
    return (d2 == 0.0D) ? 0.0D : (d1 / d2);
  }
  
  public double numFalsePositives(int paramInt) {
    double d = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b != paramInt)
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (b1 == paramInt)
            d += this.m_ConfusionMatrix[b][b1]; 
        }  
    } 
    return d;
  }
  
  public double falsePositiveRate(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b != paramInt)
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (b1 == paramInt)
            d1 += this.m_ConfusionMatrix[b][b1]; 
          d2 += this.m_ConfusionMatrix[b][b1];
        }  
    } 
    return (d2 == 0.0D) ? 0.0D : (d1 / d2);
  }
  
  public double numFalseNegatives(int paramInt) {
    double d = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b == paramInt)
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (b1 != paramInt)
            d += this.m_ConfusionMatrix[b][b1]; 
        }  
    } 
    return d;
  }
  
  public double falseNegativeRate(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b == paramInt)
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (b1 != paramInt)
            d1 += this.m_ConfusionMatrix[b][b1]; 
          d2 += this.m_ConfusionMatrix[b][b1];
        }  
    } 
    return (d2 == 0.0D) ? 0.0D : (d1 / d2);
  }
  
  public double recall(int paramInt) {
    return truePositiveRate(paramInt);
  }
  
  public double precision(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b == paramInt)
        d1 += this.m_ConfusionMatrix[b][paramInt]; 
      d2 += this.m_ConfusionMatrix[b][paramInt];
    } 
    return (d2 == 0.0D) ? 0.0D : (d1 / d2);
  }
  
  public double fMeasure(int paramInt) {
    double d1 = precision(paramInt);
    double d2 = recall(paramInt);
    return (d1 + d2 == 0.0D) ? 0.0D : (2.0D * d1 * d2 / (d1 + d2));
  }
  
  public void setPriors(Instances paramInstances) throws Exception {
    if (!this.m_ClassIsNominal) {
      this.m_NumTrainClassVals = 0;
      this.m_TrainClassVals = null;
      this.m_TrainClassWeights = null;
      this.m_PriorErrorEstimator = null;
      this.m_ErrorEstimator = null;
      for (byte b = 0; b < paramInstances.numInstances(); b++) {
        Instance instance = paramInstances.instance(b);
        if (!instance.classIsMissing())
          addNumericTrainClass(instance.classValue(), instance.weight()); 
      } 
    } else {
      byte b;
      for (b = 0; b < this.m_NumClasses; b++)
        this.m_ClassPriors[b] = 1.0D; 
      this.m_ClassPriorsSum = this.m_NumClasses;
      for (b = 0; b < paramInstances.numInstances(); b++) {
        if (!paramInstances.instance(b).classIsMissing()) {
          this.m_ClassPriors[(int)paramInstances.instance(b).classValue()] = this.m_ClassPriors[(int)paramInstances.instance(b).classValue()] + paramInstances.instance(b).weight();
          this.m_ClassPriorsSum += paramInstances.instance(b).weight();
        } 
      } 
    } 
  }
  
  public void updatePriors(Instance paramInstance) throws Exception {
    if (!paramInstance.classIsMissing())
      if (!this.m_ClassIsNominal) {
        if (!paramInstance.classIsMissing())
          addNumericTrainClass(paramInstance.classValue(), paramInstance.weight()); 
      } else {
        this.m_ClassPriors[(int)paramInstance.classValue()] = this.m_ClassPriors[(int)paramInstance.classValue()] + paramInstance.weight();
        this.m_ClassPriorsSum += paramInstance.weight();
      }  
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !paramObject.getClass().equals(getClass()))
      return false; 
    Evaluation evaluation = (Evaluation)paramObject;
    if (this.m_ClassIsNominal != evaluation.m_ClassIsNominal)
      return false; 
    if (this.m_NumClasses != evaluation.m_NumClasses)
      return false; 
    if (this.m_Incorrect != evaluation.m_Incorrect)
      return false; 
    if (this.m_Correct != evaluation.m_Correct)
      return false; 
    if (this.m_Unclassified != evaluation.m_Unclassified)
      return false; 
    if (this.m_MissingClass != evaluation.m_MissingClass)
      return false; 
    if (this.m_WithClass != evaluation.m_WithClass)
      return false; 
    if (this.m_SumErr != evaluation.m_SumErr)
      return false; 
    if (this.m_SumAbsErr != evaluation.m_SumAbsErr)
      return false; 
    if (this.m_SumSqrErr != evaluation.m_SumSqrErr)
      return false; 
    if (this.m_SumClass != evaluation.m_SumClass)
      return false; 
    if (this.m_SumSqrClass != evaluation.m_SumSqrClass)
      return false; 
    if (this.m_SumPredicted != evaluation.m_SumPredicted)
      return false; 
    if (this.m_SumSqrPredicted != evaluation.m_SumSqrPredicted)
      return false; 
    if (this.m_SumClassPredicted != evaluation.m_SumClassPredicted)
      return false; 
    if (this.m_ClassIsNominal)
      for (byte b = 0; b < this.m_NumClasses; b++) {
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
          if (this.m_ConfusionMatrix[b][b1] != evaluation.m_ConfusionMatrix[b][b1])
            return false; 
        } 
      }  
    return true;
  }
  
  protected static String printClassifications(Classifier paramClassifier, Instances paramInstances, String paramString, int paramInt, Range paramRange) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramString.length() != 0) {
      BufferedReader bufferedReader = null;
      try {
        bufferedReader = new BufferedReader(new FileReader(paramString));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + '.');
      } 
      Instances instances = new Instances(bufferedReader, 1);
      if (paramInt != -1) {
        instances.setClassIndex(paramInt - 1);
      } else {
        instances.setClassIndex(instances.numAttributes() - 1);
      } 
      for (byte b = 0; instances.readInstance(bufferedReader); b++) {
        Instance instance1 = instances.instance(0);
        Instance instance2 = (Instance)instance1.copy();
        instance2.setDataset(instances);
        double d = paramClassifier.classifyInstance(instance2);
        if (instances.classAttribute().isNumeric()) {
          if (Instance.isMissingValue(d)) {
            stringBuffer.append(b + " missing ");
          } else {
            stringBuffer.append(b + " " + d + " ");
          } 
          if (instance1.classIsMissing()) {
            stringBuffer.append("missing");
          } else {
            stringBuffer.append(instance1.classValue());
          } 
          stringBuffer.append(" " + attributeValuesString(instance2, paramRange) + "\n");
        } else {
          if (Instance.isMissingValue(d)) {
            stringBuffer.append(b + " missing ");
          } else {
            stringBuffer.append(b + " " + instances.classAttribute().value((int)d) + " ");
          } 
          if (Instance.isMissingValue(d)) {
            stringBuffer.append("missing ");
          } else {
            stringBuffer.append(paramClassifier.distributionForInstance(instance2)[(int)d] + " ");
          } 
          stringBuffer.append(instance1.toString(instance1.classIndex()) + " " + attributeValuesString(instance2, paramRange) + "\n");
        } 
        instances.delete(0);
      } 
      bufferedReader.close();
    } 
    return stringBuffer.toString();
  }
  
  protected static String attributeValuesString(Instance paramInstance, Range paramRange) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramRange != null) {
      boolean bool = true;
      paramRange.setUpper(paramInstance.numAttributes() - 1);
      for (byte b = 0; b < paramInstance.numAttributes(); b++) {
        if (paramRange.isInRange(b) && b != paramInstance.classIndex()) {
          if (bool) {
            stringBuffer.append("(");
          } else {
            stringBuffer.append(",");
          } 
          stringBuffer.append(paramInstance.toString(b));
          bool = false;
        } 
      } 
      if (!bool)
        stringBuffer.append(")"); 
    } 
    return stringBuffer.toString();
  }
  
  protected static String makeOptionString(Classifier paramClassifier) {
    StringBuffer stringBuffer = new StringBuffer("");
    stringBuffer.append("\n\nGeneral options:\n\n");
    stringBuffer.append("-t <name of training file>\n");
    stringBuffer.append("\tSets training file.\n");
    stringBuffer.append("-T <name of test file>\n");
    stringBuffer.append("\tSets test file. If missing, a cross-validation");
    stringBuffer.append(" will be performed on the training data.\n");
    stringBuffer.append("-c <class index>\n");
    stringBuffer.append("\tSets index of class attribute (default: last).\n");
    stringBuffer.append("-x <number of folds>\n");
    stringBuffer.append("\tSets number of folds for cross-validation (default: 10).\n");
    stringBuffer.append("-s <random number seed>\n");
    stringBuffer.append("\tSets random number seed for cross-validation (default: 1).\n");
    stringBuffer.append("-m <name of file with cost matrix>\n");
    stringBuffer.append("\tSets file with cost matrix.\n");
    stringBuffer.append("-l <name of input file>\n");
    stringBuffer.append("\tSets model input file.\n");
    stringBuffer.append("-d <name of output file>\n");
    stringBuffer.append("\tSets model output file.\n");
    stringBuffer.append("-v\n");
    stringBuffer.append("\tOutputs no statistics for training data.\n");
    stringBuffer.append("-o\n");
    stringBuffer.append("\tOutputs statistics only, not the classifier.\n");
    stringBuffer.append("-i\n");
    stringBuffer.append("\tOutputs detailed information-retrieval");
    stringBuffer.append(" statistics for each class.\n");
    stringBuffer.append("-k\n");
    stringBuffer.append("\tOutputs information-theoretic statistics.\n");
    stringBuffer.append("-p <attribute range>\n");
    stringBuffer.append("\tOnly outputs predictions for test instances, along with attributes (0 for none).\n");
    stringBuffer.append("-r\n");
    stringBuffer.append("\tOnly outputs cumulative margin distribution.\n");
    if (paramClassifier instanceof Sourcable) {
      stringBuffer.append("-z <class name>\n");
      stringBuffer.append("\tOnly outputs the source representation of the classifier, giving it the supplied name.\n");
    } 
    if (paramClassifier instanceof Drawable) {
      stringBuffer.append("-g\n");
      stringBuffer.append("\tOnly outputs the graph representation of the classifier.\n");
    } 
    if (paramClassifier instanceof weka.core.OptionHandler) {
      stringBuffer.append("\nOptions specific to " + paramClassifier.getClass().getName() + ":\n\n");
      Enumeration enumeration = paramClassifier.listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + "\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  protected String num2ShortID(int paramInt1, char[] paramArrayOfchar, int paramInt2) {
    char[] arrayOfChar = new char[paramInt2];
    int i;
    for (i = paramInt2 - 1; i >= 0; i--) {
      arrayOfChar[i] = paramArrayOfchar[paramInt1 % paramArrayOfchar.length];
      paramInt1 = paramInt1 / paramArrayOfchar.length - 1;
      if (paramInt1 < 0)
        break; 
    } 
    while (--i >= 0) {
      arrayOfChar[i] = ' ';
      i--;
    } 
    return new String(arrayOfChar);
  }
  
  protected double[] makeDistribution(double paramDouble) {
    double[] arrayOfDouble = new double[this.m_NumClasses];
    if (Instance.isMissingValue(paramDouble))
      return arrayOfDouble; 
    if (this.m_ClassIsNominal) {
      arrayOfDouble[(int)paramDouble] = 1.0D;
    } else {
      arrayOfDouble[0] = paramDouble;
    } 
    return arrayOfDouble;
  }
  
  protected void updateStatsForClassifier(double[] paramArrayOfdouble, Instance paramInstance) throws Exception {
    int i = (int)paramInstance.classValue();
    double d = 1.0D;
    if (!paramInstance.classIsMissing()) {
      updateMargins(paramArrayOfdouble, i, paramInstance.weight());
      byte b = -1;
      double d1 = 0.0D;
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
        if (paramArrayOfdouble[b1] > d1) {
          b = b1;
          d1 = paramArrayOfdouble[b1];
        } 
      } 
      this.m_WithClass += paramInstance.weight();
      if (this.m_CostMatrix != null)
        if (b < 0) {
          this.m_TotalCost += paramInstance.weight() * this.m_CostMatrix.getMaxCost(i);
        } else {
          this.m_TotalCost += paramInstance.weight() * this.m_CostMatrix.getElement(i, b);
        }  
      if (b < 0) {
        this.m_Unclassified += paramInstance.weight();
        return;
      } 
      double d2 = Math.max(Double.MIN_VALUE, paramArrayOfdouble[i]);
      double d3 = Math.max(Double.MIN_VALUE, this.m_ClassPriors[i] / this.m_ClassPriorsSum);
      if (d2 >= d3) {
        this.m_SumKBInfo += (Utils.log2(d2) - Utils.log2(d3)) * paramInstance.weight();
      } else {
        this.m_SumKBInfo -= (Utils.log2(1.0D - d2) - Utils.log2(1.0D - d3)) * paramInstance.weight();
      } 
      this.m_SumSchemeEntropy -= Utils.log2(d2) * paramInstance.weight();
      this.m_SumPriorEntropy -= Utils.log2(d3) * paramInstance.weight();
      updateNumericScores(paramArrayOfdouble, makeDistribution(paramInstance.classValue()), paramInstance.weight());
      this.m_ConfusionMatrix[i][b] = this.m_ConfusionMatrix[i][b] + paramInstance.weight();
      if (b != i) {
        this.m_Incorrect += paramInstance.weight();
      } else {
        this.m_Correct += paramInstance.weight();
      } 
    } else {
      this.m_MissingClass += paramInstance.weight();
    } 
  }
  
  protected void updateStatsForPredictor(double paramDouble, Instance paramInstance) throws Exception {
    if (!paramInstance.classIsMissing()) {
      this.m_WithClass += paramInstance.weight();
      if (Instance.isMissingValue(paramDouble)) {
        this.m_Unclassified += paramInstance.weight();
        return;
      } 
      this.m_SumClass += paramInstance.weight() * paramInstance.classValue();
      this.m_SumSqrClass += paramInstance.weight() * paramInstance.classValue() * paramInstance.classValue();
      this.m_SumClassPredicted += paramInstance.weight() * paramInstance.classValue() * paramDouble;
      this.m_SumPredicted += paramInstance.weight() * paramDouble;
      this.m_SumSqrPredicted += paramInstance.weight() * paramDouble * paramDouble;
      if (this.m_ErrorEstimator == null)
        setNumericPriorsFromBuffer(); 
      double d1 = Math.max(this.m_ErrorEstimator.getProbability(paramDouble - paramInstance.classValue()), Double.MIN_VALUE);
      double d2 = Math.max(this.m_PriorErrorEstimator.getProbability(paramInstance.classValue()), Double.MIN_VALUE);
      this.m_SumSchemeEntropy -= Utils.log2(d1) * paramInstance.weight();
      this.m_SumPriorEntropy -= Utils.log2(d2) * paramInstance.weight();
      this.m_ErrorEstimator.addValue(paramDouble - paramInstance.classValue(), paramInstance.weight());
      updateNumericScores(makeDistribution(paramDouble), makeDistribution(paramInstance.classValue()), paramInstance.weight());
    } else {
      this.m_MissingClass += paramInstance.weight();
    } 
  }
  
  protected void updateMargins(double[] paramArrayOfdouble, int paramInt, double paramDouble) {
    double d1 = paramArrayOfdouble[paramInt];
    double d2 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      if (b != paramInt && paramArrayOfdouble[b] > d2)
        d2 = paramArrayOfdouble[b]; 
    } 
    double d3 = d1 - d2;
    int i = (int)((d3 + 1.0D) / 2.0D * k_MarginResolution);
    this.m_MarginCounts[i] = this.m_MarginCounts[i] + paramDouble;
  }
  
  protected void updateNumericScores(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, double paramDouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    for (byte b = 0; b < this.m_NumClasses; b++) {
      double d = paramArrayOfdouble1[b] - paramArrayOfdouble2[b];
      d1 += d;
      d2 += Math.abs(d);
      d3 += d * d;
      d = this.m_ClassPriors[b] / this.m_ClassPriorsSum - paramArrayOfdouble2[b];
      d4 += Math.abs(d);
      d5 += d * d;
    } 
    this.m_SumErr += paramDouble * d1 / this.m_NumClasses;
    this.m_SumAbsErr += paramDouble * d2 / this.m_NumClasses;
    this.m_SumSqrErr += paramDouble * d3 / this.m_NumClasses;
    this.m_SumPriorAbsErr += paramDouble * d4 / this.m_NumClasses;
    this.m_SumPriorSqrErr += paramDouble * d5 / this.m_NumClasses;
  }
  
  protected void addNumericTrainClass(double paramDouble1, double paramDouble2) {
    if (this.m_TrainClassVals == null) {
      this.m_TrainClassVals = new double[100];
      this.m_TrainClassWeights = new double[100];
    } 
    if (this.m_NumTrainClassVals == this.m_TrainClassVals.length) {
      double[] arrayOfDouble = new double[this.m_TrainClassVals.length * 2];
      System.arraycopy(this.m_TrainClassVals, 0, arrayOfDouble, 0, this.m_TrainClassVals.length);
      this.m_TrainClassVals = arrayOfDouble;
      arrayOfDouble = new double[this.m_TrainClassWeights.length * 2];
      System.arraycopy(this.m_TrainClassWeights, 0, arrayOfDouble, 0, this.m_TrainClassWeights.length);
      this.m_TrainClassWeights = arrayOfDouble;
    } 
    this.m_TrainClassVals[this.m_NumTrainClassVals] = paramDouble1;
    this.m_TrainClassWeights[this.m_NumTrainClassVals] = paramDouble2;
    this.m_NumTrainClassVals++;
  }
  
  protected void setNumericPriorsFromBuffer() {
    double d = 0.01D;
    if (this.m_NumTrainClassVals > 1) {
      double[] arrayOfDouble = new double[this.m_NumTrainClassVals];
      System.arraycopy(this.m_TrainClassVals, 0, arrayOfDouble, 0, this.m_NumTrainClassVals);
      int[] arrayOfInt = Utils.sort(arrayOfDouble);
      double d1 = arrayOfDouble[arrayOfInt[0]];
      double d2 = 0.0D;
      byte b1 = 0;
      for (byte b2 = 1; b2 < arrayOfDouble.length; b2++) {
        double d3 = arrayOfDouble[arrayOfInt[b2]];
        if (d3 != d1) {
          d2 += d3 - d1;
          d1 = d3;
          b1++;
        } 
      } 
      if (b1 > 0)
        d = d2 / b1; 
    } 
    this.m_PriorErrorEstimator = (Estimator)new KernelEstimator(d);
    this.m_ErrorEstimator = (Estimator)new KernelEstimator(d);
    this.m_ClassPriors[0] = this.m_ClassPriorsSum = 0.0D;
    for (byte b = 0; b < this.m_NumTrainClassVals; b++) {
      this.m_ClassPriors[0] = this.m_ClassPriors[0] + this.m_TrainClassVals[b] * this.m_TrainClassWeights[b];
      this.m_ClassPriorsSum += this.m_TrainClassWeights[b];
      this.m_PriorErrorEstimator.addValue(this.m_TrainClassVals[b], this.m_TrainClassWeights[b]);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\Evaluation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */