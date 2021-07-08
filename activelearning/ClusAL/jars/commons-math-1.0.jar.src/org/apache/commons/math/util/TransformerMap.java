/*     */ package org.apache.commons.math.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.math.MathException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TransformerMap
/*     */   implements NumberTransformer, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -942772950698439883L;
/*  41 */   private NumberTransformer defaultTransformer = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   private Map map = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TransformerMap() {
/*  52 */     this.map = new HashMap();
/*  53 */     this.defaultTransformer = new DefaultTransformer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsClass(Class key) {
/*  62 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsTransformer(NumberTransformer value) {
/*  71 */     return this.map.containsValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumberTransformer getTransformer(Class key) {
/*  81 */     return (NumberTransformer)this.map.get(key);
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
/*     */   public Object putTransformer(Class key, NumberTransformer transformer) {
/*  93 */     return this.map.put(key, transformer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object removeTransformer(Class key) {
/* 103 */     return this.map.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 110 */     this.map.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set classes() {
/* 118 */     return this.map.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection transformers() {
/* 127 */     return this.map.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double transform(Object o) throws MathException {
/* 137 */     double value = Double.NaN;
/*     */     
/* 139 */     if (o instanceof Number || o instanceof String) {
/* 140 */       value = this.defaultTransformer.transform(o);
/*     */     } else {
/* 142 */       NumberTransformer trans = getTransformer(o.getClass());
/* 143 */       if (trans != null) {
/* 144 */         value = trans.transform(o);
/*     */       }
/*     */     } 
/*     */     
/* 148 */     return value;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\mat\\util\TransformerMap.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */