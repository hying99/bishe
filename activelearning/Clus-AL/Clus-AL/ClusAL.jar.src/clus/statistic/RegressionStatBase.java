/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ import jeans.util.StringUtils;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
/*     */ import org.apache.commons.math.distribution.TDistribution;
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
/*     */ public abstract class RegressionStatBase
/*     */   extends ClusStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public int m_NbAttrs;
/*     */   public NumericAttrType[] m_Attrs;
/*     */   public double[] m_Means;
/*     */   
/*     */   public RegressionStatBase(NumericAttrType[] attrs) {
/*  53 */     this(attrs, false);
/*     */   }
/*     */   
/*     */   public RegressionStatBase(NumericAttrType[] attrs, boolean onlymean) {
/*  57 */     this.m_Attrs = attrs;
/*  58 */     this.m_NbAttrs = attrs.length;
/*  59 */     if (onlymean) {
/*  60 */       this.m_Means = new double[this.m_NbAttrs];
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNbAttributes() {
/*  65 */     return this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public NumericAttrType[] getAttributes() {
/*  69 */     return this.m_Attrs;
/*     */   }
/*     */   
/*     */   public NumericAttrType getAttribute(int idx) {
/*  73 */     return this.m_Attrs[idx];
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/*  77 */     RegressionStatBase or = (RegressionStatBase)other;
/*  78 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  79 */       this.m_Means[i] = this.m_Means[i] + weight * or.m_Means[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/*  84 */     updateWeighted(tuple, tuple.getWeight());
/*     */   }
/*     */ 
/*     */   
/*     */   public void computePrediction() {}
/*     */ 
/*     */   
/*     */   public abstract void calcMean(double[] paramArrayOfdouble);
/*     */   
/*     */   public void calcMean() {
/*  94 */     if (this.m_Means == null) {
/*  95 */       this.m_Means = new double[this.m_NbAttrs];
/*     */     }
/*  97 */     calcMean(this.m_Means);
/*     */   }
/*     */   
/*     */   public void setMeans(double[] means) {
/* 101 */     this.m_Means = means;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract double getMean(int paramInt);
/*     */ 
/*     */   
/*     */   public abstract double getSVarS(int paramInt);
/*     */ 
/*     */   
/*     */   public double getVariance(int i) {
/* 113 */     return (this.m_SumWeight != 0.0D) ? (getSVarS(i) / this.m_SumWeight) : 0.0D;
/*     */   }
/*     */   
/*     */   public double getStandardDeviation(int i) {
/* 117 */     return Math.sqrt(getSVarS(i) / (this.m_SumWeight - 1.0D));
/*     */   }
/*     */   
/*     */   public double getScaledSS(int i, ClusAttributeWeights scale) {
/* 121 */     return getSVarS(i) * scale.getWeight((ClusAttrType)getAttribute(i));
/*     */   }
/*     */   
/*     */   public double getScaledVariance(int i, ClusAttributeWeights scale) {
/* 125 */     return getVariance(i) * scale.getWeight((ClusAttrType)getAttribute(i));
/*     */   }
/*     */   
/*     */   public double getRootScaledVariance(int i, ClusAttributeWeights scale) {
/* 129 */     return Math.sqrt(getScaledVariance(i, scale));
/*     */   }
/*     */   
/*     */   public double[] getRootScaledVariances(ClusAttributeWeights scale) {
/* 133 */     int nb = getNbAttributes();
/* 134 */     double[] res = new double[nb];
/* 135 */     for (int i = 0; i < res.length; i++) {
/* 136 */       res[i] = getRootScaledVariance(i, scale);
/*     */     }
/* 138 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDispersion(ClusAttributeWeights scale, RowData data) {
/* 146 */     System.err.println(getClass().getName() + ": getDispersion(): Not yet implemented!");
/* 147 */     return Double.POSITIVE_INFINITY;
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
/*     */   public double getTTestPValue(int att, ClusStatManager stat_manager) throws MathException {
/* 160 */     double global_mean = ((CombStat)stat_manager.getTrainSetStat()).m_RegStat.getMean(att);
/* 161 */     double global_var = ((CombStat)stat_manager.getTrainSetStat()).m_RegStat.getVariance(att);
/* 162 */     double global_n = ((CombStat)stat_manager.getTrainSetStat()).getTotalWeight();
/* 163 */     double local_mean = getMean(att);
/* 164 */     double local_var = getVariance(att);
/* 165 */     double local_n = getTotalWeight();
/* 166 */     double t = Math.abs(local_mean - global_mean) / Math.sqrt(local_var / local_n + global_var / global_n);
/* 167 */     double degreesOfFreedom = 0.0D;
/* 168 */     degreesOfFreedom = df(local_var, global_var, local_n, global_n);
/* 169 */     DistributionFactory distributionFactory = DistributionFactory.newInstance();
/* 170 */     TDistribution tDistribution = distributionFactory.createTDistribution(degreesOfFreedom);
/* 171 */     return 1.0D - tDistribution.cumulativeProbability(-t, t);
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
/*     */   
/*     */   protected double df(double v1, double v2, double n1, double n2) {
/* 185 */     return (v1 / n1 + v2 / n2) * (v1 / n1 + v2 / n2) / (v1 * v1 / n1 * n1 * (n1 - 1.0D) + v2 * v2 / n2 * n2 * (n2 - 1.0D));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getNumericPred() {
/* 193 */     return this.m_Means;
/*     */   }
/*     */   
/*     */   public String getPredictedClassName(int idx) {
/* 197 */     return "";
/*     */   }
/*     */   
/*     */   public int getNbNumericAttributes() {
/* 201 */     return this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public double getError(ClusAttributeWeights scale) {
/* 205 */     return getSVarS(scale);
/*     */   }
/*     */   
/*     */   public double getErrorDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 209 */     return getSVarSDiff(scale, other);
/*     */   }
/*     */   
/*     */   public double getRMSE(ClusAttributeWeights scale) {
/* 213 */     return Math.sqrt(getSVarS(scale) / getTotalWeight());
/*     */   }
/*     */   
/*     */   public void initNormalizationWeights(ClusAttributeWeights weights, boolean[] shouldNormalize) {
/* 217 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 218 */       int idx = this.m_Attrs[i].getIndex();
/* 219 */       if (shouldNormalize[idx]) {
/*     */         
/* 221 */         double var = getVariance(i);
/* 222 */         double norm = (var > 0.0D) ? (1.0D / var) : 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 228 */         weights.setWeight((ClusAttrType)this.m_Attrs[i], norm);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSquaredDistance(DataTuple tuple, ClusAttributeWeights weights) {
/* 237 */     double sum = 0.0D;
/* 238 */     for (int i = 0; i < getNbAttributes(); i++) {
/* 239 */       NumericAttrType type = getAttribute(i);
/* 240 */       double dist = type.getNumeric(tuple) - this.m_Means[i];
/* 241 */       sum += dist * dist * weights.getWeight((ClusAttrType)type);
/*     */     } 
/* 243 */     return sum / getNbAttributes();
/*     */   }
/*     */   
/*     */   public String getArrayOfStatistic() {
/* 247 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 248 */     StringBuffer buf = new StringBuffer();
/* 249 */     buf.append("[");
/* 250 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 251 */       if (i != 0) {
/* 252 */         buf.append(",");
/*     */       }
/* 254 */       buf.append(fr.format(this.m_Means[i]));
/*     */     } 
/* 256 */     buf.append("]");
/* 257 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getPredictString() {
/* 261 */     StringBuffer buf = new StringBuffer();
/* 262 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 263 */       if (i != 0) {
/* 264 */         buf.append(",");
/*     */       }
/* 266 */       buf.append(String.valueOf(this.m_Means[i]));
/*     */     } 
/* 268 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getDebugString() {
/* 272 */     NumberFormat fr = ClusFormat.THREE_AFTER_DOT;
/* 273 */     StringBuffer buf = new StringBuffer();
/* 274 */     buf.append("["); int i;
/* 275 */     for (i = 0; i < this.m_NbAttrs; i++) {
/* 276 */       if (i != 0) {
/* 277 */         buf.append(",");
/*     */       }
/* 279 */       buf.append(fr.format(getMean(i)));
/*     */     } 
/* 281 */     buf.append("]");
/* 282 */     buf.append("[");
/* 283 */     for (i = 0; i < this.m_NbAttrs; i++) {
/* 284 */       if (i != 0) {
/* 285 */         buf.append(",");
/*     */       }
/* 287 */       buf.append(fr.format(getVariance(i)));
/*     */     } 
/* 289 */     buf.append("]");
/* 290 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void printDistribution(PrintWriter wrt) throws IOException {
/* 294 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 295 */     for (int i = 0; i < this.m_Attrs.length; i++) {
/* 296 */       wrt.print(StringUtils.printStr(this.m_Attrs[i].getName(), 35));
/* 297 */       wrt.print(" [");
/* 298 */       wrt.print(fr.format(getMean(i)));
/* 299 */       wrt.print(",");
/* 300 */       wrt.print(fr.format(getVariance(i)));
/* 301 */       wrt.println("]");
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {
/* 306 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 307 */       ClusAttrType type = this.m_Attrs[i].cloneType();
/* 308 */       type.setName(prefix + "-p-" + type.getName());
/* 309 */       schema.addAttrType(type);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getPredictWriterString() {
/* 314 */     StringBuffer buf = new StringBuffer();
/* 315 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 316 */       if (i != 0) {
/* 317 */         buf.append(",");
/*     */       }
/* 319 */       if (this.m_Means != null) {
/* 320 */         buf.append("" + this.m_Means[i]);
/*     */       } else {
/* 322 */         buf.append("?");
/*     */       } 
/*     */     } 
/* 325 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void predictTuple(DataTuple prediction) {
/* 329 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 330 */       NumericAttrType type = this.m_Attrs[i];
/* 331 */       type.setNumeric(prediction, this.m_Means[i]);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void vote(ArrayList<RegressionStatBase> votes) {
/* 336 */     reset();
/* 337 */     this.m_Means = new double[this.m_NbAttrs];
/* 338 */     int nb_votes = votes.size();
/* 339 */     for (int j = 0; j < nb_votes; j++) {
/* 340 */       RegressionStatBase vote = votes.get(j);
/* 341 */       for (int i = 0; i < this.m_NbAttrs; i++)
/* 342 */         this.m_Means[i] = this.m_Means[i] + vote.m_Means[i] / nb_votes; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\RegressionStatBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */