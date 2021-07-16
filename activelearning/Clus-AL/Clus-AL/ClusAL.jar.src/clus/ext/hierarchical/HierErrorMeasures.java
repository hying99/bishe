/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.error.BinaryPredictionList;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.error.ROCAndPRCurve;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HierErrorMeasures
/*     */   extends ClusError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected ClassHierarchy m_Hier;
/*     */   protected boolean[] m_EvalClass;
/*     */   public BinaryPredictionList[] m_ClassWisePredictions;
/*     */   protected ROCAndPRCurve[] m_ROCAndPRCurves;
/*     */   protected ROCAndPRCurve m_PooledCurve;
/*     */   protected int m_Compatibility;
/*     */   protected int m_OptimizeMeasure;
/*     */   protected boolean m_WriteCurves;
/*     */   protected double[] m_RecallValues;
/*     */   protected double[] m_AvgPrecisionAtRecall;
/*     */   public BinaryPredictionList m_HSCPrediction;
/*     */   protected double m_AverageAUROC;
/*     */   protected double m_AverageAUPRC;
/*     */   protected double m_WAvgAUPRC;
/*     */   protected double m_PooledAUPRC;
/*     */   protected transient PrintWriter m_PRCurves;
/*     */   protected transient PrintWriter m_ROCCurves;
/*     */   
/*     */   public HierErrorMeasures(ClusErrorList par, ClassHierarchy hier, double[] recalls, int compat, int optimize, boolean wrCurves) {
/*  42 */     super(par, hier.getTotal());
/*  43 */     this.m_Hier = hier;
/*  44 */     this.m_Compatibility = compat;
/*  45 */     this.m_OptimizeMeasure = optimize;
/*  46 */     this.m_WriteCurves = wrCurves;
/*  47 */     this.m_RecallValues = recalls;
/*  48 */     this.m_EvalClass = hier.getEvalClassesVector();
/*     */ 
/*     */     
/*  51 */     this.m_ClassWisePredictions = new BinaryPredictionList[hier.getTotal()];
/*  52 */     this.m_HSCPrediction = new BinaryPredictionList();
/*  53 */     this.m_ROCAndPRCurves = new ROCAndPRCurve[hier.getTotal()];
/*  54 */     for (int i = 0; i < hier.getTotal(); i++) {
/*  55 */       BinaryPredictionList predlist = new BinaryPredictionList();
/*  56 */       this.m_ClassWisePredictions[i] = predlist;
/*  57 */       this.m_ROCAndPRCurves[i] = new ROCAndPRCurve(predlist);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/*  62 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(this.m_Hier.getType().getArrayIndex());
/*  63 */     double[] predarr = ((WHTDStatistic)pred).getNumericPred();
/*  64 */     boolean[] actual = tp.getVectorBooleanNodeAndAncestors(this.m_Hier);
/*  65 */     for (int i = 0; i < this.m_Dim; i++) {
/*  66 */       this.m_ClassWisePredictions[i].addExample(actual[i], predarr[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {
/*  71 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(this.m_Hier.getType().getArrayIndex());
/*  72 */     boolean[] actual = tp.getVectorBooleanNodeAndAncestors(this.m_Hier);
/*  73 */     for (int i = 0; i < this.m_Dim; i++) {
/*  74 */       this.m_ClassWisePredictions[i].addInvalid(actual[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isComputeForModel(String name) {
/*  79 */     if (name.equals("Original")) {
/*  80 */       return true;
/*     */     }
/*  82 */     if (name.equals("Pruned")) {
/*  83 */       return true;
/*     */     }
/*  85 */     return false;
/*     */   }
/*     */   
/*     */   public boolean shouldBeLow() {
/*  89 */     return false;
/*     */   }
/*     */   
/*     */   public double getModelError() {
/*  93 */     computeAll();
/*  94 */     switch (this.m_OptimizeMeasure) {
/*     */       case 0:
/*  96 */         return this.m_AverageAUROC;
/*     */       case 1:
/*  98 */         return this.m_AverageAUPRC;
/*     */       case 2:
/* 100 */         return this.m_WAvgAUPRC;
/*     */       case 3:
/* 102 */         return this.m_PooledAUPRC;
/*     */     } 
/* 104 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEvalClass(int idx) {
/* 111 */     return (this.m_EvalClass[idx] && includeZeroFreqClasses(idx));
/*     */   }
/*     */   
/*     */   public void reset() {
/* 115 */     for (int i = 0; i < this.m_Dim; i++) {
/* 116 */       this.m_ClassWisePredictions[i].clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 121 */     BinaryPredictionList[] olist = ((HierErrorMeasures)other).m_ClassWisePredictions;
/* 122 */     for (int i = 0; i < this.m_Dim; i++) {
/* 123 */       this.m_ClassWisePredictions[i].add(olist[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateFromGlobalMeasure(ClusError global) {
/* 131 */     BinaryPredictionList[] olist = ((HierErrorMeasures)global).m_ClassWisePredictions;
/* 132 */     for (int i = 0; i < this.m_Dim; i++) {
/* 133 */       this.m_ClassWisePredictions[i].copyActual(olist[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void printResultsRec(NumberFormat fr, PrintWriter out, ClassTerm node, boolean[] printed) {
/* 139 */     int idx = node.getIndex();
/*     */     
/* 141 */     if (printed[idx]) {
/*     */       return;
/*     */     }
/* 144 */     printed[idx] = true;
/* 145 */     if (isEvalClass(idx)) {
/* 146 */       ClassesValue val = new ClassesValue(node);
/* 147 */       out.print("      " + idx + ": " + val.toStringWithDepths(this.m_Hier));
/* 148 */       out.print(", AUROC: " + fr.format(this.m_ROCAndPRCurves[idx].getAreaROC()));
/* 149 */       out.print(", AUPRC: " + fr.format(this.m_ROCAndPRCurves[idx].getAreaPR()));
/* 150 */       out.print(", Freq: " + fr.format(this.m_ClassWisePredictions[idx].getFrequency()));
/* 151 */       if (this.m_RecallValues != null) {
/* 152 */         int nbRecalls = this.m_RecallValues.length;
/* 153 */         for (int j = 0; j < nbRecalls; j++) {
/* 154 */           int rec = (int)Math.floor(100.0D * this.m_RecallValues[j] + 0.5D);
/* 155 */           out.print(", P" + rec + "R: " + fr.format(100.0D * this.m_ROCAndPRCurves[idx].getPrecisionAtRecall(j)));
/*     */         } 
/*     */       } 
/* 158 */       out.println();
/*     */     } 
/* 160 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 161 */       printResultsRec(fr, out, (ClassTerm)node.getChild(i), printed);
/*     */     }
/*     */   }
/*     */   
/*     */   public void printResults(NumberFormat fr, PrintWriter out, ClassHierarchy hier) {
/* 166 */     ClassTerm node = hier.getRoot();
/* 167 */     boolean[] printed = new boolean[hier.getTotal()];
/* 168 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 169 */       printResultsRec(fr, out, (ClassTerm)node.getChild(i), printed);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isMultiLine() {
/* 174 */     return true;
/*     */   }
/*     */   
/*     */   public void compatibility(ROCAndPRCurve[] curves) {
/* 178 */     double[] thr = null;
/* 179 */     if (this.m_Compatibility <= 1) {
/* 180 */       thr = new double[51];
/* 181 */       for (int j = 0; j <= 50; j++) {
/* 182 */         thr[j] = 2.0D * j / 100.0D;
/*     */       }
/*     */     } 
/* 185 */     for (int i = 0; i < curves.length; i++) {
/* 186 */       curves[i].setThresholds(thr);
/*     */     }
/*     */ 
/*     */     
/* 190 */     this.m_PooledCurve.setThresholds(thr);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean includeZeroFreqClasses(int idx) {
/* 195 */     return (this.m_ClassWisePredictions[idx].getNbPos() > 0);
/*     */   }
/*     */   
/*     */   public void computeAll() {
/* 199 */     BinaryPredictionList pooled = new BinaryPredictionList();
/* 200 */     this.m_PooledCurve = new ROCAndPRCurve(pooled);
/* 201 */     compatibility(this.m_ROCAndPRCurves);
/* 202 */     for (int i = 0; i < this.m_Dim; i++) {
/* 203 */       if (isEvalClass(i)) {
/* 204 */         this.m_ClassWisePredictions[i].sort();
/* 205 */         this.m_ROCAndPRCurves[i].computeCurves();
/* 206 */         this.m_ROCAndPRCurves[i].computePrecisions(this.m_RecallValues);
/* 207 */         outputPRCurve(i, this.m_ROCAndPRCurves[i]);
/* 208 */         outputROCCurve(i, this.m_ROCAndPRCurves[i]);
/* 209 */         this.m_ROCAndPRCurves[i].clear();
/* 210 */         pooled.add(this.m_ClassWisePredictions[i]);
/* 211 */         this.m_HSCPrediction.copyFull(this.m_ClassWisePredictions[i]);
/*     */ 
/*     */         
/* 214 */         this.m_ClassWisePredictions[i].clearData();
/*     */       } 
/*     */     } 
/*     */     
/* 218 */     pooled.sort();
/* 219 */     this.m_PooledCurve.computeCurves();
/* 220 */     outputPRCurve(-1, this.m_PooledCurve);
/* 221 */     outputROCCurve(-1, this.m_PooledCurve);
/* 222 */     this.m_PooledCurve.clear();
/*     */     
/* 224 */     int cnt = 0;
/* 225 */     double sumAUROC = 0.0D;
/* 226 */     double sumAUPRC = 0.0D;
/* 227 */     double sumAUPRCw = 0.0D;
/* 228 */     double sumFrequency = 0.0D;
/* 229 */     for (int j = 0; j < this.m_Dim; j++) {
/*     */       
/* 231 */       if (isEvalClass(j)) {
/* 232 */         double freq = this.m_ClassWisePredictions[j].getFrequency();
/* 233 */         sumAUROC += this.m_ROCAndPRCurves[j].getAreaROC();
/* 234 */         sumAUPRC += this.m_ROCAndPRCurves[j].getAreaPR();
/* 235 */         sumAUPRCw += freq * this.m_ROCAndPRCurves[j].getAreaPR();
/* 236 */         sumFrequency += freq;
/* 237 */         cnt++;
/*     */       } 
/*     */     } 
/* 240 */     this.m_AverageAUROC = sumAUROC / cnt;
/* 241 */     this.m_AverageAUPRC = sumAUPRC / cnt;
/* 242 */     this.m_WAvgAUPRC = sumAUPRCw / sumFrequency;
/* 243 */     this.m_PooledAUPRC = this.m_PooledCurve.getAreaPR();
/*     */     
/* 245 */     if (this.m_RecallValues != null) {
/* 246 */       int nbRecalls = this.m_RecallValues.length;
/* 247 */       this.m_AvgPrecisionAtRecall = new double[nbRecalls];
/* 248 */       for (int k = 0; k < nbRecalls; k++) {
/* 249 */         int nbClass = 0;
/* 250 */         for (int m = 0; m < this.m_Dim; m++) {
/* 251 */           if (isEvalClass(m)) {
/* 252 */             double prec = this.m_ROCAndPRCurves[m].getPrecisionAtRecall(k);
/* 253 */             this.m_AvgPrecisionAtRecall[k] = this.m_AvgPrecisionAtRecall[k] + prec;
/* 254 */             nbClass++;
/*     */           } 
/*     */         } 
/* 257 */         this.m_AvgPrecisionAtRecall[k] = this.m_AvgPrecisionAtRecall[k] / nbClass;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void ouputCurve(int ci, ArrayList<double[]> points, PrintWriter curves) {
/* 263 */     String clName = "ALL";
/* 264 */     if (ci != -1) {
/* 265 */       ClassTerm cl = this.m_Hier.getTermAt(ci);
/* 266 */       clName = "\"" + cl.toStringHuman(this.m_Hier) + "\"";
/*     */     } 
/* 268 */     for (int i = 0; i < points.size(); i++) {
/* 269 */       double[] pt = points.get(i);
/* 270 */       curves.println(clName + "," + pt[0] + "," + pt[1]);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void outputPRCurve(int i, ROCAndPRCurve curve) {
/* 275 */     if (this.m_PRCurves != null) {
/* 276 */       ArrayList points = curve.getPRCurve();
/* 277 */       ouputCurve(i, points, this.m_PRCurves);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void outputROCCurve(int i, ROCAndPRCurve curve) {
/* 282 */     if (this.m_ROCCurves != null) {
/* 283 */       ArrayList points = curve.getROCCurve();
/* 284 */       ouputCurve(i, points, this.m_ROCCurves);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void writeCSVFilesPR(String fname) throws IOException {
/* 289 */     this.m_PRCurves = new PrintWriter(fname);
/* 290 */     this.m_PRCurves.println("Class,Recall,Precision");
/*     */   }
/*     */   
/*     */   public void writeCSVFilesROC(String fname) throws IOException {
/* 294 */     this.m_ROCCurves = new PrintWriter(fname);
/* 295 */     this.m_ROCCurves.println("Class,FP,TP");
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, String bName, int detail) throws IOException {
/* 299 */     if (this.m_WriteCurves && bName != null) {
/* 300 */       writeCSVFilesPR(bName + ".pr.csv");
/* 301 */       writeCSVFilesROC(bName + ".roc.csv");
/*     */     } 
/* 303 */     NumberFormat fr1 = ClusFormat.SIX_AFTER_DOT;
/* 304 */     computeAll();
/*     */     
/* 306 */     out.println();
/* 307 */     out.println("      Average AUROC:            " + this.m_AverageAUROC);
/* 308 */     out.println("      Average AUPRC:            " + this.m_AverageAUPRC);
/* 309 */     out.println("      Average AUPRC (weighted): " + this.m_WAvgAUPRC);
/* 310 */     out.println("      Pooled AUPRC:             " + this.m_PooledAUPRC);
/* 311 */     if (this.m_RecallValues != null) {
/* 312 */       int nbRecalls = this.m_RecallValues.length;
/* 313 */       for (int i = 0; i < nbRecalls; i++) {
/* 314 */         int rec = (int)Math.floor(100.0D * this.m_RecallValues[i] + 0.5D);
/* 315 */         out.println("      P" + rec + "R: " + (100.0D * this.m_AvgPrecisionAtRecall[i]));
/*     */       } 
/*     */     } 
/* 318 */     if (detail != 3) {
/* 319 */       printResults(fr1, out, this.m_Hier);
/*     */     }
/* 321 */     if (this.m_PRCurves != null) {
/* 322 */       this.m_PRCurves.close();
/* 323 */       this.m_PRCurves = null;
/*     */     } 
/* 325 */     if (this.m_ROCCurves != null) {
/* 326 */       this.m_ROCCurves.close();
/* 327 */       this.m_ROCCurves = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getName() {
/* 332 */     return "Hierarchical error measures";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 336 */     return new HierErrorMeasures(par, this.m_Hier, this.m_RecallValues, this.m_Compatibility, this.m_OptimizeMeasure, this.m_WriteCurves);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierErrorMeasures.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */