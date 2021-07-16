/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.Button;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
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
/*    */ public class SwitchButton
/*    */   extends Button
/*    */   implements ActionListener
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   private boolean state;
/*    */   private String normal;
/*    */   private String selected;
/*    */   
/*    */   public SwitchButton(String normal, String selected) {
/* 36 */     super(normal);
/* 37 */     this.normal = normal;
/* 38 */     this.selected = selected;
/* 39 */     addActionListener(this);
/*    */   }
/*    */   
/*    */   public void actionPerformed(ActionEvent evt) {
/* 43 */     setState(!this.state);
/*    */   }
/*    */   
/*    */   public void setState(boolean mystate) {
/* 47 */     this.state = mystate;
/* 48 */     if (this.state) { setLabel(this.selected); }
/* 49 */     else { setLabel(this.normal); }
/*    */   
/*    */   }
/*    */   public boolean getState() {
/* 53 */     return this.state;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\awt\SwitchButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */