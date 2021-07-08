/*    */ package sit.searchAlgorithm;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.main.Settings;
/*    */ import java.util.ArrayList;
/*    */ import sit.Evaluator;
/*    */ import sit.TargetSet;
/*    */ import sit.mtLearner.MTLearner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SearchAlgorithmImpl
/*    */   implements SearchAlgorithm
/*    */ {
/*    */   protected MTLearner learner;
/*    */   protected Settings m_Sett;
/*    */   
/*    */   public void setMTLearner(MTLearner learner) {
/* 26 */     this.learner = learner;
/*    */   }
/*    */   
/*    */   public void setSettings(Settings s) {
/* 30 */     this.m_Sett = s;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected double eval(TargetSet tset, ClusAttrType mainTarget) {
/* 37 */     int nbFolds = this.learner.initLOOXVal();
/*    */ 
/*    */     
/* 40 */     ArrayList<RowData[]> folds = (ArrayList)new ArrayList<>();
/* 41 */     for (int f = 0; f < nbFolds; f++) {
/* 42 */       folds.add(this.learner.LearnModel(tset, f));
/*    */     }
/*    */     
/* 45 */     String error = this.m_Sett.getError();
/* 46 */     if (error.equals("MSE"))
/*    */     {
/* 48 */       return 1.0D - Evaluator.getMSE(folds, mainTarget.getArrayIndex());
/*    */     }
/* 50 */     if (error.equals("MisclassificationError")) {
/* 51 */       return 1.0D - Evaluator.getMisclassificationError(folds, mainTarget.getArrayIndex());
/*    */     }
/*    */ 
/*    */     
/* 55 */     return Evaluator.getPearsonCorrelation(folds, mainTarget.getArrayIndex());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\SearchAlgorithmImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */