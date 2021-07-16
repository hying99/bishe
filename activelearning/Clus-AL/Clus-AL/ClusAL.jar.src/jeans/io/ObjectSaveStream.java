/*    */ package jeans.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipOutputStream;
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
/*    */ public class ObjectSaveStream
/*    */ {
/*    */   private ZipOutputStream zip;
/*    */   private ObjectOutputStream stream;
/*    */   
/*    */   public ObjectSaveStream(OutputStream file) throws IOException {
/* 54 */     this.zip = new ZipOutputStream(file);
/* 55 */     ZipEntry entry = new ZipEntry("data");
/* 56 */     entry.setMethod(8);
/* 57 */     this.zip.putNextEntry(entry);
/* 58 */     this.stream = new ObjectOutputStream(this.zip);
/*    */   }
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
/*    */   public void close() throws IOException {
/* 72 */     this.zip.closeEntry();
/* 73 */     this.stream.close();
/*    */   }
/*    */   
/*    */   public void closeEntry() throws IOException {
/* 77 */     this.zip.closeEntry();
/*    */   }
/*    */   
/*    */   public void writeObject(Object object) throws IOException {
/* 81 */     this.stream.writeObject(object);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ObjectSaveStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */