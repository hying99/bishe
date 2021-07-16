/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.ensembles.ClusForest;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.pruning.PruneTree;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
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
/*     */ public class ClusBeamInduce
/*     */   extends ClusInductionAlgorithm
/*     */ {
/*     */   protected NominalSplit m_Split;
/*     */   protected ClusBeamSearch m_Search;
/*     */   
/*     */   public ClusBeamInduce(ClusSchema schema, Settings sett, ClusBeamSearch search) throws ClusException, IOException {
/*  47 */     super(schema, sett);
/*  48 */     this.m_Search = search;
/*     */   }
/*     */   
/*     */   public void initializeHeuristic() {
/*  52 */     this.m_Search.initializeHeuristic();
/*     */   }
/*     */   
/*     */   public boolean isModelWriter() {
/*  56 */     return true;
/*     */   }
/*     */   
/*     */   public void writeModel(ClusModelCollectionIO strm) throws IOException {
/*  60 */     this.m_Search.writeModel(strm);
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/*  64 */     ClusNode root = this.m_Search.beamSearch(cr);
/*  65 */     root.updateTree();
/*  66 */     return (ClusModel)root;
/*     */   }
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/*  70 */     this.m_Search.beamSearch(cr);
/*  71 */     ClusModelInfo def_model = cr.addModelInfo(0);
/*  72 */     def_model.setModel(ClusDecisionTree.induceDefault(cr));
/*  73 */     def_model.setName("Default");
/*  74 */     ArrayList<ClusBeamModel> lst = this.m_Search.getBeam().toArray();
/*  75 */     updateAllPredictions(lst);
/*     */ 
/*     */     
/*  78 */     if (getSettings().getBeamTreeMaxSize() <= -1) postPruneBeamModels(cr, lst); 
/*  79 */     if (getSettings().getBeamSortOnTrainParameter()) sortModels(cr, lst);
/*     */     
/*  81 */     ClusBeamSimilarityOutput bsimout = new ClusBeamSimilarityOutput(getSettings());
/*  82 */     bsimout.appendToFile(lst, cr);
/*  83 */     boolean toForest = cr.getStatManager().getSettings().isBeamToForest();
/*  84 */     ClusForest bForest = new ClusForest(getStatManager());
/*     */     
/*  86 */     for (int i = 0; i < lst.size(); i++) {
/*  87 */       ClusBeamModel mdl = lst.get(lst.size() - i - 1);
/*  88 */       ClusModelInfo model_info = cr.addModelInfo(i + 1);
/*  89 */       ClusNode tree = (ClusNode)mdl.getModel();
/*  90 */       model_info.setModel((ClusModel)tree);
/*  91 */       model_info.setName("Beam " + (i + 1));
/*  92 */       model_info.clearAll();
/*  93 */       if (toForest) bForest.addModelToForest((ClusModel)tree); 
/*     */     } 
/*  95 */     if (toForest) {
/*  96 */       ClusModelInfo forest_info = cr.addModelInfo(lst.size() + 1);
/*  97 */       forest_info.setModel((ClusModel)bForest);
/*  98 */       forest_info.setName("BeamToForest");
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
/*     */   public void postPruneBeamModels(ClusRun cr, ArrayList<ClusBeamModel> arr) throws ClusException {
/* 110 */     updateAllPredictions(arr);
/* 111 */     for (int i = 0; i < arr.size(); i++) {
/* 112 */       PruneTree pruner = getStatManager().getTreePruner(null);
/* 113 */       pruner.setTrainingData((RowData)cr.getTrainingSet());
/* 114 */       ClusNode tree = (ClusNode)((ClusBeamModel)arr.get(i)).getModel();
/* 115 */       pruner.prune(tree);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateAllPredictions(ArrayList<ClusBeamModel> arr) {
/* 120 */     for (int i = 0; i < arr.size(); i++) {
/* 121 */       ClusNode tree = (ClusNode)((ClusBeamModel)arr.get(i)).getModel();
/* 122 */       tree.updateTree();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sortModels(ClusRun cr, ArrayList<ClusBeamModel> arr) throws ClusException, IOException {
/* 140 */     int size = arr.size();
/* 141 */     ClusBeamModel[] models = new ClusBeamModel[size];
/* 142 */     double[] err = new double[size];
/* 143 */     double[] heur = new double[size];
/* 144 */     for (int i = 0; i < size; i++) {
/* 145 */       models[i] = arr.get(i);
/* 146 */       err[i] = Clus.calcModelError(cr.getStatManager(), (RowData)cr.getTrainingSet(), models[i].getModel());
/* 147 */       heur[i] = models[i].getValue();
/*     */     } 
/*     */ 
/*     */     
/* 151 */     for (int j = 0; j < size - 1; j++) {
/* 152 */       for (int k = j + 1; k < size; k++) {
/* 153 */         if (err[j] > err[k]) {
/* 154 */           ClusBeamModel cbm = models[j];
/* 155 */           models[j] = models[k];
/* 156 */           models[k] = cbm;
/* 157 */           double tmp = err[j];
/* 158 */           err[j] = err[k];
/* 159 */           err[k] = tmp;
/* 160 */           tmp = heur[j];
/* 161 */           heur[j] = heur[k];
/* 162 */           heur[k] = tmp;
/*     */         }
/* 164 */         else if (err[j] == err[k] && 
/* 165 */           heur[j] < heur[k]) {
/* 166 */           ClusBeamModel cbm = models[j];
/* 167 */           models[j] = models[k];
/* 168 */           models[k] = cbm;
/* 169 */           double tmp = err[j];
/* 170 */           err[j] = err[k];
/* 171 */           err[k] = tmp;
/* 172 */           tmp = heur[j];
/* 173 */           heur[j] = heur[k];
/* 174 */           heur[k] = tmp;
/*     */         } 
/*     */       } 
/*     */     } 
/* 178 */     arr.clear();
/* 179 */     for (int m = 0; m < size; ) { arr.add(models[m]); m++; }
/*     */   
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */