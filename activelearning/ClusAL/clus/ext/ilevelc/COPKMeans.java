/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.attweights.ClusNormalizedAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.util.ClusRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import jeans.util.DisjointSetForest;
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
/*     */ public class COPKMeans
/*     */ {
/*     */   protected int m_K;
/*     */   protected RowData m_Data;
/*     */   protected RowData m_OrigData;
/*     */   protected ClusStatManager m_Mgr;
/*     */   protected ArrayList m_Constraints;
/*     */   protected int[][] m_ConstraintsIndex;
/*     */   protected COPKMeansCluster[] m_Clusters;
/*     */   protected ClusNormalizedAttributeWeights m_Scale;
/*     */   
/*     */   public COPKMeans(int maxNbClasses, ClusStatManager mgr) {
/*  49 */     this.m_K = maxNbClasses;
/*  50 */     this.m_Mgr = mgr;
/*  51 */     this.m_Scale = (ClusNormalizedAttributeWeights)mgr.getClusteringWeights();
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  55 */     return this.m_Mgr;
/*     */   }
/*     */   
/*     */   public void createInitialClusters(RowData data, ArrayList constr) {
/*  59 */     this.m_OrigData = data;
/*     */     
/*  61 */     this.m_Clusters = new COPKMeansCluster[this.m_K];
/*     */     
/*  63 */     ArrayList points = data.toArrayList();
/*  64 */     DerivedConstraintsComputer comp = new DerivedConstraintsComputer(points, constr);
/*  65 */     comp.indexPoints();
/*  66 */     DisjointSetForest dsf = comp.createDSFWithMustLinks();
/*  67 */     ArrayList[] comps = comp.assignPointsToComponents(dsf);
/*  68 */     HashSet set = comp.createCannotLinkSet(dsf);
/*  69 */     ClusSchema schema = data.getSchema();
/*  70 */     RowData new_data = new RowData(schema, comps.length);
/*  71 */     NominalAttrType classtype = (NominalAttrType)schema.getAttrType(schema.getNbAttributes() - 1);
/*  72 */     ClusStatManager mgr = getStatManager();
/*  73 */     RegressionStat stat = (RegressionStat)mgr.getStatistic(2);
/*  74 */     for (int i = 0; i < comps.length; i++) {
/*  75 */       ArrayList<DataTuple> crcomp = comps[i];
/*     */       
/*  77 */       RegressionStat avg = (RegressionStat)stat.cloneStat();
/*  78 */       for (int j = 0; j < crcomp.size(); j++) {
/*  79 */         DataTuple dataTuple = crcomp.get(j);
/*  80 */         avg.updateWeighted(dataTuple, dataTuple.getWeight());
/*     */         
/*  82 */         dataTuple.setIndex(i);
/*     */       } 
/*  84 */       avg.calcMean();
/*     */       
/*  86 */       DataTuple tuple = new DataTuple(schema);
/*  87 */       tuple.setWeight(avg.getTotalWeight());
/*  88 */       for (int k = 0; k < avg.getNbAttributes(); k++) {
/*  89 */         NumericAttrType att = avg.getAttribute(k);
/*  90 */         tuple.setDoubleVal(avg.getMean(k), att.getArrayIndex());
/*     */       } 
/*     */       
/*  93 */       DataTuple elem = crcomp.get(0);
/*  94 */       classtype.setNominal(tuple, classtype.getNominal(elem));
/*  95 */       new_data.setTuple(tuple, i);
/*     */     } 
/*  97 */     new_data.addIndices();
/*     */     
/*  99 */     this.m_Data = new_data;
/* 100 */     this.m_Constraints = new ArrayList();
/* 101 */     Iterator<int[]> edges = set.iterator();
/* 102 */     while (edges.hasNext()) {
/* 103 */       int[] edge = edges.next();
/* 104 */       DataTuple tj = new_data.getTuple(edge[0]);
/* 105 */       DataTuple tk = new_data.getTuple(edge[1]);
/* 106 */       this.m_Constraints.add(new ILevelConstraint(tj, tk, 1));
/*     */     } 
/* 108 */     this.m_ConstraintsIndex = ILevelCUtil.createConstraintsIndex(new_data.getNbRows(), this.m_Constraints);
/* 109 */     boolean[] used_tuples = new boolean[new_data.getNbRows()];
/*     */     
/* 111 */     int nb_has = 0;
/* 112 */     while (nb_has < this.m_K) {
/*     */       
/* 114 */       ArrayList<ILevelConstraint> poss_constr = new ArrayList();
/* 115 */       for (int j = 0; j < this.m_Constraints.size(); j++) {
/* 116 */         ILevelConstraint ilc = this.m_Constraints.get(j);
/* 117 */         int t1i = ilc.getT1().getIndex();
/* 118 */         int t2i = ilc.getT2().getIndex();
/* 119 */         int extra_pts = 0;
/* 120 */         if (!used_tuples[t1i]) extra_pts++; 
/* 121 */         if (!used_tuples[t2i]) extra_pts++; 
/* 122 */         if (extra_pts > 0 && nb_has + extra_pts <= this.m_K) {
/* 123 */           poss_constr.add(ilc);
/*     */         }
/*     */       } 
/* 126 */       if (poss_constr.size() > 0) {
/*     */         
/* 128 */         int ci = ClusRandom.nextInt(4, poss_constr.size());
/* 129 */         ILevelConstraint ilc = poss_constr.get(ci);
/* 130 */         int t1i = ilc.getT1().getIndex();
/* 131 */         int t2i = ilc.getT2().getIndex();
/* 132 */         if (!used_tuples[t1i]) {
/* 133 */           used_tuples[t1i] = true;
/* 134 */           this.m_Clusters[nb_has++] = new COPKMeansCluster(new_data.getTuple(t1i), mgr);
/*     */         } 
/* 136 */         if (!used_tuples[t2i]) {
/* 137 */           used_tuples[t2i] = true;
/* 138 */           this.m_Clusters[nb_has++] = new COPKMeansCluster(new_data.getTuple(t2i), mgr);
/*     */         }  continue;
/*     */       } 
/* 141 */       ArrayList<DataTuple> poss_pts = new ArrayList();
/* 142 */       for (int k = 0; k < used_tuples.length; k++) {
/* 143 */         if (!used_tuples[k]) {
/* 144 */           poss_pts.add(new_data.getTuple(k));
/*     */         }
/*     */       } 
/* 147 */       if (poss_pts.size() > 0) {
/* 148 */         int m = ClusRandom.nextInt(4, poss_pts.size());
/* 149 */         DataTuple sel_pt = poss_pts.get(m);
/* 150 */         used_tuples[sel_pt.getIndex()] = true;
/* 151 */         this.m_Clusters[nb_has++] = new COPKMeansCluster(sel_pt, mgr); continue;
/*     */       } 
/* 153 */       int pi = ClusRandom.nextInt(4, used_tuples.length);
/* 154 */       this.m_Clusters[nb_has++] = new COPKMeansCluster(new_data.getTuple(pi), mgr);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void numberClusters() {
/* 161 */     for (int i = 0; i < this.m_K; i++) {
/* 162 */       this.m_Clusters[i].setIndex(i);
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearDataFromClusters() {
/* 167 */     for (int i = 0; i < this.m_K; i++) {
/* 168 */       this.m_Clusters[i].clearData();
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateClusterCenters() {
/* 173 */     for (int i = 0; i < this.m_K; i++) {
/* 174 */       this.m_Clusters[i].updateCenter();
/*     */     }
/*     */   }
/*     */   
/*     */   public double computeVariance() {
/* 179 */     double variance = 0.0D;
/* 180 */     for (int i = 0; i < this.m_K; i++) {
/* 181 */       variance += this.m_Clusters[i].getCenter().getSVarS((ClusAttributeWeights)this.m_Scale);
/*     */     }
/* 183 */     return variance;
/*     */   }
/*     */   
/*     */   public boolean checkConstraints(DataTuple tuple, int clid, int[] assign) {
/* 187 */     int[] cidx = this.m_ConstraintsIndex[tuple.getIndex()];
/* 188 */     if (cidx != null) {
/* 189 */       for (int j = 0; j < cidx.length; j++) {
/*     */         
/* 191 */         ILevelConstraint cons = this.m_Constraints.get(cidx[j]);
/* 192 */         int otidx = cons.getOtherTupleIdx(tuple);
/* 193 */         int oclid = assign[otidx];
/* 194 */         if (clid == oclid)
/*     */         {
/* 196 */           return false;
/*     */         }
/*     */       } 
/* 199 */       return true;
/*     */     } 
/* 201 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean assignDataToClusters(int[] assign) {
/* 206 */     for (int i = 0; i < this.m_Data.getNbRows(); i++) {
/* 207 */       int best_cl = -1;
/* 208 */       double min_dist = Double.POSITIVE_INFINITY;
/* 209 */       DataTuple tuple = this.m_Data.getTuple(i);
/* 210 */       for (int j = 0; j < this.m_K; j++) {
/* 211 */         boolean ok = checkConstraints(tuple, j, assign);
/* 212 */         if (ok) {
/* 213 */           double dist = this.m_Clusters[j].computeDistance(tuple);
/* 214 */           if (dist < min_dist) {
/* 215 */             best_cl = j;
/* 216 */             min_dist = dist;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 221 */       if (best_cl == -1) {
/* 222 */         return false;
/*     */       }
/* 224 */       assign[tuple.getIndex()] = best_cl;
/* 225 */       this.m_Clusters[best_cl].addData(tuple);
/*     */     } 
/*     */     
/* 228 */     return true;
/*     */   }
/*     */   
/*     */   public double computeRandIndex(int[] assign) {
/* 232 */     int a = 0;
/* 233 */     int b = 0;
/* 234 */     int nbex = this.m_OrigData.getNbRows();
/* 235 */     ClusSchema schema = this.m_OrigData.getSchema();
/* 236 */     NominalAttrType classtype = (NominalAttrType)schema.getAttrType(schema.getNbAttributes() - 1);
/* 237 */     for (int i = 0; i < nbex; i++) {
/* 238 */       DataTuple ti = this.m_OrigData.getTuple(i);
/* 239 */       int cia = classtype.getNominal(ti);
/* 240 */       int cib = assign[this.m_Data.getTuple(ti.getIndex()).getIndex()];
/* 241 */       for (int j = i + 1; j < nbex; j++) {
/* 242 */         DataTuple tj = this.m_OrigData.getTuple(j);
/* 243 */         int cja = classtype.getNominal(tj);
/* 244 */         int cjb = assign[this.m_Data.getTuple(tj.getIndex()).getIndex()];
/* 245 */         if (cia == cja && cib == cjb) a++; 
/* 246 */         if (cia != cja && cib != cjb) b++; 
/*     */       } 
/*     */     } 
/* 249 */     double rand = 1.0D * (a + b) / (nbex * (nbex - 1) / 2);
/* 250 */     System.out.println("Rand = " + rand + " (nbex = " + nbex + ")");
/* 251 */     return rand;
/*     */   }
/*     */   
/*     */   public ClusModel induce(RowData data, ArrayList constr) {
/* 255 */     COPKMeansModel model = new COPKMeansModel();
/* 256 */     model.setK(this.m_K);
/*     */     
/* 258 */     createInitialClusters(data, constr);
/*     */     
/* 260 */     numberClusters();
/* 261 */     int[] prev_assign = null;
/* 262 */     int[] assign = new int[this.m_Data.getNbRows()];
/* 263 */     for (int k = 0; k < 1000000; k++) {
/* 264 */       clearDataFromClusters();
/* 265 */       Arrays.fill(assign, -1);
/* 266 */       if (!assignDataToClusters(assign)) {
/*     */         
/* 268 */         model.setIllegal(true);
/* 269 */         return (ClusModel)model;
/*     */       } 
/* 271 */       updateClusterCenters();
/*     */       
/* 273 */       if (prev_assign != null && Arrays.equals(assign, prev_assign)) {
/* 274 */         model.setIterations(k + 1);
/*     */         
/*     */         break;
/*     */       } 
/* 278 */       if (prev_assign == null) prev_assign = new int[this.m_Data.getNbRows()]; 
/* 279 */       System.arraycopy(assign, 0, prev_assign, 0, this.m_Data.getNbRows());
/*     */     } 
/* 281 */     clearDataFromClusters();
/* 282 */     model.setClusters(this.m_Clusters);
/* 283 */     model.setRandIndex(computeRandIndex(assign));
/* 284 */     return (ClusModel)model;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\COPKMeans.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */