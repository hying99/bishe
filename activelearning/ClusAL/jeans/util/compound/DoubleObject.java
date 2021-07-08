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
/*    */ 
/*    */ public class DoubleObject
/*    */   implements Comparable
/*    */ {
/*    */   protected double m_Double;
/*    */   protected Object m_Object;
/*    */   
/*    */   public DoubleObject() {}
/*    */   
/*    */   public DoubleObject(double val, Object obj) {
/* 34 */     this.m_Double = val;
/* 35 */     this.m_Object = obj;
/*    */   }
/*    */   
/*    */   public double getValue() {
/* 39 */     return this.m_Double;
/*    */   }
/*    */   
/*    */   public Object getObject() {
/* 43 */     return this.m_Object;
/*    */   }
/*    */   
/*    */   public void set(double val, Object obj) {
/* 47 */     this.m_Double = val;
/* 48 */     this.m_Object = obj;
/*    */   }
/*    */   
/*    */   public int compareTo(Object o) {
/* 52 */     DoubleObject ot = (DoubleObject)o;
/* 53 */     if (this.m_Double == ot.m_Double) return 0; 
/* 54 */     if (this.m_Double < ot.m_Double) return 1; 
/* 55 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\compound\DoubleObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */