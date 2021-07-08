/*    */ package org.jgap.supergenes;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.Gene;
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
/*    */ public abstract class Validator
/*    */   implements SupergeneValidator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*    */   private transient Configuration m_conf;
/*    */   
/*    */   public Validator(Configuration a_conf) {
/* 29 */     this.m_conf = a_conf;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract boolean isValid(Gene[] paramArrayOfGene, Supergene paramSupergene);
/*    */ 
/*    */   
/*    */   public String getPersistent() {
/* 38 */     return "";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setFromPersistent(String a_from) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Configuration getConfiguration() {
/* 53 */     return this.m_conf;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\supergenes\Validator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */