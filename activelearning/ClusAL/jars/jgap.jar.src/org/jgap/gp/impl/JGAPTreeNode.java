/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JGAPTreeNode
/*     */   implements TreeNode
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   private ProgramChromosome m_chrom;
/*     */   private int m_index;
/*     */   
/*     */   public JGAPTreeNode(ProgramChromosome a_chrom, int a_index) {
/*  32 */     this.m_chrom = a_chrom;
/*  33 */     this.m_chrom.redepth();
/*  34 */     this.m_index = a_index;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  38 */     return this.m_chrom.getFunctions()[this.m_index].getClass().getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNode getChildAt(int a_childIndex) {
/*  46 */     int child = this.m_chrom.getChild(this.m_index, a_childIndex);
/*  47 */     return new JGAPTreeNode(this.m_chrom, child);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getChildCount() {
/*  55 */     int count = 0;
/*     */     try {
/*     */       while (true) {
/*  58 */         if (this.m_chrom.getChild(this.m_index, count) < 0) {
/*  59 */           return count;
/*     */         }
/*  61 */         count++;
/*     */       } 
/*  63 */     } catch (RuntimeException rex) {
/*  64 */       return count;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TreeNode getParent() {
/*  72 */     return new JGAPTreeNode(this.m_chrom, this.m_chrom.getParentNode(this.m_index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndex(TreeNode a_node) {
/*  82 */     for (int i = 0; i < getChildCount(); i++) {
/*  83 */       if (getChildAt(i).equals(a_node)) {
/*  84 */         return i;
/*     */       }
/*     */     } 
/*  87 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAllowsChildren() {
/*  94 */     return (getChildCount() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLeaf() {
/* 101 */     return (getChildCount() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration children() {
/* 108 */     Vector<TreeNode> l = new Vector();
/* 109 */     for (int i = 0; i < getChildCount(); i++) {
/* 110 */       l.add(getChildAt(i));
/*     */     }
/* 112 */     return l.elements();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\JGAPTreeNode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */