/*     */ package org.jgap.util;
/*     */ 
/*     */ import java.util.Hashtable;
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
/*     */ 
/*     */ public abstract class MultiClassLoader
/*     */   extends ClassLoader
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*  37 */   private Hashtable classes = new Hashtable<Object, Object>();
/*     */ 
/*     */ 
/*     */   
/*     */   private char classNameReplacementChar;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean monitorOn = false;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean sourceMonitorOn = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class loadClass(String className) throws ClassNotFoundException {
/*  55 */     return loadClass(className, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
/*  63 */     monitor(">> MultiClassLoader.loadClass(" + className + ", " + resolveIt + ")");
/*     */ 
/*     */     
/*  66 */     Class<?> result = (Class)this.classes.get(className);
/*  67 */     if (result != null) {
/*  68 */       monitor(">> returning cached result.");
/*  69 */       return result;
/*     */     } 
/*     */     
/*     */     try {
/*  73 */       result = findSystemClass(className);
/*  74 */       monitor(">> returning system class (in CLASSPATH).");
/*  75 */       return result;
/*  76 */     } catch (ClassNotFoundException e) {
/*  77 */       monitor(">> Not a system class.");
/*     */ 
/*     */ 
/*     */       
/*  81 */       byte[] classBytes = loadClassBytes(className);
/*  82 */       if (classBytes == null) {
/*  83 */         throw new ClassNotFoundException();
/*     */       }
/*     */       
/*  86 */       result = defineClass(className, classBytes, 0, classBytes.length);
/*  87 */       if (result == null) {
/*  88 */         throw new ClassFormatError();
/*     */       }
/*     */       
/*  91 */       if (resolveIt) {
/*  92 */         resolveClass(result);
/*     */       }
/*  94 */       this.classes.put(className, result);
/*  95 */       monitor(">> Returning newly loaded class.");
/*  96 */       return result;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClassNameReplacementChar(char replacement) {
/* 107 */     this.classNameReplacementChar = replacement;
/*     */   }
/*     */   
/*     */   protected abstract byte[] loadClassBytes(String paramString);
/*     */   
/*     */   protected String formatClassName(String className) {
/* 113 */     if (this.classNameReplacementChar == '\000')
/*     */     {
/* 115 */       return className.replace('.', '/') + ".class";
/*     */     }
/*     */ 
/*     */     
/* 119 */     return className.replace('.', this.classNameReplacementChar) + ".class";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void monitor(String text) {
/* 125 */     if (this.monitorOn)
/* 126 */       print(text); 
/*     */   }
/*     */   
/*     */   protected static void print(String text) {
/* 130 */     System.out.println(text);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\MultiClassLoader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */