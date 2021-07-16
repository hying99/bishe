/*    */ package jeans.util.compound;
/*    */ 
/*    */ public class DoubleBooleanCount
/*    */   extends DoubleBoolean {
/*  5 */   protected int m_Count = 1;
/*    */   
/*    */   public DoubleBooleanCount(double val, boolean bol) {
/*  8 */     super(val, bol);
/*    */   }
/*    */   
/*    */   public DoubleBooleanCount(DoubleBooleanCount other) {
/* 12 */     super(other.getDouble(), other.getBoolean().booleanValue());
/* 13 */     this.m_Count = other.getCount();
/*    */   }
/*    */   
/*    */   public void inc() {
/* 17 */     this.m_Count++;
/*    */   }
/*    */   
/*    */   public void inc(DoubleBooleanCount other) {
/* 21 */     this.m_Count += other.getCount();
/*    */   }
/*    */   
/*    */   public int getCount() {
/* 25 */     return this.m_Count;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\compound\DoubleBooleanCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */