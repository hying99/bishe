/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusBeamTreeElem
/*     */ {
/*     */   protected Comparable m_Object;
/*     */   protected ArrayList m_Others;
/*  35 */   protected static Random m_Random = new Random(0L);
/*     */   
/*     */   public ClusBeamTreeElem(ClusBeamModel model) {
/*  38 */     this.m_Object = model;
/*     */   }
/*     */   
/*     */   public boolean hasList() {
/*  42 */     return (this.m_Others != null);
/*     */   }
/*     */   
/*     */   public ArrayList getOthers() {
/*  46 */     return this.m_Others;
/*     */   }
/*     */   
/*     */   public int getCount() {
/*  50 */     return (this.m_Others == null) ? 1 : this.m_Others.size();
/*     */   }
/*     */   
/*     */   public void setObject(Comparable obj) {
/*  54 */     this.m_Object = obj;
/*     */   }
/*     */   
/*     */   public Object getObject() {
/*  58 */     return this.m_Object;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator getOthersIterator() {
/*  63 */     return this.m_Others.iterator();
/*     */   }
/*     */   
/*     */   public Object getAnObject() {
/*  67 */     if (this.m_Others == null) {
/*  68 */       return this.m_Object;
/*     */     }
/*  70 */     return this.m_Others.get(m_Random.nextInt(this.m_Others.size()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(Collection<Comparable> lst) {
/*  75 */     if (this.m_Others == null) {
/*  76 */       lst.add(this.m_Object);
/*     */     } else {
/*  78 */       for (int i = 0; i < this.m_Others.size(); i++) {
/*  79 */         lst.add(this.m_Others.get(i));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void looseOthers() {
/*  85 */     this.m_Object = this.m_Others.get(0);
/*  86 */     this.m_Others = null;
/*     */   }
/*     */   
/*     */   public void removeFirst() {
/*  90 */     this.m_Others.remove(m_Random.nextInt(this.m_Others.size()));
/*  91 */     if (this.m_Others.size() == 1) looseOthers(); 
/*     */   }
/*     */   
/*     */   public int addIfNotIn(Comparable cmp) {
/*  95 */     if (this.m_Others == null) {
/*  96 */       if (cmp.equals(this.m_Object))
/*     */       {
/*  98 */         return 0;
/*     */       }
/* 100 */       this.m_Others = new ArrayList();
/* 101 */       this.m_Others.add(this.m_Object);
/* 102 */       this.m_Others.add(cmp);
/* 103 */       this.m_Object = null;
/* 104 */       return 1;
/*     */     } 
/*     */     
/* 107 */     for (int i = 0; i < this.m_Others.size(); i++) {
/* 108 */       Comparable cmp_other = this.m_Others.get(i);
/* 109 */       if (cmp.equals(cmp_other))
/*     */       {
/* 111 */         return 0;
/*     */       }
/*     */     } 
/* 114 */     this.m_Others.add(cmp);
/* 115 */     return 1;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamTreeElem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */