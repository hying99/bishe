/*    */ package jeans.io.ini;
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
/*    */ public class INIFileString
/*    */   extends INIFileEntry
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected String m_Value;
/*    */   
/*    */   public INIFileString(String name) {
/* 32 */     this(name, "");
/*    */   }
/*    */   
/*    */   public INIFileString(String name, String value) {
/* 36 */     super(name);
/* 37 */     this.m_Value = value;
/*    */   }
/*    */   
/*    */   public INIFileNode cloneNode() {
/* 41 */     return new INIFileString(getName(), getValue());
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 45 */     return this.m_Value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 49 */     this.m_Value = value;
/*    */   }
/*    */   
/*    */   public String getStringValue() {
/* 53 */     return getValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */