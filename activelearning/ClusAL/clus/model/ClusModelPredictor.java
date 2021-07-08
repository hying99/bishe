/*    */ package clus.model;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.util.ClusException;
/*    */ 
/*    */ public class ClusModelPredictor
/*    */ {
/*    */   public static RowData predict(ClusModel model, RowData test) throws ClusException {
/* 12 */     ClusSchema schema = test.getSchema();
/* 13 */     schema.attachModel(model);
/* 14 */     RowData predictions = new RowData(schema, test.getNbRows());
/* 15 */     for (int i = 0; i < test.getNbRows(); i++) {
/* 16 */       DataTuple prediction = new DataTuple(schema);
/* 17 */       ClusStatistic stat = model.predictWeighted(test.getTuple(i));
/* 18 */       stat.predictTuple(prediction);
/* 19 */       predictions.setTuple(prediction, i);
/*    */     } 
/* 21 */     return predictions;
/*    */   }
/*    */   
/*    */   public static DataTuple predict(ClusModel model, DataTuple test) throws ClusException {
/* 25 */     ClusSchema schema = test.getSchema();
/* 26 */     schema.attachModel(model);
/* 27 */     DataTuple prediction = new DataTuple(schema);
/* 28 */     ClusStatistic stat = model.predictWeighted(test);
/* 29 */     stat.predictTuple(prediction);
/* 30 */     return prediction;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\ClusModelPredictor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */