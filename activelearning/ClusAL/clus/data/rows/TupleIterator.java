/*    */ package clus.data.rows;
/*    */ 
/*    */ import clus.data.ClusData;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ public abstract class TupleIterator
/*    */ {
/*    */   protected DataPreprocs m_Procs;
/*    */   protected boolean m_ShouldAttach;
/*    */   
/*    */   public TupleIterator() {
/* 38 */     this.m_Procs = new DataPreprocs();
/*    */   }
/*    */   
/*    */   public TupleIterator(DataPreprocs procs) {
/* 42 */     this.m_Procs = (procs != null) ? procs : new DataPreprocs();
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract DataTuple readTuple() throws IOException, ClusException;
/*    */ 
/*    */   
/*    */   public abstract ClusSchema getSchema();
/*    */ 
/*    */   
/*    */   public void init() throws IOException, ClusException {}
/*    */   
/*    */   public void close() throws IOException {}
/*    */   
/*    */   public final void preprocTuple(DataTuple tuple) throws ClusException {
/* 57 */     if (tuple != null) this.m_Procs.preprocSingle(tuple); 
/*    */   }
/*    */   
/*    */   public final void setPreprocs(DataPreprocs procs) {
/* 61 */     this.m_Procs = procs;
/*    */   }
/*    */   
/*    */   public final boolean shouldAttach() {
/* 65 */     return this.m_ShouldAttach;
/*    */   }
/*    */   
/*    */   public final void setShouldAttach(boolean attach) {
/* 69 */     this.m_ShouldAttach = attach;
/*    */   }
/*    */   
/*    */   public ClusData getData() {
/* 73 */     return null;
/*    */   }
/*    */   
/*    */   public ClusData createInMemoryData() throws IOException, ClusException {
/* 77 */     init();
/* 78 */     ArrayList<DataTuple> list = new ArrayList();
/* 79 */     DataTuple tuple = readTuple();
/* 80 */     while (tuple != null) {
/* 81 */       list.add(tuple);
/* 82 */       tuple = readTuple();
/*    */     } 
/* 84 */     return new RowData(list, getSchema());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\rows\TupleIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */