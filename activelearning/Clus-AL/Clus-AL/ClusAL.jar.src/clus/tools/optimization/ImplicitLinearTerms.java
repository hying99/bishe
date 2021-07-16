/*     */ package clus.tools.optimization;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleLinearTerm;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
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
/*     */ public class ImplicitLinearTerms
/*     */ {
/*  45 */   private RowData m_linearTermPredictions = null;
/*  46 */   private ClusStatManager m_StatManager = null;
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
/*     */   public ImplicitLinearTerms(RowData data, ClusStatManager statMgr) {
/*  60 */     this.m_linearTermPredictions = data;
/*  61 */     this.m_StatManager = statMgr;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     if (Settings.VERBOSE > 0) {
/*  70 */       int nbTargets = this.m_StatManager.getStatistic(3).getNbAttributes();
/*  71 */       int nbDescrAttr = (statMgr.getSchema().getNumericAttrUse(1)).length;
/*     */       
/*  73 */       System.out.println("\tIn optimization using implicitly the predictions of " + nbDescrAttr + " linear terms for each target, total " + (nbDescrAttr * nbTargets) + " terms.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void DeleteImplicitLinearTerms() {
/*  79 */     this.m_linearTermPredictions = null;
/*  80 */     this.m_StatManager = null;
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
/*     */   public double predict(int iLinTerm, DataTuple instance, int iTarget, int nbOfTargets) {
/* 113 */     int iLinTermTargetAttr = iLinTerm % nbOfTargets;
/*     */ 
/*     */     
/* 116 */     if (iLinTermTargetAttr != iTarget) return 0.0D;
/*     */ 
/*     */     
/* 119 */     int iDescriptiveAttr = (int)Math.floor(iLinTerm / nbOfTargets);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 124 */     double pred = ClusRuleLinearTerm.attributeToLinTermPrediction(getSettings(), instance, iDescriptiveAttr, iLinTermTargetAttr, nbOfTargets, true);
/*     */ 
/*     */     
/* 127 */     return !Double.isNaN(pred) ? pred : 0.0D;
/*     */   }
/*     */   
/*     */   private Settings getSettings() {
/* 131 */     return this.m_StatManager.getSettings();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\tools\optimization\ImplicitLinearTerms.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */