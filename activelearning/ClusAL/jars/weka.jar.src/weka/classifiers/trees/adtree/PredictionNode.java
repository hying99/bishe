package weka.classifiers.trees.adtree;

import java.io.Serializable;
import java.util.Enumeration;
import weka.classifiers.trees.ADTree;
import weka.core.FastVector;

public final class PredictionNode implements Serializable, Cloneable {
  private double value;
  
  private FastVector children;
  
  public PredictionNode(double paramDouble) {
    this.value = paramDouble;
    this.children = new FastVector();
  }
  
  public final void setValue(double paramDouble) {
    this.value = paramDouble;
  }
  
  public final double getValue() {
    return this.value;
  }
  
  public final FastVector getChildren() {
    return this.children;
  }
  
  public final Enumeration children() {
    return this.children.elements();
  }
  
  public final void addChild(Splitter paramSplitter, ADTree paramADTree) {
    Splitter splitter = null;
    Enumeration enumeration = children();
    while (enumeration.hasMoreElements()) {
      Splitter splitter1 = enumeration.nextElement();
      if (paramSplitter.equalTo(splitter1)) {
        splitter = splitter1;
        break;
      } 
    } 
    if (splitter == null) {
      Splitter splitter1 = (Splitter)paramSplitter.clone();
      setOrderAddedSubtree(splitter1, paramADTree);
      this.children.addElement(splitter1);
    } else {
      for (byte b = 0; b < paramSplitter.getNumOfBranches(); b++) {
        PredictionNode predictionNode1 = splitter.getChildForBranch(b);
        PredictionNode predictionNode2 = paramSplitter.getChildForBranch(b);
        if (predictionNode1 != null && predictionNode2 != null)
          predictionNode1.merge(predictionNode2, paramADTree); 
      } 
    } 
  }
  
  public final Object clone() {
    PredictionNode predictionNode = new PredictionNode(this.value);
    Enumeration enumeration = this.children.elements();
    while (enumeration.hasMoreElements())
      predictionNode.children.addElement(((Splitter)enumeration.nextElement()).clone()); 
    return predictionNode;
  }
  
  public final void merge(PredictionNode paramPredictionNode, ADTree paramADTree) {
    this.value += paramPredictionNode.value;
    Enumeration enumeration = paramPredictionNode.children();
    while (enumeration.hasMoreElements())
      addChild(enumeration.nextElement(), paramADTree); 
  }
  
  private final void setOrderAddedSubtree(Splitter paramSplitter, ADTree paramADTree) {
    paramSplitter.orderAdded = paramADTree.nextSplitAddedOrder();
    for (byte b = 0; b < paramSplitter.getNumOfBranches(); b++) {
      PredictionNode predictionNode = paramSplitter.getChildForBranch(b);
      if (predictionNode != null) {
        Enumeration enumeration = predictionNode.children();
        while (enumeration.hasMoreElements())
          setOrderAddedSubtree(enumeration.nextElement(), paramADTree); 
      } 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\adtree\PredictionNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */