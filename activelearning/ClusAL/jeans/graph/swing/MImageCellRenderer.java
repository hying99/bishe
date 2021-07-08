/*    */ package jeans.graph.swing;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.SystemColor;
/*    */ import javax.swing.JList;
/*    */ import javax.swing.ListCellRenderer;
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
/*    */ public class MImageCellRenderer
/*    */   extends Component
/*    */   implements ListCellRenderer
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   public static final int BORDER = 1;
/*    */   protected Image m_hIcon;
/*    */   protected String m_sName;
/*    */   protected boolean m_bSelected;
/*    */   protected boolean m_bFocus;
/*    */   protected Dimension m_hDimension;
/*    */   protected Component m_hParent;
/*    */   protected int m_iWidth;
/*    */   protected int m_iHeight;
/*    */   protected int m_iIconWd;
/*    */   protected int m_iIconHi;
/*    */   
/*    */   public MImageCellRenderer(Image image, Component parent) {
/* 42 */     this.m_hIcon = image;
/* 43 */     this.m_hParent = parent;
/*    */   }
/*    */   
/*    */   public Dimension getPreferredSize() {
/* 47 */     this.m_hDimension = new Dimension(this.m_iWidth + 2, this.m_iHeight + 2);
/* 48 */     return this.m_hDimension;
/*    */   }
/*    */   
/*    */   public Dimension getMinimumSize() {
/* 52 */     this.m_hDimension = new Dimension(this.m_iWidth + 2, this.m_iHeight + 2);
/* 53 */     return this.m_hDimension;
/*    */   }
/*    */   
/*    */   public void paint(Graphics g) {
/* 57 */     Dimension size = getSize();
/* 58 */     if (this.m_bSelected) {
/* 59 */       g.setColor(SystemColor.textHighlight);
/* 60 */       g.fillRect(0, 0, size.width - 2, size.height - 1);
/* 61 */       g.setColor(SystemColor.textHighlightText);
/*    */     } else {
/* 63 */       g.setColor(SystemColor.textText);
/*    */     } 
/* 65 */     FontMetrics fm = g.getFontMetrics();
/* 66 */     g.drawImage(this.m_hIcon, 1, 1, this);
/* 67 */     g.drawString(this.m_sName, 5 + this.m_iIconWd, size.height / 2 + fm.getAscent() - fm.getHeight() / 2);
/* 68 */     if (this.m_bFocus) {
/* 69 */       g.setColor(Color.black);
/* 70 */       g.drawRect(0, 0, size.width - 2, size.height - 1);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 75 */     this.m_sName = value.toString();
/* 76 */     this.m_bSelected = isSelected;
/* 77 */     this.m_bFocus = cellHasFocus;
/* 78 */     this.m_iIconWd = this.m_hIcon.getWidth(this);
/* 79 */     this.m_iIconHi = this.m_hIcon.getHeight(this);
/* 80 */     FontMetrics fm = this.m_hParent.getGraphics().getFontMetrics();
/* 81 */     this.m_iWidth = this.m_iIconWd + 4 + fm.stringWidth(this.m_sName);
/* 82 */     this.m_iHeight = Math.max(this.m_iIconHi, fm.getHeight());
/* 83 */     this.m_hDimension = new Dimension(this.m_iWidth + 2, this.m_iHeight + 2);
/* 84 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\swing\MImageCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */