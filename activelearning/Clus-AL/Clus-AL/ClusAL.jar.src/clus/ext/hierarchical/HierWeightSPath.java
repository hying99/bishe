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
/*    */ public class HierWeightSPath
/*    */   implements HierBasicDistance
/*    */ {
/*    */   protected double[] m_Weights;
/* 28 */   protected double m_RootDelta = 1.0D;
/*    */   protected double fac;
/*    */   
/*    */   public HierWeightSPath(int depth, double fac) {
/* 32 */     this.fac = fac;
/* 33 */     this.m_Weights = new double[depth];
/* 34 */     for (int i = 0; i < depth; i++) {
/* 35 */       this.m_Weights[i] = Math.pow(fac, i);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   protected final double getFac() {
/* 41 */     return this.fac;
/*    */   }
/*    */   
/*    */   protected final double getWeight(int level) {
/* 45 */     return this.m_Weights[level];
/*    */   }
/*    */   
/*    */   public double getVirtualRootWeight() {
/* 49 */     return this.m_RootDelta;
/*    */   }
/*    */   
/*    */   public double calcDistance(ClassTerm t1, ClassTerm t2) {
/* 53 */     double distance = 0.0D;
/* 54 */     int d1 = t1.getLevel();
/* 55 */     int d2 = t2.getLevel();
/* 56 */     int com_d = Math.min(d1, d2);
/* 57 */     while (d1 > com_d) {
/* 58 */       distance += getWeight(d1);
/* 59 */       t1 = t1.getCTParent();
/* 60 */       d1--;
/*    */     } 
/* 62 */     while (d2 > com_d) {
/* 63 */       distance += getWeight(d2);
/* 64 */       t2 = t2.getCTParent();
/* 65 */       d2--;
/*    */     } 
/* 67 */     while (t1 != t2) {
/* 68 */       distance += 2.0D * getWeight(com_d);
/* 69 */       t1 = t1.getCTParent();
/* 70 */       t2 = t2.getCTParent();
/* 71 */       com_d--;
/*    */     } 
/* 73 */     return distance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierWeightSPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */