/*    */ package clus.tools.optimization.de;
/*    */ 
/*    */ import clus.util.ClusFormat;
/*    */ import java.text.NumberFormat;
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DeInd
/*    */ {
/* 46 */   private ArrayList<Double> m_Genes = new ArrayList<>();
/*    */   public double m_Fitness;
/*    */   
/*    */   public void setGenes(ArrayList<Double> genes) {
/* 50 */     this.m_Genes = genes;
/*    */   }
/*    */   
/*    */   public ArrayList<Double> getGenes() {
/* 54 */     return this.m_Genes;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int evaluate(DeProbl probl, int num_eval) {
/* 64 */     this.m_Fitness = probl.calcFitness(this.m_Genes);
/* 65 */     return num_eval + 1;
/*    */   }
/*    */   
/*    */   public String getIndString() {
/* 69 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/*    */     
/* 71 */     String result = "";
/* 72 */     result = result + fr.format(this.m_Fitness) + "\t";
/* 73 */     for (int i = 0; i < this.m_Genes.size(); i++) {
/* 74 */       result = result + fr.format(this.m_Genes.get(i)) + "\t";
/*    */     }
/* 76 */     return result;
/*    */   }
/*    */   
/*    */   public DeInd copy(DeInd original) {
/* 80 */     this.m_Fitness = original.m_Fitness;
/* 81 */     this.m_Genes = original.m_Genes;
/* 82 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\de\DeInd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */