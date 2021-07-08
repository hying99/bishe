/*    */ package jeans.graph.swing;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.ButtonGroup;
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
/*    */ public class MRadioButtonGroup
/*    */ {
/* 30 */   protected ButtonGroup m_hGroup = new ButtonGroup();
/*    */   
/*    */   public void add(AbstractButton b) {
/* 33 */     this.m_hGroup.add(b);
/*    */   }
/*    */   
/*    */   public void setSelectedIndex(int idx) {
/* 37 */     int nb = 0;
/* 38 */     for (Enumeration<AbstractButton> e = this.m_hGroup.getElements(); e.hasMoreElements(); ) {
/* 39 */       AbstractButton b = e.nextElement();
/* 40 */       if (nb == idx) {
/* 41 */         b.setSelected(true);
/*    */         break;
/*    */       } 
/* 44 */       nb++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\swing\MRadioButtonGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */