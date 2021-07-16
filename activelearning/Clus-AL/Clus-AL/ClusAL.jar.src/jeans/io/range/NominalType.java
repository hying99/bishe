/*    */ package jeans.io.range;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Hashtable;
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
/*    */ public class NominalType
/*    */   implements Serializable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected String[] m_Names;
/*    */   protected Hashtable m_Lookup;
/*    */   
/*    */   public NominalType(String[] names) {
/* 36 */     this.m_Names = names;
/*    */   }
/*    */   
/*    */   public void setReader(boolean reader) {
/* 40 */     if (reader) {
/* 41 */       this.m_Lookup = new Hashtable<>();
/* 42 */       for (int i = 0; i < this.m_Names.length; i++)
/* 43 */         this.m_Lookup.put(this.m_Names[i].toUpperCase(), new Integer(i)); 
/*    */     } else {
/* 45 */       this.m_Lookup = null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getName(int i) {
/* 50 */     return this.m_Names[i];
/*    */   }
/*    */   
/*    */   public int getValue(String name) {
/* 54 */     Integer val = (Integer)this.m_Lookup.get(name.toUpperCase());
/* 55 */     if (val == null) return -1; 
/* 56 */     return val.intValue();
/*    */   }
/*    */   
/*    */   public String getAllValues() {
/* 60 */     StringBuffer allowed = new StringBuffer();
/* 61 */     allowed.append("{");
/* 62 */     for (int i = 0; i < this.m_Names.length; i++) {
/* 63 */       if (i != 0) allowed.append(", "); 
/* 64 */       allowed.append(this.m_Names[i]);
/*    */     } 
/* 66 */     allowed.append("}");
/* 67 */     return allowed.toString();
/*    */   }
/*    */   
/*    */   public String getError(String name, String token) {
/* 71 */     return "'" + token + "' is illegal value for " + name + " allowed values are: " + getAllValues();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\range\NominalType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */