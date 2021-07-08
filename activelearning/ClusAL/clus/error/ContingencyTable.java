/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.PrintWriter;
/*     */ import jeans.util.StringUtils;
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
/*     */ public class ContingencyTable
/*     */   extends ClusNominalError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected static final String REAL_PRED = "REAL\\PRED";
/*     */   protected int[][][] m_ContTable;
/*     */   
/*     */   public ContingencyTable(ClusErrorList par, NominalAttrType[] nom) {
/*  43 */     super(par, nom);
/*  44 */     this.m_ContTable = new int[this.m_Dim][][];
/*  45 */     for (int i = 0; i < this.m_Dim; i++) {
/*     */       
/*  47 */       int size = this.m_Attrs[i].getNbValuesInclMissing();
/*  48 */       this.m_ContTable[i] = new int[size][size];
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isMultiLine() {
/*  53 */     return true;
/*     */   }
/*     */   
/*     */   public int calcNbTotal(int k) {
/*  57 */     int sum = 0;
/*  58 */     int size = this.m_Attrs[k].getNbValues();
/*  59 */     int[][] table = this.m_ContTable[k];
/*  60 */     for (int i = 0; i < size; i++) {
/*  61 */       for (int j = 0; j < size; j++) {
/*  62 */         sum += table[i][j];
/*     */       }
/*     */     } 
/*  65 */     return sum;
/*     */   }
/*     */   
/*     */   public int calcNbCorrect(int k) {
/*  69 */     int sum = 0;
/*  70 */     int size = this.m_Attrs[k].getNbValues();
/*  71 */     int[][] table = this.m_ContTable[k];
/*  72 */     for (int j = 0; j < size; j++) {
/*  73 */       sum += table[j][j];
/*     */     }
/*  75 */     return sum;
/*     */   }
/*     */   
/*     */   public double calcXSquare(int k) {
/*  79 */     int size = this.m_Attrs[k].getNbValues();
/*  80 */     int[] ri = new int[size];
/*  81 */     int[] cj = new int[size];
/*  82 */     for (int j = 0; j < size; j++) {
/*  83 */       ri[j] = sumRow(k, j);
/*  84 */       cj[j] = sumColumn(k, j);
/*     */     } 
/*  86 */     double xsquare = 0.0D;
/*  87 */     int nb = getNbExamples();
/*  88 */     int[][] table = this.m_ContTable[k];
/*  89 */     for (int i = 0; i < size; i++) {
/*  90 */       for (int m = 0; m < size; m++) {
/*  91 */         double eij = ri[i] * cj[m] / nb;
/*  92 */         double err = table[i][m] - eij;
/*  93 */         if (err != 0.0D) xsquare += err * err / eij; 
/*     */       } 
/*     */     } 
/*  96 */     return xsquare;
/*     */   }
/*     */   
/*     */   public double calcCramerV(int k) {
/* 100 */     int q = this.m_Attrs[k].getNbValues();
/* 101 */     int n = calcNbTotal(k);
/* 102 */     double div = n * (q - 1);
/* 103 */     return Math.sqrt(calcXSquare(k) / div);
/*     */   }
/*     */   
/*     */   public double calcAccuracy(int k) {
/* 107 */     return calcNbCorrect(k) / calcNbTotal(k);
/*     */   }
/*     */   
/*     */   public double calcDefaultAccuracy(int i) {
/* 111 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/* 115 */     return calcAccuracy(i);
/*     */   }
/*     */   
/*     */   public double getModelComponent() {
/* 119 */     double sum = 0.0D;
/* 120 */     for (int i = 0; i < this.m_Dim; i++) {
/* 121 */       sum += calcAccuracy(i);
/*     */     }
/* 123 */     return sum / this.m_Dim;
/*     */   }
/*     */   
/*     */   public void showAccuracy(PrintWriter out, int i) {
/* 127 */     int nbcorr = calcNbCorrect(i);
/* 128 */     int nbtot = calcNbTotal(i);
/* 129 */     double acc = nbcorr / nbtot;
/* 130 */     out.print("Accuracy: " + ClusFormat.SIX_AFTER_DOT.format(acc));
/*     */     
/* 132 */     out.println();
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 136 */     ContingencyTable cont = (ContingencyTable)other;
/* 137 */     for (int i = 0; i < this.m_Dim; i++) {
/* 138 */       int[][] t1 = this.m_ContTable[i];
/* 139 */       int[][] t2 = cont.m_ContTable[i];
/* 140 */       int size = t1.length;
/* 141 */       for (int j = 0; j < size; j++) {
/* 142 */         for (int k = 0; k < size; k++) {
/* 143 */           t1[j][k] = t1[j][k] + t2[j][k];
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 150 */     if (detail == 3) {
/* 151 */       out.print(getPrefix() + "[");
/* 152 */       for (int i = 0; i < this.m_Dim; i++) {
/* 153 */         if (i != 0) out.print(","); 
/* 154 */         double acc = calcAccuracy(i);
/* 155 */         out.print(ClusFormat.SIX_AFTER_DOT.format(acc));
/*     */       } 
/* 157 */       out.println("]");
/*     */     } else {
/* 159 */       for (int i = 0; i < this.m_Dim; i++) {
/* 160 */         out.println();
/* 161 */         out.println(getPrefix() + "Attribute: " + this.m_Attrs[i].getName());
/* 162 */         showContTable(out, i);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int sumColumn(int[][] table, int j) {
/* 168 */     int sum = 0;
/* 169 */     for (int i = 0; i < table.length; i++)
/* 170 */       sum += table[i][j]; 
/* 171 */     return sum;
/*     */   }
/*     */   
/*     */   public int sumRow(int[][] table, int i) {
/* 175 */     int sum = 0;
/* 176 */     for (int j = 0; j < table.length; j++)
/* 177 */       sum += table[i][j]; 
/* 178 */     return sum;
/*     */   }
/*     */   
/*     */   public int sumColumn(int k, int j) {
/* 182 */     int sum = 0;
/* 183 */     int size = this.m_Attrs[k].getNbValues();
/* 184 */     int[][] table = this.m_ContTable[k];
/* 185 */     for (int i = 0; i < size; i++)
/* 186 */       sum += table[i][j]; 
/* 187 */     return sum;
/*     */   }
/*     */   
/*     */   public int sumRow(int k, int i) {
/* 191 */     int sum = 0;
/* 192 */     int size = this.m_Attrs[k].getNbValues();
/* 193 */     int[][] table = this.m_ContTable[k];
/* 194 */     for (int j = 0; j < size; j++)
/* 195 */       sum += table[i][j]; 
/* 196 */     return sum;
/*     */   }
/*     */   
/*     */   public void showContTable(PrintWriter out, int i) {
/* 200 */     int[][] table = this.m_ContTable[i];
/* 201 */     int size = this.m_Attrs[i].getNbValues();
/* 202 */     if (this.m_Attrs[i].hasMissing())
/*     */     {
/* 204 */       size++;
/*     */     }
/*     */     
/* 207 */     int[] wds = new int[size + 2];
/*     */     
/* 209 */     wds[0] = "REAL\\PRED".length(); int j;
/* 210 */     for (j = 0; j < size; j++) {
/* 211 */       wds[j + 1] = this.m_Attrs[i].getValueOrMissing(j).length() + 1;
/*     */     }
/*     */     
/* 214 */     for (j = 0; j < size; j++) {
/* 215 */       wds[0] = Math.max(wds[0], this.m_Attrs[i].getValueOrMissing(j).length());
/* 216 */       for (int i2 = 0; i2 < size; i2++) {
/* 217 */         String str1 = String.valueOf(table[j][i2]);
/* 218 */         wds[i2 + 1] = Math.max(wds[i2 + 1], str1.length() + 1);
/*     */       } 
/* 220 */       String str = String.valueOf(sumRow(table, j));
/* 221 */       wds[size + 1] = Math.max(wds[size + 1], str.length() + 1);
/*     */     } 
/*     */     
/* 224 */     for (int k = 0; k < size; k++) {
/* 225 */       String str = String.valueOf(sumColumn(table, k));
/* 226 */       wds[k + 1] = Math.max(wds[k + 1], str.length() + 1);
/*     */     } 
/*     */     
/* 229 */     wds[size + 1] = Math.max(wds[size + 1], String.valueOf(getNbExamples()).length() + 1);
/*     */     
/* 231 */     int s = 0;
/* 232 */     for (int m = 0; m < size + 2; ) { s += wds[m]; m++; }
/* 233 */      String horiz = getPrefix() + "  " + StringUtils.makeString('-', s + (size + 1) * 2);
/*     */     
/* 235 */     out.print(getPrefix() + "  ");
/* 236 */     printString(out, wds[0], "REAL\\PRED");
/* 237 */     out.print(" |"); int i1;
/* 238 */     for (i1 = 0; i1 < size; i1++) {
/* 239 */       printString(out, wds[i1 + 1], this.m_Attrs[i].getValueOrMissing(i1));
/* 240 */       out.print(" |");
/*     */     } 
/* 242 */     out.println();
/* 243 */     out.println(horiz);
/*     */     
/* 245 */     for (i1 = 0; i1 < size; i1++) {
/* 246 */       out.print(getPrefix() + "  ");
/* 247 */       printString(out, wds[0], this.m_Attrs[i].getValueOrMissing(i1));
/* 248 */       out.print(" |");
/* 249 */       for (int i2 = 0; i2 < size; i2++) {
/* 250 */         printString(out, wds[i2 + 1], String.valueOf(table[i1][i2]));
/* 251 */         out.print(" |");
/*     */       } 
/* 253 */       printString(out, wds[size + 1], String.valueOf(sumRow(table, i1)));
/* 254 */       out.println();
/*     */     } 
/* 256 */     out.println(horiz);
/* 257 */     out.print(getPrefix() + "  ");
/* 258 */     out.print(StringUtils.makeString(' ', wds[0]));
/* 259 */     out.print(" |");
/* 260 */     for (int n = 0; n < size; n++) {
/* 261 */       printString(out, wds[n + 1], String.valueOf(sumColumn(table, n)));
/* 262 */       out.print(" |");
/*     */     } 
/* 264 */     printString(out, wds[size + 1], String.valueOf(getNbExamples()));
/* 265 */     out.println();
/* 266 */     out.print(getPrefix() + "  ");
/* 267 */     showAccuracy(out, i);
/* 268 */     out.print(getPrefix() + "  ");
/* 269 */     double cramer = calcCramerV(i);
/* 270 */     out.println("Cramer's coefficient: " + ClusFormat.SIX_AFTER_DOT.format(cramer));
/* 271 */     out.println();
/*     */   }
/*     */   
/*     */   public void showSummaryError(PrintWriter out, boolean detail) {
/* 275 */     if (!detail) {
/* 276 */       for (int i = 0; i < this.m_Dim; i++) {
/* 277 */         out.print(getPrefix() + "Attribute: " + this.m_Attrs[i].getName() + " - ");
/* 278 */         showAccuracy(out, i);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void printString(PrintWriter out, int wd, String str) {
/* 284 */     out.print(StringUtils.makeString(' ', wd - str.length()));
/* 285 */     out.print(str);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 289 */     return "Classification Error";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 293 */     return new ContingencyTable(par, this.m_Attrs);
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 297 */     int[] predicted = pred.getNominalPred();
/* 298 */     for (int i = 0; i < this.m_Dim; i++) {
/* 299 */       this.m_ContTable[i][getAttr(i).getNominal(tuple)][predicted[i]] = this.m_ContTable[i][getAttr(i).getNominal(tuple)][predicted[i]] + 1;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public double get_error_classif() {
/* 311 */     return 1.0D - get_accuracy();
/*     */   }
/*     */   
/*     */   public double get_TP() {
/* 315 */     return calcNbCorrect(0);
/*     */   }
/*     */   
/*     */   public double get_accuracy() {
/* 319 */     return calcAccuracy(0);
/*     */   }
/*     */   
/*     */   public double get_precision() {
/* 323 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public double get_recall() {
/* 327 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public double get_auc() {
/* 331 */     return 0.0D;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\ContingencyTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */