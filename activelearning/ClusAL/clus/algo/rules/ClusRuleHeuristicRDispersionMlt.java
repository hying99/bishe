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
/*    */ public class ClusRuleHeuristicRDispersionMlt
/*    */   extends ClusRuleHeuristicDispersion
/*    */ {
/*    */   public ClusRuleHeuristicRDispersionMlt(ClusAttributeWeights prod) {}
/*    */   
/*    */   public ClusRuleHeuristicRDispersionMlt(ClusStatManager stat_mgr, ClusAttributeWeights prod) {
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
/* 52 */     double disp = ((CombStat)c_pstat).rDispersionMltHeur();
/*    */     
/* 54 */     double disp1 = disp;
/* 55 */     double ad = -1.0D;
/*    */     
/* 57 */     if (((CombStat)c_pstat).getSettings().isHeurRuleDist() && this.m_CoveredBitVectArray
/* 58 */       .size() > 0) {
/* 59 */       double avg_dist = 0.0D;
/* 60 */       int nb_rules = this.m_CoveredBitVectArray.size();
/*    */       
/* 62 */       boolean[] bit_vect = new boolean[this.m_NbTuples];
/* 63 */       for (int i = 0; i < this.m_DataIndexes.length; i++) {
/* 64 */         bit_vect[this.m_DataIndexes[i]] = true;
/*    */       }
/* 66 */       boolean[] bit_vect_c = new boolean[this.m_NbTuples];
/*    */       
/* 68 */       for (int j = 0; j < nb_rules; j++) {
/* 69 */         bit_vect_c = this.m_CoveredBitVectArray.get(j);
/* 70 */         double single_dist = 0.0D;
/* 71 */         for (int k = 0; k < this.m_NbTuples; k++) {
/* 72 */           if (bit_vect[k] != bit_vect_c[k]) {
/* 73 */             single_dist++;
/*    */           }
/*    */         } 
/* 76 */         single_dist /= this.m_NbTuples;
/* 77 */         avg_dist += single_dist;
/*    */       } 
/* 79 */       avg_dist /= nb_rules;
/* 80 */       double dist_par = ((CombStat)c_pstat).getSettings().getHeurRuleDistPar();
/*    */ 
/*    */       
/* 83 */       disp = (avg_dist > 0.0D) ? (disp / Math.pow(avg_dist, dist_par)) : 100.0D;
/* 84 */       ad = avg_dist;
/*    */     } 
/*    */     
/* 87 */     return -disp;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 91 */     return "Rule Heuristic (Reduced Relative Dispersion, Multiplicative ver.)";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleHeuristicRDispersionMlt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */