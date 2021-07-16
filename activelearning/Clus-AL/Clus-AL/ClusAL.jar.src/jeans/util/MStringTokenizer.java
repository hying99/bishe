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
/*     */ public class MStringTokenizer
/*     */ {
/*     */   protected String pushbacktoken;
/*     */   protected int pushbackchar;
/*  36 */   protected String chartokens = ":;,[]{} \t"; protected String edblock;
/*     */   protected String stblock;
/*     */   
/*     */   public void setString(String strg) {
/*  40 */     this.strg = strg;
/*  41 */     this.pos = 0;
/*  42 */     this.pushbacktoken = null;
/*  43 */     this.pushbackchar = -1;
/*     */   }
/*     */   protected String spacechars; protected String strg; protected int pos;
/*     */   public void setBlockChars(String stBlock, String edBlock) {
/*  47 */     this.stblock = stBlock;
/*  48 */     this.edblock = edBlock;
/*     */   }
/*     */   
/*     */   public void setSpaceChars(String space) {
/*  52 */     this.spacechars = space;
/*     */   }
/*     */   
/*     */   public String getToken() {
/*  56 */     if (this.pushbacktoken == null) {
/*  57 */       int ch = readSignificantChar();
/*  58 */       if (ch != -1) {
/*  59 */         StringBuffer strg = new StringBuffer();
/*  60 */         boolean done = false;
/*  61 */         if (this.stblock.indexOf((char)ch) != -1)
/*     */         {
/*     */           
/*  64 */           while (!done) {
/*  65 */             ch = readChar();
/*  66 */             if (ch == -1) {
/*  67 */               done = true; continue;
/*     */             } 
/*  69 */             if (this.edblock.indexOf((char)ch) != -1) {
/*  70 */               done = true; continue;
/*     */             } 
/*  72 */             strg.append((char)ch);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*  77 */         while (!done) {
/*  78 */           strg.append((char)ch);
/*  79 */           if (this.chartokens.indexOf((char)ch) == -1) {
/*  80 */             ch = readChar();
/*  81 */             if (ch == -1 || this.spacechars.indexOf((char)ch) != -1) {
/*  82 */               done = true; continue;
/*  83 */             }  if (this.chartokens.indexOf((char)ch) != -1) {
/*  84 */               pushBackChar(ch);
/*  85 */               done = true;
/*     */             }  continue;
/*     */           } 
/*  88 */           done = true;
/*     */         } 
/*     */         
/*  91 */         return strg.toString();
/*     */       } 
/*  93 */       return null;
/*     */     } 
/*     */     
/*  96 */     String betw = this.pushbacktoken;
/*  97 */     this.pushbacktoken = null;
/*  98 */     return betw;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readToken() throws IOException {
/* 103 */     String token = getToken();
/* 104 */     if (token == null)
/* 105 */       throw new IOException("Unexpected end of file"); 
/* 106 */     return token;
/*     */   }
/*     */   
/*     */   public boolean hasMoreTokens() throws IOException {
/* 110 */     String token = getToken();
/* 111 */     if (token == null) {
/* 112 */       return false;
/*     */     }
/* 114 */     pushBackToken(token);
/* 115 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int readSignificantChar() {
/* 121 */     int ch = readChar();
/* 122 */     while (this.spacechars.indexOf((char)ch) != -1) {
/* 123 */       ch = readChar();
/*     */     }
/* 125 */     return ch;
/*     */   }
/*     */   
/*     */   public int read() {
/* 129 */     if (this.pos == this.strg.length()) return -1; 
/* 130 */     return this.strg.charAt(this.pos++);
/*     */   }
/*     */   
/*     */   public int readChar() {
/* 134 */     if (this.pushbackchar == -1) {
/* 135 */       int ch = read();
/* 136 */       return ch;
/*     */     } 
/* 138 */     int betw = this.pushbackchar;
/* 139 */     this.pushbackchar = -1;
/* 140 */     return betw;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInteger() throws IOException {
/* 145 */     String token = readToken();
/*     */     try {
/* 147 */       return Integer.parseInt(token);
/* 148 */     } catch (NumberFormatException e) {
/* 149 */       throw new IOException("Integer value expected");
/*     */     } 
/*     */   }
/*     */   
/*     */   public float readFloat() throws IOException {
/* 154 */     String token = readToken();
/*     */     try {
/* 156 */       Float fl = new Float(token);
/* 157 */       return fl.floatValue();
/* 158 */     } catch (NumberFormatException e) {
/* 159 */       throw new IOException("Float value expected");
/*     */     } 
/*     */   }
/*     */   
/*     */   public String isNextTokenIn(String strg) throws IOException {
/* 164 */     String token = getToken();
/* 165 */     if (token == null || strg.indexOf(token) != -1) {
/* 166 */       return token;
/*     */     }
/* 168 */     pushBackToken(token);
/* 169 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNextToken(String strg) throws IOException {
/* 174 */     boolean yes = false;
/* 175 */     String token = getToken();
/* 176 */     if (token == null) return false; 
/* 177 */     if (token.equals(strg)) { yes = true; }
/* 178 */     else { pushBackToken(token); }
/* 179 */      return yes;
/*     */   }
/*     */   
/*     */   public int readChar(String which) throws IOException {
/* 183 */     String character = getToken();
/* 184 */     if (character == null)
/* 185 */       throw new IOException("Unexpected end of file reading character"); 
/* 186 */     int idx = which.indexOf(character.charAt(0));
/* 187 */     if (idx < 0 || character.length() != 1)
/* 188 */       throw new IOException("Character '" + which + "' expected"); 
/* 189 */     return idx;
/*     */   }
/*     */   
/*     */   public void readChar(char ch) throws IOException {
/* 193 */     String character = getToken();
/* 194 */     if (character == null)
/* 195 */       throw new IOException("Unexpected end of file reading character"); 
/* 196 */     if (!character.equals(String.valueOf(ch)))
/* 197 */       throw new IOException("Character '" + ch + "' expected at line"); 
/*     */   }
/*     */   
/*     */   public void pushBackChar(int ch) {
/* 201 */     this.pushbackchar = ch;
/*     */   }
/*     */   
/*     */   public void pushBackToken(String token) {
/* 205 */     this.pushbacktoken = token;
/*     */   }
/*     */   
/*     */   public String setCharTokens(String chartokens) {
/* 209 */     String result = this.chartokens;
/* 210 */     this.chartokens = chartokens;
/* 211 */     return result;
/*     */   }
/*     */   
/*     */   public String getCharTokens() {
/* 215 */     return this.chartokens;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\MStringTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */