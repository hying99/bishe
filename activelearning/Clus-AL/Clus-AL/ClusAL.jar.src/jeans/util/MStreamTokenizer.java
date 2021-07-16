/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
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
/*     */ public class MStreamTokenizer
/*     */ {
/*     */   protected Reader reader;
/*     */   protected int lineno;
/*     */   protected String pushbacktoken;
/*     */   protected int pushbackchar;
/*     */   protected String chartokens;
/*     */   protected int commentchar;
/*     */   protected boolean septype;
/*     */   protected long position;
/*     */   protected String filename;
/*     */   
/*     */   public MStreamTokenizer() {}
/*     */   
/*     */   public MStreamTokenizer(String fname) throws FileNotFoundException {
/*  43 */     this(new FileInputStream(fname));
/*  44 */     this.filename = fname;
/*     */   }
/*     */   
/*     */   public MStreamTokenizer(InputStream stream) {
/*  48 */     this(new InputStreamReader(stream));
/*     */   }
/*     */   
/*     */   public MStreamTokenizer(Reader myreader) {
/*  52 */     this.reader = new BufferedReader(myreader);
/*  53 */     init();
/*     */   }
/*     */   
/*     */   public String getFileName() {
/*  57 */     return this.filename;
/*     */   }
/*     */   
/*     */   public String getFileNameForErrorMsg() {
/*  61 */     if (this.filename == null) return ""; 
/*  62 */     return " while reading '" + this.filename + "'";
/*     */   }
/*     */   
/*     */   public static MStreamTokenizer createStringParser(String strg) {
/*  66 */     MStreamTokenizer tokens = new MStreamTokenizer();
/*  67 */     tokens.reader = new StringReader(strg);
/*  68 */     tokens.init();
/*  69 */     return tokens;
/*     */   }
/*     */   
/*     */   private final void init() {
/*  73 */     this.lineno = 1;
/*  74 */     this.pushbacktoken = null;
/*  75 */     this.septype = true;
/*  76 */     this.pushbackchar = -1;
/*  77 */     this.chartokens = ":;,[]{}";
/*  78 */     this.commentchar = 35;
/*     */   }
/*     */   
/*     */   public final void setSeparatorType(boolean dontUseSpace) {
/*  82 */     this.septype = dontUseSpace;
/*     */   }
/*     */   
/*     */   public final boolean isSeparator(int ch) {
/*  86 */     if (this.septype) return (ch == 32 || ch == 10 || ch == -1 || ch == 9); 
/*  87 */     return (ch == 10 || ch == -1);
/*     */   }
/*     */   
/*     */   public final boolean isRealSeparator(int ch) {
/*  91 */     if (this.septype) return (ch == 32 || ch == 10 || ch == 9); 
/*  92 */     return (ch == 10);
/*     */   }
/*     */   
/*     */   public final String getToken() throws IOException {
/*  96 */     if (this.pushbacktoken == null) {
/*  97 */       int ch = readSignificantChar();
/*  98 */       if (ch != -1) {
/*  99 */         StringBuffer strg = new StringBuffer();
/* 100 */         boolean done = false;
/*     */         while (true) {
/* 102 */           strg.append((char)ch);
/* 103 */           if (this.chartokens.indexOf((char)ch) == -1) {
/* 104 */             ch = readChar();
/* 105 */             if (isSeparator(ch)) {
/* 106 */               pushBackChar(ch);
/* 107 */               done = true;
/*     */             } 
/* 109 */             if (this.chartokens.indexOf((char)ch) != -1) {
/* 110 */               pushBackChar(ch);
/* 111 */               done = true;
/*     */             } 
/*     */           } else {
/* 114 */             done = true;
/*     */           } 
/* 116 */           if (done)
/* 117 */             return strg.toString(); 
/*     */         } 
/* 119 */       }  return null;
/*     */     } 
/*     */     
/* 122 */     String betw = this.pushbacktoken;
/* 123 */     this.pushbacktoken = null;
/* 124 */     return betw;
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getDelimToken(char opendelim, char closedelim) throws IOException {
/* 129 */     int ch = readSignificantChar();
/* 130 */     if (ch == opendelim) {
/* 131 */       int depth = 0;
/* 132 */       StringBuffer strg = new StringBuffer();
/* 133 */       ch = readCharNoPushback();
/* 134 */       while (ch != closedelim || depth > 0) {
/* 135 */         if (ch == opendelim) depth++; 
/* 136 */         if (ch != closedelim || depth > 0) strg.append((char)ch); 
/* 137 */         if (ch == closedelim) depth--; 
/* 138 */         ch = readCharNoPushback();
/*     */       } 
/* 140 */       return strg.toString();
/*     */     } 
/* 142 */     int ch2 = readChar();
/* 143 */     if (isRealSeparator(ch2)) return String.valueOf((char)ch);
/*     */     
/* 145 */     pushBackChar(ch2);
/* 146 */     return String.valueOf((char)ch) + getToken();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getCharToken() throws IOException {
/* 152 */     if (this.pushbacktoken == null) {
/* 153 */       return readSignificantChar();
/*     */     }
/* 155 */     String betw = this.pushbacktoken;
/* 156 */     int len = betw.length();
/* 157 */     if (len >= 2) {
/* 158 */       this.pushbacktoken = betw.substring(1);
/* 159 */       return betw.charAt(0);
/*     */     } 
/* 161 */     this.pushbacktoken = null;
/* 162 */     return betw.charAt(0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final long getPosition() {
/* 168 */     return this.position;
/*     */   }
/*     */   
/*     */   public final void gotoPosition(long pos) throws IOException {
/* 172 */     this.reader.skip(pos - this.position);
/*     */   }
/*     */   
/*     */   public final String readToken() throws IOException {
/* 176 */     int saveline = getLine();
/* 177 */     String token = getToken();
/* 178 */     if (token == null)
/* 179 */       throw new IOException("Unexpected end of file at line: " + saveline); 
/* 180 */     return token;
/*     */   }
/*     */   
/*     */   public final boolean hasMoreTokens() throws IOException {
/* 184 */     String token = getToken();
/* 185 */     if (token == null) {
/* 186 */       return false;
/*     */     }
/* 188 */     pushBackToken(token);
/* 189 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int readSignificantChar() throws IOException {
/*     */     while (true) {
/* 197 */       int ch = readChar();
/* 198 */       if (ch == this.commentchar)
/* 199 */         do { ch = readCharNoPushback(); }
/* 200 */         while (ch != -1 && ch != 10); 
/* 201 */       if (!isRealSeparator(ch))
/* 202 */         return ch; 
/*     */     } 
/*     */   }
/*     */   public final int readChar() throws IOException {
/* 206 */     if (this.pushbackchar == -1) {
/* 207 */       int ch = this.reader.read();
/* 208 */       this.position++;
/* 209 */       if (ch == 13) {
/* 210 */         ch = this.reader.read();
/* 211 */         this.position++;
/*     */       } 
/* 213 */       if (ch == 10) this.lineno++; 
/* 214 */       return ch;
/*     */     } 
/* 216 */     int betw = this.pushbackchar;
/* 217 */     this.pushbackchar = -1;
/* 218 */     return betw;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int readCharNoPushback() throws IOException {
/* 223 */     int ch = this.reader.read();
/* 224 */     this.position++;
/* 225 */     if (ch == 13) {
/* 226 */       ch = this.reader.read();
/* 227 */       this.position++;
/*     */     } 
/* 229 */     if (ch == 10) this.lineno++; 
/* 230 */     return ch;
/*     */   }
/*     */   
/*     */   public final void skipTillLine(int line) throws IOException {
/* 234 */     while (this.lineno < line) {
/* 235 */       readChar();
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean skipTillLine(String line) throws IOException {
/* 240 */     int len = line.length();
/* 241 */     int ch = readChar();
/* 242 */     int first = line.charAt(0);
/*     */     
/*     */     while (true) {
/* 245 */       if (ch == first && len >= 2) {
/* 246 */         int idx = 1;
/* 247 */         ch = readCharNoPushback();
/* 248 */         while (idx < len - 1 && ch == line.charAt(idx)) {
/* 249 */           ch = readCharNoPushback();
/* 250 */           idx++;
/*     */         } 
/* 252 */         if (idx == len - 1) return true; 
/*     */       } 
/* 254 */       if (ch == -1) return false;
/*     */       
/* 256 */       while (ch != 10 && ch != -1)
/* 257 */         ch = readCharNoPushback(); 
/* 258 */       if (ch == -1) return false; 
/* 259 */       ch = readCharNoPushback();
/*     */     } 
/*     */   }
/*     */   
/*     */   public final String readTillEol() throws IOException {
/*     */     int ch;
/* 265 */     StringBuffer strg = new StringBuffer();
/* 266 */     if (this.pushbacktoken == null) {
/* 267 */       ch = readSignificantChar();
/* 268 */       if (ch == -1) return null; 
/*     */     } else {
/* 270 */       strg.append(this.pushbacktoken);
/* 271 */       this.pushbacktoken = null;
/* 272 */       ch = readChar();
/*     */     } 
/* 274 */     while (ch != 10 && ch != -1) {
/* 275 */       strg.append((char)ch);
/* 276 */       ch = readCharNoPushback();
/*     */     } 
/* 278 */     pushBackChar(ch);
/* 279 */     return strg.toString();
/*     */   }
/*     */   
/*     */   public final int readInteger() throws IOException {
/* 283 */     int saveline = getLine();
/* 284 */     String token = readToken();
/*     */     try {
/* 286 */       return Integer.parseInt(token);
/* 287 */     } catch (NumberFormatException e) {
/* 288 */       throw new IOException("Integer value expected at line: " + saveline);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final float readFloat() throws IOException {
/* 293 */     int saveline = getLine();
/* 294 */     String token = readToken();
/*     */     try {
/* 296 */       Float fl = new Float(token);
/* 297 */       return fl.floatValue();
/* 298 */     } catch (NumberFormatException e) {
/* 299 */       throw new IOException("Float value expected at line: " + saveline);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final String isNextTokenIn(String strg) throws IOException {
/* 304 */     String token = getToken();
/* 305 */     if (token == null || strg.indexOf(token) != -1) {
/* 306 */       return token;
/*     */     }
/* 308 */     pushBackToken(token);
/* 309 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isNextToken(String strg) throws IOException {
/* 314 */     boolean yes = false;
/* 315 */     String token = getToken();
/* 316 */     if (token == null) return false; 
/* 317 */     if (token.equals(strg)) { yes = true; }
/* 318 */     else { pushBackToken(token); }
/* 319 */      return yes;
/*     */   }
/*     */   
/*     */   public final boolean isNextToken(char ch) throws IOException {
/* 323 */     return isNextToken(String.valueOf(ch));
/*     */   }
/*     */   
/*     */   public final int readChar(String which) throws IOException {
/* 327 */     int character = getCharToken();
/* 328 */     if (character == -1)
/* 329 */       throw new IOException("Unexpected end of file reading character at line: " + getLine()); 
/* 330 */     int idx = which.indexOf(character);
/* 331 */     if (idx < 0)
/* 332 */       throw new IOException("Character '" + which + "' expected at line: " + getLine() + " (found '" + (char)character + "')"); 
/* 333 */     return idx;
/*     */   }
/*     */   
/*     */   public final void readChar(char ch) throws IOException {
/* 337 */     int character = getCharToken();
/* 338 */     if (character == -1)
/* 339 */       throw new IOException("Unexpected end of file reading character at line: " + getLine()); 
/* 340 */     if (character != ch)
/* 341 */       throw new IOException("Character '" + ch + "' expected at line: " + getLine() + " (found '" + (char)character + "')"); 
/*     */   }
/*     */   
/*     */   public final void pushBackChar(int ch) {
/* 345 */     this.pushbackchar = ch;
/*     */   }
/*     */   
/*     */   public final void pushBackToken(String token) {
/* 349 */     this.pushbacktoken = token;
/*     */   }
/*     */   
/*     */   public final String setCharTokens(String chartokens) {
/* 353 */     String result = this.chartokens;
/* 354 */     this.chartokens = chartokens;
/* 355 */     return result;
/*     */   }
/*     */   
/*     */   public final int setCommentChar(int commentchar) {
/* 359 */     int result = this.commentchar;
/* 360 */     this.commentchar = commentchar;
/* 361 */     return result;
/*     */   }
/*     */   
/*     */   public final String getCharTokens() {
/* 365 */     return this.chartokens;
/*     */   }
/*     */   
/*     */   public final int setCommentChar() {
/* 369 */     return this.commentchar;
/*     */   }
/*     */   
/*     */   public final int getLine() {
/* 373 */     return this.lineno;
/*     */   }
/*     */   
/*     */   public final Reader getReader() {
/* 377 */     return this.reader;
/*     */   }
/*     */   
/*     */   public final void close() throws IOException {
/* 381 */     this.reader.close();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 385 */     return "MStreamTokenizer [" + this.commentchar + "] [" + this.chartokens + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\MStreamTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */