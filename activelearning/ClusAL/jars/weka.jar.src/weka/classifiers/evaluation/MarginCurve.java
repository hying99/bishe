package weka.classifiers.evaluation;

import java.io.InputStreamReader;
import weka.classifiers.Classifier;
import weka.classifiers.meta.LogitBoost;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class MarginCurve {
  public Instances getCurve(FastVector paramFastVector) {
    if (paramFastVector.size() == 0)
      return null; 
    Instances instances = makeHeader();
    double[] arrayOfDouble = getMargins(paramFastVector);
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    int i = 0;
    int j = 0;
    instances.add(makeInstance(-1.0D, i, j));
    for (byte b = 0; b < arrayOfInt.length; b++) {
      double d1 = arrayOfDouble[arrayOfInt[b]];
      double d2 = ((NominalPrediction)paramFastVector.elementAt(arrayOfInt[b])).weight();
      j = (int)(j + d2);
      i = (int)(i + d2);
      instances.add(makeInstance(d1, i, j));
      i = 0;
    } 
    return instances;
  }
  
  private double[] getMargins(FastVector paramFastVector) {
    double[] arrayOfDouble = new double[paramFastVector.size()];
    for (byte b = 0; b < arrayOfDouble.length; b++) {
      NominalPrediction nominalPrediction = (NominalPrediction)paramFastVector.elementAt(b);
      arrayOfDouble[b] = nominalPrediction.margin();
    } 
    return arrayOfDouble;
  }
  
  private Instances makeHeader() {
    FastVector fastVector = new FastVector();
    fastVector.addElement(new Attribute("Margin"));
    fastVector.addElement(new Attribute("Current"));
    fastVector.addElement(new Attribute("Cumulative"));
    return new Instances("MarginCurve", fastVector, 100);
  }
  
  private Instance makeInstance(double paramDouble, int paramInt1, int paramInt2) {
    byte b = 0;
    double[] arrayOfDouble = new double[3];
    arrayOfDouble[b++] = paramDouble;
    arrayOfDouble[b++] = paramInt1;
    arrayOfDouble[b++] = paramInt2;
    return new Instance(1.0D, arrayOfDouble);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Utils.SMALL = 0.0D;
      Instances instances1 = new Instances(new InputStreamReader(System.in));
      instances1.setClassIndex(instances1.numAttributes() - 1);
      MarginCurve marginCurve = new MarginCurve();
      EvaluationUtils evaluationUtils = new EvaluationUtils();
      LogitBoost logitBoost = new LogitBoost();
      logitBoost.setNumIterations(20);
      FastVector fastVector = evaluationUtils.getTrainTestPredictions((Classifier)logitBoost, instances1, instances1);
      Instances instances2 = marginCurve.getCurve(fastVector);
      System.out.println(instances2);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\MarginCurve.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */