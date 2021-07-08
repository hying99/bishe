/*     */ package org.jgap.data.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
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
/*     */ public class ConfigFileReader
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*     */   private String m_fileName;
/*     */   private Properties m_props;
/*     */   private String m_ns;
/*     */   private static ConfigFileReader m_cfReader;
/*     */   
/*     */   public static ConfigFileReader instance() {
/*  47 */     if (m_cfReader == null) {
/*  48 */       m_cfReader = new ConfigFileReader();
/*     */     }
/*  50 */     return m_cfReader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ConfigFileReader() {
/*  60 */     this.m_props = new Properties();
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
/*     */   public String getValue(String a_name) {
/*  73 */     String tmpName = this.m_ns + "." + a_name;
/*  74 */     String val = this.m_props.getProperty(tmpName);
/*  75 */     return val;
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
/*     */   public List getValues(String a_name) {
/*  88 */     String val = "";
/*  89 */     boolean done = false;
/*  90 */     String tmpName = "";
/*  91 */     int idx = 0;
/*  92 */     List<?> values = Collections.synchronizedList(new ArrayList());
/*  93 */     while (!done) {
/*  94 */       tmpName = this.m_ns + "." + a_name + "[" + idx + "]";
/*  95 */       val = this.m_props.getProperty(tmpName);
/*  96 */       if (val == null) {
/*  97 */         done = true;
/*     */         continue;
/*     */       } 
/* 100 */       values.add(val);
/* 101 */       idx++;
/*     */     } 
/*     */     
/* 104 */     if (idx == 0) {
/* 105 */       return null;
/*     */     }
/*     */     
/* 108 */     return values;
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
/*     */   public void setNS(String a_ns) {
/* 121 */     this.m_ns = a_ns;
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
/*     */   public void setFileName(String a_fileName) throws ConfigException {
/* 136 */     this.m_fileName = a_fileName;
/* 137 */     load();
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
/*     */   private void load() throws ConfigException {
/*     */     try {
/* 150 */       this.m_props.load(new FileInputStream(this.m_fileName));
/*     */     }
/* 152 */     catch (Exception ex) {
/* 153 */       String dir = (new File(".")).getAbsolutePath();
/* 154 */       throw new ConfigException("Error reading Config file " + this.m_fileName + " in directory " + dir);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\ConfigFileReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */