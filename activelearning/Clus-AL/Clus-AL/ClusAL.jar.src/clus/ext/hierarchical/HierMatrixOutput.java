/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Arrays;
/*    */ import jeans.util.array.MDoubleArray;
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
/*    */ public class HierMatrixOutput
/*    */ {
/*    */   public static void writeExamples(RowData data, ClassHierarchy hier) {
/*    */     try {
/* 37 */       PrintWriter wrt = data.getSchema().getSettings().getFileAbsoluteWriter("examples.matrix");
/* 38 */       writeHeader(hier, wrt);
/* 39 */       ClassesAttrType type = hier.getType();
/* 40 */       int sidx = type.getArrayIndex();
/* 41 */       double[] vector = new double[hier.getTotal()];
/* 42 */       for (int i = 0; i < data.getNbRows(); i++) {
/* 43 */         Arrays.fill(vector, 0.0D);
/* 44 */         DataTuple tuple = data.getTuple(i);
/* 45 */         ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/* 46 */         for (int j = 0; j < tp.getNbClasses(); j++) {
/* 47 */           ClassesValue val = tp.getClass(j);
/* 48 */           vector[val.getIndex()] = 1.0D;
/*    */         } 
/* 50 */         wrt.println(MDoubleArray.toString(vector));
/*    */       } 
/* 52 */       wrt.close();
/* 53 */     } catch (IOException e) {
/* 54 */       System.out.println("Error: " + e.getMessage());
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void writeHeader(ClassHierarchy hier, PrintWriter wrt) {
/* 59 */     wrt.print("[");
/* 60 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 61 */       if (i != 0) wrt.print(","); 
/* 62 */       wrt.print(String.valueOf(hier.getWeight(i)));
/*    */     } 
/* 64 */     wrt.println("]");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierMatrixOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */