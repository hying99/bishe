/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.statistic.ClusDistance;
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ public class HierJaccardDistance
/*    */   extends ClusDistance
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected ClassesAttrType m_Attr;
/*    */   
/*    */   public HierJaccardDistance(ClassesAttrType attr) {
/* 14 */     this.m_Attr = attr;
/*    */   }
/*    */   
/*    */   public double calcDistance(DataTuple t1, DataTuple t2) {
/* 18 */     ClassesTuple cl1 = this.m_Attr.getValue(t1);
/* 19 */     ClassesTuple cl2 = this.m_Attr.getValue(t2);
/*    */     
/* 21 */     System.out.println("Computing Jaccard Distance:");
/* 22 */     System.out.println("Tuple 1: " + cl1.toString());
/* 23 */     System.out.println("Tuple 2: " + cl2.toString());
/*    */     
/* 25 */     return Math.random();
/*    */   }
/*    */   
/*    */   public double calcDistanceToCentroid(DataTuple t1, ClusStatistic s2) {
/* 29 */     return Double.POSITIVE_INFINITY;
/*    */   }
/*    */   
/*    */   public String getDistanceName() {
/* 33 */     return "Hierarchical Jaccard Distance";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierJaccardDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */