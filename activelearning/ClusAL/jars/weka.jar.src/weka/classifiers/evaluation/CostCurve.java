package weka.classifiers.evaluation;

import java.io.InputStreamReader;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class CostCurve {
  public static final String RELATION_NAME = "CostCurve";
  
  public static final String PROB_COST_FUNC_NAME = "Probability Cost Function";
  
  public static final String NORM_EXPECTED_COST_NAME = "Normalized Expected Cost";
  
  public static final String THRESHOLD_NAME = "Threshold";
  
  public Instances getCurve(FastVector paramFastVector) {
    return (paramFastVector.size() == 0) ? null : getCurve(paramFastVector, (((NominalPrediction)paramFastVector.elementAt(0)).distribution()).length - 1);
  }
  
  public Instances getCurve(FastVector paramFastVector, int paramInt) {
    if (paramFastVector.size() == 0 || (((NominalPrediction)paramFastVector.elementAt(0)).distribution()).length <= paramInt)
      return null; 
    ThresholdCurve thresholdCurve = new ThresholdCurve();
    Instances instances1 = thresholdCurve.getCurve(paramFastVector, paramInt);
    Instances instances2 = makeHeader();
    int i = instances1.attribute("False Positive Rate").index();
    int j = instances1.attribute("True Positive Rate").index();
    int k = instances1.attribute("Threshold").index();
    for (byte b = 0; b < instances1.numInstances(); b++) {
      double d1 = instances1.instance(b).value(i);
      double d2 = instances1.instance(b).value(j);
      double d3 = instances1.instance(b).value(k);
      double[] arrayOfDouble = new double[3];
      arrayOfDouble[0] = 0.0D;
      arrayOfDouble[1] = d1;
      arrayOfDouble[2] = d3;
      instances2.add(new Instance(1.0D, arrayOfDouble));
      arrayOfDouble = new double[3];
      arrayOfDouble[0] = 1.0D;
      arrayOfDouble[1] = 1.0D - d2;
      arrayOfDouble[2] = d3;
      instances2.add(new Instance(1.0D, arrayOfDouble));
    } 
    return instances2;
  }
  
  private Instances makeHeader() {
    FastVector fastVector = new FastVector();
    fastVector.addElement(new Attribute("Probability Cost Function"));
    fastVector.addElement(new Attribute("Normalized Expected Cost"));
    fastVector.addElement(new Attribute("Threshold"));
    return new Instances("CostCurve", fastVector, 100);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Instances instances1 = new Instances(new InputStreamReader(System.in));
      instances1.setClassIndex(instances1.numAttributes() - 1);
      CostCurve costCurve = new CostCurve();
      EvaluationUtils evaluationUtils = new EvaluationUtils();
      Logistic logistic = new Logistic();
      FastVector fastVector = new FastVector();
      for (byte b = 0; b < 2; b++) {
        evaluationUtils.setSeed(b);
        fastVector.appendElements(evaluationUtils.getCVPredictions((Classifier)logistic, instances1, 10));
      } 
      Instances instances2 = costCurve.getCurve(fastVector);
      System.out.println(instances2);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\CostCurve.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */