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
/*    */ public abstract class EnumerationFilter
/*    */   implements Enumeration
/*    */ {
/*    */   private Enumeration enumeration;
/*    */   private boolean hasMore = true;
/*    */   private boolean advanced = false;
/*    */   private Object currentElement;
/*    */   
/*    */   public EnumerationFilter(Enumeration enumeration) {
/* 44 */     this.enumeration = enumeration;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean hasMoreElements() {
/* 51 */     if (!this.advanced) advanceNext(); 
/* 52 */     return this.hasMore;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object nextElement() {
/* 59 */     this.advanced = false;
/* 60 */     return this.currentElement;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract boolean includeElement(Object paramObject);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void advanceNext() {
/* 75 */     this.advanced = true;
/* 76 */     while (this.enumeration.hasMoreElements()) {
/* 77 */       this.currentElement = this.enumeration.nextElement();
/* 78 */       if (includeElement(this.currentElement))
/*    */         return; 
/* 80 */     }  this.hasMore = false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\EnumerationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */