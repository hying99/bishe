/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.TuplePreproc;
/*    */ import clus.util.ClusException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
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
/*    */ public class ClassHierarchyPreproc
/*    */   implements TuplePreproc
/*    */ {
/*    */   protected ClassesAttrType m_Type;
/*    */   protected boolean m_IsSinglePass;
/*    */   protected boolean m_AddIntermediates;
/*    */   protected transient boolean[] m_Intermediates;
/*    */   protected transient ArrayList m_Scratch;
/*    */   
/*    */   public ClassHierarchyPreproc(ClassesAttrType type, boolean addinter) {
/* 40 */     this.m_Type = type;
/*    */ 
/*    */     
/* 43 */     this.m_IsSinglePass = type.getHier().isLocked();
/* 44 */     this.m_AddIntermediates = addinter;
/* 45 */     if (addinter && this.m_IsSinglePass) createScratch(); 
/*    */   }
/*    */   
/*    */   public boolean isAddIntermediateClasses() {
/* 49 */     return this.m_AddIntermediates;
/*    */   }
/*    */   
/*    */   public boolean isSinglePass() {
/* 53 */     return this.m_IsSinglePass;
/*    */   }
/*    */   
/*    */   public final ClassHierarchy getHier() {
/* 57 */     return this.m_Type.getHier();
/*    */   }
/*    */   
/*    */   public int getNbPasses() {
/* 61 */     return isSinglePass() ? 1 : 2;
/*    */   }
/*    */   
/*    */   public void preproc(int pass, DataTuple tuple) throws ClusException {
/* 65 */     ClassesTuple ct = (ClassesTuple)tuple.getObjVal(this.m_Type.getArrayIndex());
/* 66 */     if (!isSinglePass() && pass == 0) {
/* 67 */       ct.addToHierarchy(getHier());
/*    */     } else {
/* 69 */       ct.addHierarchyIndices(getHier());
/* 70 */       if (isAddIntermediateClasses()) addIntermediateElems(ct); 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void preprocSingle(DataTuple tuple) throws ClusException {
/* 75 */     ClassesTuple ct = (ClassesTuple)tuple.getObjVal(this.m_Type.getArrayIndex());
/* 76 */     ct.addHierarchyIndices(getHier());
/* 77 */     if (isAddIntermediateClasses()) addIntermediateElems(ct); 
/*    */   }
/*    */   
/*    */   public void done(int pass) throws ClusException {
/* 81 */     if (pass > 0)
/*    */       return; 
/* 83 */     if (!isSinglePass()) {
/* 84 */       getHier().initialize();
/* 85 */       if (isAddIntermediateClasses()) createScratch(); 
/*    */     } 
/*    */   }
/*    */   
/*    */   private void createScratch() {
/* 90 */     this.m_Intermediates = new boolean[getHier().getTotal()];
/* 91 */     this.m_Scratch = new ArrayList();
/*    */   }
/*    */   
/*    */   private void addIntermediateElems(ClassesTuple ct) throws ClusException {
/* 95 */     Arrays.fill(this.m_Intermediates, false);
/*    */     
/* 97 */     ct.addIntermediateElems(getHier(), this.m_Intermediates, this.m_Scratch);
/* 98 */     this.m_Scratch.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\ClassHierarchyPreproc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */