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
/*    */ public class HierLevelDistance
/*    */   implements HierBasicDistance
/*    */ {
/*    */   public double getVirtualRootWeight() {
/* 28 */     return 0.0D;
/*    */   }
/*    */ 
/*    */   
/*    */   public double calcDistance(ClassTerm t1, ClassTerm t2) {
/* 33 */     int d1 = t1.getLevel();
/* 34 */     int d2 = t2.getLevel();
/* 35 */     int d_correct = d1;
/* 36 */     int com_d = Math.min(d1, d2);
/* 37 */     double distance = d2 - d1;
/* 38 */     while (d1 > com_d) {
/* 39 */       t1 = t1.getCTParent();
/* 40 */       d1--;
/*    */     } 
/* 42 */     while (d2 > com_d) {
/* 43 */       t2 = t2.getCTParent();
/* 44 */       d2--;
/*    */     } 
/* 46 */     while (com_d >= 0) {
/* 47 */       if (t1 != t2) distance = com_d - d_correct - 1.0D; 
/* 48 */       t1 = t1.getCTParent();
/* 49 */       t2 = t2.getCTParent();
/* 50 */       com_d--;
/*    */     } 
/* 52 */     return distance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierLevelDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */