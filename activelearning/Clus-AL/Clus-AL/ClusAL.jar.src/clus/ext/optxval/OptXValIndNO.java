/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OptXValIndNO
/*     */   extends OptXValInduce
/*     */ {
/*     */   public OptXValIndNO(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  42 */     super(schema, sett);
/*     */   }
/*     */   
/*     */   public final int mkNewGroups(OptXValGroup mgrp, MyListIter ngrps) {
/*  46 */     int nb_groups = 0;
/*  47 */     int nb = mgrp.getNbFolds();
/*  48 */     for (int i = 0; i < nb; i++) {
/*  49 */       ClusNode fnode = mgrp.getNode(i);
/*  50 */       if (fnode != null) {
/*  51 */         NodeTest mtest = fnode.m_Test;
/*     */         
/*  53 */         int gsize = 0;
/*  54 */         for (int j = i + 1; j < nb; j++) {
/*  55 */           ClusNode onode = mgrp.getNode(j);
/*  56 */           if (onode != null && mtest.equals(onode.m_Test)) gsize++;
/*     */         
/*     */         } 
/*  59 */         int gidx = 1;
/*  60 */         OptXValGroup ngrp = new OptXValGroup(mgrp.getData(), gsize + 1);
/*  61 */         ngrp.setTest(mtest);
/*  62 */         ngrp.setFold(0, mgrp.getFold(i));
/*  63 */         if (gsize > 0) {
/*  64 */           for (int k = i + 1; k < nb; k++) {
/*  65 */             ClusNode onode = mgrp.getNode(k);
/*  66 */             if (onode != null && mtest.equals(onode.m_Test)) {
/*  67 */               ngrp.setFold(gidx++, mgrp.getFold(k));
/*  68 */               mgrp.cleanNode(k);
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/*  73 */         if (Settings.VERBOSE > 0) ngrp.println(); 
/*  74 */         ngrps.insertBefore(ngrp);
/*  75 */         nb_groups++;
/*     */       } 
/*     */     } 
/*  78 */     return nb_groups;
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
/*  91 */     node.init(mgrp.getFolds());
/*  92 */     mgrp.stopCrit(node);
/*  93 */     if (mgrp.cleanFolds())
/*     */       return; 
/*  95 */     if (mgrp.getNbFolds() == 1) {
/*  96 */       int fold = mgrp.getFold();
/*  97 */       ClusNode onode = new ClusNode();
/*  98 */       onode.m_ClusteringStat = mgrp.getTotStat(fold);
/*  99 */       node.setNode(fold, onode);
/* 100 */       this.m_DFirst.induce(onode, mgrp.getData().getFoldData(fold));
/*     */       
/*     */       return;
/*     */     } 
/* 104 */     initTestSelectors(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 109 */     findBestTest(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     mgrp.preprocNodes(node, this);
/*     */     
/* 116 */     MyListIter ngrps = new MyListIter();
/* 117 */     int nb_groups = mkNewGroups(mgrp, ngrps);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     if (nb_groups > 0) {
/* 128 */       int idx = 0;
/* 129 */       node.setNbChildren(nb_groups);
/* 130 */       OptXValGroup grp = (OptXValGroup)ngrps.getFirst();
/* 131 */       while (grp != null) {
/* 132 */         NodeTest test = grp.getTest();
/* 133 */         OptXValSplit split = new OptXValSplit();
/* 134 */         int arity = split.init(grp.getFolds(), test);
/* 135 */         node.setChild((Node)split, idx++);
/* 136 */         RowData gdata = grp.getData();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 142 */         for (int i = 0; i < arity; i++) {
/* 143 */           OptXValNode child = new OptXValNode();
/* 144 */           split.setChild((Node)child, i);
/* 145 */           OptXValGroup cgrp = grp.cloneGroup();
/* 146 */           cgrp.setData(gdata.apply(test, i));
/* 147 */           cgrp.create(this.m_StatManager, this.m_NbFolds);
/* 148 */           cgrp.calcTotalStats();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 157 */           xvalInduce(child, cgrp);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 163 */         grp = (OptXValGroup)ngrps.getNext();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public OptXValNode xvalInduce(OptXValGroup mgrp) {
/* 169 */     OptXValNode root = new OptXValNode();
/* 170 */     xvalInduce(root, mgrp);
/* 171 */     return root;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\optxval\OptXValIndNO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */