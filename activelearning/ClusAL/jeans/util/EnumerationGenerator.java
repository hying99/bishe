/*    */ package jeans.util;
/*    */ 
/*    */ import java.util.Enumeration;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class EnumerationGenerator
/*    */   implements Enumeration
/*    */ {
/*    */   private boolean advanced = false;
/*    */   private Object currentElement;
/*    */   
/*    */   public boolean hasMoreElements() {
/* 42 */     if (!this.advanced) {
/* 43 */       this.advanced = true;
/* 44 */       this.currentElement = generateElement();
/*    */     } 
/* 46 */     return (this.currentElement != null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object nextElement() {
/* 53 */     this.advanced = false;
/* 54 */     return this.currentElement;
/*    */   }
/*    */   
/*    */   public abstract Object generateElement();
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\EnumerationGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */