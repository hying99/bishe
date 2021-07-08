/*    */ package sit.searchAlgorithm;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.main.Settings;
/*    */ import sit.TargetSet;
/*    */ import sit.mtLearner.MTLearner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OneTarget
/*    */   implements SearchAlgorithm
/*    */ {
/*    */   public TargetSet search(ClusAttrType mainTarget, TargetSet candidates) {
/* 18 */     return new TargetSet(mainTarget);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMTLearner(MTLearner learner) {}
/*    */   
/*    */   public String getName() {
/* 25 */     return "OneTarget";
/*    */   }
/*    */   
/*    */   public void setSettings(Settings s) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\OneTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */