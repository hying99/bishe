/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.statistic.ClusDistance;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.SumPairwiseDistancesStat;
/*    */ 
/*    */ public class HierSumPairwiseDistancesStat
/*    */   extends WHTDStatistic
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected SumPairwiseDistancesStat m_PairwiseDistStat;
/*    */   
/*    */   public HierSumPairwiseDistancesStat(ClassHierarchy hier, ClusDistance dist, int comp) {
/* 17 */     super(hier, comp);
/* 18 */     this.m_PairwiseDistStat = new SumPairwiseDistancesStat(dist);
/*    */   }
/*    */   
/*    */   public ClusStatistic cloneStat() {
/* 22 */     ClusDistance dist = this.m_PairwiseDistStat.getDistance();
/* 23 */     return (ClusStatistic)new HierSumPairwiseDistancesStat(this.m_Hier, dist, this.m_Compatibility);
/*    */   }
/*    */   
/*    */   public void setSDataSize(int nbex) {
/* 27 */     this.m_PairwiseDistStat.setSDataSize(nbex);
/*    */   }
/*    */   
/*    */   public double getSVarS(ClusAttributeWeights scale, RowData data) {
/* 31 */     return this.m_PairwiseDistStat.getSVarS(scale, data);
/*    */   }
/*    */   
/*    */   public void updateWeighted(DataTuple tuple, int idx) {
/* 35 */     super.updateWeighted(tuple, idx);
/* 36 */     this.m_PairwiseDistStat.updateWeighted(tuple, idx);
/*    */   }
/*    */   
/*    */   public void reset() {
/* 40 */     super.reset();
/* 41 */     this.m_PairwiseDistStat.reset();
/*    */   }
/*    */   
/*    */   public void copy(ClusStatistic other) {
/* 45 */     HierSumPairwiseDistancesStat or = (HierSumPairwiseDistancesStat)other;
/* 46 */     super.copy((ClusStatistic)or);
/* 47 */     this.m_PairwiseDistStat.copy((ClusStatistic)or.m_PairwiseDistStat);
/*    */   }
/*    */   
/*    */   public void add(ClusStatistic other) {
/* 51 */     HierSumPairwiseDistancesStat or = (HierSumPairwiseDistancesStat)other;
/* 52 */     super.add((ClusStatistic)or);
/* 53 */     this.m_PairwiseDistStat.add((ClusStatistic)or.m_PairwiseDistStat);
/*    */   }
/*    */   
/*    */   public void addScaled(double scale, ClusStatistic other) {
/* 57 */     System.err.println("HierSumPairwiseDistancesStat: addScaled not implemented");
/*    */   }
/*    */   
/*    */   public void subtractFromThis(ClusStatistic other) {
/* 61 */     HierSumPairwiseDistancesStat or = (HierSumPairwiseDistancesStat)other;
/* 62 */     super.subtractFromThis(other);
/* 63 */     this.m_PairwiseDistStat.subtractFromThis((ClusStatistic)or.m_PairwiseDistStat);
/*    */   }
/*    */   
/*    */   public void subtractFromOther(ClusStatistic other) {
/* 67 */     HierSumPairwiseDistancesStat or = (HierSumPairwiseDistancesStat)other;
/* 68 */     super.subtractFromOther(other);
/* 69 */     this.m_PairwiseDistStat.subtractFromOther((ClusStatistic)or.m_PairwiseDistStat);
/*    */   }
/*    */   
/*    */   public String getDistanceName() {
/* 73 */     return this.m_PairwiseDistStat.getDistanceName();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\HierSumPairwiseDistancesStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */