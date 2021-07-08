/*     */ package org.apache.commons.math.complex;
/*     */ 
/*     */ import org.apache.commons.math.util.MathUtils;
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
/*     */ public class ComplexUtils
/*     */ {
/*     */   public static Complex acos(Complex z) {
/*  49 */     if (z.isNaN()) {
/*  50 */       return Complex.NaN;
/*     */     }
/*     */     
/*  53 */     return Complex.I.negate().multiply(log(z.add(Complex.I.multiply(sqrt1z(z)))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex asin(Complex z) {
/*  64 */     if (z.isNaN()) {
/*  65 */       return Complex.NaN;
/*     */     }
/*     */     
/*  68 */     return Complex.I.negate().multiply(log(sqrt1z(z).add(Complex.I.multiply(z))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex atan(Complex z) {
/*  79 */     if (z.isNaN()) {
/*  80 */       return Complex.NaN;
/*     */     }
/*     */ 
/*     */     
/*  84 */     return Complex.I.multiply(log(Complex.I.add(z).divide(Complex.I.subtract(z)))).divide(new Complex(2.0D, 0.0D));
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
/*     */   public static Complex cos(Complex z) {
/*  96 */     if (z.isNaN()) {
/*  97 */       return Complex.NaN;
/*     */     }
/*     */     
/* 100 */     double a = z.getReal();
/* 101 */     double b = z.getImaginary();
/*     */     
/* 103 */     return new Complex(Math.cos(a) * MathUtils.cosh(b), -Math.sin(a) * MathUtils.sinh(b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex cosh(Complex z) {
/* 114 */     if (z.isNaN()) {
/* 115 */       return Complex.NaN;
/*     */     }
/*     */     
/* 118 */     double a = z.getReal();
/* 119 */     double b = z.getImaginary();
/*     */     
/* 121 */     return new Complex(MathUtils.cosh(a) * Math.cos(b), MathUtils.sinh(a) * Math.sin(b));
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
/*     */   public static Complex exp(Complex z) {
/* 133 */     if (z.isNaN()) {
/* 134 */       return Complex.NaN;
/*     */     }
/*     */     
/* 137 */     double b = z.getImaginary();
/* 138 */     double expA = Math.exp(z.getReal());
/* 139 */     double sinB = Math.sin(b);
/* 140 */     double cosB = Math.cos(b);
/* 141 */     return new Complex(expA * cosB, expA * sinB);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex log(Complex z) {
/* 151 */     if (z.isNaN()) {
/* 152 */       return Complex.NaN;
/*     */     }
/*     */     
/* 155 */     return new Complex(Math.log(z.abs()), Math.atan2(z.getImaginary(), z.getReal()));
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
/*     */   public static Complex pow(Complex y, Complex x) {
/* 167 */     return exp(x.multiply(log(y)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex sin(Complex z) {
/* 177 */     if (z.isNaN()) {
/* 178 */       return Complex.NaN;
/*     */     }
/*     */     
/* 181 */     double a = z.getReal();
/* 182 */     double b = z.getImaginary();
/*     */     
/* 184 */     return new Complex(Math.sin(a) * MathUtils.cosh(b), Math.cos(a) * MathUtils.sinh(b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex sinh(Complex z) {
/* 195 */     if (z.isNaN()) {
/* 196 */       return Complex.NaN;
/*     */     }
/*     */     
/* 199 */     double a = z.getReal();
/* 200 */     double b = z.getImaginary();
/*     */     
/* 202 */     return new Complex(MathUtils.sinh(a) * Math.cos(b), MathUtils.cosh(a) * Math.sin(b));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex sqrt(Complex z) {
/* 213 */     if (z.isNaN()) {
/* 214 */       return Complex.NaN;
/*     */     }
/*     */     
/* 217 */     double a = z.getReal();
/* 218 */     double b = z.getImaginary();
/*     */     
/* 220 */     double t = Math.sqrt((Math.abs(a) + z.abs()) / 2.0D);
/* 221 */     if (a >= 0.0D) {
/* 222 */       return new Complex(t, b / 2.0D * t);
/*     */     }
/* 224 */     return new Complex(Math.abs(z.getImaginary()) / 2.0D * t, MathUtils.indicator(b) * t);
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
/*     */   public static Complex sqrt1z(Complex z) {
/* 236 */     return sqrt(Complex.ONE.subtract(z.multiply(z)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex tan(Complex z) {
/* 246 */     if (z.isNaN()) {
/* 247 */       return Complex.NaN;
/*     */     }
/*     */     
/* 250 */     double a2 = 2.0D * z.getReal();
/* 251 */     double b2 = 2.0D * z.getImaginary();
/* 252 */     double d = Math.cos(a2) + MathUtils.cosh(b2);
/*     */     
/* 254 */     return new Complex(Math.sin(a2) / d, MathUtils.sinh(b2) / d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Complex tanh(Complex z) {
/* 265 */     if (z.isNaN()) {
/* 266 */       return Complex.NaN;
/*     */     }
/*     */     
/* 269 */     double a2 = 2.0D * z.getReal();
/* 270 */     double b2 = 2.0D * z.getImaginary();
/* 271 */     double d = MathUtils.cosh(a2) + Math.cos(b2);
/*     */     
/* 273 */     return new Complex(MathUtils.sinh(a2) / d, Math.sin(b2) / d);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\complex\ComplexUtils.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */