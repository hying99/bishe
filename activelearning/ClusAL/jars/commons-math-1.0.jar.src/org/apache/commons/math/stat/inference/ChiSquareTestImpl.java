/*     */ package org.apache.commons.math.stat.inference;
/*     */ 
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.ChiSquaredDistribution;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
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
/*     */ public class ChiSquareTestImpl
/*     */   implements ChiSquareTest
/*     */ {
/*  30 */   private DistributionFactory distributionFactory = null;
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
/*     */   public double chiSquare(double[] expected, long[] observed) throws IllegalArgumentException {
/*  48 */     double sumSq = 0.0D;
/*  49 */     double dev = 0.0D;
/*  50 */     if (expected.length < 2 || expected.length != observed.length) {
/*  51 */       throw new IllegalArgumentException("observed, expected array lengths incorrect");
/*     */     }
/*     */     
/*  54 */     if (!isPositive(expected) || !isNonNegative(observed)) {
/*  55 */       throw new IllegalArgumentException("observed counts must be non-negative and expected counts must be postive");
/*     */     }
/*     */     
/*  58 */     for (int i = 0; i < observed.length; i++) {
/*  59 */       dev = observed[i] - expected[i];
/*  60 */       sumSq += dev * dev / expected[i];
/*     */     } 
/*  62 */     return sumSq;
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
/*     */   public double chiSquareTest(double[] expected, long[] observed) throws IllegalArgumentException, MathException {
/*  74 */     ChiSquaredDistribution chiSquaredDistribution = getDistributionFactory().createChiSquareDistribution(expected.length - 1.0D);
/*     */ 
/*     */     
/*  77 */     return 1.0D - chiSquaredDistribution.cumulativeProbability(chiSquare(expected, observed));
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
/*     */   public boolean chiSquareTest(double[] expected, long[] observed, double alpha) throws IllegalArgumentException, MathException {
/*  92 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  93 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*     */     }
/*     */     
/*  96 */     return (chiSquareTest(expected, observed) < alpha);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double chiSquare(long[][] counts) throws IllegalArgumentException {
/* 106 */     checkArray(counts);
/* 107 */     int nRows = counts.length;
/* 108 */     int nCols = (counts[0]).length;
/*     */ 
/*     */     
/* 111 */     double[] rowSum = new double[nRows];
/* 112 */     double[] colSum = new double[nCols];
/* 113 */     double total = 0.0D;
/* 114 */     for (int row = 0; row < nRows; row++) {
/* 115 */       for (int col = 0; col < nCols; col++) {
/* 116 */         rowSum[row] = rowSum[row] + counts[row][col];
/* 117 */         colSum[col] = colSum[col] + counts[row][col];
/* 118 */         total += counts[row][col];
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 123 */     double sumSq = 0.0D;
/* 124 */     double expected = 0.0D;
/* 125 */     for (int i = 0; i < nRows; i++) {
/* 126 */       for (int col = 0; col < nCols; col++) {
/* 127 */         expected = rowSum[i] * colSum[col] / total;
/* 128 */         sumSq += (counts[i][col] - expected) * (counts[i][col] - expected) / expected;
/*     */       } 
/*     */     } 
/*     */     
/* 132 */     return sumSq;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double chiSquareTest(long[][] counts) throws IllegalArgumentException, MathException {
/* 143 */     checkArray(counts);
/* 144 */     double df = (counts.length - 1.0D) * ((counts[0]).length - 1.0D);
/* 145 */     ChiSquaredDistribution chiSquaredDistribution = getDistributionFactory().createChiSquareDistribution(df);
/*     */     
/* 147 */     return 1.0D - chiSquaredDistribution.cumulativeProbability(chiSquare(counts));
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
/*     */   public boolean chiSquareTest(long[][] counts, double alpha) throws IllegalArgumentException, MathException {
/* 160 */     if (alpha <= 0.0D || alpha > 0.5D) {
/* 161 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*     */     }
/* 163 */     return (chiSquareTest(counts) < alpha);
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
/*     */   private void checkArray(long[][] in) throws IllegalArgumentException {
/* 176 */     if (in.length < 2) {
/* 177 */       throw new IllegalArgumentException("Input table must have at least two rows");
/*     */     }
/*     */     
/* 180 */     if ((in[0]).length < 2) {
/* 181 */       throw new IllegalArgumentException("Input table must have at least two columns");
/*     */     }
/*     */     
/* 184 */     if (!isRectangular(in)) {
/* 185 */       throw new IllegalArgumentException("Input table must be rectangular");
/*     */     }
/*     */     
/* 188 */     if (!isNonNegative(in)) {
/* 189 */       throw new IllegalArgumentException("All entries in input 2-way table must be non-negative");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DistributionFactory getDistributionFactory() {
/* 201 */     if (this.distributionFactory == null) {
/* 202 */       this.distributionFactory = DistributionFactory.newInstance();
/*     */     }
/* 204 */     return this.distributionFactory;
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
/*     */   private boolean isRectangular(long[][] in) {
/* 218 */     for (int i = 1; i < in.length; i++) {
/* 219 */       if ((in[i]).length != (in[0]).length) {
/* 220 */         return false;
/*     */       }
/*     */     } 
/* 223 */     return true;
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
/*     */   private boolean isPositive(double[] in) {
/* 235 */     for (int i = 0; i < in.length; i++) {
/* 236 */       if (in[i] <= 0.0D) {
/* 237 */         return false;
/*     */       }
/*     */     } 
/* 240 */     return true;
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
/*     */   private boolean isNonNegative(long[] in) {
/* 252 */     for (int i = 0; i < in.length; i++) {
/* 253 */       if (in[i] < 0L) {
/* 254 */         return false;
/*     */       }
/*     */     } 
/* 257 */     return true;
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
/*     */   private boolean isNonNegative(long[][] in) {
/* 269 */     for (int i = 0; i < in.length; i++) {
/* 270 */       for (int j = 0; j < (in[i]).length; j++) {
/* 271 */         if (in[i][j] < 0L) {
/* 272 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 276 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\inference\ChiSquareTestImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */