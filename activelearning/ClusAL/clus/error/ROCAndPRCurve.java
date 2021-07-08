/*     */ package clus.error;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import jeans.math.MathUtil;
/*     */ import jeans.util.compound.DoubleBooleanCount;
/*     */ 
/*     */ 
/*     */ public class ROCAndPRCurve
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected double m_AreaROC;
/*     */   protected double m_AreaPR;
/*     */   protected double[] m_Thresholds;
/*     */   protected transient boolean m_ExtendPR;
/*     */   protected transient int m_PrevTP;
/*     */   protected transient int m_PrevFP;
/*     */   protected transient ArrayList m_ROC;
/*     */   public transient ArrayList m_PR;
/*     */   protected transient BinaryPredictionList m_Predictions;
/*     */   protected transient double[] m_PrecisionAtRecall;
/*     */   
/*     */   public ROCAndPRCurve(BinaryPredictionList list) {
/*  25 */     this.m_Predictions = list;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  30 */     this.m_ROC.clear();
/*  31 */     this.m_PR.clear();
/*     */   }
/*     */   
/*     */   public ArrayList getROCCurve() {
/*  35 */     return this.m_ROC;
/*     */   }
/*     */   
/*     */   public ArrayList getPRCurve() {
/*  39 */     return this.m_PR;
/*     */   }
/*     */   
/*     */   public double getAreaROC() {
/*  43 */     return this.m_AreaROC;
/*     */   }
/*     */   
/*     */   public double getAreaPR() {
/*  47 */     return this.m_AreaPR;
/*     */   }
/*     */ 
/*     */   
/*     */   public void computeCurves() {
/*  52 */     this.m_ROC = new ArrayList();
/*  53 */     this.m_PR = new ArrayList();
/*  54 */     this.m_AreaPR = 0.0D;
/*  55 */     this.m_AreaROC = 0.5D;
/*  56 */     if (this.m_Predictions.getNbPos() != 0) {
/*  57 */       enumerateThresholds();
/*  58 */       this.m_AreaPR = computeArea(this.m_PR);
/*  59 */       if (this.m_Predictions.getNbNeg() != 0) {
/*  60 */         this.m_AreaROC = computeArea(this.m_ROC);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setThresholds(double[] thr) {
/*  67 */     this.m_Thresholds = thr;
/*     */   }
/*     */   
/*     */   public void enumerateThresholds() {
/*  71 */     if (this.m_Thresholds == null) {
/*  72 */       enumerateThresholdsAll();
/*     */     } else {
/*  74 */       enumerateThresholdsSelected(this.m_Thresholds);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void enumerateThresholdsAll() {
/*  80 */     this.m_ExtendPR = true;
/*     */     
/*  82 */     addOutputROC(0, 0);
/*  83 */     boolean first = true;
/*  84 */     int TP_cnt = 0, FP_cnt = 0;
/*  85 */     double prev = Double.NaN;
/*     */     
/*  87 */     for (int i = 0; i < this.m_Predictions.size(); i++) {
/*  88 */       DoubleBooleanCount val = this.m_Predictions.get(i);
/*     */       
/*  90 */       if (val.getDouble() != prev && !first)
/*     */       {
/*  92 */         addOutput(TP_cnt, FP_cnt);
/*     */       }
/*  94 */       if (val.getBoolean().booleanValue()) {
/*  95 */         TP_cnt += val.getCount();
/*     */       } else {
/*  97 */         FP_cnt += val.getCount();
/*     */       } 
/*     */       
/* 100 */       prev = val.getDouble();
/* 101 */       first = false;
/*     */     } 
/*     */ 
/*     */     
/* 105 */     addOutput(TP_cnt, FP_cnt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void enumerateThresholdsSelected(double[] thr) {
/* 110 */     this.m_ExtendPR = true;
/*     */ 
/*     */     
/* 113 */     addOutputROC(0, 0);
/* 114 */     int idx = 0;
/* 115 */     int TP_cnt = 0, FP_cnt = 0;
/* 116 */     int prevTP_cnt = 0, prevFP_cnt = 0;
/* 117 */     for (int i = thr.length - 1; i >= 0; i--) {
/* 118 */       DoubleBooleanCount val = null;
/* 119 */       while (idx < this.m_Predictions.size() && (val = this.m_Predictions.get(idx)).getDouble() >= thr[i]) {
/* 120 */         if (val.getBoolean().booleanValue()) {
/* 121 */           TP_cnt += val.getCount();
/*     */         } else {
/* 123 */           FP_cnt += val.getCount();
/*     */         } 
/* 125 */         idx++;
/*     */       } 
/* 127 */       if (TP_cnt != prevTP_cnt || FP_cnt != prevFP_cnt) {
/* 128 */         addOutput(TP_cnt, FP_cnt);
/*     */       }
/* 130 */       prevTP_cnt = TP_cnt;
/* 131 */       prevFP_cnt = FP_cnt;
/*     */     } 
/*     */     
/* 134 */     addOutput(TP_cnt, FP_cnt);
/*     */   }
/*     */   
/*     */   public double computeArea(ArrayList<double[]> curve) {
/* 138 */     double area = 0.0D;
/*     */ 
/*     */     
/* 141 */     if (curve.size() > 0) {
/* 142 */       double[] prev = curve.get(0);
/*     */       
/* 144 */       for (int i = 1; i < curve.size(); i++) {
/* 145 */         double[] pt = curve.get(i);
/*     */         
/* 147 */         area += 0.5D * (pt[1] + prev[1]) * (pt[0] - prev[0]);
/* 148 */         prev = pt;
/*     */       } 
/*     */     } 
/* 151 */     return area;
/*     */   }
/*     */   
/*     */   public void addOutput(int TP, int FP) {
/* 155 */     addOutputROC(TP, FP);
/* 156 */     addOutputPR(TP, FP);
/*     */   }
/*     */   
/*     */   public void addOutputROC(int TP, int FP) {
/* 160 */     double[] point = new double[2];
/* 161 */     point[0] = FP / this.m_Predictions.getNbNeg();
/* 162 */     point[1] = TP / this.m_Predictions.getNbPos();
/* 163 */     this.m_ROC.add(point);
/*     */   }
/*     */   
/*     */   public void addOutputPR(int TP, int FP) {
/* 167 */     int P = TP + FP;
/* 168 */     if (P != 0) {
/* 169 */       double prec = TP / P;
/* 170 */       double recall = TP / this.m_Predictions.getNbPos();
/* 171 */       if (this.m_ExtendPR) {
/*     */ 
/*     */         
/* 174 */         addPointPR(prec, 0.0D);
/* 175 */         this.m_ExtendPR = false;
/*     */       } else {
/* 177 */         for (int crTP = this.m_PrevTP + 1; crTP < TP; crTP++) {
/* 178 */           double crFP = this.m_PrevFP + (FP - this.m_PrevFP) / (TP - this.m_PrevTP) * (crTP - this.m_PrevTP);
/* 179 */           double crPrec = crTP / (crTP + crFP);
/* 180 */           double crRecall = crTP / this.m_Predictions.getNbPos();
/*     */           
/* 182 */           addPointPROptimized(crPrec, crRecall);
/*     */         } 
/*     */       } 
/* 185 */       addPointPROptimized(prec, recall);
/*     */       
/* 187 */       this.m_PrevTP = TP;
/* 188 */       this.m_PrevFP = FP;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addPointPR(double prec, double recall) {
/* 193 */     double[] point = new double[2];
/* 194 */     point[0] = recall;
/* 195 */     point[1] = prec;
/* 196 */     this.m_PR.add(point);
/*     */   }
/*     */   
/*     */   public void addPointPROptimized(double prec, double recall) {
/* 200 */     int size = this.m_PR.size();
/* 201 */     double[] prev = this.m_PR.get(size - 1);
/* 202 */     if (prev[0] != recall || prev[1] != prec) {
/* 203 */       if (size <= 1) {
/* 204 */         addPointPR(prec, recall);
/*     */       } else {
/* 206 */         double[] prev2 = this.m_PR.get(size - 2);
/* 207 */         if (Math.abs(prev[1] - prec) < 1.0E-15D && Math.abs(prev2[1] - prec) < 1.0E-15D) {
/*     */           
/* 209 */           prev[0] = recall;
/* 210 */         } else if (Math.abs(prev[0] - recall) < 1.0E-15D && Math.abs(prev2[0] - recall) < 1.0E-15D) {
/*     */           
/* 212 */           prev[1] = prec;
/*     */         } else {
/* 214 */           addPointPR(prec, recall);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public double getPrecisionAtRecall(int j) {
/* 221 */     return this.m_PrecisionAtRecall[j];
/*     */   }
/*     */   
/*     */   public void computePrecisions(double[] recallValues) {
/* 225 */     if (recallValues == null) {
/*     */       return;
/*     */     }
/* 228 */     int nbRecalls = recallValues.length;
/* 229 */     this.m_PrecisionAtRecall = new double[nbRecalls];
/* 230 */     for (int i = 0; i < nbRecalls; i++) {
/* 231 */       this.m_PrecisionAtRecall[i] = computePrecision(recallValues[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public double computePrecision(double recall) {
/* 236 */     return MathUtil.interpolate(recall, this.m_PR);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\ROCAndPRCurve.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */