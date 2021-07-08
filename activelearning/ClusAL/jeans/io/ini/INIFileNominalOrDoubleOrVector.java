/*     */ package jeans.io.ini;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
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
/*     */ public class INIFileNominalOrDoubleOrVector
/*     */   extends INIFileEntry
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected NominalType m_Type;
/*     */   protected int m_SingleNominal;
/*     */   protected double m_SingleDouble;
/*     */   protected int[] m_ArrayNominal;
/*     */   protected double[] m_ArrayDouble;
/*     */   private boolean m_HasArrayIndexNames;
/*     */   private String[] m_Names;
/*     */   
/*     */   public INIFileNominalOrDoubleOrVector(String name, String[] values) {
/*  44 */     this(name, new NominalType(values));
/*     */   }
/*     */   
/*     */   public INIFileNominalOrDoubleOrVector(String name, NominalType values) {
/*  48 */     super(name);
/*  49 */     this.m_Type = values;
/*  50 */     this.m_SingleNominal = -1;
/*  51 */     this.m_Names = new String[0];
/*     */   }
/*     */   
/*     */   public NominalType getType() {
/*  55 */     return this.m_Type;
/*     */   }
/*     */   
/*     */   public INIFileNode cloneNode() {
/*  59 */     return new INIFileNominalOrDoubleOrVector(getName(), getType());
/*     */   }
/*     */   
/*     */   public boolean isVector() {
/*  63 */     return (this.m_ArrayNominal != null);
/*     */   }
/*     */   
/*     */   public boolean isNominal() {
/*  67 */     return (this.m_SingleNominal != -1);
/*     */   }
/*     */   
/*     */   public boolean isNominal(int idx) {
/*  71 */     return (getNominal(idx) != -1);
/*     */   }
/*     */   
/*     */   public int getNominal(int idx) {
/*  75 */     if (isVector()) {
/*  76 */       return this.m_ArrayNominal[idx];
/*     */     }
/*  78 */     return getNominal();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNominalString(int idx) {
/*  83 */     return this.m_Type.getName(getNominal(idx));
/*     */   }
/*     */   
/*     */   public double getDouble(int idx) {
/*  87 */     if (isVector()) {
/*  88 */       return this.m_ArrayDouble[idx];
/*     */     }
/*  90 */     return getDouble();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNominal() {
/*  95 */     return this.m_SingleNominal;
/*     */   }
/*     */   
/*     */   public double getDouble() {
/*  99 */     return this.m_SingleDouble;
/*     */   }
/*     */   
/*     */   public void setDouble(double value) {
/* 103 */     this.m_SingleDouble = value;
/* 104 */     this.m_ArrayNominal = null;
/* 105 */     this.m_ArrayDouble = null;
/*     */   }
/*     */   
/*     */   public void setNominal(int value) {
/* 109 */     this.m_SingleNominal = value;
/* 110 */     this.m_ArrayNominal = null;
/* 111 */     this.m_ArrayDouble = null;
/*     */   }
/*     */   
/*     */   public void setDouble(int idx, double value) {
/* 115 */     this.m_ArrayDouble[idx] = value;
/* 116 */     this.m_ArrayNominal[idx] = -1;
/*     */   }
/*     */   
/*     */   public void setNominal(int idx, int value) {
/* 120 */     this.m_ArrayNominal[idx] = value;
/*     */   }
/*     */   
/*     */   public void setDoubleArray(double[] values) {
/* 124 */     setVector(values.length);
/* 125 */     for (int i = 0; i < values.length; i++) {
/* 126 */       setDouble(i, values[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setVector(int len) {
/* 131 */     this.m_ArrayNominal = new int[len];
/* 132 */     this.m_ArrayDouble = new double[len];
/*     */   }
/*     */   
/*     */   public int getVectorLength() {
/* 136 */     return (this.m_ArrayNominal == null) ? 1 : this.m_ArrayNominal.length;
/*     */   }
/*     */   
/*     */   public boolean hasVector() {
/* 140 */     return (this.m_ArrayNominal != null);
/*     */   }
/*     */   
/*     */   public double[] getDoubleVector() {
/* 144 */     return this.m_ArrayDouble;
/*     */   }
/*     */   
/*     */   public void build(MStreamTokenizer tokens) throws IOException {
/* 148 */     this.m_Type.setReader(true);
/* 149 */     setNominal(-1);
/* 150 */     setArrayIndexNames(false);
/* 151 */     if (tokens.isNextToken('[')) {
/* 152 */       ArrayList<String> values = new ArrayList();
/* 153 */       String token = tokens.getToken();
/* 154 */       while (token != null && !token.equals("]")) {
/* 155 */         values.add(token);
/* 156 */         token = readNextEntry(tokens);
/*     */       } 
/* 158 */       setVector(values.size());
/* 159 */       for (int i = 0; i < values.size(); i++) {
/* 160 */         String value = values.get(i);
/* 161 */         int idx = this.m_Type.getValue(value);
/* 162 */         if (idx != -1) {
/* 163 */           setNominal(i, idx);
/*     */         } else {
/*     */           try {
/* 166 */             setDouble(i, Double.parseDouble(value));
/* 167 */           } catch (NumberFormatException e) {
/* 168 */             throw new IOException("Illegal value '" + value + "' in vector at pos " + i + " for setting '" + getName() + "'");
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/* 173 */       String token = tokens.getToken();
/* 174 */       int idx = this.m_Type.getValue(token);
/* 175 */       if (idx != -1) {
/* 176 */         setNominal(idx);
/* 177 */       } else if (isArrayIndexName(0, token)) {
/* 178 */         setVector(this.m_Names.length);
/* 179 */         setArrayIndexNames(true);
/* 180 */         for (int i = 0; i < this.m_Names.length; i++) {
/* 181 */           if (!isArrayIndexName(i, token)) {
/* 182 */             throw new IOException("Expected one of [" + getArrayIndexNamesString() + "] and not '" + token + "' for setting '" + getName() + "'");
/*     */           }
/* 184 */           String value = tokens.getToken();
/* 185 */           if (!value.equals("=")) {
/* 186 */             throw new IOException("Expected '=' after '" + this.m_Names[i] + "' for setting '" + getName() + "'");
/*     */           }
/* 188 */           value = tokens.getToken();
/* 189 */           int idx2 = this.m_Type.getValue(value);
/* 190 */           if (idx2 != -1) {
/* 191 */             setNominal(i, idx2);
/*     */           } else {
/*     */             try {
/* 194 */               setDouble(i, Double.parseDouble(value));
/* 195 */             } catch (NumberFormatException e) {
/* 196 */               throw new IOException("Illegal value '" + value + "' for '" + this.m_Names[i] + "' for setting '" + getName() + "'");
/*     */             } 
/*     */           } 
/* 199 */           if (i != this.m_Names.length - 1) token = tokens.getToken();
/*     */         
/*     */         } 
/*     */       } else {
/*     */         try {
/* 204 */           setDouble(Double.parseDouble(token));
/* 205 */         } catch (NumberFormatException e) {
/* 206 */           throw new IOException("Illegal value '" + token + "' for setting '" + getName() + "'");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setValue(String value) throws IOException {
/* 213 */     build(new MStreamTokenizer(new StringReader(value)));
/*     */   }
/*     */   
/*     */   public String readNextEntry(MStreamTokenizer tokens) throws IOException {
/* 217 */     String token = tokens.getToken();
/* 218 */     if (token != null && token.equals(",")) token = tokens.getToken(); 
/* 219 */     return token;
/*     */   }
/*     */   
/*     */   public String getStringValue() {
/* 223 */     if (hasArrayIndexNames()) {
/* 224 */       StringBuffer buf = new StringBuffer();
/* 225 */       buf.append("\n");
/* 226 */       for (int i = 0; i < this.m_Names.length; i++) {
/* 227 */         buf.append("  " + this.m_Names[i] + " = ");
/* 228 */         if (isNominal(i)) { buf.append(getNominalString(i)); }
/* 229 */         else { buf.append(String.valueOf(getDouble(i))); }
/* 230 */          if (i != this.m_Names.length - 1) buf.append("\n"); 
/*     */       } 
/* 232 */       return buf.toString();
/* 233 */     }  if (isVector()) {
/* 234 */       StringBuffer buf = new StringBuffer();
/* 235 */       buf.append("[");
/* 236 */       for (int i = 0; i < getVectorLength(); i++) {
/* 237 */         if (i != 0) buf.append(","); 
/* 238 */         if (isNominal(i)) { buf.append(getNominalString(i)); }
/* 239 */         else { buf.append(String.valueOf(getDouble(i))); }
/*     */       
/* 241 */       }  buf.append("]");
/* 242 */       return buf.toString();
/*     */     } 
/* 244 */     if (isNominal()) return this.m_Type.getName(getNominal()); 
/* 245 */     return String.valueOf(getDouble());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getArrayIndexNamesString() {
/* 250 */     StringBuffer buf = new StringBuffer();
/* 251 */     for (int i = 0; i < this.m_Names.length; i++) {
/* 252 */       if (i != 0) buf.append(","); 
/* 253 */       buf.append(this.m_Names[i]);
/*     */     } 
/* 255 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public boolean isArrayIndexName(int i, String name) {
/* 259 */     if (i >= this.m_Names.length) return false; 
/* 260 */     if (this.m_Names[i].equals(name)) return true; 
/* 261 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isArrayIndexName(String name) {
/* 265 */     for (int i = 0; i < this.m_Names.length; i++) {
/* 266 */       if (this.m_Names[i].equals(name)) return true; 
/*     */     } 
/* 268 */     return false;
/*     */   }
/*     */   
/*     */   public void setArrayIndexNames(String[] names) {
/* 272 */     this.m_Names = names;
/*     */   }
/*     */   
/*     */   public void setArrayIndexNames(boolean enable) {
/* 276 */     this.m_HasArrayIndexNames = enable;
/*     */   }
/*     */   
/*     */   public boolean hasArrayIndexNames() {
/* 280 */     return this.m_HasArrayIndexNames;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileNominalOrDoubleOrVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */