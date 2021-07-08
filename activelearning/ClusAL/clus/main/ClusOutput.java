/*     */ package clus.main;
/*     */ 
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.ext.ensembles.ClusOOBErrorEstimate;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import jeans.resource.ResourceInfo;
/*     */ import jeans.util.FileUtil;
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
/*     */ public class ClusOutput
/*     */ {
/*     */   protected ClusSchema m_Schema;
/*     */   protected Settings m_Sett;
/*     */   protected PrintWriter m_Writer;
/*     */   protected String m_Fname;
/*     */   protected Settings m_Sett2;
/*     */   protected StringWriter m_StrWrt;
/*     */   
/*     */   public ClusOutput(String fname, ClusSchema schema, Settings sett) throws IOException {
/*  56 */     this.m_Schema = schema;
/*  57 */     this.m_Sett = sett;
/*  58 */     this.m_Sett2 = sett;
/*  59 */     this.m_Fname = fname;
/*  60 */     this.m_Writer = sett.getFileAbsoluteWriter(fname);
/*     */   }
/*     */   
/*     */   public ClusOutput(String fname, ClusSchema schema, Settings sett, boolean fix) throws IOException {
/*  64 */     this.m_Schema = schema;
/*  65 */     this.m_Sett = sett;
/*  66 */     this.m_Sett2 = sett;
/*  67 */     this.m_Fname = fname;
/*  68 */     this.m_Writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/*     */   }
/*     */   
/*     */   public ClusOutput(ClusSchema schema, Settings sett) throws IOException {
/*  72 */     this.m_Schema = schema;
/*  73 */     this.m_Sett = sett;
/*  74 */     this.m_Sett2 = sett;
/*  75 */     this.m_StrWrt = new StringWriter();
/*  76 */     this.m_Writer = new PrintWriter(this.m_StrWrt);
/*     */   }
/*     */   
/*     */   public void print(String str) {
/*  80 */     this.m_Writer.print(str);
/*     */   }
/*     */   
/*     */   public String getString() {
/*  84 */     return this.m_StrWrt.toString();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  88 */     return this.m_Sett;
/*     */   }
/*     */   
/*     */   public void writeLabels(HashSet<String> newLabels) {
/*  92 */     this.m_Writer.println("New Labels Amount:" + newLabels.size());
/*  93 */     for (String label : newLabels) {
/*  94 */       this.m_Writer.println(label);
/*     */     }
/*  96 */     this.m_Writer.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeHeader() throws IOException {
/* 101 */     String relname = this.m_Schema.getRelationName();
/* 102 */     this.m_Writer.println("Clus run " + relname);
/* 103 */     this.m_Writer.println(StringUtils.makeString('*', 9 + relname.length()));
/* 104 */     this.m_Writer.println();
/* 105 */     Date date = this.m_Schema.getSettings().getDate();
/* 106 */     this.m_Writer.println("Date: " + DateFormat.getInstance().format(date));
/* 107 */     this.m_Writer.println("File: " + this.m_Fname);
/* 108 */     int a_tot = this.m_Schema.getNbAttributes();
/* 109 */     int a_in = this.m_Schema.getNbDescriptiveAttributes();
/* 110 */     int a_out = this.m_Schema.getNbTargetAttributes();
/* 111 */     this.m_Writer.println("Attributes: " + a_tot + " (input: " + a_in + ", output: " + a_out + ")");
/* 112 */     this.m_Writer.println("Missing values: " + (this.m_Schema.hasMissing() ? "Yes" : "No"));
/* 113 */     if (ResourceInfo.isLibLoaded()) {
/* 114 */       this.m_Writer.println("Memory usage: " + ClusStat.m_InitialMemory + " kB (initial), " + ClusStat.m_LoadedMemory + " kB (data loaded)");
/*     */     }
/* 116 */     this.m_Writer.println();
/* 117 */     this.m_Sett.show(this.m_Writer);
/* 118 */     this.m_Writer.flush();
/*     */   }
/*     */   
/*     */   public void writeBrief(ClusRun cr) throws IOException {
/* 122 */     String ridx = cr.getIndexString();
/* 123 */     this.m_Writer.println("Run: " + ridx);
/* 124 */     ClusErrorList te_err = cr.getTestError();
/* 125 */     if (te_err != null) {
/* 126 */       te_err.showErrorBrief(cr, 1, this.m_Writer);
/*     */     }
/* 128 */     ClusErrorList tr_err = cr.getTrainError();
/* 129 */     if (this.m_Sett.isOutTrainError() && tr_err != null) {
/* 130 */       tr_err.showErrorBrief(cr, 0, this.m_Writer);
/*     */     }
/* 132 */     this.m_Writer.println();
/*     */   }
/*     */   
/*     */   public void writeOutput(ClusRun cr, boolean detail) throws IOException, ClusException {
/* 136 */     writeOutput(cr, detail, false);
/*     */   }
/*     */   
/*     */   public void writeOutput(ClusRun cr, boolean detail, boolean outputtrain) throws IOException, ClusException {
/* 140 */     ArrayList<ClusModel> models = new ArrayList();
/* 141 */     String ridx = cr.getIndexString();
/* 142 */     this.m_Writer.println("Run: " + ridx);
/* 143 */     this.m_Writer.println(StringUtils.makeString('*', 5 + ridx.length()));
/* 144 */     this.m_Writer.println();
/* 145 */     this.m_Writer.println("Statistics");
/* 146 */     this.m_Writer.println("----------");
/* 147 */     this.m_Writer.println();
/* 148 */     this.m_Writer.println("FTValue (FTest): " + this.m_Sett.getFTest());
/* 149 */     double tsec = cr.getInductionTime() / 1000.0D;
/* 150 */     double tpru = cr.getPruneTime() / 1000.0D;
/*     */     
/* 152 */     for (int i = 0; i < cr.getNbModels(); i++) {
/* 153 */       ClusModelInfo mi = cr.getModelInfo(i);
/* 154 */       if (mi != null) {
/* 155 */         ClusModel root = mi.getModel();
/* 156 */         if (mi.shouldPruneInvalid()) {
/* 157 */           root = root.prune(0);
/*     */         }
/* 159 */         models.add(root);
/*     */       } else {
/* 161 */         models.add(null);
/*     */       } 
/*     */     } 
/*     */     
/* 165 */     String cpu = ResourceInfo.isLibLoaded() ? " (CPU)" : "";
/* 166 */     this.m_Writer.println("Induction Time: " + ClusFormat.FOUR_AFTER_DOT.format(tsec) + " sec" + cpu);
/* 167 */     this.m_Writer.println("Pruning Time: " + ClusFormat.FOUR_AFTER_DOT.format(tpru) + " sec" + cpu);
/* 168 */     this.m_Writer.println("Model information");
/* 169 */     for (int j = 0; j < cr.getNbModels(); j++) {
/* 170 */       ClusModelInfo mi = cr.getModelInfo(j);
/* 171 */       if (mi != null) {
/* 172 */         ClusModel model = models.get(j);
/*     */ 
/*     */         
/* 175 */         if (model != null) {
/* 176 */           this.m_Writer.print("     " + mi.getName() + ": ");
/* 177 */           String info_str = model.getModelInfo();
/* 178 */           String[] arrayOfString = info_str.split("\\s*\\,\\s*");
/* 179 */           for (int m = 0; m < arrayOfString.length; m++) {
/* 180 */             if (m > 0) {
/* 181 */               this.m_Writer.print(StringUtils.makeString(' ', mi.getName().length() + 7));
/*     */             }
/* 183 */             this.m_Writer.println(arrayOfString[m]);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 189 */     String bName = FileUtil.getName(this.m_Fname);
/* 190 */     this.m_Writer.println();
/* 191 */     ClusErrorList te_err = cr.getTestError();
/* 192 */     if (this.m_Sett.isOutFoldError() || detail) {
/* 193 */       if (outputtrain) {
/* 194 */         ClusErrorList tr_err = cr.getTrainError();
/* 195 */         if (tr_err != null) {
/* 196 */           if (ClusOOBErrorEstimate.isOOBCalculation()) {
/* 197 */             this.m_Writer.println("Out-Of-Bag Estimate of Error");
/*     */           } else {
/* 199 */             this.m_Writer.println("Training error");
/*     */           } 
/* 201 */           this.m_Writer.println("--------------");
/* 202 */           this.m_Writer.println();
/* 203 */           tr_err.showError(cr, 0, bName + ".train", this.m_Writer);
/*     */           
/* 205 */           this.m_Writer.println();
/*     */         } 
/* 207 */         ClusErrorList.printExtraError(cr, 0, this.m_Writer);
/*     */       } 
/* 209 */       ClusErrorList va_err = cr.getValidationError();
/* 210 */       if (va_err != null && this.m_Sett.isOutValidError()) {
/* 211 */         this.m_Writer.println("Validation error");
/* 212 */         this.m_Writer.println("----------------");
/* 213 */         this.m_Writer.println();
/* 214 */         va_err.showError(cr, 2, bName + ".valid", this.m_Writer);
/*     */         
/* 216 */         this.m_Writer.println();
/*     */       } 
/* 218 */       if (te_err != null && this.m_Sett.isOutTestError()) {
/* 219 */         this.m_Writer.println("Testing error");
/* 220 */         this.m_Writer.println("-------------");
/* 221 */         this.m_Writer.println();
/* 222 */         te_err.showError(cr, 1, bName + ".test", this.m_Writer);
/*     */         
/* 224 */         this.m_Writer.println();
/*     */       } 
/*     */     } 
/* 227 */     StatisticPrintInfo info = this.m_Sett.getStatisticPrintInfo();
/* 228 */     for (int k = 0; k < cr.getNbModels(); k++) {
/* 229 */       if (cr.getModelInfo(k) != null && models.get(k) != null && this.m_Sett.shouldShowModel(k)) {
/* 230 */         ClusModelInfo mi = cr.getModelInfo(k);
/* 231 */         ClusModel root = models.get(k);
/* 232 */         String modelname = mi.getName() + " Model";
/* 233 */         this.m_Writer.println(modelname);
/* 234 */         this.m_Writer.println(StringUtils.makeString('*', modelname.length()));
/* 235 */         this.m_Writer.println();
/* 236 */         if (this.m_Sett.isPrintModelAndExamples()) {
/* 237 */           RowData pex = (RowData)cr.getTrainingSet();
/*     */           
/* 239 */           if (te_err != null) {
/* 240 */             pex = cr.getTestSet();
/*     */           }
/* 242 */           root.printModelAndExamples(this.m_Writer, info, pex);
/*     */         } else {
/* 244 */           root.printModel(this.m_Writer, info);
/*     */         } 
/* 246 */         this.m_Writer.println();
/* 247 */         if (getSettings().isOutputPythonModel()) {
/* 248 */           if (getSettings().isEnsembleMode() && k == 1) {
/* 249 */             root.printModelToPythonScript(this.m_Writer);
/*     */           } else {
/*     */             
/* 252 */             this.m_Writer.print("def clus_tree( ");
/* 253 */             ClusAttrType[] cat = ClusSchema.vectorToAttrArray(this.m_Schema.collectAttributes(1, -1));
/* 254 */             for (int ii = 0; ii < cat.length - 1; ii++) {
/* 255 */               this.m_Writer.print(cat[ii].getName() + ",");
/*     */             }
/* 257 */             this.m_Writer.println(cat[cat.length - 1].getName() + " ):");
/* 258 */             root.printModelToPythonScript(this.m_Writer);
/* 259 */             this.m_Writer.println();
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 264 */     if (getSettings().isOutputDatabaseQueries()) {
/* 265 */       int starttree = getSettings().getStartTreeCpt();
/* 266 */       int startitem = getSettings().getStartItemCpt();
/* 267 */       ClusModel root = models.get(cr.getNbModels() - 1);
/*     */       
/* 269 */       String out_database_name = this.m_Sett2.getAppName() + ".txt";
/* 270 */       PrintWriter database_writer = this.m_Sett2.getFileAbsoluteWriter(out_database_name);
/* 271 */       root.printModelToQuery(database_writer, cr, starttree, startitem, getSettings().isExhaustiveSearch());
/* 272 */       database_writer.close();
/* 273 */       System.out.println("The queries are in " + out_database_name);
/*     */     } 
/* 275 */     this.m_Writer.flush();
/*     */   }
/*     */   
/*     */   public void writeActiveOutput(ClusRun cr, boolean detail, ClusActiveLearningAlgorithm al, ClusLabelInferingAlgorithm lpa, int type, double OOBError) throws IOException, ClusException {
/*     */     ClusErrorList tr_err, va_err;
/* 280 */     ArrayList<ClusModel> models = new ArrayList();
/*     */ 
/*     */     
/* 283 */     this.m_Writer.println("Iteration: " + al.getIterationCounter());
/* 284 */     this.m_Writer.println(StringUtils.makeString('*', 5 + "iteration".length()));
/* 285 */     this.m_Writer.println();
/* 286 */     if (al.getIterationCounter() > 0) {
/* 287 */       this.m_Writer.println("Query Building Time on this iteration: " + al.getIteration().getQueryBuildingTime() + " seconds");
/*     */     } else {
/* 289 */       this.m_Writer.println("Query Building Time on this iteration: 0.0 seconds");
/*     */     } 
/*     */     
/* 292 */     this.m_Writer.println("Total Query Building Time: " + al.getQueryBuildingTimeTotal() + " seconds");
/*     */     
/* 294 */     this.m_Writer.println();
/*     */     
/* 296 */     this.m_Writer.println("Statistics");
/* 297 */     this.m_Writer.println("----------");
/* 298 */     this.m_Writer.println();
/* 299 */     this.m_Writer.println("FTValue (FTest): " + this.m_Sett.getFTest());
/* 300 */     double tsec = cr.getInductionTime() / 1000.0D;
/* 301 */     double tpru = cr.getPruneTime() / 1000.0D;
/*     */     
/* 303 */     for (int i = 0; i < cr.getNbModels(); i++) {
/* 304 */       ClusModelInfo mi = cr.getModelInfo(i);
/* 305 */       if (mi != null) {
/* 306 */         ClusModel root = mi.getModel();
/* 307 */         if (mi.shouldPruneInvalid()) {
/* 308 */           root = root.prune(0);
/*     */         }
/* 310 */         models.add(root);
/*     */       } else {
/* 312 */         models.add(null);
/*     */       } 
/*     */     } 
/*     */     
/* 316 */     String cpu = ResourceInfo.isLibLoaded() ? " (CPU)" : "";
/* 317 */     this.m_Writer.println("Induction Time: " + ClusFormat.FOUR_AFTER_DOT.format(tsec) + " sec" + cpu);
/* 318 */     this.m_Writer.println("Pruning Time: " + ClusFormat.FOUR_AFTER_DOT.format(tpru) + " sec" + cpu);
/* 319 */     this.m_Writer.println("Model information");
/* 320 */     for (int j = 0; j < cr.getNbModels(); j++) {
/* 321 */       ClusModelInfo mi = cr.getModelInfo(j);
/* 322 */       if (mi != null) {
/* 323 */         ClusModel model = models.get(j);
/*     */ 
/*     */         
/* 326 */         if (model != null) {
/* 327 */           this.m_Writer.print("     " + mi.getName() + ": ");
/* 328 */           String info_str = model.getModelInfo();
/* 329 */           String[] arrayOfString = info_str.split("\\s*\\,\\s*");
/* 330 */           for (int m = 0; m < arrayOfString.length; m++) {
/* 331 */             if (m > 0) {
/* 332 */               this.m_Writer.print(StringUtils.makeString(' ', mi.getName().length() + 7));
/*     */             }
/*     */             
/* 335 */             this.m_Writer.println(arrayOfString[m]);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 341 */     String bName = FileUtil.getName(this.m_Fname);
/* 342 */     this.m_Writer.println();
/* 343 */     this.m_Writer.println("Amount of Labels Answered at this iteration: " + al.getIteration().getLabelsAnswered());
/* 344 */     this.m_Writer.println("Amount of Labels Answered up to this iteration: " + al.getLabelsAnswered());
/* 345 */     this.m_Writer.println("Amount of Negative Labels Answered at this iteration: " + al.getIteration().getNegativeAnswer());
/*     */     
/* 347 */     this.m_Writer.println("Amount of Positive Labels Queried at this iteration: " + al.getIteration().getPositiveAnswers());
/* 348 */     this.m_Writer.println("Amount of Positive Labels Queried up to this iteration: " + al.getPositiveQueried());
/*     */     
/* 350 */     this.m_Writer.println("Amount of Negative Labels Queried at this iteration: " + al.getIteration().getNegativeAnswerNoHierarchy());
/* 351 */     this.m_Writer.println("Amount of Negative Labels Queried up to this iteration: " + al.getNegativeQueried());
/*     */     
/* 353 */     this.m_Writer.println("Amount of Labels Queried at this iteration: " + al.getIteration().getLabelsQueried());
/* 354 */     this.m_Writer.println("Amount of Labels Queried up to this iteration: " + al.getLabelsQueried());
/*     */     
/* 356 */     this.m_Writer.println();
/*     */     
/* 358 */     this.m_Writer.println("Budget Available at this Iteration: " + al.getIteration().getBudget());
/* 359 */     this.m_Writer.println("Budget Spent on this Iteration: " + al.getIteration().getBudgetSpent());
/* 360 */     this.m_Writer.println("Budget Spent up to this Iteration: " + al.getBudgetSpent());
/* 361 */     this.m_Writer.println();
/*     */     
/* 363 */     ClusErrorList te_err = cr.getTestError();
/* 364 */     switch (type) {
/*     */       case 0:
/* 366 */         this.m_Writer.println("Testing error");
/* 367 */         this.m_Writer.println("-------------");
/* 368 */         this.m_Writer.println();
/* 369 */         te_err.showError(cr, 1, bName + ".test", this.m_Writer);
/*     */         
/* 371 */         this.m_Writer.println();
/*     */         break;
/*     */       case 1:
/* 374 */         tr_err = cr.getTrainError();
/* 375 */         if (tr_err != null) {
/* 376 */           this.m_Writer.println("Training error");
/*     */           
/* 378 */           this.m_Writer.println("--------------");
/* 379 */           this.m_Writer.println();
/* 380 */           tr_err.showError(cr, 0, bName + ".train", this.m_Writer);
/*     */           
/* 382 */           this.m_Writer.println();
/*     */         } 
/*     */         
/* 385 */         ClusErrorList.printExtraError(cr, 0, this.m_Writer);
/*     */         break;
/*     */       case 2:
/* 388 */         va_err = cr.getValidationError();
/* 389 */         this.m_Writer.println("Validation error");
/* 390 */         this.m_Writer.println("----------------");
/* 391 */         this.m_Writer.println();
/* 392 */         va_err.showError(cr, 2, bName + ".valid", this.m_Writer);
/* 393 */         this.m_Writer.println();
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 398 */     this.m_Writer.println();
/*     */     
/* 400 */     this.m_Writer.close();
/* 401 */     this.m_Writer.println();
/*     */     
/* 403 */     StatisticPrintInfo info = this.m_Sett.getStatisticPrintInfo();
/* 404 */     for (int k = 0; k < cr.getNbModels(); k++) {
/* 405 */       if (cr.getModelInfo(k) != null && models.get(k) != null && this.m_Sett.shouldShowModel(k)) {
/* 406 */         ClusModelInfo mi = cr.getModelInfo(k);
/* 407 */         ClusModel root = models.get(k);
/* 408 */         String modelname = mi.getName() + " Model";
/* 409 */         this.m_Writer.println(modelname);
/* 410 */         this.m_Writer.println(StringUtils.makeString('*', modelname.length()));
/* 411 */         this.m_Writer.println();
/* 412 */         if (this.m_Sett.isPrintModelAndExamples()) {
/* 413 */           RowData pex = (RowData)cr.getTrainingSet();
/* 414 */           System.out.println(te_err);
/* 415 */           if (te_err != null) {
/* 416 */             pex = cr.getTestSet();
/*     */           }
/* 418 */           root.printModelAndExamples(this.m_Writer, info, pex);
/*     */         } else {
/* 420 */           root.printModel(this.m_Writer, info);
/*     */         } 
/* 422 */         this.m_Writer.println();
/* 423 */         if (getSettings().isOutputPythonModel()) {
/* 424 */           if (getSettings().isEnsembleMode() && k == 1) {
/* 425 */             root.printModelToPythonScript(this.m_Writer);
/*     */           } else {
/*     */             
/* 428 */             this.m_Writer.print("def clus_tree( ");
/* 429 */             ClusAttrType[] cat = ClusSchema.vectorToAttrArray(this.m_Schema.collectAttributes(1, -1));
/* 430 */             for (int ii = 0; ii < cat.length - 1; ii++) {
/* 431 */               this.m_Writer.print(cat[ii].getName() + ",");
/*     */             }
/* 433 */             this.m_Writer.println(cat[cat.length - 1].getName() + " ):");
/* 434 */             root.printModelToPythonScript(this.m_Writer);
/* 435 */             this.m_Writer.println();
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 441 */     this.m_Writer.flush();
/*     */   }
/*     */   
/*     */   public String getQuotient(int a, int b) {
/* 445 */     double val = (b == 0) ? 0.0D : (a / b);
/* 446 */     return ClusFormat.ONE_AFTER_DOT.format(val);
/*     */   }
/*     */   
/*     */   public void writeSummary(ClusSummary summary) throws IOException {
/* 450 */     this.m_Writer.println("Summary");
/* 451 */     this.m_Writer.println("*******");
/* 452 */     this.m_Writer.println();
/* 453 */     int runs = summary.getNbRuns();
/* 454 */     this.m_Writer.println("Runs: " + runs);
/* 455 */     double tsec = summary.getInductionTime() / 1000.0D;
/* 456 */     this.m_Writer.println("Induction time: " + ClusFormat.FOUR_AFTER_DOT.format(tsec) + " sec");
/* 457 */     double psec = summary.getPrepareTime() / 1000.0D;
/* 458 */     this.m_Writer.println("Preprocessing time: " + ClusFormat.ONE_AFTER_DOT.format(psec) + " sec");
/* 459 */     this.m_Writer.println("Mean number of tests");
/* 460 */     for (int i = 1; i <= 2; i++) {
/* 461 */       ClusModelInfo mi = summary.getModelInfo(i);
/* 462 */       if (mi != null) {
/* 463 */         this.m_Writer.println("     " + mi.getName() + ": " + getQuotient(mi.getModelSize(), runs));
/*     */       }
/*     */     } 
/* 466 */     this.m_Writer.println();
/* 467 */     String bName = FileUtil.getName(this.m_Fname);
/* 468 */     ClusErrorList tr_err = summary.getTrainError();
/* 469 */     if (this.m_Sett.isOutTrainError() && tr_err != null) {
/* 470 */       this.m_Writer.println("Training error");
/* 471 */       this.m_Writer.println("--------------");
/* 472 */       this.m_Writer.println();
/* 473 */       tr_err.showError(summary, 0, bName + ".train", this.m_Writer);
/*     */       
/* 475 */       this.m_Writer.println();
/*     */     } 
/* 477 */     ClusErrorList va_err = summary.getValidationError();
/* 478 */     if (va_err != null) {
/* 479 */       this.m_Writer.println("Validation error");
/* 480 */       this.m_Writer.println("----------------");
/* 481 */       this.m_Writer.println();
/* 482 */       va_err.showError(summary, 2, bName + ".valid", this.m_Writer);
/*     */       
/* 484 */       this.m_Writer.println();
/*     */     } 
/* 486 */     ClusErrorList te_err = summary.getTestError();
/* 487 */     if (te_err != null) {
/* 488 */       this.m_Writer.println("Testing error");
/* 489 */       this.m_Writer.println("-------------");
/* 490 */       this.m_Writer.println();
/* 491 */       te_err.showError(summary, 1, bName + ".test", this.m_Writer);
/*     */     } 
/*     */     
/* 494 */     this.m_Writer.println();
/* 495 */     this.m_Writer.flush();
/*     */   }
/*     */   
/*     */   public PrintWriter getWriter() {
/* 499 */     return this.m_Writer;
/*     */   }
/*     */   
/*     */   public void close() {
/* 503 */     if (this.m_Fname != null);
/*     */ 
/*     */     
/* 506 */     this.m_Writer.close();
/*     */   }
/*     */   
/*     */   public static void printHeader() {
/* 510 */     System.out.println("Clus v2.12 - Software for Predictive Clustering");
/* 511 */     System.out.println();
/* 512 */     System.out.println("Copyright (C) 2007, 2008, 2009, 2010");
/* 513 */     System.out.println("   Katholieke Universiteit Leuven, Leuven, Belgium");
/* 514 */     System.out.println("   Jozef Stefan Institute, Ljubljana, Slovenia");
/* 515 */     System.out.println();
/* 516 */     System.out.println("This program is free software and comes with ABSOLUTELY NO");
/* 517 */     System.out.println("WARRANTY. You are welcome to redistribute it under certain");
/* 518 */     System.out.println("conditions. Type 'clus -copying' for distribution details.");
/* 519 */     System.out.println();
/*     */   }
/*     */   
/*     */   public static void showHelp() {
/* 523 */     System.out.println("Usage: clus appname");
/* 524 */     System.out.println("Database: appname.arff");
/* 525 */     System.out.println("Settings: appname.s");
/* 526 */     System.out.println("Output:   appname.out");
/* 527 */     System.out.println();
/* 528 */     System.out.println("More information on:");
/* 529 */     System.out.println("http://www.cs.kuleuven.be/~dtai/clus");
/*     */   }
/*     */   
/*     */   public static void printGPL() {
/* 533 */     System.out.println("This function will display the content of 'LICENSE.TXT'.");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\main\ClusOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */