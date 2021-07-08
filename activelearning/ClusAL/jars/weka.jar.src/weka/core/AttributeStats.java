package weka.core;

import java.io.Serializable;
import weka.experiment.Stats;

public class AttributeStats implements Serializable {
  public int intCount = 0;
  
  public int realCount = 0;
  
  public int missingCount = 0;
  
  public int distinctCount = 0;
  
  public int uniqueCount = 0;
  
  public int totalCount = 0;
  
  public Stats numericStats;
  
  public int[] nominalCounts;
  
  protected void addDistinct(double paramDouble, int paramInt) {
    if (paramInt > 0) {
      if (paramInt == 1)
        this.uniqueCount++; 
      if (Utils.eq(paramDouble, (int)paramDouble)) {
        this.intCount += paramInt;
      } else {
        this.realCount += paramInt;
      } 
      if (this.nominalCounts != null)
        this.nominalCounts[(int)paramDouble] = paramInt; 
      if (this.numericStats != null) {
        this.numericStats.add(paramDouble, paramInt);
        this.numericStats.calculateDerived();
      } 
    } 
    this.distinctCount++;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(Utils.padLeft("Type", 4)).append(Utils.padLeft("Nom", 5));
    stringBuffer.append(Utils.padLeft("Int", 5)).append(Utils.padLeft("Real", 5));
    stringBuffer.append(Utils.padLeft("Missing", 12));
    stringBuffer.append(Utils.padLeft("Unique", 12));
    stringBuffer.append(Utils.padLeft("Dist", 6));
    if (this.nominalCounts != null) {
      stringBuffer.append(' ');
      for (byte b = 0; b < this.nominalCounts.length; b++)
        stringBuffer.append(Utils.padLeft("C[" + b + "]", 5)); 
    } 
    stringBuffer.append('\n');
    long l = Math.round(100.0D * this.intCount / this.totalCount);
    if (this.nominalCounts != null) {
      stringBuffer.append(Utils.padLeft("Nom", 4)).append(' ');
      stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
      stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
    } else {
      stringBuffer.append(Utils.padLeft("Num", 4)).append(' ');
      stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
      stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
    } 
    l = Math.round(100.0D * this.realCount / this.totalCount);
    stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
    stringBuffer.append(Utils.padLeft("" + this.missingCount, 5)).append(" /");
    l = Math.round(100.0D * this.missingCount / this.totalCount);
    stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
    stringBuffer.append(Utils.padLeft("" + this.uniqueCount, 5)).append(" /");
    l = Math.round(100.0D * this.uniqueCount / this.totalCount);
    stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
    stringBuffer.append(Utils.padLeft("" + this.distinctCount, 5)).append(' ');
    if (this.nominalCounts != null)
      for (byte b = 0; b < this.nominalCounts.length; b++)
        stringBuffer.append(Utils.padLeft("" + this.nominalCounts[b], 5));  
    stringBuffer.append('\n');
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\AttributeStats.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */