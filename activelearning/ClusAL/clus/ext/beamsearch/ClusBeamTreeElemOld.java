/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusBeamTreeElemOld
/*     */ {
/*     */   protected Comparable m_Object;
/*     */   protected TreeMap m_Others;
/*     */   protected Collection m_OthersList;
/*     */   protected int m_Count;
/*     */   
/*     */   public ClusBeamTreeElemOld(ClusBeamModel model) {
/*  38 */     this.m_Object = model;
/*  39 */     this.m_Count = 1;
/*     */   }
/*     */   
/*     */   public boolean hasList() {
/*  43 */     return (this.m_Others != null);
/*     */   }
/*     */   
/*     */   public int getCount() {
/*  47 */     return this.m_Count;
/*     */   }
/*     */   
/*     */   public Object getObject() {
/*  51 */     return this.m_Object;
/*     */   }
/*     */   
/*     */   public Iterator getOthersIterator() {
/*  55 */     return this.m_OthersList.iterator();
/*     */   }
/*     */   
/*     */   public Object getAnObject() {
/*  59 */     if (this.m_Others == null) {
/*  60 */       return this.m_Object;
/*     */     }
/*  62 */     Integer key = (Integer)this.m_Others.firstKey();
/*  63 */     ArrayList list = (ArrayList)this.m_Others.get(key);
/*  64 */     return list.get(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addAll(Collection<Comparable> lst) {
/*  69 */     if (this.m_Others == null) {
/*  70 */       lst.add(this.m_Object);
/*     */     } else {
/*  72 */       Iterator<ArrayList> iter = this.m_OthersList.iterator();
/*  73 */       while (iter.hasNext()) {
/*  74 */         ArrayList<Comparable> arr = iter.next();
/*  75 */         for (int i = 0; i < arr.size(); i++) {
/*  76 */           lst.add(arr.get(i));
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void looseOthers() {
/*  83 */     Integer key = (Integer)this.m_Others.firstKey();
/*  84 */     ArrayList<Comparable> list = (ArrayList)this.m_Others.get(key);
/*  85 */     if (list.size() != 1) {
/*  86 */       throw new Error("ClusBeamTreeElem::removeFirst(): count is inconsistent");
/*     */     }
/*  88 */     this.m_Object = list.get(0);
/*  89 */     this.m_Others = null;
/*  90 */     this.m_OthersList = null;
/*     */   }
/*     */   
/*     */   public void addModelToSet(Comparable cmp) {
/*  94 */     Integer key = new Integer(cmp.hashCode());
/*  95 */     ArrayList<Comparable> list = (ArrayList)this.m_Others.get(key);
/*  96 */     if (list == null) {
/*  97 */       ArrayList<Comparable> nlist = new ArrayList();
/*  98 */       nlist.add(cmp);
/*  99 */       this.m_Others.put(key, nlist);
/*     */     } else {
/* 101 */       list.add(cmp);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void removeFirst() {
/* 106 */     Integer key = (Integer)this.m_Others.firstKey();
/* 107 */     ArrayList list = (ArrayList)this.m_Others.get(key);
/* 108 */     if (list.size() == 1) {
/* 109 */       this.m_Others.remove(key);
/*     */     } else {
/* 111 */       list.remove(list.size() - 1);
/*     */     } 
/*     */     
/* 114 */     this.m_Count--;
/* 115 */     if (this.m_Count == 1) looseOthers(); 
/*     */   }
/*     */   
/*     */   public int addIfNotIn(Comparable cmp) {
/* 119 */     if (this.m_Others == null) {
/* 120 */       if (cmp.equals(this.m_Object)) {
/* 121 */         return 0;
/*     */       }
/* 123 */       this.m_Others = new TreeMap<>();
/* 124 */       this.m_OthersList = this.m_Others.values();
/* 125 */       addModelToSet(this.m_Object);
/* 126 */       addModelToSet(cmp);
/* 127 */       this.m_Object = null;
/* 128 */       this.m_Count++;
/* 129 */       return 1;
/*     */     } 
/*     */     
/* 132 */     Integer key = new Integer(cmp.hashCode());
/* 133 */     ArrayList<Comparable> list = (ArrayList)this.m_Others.get(key);
/* 134 */     if (list == null) {
/* 135 */       ArrayList<Comparable> nlist = new ArrayList();
/* 136 */       nlist.add(cmp);
/* 137 */       this.m_Others.put(key, nlist);
/* 138 */       this.m_Count++;
/* 139 */       return 1;
/*     */     } 
/* 141 */     for (int i = 0; i < list.size(); i++) {
/* 142 */       if (cmp.equals(list.get(i))) {
/* 143 */         return 0;
/*     */       }
/*     */     } 
/* 146 */     list.add(cmp);
/* 147 */     this.m_Count++;
/* 148 */     return 1;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamTreeElemOld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */