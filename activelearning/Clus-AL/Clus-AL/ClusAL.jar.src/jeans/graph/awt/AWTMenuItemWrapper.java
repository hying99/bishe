/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.MenuItem;
/*    */ import jeans.graph.componentmediator.ComponentWrapper;
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
/*    */ public class AWTMenuItemWrapper
/*    */   extends ComponentWrapper
/*    */ {
/*    */   MenuItem item;
/*    */   
/*    */   public AWTMenuItemWrapper(MenuItem item) {
/* 34 */     super(null);
/* 35 */     this.item = item;
/*    */   }
/*    */   
/*    */   public MenuItem getItem() {
/* 39 */     return this.item;
/*    */   }
/*    */   
/*    */   public void output() {
/* 43 */     System.out.println("MenuItemWrapper: " + this.item);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\awt\AWTMenuItemWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */