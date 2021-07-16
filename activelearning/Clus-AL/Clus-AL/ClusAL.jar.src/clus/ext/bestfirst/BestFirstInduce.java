/*     */ package clus.ext.bestfirst;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.FindBestTest;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.ext.ensembles.ClusEnsembleInduce;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import jeans.tree.Node;
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
/*     */ public class BestFirstInduce
/*     */   extends ClusInductionAlgorithm
/*     */ {
/*     */   protected FindBestTest m_FindBestTest;
/*     */   protected ClusNode m_Root;
/*     */   
/*     */   public BestFirstInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  46 */     super(schema, sett);
/*  47 */     this.m_FindBestTest = new FindBestTest(getStatManager());
/*     */   }
/*     */   
/*     */   public BestFirstInduce(ClusInductionAlgorithm other) {
/*  51 */     super(other);
/*  52 */     this.m_FindBestTest = new FindBestTest(getStatManager());
/*     */   }
/*     */   
/*     */   public BestFirstInduce(ClusInductionAlgorithm other, NominalSplit split) {
/*  56 */     super(other);
/*  57 */     this.m_FindBestTest = new FindBestTest(getStatManager(), split);
/*     */   }
/*     */   
/*     */   public void initialize() throws ClusException, IOException {
/*  61 */     super.initialize();
/*     */   }
/*     */   
/*     */   public FindBestTest getFindBestTest() {
/*  65 */     return this.m_FindBestTest;
/*     */   }
/*     */   
/*     */   public CurrentBestTestAndHeuristic getBestTest() {
/*  69 */     return this.m_FindBestTest.getBestTest();
/*     */   }
/*     */   
/*     */   public boolean initSelectorAndStopCrit(ClusNode node, RowData data) {
/*  73 */     int max = getSettings().getTreeMaxDepth();
/*  74 */     if (max != -1 && node.getLevel() >= max) {
/*  75 */       return true;
/*     */     }
/*  77 */     return this.m_FindBestTest.initSelectorAndStopCrit(node.getClusteringStat(), data);
/*     */   }
/*     */   public ClusAttrType[] getDescriptiveAttributes() {
/*     */     ClusAttrType[] attrsAll, attrsAll1;
/*  81 */     ClusSchema schema = getSchema();
/*  82 */     Settings sett = getSettings();
/*  83 */     if (!sett.isEnsembleMode()) {
/*  84 */       return schema.getDescriptiveAttributes();
/*     */     }
/*  86 */     switch (sett.getEnsembleMethod()) {
/*     */       case 0:
/*  88 */         return schema.getDescriptiveAttributes();
/*     */       case 1:
/*  90 */         attrsAll = schema.getDescriptiveAttributes();
/*  91 */         ClusEnsembleInduce.setRandomSubspaces(attrsAll, schema.getSettings().getNbRandomAttrSelected());
/*  92 */         return ClusEnsembleInduce.getRandomSubspaces();
/*     */       case 2:
/*  94 */         return ClusEnsembleInduce.getRandomSubspaces();
/*     */       case 3:
/*  96 */         return ClusEnsembleInduce.getRandomSubspaces();
/*     */       case 5:
/*  98 */         attrsAll1 = schema.getDescriptiveAttributes();
/*  99 */         ClusEnsembleInduce.setRandomSubspaces(attrsAll1, schema.getSettings().getNbRandomAttrSelected());
/* 100 */         return ClusEnsembleInduce.getRandomSubspaces();
/*     */     } 
/* 102 */     return schema.getDescriptiveAttributes();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void filterAlternativeSplits(ClusNode node, RowData data, RowData[] subsets) {
/* 108 */     boolean removed = false;
/*     */     
/* 110 */     CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/*     */     
/* 112 */     int arity = node.getTest().updateArity();
/* 113 */     ArrayList<NodeTest> v = best.getAlternativeBest();
/* 114 */     for (int k = 0; k < v.size(); k++) {
/* 115 */       NodeTest nt = v.get(k);
/* 116 */       int altarity = nt.updateArity();
/*     */       
/* 118 */       if (altarity != arity) {
/* 119 */         v.remove(k);
/* 120 */         k--;
/* 121 */         System.out.println("Alternative split with different arity: " + nt.getString());
/* 122 */         removed = true;
/*     */       }
/*     */       else {
/*     */         
/* 126 */         int nbsubset0 = subsets[0].getNbRows();
/* 127 */         int[] indices = new int[nbsubset0];
/* 128 */         for (int m = 0; m < nbsubset0; m++) {
/* 129 */           indices[m] = subsets[0].getTuple(m).getIndex();
/*     */         }
/* 131 */         boolean same = false;
/* 132 */         for (int l = 0; l < altarity; l++) {
/* 133 */           RowData altrd = data.applyWeighted(nt, l);
/* 134 */           if (altrd.getNbRows() == nbsubset0) {
/* 135 */             int nbsame = 0;
/* 136 */             for (int i = 0; i < nbsubset0; i++) {
/* 137 */               if (altrd.getTuple(i).getIndex() == indices[i]) {
/* 138 */                 nbsame++;
/*     */               }
/*     */             } 
/* 141 */             if (nbsame == nbsubset0) {
/* 142 */               same = true;
/* 143 */               if (l != 0) {
/*     */                 
/* 145 */                 String test = v.get(k).toString();
/* 146 */                 String newtest = "not(" + test + ")";
/* 147 */                 v.set(k, new String(newtest));
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/* 152 */         if (!same) {
/* 153 */           v.remove(k);
/* 154 */           k--;
/* 155 */           System.out.println("Alternative split with different ex in subsets: " + nt.getString());
/* 156 */           removed = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 161 */     node.setAlternatives(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeLeaf(ClusNode node) {
/* 166 */     node.makeLeaf();
/* 167 */     if (getSettings().hasTreeOptimize(0)) {
/* 168 */       node.setClusteringStat(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private int getBestNodeToSplit(ArrayList<ClusNode> listNodes) {
/* 174 */     int pos = 0;
/* 175 */     double best = Double.NEGATIVE_INFINITY;
/*     */ 
/*     */     
/* 178 */     for (int j = 0; j < listNodes.size(); j++) {
/*     */       
/* 180 */       NodeTest bestTestNode = ((ClusNode)listNodes.get(j)).getTest();
/*     */       
/* 182 */       double valueHeuristic = bestTestNode.getHeuristicValue();
/*     */ 
/*     */ 
/*     */       
/* 186 */       if (best < valueHeuristic) {
/* 187 */         best = valueHeuristic;
/* 188 */         pos = j;
/*     */       } 
/*     */     } 
/*     */     
/* 192 */     return pos;
/*     */   }
/*     */ 
/*     */   
/*     */   public void inducePre(ClusNode node, RowData data) {
/* 197 */     ArrayList<ClusNode> listNodes = new ArrayList<>();
/*     */     
/* 199 */     ArrayList<RowData> subsets = new ArrayList<>();
/*     */     
/* 201 */     ArrayList<RowData> dataLeaves = new ArrayList<>();
/*     */     
/* 203 */     subsets.add(0, data);
/* 204 */     dataLeaves.add(0, data);
/*     */ 
/*     */     
/* 207 */     if (initSelectorAndStopCrit(node, data)) {
/* 208 */       makeLeaf(node);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 213 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/* 214 */     for (int i = 0; i < attrs.length; i++) {
/* 215 */       ClusAttrType at = attrs[i];
/* 216 */       if (at instanceof NominalAttrType) {
/* 217 */         this.m_FindBestTest.findNominal((NominalAttrType)at, data);
/*     */       } else {
/* 219 */         this.m_FindBestTest.findNumeric((NumericAttrType)at, data);
/*     */       } 
/*     */     } 
/*     */     
/* 223 */     CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/*     */     
/* 225 */     if (best.hasBestTest()) {
/*     */ 
/*     */       
/* 228 */       node.testToNode(best);
/*     */       
/* 230 */       listNodes.add(node);
/*     */ 
/*     */ 
/*     */       
/* 234 */       induce(listNodes, subsets, dataLeaves, 0);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 239 */       makeLeaf(node);
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
/*     */   public void induce(ArrayList<ClusNode> listNodes, ArrayList<RowData> subsets, ArrayList<RowData> dataLeaves, int bestTestIndex) {
/* 251 */     ClusNode node = listNodes.get(bestTestIndex);
/* 252 */     RowData data = subsets.get(bestTestIndex);
/*     */ 
/*     */     
/* 255 */     if (Settings.VERBOSE > 0) {
/* 256 */       System.out.println("Test: " + node.getTestString() + " -> " + node.getTest().getHeuristicValue());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 261 */     int arity = node.updateArity();
/*     */ 
/*     */     
/* 264 */     NodeTest test = node.getTest();
/*     */ 
/*     */     
/* 267 */     listNodes.remove(bestTestIndex);
/*     */ 
/*     */     
/* 270 */     subsets.remove(bestTestIndex);
/* 271 */     dataLeaves.remove(bestTestIndex);
/*     */ 
/*     */     
/* 274 */     RowData[] subsetsLocal = new RowData[arity];
/* 275 */     for (int j = 0; j < arity; j++) {
/* 276 */       subsets.add(data.applyWeighted(test, j));
/* 277 */       dataLeaves.add(subsets.size() - 1, subsets.get(subsets.size() - 1));
/* 278 */       subsetsLocal[j] = subsets.get(subsets.size() - 1);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 283 */     if (initSelectorAndStopCrit(node, data)) {
/* 284 */       makeLeaf(node);
/*     */     }
/*     */ 
/*     */     
/* 288 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/*     */     
/* 290 */     for (int i = 0; i < attrs.length; i++) {
/* 291 */       ClusAttrType at = attrs[i];
/* 292 */       if (at instanceof NominalAttrType) {
/* 293 */         this.m_FindBestTest.findNominal((NominalAttrType)at, data);
/*     */       } else {
/* 295 */         this.m_FindBestTest.findNumeric((NumericAttrType)at, data);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 300 */     if (getSettings().showAlternativeSplits()) {
/* 301 */       filterAlternativeSplits(node, data, subsetsLocal);
/*     */     }
/*     */ 
/*     */     
/* 305 */     if (node != this.m_Root && getSettings().hasTreeOptimize(1)) {
/*     */       
/* 307 */       node.setClusteringStat(null);
/* 308 */       node.setTargetStat(null);
/*     */     } 
/*     */     
/* 311 */     for (int k = 0; k < arity; k++) {
/*     */ 
/*     */       
/* 314 */       ClusNode child = new ClusNode();
/* 315 */       node.setChild((Node)child, k);
/*     */       
/* 317 */       child.initClusteringStat(this.m_StatManager, this.m_Root.getClusteringStat(), subsetsLocal[k]);
/* 318 */       child.initTargetStat(this.m_StatManager, this.m_Root.getTargetStat(), subsetsLocal[k]);
/*     */ 
/*     */       
/* 321 */       if (initSelectorAndStopCrit(child, subsetsLocal[k])) {
/* 322 */         subsets.remove(subsetsLocal[k]);
/*     */ 
/*     */         
/* 325 */         dataLeaves.remove(subsetsLocal[k]);
/* 326 */         dataLeaves.add(subsetsLocal[k]);
/*     */         
/* 328 */         makeLeaf(child);
/*     */       }
/*     */       else {
/*     */         
/* 332 */         attrs = getDescriptiveAttributes();
/* 333 */         for (int m = 0; m < attrs.length; m++) {
/* 334 */           ClusAttrType at = attrs[m];
/* 335 */           if (at instanceof NominalAttrType) {
/* 336 */             this.m_FindBestTest.findNominal((NominalAttrType)at, subsetsLocal[k]);
/*     */           } else {
/* 338 */             this.m_FindBestTest.findNumeric((NumericAttrType)at, subsetsLocal[k]);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 343 */         CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/*     */         
/* 345 */         if (best.hasBestTest()) {
/* 346 */           child.testToNode(best);
/* 347 */           listNodes.add(child);
/*     */         } else {
/* 349 */           subsets.remove(subsetsLocal[k]);
/*     */ 
/*     */           
/* 352 */           dataLeaves.remove(subsetsLocal[k]);
/* 353 */           dataLeaves.add(subsetsLocal[k]);
/*     */           
/* 355 */           makeLeaf(child);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 361 */     if (listNodes.size() > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 368 */       bestTestIndex = getBestNodeToSplit(listNodes);
/* 369 */       induce(listNodes, subsets, dataLeaves, bestTestIndex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rankFeatures(ClusNode node, RowData data) throws IOException {
/* 376 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream("ranking.csv")));
/* 377 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/* 378 */     for (int i = 0; i < attrs.length; i++) {
/* 379 */       ClusAttrType at = attrs[i];
/* 380 */       initSelectorAndStopCrit(node, data);
/* 381 */       if (at instanceof NominalAttrType) {
/* 382 */         this.m_FindBestTest.findNominal((NominalAttrType)at, data);
/*     */       } else {
/* 384 */         this.m_FindBestTest.findNumeric((NumericAttrType)at, data);
/*     */       } 
/* 386 */       CurrentBestTestAndHeuristic cbt = this.m_FindBestTest.getBestTest();
/* 387 */       if (cbt.hasBestTest()) {
/* 388 */         NodeTest test = cbt.updateTest();
/* 389 */         wrt.print(cbt.m_BestHeur);
/* 390 */         wrt.print(",\"" + at.getName() + "\"");
/* 391 */         wrt.println(",\"" + test + "\"");
/*     */       } 
/*     */     } 
/* 394 */     wrt.close();
/*     */   }
/*     */   
/*     */   public void initSelectorAndSplit(ClusStatistic stat) throws ClusException {
/* 398 */     this.m_FindBestTest.initSelectorAndSplit(stat);
/*     */   }
/*     */   
/*     */   public void setInitialData(ClusStatistic stat, RowData data) throws ClusException {
/* 402 */     this.m_FindBestTest.setInitialData(stat, data);
/*     */   }
/*     */   
/*     */   public void cleanSplit() {
/* 406 */     this.m_FindBestTest.cleanSplit();
/*     */   }
/*     */   
/*     */   public ClusNode induceSingleUnpruned(RowData data) throws ClusException, IOException {
/* 410 */     this.m_Root = null;
/*     */     
/* 412 */     int nbr = 0;
/*     */     do {
/* 414 */       nbr++;
/*     */       
/* 416 */       this.m_Root = new ClusNode();
/* 417 */       this.m_Root.initClusteringStat(this.m_StatManager, data);
/* 418 */       this.m_Root.initTargetStat(this.m_StatManager, data);
/* 419 */       this.m_Root.getClusteringStat().showRootInfo();
/* 420 */       initSelectorAndSplit(this.m_Root.getClusteringStat());
/* 421 */       setInitialData(this.m_Root.getClusteringStat(), data);
/*     */       
/* 423 */       inducePre(this.m_Root, data);
/*     */     
/*     */     }
/* 426 */     while (Settings.EXACT_TIME);
/*     */ 
/*     */ 
/*     */     
/* 430 */     this.m_Root.postProc(null);
/*     */     
/* 432 */     cleanSplit();
/* 433 */     return this.m_Root;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 438 */     return (ClusModel)induceSingleUnpruned((RowData)cr.getTrainingSet());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\bestfirst\BestFirstInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */