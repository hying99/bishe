/*    */ package clus.model.test;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.NumericAttrType;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusRuleConstraintInduceTest
/*    */   extends NodeTest
/*    */ {
/*    */   protected double m_Bound;
/*    */   protected NumericAttrType m_Type;
/*    */   protected boolean smallerThan;
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public ClusRuleConstraintInduceTest(ClusAttrType attr, double bound, boolean test) {
/* 18 */     this.m_Type = (NumericAttrType)attr;
/* 19 */     this.m_Bound = bound;
/* 20 */     setArity(2);
/* 21 */     this.smallerThan = test;
/*    */   }
/*    */   
/*    */   public boolean isSmallerThanTest() {
/* 25 */     return this.smallerThan;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isInverseNumeric() {
/* 31 */     return false;
/*    */   }
/*    */   
/*    */   public final int getAttrIndex() {
/* 35 */     return this.m_Type.getArrayIndex();
/*    */   }
/*    */   
/*    */   public final NumericAttrType getNumType() {
/* 39 */     return this.m_Type;
/*    */   }
/*    */   
/*    */   public final double getBound() {
/* 43 */     return this.m_Bound;
/*    */   }
/*    */   
/*    */   public final void setBound(double bound) {
/* 47 */     this.m_Bound = bound;
/*    */   }
/*    */   
/*    */   public ClusAttrType getType() {
/* 51 */     return (ClusAttrType)this.m_Type;
/*    */   }
/*    */   
/*    */   public void setType(ClusAttrType type) {
/* 55 */     this.m_Type = (NumericAttrType)type;
/*    */   }
/*    */   
/*    */   public String getString() {
/* 59 */     String value = (this.m_Bound != Double.NEGATIVE_INFINITY) ? String.valueOf(this.m_Bound) : "?";
/* 60 */     if (this.smallerThan) {
/* 61 */       return this.m_Type.getName() + " < " + value;
/*    */     }
/* 63 */     return this.m_Type.getName() + " >= " + value;
/*    */   }
/*    */   
/*    */   public boolean hasConstants() {
/* 67 */     return (this.m_Bound != Double.NEGATIVE_INFINITY);
/*    */   }
/*    */   
/*    */   public boolean equals(NodeTest test) {
/* 71 */     if (this.m_Type != test.getType()) return false; 
/* 72 */     ClusRuleConstraintInduceTest ntest = (ClusRuleConstraintInduceTest)test;
/* 73 */     return (this.m_Bound == ntest.m_Bound && ntest.smallerThan == this.smallerThan);
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 77 */     long v = Double.doubleToLongBits(this.m_Bound);
/* 78 */     return this.m_Type.getIndex() + (int)(v ^ v >>> 32L);
/*    */   }
/*    */   
/*    */   public int numericPredictWeighted(double value) {
/* 82 */     if (value == Double.POSITIVE_INFINITY) {
/* 83 */       return hasUnknownBranch() ? 2 : -1;
/*    */     }
/* 85 */     return ((value < this.m_Bound && this.smallerThan) || (value >= this.m_Bound && !this.smallerThan)) ? 0 : 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int predictWeighted(DataTuple tuple) {
/* 90 */     double val = this.m_Type.getNumeric(tuple);
/* 91 */     return numericPredictWeighted(val);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\test\ClusRuleConstraintInduceTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */