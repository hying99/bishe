/*     */ package org.jgap.gp;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.File;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.function.SubProgram;
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.GPGenotype;
/*     */ import org.jgap.gp.impl.JGAPTreeBranchRenderer;
/*     */ import org.jgap.gp.impl.JGAPTreeNode;
/*     */ import org.jgap.gp.impl.JGAPTreeNodeRenderer;
/*     */ import org.jgap.gp.impl.ProgramChromosome;
/*     */ import org.jgap.util.tree.TreeBranchRenderer;
/*     */ import org.jgap.util.tree.TreeNodeRenderer;
/*     */ import org.jgap.util.tree.TreeVisualizer;
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
/*     */ public abstract class GPProblem
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*     */   private GPConfiguration m_conf;
/*     */   
/*     */   public GPProblem(GPConfiguration a_conf) throws InvalidConfigurationException {
/*  37 */     if (a_conf == null) {
/*  38 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*     */     }
/*  40 */     this.m_conf = a_conf;
/*     */   }
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
/*     */   public GPProblem() {}
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
/*     */   public abstract GPGenotype create() throws InvalidConfigurationException;
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
/*     */   public void showTree(IGPProgram a_prog, String a_filename) throws InvalidConfigurationException {
/*  75 */     if (a_prog == null) {
/*     */       return;
/*     */     }
/*  78 */     TreeNode myTree = createTree(a_prog);
/*  79 */     if (myTree == null) {
/*     */       return;
/*     */     }
/*  82 */     TreeVisualizer tv = new TreeVisualizer();
/*  83 */     tv.setTreeBranchRenderer((TreeBranchRenderer)new JGAPTreeBranchRenderer());
/*  84 */     tv.setTreeNodeRenderer((TreeNodeRenderer)new JGAPTreeNodeRenderer());
/*  85 */     tv.setBranchStartWidth(18.0D);
/*  86 */     tv.setArenaColor(Color.black);
/*  87 */     tv.setBkgndColor(Color.black);
/*  88 */     tv.setRenderNodes(true);
/*  89 */     tv.setSide(1024);
/*  90 */     tv.setCircleDiminishFactor(0.5D);
/*  91 */     tv.writeImageFile(tv.renderTree(myTree), new File(a_filename));
/*     */   }
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
/*     */   public void showTree(IGPProgram a_prog, String a_filename, TreeBranchRenderer a_treeBranchRenderer, TreeNodeRenderer a_treeNodeRenderer) throws InvalidConfigurationException {
/* 111 */     TreeNode myTree = createTree(a_prog);
/* 112 */     if (myTree == null) {
/*     */       return;
/*     */     }
/* 115 */     TreeVisualizer tv = new TreeVisualizer();
/* 116 */     tv.setTreeBranchRenderer(a_treeBranchRenderer);
/* 117 */     tv.setTreeNodeRenderer(a_treeNodeRenderer);
/* 118 */     tv.setBranchStartWidth(18.0D);
/* 119 */     tv.setArenaColor(Color.black);
/* 120 */     tv.setBkgndColor(Color.black);
/* 121 */     tv.setRenderNodes(true);
/* 122 */     tv.setSide(1024);
/* 123 */     tv.setCircleDiminishFactor(0.5D);
/* 124 */     tv.writeImageFile(tv.renderTree(myTree), new File(a_filename));
/*     */   }
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
/*     */   public TreeNode createTree(IGPProgram a_prog) throws InvalidConfigurationException {
/*     */     JGAPTreeNode jGAPTreeNode;
/* 139 */     if (a_prog == null) {
/* 140 */       return null;
/*     */     }
/* 142 */     ProgramChromosome master = new ProgramChromosome(this.m_conf);
/* 143 */     master.setIndividual(a_prog);
/*     */     
/* 145 */     if (a_prog.size() > 1) {
/* 146 */       Class[] types = new Class[a_prog.size()];
/* 147 */       for (int i = 0; i < a_prog.size(); i++) {
/* 148 */         types[i] = CommandGene.VoidClass;
/*     */       }
/* 150 */       master.setGene(0, (CommandGene)new SubProgram(this.m_conf, types));
/* 151 */       int index = 1;
/* 152 */       for (int j = 0; j < a_prog.size(); j++) {
/* 153 */         ProgramChromosome child = a_prog.getChromosome(j);
/* 154 */         for (int k = 0; k < child.size(); k++) {
/* 155 */           master.setGene(index++, child.getGene(k));
/*     */         }
/*     */       } 
/* 158 */       master.redepth();
/* 159 */       jGAPTreeNode = new JGAPTreeNode(master, 0);
/*     */     } else {
/*     */       
/* 162 */       jGAPTreeNode = new JGAPTreeNode(a_prog.getChromosome(0), 0);
/*     */     } 
/* 164 */     return (TreeNode)jGAPTreeNode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPConfiguration getGPConfiguration() {
/* 174 */     return this.m_conf;
/*     */   }
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
/*     */   protected void setGPConfiguration(GPConfiguration a_conf) {
/* 187 */     this.m_conf = a_conf;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\GPProblem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */