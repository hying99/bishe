/*     */ package clus.algo.kNN;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KNNModel
/*     */   implements ClusModel
/*     */ {
/*     */   public ClusStatManager m_SMgr;
/*     */   private RowData $trainingData;
/*     */   private VectorDistance $vectDist;
/*     */   
/*     */   public KNNModel(ClusRun clRun, VectorDistance vd) {
/*  55 */     this.$trainingData = (RowData)clRun.getTrainingSet();
/*  56 */     this.m_SMgr = clRun.getStatManager();
/*  57 */     this.$vectDist = vd;
/*     */   }
/*     */   
/*     */   public KNNModel(RowData allData, ClusStatManager statManager, VectorDistance vd) {
/*  61 */     this.$trainingData = allData;
/*  62 */     this.m_SMgr = statManager;
/*  63 */     this.$vectDist = vd;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModelInfo() {
/*  68 */     return "KNNModel";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  76 */     ClusStatistic stat = this.m_SMgr.createClusteringStat();
/*     */ 
/*     */     
/*  79 */     int amountNBS = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     PriorityQueue q = new PriorityQueue(amountNBS);
/*     */     
/*  86 */     int nbr = this.$trainingData.getNbRows();
/*     */ 
/*     */ 
/*     */     
/*  90 */     for (int i = 0; i < nbr; i++) {
/*  91 */       DataTuple curTup = this.$trainingData.getTuple(i);
/*  92 */       double dist = calcDistance(tuple, curTup);
/*  93 */       q.addElement(curTup, dist);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  98 */     double weight = 1.0D;
/*  99 */     for (int j = 0; j < amountNBS; j++) {
/* 100 */       DataTuple data = (DataTuple)q.getElement(j);
/* 101 */       stat.updateWeighted(data, weight);
/*     */     } 
/* 103 */     stat.calcMean();
/* 104 */     return stat;
/*     */   }
/*     */ 
/*     */   
/*     */   public LinkedList<DataTuple> sortNeighboursByDistance(DataTuple tuple) throws ClusException, IOException, ClassNotFoundException {
/* 109 */     int amountNBS = this.$trainingData.getNbRows();
/*     */     
/* 111 */     PriorityQueue q = new PriorityQueue(amountNBS);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 116 */     for (int i = 0; i < amountNBS; i++) {
/* 117 */       DataTuple curTup = this.$trainingData.getTuple(i);
/* 118 */       double dist = calcDistance(tuple, curTup);
/* 119 */       q.addElement(curTup, dist);
/*     */     } 
/* 121 */     LinkedList<DataTuple> sortedNeighbours = new LinkedList<>();
/*     */     
/* 123 */     for (int j = 1; j < q.getSize(); j++) {
/* 124 */       sortedNeighbours.add((DataTuple)q.getElement(j));
/*     */     }
/* 126 */     return sortedNeighbours;
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
/*     */   public DataTuple infer(DataTuple tuple, String label) {
/* 215 */     ClusStatistic stat = this.m_SMgr.createClusteringStat();
/*     */ 
/*     */     
/* 218 */     int amountNBS = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 223 */     PriorityQueue q = new PriorityQueue(amountNBS);
/*     */     
/* 225 */     int nbr = this.$trainingData.getNbRows();
/*     */ 
/*     */ 
/*     */     
/* 229 */     int a = 0;
/* 230 */     for (int i = 0; i < nbr; i++) {
/* 231 */       DataTuple curTup = this.$trainingData.getTuple(i);
/* 232 */       double dist = calcDistance(tuple, curTup);
/* 233 */       if (curTup.getOracleAnswer() == null || curTup.getOracleAnswer().queriedBefore(label)) {
/* 234 */         q.addElement(curTup, dist);
/*     */       }
/*     */     } 
/* 237 */     if (q.getSize() == 1) {
/* 238 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 243 */     double weight = 1.0D;
/* 244 */     stat.updateWeighted((DataTuple)q.getElement(q.getSize() - 1), weight);
/* 245 */     DataTuple predictedTuple = (DataTuple)q.getElement(q.getSize() - 1);
/*     */     
/* 247 */     return predictedTuple;
/*     */   }
/*     */ 
/*     */   
/*     */   private double calcDistance(DataTuple t1, DataTuple t2) {
/* 252 */     return this.$vectDist.getDistance(t1, t2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {}
/*     */ 
/*     */   
/*     */   public int getModelSize() {
/* 261 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt) {}
/*     */ 
/*     */   
/*     */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {}
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {}
/*     */ 
/*     */   
/*     */   public void printModelToPythonScript(PrintWriter wrt) {}
/*     */ 
/*     */   
/*     */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int starttree, int startitem, boolean ex) {}
/*     */   
/*     */   public void attachModel(HashMap table) {
/* 280 */     System.err.println(getClass().getName() + "attachModel() not implemented");
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
/*     */ 
/*     */   
/*     */   public int getID() {
/* 300 */     return 0;
/*     */   }
/*     */   
/*     */   public ClusModel prune(int prunetype) {
/* 304 */     return this;
/*     */   }
/*     */   
/*     */   public void retrieveStatistics(ArrayList list) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\KNNModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */