/*     */ package clus.model.modelio;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.NominalTest;
/*     */ import clus.model.test.NumericTest;
/*     */ import clus.model.test.SubsetTest;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MStringTokenizer;
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
/*     */ public class ClusTreeReader
/*     */ {
/*  39 */   public static final String[] YESNO = new String[] { "yes", "no" };
/*     */   
/*     */   String m_FName;
/*     */   ClusSchema m_Schema;
/*     */   LineNumberReader m_Reader;
/*  44 */   MStringTokenizer m_Tokens = createTokenizer();
/*     */   int m_StartLine;
/*     */   int m_CrLine;
/*     */   int m_PushBack;
/*     */   boolean m_IsReading;
/*     */   
/*     */   public ClusNode loadTree(String fname, ClusSchema schema) throws IOException {
/*  51 */     this.m_FName = fname;
/*  52 */     this.m_Schema = schema;
/*  53 */     System.out.println("Loading constraint file: " + fname);
/*  54 */     LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
/*  55 */     String line = rdr.readLine();
/*     */     
/*  57 */     while (line != null && isSkipLine(line)) {
/*  58 */       line = rdr.readLine();
/*     */     }
/*     */     
/*  61 */     ClusNode root = null;
/*  62 */     if (line != null) {
/*  63 */       root = readTree(line, rdr);
/*     */     }
/*  65 */     line = getFirstNonEmptyLine(rdr);
/*  66 */     if (line != null) {
/*  67 */       createError("Extra data after tree '" + line + "'");
/*     */     }
/*  69 */     rdr.close();
/*  70 */     return root;
/*     */   }
/*     */   boolean m_NoPartialTree; ArrayList m_Lines; ArrayList m_StartPos; String m_LineAfterTree;
/*     */   public ClusNode loadOutTree(String fname, ClusSchema schema, String find) throws IOException {
/*  74 */     this.m_FName = fname;
/*  75 */     this.m_Schema = schema;
/*  76 */     this.m_NoPartialTree = true;
/*  77 */     System.out.println("Loading .out file: " + fname);
/*  78 */     LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
/*  79 */     String line = rdr.readLine();
/*  80 */     while (line != null && !line.trim().equals(find)) {
/*  81 */       line = rdr.readLine();
/*     */     }
/*     */     
/*  84 */     line = rdr.readLine();
/*  85 */     line = rdr.readLine();
/*     */     
/*  87 */     while (line != null && isSkipLine(line)) {
/*  88 */       line = rdr.readLine();
/*     */     }
/*     */     
/*  91 */     ClusNode root = null;
/*  92 */     if (line != null)
/*     */     {
/*  94 */       root = readTree(line, rdr);
/*     */     }
/*  96 */     line = getFirstNonEmptyLine(rdr);
/*  97 */     this.m_LineAfterTree = line;
/*  98 */     rdr.close();
/*  99 */     return root;
/*     */   }
/*     */   
/*     */   public ClusNode loadTreeTree(String fname, ClusSchema schema) throws IOException {
/* 103 */     this.m_FName = fname;
/* 104 */     this.m_Schema = schema;
/* 105 */     this.m_NoPartialTree = true;
/* 106 */     System.out.println("Loading .tree file: " + fname);
/* 107 */     LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
/* 108 */     String line = rdr.readLine();
/*     */ 
/*     */     
/* 111 */     ClusNode root = null;
/* 112 */     root = readTree(line, rdr);
/*     */     
/* 114 */     rdr.close();
/* 115 */     return root;
/*     */   }
/*     */   
/*     */   public ClusNode readTree(String line, LineNumberReader rdr) throws IOException {
/* 119 */     this.m_Reader = rdr;
/* 120 */     this.m_IsReading = true;
/* 121 */     this.m_PushBack = -1;
/* 122 */     return readTree(line);
/*     */   }
/*     */   
/*     */   public ClusNode readTree(String line) throws IOException {
/* 126 */     String trim = line.trim();
/* 127 */     System.out.println("Reading: '" + line + "'");
/* 128 */     ClusNode result = new ClusNode();
/* 129 */     if (!trim.equals("?")) {
/* 130 */       int arity = 2;
/* 131 */       String[] branchnames = YESNO;
/* 132 */       MStringTokenizer tokens = getTokens();
/* 133 */       tokens.setString(trim);
/* 134 */       if (tokens.hasMoreTokens()) {
/* 135 */         String attrname = tokens.getToken();
/* 136 */         ClusAttrType attr = findAttrType(attrname, allowPartialTree());
/* 137 */         if (attr != null) {
/* 138 */           readTest(attr, result, tokens);
/* 139 */           if (result.getTest() instanceof NominalTest) {
/* 140 */             NominalAttrType ntype = (NominalAttrType)attr;
/* 141 */             arity = ntype.getNbValues();
/* 142 */             branchnames = ntype.getValues();
/*     */           } 
/*     */         } else {
/* 145 */           return result;
/*     */         } 
/*     */       } else {
/* 148 */         createError("No attribute name found");
/*     */       } 
/* 150 */       result.setNbChildren(arity);
/* 151 */       boolean[] has_ch = new boolean[arity];
/* 152 */       boolean is_wildcard = false;
/* 153 */       for (int i = 0; i < arity && !is_wildcard; i++) {
/* 154 */         is_wildcard = readNextChild(result, branchnames, has_ch);
/* 155 */         if (is_wildcard) {
/* 156 */           readWildCardChild(result, has_ch);
/* 157 */           startPlayBack();
/* 158 */           for (int j = i + 1; j < arity; j++) {
/* 159 */             reset();
/* 160 */             readWildCardChild(result, has_ch);
/*     */           } 
/* 162 */           stopPlayBack();
/*     */         } 
/*     */       } 
/*     */     } else {
/* 166 */       checkPartialTreeAllowed("while reading child node");
/*     */     } 
/* 168 */     return result;
/*     */   }
/*     */   
/*     */   public String getLineAfterTree() {
/* 172 */     return this.m_LineAfterTree;
/*     */   }
/*     */   
/*     */   public boolean allowPartialTree() {
/* 176 */     return !this.m_NoPartialTree;
/*     */   }
/*     */   
/*     */   public void checkPartialTreeAllowed(String str) throws IOException {
/* 180 */     if (!allowPartialTree()) {
/* 181 */       createError("No question marks allowed in tree (" + str + ")");
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isSkipLine(String line) {
/* 186 */     String trim = line.trim();
/* 187 */     if (trim.equals("")) return true; 
/* 188 */     if (trim.length() > 1 && trim.charAt(0) == '%') return true; 
/* 189 */     return false;
/*     */   }
/*     */   
/*     */   public ClusAttrType findAttrType(String name, boolean shouldfind) throws IOException {
/* 193 */     ClusAttrType type = this.m_Schema.getAttrType(name);
/* 194 */     if (type == null && shouldfind) {
/* 195 */       createError("Unknown attribute name: '" + name + "'");
/*     */     }
/* 197 */     return type;
/*     */   }
/*     */   
/*     */   public void readCharTokens(String chars, MStringTokenizer tokens) throws IOException {
/* 201 */     String token = tokens.getToken();
/* 202 */     for (int pos = 0; pos < chars.length(); pos++) {
/* 203 */       if (!chars.substring(pos, pos + 1).equals(token)) {
/* 204 */         createError("Expected '" + chars.substring(pos, pos + 1) + "', not '" + token + "'");
/*     */       }
/* 206 */       if (pos < chars.length() - 1) {
/* 207 */         if (tokens.hasMoreTokens()) {
/* 208 */           token = tokens.getToken();
/*     */         } else {
/* 210 */           createError("Expected '" + chars.substring(pos + 1, pos + 2) + "' at end of line");
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void readNumericTest(ClusAttrType attr, ClusNode node, MStringTokenizer tokens) throws IOException {
/* 217 */     NumericTest test = new NumericTest(attr);
/* 218 */     if (tokens.hasMoreTokens()) {
/* 219 */       readCharTokens(">", tokens);
/* 220 */       if (tokens.hasMoreTokens()) {
/* 221 */         String value = tokens.getToken();
/* 222 */         if (value.equals("?")) {
/* 223 */           checkPartialTreeAllowed("in numeric test");
/*     */         } else {
/*     */           try {
/* 226 */             double bound = Double.parseDouble(value);
/* 227 */             test.setBound(bound);
/* 228 */           } catch (NumberFormatException e) {
/* 229 */             createError("Expected numeric value for '" + attr.getName() + "', not '" + value + "'");
/*     */           } 
/*     */         } 
/*     */       } else {
/* 233 */         createError("Expected numeric value for test on '" + attr.getName() + "'");
/*     */       } 
/*     */     } 
/* 236 */     node.setTest((NodeTest)test);
/*     */   }
/*     */   
/*     */   public int readSingleValue(boolean[] isin, NominalAttrType attr, MStringTokenizer tokens) throws IOException {
/* 240 */     if (tokens.hasMoreTokens()) {
/* 241 */       String token = tokens.getToken();
/* 242 */       if (token.equals("?")) {
/* 243 */         checkPartialTreeAllowed("in subset test");
/*     */       } else {
/* 245 */         Integer res = attr.getValueIndex(token);
/* 246 */         if (res == null) {
/* 247 */           createError("Value '" + token + "=' not in domain of '" + attr.getName() + "'");
/*     */         }
/* 249 */         isin[res.intValue()] = true;
/* 250 */         return 1;
/*     */       } 
/*     */     } else {
/* 253 */       createError("Expected value after '=' while reading test on '" + attr.getName() + "'");
/*     */     } 
/* 255 */     return 0;
/*     */   }
/*     */   
/*     */   public int readMultiValue(boolean[] isin, NominalAttrType attr, MStringTokenizer tokens) throws IOException {
/* 259 */     int nb = 0;
/* 260 */     if (tokens.hasMoreTokens()) {
/* 261 */       String token = tokens.getToken();
/* 262 */       if (token.equals("?")) {
/* 263 */         checkPartialTreeAllowed("in subset test");
/*     */       } else {
/* 265 */         if (!token.equals("{")) {
/* 266 */           createError("Expected set after 'in' while reading test on '" + attr.getName() + "'");
/*     */         }
/* 268 */         while (tokens.hasMoreTokens()) {
/* 269 */           String name = tokens.getToken();
/* 270 */           Integer res = attr.getValueIndex(name);
/* 271 */           if (res == null) {
/* 272 */             createError("Value '" + name + "=' not in domain of '" + attr.getName() + "'");
/*     */           }
/* 274 */           isin[res.intValue()] = true; nb++;
/* 275 */           if (tokens.hasMoreTokens()) {
/* 276 */             String sep = tokens.getToken();
/* 277 */             if (sep.equals("}"))
/* 278 */               break;  if (!sep.equals(","))
/* 279 */               createError("Expected '}' or ',' while reading test on '" + attr.getName() + "'"); 
/*     */             continue;
/*     */           } 
/* 282 */           createError("End of set expected while reading test on '" + attr.getName() + "'");
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 287 */       createError("Expected value after 'in' while reading test on '" + attr.getName() + "'");
/*     */     } 
/* 289 */     return nb;
/*     */   }
/*     */   
/*     */   public void readNominalTest(ClusAttrType attr, ClusNode node, MStringTokenizer tokens) throws IOException {
/* 293 */     NominalAttrType ntype = (NominalAttrType)attr;
/* 294 */     if (tokens.hasMoreTokens()) {
/* 295 */       int nb = 0;
/* 296 */       boolean[] isin = new boolean[ntype.getNbValues()];
/* 297 */       String token = tokens.getToken();
/* 298 */       if (token.equals("=")) {
/* 299 */         nb = readSingleValue(isin, ntype, tokens);
/* 300 */       } else if (token.equals("in")) {
/* 301 */         nb = readMultiValue(isin, ntype, tokens);
/*     */       } else {
/* 303 */         createError("Expected '=' or 'in' while reading test on '" + attr.getName() + "'");
/*     */       } 
/* 305 */       SubsetTest test = new SubsetTest(ntype, nb, isin, Double.NEGATIVE_INFINITY);
/* 306 */       node.setTest((NodeTest)test);
/*     */     } else {
/* 308 */       double[] freqs = new double[ntype.getNbValues()];
/* 309 */       Arrays.fill(freqs, Double.NEGATIVE_INFINITY);
/* 310 */       NominalTest test = new NominalTest(ntype, freqs);
/* 311 */       node.setTest((NodeTest)test);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void readTest(ClusAttrType attr, ClusNode node, MStringTokenizer tokens) throws IOException {
/* 316 */     switch (attr.getTypeIndex()) {
/*     */       case 1:
/* 318 */         readNumericTest(attr, node, tokens);
/*     */         return;
/*     */       case 0:
/* 321 */         readNominalTest(attr, node, tokens);
/*     */         return;
/*     */     } 
/* 324 */     createError("Unsupported attribute type '" + attr.getTypeName() + "'");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean readNextChild(ClusNode node, String[] bnames, boolean[] has_ch) throws IOException {
/* 329 */     String str = readLine();
/*     */     
/* 331 */     int cnt = 1;
/* 332 */     for (int i = 0; i < has_ch.length; i++) {
/* 333 */       if (has_ch[i]) cnt++;
/*     */     
/*     */     } 
/* 336 */     if (str == null) {
/* 337 */       createError("End of file encountered while reading branch " + cnt + " for " + node.getTest());
/*     */     }
/*     */     
/* 340 */     int idx = str.indexOf(':');
/* 341 */     if (idx == -1) {
/* 342 */       createError("Expected ':' in start of branch " + cnt + " for " + node.getTest() + ", not '" + str + "'");
/*     */     }
/*     */     
/* 345 */     String name = str.substring(0, idx);
/* 346 */     int idxmm = name.lastIndexOf("--");
/* 347 */     if (idxmm != -1) {
/* 348 */       name = name.substring(idxmm + 2).trim();
/*     */     }
/*     */     
/* 351 */     if (name.equals("?")) {
/* 352 */       checkPartialTreeAllowed("as child node '" + str + "'");
/* 353 */       startRecording(str);
/* 354 */       return true;
/*     */     } 
/*     */     
/* 357 */     int pos = -1;
/* 358 */     for (int j = 0; j < bnames.length; j++) {
/* 359 */       if (name.equals(bnames[j])) {
/* 360 */         pos = j;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 365 */     if (pos == -1) {
/* 366 */       StringBuffer names = new StringBuffer("{");
/* 367 */       for (int k = 0; k < bnames.length; k++) {
/* 368 */         if (k != 0) names.append(","); 
/* 369 */         names.append(bnames[k]);
/*     */       } 
/* 371 */       names.append("}");
/* 372 */       createError("Branch start '" + name + "' does not match " + names + " for " + node.getTest());
/*     */     } 
/*     */     
/* 375 */     if (has_ch[pos]) {
/* 376 */       createError("Branch '" + bnames[pos] + "' occurs twice for " + node.getTest());
/*     */     }
/* 378 */     has_ch[pos] = true;
/*     */     
/* 380 */     if (str.length() <= idx + 1) {
/* 381 */       createError("Expected start of branch after '" + name + "' for " + node.getTest());
/*     */     }
/*     */     
/* 384 */     String rest = str.substring(idx + 1).trim();
/* 385 */     ClusNode child = readTree(rest);
/* 386 */     node.setChild((Node)child, pos);
/* 387 */     return false;
/*     */   }
/*     */   
/*     */   public void readWildCardChild(ClusNode node, boolean[] has_ch) throws IOException {
/* 391 */     String str = readLine();
/*     */     
/* 393 */     int cnt = 1;
/* 394 */     int first_free = -1;
/* 395 */     for (int i = 0; i < has_ch.length; i++) {
/* 396 */       if (has_ch[i]) { cnt++; }
/* 397 */       else if (first_free == -1) { first_free = i; }
/*     */     
/*     */     } 
/* 400 */     if (first_free == -1) {
/* 401 */       createError("Internal error (all branches already read) while reading wildcard branch for " + node.getTest());
/*     */     }
/*     */     
/* 404 */     if (str == null) {
/* 405 */       createError("End of file encountered while reading wildcard branch " + cnt + " for " + node.getTest());
/*     */     }
/*     */     
/* 408 */     int idx = str.indexOf(':');
/* 409 */     if (idx == -1) {
/* 410 */       createError("Expected ':' in start of wildcard branch " + cnt + " for " + node.getTest() + ", not '" + str + "'");
/*     */     }
/*     */     
/* 413 */     if (str.length() <= idx + 1) {
/* 414 */       createError("Expected start of branch after wildcard '?:' for " + node.getTest());
/*     */     }
/*     */     
/* 417 */     String rest = str.substring(idx + 1).trim();
/* 418 */     ClusNode child = readTree(rest);
/* 419 */     node.setChild((Node)child, first_free);
/* 420 */     has_ch[first_free] = true;
/*     */   }
/*     */   
/*     */   public void createError(String err) throws IOException {
/* 424 */     throw new IOException(err + " (" + this.m_FName + ": " + getLineNumber() + ")");
/*     */   }
/*     */   
/*     */   public String readLine() throws IOException {
/* 428 */     if (this.m_PushBack != -1) {
/* 429 */       String result = this.m_Lines.get(this.m_PushBack);
/* 430 */       this.m_PushBack = -1;
/* 431 */       return result;
/*     */     } 
/* 433 */     if (this.m_IsReading) {
/* 434 */       String line = this.m_Reader.readLine();
/* 435 */       if (isRecording() && line != null) {
/* 436 */         this.m_Lines.add(line);
/* 437 */         this.m_CrLine++;
/*     */       } 
/* 439 */       return line;
/*     */     } 
/* 441 */     return this.m_Lines.get(this.m_CrLine++);
/*     */   }
/*     */ 
/*     */   
/*     */   public void markStartRecording() {
/* 446 */     this.m_PushBack = this.m_CrLine - 1;
/* 447 */     this.m_StartPos.add(new Integer(this.m_CrLine - 1));
/*     */   }
/*     */   
/*     */   public void startRecording(String firstline) {
/* 451 */     if (this.m_Lines != null) {
/* 452 */       markStartRecording();
/*     */     } else {
/* 454 */       this.m_CrLine = 1;
/* 455 */       this.m_StartLine = this.m_Reader.getLineNumber() - 1;
/* 456 */       this.m_Lines = new ArrayList();
/* 457 */       this.m_Lines.add(firstline);
/* 458 */       this.m_StartPos = new ArrayList();
/* 459 */       markStartRecording();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void startPlayBack() {
/* 464 */     this.m_IsReading = false;
/* 465 */     reset();
/*     */   }
/*     */   
/*     */   public void stopPlayBack() {
/* 469 */     this.m_StartPos.remove(this.m_StartPos.size() - 1);
/* 470 */     if (this.m_StartPos.size() == 0) {
/* 471 */       this.m_IsReading = true;
/* 472 */       this.m_Lines = null;
/* 473 */       this.m_StartPos = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/* 478 */     Integer pos = this.m_StartPos.get(this.m_StartPos.size() - 1);
/* 479 */     this.m_CrLine = pos.intValue();
/*     */   }
/*     */   
/*     */   public boolean isRecording() {
/* 483 */     return (this.m_Lines != null);
/*     */   }
/*     */   
/*     */   public int getLineNumber() {
/* 487 */     if (this.m_IsReading) return this.m_Reader.getLineNumber(); 
/* 488 */     return this.m_StartLine + this.m_CrLine;
/*     */   }
/*     */   
/*     */   public MStringTokenizer getTokens() {
/* 492 */     return this.m_Tokens;
/*     */   }
/*     */   
/*     */   public static boolean checkAtEnd(LineNumberReader rdr) throws IOException {
/* 496 */     String line = rdr.readLine();
/*     */     
/* 498 */     while (line != null && isSkipLine(line)) {
/* 499 */       line = rdr.readLine();
/*     */     }
/* 501 */     return (line == null);
/*     */   }
/*     */   
/*     */   public static String getFirstNonEmptyLine(LineNumberReader rdr) throws IOException {
/* 505 */     String line = rdr.readLine();
/*     */     
/* 507 */     while (line != null && isSkipLine(line)) {
/* 508 */       line = rdr.readLine();
/*     */     }
/* 510 */     return line;
/*     */   }
/*     */   
/*     */   public static MStringTokenizer createTokenizer() {
/* 514 */     MStringTokenizer tokens = new MStringTokenizer();
/* 515 */     tokens.setCharTokens("=<>{},");
/*     */     
/* 517 */     tokens.setBlockChars("\"", "\"");
/* 518 */     tokens.setSpaceChars(" \t");
/* 519 */     return tokens;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\modelio\ClusTreeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */