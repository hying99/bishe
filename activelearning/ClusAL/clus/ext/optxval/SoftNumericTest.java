/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.NumericTest;
/*     */ import clus.model.test.SoftTest;
/*     */ import clus.util.ClusFormat;
/*     */ import java.util.Arrays;
/*     */ import jeans.util.array.MyIntArray;
/*     */ import jeans.util.sort.DoubleIndexSorter;
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
/*     */ public class SoftNumericTest
/*     */   extends SoftTest
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected NumericAttrType m_Type;
/*     */   protected double[] m_Bounds;
/*     */   protected int[] m_Folds;
/*     */   protected int[][] m_BoundFolds;
/*     */   protected int[][] m_PosFolds;
/*     */   protected int[][] m_NegFolds;
/*     */   
/*     */   public SoftNumericTest(NodeTest test, int gsize) {
/*  49 */     NumericTest ntest = (NumericTest)test;
/*  50 */     this.m_Type = ntest.getNumType();
/*  51 */     this.m_Bounds = new double[gsize];
/*  52 */     this.m_Folds = new int[gsize];
/*  53 */     setArity(2);
/*     */   }
/*     */   
/*     */   public String getString() {
/*  57 */     StringBuffer buff = new StringBuffer();
/*  58 */     buff.append(this.m_Type.getName());
/*  59 */     buff.append(" > [");
/*  60 */     for (int i = 0; i < this.m_Bounds.length; i++) {
/*  61 */       if (i != 0) buff.append("; "); 
/*  62 */       buff.append(ClusFormat.FOUR_AFTER_DOT.format(this.m_Bounds[i]));
/*  63 */       buff.append(" ");
/*  64 */       int[] folds = this.m_BoundFolds[i];
/*  65 */       for (int j = 0; j < folds.length; j++) {
/*  66 */         if (j != 0) buff.append(","); 
/*  67 */         buff.append(folds[j]);
/*     */       } 
/*     */     } 
/*  70 */     buff.append("]");
/*  71 */     return buff.toString();
/*     */   }
/*     */   
/*     */   public int foldSetIntersects2(DataTuple data, int[] folds) {
/*  75 */     int[] dfolds = data.m_Folds;
/*  76 */     for (int i = 0; i < folds.length; i++) {
/*  77 */       if (dfolds[folds[i]] > 0) return 1; 
/*     */     } 
/*  79 */     return 0;
/*     */   }
/*     */   
/*     */   public int softPredictNb2(DataTuple tuple, int branch) {
/*  83 */     double value = tuple.m_Doubles[this.m_Type.getArrayIndex()];
/*  84 */     if (branch == 0) {
/*  85 */       if (value > this.m_Bounds[0]) return 1; 
/*  86 */       if (value <= this.m_Bounds[this.m_Bounds.length - 1]) return 0; 
/*  87 */       int i = 1;
/*  88 */       for (; value <= this.m_Bounds[i]; i++);
/*  89 */       return foldSetIntersects2(tuple, this.m_PosFolds[i]);
/*     */     } 
/*  91 */     if (value > this.m_Bounds[0]) return 0; 
/*  92 */     if (value <= this.m_Bounds[this.m_Bounds.length - 1]) return 1; 
/*  93 */     int ps = 1;
/*  94 */     for (; value <= this.m_Bounds[ps]; ps++);
/*  95 */     return foldSetIntersects2(tuple, this.m_NegFolds[ps]);
/*     */   }
/*     */ 
/*     */   
/*     */   public int foldSetIntersects(DataTuple data, int[] folds) {
/* 100 */     int fold = data.m_Index;
/* 101 */     if (fold == -1) {
/* 102 */       return MyIntArray.isIntersectSorted(data.m_Folds, folds);
/*     */     }
/* 104 */     if (Arrays.binarySearch(folds, fold) >= 0) return (folds.length > 1) ? 1 : 0; 
/* 105 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int softPredictNb(DataTuple tuple, int branch) {
/* 110 */     double value = tuple.m_Doubles[this.m_Type.getArrayIndex()];
/* 111 */     if (branch == 0) {
/* 112 */       if (value > this.m_Bounds[0]) return 1; 
/* 113 */       if (value <= this.m_Bounds[this.m_Bounds.length - 1]) return 0; 
/* 114 */       int i = 1;
/* 115 */       for (; value <= this.m_Bounds[i]; i++);
/* 116 */       return foldSetIntersects(tuple, this.m_PosFolds[i]);
/*     */     } 
/* 118 */     if (value > this.m_Bounds[0]) return 0; 
/* 119 */     if (value <= this.m_Bounds[this.m_Bounds.length - 1]) return 1; 
/* 120 */     int ps = 1;
/* 121 */     for (; value <= this.m_Bounds[ps]; ps++);
/* 122 */     return foldSetIntersects(tuple, this.m_NegFolds[ps]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] updateFoldSet(DataTuple data, int[] folds) {
/* 128 */     int rfolds[], fold = data.m_Index;
/* 129 */     if (fold == -1) {
/* 130 */       rfolds = MyIntArray.intersectSorted(data.m_Folds, folds);
/*     */     }
/* 132 */     else if (Arrays.binarySearch(folds, fold) >= 0) {
/* 133 */       rfolds = MyIntArray.remove(fold, folds);
/*     */     } else {
/* 135 */       rfolds = folds;
/*     */     } 
/*     */     
/* 138 */     return rfolds;
/*     */   }
/*     */   
/*     */   public int updateFoldSet(RowData data, int idx, DataTuple tuple, int[] folds) {
/* 142 */     int[] res = updateFoldSet(tuple, folds);
/* 143 */     if (res.length > 0) {
/* 144 */       DataTuple clone = tuple.cloneTuple();
/* 145 */       clone.m_Index = -1;
/* 146 */       clone.m_Folds = res;
/* 147 */       data.setTuple(clone, idx++);
/*     */     } 
/* 149 */     return idx;
/*     */   }
/*     */   
/*     */   public int softPredict(RowData res, DataTuple tuple, int idx, int branch) {
/* 153 */     double value = tuple.m_Doubles[this.m_Type.getArrayIndex()];
/* 154 */     if (branch == 0) {
/* 155 */       if (value <= this.m_Bounds[this.m_Bounds.length - 1]) return idx; 
/* 156 */       if (value > this.m_Bounds[0]) {
/* 157 */         res.setTuple(tuple, idx++);
/* 158 */         return idx;
/*     */       } 
/* 160 */       int i = 1;
/* 161 */       for (; value <= this.m_Bounds[i]; i++);
/* 162 */       return updateFoldSet(res, idx, tuple, this.m_PosFolds[i]);
/*     */     } 
/* 164 */     if (value > this.m_Bounds[0]) return idx; 
/* 165 */     if (value <= this.m_Bounds[this.m_Bounds.length - 1]) {
/* 166 */       res.setTuple(tuple, idx++);
/* 167 */       return idx;
/*     */     } 
/* 169 */     int ps = 1;
/* 170 */     for (; value <= this.m_Bounds[ps]; ps++);
/* 171 */     return updateFoldSet(res, idx, tuple, this.m_NegFolds[ps]);
/*     */   }
/*     */ 
/*     */   
/*     */   public int updateFoldSet2(RowData data, int idx, DataTuple tuple, int[] folds) {
/* 176 */     if (foldSetIntersects2(tuple, folds) != 0) {
/* 177 */       DataTuple clone = tuple.cloneTuple();
/* 178 */       int[] origflds = tuple.m_Folds;
/* 179 */       clone.m_Folds = new int[origflds.length];
/* 180 */       for (int i = 0; i < folds.length; i++) {
/* 181 */         int ps = folds[i];
/* 182 */         clone.m_Folds[ps] = origflds[ps];
/*     */       } 
/* 184 */       data.setTuple(clone, idx++);
/*     */     } 
/* 186 */     return idx;
/*     */   }
/*     */   
/*     */   public int softPredict2(RowData res, DataTuple tuple, int idx, int branch) {
/* 190 */     double value = tuple.m_Doubles[this.m_Type.getArrayIndex()];
/* 191 */     if (branch == 0) {
/* 192 */       if (value <= this.m_Bounds[this.m_Bounds.length - 1]) return idx; 
/* 193 */       if (value > this.m_Bounds[0]) {
/* 194 */         res.setTuple(tuple, idx++);
/* 195 */         return idx;
/*     */       } 
/* 197 */       int i = 1;
/* 198 */       for (; value <= this.m_Bounds[i]; i++);
/* 199 */       return updateFoldSet2(res, idx, tuple, this.m_PosFolds[i]);
/*     */     } 
/* 201 */     if (value > this.m_Bounds[0]) return idx; 
/* 202 */     if (value <= this.m_Bounds[this.m_Bounds.length - 1]) {
/* 203 */       res.setTuple(tuple, idx++);
/* 204 */       return idx;
/*     */     } 
/* 206 */     int ps = 1;
/* 207 */     for (; value <= this.m_Bounds[ps]; ps++);
/* 208 */     return updateFoldSet2(res, idx, tuple, this.m_NegFolds[ps]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addTest(int idx, int fold, NodeTest test) {
/* 213 */     NumericTest ntest = (NumericTest)test;
/* 214 */     this.m_Bounds[idx] = ntest.getBound();
/* 215 */     this.m_Folds[idx] = fold;
/*     */   }
/*     */   
/*     */   public void sortIntervals() {
/* 219 */     DoubleIndexSorter sort = DoubleIndexSorter.getInstance();
/* 220 */     sort.setData(this.m_Bounds, this.m_Folds);
/* 221 */     sort.sort();
/*     */     
/* 223 */     int nb = 0;
/* 224 */     double prev = Double.NaN;
/* 225 */     for (int i = 0; i < this.m_Bounds.length; i++) {
/* 226 */       if (this.m_Bounds[i] != prev) {
/* 227 */         nb++;
/* 228 */         prev = this.m_Bounds[i];
/*     */       } 
/*     */     } 
/*     */     
/* 232 */     double[] old = this.m_Bounds;
/* 233 */     this.m_Bounds = new double[nb];
/* 234 */     this.m_BoundFolds = new int[nb][];
/*     */     
/* 236 */     int idx = 0;
/* 237 */     prev = old[0];
/* 238 */     int nb_folds = 0;
/* 239 */     for (int j = 0; j <= old.length; j++) {
/* 240 */       if (j == old.length || old[j] != prev) {
/* 241 */         if (nb_folds > 0) {
/* 242 */           int[] folds = this.m_BoundFolds[idx] = new int[nb_folds];
/* 243 */           for (int m = 0; m < nb_folds; ) { folds[m] = this.m_Folds[j - m - 1]; m++; }
/* 244 */            Arrays.sort(folds);
/* 245 */           nb_folds = 0;
/*     */         } 
/* 247 */         this.m_Bounds[idx++] = prev;
/* 248 */         if (j < old.length) prev = old[j]; 
/*     */       } 
/* 250 */       nb_folds++;
/*     */     } 
/*     */     
/* 253 */     this.m_Folds = null;
/*     */     
/* 255 */     this.m_PosFolds = new int[nb][];
/* 256 */     this.m_NegFolds = new int[nb][];
/* 257 */     int[] prev_v = this.m_PosFolds[nb - 1] = this.m_BoundFolds[nb - 1]; int k;
/* 258 */     for (k = nb - 2; k > 0; k--) {
/* 259 */       prev_v = this.m_PosFolds[k] = MyIntArray.mergeSorted(prev_v, this.m_BoundFolds[k]);
/*     */     }
/* 261 */     prev_v = this.m_NegFolds[1] = this.m_BoundFolds[0];
/* 262 */     for (k = 2; k < nb; k++) {
/* 263 */       prev_v = this.m_NegFolds[k] = MyIntArray.mergeSorted(prev_v, this.m_BoundFolds[k - 1]);
/*     */     }
/*     */   }
/*     */   
/*     */   public ClusAttrType getType() {
/* 268 */     return (ClusAttrType)this.m_Type;
/*     */   }
/*     */   
/*     */   public void setType(ClusAttrType type) {
/* 272 */     this.m_Type = (NumericAttrType)type;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\optxval\SoftNumericTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */