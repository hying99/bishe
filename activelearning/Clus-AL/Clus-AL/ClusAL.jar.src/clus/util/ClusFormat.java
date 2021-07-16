/*    */ package clus.util;
/*    */ 
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintWriter;
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.DecimalFormatSymbols;
/*    */ import java.text.NumberFormat;
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
/*    */ public class ClusFormat
/*    */ {
/* 30 */   public static final NumberFormat ONE_AFTER_DOT = makeNAfterDot(1);
/* 31 */   public static final NumberFormat TWO_AFTER_DOT = makeNAfterDot(2);
/* 32 */   public static final NumberFormat THREE_AFTER_DOT = makeNAfterDot(3);
/* 33 */   public static final NumberFormat MM3_AFTER_DOT = makeNAfterDot2(3);
/* 34 */   public static final NumberFormat SIX_AFTER_DOT = makeNAfterDot(6);
/* 35 */   public static final NumberFormat FOUR_AFTER_DOT = makeNAfterDot(4);
/* 36 */   public static final PrintWriter OUT_WRITER = new PrintWriter(new OutputStreamWriter(System.out));
/*    */   
/*    */   public static NumberFormat makeNAfterDot(int n) {
/* 39 */     NumberFormat fr = NumberFormat.getInstance();
/* 40 */     fr.setMaximumFractionDigits(n);
/*    */     try {
/* 42 */       DecimalFormat df = (DecimalFormat)fr;
/* 43 */       DecimalFormatSymbols sym = df.getDecimalFormatSymbols();
/* 44 */       sym.setDecimalSeparator('.');
/* 45 */       df.setGroupingUsed(false);
/* 46 */       df.setDecimalFormatSymbols(sym);
/* 47 */     } catch (ClassCastException classCastException) {}
/* 48 */     return fr;
/*    */   }
/*    */   
/*    */   public static NumberFormat makeNAfterDot2(int n) {
/* 52 */     NumberFormat fr = makeNAfterDot(n);
/* 53 */     fr.setMinimumFractionDigits(n);
/* 54 */     return fr;
/*    */   }
/*    */   
/*    */   public static void printArray(PrintWriter out, double[] a1, double[] a2, NumberFormat nf) {
/* 58 */     for (int i = 0; i < a1.length; i++) {
/* 59 */       if (i != 0) out.print(", "); 
/* 60 */       if (a2[i] == 0.0D) { out.print(nf.format(0.0D)); }
/* 61 */       else { out.print(nf.format(a1[i] / a2[i])); }
/*    */     
/*    */     } 
/*    */   }
/*    */   public static void printArray(PrintWriter out, double[] a1, NumberFormat nf) {
/* 66 */     for (int i = 0; i < a1.length; i++) {
/* 67 */       if (i != 0) out.print(", "); 
/* 68 */       out.print(nf.format(a1[i]));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clu\\util\ClusFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */