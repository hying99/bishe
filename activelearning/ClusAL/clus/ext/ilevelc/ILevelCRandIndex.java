/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
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
/*     */ public class ILevelCRandIndex
/*     */   extends ClusError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected double m_RandIndex;
/*     */   protected boolean m_Invalid;
/*     */   protected boolean m_IsComputed;
/*     */   protected NominalAttrType m_Attr;
/*  42 */   protected ArrayList m_Exs = new ArrayList();
/*     */   protected int m_Count;
/*     */   
/*     */   public ILevelCRandIndex(ClusErrorList par, NominalAttrType nom) {
/*  46 */     super(par, 1);
/*  47 */     this.m_Attr = nom;
/*     */   }
/*     */   
/*     */   public double computeRandIndex() {
/*  51 */     int a = 0;
/*  52 */     int b = 0;
/*  53 */     int nbex = this.m_Exs.size();
/*  54 */     if (nbex == 0) return 0.0D; 
/*  55 */     for (int i = 0; i < nbex; i++) {
/*  56 */       int[] ti = this.m_Exs.get(i);
/*  57 */       for (int j = i + 1; j < nbex; j++) {
/*  58 */         int[] tj = this.m_Exs.get(j);
/*  59 */         if (ti[0] == tj[0] && ti[1] == tj[1]) a++; 
/*  60 */         if (ti[0] != tj[0] && ti[1] != tj[1]) b++; 
/*     */       } 
/*     */     } 
/*  63 */     double rand = 1.0D * (a + b) / (nbex * (nbex - 1) / 2);
/*  64 */     System.out.println("Rand = " + rand + " (nbex = " + nbex + ")");
/*  65 */     return rand;
/*     */   }
/*     */   
/*     */   public boolean isInvalid() {
/*  69 */     return this.m_Invalid;
/*     */   }
/*     */   
/*     */   public double getRandIndex() {
/*  73 */     if (!this.m_IsComputed) {
/*  74 */       this.m_RandIndex = computeRandIndex();
/*  75 */       this.m_IsComputed = true;
/*     */     } 
/*  77 */     return this.m_RandIndex;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  81 */     this.m_IsComputed = false;
/*  82 */     this.m_Exs.clear();
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/*  86 */     ILevelCRandIndex ri = (ILevelCRandIndex)other;
/*  87 */     if (!ri.isInvalid()) {
/*  88 */       this.m_RandIndex += ri.getRandIndex();
/*  89 */       this.m_Count++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {
/*  94 */     this.m_Invalid = true;
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/*  98 */     this.m_IsComputed = false;
/*  99 */     int[] store = new int[2];
/* 100 */     ILevelCStatistic ilstat = (ILevelCStatistic)pred;
/* 101 */     store[0] = this.m_Attr.getNominal(tuple);
/* 102 */     store[1] = ilstat.getClusterID();
/* 103 */     this.m_Exs.add(store);
/*     */   }
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/* 107 */     if (this.m_Count > 0) {
/* 108 */       return this.m_RandIndex / this.m_Count;
/*     */     }
/* 110 */     return getRandIndex();
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 114 */     if (isInvalid()) {
/* 115 */       out.println("?");
/* 116 */     } else if (this.m_Count > 0) {
/* 117 */       out.println(String.valueOf(1.0D * this.m_RandIndex / this.m_Count) + " (cnt = " + this.m_Count + ")");
/*     */     } else {
/* 119 */       out.println(getRandIndex());
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 124 */     return new ILevelCRandIndex(getParent(), this.m_Attr);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 128 */     return "Rand index";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\ILevelCRandIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */