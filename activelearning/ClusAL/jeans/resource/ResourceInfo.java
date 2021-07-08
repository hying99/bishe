/*    */ package jeans.resource;
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
/*    */ public class ResourceInfo
/*    */ {
/*    */   protected static boolean m_LibLoaded = false;
/*    */   
/*    */   public static final boolean isLibLoaded() {
/* 30 */     return m_LibLoaded;
/*    */   }
/*    */   
/*    */   public static final long getTime() {
/* 34 */     if (isLibLoaded()) return getCPUTime(); 
/* 35 */     return System.currentTimeMillis();
/*    */   }
/*    */   
/*    */   public static final long getMemory() {
/* 39 */     if (isLibLoaded()) return getMemorySize(); 
/* 40 */     return -1L;
/*    */   }
/*    */   
/*    */   public static native long getCPUTime();
/*    */   
/*    */   public static native long getMemorySize();
/*    */   
/*    */   public static final void loadLibrary(boolean test) {
/*    */     try {
/* 49 */       System.loadLibrary("ResourceInfo");
/* 50 */       m_LibLoaded = true;
/* 51 */     } catch (UnsatisfiedLinkError e) {
/* 52 */       if (test) {
/* 53 */         System.out.println("Error loading resource info: " + e.getMessage());
/* 54 */         System.out.println("Value of java.library.path: " + System.getProperty("java.library.path"));
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\resource\ResourceInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */