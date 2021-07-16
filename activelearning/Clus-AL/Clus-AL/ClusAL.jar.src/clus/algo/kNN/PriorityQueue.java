/*     */ package clus.algo.kNN;
/*     */ 
/*     */ import java.util.Vector;
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
/*     */ public class PriorityQueue
/*     */ {
/*     */   private Vector $elements;
/*     */   private double[] $values;
/*     */   
/*     */   public PriorityQueue(int size) {
/*  38 */     this.$elements = new Vector(size, 0);
/*  39 */     this.$values = new double[size];
/*  40 */     for (int i = 0; i < size; i++) {
/*  41 */       this.$values[i] = -1.0D;
/*     */     }
/*  43 */     this.$elements.setSize(getSize());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/*  50 */     return this.$values.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getElement(int index) {
/*  59 */     return this.$elements.elementAt(index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getValue(int index) {
/*  67 */     return this.$values[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addElement(Object o, double value) {
/*  76 */     int i = 0;
/*  77 */     double curVal = getValue(i);
/*  78 */     while (curVal > 0.0D && curVal <= value && i < getSize() - 1) {
/*  79 */       i++;
/*  80 */       curVal = getValue(i);
/*     */     } 
/*  82 */     if (i < getSize()) {
/*  83 */       this.$elements.insertElementAt(o, i);
/*  84 */       this.$elements.setSize(getSize());
/*     */       
/*  86 */       for (int j = getSize() - 1; j > i; j--) {
/*  87 */         addValue(getValue(j - 1), j);
/*     */       }
/*  89 */       addValue(value, i);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addValue(double value, int index) {
/*  95 */     this.$values[index] = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printValues() {
/* 102 */     System.out.print("[");
/* 103 */     for (int i = 0; i < getSize() - 1; i++) {
/* 104 */       System.out.print(getValue(i) + ";");
/*     */     }
/* 106 */     System.out.println(getValue(getSize() - 1) + "]");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\kNN\PriorityQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */