/*     */ package clus.main;
/*     */ 
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.util.ClusException;
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
/*     */ public class ClusSummary
/*     */   extends ClusModelInfoList
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_Runs;
/*  34 */   protected int m_TotalRuns = 1;
/*     */   protected ClusErrorList m_TrainErr;
/*     */   protected ClusErrorList m_TestErr;
/*     */   protected ClusErrorList m_ValidErr;
/*     */   protected ClusStatManager m_StatMgr;
/*     */   
/*     */   public void resetAll() {
/*  41 */     this.m_Models.clear();
/*  42 */     this.m_IndTime = 0L; this.m_PrepTime = 0L; this.m_PruneTime = 0L;
/*     */   }
/*     */   
/*     */   public void setStatManager(ClusStatManager mgr) {
/*  46 */     this.m_StatMgr = mgr;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  50 */     return this.m_StatMgr;
/*     */   }
/*     */   
/*     */   public ClusErrorList getTrainError() {
/*  54 */     return this.m_TrainErr;
/*     */   }
/*     */   
/*     */   public ClusErrorList getTestError() {
/*  58 */     return this.m_TestErr;
/*     */   }
/*     */   
/*     */   public ClusErrorList getValidationError() {
/*  62 */     return this.m_ValidErr;
/*     */   }
/*     */   
/*     */   public boolean hasTestError() {
/*  66 */     return (this.m_TestErr != null);
/*     */   }
/*     */   
/*     */   public void setTrainError(ClusErrorList err) {
/*  70 */     this.m_TrainErr = err;
/*     */   }
/*     */   
/*     */   public void setTestError(ClusErrorList err) {
/*  74 */     this.m_TestErr = err;
/*     */   }
/*     */   
/*     */   public void setValidationError(ClusErrorList err) {
/*  78 */     this.m_ValidErr = err;
/*     */   }
/*     */   
/*     */   public int getNbRuns() {
/*  82 */     return this.m_Runs;
/*     */   }
/*     */   
/*     */   public int getTotalRuns() {
/*  86 */     return this.m_TotalRuns;
/*     */   }
/*     */   
/*     */   public void setTotalRuns(int tot) {
/*  90 */     this.m_TotalRuns = tot;
/*     */   }
/*     */   
/*     */   public ClusSummary getSummaryClone() {
/*  94 */     ClusSummary summ = new ClusSummary();
/*  95 */     summ.m_StatMgr = getStatManager();
/*  96 */     summ.setModels(cloneModels());
/*  97 */     return summ;
/*     */   }
/*     */   
/*     */   public void addSummary(ClusRun cr) throws ClusException {
/* 101 */     this.m_Runs++;
/* 102 */     this.m_IndTime += cr.getInductionTime();
/* 103 */     this.m_PruneTime += cr.getPruneTime();
/* 104 */     this.m_PrepTime += cr.getPrepareTime();
/* 105 */     int nb_models = cr.getNbModels();
/* 106 */     for (int i = 0; i < nb_models; i++) {
/* 107 */       ClusModelInfo mi = cr.getModelInfo(i);
/* 108 */       if (mi != null) {
/* 109 */         ClusModelInfo my = addModelInfo(i);
/* 110 */         my.add(mi);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\main\ClusSummary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */