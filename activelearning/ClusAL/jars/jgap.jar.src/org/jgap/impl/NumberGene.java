/*     */ package org.jgap.impl;
/*     */ 
/*     */ import org.jgap.BaseGene;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class NumberGene
/*     */   extends BaseGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.23 $";
/*     */   private Object m_value;
/*     */   
/*     */   public NumberGene(Configuration a_config) throws InvalidConfigurationException {
/*  32 */     super(a_config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Object a_other) {
/*  51 */     NumberGene otherGene = (NumberGene)a_other;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  56 */     if (otherGene == null) {
/*  57 */       return 1;
/*     */     }
/*  59 */     if (otherGene.m_value == null) {
/*     */ 
/*     */ 
/*     */       
/*  63 */       if (!otherGene.getClass().equals(getClass())) {
/*  64 */         throw new ClassCastException("Comparison not possible: different types!");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  70 */       if (this.m_value == null) {
/*  71 */         if (isCompareApplicationData()) {
/*  72 */           return compareApplicationData(getApplicationData(), otherGene.getApplicationData());
/*     */         }
/*     */ 
/*     */         
/*  76 */         return 0;
/*     */       } 
/*     */ 
/*     */       
/*  80 */       return 1;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  85 */       if (!otherGene.getClass().equals(getClass())) {
/*  86 */         throw new ClassCastException("Comparison not possible: different types!");
/*     */       }
/*     */       
/*  89 */       if (this.m_value == null) {
/*  90 */         return -1;
/*     */       }
/*  92 */       int res = compareToNative(this.m_value, otherGene.m_value);
/*  93 */       if (res == 0) {
/*  94 */         if (isCompareApplicationData()) {
/*  95 */           return compareApplicationData(getApplicationData(), otherGene.getApplicationData());
/*     */         }
/*     */ 
/*     */         
/*  99 */         return 0;
/*     */       } 
/*     */ 
/*     */       
/* 103 */       return res;
/*     */     
/*     */     }
/* 106 */     catch (ClassCastException e) {
/* 107 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract int compareToNative(Object paramObject1, Object paramObject2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllele(Object a_newValue) {
/* 139 */     if (getConstraintChecker() != null && 
/* 140 */       !getConstraintChecker().verify((Gene)this, a_newValue, null, -1)) {
/*     */       return;
/*     */     }
/*     */     
/* 144 */     this.m_value = a_newValue;
/*     */ 
/*     */ 
/*     */     
/* 148 */     mapValueToWithinBounds();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void mapValueToWithinBounds();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getInternalValue() {
/* 166 */     return this.m_value;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\NumberGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */