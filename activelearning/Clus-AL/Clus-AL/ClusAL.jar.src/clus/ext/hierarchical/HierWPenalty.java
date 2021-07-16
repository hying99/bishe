/*    */ package clus.ext.hierarchical;
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
/*    */ public class HierWPenalty
/*    */   implements HierBasicDistance
/*    */ {
/*    */   protected double[] m_Weights;
/*    */   
/*    */   public HierWPenalty(int depth, double fac) {
/* 30 */     this.m_Weights = new double[depth];
/* 31 */     for (int i = 0; i < depth; i++) {
/* 32 */       this.m_Weights[i] = Math.pow(fac, i);
/*    */     }
/*    */   }
/*    */   
/*    */   protected final double getWeight(int level) {
/* 37 */     return this.m_Weights[level - 1];
/*    */   }
/*    */   
/*    */   public double getVirtualRootWeight() {
/* 41 */     return 0.0D;
/*    */   }
/*    */   
/*    */   public double calcDistance(ClassTerm t1, ClassTerm t2) {
/* 45 */     double distance = 0.0D;
/* 46 */     int d1 = t1.getLevel();
/* 47 */     int d2 = t2.getLevel();
/* 48 */     int com_d = Math.min(d1, d2);
/* 49 */     while (d1 > com_d) {
/* 50 */       distance = getWeight(d1);
/* 51 */       t1 = t1.getCTParent();
/* 52 */       d1--;
/*    */     } 
/* 54 */     while (d2 > com_d) {
/* 55 */       distance = getWeight(d2);
/* 56 */       t2 = t2.getCTParent();
/* 57 */       d2--;
/*    */     } 
/* 59 */     while (com_d >= 0) {
/* 60 */       if (t1 != t2) distance = getWeight(com_d); 
/* 61 */       t1 = t1.getCTParent();
/* 62 */       t2 = t2.getCTParent();
/* 63 */       com_d--;
/*    */     } 
/* 65 */     return distance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierWPenalty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */