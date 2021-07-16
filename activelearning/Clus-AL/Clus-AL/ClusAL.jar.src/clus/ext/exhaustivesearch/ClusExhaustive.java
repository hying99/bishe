/*    */ package clus.ext.exhaustivesearch;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.ext.beamsearch.ClusBeamModel;
/*    */ import clus.ext.beamsearch.ClusBeamTreeElem;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.TreeMap;
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
/*    */ public class ClusExhaustive
/*    */ {
/*    */   TreeMap m_Tree;
/*    */   Collection m_Values;
/*    */   int m_MaxWidth;
/*    */   int m_CrWidth;
/*    */   boolean m_RemoveEqualHeur;
/*    */   
/*    */   public ClusExhaustive(int width) {
/* 40 */     this.m_Tree = new TreeMap<>();
/* 41 */     this.m_Values = this.m_Tree.values();
/* 42 */     this.m_MaxWidth = width;
/*    */   }
/*    */ 
/*    */   
/*    */   public int addIfNotIn(ClusBeamModel model) {
/* 47 */     Double key = new Double(model.getValue());
/* 48 */     ClusBeamTreeElem found = (ClusBeamTreeElem)this.m_Tree.get(key);
/* 49 */     if (found == null) {
/* 50 */       this.m_Tree.put(key, new ClusBeamTreeElem(model));
/* 51 */       return 1;
/* 52 */     }  return 0;
/*    */   }
/*    */   
/*    */   public void print(PrintWriter wrt, int best_n) {
/* 56 */     ArrayList<ClusBeamModel> lst = toArray();
/* 57 */     for (int i = 0; i < Math.min(best_n, lst.size()); i++) {
/* 58 */       if (i != 0) wrt.println(); 
/* 59 */       ClusBeamModel mdl = lst.get(lst.size() - i - 1);
/* 60 */       ClusNode tree = (ClusNode)mdl.getModel();
/* 61 */       double error = Double.NaN;
/* 62 */       wrt.println("Model: " + i + " value: " + mdl.getValue() + " error: " + error + " parent: " + mdl.getParentModelIndex());
/* 63 */       tree.printModel(wrt);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Iterator getIterator() {
/* 68 */     return this.m_Values.iterator();
/*    */   }
/*    */   
/*    */   public ArrayList toArray() {
/* 72 */     ArrayList lst = new ArrayList();
/* 73 */     Iterator<ClusBeamTreeElem> iter = this.m_Values.iterator();
/* 74 */     while (iter.hasNext()) {
/* 75 */       ClusBeamTreeElem elem = iter.next();
/* 76 */       elem.addAll(lst);
/*    */     } 
/* 78 */     return lst;
/*    */   }
/*    */   
/*    */   public int getMaxWidth() {
/* 82 */     return this.m_MaxWidth;
/*    */   }
/*    */   
/*    */   public int getCrWidth() {
/* 86 */     return this.m_CrWidth;
/*    */   }
/*    */   
/*    */   public void print() {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\exhaustivesearch\ClusExhaustive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */