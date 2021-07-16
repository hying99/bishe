/*     */ package clus.activelearning.matrix;
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
/*     */ public class MatrixUtils
/*     */ {
/*     */   public static double sumVector(double[] matrix) {
/*  17 */     double sum = 0.0D;
/*  18 */     for (int i = 0; i < matrix.length; i++) {
/*  19 */       sum += matrix[i];
/*     */     }
/*  21 */     return sum;
/*     */   }
/*     */   
/*     */   public static double[] multiplyMatrixVector(double[][] matrix, int[] vector) {
/*  25 */     double[] result = new double[vector.length];
/*  26 */     for (int i = 0; i < matrix.length; i++) {
/*  27 */       double sum = 0.0D;
/*  28 */       for (int j = 0; j < vector.length; j++) {
/*  29 */         sum += matrix[i][j] * vector[j];
/*     */       }
/*  31 */       result[i] = sum;
/*     */     } 
/*  33 */     return result;
/*     */   }
/*     */   
/*     */   public static double[][] multiplyMatrixVectorElementWise(double[][] matrix, double[] vector) {
/*  37 */     double[][] result = new double[matrix.length][(matrix[0]).length];
/*  38 */     for (int i = 0; i < matrix.length; i++) {
/*  39 */       for (int j = 0; j < (matrix[i]).length; j++) {
/*  40 */         result[i][j] = matrix[i][j] * vector[j];
/*     */       }
/*     */     } 
/*  43 */     return result;
/*     */   }
/*     */   
/*     */   public static double[][] addMatrixVectorElementWise(double[][] matrix, double[] vector) {
/*  47 */     double[][] result = new double[matrix.length][(matrix[0]).length];
/*  48 */     for (int i = 0; i < matrix.length; i++) {
/*  49 */       for (int j = 0; j < (matrix[i]).length; j++) {
/*  50 */         result[i][j] = matrix[i][j] + vector[j];
/*     */       }
/*     */     } 
/*  53 */     return result;
/*     */   }
/*     */   
/*     */   public static double[][] subtractMatrixVectorElementWise(double[][] matrix, double[] vector) {
/*  57 */     double[][] result = new double[matrix.length][(matrix[0]).length];
/*  58 */     for (int i = 0; i < matrix.length; i++) {
/*  59 */       for (int j = 0; j < (matrix[i]).length; j++) {
/*  60 */         result[i][j] = matrix[i][j] - vector[j];
/*     */       }
/*     */     } 
/*  63 */     return result;
/*     */   }
/*     */   
/*     */   public static double[] sumColumns(double[][] matrix) {
/*  67 */     double[] summedColumns = new double[(matrix[0]).length];
/*  68 */     for (int i = 0; i < (matrix[0]).length; i++) {
/*  69 */       double summedRow = 0.0D;
/*  70 */       for (int j = 0; j < matrix.length; j++) {
/*  71 */         summedRow += matrix[j][i];
/*     */       }
/*  73 */       summedColumns[i] = summedRow;
/*     */     } 
/*  75 */     return summedColumns;
/*     */   }
/*     */   
/*     */   public static double[][] subtractMatrix(double[][] matrix1, double[][] matrix2) {
/*  79 */     double[][] newMatrix = new double[matrix1.length][];
/*  80 */     for (int i = 0; i < newMatrix.length; i++) {
/*  81 */       newMatrix[i] = new double[(matrix1[i]).length];
/*  82 */       for (int j = 0; j < (newMatrix[i]).length; j++) {
/*  83 */         newMatrix[i][j] = matrix1[i][j] - matrix2[i][j];
/*     */       }
/*     */     } 
/*  86 */     return newMatrix;
/*     */   }
/*     */   
/*     */   public static double[][] subtractMatrixWeighted(double[][] matrix1, double weight1, double[][] matrix2, double weight2) {
/*  90 */     double[][] newMatrix = new double[matrix1.length][];
/*  91 */     for (int i = 0; i < newMatrix.length; i++) {
/*  92 */       newMatrix[i] = new double[(matrix1[i]).length];
/*  93 */       for (int j = 0; j < (newMatrix[i]).length; j++) {
/*  94 */         newMatrix[i][j] = matrix1[i][j] * weight1 - matrix2[i][j] * weight2;
/*     */       }
/*     */     } 
/*  97 */     return newMatrix;
/*     */   }
/*     */   
/*     */   public static double[][] addMatrix(double[][] matrix1, double[][] matrix2) {
/* 101 */     double[][] newMatrix = new double[matrix1.length][];
/* 102 */     for (int i = 0; i < newMatrix.length; i++) {
/* 103 */       newMatrix[i] = new double[(matrix1[i]).length];
/* 104 */       for (int j = 0; j < (newMatrix[i]).length; j++) {
/* 105 */         newMatrix[i][j] = matrix1[i][j] + matrix2[i][j];
/*     */       }
/*     */     } 
/* 108 */     return newMatrix;
/*     */   }
/*     */   
/*     */   public static double[][] addWeightedMatrix(double[][] matrix1, double[][] matrix2, double weight1, double weight2) {
/* 112 */     double[][] newMatrix = new double[matrix1.length][];
/* 113 */     for (int i = 0; i < newMatrix.length; i++) {
/* 114 */       newMatrix[i] = new double[(newMatrix[i]).length];
/* 115 */       for (int j = 0; j < (newMatrix[i]).length; j++) {
/* 116 */         newMatrix[i][j] = matrix1[i][j] * weight1 + matrix2[i][j] * weight2;
/*     */       }
/*     */     } 
/* 119 */     return newMatrix;
/*     */   }
/*     */   
/*     */   public static double[] addVector(double[] matrix1, double[] matrix2) {
/* 123 */     double[] newMatrix = new double[matrix1.length];
/* 124 */     for (int i = 0; i < newMatrix.length; i++) {
/* 125 */       newMatrix[i] = matrix1[i] + matrix2[i];
/*     */     }
/* 127 */     return newMatrix;
/*     */   }
/*     */   
/*     */   public static double[] addWeightedVector(double[] matrix1, double weight1, double[] matrix2, double weight2) {
/* 131 */     double[] newMatrix = new double[matrix1.length];
/* 132 */     for (int i = 0; i < newMatrix.length; i++) {
/* 133 */       newMatrix[i] = matrix1[i] * weight1 + matrix2[i] * weight2;
/*     */     }
/* 135 */     return newMatrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double[] multiplyVector(double[] matrix1, double[] matrix2) {
/* 140 */     double[] newMatrix = new double[matrix1.length];
/* 141 */     for (int i = 0; i < newMatrix.length; i++) {
/* 142 */       newMatrix[i] = matrix1[i] * matrix2[i];
/*     */     }
/* 144 */     return newMatrix;
/*     */   }
/*     */   
/*     */   public static double[] sumRows(double[][] matrix) {
/* 148 */     double[] summedRows = new double[matrix.length];
/* 149 */     for (int i = 0; i < summedRows.length; i++) {
/* 150 */       double rowSum = 0.0D;
/* 151 */       for (int j = 0; j < (matrix[i]).length; j++) {
/* 152 */         rowSum += matrix[i][j];
/*     */       }
/* 154 */       summedRows[i] = rowSum;
/*     */     } 
/* 156 */     return summedRows;
/*     */   }
/*     */   
/*     */   public static double[][] squaredDistance(double[][] predictions, double[][] mean) {
/* 160 */     double[][] distance = new double[predictions.length][(predictions[0]).length];
/* 161 */     for (int i = 0; i < predictions.length; i++) {
/* 162 */       for (int j = 0; j < (predictions[0]).length; j++) {
/* 163 */         distance[i][j] = Math.pow(predictions[i][j] - mean[i][j], 2.0D);
/*     */       }
/*     */     } 
/* 166 */     return distance;
/*     */   }
/*     */   
/*     */   public static double[][] mean(double[][] predictions, int forestSize) {
/* 170 */     double forestSizef = forestSize;
/* 171 */     for (int i = 0; i < predictions.length; i++) {
/* 172 */       for (int j = 0; j < (predictions[0]).length; j++) {
/* 173 */         predictions[i][j] = predictions[i][j] / forestSizef;
/*     */       }
/*     */     } 
/* 176 */     return predictions;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\matrix\MatrixUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */