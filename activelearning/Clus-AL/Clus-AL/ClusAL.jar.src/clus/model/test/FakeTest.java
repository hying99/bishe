/*    */ package clus.model.test;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import jeans.util.MyArray;
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
/*    */ 
/*    */ public class FakeTest
/*    */   extends NodeTest
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 35 */   protected MyArray m_Lines = new MyArray();
/*    */ 
/*    */   
/*    */   protected String m_Line;
/*    */ 
/*    */   
/*    */   public void setLine(String line) {
/* 42 */     this.m_Line = line;
/*    */   }
/*    */   
/*    */   public void addLine(String line) {
/* 46 */     this.m_Lines.addElement(line);
/*    */   }
/*    */   
/*    */   public int predictWeighted(DataTuple tuple) {
/* 50 */     return -1;
/*    */   }
/*    */   
/*    */   public boolean equals(NodeTest test) {
/* 54 */     return false;
/*    */   }
/*    */   
/*    */   public ClusAttrType getType() {
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setType(ClusAttrType type) {}
/*    */   
/*    */   public String getString() {
/* 65 */     return this.m_Line;
/*    */   }
/*    */   
/*    */   public int getNbLines() {
/* 69 */     return this.m_Lines.size();
/*    */   }
/*    */   
/*    */   public String getLine(int i) {
/* 73 */     return (String)this.m_Lines.elementAt(i);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\test\FakeTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */