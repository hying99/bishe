package weka.classifiers.trees.j48;

import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

public class NBTreeModelSelection extends ModelSelection {
  private int m_minNoObj;
  
  private Instances m_allData;
  
  public NBTreeModelSelection(int paramInt, Instances paramInstances) {
    this.m_minNoObj = paramInt;
    this.m_allData = paramInstances;
  }
  
  public void cleanup() {
    this.m_allData = null;
  }
  
  public final ClassifierSplitModel selectModel(Instances paramInstances) {
    double d = 0.0D;
    NBTreeSplit nBTreeSplit = null;
    NBTreeNoSplit nBTreeNoSplit = null;
    byte b = 0;
    boolean bool = true;
    try {
      nBTreeNoSplit = new NBTreeNoSplit();
      nBTreeNoSplit.buildClassifier(paramInstances);
      if (paramInstances.numInstances() < 5)
        return nBTreeNoSplit; 
      d = nBTreeNoSplit.getErrors();
      if (d == 0.0D)
        return nBTreeNoSplit; 
      Distribution distribution = new Distribution(paramInstances);
      if (Utils.sm(distribution.total(), this.m_minNoObj) || Utils.eq(distribution.total(), distribution.perClass(distribution.maxClass())))
        return nBTreeNoSplit; 
      if (this.m_allData != null) {
        Enumeration enumeration = paramInstances.enumerateAttributes();
        while (enumeration.hasMoreElements()) {
          Attribute attribute = enumeration.nextElement();
          if (attribute.isNumeric() || Utils.sm(attribute.numValues(), 0.3D * this.m_allData.numInstances())) {
            bool = false;
            break;
          } 
        } 
      } 
      NBTreeSplit[] arrayOfNBTreeSplit = new NBTreeSplit[paramInstances.numAttributes()];
      double d2 = paramInstances.sumOfWeights();
      byte b1;
      for (b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (b1 != paramInstances.classIndex()) {
          arrayOfNBTreeSplit[b1] = new NBTreeSplit(b1, this.m_minNoObj, d2);
          arrayOfNBTreeSplit[b1].setGlobalModel(nBTreeNoSplit);
          arrayOfNBTreeSplit[b1].buildClassifier(paramInstances);
          if (arrayOfNBTreeSplit[b1].checkModel())
            b++; 
        } else {
          arrayOfNBTreeSplit[b1] = null;
        } 
      } 
      if (b == 0)
        return nBTreeNoSplit; 
      double d1 = d;
      for (b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (b1 != paramInstances.classIndex() && arrayOfNBTreeSplit[b1].checkModel() && arrayOfNBTreeSplit[b1].getErrors() < d1) {
          nBTreeSplit = arrayOfNBTreeSplit[b1];
          d1 = arrayOfNBTreeSplit[b1].getErrors();
        } 
      } 
      return (ClassifierSplitModel)(((d - d1) / d < 0.05D) ? nBTreeNoSplit : nBTreeSplit);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public final ClassifierSplitModel selectModel(Instances paramInstances1, Instances paramInstances2) {
    return selectModel(paramInstances1);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\NBTreeModelSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */