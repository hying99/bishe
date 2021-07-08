/*     */ package clus.error;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import jeans.util.compound.DoubleBooleanCount;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BinaryPredictionList
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_NbPos;
/*     */   protected int m_NbNeg;
/*  18 */   protected transient ArrayList m_PredictionProbabilities = new ArrayList();
/*  19 */   protected transient HashMap m_PredictionProbabilitiesAndCounts = new HashMap<>();
/*     */   
/*     */   public void addExample(boolean actual, double predicted) {
/*  22 */     DoubleBooleanCount value = new DoubleBooleanCount(predicted, actual);
/*  23 */     DoubleBooleanCount prevValue = (DoubleBooleanCount)this.m_PredictionProbabilitiesAndCounts.get(value);
/*  24 */     if (prevValue != null) {
/*  25 */       prevValue.inc();
/*     */     } else {
/*  27 */       this.m_PredictionProbabilitiesAndCounts.put(value, value);
/*     */     } 
/*  29 */     if (actual) {
/*  30 */       this.m_NbPos++;
/*     */     } else {
/*  32 */       this.m_NbNeg++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addInvalid(boolean actual) {
/*  37 */     if (actual) {
/*  38 */       this.m_NbPos++;
/*     */     } else {
/*  40 */       this.m_NbNeg++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sort() {
/*  45 */     this.m_PredictionProbabilities.clear();
/*  46 */     this.m_PredictionProbabilities.addAll(this.m_PredictionProbabilitiesAndCounts.values());
/*  47 */     Collections.sort(this.m_PredictionProbabilities);
/*     */   }
/*     */   
/*     */   public int size() {
/*  51 */     return this.m_PredictionProbabilities.size();
/*     */   }
/*     */   
/*     */   public DoubleBooleanCount get(int i) {
/*  55 */     return this.m_PredictionProbabilities.get(i);
/*     */   }
/*     */   
/*     */   public void clear() {
/*  59 */     this.m_NbPos = 0;
/*  60 */     this.m_NbNeg = 0;
/*  61 */     this.m_PredictionProbabilities.clear();
/*  62 */     this.m_PredictionProbabilitiesAndCounts.clear();
/*     */   }
/*     */   
/*     */   public void clearData() {
/*  66 */     this.m_PredictionProbabilities.clear();
/*     */   }
/*     */   
/*     */   public int getNbPos() {
/*  70 */     return this.m_NbPos;
/*     */   }
/*     */   
/*     */   public int getNbNeg() {
/*  74 */     return this.m_NbNeg;
/*     */   }
/*     */   
/*     */   public double getFrequency() {
/*  78 */     return this.m_NbPos / (this.m_NbPos + this.m_NbNeg);
/*     */   }
/*     */   
/*     */   public boolean hasBothPosAndNegEx() {
/*  82 */     return (this.m_NbPos != 0 && this.m_NbNeg != 0);
/*     */   }
/*     */   
/*     */   public void add(BinaryPredictionList other) {
/*  86 */     this.m_NbPos += other.getNbPos();
/*  87 */     this.m_NbNeg += other.getNbNeg();
/*  88 */     Iterator<DoubleBooleanCount> values = other.m_PredictionProbabilitiesAndCounts.values().iterator();
/*  89 */     while (values.hasNext()) {
/*  90 */       DoubleBooleanCount otherValue = values.next();
/*  91 */       DoubleBooleanCount myValue = (DoubleBooleanCount)this.m_PredictionProbabilitiesAndCounts.get(otherValue);
/*  92 */       if (myValue != null) {
/*  93 */         myValue.inc(otherValue); continue;
/*     */       } 
/*  95 */       DoubleBooleanCount newValue = new DoubleBooleanCount(otherValue);
/*  96 */       this.m_PredictionProbabilitiesAndCounts.put(newValue, newValue);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void copyActual(BinaryPredictionList other) {
/* 103 */     this.m_NbPos = other.getNbPos();
/* 104 */     this.m_NbNeg = other.getNbNeg();
/*     */   }
/*     */   
/*     */   public void copyFull(BinaryPredictionList other) {
/* 108 */     this.m_NbPos = other.getNbPos();
/* 109 */     this.m_NbNeg = other.getNbNeg();
/* 110 */     this.m_PredictionProbabilities.addAll(other.m_PredictionProbabilities);
/* 111 */     this.m_PredictionProbabilitiesAndCounts.putAll(other.m_PredictionProbabilitiesAndCounts);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\BinaryPredictionList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */