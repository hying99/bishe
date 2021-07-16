/*    */ package jeans.util.compound;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class IndexedItem
/*    */   implements Serializable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 31 */   protected int m_Index = -1;
/*    */   
/*    */   public final void setIndex(int idx) {
/* 34 */     this.m_Index = idx;
/*    */   }
/*    */   
/*    */   public final int getIndex() {
/* 38 */     return this.m_Index;
/*    */   }
/*    */   
/*    */   public boolean equalsValue(IndexedItem other) {
/* 42 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\compound\IndexedItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */