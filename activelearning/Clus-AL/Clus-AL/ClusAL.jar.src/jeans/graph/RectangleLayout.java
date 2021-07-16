/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Container;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Insets;
/*    */ import java.awt.LayoutManager;
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
/*    */ public class RectangleLayout
/*    */   implements LayoutManager
/*    */ {
/* 46 */   protected int m_PrefWidth = 800;
/* 47 */   protected int m_PrefHeight = 600;
/* 48 */   protected Dimension m_MinSize = new Dimension(30, 30);
/*    */ 
/*    */ 
/*    */   
/*    */   public void addLayoutComponent(String name, Component comp) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void removeLayoutComponent(Component comp) {}
/*    */ 
/*    */   
/*    */   public int getComponentCount(Container parent) {
/* 60 */     return parent.getComponentCount();
/*    */   }
/*    */   
/*    */   public Dimension preferredLayoutSize(Container parent) {
/* 64 */     Dimension dim = new Dimension(0, 0);
/*    */     
/* 66 */     Insets insets = parent.getInsets();
/* 67 */     dim.width = this.m_PrefWidth + insets.left + insets.right;
/* 68 */     dim.height = this.m_PrefHeight + insets.top + insets.bottom;
/* 69 */     return dim;
/*    */   }
/*    */   
/*    */   public Dimension minimumLayoutSize(Container parent) {
/* 73 */     return this.m_MinSize;
/*    */   }
/*    */   
/*    */   public void layoutContainer(Container parent) {
/* 77 */     Insets ins = parent.getInsets();
/* 78 */     int maxWidth = (parent.getSize()).width - ins.left + ins.right;
/* 79 */     int maxHeight = (parent.getSize()).height - ins.top + ins.bottom;
/* 80 */     int nComps = getComponentCount(parent);
/* 81 */     if (nComps == 2) {
/* 82 */       Component c1 = parent.getComponent(0);
/* 83 */       if (c1.isVisible()) {
/* 84 */         c1.setBounds(ins.left, ins.top, maxHeight, maxHeight);
/*    */       }
/* 86 */       Component c2 = parent.getComponent(1);
/* 87 */       if (c2.isVisible()) {
/* 88 */         c2.setBounds(ins.left + maxHeight, ins.top, maxWidth - maxHeight, maxHeight);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public String toString() {
/* 94 */     return "RectangleLayout";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\RectangleLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */