package weka.classifiers.trees.m5;

import java.io.Serializable;
import weka.core.Instances;
import weka.core.Utils;
import weka.experiment.PairedStats;

public final class CorrelationSplitInfo implements Cloneable, Serializable, SplitEvaluate {
  private int m_first;
  
  private int m_last;
  
  private int m_position;
  
  private double m_maxImpurity;
  
  private int m_splitAttr;
  
  private double m_splitValue;
  
  private int m_number;
  
  public CorrelationSplitInfo(int paramInt1, int paramInt2, int paramInt3) {
    initialize(paramInt1, paramInt2, paramInt3);
  }
  
  public final SplitEvaluate copy() throws Exception {
    return (CorrelationSplitInfo)clone();
  }
  
  public final void initialize(int paramInt1, int paramInt2, int paramInt3) {
    this.m_number = paramInt2 - paramInt1 + 1;
    this.m_first = paramInt1;
    this.m_last = paramInt2;
    this.m_position = -1;
    this.m_maxImpurity = -1.7976931348623157E308D;
    this.m_splitAttr = paramInt3;
    this.m_splitValue = 0.0D;
  }
  
  public final void attrSplit(int paramInt, Instances paramInstances) throws Exception {
    byte b2 = 0;
    int k = paramInstances.numInstances() - 1;
    PairedStats pairedStats1 = new PairedStats(0.01D);
    PairedStats pairedStats2 = new PairedStats(0.01D);
    PairedStats pairedStats3 = new PairedStats(0.01D);
    int m = paramInstances.classIndex();
    double d2 = 2.0D;
    initialize(b2, k, paramInt);
    if (this.m_number < 4)
      return; 
    byte b1 = (k - b2 + 1 < 5) ? 1 : ((k - b2 + 1) / 5);
    this.m_position = b2;
    int j = b2 + b1 - 1;
    int i;
    for (i = b2; i < b1; i++) {
      pairedStats1.add(paramInstances.instance(i).value(paramInt), paramInstances.instance(i).value(m));
      pairedStats2.add(paramInstances.instance(i).value(paramInt), paramInstances.instance(i).value(m));
    } 
    for (i = b1; i < paramInstances.numInstances(); i++) {
      pairedStats1.add(paramInstances.instance(i).value(paramInt), paramInstances.instance(i).value(m));
      pairedStats3.add(paramInstances.instance(i).value(paramInt), paramInstances.instance(i).value(m));
    } 
    pairedStats1.calculateDerived();
    double d1 = pairedStats1.yStats.stdDev * pairedStats1.yStats.stdDev;
    d1 = Math.abs(d1);
    d1 = Math.pow(d1, 1.0D / d2);
    for (i = b2 + b1; i < k - b1 - 1; i++) {
      pairedStats3.subtract(paramInstances.instance(i).value(paramInt), paramInstances.instance(i).value(m));
      pairedStats2.add(paramInstances.instance(i).value(paramInt), paramInstances.instance(i).value(m));
      if (!Utils.eq(paramInstances.instance(i + 1).value(paramInt), paramInstances.instance(i).value(paramInt))) {
        pairedStats2.calculateDerived();
        pairedStats3.calculateDerived();
        double d3 = Math.abs(pairedStats2.correlation);
        double d4 = Math.abs(pairedStats3.correlation);
        double d5 = pairedStats2.yStats.stdDev * pairedStats2.yStats.stdDev;
        d5 = Math.abs(d5);
        d5 = Math.pow(d5, 1.0D / d2);
        double d6 = pairedStats3.yStats.stdDev * pairedStats3.yStats.stdDev;
        d6 = Math.abs(d6);
        d6 = Math.pow(d6, 1.0D / d2);
        double d7 = d1 - pairedStats2.count / pairedStats1.count * d5 - pairedStats3.count / pairedStats1.count * d6;
        d3 = pairedStats2.count / pairedStats1.count * d3;
        d4 = pairedStats3.count / pairedStats1.count * d4;
        double d8 = d3 + d4 - Math.abs(pairedStats1.correlation);
        if (!Utils.eq(d7, 0.0D) && d7 > this.m_maxImpurity) {
          this.m_maxImpurity = d7;
          this.m_splitValue = (paramInstances.instance(i).value(paramInt) + paramInstances.instance(i + 1).value(paramInt)) * 0.5D;
          this.m_position = i;
        } 
      } 
    } 
  }
  
  public double maxImpurity() {
    return this.m_maxImpurity;
  }
  
  public int splitAttr() {
    return this.m_splitAttr;
  }
  
  public int position() {
    return this.m_position;
  }
  
  public double splitValue() {
    return this.m_splitValue;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\CorrelationSplitInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */