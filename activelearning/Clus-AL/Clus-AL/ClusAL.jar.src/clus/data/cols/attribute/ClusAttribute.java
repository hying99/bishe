/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.cols.ColTarget;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.io.ClusSerializable;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.selection.ClusSelection;
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
/*    */ public abstract class ClusAttribute
/*    */   extends ClusSerializable
/*    */ {
/*    */   protected boolean m_Split;
/*    */   
/*    */   public void resize(int rows) {}
/*    */   
/*    */   public void setSplit(boolean split) {
/* 41 */     this.m_Split = split;
/*    */   }
/*    */   
/*    */   public boolean isSplit() {
/* 45 */     return this.m_Split;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 49 */     return getType().getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract ClusAttrType getType();
/*    */ 
/*    */   
/*    */   public void prepare() {}
/*    */ 
/*    */   
/*    */   public void unprepare() {}
/*    */ 
/*    */   
/*    */   public void findBestTest(MyArray leaves, ColTarget target, ClusStatManager smanager) {}
/*    */   
/*    */   public void split(ColTarget target) {}
/*    */   
/*    */   public ClusAttribute select(ClusSelection sel, int nbsel) {
/* 67 */     return null;
/*    */   }
/*    */   
/*    */   public void insert(ClusAttribute attr, ClusSelection sel, int nb_new) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\attribute\ClusAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */