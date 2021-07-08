/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.ICloneHandler;
/*     */ import org.jgap.INaturalSelector;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.NaturalSelector;
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
/*     */ public class ChainOfSelectors
/*     */   implements Serializable, ICloneable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.19 $";
/*     */   private List m_selectors;
/*     */   private Configuration m_conf;
/*     */   
/*     */   public ChainOfSelectors() {
/*  48 */     this(Genotype.getStaticConfiguration());
/*     */   }
/*     */   
/*     */   public ChainOfSelectors(Configuration a_conf) {
/*  52 */     this.m_selectors = new Vector();
/*  53 */     this.m_conf = a_conf;
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
/*     */   public void addNaturalSelector(NaturalSelector a_selector) throws InvalidConfigurationException {
/*  67 */     if (a_selector == null) {
/*  68 */       throw new InvalidConfigurationException("This Configuration object is locked. Settings may not be altered.");
/*     */     }
/*     */ 
/*     */     
/*  72 */     this.m_selectors.add(a_selector);
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
/*     */   public void addAll(Collection a_c) throws InvalidConfigurationException {
/*  85 */     Iterator<NaturalSelector> it = a_c.iterator();
/*  86 */     while (it.hasNext()) {
/*  87 */       NaturalSelector selector = it.next();
/*  88 */       addNaturalSelector(selector);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  99 */     return this.m_selectors.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 109 */     return (size() == 0);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 113 */     return this.m_selectors.hashCode();
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
/*     */   public boolean equals(Object a_obj) {
/*     */     try {
/* 126 */       ChainOfSelectors c2 = (ChainOfSelectors)a_obj;
/* 127 */       if (c2 == null) {
/* 128 */         return false;
/*     */       }
/* 130 */       return this.m_selectors.equals(c2.m_selectors);
/* 131 */     } catch (ClassCastException cex) {
/* 132 */       return false;
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
/*     */   public NaturalSelector get(int a_index) {
/* 146 */     return this.m_selectors.get(a_index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 157 */     this.m_selectors.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator iterator() {
/* 167 */     return this.m_selectors.iterator();
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
/* 178 */       ChainOfSelectors result = new ChainOfSelectors(this.m_conf);
/* 179 */       List<Object> v = new Vector();
/* 180 */       for (int i = 0; i < this.m_selectors.size(); i++) {
/* 181 */         Object clone; INaturalSelector o = this.m_selectors.get(i);
/*     */         
/* 183 */         ICloneHandler handler = this.m_conf.getJGAPFactory().getCloneHandlerFor(o, null);
/*     */         
/* 185 */         if (handler != null) {
/* 186 */           clone = handler.perform(o, null, null);
/*     */         } else {
/*     */           
/* 189 */           throw new IllegalStateException("No clone handler found for class " + o.getClass().getName());
/*     */         } 
/*     */         
/* 192 */         v.add(clone);
/*     */       } 
/* 194 */       result.m_selectors = v;
/* 195 */       return result;
/* 196 */     } catch (Throwable t) {
/* 197 */       throw new CloneException(t);
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
/*     */   
/*     */   public int compareTo(Object a_other) {
/* 213 */     if (a_other == null) {
/* 214 */       return 1;
/*     */     }
/*     */     
/* 217 */     ChainOfSelectors other = (ChainOfSelectors)a_other;
/* 218 */     int size = this.m_selectors.size();
/* 219 */     if (other.m_selectors.size() < size) {
/* 220 */       return 1;
/*     */     }
/* 222 */     if (other.m_selectors.size() > this.m_selectors.size()) {
/* 223 */       return -1;
/*     */     }
/* 225 */     for (int i = 0; i < size; i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 232 */       String name = this.m_selectors.get(i).getClass().getName();
/* 233 */       String nameOther = other.m_selectors.get(i).getClass().getName();
/* 234 */       int result = name.compareTo(nameOther);
/* 235 */       if (result != 0) {
/* 236 */         return result;
/*     */       }
/*     */     } 
/*     */     
/* 240 */     return 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\ChainOfSelectors.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */