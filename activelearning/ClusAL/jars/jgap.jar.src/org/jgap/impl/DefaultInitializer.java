/*    */ package org.jgap.impl;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.jgap.IChromosome;
/*    */ import org.jgap.IInitializer;
/*    */ import org.jgap.util.ICloneable;
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
/*    */ public class DefaultInitializer
/*    */   implements IInitializer, ICloneable, Serializable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.10 $";
/*    */   
/*    */   public boolean isHandlerFor(Object a_obj, Class<?> a_class) {
/* 29 */     if (IChromosome.class.isAssignableFrom(a_class)) {
/* 30 */       return true;
/*    */     }
/*    */     
/* 33 */     if (a_obj != null && IInitializer.class.isAssignableFrom(a_class)) {
/* 34 */       IInitializer initer = (IInitializer)a_obj;
/* 35 */       return initer.isHandlerFor(null, a_class);
/*    */     } 
/*    */     
/* 38 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object perform(Object a_obj, Class<?> a_class, Object a_params) throws Exception {
/* 46 */     if (IInitializer.class.isAssignableFrom(a_class)) {
/* 47 */       return ((IInitializer)a_obj).perform(null, a_class, a_params);
/*    */     }
/*    */     
/* 50 */     throw new IllegalArgumentException("DefaultInitializer not suited for class " + a_class.getName() + " !");
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
/*    */   public Object clone() {
/* 64 */     return new DefaultInitializer();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DefaultInitializer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */