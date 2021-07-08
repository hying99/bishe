package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
import weka.classifiers.Sourcable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class LogitBoost extends RandomizableIteratedSingleClassifierEnhancer implements Sourcable, WeightedInstancesHandler {
  protected Classifier[][] m_Classifiers;
  
  protected int m_NumClasses;
  
  protected int m_NumGenerated;
  
  protected int m_NumFolds = 0;
  
  protected int m_NumRuns = 1;
  
  protected int m_WeightThreshold = 100;
  
  protected static final double Z_MAX = 3.0D;
  
  protected Instances m_NumericClassData;
  
  protected Attribute m_ClassAttribute;
  
  protected boolean m_UseResampling;
  
  protected double m_Precision = -1.7976931348623157E308D;
  
  protected double m_Shrinkage = 1.0D;
  
  protected Random m_RandomInstance = null;
  
  protected double m_Offset = 0.0D;
  
  public String globalInfo() {
    return "Class for performing additive logistic regression. This class performs classification using a regression scheme as the base learner, and can handle multi-class problems.  For more information, see\n\nFriedman, J., T. Hastie and R. Tibshirani (1998) \"Additive Logistic Regression: a Statistical View of Boosting\". Technical report. Stanford University.\n\nCan do efficient internal cross-validation to determine appropriate number of iterations.";
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.DecisionStump";
  }
  
  protected Instances selectWeightQuantile(Instances paramInstances, double paramDouble) {
    int i = paramInstances.numInstances();
    Instances instances = new Instances(paramInstances, i);
    double[] arrayOfDouble = new double[i];
    double d1 = 0.0D;
    for (byte b = 0; b < i; b++) {
      arrayOfDouble[b] = paramInstances.instance(b).weight();
      d1 += arrayOfDouble[b];
    } 
    double d2 = d1 * paramDouble;
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    d1 = 0.0D;
    for (int j = i - 1; j >= 0; j--) {
      Instance instance = (Instance)paramInstances.instance(arrayOfInt[j]).copy();
      instances.add(instance);
      d1 += arrayOfDouble[arrayOfInt[j]];
      if (d1 > d2 && j > 0 && arrayOfDouble[arrayOfInt[j]] != arrayOfDouble[arrayOfInt[j - 1]])
        break; 
    } 
    if (this.m_Debug)
      System.err.println("Selected " + instances.numInstances() + " out of " + i); 
    return instances;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tUse resampling for boosting.", "Q", 0, "-Q"));
    vector.addElement(new Option("\tPercentage of weight mass to base training on.\n\t(default 100, reduce to around 90 speed up)", "P", 1, "-P <percent>"));
    vector.addElement(new Option("\tNumber of folds for internal cross-validation.\n\t(default 0 -- no cross-validation)", "F", 1, "-F <num>"));
    vector.addElement(new Option("\tNumber of runs for internal cross-validation.\n\t(default 1)", "R", 1, "-R <num>"));
    vector.addElement(new Option("\tThreshold on the improvement of the likelihood.\n\t(default -Double.MAX_VALUE)", "L", 1, "-L <num>"));
    vector.addElement(new Option("\tShrinkage parameter.\n\t(default 1)", "H", 1, "-H <num>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('F', paramArrayOfString);
    if (str1.length() != 0) {
      setNumFolds(Integer.parseInt(str1));
    } else {
      setNumFolds(0);
    } 
    String str2 = Utils.getOption('R', paramArrayOfString);
    if (str2.length() != 0) {
      setNumRuns(Integer.parseInt(str2));
    } else {
      setNumRuns(1);
    } 
    String str3 = Utils.getOption('P', paramArrayOfString);
    if (str3.length() != 0) {
      setWeightThreshold(Integer.parseInt(str3));
    } else {
      setWeightThreshold(100);
    } 
    String str4 = Utils.getOption('L', paramArrayOfString);
    if (str4.length() != 0) {
      setLikelihoodThreshold((new Double(str4)).doubleValue());
    } else {
      setLikelihoodThreshold(-1.7976931348623157E308D);
    } 
    String str5 = Utils.getOption('H', paramArrayOfString);
    if (str5.length() != 0) {
      setShrinkage((new Double(str5)).doubleValue());
    } else {
      setShrinkage(1.0D);
    } 
    setUseResampling(Utils.getFlag('Q', paramArrayOfString));
    if (this.m_UseResampling && str3.length() != 0)
      throw new Exception("Weight pruning with resamplingnot allowed."); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 10];
    int i = 0;
    if (getUseResampling()) {
      arrayOfString2[i++] = "-Q";
    } else {
      arrayOfString2[i++] = "-P";
      arrayOfString2[i++] = "" + getWeightThreshold();
    } 
    arrayOfString2[i++] = "-F";
    arrayOfString2[i++] = "" + getNumFolds();
    arrayOfString2[i++] = "-R";
    arrayOfString2[i++] = "" + getNumRuns();
    arrayOfString2[i++] = "-L";
    arrayOfString2[i++] = "" + getLikelihoodThreshold();
    arrayOfString2[i++] = "-H";
    arrayOfString2[i++] = "" + getShrinkage();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String shrinkageTipText() {
    return "Shrinkage parameter (use small value like 0.1 to reduce overfitting).";
  }
  
  public double getShrinkage() {
    return this.m_Shrinkage;
  }
  
  public void setShrinkage(double paramDouble) {
    this.m_Shrinkage = paramDouble;
  }
  
  public String likelihoodThresholdTipText() {
    return "Threshold on improvement in likelihood.";
  }
  
  public double getLikelihoodThreshold() {
    return this.m_Precision;
  }
  
  public void setLikelihoodThreshold(double paramDouble) {
    this.m_Precision = paramDouble;
  }
  
  public String numRunsTipText() {
    return "Number of runs for internal cross-validation.";
  }
  
  public int getNumRuns() {
    return this.m_NumRuns;
  }
  
  public void setNumRuns(int paramInt) {
    this.m_NumRuns = paramInt;
  }
  
  public String numFoldsTipText() {
    return "Number of folds for internal cross-validation (default 0 means no cross-validation is performed).";
  }
  
  public int getNumFolds() {
    return this.m_NumFolds;
  }
  
  public void setNumFolds(int paramInt) {
    this.m_NumFolds = paramInt;
  }
  
  public String useResamplingTipText() {
    return "Whether resampling is used instead of reweighting.";
  }
  
  public void setUseResampling(boolean paramBoolean) {
    this.m_UseResampling = paramBoolean;
  }
  
  public boolean getUseResampling() {
    return this.m_UseResampling;
  }
  
  public String weightThresholdTipText() {
    return "Weight threshold for weight pruning (reduce to 90 for speeding up learning process).";
  }
  
  public void setWeightThreshold(int paramInt) {
    this.m_WeightThreshold = paramInt;
  }
  
  public int getWeightThreshold() {
    return this.m_WeightThreshold;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_RandomInstance = new Random(this.m_Seed);
    int i = paramInstances.classIndex();
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("LogitBoost can't handle a numeric class!"); 
    if (this.m_Classifier == null)
      throw new Exception("A base classifier has not been specified!"); 
    if (!(this.m_Classifier instanceof WeightedInstancesHandler) && !this.m_UseResampling)
      this.m_UseResampling = true; 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (this.m_Debug)
      System.err.println("Creating copy of the training data"); 
    this.m_NumClasses = paramInstances.numClasses();
    this.m_ClassAttribute = paramInstances.classAttribute();
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (this.m_Debug)
      System.err.println("Creating base classifiers"); 
    this.m_Classifiers = new Classifier[this.m_NumClasses][];
    int j;
    for (j = 0; j < this.m_NumClasses; j++)
      this.m_Classifiers[j] = Classifier.makeCopies(this.m_Classifier, getNumIterations()); 
    j = getNumIterations();
    if (this.m_NumFolds > 1) {
      if (this.m_Debug)
        System.err.println("Processing first fold."); 
      double[] arrayOfDouble = new double[getNumIterations()];
      for (byte b3 = 0; b3 < this.m_NumRuns; b3++) {
        paramInstances.randomize(this.m_RandomInstance);
        paramInstances.stratify(this.m_NumFolds);
        for (byte b = 0; b < this.m_NumFolds; b++) {
          Instances instances1 = paramInstances.trainCV(this.m_NumFolds, b, this.m_RandomInstance);
          Instances instances2 = paramInstances.testCV(this.m_NumFolds, b);
          Instances instances3 = new Instances(instances1);
          instances3.setClassIndex(-1);
          instances3.deleteAttributeAt(i);
          instances3.insertAttributeAt(new Attribute("'pseudo class'"), i);
          instances3.setClassIndex(i);
          this.m_NumericClassData = new Instances(instances3, 0);
          int m = instances1.numInstances();
          double[][] arrayOfDouble4 = new double[m][this.m_NumClasses];
          double[][] arrayOfDouble5 = new double[m][this.m_NumClasses];
          for (byte b5 = 0; b5 < this.m_NumClasses; b5++) {
            for (byte b7 = 0; b7 < m; b7++)
              arrayOfDouble5[b7][b5] = (instances1.instance(b7).classValue() == b5) ? (1.0D - this.m_Offset) : (0.0D + this.m_Offset / this.m_NumClasses); 
          } 
          double[][] arrayOfDouble6 = initialProbs(m);
          this.m_NumGenerated = 0;
          double d3 = instances1.sumOfWeights();
          for (byte b6 = 0; b6 < getNumIterations(); b6++) {
            performIteration(arrayOfDouble5, arrayOfDouble4, arrayOfDouble6, instances3, d3);
            Evaluation evaluation = new Evaluation(instances1);
            evaluation.evaluateModel((Classifier)this, instances2);
            arrayOfDouble[b6] = arrayOfDouble[b6] + evaluation.correct();
          } 
        } 
      } 
      double d = -1.7976931348623157E308D;
      for (byte b4 = 0; b4 < getNumIterations(); b4++) {
        if (arrayOfDouble[b4] > d) {
          d = arrayOfDouble[b4];
          j = b4;
        } 
      } 
      if (this.m_Debug)
        System.err.println("Best result for " + j + " iterations: " + d); 
    } 
    int k = paramInstances.numInstances();
    double[][] arrayOfDouble1 = new double[k][this.m_NumClasses];
    double[][] arrayOfDouble2 = new double[k][this.m_NumClasses];
    for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
      byte b3 = 0;
      for (byte b4 = 0; b3 < k; b4++) {
        arrayOfDouble2[b3][b1] = (paramInstances.instance(b4).classValue() == b1) ? (1.0D - this.m_Offset) : (0.0D + this.m_Offset / this.m_NumClasses);
        b3++;
      } 
    } 
    paramInstances.setClassIndex(-1);
    paramInstances.deleteAttributeAt(i);
    paramInstances.insertAttributeAt(new Attribute("'pseudo class'"), i);
    paramInstances.setClassIndex(i);
    this.m_NumericClassData = new Instances(paramInstances, 0);
    double[][] arrayOfDouble3 = initialProbs(k);
    double d1 = logLikelihood(arrayOfDouble2, arrayOfDouble3);
    this.m_NumGenerated = 0;
    if (this.m_Debug)
      System.err.println("Avg. log-likelihood: " + d1); 
    double d2 = paramInstances.sumOfWeights();
    for (byte b2 = 0; b2 < j; b2++) {
      double d = d1;
      performIteration(arrayOfDouble2, arrayOfDouble1, arrayOfDouble3, paramInstances, d2);
      d1 = logLikelihood(arrayOfDouble2, arrayOfDouble3);
      if (this.m_Debug)
        System.err.println("Avg. log-likelihood: " + d1); 
      if (Math.abs(d - d1) < this.m_Precision)
        return; 
    } 
  }
  
  private double[][] initialProbs(int paramInt) {
    double[][] arrayOfDouble = new double[paramInt][this.m_NumClasses];
    for (byte b = 0; b < paramInt; b++) {
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
        arrayOfDouble[b][b1] = 1.0D / this.m_NumClasses; 
    } 
    return arrayOfDouble;
  }
  
  private double logLikelihood(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) {
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble1.length; b++) {
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++) {
        if (paramArrayOfdouble1[b][b1] == 1.0D - this.m_Offset)
          d -= Math.log(paramArrayOfdouble2[b][b1]); 
      } 
    } 
    return d / paramArrayOfdouble1.length;
  }
  
  private void performIteration(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2, double[][] paramArrayOfdouble3, Instances paramInstances, double paramDouble) throws Exception {
    if (this.m_Debug)
      System.err.println("Training classifier " + (this.m_NumGenerated + 1)); 
    byte b;
    for (b = 0; b < this.m_NumClasses; b++) {
      if (this.m_Debug)
        System.err.println("\t...for class " + (b + 1) + " (" + this.m_ClassAttribute.name() + "=" + this.m_ClassAttribute.value(b) + ")"); 
      Instances instances1 = new Instances(paramInstances);
      for (byte b1 = 0; b1 < paramArrayOfdouble3.length; b1++) {
        double d4;
        double d3 = paramArrayOfdouble3[b1][b];
        double d5 = paramArrayOfdouble1[b1][b];
        if (d5 == 1.0D - this.m_Offset) {
          d4 = 1.0D / d3;
          if (d4 > 3.0D)
            d4 = 3.0D; 
        } else {
          d4 = -1.0D / (1.0D - d3);
          if (d4 < -3.0D)
            d4 = -3.0D; 
        } 
        double d6 = (d5 - d3) / d4;
        Instance instance = instances1.instance(b1);
        instance.setValue(instances1.classIndex(), d4);
        instance.setWeight(instance.weight() * d6);
      } 
      double d1 = instances1.sumOfWeights();
      double d2 = paramDouble / d1;
      for (byte b2 = 0; b2 < paramArrayOfdouble3.length; b2++) {
        Instance instance = instances1.instance(b2);
        instance.setWeight(instance.weight() * d2);
      } 
      Instances instances2 = instances1;
      if (this.m_WeightThreshold < 100) {
        instances2 = selectWeightQuantile(instances1, this.m_WeightThreshold / 100.0D);
      } else if (this.m_UseResampling) {
        double[] arrayOfDouble = new double[instances1.numInstances()];
        for (byte b3 = 0; b3 < arrayOfDouble.length; b3++)
          arrayOfDouble[b3] = instances1.instance(b3).weight(); 
        instances2 = instances1.resampleWithWeights(this.m_RandomInstance, arrayOfDouble);
      } 
      this.m_Classifiers[b][this.m_NumGenerated].buildClassifier(instances2);
    } 
    for (b = 0; b < paramArrayOfdouble2.length; b++) {
      double[] arrayOfDouble = new double[this.m_NumClasses];
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < this.m_NumClasses; b1++) {
        arrayOfDouble[b1] = this.m_Shrinkage * this.m_Classifiers[b1][this.m_NumGenerated].classifyInstance(paramInstances.instance(b));
        d += arrayOfDouble[b1];
      } 
      d /= this.m_NumClasses;
      for (b1 = 0; b1 < this.m_NumClasses; b1++)
        paramArrayOfdouble2[b][b1] = paramArrayOfdouble2[b][b1] + (arrayOfDouble[b1] - d) * (this.m_NumClasses - 1) / this.m_NumClasses; 
    } 
    this.m_NumGenerated++;
    for (b = 0; b < paramArrayOfdouble1.length; b++)
      paramArrayOfdouble3[b] = probs(paramArrayOfdouble2[b]); 
  }
  
  public Classifier[][] classifiers() {
    Classifier[][] arrayOfClassifier = new Classifier[this.m_NumClasses][this.m_NumGenerated];
    for (byte b = 0; b < this.m_NumClasses; b++) {
      for (byte b1 = 0; b1 < this.m_NumGenerated; b1++)
        arrayOfClassifier[b][b1] = this.m_Classifiers[b][b1]; 
    } 
    return arrayOfClassifier;
  }
  
  private double[] probs(double[] paramArrayOfdouble) {
    double d1 = -1.7976931348623157E308D;
    for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++) {
      if (paramArrayOfdouble[b1] > d1)
        d1 = paramArrayOfdouble[b1]; 
    } 
    double d2 = 0.0D;
    double[] arrayOfDouble = new double[paramArrayOfdouble.length];
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      arrayOfDouble[b2] = Math.exp(paramArrayOfdouble[b2] - d1);
      d2 += arrayOfDouble[b2];
    } 
    Utils.normalize(arrayOfDouble, d2);
    return arrayOfDouble;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    paramInstance = (Instance)paramInstance.copy();
    paramInstance.setDataset(this.m_NumericClassData);
    double[] arrayOfDouble1 = new double[this.m_NumClasses];
    double[] arrayOfDouble2 = new double[this.m_NumClasses];
    for (byte b = 0; b < this.m_NumGenerated; b++) {
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < this.m_NumClasses; b1++) {
        arrayOfDouble1[b1] = this.m_Classifiers[b1][b].classifyInstance(paramInstance);
        d += arrayOfDouble1[b1];
      } 
      d /= this.m_NumClasses;
      for (b1 = 0; b1 < this.m_NumClasses; b1++)
        arrayOfDouble2[b1] = arrayOfDouble2[b1] + (arrayOfDouble1[b1] - d) * (this.m_NumClasses - 1) / this.m_NumClasses; 
    } 
    return probs(arrayOfDouble2);
  }
  
  public String toSource(String paramString) throws Exception {
    if (this.m_NumGenerated == 0)
      throw new Exception("No model built yet"); 
    if (!(this.m_Classifiers[0][0] instanceof Sourcable))
      throw new Exception("Base learner " + this.m_Classifier.getClass().getName() + " is not Sourcable"); 
    StringBuffer stringBuffer = new StringBuffer("class ");
    stringBuffer.append(paramString).append(" {\n\n");
    stringBuffer.append("  private static double RtoP(double []R, int j) {\n    double Rcenter = 0;\n    for (int i = 0; i < R.length; i++) {\n      Rcenter += R[i];\n    }\n    Rcenter /= R.length;\n    double Rsum = 0;\n    for (int i = 0; i < R.length; i++) {\n      Rsum += Math.exp(R[i] - Rcenter);\n    }\n    return Math.exp(R[j]) / Rsum;\n  }\n\n");
    stringBuffer.append("  public static double classify(Object [] i) {\n    double [] d = distribution(i);\n    double maxV = d[0];\n    int maxI = 0;\n    for (int j = 1; j < " + this.m_NumClasses + "; j++) {\n" + "      if (d[j] > maxV) { maxV = d[j]; maxI = j; }\n" + "    }\n    return (double) maxI;\n  }\n\n");
    stringBuffer.append("  public static double [] distribution(Object [] i) {\n");
    stringBuffer.append("    double [] Fs = new double [" + this.m_NumClasses + "];\n");
    stringBuffer.append("    double [] Fi = new double [" + this.m_NumClasses + "];\n");
    stringBuffer.append("    double Fsum;\n");
    byte b;
    for (b = 0; b < this.m_NumGenerated; b++) {
      stringBuffer.append("    Fsum = 0;\n");
      for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
        stringBuffer.append("    Fi[" + b1 + "] = " + paramString + '_' + b1 + '_' + b + ".classify(i); Fsum += Fi[" + b1 + "];\n"); 
      stringBuffer.append("    Fsum /= " + this.m_NumClasses + ";\n");
      stringBuffer.append("    for (int j = 0; j < " + this.m_NumClasses + "; j++) {");
      stringBuffer.append(" Fs[j] += (Fi[j] - Fsum) * " + (this.m_NumClasses - 1) + " / " + this.m_NumClasses + "; }\n");
    } 
    stringBuffer.append("    double [] dist = new double [" + this.m_NumClasses + "];\n" + "    for (int j = 0; j < " + this.m_NumClasses + "; j++) {\n" + "      dist[j] = RtoP(Fs, j);\n" + "    }\n    return dist;\n");
    stringBuffer.append("  }\n}\n");
    for (b = 0; b < this.m_Classifiers.length; b++) {
      for (byte b1 = 0; b1 < (this.m_Classifiers[b]).length; b1++)
        stringBuffer.append(((Sourcable)this.m_Classifiers[b][b1]).toSource(paramString + '_' + b + '_' + b1)); 
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_NumGenerated == 0) {
      stringBuffer.append("LogitBoost: No model built yet.");
    } else {
      stringBuffer.append("LogitBoost: Base classifiers and their weights: \n");
      for (byte b = 0; b < this.m_NumGenerated; b++) {
        stringBuffer.append("\nIteration " + (b + 1));
        for (byte b1 = 0; b1 < this.m_NumClasses; b1++)
          stringBuffer.append("\n\tClass " + (b1 + 1) + " (" + this.m_ClassAttribute.name() + "=" + this.m_ClassAttribute.value(b1) + ")\n\n" + this.m_Classifiers[b1][b].toString() + "\n"); 
      } 
      stringBuffer.append("Number of performed iterations: " + this.m_NumGenerated + "\n");
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new LogitBoost(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\LogitBoost.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */