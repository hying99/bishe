/*    */ package org.jgap.xml;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GeneCreationException
/*    */   extends Exception
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   
/*    */   public GeneCreationException(String a_message) {
/* 42 */     super(a_message);
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
/*    */ 
/*    */ 
/*    */   
/*    */   public GeneCreationException(Class a_geneClass, Throwable cause) {
/* 57 */     super((a_geneClass != null) ? ("Gene class " + a_geneClass.getName()) : "", cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\xml\GeneCreationException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */