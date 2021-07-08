package weka.classifiers.evaluation;

import java.util.Random;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class EvaluationUtils {
  private int m_Seed = 1;
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public FastVector getCVPredictions(Classifier paramClassifier, Instances paramInstances, int paramInt) throws Exception {
    FastVector fastVector = new FastVector();
    Instances instances = new Instances(paramInstances);
    Random random = new Random(this.m_Seed);
    instances.randomize(random);
    if (instances.classAttribute().isNominal() && paramInt > 1)
      instances.stratify(paramInt); 
    boolean bool = false;
    for (byte b = 0; b < paramInt; b++) {
      Instances instances1 = instances.trainCV(paramInt, b, random);
      Instances instances2 = instances.testCV(paramInt, b);
      FastVector fastVector1 = getTrainTestPredictions(paramClassifier, instances1, instances2);
      fastVector.appendElements(fastVector1);
    } 
    return fastVector;
  }
  
  public FastVector getTrainTestPredictions(Classifier paramClassifier, Instances paramInstances1, Instances paramInstances2) throws Exception {
    paramClassifier.buildClassifier(paramInstances1);
    return getTestPredictions(paramClassifier, paramInstances2);
  }
  
  public FastVector getTestPredictions(Classifier paramClassifier, Instances paramInstances) throws Exception {
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      if (!paramInstances.instance(b).classIsMissing())
        fastVector.addElement(getPrediction(paramClassifier, paramInstances.instance(b))); 
    } 
    return fastVector;
  }
  
  public Prediction getPrediction(Classifier paramClassifier, Instance paramInstance) throws Exception {
    double d = paramInstance.classValue();
    double[] arrayOfDouble = paramClassifier.distributionForInstance(paramInstance);
    return (Prediction)(paramInstance.classAttribute().isNominal() ? new NominalPrediction(d, arrayOfDouble, paramInstance.weight()) : new NumericPrediction(d, arrayOfDouble[0], paramInstance.weight()));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\EvaluationUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */