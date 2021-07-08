package weka.attributeSelection;

import java.util.BitSet;
import weka.core.Instance;
import weka.core.Instances;

public abstract class HoldOutSubsetEvaluator extends SubsetEvaluator {
  public abstract double evaluateSubset(BitSet paramBitSet, Instances paramInstances) throws Exception;
  
  public abstract double evaluateSubset(BitSet paramBitSet, Instance paramInstance, boolean paramBoolean) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\HoldOutSubsetEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */