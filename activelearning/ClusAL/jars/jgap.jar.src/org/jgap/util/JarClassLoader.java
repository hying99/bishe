/*    */ package org.jgap.util;
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
/*    */ public class JarClassLoader
/*    */   extends MultiClassLoader
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   private JarResources m_jarResources;
/*    */   
/*    */   public JarClassLoader(String a_jarName) {
/* 28 */     this.m_jarResources = new JarResources(a_jarName);
/*    */   }
/*    */ 
/*    */   
/*    */   protected byte[] loadClassBytes(String a_className) {
/* 33 */     String className = formatClassName(a_className);
/*    */     
/* 35 */     return this.m_jarResources.getResource(className);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\JarClassLoader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */