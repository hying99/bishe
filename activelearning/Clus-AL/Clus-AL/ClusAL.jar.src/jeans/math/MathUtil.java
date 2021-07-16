/*     */ package jeans.math;
/*     */ 
/*     */ import java.util.ArrayList;
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
/*     */ public class MathUtil
/*     */ {
/*     */   public static final double C1E_6 = 1.0E-6D;
/*     */   public static final double C1E_9 = 1.0E-9D;
/*  31 */   public static final double M_LN2 = Math.log(2.0D);
/*     */   
/*     */   public static double interpolate(double x, ArrayList<double[]> fct) {
/*  34 */     int len = fct.size();
/*  35 */     if (len == 0)
/*  36 */       return Double.NaN; 
/*  37 */     if (len == 1) {
/*     */       
/*  39 */       double[] pt = fct.get(0);
/*  40 */       return pt[1];
/*     */     } 
/*  42 */     double[] pt0 = fct.get(0);
/*  43 */     if (x < pt0[0])
/*     */     {
/*  45 */       return pt0[1];
/*     */     }
/*  47 */     double[] ptn = fct.get(len - 1);
/*  48 */     if (x > ptn[0])
/*     */     {
/*  50 */       return ptn[1];
/*     */     }
/*     */     
/*  53 */     int count = 0;
/*  54 */     double sum = 0.0D; int i;
/*  55 */     for (i = 0; i < len; i++) {
/*  56 */       double[] pt1 = fct.get(i);
/*  57 */       if (x == pt1[0]) {
/*  58 */         sum += pt1[1];
/*  59 */         count++;
/*     */       } 
/*     */     } 
/*  62 */     if (count > 0) {
/*  63 */       return sum / count;
/*     */     }
/*     */     
/*  66 */     for (i = 0; i < len - 1; i++) {
/*  67 */       double[] pt1 = fct.get(i);
/*  68 */       double[] pt2 = fct.get(i + 1);
/*  69 */       if (x >= pt1[0] && x < pt2[0]) {
/*  70 */         double y1 = pt1[1];
/*  71 */         double y2 = pt2[1];
/*  72 */         double x1 = pt1[0];
/*  73 */         double x2 = pt2[0];
/*  74 */         return (x - x1) / (x2 - x1) * (y2 - y1) + y1;
/*     */       } 
/*     */     } 
/*  77 */     return Double.NaN;
/*     */   }
/*     */ 
/*     */   
/*  81 */   protected double[] blackBoxTestInterpolateX = new double[] { 2.0D, 5.0D, 5.0D, 5.0D, 9.5D };
/*  82 */   protected double[] blackBoxTestInterpolateY = new double[] { 3.5D, 3.5D, 4.5D, 5.5D, 1.5D };
/*     */   
/*     */   protected void blackBoxTestInterpolate() {
/*  85 */     ArrayList<double[]> fct = new ArrayList();
/*  86 */     for (int i = 0; i < this.blackBoxTestInterpolateX.length; i++) {
/*  87 */       double[] pt = new double[2];
/*  88 */       pt[0] = this.blackBoxTestInterpolateX[i];
/*  89 */       pt[1] = this.blackBoxTestInterpolateY[i];
/*  90 */       fct.add(pt);
/*     */     } 
/*  92 */     System.out.println("Interpolate x = 0   -> y = 3.5: " + interpolate(0.0D, fct));
/*  93 */     System.out.println("Interpolate x = 4   -> y = 3.5: " + interpolate(4.0D, fct));
/*  94 */     System.out.println("Interpolate x = 5   -> y = 4.5: " + interpolate(5.0D, fct));
/*  95 */     System.out.println("Interpolate x = 7   -> y = 3.8: " + interpolate(7.0D, fct));
/*  96 */     System.out.println("Interpolate x = 9.5 -> y = 1.5: " + interpolate(9.5D, fct));
/*  97 */     System.out.println("Interpolate x = 10  -> y = 1.5: " + interpolate(10.0D, fct));
/*     */   }
/*     */   
/*     */   public static void main(String[] arg) {
/* 101 */     MathUtil mu = new MathUtil();
/* 102 */     mu.blackBoxTestInterpolate();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\MathUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */