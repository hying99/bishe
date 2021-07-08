/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.Socket;
/*    */ import org.homedns.dade.jcgrid.message.GridMessage;
/*    */ import org.homedns.dade.jcgrid.message.GridMessageVFSSessionFileRequest;
/*    */ import org.homedns.dade.jcgrid.message.GridMessageVFSSessionFileResult;
/*    */ import org.homedns.dade.jcgrid.server.ClientHandlerThread;
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
/*    */ public class JGAPClientHandlerThread
/*    */   extends ClientHandlerThread
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   
/*    */   public JGAPClientHandlerThread(GridServer server, Socket socket) throws IOException {
/* 33 */     super(server, socket);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleMsg(GridMessage msg) throws Exception {
/* 38 */     if (msg instanceof GridMessageVFSSessionFileRequest) {
/* 39 */       String n = ((GridMessageVFSSessionFileRequest)msg).getName();
/*    */ 
/*    */       
/* 42 */       File f = new File(this.gridServer.getVFSSessionPool().getPath(), n);
/* 43 */       long fsize = f.length();
/* 44 */       if (log.isDebugEnabled()) {
/* 45 */         log.debug("  File size: " + fsize);
/*    */       }
/* 47 */       byte[] data = new byte[(int)fsize];
/* 48 */       FileInputStream fis = new FileInputStream(f);
/* 49 */       fis.read(data);
/* 50 */       fis.close();
/* 51 */       this.handlerChannel.send((GridMessage)new GridMessageVFSSessionFileResult(data));
/*    */     } else {
/*    */       
/* 54 */       super.handleMsg(msg);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPClientHandlerThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */