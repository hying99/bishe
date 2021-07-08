/*     */ package org.jgap.distr;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
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
/*     */ public class CultureMemoryCell
/*     */   implements Serializable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.13 $";
/*     */   private String m_name;
/*     */   private int m_version;
/*     */   private Object m_value;
/*     */   private int m_readAccessed;
/*     */   private int m_historySize;
/*     */   private int m_internalHistorySize;
/*     */   private CultureMemoryCell m_previousVersion;
/*     */   private List m_history;
/*     */   private long m_dateTimeVersion;
/*     */   
/*     */   public CultureMemoryCell() {
/*  85 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CultureMemoryCell(String a_name) {
/*  96 */     this(a_name, 3);
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
/*     */   public CultureMemoryCell(String a_name, int a_historySize) {
/* 110 */     setHistorySize(a_historySize);
/* 111 */     this.m_history = new Vector(getHistorySize());
/* 112 */     setName(a_name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String a_name) {
/* 123 */     this.m_name = a_name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 133 */     return this.m_name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Object a_value) {
/* 144 */     if (this.m_historySize > 0) {
/* 145 */       keepHistory(a_value, getVersion(), getName());
/*     */     } else {
/*     */       
/* 148 */       this.m_previousVersion = getNewInstance(this.m_value, getVersion(), getName());
/*     */     } 
/*     */     
/* 151 */     this.m_value = a_value;
/* 152 */     incrementVersion();
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
/*     */   public void setDouble(double a_value) {
/* 164 */     setValue(new Double(a_value));
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
/*     */   public double getCurrentValueAsDouble() {
/* 176 */     return ((Double)getCurrentValue()).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/* 186 */     this.m_readAccessed++;
/* 187 */     return this.m_value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getHistory() {
/* 198 */     return this.m_history;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 208 */     return this.m_version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void incrementVersion() {
/* 218 */     this.m_version++;
/*     */     
/* 220 */     this.m_dateTimeVersion = System.currentTimeMillis();
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
/*     */   protected void keepHistory(Object a_value, int a_version, String a_name) {
/* 236 */     trimHistory(this.m_historySize - 1);
/*     */     
/* 238 */     CultureMemoryCell cell = getNewInstance(a_value, a_version, a_name);
/* 239 */     cell.m_internalHistorySize = this.m_historySize;
/* 240 */     this.m_history.add(cell);
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected CultureMemoryCell getNewInstance(Object a_value, int a_version, String a_name) {
/* 259 */     CultureMemoryCell cell = new CultureMemoryCell(a_name, 0);
/* 260 */     cell.m_value = a_value;
/* 261 */     cell.m_version = a_version;
/* 262 */     return cell;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getReadAccessed() {
/* 272 */     return this.m_readAccessed;
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
/*     */   public int getReadAccessedCurrentVersion() {
/* 284 */     if (this.m_historySize < 1)
/*     */     {
/*     */       
/* 287 */       return getReadAccessed() - this.m_previousVersion.getReadAccessed();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 292 */     CultureMemoryCell cell = this.m_history.get(this.m_history.size() - 1);
/*     */     
/* 294 */     return getReadAccessed() - cell.getReadAccessed();
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
/*     */   public void setHistorySize(int a_size) {
/* 307 */     if (getHistory() != null && a_size > getHistory().size()) {
/* 308 */       trimHistory(a_size);
/* 309 */       this.m_historySize = a_size;
/*     */     }
/* 311 */     else if (a_size < 0) {
/* 312 */       this.m_historySize = 0;
/*     */     } else {
/*     */       
/* 315 */       this.m_historySize = a_size;
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
/*     */   protected void trimHistory(int a_size) {
/* 328 */     while (this.m_history.size() > a_size)
/*     */     {
/* 330 */       this.m_history.remove(0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHistorySize() {
/* 341 */     return this.m_historySize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 352 */     StringBuffer result = new StringBuffer();
/* 353 */     toStringRecursive(result, getHistorySize());
/* 354 */     return result.toString();
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
/*     */   protected void toStringRecursive(StringBuffer a_result, int a_historySize) {
/* 367 */     List<CultureMemoryCell> history = getHistory();
/* 368 */     a_result.append("[Name:" + getName() + ";");
/* 369 */     a_result.append("Value:" + this.m_value + ";");
/* 370 */     a_result.append("Version:" + getVersion() + ";");
/* 371 */     a_result.append("Read accessed:" + getReadAccessed() + ";");
/* 372 */     a_result.append("History Size:" + a_historySize + ";");
/* 373 */     a_result.append("History:[");
/* 374 */     for (int i = 0; i < history.size(); i++) {
/* 375 */       if (i > 0) {
/* 376 */         a_result.append(";");
/*     */       }
/* 378 */       CultureMemoryCell cell = history.get(i);
/*     */       
/* 380 */       cell.toStringRecursive(a_result, cell.m_internalHistorySize);
/* 381 */       a_result.append("]");
/*     */     } 
/* 383 */     a_result.append("]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getVersionTimeMilliseconds() {
/* 393 */     return this.m_dateTimeVersion;
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 406 */       return (compareTo(a_other) == 0);
/* 407 */     } catch (ClassCastException cex) {
/* 408 */       return false;
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
/*     */   public int compareTo(Object a_other) {
/* 421 */     CultureMemoryCell other = (CultureMemoryCell)a_other;
/* 422 */     if (other == null) {
/* 423 */       return 1;
/*     */     }
/* 425 */     return (new CompareToBuilder()).append(this.m_value, other.m_value).append(this.m_version, other.m_version).append(this.m_historySize, other.m_historySize).toComparison();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\CultureMemoryCell.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */