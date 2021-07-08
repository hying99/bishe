/*    */ package org.jgap.gp;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public interface IComplexCommand
/*    */   extends Serializable
/*    */ {
/*    */   public static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   
/*    */   Complexity getComplexity();
/*    */   
/*    */   public enum Complexity
/*    */   {
/* 27 */     NANO, SMALL, MEDIUM, LARGE, VERY_LARGE;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\IComplexCommand.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */