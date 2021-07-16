/*    */ package jeans.io.range;
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
/*    */ public class DoubleRangeCheck
/*    */   implements ValueCheck
/*    */ {
/*    */   protected double m_MinDouble;
/*    */   protected double m_MaxDouble;
/*    */   
/*    */   public DoubleRangeCheck(double min, double max) {
/* 30 */     this.m_MinDouble = min;
/* 31 */     this.m_MaxDouble = max;
/*    */   }
/*    */   
/*    */   public boolean checkValue(Object value) {
/* 35 */     double number = ((Double)value).doubleValue();
/* 36 */     return (number >= this.m_MinDouble && number <= this.m_MaxDouble);
/*    */   }
/*    */   
/*    */   public String getString(String name, Object value) {
/* 40 */     return name + " = " + value + " out of range [" + this.m_MinDouble + ", " + this.m_MaxDouble + "]";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\range\DoubleRangeCheck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */