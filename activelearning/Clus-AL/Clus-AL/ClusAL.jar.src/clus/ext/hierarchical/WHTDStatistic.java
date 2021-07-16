/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStatBinaryNomiss;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import jeans.util.array.MIntArray;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
/*     */ import org.apache.commons.math.distribution.HypergeometricDistribution;
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
/*     */ public class WHTDStatistic
/*     */   extends RegressionStatBinaryNomiss
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  50 */   protected static DistributionFactory m_Fac = DistributionFactory.newInstance();
/*     */   
/*     */   protected ClassHierarchy m_Hier;
/*     */   
/*     */   protected boolean[] m_DiscrMean;
/*     */   protected WHTDStatistic m_Global;
/*  56 */   protected double m_Threshold = -1.0D; protected WHTDStatistic m_Validation; protected WHTDStatistic m_Training; protected double m_SigLevel;
/*     */   protected int m_Compatibility;
/*     */   
/*     */   public WHTDStatistic(ClassHierarchy hier, int comp) {
/*  60 */     this(hier, false, comp);
/*     */   }
/*     */   
/*     */   public WHTDStatistic(ClassHierarchy hier, boolean onlymean, int comp) {
/*  64 */     super(hier.getDummyAttrs(), onlymean);
/*  65 */     this.m_Compatibility = comp;
/*  66 */     this.m_Hier = hier;
/*     */   }
/*     */   
/*     */   public int getCompatibility() {
/*  70 */     return this.m_Compatibility;
/*     */   }
/*     */   
/*     */   public void setTrainingStat(ClusStatistic train) {
/*  74 */     this.m_Training = (WHTDStatistic)train;
/*     */   }
/*     */   
/*     */   public void setValidationStat(WHTDStatistic valid) {
/*  78 */     this.m_Validation = valid;
/*     */   }
/*     */   
/*     */   public void setGlobalStat(WHTDStatistic global) {
/*  82 */     this.m_Global = global;
/*     */   }
/*     */   
/*     */   public void setSigLevel(double sig) {
/*  86 */     this.m_SigLevel = sig;
/*     */   }
/*     */   
/*     */   public void setThreshold(double threshold) {
/*  90 */     this.m_Threshold = threshold;
/*     */   }
/*     */   
/*     */   public double getThreshold() {
/*  94 */     return this.m_Threshold;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  98 */     return (ClusStatistic)new WHTDStatistic(this.m_Hier, false, this.m_Compatibility);
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/* 102 */     WHTDStatistic res = new WHTDStatistic(this.m_Hier, true, this.m_Compatibility);
/* 103 */     res.m_Threshold = this.m_Threshold;
/* 104 */     res.m_Training = this.m_Training;
/* 105 */     if (this.m_Validation != null) {
/* 106 */       res.m_Validation = (WHTDStatistic)this.m_Validation.cloneSimple();
/* 107 */       res.m_Global = this.m_Global;
/* 108 */       res.m_SigLevel = this.m_SigLevel;
/*     */     } 
/* 110 */     return (ClusStatistic)res;
/*     */   }
/*     */   
/*     */   public void copyAll(ClusStatistic other) {
/* 114 */     copy(other);
/* 115 */     WHTDStatistic my_other = (WHTDStatistic)other;
/* 116 */     this.m_Global = my_other.m_Global;
/* 117 */     this.m_Validation = my_other.m_Validation;
/* 118 */     this.m_SigLevel = my_other.m_SigLevel;
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/* 122 */     WHTDStatistic or = (WHTDStatistic)other;
/* 123 */     super.addPrediction(other, weight);
/* 124 */     if (this.m_Validation != null) {
/* 125 */       this.m_Validation.addPrediction((ClusStatistic)or.m_Validation, weight);
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/* 130 */     int sidx = this.m_Hier.getType().getArrayIndex();
/*     */     
/* 132 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/* 133 */     if (tp != null) {
/* 134 */       this.m_SumWeight += weight;
/*     */       
/* 136 */       for (int j = 0; j < tp.getNbClasses(); j++) {
/* 137 */         ClassesValue val = tp.getClass(j);
/* 138 */         int idx = val.getIndex();
/*     */         
/* 140 */         this.m_SumValues[idx] = this.m_SumValues[idx] + weight;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final ClassHierarchy getHier() {
/* 148 */     return this.m_Hier;
/*     */   }
/*     */   
/*     */   public final void setHier(ClassHierarchy hier) throws ClusException {
/* 152 */     if (this.m_Hier != null && this.m_Hier.getTotal() != hier.getTotal()) {
/* 153 */       throw new ClusException("Different number of classes in new hierarchy: " + hier.getTotal() + " <> " + this.m_Hier.getTotal());
/*     */     }
/* 155 */     this.m_Hier = hier;
/*     */   }
/*     */   
/*     */   public int getNbPredictedClasses() {
/* 159 */     int count = 0;
/* 160 */     for (int i = 0; i < this.m_DiscrMean.length; i++) {
/* 161 */       if (this.m_DiscrMean[i]) {
/* 162 */         count++;
/*     */       }
/*     */     } 
/* 165 */     return count;
/*     */   }
/*     */   
/*     */   public ClassesTuple computeMeanTuple() {
/* 169 */     return this.m_Hier.getTuple(this.m_DiscrMean);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassesTuple computePrintTuple() {
/* 175 */     ClassesTuple printTuple = this.m_Hier.getTuple(this.m_DiscrMean);
/* 176 */     ArrayList added = new ArrayList();
/* 177 */     boolean[] interms = new boolean[this.m_Hier.getTotal()];
/* 178 */     printTuple.addIntermediateElems(this.m_Hier, interms, added);
/* 179 */     return printTuple;
/*     */   }
/*     */   
/*     */   public void computePrediction() {
/* 183 */     ClassesTuple meantuple = this.m_Hier.getBestTupleMaj(this.m_Means, this.m_Threshold);
/* 184 */     this.m_DiscrMean = meantuple.getVectorBooleanNodeAndAncestors(this.m_Hier);
/* 185 */     performSignificanceTest();
/*     */   }
/*     */   
/*     */   public void calcMean(double[] means) {
/* 189 */     if (Settings.useMEstimate() && this.m_Training != null) {
/*     */       
/* 191 */       for (int i = 0; i < this.m_NbAttrs; i++) {
/* 192 */         means[i] = (this.m_SumValues[i] + this.m_Training.m_Means[i]) / (this.m_SumWeight + 1.0D);
/*     */       }
/*     */     } else {
/*     */       
/* 196 */       for (int i = 0; i < this.m_NbAttrs; i++) {
/* 197 */         means[i] = (this.m_SumWeight != 0.0D) ? (this.m_SumValues[i] / this.m_SumWeight) : 0.0D;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getMean(int i) {
/* 203 */     if (Settings.useMEstimate() && this.m_Training != null)
/*     */     {
/* 205 */       return (this.m_SumValues[i] + this.m_Training.m_Means[i]) / (this.m_SumWeight + 1.0D);
/*     */     }
/*     */     
/* 208 */     return (this.m_SumWeight != 0.0D) ? (this.m_SumValues[i] / this.m_SumWeight) : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public void calcMean() {
/* 213 */     super.calcMean();
/* 214 */     computePrediction();
/*     */   }
/*     */   
/*     */   public int round(double value) {
/* 218 */     if (getCompatibility() == 0) {
/* 219 */       return (int)value;
/*     */     }
/* 221 */     return (int)Math.round(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void performSignificanceTest() {
/* 226 */     if (this.m_Validation != null) {
/* 227 */       for (int i = 0; i < this.m_DiscrMean.length; i++) {
/* 228 */         if (this.m_DiscrMean[i]) {
/*     */           
/* 230 */           int pop_tot = round(this.m_Global.getTotalWeight());
/* 231 */           int pop_cls = round(this.m_Global.getTotalWeight() * this.m_Global.m_Means[i]);
/* 232 */           int rule_tot = round(this.m_Validation.getTotalWeight());
/* 233 */           int rule_cls = round(this.m_Validation.getTotalWeight() * this.m_Validation.m_Means[i]);
/* 234 */           int upper = Math.min(rule_tot, pop_cls);
/* 235 */           int nb_other = pop_tot - pop_cls;
/* 236 */           int min_this = rule_tot - nb_other;
/* 237 */           int lower = Math.max(rule_cls, min_this);
/* 238 */           if (rule_cls < min_this || lower > upper) {
/* 239 */             System.err.println("BUG?");
/* 240 */             System.out.println("rule = " + (this.m_Validation.getTotalWeight() * this.m_Validation.m_Means[i]));
/* 241 */             System.out.println("pop_tot = " + pop_tot + " pop_cls = " + pop_cls + " rule_tot = " + rule_tot + " rule_cls = " + rule_cls);
/*     */           } 
/* 243 */           HypergeometricDistribution dist = m_Fac.createHypergeometricDistribution(pop_tot, pop_cls, rule_tot);
/*     */           try {
/* 245 */             double stat = dist.cumulativeProbability(lower, upper);
/* 246 */             if (stat >= this.m_SigLevel) {
/* 247 */               this.m_DiscrMean[i] = false;
/*     */             }
/* 249 */           } catch (MathException me) {
/* 250 */             System.err.println("Math error: " + me.getMessage());
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void setMeanTuple(ClassesTuple tuple) {
/* 258 */     setMeanTuple(tuple.getVectorBoolean(this.m_Hier));
/*     */   }
/*     */   
/*     */   public void setMeanTuple(boolean[] cls) {
/* 262 */     this.m_DiscrMean = new boolean[cls.length];
/* 263 */     System.arraycopy(cls, 0, this.m_DiscrMean, 0, cls.length);
/* 264 */     Arrays.fill(this.m_Means, 0.0D);
/* 265 */     for (int i = 0; i < this.m_DiscrMean.length; i++) {
/* 266 */       if (this.m_DiscrMean[i]) {
/* 267 */         this.m_Means[i] = 1.0D;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean[] getDiscretePred() {
/* 273 */     return this.m_DiscrMean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSquaredDistance(DataTuple tuple, ClusAttributeWeights weights) {
/* 280 */     double sum = 0.0D;
/* 281 */     boolean[] actual = new boolean[this.m_Hier.getTotal()];
/* 282 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(this.m_Hier.getType().getArrayIndex());
/* 283 */     tp.fillBoolArrayNodeAndAncestors(actual);
/* 284 */     for (int i = 0; i < this.m_Hier.getTotal(); i++) {
/* 285 */       NumericAttrType type = getAttribute(i);
/* 286 */       double actual_zo = actual[i] ? 1.0D : 0.0D;
/* 287 */       double dist = actual_zo - this.m_Means[i];
/* 288 */       sum += dist * dist * weights.getWeight((ClusAttrType)type);
/*     */     } 
/* 290 */     return sum / getNbAttributes();
/*     */   }
/*     */   
/*     */   public double getAbsoluteDistance(DataTuple tuple, ClusAttributeWeights weights, ClusStatManager statmanager) {
/* 294 */     double sum = 0.0D;
/* 295 */     boolean[] actual = new boolean[this.m_Hier.getTotal()];
/* 296 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(this.m_Hier.getType().getArrayIndex());
/* 297 */     tp.fillBoolArrayNodeAndAncestors(actual);
/* 298 */     for (int i = 0; i < this.m_Hier.getTotal(); i++) {
/* 299 */       NumericAttrType type = getAttribute(i);
/* 300 */       double actual_zo = actual[i] ? 1.0D : 0.0D;
/* 301 */       double dist = actual_zo - this.m_Means[i];
/* 302 */       WHTDStatistic tstat = (WHTDStatistic)statmanager.getTrainSetStat(2);
/* 303 */       if (tstat.getVariance(i) != 0.0D) {
/* 304 */         dist /= Math.pow(tstat.getVariance(i), 0.5D);
/*     */       }
/* 306 */       sum += Math.abs(dist) * weights.getWeight((ClusAttrType)type);
/*     */     } 
/* 308 */     return sum / getNbAttributes();
/*     */   }
/*     */   
/*     */   public void printTree() {
/* 312 */     this.m_Hier.print(ClusFormat.OUT_WRITER, this.m_SumValues);
/* 313 */     ClusFormat.OUT_WRITER.flush();
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
/*     */   public String getString(StatisticPrintInfo info) {
/* 326 */     String pred = null;
/* 327 */     if (this.m_Threshold >= 0.0D) {
/* 328 */       pred = computePrintTuple().toStringHuman(getHier());
/* 329 */       return pred + " [" + ClusFormat.TWO_AFTER_DOT.format(getTotalWeight()) + "]";
/*     */     } 
/* 331 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 332 */     StringBuffer buf = new StringBuffer();
/* 333 */     buf.append("[");
/* 334 */     for (int i = 0; i < getHier().getTotal(); i++) {
/* 335 */       if (i != 0) {
/* 336 */         buf.append(",");
/*     */       }
/* 338 */       if (this.m_SumWeight == 0.0D) {
/* 339 */         buf.append("?");
/*     */       } else {
/* 341 */         buf.append(fr.format(getMean(i)));
/*     */       } 
/*     */     } 
/* 344 */     buf.append("]");
/* 345 */     if (info.SHOW_EXAMPLE_COUNT) {
/* 346 */       buf.append(": ");
/* 347 */       buf.append(fr.format(this.m_SumWeight));
/*     */     } 
/* 349 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPredictString() {
/* 354 */     return "[" + computeMeanTuple().toStringHuman(getHier()) + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showRootInfo() {
/*     */     try {
/* 362 */       PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream("hierarchy.txt")));
/* 363 */       wrt.println("Hier #nodes: " + this.m_Hier.getTotal());
/* 364 */       wrt.println("Hier classes by level: " + MIntArray.toString(this.m_Hier.getClassesByLevel()));
/* 365 */       this.m_Hier.print(wrt, this.m_SumValues, null);
/* 366 */       wrt.close();
/* 367 */     } catch (IOException e) {
/* 368 */       System.out.println("IO Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void printDistributionRec(PrintWriter out, ClassTerm node) {
/* 373 */     int idx = node.getIndex();
/* 374 */     ClassesValue val = new ClassesValue(node);
/* 375 */     out.println(val.toPathString() + ", " + this.m_Means[idx]);
/* 376 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 377 */       printDistributionRec(out, (ClassTerm)node.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public void printDistribution(PrintWriter wrt) throws IOException {
/* 382 */     wrt.println("Total: " + this.m_SumWeight);
/* 383 */     ClassTerm root = this.m_Hier.getRoot();
/* 384 */     for (int i = 0; i < root.getNbChildren(); i++) {
/* 385 */       printDistributionRec(wrt, (ClassTerm)root.getChild(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public void getExtraInfoRec(ClassTerm node, double[] discrmean, StringBuffer out) {
/* 390 */     if (this.m_Validation != null) {
/* 391 */       int j = node.getIndex();
/* 392 */       if (discrmean[j] > 0.5D) {
/*     */         
/* 394 */         int pop_tot = round(this.m_Global.getTotalWeight());
/* 395 */         int pop_cls = round(this.m_Global.getTotalWeight() * this.m_Global.m_Means[j]);
/* 396 */         int rule_tot = round(this.m_Validation.getTotalWeight());
/* 397 */         int rule_cls = round(this.m_Validation.getTotalWeight() * this.m_Validation.m_Means[j]);
/* 398 */         int upper = Math.min(rule_tot, pop_cls);
/* 399 */         int nb_other = pop_tot - pop_cls;
/* 400 */         int min_this = rule_tot - nb_other;
/* 401 */         int lower = Math.max(rule_cls, min_this);
/* 402 */         HypergeometricDistribution dist = m_Fac.createHypergeometricDistribution(pop_tot, pop_cls, rule_tot);
/*     */         try {
/* 404 */           double stat = dist.cumulativeProbability(lower, upper);
/* 405 */           out.append(node.toStringHuman(getHier()) + ":");
/* 406 */           out.append(" pop_tot = " + String.valueOf(pop_tot));
/* 407 */           out.append(" pop_cls = " + String.valueOf(pop_cls));
/* 408 */           out.append(" rule_tot = " + String.valueOf(rule_tot));
/* 409 */           out.append(" rule_cls = " + String.valueOf(rule_cls));
/* 410 */           out.append(" upper = " + String.valueOf(upper));
/* 411 */           out.append(" prob = " + ClusFormat.SIX_AFTER_DOT.format(stat));
/*     */           
/* 413 */           out.append("\n");
/* 414 */         } catch (MathException me) {
/* 415 */           System.err.println("Math error: " + me.getMessage());
/*     */         } 
/*     */       } 
/*     */     } 
/* 419 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 420 */       getExtraInfoRec((ClassTerm)node.getChild(i), discrmean, out);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getExtraInfo() {
/* 425 */     StringBuffer res = new StringBuffer();
/* 426 */     ClassesTuple meantuple = this.m_Hier.getBestTupleMaj(this.m_Means, 50.0D);
/* 427 */     double[] discrmean = meantuple.getVectorNodeAndAncestors(this.m_Hier);
/* 428 */     for (int i = 0; i < this.m_Hier.getRoot().getNbChildren(); i++) {
/* 429 */       getExtraInfoRec((ClassTerm)this.m_Hier.getRoot().getChild(i), discrmean, res);
/*     */     }
/* 431 */     return res.toString();
/*     */   }
/*     */   
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {
/* 435 */     ClassHierarchy hier = getHier();
/* 436 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 437 */       ClusAttrType type = this.m_Attrs[i].cloneType();
/* 438 */       ClassTerm term = hier.getTermAt(i);
/* 439 */       type.setName(prefix + "-p-" + term.toStringHuman(hier));
/* 440 */       schema.addAttrType(type);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void unionInit() {
/* 445 */     this.m_DiscrMean = new boolean[this.m_Means.length];
/*     */   }
/*     */   
/*     */   public void union(ClusStatistic other) {
/* 449 */     boolean[] discr_mean = ((WHTDStatistic)other).m_DiscrMean;
/* 450 */     for (int i = 0; i < this.m_DiscrMean.length; i++) {
/* 451 */       if (discr_mean[i]) {
/* 452 */         this.m_DiscrMean[i] = true;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void unionDone() {}
/*     */   
/*     */   public void vote(ArrayList<WHTDStatistic> votes) {
/* 461 */     reset();
/* 462 */     this.m_Means = new double[this.m_NbAttrs];
/*     */     
/* 464 */     int nb_votes = votes.size();
/* 465 */     for (int j = 0; j < nb_votes; j++) {
/* 466 */       WHTDStatistic vote = votes.get(j);
/* 467 */       for (int i = 0; i < this.m_NbAttrs; i++) {
/* 468 */         this.m_Means[i] = this.m_Means[i] + vote.m_Means[i] / nb_votes;
/*     */       }
/*     */     } 
/* 471 */     computePrediction();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDispersion(ClusAttributeWeights scale, RowData data) {
/* 478 */     return getSVarS(scale);
/*     */   }
/*     */   
/*     */   public String getDistanceName() {
/* 482 */     return "Hierarchical Weighted Euclidean Distance";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\WHTDStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */