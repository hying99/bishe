/*    */ package clus.error;
/*    */ 
/*    */ import clus.data.type.NominalAttrType;
/*    */ import clus.statistic.ClassificationStat;
/*    */ import clus.statistic.ClusStatistic;
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
/*    */ 
/*    */ public class MisclassificationError
/*    */   extends Accuracy
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public MisclassificationError(ClusErrorList par, NominalAttrType[] nom) {
/* 38 */     super(par, nom);
/*    */   }
/*    */   
/*    */   public boolean shouldBeLow() {
/* 42 */     return true;
/*    */   }
/*    */   
/*    */   public double getModelErrorComponent(int i) {
/* 46 */     return 1.0D - this.m_NbCorrect[i] / getNbExamples();
/*    */   }
/*    */   
/*    */   public String getName() {
/* 50 */     return "Misclassification error";
/*    */   }
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 54 */     return new MisclassificationError(par, this.m_Attrs);
/*    */   }
/*    */   
/*    */   public double computeLeafError(ClusStatistic stat) {
/* 58 */     ClassificationStat cstat = (ClassificationStat)stat;
/* 59 */     return cstat.getError(null) * cstat.getNbAttributes();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\MisclassificationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */