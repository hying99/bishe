/*     */ package clus.main;
/*     */ 
/*     */ import jeans.io.MyFile;
/*     */ import jeans.resource.ResourceInfo;
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
/*     */ public class ClusStat
/*     */ {
/*     */   public static long m_PrevTime;
/*     */   public static long m_InitialMemory;
/*     */   public static long m_LoadedMemory;
/*     */   public static long m_FinalMemory;
/*     */   public static long m_TimeTest;
/*     */   public static long m_TimeSplit;
/*     */   public static long m_TimeStat;
/*     */   public static long m_TimeSort;
/*     */   public static long m_TimeHeur;
/*     */   public static double m_TTimeTest;
/*     */   public static double m_TTimeSplit;
/*     */   public static double m_TTimeStat;
/*     */   public static double m_TTimeSort;
/*     */   public static double m_TTimeHeur;
/*     */   public static double m_TTimeTotal;
/*     */   
/*     */   public static void initTime() {
/*  51 */     m_PrevTime = ResourceInfo.getCPUTime();
/*     */   }
/*     */   
/*     */   public static void deltaStat() {
/*  55 */     long now = ResourceInfo.getCPUTime();
/*  56 */     m_TimeStat += now - m_PrevTime;
/*  57 */     m_PrevTime = now;
/*     */   }
/*     */   
/*     */   public static void deltaTest() {
/*  61 */     long now = ResourceInfo.getCPUTime();
/*  62 */     m_TimeTest += now - m_PrevTime;
/*  63 */     m_PrevTime = now;
/*     */   }
/*     */   
/*     */   public static void deltaSplit() {
/*  67 */     long now = ResourceInfo.getCPUTime();
/*  68 */     m_TimeSplit += now - m_PrevTime;
/*  69 */     m_PrevTime = now;
/*     */   }
/*     */   
/*     */   public static void deltaSort() {
/*  73 */     long now = ResourceInfo.getCPUTime();
/*  74 */     m_TimeSort += now - m_PrevTime;
/*  75 */     m_PrevTime = now;
/*     */   }
/*     */   
/*     */   public static void deltaHeur() {
/*  79 */     long now = ResourceInfo.getCPUTime();
/*  80 */     m_TimeHeur += now - m_PrevTime;
/*  81 */     m_PrevTime = now;
/*     */   }
/*     */   
/*     */   public static void resetTimes() {
/*  85 */     m_TimeTest = 0L;
/*  86 */     m_TimeSplit = 0L;
/*  87 */     m_TimeStat = 0L;
/*  88 */     m_TimeSort = 0L;
/*  89 */     m_TimeHeur = 0L;
/*     */   }
/*     */   
/*     */   public static double addToTotal(long value, int div) {
/*  93 */     double mean = value / div;
/*  94 */     m_TTimeTotal += mean;
/*  95 */     return mean;
/*     */   }
/*     */   
/*     */   public static void addTimes(int div) {
/*  99 */     m_TTimeTest += m_TimeTest / div;
/* 100 */     m_TTimeSplit += m_TimeSplit / div;
/* 101 */     m_TTimeStat += m_TimeStat / div;
/* 102 */     m_TTimeSort += m_TimeSort / div;
/* 103 */     m_TTimeHeur += m_TimeHeur / div;
/*     */   }
/*     */   
/*     */   public static void updateMaxMemory() {
/* 107 */     m_FinalMemory = Math.max(m_FinalMemory, ResourceInfo.getMemorySize());
/*     */   }
/*     */   
/*     */   public static void show() {
/* 111 */     double sum = m_TTimeStat + m_TTimeTest + m_TTimeSplit + m_TTimeSort + m_TTimeHeur;
/*     */     
/* 113 */     System.out.println("Mem usage (KB) [initial, loaded, max]: [" + m_InitialMemory + "," + m_LoadedMemory + "," + m_FinalMemory + "]");
/* 114 */     System.out.println("Total estimate: " + m_TTimeTotal);
/* 115 */     System.out.println("Total induction time: " + sum);
/* 116 */     System.out.println("Time for stats: " + m_TTimeStat);
/* 117 */     System.out.println("Time for evaluating: " + m_TTimeTest);
/* 118 */     System.out.println("Time for splitting: " + m_TTimeSplit);
/* 119 */     System.out.println("Time for sorting: " + m_TTimeSort);
/* 120 */     System.out.println("Time for heuristics: " + m_TTimeHeur);
/*     */     
/* 122 */     MyFile file = new MyFile("stats.txt");
/* 123 */     file.log("Mem usage (KB) [initial, loaded, max]: [" + m_InitialMemory + "," + m_LoadedMemory + "," + m_FinalMemory + "]");
/* 124 */     file.log("Total estimate: " + m_TTimeTotal);
/* 125 */     file.log("Total induction time: " + sum);
/* 126 */     file.log("Time for stats: " + m_TTimeStat);
/* 127 */     file.log("Time for evaluating: " + m_TTimeTest);
/* 128 */     file.log("Time for splitting: " + m_TTimeSplit);
/* 129 */     file.log("Time for sorting: " + m_TTimeSort);
/* 130 */     file.log("Time for heuristics: " + m_TTimeHeur);
/* 131 */     file.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\main\ClusStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */