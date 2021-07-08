/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.rows.DataTuple;
/*     */ import java.util.ArrayList;
/*     */ import jeans.list.BitList;
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
/*     */ public class BitVectorStat
/*     */   extends ClusStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  37 */   protected BitList m_Bits = new BitList();
/*     */   protected boolean m_Modified = true;
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  41 */     BitVectorStat stat = new BitVectorStat();
/*  42 */     stat.cloneFrom(this);
/*  43 */     return stat;
/*     */   }
/*     */ 
/*     */   
/*     */   public void cloneFrom(BitVectorStat other) {}
/*     */   
/*     */   public void setSDataSize(int nbex) {
/*  50 */     this.m_Bits.resize(nbex);
/*  51 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void update(ColTarget target, int idx) {
/*  55 */     System.err.println("BitVectorStat: this version of update not implemented");
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/*  59 */     this.m_SumWeight += tuple.getWeight();
/*  60 */     this.m_Bits.setBit(idx);
/*  61 */     this.m_Modified = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void calcMean() {}
/*     */   
/*     */   public String getArrayOfStatistic() {
/*  68 */     return "[" + String.valueOf(this.m_SumWeight) + "]";
/*     */   }
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/*  72 */     return String.valueOf(this.m_SumWeight);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  76 */     this.m_SumWeight = 0.0D;
/*  77 */     this.m_Bits.reset();
/*  78 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/*  82 */     BitVectorStat or = (BitVectorStat)other;
/*  83 */     this.m_SumWeight = or.m_SumWeight;
/*  84 */     this.m_Bits.copy(or.m_Bits);
/*  85 */     this.m_Modified = or.m_Modified;
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/*  89 */     System.err.println("BitVectorStat: addPrediction not implemented");
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/*  93 */     BitVectorStat or = (BitVectorStat)other;
/*  94 */     this.m_SumWeight += or.m_SumWeight;
/*  95 */     this.m_Bits.add(or.m_Bits);
/*  96 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {
/* 100 */     System.err.println("BitVectorStat: addScaled not implemented");
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 104 */     BitVectorStat or = (BitVectorStat)other;
/* 105 */     this.m_SumWeight -= or.m_SumWeight;
/* 106 */     this.m_Bits.subtractFromThis(or.m_Bits);
/* 107 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 111 */     BitVectorStat or = (BitVectorStat)other;
/* 112 */     or.m_SumWeight -= this.m_SumWeight;
/* 113 */     this.m_Bits.subtractFromOther(or.m_Bits);
/* 114 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public int getNbTuples() {
/* 118 */     return this.m_Bits.getNbOnes();
/*     */   }
/*     */   
/*     */   public double[] getNumericPred() {
/* 122 */     System.err.println("BitVectorStat: getNumericPred not implemented");
/* 123 */     return null;
/*     */   }
/*     */   
/*     */   public int[] getNominalPred() {
/* 127 */     System.err.println("BitVectorStat: getNominalPred not implemented");
/* 128 */     return null;
/*     */   }
/*     */   public String getPredictedClassName(int idx) {
/* 131 */     return "";
/*     */   }
/*     */   
/*     */   public void vote(ArrayList votes) {
/* 135 */     System.err.println(getClass().getName() + "BitVectorStat: vote not implemented");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\statistic\BitVectorStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */