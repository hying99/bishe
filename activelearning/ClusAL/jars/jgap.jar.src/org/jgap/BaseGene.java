/*     */ package org.jgap;
/*     */ 
/*     */ import org.jgap.util.StringKit;
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
/*     */ public abstract class BaseGene
/*     */   implements Gene, IBusinessKey
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.27 $";
/*     */   public static final double DELTA = 1.0E-7D;
/*     */   private double m_energy;
/*     */   private Object m_applicationData;
/*     */   private boolean m_compareAppData;
/*     */   private Configuration m_configuration;
/*     */   public static final String S_APPLICATION_DATA = "Application data";
/*     */   private IGeneConstraintChecker m_geneAlleleChecker;
/*     */   
/*     */   public BaseGene(Configuration a_configuration) throws InvalidConfigurationException {
/*  69 */     if (a_configuration == null) {
/*  70 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*     */     }
/*  72 */     this.m_configuration = a_configuration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getAllele() {
/*  82 */     return getInternalValue();
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
/*     */   public int hashCode() {
/* 100 */     if (getInternalValue() == null) {
/* 101 */       return -79;
/*     */     }
/*     */     
/* 104 */     return getInternalValue().hashCode();
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
/*     */   public void cleanup() {}
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
/*     */   public String toString() {
/*     */     String appData;
/* 132 */     if (getInternalValue() == null) {
/* 133 */       representation = "null";
/*     */     } else {
/*     */       
/* 136 */       representation = getInternalValue().toString();
/*     */     } 
/*     */     
/* 139 */     if (getApplicationData() != null) {
/* 140 */       appData = getApplicationData().toString();
/*     */     } else {
/*     */       
/* 143 */       appData = "null";
/*     */     } 
/* 145 */     String representation = representation + ", Application data:" + appData;
/* 146 */     return representation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 157 */     return 1;
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 173 */       int result = compareTo((T)a_other);
/* 174 */       if (result == 0) {
/* 175 */         if (isCompareApplicationData()) {
/* 176 */           Gene otherGene = (Gene)a_other;
/* 177 */           int resultAppData = compareApplicationData(getApplicationData(), otherGene.getApplicationData());
/*     */           
/* 179 */           return (resultAppData == 0);
/*     */         } 
/*     */         
/* 182 */         return true;
/*     */       } 
/*     */ 
/*     */       
/* 186 */       return false;
/*     */     }
/* 188 */     catch (ClassCastException e) {
/*     */ 
/*     */ 
/*     */       
/* 192 */       return false;
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
/*     */   protected abstract Object getInternalValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEnergy() {
/* 214 */     return this.m_energy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnergy(double a_energy) {
/* 225 */     this.m_energy = a_energy;
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
/*     */   public void setApplicationData(Object a_newData) {
/* 241 */     this.m_applicationData = a_newData;
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
/*     */   public Object getApplicationData() {
/* 257 */     return this.m_applicationData;
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
/*     */   public void setCompareApplicationData(boolean a_doCompare) {
/* 271 */     this.m_compareAppData = a_doCompare;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompareApplicationData() {
/* 281 */     return this.m_compareAppData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int compareApplicationData(Object a_appdata1, Object a_appdata2) {
/* 288 */     if (a_appdata1 == null) {
/* 289 */       if (a_appdata2 != null) {
/* 290 */         return -1;
/*     */       }
/*     */       
/* 293 */       return 0;
/*     */     } 
/*     */     
/* 296 */     if (a_appdata2 == null) {
/* 297 */       return 1;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 303 */     ICompareToHandler handler = getConfiguration().getJGAPFactory().getCompareToHandlerFor(a_appdata1, a_appdata2.getClass());
/*     */     
/* 305 */     if (handler != null) {
/*     */       try {
/* 307 */         return ((Integer)handler.perform(a_appdata1, null, a_appdata2)).intValue();
/*     */       }
/* 309 */       catch (Exception ex) {
/* 310 */         throw new Error(ex);
/*     */       } 
/*     */     }
/*     */     
/* 314 */     return 0;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConstraintChecker(IGeneConstraintChecker a_constraintChecker) {
/* 338 */     this.m_geneAlleleChecker = a_constraintChecker;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGeneConstraintChecker getConstraintChecker() {
/* 349 */     return this.m_geneAlleleChecker;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Gene newGene() {
/* 371 */     Gene result = newGeneInternal();
/* 372 */     result.setConstraintChecker(getConstraintChecker());
/* 373 */     result.setEnergy(getEnergy());
/*     */     
/* 375 */     result.setApplicationData(getApplicationData());
/* 376 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Gene newGeneInternal();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Configuration getConfiguration() {
/* 388 */     return this.m_configuration;
/*     */   }
/*     */   
/*     */   public String getBusinessKey() {
/* 392 */     Object allele = getAllele();
/* 393 */     String result = getClass().getName() + ":";
/* 394 */     if (allele == null) {
/* 395 */       return result;
/*     */     }
/* 397 */     return result + allele.toString();
/*     */   }
/*     */   
/*     */   protected String encode(String a_string) {
/* 401 */     return StringKit.encode(a_string);
/*     */   }
/*     */   
/*     */   protected String decode(String a_string) {
/* 405 */     return StringKit.decode(a_string);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\BaseGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */