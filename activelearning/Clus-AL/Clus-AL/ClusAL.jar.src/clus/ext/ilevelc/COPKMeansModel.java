/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import java.io.PrintWriter;
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
/*     */ public class COPKMeansModel
/*     */   extends ClusNode
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_K;
/*     */   protected int m_Iterations;
/*     */   protected int m_CSets;
/*     */   protected int m_AvgIter;
/*     */   protected boolean m_Illegal;
/*     */   protected double m_RandIndex;
/*     */   protected COPKMeansCluster[] m_Clusters;
/*     */   
/*     */   public void setK(int k) {
/*  42 */     this.m_K = k;
/*     */   }
/*     */   
/*     */   public int getModelSize() {
/*  46 */     return this.m_K;
/*     */   }
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  50 */     if (this.m_Illegal) {
/*  51 */       return null;
/*     */     }
/*  53 */     int best_cl = -1;
/*  54 */     double min_dist = Double.POSITIVE_INFINITY;
/*  55 */     for (int j = 0; j < this.m_K; j++) {
/*  56 */       double dist = this.m_Clusters[j].computeDistance(tuple);
/*  57 */       if (dist < min_dist) {
/*  58 */         best_cl = j;
/*  59 */         min_dist = dist;
/*     */       } 
/*     */     } 
/*  62 */     return (ClusStatistic)this.m_Clusters[best_cl].getCenter();
/*     */   }
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/*  67 */     wrt.println("COPKMeans(" + this.m_K + ", iter = " + this.m_Iterations + ", max = " + this.m_AvgIter + ", csets = " + this.m_CSets + ")");
/*  68 */     if (this.m_Illegal) {
/*  69 */       wrt.println("   Illegal");
/*     */     } else {
/*  71 */       for (int j = 0; j < this.m_K; j++) {
/*  72 */         wrt.println("  " + this.m_Clusters[j].getCenter().getString(info));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getModelInfo() {
/*  78 */     if (this.m_Illegal) {
/*  79 */       return "Rand Index = ?";
/*     */     }
/*  81 */     return "Rand Index = " + this.m_RandIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCSets(int sets) {
/*  86 */     this.m_CSets = sets;
/*     */   }
/*     */   
/*     */   public int getCSets() {
/*  90 */     return this.m_CSets;
/*     */   }
/*     */   
/*     */   public void setAvgIter(int avg) {
/*  94 */     this.m_AvgIter = avg;
/*     */   }
/*     */   
/*     */   public void setIllegal(boolean illegal) {
/*  98 */     this.m_Illegal = illegal;
/*     */   }
/*     */   
/*     */   public void setRandIndex(double value) {
/* 102 */     this.m_RandIndex = value;
/*     */   }
/*     */   
/*     */   public void setClusters(COPKMeansCluster[] clusters) {
/* 106 */     this.m_Clusters = clusters;
/*     */   }
/*     */   
/*     */   public void setIterations(int i) {
/* 110 */     this.m_Iterations = i;
/*     */   }
/*     */   
/*     */   public int getIterations() {
/* 114 */     return this.m_Iterations;
/*     */   }
/*     */   
/*     */   public boolean isIllegal() {
/* 118 */     return this.m_Illegal;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ilevelc\COPKMeansModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */