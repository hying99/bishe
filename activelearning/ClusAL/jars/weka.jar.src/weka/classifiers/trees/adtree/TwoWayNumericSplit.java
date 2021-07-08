package weka.classifiers.trees.adtree;

import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class TwoWayNumericSplit extends Splitter {
  private int attIndex;
  
  private double splitPoint;
  
  private PredictionNode[] children;
  
  public TwoWayNumericSplit(int paramInt, double paramDouble) {
    this.attIndex = paramInt;
    this.splitPoint = paramDouble;
    this.children = new PredictionNode[2];
  }
  
  public int getNumOfBranches() {
    return 2;
  }
  
  public int branchInstanceGoesDown(Instance paramInstance) {
    return paramInstance.isMissing(this.attIndex) ? -1 : ((paramInstance.value(this.attIndex) < this.splitPoint) ? 0 : 1);
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
        if (!instance.isMissing(this.attIndex) && instance.value(this.attIndex) < this.splitPoint)
          referenceInstances.addReference(instance); 
      } 
    } else {
      Enumeration enumeration = paramInstances.enumerateInstances();
      while (enumeration.hasMoreElements()) {
        Instance instance = enumeration.nextElement();
        if (!instance.isMissing(this.attIndex) && instance.value(this.attIndex) >= this.splitPoint)
          referenceInstances.addReference(instance); 
      } 
    } 
    return referenceInstances;
  }
  
  public String attributeString(Instances paramInstances) {
    return paramInstances.attribute(this.attIndex).name();
  }
  
  public String comparisonString(int paramInt, Instances paramInstances) {
    return ((paramInt == 0) ? "< " : ">= ") + Utils.doubleToString(this.splitPoint, 3);
  }
  
  public boolean equalTo(Splitter paramSplitter) {
    if (paramSplitter instanceof TwoWayNumericSplit) {
      TwoWayNumericSplit twoWayNumericSplit = (TwoWayNumericSplit)paramSplitter;
      return (this.attIndex == twoWayNumericSplit.attIndex && this.splitPoint == twoWayNumericSplit.splitPoint);
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
    TwoWayNumericSplit twoWayNumericSplit = new TwoWayNumericSplit(this.attIndex, this.splitPoint);
    twoWayNumericSplit.orderAdded = this.orderAdded;
    if (this.children[0] != null)
      twoWayNumericSplit.setChildForBranch(0, (PredictionNode)this.children[0].clone()); 
    if (this.children[1] != null)
      twoWayNumericSplit.setChildForBranch(1, (PredictionNode)this.children[1].clone()); 
    return twoWayNumericSplit;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\adtree\TwoWayNumericSplit.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */