/*     */ package clus.algo.kNN;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.util.Vector;
/*     */ import jeans.tree.MyNode;
/*     */ import jeans.tree.Node;
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
/*     */ public class KNNTree
/*     */   extends ClusNode
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public ClusStatManager m_SMgr;
/*     */   private VectorDistance $vectDist;
/*     */   private Vector $decisionData;
/*     */   
/*     */   public KNNTree(ClusRun clRun, VectorDistance vDist) {
/*  49 */     this.m_SMgr = clRun.getStatManager();
/*     */     
/*  51 */     this.$vectDist = vDist;
/*     */ 
/*     */     
/*  54 */     this.$decisionData = new Vector(1, 10);
/*     */   }
/*     */ 
/*     */   
/*     */   public KNNTree(KNNTree toClone) {
/*  59 */     this.m_SMgr = toClone.m_SMgr;
/*  60 */     this.m_Test = toClone.m_Test;
/*  61 */     this.m_ClusteringStat = toClone.m_ClusteringStat;
/*  62 */     this.$vectDist = toClone.getVectorDistance();
/*  63 */     this.$decisionData = new Vector(1, 10);
/*     */   }
/*     */   
/*     */   public final ClusStatistic predictWeighted(DataTuple tuple) {
/*  67 */     if (atBottomLevel())
/*     */     {
/*     */       
/*  70 */       return predictLeaf(tuple, getDecisionData());
/*     */     }
/*     */     
/*  73 */     int n_idx = this.m_Test.predictWeighted(tuple);
/*  74 */     if (n_idx != -1) {
/*  75 */       ClusNode info = (ClusNode)getChild(n_idx);
/*  76 */       return info.predictWeighted(tuple);
/*     */     } 
/*  78 */     int nb_c = getNbChildren();
/*  79 */     ClusStatistic stat = this.m_ClusteringStat.cloneSimple();
/*  80 */     for (int i = 0; i < nb_c; i++) {
/*  81 */       ClusNode node = (ClusNode)getChild(i);
/*  82 */       ClusStatistic nodes = node.predictWeighted(tuple);
/*  83 */       stat.addPrediction(nodes, this.m_Test.getProportion(i));
/*     */     } 
/*  85 */     return stat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ClusStatistic predictLeaf(DataTuple tuple, Vector dec_data) {
/*  92 */     if (tuple == null) System.err.println("tuple == null"); 
/*  93 */     if (dec_data == null) System.err.println("dec_data == null");
/*     */     
/*  95 */     ClusStatistic stat = this.m_SMgr.createClusteringStat();
/*     */     
/*  97 */     int amountNBS = Settings.kNNT_k.getValue();
/*     */     
/*  99 */     boolean distWeighted = Settings.kNNT_distWeighted.getValue();
/*     */ 
/*     */ 
/*     */     
/* 103 */     if (amountDecisionData(dec_data) < amountNBS) amountNBS = amountDecisionData(dec_data); 
/* 104 */     PriorityQueue q = new PriorityQueue(amountNBS);
/*     */ 
/*     */ 
/*     */     
/* 108 */     int nbr = amountDecisionData(dec_data);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 113 */     for (int i = 0; i < nbr; i++) {
/*     */       
/* 115 */       DataTuple curTup = getTuple(dec_data, i);
/*     */       
/* 117 */       if (curTup == null) System.out.println("curTup == null");
/*     */       
/* 119 */       double dist = calcDistance(tuple, curTup);
/* 120 */       q.addElement(curTup, dist);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 125 */     double weight = 1.0D;
/* 126 */     for (int j = 0; j < amountNBS; j++) {
/*     */       
/* 128 */       if (distWeighted) weight = 1.0D / Math.pow(q.getValue(j), 2.0D); 
/* 129 */       stat.updateWeighted((DataTuple)q.getElement(j), weight);
/*     */     } 
/*     */     
/* 132 */     stat.calcMean();
/* 133 */     return stat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static KNNTree makeTree(ClusRun cr, ClusNode source, VectorDistance vd) {
/* 140 */     KNNTree node = new KNNTree(cr, vd);
/* 141 */     node.m_Test = source.m_Test;
/* 142 */     node.m_ClusteringStat = source.m_ClusteringStat;
/*     */     
/* 144 */     int arity = source.getNbChildren();
/* 145 */     node.setNbChildren(arity);
/*     */     
/* 147 */     for (int i = 0; i < arity; i++) {
/* 148 */       ClusNode child = (ClusNode)source.getChild(i);
/* 149 */       node.setChild((Node)makeTree(cr, child, vd), i);
/*     */     } 
/* 151 */     return node;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTuple(DataTuple t) {
/* 159 */     this.$decisionData.addElement(t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataTuple getTuple(Vector<DataTuple> dec_data, int i) {
/* 170 */     return dec_data.elementAt(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int amountDecisionData(Vector dec_data) {
/* 177 */     return dec_data.size();
/*     */   }
/*     */ 
/*     */   
/*     */   private double calcDistance(DataTuple t1, DataTuple t2) {
/* 182 */     if (this.$vectDist == null) System.out.println("$vectDist == null");
/*     */     
/* 184 */     return this.$vectDist.getDistance(t1, t2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector getDecisionData() {
/* 193 */     if (atBottomLevel()) return this.$decisionData;
/*     */     
/* 195 */     Vector dec_data = new Vector(5, 5);
/*     */     
/* 197 */     int arity = getNbChildren();
/* 198 */     for (int i = 0; i < arity; i++) {
/* 199 */       KNNTree child = (KNNTree)getChild(i);
/* 200 */       dec_data.addAll(child.getDecisionData());
/*     */     } 
/* 202 */     return dec_data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDecisionData(Vector dec_data) {
/* 211 */     this.$decisionData = dec_data;
/*     */   }
/*     */   
/*     */   public VectorDistance getVectorDistance() {
/* 215 */     return this.$vectDist;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void makeLeaf() {
/* 223 */     if (!atBottomLevel()) {
/*     */       
/* 225 */       this.$decisionData = getDecisionData();
/* 226 */       this.m_Test = null;
/* 227 */       cleanup();
/*     */       
/* 229 */       removeAllChildren();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeightedLeaf(DataTuple tuple) {
/* 239 */     return predictLeaf(tuple, getDecisionData());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MyNode cloneNode() {
/* 248 */     KNNTree clone = new KNNTree(this);
/* 249 */     if (atBottomLevel()) clone.setDecisionData(getDecisionData()); 
/* 250 */     return (MyNode)clone;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\kNN\KNNTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */