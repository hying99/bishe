/*    */ package clus.statistic;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatisticPrintInfo
/*    */ {
/*    */   public boolean SHOW_EXAMPLE_COUNT = true;
/*    */   public boolean SHOW_EXAMPLE_COUNT_BYTARGET = false;
/*    */   public boolean SHOW_DISTRIBUTION = false;
/*    */   public boolean SHOW_INDEX = false;
/*    */   public boolean INTERNAL_DISTR = false;
/* 37 */   public static StatisticPrintInfo m_Instance = new StatisticPrintInfo();
/*    */   
/*    */   public static StatisticPrintInfo getInstance() {
/* 40 */     return m_Instance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\StatisticPrintInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */