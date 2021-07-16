/*    */ package clus.activelearning.indexing;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LabelIndex
/*    */   implements Comparable
/*    */ {
/*    */   private int m_Index;
/*    */   private String m_Label;
/*    */   private double m_Measure;
/*    */   
/*    */   public LabelIndex(int index, String label) {
/* 19 */     this.m_Index = index;
/* 20 */     this.m_Label = label;
/*    */   }
/*    */   
/*    */   public LabelIndex(int index, String label, double measure) {
/* 24 */     this.m_Index = index;
/* 25 */     this.m_Label = label;
/* 26 */     this.m_Measure = measure;
/*    */   }
/*    */   
/*    */   public void setValues(int index, String label, double measure) {
/* 30 */     setIndex(index);
/* 31 */     setLabel(label);
/* 32 */     setMeasure(measure);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getIndex() {
/* 42 */     return this.m_Index;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setIndex(int m_Index) {
/* 49 */     this.m_Index = m_Index;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getLabel() {
/* 56 */     return this.m_Label;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setLabel(String m_Label) {
/* 63 */     this.m_Label = m_Label;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getMeasure() {
/* 70 */     return this.m_Measure;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setMeasure(double m_Measure) {
/* 77 */     this.m_Measure = m_Measure;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object object) {
/* 82 */     LabelIndex li = (LabelIndex)object;
/* 83 */     if (getMeasure() < li.getMeasure())
/* 84 */       return -1; 
/* 85 */     if (getMeasure() > li.getMeasure()) {
/* 86 */       return 1;
/*    */     }
/* 88 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\indexing\LabelIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */