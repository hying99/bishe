package weka.classifiers.evaluation;

import java.io.InputStreamReader;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class ThresholdCurve {
  public static final String RELATION_NAME = "ThresholdCurve";
  
  public static final String TRUE_POS_NAME = "True Positives";
  
  public static final String FALSE_NEG_NAME = "False Negatives";
  
  public static final String FALSE_POS_NAME = "False Positives";
  
  public static final String TRUE_NEG_NAME = "True Negatives";
  
  public static final String FP_RATE_NAME = "False Positive Rate";
  
  public static final String TP_RATE_NAME = "True Positive Rate";
  
  public static final String PRECISION_NAME = "Precision";
  
  public static final String RECALL_NAME = "Recall";
  
  public static final String FALLOUT_NAME = "Fallout";
  
  public static final String FMEASURE_NAME = "FMeasure";
  
  public static final String THRESHOLD_NAME = "Threshold";
  
  public Instances getCurve(FastVector paramFastVector) {
    return (paramFastVector.size() == 0) ? null : getCurve(paramFastVector, (((NominalPrediction)paramFastVector.elementAt(0)).distribution()).length - 1);
  }
  
  public Instances getCurve(FastVector paramFastVector, int paramInt) {
    if (paramFastVector.size() == 0 || (((NominalPrediction)paramFastVector.elementAt(0)).distribution()).length <= paramInt)
      return null; 
    double d1 = 0.0D;
    double d2 = 0.0D;
    double[] arrayOfDouble = getProbabilities(paramFastVector, paramInt);
    for (byte b1 = 0; b1 < arrayOfDouble.length; b1++) {
      NominalPrediction nominalPrediction = (NominalPrediction)paramFastVector.elementAt(b1);
      if (nominalPrediction.actual() == Prediction.MISSING_VALUE) {
        System.err.println(getClass().getName() + " Skipping prediction with missing class value");
      } else if (nominalPrediction.weight() < 0.0D) {
        System.err.println(getClass().getName() + " Skipping prediction with negative weight");
      } else if (nominalPrediction.actual() == paramInt) {
        d1 += nominalPrediction.weight();
      } else {
        d2 += nominalPrediction.weight();
      } 
    } 
    Instances instances = makeHeader();
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    TwoClassStats twoClassStats = new TwoClassStats(d1, d2, 0.0D, 0.0D);
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
      if (b2 == 0 || arrayOfDouble[arrayOfInt[b2]] > d3) {
        twoClassStats.setTruePositive(twoClassStats.getTruePositive() - d4);
        twoClassStats.setFalseNegative(twoClassStats.getFalseNegative() + d4);
        twoClassStats.setFalsePositive(twoClassStats.getFalsePositive() - d5);
        twoClassStats.setTrueNegative(twoClassStats.getTrueNegative() + d5);
        d3 = arrayOfDouble[arrayOfInt[b2]];
        instances.add(makeInstance(twoClassStats, d3));
        d4 = 0.0D;
        d5 = 0.0D;
        if (b2 == arrayOfInt.length - 1)
          break; 
      } 
      NominalPrediction nominalPrediction = (NominalPrediction)paramFastVector.elementAt(arrayOfInt[b2]);
      if (nominalPrediction.actual() == Prediction.MISSING_VALUE) {
        System.err.println(getClass().getName() + " Skipping prediction with missing class value");
      } else if (nominalPrediction.weight() < 0.0D) {
        System.err.println(getClass().getName() + " Skipping prediction with negative weight");
      } else if (nominalPrediction.actual() == paramInt) {
        d4 += nominalPrediction.weight();
      } else {
        d5 += nominalPrediction.weight();
      } 
    } 
    return instances;
  }
  
  public static double getNPointPrecision(Instances paramInstances, int paramInt) {
    if (!"ThresholdCurve".equals(paramInstances.relationName()) || paramInstances.numInstances() == 0)
      return Double.NaN; 
    int i = paramInstances.attribute("Recall").index();
    int j = paramInstances.attribute("Precision").index();
    double[] arrayOfDouble = paramInstances.attributeToDoubleArray(i);
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    double d1 = 1.0D / (paramInt - 1);
    double d2 = 0.0D;
    for (byte b = 0; b < paramInt; b++) {
      int k = binarySearch(arrayOfInt, arrayOfDouble, b * d1);
      double d3 = arrayOfDouble[arrayOfInt[k]];
      double d4 = paramInstances.instance(arrayOfInt[k]).value(j);
      while (k != 0 && k < arrayOfInt.length - 1) {
        double d = arrayOfDouble[arrayOfInt[++k]];
        if (d != d3) {
          double d5 = paramInstances.instance(arrayOfInt[k]).value(j);
          double d6 = (d5 - d4) / (d - d3);
          double d7 = d4 - d3 * d6;
          d4 = d1 * b * d6 + d7;
          break;
        } 
      } 
      d2 += d4;
    } 
    return d2 / paramInt;
  }
  
  public static double getROCArea(Instances paramInstances) {
    int i = paramInstances.numInstances();
    if (!"ThresholdCurve".equals(paramInstances.relationName()) || i == 0)
      return Double.NaN; 
    int j = paramInstances.attribute("True Positives").index();
    int k = paramInstances.attribute("False Positives").index();
    double[] arrayOfDouble1 = paramInstances.attributeToDoubleArray(j);
    double[] arrayOfDouble2 = paramInstances.attributeToDoubleArray(k);
    double d1 = arrayOfDouble1[0];
    double d2 = arrayOfDouble2[0];
    double d3 = 0.0D;
    double d4 = 1.0D;
    double d5 = 1.0D;
    for (byte b = 1; b < i; b++) {
      double d6 = arrayOfDouble2[b] / d2;
      double d7 = arrayOfDouble1[b] / d1;
      double d8 = (d7 + d5) * (d4 - d6) / 2.0D;
      d3 += d8;
      d4 = d6;
      d5 = d7;
    } 
    if (d4 > 0.0D) {
      double d = d5 * d4 / 2.0D;
      d3 += d;
    } 
    return d3;
  }
  
  public static int getThresholdInstance(Instances paramInstances, double paramDouble) {
    if (!"ThresholdCurve".equals(paramInstances.relationName()) || paramInstances.numInstances() == 0 || paramDouble < 0.0D || paramDouble > 1.0D)
      return -1; 
    if (paramInstances.numInstances() == 1)
      return 0; 
    double[] arrayOfDouble = paramInstances.attributeToDoubleArray(paramInstances.numAttributes() - 1);
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    return binarySearch(arrayOfInt, arrayOfDouble, paramDouble);
  }
  
  private static int binarySearch(int[] paramArrayOfint, double[] paramArrayOfdouble, double paramDouble) {
    int i = 0;
    int j = paramArrayOfint.length - 1;
    while (j - i > 1) {
      int k = i + (j - i) / 2;
      double d = paramArrayOfdouble[paramArrayOfint[k]];
      if (paramDouble > d) {
        i = k;
        continue;
      } 
      if (paramDouble < d) {
        j = k;
        continue;
      } 
      while (k > 0 && paramArrayOfdouble[paramArrayOfint[k - 1]] == paramDouble)
        k--; 
      return k;
    } 
    return i;
  }
  
  private double[] getProbabilities(FastVector paramFastVector, int paramInt) {
    double[] arrayOfDouble = new double[paramFastVector.size()];
    for (byte b = 0; b < arrayOfDouble.length; b++) {
      NominalPrediction nominalPrediction = (NominalPrediction)paramFastVector.elementAt(b);
      arrayOfDouble[b] = nominalPrediction.distribution()[paramInt];
    } 
    return arrayOfDouble;
  }
  
  private Instances makeHeader() {
    FastVector fastVector = new FastVector();
    fastVector.addElement(new Attribute("True Positives"));
    fastVector.addElement(new Attribute("False Negatives"));
    fastVector.addElement(new Attribute("False Positives"));
    fastVector.addElement(new Attribute("True Negatives"));
    fastVector.addElement(new Attribute("False Positive Rate"));
    fastVector.addElement(new Attribute("True Positive Rate"));
    fastVector.addElement(new Attribute("Precision"));
    fastVector.addElement(new Attribute("Recall"));
    fastVector.addElement(new Attribute("Fallout"));
    fastVector.addElement(new Attribute("FMeasure"));
    fastVector.addElement(new Attribute("Threshold"));
    return new Instances("ThresholdCurve", fastVector, 100);
  }
  
  private Instance makeInstance(TwoClassStats paramTwoClassStats, double paramDouble) {
    byte b = 0;
    double[] arrayOfDouble = new double[11];
    arrayOfDouble[b++] = paramTwoClassStats.getTruePositive();
    arrayOfDouble[b++] = paramTwoClassStats.getFalseNegative();
    arrayOfDouble[b++] = paramTwoClassStats.getFalsePositive();
    arrayOfDouble[b++] = paramTwoClassStats.getTrueNegative();
    arrayOfDouble[b++] = paramTwoClassStats.getFalsePositiveRate();
    arrayOfDouble[b++] = paramTwoClassStats.getTruePositiveRate();
    arrayOfDouble[b++] = paramTwoClassStats.getPrecision();
    arrayOfDouble[b++] = paramTwoClassStats.getRecall();
    arrayOfDouble[b++] = paramTwoClassStats.getFallout();
    arrayOfDouble[b++] = paramTwoClassStats.getFMeasure();
    arrayOfDouble[b++] = paramDouble;
    return new Instance(1.0D, arrayOfDouble);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Instances instances1 = new Instances(new InputStreamReader(System.in));
      instances1.setClassIndex(instances1.numAttributes() - 1);
      ThresholdCurve thresholdCurve = new ThresholdCurve();
      EvaluationUtils evaluationUtils = new EvaluationUtils();
      Logistic logistic = new Logistic();
      FastVector fastVector = new FastVector();
      for (byte b = 0; b < 2; b++) {
        evaluationUtils.setSeed(b);
        fastVector.appendElements(evaluationUtils.getCVPredictions((Classifier)logistic, instances1, 10));
      } 
      Instances instances2 = thresholdCurve.getCurve(fastVector);
      System.out.println(instances2);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\ThresholdCurve.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */