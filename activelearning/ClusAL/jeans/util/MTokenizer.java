/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class MTokenizer
/*     */ {
/*  37 */   protected int lineno = 1;
/*  38 */   protected String pushbacktoken = null;
/*     */   protected boolean prevcr = false;
/*  40 */   protected int pushbackchar = -1;
/*  41 */   protected String chartokens = ":;,[]{}";
/*  42 */   protected int commentchar = 35;
/*     */ 
/*     */   
/*     */   public String getToken() throws IOException {
/*  46 */     if (this.pushbacktoken == null) {
/*  47 */       int ch = readSignificantChar();
/*  48 */       if (ch != -1) {
/*  49 */         StringBuffer strg = new StringBuffer();
/*  50 */         boolean done = false;
/*     */         while (true) {
/*  52 */           strg.append((char)ch);
/*  53 */           if (this.chartokens.indexOf((char)ch) == -1) {
/*  54 */             ch = readChar();
/*  55 */             if (ch == 32 || ch == 10 || ch == 13 || ch == -1 || ch == 9) {
/*  56 */               pushBackChar(ch);
/*  57 */               done = true;
/*     */             } 
/*  59 */             if (this.chartokens.indexOf((char)ch) != -1) {
/*  60 */               pushBackChar(ch);
/*  61 */               done = true;
/*     */             } 
/*     */           } else {
/*  64 */             done = true;
/*     */           } 
/*  66 */           if (done)
/*  67 */             return strg.toString(); 
/*     */         } 
/*  69 */       }  return null;
/*     */     } 
/*     */     
/*  72 */     String betw = this.pushbacktoken;
/*  73 */     this.pushbacktoken = null;
/*  74 */     return betw;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readToken() throws IOException {
/*  79 */     int saveline = getLine();
/*  80 */     String token = getToken();
/*  81 */     if (token == null)
/*  82 */       throw new IOException("Unexpected end of file at line: " + saveline); 
/*  83 */     return token;
/*     */   }
/*     */   
/*     */   public boolean hasMoreTokens() throws IOException {
/*  87 */     String token = getToken();
/*  88 */     if (token == null) {
/*  89 */       return false;
/*     */     }
/*  91 */     pushBackToken(token);
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readSignificantChar() throws IOException {
/*     */     while (true) {
/* 100 */       int ch = readChar();
/* 101 */       if (ch == this.commentchar) {
/*     */         do {
/* 103 */           ch = readChar();
/* 104 */         } while (ch != -1 && ch != 10 && ch != 13);
/*     */       }
/* 106 */       if (ch != 32 && ch != 10 && ch != 13 && ch != 9)
/* 107 */         return ch; 
/*     */     } 
/*     */   }
/*     */   public abstract int read();
/*     */   
/*     */   public int readChar() throws IOException {
/* 113 */     if (this.pushbackchar == -1) {
/* 114 */       int ch = read();
/* 115 */       if (ch == 10 && !this.prevcr) this.lineno++; 
/* 116 */       this.prevcr = (ch == 13);
/* 117 */       if (this.prevcr) this.lineno++; 
/* 118 */       return ch;
/*     */     } 
/* 120 */     int betw = this.pushbackchar;
/* 121 */     this.pushbackchar = -1;
/* 122 */     return betw;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readTillEol() throws IOException {
/*     */     int ch;
/* 128 */     StringBuffer strg = new StringBuffer();
/* 129 */     if (this.pushbacktoken == null) {
/* 130 */       ch = readSignificantChar();
/* 131 */       if (ch == -1) return null; 
/*     */     } else {
/* 133 */       strg.append(this.pushbacktoken);
/* 134 */       this.pushbacktoken = null;
/* 135 */       ch = readChar();
/*     */     } 
/* 137 */     while (ch != 10 && ch != 13 && ch != -1) {
/* 138 */       strg.append((char)ch);
/* 139 */       ch = readChar();
/*     */     } 
/* 141 */     pushBackChar(ch);
/* 142 */     return strg.toString();
/*     */   }
/*     */   
/*     */   public int readInteger() throws IOException {
/* 146 */     int saveline = getLine();
/* 147 */     String token = readToken();
/*     */     try {
/* 149 */       return Integer.parseInt(token);
/* 150 */     } catch (NumberFormatException e) {
/* 151 */       throw new IOException("Integer value expected at line: " + saveline);
/*     */     } 
/*     */   }
/*     */   
/*     */   public float readFloat() throws IOException {
/* 156 */     int saveline = getLine();
/* 157 */     String token = readToken();
/*     */     try {
/* 159 */       Float fl = new Float(token);
/* 160 */       return fl.floatValue();
/* 161 */     } catch (NumberFormatException e) {
/* 162 */       throw new IOException("Float value expected at line: " + saveline);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String isNextTokenIn(String strg) throws IOException {
/* 167 */     String token = getToken();
/* 168 */     if (token == null || strg.indexOf(token) != -1) {
/* 169 */       return token;
/*     */     }
/* 171 */     pushBackToken(token);
/* 172 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNextToken(String strg) throws IOException {
/* 177 */     boolean yes = false;
/* 178 */     String token = getToken();
/* 179 */     if (token == null) return false; 
/* 180 */     if (token.equals(strg)) { yes = true; }
/* 181 */     else { pushBackToken(token); }
/* 182 */      return yes;
/*     */   }
/*     */   
/*     */   public int readChar(String which) throws IOException {
/* 186 */     int saveline = getLine();
/* 187 */     String character = getToken();
/* 188 */     if (character == null)
/* 189 */       throw new IOException("Unexpected end of file reading character at line: " + saveline); 
/* 190 */     int idx = which.indexOf(character.charAt(0));
/* 191 */     if (idx < 0 || character.length() != 1)
/* 192 */       throw new IOException("Character '" + which + "' expected at line: " + saveline); 
/* 193 */     return idx;
/*     */   }
/*     */   
/*     */   public void readChar(char ch) throws IOException {
/* 197 */     int saveline = getLine();
/* 198 */     String character = getToken();
/* 199 */     if (character == null)
/* 200 */       throw new IOException("Unexpected end of file reading character at line: " + saveline); 
/* 201 */     if (!character.equals(String.valueOf(ch)))
/* 202 */       throw new IOException("Character '" + ch + "' expected at line: " + saveline); 
/*     */   }
/*     */   
/*     */   public void pushBackChar(int ch) {
/* 206 */     this.pushbackchar = ch;
/*     */   }
/*     */   
/*     */   public void pushBackToken(String token) {
/* 210 */     this.pushbacktoken = token;
/*     */   }
/*     */   
/*     */   public String setCharTokens(String chartokens) {
/* 214 */     String result = this.chartokens;
/* 215 */     this.chartokens = chartokens;
/* 216 */     return result;
/*     */   }
/*     */   
/*     */   public int setCommentChar(int commentchar) {
/* 220 */     int result = this.commentchar;
/* 221 */     this.commentchar = commentchar;
/* 222 */     return result;
/*     */   }
/*     */   
/*     */   public String getCharTokens() {
/* 226 */     return this.chartokens;
/*     */   }
/*     */   
/*     */   public int setCommentChar() {
/* 230 */     return this.commentchar;
/*     */   }
/*     */   
/*     */   public int getLine() {
/* 234 */     return this.lineno;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\MTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */