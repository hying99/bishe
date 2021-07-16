/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import jeans.math.MathUtil;
/*     */ import jeans.util.StringUtils;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.ChiSquaredDistribution;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
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
/*     */ public class ClassificationStat
/*     */   extends ClusStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public int m_NbTarget;
/*     */   public NominalAttrType[] m_Attrs;
/*     */   public ClassificationStat m_Training;
/*     */   public double[][] m_ClassCounts;
/*     */   public double[] m_SumWeights;
/*     */   public int[] m_MajorityClasses;
/*     */   
/*     */   public ClassificationStat(NominalAttrType[] nomAtts) {
/*  65 */     this.m_NbTarget = nomAtts.length;
/*  66 */     this.m_SumWeights = new double[this.m_NbTarget];
/*  67 */     this.m_ClassCounts = new double[this.m_NbTarget][];
/*  68 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  69 */       this.m_ClassCounts[i] = new double[nomAtts[i].getNbValues()];
/*     */     }
/*  71 */     this.m_Attrs = nomAtts;
/*     */   }
/*     */   
/*     */   public void setTrainingStat(ClusStatistic train) {
/*  75 */     this.m_Training = (ClassificationStat)train;
/*     */   }
/*     */   
/*     */   public int getNbNominalAttributes() {
/*  79 */     return this.m_NbTarget;
/*     */   }
/*     */   
/*     */   public NominalAttrType[] getAttributes() {
/*  83 */     return this.m_Attrs;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  87 */     ClassificationStat res = new ClassificationStat(this.m_Attrs);
/*  88 */     res.m_Training = this.m_Training;
/*  89 */     return res;
/*     */   }
/*     */   
/*     */   public void initSingleTargetFrom(double[] distro) {
/*  93 */     this.m_ClassCounts[0] = distro;
/*  94 */     this.m_SumWeight = 0.0D;
/*  95 */     for (int i = 0; i < distro.length; i++) {
/*  96 */       this.m_SumWeight += distro[i];
/*     */     }
/*  98 */     Arrays.fill(this.m_SumWeights, this.m_SumWeight);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NominalAttrType getAttribute(int idx) {
/* 109 */     return this.m_Attrs[idx];
/*     */   }
/*     */   
/*     */   public void reset() {
/* 113 */     this.m_NbExamples = 0;
/* 114 */     this.m_SumWeight = 0.0D;
/* 115 */     Arrays.fill(this.m_SumWeights, 0.0D);
/* 116 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 117 */       Arrays.fill(this.m_ClassCounts[i], 0.0D);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetToSimple(double weight) {
/* 126 */     this.m_NbExamples = 0;
/* 127 */     this.m_SumWeight = weight;
/* 128 */     Arrays.fill(this.m_SumWeights, weight);
/* 129 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 130 */       double[] clcts = this.m_ClassCounts[i];
/* 131 */       for (int j = 0; j < clcts.length; j++) {
/* 132 */         if (j == this.m_MajorityClasses[i]) {
/* 133 */           clcts[j] = weight;
/*     */         } else {
/* 135 */           clcts[j] = 0.0D;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/* 142 */     ClassificationStat or = (ClassificationStat)other;
/* 143 */     this.m_SumWeight = or.m_SumWeight;
/* 144 */     this.m_NbExamples = or.m_NbExamples;
/* 145 */     System.arraycopy(or.m_SumWeights, 0, this.m_SumWeights, 0, this.m_NbTarget);
/* 146 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 147 */       double[] my = this.m_ClassCounts[i];
/* 148 */       System.arraycopy(or.m_ClassCounts[i], 0, my, 0, my.length);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassificationStat normalizedCopy() {
/* 156 */     ClassificationStat copy = (ClassificationStat)cloneStat();
/* 157 */     copy.copy(this);
/* 158 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 159 */       for (int j = 0; j < (this.m_ClassCounts[i]).length; j++) {
/* 160 */         copy.m_ClassCounts[i][j] = copy.m_ClassCounts[i][j] / this.m_SumWeights[i];
/*     */       }
/*     */     } 
/* 163 */     Arrays.fill(copy.m_SumWeights, 1.0D);
/* 164 */     copy.m_SumWeight = 1.0D;
/* 165 */     return copy;
/*     */   }
/*     */   
/*     */   public boolean samePrediction(ClusStatistic other) {
/* 169 */     ClassificationStat or = (ClassificationStat)other;
/*     */     
/* 171 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 172 */       if (this.m_MajorityClasses[i] != or.m_MajorityClasses[i]) {
/* 173 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     return true;
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/* 187 */     ClassificationStat or = (ClassificationStat)other;
/* 188 */     this.m_SumWeight += weight * or.m_SumWeight;
/* 189 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 190 */       double[] my = this.m_ClassCounts[i];
/* 191 */       double[] your = or.m_ClassCounts[i];
/* 192 */       for (int j = 0; j < my.length; j++) {
/* 193 */         my[j] = my[j] + weight * your[j];
/*     */       }
/* 195 */       this.m_SumWeights[i] = this.m_SumWeights[i] + weight * or.m_SumWeights[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/* 200 */     ClassificationStat or = (ClassificationStat)other;
/* 201 */     this.m_SumWeight += or.m_SumWeight;
/* 202 */     this.m_NbExamples += or.m_NbExamples;
/* 203 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 204 */       double[] my = this.m_ClassCounts[i];
/* 205 */       double[] your = or.m_ClassCounts[i];
/* 206 */       for (int j = 0; j < my.length; j++) {
/* 207 */         my[j] = my[j] + your[j];
/*     */       }
/* 209 */       this.m_SumWeights[i] = this.m_SumWeights[i] + or.m_SumWeights[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {
/* 214 */     ClassificationStat or = (ClassificationStat)other;
/* 215 */     this.m_SumWeight += scale * or.m_SumWeight;
/* 216 */     this.m_NbExamples += or.m_NbExamples;
/* 217 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 218 */       double[] my = this.m_ClassCounts[i];
/* 219 */       double[] your = or.m_ClassCounts[i];
/* 220 */       for (int j = 0; j < my.length; j++) {
/* 221 */         my[j] = my[j] + scale * your[j];
/*     */       }
/* 223 */       this.m_SumWeights[i] = this.m_SumWeights[i] + scale * or.m_SumWeights[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 228 */     ClassificationStat or = (ClassificationStat)other;
/* 229 */     this.m_SumWeight -= or.m_SumWeight;
/* 230 */     this.m_NbExamples -= or.m_NbExamples;
/* 231 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 232 */       double[] my = this.m_ClassCounts[i];
/* 233 */       double[] your = or.m_ClassCounts[i];
/* 234 */       for (int j = 0; j < my.length; j++) {
/* 235 */         my[j] = my[j] - your[j];
/*     */       }
/* 237 */       this.m_SumWeights[i] = this.m_SumWeights[i] - or.m_SumWeights[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 242 */     ClassificationStat or = (ClassificationStat)other;
/* 243 */     or.m_SumWeight -= this.m_SumWeight;
/* 244 */     or.m_NbExamples -= this.m_NbExamples;
/* 245 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 246 */       double[] my = this.m_ClassCounts[i];
/* 247 */       double[] your = or.m_ClassCounts[i];
/* 248 */       for (int j = 0; j < my.length; j++) {
/* 249 */         my[j] = your[j] - my[j];
/*     */       }
/* 251 */       this.m_SumWeights[i] = or.m_SumWeights[i] - this.m_SumWeights[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/* 256 */     updateWeighted(tuple, tuple.getWeight());
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/* 260 */     this.m_NbExamples++;
/* 261 */     this.m_SumWeight += weight;
/* 262 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 263 */       int val = this.m_Attrs[i].getNominal(tuple);
/*     */       
/* 265 */       if (val != this.m_Attrs[i].getNbValues()) {
/* 266 */         this.m_ClassCounts[i][val] = this.m_ClassCounts[i][val] + weight;
/* 267 */         this.m_SumWeights[i] = this.m_SumWeights[i] + weight;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getMajorityClass(int attr) {
/* 273 */     int m_class = -1;
/* 274 */     double m_max = Double.NEGATIVE_INFINITY;
/* 275 */     double[] clcts = this.m_ClassCounts[attr];
/* 276 */     for (int i = 0; i < clcts.length; i++) {
/* 277 */       if (clcts[i] > m_max) {
/* 278 */         m_class = i;
/* 279 */         m_max = clcts[i];
/*     */       } 
/*     */     } 
/* 282 */     if (m_max <= 1.0E-9D && this.m_Training != null)
/*     */     {
/* 284 */       return this.m_Training.getMajorityClass(attr);
/*     */     }
/* 286 */     return m_class;
/*     */   }
/*     */   
/*     */   public int getMajorityClassDiff(int attr, ClassificationStat other) {
/* 290 */     int m_class = -1;
/* 291 */     double m_max = Double.NEGATIVE_INFINITY;
/* 292 */     double[] clcts1 = this.m_ClassCounts[attr];
/* 293 */     double[] clcts2 = other.m_ClassCounts[attr];
/* 294 */     for (int i = 0; i < clcts1.length; i++) {
/* 295 */       double diff = clcts1[i] - clcts2[i];
/* 296 */       if (diff > m_max) {
/* 297 */         m_class = i;
/* 298 */         m_max = diff;
/*     */       } 
/*     */     } 
/* 301 */     if (m_max <= 1.0E-9D && this.m_Training != null)
/*     */     {
/* 303 */       return this.m_Training.getMajorityClass(attr);
/*     */     }
/* 305 */     return m_class;
/*     */   }
/*     */   
/*     */   public double entropy() {
/* 309 */     double sum = 0.0D;
/* 310 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 311 */       sum += entropy(i);
/*     */     }
/* 313 */     return sum;
/*     */   }
/*     */   
/*     */   public double entropy(int attr) {
/* 317 */     double total = this.m_SumWeights[attr];
/* 318 */     if (total < 1.0E-6D) {
/* 319 */       return 0.0D;
/*     */     }
/* 321 */     double acc = 0.0D;
/* 322 */     double[] clcts = this.m_ClassCounts[attr];
/* 323 */     for (int i = 0; i < clcts.length; i++) {
/* 324 */       if (clcts[i] != 0.0D) {
/* 325 */         double prob = clcts[i] / total;
/* 326 */         acc += prob * Math.log(prob);
/*     */       } 
/*     */     } 
/* 329 */     return -acc / MathUtil.M_LN2;
/*     */   }
/*     */ 
/*     */   
/*     */   public double entropyDifference(ClassificationStat other) {
/* 334 */     double sum = 0.0D;
/* 335 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 336 */       sum += entropyDifference(i, other);
/*     */     }
/* 338 */     return sum;
/*     */   }
/*     */   
/*     */   public double entropyDifference(int attr, ClassificationStat other) {
/* 342 */     double acc = 0.0D;
/* 343 */     double[] clcts = this.m_ClassCounts[attr];
/* 344 */     double[] otcts = other.m_ClassCounts[attr];
/* 345 */     double total = this.m_SumWeights[attr] - other.m_SumWeights[attr];
/* 346 */     for (int i = 0; i < clcts.length; i++) {
/* 347 */       double diff = clcts[i] - otcts[i];
/* 348 */       if (diff != 0.0D) {
/* 349 */         acc += diff / total * Math.log(diff / total);
/*     */       }
/*     */     } 
/* 352 */     return -acc / MathUtil.M_LN2;
/*     */   }
/*     */   
/*     */   public double getSumWeight(int attr) {
/* 356 */     return this.m_SumWeights[attr];
/*     */   }
/*     */   
/*     */   public double getProportion(int attr, int cls) {
/* 360 */     double total = this.m_SumWeights[attr];
/* 361 */     if (total <= 1.0E-9D)
/*     */     {
/* 363 */       return this.m_Training.getProportion(attr, cls);
/*     */     }
/* 365 */     return this.m_ClassCounts[attr][cls] / total;
/*     */   }
/*     */ 
/*     */   
/*     */   public double gini(int attr) {
/* 370 */     double total = this.m_SumWeights[attr];
/*     */ 
/*     */     
/* 373 */     if (total <= 1.0E-9D)
/*     */     {
/*     */ 
/*     */       
/* 377 */       return 0.0D;
/*     */     }
/* 379 */     double sum = 0.0D;
/* 380 */     double[] clcts = this.m_ClassCounts[attr];
/*     */     
/* 382 */     for (int i = 0; i < clcts.length; i++) {
/* 383 */       double prob = clcts[i] / total;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 388 */       sum += prob * prob;
/*     */     } 
/*     */ 
/*     */     
/* 392 */     return 1.0D - sum;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/* 397 */     double result = 0.0D;
/* 398 */     double sum = this.m_SumWeight;
/*     */ 
/*     */     
/* 401 */     for (int i = 0; i < this.m_NbTarget; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 407 */       result += gini(i) * scale.getWeight((ClusAttrType)this.m_Attrs[i]) * sum;
/*     */     }
/*     */     
/* 410 */     return result / this.m_NbTarget;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSVarSDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 415 */     double result = 0.0D;
/* 416 */     double sum = this.m_SumWeight - other.m_SumWeight;
/* 417 */     ClassificationStat cother = (ClassificationStat)other;
/*     */     
/* 419 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 420 */       result += giniDifference(i, cother) * scale.getWeight((ClusAttrType)this.m_Attrs[i]) * sum;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 425 */     return result / this.m_NbTarget;
/*     */   }
/*     */   
/*     */   public double giniDifference(int attr, ClassificationStat other) {
/* 429 */     double wDiff = this.m_SumWeights[attr] - other.m_SumWeights[attr];
/*     */     
/* 431 */     if (wDiff <= 1.0E-9D)
/*     */     {
/* 433 */       return this.m_Training.gini(attr);
/*     */     }
/* 435 */     double sum = 0.0D;
/* 436 */     double[] clcts = this.m_ClassCounts[attr];
/* 437 */     double[] otcts = other.m_ClassCounts[attr];
/*     */ 
/*     */     
/* 440 */     for (int i = 0; i < clcts.length; i++) {
/* 441 */       double diff = clcts[i] - otcts[i];
/* 442 */       sum += diff / wDiff * diff / wDiff;
/*     */     } 
/* 444 */     return 1.0D - sum;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double computeSplitInfo(double sum_tot, double sum_pos, double sum_neg) {
/* 449 */     if (sum_pos == 0.0D) {
/* 450 */       return -1.0D * sum_neg / sum_tot * Math.log(sum_neg / sum_tot) / MathUtil.M_LN2;
/*     */     }
/* 452 */     if (sum_neg == 0.0D) {
/* 453 */       return -1.0D * sum_pos / sum_tot * Math.log(sum_pos / sum_tot) / MathUtil.M_LN2;
/*     */     }
/* 455 */     return 
/* 456 */       -(sum_pos / sum_tot * Math.log(sum_pos / sum_tot) + sum_neg / sum_tot * Math.log(sum_neg / sum_tot)) / MathUtil.M_LN2;
/*     */   }
/*     */   
/*     */   public boolean isCalcMean() {
/* 460 */     return (this.m_MajorityClasses != null);
/*     */   }
/*     */   
/*     */   public void calcMean() {
/* 464 */     this.m_MajorityClasses = new int[this.m_NbTarget];
/* 465 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 466 */       this.m_MajorityClasses[i] = getMajorityClass(i);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDispersion(ClusAttributeWeights scale, RowData data) {
/* 475 */     System.err.println(getClass().getName() + ": getDispersion(): Not yet implemented!");
/* 476 */     return Double.POSITIVE_INFINITY;
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
/*     */   public double getGTestPValue(int att, ClusStatManager stat_manager) throws MathException {
/* 488 */     double global_n = ((CombStat)stat_manager.getTrainSetStat()).getTotalWeight();
/* 489 */     double local_n = getTotalWeight();
/* 490 */     double ratio = local_n / global_n;
/* 491 */     double[] global_counts = ((CombStat)stat_manager.getTrainSetStat()).m_ClassStat.getClassCounts(att);
/* 492 */     double[] local_counts = getClassCounts(att);
/* 493 */     double g = 0.0D;
/* 494 */     for (int i = 0; i < global_counts.length; i++) {
/* 495 */       if (local_counts[i] > 0.0D && global_counts[i] > 0.0D) {
/* 496 */         g += 2.0D * local_counts[i] * Math.log(local_counts[i] / global_counts[i] * ratio);
/*     */       }
/*     */     } 
/* 499 */     double degreesOfFreedom = global_counts.length - 1.0D;
/* 500 */     DistributionFactory distributionFactory = DistributionFactory.newInstance();
/* 501 */     ChiSquaredDistribution chiSquaredDistribution = distributionFactory.createChiSquareDistribution(degreesOfFreedom);
/* 502 */     return 1.0D - chiSquaredDistribution.cumulativeProbability(g);
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
/*     */   public boolean getGTest(int att, ClusStatManager stat_manager) {
/* 514 */     double global_n = ((CombStat)stat_manager.getTrainSetStat()).getTotalWeight();
/* 515 */     double local_n = getTotalWeight();
/* 516 */     double ratio = local_n / global_n;
/* 517 */     double[] global_counts = ((CombStat)stat_manager.getTrainSetStat()).m_ClassStat.getClassCounts(att);
/* 518 */     double[] local_counts = getClassCounts(att);
/* 519 */     double g = 0.0D;
/* 520 */     for (int i = 0; i < global_counts.length; i++) {
/* 521 */       if (local_counts[i] > 0.0D && global_counts[i] > 0.0D) {
/* 522 */         g += 2.0D * local_counts[i] * Math.log(local_counts[i] / global_counts[i] * ratio);
/*     */       }
/*     */     } 
/* 525 */     int df = global_counts.length - 1;
/* 526 */     double chi2_crit = stat_manager.getChiSquareInvProb(df);
/* 527 */     return (g > chi2_crit);
/*     */   }
/*     */   
/*     */   public double[] getNumericPred() {
/* 531 */     return null;
/*     */   }
/*     */   
/*     */   public int[] getNominalPred() {
/* 535 */     return this.m_MajorityClasses;
/*     */   }
/*     */   
/*     */   public String getString2() {
/* 539 */     StringBuffer buf = new StringBuffer();
/* 540 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 541 */     buf.append(fr.format(this.m_SumWeight));
/* 542 */     buf.append(" ");
/* 543 */     buf.append(super.toString());
/* 544 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getArrayOfStatistic() {
/* 548 */     StringBuffer buf = new StringBuffer();
/* 549 */     if (this.m_MajorityClasses != null) {
/* 550 */       buf.append("[");
/* 551 */       for (int i = 0; i < this.m_NbTarget; i++) {
/* 552 */         if (i != 0) {
/* 553 */           buf.append(",");
/*     */         }
/* 555 */         buf.append(this.m_Attrs[i].getValue(this.m_MajorityClasses[i]));
/*     */       } 
/* 557 */       buf.append("]");
/*     */     } 
/* 559 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 567 */     StringBuffer buf = new StringBuffer();
/* 568 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 569 */     if (this.m_MajorityClasses != null) {
/* 570 */       buf.append("[");
/* 571 */       for (int i = 0; i < this.m_NbTarget; i++) {
/* 572 */         if (i != 0) {
/* 573 */           buf.append(",");
/*     */         }
/* 575 */         buf.append(this.m_Attrs[i].getValue(this.m_MajorityClasses[i]));
/*     */       } 
/* 577 */       buf.append("]");
/*     */     } else {
/* 579 */       buf.append("?");
/*     */     } 
/* 581 */     if (info.SHOW_DISTRIBUTION) {
/* 582 */       for (int j = 0; j < this.m_NbTarget; j++) {
/* 583 */         buf.append(" [");
/* 584 */         for (int i = 0; i < (this.m_ClassCounts[j]).length; i++) {
/* 585 */           if (i != 0) {
/* 586 */             buf.append(",");
/*     */           }
/* 588 */           buf.append(this.m_Attrs[j].getValue(i));
/* 589 */           buf.append(":");
/* 590 */           buf.append(fr.format(this.m_ClassCounts[j][i]));
/*     */         } 
/* 592 */         buf.append("]");
/*     */       } 
/* 594 */       if (info.SHOW_EXAMPLE_COUNT) {
/* 595 */         buf.append(":");
/* 596 */         buf.append(fr.format(this.m_SumWeight));
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 601 */     else if (this.m_MajorityClasses != null) {
/* 602 */       buf.append(" [");
/* 603 */       for (int i = 0; i < this.m_NbTarget; i++) {
/* 604 */         if (i != 0) {
/* 605 */           buf.append(",");
/*     */         }
/* 607 */         buf.append(this.m_ClassCounts[i][this.m_MajorityClasses[i]]);
/*     */       } 
/*     */       
/* 610 */       buf.append("]: ");
/* 611 */       buf.append(fr.format(this.m_SumWeight));
/*     */     } 
/*     */     
/* 614 */     return buf.toString();
/*     */   }
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {
/*     */     int i;
/* 618 */     for (i = 0; i < this.m_NbTarget; i++) {
/* 619 */       ClusAttrType type = this.m_Attrs[i].cloneType();
/* 620 */       type.setName(prefix + "-p-" + type.getName());
/* 621 */       schema.addAttrType(type);
/*     */     } 
/* 623 */     for (i = 0; i < this.m_NbTarget; i++) {
/* 624 */       for (int j = 0; j < (this.m_ClassCounts[i]).length; j++) {
/* 625 */         String value = this.m_Attrs[i].getValue(j);
/* 626 */         NumericAttrType numericAttrType = new NumericAttrType(prefix + "-p-" + this.m_Attrs[i].getName() + "-" + value);
/* 627 */         schema.addAttrType((ClusAttrType)numericAttrType);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getPredictWriterString() {
/* 633 */     StringBuffer buf = new StringBuffer(); int i;
/* 634 */     for (i = 0; i < this.m_NbTarget; i++) {
/* 635 */       if (i != 0) {
/* 636 */         buf.append(",");
/*     */       }
/* 638 */       if (this.m_MajorityClasses != null) {
/* 639 */         buf.append(this.m_Attrs[i].getValue(this.m_MajorityClasses[i]));
/*     */       } else {
/* 641 */         buf.append("?");
/*     */       } 
/*     */     } 
/* 644 */     for (i = 0; i < this.m_NbTarget; i++) {
/* 645 */       for (int j = 0; j < (this.m_ClassCounts[i]).length; j++) {
/* 646 */         buf.append(",");
/* 647 */         buf.append("" + this.m_ClassCounts[i][j]);
/*     */       } 
/*     */     } 
/* 650 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public int getNbClasses(int idx) {
/* 654 */     return (this.m_ClassCounts[idx]).length;
/*     */   }
/*     */   
/*     */   public double getCount(int idx, int cls) {
/* 658 */     return this.m_ClassCounts[idx][cls];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPredictedClassName(int idx) {
/* 664 */     return this.m_Attrs[idx].getValue(getMajorityClass(idx));
/*     */   }
/*     */   
/*     */   public String getClassName(int idx, int cls) {
/* 668 */     return this.m_Attrs[idx].getValue(cls);
/*     */   }
/*     */   
/*     */   public void setCount(int idx, int cls, double count) {
/* 672 */     this.m_ClassCounts[idx][cls] = count;
/*     */   }
/*     */   
/*     */   public String getSimpleString() {
/* 676 */     return getClassString() + " : " + super.getSimpleString();
/*     */   }
/*     */   
/*     */   public String getClassString() {
/* 680 */     StringBuffer buf = new StringBuffer();
/* 681 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 682 */       if (i != 0) {
/* 683 */         buf.append(",");
/*     */       }
/* 685 */       buf.append(this.m_Attrs[i].getValue(this.m_MajorityClasses[i]));
/*     */     } 
/* 687 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public double getError(ClusAttributeWeights scale) {
/* 691 */     double result = 0.0D;
/* 692 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 693 */       int maj = getMajorityClass(i);
/* 694 */       result += this.m_SumWeights[i] - this.m_ClassCounts[i][maj];
/*     */     } 
/* 696 */     return result / this.m_NbTarget;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getErrorRel() {
/* 702 */     return getError() / getTotalWeight();
/*     */   }
/*     */   
/*     */   public double getErrorDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 706 */     double result = 0.0D;
/* 707 */     ClassificationStat or = (ClassificationStat)other;
/* 708 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 709 */       int maj = getMajorityClassDiff(i, or);
/* 710 */       double diff_maj = this.m_ClassCounts[i][maj] - or.m_ClassCounts[i][maj];
/* 711 */       double diff_total = this.m_SumWeights[i] - or.m_SumWeights[i];
/* 712 */       result += diff_total - diff_maj;
/*     */     } 
/* 714 */     return result / this.m_NbTarget;
/*     */   }
/*     */   
/*     */   public int getNbPseudoTargets() {
/* 718 */     int nbTarget = 0;
/* 719 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 720 */       nbTarget += this.m_Attrs[i].getNbValues();
/*     */     }
/* 722 */     return nbTarget;
/*     */   }
/*     */   
/*     */   public void initNormalizationWeights(ClusAttributeWeights weights, boolean[] shouldNormalize) {
/* 726 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 727 */       int idx = this.m_Attrs[i].getIndex();
/* 728 */       if (shouldNormalize[idx]) {
/* 729 */         double var = gini(i);
/* 730 */         double norm = (var > 0.0D) ? (1.0D / var) : 1.0D;
/*     */         
/* 732 */         weights.setWeight((ClusAttrType)this.m_Attrs[i], norm);
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
/*     */   public double[] getClassCounts(int i) {
/* 744 */     return this.m_ClassCounts[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getClassCounts() {
/* 753 */     return this.m_ClassCounts;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 757 */     return getString();
/*     */   }
/*     */   
/*     */   public void printDistribution(PrintWriter wrt) throws IOException {
/* 761 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 762 */     for (int i = 0; i < this.m_Attrs.length; i++) {
/* 763 */       wrt.print(StringUtils.printStr(this.m_Attrs[i].getName(), 35));
/* 764 */       wrt.print(" [");
/* 765 */       double sum = 0.0D;
/* 766 */       for (int j = 0; j < (this.m_ClassCounts[i]).length; j++) {
/* 767 */         if (j != 0) {
/* 768 */           wrt.print(",");
/*     */         }
/* 770 */         wrt.print(this.m_Attrs[i].getValue(j) + ":");
/* 771 */         wrt.print(fr.format(this.m_ClassCounts[i][j]));
/* 772 */         sum += this.m_ClassCounts[i][j];
/*     */       } 
/* 774 */       wrt.println("]: " + fr.format(sum));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void predictTuple(DataTuple prediction) {
/* 779 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 780 */       NominalAttrType type = this.m_Attrs[i];
/* 781 */       type.setNominal(prediction, this.m_MajorityClasses[i]);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void vote(ArrayList votes) {
/* 786 */     switch (Settings.m_ClassificationVoteType.getValue()) {
/*     */       case 0:
/* 788 */         voteMajority(votes);
/*     */         return;
/*     */       case 1:
/* 791 */         voteProbDistr(votes);
/*     */         return;
/*     */     } 
/* 794 */     voteMajority(votes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void voteMajority(ArrayList<ClassificationStat> votes) {
/* 799 */     reset();
/* 800 */     int nb_votes = votes.size();
/* 801 */     this.m_SumWeight = nb_votes;
/* 802 */     Arrays.fill(this.m_SumWeights, nb_votes);
/* 803 */     for (int j = 0; j < nb_votes; j++) {
/* 804 */       ClassificationStat vote = votes.get(j);
/* 805 */       for (int i = 0; i < this.m_NbTarget; i++) {
/* 806 */         this.m_ClassCounts[i][vote.getNominalPred()[i]] = this.m_ClassCounts[i][vote.getNominalPred()[i]] + 1.0D;
/*     */       }
/*     */     } 
/* 809 */     calcMean();
/*     */   }
/*     */   
/*     */   public void voteProbDistr(ArrayList<ClassificationStat> votes) {
/* 813 */     reset();
/* 814 */     int nb_votes = votes.size();
/* 815 */     for (int j = 0; j < nb_votes; j++) {
/* 816 */       ClassificationStat vote = votes.get(j);
/* 817 */       for (int i = 0; i < this.m_NbTarget; i++) {
/* 818 */         addVote(vote);
/*     */       }
/*     */     } 
/* 821 */     calcMean();
/*     */   }
/*     */   
/*     */   public void addVote(ClusStatistic vote) {
/* 825 */     ClassificationStat or = (ClassificationStat)vote;
/* 826 */     this.m_SumWeight += or.m_SumWeight;
/* 827 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 828 */       this.m_SumWeights[i] = this.m_SumWeights[i] + or.m_SumWeights[i];
/* 829 */       double[] my = this.m_ClassCounts[i];
/* 830 */       for (int j = 0; j < my.length; j++) {
/* 831 */         my[j] = my[j] + or.getProportion(i, j);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClassificationStat getClassificationStat() {
/* 837 */     return this;
/*     */   }
/*     */   
/*     */   public double[][] getProbabilityPrediction() {
/* 841 */     double[][] result = new double[this.m_NbTarget][];
/* 842 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 843 */       double total = 0.0D;
/* 844 */       for (int k = 0; k < (this.m_ClassCounts[i]).length; k++) {
/* 845 */         total += this.m_ClassCounts[i][k];
/*     */       }
/* 847 */       result[i] = new double[(this.m_ClassCounts[i]).length];
/* 848 */       for (int j = 0; j < (result[i]).length; j++) {
/* 849 */         result[i][j] = this.m_ClassCounts[i][j] / total;
/*     */       }
/*     */     } 
/* 852 */     return result;
/*     */   }
/*     */   
/*     */   public double getSquaredDistance(ClusStatistic other) {
/* 856 */     double[][] these = getProbabilityPrediction();
/* 857 */     ClassificationStat o = (ClassificationStat)other;
/* 858 */     double[][] others = o.getProbabilityPrediction();
/* 859 */     double result = 0.0D;
/* 860 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 861 */       double distance = 0.0D;
/* 862 */       for (int j = 0; j < (these[i]).length; j++) {
/* 863 */         distance += (these[i][j] - others[i][j]) * (these[i][j] - others[i][j]);
/*     */       }
/* 865 */       result += distance / (these[i]).length;
/*     */     } 
/* 867 */     return result / this.m_NbTarget;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\ClassificationStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */