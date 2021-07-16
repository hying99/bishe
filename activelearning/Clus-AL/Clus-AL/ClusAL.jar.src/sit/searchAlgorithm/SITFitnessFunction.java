/*    */ package sit.searchAlgorithm;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import java.util.ArrayList;
/*    */ import org.jgap.Chromosome;
/*    */ import org.jgap.FitnessFunction;
/*    */ import org.jgap.IChromosome;
/*    */ import sit.Evaluator;
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
/*    */ public class SITFitnessFunction
/*    */   extends FitnessFunction
/*    */ {
/*    */   protected ClusAttrType mainTarget;
/*    */   protected TargetSet candidates;
/*    */   protected MTLearner learner;
/*    */   
/*    */   public SITFitnessFunction(ClusAttrType mainTarget, MTLearner learner, TargetSet candidates) {
/* 36 */     this.mainTarget = mainTarget;
/* 37 */     this.learner = learner;
/* 38 */     this.candidates = candidates;
/*    */   }
/*    */   
/*    */   protected double evaluate(IChromosome chromyTheChromoson) {
/* 42 */     TargetSet tset = GeneticSearch.getTargetSet(this.candidates, (Chromosome)chromyTheChromoson);
/*    */     
/* 44 */     int errorIdx = tset.getIndex(this.mainTarget);
/* 45 */     if (errorIdx == -1)
/*    */     {
/* 47 */       return 0.0D;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 52 */     int nbFolds = 25;
/* 53 */     this.learner.initXVal(25);
/*    */     
/* 55 */     ArrayList<RowData[]> folds = (ArrayList)new ArrayList<>();
/* 56 */     for (int f = 0; f < nbFolds; f++) {
/* 57 */       folds.add(this.learner.LearnModel(tset, f));
/*    */     }
/*    */ 
/*    */     
/* 61 */     double error = 10.0D - Evaluator.getRelativeError(folds, this.mainTarget.getIndex());
/*    */ 
/*    */ 
/*    */     
/* 65 */     return error;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\searchAlgorithm\SITFitnessFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */