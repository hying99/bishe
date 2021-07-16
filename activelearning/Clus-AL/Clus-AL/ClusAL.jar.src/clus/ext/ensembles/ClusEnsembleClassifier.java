/*    */ package clus.ext.ensembles;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.algo.ClusInductionAlgorithm;
/*    */ import clus.algo.ClusInductionAlgorithmType;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.main.ClusRun;
/*    */ import clus.main.Settings;
/*    */ import clus.model.ClusModel;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
/*    */ import jeans.util.cmdline.CMDLineArgs;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusEnsembleClassifier
/*    */   extends ClusInductionAlgorithmType
/*    */ {
/*    */   public ClusEnsembleClassifier(Clus clus) {
/* 39 */     super(clus);
/*    */   }
/*    */ 
/*    */   
/*    */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/* 44 */     if (sett.getEnsembleMethod() == 4) {
/* 45 */       return new ClusBoostingInduce(schema, sett);
/*    */     }
/* 47 */     return new ClusEnsembleInduce(schema, sett, this.m_Clus);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 54 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*    */ 
/*    */   
/*    */   public void printInfo() {
/* 62 */     System.out.println("Ensemble Classifier");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ensembles\ClusEnsembleClassifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */