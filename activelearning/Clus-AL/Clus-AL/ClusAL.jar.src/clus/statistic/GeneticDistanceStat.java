/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.util.ClusFormat;
/*     */ import java.text.NumberFormat;
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
/*     */ public class GeneticDistanceStat
/*     */   extends BitVectorStat
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public int m_NbTarget;
/*     */   public NominalAttrType[] m_Attrs;
/*     */   
/*     */   public GeneticDistanceStat(NominalAttrType[] nomAtts) {
/*  27 */     this.m_NbTarget = nomAtts.length;
/*  28 */     this.m_Attrs = nomAtts;
/*     */   }
/*     */   
/*     */   public BitList getBits() {
/*  32 */     return this.m_Bits;
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/*  36 */     this.m_SumWeight += tuple.getWeight();
/*  37 */     this.m_Bits.setBit(idx);
/*  38 */     this.m_Modified = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTupleIndex(int index) {
/*  44 */     int size = getBits().size();
/*  45 */     int nbones = 0;
/*  46 */     int i = 0;
/*  47 */     while (nbones <= index && i < size) {
/*  48 */       if (getBits().getBit(i)) {
/*  49 */         nbones++;
/*     */       }
/*  51 */       i++;
/*     */     } 
/*  53 */     if (nbones == index + 1) {
/*  54 */       return i - 1;
/*     */     }
/*     */     
/*  57 */     System.err.println("error in getTuple (GeneticDistanceStat), requesting tuple" + index);
/*  58 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/*  64 */     this.m_SumWeight = 0.0D;
/*  65 */     this.m_Bits.reset();
/*  66 */     this.m_Modified = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public GeneticDistanceStat cloneStat() {
/*  71 */     GeneticDistanceStat stat = new GeneticDistanceStat(this.m_Attrs);
/*  72 */     stat.cloneFrom(this);
/*  73 */     return stat;
/*     */   }
/*     */   
/*     */   public void cloneFrom(GeneticDistanceStat other) {
/*  77 */     int nb = other.m_Bits.size();
/*  78 */     this.m_NbTarget = other.m_NbTarget;
/*  79 */     this.m_Attrs = other.m_Attrs;
/*  80 */     if (nb > 0) {
/*  81 */       setSDataSize(nb);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void copy(ClusStatistic other) {
/*  87 */     GeneticDistanceStat or = (GeneticDistanceStat)other;
/*  88 */     this.m_SumWeight = or.m_SumWeight;
/*  89 */     this.m_Bits.copy(or.m_Bits);
/*  90 */     this.m_Modified = or.m_Modified;
/*  91 */     this.m_NbTarget = or.m_NbTarget;
/*  92 */     this.m_Attrs = or.m_Attrs;
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/*  96 */     GeneticDistanceStat or = (GeneticDistanceStat)other;
/*  97 */     this.m_SumWeight += weight * or.m_SumWeight;
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/* 101 */     GeneticDistanceStat or = (GeneticDistanceStat)other;
/* 102 */     this.m_SumWeight += or.m_SumWeight;
/* 103 */     this.m_Bits.add(or.m_Bits);
/* 104 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {
/* 108 */     GeneticDistanceStat or = (GeneticDistanceStat)other;
/* 109 */     this.m_SumWeight += scale * or.m_SumWeight;
/*     */   }
/*     */   
/*     */   public void subtractFromThis(BitList bits) {
/* 113 */     this.m_SumWeight -= bits.getNbOnes();
/* 114 */     this.m_Bits.subtractFromThis(bits);
/* 115 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 119 */     GeneticDistanceStat or = (GeneticDistanceStat)other;
/* 120 */     this.m_SumWeight -= or.m_SumWeight;
/* 121 */     this.m_Bits.subtractFromThis(or.m_Bits);
/* 122 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void copyAndSubtractFromThis(ClusStatistic stattocopy, ClusStatistic stattosubtract) {
/* 126 */     GeneticDistanceStat tocopy = (GeneticDistanceStat)stattocopy;
/* 127 */     GeneticDistanceStat tosubtract = (GeneticDistanceStat)stattosubtract;
/*     */     
/* 129 */     tocopy.m_SumWeight -= tosubtract.m_SumWeight;
/* 130 */     this.m_Bits.copyAndSubtractFromThis(tocopy.m_Bits, tosubtract.m_Bits);
/* 131 */     this.m_NbTarget = tocopy.m_NbTarget;
/* 132 */     this.m_Attrs = tocopy.m_Attrs;
/* 133 */     this.m_Modified = true;
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 137 */     GeneticDistanceStat or = (GeneticDistanceStat)other;
/* 138 */     or.m_SumWeight -= this.m_SumWeight;
/* 139 */     this.m_Bits.subtractFromOther(or.m_Bits);
/* 140 */     this.m_Modified = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getNominalPred() {
/* 145 */     System.out.println("getNominalPred: not implemented for GeneticDistanceStat");
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 151 */     StringBuffer buf = new StringBuffer();
/* 152 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 153 */     buf.append("LEAF : ");
/* 154 */     buf.append(fr.format(this.m_SumWeight));
/* 155 */     buf.append(" sequence(s)");
/* 156 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcMean() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCount(int idx, int cls) {
/* 167 */     return 0.0D;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\GeneticDistanceStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */