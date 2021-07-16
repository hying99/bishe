/*    */ package clus.algo.split;
/*    */ 
/*    */ import clus.data.type.NominalAttrType;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import java.util.Random;
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
/*    */ public abstract class NominalSplit
/*    */ {
/*    */   public double[] createFreqList(double n_tot, ClusStatistic[] s_set, int nbvalues) {
/* 34 */     double[] res = new double[nbvalues];
/* 35 */     for (int i = 0; i < nbvalues; i++)
/* 36 */       res[i] = (s_set[i]).m_SumWeight / n_tot; 
/* 37 */     return res;
/*    */   }
/*    */   
/*    */   public abstract void initialize(ClusStatManager paramClusStatManager);
/*    */   
/*    */   public abstract void setSDataSize(int paramInt);
/*    */   
/*    */   public abstract void findSplit(CurrentBestTestAndHeuristic paramCurrentBestTestAndHeuristic, NominalAttrType paramNominalAttrType);
/*    */   
/*    */   public abstract void findRandomSplit(CurrentBestTestAndHeuristic paramCurrentBestTestAndHeuristic, NominalAttrType paramNominalAttrType, Random paramRandom);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\split\NominalSplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */