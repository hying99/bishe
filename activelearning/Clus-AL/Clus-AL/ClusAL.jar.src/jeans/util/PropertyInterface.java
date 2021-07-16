/*    */ package jeans.util;
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
/*    */ public abstract class PropertyInterface
/*    */ {
/* 30 */   protected Object[] properties = new Object[getNbProperties()];
/*    */ 
/*    */   
/*    */   public Object getProperty(int which) {
/* 34 */     return this.properties[which];
/*    */   }
/*    */   
/*    */   public void setProperty(int which, Object property) {
/* 38 */     this.properties[which] = property;
/*    */   }
/*    */   
/*    */   public int getIntegerProperty(int which) {
/* 42 */     return ((Integer)this.properties[which]).intValue();
/*    */   }
/*    */   
/*    */   public void setIntegerProperty(int which, int property) {
/* 46 */     this.properties[which] = new Integer(property);
/*    */   }
/*    */   
/*    */   public abstract int getNbProperties();
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\PropertyInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */