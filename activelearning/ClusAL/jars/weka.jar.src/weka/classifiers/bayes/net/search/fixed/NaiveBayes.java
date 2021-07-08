package weka.classifiers.bayes.net.search.fixed;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instances;

public class NaiveBayes extends SearchAlgorithm {
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != paramInstances.classIndex())
        paramBayesNet.getParentSet(b).addParent(paramInstances.classIndex(), paramInstances); 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\fixed\NaiveBayes.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */