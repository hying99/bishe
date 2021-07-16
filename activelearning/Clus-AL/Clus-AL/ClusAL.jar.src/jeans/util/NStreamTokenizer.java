/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
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
/*     */ public class NStreamTokenizer
/*     */ {
/*     */   public static final int BUFFER_SINGLE = 512;
/*     */   public static final int BUFFER_SIZE = 1024;
/*     */   public static final char CHAR_LF = '\n';
/*     */   public static final char CHAR_CR = '\r';
/*     */   public static final char CHAR_EOF = '\000';
/*     */   public static final int TYPE_DELIM = 0;
/*     */   public static final int TYPE_SKIP = 1;
/*     */   public static final int TYPE_TOKEN = 2;
/*     */   protected Reader m_reader;
/*     */   protected int m_lineNo;
/*     */   protected int m_prevLineNo;
/*     */   protected int m_crPos;
/*     */   protected int m_prevCrPos;
/*     */   protected char[] m_doubleBuffer;
/*     */   protected int m_bufOfs;
/*     */   protected int m_bufStart;
/*     */   protected int m_posModulo;
/*     */   protected int m_markPos;
/*     */   protected CallBackFunction m_posCallback;
/*     */   
/*     */   public NStreamTokenizer(Reader reader) {
/*  50 */     this.m_reader = reader;
/*  51 */     this.m_lineNo = this.m_prevLineNo = 1;
/*  52 */     this.m_crPos = this.m_prevCrPos = 0;
/*  53 */     this.m_bufOfs = -1024;
/*  54 */     this.m_posCallback = null;
/*  55 */     this.m_bufStart = -1;
/*  56 */     this.m_doubleBuffer = new char[1024];
/*     */   }
/*     */   
/*     */   public String getToken() throws IOException {
/*  60 */     return null;
/*     */   }
/*     */   
/*     */   public String readToken() throws IOException {
/*  64 */     String token = getToken();
/*  65 */     if (token == null)
/*  66 */       throw new IOException("Unexpected end of file at line: " + getPrevLine()); 
/*  67 */     return token;
/*     */   }
/*     */   
/*     */   public boolean hasMoreTokens() throws IOException {
/*  71 */     String token = getToken();
/*  72 */     if (token == null) {
/*  73 */       return false;
/*     */     }
/*  75 */     pushBackToken();
/*  76 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void doRead(int start, int len) throws IOException {
/*  82 */     int nbRead = this.m_reader.read(this.m_doubleBuffer, start, len);
/*  83 */     if (nbRead == -1) nbRead = 0; 
/*  84 */     int till = start + len;
/*  85 */     for (int ctr = start + nbRead; ctr < till; ) { this.m_doubleBuffer[ctr] = Character.MIN_VALUE; ctr++; }
/*     */   
/*     */   }
/*     */   public char readChar() throws IOException {
/*  89 */     int bufPos = this.m_crPos - this.m_bufOfs;
/*  90 */     if (bufPos >= 1024) {
/*  91 */       if (this.m_bufStart != -1) {
/*  92 */         if (this.m_bufStart == 0) {
/*  93 */           doRead(0, 512);
/*  94 */           this.m_bufStart = 512;
/*  95 */           this.m_bufOfs += 512;
/*     */         } else {
/*  97 */           doRead(512, 512);
/*  98 */           this.m_bufStart = 0;
/*  99 */           this.m_bufOfs += 512;
/*     */         } 
/*     */       } else {
/* 102 */         doRead(0, 1024);
/* 103 */         this.m_bufStart = 0;
/* 104 */         this.m_bufOfs = 0;
/*     */       } 
/* 106 */       bufPos = this.m_crPos - this.m_bufOfs;
/*     */     } 
/* 108 */     bufPos = (bufPos + this.m_bufStart) % 1024;
/* 109 */     char ch = this.m_doubleBuffer[bufPos];
/* 110 */     if (ch == '\n') this.m_lineNo++; 
/* 111 */     this.m_crPos++;
/* 112 */     if (this.m_posCallback != null && this.m_crPos % this.m_posModulo == 0)
/* 113 */       this.m_posCallback.callBackFunction(this); 
/* 114 */     return ch;
/*     */   }
/*     */   
/*     */   public String readTillEol() throws IOException {
/* 118 */     markPosition();
/* 119 */     char ch = readChar();
/* 120 */     while (ch != '\n' && ch != '\r' && ch != '\000')
/* 121 */       ch = readChar(); 
/* 122 */     if (ch == '\000') return null; 
/* 123 */     pushBackChar();
/* 124 */     String res = makeString();
/* 125 */     if (readChar() == '\r' && 
/* 126 */       readChar() != '\n') pushBackChar();
/*     */     
/* 128 */     return res;
/*     */   }
/*     */   
/*     */   public void markPosition() {
/* 132 */     if (this.m_bufStart != -1) {
/* 133 */       this.m_markPos = (this.m_crPos - this.m_bufOfs + this.m_bufStart) % 1024;
/*     */     } else {
/* 135 */       this.m_markPos = 0;
/*     */     } 
/*     */   }
/*     */   public void pushBackChar() {
/* 139 */     this.m_crPos--;
/* 140 */     int bufPos = (this.m_crPos - this.m_bufOfs + this.m_bufStart) % 1024;
/* 141 */     if (this.m_doubleBuffer[bufPos] == '\n') this.m_lineNo--; 
/*     */   }
/*     */   
/*     */   public String makeString() {
/* 145 */     int bufPos = (this.m_crPos - this.m_bufOfs + this.m_bufStart) % 1024;
/* 146 */     if (bufPos >= this.m_markPos) {
/* 147 */       return new String(this.m_doubleBuffer, this.m_markPos, bufPos - this.m_markPos);
/*     */     }
/* 149 */     return new String(this.m_doubleBuffer, bufPos, 512 - bufPos - 1) + new String(this.m_doubleBuffer, 0, this.m_markPos);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String readTillDelim(char delim) throws IOException {
/* 155 */     markPosition();
/* 156 */     char ch = readChar();
/* 157 */     while (ch != delim && ch != '\000')
/* 158 */       ch = readChar(); 
/* 159 */     if (ch == '\000') return null; 
/* 160 */     pushBackChar();
/* 161 */     String res = makeString();
/* 162 */     readChar();
/* 163 */     return res;
/*     */   }
/*     */   
/*     */   public int readInteger() throws IOException {
/*     */     try {
/* 168 */       return Integer.parseInt(readToken());
/* 169 */     } catch (NumberFormatException e) {
/* 170 */       throw new IOException("Integer value expected at line: " + getPrevLine());
/*     */     } 
/*     */   }
/*     */   
/*     */   public float readFloat() throws IOException {
/*     */     try {
/* 176 */       Float fl = new Float(readToken());
/* 177 */       return fl.floatValue();
/* 178 */     } catch (NumberFormatException e) {
/* 179 */       throw new IOException("Float value expected at line: " + getPrevLine());
/*     */     } 
/*     */   }
/*     */   
/*     */   public String isNextTokenIn(String strg) throws IOException {
/* 184 */     String token = getToken();
/* 185 */     if (token == null || strg.indexOf(token) != -1) {
/* 186 */       return token;
/*     */     }
/* 188 */     pushBackToken();
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNextToken(String strg) throws IOException {
/* 194 */     String token = getToken();
/* 195 */     if (token == null) return false; 
/* 196 */     if (token.equals(strg)) {
/* 197 */       return true;
/*     */     }
/* 199 */     pushBackToken();
/* 200 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNextChar(char ch) throws IOException {
/* 205 */     if (readChar() == ch) {
/* 206 */       return true;
/*     */     }
/* 208 */     pushBackChar();
/* 209 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void pushBackToken() {}
/*     */ 
/*     */   
/*     */   public int getLine() {
/* 217 */     return this.m_lineNo;
/*     */   }
/*     */   
/*     */   public int getPrevLine() {
/* 221 */     return this.m_prevLineNo;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 225 */     this.m_reader.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\NStreamTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */