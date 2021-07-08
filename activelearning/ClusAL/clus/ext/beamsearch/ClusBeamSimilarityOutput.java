/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.text.DateFormat;
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
/*     */ public class ClusBeamSimilarityOutput
/*     */ {
/*     */   static boolean m_WriteHeader;
/*     */   static ArrayList m_BeamSimTrain;
/*     */   static ArrayList m_BeamSimTest;
/*     */   
/*     */   public ClusBeamSimilarityOutput(Settings sett) throws IOException {
/*  45 */     if (!m_WriteHeader) {
/*  46 */       writeHeader(sett);
/*  47 */       m_WriteHeader = true;
/*  48 */       m_BeamSimTest = new ArrayList();
/*  49 */       m_BeamSimTrain = new ArrayList();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isHeaderWritten() {
/*  54 */     return m_WriteHeader;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void appendToFile(ArrayList models, ClusRun run) throws IOException, ClusException {
/*  60 */     FileWriter wrtr = new FileWriter(new File(run.getStatManager().getSettings().getAppName() + ".bsim"), true);
/*  61 */     double[] sim = new double[2];
/*     */ 
/*     */     
/*  64 */     NumberFormat outF = ClusFormat.FOUR_AFTER_DOT;
/*  65 */     run.getStatManager(); if (ClusStatManager.getMode() != 1) { run.getStatManager(); if (ClusStatManager.getMode() != 0) {
/*  66 */         System.err.println(getClass().getName() + ".appendToFile(): Unhandled Type of Target Attribute");
/*  67 */         throw new ClusException("Unhandled Type of Target Attribute");
/*     */       }  }
/*  69 */      run.getStatManager(); boolean isNum = (ClusStatManager.getMode() == 1);
/*  70 */     sim[0] = ClusBeamModelDistance.calcBeamSimilarity(models, (RowData)run.getTrainingSet(), isNum);
/*  71 */     m_BeamSimTrain.add(Double.valueOf(sim[0]));
/*     */     try {
/*  73 */       sim[1] = ClusBeamModelDistance.calcBeamSimilarity(models, run.getTestSet(), isNum);
/*  74 */       m_BeamSimTest.add(Double.valueOf(sim[1]));
/*  75 */       if (Settings.IS_XVAL)
/*  76 */       { wrtr.write("Fold " + run.getIndexString() + ":\t" + outF.format(sim[0]) + "\t\t" + outF.format(sim[1]) + "\n");
/*  77 */         if (run.getIndex() == run.getStatManager().getSettings().getXValFolds()) {
/*     */           
/*  79 */           wrtr.write("---------------------------------------\n");
/*  80 */           wrtr.write("Summary:\t" + outF.format(getAverage(m_BeamSimTrain)) + "\t\t" + outF.format(getAverage(m_BeamSimTest)) + "\n");
/*     */         }  }
/*  82 */       else { wrtr.append("\t\t" + outF.format(sim[0]) + "\t\t" + outF.format(sim[1]) + "\n"); } 
/*  83 */     } catch (NullPointerException e) {
/*  84 */       if (!Settings.IS_XVAL) wrtr.append("Summary:\t" + outF.format(sim[0]) + "\t\tN/A\n"); 
/*     */     } 
/*  86 */     wrtr.flush();
/*     */   }
/*     */   
/*     */   public void writeHeader(Settings sett) throws IOException {
/*  90 */     File output = new File(sett.getAppName() + ".bsim");
/*  91 */     FileWriter wrtr = new FileWriter(output);
/*  92 */     wrtr.write("Clus Beam-Search run\n");
/*  93 */     wrtr.write("----------------------\n");
/*  94 */     wrtr.write("Date:\t" + DateFormat.getInstance().format(sett.getDate()) + "\n");
/*  95 */     wrtr.write("File:\t" + output + "\n");
/*  96 */     wrtr.write("\n");
/*  97 */     wrtr.write("Beam Similarity Output\n");
/*  98 */     wrtr.write("----------------------\n");
/*  99 */     wrtr.write("\t\tTraining\tTesting\n");
/* 100 */     wrtr.write("---------------------------------------\n");
/* 101 */     wrtr.flush();
/*     */   }
/*     */   
/*     */   public static double getAverage(ArrayList<Double> arr) {
/* 105 */     double result = 0.0D;
/* 106 */     for (int i = 0; i < arr.size(); i++)
/* 107 */       result += ((Double)arr.get(i)).doubleValue(); 
/* 108 */     return result / arr.size();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamSimilarityOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */