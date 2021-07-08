/*     */ package org.jgap.audit;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ public class KeyedValue
/*     */   implements ICloneable, Serializable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*     */   private Comparable m_key;
/*     */   private Number m_value;
/*     */   
/*     */   public KeyedValue(Comparable a_key, Number a_value) {
/*  40 */     this.m_key = a_key;
/*  41 */     this.m_value = a_value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Comparable getKey() {
/*  51 */     return this.m_key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Number getValue() {
/*  61 */     return this.m_value;
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
/*     */   public synchronized void setValue(Number a_value) {
/*  73 */     this.m_value = a_value;
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
/*  87 */     if (this == a_object) {
/*  88 */       return true;
/*     */     }
/*  90 */     if (!(a_object instanceof KeyedValue)) {
/*  91 */       return false;
/*     */     }
/*  93 */     KeyedValue defaultKeyedValue = (KeyedValue)a_object;
/*  94 */     if ((this.m_key != null) ? !this.m_key.equals(defaultKeyedValue.m_key) : (defaultKeyedValue.m_key != null))
/*     */     {
/*  96 */       return false;
/*     */     }
/*  98 */     if ((this.m_value != null) ? !this.m_value.equals(defaultKeyedValue.m_value) : (defaultKeyedValue.m_value != null))
/*     */     {
/* 100 */       return false;
/*     */     }
/* 102 */     return true;
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
/* 113 */     if (this.m_key == null) {
/* 114 */       result = -37;
/*     */     } else {
/*     */       
/* 117 */       result = this.m_key.hashCode();
/*     */     } 
/* 119 */     int result = 41 * result;
/* 120 */     if (this.m_value == null) {
/* 121 */       result -= 3;
/*     */     } else {
/*     */       
/* 124 */       result += this.m_value.hashCode();
/*     */     } 
/* 126 */     return result;
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
/* 137 */       KeyedValue clone = (KeyedValue)super.clone();
/* 138 */       return clone;
/* 139 */     } catch (CloneNotSupportedException cex) {
/* 140 */       throw new CloneException(cex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\audit\KeyedValue.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */