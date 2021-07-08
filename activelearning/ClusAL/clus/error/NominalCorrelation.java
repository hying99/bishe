/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.type.NominalAttrType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NominalCorrelation
/*     */   extends ClusNominalError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int[][] m_ContTable;
/*     */   
/*     */   public NominalCorrelation(ClusErrorList par, NominalAttrType[] nom, int ind1, int ind2) {
/*  17 */     super(par, nom);
/*  18 */     int size1 = this.m_Attrs[ind1].getNbValues();
/*  19 */     int size2 = this.m_Attrs[ind2].getNbValues();
/*  20 */     this.m_ContTable = new int[size1][size2];
/*     */   }
/*     */   
/*     */   public int calcNbCorrect(int[][] table) {
/*  24 */     int sum = 0;
/*  25 */     int size = table.length;
/*  26 */     for (int j = 0; j < size; j++) {
/*  27 */       sum += table[j][j];
/*     */     }
/*  29 */     return sum;
/*     */   }
/*     */   
/*     */   public double calcXSquare() {
/*  33 */     int size1 = this.m_ContTable.length;
/*  34 */     int size2 = (this.m_ContTable[0]).length;
/*  35 */     int n = getNbExamples();
/*  36 */     int[] ni = new int[size1];
/*  37 */     int[] nj = new int[size2];
/*  38 */     for (int i = 0; i < size1; i++) {
/*  39 */       ni[i] = sumJ(i);
/*     */     }
/*  41 */     for (int j = 0; j < size2; j++) {
/*  42 */       nj[j] = sumI(j);
/*     */     }
/*  44 */     double xsquare = 0.0D;
/*  45 */     for (int k = 0; k < size1; k++) {
/*  46 */       for (int m = 0; m < size2; m++) {
/*  47 */         double mij = ni[k] * nj[m] / n;
/*  48 */         double err = this.m_ContTable[k][m] - mij;
/*  49 */         if (mij != 0.0D) xsquare += err * err / mij; 
/*     */       } 
/*     */     } 
/*  52 */     return xsquare;
/*     */   }
/*     */   
/*     */   public double calcCramerV() {
/*  56 */     int size1 = this.m_ContTable.length;
/*  57 */     int size2 = (this.m_ContTable[0]).length;
/*  58 */     int n = getNbExamples();
/*  59 */     double div = n * Math.min(size1 - 1, size2 - 1);
/*  60 */     return Math.sqrt(calcXSquare() / div);
/*     */   }
/*     */   
/*     */   public double calcMutualInfo() {
/*  64 */     int size1 = this.m_ContTable.length;
/*  65 */     int size2 = (this.m_ContTable[0]).length;
/*  66 */     int n = getNbExamples();
/*  67 */     int[] ni = new int[size1];
/*  68 */     int[] nj = new int[size2];
/*  69 */     for (int i = 0; i < size1; i++) {
/*  70 */       ni[i] = sumJ(i);
/*     */     }
/*  72 */     for (int j = 0; j < size2; j++) {
/*  73 */       nj[j] = sumI(j);
/*     */     }
/*  75 */     double m_info = 0.0D;
/*  76 */     for (int k = 0; k < size1; k++) {
/*  77 */       for (int m = 0; m < size2; m++) {
/*  78 */         double pij = this.m_ContTable[k][m] / n;
/*  79 */         double pi = ni[k] / n;
/*  80 */         double pj = nj[m] / n;
/*  81 */         double div = pi * pj;
/*  82 */         if (div != 0.0D) m_info += pij * Math.log(pij / div) / Math.log(2.0D); 
/*     */       } 
/*     */     } 
/*  85 */     return m_info;
/*     */   }
/*     */   
/*     */   public int sumI(int j) {
/*  89 */     int sum = 0;
/*  90 */     int size = this.m_ContTable.length;
/*  91 */     for (int i = 0; i < size; i++)
/*  92 */       sum += this.m_ContTable[i][j]; 
/*  93 */     return sum;
/*     */   }
/*     */   
/*     */   public int sumJ(int i) {
/*  97 */     int sum = 0;
/*  98 */     int size = (this.m_ContTable[0]).length;
/*  99 */     for (int j = 0; j < size; j++)
/* 100 */       sum += this.m_ContTable[i][j]; 
/* 101 */     return sum;
/*     */   }
/*     */   
/*     */   public boolean hasSummary() {
/* 105 */     return false;
/*     */   }
/*     */   
/*     */   public void addExample(int ind1, int ind2) {
/* 109 */     this.m_ContTable[ind1][ind2] = this.m_ContTable[ind1][ind2] + 1;
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 113 */     return new NominalCorrelation(par, this.m_Attrs, 0, 0);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 117 */     return "Cramer's V coefficient or Mutual information";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\NominalCorrelation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */