/*     */ package clus.heuristic;
/*     */ 
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusFormat;
/*     */ import java.util.ArrayList;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
/*     */ import org.apache.commons.math.distribution.FDistribution;
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
/*     */ public class FTest
/*     */ {
/*  35 */   public static double[] FTEST_SIG = new double[] { 1.0D, 0.1D, 0.05D, 0.01D, 0.005D, 0.001D, 0.0D };
/*     */   
/*     */   public static double FTEST_LIMIT;
/*     */   public static double[] FTEST_VALUE;
/*  39 */   public static final FDistribution m_FDist = DistributionFactory.newInstance().createFDistribution(1.0D, 1.0D);
/*     */   
/*  41 */   protected static final double[] critical_f_01 = new double[] { 39.8161D, 8.5264D, 5.5225D, 4.5369D, 4.0804D, 3.7636D, 3.61D, 3.4596D, 3.3489D, 3.2761D, 3.24D, 3.1684D, 3.1329D, 3.0976D, 3.0625D, 3.0625D, 3.0276D, 2.9929D, 2.9929D, 2.9584D };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   protected static final double[] critical_f_005 = new double[] { 161.0D, 18.5D, 10.1D, 7.71D, 6.61D, 5.99D, 5.59D, 5.32D, 5.12D, 4.96D, 4.84D, 4.75D, 4.67D, 4.6D, 4.54D, 4.49D, 4.45D, 4.41D, 4.38D, 4.35D, 4.32D, 4.3D, 4.28D, 4.26D, 4.24D, 4.23D, 4.21D, 4.2D, 4.18D, 4.17D };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   protected static final double[] critical_f_001 = new double[] { 4052.0D, 98.5D, 34.1D, 21.2D, 16.3D, 13.7D, 12.2D, 11.3D, 10.6D, 10.0D, 9.65D, 9.33D, 9.07D, 8.86D, 8.68D, 8.53D, 8.4D, 8.29D, 8.18D, 8.1D, 8.02D, 7.95D, 7.88D, 7.82D, 7.77D, 7.72D, 7.68D, 7.64D, 7.6D, 7.56D };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   protected static final double[] critical_f_0005 = new double[] { 15876.0D, 198.81D, 55.5025D, 31.36D, 22.7529D, 18.6624D, 16.2409D, 14.6689D, 13.6161D, 12.8164D, 12.25D, 11.7649D, 11.2896D, 11.0889D, 10.8241D, 10.5625D, 10.3684D, 10.24D, 10.0489D, 9.9225D, 9.8596D, 9.7344D, 9.61D, 9.5481D, 9.4864D, 9.4249D, 9.3636D, 9.3025D, 9.2416D, 9.1809D };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   protected static final double[] critical_f_0001 = new double[] { 405769.0D, 998.56D, 166.41D, 74.1321D, 47.0596D, 35.5216D, 29.16D, 25.4016D, 22.8484D, 21.0681D, 19.7136D, 18.6624D, 17.8084D, 17.1396D, 16.5649D, 16.0801D, 15.6025D, 15.3664D, 15.0544D, 14.8225D, 14.5924D, 14.3641D, 14.2129D, 13.9876D, 13.8384D, 13.7641D, 13.6161D, 13.4689D, 13.3956D, 13.3225D };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getLevelAndComputeArray(double significance) {
/*  74 */     int maxlevel = FTEST_SIG.length - 1;
/*  75 */     for (int level = 0; level < maxlevel; level++) {
/*  76 */       if (Math.abs(significance - FTEST_SIG[level]) / FTEST_SIG[level] < 0.01D) {
/*  77 */         return level;
/*     */       }
/*     */     } 
/*  80 */     FTEST_SIG[maxlevel] = significance;
/*  81 */     initializeFTable(significance);
/*  82 */     return maxlevel;
/*     */   }
/*     */   
/*     */   public static double getCriticalF(int level, int df) {
/*  86 */     switch (level) {
/*     */       case 1:
/*  88 */         if (df <= 20)
/*  89 */           return critical_f_01[df - 1]; 
/*  90 */         if (df <= 30)
/*  91 */           return 2.9D; 
/*  92 */         if (df <= 40)
/*  93 */           return 2.86D; 
/*  94 */         if (df <= 120) {
/*  95 */           return 2.79D;
/*     */         }
/*  97 */         return 2.7D;
/*     */       
/*     */       case 2:
/* 100 */         if (df <= 30)
/* 101 */           return critical_f_005[df - 1]; 
/* 102 */         if (df <= 40)
/* 103 */           return 4.08D; 
/* 104 */         if (df <= 60)
/* 105 */           return 4.0D; 
/* 106 */         if (df <= 120) {
/* 107 */           return 3.92D;
/*     */         }
/* 109 */         return 3.84D;
/*     */       
/*     */       case 3:
/* 112 */         if (df <= 30)
/* 113 */           return critical_f_001[df - 1]; 
/* 114 */         if (df <= 40)
/* 115 */           return 7.31D; 
/* 116 */         if (df <= 60)
/* 117 */           return 7.08D; 
/* 118 */         if (df <= 120) {
/* 119 */           return 6.85D;
/*     */         }
/* 121 */         return 6.63D;
/*     */       
/*     */       case 4:
/* 124 */         if (df <= 30)
/* 125 */           return critical_f_0005[df - 1]; 
/* 126 */         if (df <= 40)
/* 127 */           return 8.82D; 
/* 128 */         if (df <= 60)
/* 129 */           return 8.47D; 
/* 130 */         if (df <= 120) {
/* 131 */           return 8.18D;
/*     */         }
/* 133 */         return 7.9D;
/*     */       
/*     */       case 5:
/* 136 */         if (df <= 30)
/* 137 */           return critical_f_0001[df - 1]; 
/* 138 */         if (df <= 40)
/* 139 */           return 12.6D; 
/* 140 */         if (df <= 60)
/* 141 */           return 11.98D; 
/* 142 */         if (df <= 120) {
/* 143 */           return 11.36D;
/*     */         }
/* 145 */         return 10.82D;
/*     */     } 
/*     */     
/* 148 */     return (df < FTEST_VALUE.length) ? FTEST_VALUE[df] : FTEST_LIMIT;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double getCriticalFCommonsMath(double sig, double df) {
/*     */     try {
/* 154 */       m_FDist.setDenominatorDegreesOfFreedom(df);
/* 155 */       return m_FDist.inverseCumulativeProbability(1.0D - sig);
/* 156 */     } catch (MathException e) {
/* 157 */       System.err.println("F-Distribution error: " + e.getMessage());
/* 158 */       return 0.0D;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void initializeFTable(double sig) {
/* 164 */     int df = 3;
/* 165 */     double value = 0.0D;
/* 166 */     double limit = getCriticalFCommonsMath(sig, 100000.0D);
/* 167 */     ArrayList<Double> values = new ArrayList();
/*     */     while (true) {
/* 169 */       value = getCriticalFCommonsMath(sig, df);
/* 170 */       values.add(new Double(value));
/* 171 */       df++;
/* 172 */       if ((value - limit) / limit <= 0.05D) {
/* 173 */         System.out.println("F-Test = " + sig + " limit = " + ClusFormat.TWO_AFTER_DOT.format(limit) + " values = " + values.size());
/* 174 */         FTEST_LIMIT = limit;
/* 175 */         FTEST_VALUE = new double[values.size() + 3];
/* 176 */         for (int i = 0; i < values.size(); i++) {
/* 177 */           FTEST_VALUE[i + 3] = ((Double)values.get(i)).doubleValue();
/*     */         }
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean ftest(int level, double sst, double ssr, int df) {
/* 186 */     if (level == 0) {
/* 187 */       return true;
/*     */     }
/* 189 */     if (sst <= 0.0D) {
/* 190 */       return false;
/*     */     }
/* 192 */     if (ssr == 0.0D)
/*     */     {
/* 194 */       return true;
/*     */     }
/* 196 */     double f = df * (sst - ssr) / ssr;
/* 197 */     double cf = getCriticalF(level, df);
/* 198 */     return (f > cf);
/*     */   }
/*     */   
/*     */   public static double getSettingSig() {
/* 202 */     return FTEST_SIG[Settings.FTEST_LEVEL];
/*     */   }
/*     */   
/*     */   public static double calcVarianceReductionHeuristic(double n_tot, double ss_tot, double ss_sum) {
/* 206 */     double value = ss_tot - ss_sum;
/*     */     
/* 208 */     if (value < 1.0E-9D)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 214 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/* 216 */     if (Settings.FTEST_LEVEL == 0)
/*     */     {
/*     */       
/* 219 */       return value;
/*     */     }
/*     */     
/* 222 */     int n_2 = (int)Math.floor(n_tot - 2.0D + 0.5D);
/*     */     
/* 224 */     if (n_2 <= 0)
/*     */     {
/* 226 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/* 228 */     if (ftest(Settings.FTEST_LEVEL, ss_tot, ss_sum, n_2))
/*     */     {
/* 230 */       return value;
/*     */     }
/*     */ 
/*     */     
/* 234 */     return Double.NEGATIVE_INFINITY;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\FTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */