/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.error.ClusError;
/*    */ import clus.error.ClusErrorList;
/*    */ import clus.error.MSError;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import java.util.Arrays;
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
/*    */ public class HierRMSError
/*    */   extends MSError
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected ClassHierarchy m_Hier;
/*    */   protected double[] m_Scratch;
/*    */   protected boolean m_Root;
/*    */   protected boolean m_ContPred;
/*    */   
/*    */   public HierRMSError(ClusErrorList par, ClusAttributeWeights weights, boolean root, boolean proto, ClassHierarchy hier) {
/* 45 */     this(par, weights, false, root, proto, hier);
/*    */   }
/*    */   
/*    */   public HierRMSError(ClusErrorList par, ClusAttributeWeights weights, boolean printall, boolean root, boolean proto, ClassHierarchy hier) {
/* 49 */     super(par, hier.getDummyAttrs(), weights, printall);
/* 50 */     this.m_Hier = hier;
/* 51 */     this.m_Root = root;
/* 52 */     this.m_ContPred = proto;
/* 53 */     this.m_Scratch = new double[this.m_Dim];
/*    */   }
/*    */   
/*    */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 57 */     if (pred == null)
/* 58 */       return;  ClassesTuple tp = (ClassesTuple)tuple.getObjVal(0);
/* 59 */     Arrays.fill(this.m_Scratch, 0.0D);
/* 60 */     for (int i = 0; i < tp.getNbClasses(); i++) {
/* 61 */       ClassesValue val = tp.getClass(i);
/* 62 */       this.m_Scratch[val.getIndex()] = 1.0D;
/*    */     } 
/* 64 */     if (this.m_ContPred) {
/* 65 */       addExample(this.m_Scratch, pred.getNumericPred());
/*    */     } else {
/* 67 */       addExample(this.m_Scratch, ((WHTDStatistic)pred).getDiscretePred());
/*    */     } 
/*    */   }
/*    */   
/*    */   public double getModelError() {
/* 72 */     if (this.m_Root) return Math.sqrt(super.getModelError()); 
/* 73 */     return super.getModelError();
/*    */   }
/*    */   
/*    */   public double getModelErrorComponent(int i) {
/* 77 */     if (this.m_Root) return Math.sqrt(super.getModelErrorComponent(i)); 
/* 78 */     return super.getModelErrorComponent(i);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 82 */     String root = this.m_Root ? "RMSE" : "MSE";
/* 83 */     String proto = this.m_ContPred ? "with continuous predictions" : "with discrete predictions";
/* 84 */     if (this.m_Weights == null) return "Hierarchical " + root + " " + proto; 
/* 85 */     return "Hierarchical weighted " + root + " (" + this.m_Weights.getName() + ") " + proto;
/*    */   }
/*    */   
/*    */   public ClusError getErrorClone(ClusErrorList par) {
/* 89 */     return (ClusError)new HierRMSError(par, this.m_Weights, this.m_PrintAllComps, this.m_Root, this.m_ContPred, this.m_Hier);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\HierRMSError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */