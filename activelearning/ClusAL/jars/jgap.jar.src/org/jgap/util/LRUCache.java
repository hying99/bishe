/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class LRUCache
/*    */   extends LinkedHashMap {
/*    */   private int m_maxEntries;
/*    */   
/*    */   public LRUCache(int a_maxExtries) {
/* 11 */     super(a_maxExtries + 1, 0.75F, true);
/* 12 */     this.m_maxEntries = a_maxExtries;
/*    */   }
/*    */   
/*    */   public boolean removeEldestEntry(Map.Entry eldest) {
/* 16 */     return (size() > this.m_maxEntries);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\LRUCache.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */