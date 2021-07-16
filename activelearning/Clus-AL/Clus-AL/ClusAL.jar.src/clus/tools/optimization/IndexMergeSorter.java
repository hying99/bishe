/*     */ package clus.tools.optimization;
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
/*     */ public class IndexMergeSorter
/*     */ {
/*     */   private static double[] m_workArray;
/*     */   private static double[] m_tempArray;
/*     */   private static int[] m_indOfSorted;
/*     */   private static int[] m_indTemp;
/*     */   
/*     */   public static int[] sort(double[] targetArray, boolean useAbsoluteValues) {
/*  42 */     m_workArray = new double[targetArray.length];
/*     */     
/*  44 */     if (useAbsoluteValues) {
/*  45 */       for (int iEl = 0; iEl < targetArray.length; iEl++) {
/*  46 */         m_workArray[iEl] = Math.abs(targetArray[iEl]);
/*     */       }
/*     */     } else {
/*  49 */       m_workArray = (double[])targetArray.clone();
/*     */     } 
/*     */     
/*  52 */     int nbOfEl = m_workArray.length;
/*     */ 
/*     */     
/*  55 */     m_indOfSorted = new int[nbOfEl];
/*  56 */     for (int i = 0; i < nbOfEl; i++) {
/*  57 */       m_indOfSorted[i] = i;
/*     */     }
/*  59 */     m_indTemp = new int[(nbOfEl + 1) / 2];
/*  60 */     m_tempArray = new double[(nbOfEl + 1) / 2];
/*  61 */     mergesort(0, nbOfEl - 1);
/*     */     
/*  63 */     return m_indOfSorted;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] sortSubArray(double[] targetArray, boolean[] subArray, int nbOfTrueInSubArray, boolean useAbsoluteValues) {
/*  73 */     int nbOfEl = m_workArray.length;
/*     */     
/*  75 */     m_indOfSorted = new int[nbOfTrueInSubArray];
/*  76 */     m_workArray = new double[nbOfTrueInSubArray];
/*     */     
/*  78 */     int iSubArray = 0;
/*  79 */     for (int iWholeArray = 0; iWholeArray < nbOfEl; iWholeArray++) {
/*  80 */       if (subArray[iWholeArray]) {
/*  81 */         if (useAbsoluteValues) {
/*  82 */           m_workArray[iSubArray] = Math.abs(targetArray[iWholeArray]);
/*     */         } else {
/*  84 */           m_workArray[iSubArray] = targetArray[iWholeArray];
/*     */         } 
/*     */         
/*  87 */         m_indOfSorted[iSubArray] = iWholeArray;
/*  88 */         iSubArray++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  95 */     m_indTemp = new int[(nbOfTrueInSubArray + 1) / 2];
/*  96 */     m_tempArray = new double[(nbOfTrueInSubArray + 1) / 2];
/*  97 */     mergesort(0, nbOfTrueInSubArray - 1);
/*     */     
/*  99 */     return m_indOfSorted;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void mergesort(int lowestElement, int highestElement) {
/* 105 */     if (lowestElement < highestElement) {
/*     */       
/* 107 */       int middleElement = (lowestElement + highestElement) / 2;
/* 108 */       mergesort(lowestElement, middleElement);
/* 109 */       mergesort(middleElement + 1, highestElement);
/* 110 */       merge(lowestElement, middleElement, highestElement);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void merge(int lowestElement, int midElement, int highestElement) {
/* 120 */     int i = 0, j = lowestElement;
/*     */     
/* 122 */     while (j <= midElement) {
/* 123 */       m_indTemp[i] = m_indOfSorted[j];
/* 124 */       m_tempArray[i++] = m_workArray[j++];
/*     */     } 
/*     */     
/* 127 */     i = 0; int k = lowestElement;
/*     */     
/* 129 */     while (k < j && j <= highestElement) {
/* 130 */       if (m_tempArray[i] <= m_workArray[j]) {
/* 131 */         m_indOfSorted[k] = m_indTemp[i];
/* 132 */         m_workArray[k++] = m_tempArray[i++]; continue;
/*     */       } 
/* 134 */       m_indOfSorted[k] = m_indOfSorted[j];
/* 135 */       m_workArray[k++] = m_workArray[j++];
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 140 */     while (k < j) {
/* 141 */       m_indOfSorted[k] = m_indTemp[i];
/* 142 */       m_workArray[k++] = m_tempArray[i++];
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\tools\optimization\IndexMergeSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */