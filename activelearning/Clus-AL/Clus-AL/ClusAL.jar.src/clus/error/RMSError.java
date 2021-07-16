/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import java.io.PrintWriter;
/*    */ import java.text.NumberFormat;
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
/*    */ public class RMSError
/*    */   extends MSError
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public RMSError(ClusErrorList par, NumericAttrType[] num) {
/* 37 */     super(par, num);
/*    */   }
/*    */   
/*    */   public RMSError(ClusErrorList par, NumericAttrType[] num, ClusAttributeWeights weights) {
/* 41 */     super(par, num, weights);
/*    */   }
/*    */   
/*    */   public RMSError(ClusErrorList par, NumericAttrType[] num, ClusAttributeWeights weights, boolean printall) {
/* 45 */     super(par, num, weights, printall);
/*    */   }
/*    */   
/*    */   public double getModelError() {
/* 49 */     return Math.sqrt(super.getModelError());
/*    */   }
/*    */   
/*    */   public double getModelErrorComponent(int i) {
/* 53 */     return Math.sqrt(super.getModelErrorComponent(i));
/*    */   }
/*    */   
/*    */   public void showSummaryError(PrintWriter out, boolean detail) {
/* 57 */     NumberFormat fr = getFormat();
/* 58 */     out.println(getPrefix() + "Mean over components RMSE: " + fr.format(getModelError()));
/*    */   }
/*    */   
/*    */   public String getName() {
/* 62 */     if (this.m_Weights == null) return "Root mean squared error (RMSE)"; 
/* 63 */     return "Weighted root mean squared error (RMSE) (" + this.m_Weights.getName((ClusAttrType[])this.m_Attrs) + ")";
/*    */   }
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 67 */     return new RMSError(par, this.m_Attrs, this.m_Weights, this.m_PrintAllComps);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\RMSError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */