/*     */ package org.jgap.audit;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ public class KeyedValues
/*     */   implements ICloneable, Serializable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*  37 */   private List m_data = Collections.synchronizedList(new ArrayList());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  47 */     return this.m_data.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Number getValue(int a_index) {
/*     */     Number result;
/*  59 */     KeyedValue kval = this.m_data.get(a_index);
/*  60 */     if (kval != null) {
/*  61 */       result = kval.getValue();
/*     */     } else {
/*     */       
/*  64 */       result = null;
/*     */     } 
/*  66 */     return result;
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
/*     */   public Comparable getKey(int a_index) {
/*     */     Comparable result;
/*  79 */     KeyedValue item = this.m_data.get(a_index);
/*  80 */     if (item != null) {
/*  81 */       result = item.getKey();
/*     */     } else {
/*     */       
/*  84 */       result = null;
/*     */     } 
/*  86 */     return result;
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
/*     */   public int getIndex(Comparable a_key) {
/*  98 */     int i = 0;
/*  99 */     Iterator<KeyedValue> iterator = this.m_data.iterator();
/* 100 */     while (iterator.hasNext()) {
/* 101 */       KeyedValue kv = iterator.next();
/* 102 */       if (kv.getKey() != null) {
/* 103 */         if (kv.getKey().equals(a_key)) {
/* 104 */           return i;
/*     */         
/*     */         }
/*     */       }
/* 108 */       else if (a_key == null) {
/* 109 */         return i;
/*     */       } 
/*     */       
/* 112 */       i++;
/*     */     } 
/*     */     
/* 115 */     return -1;
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
/*     */   public List getKeys() {
/* 127 */     List<Comparable> result = new ArrayList();
/* 128 */     Iterator<KeyedValue> iterator = this.m_data.iterator();
/* 129 */     while (iterator.hasNext()) {
/* 130 */       KeyedValue kv = iterator.next();
/* 131 */       result.add(kv.getKey());
/*     */     } 
/* 133 */     return result;
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
/*     */   public Number getValue(Comparable a_key) {
/*     */     Number result;
/* 148 */     int index = getIndex(a_key);
/* 149 */     if (index >= 0) {
/* 150 */       result = getValue(index);
/*     */     } else {
/*     */       
/* 153 */       result = null;
/*     */     } 
/* 155 */     return result;
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
/*     */   public void setValue(Comparable a_key, Number a_value) {
/* 168 */     int keyIndex = getIndex(a_key);
/* 169 */     if (keyIndex >= 0) {
/* 170 */       KeyedValue kv = this.m_data.get(keyIndex);
/* 171 */       kv.setValue(a_value);
/*     */     } else {
/*     */       
/* 174 */       KeyedValue kv = new KeyedValue(a_key, a_value);
/* 175 */       this.m_data.add(kv);
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
/*     */   public boolean equals(Object a_obj) {
/* 190 */     if (a_obj == null) {
/* 191 */       return false;
/*     */     }
/* 193 */     if (a_obj == this) {
/* 194 */       return true;
/*     */     }
/* 196 */     if (!(a_obj instanceof KeyedValues)) {
/* 197 */       return false;
/*     */     }
/* 199 */     KeyedValues kvs = (KeyedValues)a_obj;
/* 200 */     int count = size();
/* 201 */     if (count != kvs.size()) {
/* 202 */       return false;
/*     */     }
/* 204 */     for (int i = 0; i < count; i++) {
/* 205 */       Comparable k1 = getKey(i);
/* 206 */       Comparable k2 = kvs.getKey(i);
/* 207 */       if (!k1.equals(k2)) {
/* 208 */         return false;
/*     */       }
/* 210 */       Number v1 = getValue(i);
/* 211 */       Number v2 = kvs.getValue(i);
/* 212 */       if (v1 == null) {
/* 213 */         if (v2 != null) {
/* 214 */           return false;
/*     */         
/*     */         }
/*     */       }
/* 218 */       else if (!v1.equals(v2)) {
/* 219 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 233 */     if (this.m_data.size() == 0) {
/* 234 */       return -29;
/*     */     }
/*     */     
/* 237 */     return this.m_data.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 249 */       KeyedValues clone = (KeyedValues)super.clone();
/* 250 */       clone.m_data = Collections.synchronizedList(new ArrayList());
/* 251 */       Iterator<KeyedValue> iterator = this.m_data.iterator();
/* 252 */       while (iterator.hasNext()) {
/* 253 */         KeyedValue kv = iterator.next();
/* 254 */         clone.m_data.add(kv.clone());
/*     */       } 
/* 256 */       return clone;
/* 257 */     } catch (CloneNotSupportedException cex) {
/* 258 */       throw new CloneException(cex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\audit\KeyedValues.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */