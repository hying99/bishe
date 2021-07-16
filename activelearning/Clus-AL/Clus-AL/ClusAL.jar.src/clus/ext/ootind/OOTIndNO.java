/*     */ package clus.ext.ootind;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.optxval.OptXValGroup;
/*     */ import clus.ext.optxval.OptXValNode;
/*     */ import clus.ext.optxval.OptXValSplit;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
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
/*     */ 
/*     */ public class OOTIndNO
/*     */   extends OOTInduce
/*     */ {
/*     */   public OOTIndNO(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  43 */     super(schema, sett);
/*     */   }
/*     */   
/*     */   public final int mkNewGroups(OptXValGroup mgrp, MyListIter ngrps) {
/*  47 */     int nb_groups = 0;
/*  48 */     int nb = mgrp.getNbFolds();
/*  49 */     for (int i = 0; i < nb; i++) {
/*  50 */       ClusNode fnode = mgrp.getNode(i);
/*  51 */       if (fnode != null) {
/*  52 */         NodeTest mtest = fnode.m_Test;
/*     */         
/*  54 */         int gsize = 0;
/*  55 */         for (int j = i + 1; j < nb; j++) {
/*  56 */           ClusNode onode = mgrp.getNode(j);
/*  57 */           if (onode != null && mtest.equals(onode.m_Test)) gsize++;
/*     */         
/*     */         } 
/*  60 */         int gidx = 1;
/*  61 */         OptXValGroup ngrp = new OptXValGroup(mgrp.getData(), gsize + 1);
/*  62 */         ngrp.setTest(mtest);
/*  63 */         ngrp.setFold(0, mgrp.getFold(i));
/*  64 */         if (gsize > 0) {
/*  65 */           for (int k = i + 1; k < nb; k++) {
/*  66 */             ClusNode onode = mgrp.getNode(k);
/*  67 */             if (onode != null && mtest.equals(onode.m_Test)) {
/*  68 */               ngrp.setFold(gidx++, mgrp.getFold(k));
/*  69 */               mgrp.cleanNode(k);
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/*  74 */         if (Settings.VERBOSE > 0) ngrp.println(); 
/*  75 */         ngrps.insertBefore((MyList)ngrp);
/*  76 */         nb_groups++;
/*     */       } 
/*     */     } 
/*  79 */     return nb_groups;
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
/*  92 */     node.init(mgrp.getFolds());
/*  93 */     mgrp.stopCrit(node);
/*  94 */     if (mgrp.cleanFolds())
/*     */       return; 
/*  96 */     if (mgrp.getNbFolds() == 1) {
/*  97 */       int fold = mgrp.getFold();
/*  98 */       ClusNode onode = new ClusNode();
/*  99 */       onode.m_ClusteringStat = mgrp.getTotStat(fold);
/* 100 */       node.setNode(fold, onode);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 105 */       this.m_DFirst.induce(onode, mgrp.getData().getFoldData2(fold));
/*     */       
/*     */       return;
/*     */     } 
/* 109 */     if (this.SHOULD_OPTIMIZE) {
/* 110 */       mgrp.optimize2();
/*     */     }
/*     */     
/* 113 */     initTestSelectors(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     findBestTest(mgrp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 123 */     mgrp.preprocNodes2(node, this);
/*     */     
/* 125 */     MyListIter ngrps = new MyListIter();
/* 126 */     int nb_groups = mkNewGroups(mgrp, ngrps);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     if (nb_groups > 0) {
/* 137 */       int idx = 0;
/* 138 */       node.setNbChildren(nb_groups);
/* 139 */       OptXValGroup grp = (OptXValGroup)ngrps.getFirst();
/* 140 */       while (grp != null) {
/* 141 */         NodeTest test = grp.getTest();
/* 142 */         OptXValSplit split = new OptXValSplit();
/* 143 */         int arity = split.init(grp.getFolds(), test);
/* 144 */         node.setChild((Node)split, idx++);
/* 145 */         RowData gdata = grp.getData();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 151 */         for (int i = 0; i < arity; i++) {
/* 152 */           OptXValNode child = new OptXValNode();
/* 153 */           split.setChild((Node)child, i);
/* 154 */           OptXValGroup cgrp = grp.cloneGroup();
/* 155 */           cgrp.setData(gdata.apply(test, i));
/* 156 */           cgrp.create2(this.m_StatManager, this.m_NbFolds);
/* 157 */           cgrp.calcTotalStats2();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 166 */           xvalInduce(child, cgrp);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 172 */         grp = (OptXValGroup)ngrps.getNext();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public OptXValNode xvalInduce(OptXValGroup mgrp) {
/* 178 */     OptXValNode root = new OptXValNode();
/* 179 */     xvalInduce(root, mgrp);
/* 180 */     return root;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ootind\OOTIndNO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */