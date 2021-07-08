/*     */ package org.jgap.data.config;
/*     */ 
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.swing.JOptionPane;
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
/*     */ public final class ConfigWriter
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*     */   private static ConfigWriter m_cWriter;
/*     */   private Properties m_config;
/*     */   
/*     */   public static ConfigWriter getInstance() {
/*  43 */     if (m_cWriter == null) {
/*  44 */       m_cWriter = new ConfigWriter();
/*     */     }
/*  46 */     return m_cWriter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ConfigWriter() {
/*  56 */     this.m_config = new Properties();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(IConfigInfo a_cInfo) {
/*  67 */     ConfigData cd = a_cInfo.getConfigData();
/*  68 */     String nsPrefix = cd.getNS() + ".";
/*     */ 
/*     */ 
/*     */     
/*  72 */     for (int i = 0; i < cd.getNumLists(); i++) {
/*  73 */       String name = cd.getListNameAt(i);
/*  74 */       List values = cd.getListValuesAt(i);
/*  75 */       int idx = 0;
/*  76 */       for (Iterator<String> iter = values.iterator(); iter.hasNext(); idx++) {
/*     */         
/*  78 */         String str = name + "[" + idx + "]";
/*  79 */         str = nsPrefix + str;
/*  80 */         this.m_config.setProperty(str, iter.next());
/*     */       } 
/*     */     } 
/*  83 */     String value = "", tmpName = "";
/*  84 */     for (int j = 0; j < cd.getNumTexts(); j++) {
/*  85 */       String name = cd.getTextNameAt(j);
/*  86 */       value = cd.getTextValueAt(j);
/*  87 */       tmpName = nsPrefix + name;
/*  88 */       this.m_config.setProperty(tmpName, value);
/*     */     } 
/*     */     try {
/*  91 */       FileOutputStream out = new FileOutputStream(a_cInfo.getFileName());
/*     */       try {
/*  93 */         this.m_config.store(out, "---JGAP Configuration File---");
/*     */       } finally {
/*  95 */         out.close();
/*     */       } 
/*  97 */     } catch (IOException ioEx) {
/*  98 */       JOptionPane.showMessageDialog(null, "Exception " + ioEx.getMessage(), "Configuration Exception", 1);
/*     */ 
/*     */ 
/*     */       
/* 102 */       ioEx.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\ConfigWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */