/*    */ package jeans.math;
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
/*    */ public class MLong
/*    */   extends MNumber
/*    */ {
/* 27 */   public static final MLong ZERO = new MLong(0L);
/* 28 */   public static final MLong ONE = new MLong(1L);
/*    */   
/*    */   private long value;
/*    */   
/*    */   public MLong(String strg) throws NumberFormatException {
/* 33 */     this(Long.parseLong(strg));
/*    */   }
/*    */   
/*    */   public MLong(long value) {
/* 37 */     this.value = value;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 41 */     return 0;
/*    */   }
/*    */   
/*    */   public MNumber doAdd(MNumber other) {
/* 45 */     return new MLong(this.value + ((MLong)other).value);
/*    */   }
/*    */   
/*    */   public MNumber doSubstract(MNumber other) {
/* 49 */     return new MLong(this.value - ((MLong)other).value);
/*    */   }
/*    */   
/*    */   public MNumber doMultiply(MNumber other) {
/* 53 */     return new MLong(this.value * ((MLong)other).value);
/*    */   }
/*    */   
/*    */   public MNumber doDivide(MNumber other) {
/* 57 */     return new MLong(this.value / ((MLong)other).value);
/*    */   }
/*    */   
/*    */   public MNumber convertTo(MNumber other) {
/* 61 */     if (other instanceof MLong) return this; 
/* 62 */     if (other instanceof MDouble) return new MDouble(this.value); 
/* 63 */     return new MComplex(this, ZERO);
/*    */   }
/*    */   
/*    */   public double getDouble() {
/* 67 */     return this.value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 71 */     return String.valueOf(this.value);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\MLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */