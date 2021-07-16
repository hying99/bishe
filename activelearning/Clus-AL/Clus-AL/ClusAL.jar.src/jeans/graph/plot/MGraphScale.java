/*    */ package jeans.graph.plot;
/*    */ 
/*    */ import java.awt.Color;
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
/*    */ public abstract class MGraphScale
/*    */ {
/*    */   public static final int TICK_SIZE = 3;
/*    */   protected String m_sLabel;
/*    */   protected int m_iGap;
/* 33 */   protected Color m_LabelColor = Color.yellow;
/*    */   protected double m_dStep;
/* 35 */   protected MDouble m_MinValue = new MDouble(); protected double m_dRealStep;
/* 36 */   protected MDouble m_MaxValue = new MDouble();
/*    */   
/*    */   public void setRounding(int nb) {
/* 39 */     this.m_MinValue.setRounding(nb);
/* 40 */     this.m_MaxValue.setRounding(nb);
/*    */   }
/*    */   
/*    */   public void setStep(double step) {
/* 44 */     this.m_dStep = step;
/* 45 */     this.m_dRealStep = this.m_dStep;
/*    */   }
/*    */   
/*    */   public void setGap(int gap) {
/* 49 */     this.m_iGap = gap;
/*    */   }
/*    */   
/*    */   public void setMinMax(float min, float max) {
/* 53 */     this.m_MinValue.setFloorValue(min);
/* 54 */     this.m_MaxValue.setCeilValue(max);
/*    */   }
/*    */   
/*    */   public float getRealMin() {
/* 58 */     return this.m_MinValue.getFloat();
/*    */   }
/*    */   
/*    */   public float getRealMax() {
/* 62 */     return this.m_MaxValue.getFloat();
/*    */   }
/*    */   
/*    */   public void setLabel(String label) {
/* 66 */     this.m_sLabel = label;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\plot\MGraphScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */