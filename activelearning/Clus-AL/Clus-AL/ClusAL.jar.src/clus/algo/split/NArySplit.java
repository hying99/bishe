/*    */ package clus.algo.split;
/*    */ 
/*    */ import clus.data.type.NominalAttrType;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.model.test.NodeTest;
/*    */ import clus.model.test.NominalTest;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.util.ClusException;
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
/*    */ public class NArySplit
/*    */   extends NominalSplit
/*    */ {
/*    */   ClusStatistic m_MStat;
/*    */   
/*    */   public void initialize(ClusStatManager manager) {
/* 39 */     this.m_MStat = manager.createClusteringStat();
/*    */   }
/*    */   
/*    */   public void setSDataSize(int size) {
/* 43 */     this.m_MStat.setSDataSize(size);
/*    */   }
/*    */   
/*    */   public void findSplit(CurrentBestTestAndHeuristic node, NominalAttrType type) {
/* 47 */     double unk_freq = 0.0D;
/* 48 */     int nbvalues = type.getNbValues();
/*    */     
/* 50 */     if (type.hasMissing()) {
/* 51 */       ClusStatistic unknown = node.m_TestStat[nbvalues];
/* 52 */       this.m_MStat.copy(node.m_TotStat);
/* 53 */       this.m_MStat.subtractFromThis(unknown);
/* 54 */       unk_freq = unknown.m_SumWeight / node.getTotWeight();
/*    */     } else {
/* 56 */       this.m_MStat.copy(node.m_TotStat);
/*    */     } 
/*    */     
/* 59 */     double mheur = node.calcHeuristic(this.m_MStat, node.m_TestStat, nbvalues);
/* 60 */     if (mheur > node.m_BestHeur + 1.0E-6D) {
/* 61 */       node.m_UnknownFreq = unk_freq;
/* 62 */       node.m_BestHeur = mheur;
/* 63 */       node.m_TestType = 1;
/* 64 */       double[] freq = createFreqList(this.m_MStat.m_SumWeight, node.m_TestStat, nbvalues);
/* 65 */       node.m_BestTest = (NodeTest)new NominalTest(type, freq);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void findRandomSplit(CurrentBestTestAndHeuristic node, NominalAttrType type, Random rn) {
/*    */     try {
/* 71 */       throw new ClusException("Not implemented yet!");
/* 72 */     } catch (ClusException e) {
/* 73 */       e.printStackTrace();
/*    */       return;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\split\NArySplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */