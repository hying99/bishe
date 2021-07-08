/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.main.ClusModelInfoList;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
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
/*     */ public class ClusErrorList
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_NbTotal;
/*     */   protected int m_NbExamples;
/*     */   protected int m_NbCover;
/*  47 */   public ArrayList m_Error = new ArrayList();
/*  48 */   public ArrayList m_ErrorWithNulls = new ArrayList();
/*     */   
/*     */   public ClusErrorList() {
/*  51 */     this.m_NbTotal = -1;
/*     */   }
/*     */   
/*     */   public void setNbTotal(int nb) {
/*  55 */     this.m_NbTotal = nb;
/*     */   }
/*     */   
/*     */   public int getNbTotal() {
/*  59 */     return (this.m_NbTotal == -1) ? this.m_NbExamples : this.m_NbTotal;
/*     */   }
/*     */   
/*     */   public void setNbExamples(int nb) {
/*  63 */     this.m_NbExamples = nb;
/*     */   }
/*     */   
/*     */   public void setNbExamples(int nb, int cover) {
/*  67 */     this.m_NbExamples = nb;
/*  68 */     this.m_NbCover = cover;
/*     */   }
/*     */   
/*     */   public void setWeights(ClusAttributeWeights weights) {
/*  72 */     for (int i = 0; i < this.m_Error.size(); i++) {
/*  73 */       ClusError err = this.m_Error.get(i);
/*  74 */       err.setWeights(weights);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void checkChildren() {
/*  79 */     int nb_e = this.m_Error.size();
/*  80 */     for (int i = 0; i < nb_e; i++) {
/*  81 */       ClusError err = this.m_Error.get(i);
/*  82 */       if (err.getParent() != this) {
/*  83 */         System.out.println("Child: " + err + " has incorrect parent: " + err.getParent() + " " + this);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void calcError(TupleIterator iter, ClusModel model) throws ClusException, IOException {
/*  89 */     iter.init();
/*  90 */     DataTuple tuple = iter.readTuple();
/*  91 */     while (tuple != null) {
/*  92 */       ClusStatistic pred = model.predictWeighted(tuple);
/*  93 */       addExample(tuple, pred);
/*  94 */       tuple = iter.readTuple();
/*     */     } 
/*  96 */     iter.close();
/*     */   }
/*     */   
/*     */   public ClusErrorList getErrorClone() {
/* 100 */     ClusErrorList res = new ClusErrorList();
/* 101 */     int nb = this.m_Error.size();
/* 102 */     for (int i = 0; i < nb; i++) {
/* 103 */       ClusError err = this.m_Error.get(i);
/* 104 */       res.addError(err.getErrorClone(res));
/*     */     } 
/* 106 */     return res;
/*     */   }
/*     */   
/*     */   public ClusErrorList getErrorClone(String model) {
/* 110 */     ClusErrorList res = new ClusErrorList();
/* 111 */     int nb = this.m_Error.size();
/* 112 */     for (int i = 0; i < nb; i++) {
/* 113 */       ClusError err = this.m_Error.get(i);
/* 114 */       if (err.isComputeForModel(model)) {
/* 115 */         res.addError(err.getErrorClone(res));
/*     */       } else {
/* 117 */         res.addError(null);
/*     */       } 
/*     */     } 
/* 120 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addError(ClusError err) {
/* 125 */     if (err != null) {
/* 126 */       this.m_Error.add(err);
/*     */     }
/* 128 */     this.m_ErrorWithNulls.add(err);
/*     */   }
/*     */   
/*     */   public void addErrors(ClusErrorList error) {
/* 132 */     for (int i = 0; i < error.getNbErrors(); i++) {
/* 133 */       addError(error.getError(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNbErrors() {
/* 138 */     return this.m_Error.size();
/*     */   }
/*     */   
/*     */   public ClusError getFirstError() {
/* 142 */     return getError(0);
/*     */   }
/*     */   
/*     */   public ClusError getError(int idx) {
/* 146 */     return this.m_Error.get(idx);
/*     */   }
/*     */   
/*     */   public ClusError getErrorOrNull(int idx) {
/* 150 */     return this.m_ErrorWithNulls.get(idx);
/*     */   }
/*     */   
/*     */   public void clear() {
/* 154 */     for (int i = 0; i < this.m_ErrorWithNulls.size(); i++) {
/* 155 */       this.m_ErrorWithNulls.set(i, null);
/*     */     }
/*     */   }
/*     */   
/*     */   public ClusError getErrorByName(String name) {
/* 160 */     int nb_e = this.m_Error.size();
/* 161 */     for (int i = 0; i < nb_e; i++) {
/* 162 */       ClusError err = this.m_Error.get(i);
/* 163 */       if (err.getName().equals(name)) {
/* 164 */         return err;
/*     */       }
/*     */     } 
/* 167 */     return null;
/*     */   }
/*     */   
/*     */   public void compute(RowData data, ClusModelInfo model) throws ClusException {
/* 171 */     int nb = this.m_Error.size();
/* 172 */     for (int i = nb - 1; i >= 0; i--) {
/* 173 */       ClusError err = this.m_Error.get(i);
/* 174 */       if (err.isComputeForModel(model.getName())) {
/* 175 */         err.compute(data, model.getModel());
/*     */       } else {
/* 177 */         this.m_Error.remove(i);
/*     */       } 
/*     */     } 
/* 180 */     this.m_NbExamples = data.getNbRows();
/* 181 */     this.m_NbCover = this.m_NbExamples;
/*     */   }
/*     */   
/*     */   public void compute(RowData data, ClusModel model) throws ClusException {
/* 185 */     int nb = this.m_Error.size();
/* 186 */     for (int i = 0; i < nb; i++) {
/* 187 */       ClusError err = this.m_Error.get(i);
/* 188 */       err.compute(data, model);
/*     */     } 
/* 190 */     this.m_NbExamples = data.getNbRows();
/* 191 */     this.m_NbCover = this.m_NbExamples;
/*     */   }
/*     */   
/*     */   public void reset() {
/* 195 */     int nb = this.m_Error.size();
/* 196 */     for (int i = 0; i < nb; i++) {
/* 197 */       ClusError err = this.m_Error.get(i);
/* 198 */       err.reset();
/*     */     } 
/* 200 */     this.m_NbExamples = 0;
/* 201 */     this.m_NbCover = 0;
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic stat) {
/* 205 */     this.m_NbExamples++;
/* 206 */     int nb = this.m_Error.size();
/* 207 */     if (stat != null && stat.isValidPrediction()) {
/* 208 */       this.m_NbCover++;
/* 209 */       for (int i = 0; i < nb; i++) {
/* 210 */         ClusError err = this.m_Error.get(i);
/* 211 */         err.addExample(tuple, stat);
/*     */       } 
/*     */     } else {
/* 214 */       for (int i = 0; i < nb; i++) {
/* 215 */         ClusError err = this.m_Error.get(i);
/* 216 */         err.addInvalid(tuple);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple real, DataTuple pred) {
/* 222 */     this.m_NbExamples++;
/* 223 */     int nb = this.m_Error.size();
/* 224 */     for (int i = 0; i < nb; i++) {
/* 225 */       ClusError err = this.m_Error.get(i);
/* 226 */       err.addExample(real, pred);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample() {
/* 231 */     this.m_NbExamples++;
/* 232 */     this.m_NbCover++;
/*     */   }
/*     */   
/*     */   public void add(ClusErrorList par) {
/* 236 */     int nb = this.m_ErrorWithNulls.size();
/* 237 */     for (int i = 0; i < nb; i++) {
/* 238 */       ClusError my = this.m_ErrorWithNulls.get(i);
/* 239 */       ClusError your = par.getErrorOrNull(i);
/* 240 */       if (your != null) {
/* 241 */         my.add(your);
/*     */       }
/*     */     } 
/* 244 */     this.m_NbExamples += par.getNbExamples();
/* 245 */     this.m_NbCover += par.getNbCover();
/*     */   }
/*     */   
/*     */   public void updateFromGlobalMeasure(ClusErrorList par) {
/* 249 */     int nb = this.m_Error.size();
/* 250 */     for (int i = 0; i < nb; i++) {
/* 251 */       ClusError err = this.m_Error.get(i);
/* 252 */       err.updateFromGlobalMeasure(par.getError(i));
/*     */     } 
/* 254 */     setNbTotal(par.getNbExamples());
/*     */   }
/*     */   
/*     */   public double getErrorClassif() {
/* 258 */     ClusError err = getError(0);
/* 259 */     return err.get_error_classif();
/*     */   }
/*     */   
/*     */   public double getErrorAccuracy() {
/* 263 */     ClusError err = getError(0);
/* 264 */     return err.get_accuracy();
/*     */   }
/*     */   
/*     */   public double getErrorPrecision() {
/* 268 */     ClusError err = getError(0);
/* 269 */     return err.get_precision();
/*     */   }
/*     */   
/*     */   public double getErrorRecall() {
/* 273 */     ClusError err = getError(0);
/* 274 */     return err.get_recall();
/*     */   }
/*     */   
/*     */   public double getErrorAuc() {
/* 278 */     ClusError err = getError(0);
/* 279 */     return err.get_auc();
/*     */   }
/*     */   
/*     */   public void showError(PrintWriter out) {
/* 283 */     int nb = this.m_Error.size();
/* 284 */     out.println("Number of examples: " + getNbTotal() + " (covered: " + getNbCover() + ")");
/* 285 */     for (int i = 0; i < nb; i++) {
/* 286 */       ClusError err1 = getError(i);
/* 287 */       out.print(err1.getName() + ": ");
/* 288 */       err1.showModelError(out, 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean checkCoverage(ClusModelInfoList models, int type, int nb) {
/* 293 */     int nb_models = models.getNbModels();
/* 294 */     for (int j = 0; j < nb_models; j++) {
/* 295 */       if (models.getModelInfo(j) != null) {
/* 296 */         ClusErrorList parent = models.getModelInfo(j).getError(type);
/* 297 */         if (parent.getNbCover() != nb) {
/* 298 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 302 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void showError(ClusModelInfoList models, int type, String bName, PrintWriter out) throws IOException {
/* 307 */     int nb = this.m_Error.size();
/* 308 */     ClusModelInfo definf = models.getModelInfo(0);
/* 309 */     ClusErrorList defpar = definf.getError(type);
/* 310 */     out.println("Number of examples: " + defpar.getNbExamples());
/* 311 */     int nb_models = models.getNbModels();
/*     */     
/* 313 */     if (!checkCoverage(models, type, defpar.getNbExamples())) {
/* 314 */       out.println("Coverage:");
/* 315 */       for (int j = 0; j < nb_models; j++) {
/* 316 */         ClusModelInfo inf = models.getModelInfo(j);
/* 317 */         if (inf != null) {
/* 318 */           ClusErrorList parent = inf.getError(type);
/* 319 */           out.println("  " + inf.getName() + ": " + parent.getNbCover());
/*     */         } 
/*     */       } 
/*     */     } 
/* 323 */     for (int i = 0; i < nb; i++) {
/* 324 */       ClusError err1 = getError(i);
/* 325 */       boolean has_models = false; int j;
/* 326 */       for (j = 0; j < nb_models; j++) {
/* 327 */         ClusModelInfo inf = models.getModelInfo(j);
/* 328 */         if (inf != null && inf.getError(type).getErrorOrNull(i) != null) {
/* 329 */           has_models = true;
/*     */         }
/*     */       } 
/* 332 */       if (has_models) {
/* 333 */         out.println(err1.getName());
/* 334 */         for (j = 0; j < nb_models; j++) {
/* 335 */           ClusModelInfo inf = models.getModelInfo(j);
/* 336 */           if (inf != null) {
/* 337 */             ClusError err2 = inf.getError(type).getErrorOrNull(i);
/* 338 */             if (err2 != null) {
/* 339 */               if (err2.isMultiLine()) {
/* 340 */                 out.print("   " + inf.getName() + ": ");
/*     */               } else {
/* 342 */                 out.print("   " + StringUtils.printStr(inf.getName(), 15) + ": ");
/*     */               } 
/*     */               
/* 345 */               err2.showModelError(out, bName, 1);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void printExtraError(ClusModelInfoList models, int type, PrintWriter out) {
/* 354 */     int ctr = 0;
/* 355 */     int nb_models = models.getNbModels();
/* 356 */     for (int j = 0; j < nb_models; j++) {
/* 357 */       ClusModelInfo inf = models.getModelInfo(j);
/* 358 */       if (inf != null) {
/* 359 */         ClusErrorList parent = inf.getExtraError(type);
/* 360 */         if (parent != null && inf.hasModel()) {
/* 361 */           int nb_err = parent.getNbErrors();
/* 362 */           for (int i = 0; i < nb_err; i++) {
/* 363 */             ClusError err = parent.getError(i);
/* 364 */             out.print(err.getName() + ": " + StringUtils.printStr(inf.getName(), 15) + ": ");
/* 365 */             err.showModelError(out, 1);
/* 366 */             ctr++;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 371 */     if (ctr != 0) {
/* 372 */       out.println();
/*     */     }
/*     */   }
/*     */   
/*     */   public void showErrorBrief(ClusModelInfoList models, int type, PrintWriter out) {
/* 377 */     int nb = this.m_Error.size();
/* 378 */     for (int i = 0; i < nb; i++) {
/* 379 */       ClusError err1 = getError(i);
/* 380 */       if (type == 0) {
/* 381 */         out.print("Train ");
/*     */       } else {
/* 383 */         out.print("Test ");
/*     */       } 
/* 385 */       out.println(err1.getName());
/* 386 */       int nb_models = models.getNbModels();
/* 387 */       for (int j = 0; j < nb_models; j++) {
/* 388 */         ClusModelInfo inf = models.getModelInfo(j);
/* 389 */         ClusErrorList parent = inf.getError(type);
/* 390 */         ClusError err2 = parent.getError(i);
/* 391 */         out.print("   " + StringUtils.printStr(inf.getName(), 15) + ": ");
/* 392 */         err2.showModelError(out, 3);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/* 398 */     return "   ";
/*     */   }
/*     */   
/*     */   public int getNbExamples() {
/* 402 */     return this.m_NbExamples;
/*     */   }
/*     */   
/*     */   public int getNbCover() {
/* 406 */     return this.m_NbCover;
/*     */   }
/*     */   
/*     */   public NumberFormat getFormat() {
/* 410 */     return ClusFormat.FOUR_AFTER_DOT;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\ClusErrorList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */