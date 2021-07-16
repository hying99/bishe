/*     */ package clus.gui;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.gui.statvis.ClassStatVis;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import jeans.graph.swing.drawable.Drawable;
/*     */ import jeans.graph.swing.drawable.DrawableCanvas;
/*     */ import jeans.graph.swing.drawable.DrawableExpandButton;
/*     */ import jeans.graph.swing.drawable.DrawableLines;
/*     */ import jeans.graph.tree.MyDrawableNode;
/*     */ import jeans.graph.tree.MyNodePainter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusNodePainter
/*     */   extends MyNodePainter
/*     */   implements ActionListener
/*     */ {
/*     */   protected DrawableExpandButton m_Button;
/*     */   protected DrawableLines m_Lines;
/*     */   protected DrawableLines m_Label;
/*     */   protected Drawable m_StatVis;
/*  44 */   protected static final Font font1 = new Font("Times", 0, 12);
/*  45 */   protected static final Font font2 = new Font("Times", 0, 8);
/*  46 */   protected static final Font font3 = new Font("Times", 0, 10);
/*     */   
/*     */   public ClusNodePainter() {
/*  49 */     super(null);
/*     */   }
/*     */   
/*     */   public ClusNodePainter(MyDrawableNode node) {
/*  53 */     super(node); String[] lines;
/*  54 */     ClusNode cnode = (ClusNode)node.getVisitor(0);
/*     */     
/*  56 */     if (cnode.hasBestTest()) {
/*  57 */       NodeTest test = cnode.getTest();
/*  58 */       int nb = test.getNbLines();
/*     */       
/*  60 */       lines = new String[nb];
/*  61 */       for (int i = 0; i < nb; i++) {
/*  62 */         lines[i] = test.getLine(i);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  71 */       TreePanel panel = (TreePanel)this.m_Node.getPaintSettings().getDocument();
/*  72 */       ClusStatistic stat = cnode.getClusteringStat();
/*  73 */       this.m_StatVis = panel.createStatVisualiser(stat);
/*     */       
/*  75 */       this.m_Button = new DrawableExpandButton(8, 8, this.m_Node.isFakeLeaf());
/*  76 */       this.m_Button.setActionListener(this);
/*     */     } else {
/*  78 */       ClusStatistic stat = cnode.getClusteringStat();
/*  79 */       lines = new String[1];
/*     */       
/*  81 */       lines[0] = stat.getSimpleString();
/*  82 */       TreePanel panel = (TreePanel)this.m_Node.getPaintSettings().getDocument();
/*  83 */       this.m_StatVis = panel.createStatVisualiser(stat);
/*     */     } 
/*  85 */     this.m_Label = createLabel(cnode, node);
/*  86 */     this.m_Lines = new DrawableLines(lines);
/*     */   }
/*     */   
/*     */   public MyNodePainter createPainter(MyDrawableNode node) {
/*  90 */     return new ClusNodePainter(node);
/*     */   }
/*     */   
/*     */   public DrawableExpandButton getExpandButton() {
/*  94 */     return this.m_Button;
/*     */   }
/*     */   
/*     */   public Drawable getLabel() {
/*  98 */     if (getZoom() <= 1)
/*     */     {
/* 100 */       return null;
/*     */     }
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onFakeLeaf(boolean fake) {
/* 107 */     if (this.m_Button != null) this.m_Button.setState(fake); 
/*     */   }
/*     */   
/*     */   public boolean mousePressed(DrawableCanvas cnv, int x, int y, MouseEvent evt) {
/*     */     try {
/* 112 */       if (evt.isPopupTrigger() || evt.getButton() == 2 || evt.getButton() == 3) {
/* 113 */         JPopupMenu pop = new JPopupMenu();
/* 114 */         pop.add(makeZoomMenu("Zoom node", false));
/* 115 */         if (!this.m_Node.atBottomLevel()) {
/* 116 */           pop.add(makeZoomMenu("Zoom subtree", true));
/* 117 */           JMenuItem jMenuItem = new JMenuItem("Expand subtree");
/* 118 */           jMenuItem.addActionListener(new MyExpandListener());
/* 119 */           pop.add(jMenuItem);
/* 120 */           jMenuItem = new JMenuItem("Prune subtree");
/* 121 */           jMenuItem.addActionListener(new MyPruneListener());
/* 122 */           pop.add(jMenuItem);
/*     */         } 
/* 124 */         JMenuItem item = new JMenuItem("Properties");
/* 125 */         pop.add(item);
/* 126 */         pop.show((Component)cnv, evt.getX(), evt.getY());
/*     */       } else {
/* 128 */         TreePanel panel = (TreePanel)this.m_Node.getPaintSettings().getDocument();
/* 129 */         ClusNode cnode = (ClusNode)this.m_Node.getVisitor(0);
/* 130 */         panel.showInfo(cnode);
/*     */       } 
/* 132 */     } catch (ClusException ex) {
/* 133 */       System.err.println("Clus error: " + ex.getMessage());
/*     */     } 
/* 135 */     return true;
/*     */   }
/*     */   
/*     */   public JMenu makeZoomMenu(String title, boolean isSubTree) {
/* 139 */     JMenu menu = new JMenu(title);
/* 140 */     JMenuItem item = new JMenuItem("Zoom 0");
/* 141 */     item.addActionListener(new MyZoomListener(0, isSubTree));
/* 142 */     menu.add(item);
/* 143 */     item = new JMenuItem("Zoom 1");
/* 144 */     item.addActionListener(new MyZoomListener(1, isSubTree));
/* 145 */     menu.add(item);
/* 146 */     item = new JMenuItem("Zoom 2");
/* 147 */     item.addActionListener(new MyZoomListener(2, isSubTree));
/* 148 */     menu.add(item);
/* 149 */     item = new JMenuItem("Zoom 3");
/* 150 */     item.addActionListener(new MyZoomListener(3, isSubTree));
/* 151 */     menu.add(item);
/* 152 */     item = new JMenuItem("Zoom 4");
/* 153 */     item.addActionListener(new MyZoomListener(4, isSubTree));
/* 154 */     menu.add(item);
/* 155 */     return menu;
/*     */   }
/*     */   
/*     */   public boolean mouseSensitive() {
/* 159 */     return true;
/*     */   }
/*     */   
/*     */   public void calcSize(Graphics2D g, FontMetrics fm, DrawableCanvas cnv) {
/* 163 */     if (getZoom() <= 1) {
/* 164 */       if (getZoom() == 0) {
/*     */         
/* 166 */         this.m_Lines.setFont(font1);
/* 167 */         if (this.m_Label != null) this.m_Label.setFont(font1);
/*     */       
/*     */       } else {
/* 170 */         this.m_Lines.setFont(font2);
/* 171 */         if (this.m_Label != null) this.m_Label.setFont(font2); 
/*     */       } 
/* 173 */       this.m_Lines.calcSize(g, fm, cnv);
/* 174 */       if (this.m_Label != null) this.m_Label.calcSize(g, fm, cnv); 
/* 175 */       this.wd = this.m_Lines.getWidth();
/* 176 */       this.hi = this.m_Lines.getHeight();
/* 177 */       if (this.m_StatVis != null)
/* 178 */         if (getZoom() == 0) {
/* 179 */           this.hi += 12;
/*     */         } else {
/* 181 */           this.hi += 5;
/*     */         }  
/*     */     } else {
/*     */       ClusNode cnode; ClusStatistic stat;
/* 185 */       switch (getZoom()) {
/*     */         case 2:
/* 187 */           this.wd = 50;
/* 188 */           this.hi = 8;
/* 189 */           cnode = (ClusNode)this.m_Node.getVisitor(0);
/* 190 */           stat = cnode.getClusteringStat();
/* 191 */           if (stat != null) {
/* 192 */             String totstr = "" + (int)stat.getTotalWeight();
/* 193 */             g.setFont(font3);
/* 194 */             FontMetrics fm2 = g.getFontMetrics();
/* 195 */             this.wd += fm2.stringWidth(totstr) + 6;
/* 196 */             this.hi = Math.max(this.hi, fm2.getHeight() + 2);
/*     */           } 
/*     */           break;
/*     */         case 3:
/* 200 */           this.wd = 50;
/* 201 */           this.hi = 8;
/*     */           break;
/*     */         case 4:
/* 204 */           this.wd = 10;
/* 205 */           this.hi = 6;
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void draw(Graphics2D g, DrawableCanvas cnv, int xofs, int yofs) {
/* 212 */     super.draw(g, cnv, xofs, yofs);
/* 213 */     if (getZoom() <= 1) {
/* 214 */       this.m_Lines.setXY(this.xp, this.yp);
/* 215 */       this.m_Lines.draw(g, cnv, xofs, yofs);
/* 216 */       if (this.m_StatVis != null) {
/* 217 */         int ypos = this.yp - yofs + this.m_Lines.getHeight();
/* 218 */         int xpos = this.xp - xofs;
/* 219 */         if (getZoom() == 0) {
/* 220 */           this.m_StatVis.setSize(this.wd - 6, 8);
/* 221 */           this.m_StatVis.setXY(xpos + 3, ypos);
/*     */         } else {
/* 223 */           this.m_StatVis.setSize(this.wd - 6, 4);
/* 224 */           this.m_StatVis.setXY(xpos + 3, ypos - 2);
/*     */         } 
/* 226 */         this.m_StatVis.draw(g, cnv, 0, 0);
/*     */       } 
/*     */     } else {
/* 229 */       int ypos = this.yp - yofs;
/* 230 */       int xpos = this.xp - xofs;
/* 231 */       ClusNode cnode = (ClusNode)this.m_Node.getVisitor(0);
/* 232 */       ClusStatistic stat = cnode.getClusteringStat();
/* 233 */       Color node_color = this.m_Node.atBottomLevel() ? (this.m_Node.getPaintSettings()).LEAF_COLOR : (this.m_Node.getPaintSettings()).NODE_COLOR;
/* 234 */       if (getZoom() == 4 || getZoom() == 5) {
/*     */         try {
/* 236 */           ClassificationStat cstat = (ClassificationStat)stat;
/* 237 */           node_color = ClassStatVis.getBinColorStatic(cstat.getMajorityClass(0));
/* 238 */         } catch (Exception exception) {}
/*     */       }
/* 240 */       switch (getZoom()) {
/*     */         case 2:
/* 242 */           if (stat != null && this.m_StatVis != null) {
/* 243 */             String totstr = "" + (int)stat.getTotalWeight();
/* 244 */             g.setFont(font3);
/* 245 */             g.setColor(Color.black);
/* 246 */             FontMetrics fm2 = g.getFontMetrics();
/* 247 */             int txtwd = fm2.stringWidth(totstr);
/* 248 */             g.drawString(totstr, xpos + 3, ypos + fm2.getAscent() + 2);
/* 249 */             this.m_StatVis.setSize(this.wd - 6 - txtwd - 2, this.hi - 4);
/* 250 */             this.m_StatVis.setXY(xpos + txtwd + 6, ypos + 2);
/* 251 */             this.m_StatVis.draw(g, cnv, 0, 0);
/*     */           } 
/*     */           break;
/*     */         case 3:
/* 255 */           if (this.m_StatVis != null) {
/* 256 */             this.m_StatVis.setSize(this.wd - 4, this.hi - 4);
/* 257 */             this.m_StatVis.setXY(xpos + 2, ypos + 2);
/* 258 */             this.m_StatVis.draw(g, cnv, 0, 0);
/*     */           } 
/*     */           break;
/*     */         case 4:
/* 262 */           g.setColor(node_color);
/* 263 */           g.fillRect(xpos + 1, ypos + 1, this.wd - 1, this.hi - 1);
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void renderTree() {
/* 270 */     TreePanel panel = (TreePanel)this.m_Node.getPaintSettings().getDocument();
/* 271 */     panel.doRender();
/*     */   }
/*     */   
/*     */   public void actionPerformed(ActionEvent evt) {
/* 275 */     boolean state = this.m_Button.getState();
/* 276 */     if (state) {
/* 277 */       this.m_Node.recursiveFakeLeaf(true);
/*     */     } else {
/* 279 */       this.m_Node.setFakeLeaf(false);
/*     */     } 
/* 281 */     renderTree();
/*     */   }
/*     */   
/*     */   private DrawableLines createLabel(ClusNode cnode, MyDrawableNode node) {
/* 285 */     ClusNode parent = (ClusNode)cnode.getParent();
/* 286 */     if (parent != null) {
/* 287 */       NodeTest test = parent.getTest();
/* 288 */       if (test.hasBranchLabels()) {
/* 289 */         String label = test.getBranchLabel(node.getIndex());
/* 290 */         DrawableLines res = new DrawableLines(label);
/* 291 */         res.setBackground(new Color(204, 204, 204));
/* 292 */         return res;
/*     */       } 
/*     */     } 
/* 295 */     return null;
/*     */   }
/*     */   
/*     */   private class MyPruneListener implements ActionListener { private MyPruneListener() {}
/*     */     
/*     */     public void actionPerformed(ActionEvent evt) {
/* 301 */       TreePanel panel = (TreePanel)ClusNodePainter.this.m_Node.getPaintSettings().getDocument();
/* 302 */       String str = JOptionPane.showInputDialog(panel, "Enter subtree size");
/*     */       try {
/* 304 */         int nodes = Integer.parseInt(str);
/* 305 */         ClusNode node = (ClusNode)ClusNodePainter.this.m_Node.getVisitor(0);
/* 306 */         panel.pruneTree(node, nodes);
/* 307 */       } catch (NumberFormatException e) {
/* 308 */         System.err.println("Number expected, got: " + str);
/* 309 */       } catch (ClusException e) {
/* 310 */         System.err.println("Clus error: " + e.getMessage());
/*     */       } 
/*     */     } }
/*     */   
/*     */   private class MyExpandListener implements ActionListener {
/*     */     private MyExpandListener() {}
/*     */     
/*     */     public void actionPerformed(ActionEvent evt) {
/* 318 */       ClusNodePainter.this.m_Node.recursiveFakeLeaf(false);
/* 319 */       ClusNodePainter.this.renderTree();
/*     */     }
/*     */   }
/*     */   
/*     */   private class MyZoomListener
/*     */     implements ActionListener {
/*     */     protected int m_Zoom;
/*     */     protected boolean m_IsSubtree;
/*     */     
/*     */     public MyZoomListener(int zoom, boolean subtree) {
/* 329 */       this.m_Zoom = zoom;
/* 330 */       this.m_IsSubtree = subtree;
/*     */     }
/*     */     
/*     */     public void actionPerformed(ActionEvent evt) {
/* 334 */       if (this.m_IsSubtree) {
/* 335 */         ClusNodePainter.this.m_Node.recursiveZoom(this.m_Zoom);
/*     */       } else {
/* 337 */         ClusNodePainter.this.m_Node.setZoom(this.m_Zoom);
/*     */       } 
/* 339 */       ClusNodePainter.this.renderTree();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\gui\ClusNodePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */