/*     */ package clus.activelearning.indexing;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class LabelIndexer
/*     */   extends Indexer {
/*     */   private LinkedList<LabelIndex> m_Indexer;
/*     */   
/*     */   public LabelIndexer(int batchSize) {
/*  13 */     super(batchSize);
/*  14 */     this.m_Indexer = new LinkedList<>();
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, String label) {
/*  19 */     LabelIndex li = new LabelIndex(index, label, Double.MAX_VALUE);
/*  20 */     getIndexer().add(li);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, String label, double measure) {
/*  25 */     LabelIndex li = new LabelIndex(index, label, measure);
/*  26 */     getIndexer().add(li);
/*     */   }
/*     */ 
/*     */   
/*     */   public void shuffle() {
/*  31 */     Collections.shuffle(this.m_Indexer);
/*  32 */     if (this.m_Indexer.size() > getBatchsize()) {
/*  33 */       this.m_Indexer = new LinkedList<>(this.m_Indexer.subList(0, getBatchsize()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMin(int index, String label, double measure) {
/*  41 */     if (getIndexer().size() < getBatchsize()) {
/*  42 */       LabelIndex li = new LabelIndex(index, label, measure);
/*  43 */       getIndexer().add(li);
/*     */     } else {
/*  45 */       if (!isIndexSet()) {
/*  46 */         updateHighestMeasureLabelIndex();
/*     */       }
/*  48 */       int selectedIndex = getSelectedIndex();
/*  49 */       if (measure < ((LabelIndex)getIndexer().get(selectedIndex)).getMeasure()) {
/*  50 */         LabelIndex li = getIndexer().get(selectedIndex);
/*  51 */         li.setValues(index, label, measure);
/*  52 */         updateHighestMeasureLabelIndex();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMax(int index, String label, double measure) {
/*  60 */     if (getIndexer().size() < getBatchsize()) {
/*  61 */       LabelIndex li = new LabelIndex(index, label, measure);
/*  62 */       getIndexer().add(li);
/*     */     } else {
/*  64 */       if (!isIndexSet()) {
/*  65 */         updateLowestMeasureLabelIndex();
/*     */       }
/*  67 */       int selectedIndex = getSelectedIndex();
/*  68 */       if (measure > ((LabelIndex)getIndexer().get(selectedIndex)).getMeasure()) {
/*  69 */         LabelIndex li = getIndexer().get(selectedIndex);
/*  70 */         li.setValues(index, label, measure);
/*  71 */         updateLowestMeasureLabelIndex();
/*     */       } 
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
/*     */ 
/*     */   
/*     */   private void updateLowestMeasureLabelIndex() {
/*  86 */     int lowestIndex = 0;
/*  87 */     double measure = ((LabelIndex)getIndexer().get(0)).getMeasure();
/*  88 */     for (int i = 1; i < getIndexer().size(); i++) {
/*  89 */       if (((LabelIndex)getIndexer().get(i)).getMeasure() < measure) {
/*  90 */         measure = ((LabelIndex)getIndexer().get(i)).getMeasure();
/*  91 */         lowestIndex = i;
/*     */       } 
/*     */     } 
/*  94 */     setSelectedIndex(lowestIndex);
/*     */   }
/*     */   
/*     */   private void updateHighestMeasureLabelIndex() {
/*  98 */     int highestIndex = 0;
/*  99 */     double measure = ((LabelIndex)getIndexer().get(0)).getMeasure();
/* 100 */     for (int i = 1; i < getIndexer().size(); i++) {
/* 101 */       if (((LabelIndex)getIndexer().get(i)).getMeasure() > measure) {
/* 102 */         measure = ((LabelIndex)getIndexer().get(i)).getMeasure();
/* 103 */         highestIndex = i;
/*     */       } 
/*     */     } 
/* 106 */     setSelectedIndex(highestIndex);
/*     */   }
/*     */   
/*     */   public void printIndexer() {
/* 110 */     HashSet<String> labels = new HashSet<>();
/* 111 */     HashSet<Integer> indexes = new HashSet<>();
/* 112 */     int[] levels = new int[12];
/*     */     
/* 114 */     for (LabelIndex i : this.m_Indexer) {
/* 115 */       labels.add(i.getLabel());
/* 116 */       indexes.add(Integer.valueOf(i.getIndex()));
/* 117 */       levels[(i.getLabel().split("/")).length] = levels[(i.getLabel().split("/")).length] + 1;
/* 118 */       System.out.println("iNDEX " + i.getIndex());
/* 119 */       System.out.println("MEASURE " + i.getMeasure());
/* 120 */       System.out.println("label" + i.getLabel());
/*     */     } 
/* 122 */     System.out.println("labels" + labels.size());
/* 123 */     System.out.println("indexes" + indexes.size());
/* 124 */     System.out.println("levels" + Arrays.toString(levels));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedList<LabelIndex> getIndexer() {
/* 131 */     return this.m_Indexer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndexer(LinkedList<LabelIndex> m_Indexer) {
/* 138 */     this.m_Indexer = m_Indexer;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\indexing\LabelIndexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */