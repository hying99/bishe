/*     */ package sit.mtLearner;
/*     */ 
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.selection.XValRandomSelection;
/*     */ import clus.selection.XValSelection;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import sit.TargetSet;
/*     */ 
/*     */ public abstract class MTLearnerImpl implements MTLearner {
/*     */   protected RowData m_Data;
/*  16 */   protected RowData m_Test = null;
/*     */ 
/*     */   
/*     */   protected Settings m_Sett;
/*     */ 
/*     */   
/*     */   protected XValMainSelection m_XValSel;
/*     */   
/*     */   protected ResultsCache m_Cache;
/*     */   
/*     */   protected ClusAttrType m_MainTarget;
/*     */ 
/*     */   
/*     */   public void init(RowData data, Settings sett) {
/*  30 */     this.m_Data = data;
/*  31 */     this.m_Sett = sett;
/*  32 */     this.m_Cache = new ResultsCache();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData[] LearnModel(TargetSet targets) throws Exception {
/*  38 */     if (this.m_Test == null) {
/*  39 */       throw new Exception();
/*     */     }
/*     */ 
/*     */     
/*  43 */     RowData[] result = this.m_Cache.getResult(targets, this.m_Test);
/*  44 */     if (result != null) {
/*  45 */       return result;
/*     */     }
/*  47 */     result = LearnModel(targets, this.m_Data, this.m_Test);
/*  48 */     this.m_Cache.addResult(targets, result);
/*  49 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setData(RowData data) {
/*  55 */     this.m_Data = data;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initXVal(int nrFolds) {
/*     */     try {
/*  62 */       ClusRandom.initialize(this.m_Sett);
/*  63 */       this.m_XValSel = (XValMainSelection)new XValRandomSelection(this.m_Data.getNbRows(), nrFolds);
/*  64 */     } catch (ClusException e) {
/*  65 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int initLOOXVal() {
/*     */     try {
/*  75 */       ClusRandom.initialize(this.m_Sett);
/*  76 */       this.m_XValSel = (XValMainSelection)new XValRandomSelection(this.m_Data.getNbRows(), this.m_Data.getNbRows());
/*  77 */     } catch (ClusException e) {
/*  78 */       e.printStackTrace();
/*     */     } 
/*     */     
/*  81 */     return this.m_Data.getNbRows();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData[] LearnModel(TargetSet targets, int foldNr) {
/*  88 */     XValSelection msel = new XValSelection(this.m_XValSel, foldNr);
/*  89 */     RowData train = (RowData)this.m_Data.cloneData();
/*  90 */     RowData test = (RowData)train.select((ClusSelection)msel);
/*     */     
/*  92 */     RowData[] result = this.m_Cache.getResult(targets, test);
/*  93 */     if (result != null) {
/*  94 */       return result;
/*     */     }
/*  96 */     result = LearnModel(targets, train, test);
/*  97 */     this.m_Cache.addResult(targets, result);
/*  98 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTestData(RowData test) {
/* 105 */     this.m_Test = test;
/*     */   }
/*     */   
/*     */   public void setMainTarget(ClusAttrType target) {
/* 109 */     this.m_MainTarget = target;
/*     */   }
/*     */   
/*     */   protected abstract RowData[] LearnModel(TargetSet paramTargetSet, RowData paramRowData1, RowData paramRowData2);
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\mtLearner\MTLearnerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */