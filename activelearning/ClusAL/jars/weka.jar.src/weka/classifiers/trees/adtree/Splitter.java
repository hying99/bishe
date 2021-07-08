package weka.classifiers.trees.adtree;

import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Splitter implements Serializable, Cloneable {
  public int orderAdded;
  
  public abstract int getNumOfBranches();
  
  public abstract int branchInstanceGoesDown(Instance paramInstance);
  
  public abstract ReferenceInstances instancesDownBranch(int paramInt, Instances paramInstances);
  
  public abstract String attributeString(Instances paramInstances);
  
  public abstract String comparisonString(int paramInt, Instances paramInstances);
  
  public abstract boolean equalTo(Splitter paramSplitter);
  
  public abstract void setChildForBranch(int paramInt, PredictionNode paramPredictionNode);
  
  public abstract PredictionNode getChildForBranch(int paramInt);
  
  public abstract Object clone();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\adtree\Splitter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */