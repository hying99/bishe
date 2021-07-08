/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.jgap.ICompareToHandler;
/*     */ import org.jgap.util.ICloneable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultCompareToHandler
/*     */   implements ICompareToHandler, ICloneable, Serializable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*     */   
/*     */   public boolean isHandlerFor(Object a_obj, Class<?> a_clazz) {
/*     */     Class<?> clazz;
/*  30 */     if (a_clazz == null) {
/*  31 */       if (a_obj == null) {
/*  32 */         return false;
/*     */       }
/*  34 */       clazz = a_obj.getClass();
/*     */     } else {
/*     */       
/*  37 */       clazz = a_clazz;
/*     */     } 
/*  39 */     if (Comparable.class.isAssignableFrom(clazz)) {
/*  40 */       return true;
/*     */     }
/*     */     
/*  43 */     if (clazz != null && Boolean.class == clazz) {
/*  44 */       return true;
/*     */     }
/*     */     
/*  47 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object perform(Object a_obj, Class a_class, Object a_params) throws Exception {
/*     */     int i;
/*  56 */     if (a_obj == null) {
/*  57 */       if (a_params != null) {
/*  58 */         i = -1;
/*     */       } else {
/*     */         
/*  61 */         i = 0;
/*     */       }
/*     */     
/*  64 */     } else if (a_params == null) {
/*  65 */       i = 1;
/*     */     
/*     */     }
/*  68 */     else if (a_obj.getClass() == Boolean.class) {
/*  69 */       boolean b1 = ((Boolean)a_obj).booleanValue();
/*  70 */       boolean b2 = ((Boolean)a_params).booleanValue();
/*  71 */       if (b1 == b2) {
/*  72 */         i = 0;
/*     */       }
/*  74 */       else if (b1) {
/*  75 */         i = 1;
/*     */       } else {
/*     */         
/*  78 */         i = -1;
/*     */       } 
/*     */     } else {
/*  81 */       i = ((Comparable<Object>)a_obj).compareTo(a_params);
/*     */     } 
/*     */     
/*  84 */     return new Integer(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*  94 */     return new DefaultCompareToHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Object a_other) {
/* 105 */     if (a_other.getClass().equals(getClass())) {
/* 106 */       return 0;
/*     */     }
/*     */     
/* 109 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DefaultCompareToHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */