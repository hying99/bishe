package weka.classifiers.bayes.net.estimate;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.classifiers.bayes.net.search.local.K2;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Statistics;

public class BMAEstimator extends SimpleEstimator {
  protected boolean m_bUseK2Prior = false;
  
  public void estimateCPTs(BayesNet paramBayesNet) throws Exception {
    Instances instances = paramBayesNet.m_Instances;
    for (byte b = 0; b < instances.numAttributes(); b++) {
      if (paramBayesNet.getParentSet(b).getNrOfParents() > 1)
        throw new Exception("Cannot handle networks with nodes with more than 1 parent (yet)."); 
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
    int i;
    for (i = 0; i < instances.numAttributes(); i++) {
      if (i != instances.classIndex()) {
        double d1 = 0.0D;
        double d2 = 0.0D;
        int j = instances.attribute(i).numValues();
        if (this.m_bUseK2Prior == true) {
          byte b2;
          for (b2 = 0; b2 < j; b2++)
            d1 += Statistics.lnGamma(1.0D + ((DiscreteEstimatorBayes)bayesNet1.m_Distributions[i][0]).getCount(b2)) - Statistics.lnGamma(1.0D); 
          d1 += Statistics.lnGamma(j) - Statistics.lnGamma((j + instances.numInstances()));
          for (b2 = 0; b2 < paramBayesNet.getParentSet(i).getCardinalityOfParents(); b2++) {
            int k = 0;
            for (byte b3 = 0; b3 < j; b3++) {
              double d = ((DiscreteEstimatorBayes)bayesNet2.m_Distributions[i][b2]).getCount(b3);
              d2 += Statistics.lnGamma(1.0D + d) - Statistics.lnGamma(1.0D);
              k = (int)(k + d);
            } 
            d2 += Statistics.lnGamma(j) - Statistics.lnGamma((j + k));
          } 
        } else {
          int k;
          for (k = 0; k < j; k++)
            d1 += Statistics.lnGamma(1.0D / j + ((DiscreteEstimatorBayes)bayesNet1.m_Distributions[i][0]).getCount(k)) - Statistics.lnGamma(1.0D / j); 
          d1 += Statistics.lnGamma(1.0D) - Statistics.lnGamma((1 + instances.numInstances()));
          k = paramBayesNet.getParentSet(i).getCardinalityOfParents();
          for (byte b2 = 0; b2 < k; b2++) {
            int m = 0;
            for (byte b3 = 0; b3 < j; b3++) {
              double d = ((DiscreteEstimatorBayes)bayesNet2.m_Distributions[i][b2]).getCount(b3);
              d2 += Statistics.lnGamma(1.0D / (j * k) + d) - Statistics.lnGamma(1.0D / (j * k));
              m = (int)(m + d);
            } 
            d2 += Statistics.lnGamma(1.0D) - Statistics.lnGamma((1 + m));
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
        for (byte b1 = 0; b1 < paramBayesNet.getParentSet(i).getCardinalityOfParents(); b1++)
          paramBayesNet.m_Distributions[i][b1] = new DiscreteEstimatorFullBayes(instances.attribute(i).numValues(), d1, d2, (DiscreteEstimatorBayes)bayesNet1.m_Distributions[i][0], (DiscreteEstimatorBayes)bayesNet2.m_Distributions[i][b1], this.m_fAlpha); 
      } 
    } 
    i = instances.classIndex();
    paramBayesNet.m_Distributions[i][0] = bayesNet1.m_Distributions[i][0];
  }
  
  public void updateClassifier(BayesNet paramBayesNet, Instance paramInstance) throws Exception {
    throw new Exception("updateClassifier does not apply to BMA estimator");
  }
  
  public void initCPTs(BayesNet paramBayesNet) throws Exception {
    int i = 1;
    for (byte b = 0; b < paramBayesNet.m_Instances.numAttributes(); b++) {
      if (paramBayesNet.getParentSet(b).getCardinalityOfParents() > i)
        i = paramBayesNet.getParentSet(b).getCardinalityOfParents(); 
    } 
    paramBayesNet.m_Distributions = new weka.estimators.Estimator[paramBayesNet.m_Instances.numAttributes()][i];
  }
  
  public boolean isUseK2Prior() {
    return this.m_bUseK2Prior;
  }
  
  public void setUseK2Prior(boolean paramBoolean) {
    this.m_bUseK2Prior = paramBoolean;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\estimate\BMAEstimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */