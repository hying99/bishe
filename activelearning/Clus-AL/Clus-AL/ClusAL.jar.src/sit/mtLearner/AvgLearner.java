/*    */ package sit.mtLearner;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import sit.TargetSet;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AvgLearner
/*    */   extends MTLearnerImpl
/*    */ {
/*    */   protected RowData[] LearnModel(TargetSet targets, RowData train, RowData test) {
/* 35 */     ClusSchema schema = this.m_Data.getSchema();
/*    */     
/* 37 */     NumericAttrType[] num = schema.getNumericAttrUse(3);
/*    */ 
/*    */     
/* 40 */     DataTuple result = new DataTuple(schema);
/*    */     
/* 42 */     for (int i = 0; i < train.getNbRows(); i++) {
/*    */       
/* 44 */       DataTuple tuple = train.getTuple(i);
/*    */       
/* 46 */       for (int m = 0; m < num.length; m++) {
/* 47 */         double d = num[m].getNumeric(tuple);
/* 48 */         double temp = num[m].getNumeric(result) + d;
/* 49 */         num[m].setNumeric(result, temp);
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 54 */     for (int j = 0; j < num.length; j++) {
/* 55 */       double temp = num[j].getNumeric(result);
/* 56 */       num[j].setNumeric(result, temp / train.getNbRows());
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 61 */     RowData predictions = new RowData(schema, test.getNbRows());
/* 62 */     for (int k = 0; k < test.getNbRows(); k++) {
/* 63 */       predictions.setTuple(result, k);
/*    */     }
/*    */     
/* 66 */     RowData[] final_result = { test, predictions };
/* 67 */     return final_result;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 71 */     return "AvgLearner";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\mtLearner\AvgLearner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */