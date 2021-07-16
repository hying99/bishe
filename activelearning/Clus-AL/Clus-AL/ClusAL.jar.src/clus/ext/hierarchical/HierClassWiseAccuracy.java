/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.Arrays;
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
/*     */ public class HierClassWiseAccuracy
/*     */   extends ClusError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected boolean m_NoTrivialClasses = false;
/*     */   protected ClassHierarchy m_Hier;
/*     */   protected double[] m_NbPosPredictions;
/*     */   protected double[] m_TP;
/*     */   protected double[] m_NbPosActual;
/*     */   protected boolean[] m_EvalClass;
/*     */   
/*     */   public HierClassWiseAccuracy(ClusErrorList par, ClassHierarchy hier) {
/*  50 */     super(par, hier.getTotal());
/*  51 */     this.m_Hier = hier;
/*  52 */     this.m_EvalClass = hier.getEvalClassesVector();
/*  53 */     this.m_NbPosPredictions = new double[this.m_Dim];
/*  54 */     this.m_TP = new double[this.m_Dim];
/*  55 */     this.m_NbPosActual = new double[this.m_Dim];
/*     */   }
/*     */ 
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/*  60 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(0);
/*  61 */     boolean[] predarr = ((WHTDStatistic)pred).getDiscretePred();
/*  62 */     for (int i = 0; i < this.m_Dim; i++) {
/*  63 */       if (predarr[i]) {
/*     */ 
/*     */ 
/*     */         
/*  67 */         this.m_NbPosPredictions[i] = this.m_NbPosPredictions[i] + 1.0D;
/*  68 */         if (tp.hasClass(i)) {
/*  69 */           this.m_TP[i] = this.m_TP[i] + 1.0D;
/*     */         }
/*     */       } 
/*     */     } 
/*  73 */     tp.updateDistribution(this.m_NbPosActual, 1.0D);
/*     */   }
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {
/*  77 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(0);
/*  78 */     tp.updateDistribution(this.m_NbPosActual, 1.0D);
/*     */   }
/*     */   
/*     */   public boolean isComputeForModel(String name) {
/*  82 */     if (name.equals("Default")) return false; 
/*  83 */     if (name.equals("Original")) return false; 
/*  84 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isNoTrivialClasses() {
/*  88 */     return this.m_NoTrivialClasses;
/*     */   }
/*     */   
/*     */   public boolean isEvalClass(int idx) {
/*  92 */     if (isNoTrivialClasses())
/*     */     {
/*  94 */       if (this.m_NbPosActual[idx] == getNbTotal()) {
/*  95 */         return false;
/*     */       }
/*     */     }
/*  98 */     return this.m_EvalClass[idx];
/*     */   }
/*     */   
/*     */   public double getPrecision() {
/* 102 */     double tot_corr = getTP();
/* 103 */     double tot_pred = getSumNbPosPredicted();
/* 104 */     return (tot_pred == 0.0D) ? 0.0D : (tot_corr / tot_pred);
/*     */   }
/*     */   
/*     */   public double getRecall() {
/* 108 */     double tot_corr = getTP();
/* 109 */     double tot_def = getSumNbPosActual();
/* 110 */     return (tot_def == 0.0D) ? 0.0D : (tot_corr / tot_def);
/*     */   }
/*     */   
/*     */   public int getTP() {
/* 114 */     int tot_corr = 0;
/* 115 */     for (int i = 0; i < this.m_Dim; i++) {
/* 116 */       if (isEvalClass(i)) tot_corr = (int)(tot_corr + this.m_TP[i]); 
/*     */     } 
/* 118 */     return tot_corr;
/*     */   }
/*     */   
/*     */   public int getFP() {
/* 122 */     int tot_pred = getSumNbPosPredicted();
/* 123 */     int tot_corr = getTP();
/* 124 */     return tot_pred - tot_corr;
/*     */   }
/*     */   
/*     */   public int getFN() {
/* 128 */     int tot_def = getSumNbPosActual();
/* 129 */     int tot_corr = getTP();
/* 130 */     return tot_def - tot_corr;
/*     */   }
/*     */   
/*     */   public int getSumNbPosActual() {
/* 134 */     int tot_def = 0;
/* 135 */     for (int i = 0; i < this.m_Dim; i++) {
/* 136 */       if (isEvalClass(i)) tot_def = (int)(tot_def + this.m_NbPosActual[i]); 
/*     */     } 
/* 138 */     return tot_def;
/*     */   }
/*     */   
/*     */   public int getSumNbPosPredicted() {
/* 142 */     int tot_pred = 0;
/* 143 */     for (int i = 0; i < this.m_Dim; i++) {
/* 144 */       if (isEvalClass(i)) tot_pred = (int)(tot_pred + this.m_NbPosPredictions[i]); 
/*     */     } 
/* 146 */     return tot_pred;
/*     */   }
/*     */   
/*     */   public int getNbPosExamplesCheck() {
/* 150 */     return getTP() + getFN();
/*     */   }
/*     */   
/*     */   public double getMacroAvgPrecision() {
/* 154 */     int cnt = 0;
/* 155 */     double avg = 0.0D;
/* 156 */     for (int i = 0; i < this.m_Dim; i++) {
/* 157 */       if (this.m_NbPosPredictions[i] != 0.0D && isEvalClass(i)) {
/* 158 */         cnt++;
/* 159 */         avg += this.m_TP[i] / this.m_NbPosPredictions[i];
/*     */       } 
/*     */     } 
/* 162 */     return (cnt == 0) ? 0.0D : (avg / cnt);
/*     */   }
/*     */   
/*     */   public void reset() {
/* 166 */     Arrays.fill(this.m_TP, 0.0D);
/* 167 */     Arrays.fill(this.m_NbPosPredictions, 0.0D);
/* 168 */     Arrays.fill(this.m_NbPosActual, 0.0D);
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 172 */     HierClassWiseAccuracy acc = (HierClassWiseAccuracy)other;
/* 173 */     for (int i = 0; i < this.m_Dim; i++) {
/* 174 */       this.m_TP[i] = this.m_TP[i] + acc.m_TP[i];
/* 175 */       this.m_NbPosPredictions[i] = this.m_NbPosPredictions[i] + acc.m_NbPosPredictions[i];
/* 176 */       this.m_NbPosActual[i] = this.m_NbPosActual[i] + acc.m_NbPosActual[i];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateFromGlobalMeasure(ClusError global) {
/* 184 */     HierClassWiseAccuracy other = (HierClassWiseAccuracy)global;
/* 185 */     System.arraycopy(other.m_NbPosActual, 0, this.m_NbPosActual, 0, this.m_NbPosActual.length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void printNonZeroAccuraciesRec(NumberFormat fr, PrintWriter out, ClassTerm node, boolean[] printed) {
/* 191 */     int idx = node.getIndex();
/*     */     
/* 193 */     if (printed[idx])
/* 194 */       return;  printed[idx] = true;
/* 195 */     if (this.m_NbPosPredictions[idx] != 0.0D && isEvalClass(idx)) {
/* 196 */       int nb = getNbTotal();
/* 197 */       double def = (nb == 0) ? 0.0D : (this.m_NbPosActual[idx] / nb);
/*     */       
/* 199 */       double prec = (this.m_NbPosPredictions[idx] == 0.0D) ? 0.0D : (this.m_TP[idx] / this.m_NbPosPredictions[idx]);
/*     */       
/* 201 */       double rec = (this.m_NbPosActual[idx] == 0.0D) ? 0.0D : (this.m_TP[idx] / this.m_NbPosActual[idx]);
/*     */       
/* 203 */       int TP = (int)this.m_TP[idx];
/* 204 */       int FP = (int)(this.m_NbPosPredictions[idx] - this.m_TP[idx]);
/* 205 */       int nbPos = (int)this.m_NbPosActual[idx];
/* 206 */       ClassesValue val = new ClassesValue(node);
/*     */       
/* 208 */       out.print("      " + val.toStringWithDepths(this.m_Hier));
/* 209 */       out.print(", def: " + fr.format(def));
/* 210 */       out.print(", prec: " + fr.format(prec));
/* 211 */       out.print(", rec: " + fr.format(rec));
/* 212 */       out.print(", TP: " + fr.format(TP) + ", FP: " + fr.format(FP) + ", nbPos: " + fr.format(nbPos));
/* 213 */       out.println();
/*     */     } 
/* 215 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 216 */       printNonZeroAccuraciesRec(fr, out, (ClassTerm)node.getChild(i), printed);
/*     */     }
/*     */   }
/*     */   
/*     */   public void printNonZeroAccuracies(NumberFormat fr, PrintWriter out, ClassHierarchy hier) {
/* 221 */     boolean[] printed = new boolean[hier.getTotal()];
/* 222 */     ClassTerm node = hier.getRoot();
/* 223 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 224 */       printNonZeroAccuraciesRec(fr, out, (ClassTerm)node.getChild(i), printed);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 230 */     NumberFormat fr1 = getFormat();
/* 231 */     NumberFormat fr2 = ClusFormat.SIX_AFTER_DOT;
/* 232 */     out.print("precision: " + fr2.format(getPrecision()));
/* 233 */     out.print(", recall: " + fr2.format(getRecall()));
/* 234 */     out.print(", coverage: " + fr2.format(getCoverage()));
/* 235 */     out.print(", TP: " + getTP() + ", FP: " + getFP() + ", nbPos: " + getSumNbPosActual());
/* 236 */     out.println();
/* 237 */     printNonZeroAccuracies(fr1, out, this.m_Hier);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 241 */     return "Hierarchical accuracy by class";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 245 */     return new HierClassWiseAccuracy(par, this.m_Hier);
/*     */   }
/*     */   
/*     */   public void nextPrediction(int cls, boolean predicted_class, boolean actually_has_class) {
/* 249 */     if (predicted_class) {
/*     */       
/* 251 */       this.m_NbPosPredictions[cls] = this.m_NbPosPredictions[cls] + 1.0D;
/* 252 */       if (actually_has_class) {
/* 253 */         this.m_TP[cls] = this.m_TP[cls] + 1.0D;
/*     */       }
/*     */     } 
/* 256 */     if (actually_has_class) this.m_NbPosActual[cls] = this.m_NbPosActual[cls] + 1.0D; 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierClassWiseAccuracy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */