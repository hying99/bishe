package weka.classifiers.trees.m5;

import weka.core.Instances;

public final class Impurity {
  double n;
  
  int attr;
  
  double nl;
  
  double nr;
  
  double sl;
  
  double sr;
  
  double s2l;
  
  double s2r;
  
  double sdl;
  
  double sdr;
  
  double vl;
  
  double vr;
  
  double sd;
  
  double va;
  
  double impurity;
  
  int order;
  
  public Impurity(int paramInt1, int paramInt2, Instances paramInstances, int paramInt3) {
    Values values = new Values(0, paramInstances.numInstances() - 1, paramInstances.classIndex(), paramInstances);
    this.attr = paramInt2;
    this.n = paramInstances.numInstances();
    this.sd = values.sd;
    this.va = values.va;
    values = new Values(0, paramInt1, paramInstances.classIndex(), paramInstances);
    this.nl = (paramInt1 + 1);
    this.sl = values.sum;
    this.s2l = values.sqrSum;
    values = new Values(paramInt1 + 1, paramInstances.numInstances() - 1, paramInstances.classIndex(), paramInstances);
    this.nr = (paramInstances.numInstances() - paramInt1 - 1);
    this.sr = values.sum;
    this.s2r = values.sqrSum;
    this.order = paramInt3;
    incremental(0.0D, 0);
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Print impurity values:\n");
    stringBuffer.append("    Number of total instances:\t" + this.n + "\n");
    stringBuffer.append("    Splitting attribute:\t\t" + this.attr + "\n");
    stringBuffer.append("    Number of the instances in the left:\t" + this.nl + "\n");
    stringBuffer.append("    Number of the instances in the right:\t" + this.nr + "\n");
    stringBuffer.append("    Sum of the left:\t\t\t" + this.sl + "\n");
    stringBuffer.append("    Sum of the right:\t\t\t" + this.sr + "\n");
    stringBuffer.append("    Squared sum of the left:\t\t" + this.s2l + "\n");
    stringBuffer.append("    Squared sum of the right:\t\t" + this.s2r + "\n");
    stringBuffer.append("    Standard deviation of the left:\t" + this.sdl + "\n");
    stringBuffer.append("    Standard deviation of the right:\t" + this.sdr + "\n");
    stringBuffer.append("    Variance of the left:\t\t" + this.vr + "\n");
    stringBuffer.append("    Variance of the right:\t\t" + this.vr + "\n");
    stringBuffer.append("    Overall standard deviation:\t\t" + this.sd + "\n");
    stringBuffer.append("    Overall variance:\t\t\t" + this.va + "\n");
    stringBuffer.append("    Impurity (order " + this.order + "):\t\t" + this.impurity + "\n");
    return stringBuffer.toString();
  }
  
  public final void incremental(double paramDouble, int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    switch (paramInt) {
      case 1:
        this.nl++;
        this.nr--;
        this.sl += paramDouble;
        this.sr -= paramDouble;
        this.s2l += paramDouble * paramDouble;
        this.s2r -= paramDouble * paramDouble;
        break;
      case -1:
        this.nl--;
        this.nr++;
        this.sl -= paramDouble;
        this.sr += paramDouble;
        this.s2l -= paramDouble * paramDouble;
        this.s2r += paramDouble * paramDouble;
        break;
      case 0:
        break;
      default:
        System.err.println("wrong type in Impurity.incremental().");
        break;
    } 
    if (this.nl <= 0.0D) {
      this.vl = 0.0D;
      this.sdl = 0.0D;
    } else {
      this.vl = (this.nl * this.s2l - this.sl * this.sl) / this.nl * this.nl;
      this.vl = Math.abs(this.vl);
      this.sdl = Math.sqrt(this.vl);
    } 
    if (this.nr <= 0.0D) {
      this.vr = 0.0D;
      this.sdr = 0.0D;
    } else {
      this.vr = (this.nr * this.s2r - this.sr * this.sr) / this.nr * this.nr;
      this.vr = Math.abs(this.vr);
      this.sdr = Math.sqrt(this.vr);
    } 
    if (this.order <= 0) {
      System.err.println("Impurity order less than zero in Impurity.incremental()");
    } else if (this.order == 1) {
      d1 = this.va;
      d2 = this.vl;
      d3 = this.vr;
    } else {
      d1 = Math.pow(this.va, 1.0D / this.order);
      d2 = Math.pow(this.vl, 1.0D / this.order);
      d3 = Math.pow(this.vr, 1.0D / this.order);
    } 
    if (this.nl <= 0.0D || this.nr <= 0.0D) {
      this.impurity = 0.0D;
    } else {
      this.impurity = d1 - this.nl / this.n * d2 - this.nr / this.n * d3;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\Impurity.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */