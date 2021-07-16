/*    */ package jeans.math;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SingleStat
/*    */ {
/* 34 */   protected double m_Min = Double.POSITIVE_INFINITY;
/* 35 */   protected double m_Max = Double.NEGATIVE_INFINITY;
/*    */   protected double m_Sum;
/*    */   protected double m_SumSQ;
/*    */   protected double m_Count;
/*    */   
/*    */   public SingleStat() {
/* 41 */     this.m_Min = Double.POSITIVE_INFINITY;
/* 42 */     this.m_Max = Double.NEGATIVE_INFINITY;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 46 */     this.m_Sum = 0.0D;
/* 47 */     this.m_SumSQ = 0.0D;
/* 48 */     this.m_Count = 0.0D;
/* 49 */     this.m_Min = Double.POSITIVE_INFINITY;
/* 50 */     this.m_Max = Double.NEGATIVE_INFINITY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getCount() {
/* 55 */     return this.m_Count;
/*    */   }
/*    */   
/*    */   public void addFloat(double value) {
/* 59 */     this.m_Sum += value;
/* 60 */     this.m_SumSQ += value * value;
/* 61 */     this.m_Count++;
/* 62 */     if (value < this.m_Min) this.m_Min = value; 
/* 63 */     if (value > this.m_Max) this.m_Max = value; 
/*    */   }
/*    */   
/*    */   public double getRange() {
/* 67 */     return Math.abs(getMax() - getMin());
/*    */   }
/*    */   
/*    */   public void addMean(SingleStat other) {
/* 71 */     addFloat(other.getMean());
/*    */   }
/*    */   
/*    */   public double getMin() {
/* 75 */     return this.m_Min;
/*    */   }
/*    */   
/*    */   public double getMax() {
/* 79 */     return this.m_Max;
/*    */   }
/*    */   
/*    */   public double getVariance() {
/* 83 */     return (this.m_SumSQ - this.m_Sum * this.m_Sum / this.m_Count) / this.m_Count;
/*    */   }
/*    */   
/*    */   public double getStdDev() {
/* 87 */     return Math.sqrt(getVariance() * this.m_Count / (this.m_Count - 1.0D));
/*    */   }
/*    */   
/*    */   public double getStdDefOfMean() {
/* 91 */     return getStdDev() / Math.sqrt(this.m_Count);
/*    */   }
/*    */   
/*    */   public double getMean() {
/* 95 */     return this.m_Sum / this.m_Count;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 99 */     return "" + getMean() + " (" + getStdDev() + ") " + getMin() + "/" + getMax();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\SingleStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */