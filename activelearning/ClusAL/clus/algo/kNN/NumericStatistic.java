/*    */ package clus.algo.kNN;
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
/*    */ public class NumericStatistic
/*    */   extends AttributeStatistic
/*    */ {
/*    */   private double $mean;
/*    */   private double $variance;
/*    */   private double $min;
/*    */   private double $max;
/*    */   
/*    */   public double mean() {
/* 37 */     return this.$mean;
/*    */   }
/*    */   public void setMean(double m) {
/* 40 */     this.$mean = m;
/*    */   }
/*    */   public double variance() {
/* 43 */     return this.$variance;
/*    */   }
/*    */   public void setVariance(double v) {
/* 46 */     this.$variance = v;
/*    */   }
/*    */   public double min() {
/* 49 */     return this.$min;
/*    */   }
/*    */   public void setMin(double m) {
/* 52 */     this.$min = m;
/*    */   }
/*    */   public double max() {
/* 55 */     return this.$max;
/*    */   }
/*    */   public void setMax(double m) {
/* 58 */     this.$max = m;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\NumericStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */