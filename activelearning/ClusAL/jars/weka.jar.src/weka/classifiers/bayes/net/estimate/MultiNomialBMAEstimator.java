package weka.classifiers.bayes.net.estimate;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.classifiers.bayes.net.search.local.K2;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Statistics;
import weka.core.Utils;

public class MultiNomialBMAEstimator extends BayesNetEstimator {
  protected boolean m_bUseK2Prior = true;
  
  public void estimateCPTs(BayesNet paramBayesNet) throws Exception {
    for (byte b = 0; b < paramBayesNet.m_Instances.numAttributes(); b++) {
      if (paramBayesNet.getParentSet(b).getNrOfParents() > 1)
        throw new Exception("Cannot handle networks with nodes with more than 1 parent (yet)."); 
    } 
    Instances instances = new Instances(paramBayesNet.m_Instances);
    while (instances.numInstances() > 0)
      instances.delete(0); 
    int i;
    for (i = instances.numAttributes(); i >= 0; i--) {
      if (i != instances.classIndex()) {
        Attribute attribute = new Attribute(instances.attribute(i).name());
        attribute.addStringValue("0");
        attribute.addStringValue("1");
        instances.deleteAttributeAt(i);
        instances.insertAttributeAt(attribute, i);
      } 
    } 
    for (i = 0; i < paramBayesNet.m_Instances.numInstances(); i++) {
      Instance instance1 = paramBayesNet.m_Instances.instance(i);
      Instance instance2 = new Instance(instances.numAttributes());
      for (byte b1 = 0; b1 < instances.numAttributes(); b1++) {
        if (b1 != instances.classIndex()) {
          if (instance1.value(b1) > 0.0D)
            instance2.setValue(b1, 1.0D); 
        } else {
          instance2.setValue(b1, instance1.value(b1));
        } 
      } 
    } 
    BayesNet bayesNet1 = new BayesNet();
    K2 k2 = new K2();
    k2.setInitAsNaiveBayes(false);
    k2.setMaxNrOfParents(0);
    bayesNet1.setSearchAlgorithm((SearchAlgorithm)k2);
    bayesNet1.buildClassifier(instances);
    BayesNet bayesNet2 = new BayesNet();
    k2.setInitAsNaiveBayes(true);
    k2.setMaxNrOfParents(1);
    bayesNet2.setSearchAlgorithm((SearchAlgorithm)k2);
    bayesNet2.buildClassifier(instances);
    int j;
    for (j = 0; j < instances.numAttributes(); j++) {
      if (j != instances.classIndex()) {
        double d1 = 0.0D;
        double d2 = 0.0D;
        int k = instances.attribute(j).numValues();
        if (this.m_bUseK2Prior == true) {
          byte b2;
          for (b2 = 0; b2 < k; b2++)
            d1 += Statistics.lnGamma(1.0D + ((DiscreteEstimatorBayes)bayesNet1.m_Distributions[j][0]).getCount(b2)) - Statistics.lnGamma(1.0D); 
          d1 += Statistics.lnGamma(k) - Statistics.lnGamma((k + instances.numInstances()));
          for (b2 = 0; b2 < paramBayesNet.getParentSet(j).getCardinalityOfParents(); b2++) {
            int m = 0;
            for (byte b3 = 0; b3 < k; b3++) {
              double d = ((DiscreteEstimatorBayes)bayesNet2.m_Distributions[j][b2]).getCount(b3);
              d2 += Statistics.lnGamma(1.0D + d) - Statistics.lnGamma(1.0D);
              m = (int)(m + d);
            } 
            d2 += Statistics.lnGamma(k) - Statistics.lnGamma((k + m));
          } 
        } else {
          int m;
          for (m = 0; m < k; m++)
            d1 += Statistics.lnGamma(1.0D / k + ((DiscreteEstimatorBayes)bayesNet1.m_Distributions[j][0]).getCount(m)) - Statistics.lnGamma(1.0D / k); 
          d1 += Statistics.lnGamma(1.0D) - Statistics.lnGamma((1 + instances.numInstances()));
          m = paramBayesNet.getParentSet(j).getCardinalityOfParents();
          for (byte b2 = 0; b2 < m; b2++) {
            int n = 0;
            for (byte b3 = 0; b3 < k; b3++) {
              double d = ((DiscreteEstimatorBayes)bayesNet2.m_Distributions[j][b2]).getCount(b3);
              d2 += Statistics.lnGamma(1.0D / (k * m) + d) - Statistics.lnGamma(1.0D / (k * m));
              n = (int)(n + d);
            } 
            d2 += Statistics.lnGamma(1.0D) - Statistics.lnGamma((1 + n));
          } 
        } 
        if (d1 < d2) {
          d2 -= d1;
          d1 = 0.0D;
          d1 = 1.0D / (1.0D + Math.exp(d2));
          d2 = Math.exp(d2) / (1.0D + Math.exp(d2));
        } else {
          d1 -= d2;
          d2 = 0.0D;
          d2 = 1.0D / (1.0D + Math.exp(d1));
          d1 = Math.exp(d1) / (1.0D + Math.exp(d1));
        } 
        for (byte b1 = 0; b1 < paramBayesNet.getParentSet(j).getCardinalityOfParents(); b1++)
          paramBayesNet.m_Distributions[j][b1] = new DiscreteEstimatorFullBayes(instances.attribute(j).numValues(), d1, d2, (DiscreteEstimatorBayes)bayesNet1.m_Distributions[j][0], (DiscreteEstimatorBayes)bayesNet2.m_Distributions[j][b1], this.m_fAlpha); 
      } 
    } 
    j = instances.classIndex();
    paramBayesNet.m_Distributions[j][0] = bayesNet1.m_Distributions[j][0];
  }
  
  public void updateClassifier(BayesNet paramBayesNet, Instance paramInstance) throws Exception {
    throw new Exception("updateClassifier does not apply to BMA estimator");
  }
  
  public void initCPTs(BayesNet paramBayesNet) throws Exception {
    paramBayesNet.m_Distributions = new weka.estimators.Estimator[paramBayesNet.m_Instances.numAttributes()][2];
  }
  
  public boolean isUseK2Prior() {
    return this.m_bUseK2Prior;
  }
  
  public void setUseK2Prior(boolean paramBoolean) {
    this.m_bUseK2Prior = paramBoolean;
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
          d1 += paramInstance.value(b) * Math.log(paramBayesNet.m_Distributions[b][(int)d2].getProbability(paramInstance.value(1)));
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\estimate\MultiNomialBMAEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */