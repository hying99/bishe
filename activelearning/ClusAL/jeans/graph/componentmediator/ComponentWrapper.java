/*    */ package jeans.graph.componentmediator;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Vector;
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
/*    */ public class ComponentWrapper
/*    */ {
/*    */   private Component comp;
/*    */   private int enable_cnt;
/*    */   private Vector my_events;
/*    */   private boolean my_enable = true;
/*    */   
/*    */   public ComponentWrapper(Component comp) {
/* 36 */     this.comp = comp;
/*    */   }
/*    */   
/*    */   public Component getComponent() {
/* 40 */     return this.comp;
/*    */   }
/*    */   
/*    */   public boolean groupShouldSetEnabled(boolean enable) {
/* 44 */     if (enable)
/* 45 */     { if (this.enable_cnt > 0 && --this.enable_cnt == 0) return true;
/*    */        }
/* 47 */     else if (this.enable_cnt++ == 0) { return true; }
/*    */     
/* 49 */     return false;
/*    */   }
/*    */   
/*    */   public boolean shouldSetEnabled(boolean enable) {
/* 53 */     if (this.my_enable == enable) return false; 
/* 54 */     this.my_enable = enable;
/* 55 */     return groupShouldSetEnabled(enable);
/*    */   }
/*    */   
/*    */   public boolean hasEvents() {
/* 59 */     return (this.my_events != null);
/*    */   }
/*    */   
/*    */   public void addAction(ComponentAction action) {
/* 63 */     if (!hasEvents()) this.my_events = new Vector(); 
/* 64 */     this.my_events.addElement(action);
/*    */   }
/*    */   
/*    */   public Enumeration getActions() {
/* 68 */     return this.my_events.elements();
/*    */   }
/*    */   
/*    */   public void output() {
/* 72 */     System.out.println("Component: " + this.comp + " Count: " + this.enable_cnt);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\componentmediator\ComponentWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */