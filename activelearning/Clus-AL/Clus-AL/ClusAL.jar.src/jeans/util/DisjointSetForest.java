/*    */ package jeans.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DisjointSetForest
/*    */ {
/*    */   protected int m_Size;
/*    */   protected int[] m_P;
/*    */   protected int[] m_Rank;
/*    */   protected int[] m_ComprTable;
/*    */   protected int[] m_Map;
/*    */   
/*    */   public DisjointSetForest(int size) {
/* 34 */     this.m_Size = size;
/* 35 */     this.m_P = new int[size];
/* 36 */     this.m_Rank = new int[size];
/* 37 */     this.m_ComprTable = new int[size];
/* 38 */     this.m_Map = new int[size];
/* 39 */     for (int i = 0; i < size; i++) {
/* 40 */       this.m_Map[i] = -1;
/*    */     }
/*    */   }
/*    */   
/*    */   public void makeSets(int nb) {
/* 45 */     for (int i = 0; i < nb; i++) {
/* 46 */       makeSet(i);
/*    */     }
/*    */   }
/*    */   
/*    */   public void makeSet(int x) {
/* 51 */     this.m_P[x] = x;
/* 52 */     this.m_Rank[x] = 0;
/*    */   }
/*    */   
/*    */   public void union(int x, int y) {
/* 56 */     link(findSet(x), findSet(y));
/*    */   }
/*    */   
/*    */   public void link(int x, int y) {
/* 60 */     if (this.m_Rank[x] > this.m_Rank[y]) {
/* 61 */       this.m_P[y] = x;
/*    */     } else {
/* 63 */       this.m_P[x] = y;
/* 64 */       if (this.m_Rank[x] == this.m_Rank[y]) {
/* 65 */         this.m_Rank[y] = this.m_Rank[y] + 1;
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public int findSet(int x) {
/* 72 */     int idx = 0;
/* 73 */     while (this.m_P[x] != x) {
/* 74 */       this.m_ComprTable[idx++] = x;
/* 75 */       x = this.m_P[x];
/*    */     } 
/*    */     
/* 78 */     while (idx > 0) {
/* 79 */       this.m_P[this.m_ComprTable[--idx]] = x;
/*    */     }
/* 81 */     return x;
/*    */   }
/*    */   
/*    */   public int getComponent(int x) {
/* 85 */     return this.m_Map[findSet(x)];
/*    */   }
/*    */   
/*    */   public int numberComponents() {
/* 89 */     int idx = 0;
/* 90 */     for (int i = 0; i < this.m_Size; i++) {
/* 91 */       int x = findSet(i);
/* 92 */       if (this.m_Map[x] == -1) {
/* 93 */         this.m_Map[x] = idx++;
/*    */       }
/*    */     } 
/* 96 */     return idx;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\DisjointSetForest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */