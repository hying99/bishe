/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.ootind.OOTInduce;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import jeans.util.list.MyList;
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
/*     */ public class OptXValGroup
/*     */   extends MyList
/*     */ {
/*     */   public RowData m_Data;
/*     */   public int[] m_Folds;
/*     */   public boolean m_IsSoft;
/*     */   public NodeTest m_Test;
/*     */   public ClusStatistic[] m_TotStat;
/*     */   public ClusNode[] m_Nodes;
/*     */   
/*     */   public OptXValGroup(RowData data, int size) {
/*  47 */     this.m_Data = data;
/*  48 */     this.m_Folds = new int[size];
/*     */   }
/*     */   
/*     */   public OptXValGroup(OptXValGroup grp, int size) {
/*  52 */     this.m_Data = grp.m_Data;
/*  53 */     this.m_IsSoft = grp.m_IsSoft;
/*  54 */     this.m_Folds = new int[size];
/*     */   }
/*     */   
/*     */   public void optimize2() {
/*  58 */     this.m_Data.optimize2(this.m_Folds);
/*     */   }
/*     */   
/*     */   public final int[] getFolds() {
/*  62 */     return this.m_Folds;
/*     */   }
/*     */   
/*     */   public final void preprocNodes(OptXValNode node, OptXValInduce induce) {
/*  66 */     int nb = getNbFolds();
/*  67 */     for (int i = 0; i < nb; i++) {
/*  68 */       CurrentBestTestAndHeuristic sel = induce.getSelector(i);
/*  69 */       ClusNode fnode = getNode(i);
/*  70 */       if (sel.hasBestTest()) {
/*  71 */         fnode.testToNode(sel);
/*     */       } else {
/*  73 */         fnode.makeLeaf();
/*  74 */         cleanNode(i);
/*     */       } 
/*     */       
/*  77 */       node.setNode(getFold(i), fnode);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void preprocNodes2(OptXValNode node, OOTInduce induce) {
/*  82 */     int nb = getNbFolds();
/*  83 */     for (int i = 0; i < nb; i++) {
/*  84 */       CurrentBestTestAndHeuristic sel = induce.getSelector(i);
/*  85 */       ClusNode fnode = getNode(i);
/*  86 */       if (sel.hasBestTest()) {
/*  87 */         fnode.testToNode(sel);
/*     */       } else {
/*  89 */         fnode.makeLeaf();
/*  90 */         cleanNode(i);
/*     */       } 
/*     */       
/*  93 */       node.setNode(getFold(i), fnode);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void setSoft() {
/*  98 */     this.m_IsSoft = true;
/*     */   }
/*     */   
/*     */   public final boolean updateSoft() {
/* 102 */     return this.m_IsSoft = this.m_Data.isSoft();
/*     */   }
/*     */   
/*     */   public final OptXValGroup cloneGroup() {
/* 106 */     OptXValGroup res = new OptXValGroup(this.m_Data, this.m_Folds.length);
/* 107 */     System.arraycopy(this.m_Folds, 0, res.m_Folds, 0, this.m_Folds.length);
/* 108 */     res.m_IsSoft = this.m_IsSoft;
/* 109 */     return res;
/*     */   }
/*     */   
/*     */   public final void setFold(int idx, int fold) {
/* 113 */     this.m_Folds[idx] = fold;
/*     */   }
/*     */   
/*     */   public final void setTest(NodeTest test) {
/* 117 */     this.m_Test = test;
/*     */   }
/*     */   
/*     */   public final NodeTest getTest() {
/* 121 */     return this.m_Test;
/*     */   }
/*     */   
/*     */   public final void println() {
/* 125 */     System.out.print("[");
/* 126 */     System.out.print(this.m_Folds[0]);
/* 127 */     for (int i = 1; i < this.m_Folds.length; i++) {
/* 128 */       System.out.print("," + this.m_Folds[i]);
/*     */     }
/* 130 */     System.out.print("] - ");
/* 131 */     System.out.println(this.m_Test.getString());
/*     */   }
/*     */   
/*     */   public final void initializeFolds() {
/* 135 */     for (int i = 0; i < this.m_Folds.length; i++)
/* 136 */       this.m_Folds[i] = i; 
/*     */   }
/*     */   
/*     */   public final ClusNode getNode(int i) {
/* 140 */     return this.m_Nodes[i];
/*     */   }
/*     */   
/*     */   public final ClusNode[] getNodes() {
/* 144 */     return this.m_Nodes;
/*     */   }
/*     */   
/*     */   public final void cleanNode(int i) {
/* 148 */     this.m_Nodes[i] = null;
/*     */   }
/*     */   
/*     */   public final void create(ClusStatManager m_StatManager, int folds) {
/* 152 */     this.m_TotStat = new ClusStatistic[folds + 1];
/* 153 */     for (int i = 0; i <= folds; i++)
/* 154 */       this.m_TotStat[i] = m_StatManager.createClusteringStat(); 
/*     */   }
/*     */   
/*     */   public final void create2(ClusStatManager m_StatManager, int folds) {
/* 158 */     this.m_TotStat = new ClusStatistic[folds];
/* 159 */     for (int i = 0; i < folds; i++)
/* 160 */       this.m_TotStat[i] = m_StatManager.createClusteringStat(); 
/*     */   }
/*     */   
/*     */   public final void makeNodes() {
/* 164 */     int nb = this.m_Folds.length;
/* 165 */     this.m_Nodes = new ClusNode[nb];
/* 166 */     for (int i = 0; i < nb; i++) {
/* 167 */       this.m_Nodes[i] = new ClusNode();
/* 168 */       int foldnr = this.m_Folds[i];
/* 169 */       (this.m_Nodes[i]).m_ClusteringStat = this.m_TotStat[foldnr];
/*     */     } 
/*     */   }
/*     */   
/*     */   public final ClusStatistic getTotStat(int fold) {
/* 174 */     return this.m_TotStat[fold];
/*     */   }
/*     */   
/*     */   public final RowData getData() {
/* 178 */     return this.m_Data;
/*     */   }
/*     */   
/*     */   public final void setData(RowData data) {
/* 182 */     this.m_Data = data;
/*     */   }
/*     */   
/*     */   public final int getFold() {
/* 186 */     return this.m_Folds[0];
/*     */   }
/*     */   
/*     */   public final int getNbFolds() {
/* 190 */     return this.m_Folds.length;
/*     */   }
/*     */   
/*     */   public final int getFold(int idx) {
/* 194 */     return this.m_Folds[idx];
/*     */   }
/*     */   
/*     */   public final void killFold(int idx) {
/* 198 */     this.m_Folds[idx] = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean cleanFolds() {
/* 203 */     int nb = 0;
/* 204 */     for (int i = 0; i < this.m_Folds.length; i++) {
/* 205 */       if (this.m_Folds[i] != -1) nb++;
/*     */     
/*     */     } 
/* 208 */     int idx = 0;
/* 209 */     int[] old = this.m_Folds;
/* 210 */     this.m_Folds = new int[nb];
/* 211 */     if (nb > 0) {
/* 212 */       for (int j = 0; j < old.length; j++) {
/* 213 */         if (old[j] != -1) this.m_Folds[idx++] = old[j];
/*     */       
/*     */       } 
/*     */     }
/* 217 */     return (nb == 0);
/*     */   }
/*     */   
/*     */   public final ClusNode makeLeaf(int idx) {
/* 221 */     ClusNode leaf = new ClusNode();
/* 222 */     leaf.m_ClusteringStat = this.m_TotStat[idx];
/* 223 */     leaf.makeLeaf();
/* 224 */     return leaf;
/*     */   }
/*     */   
/*     */   public final boolean stopCrit(int idx) {
/* 228 */     if ((this.m_TotStat[idx]).m_SumWeight < 2.0D * Settings.MINIMAL_WEIGHT) return true; 
/* 229 */     return false;
/*     */   }
/*     */   
/*     */   public final void stopCrit(OptXValNode node) {
/* 233 */     int nb = getNbFolds();
/* 234 */     for (int i = 0; i < nb; i++) {
/* 235 */       int fld = getFold(i);
/* 236 */       if (stopCrit(fld)) {
/*     */         
/* 238 */         node.setNodeIndex(i, makeLeaf(fld));
/*     */         
/* 240 */         killFold(i);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void calcTotalStats2() {
/* 246 */     int nb = this.m_Data.getNbRows();
/* 247 */     for (int i = 0; i < nb; i++) {
/* 248 */       DataTuple tuple = this.m_Data.getTuple(i);
/* 249 */       int[] folds = tuple.m_Folds;
/* 250 */       for (int j = 0; j < folds.length; j++) {
/* 251 */         int times = folds[j];
/* 252 */         if (times != 0) this.m_TotStat[j].updateWeighted(tuple, times * tuple.getWeight()); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void calcTotalStats() {
/* 258 */     this.m_Data.calcXValTotalStat(this.m_TotStat);
/* 259 */     ClusStatistic sum = this.m_TotStat[0];
/*     */     int i;
/* 261 */     for (i = 1; i < this.m_TotStat.length; i++) {
/* 262 */       sum.add(this.m_TotStat[i]);
/*     */     }
/*     */     
/* 265 */     for (i = 1; i < this.m_TotStat.length; i++) {
/* 266 */       this.m_TotStat[i].subtractFromOther(sum);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void calcTotalStats(ClusStatistic[] extra) {
/* 271 */     this.m_Data.calcXValTotalStat(this.m_TotStat, extra);
/* 272 */     ClusStatistic sum = this.m_TotStat[0];
/*     */     int i;
/* 274 */     for (i = 1; i < this.m_TotStat.length; i++) {
/* 275 */       sum.add(this.m_TotStat[i]);
/*     */     }
/*     */     
/* 278 */     for (i = 1; i < this.m_TotStat.length; i++) {
/* 279 */       this.m_TotStat[i].subtractFromOther(sum);
/*     */     }
/*     */     
/* 282 */     for (i = 0; i < this.m_TotStat.length; i++)
/* 283 */       this.m_TotStat[i].add(extra[i]); 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\optxval\OptXValGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */