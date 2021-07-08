/*    */ package sit.searchAlgorithm;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import sit.TargetSet;
/*    */ 
/*    */ public class TC
/*    */   extends SearchAlgorithmImpl
/*    */ {
/*    */   public String getName() {
/* 12 */     return "TC";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TargetSet search(ClusAttrType mainTarget, TargetSet candidates) {
/* 18 */     Iterator<ClusAttrType> i = candidates.iterator();
/* 19 */     Iterator<ClusAttrType> i2 = candidates.iterator();
/* 20 */     while (i.hasNext()) {
/* 21 */       ClusAttrType target = i.next();
/* 22 */       i2 = candidates.iterator();
/*    */       
/* 24 */       TargetSet base = new TargetSet(target);
/* 25 */       double base_err = eval(base, target);
/*    */       
/* 27 */       ArrayList<Double> l = new ArrayList();
/* 28 */       while (i2.hasNext()) {
/*    */         
/* 30 */         TargetSet test = new TargetSet(target);
/* 31 */         ClusAttrType from = i2.next();
/* 32 */         test.add(from);
/*    */         
/* 34 */         double err = eval(test, target);
/* 35 */         l.add(Double.valueOf((err - base_err) / base_err));
/*    */       } 
/*    */       
/* 38 */       System.out.print("[");
/* 39 */       for (int c = l.size() - 1; c > 0; c--) {
/* 40 */         System.out.print((new StringBuilder()).append(l.get(c)).append(",").toString());
/*    */       }
/* 42 */       System.out.println((new StringBuilder()).append(l.get(0)).append("],").toString());
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 49 */     return candidates;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\TC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */