/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.heuristic.ClusHeuristic;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.main.Settings;
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ClusRuleHeuristicDispersion
/*    */   extends ClusHeuristic
/*    */ {
/* 39 */   public ClusStatManager m_StatManager = null;
/*    */   public int[] m_DataIndexes;
/*    */   public int[][] m_DataIndexesPerVal;
/*    */   public ArrayList m_CoveredBitVectArray;
/*    */   public int m_NbTuples;
/*    */   
/*    */   public void setDataIndexes(int[] indexes) {
/* 46 */     this.m_DataIndexes = indexes;
/*    */   }
/*    */   
/*    */   public void setDataIndexes(boolean[] isin) {
/* 50 */     if (this.m_DataIndexesPerVal != null && isin.length == this.m_DataIndexesPerVal.length) {
/* 51 */       int size = 0;
/* 52 */       for (int i = 0; i < isin.length; i++) {
/* 53 */         if (isin[i]) {
/* 54 */           size += (this.m_DataIndexesPerVal[i]).length;
/*    */         }
/*    */       } 
/* 57 */       int[] new_data_idx = new int[size];
/* 58 */       int pt = 0;
/* 59 */       for (int j = 0; j < this.m_DataIndexesPerVal.length; j++) {
/* 60 */         if (isin[j]) {
/* 61 */           System.arraycopy(this.m_DataIndexesPerVal[j], 0, new_data_idx, pt, (this.m_DataIndexesPerVal[j]).length);
/* 62 */           pt += (this.m_DataIndexesPerVal[j]).length;
/*    */         } 
/*    */       } 
/* 65 */       setDataIndexes(new_data_idx);
/*    */     } else {
/* 67 */       System.err.println("ClusRuleHeuristicDispersion: setDataIndexes(boolean[])");
/* 68 */       System.exit(1);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void setDataIndexesPerVal(int[][] indexes) {
/* 73 */     this.m_DataIndexesPerVal = indexes;
/*    */   }
/*    */   
/*    */   public int[][] getDataIndexesPerVal() {
/* 77 */     return this.m_DataIndexesPerVal;
/*    */   }
/*    */   
/*    */   public void initCoveredBitVectArray(int size) {
/* 81 */     this.m_CoveredBitVectArray = new ArrayList();
/* 82 */     this.m_NbTuples = size;
/*    */   }
/*    */   
/*    */   public void setCoveredBitVectArray(ArrayList bit_vect_array) {
/* 86 */     this.m_CoveredBitVectArray = bit_vect_array;
/*    */   }
/*    */   
/*    */   public Settings getSettings() {
/* 90 */     return this.m_StatManager.getSettings();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleHeuristicDispersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */