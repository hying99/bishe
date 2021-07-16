/*     */ package clus.algo.kNN;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.util.cmdline.CMDLineArgs;
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
/*     */ public class KNNClassifier
/*     */   extends ClusInductionAlgorithmType
/*     */ {
/*     */   public KNNClassifier(Clus clus) {
/*  43 */     super(clus);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printInfo() {
/*  50 */     System.out.println("Nearest Neighbour Classifier");
/*  51 */     System.out.println("# Neighbours : " + Settings.kNN_k.getValue());
/*  52 */     String s = Settings.kNN_distWeighted.getValue() ? "yes" : "no";
/*  53 */     System.out.println("Distance Weighted ? " + s);
/*  54 */     System.out.println("Vector Distance Measure used: " + Settings.kNN_vectDist.getValue());
/*  55 */     s = Settings.kNN_normalized.getValue() ? "yes" : "no";
/*  56 */     System.out.println("Attribute Normalizing used: " + s);
/*  57 */     s = Settings.kNN_attrWeighted.getValue() ? "yes" : "no";
/*  58 */     System.out.println("Separate Attribute Weights used: " + s);
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) {
/*  62 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusModel induceSingleActiveLearning(RowData trainData, ClusStatManager statManager) {
/*     */     double[] weights;
/*     */     VectorDistance vd;
/*  69 */     KNNStatistics stats = new KNNStatistics(trainData);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     if (Settings.kNN_attrWeighted.getValue() && Settings.kNN_normalized.getValue()) {
/*  75 */       weights = calcWeights(stats, trainData);
/*     */     } else {
/*  77 */       weights = noWeights(trainData);
/*     */     } 
/*     */     
/*  80 */     ClusAttrType[] attrs = trainData.getSchema().getDescriptiveAttributes();
/*     */     
/*  82 */     String d = Settings.kNN_vectDist.getValue();
/*  83 */     if (d.equals("Manhattan")) {
/*  84 */       vd = new ManhattanDistance(attrs, weights);
/*     */     } else {
/*  86 */       vd = new EuclidianDistance(attrs, weights);
/*     */     } 
/*     */     
/*  89 */     KNNModel model = new KNNModel(trainData, statManager, vd);
/*  90 */     return model;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModel induceSingle(ClusRun cr) {
/*     */     double[] weights;
/*     */     VectorDistance vd;
/* 101 */     RowData trainData = (RowData)cr.getTrainingSet();
/* 102 */     KNNStatistics stats = new KNNStatistics(trainData);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     if (Settings.kNN_attrWeighted.getValue() && Settings.kNN_normalized.getValue()) {
/* 108 */       weights = calcWeights(stats, trainData);
/*     */     } else {
/* 110 */       weights = noWeights(trainData);
/*     */     } 
/*     */     
/* 113 */     ClusAttrType[] attrs = trainData.getSchema().getDescriptiveAttributes();
/*     */     
/* 115 */     String d = Settings.kNN_vectDist.getValue();
/* 116 */     if (d.equals("Manhattan")) {
/* 117 */       vd = new ManhattanDistance(attrs, weights);
/*     */     } else {
/* 119 */       vd = new EuclidianDistance(attrs, weights);
/*     */     } 
/*     */     
/* 122 */     KNNModel model = new KNNModel(cr, vd);
/* 123 */     return model;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induce(ClusRun cr) {
/*     */     double[] weights;
/*     */     VectorDistance vd;
/* 132 */     System.out.println("Calculating Statistical Measures...");
/*     */     
/* 134 */     RowData trainData = (RowData)cr.getTrainingSet();
/* 135 */     KNNStatistics stats = new KNNStatistics(trainData);
/* 136 */     System.out.println("Done.");
/*     */ 
/*     */ 
/*     */     
/* 140 */     if (Settings.kNN_attrWeighted.getValue() && Settings.kNN_normalized.getValue()) {
/* 141 */       weights = calcWeights(stats, trainData);
/*     */     } else {
/* 143 */       weights = noWeights(trainData);
/*     */     } 
/*     */ 
/*     */     
/* 147 */     ClusAttrType[] attrs = trainData.getSchema().getDescriptiveAttributes();
/*     */     
/* 149 */     String d = Settings.kNN_vectDist.getValue();
/* 150 */     if (d.equals("Manhattan")) {
/* 151 */       vd = new ManhattanDistance(attrs, weights);
/*     */     } else {
/* 153 */       vd = new EuclidianDistance(attrs, weights);
/*     */     } 
/*     */     
/* 156 */     KNNModel model = new KNNModel(cr, vd);
/*     */     
/* 158 */     cr.getModelInfo(1).setModel(model);
/* 159 */     ClusModel defmodel = ClusDecisionTree.induceDefault(cr);
/* 160 */     cr.getModelInfo(0).setModel(defmodel);
/*     */   }
/*     */   
/*     */   public void initializeSummary(ClusSummary summ) {
/* 164 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 165 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/* 166 */     ClusSchema schema = this.m_Clus.getSchema();
/* 167 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 168 */     for (int i = 0; i < attrs.length; i++) {
/* 169 */       if (attrs[i].getTypeIndex() == 0) {
/* 170 */         attrs[i].setBasicDistance(nomDist);
/*     */       } else {
/* 172 */         attrs[i].setBasicDistance(numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private double[] calcWeights(KNNStatistics stats, RowData data) {
/* 180 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/* 181 */     double[] weights = new double[attrs.length];
/* 182 */     int nbr = data.getNbRows();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 187 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 188 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/*     */     
/* 190 */     for (int i = 0; i < nbr; i++) {
/* 191 */       DataTuple curTup = data.getTuple(i);
/* 192 */       int curTargetVal = curTup.getClassification();
/* 193 */       DataTuple curProto = stats.getPrototype(curTargetVal);
/* 194 */       for (int k = 0; k < attrs.length; k++) {
/*     */         
/* 196 */         if (attrs[k].getTypeIndex() == 0) {
/* 197 */           double w_upd = nomDist.getDistance(attrs[k], curTup, curProto);
/* 198 */           weights[k] = weights[k] + w_upd;
/* 199 */         } else if (attrs[k].getTypeIndex() == 1) {
/* 200 */           double w_upd = numDist.getDistance(attrs[k], curTup, curProto);
/* 201 */           weights[k] = weights[k] + w_upd;
/*     */         } 
/*     */       } 
/*     */     } 
/* 205 */     System.out.println("Weights : ");
/* 206 */     for (int j = 0; j < attrs.length; j++) {
/* 207 */       weights[j] = 1.0D - weights[j] / nbr;
/* 208 */       System.out.print(weights[j] + ",");
/*     */     } 
/* 210 */     System.out.println();
/* 211 */     return weights;
/*     */   }
/*     */ 
/*     */   
/*     */   private double[] noWeights(RowData data) {
/* 216 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/* 217 */     double[] weights = new double[attrs.length];
/* 218 */     for (int j = 0; j < weights.length; j++) {
/* 219 */       weights[j] = 1.0D;
/*     */     }
/* 221 */     return weights;
/*     */   }
/*     */ 
/*     */   
/*     */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*     */   
/*     */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 228 */     return model;
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/* 232 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\kNN\KNNClassifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */