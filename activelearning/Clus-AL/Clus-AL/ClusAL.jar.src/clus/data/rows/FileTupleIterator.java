/*    */ package clus.data.rows;
/*    */ 
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.io.ClusView;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
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
/*    */ public class FileTupleIterator
/*    */   extends TupleIterator
/*    */ {
/*    */   protected ClusReader m_Reader;
/*    */   protected RowData m_Data;
/*    */   protected ClusView m_View;
/*    */   
/*    */   public FileTupleIterator(DataPreprocs preproc) {
/* 38 */     super(preproc);
/*    */   }
/*    */   
/*    */   public FileTupleIterator(ClusSchema schema, ClusReader reader) throws ClusException {
/* 42 */     this.m_Data = new RowData(schema);
/* 43 */     this.m_Reader = reader;
/*    */   }
/*    */   
/*    */   public FileTupleIterator(ClusSchema schema, ClusReader reader, DataPreprocs procs) throws ClusException {
/* 47 */     super(procs);
/* 48 */     this.m_Data = new RowData(schema);
/* 49 */     this.m_Reader = reader;
/*    */   }
/*    */   
/*    */   public final ClusSchema getSchema() {
/* 53 */     return this.m_Data.getSchema();
/*    */   }
/*    */   
/*    */   public void init() throws IOException, ClusException {
/* 57 */     ClusSchema schema = getSchema();
/* 58 */     this.m_View = schema.createNormalView();
/* 59 */     schema.setReader(true);
/*    */   }
/*    */ 
/*    */   
/*    */   public final DataTuple readTuple() throws IOException, ClusException {
/* 64 */     DataTuple tuple = this.m_View.readDataTuple(this.m_Reader, this.m_Data.getSchema());
/* 65 */     preprocTuple(tuple);
/* 66 */     return tuple;
/*    */   }
/*    */   
/*    */   public final void close() throws IOException {
/* 70 */     ClusSchema schema = this.m_Data.getSchema();
/* 71 */     schema.setReader(false);
/* 72 */     this.m_Reader.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\rows\FileTupleIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */