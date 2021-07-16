/*     */ package clus.algo;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.resource.ResourceInfo;
/*     */ import jeans.util.cmdline.CMDLineArgs;
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
/*     */ public abstract class ClusInductionAlgorithmType
/*     */ {
/*     */   public static final int REGULAR_TREE = 0;
/*     */   protected Clus m_Clus;
/*     */   
/*     */   public ClusInductionAlgorithmType(Clus clus) {
/*  49 */     this.m_Clus = clus;
/*     */   }
/*     */   
/*     */   public Clus getClus() {
/*  53 */     return this.m_Clus;
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm getInduce() {
/*  57 */     return getClus().getInduce();
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  61 */     return getInduce().getStatManager();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  65 */     return getClus().getSettings();
/*     */   }
/*     */   
/*     */   public abstract ClusInductionAlgorithm createInduce(ClusSchema paramClusSchema, Settings paramSettings, CMDLineArgs paramCMDLineArgs) throws ClusException, IOException;
/*     */   
/*     */   public void printInfo() {
/*  71 */     System.out.println("Classifier: " + getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void pruneAll(ClusRun paramClusRun) throws ClusException, IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ClusModel pruneSingle(ClusModel paramClusModel, ClusRun paramClusRun) throws ClusException, IOException;
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcess(ClusRun cr) throws ClusException, IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/*  91 */     long start_time = ResourceInfo.getTime();
/*  92 */     getInduce().induceAll(cr);
/*  93 */     long done_time = ResourceInfo.getTime();
/*  94 */     cr.setInductionTime(done_time - start_time);
/*  95 */     pruneAll(cr);
/*  96 */     cr.setPruneTime(ResourceInfo.getTime() - done_time);
/*  97 */     postProcess(cr);
/*  98 */     if (Settings.VERBOSE > 0) {
/*  99 */       String cpu = ResourceInfo.isLibLoaded() ? " (CPU)" : "";
/* 100 */       System.out.println("Induction Time: " + (cr.getInductionTime() / 1000.0D) + " sec" + cpu);
/* 101 */       System.out.println("Pruning Time: " + (cr.getPruneTime() / 1000.0D) + " sec" + cpu);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusModel induceSingle(ClusRun cr) throws ClusException, IOException {
/* 106 */     ClusModel unpruned = induceSingleUnpruned(cr);
/* 107 */     return pruneSingle(unpruned, cr);
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 111 */     return getInduce().induceSingleUnpruned(cr);
/*     */   }
/*     */   
/*     */   public void saveInformation(String fname) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\ClusInductionAlgorithmType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */