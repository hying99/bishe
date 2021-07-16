/*     */ package clus.tools.optimization.de;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.tools.optimization.OptAlg;
/*     */ import clus.tools.optimization.OptProbl;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
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
/*     */ public class DeAlg
/*     */   extends OptAlg
/*     */ {
/*     */   private DeProbl m_DeProbl;
/*     */   private DePop m_Pop;
/*     */   private DeInd m_Best;
/*     */   
/*     */   public DeAlg(ClusStatManager stat_mgr, OptProbl.OptParam dataInformation, ClusRuleSet rset) {
/*  59 */     super(stat_mgr);
/*  60 */     this.m_DeProbl = new DeProbl(stat_mgr, dataInformation, rset);
/*  61 */     this.m_Pop = new DePop(stat_mgr, this.m_DeProbl);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<Double> optimize() {
/*  70 */     System.out.print("\nDifferential evolution: Optimizing rule weights (" + getSettings().getOptDENumEval() + ") ");
/*     */     try {
/*  72 */       PrintWriter wrt_log = new PrintWriter(new OutputStreamWriter(new FileOutputStream("evol.log")));
/*     */ 
/*     */ 
/*     */       
/*  76 */       this.m_Pop.createFirstPop();
/*  77 */       int num_eval = this.m_Pop.evaluatePop(0);
/*  78 */       this.m_Best = new DeInd();
/*  79 */       this.m_Best.copy(this.m_Pop.m_Inds.get(0));
/*  80 */       for (int i = 0; i < getSettings().getOptDEPopSize(); i++) {
/*  81 */         checkIfBest(this.m_Pop.m_Inds.get(i));
/*  82 */         OutputLog(this.m_Pop.m_Inds.get(i), i, wrt_log);
/*     */       } 
/*  84 */       OutputPop();
/*     */       
/*  86 */       while (num_eval < getSettings().getOptDENumEval()) {
/*  87 */         System.out.print(".");
/*  88 */         this.m_Pop.sortPopRandom();
/*  89 */         DeInd candidate = new DeInd();
/*     */ 
/*     */         
/*  92 */         for (int j = 0; j < getSettings().getOptDEPopSize(); j++) {
/*  93 */           candidate.setGenes(this.m_Pop.getCandidate(j));
/*  94 */           num_eval = candidate.evaluate(this.m_DeProbl, num_eval);
/*  95 */           checkIfBest(this.m_Pop.m_Inds.get(j));
/*  96 */           OutputLog(candidate, num_eval, wrt_log);
/*     */           
/*  98 */           if (candidate.m_Fitness < ((DeInd)this.m_Pop.m_Inds.get(j)).m_Fitness) {
/*  99 */             ((DeInd)this.m_Pop.m_Inds.get(j)).copy(candidate);
/*     */           }
/*     */         } 
/*     */       } 
/* 103 */       wrt_log.close();
/*     */       
/* 105 */       System.out.println(" done!");
/* 106 */     } catch (Exception e) {
/* 107 */       e.printStackTrace();
/*     */     } 
/* 109 */     return this.m_Best.getGenes();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void OutputPop() throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkIfBest(DeInd ind) {
/* 124 */     if (this.m_Best.m_Fitness > ind.m_Fitness) {
/* 125 */       this.m_Best.copy(ind);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void OutputLog(DeInd ind, int index, PrintWriter wrt) {
/* 131 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 132 */     wrt.print("" + fr.format(index));
/* 133 */     wrt.print("\t");
/* 134 */     wrt.print("" + fr.format(this.m_Best.m_Fitness));
/* 135 */     wrt.print("\t");
/* 136 */     wrt.print(ind.getIndString());
/* 137 */     wrt.print("\n");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\tools\optimization\de\DeAlg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */