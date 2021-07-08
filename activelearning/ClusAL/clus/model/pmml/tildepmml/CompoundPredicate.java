/*    */ package clus.model.pmml.tildepmml;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.util.ArrayList;
/*    */ import jeans.util.StringUtils;
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
/*    */ 
/*    */ public class CompoundPredicate
/*    */ {
/* 32 */   protected ArrayList m_ItemSets = new ArrayList();
/*    */   
/*    */   public void addItemset(Itemset set) {
/* 35 */     this.m_ItemSets.add(set);
/*    */   }
/*    */   
/*    */   public void print(PrintWriter out, int tabs) {
/* 39 */     StringUtils.printTabs(out, tabs);
/* 40 */     out.println("<CompoundPredicate booleanOperator=\"and\">");
/*    */     
/* 42 */     for (int i = 0; i < this.m_ItemSets.size(); i++) {
/* 43 */       Itemset set = this.m_ItemSets.get(i);
/*    */       
/* 45 */       StringUtils.printTabs(out, tabs);
/* 46 */       out.println("<ItemsetRef itemsetRef=\"" + set.getId() + "\" />");
/*    */     } 
/*    */     
/* 49 */     StringUtils.printTabs(out, tabs);
/* 50 */     out.println("</CompoundPredicate>");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\pmml\tildepmml\CompoundPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */