/*    */ package clus.error.multiscore;
/*    */ 
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.main.Settings;
/*    */ import clus.util.ClusException;
/*    */ import java.util.StringTokenizer;
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
/*    */ public class MultiScore
/*    */ {
/*    */   protected double[] m_Thresholds;
/*    */   protected int m_NbValues;
/*    */   
/*    */   public MultiScore(ClusSchema schema, Settings sett) throws ClusException {
/* 37 */     String val = "";
/* 38 */     int len = val.length();
/* 39 */     int nb_wanted = 0;
/*    */     try {
/* 41 */       if (len > 2 && val.charAt(0) == '{' && val.charAt(len - 1) == '}')
/* 42 */       { StringTokenizer tokens = new StringTokenizer(val.substring(1, len - 1), ", ");
/* 43 */         this.m_NbValues = tokens.countTokens();
/* 44 */         if (this.m_NbValues != nb_wanted)
/* 45 */           throw new ClusException("Not enough (" + this.m_NbValues + " < " + nb_wanted + ") thresholds given for multi-score"); 
/* 46 */         this.m_Thresholds = new double[this.m_NbValues];
/* 47 */         for (int i = 0; i < this.m_NbValues; ) { this.m_Thresholds[i] = Double.parseDouble(tokens.nextToken()); i++; }
/*    */          }
/* 49 */       else { double thr = Double.parseDouble(val);
/* 50 */         this.m_Thresholds = new double[this.m_NbValues = nb_wanted];
/* 51 */         for (int i = 0; i < this.m_NbValues; ) { this.m_Thresholds[i] = thr; i++; }  }
/*    */     
/* 53 */     } catch (NumberFormatException e) {
/* 54 */       throw new ClusException("Parse error reading multi-score values");
/*    */     } 
/*    */   }
/*    */   
/*    */   public int getNbTarget() {
/* 59 */     return this.m_NbValues;
/*    */   }
/*    */ 
/*    */   
/*    */   public int[] multiScore(double[] input) {
/* 64 */     int[] res = new int[input.length];
/* 65 */     for (int i = 0; i < this.m_NbValues; i++)
/* 66 */       res[i] = (input[i] > this.m_Thresholds[i]) ? 0 : 1; 
/* 67 */     return res;
/*    */   }
/*    */ 
/*    */   
/*    */   public void multiScore(double[] input, int[] res) {
/* 72 */     for (int i = 0; i < this.m_NbValues; i++)
/* 73 */       res[i] = (input[i] > this.m_Thresholds[i]) ? 0 : 1; 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\multiscore\MultiScore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */