/*     */ package clus.algo.kNN;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
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
/*     */ public class KNNStatistics
/*     */ {
/*     */   private DataTuple[] $prototypes;
/*     */   
/*     */   public KNNStatistics(RowData data) {
/*  40 */     calcMeasures(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcMeasures(RowData data) {
/*  50 */     ClusSchema schema = data.getSchema();
/*  51 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/*  52 */     this.$prototypes = new DataTuple[1];
/*     */     
/*  54 */     for (int i = 0; i < this.$prototypes.length; i++) {
/*  55 */       if (schema.isSparse()) {
/*  56 */         this.$prototypes[i] = (DataTuple)new SparseDataTuple(schema);
/*  57 */         (this.$prototypes[i]).m_Doubles = new double[schema.getM_NbSparse()];
/*     */       } else {
/*  59 */         this.$prototypes[i] = new DataTuple(schema);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  66 */     for (ClusAttrType attr : attrs) {
/*     */       
/*  68 */       if (attr.getTypeIndex() == 0) {
/*  69 */         calcNominalMeasures(data, (NominalAttrType)attr);
/*  70 */       } else if (attr.getTypeIndex() == 1) {
/*  71 */         calcNumericMeasures(data, (NumericAttrType)attr);
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
/*     */   public void calcNominalMeasures(RowData data, NominalAttrType attr) {
/*  83 */     int[] occurences = new int[attr.getNbValues()];
/*     */     
/*  85 */     int aTargetValues = this.$prototypes.length;
/*  86 */     int[][] p_occs = new int[attr.getNbValues()][aTargetValues];
/*     */     
/*  88 */     NominalStatistic stat = new NominalStatistic();
/*  89 */     int nbr = data.getNbRows();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  94 */     for (int i = 0; i < nbr; i++) {
/*  95 */       int curVal = attr.getNominal(data.getTuple(i));
/*     */       
/*  97 */       int curTargetVal = 0;
/*     */ 
/*     */       
/* 100 */       if (curVal != attr.getNbValues()) {
/* 101 */         occurences[curVal] = occurences[curVal] + 1;
/* 102 */         p_occs[curVal][curTargetVal] = p_occs[curVal][curTargetVal] + 1;
/*     */       } 
/*     */     } 
/*     */     
/* 106 */     int max = occurences[0];
/* 107 */     int[] maxs = new int[aTargetValues];
/* 108 */     int index_max = 0;
/* 109 */     int[] index_maxs = new int[aTargetValues];
/*     */     
/* 111 */     for (int j = 1; j < attr.getNbValues(); j++) {
/* 112 */       if (max < occurences[j]) {
/* 113 */         max = occurences[j];
/* 114 */         index_max = j;
/*     */       } 
/* 116 */       for (int m = 1; m < aTargetValues; m++) {
/* 117 */         if (maxs[m] < p_occs[j][m]) {
/* 118 */           maxs[m] = p_occs[j][m];
/* 119 */           index_maxs[m] = j;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 124 */     stat.setMean(index_max);
/*     */     
/* 126 */     attr.setStatistic(stat);
/*     */ 
/*     */ 
/*     */     
/* 130 */     int idx = attr.getArrayIndex();
/*     */     
/* 132 */     for (int k = 0; k < aTargetValues; k++)
/*     */     {
/* 134 */       this.$prototypes[k].setIntVal(index_maxs[k], idx);
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
/*     */   
/*     */   public void calcNumericMeasures(RowData data, NumericAttrType attr) {
/* 148 */     NumericStatistic stat = new NumericStatistic();
/* 149 */     int nbr = data.getNbRows();
/* 150 */     int actualnbr = nbr;
/*     */ 
/*     */     
/* 153 */     double mean = 0.0D;
/*     */     
/* 155 */     int aTargetValues = this.$prototypes.length;
/* 156 */     double[] means = new double[aTargetValues];
/* 157 */     double[] p_occs = new double[aTargetValues];
/*     */     
/* 159 */     double min = Double.POSITIVE_INFINITY;
/* 160 */     double max = Double.NEGATIVE_INFINITY;
/*     */     int i;
/* 162 */     for (i = 0; i < nbr; i++) {
/* 163 */       double curVal = attr.getNumeric(data.getTuple(i));
/*     */ 
/*     */       
/* 166 */       int curTargetVal = 0;
/*     */ 
/*     */ 
/*     */       
/* 170 */       if (curVal == Double.NaN || Double.isInfinite(curVal)) {
/* 171 */         actualnbr--;
/*     */       } else {
/* 173 */         if (curVal < min) {
/* 174 */           min = curVal;
/*     */         }
/* 176 */         if (curVal > max) {
/* 177 */           max = curVal;
/*     */         }
/*     */         
/* 180 */         means[curTargetVal] = means[curTargetVal] + curVal;
/* 181 */         p_occs[curTargetVal] = p_occs[curTargetVal] + 1.0D;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 187 */     for (i = 0; i < aTargetValues; i++) {
/* 188 */       means[i] = means[i] / p_occs[i];
/* 189 */       mean += means[i];
/*     */     } 
/* 191 */     mean /= aTargetValues;
/*     */ 
/*     */     
/* 194 */     double variance = 0.0D;
/* 195 */     for (i = 0; i < nbr; i++) {
/* 196 */       double curVal = attr.getNumeric(data.getTuple(i));
/*     */       
/* 198 */       if (curVal != Double.NaN) {
/* 199 */         variance += Math.pow(curVal - mean, 2.0D);
/*     */       }
/*     */     } 
/* 202 */     variance /= actualnbr;
/* 203 */     stat.setMean(mean);
/* 204 */     stat.setMax(max);
/* 205 */     stat.setMin(min);
/* 206 */     stat.setVariance(variance);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     attr.setStatistic(stat);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 218 */     int idx = attr.getArrayIndex();
/* 219 */     for (int j = 0; j < aTargetValues; j++)
/*     */     {
/*     */ 
/*     */       
/* 223 */       this.$prototypes[j].setDoubleVal(means[j], idx);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DataTuple[] getPrototypes() {
/* 230 */     return this.$prototypes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataTuple getPrototype(int idx) {
/* 238 */     return this.$prototypes[idx];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printPrototype(int idx, RowData data) {
/* 245 */     DataTuple t = this.$prototypes[idx];
/* 246 */     ClusSchema schema = data.getSchema();
/* 247 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/*     */     
/* 249 */     for (int i = 0; i < attrs.length; i++) {
/* 250 */       if (attrs[i].getTypeIndex() == 0) {
/* 251 */         System.out.print(attrs[i].getNominal(t) + ",");
/* 252 */       } else if (attrs[i].getTypeIndex() == 1) {
/* 253 */         System.out.print(attrs[i].getNumeric(t) + ",");
/*     */       } 
/*     */     } 
/* 256 */     System.out.println(")");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\kNN\KNNStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */