/*    */ package clus.ext.ensembles;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.ext.hierarchical.WHTDStatistic;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.model.ClusModel;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.RegressionStat;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Comparator;
/*    */ import jeans.util.array.MDoubleArrayComparator;
/*    */ 
/*    */ 
/*    */ public class ClusBoostingForest
/*    */   extends ClusForest
/*    */ {
/* 18 */   protected ArrayList m_BetaI = new ArrayList();
/* 19 */   protected transient MDoubleArrayComparator m_Compare = new MDoubleArrayComparator(0);
/*    */ 
/*    */   
/*    */   public ClusBoostingForest() {}
/*    */ 
/*    */   
/*    */   public ClusBoostingForest(ClusStatManager statmgr) {
/* 26 */     super(statmgr);
/*    */   }
/*    */   
/*    */   public void addModelToForest(ClusModel model, double beta) {
/* 30 */     addModelToForest(model);
/* 31 */     this.m_BetaI.add(new Double(beta));
/*    */   }
/*    */   
/*    */   public double getBetaI(int i) {
/* 35 */     return ((Double)this.m_BetaI.get(i)).doubleValue();
/*    */   }
/*    */   
/*    */   public double getMedianThreshold() {
/* 39 */     double sum = 0.0D;
/* 40 */     for (int i = 0; i < this.m_BetaI.size(); i++) {
/* 41 */       sum += Math.log(1.0D / getBetaI(i));
/*    */     }
/* 43 */     return 0.5D * sum;
/*    */   }
/*    */   
/*    */   public ClusStatistic predictWeighted(DataTuple tuple) {
/* 47 */     ClusStatistic predicted = this.m_Stat.cloneSimple();
/*    */     
/* 49 */     for (int i = 0; i < getNbModels(); i++) {
/* 50 */       predicted.addPrediction(getModel(i).predictWeighted(tuple), 1.0D / getNbModels());
/*    */     }
/* 52 */     predicted.computePrediction();
/* 53 */     return predicted;
/*    */   }
/*    */   
/*    */   public void predictWeightedRegression(RegressionStat predicted, DataTuple tuple) {
/* 57 */     double[] result = predicted.getNumericPred();
/* 58 */     double[][] treePredictions = new double[getNbModels()][];
/* 59 */     for (int i = 0; i < treePredictions.length; i++) {
/* 60 */       RegressionStat pred = (RegressionStat)getModel(i).predictWeighted(tuple);
/* 61 */       treePredictions[i] = pred.getNumericPred();
/*    */     } 
/* 63 */     double medianThr = getMedianThreshold();
/* 64 */     double[][] preds = new double[getNbModels()][2];
/* 65 */     int nbAttr = predicted.getNbAttributes();
/* 66 */     for (int j = 0; j < nbAttr; j++) {
/*    */       int k;
/*    */       
/* 69 */       for (k = 0; k < getNbModels(); k++) {
/* 70 */         preds[k][0] = treePredictions[k][j];
/* 71 */         preds[k][1] = Math.log(1.0D / getBetaI(k));
/*    */       } 
/* 73 */       Arrays.sort(preds, (Comparator<? super double>)this.m_Compare);
/* 74 */       k = 0;
/* 75 */       double sum = 0.0D;
/*    */       while (true) {
/* 77 */         sum += preds[k][1];
/* 78 */         if (sum >= medianThr)
/* 79 */           break;  k++;
/*    */       } 
/* 81 */       result[j] = preds[k][0];
/*    */     } 
/*    */   }
/*    */   
/*    */   public ClusBoostingForest cloneBoostingForestWithThreshold(double threshold) {
/* 86 */     ClusBoostingForest clone = new ClusBoostingForest();
/* 87 */     clone.setModels(getModels());
/* 88 */     clone.m_BetaI = this.m_BetaI;
/* 89 */     WHTDStatistic stat = (WHTDStatistic)getStat().cloneStat();
/* 90 */     stat.copyAll(getStat());
/* 91 */     stat.setThreshold(threshold);
/* 92 */     clone.setStat((ClusStatistic)stat);
/* 93 */     return clone;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ensembles\ClusBoostingForest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */