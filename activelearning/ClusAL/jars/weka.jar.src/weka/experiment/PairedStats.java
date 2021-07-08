package weka.experiment;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.StringTokenizer;
import weka.core.Statistics;
import weka.core.Utils;

public class PairedStats {
  public Stats xStats = new Stats();
  
  public Stats yStats = new Stats();
  
  public Stats differencesStats = new Stats();
  
  public double differencesProbability;
  
  public double correlation;
  
  public double xySum;
  
  public double count;
  
  public int differencesSignificance;
  
  public double sigLevel;
  
  public PairedStats(double paramDouble) {
    this.sigLevel = paramDouble;
  }
  
  public void add(double paramDouble1, double paramDouble2) {
    this.xStats.add(paramDouble1);
    this.yStats.add(paramDouble2);
    this.differencesStats.add(paramDouble1 - paramDouble2);
    this.xySum += paramDouble1 * paramDouble2;
    this.count++;
  }
  
  public void subtract(double paramDouble1, double paramDouble2) {
    this.xStats.subtract(paramDouble1);
    this.yStats.subtract(paramDouble2);
    this.differencesStats.subtract(paramDouble1 - paramDouble2);
    this.xySum -= paramDouble1 * paramDouble2;
    this.count--;
  }
  
  public void calculateDerived() {
    this.xStats.calculateDerived();
    this.yStats.calculateDerived();
    this.differencesStats.calculateDerived();
    this.correlation = Double.NaN;
    if (!Double.isNaN(this.xStats.stdDev) && !Double.isNaN(this.yStats.stdDev) && !Utils.eq(this.xStats.stdDev, 0.0D)) {
      double d = (this.xySum - this.xStats.sum * this.yStats.sum / this.count) / (this.xStats.sumSq - this.xStats.sum * this.xStats.mean);
      if (!Utils.eq(this.yStats.stdDev, 0.0D)) {
        this.correlation = d * this.xStats.stdDev / this.yStats.stdDev;
      } else {
        this.correlation = 1.0D;
      } 
    } 
    if (Utils.gr(this.differencesStats.stdDev, 0.0D)) {
      double d = this.differencesStats.mean * Math.sqrt(this.count) / this.differencesStats.stdDev;
      if (this.count > 1.0D) {
        this.differencesProbability = Statistics.FProbability(d * d, 1, (int)this.count - 1);
      } else {
        this.differencesProbability = 1.0D;
      } 
    } else if (this.differencesStats.sumSq == 0.0D) {
      this.differencesProbability = 1.0D;
    } else {
      this.differencesProbability = 0.0D;
    } 
    this.differencesSignificance = 0;
    if (this.differencesProbability <= this.sigLevel)
      if (this.xStats.mean > this.yStats.mean) {
        this.differencesSignificance = 1;
      } else {
        this.differencesSignificance = -1;
      }  
  }
  
  public String toString() {
    return "Analysis for " + Utils.doubleToString(this.count, 0) + " points:\n" + "                " + "         Column 1" + "         Column 2" + "       Difference\n" + "Minimums        " + Utils.doubleToString(this.xStats.min, 17, 4) + Utils.doubleToString(this.yStats.min, 17, 4) + Utils.doubleToString(this.differencesStats.min, 17, 4) + '\n' + "Maximums        " + Utils.doubleToString(this.xStats.max, 17, 4) + Utils.doubleToString(this.yStats.max, 17, 4) + Utils.doubleToString(this.differencesStats.max, 17, 4) + '\n' + "Sums            " + Utils.doubleToString(this.xStats.sum, 17, 4) + Utils.doubleToString(this.yStats.sum, 17, 4) + Utils.doubleToString(this.differencesStats.sum, 17, 4) + '\n' + "SumSquares      " + Utils.doubleToString(this.xStats.sumSq, 17, 4) + Utils.doubleToString(this.yStats.sumSq, 17, 4) + Utils.doubleToString(this.differencesStats.sumSq, 17, 4) + '\n' + "Means           " + Utils.doubleToString(this.xStats.mean, 17, 4) + Utils.doubleToString(this.yStats.mean, 17, 4) + Utils.doubleToString(this.differencesStats.mean, 17, 4) + '\n' + "SDs             " + Utils.doubleToString(this.xStats.stdDev, 17, 4) + Utils.doubleToString(this.yStats.stdDev, 17, 4) + Utils.doubleToString(this.differencesStats.stdDev, 17, 4) + '\n' + "Prob(differences) " + Utils.doubleToString(this.differencesProbability, 4) + " (sigflag " + this.differencesSignificance + ")\n" + "Correlation       " + Utils.doubleToString(this.correlation, 4) + "\n";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      PairedStats pairedStats = new PairedStats(0.05D);
      LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(System.in));
      String str;
      while ((str = lineNumberReader.readLine()) != null) {
        str = str.trim();
        if (str.equals("") || str.startsWith("@") || str.startsWith("%"))
          continue; 
        StringTokenizer stringTokenizer = new StringTokenizer(str, " ,\t\n\r\f");
        byte b = 0;
        double d1 = 0.0D;
        double d2 = 0.0D;
        while (stringTokenizer.hasMoreTokens()) {
          double d = (new Double(stringTokenizer.nextToken())).doubleValue();
          if (!b) {
            d1 = d;
          } else if (b == 1) {
            d2 = d;
          } else {
            System.err.println("MSG: Too many values in line \"" + str + "\", skipped.");
            break;
          } 
          b++;
        } 
        if (b == 2)
          pairedStats.add(d1, d2); 
      } 
      pairedStats.calculateDerived();
      System.err.println(pairedStats);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\PairedStats.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */