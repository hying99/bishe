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
/*    */ public class MComplex
/*    */   extends MNumber
/*    */ {
/*    */   private MNumber real;
/*    */   private MNumber imag;
/*    */   
/*    */   public MComplex(MNumber real, MNumber imag) {
/* 30 */     this.real = real;
/* 31 */     this.imag = imag;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 35 */     return 3;
/*    */   }
/*    */   
/*    */   public MNumber doAdd(MNumber other) {
/* 39 */     MNumber myreal = this.real.add(((MComplex)other).real);
/* 40 */     MNumber myimag = this.imag.add(((MComplex)other).imag);
/* 41 */     return new MComplex(myreal, myimag);
/*    */   }
/*    */   
/*    */   public MNumber doSubstract(MNumber other) {
/* 45 */     MNumber myreal = this.real.substract(((MComplex)other).real);
/* 46 */     MNumber myimag = this.imag.substract(((MComplex)other).imag);
/* 47 */     return new MComplex(myreal, myimag);
/*    */   }
/*    */   
/*    */   public MNumber doMultiply(MNumber other) {
/* 51 */     MNumber oreal = ((MComplex)other).real;
/* 52 */     MNumber oimag = ((MComplex)other).imag;
/* 53 */     MNumber myreal = this.real.multiply(oreal).substract(this.imag.multiply(oimag));
/* 54 */     MNumber myimag = this.real.multiply(oimag).add(this.imag.multiply(oreal));
/* 55 */     return new MComplex(myreal, myimag);
/*    */   }
/*    */   
/*    */   public MNumber doDivide(MNumber other) {
/* 59 */     MNumber oreal = ((MComplex)other).real;
/* 60 */     MNumber oimag = ((MComplex)other).imag;
/* 61 */     MNumber myreal = this.real.multiply(oreal).add(this.imag.multiply(oimag));
/* 62 */     MNumber myimag = this.imag.multiply(oreal).substract(this.real.multiply(oimag));
/* 63 */     MNumber denom = oreal.multiply(oreal).add(oimag.multiply(oimag));
/* 64 */     return new MComplex(myreal.divide(denom), myimag.divide(denom));
/*    */   }
/*    */   
/*    */   public MNumber convertTo(MNumber other) {
/* 68 */     return this;
/*    */   }
/*    */   
/*    */   public double getDouble() {
/* 72 */     return 0.0D;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 76 */     return String.valueOf(this.real) + "+" + String.valueOf(this.imag) + "i";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\MComplex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */