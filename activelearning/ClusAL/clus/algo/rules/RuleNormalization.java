/*    */ package clus.algo.rules;
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
/*    */ 
/*    */ public class RuleNormalization
/*    */ {
/* 34 */   private static double[] C_descMeans = null;
/*    */   
/* 36 */   private static double[] C_descStdDevs = null;
/*    */   
/* 38 */   private static double[] C_targMeans = null;
/*    */   
/* 40 */   private static double[] C_targStdDevs = null;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void initialize(double[][] descMeanAndStdDev, double[][] targMeanAndStdDev) {
/* 48 */     C_descStdDevs = descMeanAndStdDev[1];
/* 49 */     C_descMeans = descMeanAndStdDev[0];
/*    */     
/* 51 */     C_targStdDevs = targMeanAndStdDev[1];
/* 52 */     C_targMeans = targMeanAndStdDev[0];
/*    */   }
/*    */   
/*    */   public static double getDescMean(int iDescriptiveAttr) {
/* 56 */     return C_descMeans[iDescriptiveAttr];
/*    */   }
/*    */   
/*    */   public static double getTargMean(int iTargetAttr) {
/* 60 */     return C_targMeans[iTargetAttr];
/*    */   }
/*    */   
/*    */   public static double getDescStdDev(int iDescriptiveAttr) {
/* 64 */     return C_descStdDevs[iDescriptiveAttr];
/*    */   }
/*    */   
/*    */   public static double getTargStdDev(int iTargetAttr) {
/* 68 */     return C_targStdDevs[iTargetAttr];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\RuleNormalization.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */