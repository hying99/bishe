/*    */ package jeans.graph.componentmediator;
/*    */ 
/*    */ import java.awt.Button;
/*    */ import java.awt.Checkbox;
/*    */ import java.awt.Component;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.event.ItemListener;
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
/*    */ public abstract class AWTComponentInterface
/*    */   implements ComponentInterface
/*    */ {
/*    */   public void setEnabled(Component comp, boolean enable) {
/* 31 */     comp.setEnabled(enable);
/*    */   }
/*    */   
/*    */   public void setCheck(Component comp, boolean check) {
/* 35 */     if (comp instanceof Checkbox)
/* 36 */       ((Checkbox)comp).setState(check); 
/*    */   }
/*    */   
/*    */   public boolean isCheck(Component comp) {
/* 40 */     if (comp instanceof Checkbox) {
/* 41 */       return ((Checkbox)comp).getState();
/*    */     }
/* 43 */     return false;
/*    */   }
/*    */   
/*    */   public void addActionListener(ComponentWrapper cw, ActionListener listener) {
/* 47 */     Component comp = cw.getComponent();
/* 48 */     if (comp instanceof Checkbox) {
/* 49 */       ((Checkbox)comp).addItemListener(new MyItemListener(listener));
/* 50 */     } else if (comp instanceof Button) {
/* 51 */       ((Button)comp).addActionListener(listener);
/*    */     } 
/*    */   }
/*    */   
/*    */   private class MyItemListener implements ItemListener {
/*    */     private ActionListener mylistener;
/*    */     
/*    */     private MyItemListener(ActionListener listener) {
/* 59 */       this.mylistener = listener;
/*    */     }
/*    */     
/*    */     public void itemStateChanged(ItemEvent evt) {
/* 63 */       this.mylistener.actionPerformed(new ActionEvent(evt.getSource(), evt.getID(), ""));
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\componentmediator\AWTComponentInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */