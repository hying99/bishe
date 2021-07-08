/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ public abstract class ClusHeuristicImpl
/*    */   extends ClusHeuristic {
/*    */   protected ClusStatistic m_NegStat;
/*    */   
/*    */   public ClusHeuristicImpl(ClusStatistic negstat) {
/* 10 */     this.m_NegStat = negstat;
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 14 */     this.m_NegStat.copy(tstat);
/* 15 */     this.m_NegStat.subtractFromThis(pstat);
/* 16 */     return calcHeuristic(tstat, pstat, this.m_NegStat, missing);
/*    */   }
/*    */   
/*    */   public abstract double calcHeuristic(ClusStatistic paramClusStatistic1, ClusStatistic paramClusStatistic2, ClusStatistic paramClusStatistic3, ClusStatistic paramClusStatistic4);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\ClusHeuristicImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */