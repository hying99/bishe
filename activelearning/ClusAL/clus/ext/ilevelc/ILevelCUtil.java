/*    */ package clus.ext.ilevelc;
/*    */ 
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
/*    */ public class ILevelCUtil
/*    */ {
/*    */   public static int[][] createConstraintsIndex(int nbtrain, ArrayList<ILevelConstraint> constr) {
/* 31 */     ArrayList[] crIndex = new ArrayList[nbtrain];
/* 32 */     for (int i = 0; i < constr.size(); i++) {
/* 33 */       ILevelConstraint ic = constr.get(i);
/* 34 */       int t1 = ic.getT1().getIndex();
/* 35 */       int t2 = ic.getT2().getIndex();
/* 36 */       if (crIndex[t1] == null) crIndex[t1] = new ArrayList(); 
/* 37 */       if (crIndex[t2] == null) crIndex[t2] = new ArrayList(); 
/* 38 */       crIndex[t1].add(new Integer(i));
/* 39 */       crIndex[t2].add(new Integer(i));
/*    */     } 
/*    */     
/* 42 */     int[][] index = new int[nbtrain][];
/* 43 */     for (int j = 0; j < nbtrain; j++) {
/* 44 */       if (crIndex[j] != null) {
/* 45 */         int nb = crIndex[j].size();
/* 46 */         index[j] = new int[nb];
/* 47 */         for (int k = 0; k < nb; k++) {
/* 48 */           Integer value = crIndex[j].get(k);
/* 49 */           index[j][k] = value.intValue();
/*    */         } 
/*    */       } 
/*    */     } 
/* 53 */     return index;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\ILevelCUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */