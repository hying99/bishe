/*    */ package org.jgap.distr.grid.common;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.jgap.util.FileKit;
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
/*    */ public abstract class CommonConfiguration
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/* 28 */   private transient Logger LOGGER = Logger.getLogger(CommonConfiguration.class);
/*    */ 
/*    */ 
/*    */   
/*    */   private String m_workDir;
/*    */ 
/*    */ 
/*    */   
/*    */   private String m_libDir;
/*    */ 
/*    */ 
/*    */   
/*    */   private String m_server;
/*    */ 
/*    */ 
/*    */   
/*    */   public CommonConfiguration() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public CommonConfiguration(String a_workDir, String a_libDir) {
/* 49 */     this();
/* 50 */     setWorkDir(a_workDir);
/* 51 */     setLibDir(a_libDir);
/*    */   }
/*    */   
/*    */   public void setWorkDir(String a_workDir) {
/*    */     try {
/* 56 */       this.m_workDir = FileKit.addSubDir(FileKit.getCurrentDir(), a_workDir, true);
/* 57 */       this.LOGGER.info("Using work directory " + this.m_workDir);
/* 58 */       if (!FileKit.directoryExists(this.m_workDir)) {
/* 59 */         this.LOGGER.info("  Directory does not exist yet. Wil create it.");
/* 60 */         FileKit.createDirectory(this.m_workDir);
/*    */       }
/*    */     
/*    */     }
/* 64 */     catch (IOException iex) {
/* 65 */       throw new RuntimeException("Work directory " + a_workDir + " is invalid!");
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getWorkDir() {
/* 70 */     return this.m_workDir;
/*    */   }
/*    */   
/*    */   public void setLibDir(String a_libDir) {
/*    */     try {
/* 75 */       this.m_libDir = FileKit.addSubDir(FileKit.getCurrentDir(), a_libDir, true);
/* 76 */       this.LOGGER.info("Using lib directory " + this.m_libDir);
/* 77 */       if (!FileKit.directoryExists(this.m_libDir)) {
/* 78 */         this.LOGGER.info("  Directory does not exist yet. Wil create it.");
/* 79 */         FileKit.createDirectory(this.m_libDir);
/*    */       }
/*    */     
/*    */     }
/* 83 */     catch (IOException iex) {
/* 84 */       throw new RuntimeException("Lib directory " + a_libDir + " is invalid!");
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getLibDir() {
/* 89 */     return this.m_libDir;
/*    */   }
/*    */   
/*    */   public void setServerAddress(String a_server) {
/* 93 */     this.m_server = a_server;
/*    */   }
/*    */   
/*    */   public String getServerAddress() {
/* 97 */     return this.m_server;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\common\CommonConfiguration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */