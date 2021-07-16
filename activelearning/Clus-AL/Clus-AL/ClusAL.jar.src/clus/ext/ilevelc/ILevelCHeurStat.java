/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
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
/*     */ public class ILevelCHeurStat
/*     */   extends ILevelCStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int EXT = 0;
/*     */   public static final int POS = 1;
/*     */   public static final int NEG = 2;
/*     */   protected int m_NbClass;
/*     */   protected ArrayList m_Constraints;
/*     */   protected int[][] m_ConstraintIndex;
/*     */   protected int[] m_IE;
/*     */   protected int[] m_Clusters;
/*     */   protected int[] m_CL;
/*     */   protected int[] m_ML;
/*     */   
/*     */   public ILevelCHeurStat(ILevelCStatistic stat, int nbclass) {
/*  47 */     super(stat.m_Numeric);
/*  48 */     this.m_NbClass = nbclass;
/*  49 */     this.m_CL = new int[nbclass];
/*  50 */     this.m_ML = new int[nbclass];
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/*  54 */     super.updateWeighted(tuple, weight);
/*  55 */     int tidx = tuple.getIndex();
/*  56 */     int[] cidx = this.m_ConstraintIndex[tidx];
/*  57 */     if (cidx == null)
/*  58 */       return;  for (int i = 0; i < cidx.length; i++) {
/*  59 */       ILevelConstraint cons = this.m_Constraints.get(cidx[i]);
/*  60 */       int otidx = cons.getOtherTupleIdx(tuple);
/*  61 */       if (this.m_IE[otidx] == 0) {
/*  62 */         int oclass = this.m_Clusters[otidx];
/*  63 */         if (cons.getType() == 0) { this.m_ML[oclass] = this.m_ML[oclass] + 1; }
/*  64 */         else { this.m_CL[oclass] = this.m_CL[oclass] + 1; }
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public void removeWeighted(DataTuple tuple, double weight) {
/*  70 */     super.updateWeighted(tuple, -1.0D * weight);
/*  71 */     int tidx = tuple.getIndex();
/*  72 */     int[] cidx = this.m_ConstraintIndex[tidx];
/*  73 */     if (cidx == null)
/*  74 */       return;  for (int i = 0; i < cidx.length; i++) {
/*  75 */       ILevelConstraint cons = this.m_Constraints.get(cidx[i]);
/*  76 */       int otidx = cons.getOtherTupleIdx(tuple);
/*  77 */       if (this.m_IE[otidx] == 0) {
/*  78 */         int oclass = this.m_Clusters[otidx];
/*  79 */         if (cons.getType() == 0) { this.m_ML[oclass] = this.m_ML[oclass] - 1; }
/*  80 */         else { this.m_CL[oclass] = this.m_CL[oclass] - 1; }
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public void setIndices(int[][] considx, ArrayList constr, int[] ie, int[] clusters) {
/*  86 */     this.m_IE = ie;
/*  87 */     this.m_Constraints = constr;
/*  88 */     this.m_ConstraintIndex = considx;
/*  89 */     this.m_Clusters = clusters;
/*     */   }
/*     */   
/*     */   public int computeMinimumExtViolated(int ig1, int ig2, boolean allownew) {
/*  93 */     int totalml = 0;
/*  94 */     for (int i = 0; i < this.m_NbClass; i++) {
/*  95 */       totalml += this.m_ML[i];
/*     */     }
/*  97 */     int best = -2;
/*  98 */     int bestviolated = Integer.MAX_VALUE;
/*  99 */     for (int j = 0; j < this.m_NbClass; j++) {
/* 100 */       if (j != ig1 && j != ig2 && this.m_ML[j] > 0) {
/* 101 */         int violated = this.m_CL[j] + totalml - this.m_ML[j];
/* 102 */         if (violated < bestviolated) {
/* 103 */           bestviolated = violated;
/* 104 */           best = j;
/*     */         } 
/*     */       } 
/*     */     } 
/* 108 */     if (allownew && totalml < bestviolated) {
/* 109 */       bestviolated = totalml;
/* 110 */       best = -1;
/*     */     } 
/* 112 */     setClusterID(best);
/* 113 */     return (best == -2) ? -1 : bestviolated;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ilevelc\ILevelCHeurStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */