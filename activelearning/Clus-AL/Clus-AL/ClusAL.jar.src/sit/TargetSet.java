/*    */ package sit;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.ClusSchema;
/*    */ import java.util.Iterator;
/*    */ import java.util.TreeSet;
/*    */ import jeans.util.IntervalCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TargetSet
/*    */   extends TreeSet
/*    */ {
/*    */   public TargetSet(ClusAttrType MainTarget) {
/* 15 */     add((E)MainTarget);
/*    */   }
/*    */ 
/*    */   
/*    */   public TargetSet(TargetSet Other) {
/* 20 */     addAll(Other);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TargetSet() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TargetSet(ClusSchema schema, IntervalCollection targets) {
/* 35 */     targets.reset();
/* 36 */     while (targets.hasMoreInts()) {
/* 37 */       add((E)schema.getAttrType(targets.nextInt() - 1));
/*    */     }
/*    */   }
/*    */   
/*    */   public int getIndex(ClusAttrType target) {
/* 42 */     Object[] set = toArray();
/* 43 */     for (int i = 0; i < set.length; i++) {
/* 44 */       if (set[i].equals(target)) {
/* 45 */         return i;
/*    */       }
/*    */     } 
/* 48 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 53 */     Iterator<E> targets = iterator();
/*    */     
/* 55 */     String result = "";
/* 56 */     while (targets.hasNext()) {
/* 57 */       ClusAttrType target = (ClusAttrType)targets.next();
/* 58 */       int idx = target.getIndex();
/* 59 */       result = result.concat(" ");
/* 60 */       result = result.concat(Integer.toString(idx + 1));
/*    */     } 
/*    */ 
/*    */     
/* 64 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\sit\TargetSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */