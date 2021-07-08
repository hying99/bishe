/*    */ package jeans.util.graph;
/*    */ 
/*    */ import jeans.util.list.MyIntList;
/*    */ import jeans.util.list.MyList;
/*    */ import jeans.util.list.MyListIter;
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
/*    */ public class DirectedGraph
/*    */ {
/*    */   protected int m_NbNodes;
/*    */   protected boolean[][] m_AdjacencyMatrix;
/*    */   protected MyListIter[] m_Edges;
/*    */   
/*    */   public DirectedGraph(int nbNodes) {
/* 34 */     this.m_NbNodes = nbNodes;
/* 35 */     this.m_AdjacencyMatrix = new boolean[nbNodes][nbNodes];
/* 36 */     this.m_Edges = new MyListIter[nbNodes];
/* 37 */     for (int i = 0; i < nbNodes; i++) {
/* 38 */       this.m_Edges[i] = new MyListIter();
/*    */     }
/*    */   }
/*    */   
/*    */   public void addEdge(int i, int j) {
/* 43 */     if (!this.m_AdjacencyMatrix[i][j]) {
/* 44 */       this.m_AdjacencyMatrix[i][j] = true;
/* 45 */       this.m_Edges[i].addEnd((MyList)new MyIntList(j));
/*    */     } 
/*    */   }
/*    */   
/*    */   public int size() {
/* 50 */     return this.m_NbNodes;
/*    */   }
/*    */   
/*    */   public MyListIter getEdges(int i) {
/* 54 */     return this.m_Edges[i];
/*    */   }
/*    */   
/*    */   public void print() {
/* 58 */     for (int i = 0; i < size(); i++) {
/* 59 */       MyListIter iter = getEdges(i);
/* 60 */       iter.reset();
/* 61 */       MyList elem = iter.getNext();
/* 62 */       if (elem != null) {
/* 63 */         System.out.print(String.valueOf(i) + ": ");
/* 64 */         int idx = 0;
/* 65 */         while (elem != null) {
/* 66 */           if (idx != 0) System.out.print(", "); 
/* 67 */           System.out.print(String.valueOf(((MyIntList)elem).getValue()));
/* 68 */           elem = iter.getNext();
/* 69 */           idx++;
/*    */         } 
/* 71 */         System.out.println();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\graph\DirectedGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */