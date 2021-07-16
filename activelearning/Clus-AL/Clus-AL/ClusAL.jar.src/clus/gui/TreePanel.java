/*     */ package clus.gui;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.gui.statvis.ClassStatVis;
/*     */ import clus.gui.statvis.ClusStatVisualizer;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.pruning.SizeConstraintPruning;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.util.HashMap;
/*     */ import javax.swing.JPanel;
/*     */ import jeans.graph.swing.drawable.Drawable;
/*     */ import jeans.graph.swing.drawable.DrawableCanvas;
/*     */ import jeans.graph.swing.drawable.DrawableRenderer;
/*     */ import jeans.graph.swing.drawable.DrawableScrollableCanvas;
/*     */ import jeans.graph.tree.MyDrawableNode;
/*     */ import jeans.graph.tree.MyNodePainter;
/*     */ import jeans.graph.tree.NodePainterSettings;
/*     */ import jeans.graph.tree.TreeRenderer;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MyVisitorParent;
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
/*     */ public class TreePanel
/*     */   extends JPanel
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   TreeFrame m_Frame;
/*     */   MyDrawableNode m_Root;
/*     */   ClusNode m_ClusRoot;
/*     */   DrawableScrollableCanvas m_Canvas;
/*  51 */   HashMap m_StatVis = createStatVis();
/*  52 */   MyNodePainter m_Paint = new ClusNodePainter();
/*  53 */   NodePainterSettings m_Sett = new NodePainterSettings();
/*     */   
/*     */   ClusStatManager m_Manager;
/*     */   boolean m_HorzVert = true;
/*  57 */   private static final int[] XGAPS = new int[] { 10, 5, 3, 3, 2 };
/*     */   
/*     */   public TreePanel() {
/*  60 */     this((ClusNode)null);
/*     */   }
/*     */   
/*     */   public TreePanel(ClusNode root) {
/*  64 */     this(root, (String[])null);
/*     */   }
/*     */   
/*     */   public TreePanel(ClusNode root, String[] info) {
/*  68 */     System.out.println("Setting node settings");
/*  69 */     this.m_Sett.NODE_COLOR = new Color(228, 186, 143);
/*  70 */     this.m_Sett.LEAF_COLOR = new Color(172, 193, 232);
/*  71 */     this.m_Sett.NODE_BORDER_COLOR = Color.red;
/*  72 */     this.m_Sett.LEAF_BORDER_COLOR = Color.blue;
/*     */     
/*  74 */     this.m_Sett.XGAP = XGAPS;
/*  75 */     this.m_Sett.setDocument(this);
/*  76 */     setLayout(new BorderLayout());
/*  77 */     add((Component)(this.m_Canvas = new DrawableScrollableCanvas()), "Center");
/*  78 */     this.m_Canvas.setBackground(new Color(204, 204, 204));
/*  79 */     setTree(root, false);
/*     */   }
/*     */   
/*     */   public void setStatManager(ClusStatManager mgr) {
/*  83 */     this.m_Manager = mgr;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  87 */     return this.m_Manager;
/*     */   }
/*     */   
/*     */   public ClusAttributeWeights createClusAttributeWeights() throws ClusException {
/*  91 */     return this.m_Manager.getClusteringWeights();
/*     */   }
/*     */   
/*     */   public void setHorzVert(boolean toggle) {
/*  95 */     System.out.println("Set horz/vert: " + toggle);
/*  96 */     DrawableCanvas cnv = this.m_Canvas.getCanvas();
/*  97 */     TreeRenderer rend = (TreeRenderer)cnv.getRenderer();
/*  98 */     if (rend != null) {
/*  99 */       rend.setHorzVert(toggle);
/* 100 */       cnv.setRenderState(2);
/* 101 */       cnv.repaint();
/*     */     } 
/* 103 */     this.m_HorzVert = toggle;
/*     */   }
/*     */   
/*     */   public void setBackGroundColor(Color color) {
/* 107 */     this.m_Canvas.setBackground(color);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setScrollTo(int[] path, int skip) {
/* 112 */     MyDrawableNode node = (MyDrawableNode)this.m_Root.fromPath(path, skip);
/* 113 */     if (node != null) this.m_Canvas.setScrollTo((Drawable)node.getPainter()); 
/*     */   }
/*     */   
/*     */   public void setTree(ClusNode root) {
/* 117 */     setTree(root, true);
/*     */   }
/*     */   
/*     */   public void setTree(ClusNode root, boolean redraw) {
/* 121 */     this.m_ClusRoot = root;
/* 122 */     DrawableCanvas cnv = this.m_Canvas.getCanvas();
/* 123 */     if (root != null) {
/* 124 */       this.m_Root = createTree((Node)root, this.m_Sett, this.m_Paint);
/* 125 */       TreeRenderer renderer = new TreeRenderer(this.m_Root);
/* 126 */       renderer.setHorzVert(this.m_HorzVert);
/* 127 */       renderer.setZoomLevels(5);
/* 128 */       cnv.setRenderer((DrawableRenderer)renderer);
/*     */     } else {
/* 130 */       cnv.setRenderer(null);
/*     */     } 
/* 132 */     if (redraw) doRender();
/*     */   
/*     */   }
/*     */   
/*     */   public void setFrame(TreeFrame frame) {
/* 137 */     this.m_Frame = frame;
/*     */   }
/*     */   
/*     */   public void showInfo(ClusNode node) throws ClusException {
/* 141 */     if (this.m_Frame != null) this.m_Frame.showInfo(node); 
/*     */   }
/*     */   
/*     */   public Drawable createStatVisualiser(ClusStatistic stat) {
/* 145 */     String name = stat.getClass().getName();
/* 146 */     ClusStatVisualizer statvis = (ClusStatVisualizer)this.m_StatVis.get(name);
/* 147 */     if (statvis == null) return null; 
/* 148 */     return statvis.createInstance(stat);
/*     */   }
/*     */   
/*     */   public void doRender() {
/* 152 */     DrawableCanvas cnv = this.m_Canvas.getCanvas();
/* 153 */     cnv.setRenderState(2);
/* 154 */     cnv.repaint();
/*     */   }
/*     */   
/*     */   public void collapseChildren(MyDrawableNode node) {
/* 158 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 159 */       MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 160 */       child.recursiveFakeLeaf(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void pruneTree(ClusNode node, int size) throws ClusException {
/* 165 */     SizeConstraintPruning pruner = new SizeConstraintPruning(size, this.m_Manager.getClusteringWeights());
/* 166 */     pruner.prune(node);
/* 167 */     recursiveUpdate(this.m_Root);
/* 168 */     doRender();
/*     */   }
/*     */   
/*     */   public static void recursiveUpdate(MyDrawableNode node) {
/* 172 */     ClusNode cnode = (ClusNode)node.getVisitor(0);
/* 173 */     if (cnode.atBottomLevel() && !node.atBottomLevel()) {
/* 174 */       node.removeAllChildren();
/*     */     }
/* 176 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 177 */       recursiveUpdate((MyDrawableNode)node.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */   private HashMap createStatVis() {
/* 182 */     HashMap<Object, Object> res = new HashMap<>();
/* 183 */     res.put(ClassificationStat.class.getName(), new ClassStatVis());
/* 184 */     return res;
/*     */   }
/*     */   
/*     */   private static MyDrawableNode createTree(Node node, NodePainterSettings par, MyNodePainter paint) {
/* 188 */     par.addVisitor();
/* 189 */     MyDrawableNode root = new MyDrawableNode((MyVisitorParent)par);
/* 190 */     recursiveCreateTree(root, node, paint);
/* 191 */     if (root.getNbNodes() > 10) {
/* 192 */       root.recursiveZoom(1);
/*     */     }
/* 194 */     return root;
/*     */   }
/*     */   
/*     */   private static void recursiveCreateTree(MyDrawableNode root, Node node, MyNodePainter paint) {
/* 198 */     root.setVisitor(node, 0);
/* 199 */     root.setPainter(paint.createPainter(root));
/* 200 */     ClusNode cnode = (ClusNode)node;
/* 201 */     NodeTest test = cnode.getTest();
/* 202 */     if (test != null) {
/* 203 */       int n_arity = node.getNbChildren();
/* 204 */       int t_arity = test.getNbChildren();
/* 205 */       root.setNbChildren(t_arity); int i;
/* 206 */       for (i = 0; i < n_arity; i++) {
/* 207 */         MyDrawableNode child = new MyDrawableNode(root.getVisParent());
/* 208 */         root.setChild((Node)child, i);
/* 209 */         child.setIndex(i);
/* 210 */         recursiveCreateTree(child, node.getChild(i), paint);
/*     */       } 
/* 212 */       for (i = n_arity; i < t_arity; i++) {
/* 213 */         MyDrawableNode child = new MyDrawableNode(root.getVisParent());
/* 214 */         root.setChild((Node)child, i);
/* 215 */         child.setIndex(i);
/* 216 */         child.setPainter(new ClusDummyPainter(cnode, i));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\gui\TreePanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */