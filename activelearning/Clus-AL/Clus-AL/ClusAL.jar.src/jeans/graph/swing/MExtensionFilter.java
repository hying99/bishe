/*    */ package jeans.graph.swing;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import javax.swing.filechooser.FileFilter;
/*    */ import jeans.util.FileUtil;
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
/*    */ public class MExtensionFilter
/*    */   extends FileFilter
/*    */ {
/*    */   protected String m_Descr;
/* 34 */   protected ArrayList m_List = new ArrayList();
/*    */   
/*    */   public MExtensionFilter(String descr) {
/* 37 */     this.m_Descr = descr;
/*    */   }
/*    */   
/*    */   public void addExtension(String ext) {
/* 41 */     this.m_List.add(ext);
/*    */   }
/*    */   
/*    */   public boolean accept(File f) {
/* 45 */     if (f.isDirectory()) return true; 
/* 46 */     String extension = FileUtil.getExtension(f);
/* 47 */     if (extension != null) {
/* 48 */       extension = extension.toLowerCase();
/* 49 */       for (int i = 0; i < this.m_List.size(); i++) {
/* 50 */         String crext = this.m_List.get(i);
/* 51 */         if (extension.equals(crext)) return true; 
/*    */       } 
/*    */     } 
/* 54 */     return false;
/*    */   }
/*    */   
/*    */   public String getDescription() {
/* 58 */     return this.m_Descr;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\MExtensionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */