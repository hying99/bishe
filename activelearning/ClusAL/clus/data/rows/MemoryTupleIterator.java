/*    */ package clus.data.rows;
/*    */ 
/*    */ import clus.data.ClusData;
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
/*    */ 
/*    */ public class MemoryTupleIterator
/*    */   extends TupleIterator
/*    */ {
/*    */   protected RowData m_Data;
/*    */   protected int m_Index;
/*    */   
/*    */   public MemoryTupleIterator(RowData data) {
/* 37 */     this.m_Data = data;
/*    */   }
/*    */   
/*    */   public MemoryTupleIterator(RowData data, DataPreprocs procs) {
/* 41 */     super(procs);
/* 42 */     this.m_Data = data;
/*    */   }
/*    */   
/*    */   public void init() throws IOException, ClusException {
/* 46 */     this.m_Index = 0;
/*    */   }
/*    */   
/*    */   public int getNbExamples() {
/* 50 */     return this.m_Data.getNbRows();
/*    */   }
/*    */   
/*    */   public final ClusSchema getSchema() {
/* 54 */     return this.m_Data.getSchema();
/*    */   }
/*    */   
/*    */   public final ClusData getData() {
/* 58 */     return this.m_Data;
/*    */   }
/*    */   
/*    */   public final DataTuple readTuple() throws ClusException {
/* 62 */     if (this.m_Index >= this.m_Data.getNbRows()) return null; 
/* 63 */     DataTuple tuple = this.m_Data.getTuple(this.m_Index++);
/* 64 */     preprocTuple(tuple);
/* 65 */     return tuple;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\rows\MemoryTupleIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */