package weka.experiment;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.StringTokenizer;
import weka.core.Utils;

public class Stats implements Serializable {
  public double count = 0.0D;
  
  public double sum = 0.0D;
  
  public double sumSq = 0.0D;
  
  public double stdDev = Double.NaN;
  
  public double mean = Double.NaN;
  
  public double min = Double.NaN;
  
  public double max = Double.NaN;
  
  public void add(double paramDouble) {
    add(paramDouble, 1.0D);
  }
  
  public void add(double paramDouble1, double paramDouble2) {
    this.sum += paramDouble1 * paramDouble2;
    this.sumSq += paramDouble1 * paramDouble1 * paramDouble2;
    this.count += paramDouble2;
    this.min = this.max = paramDouble1;
    if (paramDouble1 < this.min) {
      this.min = paramDouble1;
    } else if (paramDouble1 > this.max) {
      this.max = paramDouble1;
    } 
  }
  
  public void subtract(double paramDouble) {
    subtract(paramDouble, 1.0D);
  }
  
  public void subtract(double paramDouble1, double paramDouble2) {
    this.sum -= paramDouble1 * paramDouble2;
    this.sumSq -= paramDouble1 * paramDouble1 * paramDouble2;
    this.count -= paramDouble2;
  }
  
  public void calculateDerived() {
    this.mean = Double.NaN;
    this.stdDev = Double.NaN;
    if (this.count > 0.0D) {
      this.mean = this.sum / this.count;
      this.stdDev = Double.POSITIVE_INFINITY;
      if (this.count > 1.0D) {
        this.stdDev = this.sumSq - this.sum * this.sum / this.count;
        this.stdDev /= this.count - 1.0D;
        if (this.stdDev < 0.0D)
          this.stdDev = 0.0D; 
        this.stdDev = Math.sqrt(this.stdDev);
      } 
    } 
  }
  
  public String toString() {
    calculateDerived();
    return "Count   " + Utils.doubleToString(this.count, 8) + '\n' + "Min     " + Utils.doubleToString(this.min, 8) + '\n' + "Max     " + Utils.doubleToString(this.max, 8) + '\n' + "Sum     " + Utils.doubleToString(this.sum, 8) + '\n' + "SumSq   " + Utils.doubleToString(this.sumSq, 8) + '\n' + "Mean    " + Utils.doubleToString(this.mean, 8) + '\n' + "StdDev  " + Utils.doubleToString(this.stdDev, 8) + '\n';
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Stats stats = new Stats();
      LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(System.in));
      String str;
      while ((str = lineNumberReader.readLine()) != null) {
        str = str.trim();
        if (str.equals("") || str.startsWith("@") || str.startsWith("%"))
          continue; 
        StringTokenizer stringTokenizer = new StringTokenizer(str, " ,\t\n\r\f");
        byte b = 0;
        double d = 0.0D;
        while (stringTokenizer.hasMoreTokens()) {
          double d1 = (new Double(stringTokenizer.nextToken())).doubleValue();
          if (!b) {
            d = d1;
          } else {
            System.err.println("MSG: Too many values in line \"" + str + "\", skipped.");
            break;
          } 
          b++;
        } 
        if (b == 1)
          stats.add(d); 
      } 
      stats.calculateDerived();
      System.err.println(stats);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\Stats.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */