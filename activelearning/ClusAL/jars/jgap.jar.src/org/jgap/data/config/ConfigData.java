/*     */ package org.jgap.data.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigData
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*  33 */   private List m_listData = Collections.synchronizedList(new ArrayList());
/*  34 */   private List m_textData = Collections.synchronizedList(new ArrayList());
/*     */   private String m_ns;
/*     */   
/*     */   public void addListData(String a_name, List a_values) {
/*  38 */     ListData ld = new ListData(a_name, a_values);
/*  39 */     this.m_listData.add(ld);
/*     */   }
/*     */   
/*     */   public void addTextData(String a_name, String a_value) {
/*  43 */     TextData td = new TextData(a_name, a_value);
/*  44 */     this.m_textData.add(td);
/*     */   }
/*     */   
/*     */   public int getNumLists() {
/*  48 */     return this.m_listData.size();
/*     */   }
/*     */   
/*     */   public int getNumTexts() {
/*  52 */     return this.m_textData.size();
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
/*     */   public String getListNameAt(int a_index) {
/*  66 */     ListData ld = this.m_listData.get(a_index);
/*  67 */     return ld.getName();
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
/*     */   public List getListValuesAt(int a_index) {
/*  81 */     ListData ld = this.m_listData.get(a_index);
/*  82 */     return ld.getListData();
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
/*     */   public String getTextNameAt(int a_index) {
/*  96 */     TextData td = this.m_textData.get(a_index);
/*  97 */     return td.getName();
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
/*     */   public String getTextValueAt(int a_index) {
/* 111 */     TextData ld = this.m_textData.get(a_index);
/* 112 */     return ld.getValue();
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
/* 125 */     this.m_ns = a_ns;
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
/*     */   public String getNS() {
/* 138 */     return this.m_ns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class ListData
/*     */   {
/*     */     private String m_name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private List m_data;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ListData(String a_name, List a_data) {
/* 162 */       this.m_data = a_data;
/* 163 */       this.m_name = a_name;
/*     */     }
/*     */     
/*     */     public List getListData() {
/* 167 */       return this.m_data;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 171 */       return this.m_name;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class TextData
/*     */   {
/*     */     private String m_textname;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String m_textvalue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TextData(String a_name, String a_value) {
/* 194 */       this.m_textname = a_name;
/* 195 */       this.m_textvalue = a_value;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 199 */       return this.m_textname;
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 203 */       return this.m_textvalue;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\ConfigData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */