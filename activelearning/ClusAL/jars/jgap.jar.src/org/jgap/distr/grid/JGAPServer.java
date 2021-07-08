/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import org.apache.commons.cli.CommandLine;
/*    */ import org.apache.commons.cli.Options;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.homedns.dade.jcgrid.cmd.MainCmd;
/*    */ import org.homedns.dade.jcgrid.server.GridServer;
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
/*    */ public class JGAPServer
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/* 30 */   private static final String className = JGAPServer.class.getName();
/*    */   
/* 32 */   private static Logger log = Logger.getLogger(className);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 38 */   private GridServer m_gs = new GridServer(JGAPClientHandlerThread.class); public JGAPServer(String[] args) throws Exception {
/* 39 */     Options options = new Options();
/* 40 */     CommandLine cmd = MainCmd.parseCommonOptions(options, this.m_gs.getNodeConfig(), args);
/*    */ 
/*    */ 
/*    */     
/* 44 */     this.m_gs.start();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addFile(String a_filename) throws Exception {
/* 50 */     String path = this.m_gs.getVFSSessionPool().getPath();
/* 51 */     if (path == null) {
/*    */       return;
/*    */     }
/* 54 */     if (path.charAt(path.length() - 1) != '\\') {
/* 55 */       path = path + "\\";
/*    */     }
/* 57 */     copyFile(a_filename, path);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void copyFile(String source, String dest) throws Exception {
/* 62 */     File destFile = new File(dest);
/* 63 */     if (!destFile.isFile()) {
/* 64 */       String origFilename = (new File(source)).getName();
/* 65 */       dest = dest + origFilename;
/*    */     } 
/*    */     
/* 68 */     File inputFile = new File(source);
/* 69 */     File outputFile = new File(dest);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 76 */     FileInputStream in = new FileInputStream(inputFile);
/* 77 */     FileOutputStream out = new FileOutputStream(outputFile);
/*    */     
/*    */     int c;
/*    */     
/* 81 */     while ((c = in.read()) != -1) {
/* 82 */       out.write(c);
/*    */     }
/* 84 */     in.close();
/* 85 */     out.close();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 91 */     MainCmd.setUpLog4J("server", true);
/*    */ 
/*    */     
/* 94 */     new JGAPServer(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPServer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */