/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.util.ClusFormat;
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
/*     */ public class CorrelationMatrixComputer
/*     */ {
/*     */   PearsonCorrelation[][] m_MatrixPC;
/*     */   NominalCorrelation[][] m_MatrixNC;
/*     */   boolean m_IsRegression = true;
/*     */   
/*     */   public void compute(RowData data) {
/*  43 */     if (data.getSchema().isRegression()) {
/*  44 */       computeNum(data);
/*     */     } else {
/*  46 */       this.m_IsRegression = false;
/*  47 */       computeNom(data);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeNum(RowData data) {
/*  52 */     ClusSchema schema = data.getSchema();
/*  53 */     NumericAttrType[] attrs = schema.getNumericAttrUse(3);
/*  54 */     int nb_num = attrs.length;
/*  55 */     this.m_MatrixPC = new PearsonCorrelation[nb_num][nb_num];
/*  56 */     NumericAttrType[] crtype = new NumericAttrType[1];
/*  57 */     crtype[0] = new NumericAttrType("corr");
/*  58 */     ClusErrorList par = new ClusErrorList();
/*  59 */     for (int i = 0; i < nb_num; i++) {
/*  60 */       for (int k = 0; k < nb_num; k++) {
/*  61 */         this.m_MatrixPC[i][k] = new PearsonCorrelation(par, crtype);
/*     */       }
/*     */     } 
/*  64 */     double[] a1 = new double[1];
/*  65 */     double[] a2 = new double[1];
/*  66 */     par.setNbExamples(data.getNbRows());
/*  67 */     for (int j = 0; j < data.getNbRows(); j++) {
/*  68 */       DataTuple tuple = data.getTuple(j);
/*  69 */       for (int k = 0; k < nb_num; k++) {
/*  70 */         for (int m = 0; m < nb_num; m++) {
/*  71 */           a1[0] = attrs[k].getNumeric(tuple);
/*  72 */           a2[0] = attrs[m].getNumeric(tuple);
/*  73 */           this.m_MatrixPC[k][m].addExample(a1, a2);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeNom(RowData data) {
/*  80 */     ClusSchema schema = data.getSchema();
/*  81 */     NominalAttrType[] attrs = schema.getNominalAttrUse(3);
/*  82 */     int nb_nom = attrs.length;
/*  83 */     this.m_MatrixNC = new NominalCorrelation[nb_nom][nb_nom];
/*     */ 
/*     */     
/*  86 */     ClusErrorList par = new ClusErrorList();
/*  87 */     for (int i = 0; i < nb_nom; i++) {
/*  88 */       for (int k = 0; k < nb_nom; k++)
/*     */       {
/*  90 */         this.m_MatrixNC[i][k] = new NominalCorrelation(par, attrs, i, k);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  95 */     par.setNbExamples(data.getNbRows());
/*  96 */     for (int j = 0; j < data.getNbRows(); j++) {
/*  97 */       DataTuple tuple = data.getTuple(j);
/*  98 */       for (int k = 0; k < nb_nom; k++) {
/*  99 */         for (int m = 0; m < nb_nom; m++) {
/* 100 */           int a1 = attrs[k].getNominal(tuple);
/* 101 */           int a2 = attrs[m].getNominal(tuple);
/* 102 */           this.m_MatrixNC[k][m].addExample(a1, a2);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void printMatrixTeX() {
/*     */     int nb_tar;
/* 110 */     if (this.m_IsRegression) {
/* 111 */       nb_tar = this.m_MatrixPC.length;
/* 112 */       System.out.println("Number of numeric: " + nb_tar);
/*     */     } else {
/* 114 */       nb_tar = this.m_MatrixNC.length;
/* 115 */       System.out.println("Number of nominal: " + nb_tar);
/*     */     } 
/* 117 */     System.out.println();
/* 118 */     System.out.print("\\begin{tabular}{"); int i;
/* 119 */     for (i = 0; i < nb_tar + 2; i++) {
/* 120 */       System.out.print("l");
/*     */     }
/* 122 */     System.out.println("}");
/* 123 */     for (i = 0; i < nb_tar; i++) {
/* 124 */       System.out.print(" & " + (i + 1));
/*     */     }
/* 126 */     System.out.println("& Avg.");
/* 127 */     System.out.println("\\\\");
/* 128 */     int nb_pairs = 0;
/* 129 */     double pairs_sum = 0.0D;
/* 130 */     for (int j = 0; j < nb_tar; j++) {
/* 131 */       System.out.print(j + 1);
/* 132 */       double avg = 0.0D;
/* 133 */       double cnt = 0.0D;
/* 134 */       for (int k = 0; k < nb_tar; k++) {
/*     */         double corr;
/* 136 */         if (this.m_IsRegression) {
/* 137 */           corr = this.m_MatrixPC[j][k].getCorrelation(0);
/*     */         }
/*     */         else {
/*     */           
/* 141 */           corr = this.m_MatrixNC[j][k].calcMutualInfo();
/*     */         } 
/* 143 */         if (j != k) {
/* 144 */           avg += corr;
/* 145 */           cnt++;
/*     */         } 
/* 147 */         if (j > k) {
/* 148 */           pairs_sum += corr;
/* 149 */           nb_pairs++;
/*     */         } 
/* 151 */         System.out.print(" & " + ClusFormat.THREE_AFTER_DOT.format(corr));
/*     */       } 
/* 153 */       System.out.print(" & " + ClusFormat.THREE_AFTER_DOT.format(avg / cnt));
/* 154 */       System.out.println("\\\\");
/*     */     } 
/* 156 */     System.out.print("\\multicolumn{" + (nb_tar + 2) + "}{l}{Pairwise average:");
/* 157 */     if (nb_pairs > 0) {
/* 158 */       System.out.println(" " + ClusFormat.THREE_AFTER_DOT.format(pairs_sum / nb_pairs) + "}");
/*     */     } else {
/* 160 */       System.out.println(" Undefined}");
/*     */     } 
/* 162 */     System.out.println("\\end{tabular}");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\CorrelationMatrixComputer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */