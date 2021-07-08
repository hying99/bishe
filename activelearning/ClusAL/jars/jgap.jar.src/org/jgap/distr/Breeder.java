/*     */ package org.jgap.distr;
/*     */ 
/*     */ import org.jgap.Genotype;
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
/*     */ public abstract class Breeder
/*     */   implements Runnable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.13 $";
/*     */   private Breeder m_master;
/*     */   private Breeder[] m_workers;
/*     */   private Genotype m_genotype;
/*     */   private IPopulationMerger m_populationMerger;
/*     */   private transient boolean m_running;
/*     */   private transient boolean m_stopped = true;
/*  54 */   private transient MeanBuffer m_meanBuffer = new MeanBuffer(40);
/*     */ 
/*     */   
/*     */   public Breeder(IPopulationMerger a_populationMerger) {
/*  58 */     this.m_populationMerger = a_populationMerger;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  69 */       this.m_stopped = false;
/*  70 */       while (this.m_running) {
/*  71 */         evalOneGeneration();
/*  72 */         int sleepTime = this.m_meanBuffer.mean() / 100;
/*  73 */         if (sleepTime <= 0) {
/*  74 */           pause(1);
/*     */           continue;
/*     */         } 
/*  77 */         pause(sleepTime);
/*     */       } 
/*     */       
/*  80 */       this.m_stopped = true;
/*     */     }
/*  82 */     catch (Throwable t) {
/*  83 */       this.m_stopped = true;
/*  84 */       this.m_running = false;
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
/*     */   private void evalOneGeneration() throws Exception {
/* 101 */     long begin = System.currentTimeMillis();
/* 102 */     this.m_genotype.evolve(1);
/* 103 */     informParent();
/* 104 */     this.m_meanBuffer.add((int)(System.currentTimeMillis() - begin));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void informParent() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void pause(int a_milliSec) {
/*     */     try {
/* 121 */       wait(a_milliSec);
/*     */     }
/* 123 */     catch (InterruptedException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 129 */     if (!this.m_running) {
/* 130 */       this.m_running = true;
/* 131 */       Thread thread = new Thread(this);
/* 132 */       thread.start();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void stop() {
/* 137 */     if (this.m_running) {
/* 138 */       this.m_running = false;
/* 139 */       if (this.m_genotype != null);
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
/*     */   public boolean isRunning() {
/* 152 */     return this.m_running;
/*     */   }
/*     */   
/*     */   public boolean canBeStarted() {
/* 156 */     return !this.m_running;
/*     */   }
/*     */   
/*     */   public boolean canBeStopped() {
/* 160 */     return this.m_running;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\Breeder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */