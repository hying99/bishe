/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.main.Settings;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.CombStat;
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
/*    */ 
/*    */ public class ClusRuleHeuristicDispersionAdt
/*    */   extends ClusRuleHeuristicDispersion
/*    */ {
/*    */   public ClusRuleHeuristicDispersionAdt(ClusAttributeWeights prod) {}
/*    */   
/*    */   public ClusRuleHeuristicDispersionAdt(ClusStatManager stat_mgr, ClusAttributeWeights prod) {
/* 39 */     this.m_StatManager = stat_mgr;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 47 */     double n_pos = c_pstat.m_SumWeight;
/*    */     
/* 49 */     if (n_pos - Settings.MINIMAL_WEIGHT < 1.0E-6D) {
/* 50 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/* 52 */     double disp = ((CombStat)c_pstat).dispersionAdtHeur();
/*    */     
/* 54 */     if (((CombStat)c_pstat).getSettings().isHeurRuleDist() && this.m_CoveredBitVectArray
/* 55 */       .size() > 0) {
/* 56 */       double avg_dist = 0.0D;
/* 57 */       int nb_rules = this.m_CoveredBitVectArray.size();
/* 58 */       boolean[] bit_vect = new boolean[this.m_NbTuples];
/* 59 */       for (int i = 0; i < this.m_DataIndexes.length; i++) {
/* 60 */         bit_vect[this.m_DataIndexes[i]] = true;
/*    */       }
/* 62 */       boolean[] bit_vect_c = new boolean[this.m_NbTuples];
/* 63 */       for (int j = 0; j < nb_rules; j++) {
/* 64 */         bit_vect_c = this.m_CoveredBitVectArray.get(j);
/* 65 */         double single_dist = 0.0D;
/* 66 */         for (int k = 0; k < this.m_NbTuples; k++) {
/* 67 */           if (bit_vect[k] != bit_vect_c[k]) {
/* 68 */             single_dist++;
/*    */           }
/*    */         } 
/* 71 */         single_dist /= this.m_NbTuples;
/* 72 */         avg_dist += single_dist;
/*    */       } 
/* 74 */       avg_dist /= nb_rules;
/* 75 */       double dist_par = ((CombStat)c_pstat).getSettings().getHeurRuleDistPar();
/* 76 */       double dist_part = avg_dist * dist_par;
/* 77 */       disp += 1.0D - dist_part;
/*    */     } 
/* 79 */     return -disp;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 83 */     return "Rule Heuristic (Reduced Dispersion, Additive ver.)";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRuleHeuristicDispersionAdt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */