/*    */ package jeans.io.ini;
/*    */ 
/*    */ import jeans.io.range.ValueCheck;
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
/*    */ public class INIFileDouble
/*    */   extends INIFileEntry
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected double m_Value;
/*    */   protected ValueCheck m_Check;
/*    */   
/*    */   public INIFileDouble(String name, String value) {
/* 35 */     super(name);
/* 36 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileDouble(String name, double value) {
/* 40 */     super(name);
/* 41 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileDouble(String name) {
/* 45 */     this(name, 0.0D);
/*    */   }
/*    */   
/*    */   public void setValueCheck(ValueCheck check) {
/* 49 */     this.m_Check = check;
/*    */   }
/*    */   
/*    */   public INIFileNode cloneNode() {
/* 53 */     return new INIFileDouble(getName(), getValue());
/*    */   }
/*    */   
/*    */   public double getValue() {
/* 57 */     return this.m_Value;
/*    */   }
/*    */   
/*    */   public void setValue(double value) {
/* 61 */     if (this.m_Check != null) {
/* 62 */       Double dval = new Double(value);
/* 63 */       if (!this.m_Check.checkValue(dval))
/* 64 */         throw new NumberFormatException(this.m_Check.getString(getName(), dval)); 
/*    */     } 
/* 66 */     this.m_Value = value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 70 */     setValue(Double.parseDouble(value));
/*    */   }
/*    */   
/*    */   public String getStringValue() {
/* 74 */     return String.valueOf(getValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFileDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */