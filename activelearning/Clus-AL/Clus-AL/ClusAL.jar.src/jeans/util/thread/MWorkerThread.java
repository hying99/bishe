/*     */ package jeans.util.thread;
/*     */ 
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
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
/*     */ public class MWorkerThread
/*     */   extends Thread
/*     */ {
/*     */   protected static MWorkerThread m_Instance;
/*  31 */   protected ArrayList m_Jobs = new ArrayList();
/*     */   
/*     */   protected boolean m_Running;
/*     */   
/*     */   protected boolean m_Terminate;
/*     */   protected ActionListener m_Idle;
/*     */   
/*     */   public void setIdleListener(ActionListener idle) {
/*  39 */     this.m_Idle = idle;
/*     */   }
/*     */   
/*     */   public static MWorkerThread getInstance() {
/*  43 */     if (m_Instance == null) m_Instance = new MWorkerThread(); 
/*  44 */     return m_Instance;
/*     */   }
/*     */   
/*     */   public synchronized void executeOverwrite(Runnable runner) {
/*  48 */     executeOverwrite(runner, true);
/*     */   }
/*     */   
/*     */   public synchronized void executeOverwrite(Runnable runner, boolean newThread) {
/*  52 */     if (this.m_Running) {
/*  53 */       Class<?> cls = runner.getClass();
/*  54 */       for (int i = this.m_Jobs.size() - 1; i >= 0; i--) {
/*  55 */         if (this.m_Jobs.get(i).getClass() == cls) this.m_Jobs.remove(i); 
/*     */       } 
/*     */     } 
/*  58 */     execute(runner, newThread);
/*     */   }
/*     */   
/*     */   public synchronized void execute(Runnable runner) {
/*  62 */     execute(runner, true);
/*     */   }
/*     */   
/*     */   public synchronized void execute(Runnable runner, boolean newThread) {
/*  66 */     if (newThread) {
/*  67 */       if (this.m_Running) {
/*  68 */         this.m_Jobs.add(runner);
/*  69 */         if (this.m_Jobs.size() == 1) notify(); 
/*     */       } else {
/*  71 */         this.m_Jobs.add(runner);
/*  72 */         this.m_Running = true;
/*  73 */         start();
/*     */       } 
/*     */     } else {
/*  76 */       doRunJob(runner);
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized Runnable getNextJob() throws InterruptedException {
/*  81 */     if (this.m_Jobs.size() == 0) {
/*  82 */       this.m_Idle.actionPerformed(null);
/*  83 */       wait();
/*     */     } 
/*  85 */     Runnable job = this.m_Jobs.get(0);
/*  86 */     this.m_Jobs.remove(0);
/*  87 */     return job;
/*     */   }
/*     */   
/*     */   public void doRunJob(Runnable job) {
/*     */     try {
/*  92 */       job.run();
/*  93 */     } catch (Exception e) {
/*  94 */       System.out.println("Exception: " + e);
/*  95 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void run() {
/*     */     try {
/* 101 */       while (!this.m_Terminate) {
/* 102 */         Runnable job = getNextJob();
/* 103 */         doRunJob(job);
/*     */       } 
/* 105 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\thread\MWorkerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */