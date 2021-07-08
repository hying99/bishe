/*     */ package clus.algo.rules;
/*     */ 
/*     */ import clus.algo.split.FindBestTest;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
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
/*     */ public class FindBestTestRules
/*     */   extends FindBestTest
/*     */ {
/*     */   public FindBestTestRules(ClusStatManager mgr) {
/*  45 */     super(mgr);
/*     */   }
/*     */   
/*     */   public FindBestTestRules(ClusStatManager mgr, NominalSplit split) {
/*  49 */     super(mgr, split);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void findNominal(NominalAttrType at, RowData data) {
/*  59 */     int nbvalues = at.getNbValues();
/*  60 */     this.m_BestTest.reset(nbvalues + 1);
/*  61 */     int nb_rows = data.getNbRows();
/*  62 */     if (!getSettings().isHeurRuleDist()) {
/*     */       
/*  64 */       for (int i = 0; i < nb_rows; i++) {
/*  65 */         DataTuple tuple = data.getTuple(i);
/*  66 */         int value = at.getNominal(tuple);
/*  67 */         this.m_BestTest.m_TestStat[value].updateWeighted(tuple, i);
/*     */       } 
/*     */     } else {
/*     */       
/*  71 */       int[][] data_idx_per_val = new int[nbvalues][nb_rows];
/*  72 */       for (int j = 0; j < nbvalues; j++) {
/*  73 */         for (int m = 0; m < nb_rows; m++) {
/*  74 */           data_idx_per_val[j][m] = -1;
/*     */         }
/*     */       } 
/*     */       
/*  78 */       int[] counts = new int[nbvalues];
/*  79 */       for (int i = 0; i < nb_rows; i++) {
/*  80 */         DataTuple tuple = data.getTuple(i);
/*  81 */         int value = at.getNominal(tuple);
/*  82 */         this.m_BestTest.m_TestStat[value].updateWeighted(tuple, i);
/*  83 */         if (value < nbvalues) {
/*  84 */           data_idx_per_val[value][i] = tuple.getIndex();
/*  85 */           counts[value] = counts[value] + 1;
/*     */         } 
/*     */       } 
/*     */       
/*  89 */       int[][] data_ipv = new int[nbvalues][];
/*  90 */       for (int k = 0; k < nbvalues; k++) {
/*  91 */         data_ipv[k] = new int[counts[k]];
/*  92 */         int m = 0;
/*  93 */         for (int n = 0; n < nb_rows; n++) {
/*  94 */           if (data_idx_per_val[k][n] != -1) {
/*  95 */             data_ipv[k][m] = data_idx_per_val[k][n];
/*  96 */             m++;
/*     */           } 
/*     */         } 
/*     */       } 
/* 100 */       ((ClusRuleHeuristicDispersion)this.m_BestTest.m_Heuristic).setDataIndexesPerVal(data_ipv);
/*     */     } 
/*     */     
/* 103 */     this.m_Split.findSplit(this.m_BestTest, at);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void findNominalRandom(NominalAttrType at, RowData data, Random rn) {
/* 114 */     int nbvalues = at.getNbValues();
/* 115 */     this.m_BestTest.reset(nbvalues + 1);
/*     */     
/* 117 */     int nb_rows = data.getNbRows();
/* 118 */     for (int i = 0; i < nb_rows; i++) {
/* 119 */       DataTuple tuple = data.getTuple(i);
/* 120 */       int value = at.getNominal(tuple);
/* 121 */       this.m_BestTest.m_TestStat[value].updateWeighted(tuple, i);
/*     */     } 
/*     */     
/* 124 */     this.m_Split.findRandomSplit(this.m_BestTest, at, rn);
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
/*     */   public void findNumeric(NumericAttrType at, RowData data) {
/* 137 */     if (at.isSparse()) {
/* 138 */       data.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 140 */       data.sort(at);
/*     */     } 
/* 142 */     this.m_BestTest.reset(2);
/*     */     
/* 144 */     int first = 0;
/* 145 */     int nb_rows = data.getNbRows();
/*     */     
/* 147 */     this.m_BestTest.copyTotal();
/* 148 */     if (at.hasMissing()) {
/*     */       DataTuple tuple;
/* 150 */       while (first < nb_rows && at.isMissing(tuple = data.getTuple(first))) {
/* 151 */         this.m_BestTest.m_MissingStat.updateWeighted(tuple, first);
/* 152 */         first++;
/*     */       } 
/* 154 */       this.m_BestTest.subtractMissing();
/*     */     } 
/* 156 */     double prev = Double.NaN;
/* 157 */     int[] data_idx = new int[nb_rows];
/* 158 */     if (getSettings().isHeurRuleDist()) {
/* 159 */       for (int j = first; j < nb_rows; j++) {
/* 160 */         data_idx[j] = data.getTuple(j).getIndex();
/*     */       }
/*     */     }
/* 163 */     for (int i = first; i < nb_rows; i++) {
/* 164 */       DataTuple tuple = data.getTuple(i);
/* 165 */       double value = at.getNumeric(tuple);
/* 166 */       if (value != prev) {
/* 167 */         if (value != Double.NaN) {
/* 168 */           if (getSettings().isHeurRuleDist()) {
/* 169 */             int[] subset_idx = new int[i - first];
/* 170 */             System.arraycopy(data_idx, first, subset_idx, 0, i - first);
/* 171 */             ((ClusRuleHeuristicDispersion)this.m_BestTest.m_Heuristic).setDataIndexes(subset_idx);
/*     */           } 
/*     */           
/* 174 */           this.m_BestTest.updateNumeric(value, (ClusAttrType)at);
/*     */         } 
/* 176 */         prev = value;
/*     */       } 
/* 178 */       this.m_BestTest.m_PosStat.updateWeighted(tuple, i);
/*     */     } 
/*     */     
/* 181 */     if (this.m_StatManager.isRuleInduceOnly()) {
/* 182 */       this.m_BestTest.reset();
/* 183 */       DataTuple next_tuple = data.getTuple(nb_rows - 1);
/* 184 */       double next = at.getNumeric(next_tuple);
/* 185 */       for (int j = nb_rows - 1; j > first; j--) {
/* 186 */         DataTuple tuple = next_tuple;
/* 187 */         next_tuple = data.getTuple(j - 1);
/* 188 */         double value = next;
/* 189 */         next = at.getNumeric(next_tuple);
/* 190 */         this.m_BestTest.m_PosStat.updateWeighted(tuple, j);
/* 191 */         if (value != next && value != Double.NaN) {
/* 192 */           if (getSettings().isHeurRuleDist()) {
/* 193 */             int[] subset_idx = new int[nb_rows - j];
/* 194 */             System.arraycopy(data_idx, j, subset_idx, 0, nb_rows - j);
/* 195 */             ((ClusRuleHeuristicDispersion)this.m_BestTest.m_Heuristic).setDataIndexes(subset_idx);
/*     */           } 
/*     */           
/* 198 */           this.m_BestTest.updateInverseNumeric(value, (ClusAttrType)at);
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */   public void findNumericRandom(NumericAttrType at, RowData data, RowData orig_data, Random rn) {
/* 213 */     int idx = at.getArrayIndex();
/*     */     
/* 215 */     if (at.isSparse()) {
/* 216 */       data.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 218 */       data.sort(at);
/*     */     } 
/* 220 */     this.m_BestTest.reset(2);
/*     */     
/* 222 */     int first = 0;
/* 223 */     int nb_rows = data.getNbRows();
/*     */     
/* 225 */     this.m_BestTest.copyTotal();
/* 226 */     if (at.hasMissing()) {
/*     */       DataTuple tuple;
/* 228 */       while (first < nb_rows && (tuple = data.getTuple(first)).hasNumMissing(idx)) {
/* 229 */         this.m_BestTest.m_MissingStat.updateWeighted(tuple, first);
/* 230 */         first++;
/*     */       } 
/* 232 */       this.m_BestTest.subtractMissing();
/*     */     } 
/*     */ 
/*     */     
/* 236 */     if (at.isSparse()) {
/* 237 */       orig_data.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 239 */       orig_data.sort(at);
/*     */     } 
/*     */     
/* 242 */     int orig_first = 0;
/* 243 */     int orig_nb_rows = orig_data.getNbRows();
/* 244 */     if (at.hasMissing()) {
/*     */       DataTuple tuple;
/* 246 */       while (orig_first < orig_nb_rows && (
/* 247 */         tuple = orig_data.getTuple(orig_first)).hasNumMissing(idx)) {
/* 248 */         orig_first++;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 253 */     double min_value = orig_data.getTuple(orig_nb_rows - 1).getDoubleVal(idx);
/* 254 */     double max_value = orig_data.getTuple(orig_first).getDoubleVal(idx);
/* 255 */     double split_value = (max_value - min_value) * rn.nextDouble() + min_value;
/* 256 */     for (int i = first; i < nb_rows; i++) {
/* 257 */       DataTuple tuple = data.getTuple(i);
/* 258 */       if (tuple.getDoubleVal(idx) <= split_value)
/* 259 */         break;  this.m_BestTest.m_PosStat.updateWeighted(tuple, i);
/*     */     } 
/* 261 */     this.m_BestTest.updateNumeric(split_value, (ClusAttrType)at);
/* 262 */     System.err.println("Inverse splits not yet included!");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\FindBestTestRules.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */