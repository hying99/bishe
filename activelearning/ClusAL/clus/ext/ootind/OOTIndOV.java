/*     */ package clus.ext.ootind;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.optxval.OptXValGroup;
/*     */ import clus.ext.optxval.OptXValNode;
/*     */ import clus.ext.optxval.OptXValSplit;
/*     */ import clus.ext.optxval.SoftNumericTest;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.SoftTest;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.list.MyList;
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
/*     */ 
/*     */ public class OOTIndOV
/*     */   extends OOTInduce
/*     */ {
/*     */   public OOTIndOV(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  44 */     super(schema, sett);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int mkNewGroups(OptXValGroup mgrp, MyListIter ngrps) {
/*  49 */     int nb_groups = 0;
/*  50 */     int nb = mgrp.getNbFolds();
/*  51 */     for (int i = 0; i < nb; i++) {
/*  52 */       ClusNode fnode = mgrp.getNode(i);
/*  53 */       if (fnode != null) {
/*  54 */         NodeTest mtest = fnode.m_Test;
/*     */         
/*  56 */         int gsize = 0;
/*  57 */         boolean soft = false;
/*  58 */         SoftNumericTest stest = null;
/*  59 */         for (int j = i + 1; j < nb; j++) {
/*  60 */           ClusNode onode = mgrp.getNode(j);
/*  61 */           if (onode != null) {
/*  62 */             int tres = mtest.softEquals(onode.m_Test);
/*  63 */             if (tres != 0) gsize++; 
/*  64 */             if (tres == 1) soft = true;
/*     */           
/*     */           } 
/*     */         } 
/*  68 */         int gidx = 1;
/*  69 */         int fold = mgrp.getFold(i);
/*  70 */         OptXValGroup ngrp = new OptXValGroup(mgrp, gsize + 1);
/*  71 */         if (soft) {
/*  72 */           stest = new SoftNumericTest(mtest, gsize + 1);
/*  73 */           stest.addTest(0, fold, mtest);
/*  74 */           ngrp.setTest((NodeTest)stest);
/*  75 */           ngrp.setSoft();
/*     */         } else {
/*  77 */           ngrp.setTest(mtest);
/*     */         } 
/*  79 */         ngrp.setFold(0, fold);
/*  80 */         if (gsize > 0) {
/*  81 */           for (int k = i + 1; k < nb; k++) {
/*  82 */             ClusNode onode = mgrp.getNode(k);
/*  83 */             if (onode != null) {
/*  84 */               int tres = mtest.softEquals(onode.m_Test);
/*  85 */               if (tres != 0) {
/*  86 */                 fold = mgrp.getFold(k);
/*  87 */                 if (stest != null) stest.addTest(gidx, fold, onode.m_Test); 
/*  88 */                 ngrp.setFold(gidx++, fold);
/*  89 */                 mgrp.cleanNode(k);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/*  95 */         if (stest != null) stest.sortIntervals(); 
/*  96 */         if (Settings.VERBOSE > 0) ngrp.println(); 
/*  97 */         ngrps.insertBefore((MyList)ngrp);
/*  98 */         nb_groups++;
/*     */       } 
/*     */     } 
/* 101 */     return nb_groups;
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
/*     */   public final void xvalInduce(OptXValNode node, OptXValGroup mgrp) {
/* 114 */     node.init(mgrp.getFolds());
/* 115 */     mgrp.stopCrit(node);
/* 116 */     if (mgrp.cleanFolds())
/*     */       return; 
/* 118 */     if (mgrp.getNbFolds() == 1) {
/* 119 */       int fold = mgrp.getFold();
/* 120 */       ClusNode onode = new ClusNode();
/* 121 */       onode.m_ClusteringStat = mgrp.getTotStat(fold);
/* 122 */       node.setNode(fold, onode);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 127 */       this.m_DFirst.induce(onode, mgrp.getData().getFoldData2(fold));
/*     */       
/*     */       return;
/*     */     } 
/* 131 */     if (this.SHOULD_OPTIMIZE) {
/* 132 */       mgrp.optimize2();
/*     */     }
/*     */     
/* 135 */     initTestSelectors(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     findBestTest(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     mgrp.preprocNodes2(node, this);
/*     */     
/* 147 */     MyListIter ngrps = new MyListIter();
/* 148 */     int nb_groups = mkNewGroups(mgrp, ngrps);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 158 */     if (nb_groups > 0) {
/* 159 */       int idx = 0;
/* 160 */       node.setNbChildren(nb_groups);
/* 161 */       OptXValGroup grp = (OptXValGroup)ngrps.getFirst();
/* 162 */       while (grp != null) {
/* 163 */         NodeTest test = grp.getTest();
/* 164 */         OptXValSplit split = new OptXValSplit();
/* 165 */         int arity = split.init(grp.getFolds(), test);
/* 166 */         node.setChild((Node)split, idx++);
/* 167 */         RowData gdata = grp.getData();
/* 168 */         if (grp.m_IsSoft) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 174 */           for (int i = 0; i < arity; i++) {
/* 175 */             OptXValNode child = new OptXValNode();
/* 176 */             split.setChild((Node)child, i);
/* 177 */             OptXValGroup cgrp = grp.cloneGroup();
/* 178 */             if (test.isSoft()) { cgrp.setData(gdata.applySoft2((SoftTest)test, i)); }
/* 179 */             else { cgrp.setData(gdata.apply(test, i)); }
/* 180 */              cgrp.create2(this.m_StatManager, this.m_NbFolds);
/* 181 */             cgrp.calcTotalStats2();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 190 */             xvalInduce(child, cgrp);
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
/* 202 */           for (int i = 0; i < arity; i++) {
/* 203 */             OptXValNode child = new OptXValNode();
/* 204 */             split.setChild((Node)child, i);
/* 205 */             OptXValGroup cgrp = grp.cloneGroup();
/* 206 */             cgrp.setData(gdata.apply(test, i));
/* 207 */             cgrp.create2(this.m_StatManager, this.m_NbFolds);
/* 208 */             cgrp.calcTotalStats2();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 217 */             xvalInduce(child, cgrp);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 224 */         grp = (OptXValGroup)ngrps.getNext();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public OptXValNode xvalInduce(OptXValGroup mgrp) {
/* 230 */     OptXValNode root = new OptXValNode();
/* 231 */     xvalInduce(root, mgrp);
/* 232 */     return root;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ootind\OOTIndOV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */