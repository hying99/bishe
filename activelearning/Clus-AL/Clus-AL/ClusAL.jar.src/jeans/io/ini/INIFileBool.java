/*    */ package jeans.io.ini;
/*    */ 
/*    */ import jeans.util.StringUtils;
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
/*    */ public class INIFileBool
/*    */   extends INIFileEntry
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected boolean m_Value;
/*    */   
/*    */   public INIFileBool(String name) {
/* 34 */     this(name, false);
/*    */   }
/*    */   
/*    */   public INIFileBool(String name, String value) {
/* 38 */     super(name);
/* 39 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileBool(String name, boolean value) {
/* 43 */     super(name);
/* 44 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileNode cloneNode() {
/* 48 */     return new INIFileBool(getName(), getValue());
/*    */   }
/*    */   
/*    */   public boolean getValue() {
/* 52 */     return this.m_Value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 56 */     setValue((StringUtils.getBoolean(value) == 1));
/*    */   }
/*    */   
/*    */   public void setValue(boolean value) {
/* 60 */     this.m_Value = value;
/*    */   }
/*    */   
/*    */   public String getStringValue() {
/* 64 */     if (this.m_Value) return "Yes"; 
/* 65 */     return "No";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFileBool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */