/*     */ package clus.algo.kNN;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.MemoryTupleIterator;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.pruning.BottomUpPruningVSB;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.util.MyArray;
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
/*     */ 
/*     */ 
/*     */ public class KNNTreeClassifier
/*     */   extends ClusInductionAlgorithmType
/*     */ {
/*     */   public KNNTreeClassifier(Clus clus) {
/*  48 */     super(clus);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printInfo() {
/*  55 */     System.out.println("Nearest Neighbour Decision Tree Classifier");
/*  56 */     System.out.println("# Neighbours : " + Settings.kNNT_k.getValue());
/*  57 */     String s = Settings.kNNT_distWeighted.getValue() ? "yes" : "no";
/*  58 */     System.out.println("Distance Weighted ? " + s);
/*  59 */     System.out.println("Vector Distance Measure used: " + Settings.kNNT_vectDist.getValue());
/*  60 */     s = Settings.kNN_normalized.getValue() ? "yes" : "no";
/*  61 */     System.out.println("Attribute Normalizing used: " + s);
/*  62 */     s = Settings.kNN_attrWeighted.getValue() ? "yes" : "no";
/*  63 */     System.out.println("Separate Attribute Weights used: " + s);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModel induceSingle(ClusRun cr) throws ClusException, IOException {
/*     */     double[] weights;
/*     */     VectorDistance vd;
/*  71 */     ClusNode orig = (ClusNode)getInduce().induceSingleUnpruned(cr);
/*     */     
/*  73 */     System.out.println("Calculating Statistical Measures...");
/*     */     
/*  75 */     RowData trainData = (RowData)cr.getTrainingSet();
/*  76 */     KNNStatistics stats = new KNNStatistics(trainData);
/*  77 */     System.out.println("Done.");
/*     */ 
/*     */ 
/*     */     
/*  81 */     if (Settings.kNN_attrWeighted.getValue() && Settings.kNN_normalized.getValue()) {
/*  82 */       weights = calcWeights(stats, trainData);
/*     */     } else {
/*     */       
/*  85 */       weights = noWeights(trainData);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  90 */     ClusAttrType[] attrs = trainData.getSchema().getDescriptiveAttributes();
/*     */     
/*  92 */     String d = Settings.kNN_vectDist.getValue();
/*  93 */     if (d.equals("Manhattan")) {
/*  94 */       vd = new ManhattanDistance(attrs, weights);
/*     */     } else {
/*     */       
/*  97 */       vd = new EuclidianDistance(attrs, weights);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 102 */     KNNTree tree = KNNTree.makeTree(cr, orig, vd);
/* 103 */     storeDataInTree((RowData)cr.getTrainingSet(), tree);
/*     */     
/* 105 */     return (ClusModel)tree;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induce(ClusRun cr) throws ClusException, IOException {
/*     */     double[] weights;
/*     */     VectorDistance vd;
/* 115 */     ClusNode orig = (ClusNode)getInduce().induceSingleUnpruned(cr);
/*     */ 
/*     */ 
/*     */     
/* 119 */     cr.getModelInfo(1).setModel((ClusModel)orig);
/*     */ 
/*     */     
/* 122 */     System.out.println("Calculating Statistical Measures...");
/*     */     
/* 124 */     RowData trainData = (RowData)cr.getTrainingSet();
/* 125 */     KNNStatistics stats = new KNNStatistics(trainData);
/* 126 */     System.out.println("Done.");
/*     */ 
/*     */ 
/*     */     
/* 130 */     if (Settings.kNN_attrWeighted.getValue() && Settings.kNN_normalized.getValue()) {
/* 131 */       weights = calcWeights(stats, trainData);
/*     */     } else {
/*     */       
/* 134 */       weights = noWeights(trainData);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 139 */     ClusAttrType[] attrs = trainData.getSchema().getDescriptiveAttributes();
/*     */     
/* 141 */     String d = Settings.kNN_vectDist.getValue();
/* 142 */     if (d.equals("Manhattan")) {
/* 143 */       vd = new ManhattanDistance(attrs, weights);
/*     */     } else {
/*     */       
/* 146 */       vd = new EuclidianDistance(attrs, weights);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     KNNTree tree = KNNTree.makeTree(cr, orig, vd);
/*     */ 
/*     */     
/* 155 */     storeDataInTree(trainData, tree);
/*     */ 
/*     */     
/* 158 */     cr.getModelInfo(1).setModel((ClusModel)tree);
/*     */ 
/*     */ 
/*     */     
/* 162 */     double vsb = this.m_Clus.getSettings().getPruneProportion();
/* 163 */     if (vsb > 0.0D) {
/*     */       
/* 165 */       KNNTree pruned = (KNNTree)tree.cloneTree();
/*     */       
/* 167 */       ClusErrorList error_parent = cr.getStatManager().createEvalError();
/* 168 */       RowData pruneset = (RowData)cr.getPruneSet();
/* 169 */       BottomUpPruningVSB pruner = new BottomUpPruningVSB(error_parent, pruneset);
/*     */       
/* 171 */       pruner.prune(pruned);
/*     */       
/* 173 */       cr.getModelInfo(2).setModel((ClusModel)pruned);
/*     */     } 
/*     */ 
/*     */     
/* 177 */     ClusModel defmodel = ClusDecisionTree.induceDefault(cr);
/* 178 */     cr.getModelInfo(0).setModel(defmodel);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void storeDataInTree(RowData data, KNNTree tree) {
/*     */     try {
/* 185 */       StoreExampleProcessor p = new StoreExampleProcessor();
/* 186 */       MyArray a = new MyArray();
/* 187 */       a.addElement(p);
/*     */       
/* 189 */       MemoryTupleIterator memoryTupleIterator = data.getIterator();
/* 190 */       DataTuple tuple = memoryTupleIterator.readTuple();
/* 191 */       while (tuple != null) {
/* 192 */         tree.applyModelProcessors(tuple, a);
/*     */         
/* 194 */         tuple = memoryTupleIterator.readTuple();
/*     */       } 
/* 196 */       memoryTupleIterator.close();
/* 197 */     } catch (IOException ioe) {
/* 198 */       System.err.println("IOException occurred:" + ioe.getMessage());
/* 199 */     } catch (ClusException ce) {
/* 200 */       System.err.println("ClusException occurred:" + ce.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private double[] calcWeights(KNNStatistics stats, RowData data) {
/* 206 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/* 207 */     double[] weights = new double[attrs.length];
/* 208 */     int nbr = data.getNbRows();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 214 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/*     */     
/* 216 */     for (int i = 0; i < nbr; i++) {
/* 217 */       DataTuple curTup = data.getTuple(i);
/* 218 */       int curTargetVal = curTup.getClassification();
/* 219 */       DataTuple curProto = stats.getPrototype(curTargetVal);
/* 220 */       for (int k = 0; k < attrs.length; k++) {
/*     */         
/* 222 */         if (attrs[k].getTypeIndex() == 0) {
/* 223 */           double w_upd = nomDist.getDistance(attrs[k], curTup, curProto);
/* 224 */           weights[k] = weights[k] + w_upd;
/*     */         }
/* 226 */         else if (attrs[k].getTypeIndex() == 1) {
/* 227 */           double w_upd = numDist.getDistance(attrs[k], curTup, curProto);
/* 228 */           weights[k] = weights[k] + w_upd;
/*     */         } 
/*     */       } 
/*     */     } 
/* 232 */     for (int j = 0; j < attrs.length; j++) {
/* 233 */       weights[j] = 1.0D - weights[j] / nbr;
/*     */     }
/*     */     
/* 236 */     return weights;
/*     */   }
/*     */   
/*     */   private double[] noWeights(RowData data) {
/* 240 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/* 241 */     double[] weights = new double[attrs.length];
/* 242 */     for (int j = 0; j < weights.length; j++) {
/* 243 */       weights[j] = 1.0D;
/*     */     }
/* 245 */     return weights;
/*     */   }
/*     */ 
/*     */   
/*     */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*     */   
/*     */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 252 */     return model;
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/* 256 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\KNNTreeClassifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */