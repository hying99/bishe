/*     */ package clus.data.attweights;
/*     */ 
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusAttributeWeights
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public double[] m_Weights;
/*     */   
/*     */   public ClusAttributeWeights(int nbAttr) {
/*  43 */     this.m_Weights = new double[nbAttr];
/*     */   }
/*     */   
/*     */   public double getWeight(ClusAttrType atttype) {
/*  47 */     return this.m_Weights[atttype.getIndex()];
/*     */   }
/*     */   
/*     */   public double getWeight(int i) {
/*  51 */     return this.m_Weights[i];
/*     */   }
/*     */   
/*     */   public void setWeight(ClusAttrType atttype, double weight) {
/*  55 */     this.m_Weights[atttype.getIndex()] = weight;
/*     */   }
/*     */   
/*     */   public void setWeight(int attidx, double weight) {
/*  59 */     this.m_Weights[attidx] = weight;
/*     */   }
/*     */   
/*     */   public void setAllWeights(double value) {
/*  63 */     Arrays.fill(this.m_Weights, value);
/*     */   }
/*     */   
/*     */   public int getNbAttributes() {
/*  67 */     return this.m_Weights.length;
/*     */   }
/*     */   
/*     */   public double[] getWeights() {
/*  71 */     return this.m_Weights;
/*     */   }
/*     */   
/*     */   public void copyFrom(ClusAttributeWeights other) {
/*  75 */     System.arraycopy(other.getWeights(), 0, getWeights(), 0, getNbAttributes());
/*     */   }
/*     */   
/*     */   public String getName() {
/*  79 */     if (getNbAttributes() > 10) {
/*  80 */       return "Weights (" + getNbAttributes() + ")";
/*     */     }
/*  82 */     StringBuffer buf = new StringBuffer();
/*  83 */     buf.append("Weights [");
/*  84 */     for (int i = 0; i < getNbAttributes(); i++) {
/*  85 */       if (i != 0) buf.append(","); 
/*  86 */       buf.append(ClusFormat.THREE_AFTER_DOT.format(getWeight(i)));
/*     */     } 
/*  88 */     buf.append("]");
/*  89 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName(ClusAttrType[] type) {
/*  94 */     if (type == null) {
/*  95 */       return getName();
/*     */     }
/*  97 */     if (type.length > 10) {
/*  98 */       return "Weights (" + type.length + ")";
/*     */     }
/* 100 */     StringBuffer buf = new StringBuffer();
/* 101 */     buf.append("Weights [");
/* 102 */     for (int i = 0; i < type.length; i++) {
/* 103 */       if (i != 0) buf.append(","); 
/* 104 */       buf.append(ClusFormat.THREE_AFTER_DOT.format(getWeight(type[i])));
/*     */     } 
/* 106 */     buf.append("]");
/* 107 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\attweights\ClusAttributeWeights.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */