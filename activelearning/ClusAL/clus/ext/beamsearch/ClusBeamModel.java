/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
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
/*     */ public class ClusBeamModel
/*     */   implements Comparable, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  41 */   protected transient int m_HashCode = -1;
/*     */   
/*     */   protected int m_ParentIndex;
/*     */   protected boolean m_Refined;
/*     */   protected boolean m_Finished;
/*     */   protected double m_Value;
/*     */   protected ClusModel m_Root;
/*     */   protected Object m_Refinement;
/*     */   protected double m_DistanceToBeam;
/*     */   protected ArrayList<ClusStatistic> m_Predictions;
/*     */   
/*     */   public ClusBeamModel() {}
/*     */   
/*     */   public ClusBeamModel(double value, ClusModel root) {
/*  55 */     this.m_Value = value;
/*  56 */     this.m_Root = root;
/*     */   }
/*     */   
/*     */   public void setValue(double value) {
/*  60 */     this.m_Value = value;
/*     */   }
/*     */   
/*     */   public double getValue() {
/*  64 */     return this.m_Value;
/*     */   }
/*     */   
/*     */   public ClusModel getModel() {
/*  68 */     return this.m_Root;
/*     */   }
/*     */   
/*     */   public void setModel(ClusNode root) {
/*  72 */     this.m_Root = (ClusModel)root;
/*     */   }
/*     */   
/*     */   public Object getRefinement() {
/*  76 */     return this.m_Refinement;
/*     */   }
/*     */   
/*     */   public void setRefinement(Object refinement) {
/*  80 */     this.m_Refinement = refinement;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  84 */     return "" + this.m_Value;
/*     */   }
/*     */   
/*     */   public final boolean isRefined() {
/*  88 */     return this.m_Refined;
/*     */   }
/*     */   
/*     */   public final void setRefined(boolean ref) {
/*  92 */     this.m_Refined = ref;
/*     */   }
/*     */   
/*     */   public final boolean isFinished() {
/*  96 */     return this.m_Finished;
/*     */   }
/*     */   
/*     */   public final void setFinished(boolean finish) {
/* 100 */     this.m_Finished = finish;
/*     */   }
/*     */   
/*     */   public void setParentModelIndex(int parent) {
/* 104 */     this.m_ParentIndex = parent;
/*     */   }
/*     */   
/*     */   public int getParentModelIndex() {
/* 108 */     return this.m_ParentIndex;
/*     */   }
/*     */   
/*     */   public int compareTo(Object e2) {
/* 112 */     ClusBeamModel m2 = (ClusBeamModel)e2;
/* 113 */     if (m2.m_Value != this.m_Value) {
/* 114 */       return (m2.m_Value < this.m_Value) ? -1 : 1;
/*     */     }
/* 116 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 121 */     if (this.m_HashCode == -1) {
/* 122 */       this.m_HashCode = this.m_Root.hashCode();
/* 123 */       if (this.m_HashCode == -1) this.m_HashCode = 0; 
/*     */     } 
/* 125 */     return this.m_HashCode;
/*     */   }
/*     */   
/*     */   public boolean equals(Object other) {
/* 129 */     ClusBeamModel o = (ClusBeamModel)other;
/* 130 */     if (hashCode() != o.hashCode()) {
/* 131 */       return false;
/*     */     }
/* 133 */     return this.m_Root.equals(o.m_Root);
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusBeamModel cloneNoModel() {
/* 138 */     ClusBeamModel res = new ClusBeamModel();
/* 139 */     res.m_ParentIndex = this.m_ParentIndex;
/* 140 */     return res;
/*     */   }
/*     */   
/*     */   public ClusBeamModel cloneModel() {
/* 144 */     ClusBeamModel res = new ClusBeamModel();
/* 145 */     res.m_ParentIndex = this.m_ParentIndex;
/* 146 */     res.m_Root = this.m_Root;
/* 147 */     return res;
/*     */   }
/*     */   
/*     */   public void setDistanceToBeam(double distance) {
/* 151 */     this.m_DistanceToBeam = distance;
/*     */   }
/*     */   
/*     */   public double getDistanceToBeam() {
/* 155 */     return this.m_DistanceToBeam;
/*     */   }
/*     */   
/*     */   public void setModelPredictions(ArrayList<ClusStatistic> predictions) {
/* 159 */     this.m_Predictions = predictions;
/*     */   }
/*     */   
/*     */   public ArrayList<ClusStatistic> getModelPredictions() {
/* 163 */     return this.m_Predictions;
/*     */   }
/*     */   
/*     */   public ClusStatistic getPredictionForTuple(DataTuple tuple) {
/* 167 */     return this.m_Predictions.get(tuple.getIndex());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */