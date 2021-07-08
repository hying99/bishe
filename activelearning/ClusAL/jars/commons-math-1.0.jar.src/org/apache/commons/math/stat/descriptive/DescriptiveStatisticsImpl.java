/*     */ package org.apache.commons.math.stat.descriptive;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.util.ResizableDoubleArray;
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
/*     */ public class DescriptiveStatisticsImpl
/*     */   extends DescriptiveStatistics
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -1868088725461221010L;
/*     */   protected int windowSize;
/*     */   protected ResizableDoubleArray eDA;
/*     */   
/*     */   public DescriptiveStatisticsImpl() {
/*  45 */     this(-1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DescriptiveStatisticsImpl(int window) {
/*  54 */     this.eDA = new ResizableDoubleArray();
/*  55 */     setWindowSize(window);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWindowSize() {
/*  63 */     return this.windowSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getValues() {
/*  71 */     double[] copiedArray = new double[this.eDA.getNumElements()];
/*  72 */     System.arraycopy(this.eDA.getElements(), 0, copiedArray, 0, this.eDA.getNumElements());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  78 */     return copiedArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getElement(int index) {
/*  85 */     return this.eDA.getElement(index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/*  92 */     return this.eDA.getNumElements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(double v) {
/*  99 */     if (this.windowSize != -1) {
/* 100 */       if (getN() == this.windowSize) {
/* 101 */         this.eDA.addElementRolling(v);
/* 102 */       } else if (getN() < this.windowSize) {
/* 103 */         this.eDA.addElement(v);
/*     */       } 
/*     */     } else {
/* 106 */       this.eDA.addElement(v);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 114 */     this.eDA.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWindowSize(int windowSize) {
/* 121 */     if (windowSize < 1 && 
/* 122 */       windowSize != -1) {
/* 123 */       throw new IllegalArgumentException("window size must be positive.");
/*     */     }
/*     */ 
/*     */     
/* 127 */     this.windowSize = windowSize;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     if (windowSize != -1 && windowSize < this.eDA.getNumElements()) {
/* 133 */       this.eDA.discardFrontElements(this.eDA.getNumElements() - windowSize);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double apply(UnivariateStatistic stat) {
/* 143 */     return stat.evaluate(this.eDA.getValues(), this.eDA.start(), this.eDA.getNumElements());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\DescriptiveStatisticsImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */