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
/*     */ import org.apache.commons.math.MathException;
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
/*     */ public class CombStat
/*     */   extends ClusStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  46 */   public static int IN_HEURISTIC = 0;
/*  47 */   public static int IN_OUTPUT = 1;
/*     */   
/*     */   protected RegressionStat m_RegStat;
/*     */   
/*     */   protected ClassificationStat m_ClassStat;
/*     */   
/*     */   private ClusStatManager m_StatManager;
/*     */ 
/*     */   
/*     */   public CombStat(ClusStatManager statManager, NumericAttrType[] num, NominalAttrType[] nom) {
/*  57 */     this.m_StatManager = statManager;
/*  58 */     this.m_RegStat = new RegressionStat(num);
/*  59 */     this.m_ClassStat = new ClassificationStat(nom);
/*     */   }
/*     */   
/*     */   public CombStat(ClusStatManager statManager, RegressionStat reg, ClassificationStat cls) {
/*  63 */     this.m_StatManager = statManager;
/*  64 */     this.m_RegStat = reg;
/*  65 */     this.m_ClassStat = cls;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  69 */     return new CombStat(this.m_StatManager, (RegressionStat)this.m_RegStat.cloneStat(), (ClassificationStat)this.m_ClassStat.cloneStat());
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/*  73 */     return new CombStat(this.m_StatManager, (RegressionStat)this.m_RegStat.cloneSimple(), (ClassificationStat)this.m_ClassStat.cloneSimple());
/*     */   }
/*     */   
/*     */   public RegressionStat getRegressionStat() {
/*  77 */     return this.m_RegStat;
/*     */   }
/*     */   
/*     */   public ClassificationStat getClassificationStat() {
/*  81 */     return this.m_ClassStat;
/*     */   }
/*     */   
/*     */   public void setTrainingStat(ClusStatistic train) {
/*  85 */     CombStat ctrain = (CombStat)train;
/*  86 */     this.m_RegStat.setTrainingStat(train.getRegressionStat());
/*  87 */     this.m_ClassStat.setTrainingStat(train.getClassificationStat());
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/*  91 */     this.m_RegStat.updateWeighted(tuple, weight);
/*  92 */     this.m_ClassStat.updateWeighted(tuple, weight);
/*  93 */     this.m_SumWeight += weight;
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/*  97 */     this.m_RegStat.updateWeighted(tuple, tuple.getWeight());
/*  98 */     this.m_ClassStat.updateWeighted(tuple, tuple.getWeight());
/*  99 */     this.m_SumWeight += tuple.getWeight();
/*     */   }
/*     */   
/*     */   public void calcMean() {
/* 103 */     this.m_RegStat.calcMean();
/* 104 */     this.m_ClassStat.calcMean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDispersion(ClusAttributeWeights scale, RowData data) {
/* 111 */     return dispersionCalc();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double dispersionCalc() {
/* 118 */     return dispersion(IN_OUTPUT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double dispersionAdtHeur() {
/* 127 */     double offset = getSettings().getHeurDispOffset();
/* 128 */     double disp = dispersion(IN_HEURISTIC) + offset;
/*     */     
/* 130 */     double train_sum_w = this.m_StatManager.getTrainSetStat().getTotalWeight();
/* 131 */     double cov_par = getSettings().getHeurCoveragePar();
/*     */     
/* 133 */     disp -= cov_par * this.m_SumWeight / train_sum_w / 2.0D;
/*     */ 
/*     */     
/* 136 */     if (getSettings().isHeurPrototypeDistPar()) {
/* 137 */       double proto_par = getSettings().getHeurPrototypeDistPar();
/* 138 */       double proto_val = prototypeDifference((CombStat)this.m_StatManager.getTrainSetStat());
/*     */       
/* 140 */       disp -= proto_par * proto_val;
/*     */     } 
/*     */     
/* 143 */     if (Settings.IS_RULE_SIG_TESTING) {
/*     */       
/* 145 */       int thresh = getSettings().getRuleNbSigAtt();
/* 146 */       if (thresh > 0) {
/* 147 */         int sign_diff = signDifferent();
/* 148 */         if (sign_diff < thresh) {
/* 149 */           disp += 1000.0D;
/*     */         }
/* 151 */       } else if (thresh < 0 && 
/* 152 */         !targetSignDifferent()) {
/* 153 */         disp += 1000.0D;
/*     */       } 
/*     */     } 
/*     */     
/* 157 */     return disp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double rDispersionAdtHeur() {
/* 165 */     double offset = getSettings().getHeurDispOffset();
/* 166 */     double disp = dispersion(IN_HEURISTIC) + offset;
/*     */     
/* 168 */     double def_disp = ((CombStat)this.m_StatManager.getTrainSetStat()).dispersion(IN_HEURISTIC);
/* 169 */     disp -= def_disp;
/*     */ 
/*     */     
/* 172 */     double train_sum_w = this.m_StatManager.getTrainSetStat().getTotalWeight();
/* 173 */     double cov_par = getSettings().getHeurCoveragePar();
/*     */     
/* 175 */     disp -= cov_par * this.m_SumWeight / train_sum_w / 2.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     if (getSettings().isHeurPrototypeDistPar()) {
/* 181 */       double proto_par = getSettings().getHeurPrototypeDistPar();
/* 182 */       double proto_val = prototypeDifference((CombStat)this.m_StatManager.getTrainSetStat());
/*     */       
/* 184 */       disp -= proto_par * proto_val;
/*     */     } 
/*     */     
/* 187 */     if (Settings.IS_RULE_SIG_TESTING) {
/*     */       
/* 189 */       int thresh = getSettings().getRuleNbSigAtt();
/* 190 */       if (thresh > 0) {
/* 191 */         int sign_diff = signDifferent();
/* 192 */         if (sign_diff < thresh) {
/* 193 */           disp += 1000.0D;
/*     */         }
/* 195 */       } else if (thresh < 0 && 
/* 196 */         !targetSignDifferent()) {
/* 197 */         disp += 1000.0D;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 202 */     return disp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double dispersionMltHeur() {
/* 211 */     double offset = getSettings().getHeurDispOffset();
/* 212 */     double disp = dispersion(IN_HEURISTIC) + offset;
/* 213 */     double dis1 = disp;
/*     */     
/* 215 */     double train_sum_w = this.m_StatManager.getTrainSetStat().getTotalWeight();
/* 216 */     double cov_par = getSettings().getHeurCoveragePar();
/*     */ 
/*     */     
/* 219 */     disp *= Math.pow(this.m_SumWeight / train_sum_w, cov_par);
/* 220 */     double dis2 = disp;
/*     */ 
/*     */     
/* 223 */     if (getSettings().isHeurPrototypeDistPar()) {
/* 224 */       double proto_par = getSettings().getHeurPrototypeDistPar();
/* 225 */       double proto_val = prototypeDifference((CombStat)this.m_StatManager.getTrainSetStat());
/*     */       
/* 227 */       disp = (proto_val > 0.0D) ? (disp / Math.pow(proto_val, proto_par)) : 0.0D;
/*     */     } 
/*     */     
/* 230 */     if (Settings.IS_RULE_SIG_TESTING) {
/*     */       
/* 232 */       int thresh = getSettings().getRuleNbSigAtt();
/* 233 */       if (thresh > 0) {
/* 234 */         int sign_diff = signDifferent();
/* 235 */         if (sign_diff < thresh) {
/* 236 */           disp *= 1000.0D;
/*     */         }
/* 238 */       } else if (thresh < 0 && 
/* 239 */         !targetSignDifferent()) {
/* 240 */         disp *= 1000.0D;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 245 */     return disp;
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
/*     */   public double rDispersionMltHeur() {
/* 259 */     double offset = getSettings().getHeurDispOffset();
/* 260 */     double disp = dispersion(IN_HEURISTIC) + offset;
/* 261 */     double dis1 = disp;
/* 262 */     double def_disp = ((CombStat)this.m_StatManager.getTrainSetStat()).dispersion(IN_HEURISTIC);
/* 263 */     disp -= def_disp;
/* 264 */     double dis2 = disp;
/*     */     
/* 266 */     double train_sum_w = this.m_StatManager.getTrainSetStat().getTotalWeight();
/* 267 */     double cov_par = getSettings().getHeurCoveragePar();
/*     */ 
/*     */ 
/*     */     
/* 271 */     disp *= Math.pow(this.m_SumWeight / train_sum_w, cov_par);
/* 272 */     double dis3 = disp;
/*     */ 
/*     */     
/* 275 */     if (getSettings().isHeurPrototypeDistPar()) {
/* 276 */       double proto_par = getSettings().getHeurPrototypeDistPar();
/* 277 */       double proto_val = prototypeDifference((CombStat)this.m_StatManager.getTrainSetStat());
/*     */       
/* 279 */       disp = (proto_val > 0.0D) ? (disp / Math.pow(proto_val, proto_par)) : 0.0D;
/*     */     } 
/*     */     
/* 282 */     if (Settings.IS_RULE_SIG_TESTING) {
/*     */       
/* 284 */       int thresh = getSettings().getRuleNbSigAtt();
/* 285 */       if (thresh > 0) {
/* 286 */         int sign_diff = signDifferent();
/* 287 */         if (sign_diff < thresh) {
/* 288 */           disp *= 1000.0D;
/*     */         }
/* 290 */       } else if (thresh < 0 && 
/* 291 */         !targetSignDifferent()) {
/* 292 */         disp *= 1000.0D;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 297 */     return disp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double dispersion(int use) {
/* 305 */     return dispersionNom(use) + meanVariance(use);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double dispersionNum(int use) {
/* 315 */     return meanVariance(use);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double dispersionNom(int use) {
/* 325 */     return meanDistNom(use);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double meanVariance(int use) {
/* 334 */     double sumvar = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 339 */     double norm = getSettings().getVarBasedDispNormWeight();
/* 340 */     for (int i = 0; i < this.m_RegStat.getNbNumericAttributes(); i++) {
/* 341 */       double weight; if (use == IN_HEURISTIC) {
/* 342 */         weight = this.m_StatManager.getClusteringWeights().getWeight((ClusAttrType)this.m_RegStat.getAttribute(i));
/*     */       } else {
/* 344 */         weight = this.m_StatManager.getDispersionWeights().getWeight((ClusAttrType)this.m_RegStat.getAttribute(i));
/*     */       } 
/* 346 */       sumvar += this.m_RegStat.getVariance(i) * weight / norm * norm;
/*     */     } 
/* 348 */     return sumvar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double meanDistNom(int use) {
/* 357 */     double sumdist = 0.0D;
/* 358 */     double weight = 0.0D;
/* 359 */     for (int i = 0; i < this.m_ClassStat.getNbNominalAttributes(); i++) {
/* 360 */       if (use == IN_HEURISTIC) {
/* 361 */         weight = this.m_StatManager.getClusteringWeights().getWeight((ClusAttrType)this.m_ClassStat.getAttribute(i));
/*     */       } else {
/* 363 */         weight = this.m_StatManager.getDispersionWeights().getWeight((ClusAttrType)this.m_ClassStat.getAttribute(i));
/*     */       } 
/* 365 */       sumdist += meanDistNomOne(i) * weight;
/*     */     } 
/* 367 */     return sumdist;
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
/*     */   public double meanDistNomOne(int attr_idx) {
/* 379 */     double[] counts = this.m_ClassStat.m_ClassCounts[attr_idx];
/* 380 */     double[] prototype = new double[counts.length];
/* 381 */     double sum = 0.0D;
/* 382 */     double dist = 0.0D;
/* 383 */     int nbval = counts.length;
/*     */     int i;
/* 385 */     for (i = 0; i < nbval; i++) {
/* 386 */       sum += counts[i];
/*     */     }
/* 388 */     for (i = 0; i < nbval; i++) {
/* 389 */       prototype[i] = (sum != 0.0D) ? (counts[i] / sum) : 0.0D;
/*     */     }
/*     */     
/* 392 */     for (i = 0; i < nbval; i++) {
/* 393 */       dist += (1.0D - prototype[i]) * counts[i];
/*     */     }
/*     */     
/* 396 */     dist = dist * nbval / (nbval - 1);
/* 397 */     dist = (dist != 0.0D) ? (dist / sum) : 0.0D;
/* 398 */     return dist;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double meanEntropy() {
/* 407 */     double sent = 0.0D;
/* 408 */     int nbNominal = this.m_ClassStat.getNbNominalAttributes();
/* 409 */     for (int i = 0; i < nbNominal; i++) {
/* 410 */       sent += entropy(i);
/*     */     }
/* 412 */     return sent / nbNominal;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double entropy(int attr) {
/* 423 */     return this.m_ClassStat.entropy(attr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double prototypeDifference(CombStat stat) {
/* 431 */     double sumdiff = 0.0D;
/*     */     
/*     */     int i;
/* 434 */     for (i = 0; i < this.m_RegStat.getNbNumericAttributes(); i++) {
/*     */       
/* 436 */       double weight = this.m_StatManager.getClusteringWeights().getWeight((ClusAttrType)this.m_RegStat.getAttribute(i));
/* 437 */       sumdiff += Math.abs(prototypeNum(i) - stat.prototypeNum(i)) * weight;
/*     */     } 
/*     */ 
/*     */     
/* 441 */     for (i = 0; i < this.m_ClassStat.getNbNominalAttributes(); i++) {
/*     */       
/* 443 */       double weight = this.m_StatManager.getClusteringWeights().getWeight((ClusAttrType)this.m_ClassStat.getAttribute(i));
/* 444 */       double sum = 0.0D;
/* 445 */       double[] proto1 = prototypeNom(i);
/* 446 */       double[] proto2 = stat.prototypeNom(i);
/* 447 */       for (int j = 0; j < proto1.length; j++) {
/* 448 */         sum += Math.abs(proto1[j] - proto2[j]);
/*     */       }
/* 450 */       sumdiff += sum * weight;
/*     */     } 
/*     */ 
/*     */     
/* 454 */     return (sumdiff != 0.0D) ? sumdiff : 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] prototypeNom(int attr_idx) {
/* 465 */     double[] counts = this.m_ClassStat.m_ClassCounts[attr_idx];
/* 466 */     double[] prototype = new double[counts.length];
/* 467 */     double sum = 0.0D;
/* 468 */     int nbval = counts.length; int i;
/* 469 */     for (i = 0; i < nbval; i++) {
/* 470 */       sum += counts[i];
/*     */     }
/* 472 */     for (i = 0; i < nbval; i++) {
/* 473 */       prototype[i] = (sum != 0.0D) ? (counts[i] / sum) : 0.0D;
/*     */     }
/* 475 */     return prototype;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double prototypeNum(int attr_idx) {
/* 485 */     return this.m_RegStat.getMean(attr_idx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int signDifferent() {
/* 492 */     int sign_diff = 0;
/*     */     int i;
/* 494 */     for (i = 0; i < this.m_ClassStat.getNbAttributes(); i++) {
/* 495 */       if (SignDifferentNom(i)) {
/* 496 */         sign_diff++;
/*     */       }
/*     */     } 
/*     */     
/* 500 */     for (i = 0; i < this.m_RegStat.getNbAttributes(); i++) {
/*     */       try {
/* 502 */         if (SignDifferentNum(i)) {
/* 503 */           sign_diff++;
/*     */         }
/* 505 */       } catch (IllegalArgumentException e) {
/* 506 */         e.printStackTrace();
/* 507 */       } catch (MathException e) {
/* 508 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 511 */     System.out.println("Nb.sig.atts: " + sign_diff);
/* 512 */     return sign_diff;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean targetSignDifferent() {
/* 520 */     boolean res = false;
/* 521 */     int att = -1;
/*     */ 
/*     */     
/* 524 */     ClusStatistic targetStat = this.m_StatManager.getStatistic(3);
/* 525 */     if (targetStat instanceof ClassificationStat) {
/* 526 */       for (int i = 0; i < targetStat.getNbNominalAttributes(); i++) {
/* 527 */         String att_name = ((ClassificationStat)targetStat).getAttribute(i).getName();
/* 528 */         for (int j = 0; j < this.m_ClassStat.getNbNominalAttributes(); j++) {
/* 529 */           String att_name2 = this.m_ClassStat.getAttribute(j).getName();
/* 530 */           if (att_name.equals(att_name2)) {
/* 531 */             att = j;
/*     */             break;
/*     */           } 
/*     */         } 
/* 535 */         if (SignDifferentNom(att)) {
/* 536 */           res = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 541 */       return res;
/* 542 */     }  if (targetStat instanceof RegressionStat) {
/* 543 */       for (int i = 0; i < targetStat.getNbNumericAttributes(); i++) {
/* 544 */         String att_name = ((RegressionStat)targetStat).getAttribute(i).getName();
/* 545 */         for (int j = 0; j < this.m_RegStat.getNbNumericAttributes(); j++) {
/* 546 */           String att_name2 = this.m_RegStat.getAttribute(j).getName();
/* 547 */           if (att_name.equals(att_name2)) {
/* 548 */             att = j;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */         try {
/* 553 */           if (SignDifferentNum(att)) {
/* 554 */             res = true;
/*     */             break;
/*     */           } 
/* 557 */         } catch (IllegalArgumentException e) {
/* 558 */           e.printStackTrace();
/* 559 */         } catch (MathException e) {
/* 560 */           e.printStackTrace();
/*     */         } 
/*     */       } 
/* 563 */       return res;
/*     */     } 
/*     */     
/* 566 */     return true;
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
/*     */   private boolean SignDifferentNom(int att) {
/* 596 */     return this.m_ClassStat.getGTest(att, this.m_StatManager);
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
/*     */   private boolean SignDifferentNum(int att) throws IllegalArgumentException, MathException {
/* 608 */     double alpha = getSettings().getRuleSignificanceLevel();
/* 609 */     double p_value = this.m_RegStat.getTTestPValue(att, this.m_StatManager);
/* 610 */     return (p_value < alpha);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDispersionString() {
/* 618 */     StringBuffer buf = new StringBuffer();
/* 619 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 620 */     buf.append("[");
/* 621 */     buf.append(fr.format(dispersionCalc()));
/* 622 */     buf.append(" : ");
/* 623 */     buf.append(fr.format(dispersionNum(IN_OUTPUT)));
/* 624 */     buf.append(" , ");
/* 625 */     buf.append(fr.format(dispersionNom(IN_OUTPUT)));
/* 626 */     buf.append("]");
/* 627 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 631 */     StringBuffer buf = new StringBuffer();
/* 632 */     buf.append("[");
/* 633 */     buf.append(this.m_ClassStat.getString(info));
/* 634 */     buf.append(" | ");
/* 635 */     buf.append(this.m_RegStat.getString(info));
/* 636 */     buf.append("]");
/* 637 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {
/* 641 */     this.m_ClassStat.addPredictWriterSchema(prefix, schema);
/* 642 */     this.m_RegStat.addPredictWriterSchema(prefix, schema);
/*     */   }
/*     */   
/*     */   public String getPredictWriterString() {
/* 646 */     StringBuffer buf = new StringBuffer();
/* 647 */     buf.append(this.m_ClassStat.getPredictWriterString());
/* 648 */     if (buf.length() != 0) buf.append(","); 
/* 649 */     buf.append(this.m_RegStat.getPredictWriterString());
/* 650 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String getArrayOfStatistic() {
/* 654 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/* 659 */     int nbTargetNom = this.m_ClassStat.getNbNominalAttributes();
/* 660 */     int nbTargetNum = this.m_RegStat.getNbNumericAttributes();
/* 661 */     return (this.m_ClassStat.getSVarS(scale) * nbTargetNom + this.m_RegStat.getSVarS(scale) * nbTargetNum) / (nbTargetNom + nbTargetNum);
/*     */   }
/*     */   
/*     */   public double getSVarSDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 665 */     int nbTargetNom = this.m_ClassStat.getNbNominalAttributes();
/* 666 */     int nbTargetNum = this.m_RegStat.getNbNumericAttributes();
/* 667 */     ClassificationStat ocls = ((CombStat)other).getClassificationStat();
/* 668 */     RegressionStat oreg = ((CombStat)other).getRegressionStat();
/* 669 */     return (this.m_ClassStat.getSVarSDiff(scale, ocls) * nbTargetNom + this.m_RegStat.getSVarSDiff(scale, oreg) * nbTargetNum) / (nbTargetNom + nbTargetNum);
/*     */   }
/*     */   
/*     */   public void reset() {
/* 673 */     this.m_RegStat.reset();
/* 674 */     this.m_ClassStat.reset();
/* 675 */     this.m_SumWeight = 0.0D;
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/* 679 */     CombStat or = (CombStat)other;
/* 680 */     this.m_SumWeight = or.m_SumWeight;
/* 681 */     this.m_StatManager = or.m_StatManager;
/* 682 */     this.m_RegStat.copy(or.m_RegStat);
/* 683 */     this.m_ClassStat.copy(or.m_ClassStat);
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/* 687 */     CombStat or = (CombStat)other;
/* 688 */     this.m_RegStat.addPrediction(or.m_RegStat, weight);
/* 689 */     this.m_ClassStat.addPrediction(or.m_ClassStat, weight);
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/* 693 */     CombStat or = (CombStat)other;
/* 694 */     this.m_RegStat.add(or.m_RegStat);
/* 695 */     this.m_ClassStat.add(or.m_ClassStat);
/* 696 */     this.m_SumWeight += or.m_SumWeight;
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 700 */     CombStat or = (CombStat)other;
/* 701 */     this.m_RegStat.subtractFromThis(or.m_RegStat);
/* 702 */     this.m_ClassStat.subtractFromThis(or.m_ClassStat);
/* 703 */     this.m_SumWeight -= or.m_SumWeight;
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 707 */     CombStat or = (CombStat)other;
/* 708 */     this.m_RegStat.subtractFromOther(or.m_RegStat);
/* 709 */     this.m_ClassStat.subtractFromOther(or.m_ClassStat);
/* 710 */     or.m_SumWeight -= this.m_SumWeight;
/*     */   }
/*     */   
/*     */   public int getNbNominalAttributes() {
/* 714 */     return this.m_ClassStat.getNbNominalAttributes();
/*     */   }
/*     */   
/*     */   public String getPredictedClassName(int idx) {
/* 718 */     return "";
/*     */   }
/*     */   
/*     */   public int getNbNumericAttributes() {
/* 722 */     return this.m_RegStat.getNbNumericAttributes();
/*     */   }
/*     */   
/*     */   public double[] getNumericPred() {
/* 726 */     return this.m_RegStat.getNumericPred();
/*     */   }
/*     */   
/*     */   public int[] getNominalPred() {
/* 730 */     return this.m_ClassStat.getNominalPred();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/* 734 */     return this.m_StatManager.getSettings();
/*     */   }
/*     */ 
/*     */   
/*     */   public double getError(ClusAttributeWeights scale) {
/* 739 */     System.out.println("CombStat :getError");
/* 740 */     switch (ClusStatManager.getMode()) {
/*     */       case 0:
/* 742 */         return this.m_ClassStat.getError(scale);
/*     */       case 1:
/* 744 */         return this.m_RegStat.getError(scale);
/*     */       case 4:
/* 746 */         return this.m_RegStat.getError(scale) + this.m_ClassStat.getError(scale);
/*     */     } 
/* 748 */     System.err.println(getClass().getName() + ": getError(): Invalid mode!");
/* 749 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public void printDistribution(PrintWriter wrt) throws IOException {
/* 753 */     this.m_ClassStat.printDistribution(wrt);
/* 754 */     this.m_RegStat.printDistribution(wrt);
/*     */   }
/*     */   
/*     */   public void vote(ArrayList votes) {
/* 758 */     System.err.println(getClass().getName() + "vote (): Not implemented");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\statistic\CombStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */