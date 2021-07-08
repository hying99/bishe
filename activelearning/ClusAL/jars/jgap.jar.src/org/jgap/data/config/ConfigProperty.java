/*     */ package org.jgap.data.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ public class ConfigProperty
/*     */ {
/*  44 */   private String m_name = "";
/*  45 */   private String m_label = "";
/*  46 */   private String m_widget = "JTextField";
/*  47 */   private List m_values = Collections.synchronizedList(new ArrayList());
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  57 */     return this.m_name;
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
/*     */   public void setName(String a_name) {
/*  70 */     this.m_name = a_name;
/*     */     
/*  72 */     if (this.m_label.equals("")) {
/*  73 */       this.m_label = a_name;
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
/*     */   public String getWidget() {
/*  85 */     return this.m_widget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWidget(String a_widget) {
/*  96 */     this.m_widget = a_widget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLabel() {
/* 106 */     return this.m_label;
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
/*     */   public void setLabel(String a_label) {
/* 118 */     this.m_label = a_label;
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
/*     */   public void addValue(String a_value) {
/* 131 */     this.m_values.add(a_value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator getValuesIter() {
/* 141 */     return this.m_values.iterator();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\ConfigProperty.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */