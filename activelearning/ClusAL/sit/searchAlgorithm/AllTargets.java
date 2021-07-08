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
/*    */ public class AllTargets
/*    */   implements SearchAlgorithm
/*    */ {
/*    */   protected MTLearner m_MTLearner;
/*    */   
/*    */   public TargetSet search(ClusAttrType mainTarget, TargetSet candidates) {
/* 31 */     return new TargetSet(candidates);
/*    */   }
/*    */   
/*    */   public void setMTLearner(MTLearner learner) {
/* 35 */     this.m_MTLearner = learner;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 39 */     return "AllTargets";
/*    */   }
/*    */   
/*    */   public void setSettings(Settings s) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\AllTargets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */