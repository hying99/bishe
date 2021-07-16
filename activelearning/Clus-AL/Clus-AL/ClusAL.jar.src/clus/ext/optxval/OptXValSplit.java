/*    */ package clus.ext.optxval;
/*    */ 
/*    */ import clus.model.test.NodeTest;
/*    */ import java.util.Arrays;
/*    */ import jeans.tree.MyNode;
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
/*    */ public class OptXValSplit
/*    */   extends MyNode
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected int[] m_Folds;
/*    */   protected NodeTest m_Test;
/*    */   
/*    */   public int init(int[] folds, NodeTest test) {
/* 39 */     this.m_Test = test;
/* 40 */     int mnb = folds.length;
/* 41 */     this.m_Folds = new int[mnb];
/* 42 */     System.arraycopy(folds, 0, this.m_Folds, 0, mnb);
/* 43 */     int arity = test.getNbChildren();
/* 44 */     setNbChildren(arity);
/* 45 */     return arity;
/*    */   }
/*    */   
/*    */   public int[] getFolds() {
/* 49 */     return this.m_Folds;
/*    */   }
/*    */   
/*    */   public void setTest(NodeTest test) {
/* 53 */     this.m_Test = test;
/*    */   }
/*    */   
/*    */   public NodeTest getTest() {
/* 57 */     return this.m_Test;
/*    */   }
/*    */   
/*    */   public boolean contains(int fold) {
/* 61 */     return (Arrays.binarySearch(this.m_Folds, fold) >= 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\optxval\OptXValSplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */