/*     */ package org.jgap.util.tree;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.RenderedImage;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.ImageIO;
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
/*     */ public class TreeVisualizer
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*  33 */   private int side = 512;
/*     */   
/*  35 */   private double circleDiminishFactor = 0.875D;
/*     */   
/*  37 */   private double branchStartWidth = 16.0D;
/*     */   
/*  39 */   private double branchDiminshFactor = 0.6666D;
/*     */   
/*  41 */   private TreeBranchRenderer tbr = null;
/*     */   
/*  43 */   private TreeNodeRenderer tnr = null;
/*     */   
/*  45 */   private Color bkgndColor = Color.white;
/*     */   
/*  47 */   private Color arenaColor = Color.black;
/*     */   
/*  49 */   private Color branchColor = Color.white;
/*     */   
/*  51 */   private Color nodeColor = Color.red;
/*     */   
/*     */   private boolean renderNodes = true;
/*     */   
/*  55 */   private int ignorePastLevel = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage renderTree(TreeNode tn) {
/*  61 */     BufferedImage bufferedImage = new BufferedImage(this.side, this.side, 1);
/*     */ 
/*     */     
/*  64 */     Graphics2D g2d = bufferedImage.createGraphics();
/*     */     
/*  66 */     g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
/*     */ 
/*     */     
/*  69 */     g2d.setColor(this.bkgndColor);
/*  70 */     g2d.fillRect(0, 0, this.side, this.side);
/*  71 */     g2d.setColor(this.arenaColor);
/*  72 */     g2d.fillOval(0, 0, this.side, this.side);
/*  73 */     g2d.setColor(this.branchColor);
/*  74 */     drawBranches(g2d, tn, 0, 0.0D, 6.283185307179586D);
/*  75 */     if (this.renderNodes) {
/*  76 */       g2d.setColor(this.nodeColor);
/*  77 */       drawNodes(g2d, tn, 0, 0.0D, 6.283185307179586D);
/*     */     } 
/*  79 */     g2d.dispose();
/*  80 */     return bufferedImage;
/*     */   }
/*     */   
/*     */   public void writeImageFile(RenderedImage ri, File f) {
/*     */     try {
/*  85 */       ImageIO.write(ri, "png", f);
/*  86 */     } catch (IOException e) {
/*  87 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Point drawBranches(Graphics2D g, TreeNode tn, int level, double start, double finish) {
/*  93 */     double span = finish - start;
/*  94 */     double middle = start + span / 2.0D;
/*  95 */     Point middlePoint = radToCart(getR(level), middle, this.side / 2, this.side / 2);
/*  96 */     Stroke strokeSize = getStroke(level);
/*  97 */     if (!tn.isLeaf()) {
/*  98 */       if (this.ignorePastLevel >= 0 && 
/*  99 */         this.ignorePastLevel < level + 1) {
/* 100 */         return middlePoint;
/*     */       }
/*     */       
/* 103 */       double subSection = span / tn.getChildCount();
/* 104 */       double s1 = start;
/* 105 */       double s2 = start + subSection;
/* 106 */       for (int i = 0; i < tn.getChildCount(); i++) {
/* 107 */         TreeNode tn2 = tn.getChildAt(i);
/* 108 */         Point connectPoint = drawBranches(g, tn2, level + 1, s1, s2);
/* 109 */         g.setStroke(strokeSize);
/* 110 */         if (this.tbr != null) {
/* 111 */           Color nc = this.tbr.getBranchColor(tn, level);
/* 112 */           if (nc != null) {
/* 113 */             g.setColor(nc);
/*     */           }
/*     */         } 
/* 116 */         g.drawLine((int)middlePoint.getX(), (int)middlePoint.getY(), (int)connectPoint.getX(), (int)connectPoint.getY());
/*     */         
/* 118 */         s1 += subSection;
/* 119 */         s2 += subSection;
/*     */       } 
/*     */     } 
/* 122 */     return middlePoint;
/*     */   }
/*     */ 
/*     */   
/*     */   private void drawNodes(Graphics2D g, TreeNode tn, int level, double start, double finish) {
/* 127 */     double span = finish - start;
/* 128 */     double middle = start + span / 2.0D;
/* 129 */     Point middlePoint = radToCart(getR(level), middle, this.side / 2, this.side / 2);
/* 130 */     double x = middlePoint.getX();
/* 131 */     double y = middlePoint.getY();
/* 132 */     g.setStroke(new BasicStroke(0.0F));
/* 133 */     double r = this.branchStartWidth * Math.pow(this.branchDiminshFactor, level);
/* 134 */     if ((int)(2.0D * r) > 0) {
/* 135 */       if (this.tnr != null) {
/* 136 */         Color nc = this.tnr.getNodeColor(tn, level);
/* 137 */         if (nc != null) {
/* 138 */           g.setColor(nc);
/*     */         }
/*     */       } 
/* 141 */       g.fillOval((int)(x - r), (int)(y - r), (int)(2.0D * r), (int)(2.0D * r));
/*     */     } 
/* 143 */     if (!tn.isLeaf()) {
/* 144 */       if (this.ignorePastLevel >= 0 && 
/* 145 */         this.ignorePastLevel < level + 1) {
/*     */         return;
/*     */       }
/*     */       
/* 149 */       double subSection = span / tn.getChildCount();
/* 150 */       double s1 = start;
/* 151 */       double s2 = start + subSection;
/* 152 */       for (int i = 0; i < tn.getChildCount(); i++) {
/* 153 */         TreeNode tn2 = tn.getChildAt(i);
/* 154 */         drawNodes(g, tn2, level + 1, s1, s2);
/* 155 */         s1 += subSection;
/* 156 */         s2 += subSection;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private Stroke getStroke(int level) {
/* 162 */     return new BasicStroke((float)(this.branchStartWidth * Math.pow(this.branchDiminshFactor, level)), 1, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Point radToCart(double r, double theta, int x, int y) {
/* 169 */     theta += 1.5707963267948966D;
/* 170 */     int nx = (int)(r * Math.cos(theta)) + x;
/* 171 */     int ny = (int)(r * Math.sin(theta)) + y;
/* 172 */     return new Point(nx, ny);
/*     */   }
/*     */   
/*     */   private double getR(int level) {
/* 176 */     double r = (this.side / 2) - (this.side / 2) * Math.pow(this.circleDiminishFactor, level);
/*     */     
/* 178 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSide(int s) {
/* 183 */     this.side = s;
/*     */   }
/*     */   
/*     */   public int getSide() {
/* 187 */     return this.side;
/*     */   }
/*     */   
/*     */   public void setCircleDiminishFactor(double c) {
/* 191 */     this.circleDiminishFactor = c;
/*     */   }
/*     */   
/*     */   public double getCircleDiminishFactor() {
/* 195 */     return this.circleDiminishFactor;
/*     */   }
/*     */   
/*     */   public void setBranchStartWidth(double b) {
/* 199 */     this.branchStartWidth = b;
/*     */   }
/*     */   
/*     */   public double getBranchStartWidth() {
/* 203 */     return this.branchStartWidth;
/*     */   }
/*     */   
/*     */   public void setBranchDiminishFactor(double s) {
/* 207 */     this.branchDiminshFactor = s;
/*     */   }
/*     */   
/*     */   public double getBranchDiminshFactor() {
/* 211 */     return this.branchDiminshFactor;
/*     */   }
/*     */   
/*     */   public void setBkgndColor(Color c) {
/* 215 */     this.bkgndColor = c;
/*     */   }
/*     */   
/*     */   public Color getBkgndColor() {
/* 219 */     return this.bkgndColor;
/*     */   }
/*     */   
/*     */   public void setArenaColor(Color c) {
/* 223 */     this.arenaColor = c;
/*     */   }
/*     */   
/*     */   public Color getArenaColor() {
/* 227 */     return this.arenaColor;
/*     */   }
/*     */   
/*     */   public void setBranchColor(Color c) {
/* 231 */     this.branchColor = c;
/*     */   }
/*     */   
/*     */   public Color getBranchColor() {
/* 235 */     return this.branchColor;
/*     */   }
/*     */   
/*     */   public void setNodeColor(Color c) {
/* 239 */     this.nodeColor = c;
/*     */   }
/*     */   
/*     */   public Color getNodeColor() {
/* 243 */     return this.nodeColor;
/*     */   }
/*     */   
/*     */   public void setTreeBranchRenderer(TreeBranchRenderer ntbr) {
/* 247 */     this.tbr = ntbr;
/*     */   }
/*     */   
/*     */   public TreeBranchRenderer getTreeBranchRenderer() {
/* 251 */     return this.tbr;
/*     */   }
/*     */   
/*     */   public void setTreeNodeRenderer(TreeNodeRenderer ntnr) {
/* 255 */     this.tnr = ntnr;
/*     */   }
/*     */   
/*     */   public TreeNodeRenderer getTreeNodeRenderer() {
/* 259 */     return this.tnr;
/*     */   }
/*     */   
/*     */   public void setRenderNodes(boolean r) {
/* 263 */     this.renderNodes = r;
/*     */   }
/*     */   
/*     */   public boolean getRenderNodes() {
/* 267 */     return this.renderNodes;
/*     */   }
/*     */   
/*     */   public void setIgnorePastLevel(int i) {
/* 271 */     this.ignorePastLevel = i;
/*     */   }
/*     */   
/*     */   public int getIgnorePastLevel() {
/* 275 */     return this.ignorePastLevel;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\tree\TreeVisualizer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */