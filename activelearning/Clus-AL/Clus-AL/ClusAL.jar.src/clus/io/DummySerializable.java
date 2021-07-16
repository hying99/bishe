/*    */ package clus.io;
/*    */ 
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.rows.DataTuple;
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
/*    */ public class DummySerializable
/*    */   extends ClusSerializable
/*    */ {
/*    */   public boolean read(ClusReader data, int row) throws IOException {
/* 33 */     return data.skipTillComma();
/*    */   }
/*    */   
/*    */   public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 37 */     return data.skipTillComma();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\io\DummySerializable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */