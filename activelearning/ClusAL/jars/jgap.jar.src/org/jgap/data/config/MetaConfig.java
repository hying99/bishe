/*     */ package org.jgap.data.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
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
/*     */ public class MetaConfig
/*     */ {
/*     */   private static final String METACON_FILENAME = "jgap-meta.con";
/*     */   private static final String CN = "MetaConfig";
/*     */   private static MetaConfig instance;
/*  32 */   private Hashtable m_metaMap = new Hashtable<Object, Object>();
/*     */ 
/*     */   
/*     */   private int m_state;
/*     */ 
/*     */   
/*     */   private static final int INIT = 0;
/*     */   
/*     */   private static final int CLASS = 1;
/*     */   
/*     */   private static final int PROPERTY = 2;
/*     */   
/*     */   private static final int VALUES = 3;
/*     */   
/*     */   private String m_currName;
/*     */   
/*     */   private ConfigProperty m_currProperty;
/*     */ 
/*     */   
/*     */   public static MetaConfig getInstance() throws MetaConfigException, IOException {
/*  52 */     if (null == instance) {
/*  53 */       instance = new MetaConfig();
/*     */     }
/*  55 */     return instance;
/*     */   }
/*     */ 
/*     */   
/*     */   private MetaConfig() throws MetaConfigException, IOException {
/*  60 */     this.m_state = 0;
/*  61 */     init();
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
/*     */   public List getConfigProperty(String className) {
/*  77 */     return (List)this.m_metaMap.get(className);
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
/*     */   protected void init() throws MetaConfigException, IOException {
/*  93 */     Reader fr = getReader("jgap-meta.con");
/*  94 */     LineNumberReader lr = new LineNumberReader(fr);
/*  95 */     String line = lr.readLine();
/*  96 */     while (line != null) {
/*  97 */       if (!isComment(line)) {
/*  98 */         parseLine(line);
/*     */       }
/* 100 */       line = lr.readLine();
/*     */     } 
/* 102 */     endState();
/* 103 */     lr.close();
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
/*     */   public Reader getReader(String a_filename) throws IOException {
/* 116 */     File metaFile = new File(a_filename);
/* 117 */     FileReader fr = new FileReader(metaFile);
/* 118 */     return fr;
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
/*     */   private boolean isComment(String line) {
/* 130 */     String tmpLine = line.trim();
/* 131 */     StringBuffer sb = new StringBuffer(tmpLine);
/* 132 */     if (sb.charAt(0) == '#') {
/* 133 */       return true;
/*     */     }
/* 135 */     return false;
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
/*     */   private void parseLine(String a_line) throws MetaConfigException {
/* 148 */     String[] tokens = a_line.split("=");
/* 149 */     if (tokens == null || tokens.length != 2) {
/* 150 */       throw new MetaConfigException("MetaConfig.parseLine():Exception while parsing jgap-meta.con line " + a_line + " is invalid");
/*     */     }
/*     */     
/* 153 */     if (this.m_state == 0 && tokens[0].equals("class")) {
/* 154 */       handleClass(tokens[1]);
/*     */     }
/* 156 */     else if (this.m_state == 1 && tokens[0].equals("property")) {
/* 157 */       handleProperty(tokens[1]);
/*     */     }
/* 159 */     else if (this.m_state == 2 && tokens[0].equals("values")) {
/* 160 */       handleValues(tokens[1]);
/*     */     }
/* 162 */     else if (this.m_state == 2 && tokens[0].equals("class")) {
/* 163 */       handleClass(tokens[1]);
/*     */     }
/* 165 */     else if (this.m_state == 3 && tokens[0].equals("class")) {
/* 166 */       handleClass(tokens[1]);
/*     */     }
/* 168 */     else if (this.m_state == 3 && tokens[0].equals("property")) {
/* 169 */       handleProperty(tokens[1]);
/*     */     } else {
/*     */       
/* 172 */       throw new MetaConfigException("MetaConfig.parseLine():Exception while parsing jgap-meta.con state " + this.m_state + " incompatible with line " + a_line);
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
/*     */   private void handleClass(String a_token) {
/* 185 */     this.m_state = 1;
/* 186 */     if (this.m_currProperty != null) {
/* 187 */       add(this.m_currName, this.m_currProperty);
/*     */     }
/* 189 */     this.m_currProperty = new ConfigProperty();
/* 190 */     this.m_currName = a_token;
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
/*     */   private void handleProperty(String a_token) throws MetaConfigException {
/* 202 */     int prevState = this.m_state;
/* 203 */     if (prevState == 3 && 
/* 204 */       this.m_currProperty != null) {
/* 205 */       add(this.m_currName, this.m_currProperty);
/*     */     }
/*     */     
/* 208 */     this.m_currProperty = new ConfigProperty();
/* 209 */     this.m_state = 2;
/* 210 */     String[] tokens = a_token.split(",");
/* 211 */     if (tokens.length < 2 || tokens.length > 3) {
/* 212 */       throw new MetaConfigException("Invalid format of property line: " + a_token);
/*     */     }
/*     */     
/* 215 */     this.m_currProperty.setName(tokens[0].trim());
/* 216 */     this.m_currProperty.setWidget(tokens[1].trim());
/* 217 */     if (tokens.length == 3) {
/* 218 */       this.m_currProperty.setLabel(tokens[2]);
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
/*     */   private void handleValues(String a_token) throws MetaConfigException {
/* 233 */     this.m_state = 3;
/* 234 */     String[] tokens = a_token.split(",");
/* 235 */     if (tokens.length == 0) {
/* 236 */       throw new MetaConfigException("Invalid format of property line: " + a_token);
/*     */     }
/*     */     
/* 239 */     for (int i = 0; i < tokens.length; i++) {
/* 240 */       this.m_currProperty.addValue(tokens[i].trim());
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
/*     */   private void endState() throws MetaConfigException {
/* 254 */     if (this.m_state != 2 && this.m_state != 3) {
/* 255 */       throw new MetaConfigException("Invalid format of JGAP MetaConfig file: jgap-meta.conEnding in Invalid state : " + this.m_state);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 260 */     if (this.m_currProperty != null) {
/* 261 */       add(this.m_currName, this.m_currProperty);
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
/*     */   private void add(String currName, ConfigProperty a_cp) {
/* 275 */     List<?> props = (List)this.m_metaMap.get(currName);
/* 276 */     if (null == props) {
/* 277 */       props = Collections.synchronizedList(new ArrayList());
/* 278 */       this.m_metaMap.put(currName, props);
/*     */     } 
/* 280 */     props.add(a_cp);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\MetaConfig.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */