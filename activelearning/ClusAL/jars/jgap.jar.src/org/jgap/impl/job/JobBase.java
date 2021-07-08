/*    */ package org.jgap.impl.job;
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
/*    */ public abstract class JobBase
/*    */   implements IJob
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*    */   private JobData m_data;
/*    */   private boolean m_finished;
/*    */   private JobResult m_result;
/*    */   
/*    */   public JobBase(JobData a_data) {
/* 30 */     this.m_data = a_data;
/*    */   }
/*    */   
/*    */   public void run() {
/*    */     try {
/* 35 */       this.m_result = execute(this.m_data);
/* 36 */       if (this.m_result == null) {
/* 37 */         throw new IllegalStateException("Result must not be null!");
/*    */       }
/* 39 */     } catch (Exception ex) {
/*    */       
/* 41 */       ex.printStackTrace();
/* 42 */       throw new RuntimeException("Job failed");
/*    */     } 
/* 44 */     setFinished();
/*    */   }
/*    */   
/*    */   public JobData getJobData() {
/* 48 */     return this.m_data;
/*    */   }
/*    */   
/*    */   public boolean isFinished() {
/* 52 */     return this.m_finished;
/*    */   }
/*    */   
/*    */   protected void setFinished() {
/* 56 */     this.m_finished = true;
/*    */   }
/*    */   
/*    */   public JobResult getResult() {
/* 60 */     if (!this.m_finished) {
/* 61 */       return null;
/*    */     }
/* 63 */     if (this.m_result == null) {
/* 64 */       throw new IllegalStateException("Result must not be null!");
/*    */     }
/* 66 */     return this.m_result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\JobBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */