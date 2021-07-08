/*    */ package jeans.io.ini;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
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
/*    */ public abstract class INIFileNode
/*    */   implements Serializable
/*    */ {
/*    */   protected String m_hName;
/*    */   protected INIFileNode m_hParent;
/*    */   protected boolean m_Enabled = true;
/*    */   
/*    */   public INIFileNode(String name) {
/* 34 */     this.m_hName = name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 38 */     this.m_hName = name;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 42 */     return this.m_hName;
/*    */   }
/*    */   
/*    */   public void setEnabled(boolean enable) {
/* 46 */     this.m_Enabled = enable;
/*    */   }
/*    */   
/*    */   public boolean isEnabled() {
/* 50 */     return this.m_Enabled;
/*    */   }
/*    */   
/*    */   public int getDepth() {
/* 54 */     int depth = 0;
/* 55 */     INIFileNode node = getParent();
/* 56 */     while (node != null) {
/* 57 */       if (!node.isSectionGroup()) depth++; 
/* 58 */       node = node.getParent();
/*    */     } 
/* 60 */     return depth;
/*    */   }
/*    */   
/*    */   public String getPathName(String my_name) {
/* 64 */     String name = my_name;
/* 65 */     INIFileNode node = this;
/* 66 */     while (node != null) {
/* 67 */       if (!node.isSectionGroup()) {
/* 68 */         INIFileNode parent = node.getParent();
/* 69 */         if (parent != null && parent.isSectionGroup()) {
/* 70 */           name = parent.getName() + "." + name;
/*    */         } else {
/* 72 */           name = node.getName() + "." + name;
/*    */         } 
/*    */       } 
/* 75 */       node = node.getParent();
/*    */     } 
/* 77 */     return name;
/*    */   }
/*    */   
/*    */   public void setParent(INIFileNode node) {
/* 81 */     this.m_hParent = node;
/*    */   }
/*    */   
/*    */   public INIFileNode getParent() {
/* 85 */     return this.m_hParent;
/*    */   }
/*    */   
/*    */   public boolean hasParent() {
/* 89 */     return (this.m_hParent != null);
/*    */   }
/*    */   
/*    */   public abstract INIFileNode cloneNode();
/*    */   
/*    */   public abstract boolean isSectionGroup();
/*    */   
/*    */   public abstract boolean isSection();
/*    */   
/*    */   public abstract void save(PrintWriter paramPrintWriter) throws IOException;
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */