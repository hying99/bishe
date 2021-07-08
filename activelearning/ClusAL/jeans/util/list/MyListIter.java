/*    */ package jeans.util.list;
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
/*    */ public class MyListIter
/*    */   extends MyList
/*    */ {
/*    */   protected MyList m_Prev;
/*    */   protected MyList m_Curr;
/*    */   protected MyList m_Last;
/*    */   
/*    */   public MyListIter() {
/* 32 */     this.m_Prev = this;
/* 33 */     this.m_Last = this;
/*    */   }
/*    */   
/*    */   public MyListIter(MyList first) {
/* 37 */     this.m_Prev = this;
/* 38 */     this.m_Next = this.m_Curr = first;
/*    */   }
/*    */   
/*    */   public final void reset() {
/* 42 */     this.m_Prev = this;
/* 43 */     this.m_Curr = this;
/*    */   }
/*    */   
/*    */   public final MyList getFirst() {
/* 47 */     this.m_Prev = this;
/* 48 */     this.m_Curr = this.m_Next;
/* 49 */     return this.m_Curr;
/*    */   }
/*    */   
/*    */   public final MyList getNext() {
/* 53 */     MyList next = this.m_Curr.m_Next;
/* 54 */     if (next != null) {
/* 55 */       this.m_Prev = this.m_Curr;
/* 56 */       this.m_Curr = next;
/*    */     } 
/* 58 */     return next;
/*    */   }
/*    */   
/*    */   public final boolean hasNext() {
/* 62 */     return (this.m_Curr.m_Next != null);
/*    */   }
/*    */   
/*    */   public final boolean isEmpty() {
/* 66 */     return (this.m_Last == this);
/*    */   }
/*    */   
/*    */   public final void addEnd(MyList elem) {
/* 70 */     this.m_Last.m_Next = elem;
/* 71 */     this.m_Last = elem;
/*    */   }
/*    */   
/*    */   public final void insertBefore(MyList elem) {
/* 75 */     this.m_Prev.m_Next = elem;
/* 76 */     elem.m_Next = this.m_Curr;
/* 77 */     this.m_Prev = elem;
/*    */   }
/*    */   
/*    */   public final MyList deleteElement() {
/* 81 */     this.m_Curr = this.m_Curr.m_Next;
/* 82 */     this.m_Prev.m_Next = this.m_Curr;
/* 83 */     return this.m_Curr;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\list\MyListIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */