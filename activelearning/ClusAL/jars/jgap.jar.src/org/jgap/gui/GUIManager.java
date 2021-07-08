/*     */ package org.jgap.gui;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.data.config.Configurable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GUIManager
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*     */   private ConfigFrame m_frame;
/*     */   private List m_childFrames;
/*     */   private List m_childCons;
/*     */   private Configurable m_con;
/*     */   private static GUIManager m_gm;
/*     */   
/*     */   public static GUIManager getInstance() {
/*  58 */     if (m_gm == null) {
/*  59 */       m_gm = new GUIManager();
/*     */     }
/*  61 */     return m_gm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private GUIManager() {
/*  71 */     this.m_frame = null;
/*  72 */     this.m_childFrames = Collections.synchronizedList(new ArrayList());
/*  73 */     this.m_childCons = Collections.synchronizedList(new ArrayList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showFrame(ConfigFrame a_parent, Configurable a_con) throws Exception {
/*     */     try {
/*  89 */       if (a_con.getClass() == Configuration.class) {
/*  90 */         this.m_frame = new ConfigFrame(null, "JGAP Configurator: Configuration", true);
/*     */ 
/*     */         
/*  93 */         this.m_con = a_con;
/*  94 */         this.m_frame.createAndShowGUI(a_con);
/*     */       } else {
/*     */         
/*  97 */         ConfigFrame tmpFrame = new ConfigFrame(a_parent, "JGAP Configurator: Unknown Title", false);
/*     */ 
/*     */ 
/*     */         
/* 101 */         this.m_childCons.add(a_con);
/* 102 */         this.m_childFrames.add(tmpFrame);
/* 103 */         tmpFrame.createAndShowGUI(a_con);
/*     */       }
/*     */     
/* 106 */     } catch (Exception ex) {
/* 107 */       JOptionPane.showMessageDialog(null, "Could not show configuration frame. This attribute may not be configurable.", "Configuration Error", 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 125 */       SwingUtilities.invokeLater(new Runnable() {
/*     */             public void run() {
/* 127 */               Configuration con = new Configuration();
/*     */               
/*     */               try {
/* 130 */                 GUIManager.getInstance().showFrame(null, (Configurable)con);
/*     */               }
/* 132 */               catch (Exception ex) {}
/*     */             }
/*     */           });
/*     */     
/*     */     }
/* 137 */     catch (Exception ex) {
/* 138 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gui\GUIManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */