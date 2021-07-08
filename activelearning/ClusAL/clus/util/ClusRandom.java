/*     */ package clus.util;
/*     */ 
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndexer;
/*     */ import clus.main.Settings;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Random;
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
/*     */ public class ClusRandom
/*     */ {
/*     */   public static int m_Preset;
/*     */   public static boolean m_IsPreset;
/*     */   public static final int NB_RANDOM = 7;
/*     */   public static final int RANDOM_TEST_DIR = 0;
/*     */   public static final int RANDOM_SELECTION = 1;
/*     */   public static final int RANDOM_PARAM_TUNE = 2;
/*     */   public static final int RANDOM_CREATE_DATA = 3;
/*     */   public static final int RANDOM_ALGO_INTERNAL = 4;
/*     */   public static final int RANDOM_INT_RANFOR_TREE_DEPTH = 5;
/*     */   public static final int RANDOM_SAMPLE = 6;
/*     */   public static Random[] m_Random;
/*     */   
/*     */   public static Random getRandom(int idx) {
/*  56 */     return m_Random[idx];
/*     */   }
/*     */   
/*     */   public static double nextDouble(int which) {
/*  60 */     return m_Random[which].nextDouble();
/*     */   }
/*     */   
/*     */   public static int nextInt(int which, int max) {
/*  64 */     return m_Random[which].nextInt(max);
/*     */   }
/*     */   
/*     */   public static void initialize(Settings sett) {
/*  68 */     m_Random = new Random[7];
/*  69 */     if (sett.hasRandomSeed()) {
/*  70 */       m_IsPreset = true;
/*  71 */       m_Preset = sett.getRandomSeed();
/*  72 */       for (int i = 0; i < 7; i++) {
/*  73 */         m_Random[i] = new Random(m_Preset);
/*     */       }
/*     */     } else {
/*  76 */       for (int i = 0; i < 7; i++) {
/*  77 */         m_Random[i] = new Random();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void initialize(int initial) {
/*  83 */     m_Random = new Random[7];
/*  84 */     for (int i = 0; i < 7; i++) {
/*  85 */       m_Random[i] = new Random(initial);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void reset(int rnd) {
/*  90 */     if (m_IsPreset) {
/*  91 */       m_Random[rnd] = new Random(m_Preset);
/*     */     } else {
/*  93 */       m_Random[rnd] = new Random(m_Preset);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void getShuffledRandomSubset(LinkedList<?> indexes) {
/*  98 */     Collections.shuffle(indexes);
/*     */   }
/*     */   
/*     */   public static LinkedList<TupleIndexer> getShuffledRandomSubset(LinkedList<TupleIndexer> indexes, int size) {
/* 102 */     Collections.shuffle(indexes);
/* 103 */     LinkedList<TupleIndexer> randomSubset = new LinkedList<>();
/* 104 */     if (!indexes.isEmpty()) {
/* 105 */       int i = 0;
/* 106 */       while (randomSubset.size() < size && i < indexes.size()) {
/* 107 */         randomSubset.add(indexes.get(i));
/* 108 */         i++;
/*     */       } 
/*     */     } 
/* 111 */     return randomSubset;
/*     */   }
/*     */   
/*     */   public static LinkedList<?> getShuffledRandomSubsetPartial(LinkedList<LabelIndex> indexes, int size) {
/* 115 */     Collections.shuffle(indexes);
/* 116 */     LinkedList<LabelIndex> randomSubset = new LinkedList<>();
/* 117 */     if (!indexes.isEmpty()) {
/* 118 */       for (int i = 0; i < size; i++) {
/* 119 */         randomSubset.add(indexes.get(i));
/*     */       }
/*     */     }
/* 122 */     return randomSubset;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clu\\util\ClusRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */