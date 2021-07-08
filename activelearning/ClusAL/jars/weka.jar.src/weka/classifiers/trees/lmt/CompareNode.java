package weka.classifiers.trees.lmt;

import java.util.Comparator;

class CompareNode implements Comparator {
  public int compare(Object paramObject1, Object paramObject2) {
    return (((LMTNode)paramObject1).m_alpha < ((LMTNode)paramObject2).m_alpha) ? -1 : ((((LMTNode)paramObject1).m_alpha > ((LMTNode)paramObject2).m_alpha) ? 1 : 0);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\lmt\CompareNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */