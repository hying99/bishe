/*     */ package jeans.io.ini;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ import jeans.util.MStreamTokenizer;
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
/*     */ 
/*     */ public class INIFileArray
/*     */   extends INIFileEntry
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  34 */   protected Vector m_hValue = new Vector();
/*     */   protected int m_iType;
/*     */   
/*     */   public INIFileArray(String name, int type) {
/*  38 */     super(name);
/*  39 */     this.m_iType = type;
/*     */   }
/*     */   
/*     */   public INIFileArray(String name, int[] init) {
/*  43 */     super(name);
/*  44 */     fromArray(init);
/*     */   }
/*     */   
/*     */   public INIFileArray(String name, String[] init) {
/*  48 */     super(name);
/*  49 */     fromArray(init);
/*     */   }
/*     */   
/*     */   public INIFileArray(String name, double[] init) {
/*  53 */     super(name);
/*  54 */     fromArray(init);
/*     */   }
/*     */   
/*     */   public int getSize() {
/*  58 */     return this.m_hValue.size();
/*     */   }
/*     */   
/*     */   public int getType() {
/*  62 */     return this.m_iType;
/*     */   }
/*     */   
/*     */   public void removeAllElements() {
/*  66 */     this.m_hValue.removeAllElements();
/*     */   }
/*     */   
/*     */   public Vector getVector() {
/*  70 */     return this.m_hValue;
/*     */   }
/*     */   
/*     */   public void fromArray(int[] init) {
/*  74 */     removeAllElements();
/*  75 */     this.m_iType = 2;
/*  76 */     for (int i = 0; i < init.length; ) { addValue(init[i]); i++; }
/*     */   
/*     */   }
/*     */   public void fromArray(String[] init) {
/*  80 */     removeAllElements();
/*  81 */     this.m_iType = 1;
/*  82 */     for (int i = 0; i < init.length; ) { addValue(init[i]); i++; }
/*     */   
/*     */   }
/*     */   public void fromArray(double[] init) {
/*  86 */     removeAllElements();
/*  87 */     this.m_iType = 0;
/*  88 */     for (int i = 0; i < init.length; ) { addValue(init[i]); i++; }
/*     */   
/*     */   }
/*     */   public void addValue(int value) {
/*  92 */     if (this.m_iType == 2)
/*  93 */       this.m_hValue.addElement(new Integer(value)); 
/*     */   }
/*     */   
/*     */   public void addValue(String value) {
/*  97 */     if (this.m_iType == 1)
/*  98 */       this.m_hValue.addElement(value); 
/*     */   }
/*     */   
/*     */   public void addValue(double value) {
/* 102 */     if (this.m_iType == 0)
/* 103 */       this.m_hValue.addElement(new Double(value)); 
/*     */   }
/*     */   
/*     */   public int getIntAt(int idx) {
/* 107 */     if (this.m_iType == 2) {
/* 108 */       Integer value = this.m_hValue.elementAt(idx);
/* 109 */       return value.intValue();
/*     */     } 
/* 111 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStringAt(int idx) {
/* 116 */     if (this.m_iType == 1) {
/* 117 */       return this.m_hValue.elementAt(idx);
/*     */     }
/* 119 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDoubleAt(int idx) {
/* 124 */     if (this.m_iType == 0) {
/* 125 */       Double value = this.m_hValue.elementAt(idx);
/* 126 */       return value.doubleValue();
/*     */     } 
/* 128 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public INIFileNode cloneNode() {
/* 133 */     return new INIFileArray(getName(), getType());
/*     */   }
/*     */   
/*     */   public void build(MStreamTokenizer tokens) throws IOException {
/* 137 */     tokens.readChar('{');
/* 138 */     removeAllElements();
/* 139 */     String token = tokens.getToken();
/* 140 */     while (token != null && !token.equals("}")) {
/* 141 */       addToken(token, tokens);
/* 142 */       token = readNextEntry(tokens);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addToken(String token, MStreamTokenizer tokens) throws IOException {
/* 147 */     switch (this.m_iType) {
/*     */       case 0:
/*     */         try {
/* 150 */           addValue(Double.parseDouble(token.trim()));
/* 151 */         } catch (NumberFormatException e) {
/* 152 */           throw new IOException("Double expected at line " + tokens.getLine());
/*     */         } 
/*     */         break;
/*     */       case 2:
/*     */         try {
/* 157 */           addValue(Integer.parseInt(token.trim()));
/* 158 */         } catch (NumberFormatException e) {
/* 159 */           throw new IOException("Integer expected at line " + tokens.getLine());
/*     */         } 
/*     */         break;
/*     */       case 1:
/* 163 */         this.m_hValue.addElement(token.trim());
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) {}
/*     */   
/*     */   public String readNextEntry(MStreamTokenizer tokens) throws IOException {
/* 172 */     String token = tokens.getToken();
/* 173 */     if (token != null && token.equals(",")) token = tokens.getToken(); 
/* 174 */     return token;
/*     */   }
/*     */   
/*     */   public String getStringValue() {
/* 178 */     StringBuffer buffer = new StringBuffer("{");
/* 179 */     for (int idx = 0; idx < this.m_hValue.size(); idx++) {
/* 180 */       Object value = this.m_hValue.elementAt(idx);
/* 181 */       if (idx != 0) buffer.append(", "); 
/* 182 */       buffer.append(value.toString());
/*     */     } 
/* 184 */     buffer.append("}");
/* 185 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFileArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */