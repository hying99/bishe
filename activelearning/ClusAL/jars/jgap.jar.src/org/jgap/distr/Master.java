/*     */ package org.jgap.distr;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.jgap.util.NetworkKit;
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
/*     */ public abstract class Master
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*     */   private MasterInfo m_masterinfo;
/*     */   private RequestDispatcher m_dispatcher;
/*     */   private WorkerListener m_workerListener;
/*     */   
/*     */   public Master(RequestDispatcher a_dispatcher, WorkerListener a_workerListener) throws Exception {
/*  54 */     this.m_dispatcher = a_dispatcher;
/*  55 */     this.m_workerListener = a_workerListener;
/*  56 */     this.m_masterinfo = new MasterInfo();
/*  57 */     this.m_masterinfo.m_IPAddress = NetworkKit.getLocalIPAddress();
/*  58 */     this.m_masterinfo.m_name = NetworkKit.getLocalHostName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void start() throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/*  76 */     this.m_workerListener.stop();
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
/*     */   public MasterInfo getMasterInfo() {
/*  90 */     return this.m_masterinfo;
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
/*     */   public void sendToWorker(IWorker a_worker, WorkerCommand a_command) throws IOException {
/* 103 */     this.m_dispatcher.dispatch(a_worker, a_command);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestDispatcher getDispatcher() {
/* 114 */     return this.m_dispatcher;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WorkerListener getWorkerListener() {
/* 124 */     return this.m_workerListener;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\Master.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */