/*    */ package sit.mtLearner;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.algo.ClusInductionAlgorithmType;
/*    */ import clus.algo.tdidt.ClusDecisionTree;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.main.ClusRun;
/*    */ import clus.main.Settings;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.ClusModelPredictor;
/*    */ import java.util.Iterator;
/*    */ import sit.TargetSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusLearner
/*    */   extends MTLearnerImpl
/*    */ {
/*    */   protected Clus m_Clus;
/*    */   protected ClusSchema m_Schema;
/*    */   
/*    */   public void init(RowData data, Settings sett) {
/* 26 */     this.m_Schema = data.getSchema().cloneSchema();
/* 27 */     RowData mydata = new RowData(data);
/* 28 */     mydata.setSchema(this.m_Schema);
/* 29 */     super.init(mydata, sett);
/* 30 */     this.m_Clus = new Clus();
/* 31 */     ClusDecisionTree clusDecisionTree = new ClusDecisionTree(this.m_Clus);
/*    */     try {
/* 33 */       this.m_Clus.initialize(mydata, this.m_Schema, sett, (ClusInductionAlgorithmType)clusDecisionTree);
/* 34 */     } catch (Exception e1) {
/* 35 */       e1.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   protected RowData[] LearnModel(TargetSet targets, RowData train, RowData test) {
/*    */     try {
/* 41 */       ClusSchema schema = this.m_Clus.getSchema();
/* 42 */       schema.clearAttributeStatusClusteringAndTarget();
/* 43 */       Iterator<ClusAttrType> targetIterator = targets.iterator();
/* 44 */       while (targetIterator.hasNext()) {
/* 45 */         ClusAttrType attr = targetIterator.next();
/* 46 */         ClusAttrType clusAttr = schema.getAttrType(attr.getIndex());
/* 47 */         clusAttr.setStatus(1);
/* 48 */         clusAttr.setClustering(true);
/*    */       } 
/*    */ 
/*    */       
/* 52 */       schema.addIndices(0);
/*    */ 
/*    */       
/* 55 */       for (int i = 0; i < train.getNbRows(); i++) {
/* 56 */         DataTuple tr = train.getTuple(i);
/*    */ 
/*    */         
/* 59 */         tr.setWeight(0.0D);
/* 60 */         for (int j = 0; j < test.getNbRows(); j++) {
/* 61 */           DataTuple te = test.getTuple(j);
/* 62 */           tr.setWeight(tr.getWeight() + 1.0D / (1.0D + Math.pow(te.euclDistance(tr), 1.0D)));
/*    */         } 
/*    */         
/* 65 */         tr.setWeight(tr.getWeight() / test.getNbRows());
/*    */       } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 72 */       ClusRun cr = this.m_Clus.train(train);
/* 73 */       ClusModel pruned = cr.getModel(2);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 79 */       RowData predictions = ClusModelPredictor.predict(pruned, test);
/* 80 */       RowData[] final_result = { test, predictions };
/* 81 */       return final_result;
/* 82 */     } catch (Exception e1) {
/* 83 */       e1.printStackTrace();
/*    */       
/* 85 */       return null;
/*    */     } 
/*    */   }
/*    */   public String getName() {
/* 89 */     return "ClusLearner";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\mtLearner\ClusLearner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */