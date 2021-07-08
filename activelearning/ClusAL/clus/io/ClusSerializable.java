/*    */ package clus.io;
/*    */ 
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusSchema;
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
/*    */ 
/*    */ 
/*    */ public abstract class ClusSerializable
/*    */ {
/*    */   public void term(ClusSchema schema) {}
/*    */   
/*    */   public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 37 */     throw new IOException("Attribute does not support tuple wise reading");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\io\ClusSerializable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */