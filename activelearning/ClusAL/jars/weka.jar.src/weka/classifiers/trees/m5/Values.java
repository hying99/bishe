package weka.classifiers.trees.m5;

import weka.core.Instances;

public final class Values {
  int numInstances;
  
  int missingInstances;
  
  int first;
  
  int last;
  
  int attr;
  
  double sum;
  
  double sqrSum;
  
  double va;
  
  double sd;
  
  public Values(int paramInt1, int paramInt2, int paramInt3, Instances paramInstances) {
    byte b = 0;
    this.numInstances = paramInt2 - paramInt1 + 1;
    this.missingInstances = 0;
    this.first = paramInt1;
    this.last = paramInt2;
    this.attr = paramInt3;
    this.sum = 0.0D;
    this.sqrSum = 0.0D;
    for (int i = this.first; i <= this.last; i++) {
      if (!paramInstances.instance(i).isMissing(this.attr)) {
        b++;
        double d = paramInstances.instance(i).value(this.attr);
        this.sum += d;
        this.sqrSum += d * d;
      } 
      if (b > 1) {
        this.va = (this.sqrSum - this.sum * this.sum / b) / b;
        this.va = Math.abs(this.va);
        this.sd = Math.sqrt(this.va);
      } else {
        this.va = 0.0D;
        this.sd = 0.0D;
      } 
    } 
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Print statistic values of instances (" + this.first + "-" + this.last + "\n");
    stringBuffer.append("    Number of instances:\t" + this.numInstances + "\n");
    stringBuffer.append("    NUmber of instances with unknowns:\t" + this.missingInstances + "\n");
    stringBuffer.append("    Attribute:\t\t\t:" + this.attr + "\n");
    stringBuffer.append("    Sum:\t\t\t" + this.sum + "\n");
    stringBuffer.append("    Squared sum:\t\t" + this.sqrSum + "\n");
    stringBuffer.append("    Stanard Deviation:\t\t" + this.sd + "\n");
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\m5\Values.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */