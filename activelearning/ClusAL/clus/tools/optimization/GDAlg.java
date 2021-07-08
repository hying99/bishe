/*     */ package clus.tools.optimization;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
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
/*     */ public class GDAlg
/*     */   extends OptAlg
/*     */ {
/*     */   private GDProbl m_GDProbl;
/*     */   protected ArrayList<Double> m_weights;
/*     */   protected int m_earlyStopStep;
/*     */   protected int m_earlyStopStepsizeReducedNb;
/*     */   private ArrayList<Integer> m_iOscillatingWeights;
/*     */   private double m_minStepSizeReduction;
/*     */   private double[] m_prevChange;
/*     */   private double[] m_newChange;
/*     */   private int[] m_iPrevDimension;
/*     */   private int[] m_iNewDimension;
/*     */   
/*     */   public GDAlg(ClusStatManager stat_mgr, OptProbl.OptParam dataInformation, ClusRuleSet rset) {
/*  68 */     super(stat_mgr);
/*  69 */     this.m_GDProbl = new GDProbl(stat_mgr, dataInformation);
/*  70 */     initGDForNewRunWithSamePredictions();
/*  71 */     this.m_earlyStopStep = 100;
/*     */ 
/*     */     
/*  74 */     if (GDProbl.m_printGDDebugInformation) {
/*  75 */       String fname = getSettings().getDataFile();
/*     */       
/*  77 */       PrintWriter wrt_pred = null;
/*  78 */       PrintWriter wrt_true = null;
/*     */       try {
/*  80 */         wrt_pred = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".gd-pred")));
/*  81 */         wrt_true = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".gd-true")));
/*  82 */       } catch (FileNotFoundException e) {
/*  83 */         e.printStackTrace();
/*  84 */         System.exit(1);
/*     */       } 
/*     */       
/*  87 */       this.m_GDProbl.printPredictionsToFile(wrt_pred);
/*  88 */       wrt_pred.close();
/*  89 */       this.m_GDProbl.printTrueValuesToFile(wrt_true);
/*  90 */       wrt_true.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initGDForNewRunWithSamePredictions() {
/* 100 */     this.m_GDProbl.initGDForNewRunWithSamePredictions();
/*     */     
/* 102 */     this.m_weights = this.m_GDProbl.getInitialWeightVector();
/*     */ 
/*     */     
/* 105 */     this.m_prevChange = null;
/* 106 */     this.m_iPrevDimension = null;
/* 107 */     this.m_iNewDimension = null;
/* 108 */     this.m_newChange = null;
/* 109 */     this.m_minStepSizeReduction = 1.0D;
/*     */     
/* 111 */     this.m_earlyStopStepsizeReducedNb = 0;
/*     */ 
/*     */     
/* 114 */     if (this.m_GDProbl.m_bannedWeights != null) {
/* 115 */       this.m_iOscillatingWeights = new ArrayList<>();
/*     */     } else {
/* 117 */       this.m_iOscillatingWeights = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<Double> optimize() {
/* 129 */     if (Settings.VERBOSE > 0) System.out.print("\nGradient descent: Optimizing rule weights (" + getSettings().getOptGDMaxIter() + ") ");
/*     */     
/* 131 */     PrintWriter wrt_log = null;
/*     */     
/* 133 */     if (GDProbl.m_printGDDebugInformation) {
/*     */       try {
/* 135 */         wrt_log = new PrintWriter(new OutputStreamWriter(new FileOutputStream("gradDesc.log")));
/*     */       }
/* 137 */       catch (Exception e) {
/* 138 */         e.printStackTrace();
/* 139 */         System.err.println("Log file could not be opened. Logging omitted.");
/*     */       } 
/*     */     }
/*     */     
/* 143 */     if (this.m_GDProbl.isClassifTask()) {
/*     */       try {
/* 145 */         throw new Exception("Classification not yeat implemented for gradient descent. Skipping the optimization.");
/* 146 */       } catch (Exception s) {
/* 147 */         s.printStackTrace();
/*     */         
/* 149 */         return null;
/*     */       } 
/*     */     }
/* 152 */     this.m_GDProbl.fullGradientComputation(this.m_weights);
/*     */     
/* 154 */     int nbOfIterations = 0;
/* 155 */     while (nbOfIterations < getSettings().getOptGDMaxIter()) {
/*     */       
/* 157 */       if (Settings.VERBOSE > 0 && nbOfIterations % Math.ceil(getSettings().getOptGDMaxIter() / 50.0D) == 0.0D)
/* 158 */         System.out.print("."); 
/* 159 */       if (nbOfIterations % this.m_earlyStopStep == 0)
/*     */       {
/* 161 */         if (getSettings().getOptGDEarlyStopAmount() > 0.0D && this.m_GDProbl
/* 162 */           .isEarlyStop(this.m_weights)) {
/*     */           
/* 164 */           if (GDProbl.m_printGDDebugInformation) {
/* 165 */             wrt_log.println("Increase in test fitness. Reducing step size or stopping.");
/*     */           }
/* 167 */           if (Settings.VERBOSE > 0) System.out.print("\n\tOverfitting after " + nbOfIterations + " iterations.");
/*     */           
/* 169 */           if (!getSettings().isOptGDIsDynStepsize() && this.m_earlyStopStepsizeReducedNb < 
/* 170 */             getSettings().getOptGDNbOfStepSizeReduce()) {
/* 171 */             this.m_earlyStopStepsizeReducedNb++;
/* 172 */             this.m_GDProbl.dropStepSize(0.1D);
/* 173 */             this.m_GDProbl.restoreBestWeight(this.m_weights);
/* 174 */             this.m_GDProbl.fullGradientComputation(this.m_weights);
/* 175 */             if (Settings.VERBOSE > 0) System.out.print(" Reducing step, continuing.\n"); 
/*     */           } else {
/* 177 */             if (Settings.VERBOSE > 0) System.out.print(" Stopping.\n"); 
/* 178 */             if (GDProbl.m_printGDDebugInformation) {
/* 179 */               wrt_log.println("Early stopping detected after " + nbOfIterations + " iterations.");
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/* 186 */       OutputLog(nbOfIterations, wrt_log);
/*     */ 
/*     */ 
/*     */       
/* 190 */       int[] maxGradients = this.m_GDProbl.getMaxGradients(nbOfIterations);
/*     */       
/* 192 */       boolean oscillation = false;
/*     */ 
/*     */       
/* 195 */       storeGradientsForOscillation(maxGradients);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 202 */       double[] valueChange = new double[maxGradients.length];
/*     */ 
/*     */       
/* 205 */       if (Settings.VERBOSE > 0); boolean debugPrint = false;
/*     */       
/* 207 */       if (debugPrint) System.out.print("\nDEBUG: Computing covariances, total " + maxGradients.length + "\n"); 
/*     */       int iiGradient;
/* 209 */       for (iiGradient = 0; iiGradient < maxGradients.length; iiGradient++) {
/* 210 */         if (debugPrint) System.out.print(iiGradient % 10);
/*     */         
/* 212 */         this.m_GDProbl.computeCovariancesIfNeeded(maxGradients[iiGradient]);
/*     */ 
/*     */         
/* 215 */         valueChange[iiGradient] = this.m_GDProbl.howMuchWeightChanges(maxGradients[iiGradient]);
/*     */ 
/*     */         
/* 218 */         if (nbOfIterations < 100)
/* 219 */           oscillation = (detectOscillation(iiGradient, valueChange[iiGradient]) || oscillation); 
/*     */       } 
/* 221 */       if (debugPrint) System.out.print("\nDEBUG: Computing covariances ended\n");
/*     */ 
/*     */ 
/*     */       
/* 225 */       if (oscillation && !getSettings().isOptGDIsDynStepsize()) {
/*     */         
/* 227 */         if (GDProbl.m_printGDDebugInformation) {
/* 228 */           wrt_log.println("Detected oscillation, reducing step size of: " + this.m_GDProbl.m_stepSize);
/*     */         }
/* 230 */         if (debugPrint) {
/* 231 */           System.out.println("DEBUG: Detected oscillation on iteration " + nbOfIterations + ", reducing step size of: " + this.m_GDProbl.m_stepSize);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 236 */         if (nbOfIterations > 10 && (
/* 237 */           getSettings().getOptGDMTGradientCombine() == 2 || 
/* 238 */           getSettings().getOptGDMTGradientCombine() == 3)) {
/*     */           
/* 240 */           putOscillatingWeightsToBan(nbOfIterations);
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 246 */         reversePreviousStep();
/* 247 */         reduceStepSizeDueOscillation();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 252 */       if (nbOfIterations < 100) {
/* 253 */         storeTheOscillationData();
/*     */       }
/*     */ 
/*     */       
/* 257 */       for (iiGradient = 0; iiGradient < maxGradients.length; iiGradient++) {
/* 258 */         this.m_weights.set(maxGradients[iiGradient], Double.valueOf(((Double)this.m_weights.get(maxGradients[iiGradient])).doubleValue() + valueChange[iiGradient]));
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 266 */       this.m_GDProbl.modifyGradients(maxGradients, this.m_weights);
/*     */       
/* 268 */       nbOfIterations++;
/*     */     } 
/*     */     
/* 271 */     if (Settings.VERBOSE > 0) System.out.println(" done!");
/*     */     
/* 273 */     if (getSettings().getOptGDEarlyStopAmount() > 0.0D) {
/* 274 */       this.m_GDProbl.isEarlyStop(this.m_weights);
/* 275 */       this.m_GDProbl.restoreBestWeight(this.m_weights);
/*     */     } 
/*     */     
/* 278 */     if (GDProbl.m_printGDDebugInformation)
/* 279 */       wrt_log.println("The result of optimization"); 
/* 280 */     OutputLog(nbOfIterations, wrt_log);
/* 281 */     if (GDProbl.m_printGDDebugInformation) {
/* 282 */       wrt_log.close();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 287 */     return this.m_weights;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void putOscillatingWeightsToBan(int iterationNb) {
/* 296 */     for (int iWeight = 0; iWeight < this.m_iOscillatingWeights.size(); iWeight++) {
/* 297 */       this.m_GDProbl.m_bannedWeights[((Integer)this.m_iOscillatingWeights.get(iWeight)).intValue()] = iterationNb + 50;
/*     */     }
/* 299 */     this.m_iOscillatingWeights.clear();
/*     */   }
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
/*     */   private void reduceStepSizeDueOscillation() {
/* 312 */     this.m_GDProbl.dropStepSize(this.m_minStepSizeReduction * 0.99D);
/* 313 */     this.m_minStepSizeReduction = 1.0D;
/*     */   }
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
/*     */   private void storeTheOscillationData() {
/* 332 */     if (getSettings().isOptGDIsDynStepsize())
/* 333 */       return;  this.m_prevChange = (double[])this.m_newChange.clone();
/* 334 */     this.m_iPrevDimension = (int[])this.m_iNewDimension.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void reversePreviousStep() {
/* 343 */     for (int iiGradient = 0; iiGradient < this.m_iPrevDimension.length; iiGradient++) {
/* 344 */       this.m_weights.set(this.m_iPrevDimension[iiGradient], Double.valueOf(((Double)this.m_weights.get(this.m_iPrevDimension[iiGradient])).doubleValue() - this.m_prevChange[iiGradient]));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 350 */     this.m_GDProbl.fullGradientComputation(this.m_weights);
/*     */ 
/*     */     
/* 353 */     this.m_prevChange = null;
/* 354 */     this.m_iPrevDimension = null;
/*     */ 
/*     */     
/* 357 */     if (this.m_iOscillatingWeights != null) {
/* 358 */       this.m_iOscillatingWeights.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void storeGradientsForOscillation(int[] maxGradients) {
/* 365 */     if (getSettings().isOptGDIsDynStepsize())
/* 366 */       return;  this.m_iNewDimension = (int[])maxGradients.clone();
/* 367 */     this.m_newChange = new double[maxGradients.length];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean detectOscillation(int iiNewChange, double valueChange) {
/* 376 */     if (getSettings().isOptGDIsDynStepsize()) return false; 
/* 377 */     boolean detectOscillation = false;
/*     */ 
/*     */     
/* 380 */     this.m_newChange[iiNewChange] = valueChange;
/*     */ 
/*     */     
/* 383 */     for (int iiPrevChange = 0; this.m_prevChange != null && iiPrevChange < this.m_prevChange.length; iiPrevChange++) {
/*     */ 
/*     */       
/* 386 */       if (this.m_iPrevDimension[iiPrevChange] == this.m_iNewDimension[iiNewChange]) {
/*     */ 
/*     */ 
/*     */         
/* 390 */         if (valueChange * this.m_prevChange[iiPrevChange] < 0.0D && 
/* 391 */           Math.abs(valueChange) > Math.abs(this.m_prevChange[iiPrevChange])) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 396 */           if (this.m_iOscillatingWeights != null) {
/* 397 */             this.m_iOscillatingWeights.add(Integer.valueOf(this.m_iPrevDimension[iiPrevChange]));
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 406 */           double needReduction = Math.abs(this.m_prevChange[iiPrevChange]) / Math.abs(valueChange);
/*     */ 
/*     */           
/* 409 */           if (needReduction < this.m_minStepSizeReduction) {
/* 410 */             this.m_minStepSizeReduction = needReduction;
/*     */           }
/* 412 */           detectOscillation = true;
/*     */         } 
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*     */     
/* 420 */     return detectOscillation;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void OutputLog(int iterNro, PrintWriter wrt) {
/* 426 */     if (!GDProbl.m_printGDDebugInformation) {
/*     */       return;
/*     */     }
/* 429 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 430 */     double trainingFitness = this.m_GDProbl.calcFitness(this.m_weights);
/* 431 */     double testFitness = 0.0D;
/* 432 */     if (getSettings().getOptGDEarlyStopAmount() > 0.0D) {
/* 433 */       testFitness = this.m_GDProbl.m_earlyStopProbl.calcFitness(this.m_weights);
/*     */     }
/*     */     
/* 436 */     wrt.print("Iteration " + iterNro + " ");
/* 437 */     if (getSettings().isOptGDIsDynStepsize())
/* 438 */       wrt.print("Step size: " + this.m_GDProbl.m_stepSize + " "); 
/* 439 */     wrt.print("(" + fr.format(trainingFitness) + ", " + fr.format(testFitness) + "): ");
/*     */ 
/*     */     
/* 442 */     for (int i = 0; i < this.m_weights.size(); i++) {
/* 443 */       wrt.print(fr.format(((Double)this.m_weights.get(i)).doubleValue()) + "\t");
/*     */     }
/* 445 */     wrt.print("\n");
/*     */   }
/*     */ 
/*     */   
/*     */   public double getBestFitness() {
/* 450 */     return this.m_GDProbl.getBestFitness();
/*     */   }
/*     */   
/*     */   public void postProcess(ClusRuleSet rset) {
/* 454 */     this.m_GDProbl.changeRuleSetToUndoNormNormalization(rset);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\GDAlg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */