/*     */ package clus.main;
/*     */ 
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ public class ClusRun
/*     */   extends ClusModelInfoList
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_Index;
/*     */   protected boolean m_FileTestSet;
/*     */   protected ClusData m_Train;
/*     */   protected ClusData m_Prune;
/*     */   protected ClusData m_Orig;
/*     */   protected ClusSelection m_TestSel;
/*     */   protected ClusSelection m_PruneSel;
/*     */   protected TupleIterator m_Test;
/*     */   protected ClusSummary m_Summary;
/*     */   
/*     */   public ClusRun(ClusData train, ClusSummary summary) {
/*  48 */     this.m_Index = 1;
/*  49 */     this.m_Train = train;
/*  50 */     this.m_Summary = summary;
/*     */   }
/*     */   
/*     */   public ClusRun(ClusRun other) {
/*  54 */     this.m_Index = 1;
/*  55 */     this.m_Train = other.m_Train;
/*  56 */     this.m_Prune = other.m_Prune;
/*  57 */     this.m_Orig = other.m_Orig;
/*  58 */     this.m_TestSel = other.m_TestSel;
/*  59 */     this.m_PruneSel = other.m_PruneSel;
/*  60 */     this.m_Test = other.m_Test;
/*  61 */     this.m_Summary = other.m_Summary.getSummaryClone();
/*  62 */     setModels(other.cloneModels());
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  66 */     return this.m_Summary.getStatManager();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusSummary getSummary() {
/*  75 */     return this.m_Summary;
/*     */   }
/*     */   
/*     */   public ClusErrorList getTrainError() {
/*  79 */     return this.m_Summary.getTrainError();
/*     */   }
/*     */   
/*     */   public ClusErrorList getTestError() {
/*  83 */     return this.m_Summary.getTestError();
/*     */   }
/*     */   
/*     */   public ClusErrorList getValidationError() {
/*  87 */     return this.m_Summary.getValidationError();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getIndex() {
/*  96 */     return this.m_Index;
/*     */   }
/*     */   
/*     */   public final void setIndex(int idx) {
/* 100 */     this.m_Index = idx;
/*     */   }
/*     */   
/*     */   public final String getIndexString() {
/* 104 */     String ridx = String.valueOf(getIndex());
/* 105 */     if (getIndex() < 10) {
/* 106 */       ridx = "0" + ridx;
/*     */     }
/* 108 */     return ridx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClusData getOriginalSet() {
/* 117 */     return this.m_Orig;
/*     */   }
/*     */   
/*     */   public final void setOrigSet(ClusData data) {
/* 121 */     this.m_Orig = data;
/*     */   }
/*     */   
/*     */   public final RowData getDataSet(int whichone) throws ClusException, IOException {
/* 125 */     switch (whichone) {
/*     */       case 0:
/* 127 */         return (RowData)getTrainingSet();
/*     */       case 1:
/* 129 */         return getTestSet();
/*     */       case 2:
/* 131 */         return (RowData)getPruneSet();
/*     */     } 
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClusData getTrainingSet() {
/* 142 */     return this.m_Train;
/*     */   }
/*     */   
/*     */   public final void setTrainingSet(ClusData data) {
/* 146 */     this.m_Train = data;
/*     */   }
/*     */   
/*     */   public final TupleIterator getTrainIter() {
/* 150 */     return (TupleIterator)((RowData)this.m_Train).getIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void copyTrainingData() {
/* 155 */     RowData clone = (RowData)this.m_Train.cloneData();
/* 156 */     setTrainingSet((ClusData)clone);
/*     */   }
/*     */   
/*     */   public final ClusSelection getTestSelection() {
/* 160 */     return this.m_TestSel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setTestSet(TupleIterator iter) {
/* 169 */     this.m_Test = iter;
/*     */   }
/*     */   
/*     */   public final TupleIterator getTestIter() {
/* 173 */     return this.m_Test;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void initTestSet() throws IOException, ClusException {
/* 178 */     RowData data = (RowData)this.m_Test.getData();
/*     */     
/* 180 */     if (data == null) {
/* 181 */       data = (RowData)this.m_Test.createInMemoryData();
/* 182 */       this.m_Test = (TupleIterator)data.getIterator();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final RowData getTestSet() throws IOException, ClusException {
/* 189 */     if (this.m_Test == null) {
/* 190 */       return null;
/*     */     }
/* 192 */     RowData data = (RowData)this.m_Test.getData();
/*     */     
/* 194 */     if (data == null) {
/* 195 */       data = (RowData)this.m_Test.createInMemoryData();
/* 196 */       this.m_Test = (TupleIterator)data.getIterator();
/*     */     } 
/* 198 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClusData getPruneSet() {
/* 207 */     return this.m_Prune;
/*     */   }
/*     */   
/*     */   public final TupleIterator getPruneIter() {
/* 211 */     return (TupleIterator)((RowData)this.m_Prune).getIterator();
/*     */   }
/*     */   
/*     */   public final void setPruneSet(ClusData data, ClusSelection sel) {
/* 215 */     this.m_Prune = data;
/* 216 */     this.m_PruneSel = sel;
/*     */   }
/*     */   
/*     */   public final ClusSelection getPruneSelection() {
/* 220 */     return this.m_PruneSel;
/*     */   }
/*     */   
/*     */   public void combineTrainAndValidSets() {
/* 224 */     RowData valid = (RowData)getPruneSet();
/* 225 */     if (valid != null) {
/* 226 */       RowData train = (RowData)getTrainingSet();
/* 227 */       ArrayList lst = train.toArrayList();
/* 228 */       lst.addAll(valid.toArrayList());
/* 229 */       setTrainingSet((ClusData)new RowData(lst, train.getSchema()));
/* 230 */       setPruneSet((ClusData)null, (ClusSelection)null);
/* 231 */       changePruneError((ClusErrorList)null);
/* 232 */       copyTrainingData();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void changeTestError(ClusErrorList par) {
/* 242 */     this.m_Summary.setTestError(par);
/* 243 */     int nb_models = getNbModels();
/* 244 */     for (int i = 0; i < nb_models; i++) {
/* 245 */       ClusModelInfo my = getModelInfo(i);
/* 246 */       my.setTestError(par.getErrorClone());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void changePruneError(ClusErrorList par) {
/* 251 */     this.m_Summary.setValidationError(par);
/* 252 */     int nb_models = getNbModels();
/* 253 */     for (int i = 0; i < nb_models; i++) {
/* 254 */       ClusModelInfo my = getModelInfo(i);
/* 255 */       my.setValidationError((par != null) ? par.getErrorClone() : null);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void deleteData() {
/* 260 */     this.m_Train = null;
/* 261 */     this.m_Prune = null;
/* 262 */     this.m_Orig = null;
/* 263 */     this.m_TestSel = null;
/* 264 */     this.m_PruneSel = null;
/* 265 */     this.m_Test = null;
/*     */   }
/*     */   
/*     */   public void deleteDataAndModels() {
/* 269 */     deleteData();
/* 270 */     deleteModels();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\main\ClusRun.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */