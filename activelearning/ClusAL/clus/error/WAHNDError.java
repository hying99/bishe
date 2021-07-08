/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.ClusData;
/*    */ import clus.data.rows.DataTuple;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WAHNDError
/*    */   extends ClusError
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected double m_Weight;
/*    */   protected double m_TreeErr;
/*    */   protected double m_SumWeight;
/*    */   
/*    */   public WAHNDError(ClusErrorList par, double weight) {
/* 44 */     super(par, 0);
/* 45 */     this.m_Weight = weight;
/*    */   }
/*    */   
/*    */   public void add(ClusError other) {
/* 49 */     WAHNDError err = (WAHNDError)other;
/* 50 */     this.m_TreeErr += err.m_TreeErr;
/* 51 */     this.m_SumWeight += err.m_SumWeight;
/*    */   }
/*    */   
/*    */   public void showModelError(PrintWriter out, int detail) {
/* 55 */     out.println(this.m_TreeErr / this.m_SumWeight);
/*    */   }
/*    */   
/*    */   public void addExample(ClusData data, int idx, ClusStatistic pred) {
/* 59 */     System.out.println("WAHNDError: addExample/3 not implemented");
/*    */   }
/*    */   
/*    */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 63 */     double weight = tuple.getWeight();
/*    */ 
/*    */     
/* 66 */     this.m_SumWeight += weight;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addInvalid(DataTuple tuple) {}
/*    */   
/*    */   public double getModelError() {
/* 73 */     return this.m_TreeErr / this.m_SumWeight;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 77 */     this.m_TreeErr = 0.0D;
/* 78 */     this.m_SumWeight = 0.0D;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 82 */     return "WAHND RE with parameter " + this.m_Weight;
/*    */   }
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 86 */     return new WAHNDError(par, this.m_Weight);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\WAHNDError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */