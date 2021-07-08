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
/*    */ public class PriorityQueue
/*    */ {
/* 27 */   ListElement head = null;
/*    */   
/*    */   public void addElement(ListElement element) {
/* 30 */     if (isEmpty()) {
/* 31 */       this.head = element;
/*    */     }
/* 33 */     else if (((ComparableElement)element).compare((ComparableElement)this.head) != 1) {
/* 34 */       element.setNext(this.head);
/* 35 */       this.head = element;
/*    */     } else {
/* 37 */       ListElement prev = this.head;
/* 38 */       ListElement next = this.head.getNext();
/* 39 */       while (!prev.isTail() && ((ComparableElement)element).compare((ComparableElement)next) == 1) {
/* 40 */         prev = next;
/* 41 */         next = next.getNext();
/*    */       } 
/* 43 */       element.setNext(next);
/* 44 */       prev.setNext(element);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 50 */     return (this.head == null);
/*    */   }
/*    */   
/*    */   public ListElement removeFront() {
/* 54 */     ListElement res = this.head;
/* 55 */     this.head = res.getNext();
/* 56 */     return res;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\list\PriorityQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */