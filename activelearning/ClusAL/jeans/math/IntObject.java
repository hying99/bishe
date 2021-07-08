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
/*    */ public class IntObject
/*    */   implements Comparable
/*    */ {
/*    */   protected int m_Int;
/*    */   protected Object m_Object;
/*    */   
/*    */   public IntObject(int val, Object obj) {
/* 31 */     this.m_Int = val;
/* 32 */     this.m_Object = obj;
/*    */   }
/*    */   
/*    */   public int getValue() {
/* 36 */     return this.m_Int;
/*    */   }
/*    */   
/*    */   public Object getObject() {
/* 40 */     return this.m_Object;
/*    */   }
/*    */   
/*    */   public int compareTo(Object o) {
/* 44 */     IntObject ot = (IntObject)o;
/* 45 */     if (this.m_Int == ot.m_Int) return 0; 
/* 46 */     if (this.m_Int < ot.m_Int) return 1; 
/* 47 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\IntObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */