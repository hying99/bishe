/*    */ package jeans.util.compound;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class DoubleBoolean
/*    */   implements Comparable, Serializable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected double m_Double;
/*    */   protected boolean m_Boolean;
/*    */   
/*    */   public DoubleBoolean(double val, boolean bol) {
/* 37 */     this.m_Double = val;
/* 38 */     this.m_Boolean = bol;
/*    */   }
/*    */   
/*    */   public double getDouble() {
/* 42 */     return this.m_Double;
/*    */   }
/*    */   
/*    */   public Boolean getBoolean() {
/* 46 */     return Boolean.valueOf(this.m_Boolean);
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 50 */     DoubleBoolean ot = (DoubleBoolean)o;
/* 51 */     return (ot.m_Boolean == this.m_Boolean && ot.m_Double == this.m_Double);
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 55 */     long v = Double.doubleToLongBits(this.m_Double);
/* 56 */     return (int)(v ^ v >>> 32L) ^ (this.m_Boolean ? 1 : 0);
/*    */   }
/*    */   
/*    */   public int compareTo(Object o) {
/* 60 */     DoubleBoolean ot = (DoubleBoolean)o;
/* 61 */     if (this.m_Double == ot.m_Double) return 0; 
/* 62 */     if (this.m_Double < ot.m_Double) return 1; 
/* 63 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\compound\DoubleBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */