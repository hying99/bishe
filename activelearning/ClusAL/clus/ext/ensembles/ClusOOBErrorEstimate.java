/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.processor.ModelProcessorCollection;
/*     */ import clus.selection.OOBSelection;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import jeans.util.MyArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusOOBErrorEstimate
/*     */ {
/*     */   static HashMap m_OOBPredictions;
/*     */   static HashMap m_OOBUsage;
/*     */   static boolean m_OOBCalculation;
/*     */   int m_Mode;
/*     */   
/*     */   public ClusOOBErrorEstimate(int mode) {
/*  36 */     m_OOBPredictions = new HashMap<>();
/*  37 */     m_OOBUsage = new HashMap<>();
/*  38 */     m_OOBCalculation = false;
/*  39 */     this.m_Mode = mode;
/*     */   }
/*     */   
/*     */   public static boolean containsPredictionForTuple(DataTuple tuple) {
/*  43 */     return m_OOBPredictions.containsKey(Integer.valueOf(tuple.hashCode()));
/*     */   }
/*     */   
/*     */   public static double[] getPredictionForRegressionHMCTuple(DataTuple tuple) {
/*  47 */     return (double[])m_OOBPredictions.get(Integer.valueOf(tuple.hashCode()));
/*     */   }
/*     */   
/*     */   public static double[][] getPredictionForClassificationTuple(DataTuple tuple) {
/*  51 */     return (double[][])m_OOBPredictions.get(Integer.valueOf(tuple.hashCode()));
/*     */   }
/*     */   
/*     */   public void postProcessForestForOOBEstimate(ClusRun cr, OOBSelection oob_total, RowData all_data, Clus cl, String addname) throws ClusException, IOException {
/*  55 */     Settings sett = cr.getStatManager().getSettings();
/*  56 */     ClusSchema schema = all_data.getSchema();
/*  57 */     ClusOutput output = new ClusOutput(sett.getAppName() + addname + ".oob", schema, sett);
/*  58 */     setOOBCalculation(true);
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
/*  70 */     calcOOBError(oob_total, all_data, 0, cr);
/*  71 */     cl.calcExtraTrainingSetErrors(cr);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     output.writeHeader();
/*  78 */     output.writeOutput(cr, true, cl.getSettings().isOutTrainError());
/*  79 */     output.close();
/*     */ 
/*     */     
/*  82 */     setOOBCalculation(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateOOBTuples(OOBSelection oob_sel, RowData train_data, ClusModel model) throws IOException, ClusException {
/*  87 */     for (int i = 0; i < train_data.getNbRows(); i++) {
/*  88 */       if (oob_sel.isSelected(i)) {
/*  89 */         DataTuple tuple = train_data.getTuple(i);
/*  90 */         if (existsOOBtuple(tuple)) {
/*  91 */           updateOOBTuple(tuple, model);
/*     */         } else {
/*  93 */           addOOBTuple(tuple, model);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean existsOOBtuple(DataTuple tuple) {
/* 100 */     if (m_OOBUsage.containsKey(Integer.valueOf(tuple.hashCode())) && m_OOBPredictions.containsKey(Integer.valueOf(tuple.hashCode()))) {
/* 101 */       return true;
/*     */     }
/* 103 */     if (!m_OOBUsage.containsKey(Integer.valueOf(tuple.hashCode())) && m_OOBPredictions.containsKey(Integer.valueOf(tuple.hashCode()))) {
/* 104 */       System.err.println(getClass().getName() + ":existsOOBtuple(DataTuple) OOB tuples mismatch-> Usage = False, Predictions = True");
/*     */     }
/* 106 */     if (m_OOBUsage.containsKey(Integer.valueOf(tuple.hashCode())) && !m_OOBPredictions.containsKey(Integer.valueOf(tuple.hashCode()))) {
/* 107 */       System.err.println(getClass().getName() + ":existsOOBtuple(DataTuple) OOB tuples mismatch-> Usage = True, Predictions = False");
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */   
/*     */   public void addOOBTuple(DataTuple tuple, ClusModel model) {
/* 113 */     m_OOBUsage.put(Integer.valueOf(tuple.hashCode()), Integer.valueOf(1));
/*     */     
/* 115 */     if (this.m_Mode == 2) {
/*     */       
/* 117 */       WHTDStatistic stat = (WHTDStatistic)model.predictWeighted(tuple);
/* 118 */       m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), stat.getNumericPred());
/*     */     } 
/*     */     
/* 121 */     if (this.m_Mode == 1) {
/*     */       
/* 123 */       RegressionStat stat = (RegressionStat)model.predictWeighted(tuple);
/* 124 */       m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), stat.getNumericPred());
/*     */     } 
/*     */     
/* 127 */     if (this.m_Mode == 0) {
/*     */       
/* 129 */       ClassificationStat stat = (ClassificationStat)model.predictWeighted(tuple);
/* 130 */       switch (Settings.m_ClassificationVoteType.getValue()) {
/*     */         case 0:
/* 132 */           m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), ClusEnsembleInduceOptimization.transformToMajority(stat.m_ClassCounts));
/*     */           return;
/*     */         case 1:
/* 135 */           m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), ClusEnsembleInduceOptimization.transformToProbabilityDistribution(stat.m_ClassCounts));
/*     */           return;
/*     */       } 
/* 138 */       m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), ClusEnsembleInduceOptimization.transformToMajority(stat.m_ClassCounts));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateOOBTuple(DataTuple tuple, ClusModel model) {
/* 144 */     Integer used = (Integer)m_OOBUsage.get(Integer.valueOf(tuple.hashCode()));
/* 145 */     used = Integer.valueOf(used.intValue() + 1);
/* 146 */     m_OOBUsage.put(Integer.valueOf(tuple.hashCode()), used);
/*     */     
/* 148 */     if (this.m_Mode == 2) {
/*     */       
/* 150 */       WHTDStatistic stat = (WHTDStatistic)model.predictWeighted(tuple);
/* 151 */       double[] predictions = stat.getNumericPred();
/* 152 */       double[] avg_predictions = (double[])m_OOBPredictions.get(Integer.valueOf(tuple.hashCode()));
/* 153 */       avg_predictions = ClusEnsembleInduceOptimization.incrementPredictions(avg_predictions, predictions, used.doubleValue());
/* 154 */       m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), avg_predictions);
/*     */     } 
/*     */     
/* 157 */     if (this.m_Mode == 1) {
/*     */       
/* 159 */       RegressionStat stat = (RegressionStat)model.predictWeighted(tuple);
/* 160 */       double[] predictions = stat.getNumericPred();
/* 161 */       double[] avg_predictions = (double[])m_OOBPredictions.get(Integer.valueOf(tuple.hashCode()));
/* 162 */       avg_predictions = ClusEnsembleInduceOptimization.incrementPredictions(avg_predictions, predictions, used.doubleValue());
/* 163 */       m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), avg_predictions);
/*     */     } 
/*     */     
/* 166 */     if (this.m_Mode == 0) {
/*     */       
/* 168 */       ClassificationStat stat = (ClassificationStat)model.predictWeighted(tuple);
/* 169 */       double[][] predictions = (double[][])stat.m_ClassCounts.clone();
/* 170 */       switch (Settings.m_ClassificationVoteType.getValue()) {
/*     */         case 0:
/* 172 */           predictions = ClusEnsembleInduceOptimization.transformToMajority(predictions);
/*     */           break;
/*     */         case 1:
/* 175 */           predictions = ClusEnsembleInduceOptimization.transformToProbabilityDistribution(predictions);
/*     */           break;
/*     */         default:
/* 178 */           predictions = ClusEnsembleInduceOptimization.transformToMajority(predictions); break;
/*     */       } 
/* 180 */       double[][] sum_predictions = (double[][])m_OOBPredictions.get(Integer.valueOf(tuple.hashCode()));
/* 181 */       sum_predictions = ClusEnsembleInduceOptimization.incrementPredictions(sum_predictions, predictions);
/* 182 */       m_OOBPredictions.put(Integer.valueOf(tuple.hashCode()), sum_predictions);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void calcOOBError(OOBSelection oob_tot, RowData all_data, int type, ClusRun cr) throws IOException, ClusException {
/* 187 */     ClusSchema mschema = all_data.getSchema();
/*     */     
/* 189 */     cr.initModelProcessors(type, mschema);
/* 190 */     ModelProcessorCollection allcoll = cr.getAllModelsMI().getAddModelProcessors(type);
/*     */     
/* 192 */     for (int t = 0; t < all_data.getNbRows(); t++) {
/*     */       
/* 194 */       if (oob_tot.isSelected(t)) {
/* 195 */         DataTuple tuple = all_data.getTuple(t);
/* 196 */         allcoll.exampleUpdate(tuple);
/* 197 */         for (int i = 0; i < cr.getNbModels(); i++) {
/* 198 */           ClusModelInfo mi = cr.getModelInfo(i);
/* 199 */           ClusModel model = mi.getModel();
/* 200 */           if (model != null) {
/* 201 */             ClusStatistic pred = model.predictWeighted(tuple);
/* 202 */             ClusErrorList err = mi.getError(type);
/* 203 */             if (err != null) {
/* 204 */               err.addExample(tuple, pred);
/*     */             }
/* 206 */             ModelProcessorCollection coll = mi.getModelProcessors(type);
/* 207 */             if (coll != null) {
/* 208 */               if (coll.needsModelUpdate()) {
/* 209 */                 model.applyModelProcessors(tuple, (MyArray)coll);
/* 210 */                 coll.modelDone();
/*     */               } 
/* 212 */               coll.exampleUpdate(tuple, pred);
/*     */             } 
/*     */           } 
/*     */         } 
/* 216 */         allcoll.exampleDone();
/*     */       } 
/*     */     } 
/* 219 */     cr.termModelProcessors(type);
/*     */   }
/*     */   
/*     */   public static boolean isOOBCalculation() {
/* 223 */     return m_OOBCalculation;
/*     */   }
/*     */   
/*     */   public void setOOBCalculation(boolean value) {
/* 227 */     m_OOBCalculation = value;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ensembles\ClusOOBErrorEstimate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */