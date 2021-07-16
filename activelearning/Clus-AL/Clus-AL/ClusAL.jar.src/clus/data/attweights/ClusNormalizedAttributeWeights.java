/*    */ package clus.data.attweights;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.util.ClusFormat;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusNormalizedAttributeWeights
/*    */   extends ClusAttributeWeights
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected double[] m_NormalizationWeights;
/*    */   
/*    */   public ClusNormalizedAttributeWeights(ClusAttributeWeights norm) {
/* 42 */     super(norm.getNbAttributes());
/* 43 */     this.m_NormalizationWeights = norm.getWeights();
/*    */   }
/*    */   
/*    */   public double getWeight(ClusAttrType atttype) {
/* 47 */     int idx = atttype.getIndex();
/*    */ 
/*    */ 
/*    */     
/* 51 */     return this.m_Weights[idx] * this.m_NormalizationWeights[idx];
/*    */   }
/*    */   
/*    */   public double getWeight(int idx) {
/* 55 */     return this.m_Weights[idx] * this.m_NormalizationWeights[idx];
/*    */   }
/*    */   
/*    */   public double getComposeWeight(ClusAttrType atttype) {
/* 59 */     return this.m_Weights[atttype.getIndex()];
/*    */   }
/*    */   
/*    */   public double getNormalizationWeight(ClusAttrType atttype) {
/* 63 */     return this.m_NormalizationWeights[atttype.getIndex()];
/*    */   }
/*    */   
/*    */   public double[] getNormalizationWeights() {
/* 67 */     return this.m_NormalizationWeights;
/*    */   }
/*    */   
/*    */   public void setAllNormalizationWeights(double value) {
/* 71 */     Arrays.fill(this.m_NormalizationWeights, value);
/*    */   }
/*    */   
/*    */   public String getName(ClusAttrType[] type) {
/* 75 */     if (type.length > 50) {
/* 76 */       return "Weights (" + type.length + ")";
/*    */     }
/* 78 */     StringBuffer buf = new StringBuffer();
/* 79 */     buf.append("Weights C=["); int i;
/* 80 */     for (i = 0; i < type.length; i++) {
/* 81 */       if (i != 0) buf.append(","); 
/* 82 */       buf.append(ClusFormat.THREE_AFTER_DOT.format(getComposeWeight(type[i])));
/*    */     } 
/* 84 */     buf.append("], N=[");
/* 85 */     for (i = 0; i < type.length; i++) {
/* 86 */       if (i != 0) buf.append(","); 
/* 87 */       buf.append(ClusFormat.THREE_AFTER_DOT.format(getNormalizationWeight(type[i])));
/*    */     } 
/* 89 */     buf.append("]");
/* 90 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\attweights\ClusNormalizedAttributeWeights.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */