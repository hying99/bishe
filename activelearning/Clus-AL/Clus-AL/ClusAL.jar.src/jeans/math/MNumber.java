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
/*    */ 
/*    */ public abstract class MNumber
/*    */ {
/*    */   public abstract int getLevel();
/*    */   
/*    */   public MNumber add(MNumber other) {
/* 30 */     if (other.getLevel() <= getLevel()) {
/* 31 */       MNumber convertedOther = other.convertTo(this);
/* 32 */       return doAdd(convertedOther);
/*    */     } 
/* 34 */     MNumber convertedThis = convertTo(other);
/* 35 */     return convertedThis.doAdd(other);
/*    */   }
/*    */ 
/*    */   
/*    */   public MNumber substract(MNumber other) {
/* 40 */     if (other.getLevel() <= getLevel()) {
/* 41 */       MNumber convertedOther = other.convertTo(this);
/* 42 */       return doSubstract(convertedOther);
/*    */     } 
/* 44 */     MNumber convertedThis = convertTo(other);
/* 45 */     return convertedThis.doSubstract(other);
/*    */   }
/*    */ 
/*    */   
/*    */   public MNumber multiply(MNumber other) {
/* 50 */     if (other.getLevel() <= getLevel()) {
/* 51 */       MNumber convertedOther = other.convertTo(this);
/* 52 */       return doMultiply(convertedOther);
/*    */     } 
/* 54 */     MNumber convertedThis = convertTo(other);
/* 55 */     return convertedThis.doMultiply(other);
/*    */   }
/*    */ 
/*    */   
/*    */   public MNumber divide(MNumber other) {
/* 60 */     if (other.getLevel() <= getLevel()) {
/* 61 */       MNumber convertedOther = other.convertTo(this);
/* 62 */       return doDivide(convertedOther);
/*    */     } 
/* 64 */     MNumber convertedThis = convertTo(other);
/* 65 */     return convertedThis.doDivide(other);
/*    */   }
/*    */   
/*    */   public abstract MNumber doAdd(MNumber paramMNumber);
/*    */   
/*    */   public abstract MNumber doSubstract(MNumber paramMNumber);
/*    */   
/*    */   public abstract MNumber doMultiply(MNumber paramMNumber);
/*    */   
/*    */   public abstract MNumber doDivide(MNumber paramMNumber);
/*    */   
/*    */   public abstract MNumber convertTo(MNumber paramMNumber);
/*    */   
/*    */   public abstract double getDouble();
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\MNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */