package weka.classifiers.trees.j48;

import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;

public class NBTreeClassifierTree extends ClassifierTree {
  public NBTreeClassifierTree(ModelSelection paramModelSelection) {
    super(paramModelSelection);
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("Class is numeric!"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    buildTree(paramInstances, false);
    cleanup(new Instances(paramInstances, 0));
    assignIDs(-1);
  }
  
  protected ClassifierTree getNewTree(Instances paramInstances) throws Exception {
    NBTreeClassifierTree nBTreeClassifierTree = new NBTreeClassifierTree(this.m_toSelectModel);
    nBTreeClassifierTree.buildTree(paramInstances, false);
    return nBTreeClassifierTree;
  }
  
  protected ClassifierTree getNewTree(Instances paramInstances1, Instances paramInstances2) throws Exception {
    NBTreeClassifierTree nBTreeClassifierTree = new NBTreeClassifierTree(this.m_toSelectModel);
    nBTreeClassifierTree.buildTree(paramInstances1, paramInstances2, false);
    return nBTreeClassifierTree;
  }
  
  public String printLeafModels() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_isLeaf) {
      stringBuffer.append("\nLeaf number: " + this.m_id + " ");
      stringBuffer.append(this.m_localModel.toString());
      stringBuffer.append("\n");
    } else {
      for (byte b = 0; b < this.m_sons.length; b++)
        stringBuffer.append(((NBTreeClassifierTree)this.m_sons[b]).printLeafModels()); 
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    try {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_isLeaf) {
        stringBuffer.append(": NB");
        stringBuffer.append(this.m_id);
      } else {
        dumpTreeNB(0, stringBuffer);
      } 
      stringBuffer.append("\n" + printLeafModels());
      stringBuffer.append("\n\nNumber of Leaves  : \t" + numLeaves() + "\n");
      stringBuffer.append("\nSize of the tree : \t" + numNodes() + "\n");
      return stringBuffer.toString();
    } catch (Exception exception) {
      exception.printStackTrace();
      return "Can't print nb tree.";
    } 
  }
  
  private void dumpTreeNB(int paramInt, StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("\n");
      for (byte b1 = 0; b1 < paramInt; b1++)
        paramStringBuffer.append("|   "); 
      paramStringBuffer.append(this.m_localModel.leftSide(this.m_train));
      paramStringBuffer.append(this.m_localModel.rightSide(b, this.m_train));
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append(": NB ");
        paramStringBuffer.append((this.m_sons[b]).m_id);
      } else {
        ((NBTreeClassifierTree)this.m_sons[b]).dumpTreeNB(paramInt + 1, paramStringBuffer);
      } 
    } 
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("digraph J48Tree {\n");
    if (this.m_isLeaf) {
      stringBuffer.append("N" + this.m_id + " [label=\"" + "NB model" + "\" " + "shape=box style=filled ");
      if (this.m_train != null && this.m_train.numInstances() > 0) {
        stringBuffer.append("data =\n" + this.m_train + "\n");
        stringBuffer.append(",\n");
      } 
      stringBuffer.append("]\n");
    } else {
      stringBuffer.append("N" + this.m_id + " [label=\"" + this.m_localModel.leftSide(this.m_train) + "\" ");
      if (this.m_train != null && this.m_train.numInstances() > 0) {
        stringBuffer.append("data =\n" + this.m_train + "\n");
        stringBuffer.append(",\n");
      } 
      stringBuffer.append("]\n");
      graphTree(stringBuffer);
    } 
    return stringBuffer.toString() + "}\n";
  }
  
  private void graphTree(StringBuffer paramStringBuffer) throws Exception {
    for (byte b = 0; b < this.m_sons.length; b++) {
      paramStringBuffer.append("N" + this.m_id + "->" + "N" + (this.m_sons[b]).m_id + " [label=\"" + this.m_localModel.rightSide(b, this.m_train).trim() + "\"]\n");
      if ((this.m_sons[b]).m_isLeaf) {
        paramStringBuffer.append("N" + (this.m_sons[b]).m_id + " [label=\"" + "NB Model" + "\" " + "shape=box style=filled ");
        if (this.m_train != null && this.m_train.numInstances() > 0) {
          paramStringBuffer.append("data =\n" + (this.m_sons[b]).m_train + "\n");
          paramStringBuffer.append(",\n");
        } 
        paramStringBuffer.append("]\n");
      } else {
        paramStringBuffer.append("N" + (this.m_sons[b]).m_id + " [label=\"" + (this.m_sons[b]).m_localModel.leftSide(this.m_train) + "\" ");
        if (this.m_train != null && this.m_train.numInstances() > 0) {
          paramStringBuffer.append("data =\n" + (this.m_sons[b]).m_train + "\n");
          paramStringBuffer.append(",\n");
        } 
        paramStringBuffer.append("]\n");
        ((NBTreeClassifierTree)this.m_sons[b]).graphTree(paramStringBuffer);
      } 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\NBTreeClassifierTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */