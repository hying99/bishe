package weka.classifiers.trees.m5;

import weka.core.Instances;

public interface SplitEvaluate {
  SplitEvaluate copy() throws Exception;
  
  void attrSplit(int paramInt, Instances paramInstances) throws Exception;
  
  double maxImpurity();
  
  int position();
  
  int splitAttr();
  
  double splitValue();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\SplitEvaluate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */