/*     */ package clus.algo.tdidt;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.data.type.SparseNumericAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import jeans.tree.Node;
/*     */ 
/*     */ public class DepthFirstInduceSparse extends DepthFirstInduce {
/*     */   public DepthFirstInduceSparse(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  23 */     super(schema, sett);
/*  24 */     if (Settings.VERBOSE > 0) System.out.println("Sparse implementation"); 
/*     */   }
/*     */   
/*     */   public DepthFirstInduceSparse(ClusInductionAlgorithm other) {
/*  28 */     super(other);
/*  29 */     if (Settings.VERBOSE > 0) System.out.println("Sparse implementation"); 
/*     */   }
/*     */   
/*     */   public DepthFirstInduceSparse(ClusInductionAlgorithm other, NominalSplit split) {
/*  33 */     super(other);
/*  34 */     if (Settings.VERBOSE > 0) System.out.println("Sparse implementation"); 
/*     */   }
/*     */   
/*     */   public void initializeExamples(ClusAttrType[] attrs, RowData data) {
/*     */     int i;
/*  39 */     for (i = 0; i < attrs.length; i++) {
/*  40 */       ClusAttrType at = attrs[i];
/*  41 */       if (at.isSparse()) {
/*  42 */         ((SparseNumericAttrType)at).resetExamples();
/*     */       }
/*     */     } 
/*     */     
/*  46 */     for (i = 0; i < data.getNbRows(); i++) {
/*  47 */       SparseDataTuple tuple = (SparseDataTuple)data.getTuple(i);
/*  48 */       tuple.addExampleToAttributes();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void induce(ClusNode node, RowData data) {
/*  54 */     if (getSettings().isEnsembleMode() && (getSettings().getEnsembleMethod() == 1 || getSettings().getEnsembleMethod() == 5)) {
/*     */       
/*  56 */       induceRandomForest(node, data);
/*     */     } else {
/*     */       
/*  59 */       ClusAttrType[] attrs = getDescriptiveAttributes();
/*  60 */       initializeExamples(attrs, data);
/*  61 */       ArrayList<ClusAttrType> attrList = new ArrayList<>();
/*  62 */       ArrayList<ArrayList> examplelistList = new ArrayList<>();
/*  63 */       for (int i = 0; i < attrs.length; i++) {
/*  64 */         ClusAttrType at = attrs[i];
/*  65 */         if (at.isSparse()) {
/*  66 */           if (((SparseNumericAttrType)at).getExampleWeight() >= getSettings().getMinimalWeight()) {
/*  67 */             attrList.add(at);
/*     */             
/*  69 */             Object[] exampleArray = ((SparseNumericAttrType)at).getExamples().toArray();
/*  70 */             RowData exampleData = new RowData(exampleArray, exampleArray.length);
/*  71 */             exampleData.sortSparse((NumericAttrType)at, this.m_FindBestTest.getSortHelper());
/*  72 */             ArrayList<SparseDataTuple> exampleList = new ArrayList<>();
/*  73 */             for (int j = 0; j < exampleData.getNbRows(); j++) {
/*  74 */               exampleList.add((SparseDataTuple)exampleData.getTuple(j));
/*     */             }
/*  76 */             ((SparseNumericAttrType)at).setExamples(exampleList);
/*  77 */             examplelistList.add(exampleList);
/*     */           } 
/*     */         } else {
/*     */           
/*  81 */           attrList.add(at);
/*  82 */           examplelistList.add(null);
/*     */         } 
/*     */       } 
/*  85 */       Object[] attrArray = attrList.toArray();
/*  86 */       Object[] examplelistArray = examplelistList.toArray();
/*  87 */       induce(node, data, attrArray, examplelistArray);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induce(ClusNode node, RowData data, Object[] attrs, Object[] examplelists) {
/*  95 */     if (initSelectorAndStopCrit(node, data)) {
/*  96 */       makeLeaf(node);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 101 */     for (int i = 0; i < attrs.length; i++) {
/* 102 */       ClusAttrType at = (ClusAttrType)attrs[i];
/* 103 */       ArrayList examplelist = (ArrayList)examplelists[i];
/* 104 */       if (at instanceof NominalAttrType) { this.m_FindBestTest.findNominal((NominalAttrType)at, data); }
/* 105 */       else if (examplelist == null)
/* 106 */       { this.m_FindBestTest.findNumeric((NumericAttrType)at, data); }
/*     */       else
/*     */       
/* 109 */       { this.m_FindBestTest.findNumeric((NumericAttrType)at, examplelist); }
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 114 */     CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/*     */     
/* 116 */     if (best.hasBestTest()) {
/* 117 */       node.testToNode(best);
/*     */       
/* 119 */       if (Settings.VERBOSE > 0) System.out.println("Test: " + node.getTestString() + " -> " + best.getHeuristicValue());
/*     */       
/* 121 */       int arity = node.updateArity();
/* 122 */       NodeTest test = node.getTest();
/* 123 */       RowData[] subsets = new RowData[arity]; int j;
/* 124 */       for (j = 0; j < arity; j++) {
/* 125 */         subsets[j] = data.applyWeighted(test, j);
/*     */       }
/* 127 */       if (getSettings().showAlternativeSplits()) {
/* 128 */         filterAlternativeSplits(node, data, subsets);
/*     */       }
/* 130 */       if (node != this.m_Root && getSettings().hasTreeOptimize(1)) {
/*     */         
/* 132 */         node.setClusteringStat((ClusStatistic)null);
/* 133 */         node.setTargetStat((ClusStatistic)null);
/*     */       } 
/*     */       
/* 136 */       for (j = 0; j < arity; j++) {
/* 137 */         ClusNode child = new ClusNode();
/* 138 */         node.setChild((Node)child, j);
/* 139 */         child.initClusteringStat(this.m_StatManager, this.m_Root.getClusteringStat(), subsets[j]);
/* 140 */         child.initTargetStat(this.m_StatManager, this.m_Root.getTargetStat(), subsets[j]);
/* 141 */         ArrayList<ClusAttrType> attrList = new ArrayList<>();
/* 142 */         ArrayList<ArrayList> examplelistList = new ArrayList<>();
/* 143 */         for (int k = 0; k < attrs.length; k++) {
/* 144 */           ClusAttrType at = (ClusAttrType)attrs[k];
/* 145 */           if (at.isSparse()) {
/* 146 */             ArrayList newExampleList = ((SparseNumericAttrType)at).pruneExampleList(subsets[j]);
/* 147 */             double exampleWeight = getExampleWeight(newExampleList);
/* 148 */             if (exampleWeight >= getSettings().getMinimalWeight()) {
/* 149 */               attrList.add(at);
/* 150 */               examplelistList.add(newExampleList);
/*     */             } 
/*     */           } else {
/*     */             
/* 154 */             attrList.add(at);
/* 155 */             examplelistList.add(null);
/*     */           } 
/*     */         } 
/* 158 */         Object[] attrArray = attrList.toArray();
/* 159 */         Object[] exampleListArray = examplelistList.toArray();
/* 160 */         induce(child, subsets[j], attrArray, exampleListArray);
/*     */       } 
/*     */     } else {
/* 163 */       makeLeaf(node);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public double getExampleWeight(ArrayList<SparseDataTuple> examples) {
/* 169 */     double weight = 0.0D;
/* 170 */     for (int i = 0; i < examples.size(); i++) {
/* 171 */       SparseDataTuple tup = examples.get(i);
/* 172 */       weight += tup.getWeight();
/*     */     } 
/* 174 */     return weight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induceRandomForest(ClusNode node, RowData data) {
/* 183 */     ClusAttrType[] attrs = getSchema().getDescriptiveAttributes();
/* 184 */     initializeExamples(attrs, data);
/* 185 */     induceRandomForestRecursive(node, data);
/*     */   }
/*     */   
/*     */   public void induceRandomForestRecursive(ClusNode node, RowData data) {
/* 189 */     ClusAttrType[] attrs = getDescriptiveAttributes();
/* 190 */     ArrayList<ClusAttrType> attrList = new ArrayList<>();
/* 191 */     for (int i = 0; i < attrs.length; i++) {
/* 192 */       ClusAttrType at = attrs[i];
/* 193 */       if (at.isSparse()) {
/* 194 */         if (((SparseNumericAttrType)at).getExampleWeight() >= getSettings().getMinimalWeight()) {
/* 195 */           attrList.add(at);
/*     */         }
/*     */       } else {
/*     */         
/* 199 */         attrList.add(at);
/*     */       } 
/*     */     } 
/* 202 */     Object[] attrArray = attrList.toArray();
/* 203 */     induceRandomForestRecursive2(node, data, attrArray);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induceRandomForestRecursive2(ClusNode node, RowData data, Object[] attrs) {
/* 210 */     if (initSelectorAndStopCrit(node, data)) {
/* 211 */       makeLeaf(node);
/*     */       
/*     */       return;
/*     */     } 
/* 215 */     for (int i = 0; i < attrs.length; i++) {
/* 216 */       ClusAttrType at = (ClusAttrType)attrs[i];
/* 217 */       if (at instanceof NominalAttrType) { this.m_FindBestTest.findNominal((NominalAttrType)at, data); }
/* 218 */       else { this.m_FindBestTest.findNumeric((NumericAttrType)at, data); }
/*     */     
/*     */     } 
/*     */     
/* 222 */     CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/* 223 */     if (best.hasBestTest()) {
/* 224 */       node.testToNode(best);
/*     */       
/* 226 */       if (Settings.VERBOSE > 0) System.out.println("Test: " + node.getTestString() + " -> " + best.getHeuristicValue());
/*     */       
/* 228 */       int arity = node.updateArity();
/* 229 */       NodeTest test = node.getTest();
/* 230 */       RowData[] subsets = new RowData[arity]; int j;
/* 231 */       for (j = 0; j < arity; j++) {
/* 232 */         subsets[j] = data.applyWeighted(test, j);
/*     */       }
/* 234 */       if (getSettings().showAlternativeSplits()) {
/* 235 */         filterAlternativeSplits(node, data, subsets);
/*     */       }
/* 237 */       if (node != this.m_Root && getSettings().hasTreeOptimize(1)) {
/*     */         
/* 239 */         node.setClusteringStat((ClusStatistic)null);
/* 240 */         node.setTargetStat((ClusStatistic)null);
/*     */       } 
/*     */       
/* 243 */       for (j = 0; j < arity; j++) {
/* 244 */         ClusNode child = new ClusNode();
/* 245 */         node.setChild((Node)child, j);
/* 246 */         child.initClusteringStat(this.m_StatManager, this.m_Root.getClusteringStat(), subsets[j]);
/* 247 */         child.initTargetStat(this.m_StatManager, this.m_Root.getTargetStat(), subsets[j]);
/*     */         
/* 249 */         induceRandomForestRecursive(child, subsets[j]);
/*     */       } 
/*     */     } else {
/* 252 */       makeLeaf(node);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\tdidt\DepthFirstInduceSparse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */