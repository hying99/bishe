/*    */ package clus.activelearning.data;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.ext.hierarchical.ClassHierarchy;
/*    */ import clus.ext.hierarchical.ClassesTuple;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClassCounterCorrelation
/*    */ {
/*    */   private double counterA;
/*    */   private double counterB;
/*    */   private double counterC;
/*    */   private double counterD;
/*    */   int i;
/*    */   int j;
/*    */   
/*    */   public double getCounterA() {
/* 24 */     return this.counterA;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getCounterB() {
/* 32 */     return this.counterA + this.counterB;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getCounterC() {
/* 40 */     return this.counterC;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getCounterD() {
/* 47 */     return this.counterD;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClassCounterCorrelation(int i, int j) {
/* 59 */     this.counterA = 0.0D;
/* 60 */     this.counterB = 0.0D;
/* 61 */     this.counterC = 0.0D;
/* 62 */     this.counterD = 0.0D;
/* 63 */     this.i = i;
/* 64 */     this.j = j;
/*    */   }
/*    */   
/*    */   public void count(RowData rowData, ClassHierarchy h, int labelIndex1, int labelIndex2) {
/* 68 */     int nbRows = rowData.getNbRows();
/*    */     
/* 70 */     ClassesTuple ct = null;
/*    */     
/* 72 */     for (int index = 0; index < nbRows; index++) {
/* 73 */       DataTuple dataTuple = rowData.getTuple(index);
/* 74 */       ct = (ClassesTuple)dataTuple.getObjVal(0);
/* 75 */       boolean[] vectorBooleanNodeAndAncestors = ct.getVectorBooleanNodeAndAncestors(h);
/* 76 */       parseBooleanVector(vectorBooleanNodeAndAncestors);
/*    */     } 
/*    */   }
/*    */   
/*    */   private void parseBooleanVector(boolean[] booleanVector) {
/* 81 */     if (booleanVector[this.i] && booleanVector[this.j]) {
/* 82 */       this.counterA++;
/* 83 */     } else if (booleanVector[this.i] && !booleanVector[this.j]) {
/* 84 */       this.counterB++;
/* 85 */     } else if (!booleanVector[this.i] && booleanVector[this.j]) {
/* 86 */       this.counterC++;
/*    */     } else {
/* 88 */       this.counterD++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\data\ClassCounterCorrelation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */