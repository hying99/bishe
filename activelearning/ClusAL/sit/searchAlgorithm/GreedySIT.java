/*    */ package sit.searchAlgorithm;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import java.util.Iterator;
/*    */ import sit.TargetSet;
/*    */ import sit.mtLearner.MTLearner;
/*    */ 
/*    */ public class GreedySIT
/*    */   extends SearchAlgorithmImpl
/*    */ {
/*    */   protected MTLearner learner;
/*    */   
/*    */   public String getName() {
/* 14 */     return "GreedySIT";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TargetSet search(ClusAttrType mainTarget, TargetSet candidates) {
/* 20 */     TargetSet best_set = new TargetSet(mainTarget);
/* 21 */     double best_err = eval(best_set, mainTarget);
/* 22 */     System.out.println("Best set = " + best_set + " with correlation " + best_err);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 28 */     boolean improvement = true;
/* 29 */     while (improvement) {
/*    */       
/* 31 */       improvement = false;
/* 32 */       double tmp_best_err = best_err;
/* 33 */       TargetSet tmp_best_set = best_set;
/* 34 */       System.out.println("Trying to improve this set:" + best_set);
/* 35 */       Iterator i = candidates.iterator();
/* 36 */       while (i.hasNext()) {
/* 37 */         TargetSet test = (TargetSet)best_set.clone();
/* 38 */         test.add(i.next());
/* 39 */         System.out.println("Eval:" + test);
/* 40 */         double err = eval(test, mainTarget);
/* 41 */         if (err > tmp_best_err) {
/* 42 */           tmp_best_err = err;
/* 43 */           tmp_best_set = test;
/* 44 */           improvement = true;
/* 45 */           System.out.println("-->improvement: " + err);
/*    */         } 
/*    */       } 
/*    */       
/* 49 */       best_err = tmp_best_err;
/* 50 */       best_set = tmp_best_set;
/* 51 */       System.out.println("Best set found:" + best_set + " correlation " + best_err);
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 56 */     return best_set;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\GreedySIT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */