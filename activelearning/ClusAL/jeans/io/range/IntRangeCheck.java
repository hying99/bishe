/*    */ package jeans.io.range;
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
/*    */ public class IntRangeCheck
/*    */   implements ValueCheck, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -4723124353605331288L;
/*    */   protected int m_MinInt;
/*    */   protected int m_MaxInt;
/*    */   
/*    */   public IntRangeCheck(int min, int max) {
/* 33 */     this.m_MinInt = min;
/* 34 */     this.m_MaxInt = max;
/*    */   }
/*    */   
/*    */   public boolean checkValue(Object value) {
/* 38 */     int number = ((Integer)value).intValue();
/* 39 */     return (number >= this.m_MinInt && number <= this.m_MaxInt);
/*    */   }
/*    */   
/*    */   public String getString(String name, Object value) {
/* 43 */     return name + " = " + value + " out of range [" + this.m_MinInt + ", " + this.m_MaxInt + "]";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\range\IntRangeCheck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */