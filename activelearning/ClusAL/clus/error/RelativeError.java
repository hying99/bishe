/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RelativeError
/*    */   extends ClusNumericError
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected double[] m_SumRelErr;
/*    */   
/*    */   public RelativeError(ClusErrorList par, NumericAttrType[] num) {
/* 17 */     this(par, num, (ClusAttributeWeights)null, true);
/* 18 */     this.m_SumRelErr = new double[this.m_Dim];
/*    */   }
/*    */   
/*    */   public RelativeError(ClusErrorList par, NumericAttrType[] num, ClusAttributeWeights weights) {
/* 22 */     this(par, num, weights, true);
/* 23 */     this.m_SumRelErr = new double[this.m_Dim];
/*    */   }
/*    */   
/*    */   public RelativeError(ClusErrorList par, NumericAttrType[] num, ClusAttributeWeights weights, boolean printall) {
/* 27 */     super(par, num);
/* 28 */     this.m_SumRelErr = new double[this.m_Dim];
/*    */   }
/*    */ 
/*    */   
/*    */   public void addExample(double[] real, double[] predicted) {
/* 33 */     for (int i = 0; i < this.m_Dim; i++) {
/* 34 */       double err = (real[i] - predicted[i]) / real[i];
/* 35 */       this.m_SumRelErr[i] = this.m_SumRelErr[i] + err;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 41 */     double[] predicted = pred.getNumericPred();
/* 42 */     for (int i = 0; i < this.m_Dim; i++) {
/* 43 */       double err = Math.abs(getAttr(i).getNumeric(tuple) - predicted[i]) / getAttr(i).getNumeric(tuple);
/* 44 */       this.m_SumRelErr[i] = this.m_SumRelErr[i] + err;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addExample(DataTuple real, DataTuple pred) {
/* 49 */     for (int i = 0; i < this.m_Dim; i++) {
/* 50 */       double real_i = getAttr(i).getNumeric(real);
/* 51 */       double predicted_i = getAttr(i).getNumeric(pred);
/* 52 */       double err = Math.abs(real_i - predicted_i) / real_i;
/* 53 */       System.out.println(real_i);
/*    */ 
/*    */       
/* 56 */       this.m_SumRelErr[i] = this.m_SumRelErr[i] + err;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 64 */     return null;
/*    */   }
/*    */   
/*    */   public double getModelErrorComponent(int i) {
/* 68 */     int nb = getNbExamples();
/*    */     
/* 70 */     double err = (nb != 0.0D) ? (this.m_SumRelErr[i] / nb) : 0.0D;
/* 71 */     System.out.println(err);
/*    */     
/* 73 */     return err;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 79 */     return "Relative Error";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\RelativeError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */