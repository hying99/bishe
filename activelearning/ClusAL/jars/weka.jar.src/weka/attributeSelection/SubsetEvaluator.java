package weka.attributeSelection;

import java.util.BitSet;

public abstract class SubsetEvaluator extends ASEvaluation {
  public abstract double evaluateSubset(BitSet paramBitSet) throws Exception;
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\SubsetEvaluator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */