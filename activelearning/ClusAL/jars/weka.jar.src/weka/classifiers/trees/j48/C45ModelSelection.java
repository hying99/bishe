package weka.classifiers.trees.j48;

import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

public class C45ModelSelection extends ModelSelection {
  private int m_minNoObj;
  
  private Instances m_allData;
  
  public C45ModelSelection(int paramInt, Instances paramInstances) {
    this.m_minNoObj = paramInt;
    this.m_allData = paramInstances;
  }
  
  public void cleanup() {
    this.m_allData = null;
  }
  
  public final ClassifierSplitModel selectModel(Instances paramInstances) {
    C45Split c45Split = null;
    NoSplit noSplit = null;
    double d = 0.0D;
    byte b = 0;
    boolean bool = true;
    try {
      Distribution distribution = new Distribution(paramInstances);
      noSplit = new NoSplit(distribution);
      if (Utils.sm(distribution.total(), (2 * this.m_minNoObj)) || Utils.eq(distribution.total(), distribution.perClass(distribution.maxClass())))
        return noSplit; 
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
      C45Split[] arrayOfC45Split = new C45Split[paramInstances.numAttributes()];
      double d2 = paramInstances.sumOfWeights();
      byte b1;
      for (b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (b1 != paramInstances.classIndex()) {
          arrayOfC45Split[b1] = new C45Split(b1, this.m_minNoObj, d2);
          arrayOfC45Split[b1].buildClassifier(paramInstances);
          if (arrayOfC45Split[b1].checkModel())
            if (this.m_allData != null) {
              if (paramInstances.attribute(b1).isNumeric() || bool || Utils.sm(paramInstances.attribute(b1).numValues(), 0.3D * this.m_allData.numInstances())) {
                d += arrayOfC45Split[b1].infoGain();
                b++;
              } 
            } else {
              d += arrayOfC45Split[b1].infoGain();
              b++;
            }  
        } else {
          arrayOfC45Split[b1] = null;
        } 
      } 
      if (b == 0)
        return noSplit; 
      d /= b;
      double d1 = 0.0D;
      for (b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
        if (b1 != paramInstances.classIndex() && arrayOfC45Split[b1].checkModel() && arrayOfC45Split[b1].infoGain() >= d - 0.001D && Utils.gr(arrayOfC45Split[b1].gainRatio(), d1)) {
          c45Split = arrayOfC45Split[b1];
          d1 = arrayOfC45Split[b1].gainRatio();
        } 
      } 
      if (Utils.eq(d1, 0.0D))
        return noSplit; 
      c45Split.distribution().addInstWithUnknown(paramInstances, c45Split.attIndex());
      if (this.m_allData != null)
        c45Split.setSplitPoint(this.m_allData); 
      return c45Split;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  public final ClassifierSplitModel selectModel(Instances paramInstances1, Instances paramInstances2) {
    return selectModel(paramInstances1);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\j48\C45ModelSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */