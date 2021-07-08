package weka.classifiers.trees.m5;

import java.io.Serializable;
import weka.core.Instances;
import weka.core.Utils;

public final class YongSplitInfo implements Cloneable, Serializable, SplitEvaluate {
  private int number;
  
  private int first;
  
  private int last;
  
  private int position;
  
  private double maxImpurity;
  
  private double leftAve;
  
  private double rightAve;
  
  private int splitAttr;
  
  private double splitValue;
  
  public YongSplitInfo(int paramInt1, int paramInt2, int paramInt3) {
    this.number = paramInt2 - paramInt1 + 1;
    this.first = paramInt1;
    this.last = paramInt2;
    this.position = -1;
    this.maxImpurity = -1.0E20D;
    this.splitAttr = paramInt3;
    this.splitValue = 0.0D;
    Utils.SMALL = 1.0E-10D;
  }
  
  public final SplitEvaluate copy() throws Exception {
    return (YongSplitInfo)clone();
  }
  
  public final void initialize(int paramInt1, int paramInt2, int paramInt3) {
    this.number = paramInt2 - paramInt1 + 1;
    this.first = paramInt1;
    this.last = paramInt2;
    this.position = -1;
    this.maxImpurity = -1.0E20D;
    this.splitAttr = paramInt3;
    this.splitValue = 0.0D;
  }
  
  public final String toString(Instances paramInstances) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Print SplitInfo:\n");
    stringBuffer.append("    Instances:\t\t" + this.number + " (" + this.first + "-" + this.position + "," + (this.position + 1) + "-" + this.last + ")\n");
    stringBuffer.append("    Maximum Impurity Reduction:\t" + Utils.doubleToString(this.maxImpurity, 1, 4) + "\n");
    stringBuffer.append("    Left average:\t" + this.leftAve + "\n");
    stringBuffer.append("    Right average:\t" + this.rightAve + "\n");
    if (this.maxImpurity > 0.0D) {
      stringBuffer.append("    Splitting function:\t" + paramInstances.attribute(this.splitAttr).name() + " = " + this.splitValue + "\n");
    } else {
      stringBuffer.append("    Splitting function:\tnull\n");
    } 
    return stringBuffer.toString();
  }
  
  public final void attrSplit(int paramInt, Instances paramInstances) throws Exception {
    byte b2 = 0;
    int m = paramInstances.numInstances() - 1;
    initialize(b2, m, paramInt);
    if (this.number < 4)
      return; 
    byte b1 = (m - b2 + 1 < 5) ? 1 : ((m - b2 + 1) / 5);
    this.position = b2;
    int k = b2 + b1 - 1;
    Impurity impurity = new Impurity(k, paramInt, paramInstances, 5);
    int j = 0;
    for (int i = b2 + b1; i <= m - b1 - 1; i++) {
      impurity.incremental(paramInstances.instance(i).classValue(), 1);
      if (!Utils.eq(paramInstances.instance(i + 1).value(paramInt), paramInstances.instance(i).value(paramInt))) {
        j = i;
        if (impurity.impurity > this.maxImpurity) {
          this.maxImpurity = impurity.impurity;
          this.splitValue = (paramInstances.instance(i).value(paramInt) + paramInstances.instance(i + 1).value(paramInt)) * 0.5D;
          this.leftAve = impurity.sl / impurity.nl;
          this.rightAve = impurity.sr / impurity.nr;
          this.position = i;
        } 
      } 
    } 
  }
  
  public double maxImpurity() {
    return this.maxImpurity;
  }
  
  public int splitAttr() {
    return this.splitAttr;
  }
  
  public int position() {
    return this.position;
  }
  
  public double splitValue() {
    return this.splitValue;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\YongSplitInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */