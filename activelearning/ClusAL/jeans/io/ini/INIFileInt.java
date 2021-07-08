/*    */ package jeans.io.ini;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public class INIFileInt
/*    */   extends INIFileEntry
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected int m_Value;
/*    */   protected ValueCheck m_Check;
/*    */   protected transient HashMap m_NameToValue;
/*    */   protected transient HashMap m_ValueToName;
/*    */   
/*    */   public INIFileInt(String name, String value) throws NumberFormatException {
/* 38 */     super(name);
/* 39 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileInt(String name, int value) {
/* 43 */     super(name);
/* 44 */     setValue(value);
/*    */   }
/*    */   
/*    */   public INIFileInt(String name) {
/* 48 */     this(name, 0);
/*    */   }
/*    */   
/*    */   public void setValueCheck(ValueCheck check) {
/* 52 */     this.m_Check = check;
/*    */   }
/*    */   
/*    */   public INIFileNode cloneNode() {
/* 56 */     return new INIFileInt(getName(), getValue());
/*    */   }
/*    */   
/*    */   public void setNamedValue(int value, String name) {
/* 60 */     if (this.m_NameToValue == null) this.m_NameToValue = new HashMap<>(); 
/* 61 */     if (this.m_ValueToName == null) this.m_ValueToName = new HashMap<>(); 
/* 62 */     Integer value_int = new Integer(value);
/* 63 */     this.m_NameToValue.put(name, value_int);
/* 64 */     this.m_ValueToName.put(value_int, name);
/*    */   }
/*    */   
/*    */   public int getValue() {
/* 68 */     return this.m_Value;
/*    */   }
/*    */   
/*    */   public void setValue(int value) {
/* 72 */     if (this.m_Check != null) {
/* 73 */       Integer dval = new Integer(value);
/* 74 */       if (!this.m_Check.checkValue(dval))
/* 75 */         throw new NumberFormatException(this.m_Check.getString(getName(), dval)); 
/*    */     } 
/* 77 */     this.m_Value = value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) throws NumberFormatException {
/* 81 */     if (this.m_NameToValue != null) {
/* 82 */       Integer int_value = (Integer)this.m_NameToValue.get(value);
/* 83 */       if (int_value != null) {
/* 84 */         setValue(int_value.intValue());
/*    */       } else {
/* 86 */         setValue(Integer.parseInt(value));
/*    */       } 
/*    */     } else {
/* 89 */       setValue(Integer.parseInt(value));
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getStringValue() {
/* 94 */     if (this.m_ValueToName != null) {
/* 95 */       String name = (String)this.m_ValueToName.get(new Integer(getValue()));
/* 96 */       if (name != null) return name; 
/*    */     } 
/* 98 */     return String.valueOf(getValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */