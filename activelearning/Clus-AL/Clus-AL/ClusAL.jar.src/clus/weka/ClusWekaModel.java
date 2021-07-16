/*     */ package clus.weka;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import jeans.io.ObjectSaveStream;
/*     */ import jeans.util.MyArray;
/*     */ import weka.classifiers.Classifier;
/*     */ import weka.core.Instance;
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
/*     */ public class ClusWekaModel
/*     */   implements ClusModel
/*     */ {
/*     */   Classifier m_Classifier;
/*     */   ClusWekaClassifier m_Parent;
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  47 */     Instance weka_tuple = this.m_Parent.m_Data.convertInstance(tuple);
/*  48 */     Instance classMissing = (Instance)weka_tuple.copy();
/*  49 */     classMissing.setDataset(this.m_Parent.getDummyData());
/*  50 */     classMissing.setClassMissing();
/*     */     try {
/*  52 */       double[] dist = this.m_Classifier.distributionForInstance(classMissing);
/*  53 */       ClassificationStat stat = (ClassificationStat)this.m_Parent.createStatistic();
/*  54 */       stat.initSingleTargetFrom(dist);
/*  55 */       stat.calcMean();
/*  56 */       return (ClusStatistic)stat;
/*  57 */     } catch (Exception e) {
/*  58 */       System.out.println("Weka Error: " + e.getClass().getName() + ": " + e.getMessage());
/*     */       
/*  60 */       return null;
/*     */     } 
/*     */   }
/*     */   public void setParent(ClusWekaClassifier parent) {
/*  64 */     this.m_Parent = parent;
/*     */   }
/*     */   
/*     */   public void setClassifier(Classifier classifier) {
/*  68 */     this.m_Classifier = classifier;
/*     */   }
/*     */ 
/*     */   
/*     */   public void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {}
/*     */   
/*     */   public int getModelSize() {
/*  75 */     return 0;
/*     */   }
/*     */   
/*     */   public String getModelInfo() {
/*  79 */     return "Weka Model";
/*     */   }
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {}
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt) {}
/*     */ 
/*     */   
/*     */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {}
/*     */ 
/*     */   
/*     */   public void printModelToPythonScript(PrintWriter wrt) {}
/*     */ 
/*     */   
/*     */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int starttree, int startitem, boolean ex) {}
/*     */   
/*     */   public void saveModel(ObjectSaveStream strm) throws IOException {}
/*     */   
/*     */   public void attachModel(HashMap table) {
/* 100 */     System.err.println(getClass().getName() + "attachModel() not implemented");
/*     */   }
/*     */   
/*     */   public int getID() {
/* 104 */     return 0;
/*     */   }
/*     */   
/*     */   public ClusModel prune(int prunetype) {
/* 108 */     return this;
/*     */   }
/*     */   
/*     */   public void retrieveStatistics(ArrayList list) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\weka\ClusWekaModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */