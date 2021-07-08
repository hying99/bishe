/*     */ package org.jgap;
/*     */ 
/*     */ import gnu.trove.THashMap;
/*     */ import java.util.Map;
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
/*     */ public abstract class CachedFitnessFunction
/*     */   extends FitnessFunction
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*     */   private Map<String, Double> cachedFitnessValues;
/*     */   
/*     */   public CachedFitnessFunction() {
/*  40 */     this((Map<String, Double>)new THashMap());
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
/*     */   public CachedFitnessFunction(Map<String, Double> cache) {
/*  57 */     this.cachedFitnessValues = cache;
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
/*     */   public final double getFitnessValue(IChromosome a_subject) {
/*  73 */     String businessKey = getBusinessKey(a_subject);
/*  74 */     if (businessKey == null)
/*     */     {
/*     */       
/*  77 */       return super.getFitnessValue(a_subject);
/*     */     }
/*     */ 
/*     */     
/*  81 */     Double fitnessValue = this.cachedFitnessValues.get(businessKey);
/*  82 */     if (fitnessValue != null)
/*     */     {
/*     */       
/*  85 */       return fitnessValue.doubleValue();
/*     */     }
/*     */ 
/*     */     
/*  89 */     double returnValue = super.getFitnessValue(a_subject);
/*     */ 
/*     */     
/*  92 */     this.cachedFitnessValues.put(businessKey, Double.valueOf(returnValue));
/*     */ 
/*     */     
/*  95 */     return returnValue;
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
/*     */   protected String getBusinessKey(IChromosome a_subject) {
/*     */     String result;
/* 109 */     Class<?> clazz = a_subject.getClass();
/* 110 */     if (IBusinessKey.class.isAssignableFrom(clazz)) {
/* 111 */       result = ((IBusinessKey)a_subject).getBusinessKey();
/*     */     }
/* 113 */     else if (IPersistentRepresentation.class.isAssignableFrom(clazz)) {
/* 114 */       result = ((IPersistentRepresentation)a_subject).getPersistentRepresentation();
/*     */     }
/*     */     else {
/*     */       
/* 118 */       result = null;
/*     */     } 
/* 120 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\CachedFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */