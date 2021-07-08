/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.FindBestTest;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
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
/*     */ public class ClusFastBeamSearch
/*     */   extends ClusBeamSearch
/*     */ {
/*     */   ClusBeamSizeConstraints m_Constr;
/*     */   
/*     */   public ClusFastBeamSearch(Clus clus) throws IOException, ClusException {
/*  77 */     super(clus);
/*  78 */     this.m_Constr = new ClusBeamSizeConstraints();
/*     */   }
/*     */   
/*     */   public ClusBeam initializeBeam(ClusRun run) throws ClusException, IOException {
/*  82 */     ClusBeam beam = super.initializeBeam(run);
/*  83 */     ClusBeamModel model = beam.getBestModel();
/*  84 */     initModelRecursive((ClusNode)model.getModel(), (RowData)run.getTrainingSet());
/*  85 */     return beam;
/*     */   }
/*     */   
/*     */   public void initModelRecursive(ClusNode node, RowData data) {
/*  89 */     if (node.atBottomLevel()) {
/*  90 */       ClusBeamAttrSelector attrsel = new ClusBeamAttrSelector();
/*  91 */       attrsel.setData(data);
/*  92 */       node.setVisitor(attrsel);
/*     */     } else {
/*  94 */       NodeTest test = node.getTest();
/*  95 */       for (int j = 0; j < node.getNbChildren(); j++) {
/*  96 */         ClusNode child = (ClusNode)node.getChild(j);
/*  97 */         RowData subset = data.applyWeighted(test, j);
/*  98 */         initModelRecursive(child, subset);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeGlobalHeuristic(NodeTest test, RowData data, CurrentBestTestAndHeuristic sel) {
/* 104 */     sel.reset(2);
/* 105 */     data.calcPosAndMissStat(test, 0, sel.getPosStat(), sel.getMissStat());
/* 106 */     double global_heur = this.m_Heuristic.calcHeuristic(sel.getTotStat(), sel.getPosStat(), sel.getMissStat());
/* 107 */     test.setHeuristicValue(global_heur);
/*     */   }
/*     */   
/*     */   public void refineGivenLeaf(ClusNode leaf, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs) {
/* 111 */     ClusBeamAttrSelector attrsel = (ClusBeamAttrSelector)leaf.getVisitor();
/* 112 */     if (attrsel.isStopCrit()) {
/*     */       
/* 114 */       if (this.m_Verbose) System.out.print("[S:" + leaf.getClusteringStat() + "]"); 
/*     */       return;
/*     */     } 
/* 117 */     RowData data = attrsel.getData();
/* 118 */     if (!attrsel.hasEvaluations()) {
/* 119 */       if (this.m_Induce.initSelectorAndStopCrit(leaf, data)) {
/*     */         
/* 121 */         attrsel.setStopCrit(true);
/*     */         return;
/*     */       } 
/* 124 */       CurrentBestTestAndHeuristic sel = this.m_Induce.getBestTest();
/* 125 */       FindBestTest find = this.m_Induce.getFindBestTest();
/* 126 */       this.m_Heuristic.setTreeOffset(0.0D);
/* 127 */       attrsel.newEvaluations(attrs.length);
/* 128 */       for (int j = 0; j < attrs.length; j++) {
/* 129 */         sel.resetBestTest();
/* 130 */         ClusAttrType at = attrs[j];
/* 131 */         if (at instanceof NominalAttrType) { find.findNominal((NominalAttrType)at, data); }
/* 132 */         else { find.findNumeric((NumericAttrType)at, data); }
/*     */         
/* 134 */         if (sel.hasBestTest()) {
/* 135 */           NodeTest test = sel.updateTest();
/* 136 */           if (hasAttrHeuristic())
/*     */           {
/* 138 */             computeGlobalHeuristic(test, data, sel);
/*     */           }
/* 140 */           attrsel.setBestTest(j, test);
/*     */         } 
/*     */       } 
/*     */     } 
/* 144 */     double offset = root.getValue() - this.m_Heuristic.computeLeafAdd(leaf);
/* 145 */     NodeTest[] besttests = attrsel.getBestTests();
/* 146 */     if (this.m_Verbose) System.out.println("[M:" + beam.getMinValue() + "]"); 
/* 147 */     for (int i = 0; i < besttests.length; i++) {
/* 148 */       NodeTest test = besttests[i];
/* 149 */       if (test != null) {
/* 150 */         double beam_min_value = beam.getMinValue();
/* 151 */         double heuristic = test.getHeuristicValue() + offset;
/* 152 */         if (heuristic >= beam_min_value)
/* 153 */         { if (this.m_Verbose) System.out.print("[+]"); 
/* 154 */           ClusNode ref_leaf = (ClusNode)leaf.cloneNode();
/* 155 */           ref_leaf.setTest(test);
/*     */           
/* 157 */           ref_leaf.setVisitor(leaf.getVisitor());
/* 158 */           if (Settings.VERBOSE > 0) System.out.println("Test: " + ref_leaf.getTestString() + " -> " + ref_leaf.getTest().getHeuristicValue() + " (" + ref_leaf.getTest().getPosFreq() + ")"); 
/* 159 */           int arity = ref_leaf.updateArity();
/* 160 */           for (int j = 0; j < arity; j++) {
/* 161 */             ClusNode child = new ClusNode();
/* 162 */             ref_leaf.setChild((Node)child, j);
/*     */           } 
/* 164 */           ClusNode root_model = (ClusNode)root.getModel();
/* 165 */           ClusNode ref_tree = root_model.cloneTreeWithVisitors(leaf, ref_leaf);
/* 166 */           ClusBeamModel new_model = new ClusBeamModel(heuristic, (ClusModel)ref_tree);
/* 167 */           new_model.setRefinement(ref_leaf);
/* 168 */           new_model.setParentModelIndex(getCurrentModel());
/* 169 */           beam.addModel(new_model);
/* 170 */           setBeamChanged(true); }
/*     */         
/* 172 */         else if (this.m_Verbose) { System.out.print("[-:" + heuristic + "]"); }
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void refineModel(ClusBeamModel model, ClusBeam beam, ClusRun run) throws IOException {
/* 179 */     ClusNode tree = (ClusNode)model.getModel();
/* 180 */     ClusBeamModel new_model = model.cloneModel();
/*     */     
/* 182 */     new_model.setValue(sanityCheck(model.getValue(), tree));
/* 183 */     if (isBeamPostPrune()) {
/* 184 */       ClusNode clone = tree.cloneTreeWithVisitors();
/* 185 */       this.m_Constr.enforce(clone, this.m_MaxTreeSize);
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
/* 197 */       if (this.m_Constr.isFinished()) {
/* 198 */         model.setFinished(true);
/*     */         return;
/*     */       } 
/* 201 */       if (this.m_Constr.isModified()) {
/* 202 */         new_model.setModel(clone);
/* 203 */         new_model.setValue(estimateBeamMeasure(clone));
/*     */       }
/*     */     
/* 206 */     } else if (this.m_MaxTreeSize >= 0) {
/* 207 */       int size = tree.getNbNodes();
/* 208 */       if (size + 2 > this.m_MaxTreeSize) {
/* 209 */         model.setFinished(true);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 214 */     RowData train = (RowData)run.getTrainingSet();
/* 215 */     ClusAttrType[] attrs = train.getSchema().getDescriptiveAttributes();
/* 216 */     refineEachLeaf((ClusNode)new_model.getModel(), new_model, beam, attrs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateModelRefinement(ClusBeamModel model) {
/* 221 */     ClusNode leaf = (ClusNode)model.getRefinement();
/* 222 */     if (leaf == null)
/* 223 */       return;  ClusBeamAttrSelector attrsel = (ClusBeamAttrSelector)leaf.getVisitor();
/* 224 */     RowData data = attrsel.getData();
/* 225 */     ClusStatManager mgr = this.m_Induce.getStatManager();
/* 226 */     for (int j = 0; j < leaf.getNbChildren(); j++) {
/* 227 */       ClusNode child = (ClusNode)leaf.getChild(j);
/* 228 */       ClusBeamAttrSelector casel = new ClusBeamAttrSelector();
/* 229 */       RowData subset = data.applyWeighted(leaf.getTest(), j);
/* 230 */       child.initTargetStat(mgr, subset);
/* 231 */       child.initClusteringStat(mgr, subset);
/* 232 */       casel.setData(subset);
/* 233 */       child.setVisitor(casel);
/*     */     } 
/* 235 */     leaf.setVisitor(null);
/* 236 */     model.setRefinement(null);
/*     */   }
/*     */   
/*     */   public void refineBeam(ClusBeam beam, ClusRun run) throws IOException {
/* 240 */     super.refineBeam(beam, run);
/* 241 */     ArrayList<ClusBeamModel> models = beam.toArray();
/* 242 */     for (int i = 0; i < models.size(); i++) {
/* 243 */       ClusBeamModel model = models.get(i);
/* 244 */       updateModelRefinement(model);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusFastBeamSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */