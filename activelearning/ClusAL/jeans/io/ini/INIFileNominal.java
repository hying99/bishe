/*     */ package jeans.io.ini;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Vector;
/*     */ import jeans.io.range.NominalType;
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
/*     */ 
/*     */ 
/*     */ public class INIFileNominal
/*     */   extends INIFileEntry
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected NominalType m_Type;
/*     */   protected int[] m_Values;
/*     */   protected boolean m_IsSet;
/*     */   
/*     */   public INIFileNominal(String name, String[] values) {
/*  43 */     super(name);
/*  44 */     this.m_Type = new NominalType(values);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public INIFileNominal(String name, String[] values, int def) {
/*  54 */     super(name);
/*  55 */     this.m_Type = new NominalType(values);
/*  56 */     this.m_Values = new int[1];
/*  57 */     this.m_Values[0] = def;
/*     */   }
/*     */   
/*     */   public INIFileNominal(String name, String[] type, int[] values) {
/*  61 */     super(name);
/*  62 */     this.m_Type = new NominalType(type);
/*  63 */     this.m_Values = values;
/*  64 */     this.m_IsSet = true;
/*     */   }
/*     */   
/*     */   public INIFileNominal(String name, NominalType type) {
/*  68 */     super(name);
/*  69 */     this.m_Type = type;
/*     */   }
/*     */   
/*     */   public INIFileNominal(String name, NominalType type, int[] values) {
/*  73 */     super(name);
/*  74 */     this.m_Type = type;
/*  75 */     this.m_Values = values;
/*  76 */     this.m_IsSet = true;
/*     */   }
/*     */   
/*     */   public INIFileNominal(String name, NominalType type, int value) {
/*  80 */     super(name);
/*  81 */     setSingleValue(value);
/*  82 */     this.m_Type = type;
/*  83 */     this.m_IsSet = false;
/*     */   }
/*     */   
/*     */   public void setSingleValue(int idx) {
/*  87 */     this.m_Values = new int[1];
/*  88 */     this.m_Values[0] = idx;
/*     */   }
/*     */   
/*     */   public int getSize() {
/*  92 */     return this.m_Values.length;
/*     */   }
/*     */   
/*     */   public NominalType getType() {
/*  96 */     return this.m_Type;
/*     */   }
/*     */   
/*     */   public boolean contains(int idx) {
/* 100 */     return (Arrays.binarySearch(this.m_Values, idx) >= 0);
/*     */   }
/*     */   
/*     */   public int getIntAt(int idx) {
/* 104 */     return this.m_Values[idx];
/*     */   }
/*     */   
/*     */   public String getStringSingle() {
/* 108 */     return this.m_Type.getName(this.m_Values[0]);
/*     */   }
/*     */   
/*     */   public int getValue() {
/* 112 */     return this.m_Values[0];
/*     */   }
/*     */   
/*     */   public int getSingle() {
/* 116 */     return this.m_Values[0];
/*     */   }
/*     */   
/*     */   public String getStringAt(int idx) {
/* 120 */     return this.m_Type.getName(this.m_Values[idx]);
/*     */   }
/*     */   
/*     */   public void setValues(int[] vals) {
/* 124 */     this.m_Values = vals;
/*     */   }
/*     */   
/*     */   public INIFileNode cloneNode() {
/* 128 */     return new INIFileNominal(getName(), getType());
/*     */   }
/*     */   
/*     */   public void build(MStreamTokenizer tokens) throws IOException {
/* 132 */     this.m_Type.setReader(true);
/* 133 */     if (tokens.isNextToken('{')) {
/* 134 */       if (this.m_IsSet) {
/* 135 */         Vector<Integer> vals = new Vector();
/* 136 */         String token = tokens.getToken();
/* 137 */         while (token != null && !token.equals("}")) {
/* 138 */           int idx = this.m_Type.getValue(token);
/* 139 */           if (idx == -1) throw new IOException(this.m_Type.getError(getName(), token)); 
/* 140 */           vals.addElement(new Integer(idx));
/* 141 */           token = readNextEntry(tokens);
/*     */         } 
/* 143 */         this.m_Values = new int[vals.size()];
/* 144 */         for (int i = 0; i < vals.size(); i++)
/* 145 */           this.m_Values[i] = ((Integer)vals.elementAt(i)).intValue(); 
/* 146 */         Arrays.sort(this.m_Values);
/*     */       } else {
/* 148 */         throw new IOException("'" + getName() + "' is not a set");
/*     */       } 
/*     */     } else {
/* 151 */       String token = tokens.getToken();
/* 152 */       int idx = this.m_Type.getValue(token);
/* 153 */       if (idx == -1) throw new IOException(this.m_Type.getError(getName(), token)); 
/* 154 */       setSingleValue(idx);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) {}
/*     */   
/*     */   public String readNextEntry(MStreamTokenizer tokens) throws IOException {
/* 162 */     String token = tokens.getToken();
/* 163 */     if (token != null && token.equals(",")) token = tokens.getToken(); 
/* 164 */     return token;
/*     */   }
/*     */   
/*     */   public String getStringValue() {
/* 168 */     if (this.m_IsSet) {
/* 169 */       StringBuffer buffer = new StringBuffer("{");
/* 170 */       for (int idx = 0; idx < this.m_Values.length; idx++) {
/* 171 */         if (idx != 0) buffer.append(", "); 
/* 172 */         buffer.append(this.m_Type.getName(this.m_Values[idx]));
/*     */       } 
/* 174 */       buffer.append("}");
/* 175 */       return buffer.toString();
/*     */     } 
/* 177 */     return getStringSingle();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileNominal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */