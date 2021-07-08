/*    */ package clus.activelearning.data;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FrequencyCounter
/*    */ {
/*    */   public double[] frequency;
/*    */   public double total;
/*    */   
/*    */   public FrequencyCounter(RowData rowData, ClassHierarchy h) {
/* 24 */     this.frequency = new double[h.getTotal()];
/* 25 */     measureFrequency(rowData, h);
/*    */   }
/*    */   
/*    */   private void measureFrequency(RowData rowData, ClassHierarchy h) {
/* 29 */     int nbRows = rowData.getNbRows();
/*    */     int i;
/* 31 */     for (i = 0; i < nbRows; i++) {
/* 32 */       ClassesTuple ct = (ClassesTuple)(rowData.getTuple(i)).m_Objects[0];
/* 33 */       boolean[] vectorBooleanNodeAndAncestors = ct.getVectorBooleanNodeAndAncestors(h);
/* 34 */       parseBooleanVector(vectorBooleanNodeAndAncestors);
/*    */     } 
/* 36 */     for (i = 0; i < this.frequency.length; i++) {
/* 37 */       this.frequency[i] = this.frequency[i] / nbRows;
/*    */     }
/*    */   }
/*    */   
/*    */   private void parseBooleanVector(boolean[] booleanVector) {
/* 42 */     for (int i = 0; i < booleanVector.length; i++) {
/* 43 */       if (booleanVector[i]) {
/* 44 */         this.frequency[i] = this.frequency[i] + 1.0D;
/* 45 */         this.total++;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\data\FrequencyCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */