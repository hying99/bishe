/*    */ package clus.data.rows;
/*    */ 
/*    */ import clus.data.io.ARFFFile;
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.data.type.ClusSchemaInitializer;
/*    */ import clus.main.Settings;
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
/*    */ public class DiskTupleIterator
/*    */   extends FileTupleIterator
/*    */ {
/*    */   protected String m_File;
/*    */   protected ClusSchemaInitializer m_Init;
/*    */   protected Settings m_Sett;
/*    */   
/*    */   public DiskTupleIterator(String file, ClusSchemaInitializer init, Settings sett) {
/* 39 */     this(file, init, (DataPreprocs)null, sett);
/*    */   }
/*    */   
/*    */   public DiskTupleIterator(String file, ClusSchemaInitializer init, DataPreprocs procs, Settings sett) {
/* 43 */     super(procs);
/* 44 */     this.m_File = file;
/* 45 */     this.m_Init = init;
/* 46 */     this.m_Sett = sett;
/*    */   }
/*    */ 
/*    */   
/*    */   public void init() throws IOException, ClusException {
/* 51 */     this.m_Reader = new ClusReader(this.m_File, this.m_Sett);
/*    */     
/* 53 */     ARFFFile arff = new ARFFFile(this.m_Reader);
/* 54 */     ClusSchema schema = arff.read(this.m_Sett);
/*    */     
/* 56 */     if (this.m_Init != null) {
/* 57 */       this.m_Init.initSchema(schema);
/*    */     }
/* 59 */     schema.addIndices(0);
/* 60 */     this.m_Data = new RowData(schema);
/* 61 */     super.init();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\rows\DiskTupleIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */