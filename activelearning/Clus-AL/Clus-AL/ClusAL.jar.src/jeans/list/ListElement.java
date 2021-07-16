/*    */ package jeans.list;
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
/*    */ public class ListElement
/*    */ {
/*    */   ListElement next;
/*    */   
/*    */   public void setNext(ListElement next) {
/* 30 */     this.next = next;
/*    */   }
/*    */   
/*    */   public ListElement getNext() {
/* 34 */     return this.next;
/*    */   }
/*    */   
/*    */   public boolean isTail() {
/* 38 */     return (this.next == null);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\list\ListElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */