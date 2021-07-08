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
/*     */ public class Culture
/*     */   implements Serializable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.15 $";
/*     */   private CultureMemoryCell[] m_memory;
/*  37 */   private List m_memoryNames = new Vector();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_size;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_width;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Culture(int a_size) {
/*  60 */     if (a_size < 1) {
/*  61 */       throw new IllegalArgumentException("Size must be greater than zero!");
/*     */     }
/*  63 */     this.m_size = a_size;
/*  64 */     this.m_memory = new CultureMemoryCell[this.m_size];
/*  65 */     this.m_width = 1;
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
/*     */   public CultureMemoryCell set(int a_index, double a_value, int a_historySize, String a_name) {
/*  84 */     if (a_index < 0 || a_index >= size()) {
/*  85 */       throw new IllegalArgumentException("Illegal memory index!");
/*     */     }
/*  87 */     CultureMemoryCell cell = new CultureMemoryCell(a_name, a_historySize);
/*  88 */     cell.setDouble(a_value);
/*  89 */     this.m_memory[a_index] = cell;
/*  90 */     return cell;
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
/*     */   public CultureMemoryCell set(int a_index, Object a_value, int a_historySize, String a_infotext) {
/* 109 */     if (a_index < 0 || a_index >= size()) {
/* 110 */       throw new IllegalArgumentException("Illegal memory index!");
/*     */     }
/* 112 */     CultureMemoryCell cell = new CultureMemoryCell(a_infotext, a_historySize);
/* 113 */     cell.setValue(a_value);
/* 114 */     this.m_memory[a_index] = cell;
/* 115 */     return cell;
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
/*     */   public CultureMemoryCell set(String a_name, Object a_value, int a_historySize) {
/* 133 */     if (a_name == null || a_name.length() < 1) {
/* 134 */       throw new IllegalArgumentException("Illegal memory name!");
/*     */     }
/* 136 */     int index = this.m_memoryNames.indexOf(a_name);
/* 137 */     if (index < 0) {
/*     */ 
/*     */       
/* 140 */       this.m_memoryNames.add(a_name);
/* 141 */       index = this.m_memoryNames.size() - 1;
/*     */     } 
/* 143 */     CultureMemoryCell cell = new CultureMemoryCell(a_name, a_historySize);
/* 144 */     cell.setValue(a_value);
/* 145 */     this.m_memory[index] = cell;
/* 146 */     return cell;
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
/*     */   public CultureMemoryCell get(int a_index) {
/* 159 */     if (a_index < 0 || a_index >= size()) {
/* 160 */       throw new IllegalArgumentException("Illegal memory index!");
/*     */     }
/* 162 */     return this.m_memory[a_index];
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
/*     */   public CultureMemoryCell get(String a_name) {
/* 175 */     if (a_name == null || a_name.length() < 1) {
/* 176 */       throw new IllegalArgumentException("Illegal memory name!");
/*     */     }
/* 178 */     int index = this.m_memoryNames.indexOf(a_name);
/* 179 */     if (index < 0) {
/* 180 */       throw new IllegalArgumentException("Memory name unknown: " + a_name);
/*     */     }
/* 182 */     return this.m_memory[index];
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
/*     */   public boolean contains(String a_name) {
/* 195 */     if (a_name == null || a_name.length() < 1) {
/* 196 */       throw new IllegalArgumentException("Illegal memory name!");
/*     */     }
/* 198 */     int index = this.m_memoryNames.indexOf(a_name);
/* 199 */     return (index >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 209 */     return this.m_memory.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 219 */     StringBuffer result = new StringBuffer("[");
/* 220 */     int len = this.m_memory.length;
/* 221 */     if (this.m_memory[0] == null) {
/* 222 */       result.append(this.m_memory[0]);
/*     */     } else {
/*     */       
/* 225 */       result.append(this.m_memory[0].toString());
/*     */     } 
/* 227 */     for (int i = 1; i < len; i++) {
/* 228 */       if (this.m_memory[i] == null) {
/* 229 */         result.append(";" + this.m_memory[i]);
/*     */       } else {
/*     */         
/* 232 */         result.append(";" + this.m_memory[i].toString());
/*     */       } 
/*     */     } 
/* 235 */     result.append("]");
/* 236 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 246 */     this.m_memory = new CultureMemoryCell[this.m_size];
/* 247 */     this.m_memoryNames.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getMemoryNames() {
/* 257 */     return new Vector(this.m_memoryNames);
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
/* 270 */       return (compareTo(a_other) == 0);
/* 271 */     } catch (ClassCastException cex) {
/* 272 */       cex.printStackTrace();
/* 273 */       return false;
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
/*     */   public int compareTo(Object a_other) {
/* 287 */     Culture other = (Culture)a_other;
/* 288 */     if (other == null) {
/* 289 */       return 1;
/*     */     }
/*     */ 
/*     */     
/* 293 */     return (new CompareToBuilder()).append(this.m_size, other.m_size).append((Object[])this.m_memory, (Object[])other.m_memory).append(this.m_memoryNames.toArray(), other.m_memoryNames.toArray()).toComparison();
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
/*     */   public void setMatrixWidth(int a_width) {
/* 310 */     int size = size();
/* 311 */     if (a_width > size) {
/* 312 */       throw new IllegalArgumentException("Width must not be greater than the size of the memory (" + size + ") !");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 317 */     this.m_width = a_width;
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
/*     */   public CultureMemoryCell setMatrix(int a_x, int a_y, Object a_value) {
/* 332 */     int index = a_x * this.m_width + a_y;
/* 333 */     CultureMemoryCell cell = this.m_memory[index];
/* 334 */     if (cell == null) {
/* 335 */       cell = new CultureMemoryCell(a_x + "_" + a_y, -1);
/*     */     }
/* 337 */     cell.setValue(a_value);
/* 338 */     this.m_memory[index] = cell;
/* 339 */     return cell;
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
/*     */   public CultureMemoryCell getMatrix(int a_x, int a_y) {
/* 354 */     int index = a_x * this.m_width + a_y;
/* 355 */     return get(index);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\Culture.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */