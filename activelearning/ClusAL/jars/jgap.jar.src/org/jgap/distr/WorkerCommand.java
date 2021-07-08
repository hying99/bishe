/*    */ package org.jgap.distr;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.TimeZone;
/*    */ import org.jgap.util.CommandResult;
/*    */ import org.jgap.util.ICommand;
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
/*    */ 
/*    */ public class WorkerCommand
/*    */   implements ICommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*    */   private String m_name;
/*    */   private long m_timeCreated;
/*    */   
/*    */   public WorkerCommand(String a_name) {
/* 39 */     this.m_name = a_name;
/* 40 */     this.m_timeCreated = getCurrentMilliseconds();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommandResult execute(Object a_parameters) throws Exception {
/* 47 */     return null;
/*    */   }
/*    */   
/*    */   private static long getCurrentMilliseconds() {
/* 51 */     Calendar cal = Calendar.getInstance(TimeZone.getDefault());
/* 52 */     return cal.getTimeInMillis();
/*    */   }
/*    */   
/*    */   public String getName() {
/* 56 */     return this.m_name;
/*    */   }
/*    */   
/*    */   public long getAgeMillis() {
/* 60 */     return getCurrentMilliseconds() - this.m_timeCreated;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\WorkerCommand.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */