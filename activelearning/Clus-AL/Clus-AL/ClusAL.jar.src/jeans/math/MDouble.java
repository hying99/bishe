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
/*    */ public class MDouble
/*    */   extends MNumber
/*    */ {
/*    */   private double value;
/*    */   
/*    */   public MDouble(String strg) throws NumberFormatException {
/* 30 */     this(Double.parseDouble(strg));
/*    */   }
/*    */   
/*    */   public MDouble(double value) {
/* 34 */     this.value = value;
/*    */   }
/*    */   
/*    */   public MDouble() {
/* 38 */     this.value = 0.0D;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 42 */     return 2;
/*    */   }
/*    */   
/*    */   public static boolean isDouble(String str) {
/* 46 */     if (str.length() == 0) return false; 
/* 47 */     int ch = str.charAt(0);
/* 48 */     if ((ch < 48 || ch > 57) && ch != 46 && ch != 43 && ch != 45) return false; 
/* 49 */     for (int i = 1; i < str.length(); i++) {
/* 50 */       ch = str.charAt(i);
/* 51 */       if ((ch < 48 || ch > 57) && ch != 46 && ch != 43 && ch != 45 && ch != 101 && ch != 69) return false; 
/*    */     } 
/* 53 */     return true;
/*    */   }
/*    */   
/*    */   public MNumber doAdd(MNumber other) {
/* 57 */     return new MDouble(this.value + ((MDouble)other).value);
/*    */   }
/*    */   
/*    */   public MNumber doSubstract(MNumber other) {
/* 61 */     return new MDouble(this.value - ((MDouble)other).value);
/*    */   }
/*    */   
/*    */   public MNumber doMultiply(MNumber other) {
/* 65 */     return new MDouble(this.value * ((MDouble)other).value);
/*    */   }
/*    */   
/*    */   public MNumber doDivide(MNumber other) {
/* 69 */     return new MDouble(this.value / ((MDouble)other).value);
/*    */   }
/*    */   
/*    */   public MNumber convertTo(MNumber other) {
/* 73 */     if (other instanceof MDouble) return this; 
/* 74 */     return new MComplex(this, MLong.ZERO);
/*    */   }
/*    */   
/*    */   public void addDouble(double add) {
/* 78 */     this.value += add;
/*    */   }
/*    */   
/*    */   public double getDouble() {
/* 82 */     return this.value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 86 */     return String.valueOf(this.value);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\MDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */