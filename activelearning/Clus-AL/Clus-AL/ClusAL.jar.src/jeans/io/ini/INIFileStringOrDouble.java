/*    */ package jeans.io.ini;
/*    */ 
/*    */ import jeans.math.MDouble;
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
/*    */ public class INIFileStringOrDouble
/*    */   extends INIFileEntry
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected String m_Value;
/*    */   protected double m_Double;
/*    */   protected boolean m_IsDouble;
/*    */   
/*    */   public INIFileStringOrDouble(String name) {
/* 37 */     this(name, "");
/*    */   }
/*    */   
/*    */   public INIFileStringOrDouble(String name, String value) {
/* 41 */     super(name);
/* 42 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileNode cloneNode() {
/* 46 */     return new INIFileStringOrDouble(getName(), getValue());
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 50 */     return this.m_Value;
/*    */   }
/*    */   
/*    */   public double getDoubleValue() {
/* 54 */     return this.m_Double;
/*    */   }
/*    */   
/*    */   public void setDoubleValue(double val) {
/* 58 */     this.m_IsDouble = true;
/* 59 */     this.m_Double = val;
/* 60 */     this.m_Value = "";
/*    */   }
/*    */   
/*    */   public boolean isDoubleOrNull(String nullstr) {
/* 64 */     if (isDouble()) return true; 
/* 65 */     if (StringUtils.unCaseCompare(getStringValue(), nullstr)) return true; 
/* 66 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isString(String str) {
/* 70 */     if (isDouble()) return false; 
/* 71 */     if (StringUtils.unCaseCompare(getStringValue(), str)) return true; 
/* 72 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isDouble() {
/* 76 */     return this.m_IsDouble;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 80 */     if (MDouble.isDouble(value)) {
/* 81 */       this.m_Double = Double.parseDouble(value);
/* 82 */       this.m_IsDouble = true;
/*    */     } 
/* 84 */     this.m_Value = value;
/*    */   }
/*    */   
/*    */   public String getStringValue() {
/* 88 */     if (this.m_IsDouble) {
/* 89 */       return String.valueOf(this.m_Double);
/*    */     }
/* 91 */     return getValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFileStringOrDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */