/*     */ package addon.hmc.HMCAverageSingleClass;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.HierClassTresholdPruner;
/*     */ import clus.ext.hierarchical.HierClassWiseAccuracy;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.statistic.ClusStatistic;
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
/*     */ public class HMCAverageNodeWiseModels
/*     */ {
/*     */   protected int m_NbModels;
/*     */   protected int m_TotSize;
/*     */   protected HSCActive m_Cls;
/*     */   protected double[][][] m_PredProb;
/*     */   public double[][] m_Variance;
/*     */   
/*     */   public HMCAverageNodeWiseModels(HSCActive cls, double[][][] predprop) {
/*  48 */     this.m_Cls = cls;
/*  49 */     this.m_PredProb = predprop;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNbModels() {
/*  54 */     return this.m_NbModels;
/*     */   }
/*     */   
/*     */   public int getTotalSize() {
/*  58 */     return this.m_TotSize;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  62 */     return this.m_Cls.getStatManager();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  66 */     return this.m_Cls.getSettings();
/*     */   }
/*     */   
/*     */   public Clus getClus() {
/*  70 */     return this.m_Cls.getClus();
/*     */   }
/*     */   
/*     */   public boolean allParentsOk(ClassTerm term, boolean[] computed) {
/*  74 */     for (int j = 0; j < term.getNbParents(); j++) {
/*  75 */       ClassTerm parent = term.getParent(j);
/*  76 */       if (parent.getIndex() != -1 && !computed[parent.getIndex()]) {
/*  77 */         return false;
/*     */       }
/*     */     } 
/*  80 */     return true;
/*     */   }
/*     */   
/*     */   public void processModels(ClusRun cr) throws ClusException, IOException, ClassNotFoundException {
/*  84 */     ClassHierarchy hier = getStatManager().getHier();
/*  85 */     boolean[] prob_computed = new boolean[hier.getTotal()];
/*     */ 
/*     */     
/*  88 */     ArrayList<ClassTerm> todo = new ArrayList();
/*  89 */     for (int i = 0; i < hier.getTotal(); i++) {
/*  90 */       ClassTerm term = hier.getTermAt(i);
/*  91 */       todo.add(term);
/*     */     } 
/*  93 */     int nb_done = 0;
/*  94 */     while (nb_done < hier.getTotal()) {
/*  95 */       for (int j = todo.size() - 1; j >= 0; j--) {
/*  96 */         ClassTerm term = todo.get(j);
/*  97 */         if (allParentsOk(term, prob_computed)) {
/*     */           
/*  99 */           doOneClass(term, cr);
/*     */           
/* 101 */           prob_computed[term.getIndex()] = true;
/* 102 */           todo.remove(j);
/* 103 */           nb_done++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateErrorMeasures(ClusRun cr) throws ClusException, IOException {
/* 110 */     ClassHierarchy hier = getStatManager().getHier();
/* 111 */     HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 112 */     for (int traintest = 0; traintest <= 1; traintest++) {
/* 113 */       RowData data = cr.getDataSet(traintest);
/* 114 */       for (int exid = 0; exid < data.getNbRows(); exid++) {
/* 115 */         DataTuple tuple = data.getTuple(exid);
/* 116 */         ClassesTuple tp = (ClassesTuple)tuple.getObjVal(0);
/* 117 */         for (int clidx = 0; clidx < hier.getTotal(); clidx++) {
/* 118 */           double predicted_weight = this.m_PredProb[traintest][exid][clidx];
/* 119 */           boolean actually_has_class = tp.hasClass(clidx);
/* 120 */           for (int i = 0; i < pruner.getNbResults(); i++) {
/*     */             
/* 122 */             boolean predicted_class = (predicted_weight >= pruner.getThreshold(i) / 100.0D);
/* 123 */             HierClassWiseAccuracy acc = (HierClassWiseAccuracy)this.m_Cls.getEvalArray(traintest, i).getError(0);
/* 124 */             acc.nextPrediction(clidx, predicted_class, actually_has_class);
/*     */           } 
/*     */         } 
/*     */       } 
/* 128 */       for (int j = 0; j < pruner.getNbResults(); j++) {
/* 129 */         ClusErrorList error = this.m_Cls.getEvalArray(traintest, j);
/* 130 */         error.setNbExamples(data.getNbRows(), data.getNbRows());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void doOneClass(ClassTerm term, ClusRun cr) throws IOException, ClassNotFoundException, ClusException {
/* 136 */     String childName = term.toPathString("=");
/* 137 */     for (int j = 0; j < term.getNbParents(); j++) {
/* 138 */       ClassTerm parent = term.getParent(j);
/*     */       
/* 140 */       String nodeName = parent.toPathString("=");
/* 141 */       String name = getSettings().getAppName() + "-" + nodeName + "-" + childName;
/* 142 */       String toload = getSettings().getFileAbsolute(getSettings().getAppNameWithSuffix()) + "/hsc/model/" + name + ".model";
/*     */       
/* 144 */       ClusModelCollectionIO io = ClusModelCollectionIO.load(toload);
/*     */       
/* 146 */       ClusModel model = io.getModel("Original");
/* 147 */       if (model == null) {
/* 148 */         throw new ClusException("Error: .model file does not contain model named 'Original'");
/*     */       }
/*     */       
/* 151 */       this.m_NbModels++;
/* 152 */       this.m_TotSize += model.getModelSize();
/* 153 */       getClus().getSchema().attachModel(model);
/*     */       
/* 155 */       for (int i = 0; i <= 1; i++) {
/* 156 */         RowData data = cr.getDataSet(i);
/* 157 */         ClassHierarchy hier = getStatManager().getHier();
/* 158 */         for (int exid = 0; exid < data.getNbRows(); exid++) {
/* 159 */           updatePrediction(data, exid, i, model, parent, term);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 164 */     int child_idx = term.getIndex();
/* 165 */     for (int traintest = 0; traintest <= 1; traintest++) {
/* 166 */       RowData data = cr.getDataSet(traintest);
/* 167 */       for (int exid = 0; exid < data.getNbRows(); exid++) {
/* 168 */         this.m_PredProb[traintest][exid][child_idx] = this.m_PredProb[traintest][exid][child_idx] / term.getNbParents();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updatePrediction(RowData data, int exid, int traintest, ClusModel model, ClassTerm parent, ClassTerm term) {
/* 174 */     DataTuple tuple = data.getTuple(exid);
/* 175 */     ClusStatistic prediction = model.predictWeighted(tuple);
/* 176 */     double[] predicted_distr = prediction.getNumericPred();
/* 177 */     double predicted_prob = predicted_distr[0];
/* 178 */     int parent_idx = parent.getIndex();
/* 179 */     int child_idx = term.getIndex();
/* 180 */     double parent_prob = (parent_idx == -1) ? 1.0D : this.m_PredProb[traintest][exid][parent_idx];
/* 181 */     double child_prob = parent_prob * predicted_prob;
/* 182 */     if (child_prob < this.m_PredProb[traintest][exid][child_idx])
/* 183 */       this.m_PredProb[traintest][exid][child_idx] = child_prob; 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\hmc\HMCAverageSingleClass\HMCAverageNodeWiseModels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */