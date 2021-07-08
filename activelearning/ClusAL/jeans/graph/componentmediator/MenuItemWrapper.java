/*    */ package jeans.graph.componentmediator;
/*    */ 
/*    */ import java.awt.MenuItem;
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
/*    */ public class MenuItemWrapper
/*    */   extends ComponentWrapper
/*    */ {
/*    */   MenuItem item;
/*    */   
/*    */   public MenuItemWrapper(MenuItem item) {
/* 32 */     super(null);
/* 33 */     this.item = item;
/*    */   }
/*    */   
/*    */   public MenuItem getItem() {
/* 37 */     return this.item;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\componentmediator\MenuItemWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */