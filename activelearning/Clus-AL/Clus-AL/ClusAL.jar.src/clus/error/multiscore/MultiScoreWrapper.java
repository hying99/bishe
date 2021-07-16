/*    */ package clus.error.multiscore;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import clus.error.ClusError;
/*    */ import clus.error.ClusErrorList;
/*    */ import clus.error.ClusNominalError;
/*    */ import clus.error.ClusNumericError;
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
/*    */ public class MultiScoreWrapper
/*    */   extends ClusNumericError
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected ClusNominalError m_Child;
/*    */   protected byte[] m_Real;
/*    */   protected int[] m_Pred;
/*    */   
/*    */   public MultiScoreWrapper(ClusNominalError child, NumericAttrType[] num) {
/* 42 */     super(child.getParent(), num);
/* 43 */     int dim = getDimension();
/* 44 */     this.m_Real = new byte[dim];
/* 45 */     this.m_Pred = new int[dim];
/* 46 */     this.m_Child = child;
/*    */   }
/*    */   
/*    */   public boolean shouldBeLow() {
/* 50 */     return this.m_Child.shouldBeLow();
/*    */   }
/*    */   
/*    */   public void reset() {
/* 54 */     this.m_Child.reset();
/*    */   }
/*    */   
/*    */   public double getModelError() {
/* 58 */     return this.m_Child.getModelError();
/*    */   }
/*    */   
/*    */   public void addExample(double[] real, double[] predicted) {
/* 62 */     for (int i = 0; i < this.m_Real.length; i++) {
/* 63 */       this.m_Real[i] = (byte)((real[i] > 0.5D) ? 0 : 1);
/* 64 */       this.m_Pred[i] = (predicted[i] > 0.5D) ? 0 : 1;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addInvalid(DataTuple tuple) {}
/*    */ 
/*    */   
/*    */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 74 */     for (int i = 0; i < this.m_Dim; i++);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(ClusError other) {
/* 81 */     MultiScoreWrapper oe = (MultiScoreWrapper)other;
/* 82 */     this.m_Child.add((ClusError)oe.m_Child);
/*    */   }
/*    */   
/*    */   public void showModelError(PrintWriter out, int detail) {
/* 86 */     this.m_Child.showModelError(out, detail);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 94 */     return this.m_Child.getName();
/*    */   }
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 98 */     return (ClusError)new MultiScoreWrapper((ClusNominalError)this.m_Child.getErrorClone(par), this.m_Attrs);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\multiscore\MultiScoreWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */