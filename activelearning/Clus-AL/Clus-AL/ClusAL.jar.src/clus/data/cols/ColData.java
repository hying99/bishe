/*     */ package clus.data.cols;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.cols.attribute.ClusAttribute;
/*     */ import clus.data.io.ClusView;
/*     */ import clus.data.rows.DataPreprocs;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.io.ClusSerializable;
/*     */ import clus.io.DummySerializable;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.util.Vector;
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
/*     */ public class ColData
/*     */   extends ClusData
/*     */ {
/*     */   protected ColTarget m_Target;
/*     */   protected int m_NbAttrs;
/*  44 */   protected Vector m_Attr = new Vector();
/*     */   
/*     */   public ClusData select(ClusSelection sel) {
/*  47 */     ColData res = new ColData();
/*  48 */     int nbsel = sel.getNbSelected();
/*  49 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  50 */       ClusAttribute attr = getAttribute(i);
/*  51 */       res.addAttribute(attr.select(sel, nbsel));
/*     */     } 
/*  53 */     res.setTarget(this.m_Target.select(sel, nbsel));
/*  54 */     res.setNbRows(nbsel);
/*  55 */     setNbRows(this.m_NbRows - nbsel);
/*  56 */     return res;
/*     */   }
/*     */   
/*     */   public ClusData cloneData() {
/*  60 */     return null;
/*     */   }
/*     */   
/*     */   public void insert(ClusData data, ClusSelection sel) {
/*  64 */     ColData other = (ColData)data;
/*  65 */     int nb_new = this.m_NbRows + sel.getNbSelected();
/*  66 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  67 */       ClusAttribute attr = other.getAttribute(i);
/*  68 */       getAttribute(i).insert(attr, sel, nb_new);
/*     */     } 
/*  70 */     this.m_Target.insert(other.getColTarget(), sel, nb_new);
/*  71 */     setNbRows(nb_new);
/*     */   }
/*     */   
/*     */   public ClusRun partition(ClusSummary summary) {
/*  75 */     return new ClusRun(this, summary);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unpartition(ClusRun cr) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbAttributes() {
/*  87 */     return this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public void setTarget(ColTarget target) {
/*  91 */     this.m_Target = target;
/*     */   }
/*     */   
/*     */   public ColTarget getColTarget() {
/*  95 */     return this.m_Target;
/*     */   }
/*     */   
/*     */   public ClusAttribute getAttribute(int idx) {
/*  99 */     return this.m_Attr.elementAt(idx);
/*     */   }
/*     */   
/*     */   public void addAttribute(ClusAttribute attr) {
/* 103 */     this.m_Attr.addElement(attr);
/* 104 */     this.m_NbAttrs++;
/*     */   }
/*     */   
/*     */   public void resetSplitAttrs() {
/* 108 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/* 109 */       ClusAttribute attr = this.m_Attr.elementAt(j);
/* 110 */       attr.setSplit(false);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resize(int nbrows) {
/* 115 */     this.m_NbRows = nbrows;
/* 116 */     this.m_Target.resize(nbrows);
/* 117 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/* 118 */       ClusAttribute attr = this.m_Attr.elementAt(j);
/* 119 */       attr.resize(nbrows);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void prepareAttributes() {
/* 124 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/* 125 */       ClusAttribute attr = this.m_Attr.elementAt(j);
/* 126 */       attr.prepare();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void unPrepareAttributes() {
/* 131 */     for (int j = 0; j < this.m_NbAttrs; j++) {
/* 132 */       ClusAttribute attr = this.m_Attr.elementAt(j);
/* 133 */       attr.unprepare();
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusView createNormalView(ClusSchema schema) throws ClusException {
/* 138 */     int my_idx = 0;
/* 139 */     ClusView view = new ClusView();
/* 140 */     int nb = schema.getNbAttributes();
/* 141 */     for (int j = 0; j < nb; j++) {
/* 142 */       ClusAttrType at = schema.getAttrType(j);
/* 143 */       switch (at.getStatus()) {
/*     */         case 0:
/* 145 */           view.addAttribute((ClusSerializable)new DummySerializable());
/*     */           break;
/*     */         case 1:
/* 148 */           view.addAttribute((ClusSerializable)at.createTargetAttr(this.m_Target));
/*     */           break;
/*     */         default:
/* 151 */           view.addAttribute((ClusSerializable)getAttribute(my_idx++)); break;
/*     */       } 
/*     */     } 
/* 154 */     return view;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void attach(ClusNode node) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcError(ClusNode node, ClusErrorList par) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcTotalStat(ClusStatistic stat) {
/* 173 */     this.m_Target.calcTotalStat(stat);
/*     */   }
/*     */   
/*     */   public double[] getNumeric(int idx) {
/* 177 */     return this.m_Target.m_Numeric[idx];
/*     */   }
/*     */   
/*     */   public int[] getNominal(int idx) {
/* 181 */     return null;
/*     */   }
/*     */   
/*     */   public void preprocess(int pass, DataPreprocs pps) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\ColData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */