/*     */ package clus.data.cols;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.statistic.ClusStatistic;
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
/*     */ public class ColTarget
/*     */ {
/*     */   public int m_NbNumeric;
/*     */   public int m_NbNominal;
/*     */   public int m_NbRows;
/*     */   public double[][] m_Numeric;
/*     */   public int[][] m_Nominal;
/*     */   public ClusNode[] m_Node;
/*     */   
/*     */   public ColTarget(ClusSchema schema) {}
/*     */   
/*     */   public ClusNode[] getNodes() {
/*  43 */     return this.m_Node;
/*     */   }
/*     */   
/*     */   public int getNbAttributes() {
/*  47 */     return this.m_NbNumeric + this.m_NbNominal;
/*     */   }
/*     */   
/*     */   public int getNbNum() {
/*  51 */     return this.m_NbNumeric;
/*     */   }
/*     */   
/*     */   public int getNbNom() {
/*  55 */     return this.m_NbNominal;
/*     */   }
/*     */   
/*     */   public int getNbRows() {
/*  59 */     return this.m_NbRows;
/*     */   }
/*     */   
/*     */   public void setNbRows(int nb) {
/*  63 */     this.m_NbRows = nb;
/*     */   }
/*     */   
/*     */   public void setData(double[][] num, int[][] nom, int nbrows) {
/*  67 */     this.m_Numeric = num;
/*  68 */     this.m_Nominal = nom;
/*  69 */     this.m_NbRows = nbrows;
/*     */   }
/*     */   
/*     */   public void resize(int nbrows) {
/*  73 */     this.m_NbRows = nbrows;
/*  74 */     if (this.m_NbNumeric != 0) this.m_Numeric = new double[this.m_NbRows][this.m_NbNumeric]; 
/*  75 */     if (this.m_NbNominal != 0) this.m_Nominal = new int[this.m_NbRows][this.m_NbNominal]; 
/*     */   }
/*     */   
/*     */   public ColTarget select(ClusSelection sel, int nbsel) {
/*  79 */     double[][] numsubset = selectNumeric(sel, nbsel);
/*  80 */     int[][] nomsubset = selectNominal(sel, nbsel);
/*  81 */     setNbRows(this.m_NbRows - nbsel);
/*  82 */     ColTarget s_targ = null;
/*  83 */     s_targ.setData(numsubset, nomsubset, nbsel);
/*  84 */     return s_targ;
/*     */   }
/*     */   
/*     */   public double[][] selectNumeric(ClusSelection sel, int nbsel) {
/*  88 */     if (this.m_Numeric == null) return (double[][])null; 
/*  89 */     int s_data = 0;
/*  90 */     int s_subset = 0;
/*  91 */     double[][] data = this.m_Numeric;
/*  92 */     this.m_Numeric = new double[this.m_NbRows - nbsel][];
/*  93 */     double[][] subset = new double[nbsel][];
/*  94 */     for (int i = 0; i < this.m_NbRows; i++) {
/*  95 */       if (sel.isSelected(i)) { subset[s_subset++] = data[i]; }
/*  96 */       else { this.m_Numeric[s_data++] = data[i]; }
/*     */     
/*  98 */     }  return subset;
/*     */   }
/*     */   
/*     */   public int[][] selectNominal(ClusSelection sel, int nbsel) {
/* 102 */     if (this.m_Nominal == null) return (int[][])null; 
/* 103 */     int s_data = 0;
/* 104 */     int s_subset = 0;
/* 105 */     int[][] data = this.m_Nominal;
/* 106 */     this.m_Nominal = new int[this.m_NbRows - nbsel][];
/* 107 */     int[][] subset = new int[nbsel][];
/* 108 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 109 */       if (sel.isSelected(i)) { subset[s_subset++] = data[i]; }
/* 110 */       else { this.m_Nominal[s_data++] = data[i]; }
/*     */     
/* 112 */     }  return subset;
/*     */   }
/*     */   
/*     */   public void insert(ColTarget target, ClusSelection sel, int nb_new) {
/* 116 */     insertNumeric(target, sel, nb_new);
/* 117 */     insertNominal(target, sel, nb_new);
/* 118 */     this.m_NbRows = nb_new;
/*     */   }
/*     */   
/*     */   public void insertNumeric(ColTarget target, ClusSelection sel, int nb_new) {
/* 122 */     if (this.m_Numeric == null)
/* 123 */       return;  int s_data = 0;
/* 124 */     int s_subset = 0;
/* 125 */     double[][] data = this.m_Numeric;
/* 126 */     this.m_Numeric = new double[nb_new][];
/* 127 */     double[][] subset = target.m_Numeric;
/* 128 */     for (int i = 0; i < nb_new; i++) {
/* 129 */       if (sel.isSelected(i)) { this.m_Numeric[i] = subset[s_subset++]; }
/* 130 */       else { this.m_Numeric[i] = data[s_data++]; }
/*     */     
/*     */     } 
/*     */   }
/*     */   public void insertNominal(ColTarget target, ClusSelection sel, int nb_new) {
/* 135 */     if (this.m_Nominal == null)
/* 136 */       return;  int s_data = 0;
/* 137 */     int s_subset = 0;
/* 138 */     int[][] data = this.m_Nominal;
/* 139 */     this.m_Nominal = new int[nb_new][];
/* 140 */     int[][] subset = target.m_Nominal;
/* 141 */     for (int i = 0; i < nb_new; i++) {
/* 142 */       if (sel.isSelected(i)) { this.m_Nominal[i] = subset[s_subset++]; }
/* 143 */       else { this.m_Nominal[i] = data[s_data++]; }
/*     */     
/*     */     } 
/*     */   }
/*     */   public void addToRoot(ClusNode info) {
/* 148 */     this.m_Node = new ClusNode[this.m_NbRows];
/* 149 */     for (int i = 0; i < this.m_NbRows; i++) {
/* 150 */       this.m_Node[i] = info;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void normalize() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcTotalStat(ClusStatistic stat) {
/* 169 */     for (int i = 0; i < this.m_NbRows; ) { stat.update(this, i); i++; }
/*     */   
/*     */   }
/*     */   public void setNumeric(int idx, int row, double data) {
/* 173 */     this.m_Numeric[row][idx] = data;
/*     */   }
/*     */   
/*     */   public void setNominal(int idx, int row, int data) {
/* 177 */     this.m_Nominal[row][idx] = data;
/*     */   }
/*     */   
/*     */   public double[] getNumeric(int i) {
/* 181 */     return this.m_Numeric[i];
/*     */   }
/*     */   
/*     */   public int[] getNominal(int i) {
/* 185 */     return this.m_Nominal[i];
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\cols\ColTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */