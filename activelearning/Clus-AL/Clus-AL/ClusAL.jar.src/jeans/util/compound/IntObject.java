/*    */ package jeans.util.compound;
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
/*    */   public void setValue(int value) {
/* 40 */     this.m_Int = value;
/*    */   }
/*    */   
/*    */   public void incValue() {
/* 44 */     this.m_Int++;
/*    */   }
/*    */   
/*    */   public Object getObject() {
/* 48 */     return this.m_Object;
/*    */   }
/*    */   
/*    */   public int compareTo(Object o) {
/* 52 */     IntObject ot = (IntObject)o;
/* 53 */     if (this.m_Int == ot.m_Int) return 0; 
/* 54 */     if (this.m_Int < ot.m_Int) return 1; 
/* 55 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\compound\IntObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */