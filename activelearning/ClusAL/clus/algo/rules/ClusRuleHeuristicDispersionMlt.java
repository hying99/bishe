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
/*    */ public class ClusRuleHeuristicDispersionMlt
/*    */   extends ClusRuleHeuristicDispersion
/*    */ {
/*    */   public ClusRuleHeuristicDispersionMlt(ClusAttributeWeights prod) {}
/*    */   
/*    */   public ClusRuleHeuristicDispersionMlt(ClusStatManager stat_mgr, ClusAttributeWeights prod) {
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
/* 52 */     double disp = ((CombStat)c_pstat).dispersionMltHeur();
/* 53 */     double disp1 = disp;
/* 54 */     double ad = -1.0D;
/*    */     
/* 56 */     if (((CombStat)c_pstat).getSettings().isHeurRuleDist() && this.m_CoveredBitVectArray
/* 57 */       .size() > 0) {
/* 58 */       double avg_dist = 0.0D;
/* 59 */       int nb_rules = this.m_CoveredBitVectArray.size();
/* 60 */       boolean[] bit_vect = new boolean[this.m_NbTuples];
/* 61 */       for (int i = 0; i < this.m_DataIndexes.length; i++) {
/* 62 */         bit_vect[this.m_DataIndexes[i]] = true;
/*    */       }
/* 64 */       boolean[] bit_vect_c = new boolean[this.m_NbTuples];
/* 65 */       for (int j = 0; j < nb_rules; j++) {
/* 66 */         bit_vect_c = this.m_CoveredBitVectArray.get(j);
/* 67 */         double single_dist = 0.0D;
/* 68 */         for (int k = 0; k < this.m_NbTuples; k++) {
/* 69 */           if (bit_vect[k] != bit_vect_c[k]) {
/* 70 */             single_dist++;
/*    */           }
/*    */         } 
/* 73 */         single_dist /= this.m_NbTuples;
/* 74 */         avg_dist += single_dist;
/*    */       } 
/* 76 */       avg_dist /= nb_rules;
/* 77 */       double dist_par = ((CombStat)c_pstat).getSettings().getHeurRuleDistPar();
/*    */ 
/*    */       
/* 80 */       disp = (avg_dist > 0.0D) ? (disp / Math.pow(avg_dist, dist_par)) : 100.0D;
/* 81 */       ad = avg_dist;
/*    */     } 
/*    */     
/* 84 */     return -disp;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 88 */     return "Rule Heuristic (Reduced Dispersion, Multiplicative ver.)";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleHeuristicDispersionMlt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */