/*     */ package clus.data.io;
/*     */ 
/*     */ import clus.main.Settings;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import jeans.util.FileUtil;
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
/*     */ public class ClusReader
/*     */ {
/*     */   String m_Name;
/*  35 */   int m_Row = 0; int m_Attr = 0; int m_LastChar = 0;
/*     */   MStreamTokenizer m_Token;
/*  37 */   StringBuffer m_Scratch = new StringBuffer();
/*     */   Settings m_Settings;
/*     */   boolean m_IsClosed;
/*     */   
/*     */   public ClusReader(String fname, Settings sett) throws IOException {
/*  42 */     this.m_Name = fname;
/*  43 */     this.m_Settings = sett;
/*  44 */     this.m_IsClosed = true;
/*  45 */     open();
/*     */   }
/*     */   
/*     */   public String getName() {
/*  49 */     return this.m_Name;
/*     */   }
/*     */   
/*     */   public int getRow() {
/*  53 */     return this.m_Row;
/*     */   }
/*     */   
/*     */   public int countRows() throws IOException {
/*  57 */     int nbr = countRows2();
/*  58 */     reOpen();
/*  59 */     return nbr;
/*     */   }
/*     */   
/*     */   public MStreamTokenizer zipOpen(String fname) throws IOException {
/*  63 */     ZipInputStream zip = new ZipInputStream(new FileInputStream(fname));
/*  64 */     zip.getNextEntry();
/*  65 */     return new MStreamTokenizer(zip);
/*     */   }
/*     */   
/*     */   public void open() throws IOException {
/*  69 */     String fname = this.m_Name;
/*  70 */     if (this.m_Settings != null) this.m_Settings.getFileAbsolute(this.m_Name); 
/*  71 */     if (FileUtil.fileExists(fname)) {
/*  72 */       if (fname.toUpperCase().endsWith(".ZIP")) {
/*  73 */         this.m_Token = zipOpen(fname);
/*     */       } else {
/*  75 */         this.m_Token = new MStreamTokenizer(fname);
/*     */       } 
/*     */     } else {
/*  78 */       String zipname = fname + ".zip";
/*  79 */       if (FileUtil.fileExists(zipname)) {
/*  80 */         this.m_Token = zipOpen(zipname);
/*     */       } else {
/*  82 */         throw new FileNotFoundException("'" + fname + "'");
/*     */       } 
/*     */     } 
/*  85 */     this.m_Token.setCommentChar(37);
/*  86 */     this.m_IsClosed = false;
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/*  90 */     return this.m_IsClosed;
/*     */   }
/*     */   
/*     */   public void tryReOpen() throws IOException {
/*  94 */     if (isClosed()) reOpen(); 
/*     */   }
/*     */   
/*     */   public void reOpen() throws IOException {
/*  98 */     close();
/*  99 */     open();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 103 */     this.m_Token.close();
/* 104 */     this.m_IsClosed = true;
/*     */   }
/*     */   
/*     */   public MStreamTokenizer getTokens() {
/* 108 */     return this.m_Token;
/*     */   }
/*     */   
/*     */   public boolean isNextToken(String token) throws IOException {
/* 112 */     return this.m_Token.isNextToken(token);
/*     */   }
/*     */   
/*     */   public boolean hasMoreTokens() throws IOException {
/* 116 */     Reader reader = this.m_Token.getReader();
/* 117 */     int ch = reader.read();
/* 118 */     setLastChar(ch);
/* 119 */     return (ch != -1);
/*     */   }
/*     */   
/*     */   public boolean isEol() throws IOException {
/* 123 */     Reader reader = this.m_Token.getReader();
/* 124 */     int ch = getNextChar(reader);
/* 125 */     if (ch == 10 || ch == 13) return true; 
/* 126 */     setLastChar(ch);
/* 127 */     return false;
/*     */   }
/*     */   
/*     */   public void setLastChar(int ch) {
/* 131 */     this.m_LastChar = ch;
/*     */   }
/*     */   
/*     */   public int getNextChar(Reader reader) throws IOException {
/* 135 */     if (this.m_LastChar != 0) {
/* 136 */       int ch = this.m_LastChar;
/* 137 */       this.m_LastChar = 0;
/* 138 */       return ch;
/*     */     } 
/* 140 */     return reader.read();
/*     */   }
/*     */   
/*     */   public int getNextChar() throws IOException {
/* 144 */     return getNextChar(this.m_Token.getReader());
/*     */   }
/*     */   
/*     */   public int getNextCharNoSpace() throws IOException {
/* 148 */     int ch = getNextChar();
/* 149 */     while (ch == 32 || ch == 9) {
/* 150 */       ch = getNextChar();
/*     */     }
/* 152 */     return ch;
/*     */   }
/*     */   
/*     */   public boolean isNextChar(int ch) throws IOException {
/* 156 */     int found = getNextChar();
/* 157 */     if (ch == found) return true; 
/* 158 */     setLastChar(found);
/* 159 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isNextCharNoSpace(int ch) throws IOException {
/* 163 */     int found = getNextCharNoSpace();
/* 164 */     if (ch == found) return true; 
/* 165 */     setLastChar(found);
/* 166 */     return false;
/*     */   }
/*     */   
/*     */   public void ensureNextChar(int ch) throws IOException {
/* 170 */     int found = getNextChar();
/* 171 */     if (ch != found) {
/* 172 */       throw new IOException("Character '" + (char)ch + "' expected on row " + this.m_Row + ", not '" + (char)found + "'");
/*     */     }
/*     */   }
/*     */   
/*     */   public void readEol() throws IOException {
/* 177 */     boolean allowall = false;
/* 178 */     Reader reader = this.m_Token.getReader();
/* 179 */     int ch = getNextChar(reader);
/* 180 */     while (ch != -1) {
/* 181 */       if (ch == 10 || ch == 13) {
/* 182 */         this.m_Attr = 0;
/* 183 */         this.m_Row++; break;
/*     */       } 
/* 185 */       if (ch == 37) {
/* 186 */         allowall = true;
/* 187 */       } else if (ch != 32 && ch != 9 && !allowall) {
/* 188 */         int cnt = 0;
/* 189 */         StringBuffer err = new StringBuffer();
/* 190 */         while (ch != 10 && ch != 13 && cnt < 100) {
/* 191 */           err.append((char)ch);
/* 192 */           ch = getNextChar(reader);
/* 193 */           cnt++;
/*     */         } 
/* 195 */         throw new IOException("Too many data on row " + this.m_Row + ": '" + err.toString() + "'");
/*     */       } 
/* 197 */       ch = reader.read();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void readTillEol() throws IOException {
/* 202 */     Reader reader = this.m_Token.getReader();
/* 203 */     int ch = getNextChar(reader);
/* 204 */     while (ch != -1) {
/* 205 */       if (ch == 10 || ch == 13) {
/* 206 */         setLastChar(13);
/*     */         break;
/*     */       } 
/* 209 */       ch = reader.read();
/*     */     } 
/*     */   }
/*     */   
/*     */   public String readString() throws IOException {
/* 214 */     int nb = 0;
/* 215 */     Reader reader = this.m_Token.getReader();
/* 216 */     this.m_Scratch.setLength(0);
/* 217 */     int ch = getNextChar(reader);
/* 218 */     while (ch != -1 && ch != 44 && ch != 125) {
/* 219 */       if (ch == 37) {
/* 220 */         readTillEol();
/*     */         break;
/*     */       } 
/* 223 */       if (ch != 9 && ch != 10 && ch != 13) {
/* 224 */         this.m_Scratch.append((char)ch);
/* 225 */         if (ch != 32) nb++; 
/* 226 */         if (nb == 1 && ch == 34) {
/* 227 */           ch = reader.read();
/*     */           
/* 229 */           while (ch != -1 && ch != 10 && ch != 13 && ch != 34) {
/* 230 */             this.m_Scratch.append((char)ch);
/* 231 */             ch = reader.read();
/*     */           } 
/* 233 */           if (ch == 34) this.m_Scratch.append((char)ch); 
/*     */         } 
/*     */       } else {
/* 236 */         if (ch == 10 || ch == 13) setLastChar(13); 
/* 237 */         if (nb > 0)
/*     */           break; 
/* 239 */       }  ch = reader.read();
/*     */     } 
/* 241 */     if (ch == 125) setLastChar(ch); 
/* 242 */     String result = this.m_Scratch.toString().trim();
/* 243 */     if (result.length() > 0) {
/* 244 */       this.m_Attr++;
/* 245 */       return result;
/*     */     } 
/* 247 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readName() throws IOException {
/* 252 */     readNoSpace();
/* 253 */     if (this.m_Scratch.length() > 0) {
/* 254 */       String value = this.m_Scratch.toString();
/* 255 */       return value;
/*     */     } 
/* 257 */     throw new IOException("Error reading name at row " + (this.m_Row + 1));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean readNoSpace() throws IOException {
/* 262 */     int nb = 0;
/* 263 */     Reader reader = this.m_Token.getReader();
/* 264 */     this.m_Scratch.setLength(0);
/* 265 */     int ch = getNextChar(reader);
/* 266 */     while (ch != -1 && ch != 44 && ch != 125) {
/* 267 */       if (ch != 32 && ch != 9 && ch != 10 && ch != 13) {
/* 268 */         if (ch != 39 && ch != 34) {
/* 269 */           this.m_Scratch.append((char)ch);
/* 270 */           nb++;
/*     */         } 
/*     */       } else {
/* 273 */         if (ch == 10 || ch == 13) setLastChar(13); 
/* 274 */         if (nb > 0)
/*     */           break; 
/* 276 */       }  ch = reader.read();
/*     */     } 
/* 278 */     if (ch == 125) setLastChar(ch); 
/* 279 */     return (this.m_Scratch.length() > 0);
/*     */   }
/*     */   
/*     */   public double getFloat() throws IOException {
/* 283 */     if (this.m_Scratch.length() > 0) {
/* 284 */       this.m_Attr++;
/* 285 */       String value = this.m_Scratch.toString();
/*     */       try {
/* 287 */         if (value.equals("?")) return Double.POSITIVE_INFINITY; 
/* 288 */         return Double.parseDouble(value);
/* 289 */       } catch (NumberFormatException e) {
/* 290 */         throw new IOException("Error parsing numeric value '" + value + "' for attribute " + this.m_Attr + " at row " + (this.m_Row + 1));
/*     */       } 
/*     */     } 
/* 293 */     throw new IOException("Error reading numeric attirbute " + this.m_Attr + " at row " + (this.m_Row + 1));
/*     */   }
/*     */ 
/*     */   
/*     */   public double readFloat() throws IOException {
/* 298 */     readNoSpace();
/* 299 */     return getFloat();
/*     */   }
/*     */   
/*     */   public int readIntIndex() throws IOException {
/* 303 */     readNoSpace();
/* 304 */     if (this.m_Scratch.length() > 0) {
/* 305 */       String value = this.m_Scratch.toString();
/*     */       try {
/* 307 */         return Integer.parseInt(value);
/* 308 */       } catch (NumberFormatException e) {
/* 309 */         throw new IOException("Error parsing integer index '" + value + "' at row " + (this.m_Row + 1));
/*     */       } 
/*     */     } 
/* 312 */     throw new IOException("Error: empty index at row " + (this.m_Row + 1));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String readTimeSeries() throws IOException {
/* 318 */     Reader reader = this.m_Token.getReader();
/* 319 */     this.m_Scratch.setLength(0);
/* 320 */     int ch = getNextChar(reader);
/* 321 */     while ((ch == 32 || ch == 9) && ch != -1) {
/* 322 */       ch = getNextChar(reader);
/*     */     }
/* 324 */     if (ch != 91) {
/* 325 */       throw new IOException("Error parsing time series attribute " + this.m_Attr + " at row " + (this.m_Row + 1) + ": '[' not found");
/*     */     }
/* 327 */     this.m_Scratch.append((char)ch);
/*     */     
/* 329 */     ch = getNextChar(reader);
/* 330 */     while (ch != -1) {
/* 331 */       if (ch != 9) {
/* 332 */         this.m_Scratch.append((char)ch);
/*     */       }
/* 334 */       if (ch == 93) {
/* 335 */         ch = getNextChar(reader);
/* 336 */         if (ch == 10 || ch == 13) setLastChar(13); 
/*     */         break;
/*     */       } 
/* 339 */       ch = getNextChar(reader);
/*     */     } 
/* 341 */     String result = this.m_Scratch.toString().trim();
/* 342 */     if (result.length() > 0) {
/* 343 */       this.m_Attr++;
/* 344 */       return result;
/*     */     } 
/* 346 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean skipTillComma() throws IOException {
/* 353 */     int nb = 0;
/* 354 */     boolean is_ts = false;
/* 355 */     Reader reader = this.m_Token.getReader();
/* 356 */     int ch = getNextChar(reader);
/* 357 */     while (ch != -1 && ch != 44 && ch != 125) {
/* 358 */       if (ch != 32 && ch != 9 && ch != 10 && ch != 13) {
/* 359 */         if (ch == 91) {
/* 360 */           is_ts = true;
/*     */           break;
/*     */         } 
/* 363 */         nb++;
/*     */       } else {
/* 365 */         if (ch == 10 || ch == 13) setLastChar(13); 
/* 366 */         if (nb > 0)
/*     */           break; 
/* 368 */       }  ch = reader.read();
/*     */     } 
/* 370 */     int prev = ch;
/* 371 */     while (ch != -1 && prev != 93 && is_ts) {
/* 372 */       if (ch != 32 && ch != 9 && ch != 10 && ch != 13) {
/* 373 */         nb++;
/*     */       } else {
/* 375 */         if (ch == 10 || ch == 13) setLastChar(13); 
/* 376 */         if (nb > 0)
/*     */           break; 
/* 378 */       }  prev = ch;
/* 379 */       ch = reader.read();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 386 */     if (ch == 125) this.m_LastChar = ch; 
/* 387 */     return (ch != -1);
/*     */   }
/*     */   
/*     */   public int countRows2() throws IOException {
/* 391 */     int nbrows = 0;
/* 392 */     int nbchars = 0;
/* 393 */     Reader reader = this.m_Token.getReader();
/* 394 */     int ch = reader.read();
/* 395 */     while (ch != -1) {
/* 396 */       if (ch == 10 || ch == 13) {
/* 397 */         if (nbchars > 0) nbrows++; 
/* 398 */         nbchars = 0;
/* 399 */       } else if (ch != 32 && ch != 9) {
/* 400 */         nbchars++;
/*     */       } 
/* 402 */       ch = reader.read();
/*     */     } 
/*     */ 
/*     */     
/* 406 */     return nbrows;
/*     */   }
/*     */   
/*     */   public boolean ensureAtEnd() throws IOException {
/* 410 */     Reader reader = this.m_Token.getReader();
/* 411 */     int ch = reader.read();
/* 412 */     while (ch == 32 || ch == 9 || ch == 10 || ch == 13) {
/* 413 */       ch = reader.read();
/*     */     }
/* 415 */     return (ch == -1);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\io\ClusReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */