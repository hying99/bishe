/*    */ package clus.util;
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
/*    */ public class ClusUtil
/*    */ {
/*    */   public static final double SMALL = 1.0E-6D;
/*    */   
/*    */   public static boolean grOrEq(double a, double b) {
/* 31 */     return (b - a < 1.0E-6D);
/*    */   }
/*    */   
/*    */   public static boolean smOrEq(double a, double b) {
/* 35 */     return (a - b < 1.0E-6D);
/*    */   }
/*    */   
/*    */   public static boolean eq(double a, double b) {
/* 39 */     return (a - b < 1.0E-6D && b - a < 1.0E-6D);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clu\\util\ClusUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */