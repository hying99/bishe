/*     */ package jeans.io.ini;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */ public class INIFileNominalOrIntOrVector
/*     */   extends INIFileEntry
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected NominalType m_Type;
/*     */   protected int m_SingleNominal;
/*     */   protected int m_SingleInt;
/*     */   protected int[] m_ArrayNominal;
/*     */   protected int[] m_ArrayInt;
/*     */   
/*     */   public INIFileNominalOrIntOrVector(String name, String[] values) {
/*  42 */     this(name, new NominalType(values));
/*     */   }
/*     */   
/*     */   public INIFileNominalOrIntOrVector(String name, NominalType values) {
/*  46 */     super(name);
/*  47 */     this.m_Type = values;
/*  48 */     this.m_SingleNominal = -1;
/*     */   }
/*     */   
/*     */   public NominalType getType() {
/*  52 */     return this.m_Type;
/*     */   }
/*     */   
/*     */   public INIFileNode cloneNode() {
/*  56 */     return new INIFileNominalOrIntOrVector(getName(), getType());
/*     */   }
/*     */   
/*     */   public boolean isVector() {
/*  60 */     return (this.m_ArrayNominal != null);
/*     */   }
/*     */   
/*     */   public boolean isNominal() {
/*  64 */     return (this.m_SingleNominal != -1);
/*     */   }
/*     */   
/*     */   public boolean isNominal(int idx) {
/*  68 */     return (getNominal(idx) != -1);
/*     */   }
/*     */   
/*     */   public int getNominal(int idx) {
/*  72 */     if (isVector()) {
/*  73 */       return this.m_ArrayNominal[idx];
/*     */     }
/*  75 */     return getNominal();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNominalString(int idx) {
/*  80 */     return this.m_Type.getName(getNominal(idx));
/*     */   }
/*     */   
/*     */   public int getInt(int idx) {
/*  84 */     if (isVector()) {
/*  85 */       return this.m_ArrayInt[idx];
/*     */     }
/*  87 */     return getInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNominal() {
/*  92 */     return this.m_SingleNominal;
/*     */   }
/*     */   
/*     */   public int getInt() {
/*  96 */     return this.m_SingleInt;
/*     */   }
/*     */   
/*     */   public void setInt(int value) {
/* 100 */     this.m_SingleInt = value;
/* 101 */     this.m_ArrayNominal = null;
/* 102 */     this.m_ArrayInt = null;
/*     */   }
/*     */   
/*     */   public void setNominal(int value) {
/* 106 */     this.m_SingleNominal = value;
/* 107 */     this.m_ArrayNominal = null;
/* 108 */     this.m_ArrayInt = null;
/*     */   }
/*     */   
/*     */   public void setInt(int idx, int value) {
/* 112 */     this.m_ArrayInt[idx] = value;
/* 113 */     this.m_ArrayNominal[idx] = -1;
/*     */   }
/*     */   
/*     */   public void setNominal(int idx, int value) {
/* 117 */     this.m_ArrayNominal[idx] = value;
/*     */   }
/*     */   
/*     */   public void setVector(int len) {
/* 121 */     this.m_ArrayNominal = new int[len];
/* 122 */     this.m_ArrayInt = new int[len];
/*     */   }
/*     */   
/*     */   public int getVectorLength() {
/* 126 */     return (this.m_ArrayNominal == null) ? 1 : this.m_ArrayNominal.length;
/*     */   }
/*     */   
/*     */   public int[] getIntVector() {
/* 130 */     if (isVector()) {
/* 131 */       int[] arrayOfInt = new int[this.m_ArrayInt.length];
/* 132 */       System.arraycopy(this.m_ArrayInt, 0, arrayOfInt, 0, this.m_ArrayInt.length);
/* 133 */       return arrayOfInt;
/*     */     } 
/* 135 */     int[] res = new int[1];
/* 136 */     res[0] = this.m_SingleInt;
/* 137 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getIntVectorSorted() {
/* 142 */     int[] result = getIntVector();
/* 143 */     Arrays.sort(result);
/* 144 */     return result;
/*     */   }
/*     */   
/*     */   public void build(MStreamTokenizer tokens) throws IOException {
/* 148 */     this.m_Type.setReader(true);
/* 149 */     setNominal(-1);
/* 150 */     if (tokens.isNextToken('[')) {
/* 151 */       ArrayList<String> values = new ArrayList();
/* 152 */       String token = tokens.getToken();
/* 153 */       while (token != null && !token.equals("]")) {
/* 154 */         values.add(token);
/* 155 */         token = readNextEntry(tokens);
/*     */       } 
/* 157 */       setVector(values.size());
/* 158 */       for (int i = 0; i < values.size(); i++) {
/* 159 */         String value = values.get(i);
/* 160 */         int idx = this.m_Type.getValue(value);
/* 161 */         if (idx != -1) {
/* 162 */           setNominal(i, idx);
/*     */         } else {
/*     */           try {
/* 165 */             setInt(i, Integer.parseInt(value));
/* 166 */           } catch (NumberFormatException e) {
/* 167 */             throw new IOException("Illegal value '" + value + "' in vector at pos " + i + " for setting '" + getName() + "'");
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/* 172 */       String token = tokens.getToken();
/* 173 */       int idx = this.m_Type.getValue(token);
/* 174 */       if (idx != -1) {
/* 175 */         setNominal(idx);
/*     */       } else {
/*     */         
/*     */         try {
/* 179 */           setInt(Integer.parseInt(token));
/* 180 */         } catch (NumberFormatException e) {
/* 181 */           throw new IOException("Illegal value '" + token + "' for setting '" + getName() + "'");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setValue(String value) throws IOException {
/* 188 */     build(new MStreamTokenizer(new StringReader(value)));
/*     */   }
/*     */   
/*     */   public String readNextEntry(MStreamTokenizer tokens) throws IOException {
/* 192 */     String token = tokens.getToken();
/* 193 */     if (token != null && token.equals(",")) token = tokens.getToken(); 
/* 194 */     return token;
/*     */   }
/*     */   
/*     */   public String getStringValue() {
/* 198 */     if (isVector()) {
/* 199 */       StringBuffer buf = new StringBuffer();
/* 200 */       buf.append("[");
/* 201 */       for (int i = 0; i < getVectorLength(); i++) {
/* 202 */         if (i != 0) buf.append(","); 
/* 203 */         if (isNominal(i)) { buf.append(getNominalString(i)); }
/* 204 */         else { buf.append(String.valueOf(getInt(i))); }
/*     */       
/* 206 */       }  buf.append("]");
/* 207 */       return buf.toString();
/*     */     } 
/* 209 */     if (isNominal()) return this.m_Type.getName(getNominal()); 
/* 210 */     return String.valueOf(getInt());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileNominalOrIntOrVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */