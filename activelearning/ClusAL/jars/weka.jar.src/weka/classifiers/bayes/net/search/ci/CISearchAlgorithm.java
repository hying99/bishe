package weka.classifiers.bayes.net.search.ci;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm;
import weka.core.Instances;

public class CISearchAlgorithm extends LocalScoreSearchAlgorithm {
  BayesNet m_BayesNet;
  
  Instances m_instances;
  
  protected boolean isConditionalIndependent(int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt1);
    while (parentSet.getNrOfParents() > 0)
      parentSet.deleteLastParent(this.m_instances); 
    for (byte b = 0; b < paramInt3; b++)
      parentSet.addParent(paramArrayOfint[b], this.m_instances); 
    double d1 = calcNodeScore(paramInt1);
    double d2 = calcScoreWithExtraParent(paramInt1, paramInt2);
    return (d2 <= d1);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\ci\CISearchAlgorithm.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */