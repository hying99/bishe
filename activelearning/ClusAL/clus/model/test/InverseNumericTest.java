/*    */ package clus.model.test;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.util.ClusRandom;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InverseNumericTest
/*    */   extends NumericTest
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public InverseNumericTest(ClusAttrType attr, double bound, double posfreq) {
/* 41 */     super(attr, bound, posfreq);
/*    */   }
/*    */   
/*    */   public String getString() {
/* 45 */     String value = (this.m_Bound != Double.NEGATIVE_INFINITY) ? NumberFormat.getInstance().format(this.m_Bound) : "?";
/* 46 */     return this.m_Type.getName() + " <= " + value;
/*    */   }
/*    */   
/*    */   public boolean isInverseNumeric() {
/* 50 */     return true;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 54 */     long v = Double.doubleToLongBits(this.m_Bound);
/* 55 */     return this.m_Type.getIndex() + (int)(v ^ v >>> 32L) + 1;
/*    */   }
/*    */   
/*    */   public int numericPredict(double value) {
/* 59 */     if (value == Double.POSITIVE_INFINITY) {
/* 60 */       return (ClusRandom.nextDouble(0) < getPosFreq()) ? 0 : 1;
/*    */     }
/* 62 */     return (value <= this.m_Bound) ? 0 : 1;
/*    */   }
/*    */   
/*    */   public int numericPredictWeighted(double value) {
/* 66 */     if (value == Double.POSITIVE_INFINITY) {
/* 67 */       return hasUnknownBranch() ? 2 : -1;
/*    */     }
/* 69 */     return (value <= this.m_Bound) ? 0 : 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int predictWeighted(DataTuple tuple) {
/* 74 */     double val = this.m_Type.getNumeric(tuple);
/* 75 */     return numericPredictWeighted(val);
/*    */   }
/*    */   
/*    */   public NodeTest getBranchTest(int i) {
/* 79 */     if (i == 0) {
/* 80 */       return this;
/*    */     }
/* 82 */     return new NumericTest((ClusAttrType)this.m_Type, getBound(), 1.0D - getPosFreq());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\test\InverseNumericTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */