/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.util.ClusFormat;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RegressionStat
/*     */   extends RegressionStatBase
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public double[] m_SumValues;
/*     */   public double[] m_SumWeights;
/*     */   public double[] m_SumSqValues;
/*     */   public RegressionStat m_Training;
/*     */   
/*     */   public RegressionStat(NumericAttrType[] attrs) {
/*  55 */     this(attrs, false);
/*     */   }
/*     */   
/*     */   public RegressionStat(NumericAttrType[] attrs, boolean onlymean) {
/*  59 */     super(attrs, onlymean);
/*  60 */     if (!onlymean) {
/*  61 */       this.m_SumValues = new double[this.m_NbAttrs];
/*  62 */       this.m_SumWeights = new double[this.m_NbAttrs];
/*  63 */       this.m_SumSqValues = new double[this.m_NbAttrs];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setTrainingStat(ClusStatistic train) {
/*  68 */     this.m_Training = (RegressionStat)train;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  72 */     RegressionStat res = new RegressionStat(this.m_Attrs, false);
/*  73 */     res.m_Training = this.m_Training;
/*  74 */     return res;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/*  78 */     RegressionStat res = new RegressionStat(this.m_Attrs, true);
/*  79 */     res.m_Training = this.m_Training;
/*  80 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusStatistic copyNormalizedWeighted(double weight) {
/*  89 */     RegressionStat newStat = normalizedCopy();
/*  90 */     for (int iTarget = 0; iTarget < newStat.getNbAttributes(); iTarget++) {
/*  91 */       newStat.m_Means[iTarget] = weight * newStat.m_Means[iTarget];
/*     */     }
/*  93 */     return newStat;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  97 */     this.m_SumWeight = 0.0D;
/*  98 */     this.m_NbExamples = 0;
/*  99 */     Arrays.fill(this.m_SumWeights, 0.0D);
/* 100 */     Arrays.fill(this.m_SumValues, 0.0D);
/* 101 */     Arrays.fill(this.m_SumSqValues, 0.0D);
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/* 105 */     RegressionStat or = (RegressionStat)other;
/* 106 */     this.m_SumWeight = or.m_SumWeight;
/* 107 */     this.m_NbExamples = or.m_NbExamples;
/* 108 */     System.arraycopy(or.m_SumWeights, 0, this.m_SumWeights, 0, this.m_NbAttrs);
/* 109 */     System.arraycopy(or.m_SumValues, 0, this.m_SumValues, 0, this.m_NbAttrs);
/* 110 */     System.arraycopy(or.m_SumSqValues, 0, this.m_SumSqValues, 0, this.m_NbAttrs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RegressionStat normalizedCopy() {
/* 117 */     RegressionStat copy = (RegressionStat)cloneSimple();
/* 118 */     copy.m_NbExamples = 0;
/* 119 */     copy.m_SumWeight = 1.0D;
/* 120 */     calcMean(copy.m_Means);
/* 121 */     return copy;
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/* 125 */     RegressionStat or = (RegressionStat)other;
/* 126 */     this.m_SumWeight += or.m_SumWeight;
/* 127 */     this.m_NbExamples += or.m_NbExamples;
/* 128 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 129 */       this.m_SumWeights[i] = this.m_SumWeights[i] + or.m_SumWeights[i];
/* 130 */       this.m_SumValues[i] = this.m_SumValues[i] + or.m_SumValues[i];
/* 131 */       this.m_SumSqValues[i] = this.m_SumSqValues[i] + or.m_SumSqValues[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {
/* 136 */     RegressionStat or = (RegressionStat)other;
/* 137 */     this.m_SumWeight += scale * or.m_SumWeight;
/* 138 */     this.m_NbExamples += or.m_NbExamples;
/* 139 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 140 */       this.m_SumWeights[i] = this.m_SumWeights[i] + scale * or.m_SumWeights[i];
/* 141 */       this.m_SumValues[i] = this.m_SumValues[i] + scale * or.m_SumValues[i];
/* 142 */       this.m_SumSqValues[i] = this.m_SumSqValues[i] + scale * or.m_SumSqValues[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 147 */     RegressionStat or = (RegressionStat)other;
/*     */     
/* 149 */     this.m_SumWeight -= or.m_SumWeight;
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
/* 160 */     this.m_NbExamples -= or.m_NbExamples;
/*     */     
/* 162 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*     */ 
/*     */ 
/*     */       
/* 166 */       this.m_SumWeights[i] = this.m_SumWeights[i] - or.m_SumWeights[i];
/* 167 */       this.m_SumValues[i] = this.m_SumValues[i] - or.m_SumValues[i];
/* 168 */       this.m_SumSqValues[i] = this.m_SumSqValues[i] - or.m_SumSqValues[i];
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 187 */     RegressionStat or = (RegressionStat)other;
/* 188 */     or.m_SumWeight -= this.m_SumWeight;
/* 189 */     or.m_NbExamples -= this.m_NbExamples;
/* 190 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 191 */       this.m_SumWeights[i] = or.m_SumWeights[i] - this.m_SumWeights[i];
/* 192 */       this.m_SumValues[i] = or.m_SumValues[i] - this.m_SumValues[i];
/* 193 */       this.m_SumSqValues[i] = or.m_SumSqValues[i] - this.m_SumSqValues[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/* 198 */     this.m_NbExamples++;
/* 199 */     this.m_SumWeight += weight;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 206 */       double val = this.m_Attrs[i].getNumeric(tuple);
/* 207 */       if (val != Double.POSITIVE_INFINITY) {
/*     */         
/* 209 */         this.m_SumWeights[i] = this.m_SumWeights[i] + weight;
/* 210 */         this.m_SumValues[i] = this.m_SumValues[i] + weight * val;
/* 211 */         this.m_SumSqValues[i] = this.m_SumSqValues[i] + weight * val * val;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void calcMean(double[] means) {
/* 218 */     for (int i = 0; i < this.m_NbAttrs; i++)
/*     */     {
/* 220 */       means[i] = (this.m_SumWeights[i] != 0.0D) ? (this.m_SumValues[i] / this.m_SumWeights[i]) : 0.0D;
/*     */     }
/*     */   }
/*     */   
/*     */   public double getMean(int i) {
/* 225 */     return (this.m_SumWeights[i] != 0.0D) ? (this.m_SumValues[i] / this.m_SumWeights[i]) : 0.0D;
/*     */   }
/*     */   
/*     */   public double getSumValues(int i) {
/* 229 */     return this.m_SumValues[i];
/*     */   }
/*     */   
/*     */   public double getSumWeights(int i) {
/* 233 */     return this.m_SumWeights[i];
/*     */   }
/*     */   
/*     */   public double getSVarS(int i) {
/* 237 */     double n_tot = this.m_SumWeight;
/* 238 */     double k_tot = this.m_SumWeights[i];
/* 239 */     double sv_tot = this.m_SumValues[i];
/* 240 */     double ss_tot = this.m_SumSqValues[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 246 */     if (k_tot <= 1.0E-9D && this.m_Training != null)
/*     */     {
/* 248 */       return this.m_Training.getSVarS(i);
/*     */     }
/*     */ 
/*     */     
/* 252 */     return (k_tot > 1.0D) ? (ss_tot * (n_tot - 1.0D) / (k_tot - 1.0D) - n_tot * sv_tot / k_tot * sv_tot / k_tot) : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSVarS2(ClusAttributeWeights scale) {
/* 257 */     double result = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 264 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 265 */       double n_tot = this.m_SumWeight;
/* 266 */       double k_tot = this.m_SumWeights[i];
/* 267 */       double sv_tot = this.m_SumValues[i];
/* 268 */       double ss_tot = this.m_SumSqValues[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 275 */       if (k_tot == n_tot) {
/* 276 */         result += (ss_tot - sv_tot * sv_tot / n_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 281 */       else if (k_tot <= 1.0E-9D && this.m_Training != null) {
/* 282 */         result += this.m_Training.getSVarS(i) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       } else {
/*     */         
/* 285 */         result += (ss_tot * (n_tot - 1.0D) / (k_tot - 1.0D) - n_tot * sv_tot / k_tot * sv_tot / k_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 292 */     return result / this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/* 296 */     double result = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 303 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 304 */       double n_tot = this.m_SumWeight;
/* 305 */       double k_tot = this.m_SumWeights[i];
/* 306 */       double sv_tot = this.m_SumValues[i];
/* 307 */       double ss_tot = this.m_SumSqValues[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 314 */       if (k_tot == n_tot) {
/* 315 */         result += (ss_tot - sv_tot * sv_tot / n_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 320 */       else if (k_tot <= 1.0E-9D && this.m_Training != null) {
/* 321 */         result += this.m_Training.getSVarS(i) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 327 */         result += (ss_tot * (n_tot - 1.0D) / (k_tot - 1.0D) - n_tot * sv_tot / k_tot * sv_tot / k_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     return result / this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public double getSVarSDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 339 */     double result = 0.0D;
/* 340 */     RegressionStat or = (RegressionStat)other;
/* 341 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 342 */       double n_tot = this.m_SumWeight - or.m_SumWeight;
/* 343 */       double k_tot = this.m_SumWeights[i] - or.m_SumWeights[i];
/* 344 */       double sv_tot = this.m_SumValues[i] - or.m_SumValues[i];
/* 345 */       double ss_tot = this.m_SumSqValues[i] - or.m_SumSqValues[i];
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
/* 361 */       if (k_tot == n_tot) {
/*     */         
/* 363 */         result += (ss_tot - sv_tot * sv_tot / n_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       
/*     */       }
/* 366 */       else if (k_tot <= 1.0E-9D && this.m_Training != null) {
/* 367 */         result += this.m_Training.getSVarS(i) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       } else {
/* 369 */         result += (ss_tot * (n_tot - 1.0D) / (k_tot - 1.0D) - n_tot * sv_tot / k_tot * sv_tot / k_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */       } 
/*     */     } 
/*     */     
/* 373 */     return result / this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 377 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 378 */     StringBuffer buf = new StringBuffer();
/* 379 */     buf.append("["); int i;
/* 380 */     for (i = 0; i < this.m_NbAttrs; i++) {
/* 381 */       if (i != 0) {
/* 382 */         buf.append(",");
/*     */       }
/* 384 */       double tot = getSumWeights(i);
/* 385 */       if (tot == 0.0D) {
/* 386 */         buf.append("?");
/*     */       } else {
/* 388 */         buf.append(fr.format(getSumValues(i) / tot));
/*     */       } 
/*     */     } 
/* 391 */     buf.append("]");
/* 392 */     if (info.SHOW_EXAMPLE_COUNT_BYTARGET) {
/* 393 */       buf.append(": [");
/* 394 */       for (i = 0; i < this.m_NbAttrs; i++) {
/* 395 */         if (i != 0) {
/* 396 */           buf.append(",");
/*     */         }
/* 398 */         buf.append(fr.format(this.m_SumWeights[i]));
/*     */       } 
/* 400 */       buf.append("]");
/* 401 */     } else if (info.SHOW_EXAMPLE_COUNT) {
/* 402 */       buf.append(": ");
/* 403 */       buf.append(fr.format(this.m_SumWeight));
/*     */     } 
/* 405 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void printDebug() {
/* 409 */     for (int i = 0; i < getNbAttributes(); i++) {
/* 410 */       double n_tot = this.m_SumWeight;
/* 411 */       double k_tot = this.m_SumWeights[i];
/* 412 */       double sv_tot = this.m_SumValues[i];
/* 413 */       double ss_tot = this.m_SumSqValues[i];
/* 414 */       System.out.println("n: " + n_tot + " k: " + k_tot);
/* 415 */       System.out.println("sv: " + sv_tot);
/* 416 */       System.out.println("ss: " + ss_tot);
/* 417 */       double mean = sv_tot / n_tot;
/* 418 */       double var = ss_tot - n_tot * mean * mean;
/* 419 */       System.out.println("mean: " + mean);
/* 420 */       System.out.println("var: " + var);
/*     */     } 
/* 422 */     System.out.println("err: " + getError());
/*     */   }
/*     */   
/*     */   public RegressionStat getRegressionStat() {
/* 426 */     return this;
/*     */   }
/*     */   
/*     */   public double getSquaredDistance(ClusStatistic other) {
/* 430 */     double result = 0.0D;
/* 431 */     RegressionStat o = (RegressionStat)other;
/* 432 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 433 */       double distance = getMean(i) - o.getMean(i);
/* 434 */       result += distance * distance;
/*     */     } 
/* 436 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\RegressionStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */