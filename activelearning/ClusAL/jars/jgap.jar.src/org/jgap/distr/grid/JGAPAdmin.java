/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.homedns.dade.jcgrid.WorkerStats;
/*    */ import org.homedns.dade.jcgrid.admin.GridAdmin;
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
/*    */ public class JGAPAdmin
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*    */   
/*    */   public JGAPAdmin() throws Exception {
/* 29 */     GridAdmin admin = new GridAdmin();
/*    */     
/* 31 */     admin.start();
/*    */     while (true) {
/* 33 */       List v = admin.getWorkerStats();
/* 34 */       System.out.println("Number of workers: " + v.size());
/* 35 */       Iterator<WorkerStats> it = v.iterator();
/* 36 */       while (it.hasNext()) {
/* 37 */         WorkerStats stat = it.next();
/* 38 */         System.out.println(" " + stat.getName() + " / " + stat.getWorkingFor() + " / " + stat.getStatus() + " / " + stat.getUnitSec());
/*    */       } 
/*    */       
/* 41 */       Thread.sleep(1000L);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 49 */     new JGAPAdmin();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPAdmin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */