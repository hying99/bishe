/*     */ package clus.algo.split;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.RowDataSortHelper;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Random;
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
/*     */ public class FindBestTest
/*     */ {
/*  36 */   public CurrentBestTestAndHeuristic m_BestTest = new CurrentBestTestAndHeuristic();
/*     */   
/*  38 */   protected RowDataSortHelper m_SortHelper = new RowDataSortHelper();
/*     */   
/*     */   protected ClusStatManager m_StatManager;
/*     */   protected NominalSplit m_Split;
/*     */   protected int m_MaxStats;
/*     */   
/*     */   public FindBestTest(ClusStatManager mgr) {
/*  45 */     this.m_StatManager = mgr;
/*  46 */     this.m_MaxStats = getSchema().getMaxNbStats();
/*     */   }
/*     */   
/*     */   public FindBestTest(ClusStatManager mgr, NominalSplit split) {
/*  50 */     this.m_StatManager = mgr;
/*  51 */     this.m_Split = split;
/*  52 */     this.m_MaxStats = getSchema().getMaxNbStats();
/*     */   }
/*     */   
/*     */   public ClusSchema getSchema() {
/*  56 */     return getStatManager().getSchema();
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  60 */     return this.m_StatManager;
/*     */   }
/*     */   
/*     */   public RowDataSortHelper getSortHelper() {
/*  64 */     return this.m_SortHelper;
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  68 */     return getStatManager().getSettings();
/*     */   }
/*     */   
/*     */   public CurrentBestTestAndHeuristic getBestTest() {
/*  72 */     return this.m_BestTest;
/*     */   }
/*     */   
/*     */   public void cleanSplit() {
/*  76 */     this.m_Split = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void findNominal(NominalAttrType at, RowData data) {
/*  82 */     RowData sample = createSample(data);
/*  83 */     int nbvalues = at.getNbValues();
/*  84 */     this.m_BestTest.reset(nbvalues + 1);
/*  85 */     int nb_rows = sample.getNbRows();
/*  86 */     if (nbvalues == 2 && !at.hasMissing()) {
/*     */       
/*  88 */       for (int i = 0; i < nb_rows; i++) {
/*  89 */         DataTuple tuple = sample.getTuple(i);
/*  90 */         int value = at.getNominal(tuple);
/*     */         
/*  92 */         if (value == 0) {
/*  93 */           this.m_BestTest.m_TestStat[0].updateWeighted(tuple, i);
/*     */         }
/*     */       } 
/*     */       
/*  97 */       this.m_BestTest.m_TestStat[1].copy(this.m_BestTest.m_TotStat);
/*  98 */       this.m_BestTest.m_TestStat[1].subtractFromThis(this.m_BestTest.m_TestStat[0]);
/*     */     } else {
/*     */       
/* 101 */       for (int i = 0; i < nb_rows; i++) {
/* 102 */         DataTuple tuple = sample.getTuple(i);
/* 103 */         int value = at.getNominal(tuple);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 109 */         this.m_BestTest.m_TestStat[value].updateWeighted(tuple, i);
/*     */       } 
/*     */     } 
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
/* 124 */     this.m_Split.findSplit(this.m_BestTest, at);
/*     */   }
/*     */ 
/*     */   
/*     */   public void findNominalRandom(NominalAttrType at, RowData data, Random rn) {
/* 129 */     RowData sample = createSample(data);
/* 130 */     int nbvalues = at.getNbValues();
/* 131 */     this.m_BestTest.reset(nbvalues + 1);
/*     */     
/* 133 */     int nb_rows = sample.getNbRows();
/* 134 */     for (int i = 0; i < nb_rows; i++) {
/* 135 */       DataTuple tuple = sample.getTuple(i);
/* 136 */       int value = at.getNominal(tuple);
/* 137 */       this.m_BestTest.m_TestStat[value].updateWeighted(tuple, i);
/*     */     } 
/*     */     
/* 140 */     this.m_Split.findRandomSplit(this.m_BestTest, at, rn);
/*     */   }
/*     */   
/*     */   public void findNumeric(NumericAttrType at, RowData data) {
/* 144 */     RowData sample = createSample(data);
/*     */     
/* 146 */     if (at.isSparse()) {
/* 147 */       sample.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 149 */       sample.sort(at);
/*     */     } 
/*     */     
/* 152 */     this.m_BestTest.reset(2);
/*     */     
/* 154 */     int first = 0;
/* 155 */     int nb_rows = sample.getNbRows();
/*     */ 
/*     */     
/* 158 */     this.m_BestTest.copyTotal();
/*     */     
/* 160 */     if (at.hasMissing()) {
/*     */       DataTuple tuple;
/*     */       
/* 163 */       while (first < nb_rows && at.isMissing(tuple = sample.getTuple(first))) {
/* 164 */         this.m_BestTest.m_MissingStat.updateWeighted(tuple, first);
/* 165 */         first++;
/*     */       } 
/*     */ 
/*     */       
/* 169 */       this.m_BestTest.subtractMissing();
/*     */     } 
/* 171 */     double prev = Double.NaN;
/*     */ 
/*     */     
/* 174 */     for (int i = first; i < nb_rows; i++) {
/* 175 */       DataTuple tuple = sample.getTuple(i);
/* 176 */       double value = at.getNumeric(tuple);
/*     */       
/* 178 */       if (value != prev) {
/*     */ 
/*     */         
/* 181 */         if (value != Double.NaN)
/*     */         {
/*     */ 
/*     */           
/* 185 */           this.m_BestTest.updateNumeric(value, (ClusAttrType)at);
/*     */         }
/* 187 */         prev = value;
/*     */       } 
/* 189 */       this.m_BestTest.m_PosStat.updateWeighted(tuple, i);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void findNumeric(NumericAttrType at, ArrayList<DataTuple> data) {
/*     */     ArrayList<DataTuple> sample;
/* 196 */     if (getSettings().getTreeSplitSampling() > 0) {
/* 197 */       RowData tmp = new RowData(data, getSchema());
/* 198 */       RowData smpl = createSample(tmp);
/* 199 */       if (at.isSparse()) {
/* 200 */         smpl.sortSparse(at, getSortHelper());
/*     */       } else {
/* 202 */         smpl.sort(at);
/*     */       } 
/* 204 */       sample = smpl.toArrayList();
/*     */     } else {
/* 206 */       sample = data;
/*     */     } 
/*     */     
/* 209 */     this.m_BestTest.reset(2);
/*     */     
/* 211 */     int first = 0;
/* 212 */     int nb_rows = sample.size();
/*     */     
/* 214 */     this.m_BestTest.copyTotal();
/* 215 */     if (at.hasMissing()) {
/*     */       
/* 217 */       while (first < nb_rows && at.isMissing(sample.get(first))) {
/* 218 */         DataTuple tuple = sample.get(first);
/* 219 */         this.m_BestTest.m_MissingStat.updateWeighted(tuple, first);
/* 220 */         first++;
/*     */       } 
/* 222 */       this.m_BestTest.subtractMissing();
/*     */     } 
/* 224 */     double prev = Double.NaN;
/*     */     
/* 226 */     for (int i = first; i < nb_rows; i++) {
/* 227 */       DataTuple tuple = sample.get(i);
/* 228 */       double value = at.getNumeric(tuple);
/* 229 */       if (value != prev) {
/* 230 */         if (value != Double.NaN)
/*     */         {
/* 232 */           this.m_BestTest.updateNumeric(value, (ClusAttrType)at);
/*     */         }
/* 234 */         prev = value;
/*     */       } 
/* 236 */       this.m_BestTest.m_PosStat.updateWeighted(tuple, i);
/*     */     } 
/* 238 */     this.m_BestTest.updateNumeric(0.0D, (ClusAttrType)at);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void findNumericRandom(NumericAttrType at, RowData data, RowData orig_data, Random rn) {
/* 244 */     int idx = at.getArrayIndex();
/*     */     
/* 246 */     if (at.isSparse()) {
/* 247 */       data.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 249 */       data.sort(at);
/*     */     } 
/* 251 */     this.m_BestTest.reset(2);
/*     */     
/* 253 */     int first = 0;
/* 254 */     int nb_rows = data.getNbRows();
/*     */     
/* 256 */     this.m_BestTest.copyTotal();
/* 257 */     if (at.hasMissing()) {
/*     */       DataTuple tuple;
/* 259 */       while (first < nb_rows && (tuple = data.getTuple(first)).hasNumMissing(idx)) {
/* 260 */         this.m_BestTest.m_MissingStat.updateWeighted(tuple, first);
/* 261 */         first++;
/*     */       } 
/* 263 */       this.m_BestTest.subtractMissing();
/*     */     } 
/*     */ 
/*     */     
/* 267 */     if (at.isSparse()) {
/* 268 */       orig_data.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 270 */       orig_data.sort(at);
/*     */     } 
/*     */     
/* 273 */     int orig_first = 0;
/* 274 */     int orig_nb_rows = orig_data.getNbRows();
/* 275 */     if (at.hasMissing()) {
/*     */       DataTuple tuple;
/* 277 */       while (orig_first < orig_nb_rows && (
/* 278 */         tuple = orig_data.getTuple(orig_first)).hasNumMissing(idx)) {
/* 279 */         orig_first++;
/*     */       }
/*     */     } 
/*     */     
/* 283 */     double min_value = orig_data.getTuple(orig_nb_rows - 1).getDoubleVal(idx);
/* 284 */     double max_value = orig_data.getTuple(orig_first).getDoubleVal(idx);
/* 285 */     double split_value = (max_value - min_value) * rn.nextDouble() + min_value;
/* 286 */     for (int i = first; i < nb_rows; i++) {
/* 287 */       DataTuple tuple = data.getTuple(i);
/* 288 */       if (tuple.getDoubleVal(idx) <= split_value) {
/*     */         break;
/*     */       }
/* 291 */       this.m_BestTest.m_PosStat.updateWeighted(tuple, i);
/*     */     } 
/* 293 */     this.m_BestTest.updateNumeric(split_value, (ClusAttrType)at);
/* 294 */     System.err.println("Inverse splits not yet included!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void initSelectorAndSplit(ClusStatistic totstat) throws ClusException {
/* 299 */     this.m_BestTest.create(this.m_StatManager, this.m_MaxStats);
/* 300 */     this.m_BestTest.setRootStatistic(totstat);
/* 301 */     if (getSettings().isBinarySplit()) {
/* 302 */       this.m_Split = new SubsetSplit();
/*     */     } else {
/* 304 */       this.m_Split = new NArySplit();
/*     */     } 
/* 306 */     this.m_Split.initialize(this.m_StatManager);
/*     */   }
/*     */   
/*     */   public boolean initSelectorAndStopCrit(ClusStatistic total, RowData data) {
/* 310 */     this.m_BestTest.initTestSelector(total, data);
/* 311 */     this.m_Split.setSDataSize(data.getNbRows());
/* 312 */     return this.m_BestTest.stopCrit();
/*     */   }
/*     */   
/*     */   public void setInitialData(ClusStatistic total, RowData data) throws ClusException {
/* 316 */     this.m_BestTest.setInitialData(total, data);
/*     */   }
/*     */   
/*     */   private RowData createSample(RowData original) {
/* 320 */     return original.sample(getSettings().getTreeSplitSampling());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\split\FindBestTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */