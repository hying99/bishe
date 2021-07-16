/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.type.NumericAttrType;
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
/*    */ public abstract class ClusNumericError
/*    */   extends ClusError
/*    */ {
/*    */   protected NumericAttrType[] m_Attrs;
/*    */   
/*    */   public ClusNumericError(ClusErrorList par, NumericAttrType[] num) {
/* 32 */     super(par, num.length);
/* 33 */     this.m_Attrs = num;
/*    */   }
/*    */   
/*    */   public NumericAttrType getAttr(int i) {
/* 37 */     return this.m_Attrs[i];
/*    */   }
/*    */   
/*    */   public ClusNumericError(ClusErrorList par, int nbnum) {
/* 41 */     super(par, nbnum);
/*    */   }
/*    */   
/*    */   public abstract void addExample(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\ClusNumericError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */