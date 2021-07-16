/*     */ package addon.hmc.HMCAverageSingleClass;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
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
/*     */ public class HSCPrediction
/*     */ {
/*     */   protected int m_NbModels;
/*     */   protected int m_TotSize;
/*     */   protected Clus m_Clus;
/*     */   public double[][][] m_PredProb;
/*     */   public double[][] m_;
/*     */   
/*     */   public HSCPrediction(Clus clus, double[][][] predProb) {
/*  44 */     this.m_Clus = clus;
/*  45 */     this.m_PredProb = predProb;
/*     */   }
/*     */   
/*     */   public int getNbModels() {
/*  49 */     return this.m_NbModels;
/*     */   }
/*     */   
/*     */   public int getTotalSize() {
/*  53 */     return this.m_TotSize;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  57 */     return this.m_Clus.getStatManager();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/*  61 */     return this.m_Clus.getSettings();
/*     */   }
/*     */   
/*     */   public Clus getClus() {
/*  65 */     return this.m_Clus;
/*     */   }
/*     */   
/*     */   public boolean allParentsOk(ClassTerm term, boolean[] computed) {
/*  69 */     for (int j = 0; j < term.getNbParents(); j++) {
/*  70 */       ClassTerm parent = term.getParent(j);
/*  71 */       if (parent.getIndex() != -1 && !computed[parent.getIndex()]) {
/*  72 */         return false;
/*     */       }
/*     */     } 
/*  75 */     return true;
/*     */   }
/*     */   
/*     */   public void processModels(Clus clus, ClusModel[] models) throws ClusException, IOException, ClassNotFoundException {
/*  79 */     ClusRun cr = clus.partitionData((ClusData)clus.getData(), null, true, false, clus.getM_Summary(), 1);
/*  80 */     ClassHierarchy hier = getStatManager().getHier();
/*  81 */     boolean[] prob_computed = new boolean[hier.getTotal()];
/*     */     
/*  83 */     ArrayList<ClassTerm> todo = new ArrayList();
/*  84 */     for (int i = 0; i < hier.getTotal(); i++) {
/*  85 */       ClassTerm term = hier.getTermAt(i);
/*  86 */       todo.add(term);
/*     */     } 
/*  88 */     int nb_done = 0;
/*  89 */     while (nb_done < hier.getTotal()) {
/*  90 */       for (int j = todo.size() - 1; j >= 0; j--) {
/*  91 */         ClassTerm term = todo.get(j);
/*  92 */         if (allParentsOk(term, prob_computed)) {
/*     */           
/*  94 */           doOneClass(term, cr, models);
/*     */           
/*  96 */           prob_computed[term.getIndex()] = true;
/*  97 */           todo.remove(j);
/*  98 */           nb_done++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void doOneClass(ClassTerm term, ClusRun cr, ClusModel[] models) throws IOException, ClassNotFoundException, ClusException {
/* 110 */     for (int j = 0; j < term.getNbParents(); j++) {
/* 111 */       ClassTerm parent = term.getParent(j);
/*     */       
/* 113 */       this.m_NbModels++;
/* 114 */       this.m_TotSize += models[term.getIndex()].getModelSize();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 120 */       for (int i = 0; i <= 1; i++) {
/* 121 */         RowData data = cr.getDataSet(i);
/* 122 */         for (int exid = 0; exid < data.getNbRows(); exid++) {
/* 123 */           updatePrediction(data, exid, i, models, parent, term);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 128 */     int child_idx = term.getIndex();
/*     */ 
/*     */ 
/*     */     
/* 132 */     for (int traintest = 0; traintest <= 1; traintest++) {
/* 133 */       RowData data = cr.getDataSet(traintest);
/* 134 */       for (int exid = 0; exid < data.getNbRows(); exid++) {
/* 135 */         this.m_PredProb[traintest][exid][child_idx] = this.m_PredProb[traintest][exid][child_idx] / term.getNbParents();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updatePrediction(RowData data, int exid, int traintest, ClusModel[] models, ClassTerm parent, ClassTerm term) {
/* 141 */     DataTuple tuple = data.getTuple(exid);
/* 142 */     int parent_idx = parent.getIndex();
/* 143 */     int child_idx = term.getIndex();
/* 144 */     ClusModel model = models[child_idx];
/*     */ 
/*     */     
/* 147 */     ClusStatistic prediction = model.predictWeighted(tuple);
/* 148 */     double[] predicted_distr = prediction.getNumericPred();
/* 149 */     double predicted_prob = predicted_distr[0];
/* 150 */     double parent_prob = (parent_idx == -1) ? 1.0D : this.m_PredProb[traintest][exid][parent_idx];
/* 151 */     double child_prob = parent_prob * predicted_prob;
/* 152 */     if (child_prob < this.m_PredProb[traintest][exid][child_idx])
/* 153 */       this.m_PredProb[traintest][exid][child_idx] = child_prob; 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\hmc\HMCAverageSingleClass\HSCPrediction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */