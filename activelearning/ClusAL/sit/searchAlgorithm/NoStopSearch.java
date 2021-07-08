/*    */ package sit.searchAlgorithm;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import java.util.Iterator;
/*    */ import sit.TargetSet;
/*    */ 
/*    */ 
/*    */ public class NoStopSearch
/*    */   extends SearchAlgorithmImpl
/*    */ {
/*    */   public String getName() {
/* 12 */     return "NoStop";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TargetSet search(ClusAttrType mainTarget, TargetSet candidates) {
/* 18 */     TargetSet best_set = new TargetSet(mainTarget);
/* 19 */     double best_err = eval(best_set, mainTarget);
/*    */     
/* 21 */     System.out.println("Best set = " + best_set + " MSE " + ((best_err - 1.0D) * -1.0D));
/*    */ 
/*    */     
/* 24 */     TargetSet overal_best_set = new TargetSet(mainTarget);
/* 25 */     double overal_best_err = eval(best_set, mainTarget);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 30 */     boolean c = true;
/* 31 */     while (c) {
/*    */       
/* 33 */       double tmp_best_err = -1.7976931348623157E308D;
/* 34 */       TargetSet tmp_best_set = best_set;
/* 35 */       System.out.println("Trying to improve this set:" + best_set);
/* 36 */       Iterator<ClusAttrType> i = candidates.iterator();
/* 37 */       while (i.hasNext()) {
/* 38 */         TargetSet test = (TargetSet)best_set.clone();
/*    */         
/* 40 */         ClusAttrType cat = i.next();
/* 41 */         if (!test.contains(cat)) {
/* 42 */           test.add(cat);
/*    */           
/* 44 */           double err = eval(test, mainTarget);
/* 45 */           System.out.println("Eval:" + test + "->" + ((err - 1.0D) * -1.0D));
/*    */ 
/*    */ 
/*    */ 
/*    */           
/* 50 */           if (err > tmp_best_err) {
/* 51 */             tmp_best_err = err;
/* 52 */             tmp_best_set = test;
/* 53 */             System.out.println("-->improvement ");
/*    */           } 
/*    */         } 
/*    */       } 
/*    */       
/* 58 */       best_err = tmp_best_err;
/* 59 */       best_set = tmp_best_set;
/*    */       
/* 61 */       if (best_err > overal_best_err) {
/* 62 */         overal_best_err = best_err;
/* 63 */         overal_best_set = best_set;
/* 64 */         System.out.println("-->OVERAL improvement");
/*    */       } else {
/* 66 */         System.out.println("-->NO overal improvement...");
/*    */       } 
/* 68 */       if (tmp_best_set.size() == candidates.size()) {
/* 69 */         c = false;
/*    */       }
/* 71 */       System.out.println("Best set found:" + best_set + " correlation " + best_err);
/*    */     } 
/*    */     
/* 74 */     System.out.println("Overal best set found:" + overal_best_set + " correlation " + overal_best_err);
/*    */     
/* 76 */     return overal_best_set;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\NoStopSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */