/*     */ package org.apache.commons.math;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
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
/*     */ public class MathException
/*     */   extends Exception
/*     */ {
/*     */   static final long serialVersionUID = -8594613561393443827L;
/*     */   private static final boolean JDK_SUPPORTS_NESTED;
/*     */   private final Throwable rootCause;
/*     */   
/*     */   static {
/*  42 */     boolean flag = false;
/*     */     try {
/*  44 */       Throwable.class.getDeclaredMethod("getCause", new Class[0]);
/*  45 */       flag = true;
/*  46 */     } catch (NoSuchMethodException ex) {
/*  47 */       flag = false;
/*     */     } 
/*  49 */     JDK_SUPPORTS_NESTED = flag;
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
/*     */   public MathException() {
/*  63 */     this.rootCause = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MathException(String msg) {
/*  73 */     super(msg);
/*  74 */     this.rootCause = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MathException(Throwable rootCause) {
/*  85 */     super((rootCause == null) ? null : rootCause.getMessage());
/*  86 */     this.rootCause = rootCause;
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
/*     */   public MathException(String msg, Throwable rootCause) {
/*  98 */     super(msg);
/*  99 */     this.rootCause = rootCause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Throwable getCause() {
/* 108 */     return this.rootCause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStackTrace() {
/* 115 */     printStackTrace(System.err);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintStream out) {
/* 124 */     synchronized (out) {
/* 125 */       PrintWriter pw = new PrintWriter(out, false);
/* 126 */       printStackTrace(pw);
/*     */       
/* 128 */       pw.flush();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintWriter out) {
/* 138 */     synchronized (out) {
/* 139 */       super.printStackTrace(out);
/* 140 */       if (this.rootCause != null && !JDK_SUPPORTS_NESTED) {
/* 141 */         out.print("Caused by: ");
/* 142 */         this.rootCause.printStackTrace(out);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\MathException.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */