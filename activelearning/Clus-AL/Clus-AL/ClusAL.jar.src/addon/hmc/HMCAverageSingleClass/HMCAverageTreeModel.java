/*     */ package addon.hmc.HMCAverageSingleClass;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import jeans.util.MyArray;
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
/*     */ public class HMCAverageTreeModel
/*     */   implements ClusModel
/*     */ {
/*     */   protected int m_DataSet;
/*     */   protected int m_Trees;
/*     */   protected int m_TotSize;
/*     */   protected WHTDStatistic m_Target;
/*     */   protected double[][][] m_PredProb;
/*     */   
/*     */   public HMCAverageTreeModel(ClusStatistic target, double[][][] predprop, int trees, int size) {
/*  47 */     this.m_Target = (WHTDStatistic)target;
/*  48 */     this.m_PredProb = predprop;
/*  49 */     this.m_Trees = trees;
/*  50 */     this.m_TotSize = size;
/*     */   }
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  54 */     WHTDStatistic stat = (WHTDStatistic)this.m_Target.cloneSimple();
/*  55 */     stat.setMeans(this.m_PredProb[this.m_DataSet][tuple.getIndex()]);
/*  56 */     return (ClusStatistic)stat;
/*     */   }
/*     */ 
/*     */   
/*     */   public void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {}
/*     */   
/*     */   public int getModelSize() {
/*  63 */     return 0;
/*     */   }
/*     */   
/*     */   public String getModelInfo() {
/*  67 */     return "Combined model with " + this.m_Trees + " trees with " + this.m_TotSize + " nodes";
/*     */   }
/*     */   
/*     */   public void printModel(PrintWriter wrt) {
/*  71 */     wrt.println(getModelInfo());
/*     */   }
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/*  75 */     printModel(wrt);
/*     */   }
/*     */   
/*     */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {
/*  79 */     printModel(wrt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void printModelToPythonScript(PrintWriter wrt) {}
/*     */ 
/*     */   
/*     */   public void attachModel(HashMap table) throws ClusException {}
/*     */   
/*     */   public ClusModel prune(int prunetype) {
/*  89 */     return this;
/*     */   }
/*     */   
/*     */   public int getID() {
/*  93 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void retrieveStatistics(ArrayList stats) {}
/*     */ 
/*     */   
/*     */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int a, int b, boolean ex) {}
/*     */   
/*     */   public void setDataSet(int set) {
/* 103 */     this.m_DataSet = set;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\hmc\HMCAverageSingleClass\HMCAverageTreeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */