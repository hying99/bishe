/*     */ package clus.gui;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.ToolTipManager;
/*     */ import javax.swing.event.TreeExpansionEvent;
/*     */ import javax.swing.event.TreeExpansionListener;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
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
/*     */ public class ShowHierarchy
/*     */   extends JPanel
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected ClusNode m_RootNode;
/*     */   protected ClassHierarchy m_Hier;
/*     */   protected JTHierTreeNode m_JTNode;
/*     */   protected Hashtable m_ToolTips;
/*     */   protected JTree m_JTree;
/*     */   protected boolean[] m_Expanded;
/*     */   double[] m_RCnts;
/*     */   double m_RWeight;
/*     */   double[] m_RVars;
/*     */   double[] m_NCnts;
/*     */   double m_NWeight;
/*  54 */   double m_Fac = 1.0D; double m_Sig = 0.5D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Color[] COLOR_BOUNDS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFac(double fac) {
/*  73 */     this.m_Fac = fac;
/*     */   }
/*     */   
/*     */   public void setSig(double sig) {
/*  77 */     this.m_Sig = sig;
/*     */   }
/*     */   
/*     */   public void renewTree() {
/*  81 */     this.m_JTNode.removeAllChildren();
/*  82 */     createSignificantJTree(this.m_Hier.getRoot(), this.m_JTNode);
/*  83 */     this.m_JTree.setModel(new DefaultTreeModel(this.m_JTNode));
/*  84 */     doExpansion(this.m_JTNode);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateScreen() {
/*  89 */     updateScreen(this.m_JTNode);
/*     */   }
/*     */   
/*     */   public void updateScreen(JTHierTreeNode node) {
/*  93 */     this.m_JTree.getModel().valueForPathChanged(new TreePath((Object[])node.getPath()), node.getUserObject());
/*  94 */     for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements(); ) {
/*  95 */       JTHierTreeNode subnode = (JTHierTreeNode)e.nextElement();
/*  96 */       updateScreen(subnode);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doExpansion(JTHierTreeNode node) {
/* 101 */     int nb = node.getChildCount();
/* 102 */     for (int i = 0; i < nb; i++) {
/* 103 */       JTHierTreeNode child = (JTHierTreeNode)node.getChildAt(i);
/* 104 */       ClassTerm term = (ClassTerm)child.getUserObject();
/* 105 */       if (this.m_Expanded[term.getIndex()]) {
/* 106 */         this.m_JTree.expandPath(new TreePath((Object[])child.getPath()));
/*     */       }
/* 108 */       doExpansion(child);
/*     */     } 
/*     */   }
/*     */   
/*     */   private JTHierTreeNode createSignificantJTree(ClassTerm term, JTHierTreeNode node) {
/* 113 */     node.setUserObject(term);
/* 114 */     int nb = term.getNbChildren();
/* 115 */     for (int i = 0; i < nb; i++) {
/* 116 */       ClassTerm child = (ClassTerm)term.getChild(i);
/*     */       
/* 118 */       if (significant(child, this.m_Sig))
/* 119 */         node.add(createSignificantJTree(child, new JTHierTreeNode())); 
/*     */     } 
/* 121 */     return node;
/*     */   }
/*     */   
/*     */   private boolean significant(ClassTerm term, double zValue) {
/* 125 */     if (zValue(term) > zValue) return true;
/*     */     
/* 127 */     boolean sign = false;
/* 128 */     int nb = term.getNbChildren();
/* 129 */     for (int i = 0; i < nb; i++) {
/* 130 */       if (significant((ClassTerm)term.getChild(i), zValue)) {
/* 131 */         sign = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 135 */     return sign;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private JTHierTreeNode createJTree(ClassTerm term) {
/* 141 */     JTHierTreeNode node = new JTHierTreeNode(term);
/* 142 */     int nb = term.getNbChildren();
/* 143 */     for (int i = 0; i < nb; i++) {
/* 144 */       ClassTerm child = (ClassTerm)term.getChild(i);
/* 145 */       node.add(createJTree(child));
/*     */     } 
/* 147 */     return node;
/*     */   }
/*     */   
/*     */   public ClassHierarchy getHier() {
/* 151 */     return this.m_Hier;
/*     */   }
/*     */   
/*     */   public ClusNode getRootNode() {
/* 155 */     return this.m_RootNode;
/*     */   }
/*     */   
/*     */   public void setRootCounts(ClusNode root) {
/* 159 */     this.m_RCnts = getCounts(root);
/* 160 */     this.m_RWeight = getWeight(root);
/* 161 */     this.m_RVars = getVariances(root);
/*     */   }
/*     */   
/*     */   public void setNodeCounts(ClusNode node) {
/* 165 */     this.m_NCnts = getCounts(node);
/* 166 */     this.m_NWeight = getWeight(node);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double[] getCounts(ClusNode node) {
/* 212 */     return null;
/*     */   }
/*     */   
/*     */   public static double getWeight(ClusNode node) {
/* 216 */     ClusStatistic stat = node.getClusteringStat();
/* 217 */     return stat.m_SumWeight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double[] getVariances(ClusNode node) {
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private double zValue(ClassTerm child) {
/* 230 */     int index = child.getIndex();
/*     */ 
/*     */     
/* 233 */     double zValue = 0.0D;
/* 234 */     if (this.m_RVars != null) {
/* 235 */       zValue = Math.abs((this.m_RCnts[index] / this.m_RWeight - this.m_NCnts[index] / this.m_NWeight) / Math.sqrt(this.m_RVars[index]));
/*     */     } else {
/* 237 */       zValue = Double.MAX_VALUE;
/*     */     } 
/*     */     
/* 240 */     return zValue;
/*     */   }
/*     */   
/*     */   public ShowHierarchy(ClusNode rootClass, ClassHierarchy hier) {
/* 244 */     this.COLOR_BOUNDS = new Color[] { Color.black, Color.blue, Color.green, Color.red }; this.m_Hier = hier; this.m_RootNode = rootClass; setRootCounts(rootClass); this.m_Expanded = new boolean[hier.getTotal()]; this.m_JTNode = createJTree(hier.getRoot()); this.m_JTree = new JTree(this.m_JTNode); ToolTipManager.sharedInstance().registerComponent(this.m_JTree); this.m_JTree.setCellRenderer(new MyRenderer());
/*     */     this.m_JTree.addTreeExpansionListener(new MyExpansionListener());
/*     */     JScrollPane scroll = new JScrollPane(this.m_JTree);
/*     */     setLayout(new BorderLayout());
/* 248 */     add(scroll, "Center"); } public Color createColor(double val) { int idx = 0;
/* 249 */     double fac = 1.0D / (this.COLOR_BOUNDS.length - 1);
/* 250 */     double lb = 0.0D;
/* 251 */     double ub = fac;
/* 252 */     while (val > ub) {
/* 253 */       idx++;
/* 254 */       lb = ub;
/* 255 */       ub = (idx + 1) * fac;
/*     */     } 
/* 257 */     if (idx >= this.COLOR_BOUNDS.length - 1) return Color.red; 
/* 258 */     double perc = (val - lb) / fac;
/* 259 */     double red = (1.0D - perc) * this.COLOR_BOUNDS[idx].getRed() + perc * this.COLOR_BOUNDS[idx + 1].getRed();
/* 260 */     double green = (1.0D - perc) * this.COLOR_BOUNDS[idx].getGreen() + perc * this.COLOR_BOUNDS[idx + 1].getGreen();
/* 261 */     double blue = (1.0D - perc) * this.COLOR_BOUNDS[idx].getBlue() + perc * this.COLOR_BOUNDS[idx + 1].getBlue();
/* 262 */     return new Color((int)red, (int)green, (int)blue); }
/*     */ 
/*     */   
/*     */   private class MyExpansionListener implements TreeExpansionListener { private MyExpansionListener() {}
/*     */     
/*     */     public void treeCollapsed(TreeExpansionEvent event) {
/* 268 */       JTHierTreeNode node = (JTHierTreeNode)event.getPath().getLastPathComponent();
/* 269 */       ShowHierarchy.this.m_Expanded[((ClassTerm)node.getUserObject()).getIndex()] = false;
/*     */     }
/*     */     
/*     */     public void treeExpanded(TreeExpansionEvent event) {
/* 273 */       JTHierTreeNode node = (JTHierTreeNode)event.getPath().getLastPathComponent();
/* 274 */       ShowHierarchy.this.m_Expanded[((ClassTerm)node.getUserObject()).getIndex()] = true;
/*     */     } }
/*     */ 
/*     */   
/*     */   private class MyRenderer extends DefaultTreeCellRenderer {
/*     */     public static final long serialVersionUID = 1L;
/*     */     protected static final int DX = 24;
/*     */     protected String m_String;
/*     */     protected ClassTerm m_Term;
/*     */     
/*     */     private MyRenderer() {}
/*     */     
/*     */     public Dimension getPreferredSize() {
/* 287 */       Dimension d = super.getPreferredSize();
/*     */       
/* 289 */       int half = (d.height + 1) / 2;
/* 290 */       return new Dimension(d.width + 6 + 80, 2 * half);
/*     */     }
/*     */     
/*     */     public Color getColor(int which) {
/* 294 */       if (which == 0) {
/* 295 */         return ShowHierarchy.this.createColor(ShowHierarchy.this.m_RCnts[this.m_Term.getIndex()] * ShowHierarchy.this.m_Fac);
/*     */       }
/* 297 */       if (ShowHierarchy.this.m_NCnts == null) return Color.black; 
/* 298 */       return ShowHierarchy.this.createColor(ShowHierarchy.this.m_NCnts[this.m_Term.getIndex()] * ShowHierarchy.this.m_Fac);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getWidth(double val) {
/* 303 */       return (int)Math.min(val * ShowHierarchy.this.m_Fac * 80.0D, 80.0D);
/*     */     }
/*     */     
/*     */     public void paintComponent(Graphics g) {
/* 307 */       super.paintComponent(g);
/* 308 */       Dimension d = super.getPreferredSize();
/* 309 */       g.setColor(Color.blue);
/* 310 */       int half = (d.height + 1) / 2;
/* 311 */       int hi = half - 4;
/* 312 */       int idx = this.m_Term.getIndex();
/* 313 */       double rRelCnt = ShowHierarchy.this.m_RCnts[idx] / ShowHierarchy.this.m_RWeight;
/* 314 */       int wd1 = getWidth(rRelCnt);
/* 315 */       g.fillRect(d.width + 3, 4, wd1, hi);
/* 316 */       if (ShowHierarchy.this.m_NCnts != null) {
/* 317 */         double nRelCnt = ShowHierarchy.this.m_NCnts[idx] / ShowHierarchy.this.m_NWeight;
/* 318 */         if (ShowHierarchy.this.m_RVars != null)
/* 319 */         { double zValue = Math.abs(rRelCnt - nRelCnt) / Math.sqrt(ShowHierarchy.this.m_RVars[idx]);
/* 320 */           if (zValue > ShowHierarchy.this.m_Sig) {
/* 321 */             g.setColor(Color.red);
/*     */           } else {
/* 323 */             g.setColor(Color.gray);
/*     */           }  }
/* 325 */         else { g.setColor(Color.red); }
/*     */         
/* 327 */         int wd2 = getWidth(nRelCnt);
/* 328 */         g.fillRect(d.width + 3, 5 + hi, wd2, hi);
/*     */       } 
/* 330 */       g.setColor(Color.black);
/* 331 */       g.drawRect(d.width + 1, 2, 84, 2 * half - 4);
/*     */     }
/*     */     
/*     */     public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
/* 335 */       super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
/* 336 */       JTHierTreeNode node = (JTHierTreeNode)value;
/* 337 */       this.m_Term = (ClassTerm)node.getUserObject();
/* 338 */       this.m_String = this.m_Term.toString();
/*     */ 
/*     */       
/* 341 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\gui\ShowHierarchy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */