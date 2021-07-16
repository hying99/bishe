/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ public class SumPairwiseDistancesStat
/*     */   extends BitVectorStat
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int linearParameter = 10;
/*  14 */   public static final Random m_Random = new Random(0L);
/*     */   
/*     */   protected double m_SVarS;
/*     */   protected ClusDistance m_Distance;
/*  18 */   protected int m_Efficiency = 2;
/*     */   
/*     */   public SumPairwiseDistancesStat(ClusDistance dist) {
/*  21 */     this.m_Distance = dist;
/*     */   }
/*     */   
/*     */   public SumPairwiseDistancesStat(ClusDistance dist, int efflvl) {
/*  25 */     this.m_Distance = dist;
/*  26 */     this.m_Efficiency = efflvl;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  30 */     return new SumPairwiseDistancesStat(this.m_Distance, this.m_Efficiency);
/*     */   }
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale, RowData data) {
/*  34 */     optimizePreCalc(data);
/*  35 */     return this.m_SVarS;
/*     */   }
/*     */   
/*     */   public int getEfficiencyLevel() {
/*  39 */     return this.m_Efficiency;
/*     */   }
/*     */   
/*     */   public void optimizePreCalc(RowData data) {
/*  43 */     if (!this.m_Modified)
/*  44 */       return;  switch (getEfficiencyLevel()) {
/*     */       case 1:
/*  46 */         optimizeLogPreCalc(data);
/*     */         break;
/*     */       case 2:
/*  49 */         optimizeLinearPreCalc(data);
/*     */         break;
/*     */       default:
/*  52 */         optimizePreCalcExact(data);
/*     */         break;
/*     */     } 
/*  55 */     this.m_Modified = false;
/*     */   }
/*     */   
/*     */   public double calcDistance(DataTuple t1, DataTuple t2) {
/*  59 */     return this.m_Distance.calcDistance(t1, t2);
/*     */   }
/*     */   
/*     */   public double calcDistanceToCentroid(DataTuple t1) {
/*  63 */     return this.m_Distance.calcDistanceToCentroid(t1, this);
/*     */   }
/*     */   
/*     */   public void optimizePreCalcExact(RowData data) {
/*  67 */     this.m_SVarS = 0.0D;
/*  68 */     double sumWiDiag = 0.0D;
/*  69 */     double sumWiTria = 0.0D;
/*  70 */     int nb = this.m_Bits.size();
/*  71 */     for (int i = 0; i < nb; i++) {
/*  72 */       if (this.m_Bits.getBit(i)) {
/*  73 */         DataTuple a = data.getTuple(i);
/*  74 */         double a_weight = a.getWeight();
/*     */         
/*  76 */         for (int j = 0; j < i; j++) {
/*  77 */           if (this.m_Bits.getBit(j)) {
/*  78 */             DataTuple b = data.getTuple(j);
/*  79 */             double wi = a_weight * b.getWeight();
/*  80 */             double d = calcDistance(a, b);
/*  81 */             this.m_SVarS += wi * d;
/*  82 */             sumWiTria += wi;
/*     */           } 
/*     */         } 
/*     */         
/*  86 */         sumWiDiag += a_weight * a_weight;
/*     */       } 
/*     */     } 
/*  89 */     this.m_SVarS = getTotalWeight() * this.m_SVarS / (2.0D * sumWiTria + sumWiDiag);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final int Sampling_K_Random(int a, int b) {
/*  94 */     return a + m_Random.nextInt(b + 1);
/*     */   }
/*     */   
/*     */   public void optimizeLinearPreCalc(RowData data) {
/*  98 */     optimizeLinearPreCalc(data, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void optimizeLinearPreCalc(RowData data, int samplenb) {
/* 105 */     this.m_SVarS = 0.0D;
/* 106 */     int nb = this.m_Bits.size();
/*     */     
/* 108 */     int nb_total = 0;
/* 109 */     int[] indices = new int[nb];
/* 110 */     for (int i = 0; i < nb; i++) {
/* 111 */       if (this.m_Bits.getBit(i)) indices[nb_total++] = i; 
/*     */     } 
/* 113 */     if (nb_total < samplenb) {
/*     */       
/* 115 */       optimizePreCalcExact(data);
/*     */       
/*     */       return;
/*     */     } 
/* 119 */     double sumWi = 0.0D;
/* 120 */     for (int j = 0; j < nb; j++) {
/* 121 */       if (this.m_Bits.getBit(j)) {
/* 122 */         DataTuple a = data.getTuple(j);
/* 123 */         double a_weight = a.getWeight();
/*     */         
/* 125 */         int T = 0;
/* 126 */         int M = 0;
/* 127 */         while (M < samplenb) {
/* 128 */           if (Sampling_K_Random(0, nb_total - T - 1) < samplenb - M) {
/* 129 */             DataTuple b = data.getTuple(indices[T]);
/* 130 */             double wi = a_weight * b.getWeight();
/* 131 */             double d = calcDistance(a, b);
/* 132 */             this.m_SVarS += wi * d;
/* 133 */             sumWi += wi;
/* 134 */             M++;
/*     */           } 
/* 136 */           T++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 140 */     this.m_SVarS = getTotalWeight() * this.m_SVarS / sumWi / 2.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public void optimizePairwiseLinearPreCalc(RowData data) {
/* 145 */     this.m_SVarS = 0.0D;
/* 146 */     int nb = this.m_Bits.size();
/*     */     
/* 148 */     int nb_total = 0;
/* 149 */     int[] indices = new int[nb];
/* 150 */     for (int i = 0; i < nb; i++) {
/* 151 */       if (this.m_Bits.getBit(i)) indices[nb_total++] = i;
/*     */     
/*     */     } 
/* 154 */     double sumWi = 0.0D;
/* 155 */     for (int j = 0; j < nb_total; j++) {
/*     */       
/* 157 */       int a = Sampling_K_Random(0, nb_total - 1);
/* 158 */       DataTuple dt1 = data.getTuple(indices[a]);
/*     */       
/* 160 */       int b = Sampling_K_Random(0, nb_total - 1);
/* 161 */       DataTuple dt2 = data.getTuple(indices[b]);
/*     */       
/* 163 */       double wi = dt1.getWeight() * dt2.getWeight();
/* 164 */       this.m_SVarS += wi * calcDistance(dt1, dt2);
/* 165 */       sumWi += wi;
/*     */     } 
/* 167 */     this.m_SVarS = getTotalWeight() * this.m_SVarS / sumWi;
/*     */   }
/*     */ 
/*     */   
/*     */   public void optimizeLogPreCalc(RowData data) {
/* 172 */     int nb = getNbTuples();
/* 173 */     int lognb = (int)Math.floor(Math.log(nb) / Math.log(2.0D)) + 1;
/* 174 */     optimizeLinearPreCalc(data, lognb);
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/* 178 */     super.copy(other);
/* 179 */     SumPairwiseDistancesStat or = (SumPairwiseDistancesStat)other;
/* 180 */     this.m_SVarS = or.m_SVarS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcMean() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusDistance getDistance() {
/* 192 */     return this.m_Distance;
/*     */   }
/*     */   
/*     */   public String getDistanceName() {
/* 196 */     return getDistance().getDistanceName();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\SumPairwiseDistancesStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */