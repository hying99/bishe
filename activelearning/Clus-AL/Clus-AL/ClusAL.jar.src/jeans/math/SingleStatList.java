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
/*    */ public class SingleStatList
/*    */   extends SingleStat
/*    */ {
/*    */   double[] m_Values;
/*    */   double m_X;
/*    */   double m_Y;
/*    */   int m_Idx;
/*    */   
/*    */   public SingleStatList(int size) {
/* 35 */     this.m_Values = new double[size];
/*    */   }
/*    */   
/*    */   public double getTValueSigTest(SingleStatList other) {
/* 39 */     SingleStat stat = new SingleStat();
/* 40 */     for (int i = 0; i < this.m_Values.length; i++) {
/* 41 */       stat.addFloat(this.m_Values[i] - other.m_Values[i]);
/*    */     }
/* 43 */     return Math.abs(stat.getMean()) / stat.getStdDefOfMean();
/*    */   }
/*    */   
/*    */   public void reset() {
/* 47 */     super.reset();
/* 48 */     for (int i = 0; i < this.m_Values.length; i++) {
/* 49 */       this.m_Values[i] = 0.0D;
/*    */     }
/* 51 */     this.m_Idx = 0;
/*    */   }
/*    */   
/*    */   public void addFloat(double value) {
/* 55 */     super.addFloat(value);
/* 56 */     this.m_Values[this.m_Idx++] = value;
/*    */   }
/*    */   
/*    */   public double[] getValues() {
/* 60 */     return this.m_Values;
/*    */   }
/*    */   
/*    */   public void setX(double value) {
/* 64 */     this.m_X = value;
/*    */   }
/*    */   
/*    */   public double getX() {
/* 68 */     return this.m_X;
/*    */   }
/*    */   
/*    */   public void setY(double value) {
/* 72 */     this.m_Y = value;
/*    */   }
/*    */   
/*    */   public double getY() {
/* 76 */     return this.m_Y;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\SingleStatList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */