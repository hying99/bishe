/*     */ package clus.algo.tdidt;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.FindBestTest;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.multiscore.MultiScore;
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
/*     */ public class DepthFirstInduce
/*     */   extends ClusInductionAlgorithm
/*     */ {
/*     */   protected FindBestTest m_FindBestTest;
/*     */   protected ClusNode m_Root;
/*     */   
/*     */   public DepthFirstInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  45 */     super(schema, sett);
/*  46 */     this.m_FindBestTest = new FindBestTest(getStatManager());
/*     */   }
/*     */   
/*     */   public DepthFirstInduce(ClusInductionAlgorithm other) {
/*  50 */     super(other);
/*  51 */     this.m_FindBestTest = new FindBestTest(getStatManager());
/*     */   }
/*     */   
/*     */   public DepthFirstInduce(ClusInductionAlgorithm other, NominalSplit split) {
/*  55 */     super(other);
/*  56 */     this.m_FindBestTest = new FindBestTest(getStatManager(), split);
/*     */   }
/*     */   
/*     */   public void initialize() throws ClusException, IOException {
/*  60 */     super.initialize();
/*     */   }
/*     */   
/*     */   public FindBestTest getFindBestTest() {
/*  64 */     return this.m_FindBestTest;
/*     */   }
/*     */   
/*     */   public CurrentBestTestAndHeuristic getBestTest() {
/*  68 */     return this.m_FindBestTest.getBestTest();
/*     */   }
/*     */   
/*     */   public boolean initSelectorAndStopCrit(ClusNode node, RowData data) {
/*  72 */     int max = getSettings().getTreeMaxDepth();
/*     */     
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
/* 109 */     CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/* 110 */     int arity = node.getTest().updateArity();
/* 111 */     ArrayList<NodeTest> v = best.getAlternativeBest();
/* 112 */     for (int k = 0; k < v.size(); k++) {
/* 113 */       NodeTest nt = v.get(k);
/* 114 */       int altarity = nt.updateArity();
/*     */       
/* 116 */       if (altarity != arity) {
/* 117 */         v.remove(k);
/* 118 */         k--;
/* 119 */         System.out.println("Alternative split with different arity: " + nt.getString());
/* 120 */         removed = true;
/*     */       }
/*     */       else {
/*     */         
/* 124 */         int nbsubset0 = subsets[0].getNbRows();
/* 125 */         int[] indices = new int[nbsubset0];
/* 126 */         for (int m = 0; m < nbsubset0; m++) {
/* 127 */           indices[m] = subsets[0].getTuple(m).getIndex();
/*     */         }
/* 129 */         boolean same = false;
/* 130 */         for (int l = 0; l < altarity; l++) {
/* 131 */           RowData altrd = data.applyWeighted(nt, l);
/* 132 */           if (altrd.getNbRows() == nbsubset0) {
/* 133 */             int nbsame = 0;
/* 134 */             for (int i = 0; i < nbsubset0; i++) {
/* 135 */               if (altrd.getTuple(i).getIndex() == indices[i]) {
/* 136 */                 nbsame++;
/*     */               }
/*     */             } 
/* 139 */             if (nbsame == nbsubset0) {
/* 140 */               same = true;
/* 141 */               if (l != 0) {
/*     */                 
/* 143 */                 String test = v.get(k).toString();
/* 144 */                 String newtest = "not(" + test + ")";
/* 145 */                 v.set(k, new String(newtest));
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/* 150 */         if (!same) {
/* 151 */           v.remove(k);
/* 152 */           k--;
/* 153 */           System.out.println("Alternative split with different ex in subsets: " + nt.getString());
/* 154 */           removed = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 159 */     node.setAlternatives(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeLeaf(ClusNode node) {
/* 164 */     node.makeLeaf();
/* 165 */     if (getSettings().hasTreeOptimize(0)) {
/* 166 */       node.setClusteringStat((ClusStatistic)null);
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
/*     */   
/*     */   public void induce(ClusNode node, RowData data) {
/* 185 */     if (initSelectorAndStopCrit(node, data)) {
/*     */       
/* 187 */       makeLeaf(node);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 193 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/*     */     
/* 195 */     for (int i = 0; i < attrs.length; i++) {
/* 196 */       ClusAttrType at = attrs[i];
/*     */       
/* 198 */       if (at instanceof NominalAttrType) {
/* 199 */         this.m_FindBestTest.findNominal((NominalAttrType)at, data);
/*     */       } else {
/* 201 */         this.m_FindBestTest.findNumeric((NumericAttrType)at, data);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 209 */     CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/*     */ 
/*     */ 
/*     */     
/* 213 */     if (best.hasBestTest()) {
/*     */ 
/*     */       
/* 216 */       node.testToNode(best);
/*     */       
/* 218 */       if (Settings.VERBOSE > 0) {
/* 219 */         System.out.println("Test: " + node.getTestString() + " -> " + best.getHeuristicValue());
/*     */       }
/*     */       
/* 222 */       int arity = node.updateArity();
/* 223 */       NodeTest test = node.getTest();
/* 224 */       RowData[] subsets = new RowData[arity]; int j;
/* 225 */       for (j = 0; j < arity; j++)
/*     */       {
/* 227 */         subsets[j] = data.applyWeighted(test, j);
/*     */       }
/*     */ 
/*     */       
/* 231 */       if (getSettings().showAlternativeSplits()) {
/* 232 */         filterAlternativeSplits(node, data, subsets);
/*     */       }
/* 234 */       if (node != this.m_Root && getSettings().hasTreeOptimize(1)) {
/*     */         
/* 236 */         node.setClusteringStat((ClusStatistic)null);
/* 237 */         node.setTargetStat((ClusStatistic)null);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 242 */       for (j = 0; j < arity; j++)
/*     */       {
/* 244 */         ClusNode child = new ClusNode();
/* 245 */         node.setChild((Node)child, j);
/* 246 */         child.initClusteringStat(this.m_StatManager, this.m_Root.getClusteringStat(), subsets[j]);
/* 247 */         child.initTargetStat(this.m_StatManager, this.m_Root.getTargetStat(), subsets[j]);
/*     */         
/* 249 */         induce(child, subsets[j]);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 254 */       makeLeaf(node);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rankFeatures(ClusNode node, RowData data) throws IOException {
/* 261 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream("ranking.csv")));
/* 262 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/* 263 */     for (int i = 0; i < attrs.length; i++) {
/* 264 */       ClusAttrType at = attrs[i];
/* 265 */       initSelectorAndStopCrit(node, data);
/* 266 */       if (at instanceof NominalAttrType) {
/* 267 */         this.m_FindBestTest.findNominal((NominalAttrType)at, data);
/*     */       } else {
/* 269 */         this.m_FindBestTest.findNumeric((NumericAttrType)at, data);
/*     */       } 
/* 271 */       CurrentBestTestAndHeuristic cbt = this.m_FindBestTest.getBestTest();
/* 272 */       if (cbt.hasBestTest()) {
/* 273 */         NodeTest test = cbt.updateTest();
/* 274 */         wrt.print(cbt.m_BestHeur);
/* 275 */         wrt.print(",\"" + at.getName() + "\"");
/* 276 */         wrt.println(",\"" + test + "\"");
/*     */       } 
/*     */     } 
/* 279 */     wrt.close();
/*     */   }
/*     */   
/*     */   public void initSelectorAndSplit(ClusStatistic stat) throws ClusException {
/* 283 */     this.m_FindBestTest.initSelectorAndSplit(stat);
/*     */   }
/*     */   
/*     */   public void setInitialData(ClusStatistic stat, RowData data) throws ClusException {
/* 287 */     this.m_FindBestTest.setInitialData(stat, data);
/*     */   }
/*     */   
/*     */   public void cleanSplit() {
/* 291 */     this.m_FindBestTest.cleanSplit();
/*     */   }
/*     */   
/*     */   public ClusNode induceSingleUnpruned(RowData data) throws ClusException, IOException {
/* 295 */     this.m_Root = null;
/*     */     
/* 297 */     int nbr = 0;
/*     */     do {
/* 299 */       nbr++;
/*     */       
/* 301 */       this.m_Root = new ClusNode();
/* 302 */       this.m_Root.initClusteringStat(this.m_StatManager, data);
/* 303 */       this.m_Root.initTargetStat(this.m_StatManager, data);
/* 304 */       this.m_Root.getClusteringStat().showRootInfo();
/* 305 */       initSelectorAndSplit(this.m_Root.getClusteringStat());
/* 306 */       setInitialData(this.m_Root.getClusteringStat(), data);
/*     */ 
/*     */       
/* 309 */       induce(this.m_Root, data);
/*     */ 
/*     */     
/*     */     }
/* 313 */     while (Settings.EXACT_TIME);
/*     */ 
/*     */ 
/*     */     
/* 317 */     this.m_Root.postProc((MultiScore)null);
/*     */     
/* 319 */     cleanSplit();
/* 320 */     return this.m_Root;
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 324 */     return induceSingleUnpruned((RowData)cr.getTrainingSet());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\tdidt\DepthFirstInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */