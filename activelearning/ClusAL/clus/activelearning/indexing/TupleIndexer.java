/*     */ package clus.activelearning.indexing;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class TupleIndexer
/*     */   extends Indexer {
/*     */   private LinkedList<TupleIndex> m_Indexer;
/*     */   
/*     */   public TupleIndexer(int batchSize) {
/*  11 */     super(batchSize);
/*  12 */     this.m_Indexer = new LinkedList<>();
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index) {
/*  17 */     TupleIndex ti = new TupleIndex(index, 0.0D);
/*  18 */     this.m_Indexer.add(ti);
/*     */   }
/*     */ 
/*     */   
/*     */   public void shuffle() {
/*  23 */     Collections.shuffle(this.m_Indexer);
/*  24 */     if (this.m_Indexer.size() > getBatchsize()) {
/*  25 */       this.m_Indexer = new LinkedList<>(this.m_Indexer.subList(0, getBatchsize()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addMax(int index, double measure) {
/*  31 */     if (this.m_Indexer.size() < getBatchsize()) {
/*  32 */       TupleIndex ti = new TupleIndex(index, measure);
/*  33 */       this.m_Indexer.add(ti);
/*     */     } else {
/*  35 */       if (!isIndexSet()) {
/*  36 */         updateLowestMeasureTupleIndex();
/*     */       }
/*  38 */       int selectedIndex = getSelectedIndex();
/*  39 */       if (measure > ((TupleIndex)getIndexer().get(selectedIndex)).getMeasure()) {
/*  40 */         TupleIndex ti = this.m_Indexer.get(selectedIndex);
/*  41 */         ti.setValues(index, measure);
/*  42 */         updateLowestMeasureTupleIndex();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMin(int index, double measure) {
/*  50 */     if (this.m_Indexer.size() < getBatchsize()) {
/*  51 */       TupleIndex ti = new TupleIndex(index, measure);
/*  52 */       this.m_Indexer.add(ti);
/*     */     } else {
/*  54 */       if (!isIndexSet()) {
/*  55 */         updateHighestMeasureTupleIndex();
/*     */       }
/*  57 */       int selectedIndex = getSelectedIndex();
/*  58 */       if (measure < ((TupleIndex)getIndexer().get(selectedIndex)).getMeasure()) {
/*  59 */         TupleIndex ti = this.m_Indexer.get(selectedIndex);
/*  60 */         ti.setValues(index, measure);
/*  61 */         updateHighestMeasureTupleIndex();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateLowestMeasureTupleIndex() {
/*  67 */     int lowestIndex = 0;
/*  68 */     double measure = ((TupleIndex)this.m_Indexer.get(0)).getMeasure();
/*  69 */     for (int i = 1; i < this.m_Indexer.size(); i++) {
/*  70 */       if (((TupleIndex)this.m_Indexer.get(i)).getMeasure() < measure) {
/*  71 */         measure = ((TupleIndex)this.m_Indexer.get(i)).getMeasure();
/*  72 */         lowestIndex = i;
/*     */       } 
/*     */     } 
/*  75 */     setSelectedIndex(lowestIndex);
/*     */   }
/*     */   
/*     */   private void updateHighestMeasureTupleIndex() {
/*  79 */     int highestIndex = 0;
/*  80 */     double measure = ((TupleIndex)this.m_Indexer.get(0)).getMeasure();
/*  81 */     for (int i = 1; i < this.m_Indexer.size(); i++) {
/*  82 */       if (((TupleIndex)this.m_Indexer.get(i)).getMeasure() > measure) {
/*  83 */         measure = ((TupleIndex)this.m_Indexer.get(i)).getMeasure();
/*  84 */         highestIndex = i;
/*     */       } 
/*     */     } 
/*  87 */     setSelectedIndex(highestIndex);
/*     */   }
/*     */   
/*     */   public void printIndexer() {
/*  91 */     for (TupleIndex i : this.m_Indexer) {
/*  92 */       System.out.println("iNDEX " + i.getIndex());
/*  93 */       System.out.println("MEASURE " + i.getMeasure());
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
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedList<TupleIndex> getIndexer() {
/* 110 */     return this.m_Indexer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndexer(LinkedList<TupleIndex> m_Indexer) {
/* 117 */     this.m_Indexer = m_Indexer;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\indexing\TupleIndexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */