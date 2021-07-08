package weka.experiment;

import weka.core.Statistics;
import weka.core.Utils;

public class PairedStatsCorrected extends PairedStats {
  protected double m_testTrainRatio;
  
  public PairedStatsCorrected(double paramDouble1, double paramDouble2) {
    super(paramDouble1);
    this.m_testTrainRatio = paramDouble2;
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
      double d = this.differencesStats.mean / Math.sqrt((1.0D / this.count + this.m_testTrainRatio) * this.differencesStats.stdDev * this.differencesStats.stdDev);
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
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\PairedStatsCorrected.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */