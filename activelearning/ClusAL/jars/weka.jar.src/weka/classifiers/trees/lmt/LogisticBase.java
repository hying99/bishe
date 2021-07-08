package weka.classifiers.trees.lmt;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class LogisticBase extends Classifier implements WeightedInstancesHandler {
  protected Instances m_numericDataHeader;
  
  protected Instances m_numericData;
  
  protected Instances m_train;
  
  protected boolean m_useCrossValidation = true;
  
  protected boolean m_errorOnProbabilities = false;
  
  protected int m_fixedNumIterations = -1;
  
  protected int m_heuristicStop = 50;
  
  protected int m_numRegressions = 0;
  
  protected int m_maxIterations = 500;
  
  protected int m_numClasses;
  
  protected SimpleLinearRegression[][] m_regressions;
  
  protected static int m_numFoldsBoosting = 5;
  
  protected static final double Z_MAX = 3.0D;
  
  public LogisticBase() {}
  
  public LogisticBase(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_train = new Instances(paramInstances);
    this.m_numClasses = this.m_train.numClasses();
    this.m_regressions = initRegressions();
    this.m_numRegressions = 0;
    this.m_numericData = getNumericData(this.m_train);
    this.m_numericDataHeader = new Instances(this.m_numericData, 0);
    if (this.m_fixedNumIterations > 0) {
      performBoosting(this.m_fixedNumIterations);
    } else if (this.m_useCrossValidation) {
      performBoostingCV();
    } else {
      performBoosting();
    } 
    this.m_regressions = selectRegressions(this.m_regressions);
  }
  
  protected void performBoostingCV() throws Exception {
    int i = this.m_maxIterations;
    Instances instances = new Instances(this.m_train);
    instances.stratify(m_numFoldsBoosting);
    double[] arrayOfDouble = new double[this.m_maxIterations + 1];
    int j;
    for (j = 0; j < m_numFoldsBoosting; j++) {
      Instances instances1 = instances.trainCV(m_numFoldsBoosting, j);
      Instances instances2 = instances.testCV(m_numFoldsBoosting, j);
      this.m_numRegressions = 0;
      this.m_regressions = initRegressions();
      int k = performBoosting(instances1, instances2, arrayOfDouble, i);
      if (k < i)
        i = k; 
    } 
    j = getBestIteration(arrayOfDouble, i);
    this.m_numRegressions = 0;
    performBoosting(j);
  }
  
  protected int performBoosting(Instances paramInstances1, Instances paramInstances2, double[] paramArrayOfdouble, int paramInt) throws Exception {
    Instances instances = getNumericData(paramInstances1);
    double[][] arrayOfDouble1 = getYs(paramInstances1);
    double[][] arrayOfDouble2 = getFs(instances);
    double[][] arrayOfDouble3 = getProbs(arrayOfDouble2);
    byte b1 = 0;
    double[] arrayOfDouble = new double[paramInt + 1];
    byte b2 = 0;
    double d = Double.MAX_VALUE;
    if (this.m_errorOnProbabilities) {
      paramArrayOfdouble[0] = paramArrayOfdouble[0] + getMeanAbsoluteError(paramInstances2);
    } else {
      paramArrayOfdouble[0] = paramArrayOfdouble[0] + getErrorRate(paramInstances2);
    } 
    while (b1 < paramInt) {
      boolean bool = performIteration(b1, arrayOfDouble1, arrayOfDouble2, arrayOfDouble3, instances);
      if (bool) {
        this.m_numRegressions = ++b1;
        if (this.m_errorOnProbabilities) {
          paramArrayOfdouble[b1] = paramArrayOfdouble[b1] + getMeanAbsoluteError(paramInstances2);
        } else {
          paramArrayOfdouble[b1] = paramArrayOfdouble[b1] + getErrorRate(paramInstances2);
        } 
        if (b2 > this.m_heuristicStop)
          break; 
        if (paramArrayOfdouble[b1] < d) {
          d = paramArrayOfdouble[b1];
          b2 = 0;
          continue;
        } 
        b2++;
      } 
    } 
    return b1;
  }
  
  protected void performBoosting(int paramInt) throws Exception {
    double[][] arrayOfDouble1 = getYs(this.m_train);
    double[][] arrayOfDouble2 = getFs(this.m_numericData);
    double[][] arrayOfDouble3 = getProbs(arrayOfDouble2);
    byte b = 0;
    while (b < paramInt) {
      boolean bool = performIteration(b, arrayOfDouble1, arrayOfDouble2, arrayOfDouble3, this.m_numericData);
      if (bool)
        b++; 
    } 
    this.m_numRegressions = b;
  }
  
  protected void performBoosting() throws Exception {
    double[][] arrayOfDouble1 = getYs(this.m_train);
    double[][] arrayOfDouble2 = getFs(this.m_numericData);
    double[][] arrayOfDouble3 = getProbs(arrayOfDouble2);
    byte b1 = 0;
    double[] arrayOfDouble = new double[this.m_maxIterations + 1];
    arrayOfDouble[0] = getErrorRate(this.m_train);
    byte b2 = 0;
    double d = Double.MAX_VALUE;
    while (b1 < this.m_maxIterations) {
      boolean bool = performIteration(b1, arrayOfDouble1, arrayOfDouble2, arrayOfDouble3, this.m_numericData);
      if (bool) {
        this.m_numRegressions = ++b1;
        arrayOfDouble[b1] = getErrorRate(this.m_train);
        if (b2 > this.m_heuristicStop)
          break; 
        if (arrayOfDouble[b1] < d) {
          d = arrayOfDouble[b1];
          b2 = 0;
          continue;
        } 
        b2++;
      } 
    } 
    this.m_numRegressions = getBestIteration(arrayOfDouble, b1);
  }
  
  protected double getErrorRate(Instances paramInstances) throws Exception {
    Evaluation evaluation = new Evaluation(paramInstances);
    evaluation.evaluateModel(this, paramInstances);
    return evaluation.errorRate();
  }
  
  protected double getMeanAbsoluteError(Instances paramInstances) throws Exception {
    Evaluation evaluation = new Evaluation(paramInstances);
    evaluation.evaluateModel(this, paramInstances);
    return evaluation.meanAbsoluteError();
  }
  
  protected int getBestIteration(double[] paramArrayOfdouble, int paramInt) {
    double d = paramArrayOfdouble[0];
    byte b1 = 0;
    for (byte b2 = 1; b2 <= paramInt; b2++) {
      if (paramArrayOfdouble[b2] < d) {
        d = paramArrayOfdouble[b2];
        b1 = b2;
      } 
    } 
    return b1;
  }
  
  protected boolean performIteration(int paramInt, double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2, double[][] paramArrayOfdouble3, Instances paramInstances) throws Exception {
    byte b;
    for (b = 0; b < this.m_numClasses; b++) {
      Instances instances = new Instances(paramInstances);
      for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++) {
        double d1 = paramArrayOfdouble3[b1][b];
        double d2 = paramArrayOfdouble1[b1][b];
        double d3 = getZ(d2, d1);
        double d4 = (d2 - d1) / d3;
        Instance instance = instances.instance(b1);
        instance.setValue(instances.classIndex(), d3);
        instance.setWeight(instance.weight() * d4);
      } 
      this.m_regressions[b][paramInt].buildClassifier(instances);
      boolean bool = this.m_regressions[b][paramInt].foundUsefulAttribute();
      if (!bool)
        return false; 
    } 
    for (b = 0; b < paramArrayOfdouble2.length; b++) {
      double[] arrayOfDouble = new double[this.m_numClasses];
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < this.m_numClasses; b1++) {
        arrayOfDouble[b1] = this.m_regressions[b1][paramInt].classifyInstance(paramInstances.instance(b));
        d += arrayOfDouble[b1];
      } 
      d /= this.m_numClasses;
      for (b1 = 0; b1 < this.m_numClasses; b1++)
        paramArrayOfdouble2[b][b1] = paramArrayOfdouble2[b][b1] + (arrayOfDouble[b1] - d) * (this.m_numClasses - 1) / this.m_numClasses; 
    } 
    for (b = 0; b < paramArrayOfdouble1.length; b++)
      paramArrayOfdouble3[b] = probs(paramArrayOfdouble2[b]); 
    return true;
  }
  
  protected SimpleLinearRegression[][] initRegressions() {
    SimpleLinearRegression[][] arrayOfSimpleLinearRegression = new SimpleLinearRegression[this.m_numClasses][this.m_maxIterations];
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < this.m_maxIterations; b1++) {
        arrayOfSimpleLinearRegression[b][b1] = new SimpleLinearRegression();
        arrayOfSimpleLinearRegression[b][b1].setSuppressErrorMessage(true);
      } 
    } 
    return arrayOfSimpleLinearRegression;
  }
  
  protected Instances getNumericData(Instances paramInstances) throws Exception {
    Instances instances = new Instances(paramInstances);
    int i = instances.classIndex();
    instances.setClassIndex(-1);
    instances.deleteAttributeAt(i);
    instances.insertAttributeAt(new Attribute("'pseudo class'"), i);
    instances.setClassIndex(i);
    return instances;
  }
  
  protected SimpleLinearRegression[][] selectRegressions(SimpleLinearRegression[][] paramArrayOfSimpleLinearRegression) {
    SimpleLinearRegression[][] arrayOfSimpleLinearRegression = new SimpleLinearRegression[this.m_numClasses][this.m_numRegressions];
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < this.m_numRegressions; b1++)
        arrayOfSimpleLinearRegression[b][b1] = paramArrayOfSimpleLinearRegression[b][b1]; 
    } 
    return arrayOfSimpleLinearRegression;
  }
  
  protected double getZ(double paramDouble1, double paramDouble2) {
    double d;
    if (paramDouble1 == 1.0D) {
      d = 1.0D / paramDouble2;
      if (d > 3.0D)
        d = 3.0D; 
    } else {
      d = -1.0D / (1.0D - paramDouble2);
      if (d < -3.0D)
        d = -3.0D; 
    } 
    return d;
  }
  
  protected double[][] getZs(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) {
    double[][] arrayOfDouble = new double[paramArrayOfdouble1.length][this.m_numClasses];
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < paramArrayOfdouble1.length; b1++)
        arrayOfDouble[b1][b] = getZ(paramArrayOfdouble2[b1][b], paramArrayOfdouble1[b1][b]); 
    } 
    return arrayOfDouble;
  }
  
  protected double[][] getWs(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) {
    double[][] arrayOfDouble = new double[paramArrayOfdouble1.length][this.m_numClasses];
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < paramArrayOfdouble1.length; b1++) {
        double d = getZ(paramArrayOfdouble2[b1][b], paramArrayOfdouble1[b1][b]);
        arrayOfDouble[b1][b] = (paramArrayOfdouble2[b1][b] - paramArrayOfdouble1[b1][b]) / d;
      } 
    } 
    return arrayOfDouble;
  }
  
  protected double[] probs(double[] paramArrayOfdouble) {
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
  
  protected double[][] getYs(Instances paramInstances) {
    double[][] arrayOfDouble = new double[paramInstances.numInstances()][this.m_numClasses];
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++)
        arrayOfDouble[b1][b] = (paramInstances.instance(b1).classValue() == b) ? 1.0D : 0.0D; 
    } 
    return arrayOfDouble;
  }
  
  protected double[] getFs(Instance paramInstance) throws Exception {
    double[] arrayOfDouble1 = new double[this.m_numClasses];
    double[] arrayOfDouble2 = new double[this.m_numClasses];
    for (byte b = 0; b < this.m_numRegressions; b++) {
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < this.m_numClasses; b1++) {
        arrayOfDouble1[b1] = this.m_regressions[b1][b].classifyInstance(paramInstance);
        d += arrayOfDouble1[b1];
      } 
      d /= this.m_numClasses;
      for (b1 = 0; b1 < this.m_numClasses; b1++)
        arrayOfDouble2[b1] = arrayOfDouble2[b1] + (arrayOfDouble1[b1] - d) * (this.m_numClasses - 1) / this.m_numClasses; 
    } 
    return arrayOfDouble2;
  }
  
  protected double[][] getFs(Instances paramInstances) throws Exception {
    double[][] arrayOfDouble = new double[paramInstances.numInstances()][];
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      arrayOfDouble[b] = getFs(paramInstances.instance(b)); 
    return arrayOfDouble;
  }
  
  protected double[][] getProbs(double[][] paramArrayOfdouble) {
    int i = paramArrayOfdouble.length;
    double[][] arrayOfDouble = new double[i][];
    for (byte b = 0; b < i; b++)
      arrayOfDouble[b] = probs(paramArrayOfdouble[b]); 
    return arrayOfDouble;
  }
  
  protected double logLikelihood(double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) {
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble1.length; b++) {
      for (byte b1 = 0; b1 < this.m_numClasses; b1++) {
        if (paramArrayOfdouble1[b][b1] == 1.0D)
          d -= Math.log(paramArrayOfdouble2[b][b1]); 
      } 
    } 
    return d / paramArrayOfdouble1.length;
  }
  
  public int[][] getUsedAttributes() {
    int[][] arrayOfInt = new int[this.m_numClasses][];
    double[][] arrayOfDouble = getCoefficients();
    for (byte b = 0; b < this.m_numClasses; b++) {
      boolean[] arrayOfBoolean = new boolean[this.m_numericDataHeader.numAttributes()];
      byte b1;
      for (b1 = 0; b1 < arrayOfBoolean.length; b1++) {
        if (!Utils.eq(arrayOfDouble[b][b1 + 1], 0.0D))
          arrayOfBoolean[b1] = true; 
      } 
      b1 = 0;
      for (byte b2 = 0; b2 < this.m_numericDataHeader.numAttributes(); b2++) {
        if (arrayOfBoolean[b2])
          b1++; 
      } 
      int[] arrayOfInt1 = new int[b1];
      byte b3 = 0;
      for (byte b4 = 0; b4 < this.m_numericDataHeader.numAttributes(); b4++) {
        if (arrayOfBoolean[b4]) {
          arrayOfInt1[b3] = b4;
          b3++;
        } 
      } 
      arrayOfInt[b] = arrayOfInt1;
    } 
    return arrayOfInt;
  }
  
  public int getNumRegressions() {
    return this.m_numRegressions;
  }
  
  public void setMaxIterations(int paramInt) {
    this.m_maxIterations = paramInt;
  }
  
  public void setHeuristicStop(int paramInt) {
    this.m_heuristicStop = paramInt;
  }
  
  public int getMaxIterations() {
    return this.m_maxIterations;
  }
  
  protected double[][] getCoefficients() {
    double[][] arrayOfDouble = new double[this.m_numClasses][this.m_numericDataHeader.numAttributes() + 1];
    for (byte b = 0; b < this.m_numClasses; b++) {
      for (byte b1 = 0; b1 < this.m_numRegressions; b1++) {
        double d1 = this.m_regressions[b][b1].getSlope();
        double d2 = this.m_regressions[b][b1].getIntercept();
        int i = this.m_regressions[b][b1].getAttributeIndex();
        arrayOfDouble[b][0] = arrayOfDouble[b][0] + d2;
        arrayOfDouble[b][i + 1] = arrayOfDouble[b][i + 1] + d1;
      } 
    } 
    return arrayOfDouble;
  }
  
  public double percentAttributesUsed() {
    boolean[] arrayOfBoolean = new boolean[this.m_numericDataHeader.numAttributes()];
    double[][] arrayOfDouble = getCoefficients();
    for (byte b1 = 0; b1 < this.m_numClasses; b1++) {
      for (byte b = 1; b < this.m_numericDataHeader.numAttributes() + 1; b++) {
        if (!Utils.eq(arrayOfDouble[b1][b], 0.0D))
          arrayOfBoolean[b - 1] = true; 
      } 
    } 
    double d = 0.0D;
    for (byte b2 = 0; b2 < arrayOfBoolean.length; b2++) {
      if (arrayOfBoolean[b2])
        d++; 
    } 
    return d / (this.m_numericDataHeader.numAttributes() - 1) * 100.0D;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    int[][] arrayOfInt = getUsedAttributes();
    double[][] arrayOfDouble = getCoefficients();
    for (byte b = 0; b < this.m_numClasses; b++) {
      stringBuffer.append("\nClass " + b + " :\n");
      stringBuffer.append(Utils.doubleToString(arrayOfDouble[b][0], 4, 2) + " + \n");
      for (byte b1 = 0; b1 < (arrayOfInt[b]).length; b1++) {
        stringBuffer.append("[" + this.m_numericDataHeader.attribute(arrayOfInt[b][b1]).name() + "]");
        stringBuffer.append(" * " + Utils.doubleToString(arrayOfDouble[b][arrayOfInt[b][b1] + 1], 4, 2));
        if (b1 != (arrayOfInt[b]).length - 1)
          stringBuffer.append(" +"); 
        stringBuffer.append("\n");
      } 
    } 
    return new String(stringBuffer);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    paramInstance = (Instance)paramInstance.copy();
    paramInstance.setDataset(this.m_numericDataHeader);
    return probs(getFs(paramInstance));
  }
  
  public void cleanup() {
    this.m_train = new Instances(this.m_train, 0);
    this.m_numericData = null;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\lmt\LogisticBase.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */