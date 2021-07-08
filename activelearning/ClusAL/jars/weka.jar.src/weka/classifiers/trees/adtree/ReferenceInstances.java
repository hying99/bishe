package weka.classifiers.trees.adtree;

import weka.core.Instance;
import weka.core.Instances;

public class ReferenceInstances extends Instances {
  public ReferenceInstances(Instances paramInstances, int paramInt) {
    super(paramInstances, paramInt);
  }
  
  public final void addReference(Instance paramInstance) {
    this.m_Instances.addElement(paramInstance);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\adtree\ReferenceInstances.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */