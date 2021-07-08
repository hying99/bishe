/*    */ package org.jgap.gp.impl;
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
/*    */ public class GPProgramInfo
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private double m_fitnessValue;
/*    */   private String m_toStringNorm;
/*    */   private boolean m_found;
/*    */   
/*    */   public GPProgramInfo(GPProgram a_prog, boolean a_found) {
/* 30 */     this.m_fitnessValue = a_prog.getFitnessValueDirectly();
/* 31 */     this.m_toStringNorm = a_prog.toStringNorm(0);
/* 32 */     this.m_found = a_found;
/*    */   }
/*    */   
/*    */   public String getToStringNorm() {
/* 36 */     return this.m_toStringNorm;
/*    */   }
/*    */   
/*    */   public double getFitnessValue() {
/* 40 */     return this.m_fitnessValue;
/*    */   }
/*    */   
/*    */   public boolean isFound() {
/* 44 */     return this.m_found;
/*    */   }
/*    */   
/*    */   public void setFound(boolean a_found) {
/* 48 */     this.m_found = a_found;
/*    */   }
/*    */   
/*    */   public boolean equals(Object a_other) {
/* 52 */     GPProgramInfo other = (GPProgramInfo)a_other;
/* 53 */     if (this.m_toStringNorm == null) {
/* 54 */       if (other.m_toStringNorm == null) {
/* 55 */         return true;
/*    */       }
/* 57 */       return false;
/*    */     } 
/* 59 */     return this.m_toStringNorm.equals(other.m_toStringNorm);
/*    */   }
/*    */   
/*    */   public int compareTo(Object a_other) {
/* 63 */     GPProgramInfo other = (GPProgramInfo)a_other;
/* 64 */     if (this.m_toStringNorm == null && 
/* 65 */       other.m_toStringNorm == null) {
/* 66 */       return 0;
/*    */     }
/*    */     
/* 69 */     return this.m_toStringNorm.compareTo(other.m_toStringNorm);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\GPProgramInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */