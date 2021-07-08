/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.jgap.ICloneHandler;
/*     */ import org.jgap.ICompareToHandler;
/*     */ import org.jgap.IGeneticOperatorConstraint;
/*     */ import org.jgap.IHandler;
/*     */ import org.jgap.IInitializer;
/*     */ import org.jgap.IJGAPFactory;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
/*     */ import org.jgap.util.LRUCache;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JGAPFactory
/*     */   implements IJGAPFactory, Serializable, ICloneable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.18 $";
/*     */   private List m_parameters;
/*     */   private List m_cloneHandlers;
/*     */   private List m_initer;
/*     */   private List m_compareHandlers;
/*     */   private ICloneHandler m_defaultCloneHandler;
/*     */   private IInitializer m_defaultIniter;
/*     */   private ICompareToHandler m_defaultComparer;
/*     */   private IGeneticOperatorConstraint m_geneticOpConstraint;
/*     */   private transient LRUCache m_cache;
/*     */   private boolean m_useCaching;
/*     */   
/*     */   public JGAPFactory(boolean a_useCaching) {
/*  57 */     this.m_initer = new Vector();
/*  58 */     this.m_cache = new LRUCache(50);
/*  59 */     this.m_useCaching = a_useCaching;
/*  60 */     this.m_cloneHandlers = new Vector();
/*  61 */     this.m_compareHandlers = new Vector();
/*     */ 
/*     */     
/*  64 */     this.m_defaultCloneHandler = new DefaultCloneHandler();
/*  65 */     this.m_defaultIniter = new DefaultInitializer();
/*  66 */     this.m_defaultComparer = new DefaultCompareToHandler();
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
/*     */   public void setParameters(Collection<?> a_parameters) {
/*  78 */     this.m_parameters = new Vector(a_parameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection getParameters() {
/*  88 */     return this.m_parameters;
/*     */   }
/*     */   
/*     */   public RandomGenerator createRandomGenerator() {
/*  92 */     return new StockRandomGenerator();
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
/*     */   public int registerCloneHandler(ICloneHandler a_cloneHandler) {
/* 107 */     this.m_cloneHandlers.add(a_cloneHandler);
/* 108 */     return this.m_cloneHandlers.size() - 1;
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
/*     */   public ICloneHandler removeCloneHandler(int a_index) {
/* 122 */     return this.m_cloneHandlers.remove(a_index);
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
/*     */   public ICloneHandler getCloneHandlerFor(Object a_obj, Class a_classToClone) {
/* 138 */     return (ICloneHandler)findHandlerFor(a_obj, a_classToClone, this.m_cloneHandlers, (IHandler)this.m_defaultCloneHandler, "clone");
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
/*     */   public int registerInitializer(IInitializer a_chromIniter) {
/* 156 */     this.m_initer.add(a_chromIniter);
/* 157 */     return this.m_initer.size() - 1;
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
/*     */   public IInitializer removeInitializer(int a_index) {
/* 171 */     return this.m_initer.remove(a_index);
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
/*     */   public IInitializer getInitializerFor(Object a_obj, Class a_class) {
/* 187 */     return (IInitializer)findHandlerFor(a_obj, a_class, this.m_initer, (IHandler)this.m_defaultIniter, "init");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGeneticOperatorConstraint(IGeneticOperatorConstraint a_constraint) {
/* 195 */     this.m_geneticOpConstraint = a_constraint;
/*     */   }
/*     */   
/*     */   public IGeneticOperatorConstraint getGeneticOperatorConstraint() {
/* 199 */     return this.m_geneticOpConstraint;
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
/*     */   public ICompareToHandler getCompareToHandlerFor(Object a_obj, Class a_classToCompareTo) {
/* 215 */     return (ICompareToHandler)findHandlerFor(a_obj, a_classToCompareTo, this.m_compareHandlers, (IHandler)this.m_defaultComparer, "compare");
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
/*     */   public int registerCompareToHandler(ICompareToHandler a_compareToHandler) {
/* 233 */     this.m_compareHandlers.add(a_compareToHandler);
/* 234 */     return this.m_compareHandlers.size() - 1;
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
/*     */   public ICompareToHandler removeCompareToHandler(int a_index) {
/* 248 */     return this.m_compareHandlers.remove(a_index);
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected IHandler findHandlerFor(Object a_obj, Class a_class, List a_list, IHandler a_default, String a_listID) {
/* 270 */     String key = null;
/*     */     
/* 272 */     if (this.m_useCaching) {
/*     */       String key1, key2;
/*     */ 
/*     */       
/* 276 */       if (a_class == null) {
/* 277 */         key1 = "null";
/*     */       } else {
/*     */         
/* 280 */         key1 = a_class.getName();
/*     */       } 
/* 282 */       if (a_obj == null) {
/* 283 */         key2 = "null";
/*     */       } else {
/*     */         
/* 286 */         key2 = a_obj.getClass().getName();
/*     */       } 
/* 288 */       key = a_listID + "/" + key1 + "/" + key2;
/*     */ 
/*     */       
/* 291 */       Object handler = this.m_cache.get(key);
/* 292 */       if (handler != null) {
/* 293 */         return (IHandler)handler;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 298 */     IHandler result = null;
/* 299 */     Iterator<IHandler> it = a_list.iterator();
/* 300 */     while (it.hasNext()) {
/* 301 */       IHandler initer = it.next();
/* 302 */       if (initer.isHandlerFor(a_obj, a_class)) {
/* 303 */         result = initer;
/*     */         break;
/*     */       } 
/*     */     } 
/* 307 */     if (result == null)
/*     */     {
/*     */       
/* 310 */       if (a_default != null && 
/* 311 */         a_default.isHandlerFor(a_obj, a_class)) {
/* 312 */         result = a_default;
/*     */       }
/*     */     }
/*     */     
/* 316 */     if (this.m_useCaching)
/*     */     {
/*     */       
/* 319 */       if (result != null) {
/* 320 */         this.m_cache.put(key, result);
/*     */       }
/*     */     }
/* 323 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUseCaching() {
/* 333 */     return this.m_useCaching;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 339 */       return super.clone();
/* 340 */     } catch (CloneNotSupportedException cex) {
/* 341 */       throw new CloneException(cex);
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 355 */       return (compareTo(a_other) == 0);
/* 356 */     } catch (ClassCastException cex) {
/* 357 */       return false;
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
/*     */   public int compareTo(Object a_other) {
/* 369 */     if (a_other == null) {
/* 370 */       return 1;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 375 */     JGAPFactory other = (JGAPFactory)a_other;
/* 376 */     return (new CompareToBuilder()).append(this.m_cloneHandlers.toArray(), other.m_cloneHandlers.toArray()).append(this.m_initer.toArray(), other.m_initer.toArray()).append(this.m_compareHandlers.toArray(), other.m_compareHandlers.toArray()).append(this.m_defaultCloneHandler, other.m_defaultCloneHandler).append(this.m_defaultComparer, other.m_defaultComparer).append(this.m_geneticOpConstraint, other.m_geneticOpConstraint).toComparison();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\JGAPFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */