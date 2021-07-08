/*    */ package clus.activelearning.algoHMC;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.indexing.TupleIndex;
/*    */ import clus.activelearning.matrix.MatrixUtils;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.main.ClusRun;
/*    */ import clus.util.ClusException;
/*    */ import java.util.HashMap;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UncertaintyVarianceCostHMC
/*    */   extends UncertaintyVarianceHMC
/*    */ {
/*    */   public UncertaintyVarianceCostHMC(Clus clus, String name) throws ClusException, Exception {
/* 26 */     super(clus, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData samplePartialLabelling(ClusRun clusRun) throws ClusException {
/* 31 */     double[][] uncertainty = getLabelsEntropyUncertainty(clusRun);
/* 32 */     double[][] variance = getLabelsVariance(clusRun);
/* 33 */     double[][] uncvar = MatrixUtils.subtractMatrix(variance, uncertainty);
/* 34 */     double[][] labelFrequency = getLabelFrequencyCost(getUnlabeledData());
/* 35 */     uncvar = MatrixUtils.addMatrix(uncvar, labelFrequency);
/* 36 */     LinkedList<LabelIndex> labelIndexer = buildLabelMaxIndexer(uncvar);
/*    */     
/* 38 */     return getPartialLabelledDataset(labelIndexer);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleWithoutPartialLabelling(ClusRun clusRun) throws ClusException {
/* 43 */     double[] uncertainty = getTuplesEntropyUncertainty(clusRun);
/* 44 */     double[] variance = getTuplesVariance(clusRun);
/* 45 */     double[] uncVar = MatrixUtils.addVector(variance, uncertainty);
/* 46 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncVar);
/* 47 */     return getLabeledDataset(indexer);
/*    */   }
/*    */   
/*    */   protected double[][] getLabelFrequencyCost(RowData rowData) {
/* 51 */     String[] labels = getLabels();
/* 52 */     double[] frequencyCost = new double[labels.length];
/* 53 */     HashMap<String, Integer> labelQueryCounter = getLabelQueryCounter();
/*    */     
/* 55 */     for (int i = 0; i < labels.length; i++) {
/* 56 */       frequencyCost[i] = 1.0D / (1.0D + Math.exp(((Integer)labelQueryCounter.get(getLabels()[i])).intValue()));
/*    */     }
/*    */ 
/*    */     
/* 60 */     double[][] labelFrequencyCost = new double[rowData.getNbRows()][labels.length];
/* 61 */     for (int j = 0; j < rowData.getNbRows(); j++) {
/* 62 */       System.arraycopy(frequencyCost, 0, labelFrequencyCost[j], 0, labels.length);
/*    */     }
/* 64 */     return labelFrequencyCost;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHMC\UncertaintyVarianceCostHMC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */