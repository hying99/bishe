/*     */ package clus.algo.tdidt.tune;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.Accuracy;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.error.RMSError;
/*     */ import clus.ext.hierarchical.HierErrorMeasures;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.selection.XValRandomSelection;
/*     */ import clus.selection.XValSelection;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Random;
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
/*     */ public class CDTTuneFTest
/*     */   extends ClusDecisionTree
/*     */ {
/*     */   protected ClusInductionAlgorithmType m_Class;
/*     */   protected double[] m_FTests;
/*     */   
/*     */   public CDTTuneFTest(ClusInductionAlgorithmType clss) {
/*  61 */     super(clss.getClus());
/*  62 */     this.m_Class = clss;
/*     */   }
/*     */   
/*     */   public CDTTuneFTest(ClusInductionAlgorithmType clss, double[] ftests) {
/*  66 */     super(clss.getClus());
/*  67 */     this.m_Class = clss;
/*  68 */     this.m_FTests = ftests;
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  72 */     return this.m_Class.createInduce(schema, sett, cargs);
/*     */   }
/*     */   
/*     */   public void printInfo() {
/*  76 */     System.out.println("TDIDT (Tuning F-Test)");
/*  77 */     System.out.println("Heuristic: " + getStatManager().getHeuristicName());
/*     */   }
/*     */   
/*     */   private final void showFold(int i) {
/*  81 */     if (i != 0) System.out.print(" "); 
/*  82 */     System.out.print(String.valueOf(i + 1));
/*  83 */     System.out.flush();
/*     */   }
/*     */   
/*     */   public ClusErrorList createTuneError(ClusStatManager mgr) {
/*  87 */     ClusErrorList parent = new ClusErrorList();
/*  88 */     if (ClusStatManager.getMode() == 2) {
/*  89 */       int optimize = getSettings().getHierOptimizeErrorMeasure();
/*  90 */       parent.addError((ClusError)new HierErrorMeasures(parent, mgr.getHier(), null, getSettings().getCompatibility(), optimize, false));
/*  91 */       return parent;
/*     */     } 
/*  93 */     NumericAttrType[] num = mgr.getSchema().getNumericAttrUse(3);
/*  94 */     NominalAttrType[] nom = mgr.getSchema().getNominalAttrUse(3);
/*  95 */     if (nom.length != 0) {
/*  96 */       parent.addError((ClusError)new Accuracy(parent, nom));
/*     */     }
/*  98 */     if (num.length != 0)
/*     */     {
/* 100 */       parent.addError((ClusError)new RMSError(parent, num));
/*     */     }
/* 102 */     return parent;
/*     */   }
/*     */   
/*     */   public final ClusRun partitionDataBasic(ClusData data, ClusSelection sel, ClusSummary summary, int idx) throws IOException, ClusException {
/* 106 */     ClusRun cr = new ClusRun(data.cloneData(), summary);
/* 107 */     if (sel != null) {
/* 108 */       if (sel.changesDistribution()) {
/* 109 */         ((RowData)cr.getTrainingSet()).update(sel);
/*     */       } else {
/* 111 */         ClusData val = cr.getTrainingSet().select(sel);
/* 112 */         cr.setTestSet((TupleIterator)((RowData)val).getIterator());
/*     */       } 
/*     */     }
/* 115 */     cr.setIndex(idx);
/* 116 */     cr.copyTrainingData();
/* 117 */     return cr;
/*     */   }
/*     */   
/*     */   public double doParamXVal(RowData trset, RowData pruneset) throws ClusException, IOException {
/* 121 */     int prevVerb = Settings.enableVerbose(0);
/* 122 */     ClusStatManager mgr = getStatManager();
/* 123 */     ClusSummary summ = new ClusSummary();
/* 124 */     summ.setStatManager(getStatManager());
/* 125 */     summ.addModelInfo(1).setTestError(createTuneError(mgr));
/* 126 */     ClusRandom.initialize(getSettings());
/* 127 */     double avgSize = 0.0D;
/* 128 */     if (pruneset != null) {
/* 129 */       ClusRun cr = new ClusRun(trset.cloneData(), summ);
/* 130 */       ClusModel model = this.m_Class.induceSingleUnpruned(cr);
/* 131 */       avgSize = model.getModelSize();
/* 132 */       cr.addModelInfo(1).setModel(model);
/* 133 */       cr.addModelInfo(1).setTestError(createTuneError(mgr));
/* 134 */       this.m_Clus.calcError((TupleIterator)pruneset.getIterator(), 1, cr, null);
/* 135 */       summ.addSummary(cr);
/*     */     }
/*     */     else {
/*     */       
/* 139 */       Random random = new Random(0L);
/* 140 */       int nbfolds = Integer.parseInt(getSettings().getTuneFolds());
/* 141 */       XValRandomSelection xValRandomSelection = new XValRandomSelection(trset.getNbRows(), nbfolds, random);
/* 142 */       for (int i = 0; i < nbfolds; i++) {
/* 143 */         showFold(i);
/* 144 */         XValSelection msel = new XValSelection((XValMainSelection)xValRandomSelection, i);
/* 145 */         ClusRun cr = partitionDataBasic((ClusData)trset, (ClusSelection)msel, summ, i + 1);
/* 146 */         ClusModel model = this.m_Class.induceSingleUnpruned(cr);
/* 147 */         avgSize += model.getModelSize();
/* 148 */         cr.addModelInfo(1).setModel(model);
/* 149 */         cr.addModelInfo(1).setTestError(createTuneError(mgr));
/* 150 */         this.m_Clus.calcError(cr.getTestIter(), 1, cr, null);
/* 151 */         summ.addSummary(cr);
/*     */       } 
/* 153 */       avgSize /= nbfolds;
/* 154 */       System.out.println();
/*     */     } 
/* 156 */     ClusModelInfo mi = summ.getModelInfo(1);
/* 157 */     Settings.enableVerbose(prevVerb);
/* 158 */     ClusError err = mi.getTestError().getFirstError();
/* 159 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(System.out));
/* 160 */     wrt.print("Size: " + avgSize + ", ");
/* 161 */     wrt.print("Error: "); err.showModelError(wrt, 3);
/* 162 */     wrt.flush();
/* 163 */     return err.getModelError();
/*     */   }
/*     */   
/*     */   public void findBestFTest(RowData trset, RowData pruneset) throws ClusException, IOException {
/* 167 */     int best_value = 0;
/* 168 */     boolean low = createTuneError(getStatManager()).getFirstError().shouldBeLow();
/* 169 */     double best_error = low ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
/* 170 */     for (int i = 0; i < this.m_FTests.length; i++) {
/* 171 */       getSettings().setFTest(this.m_FTests[i]);
/* 172 */       System.out.println("Try for F-test value = " + this.m_FTests[i]);
/* 173 */       double err = doParamXVal(trset, pruneset);
/* 174 */       System.out.print("-> " + err);
/* 175 */       if (low) {
/* 176 */         if (err < best_error - 1.0E-16D) {
/* 177 */           best_error = err;
/* 178 */           best_value = i;
/* 179 */           System.out.println(" *");
/*     */         } else {
/* 181 */           System.out.println();
/*     */         }
/*     */       
/* 184 */       } else if (err > best_error + 1.0E-16D) {
/* 185 */         best_error = err;
/* 186 */         best_value = i;
/* 187 */         System.out.println(" *");
/*     */       } else {
/* 189 */         System.out.println();
/*     */       } 
/*     */       
/* 192 */       System.out.println();
/*     */     } 
/* 194 */     getSettings().setFTest(this.m_FTests[best_value]);
/* 195 */     System.out.println("Best F-test value is: " + this.m_FTests[best_value]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/*     */     try {
/* 201 */       RowData valid = (RowData)cr.getPruneSet();
/* 202 */       RowData train = (RowData)cr.getTrainingSet();
/* 203 */       findBestFTest(train, valid);
/* 204 */       System.out.println();
/*     */       
/* 206 */       cr.combineTrainAndValidSets();
/* 207 */       ClusRandom.initialize(getSettings());
/* 208 */       this.m_Class.induceAll(cr);
/* 209 */     } catch (ClusException e) {
/* 210 */       System.err.println("Error: " + e);
/* 211 */     } catch (IOException e) {
/* 212 */       System.err.println("IO Error: " + e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\tdidt\tune\CDTTuneFTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */