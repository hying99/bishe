package weka.classifiers.bayes.net.estimate;

import java.util.Enumeration;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class SimpleEstimator extends BayesNetEstimator {
  public void estimateCPTs(BayesNet paramBayesNet) throws Exception {
    initCPTs(paramBayesNet);
    Enumeration enumeration = paramBayesNet.m_Instances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      updateClassifier(paramBayesNet, instance);
    } 
  }
  
  public void updateClassifier(BayesNet paramBayesNet, Instance paramInstance) throws Exception {
    for (byte b = 0; b < paramBayesNet.m_Instances.numAttributes(); b++) {
      double d = 0.0D;
      for (byte b1 = 0; b1 < paramBayesNet.getParentSet(b).getNrOfParents(); b1++) {
        int i = paramBayesNet.getParentSet(b).getParent(b1);
        d = d * paramBayesNet.m_Instances.attribute(i).numValues() + paramInstance.value(i);
      } 
      paramBayesNet.m_Distributions[b][(int)d].addValue(paramInstance.value(b), paramInstance.weight());
    } 
  }
  
  public void initCPTs(BayesNet paramBayesNet) throws Exception {
    Instances instances = paramBayesNet.m_Instances;
    int i = 1;
    byte b;
    for (b = 0; b < instances.numAttributes(); b++) {
      if (paramBayesNet.getParentSet(b).getCardinalityOfParents() > i)
        i = paramBayesNet.getParentSet(b).getCardinalityOfParents(); 
    } 
    paramBayesNet.m_Distributions = new weka.estimators.Estimator[instances.numAttributes()][i];
    for (b = 0; b < instances.numAttributes(); b++) {
      for (byte b1 = 0; b1 < paramBayesNet.getParentSet(b).getCardinalityOfParents(); b1++)
        paramBayesNet.m_Distributions[b][b1] = new DiscreteEstimatorBayes(instances.attribute(b).numValues(), this.m_fAlpha); 
    } 
  }
  
  public double[] distributionForInstance(BayesNet paramBayesNet, Instance paramInstance) throws Exception {
    Instances instances = paramBayesNet.m_Instances;
    int i = instances.numClasses();
    double[] arrayOfDouble = new double[i];
    byte b1;
    for (b1 = 0; b1 < i; b1++)
      arrayOfDouble[b1] = 1.0D; 
    for (b1 = 0; b1 < i; b1++) {
      double d1 = 0.0D;
      for (byte b = 0; b < instances.numAttributes(); b++) {
        double d2 = 0.0D;
        for (byte b3 = 0; b3 < paramBayesNet.getParentSet(b).getNrOfParents(); b3++) {
          int j = paramBayesNet.getParentSet(b).getParent(b3);
          if (j == instances.classIndex()) {
            d2 = d2 * i + b1;
          } else {
            d2 = d2 * instances.attribute(j).numValues() + paramInstance.value(j);
          } 
        } 
        if (b == instances.classIndex()) {
          d1 += Math.log(paramBayesNet.m_Distributions[b][(int)d2].getProbability(b1));
        } else {
          d1 += Math.log(paramBayesNet.m_Distributions[b][(int)d2].getProbability(paramInstance.value(b)));
        } 
      } 
      arrayOfDouble[b1] = arrayOfDouble[b1] + d1;
    } 
    double d = arrayOfDouble[0];
    byte b2;
    for (b2 = 0; b2 < i; b2++) {
      if (arrayOfDouble[b2] > d)
        d = arrayOfDouble[b2]; 
    } 
    for (b2 = 0; b2 < i; b2++)
      arrayOfDouble[b2] = Math.exp(arrayOfDouble[b2] - d); 
    Utils.normalize(arrayOfDouble);
    return arrayOfDouble;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\estimate\SimpleEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */