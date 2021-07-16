/*     */ package jeans.io.ini;
/*     */ 
/*     */ import jeans.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class INIFileStringOrInt
/*     */   extends INIFileEntry
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected String m_Value;
/*     */   protected int m_Int;
/*     */   protected boolean m_IsInt;
/*     */   
/*     */   public INIFileStringOrInt(String name) {
/*  36 */     this(name, "");
/*     */   }
/*     */   
/*     */   public INIFileStringOrInt(String name, String value) {
/*  40 */     super(name);
/*  41 */     setValue(value);
/*     */   }
/*     */   
/*     */   public INIFileNode cloneNode() {
/*  45 */     return new INIFileStringOrInt(getName(), getValue());
/*     */   }
/*     */   
/*     */   public String getValue() {
/*  49 */     return this.m_Value;
/*     */   }
/*     */   
/*     */   public int getIntValue() {
/*  53 */     return this.m_Int;
/*     */   }
/*     */   
/*     */   public void setIntValue(int val) {
/*  57 */     this.m_IsInt = true;
/*  58 */     this.m_Int = val;
/*  59 */     this.m_Value = "";
/*     */   }
/*     */   
/*     */   public boolean isIntOrNull(String nullstr) {
/*  63 */     if (isInt()) return true; 
/*  64 */     if (StringUtils.unCaseCompare(getStringValue(), nullstr)) return true; 
/*  65 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isString(String str) {
/*  69 */     if (isInt()) return false; 
/*  70 */     if (StringUtils.unCaseCompare(getStringValue(), str)) return true; 
/*  71 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isInt() {
/*  75 */     return this.m_IsInt;
/*     */   }
/*     */   
/*     */   public void setValue(String value) {
/*  79 */     if (isInt(value)) {
/*  80 */       this.m_Int = Integer.parseInt(value);
/*  81 */       this.m_IsInt = true;
/*     */     } else {
/*  83 */       this.m_IsInt = false;
/*     */     } 
/*  85 */     this.m_Value = value;
/*     */   }
/*     */   
/*     */   public String getStringValue() {
/*  89 */     if (this.m_IsInt) {
/*  90 */       return String.valueOf(this.m_Int);
/*     */     }
/*  92 */     return getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isInt(String value) {
/*  97 */     int len = value.length();
/*  98 */     if (len == 0) return false; 
/*  99 */     for (int i = 0; i < len; i++) {
/* 100 */       int ch = value.charAt(i);
/* 101 */       if (ch < 48 || ch > 57) return false; 
/*     */     } 
/* 103 */     return true;
/*     */   }
/*     */   
/*     */   public void setValue(int newValue) {
/* 107 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFileStringOrInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */