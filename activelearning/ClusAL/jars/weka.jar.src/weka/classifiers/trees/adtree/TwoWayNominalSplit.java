package weka.classifiers.trees.adtree;

import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class TwoWayNominalSplit extends Splitter {
  private int attIndex;
  
  private int trueSplitValue;
  
  private PredictionNode[] children;
  
  public TwoWayNominalSplit(int paramInt1, int paramInt2) {
    this.attIndex = paramInt1;
    this.trueSplitValue = paramInt2;
    this.children = new PredictionNode[2];
  }
  
  public int getNumOfBranches() {
    return 2;
  }
  
  public int branchInstanceGoesDown(Instance paramInstance) {
    return paramInstance.isMissing(this.attIndex) ? -1 : ((paramInstance.value(this.attIndex) == this.trueSplitValue) ? 0 : 1);
  }
  
  public ReferenceInstances instancesDownBranch(int paramInt, Instances paramInstances) {
    ReferenceInstances referenceInstances = new ReferenceInstances(paramInstances, 1);
    if (paramInt == -1) {
      Enumeration enumeration = paramInstances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (instance.isMissing(this.attIndex))
          referenceInstances.addReference(instance); 
      } 
    } else if (paramInt == 0) {
      Enumeration enumeration = paramInstances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (!instance.isMissing(this.attIndex) && instance.value(this.attIndex) == this.trueSplitValue)
          referenceInstances.addReference(instance); 
      } 
    } else {
      Enumeration enumeration = paramInstances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (!instance.isMissing(this.attIndex) && instance.value(this.attIndex) != this.trueSplitValue)
          referenceInstances.addReference(instance); 
      } 
    } 
    return referenceInstances;
  }
  
  public String attributeString(Instances paramInstances) {
    return paramInstances.attribute(this.attIndex).name();
  }
  
  public String comparisonString(int paramInt, Instances paramInstances) {
    Attribute attribute = paramInstances.attribute(this.attIndex);
    return (attribute.numValues() != 2) ? (((paramInt == 0) ? "= " : "!= ") + attribute.value(this.trueSplitValue)) : ("= " + ((paramInt == 0) ? attribute.value(this.trueSplitValue) : attribute.value((this.trueSplitValue == 0) ? 1 : 0)));
  }
  
  public boolean equalTo(Splitter paramSplitter) {
    if (paramSplitter instanceof TwoWayNominalSplit) {
      TwoWayNominalSplit twoWayNominalSplit = (TwoWayNominalSplit)paramSplitter;
      return (this.attIndex == twoWayNominalSplit.attIndex && this.trueSplitValue == twoWayNominalSplit.trueSplitValue);
    } 
    return false;
  }
  
  public void setChildForBranch(int paramInt, PredictionNode paramPredictionNode) {
    this.children[paramInt] = paramPredictionNode;
  }
  
  public PredictionNode getChildForBranch(int paramInt) {
    return this.children[paramInt];
  }
  
  public Object clone() {
    TwoWayNominalSplit twoWayNominalSplit = new TwoWayNominalSplit(this.attIndex, this.trueSplitValue);
    twoWayNominalSplit.orderAdded = this.orderAdded;
    if (this.children[0] != null)
      twoWayNominalSplit.setChildForBranch(0, (PredictionNode)this.children[0].clone()); 
    if (this.children[1] != null)
      twoWayNominalSplit.setChildForBranch(1, (PredictionNode)this.children[1].clone()); 
    return twoWayNominalSplit;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\adtree\TwoWayNominalSplit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */