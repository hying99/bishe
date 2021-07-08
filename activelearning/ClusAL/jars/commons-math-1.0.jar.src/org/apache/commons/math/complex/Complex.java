/*     */ package org.apache.commons.math.complex;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ public class Complex
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -6530173849413811929L;
/*  34 */   public static final Complex I = new Complex(0.0D, 1.0D);
/*     */ 
/*     */   
/*  37 */   public static final Complex NaN = new Complex(Double.NaN, Double.NaN);
/*     */ 
/*     */   
/*  40 */   public static final Complex ONE = new Complex(1.0D, 0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double imaginary;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double real;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex(double real, double imaginary) {
/*  56 */     this.real = real;
/*  57 */     this.imaginary = imaginary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double abs() {
/*  66 */     if (isNaN()) {
/*  67 */       return Double.NaN;
/*     */     }
/*  69 */     return Math.sqrt(squareSum());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex add(Complex rhs) {
/*  79 */     if (isNaN() || rhs.isNaN()) {
/*  80 */       return NaN;
/*     */     }
/*     */     
/*  83 */     return new Complex(this.real + rhs.getReal(), this.imaginary + rhs.getImaginary());
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
/*     */   public Complex conjugate() {
/*  95 */     if (isNaN()) {
/*  96 */       return NaN;
/*     */     }
/*     */     
/*  99 */     return new Complex(this.real, -this.imaginary);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex divide(Complex rhs) {
/* 108 */     if (isNaN() || rhs.isNaN()) {
/* 109 */       return NaN;
/*     */     }
/*     */     
/* 112 */     if (Math.abs(rhs.getReal()) < Math.abs(rhs.getImaginary())) {
/* 113 */       double d1 = rhs.getReal() / rhs.getImaginary();
/* 114 */       double d2 = rhs.getReal() * d1 + rhs.getImaginary();
/* 115 */       return new Complex((this.real * d1 + this.imaginary) / d2, (this.imaginary * d1 - this.real) / d2);
/*     */     } 
/*     */     
/* 118 */     double q = rhs.getImaginary() / rhs.getReal();
/* 119 */     double d = rhs.getImaginary() * q + rhs.getReal();
/* 120 */     return new Complex((this.imaginary * q + this.real) / d, (this.imaginary - this.real * q) / d);
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
/*     */   public boolean equals(Object other) {
/*     */     boolean ret;
/* 139 */     if (this == other) {
/* 140 */       ret = true;
/* 141 */     } else if (other == null) {
/* 142 */       ret = false;
/*     */     } else {
/*     */       try {
/* 145 */         Complex rhs = (Complex)other;
/* 146 */         ret = (Double.doubleToRawLongBits(this.real) == Double.doubleToRawLongBits(rhs.getReal()) && Double.doubleToRawLongBits(this.imaginary) == Double.doubleToRawLongBits(rhs.getImaginary()));
/*     */ 
/*     */       
/*     */       }
/* 150 */       catch (ClassCastException ex) {
/*     */         
/* 152 */         ret = false;
/*     */       } 
/*     */     } 
/*     */     
/* 156 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getImaginary() {
/* 165 */     return this.imaginary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getReal() {
/* 174 */     return this.real;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNaN() {
/* 185 */     return (Double.isNaN(this.real) || Double.isNaN(this.imaginary));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex multiply(Complex rhs) {
/* 195 */     if (isNaN() || rhs.isNaN()) {
/* 196 */       return NaN;
/*     */     }
/*     */     
/* 199 */     double p = (this.real + this.imaginary) * (rhs.getReal() + rhs.getImaginary());
/* 200 */     double ac = this.real * rhs.getReal();
/* 201 */     double bd = this.imaginary * rhs.getImaginary();
/* 202 */     return new Complex(ac - bd, p - ac - bd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex negate() {
/* 211 */     if (isNaN()) {
/* 212 */       return NaN;
/*     */     }
/*     */     
/* 215 */     return new Complex(-this.real, -this.imaginary);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double squareSum() {
/* 224 */     return this.real * this.real + this.imaginary * this.imaginary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex subtract(Complex rhs) {
/* 235 */     if (isNaN() || rhs.isNaN()) {
/* 236 */       return NaN;
/*     */     }
/*     */     
/* 239 */     return new Complex(this.real - rhs.getReal(), this.imaginary - rhs.getImaginary());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\complex\Complex.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */