/*     */ package org.jgap.audit;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
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
/*     */ public class KeyedValues2D
/*     */   implements ICloneable, Serializable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*     */   private List m_rowKeys;
/*     */   private List m_columnKeys;
/*     */   private List m_rows;
/*     */   private boolean m_sortRowKeys;
/*     */   
/*     */   public KeyedValues2D() {
/*  46 */     this(false);
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
/*     */   public KeyedValues2D(boolean a_sortRowKeys) {
/*  58 */     this.m_rowKeys = Collections.synchronizedList(new ArrayList());
/*  59 */     this.m_columnKeys = Collections.synchronizedList(new ArrayList());
/*  60 */     this.m_rows = Collections.synchronizedList(new ArrayList());
/*  61 */     this.m_sortRowKeys = a_sortRowKeys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRowCount() {
/*  71 */     return this.m_rowKeys.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/*  81 */     return this.m_columnKeys.size();
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
/*     */   public Number getValue(int a_row, int a_column) {
/*  96 */     Number result = null;
/*  97 */     KeyedValues rowData = this.m_rows.get(a_row);
/*  98 */     Comparable columnKey = this.m_columnKeys.get(a_column);
/*  99 */     if (columnKey != null) {
/* 100 */       result = rowData.getValue(columnKey);
/*     */     }
/* 102 */     return result;
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
/*     */   public Comparable getRowKey(int a_row) {
/* 114 */     return this.m_rowKeys.get(a_row);
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
/*     */   public int getRowIndex(Comparable a_key) {
/* 126 */     if (this.m_sortRowKeys) {
/* 127 */       return Collections.binarySearch(this.m_rowKeys, a_key);
/*     */     }
/*     */     
/* 130 */     return this.m_rowKeys.indexOf(a_key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getRowKeys() {
/* 141 */     return Collections.unmodifiableList(this.m_rowKeys);
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
/*     */   public Comparable getColumnKey(int a_column) {
/* 153 */     return this.m_columnKeys.get(a_column);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getColumnKeys() {
/* 163 */     return Collections.unmodifiableList(this.m_columnKeys);
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
/*     */   public Number getValue(Comparable a_rowKey, Comparable a_columnKey) {
/*     */     Number result;
/* 178 */     int row = getRowIndex(a_rowKey);
/* 179 */     if (row >= 0) {
/* 180 */       KeyedValues rowData = this.m_rows.get(row);
/* 181 */       result = rowData.getValue(a_columnKey);
/*     */     } else {
/*     */       
/* 184 */       result = null;
/*     */     } 
/* 186 */     return result;
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
/*     */   public void setValue(Number a_value, Comparable a_rowKey, Comparable a_columnKey) {
/*     */     KeyedValues row;
/* 202 */     int rowIndex = getRowIndex(a_rowKey);
/* 203 */     if (rowIndex >= 0) {
/* 204 */       row = this.m_rows.get(rowIndex);
/*     */     } else {
/*     */       
/* 207 */       row = new KeyedValues();
/* 208 */       if (this.m_sortRowKeys) {
/* 209 */         rowIndex = -rowIndex - 1;
/* 210 */         this.m_rowKeys.add(rowIndex, a_rowKey);
/* 211 */         this.m_rows.add(rowIndex, row);
/*     */       } else {
/*     */         
/* 214 */         this.m_rowKeys.add(a_rowKey);
/* 215 */         this.m_rows.add(row);
/*     */       } 
/*     */     } 
/* 218 */     row.setValue(a_columnKey, a_value);
/* 219 */     int columnIndex = this.m_columnKeys.indexOf(a_columnKey);
/* 220 */     if (columnIndex < 0) {
/* 221 */       this.m_columnKeys.add(a_columnKey);
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
/*     */   public boolean equals(Object a_object) {
/* 236 */     if (a_object == null) {
/* 237 */       return false;
/*     */     }
/* 239 */     if (a_object == this) {
/* 240 */       return true;
/*     */     }
/* 242 */     if (!(a_object instanceof KeyedValues2D)) {
/* 243 */       return false;
/*     */     }
/* 245 */     KeyedValues2D kv2D = (KeyedValues2D)a_object;
/* 246 */     if (!getRowKeys().equals(kv2D.getRowKeys())) {
/* 247 */       return false;
/*     */     }
/* 249 */     if (!getColumnKeys().equals(kv2D.getColumnKeys())) {
/* 250 */       return false;
/*     */     }
/* 252 */     int rowCount = getRowCount();
/* 253 */     int colCount = getColumnCount();
/* 254 */     for (int r = 0; r < rowCount; r++) {
/* 255 */       for (int c = 0; c < colCount; c++) {
/* 256 */         Number v1 = getValue(r, c);
/* 257 */         Number v2 = kv2D.getValue(r, c);
/* 258 */         if (v1 == null) {
/* 259 */           if (v2 != null) {
/* 260 */             return false;
/*     */           
/*     */           }
/*     */         }
/* 264 */         else if (!v1.equals(v2)) {
/* 265 */           return false;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 270 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 281 */     int result = this.m_rowKeys.hashCode();
/* 282 */     result = 29 * result + this.m_columnKeys.hashCode();
/* 283 */     result = 31 * result + this.m_rows.hashCode();
/* 284 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 295 */       return super.clone();
/* 296 */     } catch (CloneNotSupportedException cex) {
/* 297 */       throw new CloneException(cex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\audit\KeyedValues2D.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */