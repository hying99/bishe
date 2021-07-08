/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.statistic.ClusDistance;
/*    */ import clus.statistic.ClusStatistic;
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
/*    */ public class AvgDistancesError
/*    */   extends ClusError
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected double m_SumErr;
/*    */   protected ClusDistance m_Distance;
/*    */   
/*    */   public AvgDistancesError(ClusErrorList par, ClusDistance dist) {
/* 40 */     super(par);
/* 41 */     this.m_Distance = dist;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 45 */     this.m_SumErr = 0.0D;
/*    */   }
/*    */   
/*    */   public void add(ClusError other) {
/* 49 */     AvgDistancesError oe = (AvgDistancesError)other;
/* 50 */     this.m_SumErr += oe.m_SumErr;
/*    */   }
/*    */   
/*    */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 54 */     this.m_SumErr += this.m_Distance.calcDistanceToCentroid(tuple, pred);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double getModelErrorAdditive() {
/* 60 */     return this.m_SumErr;
/*    */   }
/*    */   
/*    */   public double getModelError() {
/* 64 */     return getModelErrorComponent(0);
/*    */   }
/*    */   
/*    */   public boolean shouldBeLow() {
/* 68 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addInvalid(DataTuple tuple) {}
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 75 */     return new AvgDistancesError(par, this.m_Distance);
/*    */   }
/*    */   
/*    */   public void showModelError(PrintWriter wrt, int detail) {
/* 79 */     StringBuffer res = new StringBuffer();
/* 80 */     res.append(String.valueOf(getModelError()));
/* 81 */     wrt.println(res.toString());
/*    */   }
/*    */   
/*    */   public String getName() {
/* 85 */     return "AvgDistancesError";
/*    */   }
/*    */   
/*    */   public double getModelErrorComponent(int i) {
/* 89 */     int nb = getNbExamples();
/* 90 */     double err = (nb != 0) ? (this.m_SumErr / nb) : 0.0D;
/* 91 */     return err;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\AvgDistancesError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */