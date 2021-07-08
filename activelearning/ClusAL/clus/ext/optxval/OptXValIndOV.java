/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.SoftTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.list.MyListIter;
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
/*     */ public class OptXValIndOV
/*     */   extends OptXValInduce
/*     */ {
/*     */   protected ClusStatistic[][] m_TestExtraStat;
/*     */   
/*     */   public OptXValIndOV(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  45 */     super(schema, sett);
/*     */   }
/*     */   
/*     */   public static void updateExtra(DataTuple tuple, ClusStatistic[] stats, int idx) {
/*  49 */     int[] folds = tuple.m_Folds;
/*  50 */     for (int j = 0; j < folds.length; ) { stats[folds[j]].updateWeighted(tuple, idx); j++; }
/*     */   
/*     */   }
/*     */   
/*     */   public final void findNominalOV(NominalAttrType at, OptXValGroup grp) {
/*  55 */     int nbvalues = at.getNbValues();
/*  56 */     int statsize = nbvalues + at.intHasMissing();
/*  57 */     reset(statsize);
/*  58 */     resetExtra(statsize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     RowData data = grp.getData();
/*  65 */     int nb_rows = data.getNbRows();
/*  66 */     for (int i = 0; i < nb_rows; i++) {
/*  67 */       DataTuple tuple = data.getTuple(i);
/*  68 */       int value = at.getNominal(tuple);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  73 */       if (tuple.m_Index != -1) {
/*  74 */         this.m_TestStat[tuple.m_Index][value].updateWeighted(tuple, i);
/*     */       } else {
/*  76 */         updateExtra(tuple, this.m_TestExtraStat[value], i);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     sumStats(statsize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     int nb = grp.getNbFolds();
/*  90 */     for (int j = nb - 1; j >= 0; j--) {
/*  91 */       int foldnr = grp.getFold(j);
/*  92 */       ClusStatistic[] cr_stat = this.m_TestStat[foldnr];
/*  93 */       if (foldnr != 0) {
/*  94 */         ClusStatistic[] zero_stat = this.m_TestStat[0];
/*  95 */         for (int m = 0; m < statsize; m++) {
/*  96 */           cr_stat[m].subtractFromOther(zero_stat[m]);
/*     */         }
/*     */       } 
/*  99 */       for (int k = 0; k < statsize; k++) {
/* 100 */         cr_stat[k].add(this.m_TestExtraStat[k][foldnr]);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 106 */       this.m_Split.findSplit(this.m_Selector[j], at);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void resetExtra(int nb) {
/* 115 */     for (int i = 0; i < nb; i++) {
/* 116 */       for (int j = 0; j <= this.m_NbFolds; j++) {
/* 117 */         this.m_TestExtraStat[i][j].reset();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final void findNumericOV(NumericAttrType at, OptXValGroup grp) {
/* 125 */     RowData data = grp.getData();
/* 126 */     int idx = at.getArrayIndex();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 131 */     data.sort(at);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     reset(2);
/* 137 */     ClusStatistic[] extra = this.m_TestExtraStat[0];
/* 138 */     ClusStatistic.reset(extra);
/*     */     
/* 140 */     int first = 0;
/* 141 */     int nb_rows = data.getNbRows();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     copyTotal(grp);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 156 */     int[] folds = grp.getFolds();
/*     */     
/* 158 */     for (int i = 0; i < folds.length; i++) {
/* 159 */       this.m_PrevCl[i] = -1;
/* 160 */       this.m_PrevVl[i] = Double.NaN;
/*     */     } 
/* 162 */     ClusStatistic sum = this.m_PosStat[0];
/* 163 */     if (Settings.ONE_NOMINAL) {
/* 164 */       for (int j = first; j < nb_rows; j++) {
/* 165 */         DataTuple tuple = data.getTuple(j);
/* 166 */         boolean no_sum_calc = true;
/* 167 */         int foldnr = tuple.m_Index;
/* 168 */         int crcl = tuple.getClassification();
/* 169 */         double value = tuple.getDoubleVal(idx);
/* 170 */         if (foldnr != -1) {
/* 171 */           for (int k = 0; k < folds.length; k++) {
/* 172 */             int cr_fold = folds[k];
/* 173 */             if (foldnr != cr_fold) {
/* 174 */               if (this.m_PrevCl[k] == -1 && value != this.m_PrevVl[k] && this.m_PrevVl[k] != Double.NaN)
/* 175 */               { if (no_sum_calc) {
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 180 */                   sum.reset();
/* 181 */                   for (int m = 1; m <= this.m_NbFolds; ) { sum.add(this.m_PosStat[m]); m++; }
/* 182 */                    no_sum_calc = false;
/*     */                 } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 192 */                 this.m_Scratch.copy(sum);
/* 193 */                 this.m_Scratch.add(extra[cr_fold]);
/* 194 */                 if (cr_fold != 0) this.m_Scratch.subtractFromThis(this.m_PosStat[cr_fold]);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 199 */                 this.m_Selector[k].updateNumeric(value, this.m_Scratch, (ClusAttrType)at);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 204 */                 this.m_PrevCl[k] = crcl; }
/*     */               
/* 206 */               else if (this.m_PrevCl[k] != crcl) { this.m_PrevCl[k] = -1; }
/*     */               
/* 208 */               this.m_PrevVl[k] = value;
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 215 */           this.m_PosStat[foldnr].updateWeighted(tuple, j);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 221 */           int ei = 0;
/* 222 */           int fi = 0;
/* 223 */           int[] efolds = tuple.m_Folds;
/* 224 */           while (ei < efolds.length && fi < folds.length) {
/* 225 */             if (efolds[ei] == folds[fi]) {
/* 226 */               int cr_fold = efolds[ei];
/* 227 */               if (this.m_PrevCl[fi] == -1 && value != this.m_PrevVl[fi] && this.m_PrevVl[fi] != Double.NaN)
/* 228 */               { if (no_sum_calc) {
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 233 */                   sum.reset();
/* 234 */                   for (int k = 1; k <= this.m_NbFolds; ) { sum.add(this.m_PosStat[k]); k++; }
/* 235 */                    no_sum_calc = false;
/*     */                 } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 245 */                 this.m_Scratch.copy(sum);
/* 246 */                 this.m_Scratch.add(extra[cr_fold]);
/* 247 */                 if (cr_fold != 0) this.m_Scratch.subtractFromThis(this.m_PosStat[cr_fold]);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 252 */                 this.m_Selector[fi].updateNumeric(value, this.m_Scratch, (ClusAttrType)at);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 257 */                 this.m_PrevCl[fi] = crcl; }
/*     */               
/* 259 */               else if (this.m_PrevCl[fi] != crcl) { this.m_PrevCl[fi] = -1; }
/*     */               
/* 261 */               this.m_PrevVl[fi] = value;
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 266 */               extra[cr_fold].updateWeighted(tuple, j);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 271 */               ei++; fi++; continue;
/* 272 */             }  if (efolds[ei] < folds[fi]) {
/* 273 */               ei++; continue;
/*     */             } 
/* 275 */             fi++;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 281 */       for (int j = first; j < nb_rows; j++) {
/* 282 */         DataTuple tuple = data.getTuple(j);
/* 283 */         boolean no_sum_calc = true;
/* 284 */         int foldnr = tuple.m_Index;
/* 285 */         double value = tuple.getDoubleVal(idx);
/* 286 */         if (foldnr != -1) {
/* 287 */           for (int k = 0; k < folds.length; k++) {
/* 288 */             int cr_fold = folds[k];
/* 289 */             if (foldnr != cr_fold) {
/* 290 */               if (value != this.m_PrevVl[k] && this.m_PrevVl[k] != Double.NaN) {
/* 291 */                 if (no_sum_calc) {
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 296 */                   sum.reset();
/* 297 */                   for (int m = 1; m <= this.m_NbFolds; ) { sum.add(this.m_PosStat[m]); m++; }
/* 298 */                    no_sum_calc = false;
/*     */                 } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 308 */                 this.m_Scratch.copy(sum);
/* 309 */                 this.m_Scratch.add(extra[cr_fold]);
/* 310 */                 if (cr_fold != 0) this.m_Scratch.subtractFromThis(this.m_PosStat[cr_fold]);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 315 */                 this.m_Selector[k].updateNumeric(value, this.m_Scratch, (ClusAttrType)at);
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 321 */               this.m_PrevVl[k] = value;
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 328 */           this.m_PosStat[foldnr].updateWeighted(tuple, j);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 334 */           int ei = 0;
/* 335 */           int fi = 0;
/* 336 */           int[] efolds = tuple.m_Folds;
/* 337 */           while (ei < efolds.length && fi < folds.length) {
/* 338 */             if (efolds[ei] == folds[fi]) {
/* 339 */               int cr_fold = efolds[ei];
/* 340 */               if (value != this.m_PrevVl[fi] && this.m_PrevVl[fi] != Double.NaN) {
/* 341 */                 if (no_sum_calc) {
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 346 */                   sum.reset();
/* 347 */                   for (int k = 1; k <= this.m_NbFolds; ) { sum.add(this.m_PosStat[k]); k++; }
/* 348 */                    no_sum_calc = false;
/*     */                 } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 358 */                 this.m_Scratch.copy(sum);
/* 359 */                 this.m_Scratch.add(extra[cr_fold]);
/* 360 */                 if (cr_fold != 0) this.m_Scratch.subtractFromThis(this.m_PosStat[cr_fold]);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 365 */                 this.m_Selector[fi].updateNumeric(value, this.m_Scratch, (ClusAttrType)at);
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 371 */               this.m_PrevVl[fi] = value;
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 376 */               extra[cr_fold].updateWeighted(tuple, j);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 381 */               ei++; fi++; continue;
/* 382 */             }  if (efolds[ei] < folds[fi]) {
/* 383 */               ei++; continue;
/*     */             } 
/* 385 */             fi++;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final int mkNewGroups(OptXValGroup mgrp, MyListIter ngrps) {
/* 394 */     int nb_groups = 0;
/* 395 */     int nb = mgrp.getNbFolds();
/* 396 */     for (int i = 0; i < nb; i++) {
/* 397 */       ClusNode fnode = mgrp.getNode(i);
/* 398 */       if (fnode != null) {
/* 399 */         NodeTest mtest = fnode.m_Test;
/*     */         
/* 401 */         int gsize = 0;
/* 402 */         boolean soft = false;
/* 403 */         SoftNumericTest stest = null;
/* 404 */         for (int j = i + 1; j < nb; j++) {
/* 405 */           ClusNode onode = mgrp.getNode(j);
/* 406 */           if (onode != null) {
/* 407 */             int tres = mtest.softEquals(onode.m_Test);
/* 408 */             if (tres != 0) gsize++; 
/* 409 */             if (tres == 1) soft = true;
/*     */           
/*     */           } 
/*     */         } 
/* 413 */         int gidx = 1;
/* 414 */         int fold = mgrp.getFold(i);
/* 415 */         OptXValGroup ngrp = new OptXValGroup(mgrp, gsize + 1);
/* 416 */         if (soft) {
/* 417 */           stest = new SoftNumericTest(mtest, gsize + 1);
/* 418 */           stest.addTest(0, fold, mtest);
/* 419 */           ngrp.setTest((NodeTest)stest);
/* 420 */           ngrp.setSoft();
/*     */         } else {
/* 422 */           ngrp.setTest(mtest);
/*     */         } 
/* 424 */         ngrp.setFold(0, fold);
/* 425 */         if (gsize > 0) {
/* 426 */           for (int k = i + 1; k < nb; k++) {
/* 427 */             ClusNode onode = mgrp.getNode(k);
/* 428 */             if (onode != null) {
/* 429 */               int tres = mtest.softEquals(onode.m_Test);
/* 430 */               if (tres != 0) {
/* 431 */                 fold = mgrp.getFold(k);
/* 432 */                 if (stest != null) stest.addTest(gidx, fold, onode.m_Test); 
/* 433 */                 ngrp.setFold(gidx++, fold);
/* 434 */                 mgrp.cleanNode(k);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/* 440 */         if (stest != null) stest.sortIntervals(); 
/* 441 */         if (Settings.VERBOSE > 0) ngrp.println(); 
/* 442 */         ngrps.insertBefore(ngrp);
/* 443 */         nb_groups++;
/*     */       } 
/*     */     } 
/* 446 */     return nb_groups;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void findBestTestOV(OptXValGroup mgrp) {
/* 451 */     mgrp.makeNodes();
/*     */     
/* 453 */     RowData data = mgrp.getData();
/* 454 */     ClusSchema schema = data.getSchema();
/* 455 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 456 */     int nb_normal = attrs.length;
/* 457 */     for (int i = 0; i < nb_normal; i++) {
/* 458 */       ClusAttrType at = attrs[i];
/* 459 */       if (at instanceof NominalAttrType) { findNominalOV((NominalAttrType)at, mgrp); }
/* 460 */       else { findNumericOV((NumericAttrType)at, mgrp); }
/*     */     
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
/*     */   public final void xvalInduce(OptXValNode node, OptXValGroup mgrp) {
/* 474 */     node.init(mgrp.getFolds());
/* 475 */     mgrp.stopCrit(node);
/* 476 */     if (mgrp.cleanFolds())
/*     */       return; 
/* 478 */     if (mgrp.getNbFolds() == 1) {
/* 479 */       int fold = mgrp.getFold();
/* 480 */       ClusNode onode = new ClusNode();
/* 481 */       onode.m_ClusteringStat = mgrp.getTotStat(fold);
/* 482 */       node.setNode(fold, onode);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 487 */       this.m_DFirst.induce(onode, mgrp.getData().getOVFoldData(fold));
/*     */       
/*     */       return;
/*     */     } 
/* 491 */     initTestSelectors(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 496 */     if (mgrp.m_IsSoft) { findBestTestOV(mgrp); }
/* 497 */     else { findBestTest(mgrp); }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 502 */     mgrp.preprocNodes(node, this);
/*     */     
/* 504 */     MyListIter ngrps = new MyListIter();
/* 505 */     int nb_groups = mkNewGroups(mgrp, ngrps);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 515 */     if (nb_groups > 0) {
/* 516 */       int idx = 0;
/* 517 */       node.setNbChildren(nb_groups);
/* 518 */       OptXValGroup grp = (OptXValGroup)ngrps.getFirst();
/* 519 */       while (grp != null) {
/* 520 */         NodeTest test = grp.getTest();
/* 521 */         OptXValSplit split = new OptXValSplit();
/* 522 */         int arity = split.init(grp.getFolds(), test);
/* 523 */         node.setChild((Node)split, idx++);
/* 524 */         RowData gdata = grp.getData();
/* 525 */         if (grp.m_IsSoft) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 531 */           for (int i = 0; i < arity; i++) {
/* 532 */             OptXValNode child = new OptXValNode();
/* 533 */             split.setChild((Node)child, i);
/* 534 */             OptXValGroup cgrp = grp.cloneGroup();
/* 535 */             if (test.isSoft()) { cgrp.setData(gdata.applySoft((SoftTest)test, i)); }
/* 536 */             else { cgrp.setData(gdata.apply(test, i)); }
/* 537 */              cgrp.create(this.m_StatManager, this.m_NbFolds);
/* 538 */             if (cgrp.updateSoft()) { cgrp.calcTotalStats(this.m_TestExtraStat[0]); }
/* 539 */             else { cgrp.calcTotalStats(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 548 */             xvalInduce(child, cgrp);
/*     */ 
/*     */           
/*     */           }
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */ 
/*     */           
/* 560 */           for (int i = 0; i < arity; i++) {
/* 561 */             OptXValNode child = new OptXValNode();
/* 562 */             split.setChild((Node)child, i);
/* 563 */             OptXValGroup cgrp = grp.cloneGroup();
/* 564 */             cgrp.setData(gdata.apply(test, i));
/* 565 */             cgrp.create(this.m_StatManager, this.m_NbFolds);
/* 566 */             cgrp.calcTotalStats();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 575 */             xvalInduce(child, cgrp);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 582 */         grp = (OptXValGroup)ngrps.getNext();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void createExtraStats() {
/* 588 */     this.m_TestExtraStat = new ClusStatistic[this.m_MaxStats][this.m_NbFolds + 1];
/* 589 */     for (int j = 0; j < this.m_MaxStats; j++) {
/* 590 */       for (int i = 0; i <= this.m_NbFolds; i++) {
/* 591 */         this.m_TestExtraStat[j][i] = this.m_StatManager.createClusteringStat();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public OptXValNode xvalInduce(OptXValGroup mgrp) {
/* 597 */     createExtraStats();
/* 598 */     OptXValNode root = new OptXValNode();
/* 599 */     xvalInduce(root, mgrp);
/* 600 */     return root;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\optxval\OptXValIndOV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */