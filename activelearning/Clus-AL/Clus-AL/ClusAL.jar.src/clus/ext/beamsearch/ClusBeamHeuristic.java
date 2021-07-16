/*    */ package clus.ext.beamsearch;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.heuristic.ClusHeuristic;
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ClusBeamHeuristic
/*    */   extends ClusHeuristic
/*    */ {
/*    */   protected double m_NbTrain;
/*    */   protected double m_TreeOffset;
/*    */   protected ClusStatistic m_Pos;
/*    */   protected ClusStatistic m_Neg;
/*    */   protected ClusHeuristic m_AttrHeuristic;
/*    */   
/*    */   public ClusBeamHeuristic(ClusStatistic stat) {
/* 40 */     this.m_Pos = stat;
/* 41 */     this.m_Neg = stat.cloneStat();
/*    */   }
/*    */   
/*    */   public abstract double estimateBeamMeasure(ClusNode paramClusNode);
/*    */   
/*    */   public abstract double computeLeafAdd(ClusNode paramClusNode);
/*    */   
/*    */   public void setTreeOffset(double value) {
/* 49 */     this.m_TreeOffset = value;
/*    */   }
/*    */   
/*    */   public void setRootStatistic(ClusStatistic stat) {
/* 53 */     this.m_NbTrain = stat.m_SumWeight;
/*    */   }
/*    */   
/*    */   public void setAttrHeuristic(ClusHeuristic heur) {
/* 57 */     this.m_AttrHeuristic = heur;
/*    */   }
/*    */   
/*    */   public String getAttrHeuristicString() {
/* 61 */     if (this.m_AttrHeuristic == null) return ""; 
/* 62 */     return ", attribute heuristic = " + this.m_AttrHeuristic.getName();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */