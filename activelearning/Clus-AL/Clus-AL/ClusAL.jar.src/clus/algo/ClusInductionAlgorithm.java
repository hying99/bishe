/*     */ package clus.algo;
/*     */ 
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataPreprocs;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
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
/*     */ public abstract class ClusInductionAlgorithm
/*     */ {
/*     */   protected ClusSchema m_Schema;
/*     */   protected ClusStatManager m_StatManager;
/*     */   
/*     */   public ClusInductionAlgorithm(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  54 */     this.m_Schema = schema;
/*  55 */     this.m_StatManager = new ClusStatManager(schema, sett);
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm(ClusInductionAlgorithm other) {
/*  59 */     this.m_Schema = other.m_Schema;
/*  60 */     this.m_StatManager = other.m_StatManager;
/*     */   }
/*     */   
/*     */   public ClusSchema getSchema() {
/*  64 */     return this.m_Schema;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  68 */     return this.m_StatManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Settings getSettings() {
/*  76 */     return this.m_StatManager.getSettings();
/*     */   }
/*     */   
/*     */   public void initialize() throws ClusException, IOException {
/*  80 */     this.m_StatManager.initStatisticAndStatManager();
/*     */   }
/*     */   
/*     */   public void getPreprocs(DataPreprocs pps) {
/*  84 */     this.m_StatManager.getPreprocs(pps);
/*     */   }
/*     */   
/*     */   public boolean isModelWriter() {
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeModel(ClusModelCollectionIO strm) throws IOException {}
/*     */   
/*     */   public ClusData createData() {
/*  95 */     return (ClusData)new RowData(this.m_Schema);
/*     */   }
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/*  99 */     ClusModel model = induceSingleUnpruned(cr);
/* 100 */     ClusModelInfo model_info = cr.addModelInfo(1);
/* 101 */     model_info.setModel(model);
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract ClusModel induceSingleUnpruned(ClusRun paramClusRun) throws ClusException, IOException;
/*     */   
/*     */   public void initializeHeuristic() {}
/*     */   
/*     */   public ClusStatistic createTotalClusteringStat(RowData data) {
/* 110 */     ClusStatistic stat = this.m_StatManager.createClusteringStat();
/* 111 */     stat.setSDataSize(data.getNbRows());
/* 112 */     data.calcTotalStat(stat);
/* 113 */     stat.optimizePreCalc(data);
/* 114 */     return stat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusStatistic createTotalTargetStat(RowData data) {
/* 121 */     ClusStatistic stat = this.m_StatManager.createTargetStat();
/* 122 */     stat.setSDataSize(data.getNbRows());
/* 123 */     data.calcTotalStat(stat);
/* 124 */     stat.optimizePreCalc(data);
/* 125 */     return stat;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\ClusInductionAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */