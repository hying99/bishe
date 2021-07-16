/*    */ package jeans.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FilenameFilter;
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
/*    */ public class ExtensionFilter
/*    */   implements FilenameFilter
/*    */ {
/*    */   private String extension;
/*    */   
/*    */   public ExtensionFilter(String extension) {
/* 41 */     this.extension = extension;
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
/*    */   public boolean accept(File dir, String name) {
/* 53 */     String fileext = FileUtil.getExtension(name);
/* 54 */     return (fileext != null && fileext.equals(this.extension));
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\ExtensionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */