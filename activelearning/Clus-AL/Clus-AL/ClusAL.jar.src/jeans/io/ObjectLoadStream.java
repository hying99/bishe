/*    */ package jeans.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.util.zip.ZipInputStream;
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
/*    */ public class ObjectLoadStream
/*    */   extends ObjectInputStream
/*    */ {
/*    */   public ObjectLoadStream(InputStream file) throws IOException {
/* 52 */     super(makeStream(file));
/*    */   }
/*    */   
/*    */   private static ZipInputStream makeStream(InputStream file) throws IOException {
/* 56 */     ZipInputStream zip = new ZipInputStream(file);
/* 57 */     zip.getNextEntry();
/* 58 */     return zip;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ObjectLoadStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */