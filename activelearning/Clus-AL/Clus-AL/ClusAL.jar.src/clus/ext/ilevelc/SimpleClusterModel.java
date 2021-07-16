/*    */ package clus.ext.ilevelc;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.StatisticPrintInfo;
/*    */ import java.io.PrintWriter;
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
/*    */ 
/*    */ public class SimpleClusterModel
/*    */   extends ClusNode
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected int[] m_Assign;
/*    */   protected ClusStatManager m_Manager;
/*    */   
/*    */   public SimpleClusterModel(int[] assign, ClusStatManager mgr) {
/* 42 */     this.m_Assign = assign;
/* 43 */     this.m_Manager = mgr;
/*    */   }
/*    */   
/*    */   public ClusStatistic predictWeighted(DataTuple tuple) {
/* 47 */     int idx = tuple.getIndex();
/* 48 */     int cl = this.m_Assign[idx];
/* 49 */     ILevelCStatistic stat = (ILevelCStatistic)this.m_Manager.getStatistic(2).cloneStat();
/* 50 */     stat.setClusterID(cl);
/* 51 */     stat.calcMean();
/* 52 */     return (ClusStatistic)stat;
/*    */   }
/*    */   
/*    */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/* 56 */     wrt.println("MPCKMeans()");
/* 57 */     if (this.m_Assign == null) {
/* 58 */       wrt.println("   Illegal");
/*    */     } else {
/* 60 */       wrt.println("   " + this.m_Assign.length);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ilevelc\SimpleClusterModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */