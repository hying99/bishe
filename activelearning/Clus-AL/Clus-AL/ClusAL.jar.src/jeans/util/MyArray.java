/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MyArray
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   private Object[] m_Objects;
/*     */   private int m_Size;
/*     */   
/*     */   public MyArray() {
/*  37 */     this.m_Objects = new Object[0];
/*  38 */     this.m_Size = 0;
/*     */   }
/*     */   
/*     */   public MyArray(int cap) {
/*  42 */     this.m_Objects = new Object[cap];
/*  43 */     this.m_Size = 0;
/*     */   }
/*     */   
/*     */   public final void sort() {
/*  47 */     Arrays.sort(this.m_Objects, 0, this.m_Size);
/*     */   }
/*     */   
/*     */   public final void setSize(int size) {
/*  51 */     this.m_Objects = new Object[size];
/*     */   }
/*     */ 
/*     */   
/*     */   public final void addElement(Object element) {
/*  56 */     if (this.m_Size == this.m_Objects.length) {
/*  57 */       Object[] newObjects = new Object[2 * this.m_Objects.length + 1];
/*  58 */       System.arraycopy(this.m_Objects, 0, newObjects, 0, this.m_Size);
/*  59 */       this.m_Objects = newObjects;
/*     */     } 
/*  61 */     this.m_Objects[this.m_Size++] = element;
/*     */   }
/*     */   
/*     */   public final Object[] getObjects() {
/*  65 */     return this.m_Objects;
/*     */   }
/*     */   
/*     */   public final Object elementAt(int index) {
/*  69 */     return this.m_Objects[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public final void insertElementAt(Object element, int index) {
/*  74 */     if (this.m_Size < this.m_Objects.length) {
/*  75 */       System.arraycopy(this.m_Objects, index, this.m_Objects, index + 1, this.m_Size - index);
/*  76 */       this.m_Objects[index] = element;
/*     */     } else {
/*  78 */       Object[] nObjs = new Object[2 * this.m_Objects.length + 1];
/*  79 */       System.arraycopy(this.m_Objects, 0, nObjs, 0, index);
/*  80 */       nObjs[index] = element;
/*  81 */       System.arraycopy(this.m_Objects, index, nObjs, index + 1, this.m_Size - index);
/*  82 */       this.m_Objects = nObjs;
/*     */     } 
/*  84 */     this.m_Size++;
/*     */   }
/*     */   
/*     */   public final void removeElementAt(int index) {
/*  88 */     System.arraycopy(this.m_Objects, index + 1, this.m_Objects, index, this.m_Size - index - 1);
/*  89 */     this.m_Objects[--this.m_Size] = null;
/*     */   }
/*     */   
/*     */   public final void removeAllElements() {
/*  93 */     for (int i = 0; i < this.m_Size; ) { this.m_Objects[i] = null; i++; }
/*  94 */      this.m_Size = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void removeElement(Object element) {
/*  99 */     int index = 0;
/* 100 */     for (int i = 0; i < this.m_Size; i++) {
/* 101 */       if (this.m_Objects[i].equals(element))
/* 102 */         index = i; 
/*     */     } 
/* 104 */     removeElementAt(index);
/*     */   }
/*     */   
/*     */   public final void setElementAt(Object element, int index) {
/* 108 */     this.m_Objects[index] = element;
/* 109 */     if (index >= this.m_Size) this.m_Size = index + 1; 
/*     */   }
/*     */   
/*     */   public final int size() {
/* 113 */     return this.m_Size;
/*     */   }
/*     */   
/*     */   public final void setCapacity(int cap) {
/* 117 */     Object[] newObjects = new Object[cap];
/* 118 */     System.arraycopy(this.m_Objects, 0, newObjects, 0, Math.min(cap, this.m_Size));
/* 119 */     this.m_Objects = newObjects;
/* 120 */     if (this.m_Objects.length < this.m_Size) this.m_Size = this.m_Objects.length; 
/*     */   }
/*     */   
/*     */   public final void swap(int first, int second) {
/* 124 */     Object help = this.m_Objects[first];
/* 125 */     this.m_Objects[first] = this.m_Objects[second];
/* 126 */     this.m_Objects[second] = help;
/*     */   }
/*     */   
/*     */   public final void trimToSize() {
/* 130 */     Object[] newObjects = new Object[this.m_Size];
/* 131 */     System.arraycopy(this.m_Objects, 0, newObjects, 0, this.m_Size);
/* 132 */     this.m_Objects = newObjects;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\MyArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */