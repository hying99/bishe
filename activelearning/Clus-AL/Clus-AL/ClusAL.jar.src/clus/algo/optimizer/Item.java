/*    */ package clus.algo.optimizer;
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
/*    */ public class Item
/*    */ {
/*    */   private Boolean m_Active;
/*    */   
/*    */   public Item(Boolean chosen) {
/* 17 */     this.m_Active = chosen;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Boolean isActive() {
/* 24 */     return this.m_Active;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setActive(Boolean m_Active) {
/* 31 */     this.m_Active = m_Active;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\optimizer\Item.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */