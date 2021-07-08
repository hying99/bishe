/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.jgap.IApplicationData;
/*     */ import org.jgap.ICloneHandler;
/*     */ import org.jgap.util.CloneException;
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
/*     */ public class DefaultCloneHandler
/*     */   implements ICloneHandler, ICloneable, Serializable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.14 $";
/*     */   
/*     */   public boolean isHandlerFor(Object a_obj, Class<?> a_clazz) {
/*     */     Class<?> clazz;
/*  45 */     if (a_clazz == null) {
/*  46 */       if (a_obj == null) {
/*  47 */         return false;
/*     */       }
/*  49 */       clazz = a_obj.getClass();
/*     */     } else {
/*     */       
/*  52 */       clazz = a_clazz;
/*     */     } 
/*  54 */     if (IApplicationData.class.isAssignableFrom(clazz)) {
/*  55 */       return true;
/*     */     }
/*  57 */     if (ICloneable.class.isAssignableFrom(clazz)) {
/*  58 */       return true;
/*     */     }
/*  60 */     if (Cloneable.class.isAssignableFrom(clazz)) {
/*     */       boolean result;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/*  67 */         Method m = clazz.getMethod("clone", new Class[0]);
/*  68 */         boolean modified = false;
/*  69 */         if (!m.isAccessible()) {
/*  70 */           m.setAccessible(true);
/*  71 */           modified = true;
/*     */         } 
/*  73 */         result = m.isAccessible();
/*  74 */         if (modified) {
/*  75 */           m.setAccessible(false);
/*     */         }
/*     */       }
/*  78 */       catch (Exception ex) {
/*  79 */         return false;
/*     */       } 
/*  81 */       return result;
/*     */     } 
/*     */     
/*  84 */     return false;
/*     */   }
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
/*     */   public Object perform(Object a_objToClone, Class<?> a_class, Object a_params) {
/*     */     Class<?> clazz;
/* 103 */     if (a_class == null) {
/* 104 */       if (a_objToClone == null) {
/* 105 */         return null;
/*     */       }
/* 107 */       clazz = a_objToClone.getClass();
/*     */     } else {
/*     */       
/* 110 */       clazz = a_class;
/*     */     } 
/* 112 */     if (ICloneable.class.isAssignableFrom(clazz)) {
/*     */       try {
/* 114 */         return ((ICloneable)a_objToClone).clone();
/*     */       }
/* 116 */       catch (CloneException cex) {
/* 117 */         throw new IllegalStateException(cex);
/*     */       } 
/*     */     }
/* 120 */     if (IApplicationData.class.isAssignableFrom(a_objToClone.getClass())) {
/*     */       try {
/* 122 */         return ((IApplicationData)a_objToClone).clone();
/*     */       }
/* 124 */       catch (CloneNotSupportedException cex) {
/* 125 */         throw new IllegalStateException(cex);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 132 */       Method cloneMethod = a_objToClone.getClass().getMethod("clone", new Class[0]);
/*     */       
/* 134 */       cloneMethod.setAccessible(true);
/* 135 */       return cloneMethod.invoke(a_objToClone, new Object[0]);
/*     */     }
/* 137 */     catch (InvocationTargetException iex) {
/* 138 */       throw new IllegalStateException(iex.getTargetException());
/*     */     }
/* 140 */     catch (Throwable ex) {
/* 141 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 152 */     return new DefaultCloneHandler();
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
/* 163 */     if (a_other.getClass().equals(getClass())) {
/* 164 */       return 0;
/*     */     }
/*     */     
/* 167 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DefaultCloneHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */