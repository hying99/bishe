/*     */ package clus.tools.optimization.de;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.tools.optimization.OptProbl;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Random;
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
/*     */ public class DeProbl
/*     */   extends OptProbl
/*     */ {
/*     */   private ArrayList<Double> m_VarMin;
/*     */   private ArrayList<Double> m_VarMax;
/*     */   
/*     */   public DeProbl(ClusStatManager stat_mgr, OptProbl.OptParam optInfo, ClusRuleSet rset) {
/*  54 */     super(stat_mgr, optInfo);
/*     */     
/*  56 */     this.m_VarMin = new ArrayList<>(getNumVar());
/*  57 */     this.m_VarMax = new ArrayList<>(getNumVar());
/*     */     
/*  59 */     for (int i = 0; i < getNumVar(); i++) {
/*  60 */       this.m_VarMin.add(new Double(0.0D));
/*  61 */       this.m_VarMax.add(new Double(1.0D));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<Double> getRandVector(Random rand) {
/*  69 */     ArrayList<Double> result = new ArrayList<>(getNumVar());
/*  70 */     for (int i = 0; i < getNumVar(); i++) {
/*  71 */       result.add(new Double(getRandValueInRange(rand, i)));
/*     */     }
/*  73 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getRandValueInRange(Random rand, int indexOfValue) {
/*  81 */     return ((Double)this.m_VarMin.get(indexOfValue)).doubleValue() + (((Double)this.m_VarMax
/*  82 */       .get(indexOfValue)).doubleValue() - ((Double)this.m_VarMin
/*  83 */       .get(indexOfValue)).doubleValue()) * rand
/*  84 */       .nextDouble();
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<Double> getRoundVector(ArrayList<Double> genes) {
/*  89 */     ArrayList<Double> result = new ArrayList<>(getNumVar());
/*  90 */     for (int i = 0; i < getNumVar(); i++) {
/*  91 */       if (((Double)genes.get(i)).doubleValue() >= ((Double)this.m_VarMax
/*  92 */         .get(i)).doubleValue()) {
/*  93 */         result.add(this.m_VarMax.get(i));
/*  94 */       } else if (((Double)genes.get(i)).doubleValue() <= ((Double)this.m_VarMin
/*  95 */         .get(i)).doubleValue()) {
/*  96 */         result.add(this.m_VarMin.get(i));
/*     */       } else {
/*  98 */         result.add(genes.get(i));
/*     */       } 
/* 100 */     }  return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\de\DeProbl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */