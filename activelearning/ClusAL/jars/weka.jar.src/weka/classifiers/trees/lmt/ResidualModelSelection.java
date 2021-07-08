package weka.classifiers.trees.lmt;

import weka.classifiers.trees.j48.ClassifierSplitModel;
import weka.classifiers.trees.j48.Distribution;
import weka.classifiers.trees.j48.ModelSelection;
import weka.classifiers.trees.j48.NoSplit;
import weka.core.Instances;

public class ResidualModelSelection extends ModelSelection {
  protected int m_minNumInstances;
  
  protected double m_minInfoGain;
  
  public ResidualModelSelection(int paramInt) {
    this.m_minNumInstances = paramInt;
    this.m_minInfoGain = 1.0E-4D;
  }
  
  public void cleanup() {}
  
  public final ClassifierSplitModel selectModel(Instances paramInstances, double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2) throws Exception {
    int i = paramInstances.numAttributes();
    if (i < 2)
      throw new Exception("Can't select Model without non-class attribute"); 
    if (paramInstances.numInstances() < this.m_minNumInstances)
      return (ClassifierSplitModel)new NoSplit(new Distribution(paramInstances)); 
    double d = -1.7976931348623157E308D;
    byte b = -1;
    for (byte b1 = 0; b1 < i; b1++) {
      if (b1 != paramInstances.classIndex()) {
        ResidualSplit residualSplit = new ResidualSplit(b1);
        residualSplit.buildClassifier(paramInstances, paramArrayOfdouble1, paramArrayOfdouble2);
        if (residualSplit.checkModel(this.m_minNumInstances)) {
          double d1 = residualSplit.entropyGain();
          if (d1 > d) {
            d = d1;
            b = b1;
          } 
        } 
      } 
    } 
    if (d >= this.m_minInfoGain) {
      ResidualSplit residualSplit = new ResidualSplit(b);
      residualSplit.buildClassifier(paramInstances, paramArrayOfdouble1, paramArrayOfdouble2);
      return residualSplit;
    } 
    return (ClassifierSplitModel)new NoSplit(new Distribution(paramInstances));
  }
  
  public final ClassifierSplitModel selectModel(Instances paramInstances) {
    return null;
  }
  
  public final ClassifierSplitModel selectModel(Instances paramInstances1, Instances paramInstances2) {
    return null;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\lmt\ResidualModelSelection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */