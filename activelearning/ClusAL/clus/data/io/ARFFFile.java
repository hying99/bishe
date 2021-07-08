/*     */ package clus.data.io;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.BitwiseNominalAttrType;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.data.type.StringAttrType;
/*     */ import clus.data.type.TimeSeriesAttrType;
/*     */ import clus.ext.hierarchical.ClassesAttrType;
/*     */ import clus.ext.hierarchical.ClassesAttrTypeSingleLabel;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import jeans.util.MStreamTokenizer;
/*     */ import jeans.util.StringUtils;
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
/*     */ public class ARFFFile
/*     */ {
/*     */   protected static final String TAG_ERROR = " tag not found in ARFF file, found instead: '";
/*  41 */   protected static final String[] TAG_NAME = new String[] { "@RELATION", "@ATTRIBUTE", "@DATA" };
/*     */   
/*     */   protected ClusReader m_Reader;
/*  44 */   protected int m_DataLine = -1;
/*     */   
/*     */   public ARFFFile(ClusReader reader) {
/*  47 */     this.m_Reader = reader;
/*     */   }
/*     */ 
/*     */   
/*     */   public ARFFFile() {}
/*     */   
/*     */   public ClusSchema read(Settings sett) throws IOException, ClusException {
/*  54 */     int expected = 0;
/*  55 */     ClusSchema schema = new ClusSchema(this.m_Reader.getName());
/*  56 */     schema.setSettings(sett);
/*  57 */     MStreamTokenizer tokens = this.m_Reader.getTokens();
/*  58 */     String token = tokens.getToken().toUpperCase();
/*  59 */     HashMap<Object, Object> attrMap = new HashMap<>();
/*  60 */     while (expected < 3) {
/*  61 */       if (token == null) {
/*  62 */         throw new IOException("End of ARFF file before " + TAG_NAME[expected] + " tag");
/*     */       }
/*  64 */       if (token.equals(TAG_NAME[0])) {
/*  65 */         schema.setRelationName(tokens.readTillEol().trim());
/*     */         
/*  67 */         expected = 1;
/*  68 */       } else if (token.equals(TAG_NAME[1])) {
/*  69 */         if (expected == 0) {
/*  70 */           throw new IOException(TAG_NAME[expected] + " tag not found in ARFF file, found instead: '" + token + "'");
/*     */         }
/*  72 */         String aname = tokens.getDelimToken('"', '"');
/*  73 */         String atype = tokens.readTillEol();
/*  74 */         int idx = atype.indexOf('%');
/*  75 */         if (idx != -1) {
/*  76 */           atype = atype.substring(0, idx - 1);
/*     */         }
/*  78 */         atype = atype.trim();
/*  79 */         addAttribute(schema, aname, atype, attrMap);
/*  80 */         expected = 2;
/*  81 */       } else if (token.equals(TAG_NAME[2])) {
/*  82 */         if (expected != 2) {
/*  83 */           throw new IOException(TAG_NAME[expected] + " tag not found in ARFF file, found instead: '" + token + "'");
/*     */         }
/*  85 */         this.m_DataLine = tokens.getLine();
/*  86 */         expected = 3;
/*     */       } else {
/*  88 */         throw new IOException(TAG_NAME[expected] + " tag not found in ARFF file, found instead: '" + token + "'");
/*     */       } 
/*  90 */       if (expected < 3) {
/*  91 */         token = tokens.getToken().toUpperCase();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  96 */     return schema;
/*     */   }
/*     */   
/*     */   public void skipTillData() throws IOException {
/* 100 */     boolean error = false;
/* 101 */     MStreamTokenizer tokens = this.m_Reader.getTokens();
/* 102 */     String token = tokens.getToken().toUpperCase();
/* 103 */     while (token != null) {
/* 104 */       if (this.m_DataLine != -1 && tokens.getLine() > this.m_DataLine) {
/* 105 */         error = true;
/*     */         break;
/*     */       } 
/* 108 */       if (token.equals(TAG_NAME[2])) {
/*     */         break;
/*     */       }
/* 111 */       token = tokens.getToken().toUpperCase();
/*     */     } 
/* 113 */     if (token == null || error) {
/* 114 */       throw new IOException("Unexpected ARFF reader error looking for @data tag");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void addAttribute(ClusSchema schema, String aname, String atype, HashMap<String, int[]> attrMap) throws IOException, ClusException {
/* 119 */     Settings sett = schema.getSettings();
/* 120 */     String uptype = atype.toUpperCase();
/* 121 */     while (attrMap.containsKey(aname)) {
/* 122 */       int[] cnt = (int[])attrMap.get(aname);
/* 123 */       int idx = cnt[0] = cnt[0] + 1;
/* 124 */       aname = aname + "-" + idx;
/*     */     } 
/* 126 */     attrMap.put(aname, new int[1]);
/* 127 */     if (uptype.equals("NUMERIC") || uptype.equals("REAL") || uptype.equals("INTEGER")) {
/* 128 */       schema.addAttrType((ClusAttrType)new NumericAttrType(aname));
/* 129 */     } else if (uptype.equals("CLASSES")) {
/* 130 */       ClassesAttrType type = new ClassesAttrType(aname);
/* 131 */       schema.addAttrType((ClusAttrType)type);
/* 132 */       type.initSettings(schema.getSettings());
/* 133 */     } else if (uptype.startsWith("HIERARCHICAL")) {
/* 134 */       if (schema.getSettings().getHierSingleLabel()) {
/* 135 */         ClassesAttrTypeSingleLabel type = new ClassesAttrTypeSingleLabel(aname, atype);
/* 136 */         schema.addAttrType((ClusAttrType)type);
/* 137 */         type.initSettings(schema.getSettings());
/*     */       } else {
/* 139 */         ClassesAttrType type = new ClassesAttrType(aname, atype);
/* 140 */         schema.addAttrType((ClusAttrType)type);
/* 141 */         type.initSettings(schema.getSettings());
/*     */       }
/*     */     
/* 144 */     } else if (uptype.equals("STRING")) {
/* 145 */       schema.addAttrType((ClusAttrType)new StringAttrType(aname));
/* 146 */     } else if (uptype.equals("KEY")) {
/* 147 */       StringAttrType key = new StringAttrType(aname);
/* 148 */       schema.addAttrType((ClusAttrType)key);
/* 149 */       key.setStatus(4);
/* 150 */     } else if (uptype.equals("TIMESERIES")) {
/* 151 */       TimeSeriesAttrType tsat = new TimeSeriesAttrType(aname);
/* 152 */       schema.addAttrType((ClusAttrType)tsat);
/*     */     } else {
/* 154 */       if (uptype.equals("BINARY")) {
/* 155 */         atype = "{1,0}";
/*     */       }
/* 157 */       int tlen = atype.length();
/* 158 */       if (tlen > 2 && atype.charAt(0) == '{' && atype.charAt(tlen - 1) == '}') {
/* 159 */         if (sett.getReduceMemoryNominalAttrs() == true) {
/* 160 */           schema.addAttrType((ClusAttrType)new BitwiseNominalAttrType(aname, atype));
/*     */         } else {
/* 162 */           schema.addAttrType((ClusAttrType)new NominalAttrType(aname, atype));
/*     */         } 
/*     */       } else {
/* 165 */         throw new IOException("Attribute '" + aname + "' has unknown type '" + atype + "'");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void writeArffHeader(PrintWriter wrt, ClusSchema schema) throws IOException, ClusException {
/* 171 */     wrt.println("@RELATION '" + StringUtils.removeSingleQuote(schema.getRelationName()) + "'");
/* 172 */     wrt.println();
/* 173 */     for (int i = 0; i < schema.getNbAttributes(); i++) {
/* 174 */       ClusAttrType type = schema.getAttrType(i);
/* 175 */       if (!type.isDisabled()) {
/* 176 */         wrt.print("@ATTRIBUTE ");
/* 177 */         wrt.print(StringUtils.printStr(type.getName(), 65));
/* 178 */         if (type.isKey()) {
/* 179 */           wrt.print("key");
/*     */         } else {
/* 181 */           type.writeARFFType(wrt);
/*     */         } 
/* 183 */         wrt.println();
/*     */       } 
/*     */     } 
/* 186 */     if (!schema.getSettings().shouldWritePredictionsFromEnsemble()) {
/* 187 */       wrt.println();
/*     */     }
/*     */   }
/*     */   
/*     */   public static RowData readArff(String fname) throws IOException, ClusException {
/* 192 */     ClusReader reader = new ClusReader(fname, null);
/* 193 */     ARFFFile arff = new ARFFFile(reader);
/* 194 */     ClusSchema schema = arff.read(null);
/* 195 */     schema.initialize();
/* 196 */     ClusView view = schema.createNormalView();
/* 197 */     RowData data = view.readData(reader, schema);
/* 198 */     reader.close();
/* 199 */     return data;
/*     */   }
/*     */   
/*     */   public static void writeArff(String fname, RowData data) throws IOException, ClusException {
/* 203 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 204 */     ClusSchema schema = data.getSchema();
/* 205 */     writeArffHeader(wrt, schema);
/* 206 */     wrt.println("@DATA");
/* 207 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 208 */       DataTuple tuple = data.getTuple(j);
/* 209 */       tuple.writeTuple(wrt);
/*     */     } 
/* 211 */     wrt.close();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeActiveArff(PrintWriter wrt, RowData data) throws IOException, ClusException {
/* 217 */     wrt.println("@DATA");
/* 218 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 219 */       DataTuple tuple = data.getTuple(j);
/* 220 */       tuple.writeTuple(wrt);
/*     */     } 
/*     */     
/* 223 */     wrt.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeBudgetInformation(PrintWriter wrt, int iteration, double budgetAvailable, double budgetIteration, double totalSpentBudget) {
/* 228 */     wrt.println("Iteration: " + iteration);
/* 229 */     wrt.println("Budget Available per Iteration: " + budgetAvailable);
/* 230 */     wrt.println("Budget Spent on this iteration: " + budgetIteration);
/* 231 */     wrt.println("Budget Spent up to this iteration: " + totalSpentBudget);
/* 232 */     wrt.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeCN2Data(String fname, RowData data) throws IOException, ClusException {
/* 237 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 238 */     ClusSchema schema = data.getSchema();
/* 239 */     wrt.println("**EXAMPLE FILE**\n");
/* 240 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 241 */       DataTuple tuple = data.getTuple(j);
/* 242 */       int aidx = 0;
/* 243 */       for (int i = 0; i < schema.getNbAttributes(); i++) {
/* 244 */         ClusAttrType type = schema.getAttrType(i);
/* 245 */         if (!type.isDisabled()) {
/* 246 */           if (aidx != 0) {
/* 247 */             wrt.print("\t");
/*     */           }
/* 249 */           if (type instanceof NominalAttrType) {
/* 250 */             String label = type.getString(tuple);
/* 251 */             label = label.replace("^2", "two");
/* 252 */             label = label.replace("<", "le");
/* 253 */             label = label.replace(">", "gt");
/* 254 */             label = label.replace("-", "_");
/* 255 */             label = label.replace("&", "");
/* 256 */             if (!label.equals("?")) {
/* 257 */               wrt.print("_" + label);
/*     */             } else {
/* 259 */               wrt.print(label);
/*     */             } 
/*     */           } else {
/* 262 */             wrt.print(type.getString(tuple));
/*     */           } 
/* 264 */           aidx++;
/*     */         } 
/*     */       } 
/* 267 */       wrt.println(";");
/*     */     } 
/* 269 */     wrt.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeFRSData(String fname, RowData data, boolean train) throws IOException, ClusException {
/* 274 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 275 */     ClusSchema schema = data.getSchema();
/*     */     
/* 277 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 278 */       DataTuple tuple = data.getTuple(j);
/* 279 */       int aidx = 0;
/* 280 */       if (train) {
/* 281 */         wrt.print("lrn(lr(");
/*     */       } else {
/* 283 */         wrt.print("tst(lr(");
/*     */       } 
/* 285 */       for (int i = schema.getNbAttributes() - 1; i >= 0; i--) {
/* 286 */         ClusAttrType type = schema.getAttrType(i);
/* 287 */         if (!type.isDisabled()) {
/* 288 */           if (aidx != 0) {
/* 289 */             wrt.print(",");
/*     */           }
/* 291 */           if (type instanceof NominalAttrType) {
/* 292 */             String label = type.getString(tuple);
/* 293 */             label = label.replace("^2", "two");
/* 294 */             label = label.replace("<", "le");
/* 295 */             label = label.replace(">", "gt");
/* 296 */             label = label.replace("-", "");
/* 297 */             label = label.replace("_", "");
/* 298 */             label = label.replace("&", "");
/* 299 */             label = label.replace(".", "");
/* 300 */             wrt.print("a" + label);
/*     */           } else {
/* 302 */             wrt.print(type.getString(tuple));
/*     */           } 
/* 304 */           aidx++;
/*     */         } 
/*     */       } 
/* 307 */       wrt.println(")).");
/*     */     } 
/* 309 */     wrt.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeFRSHead(String fname, RowData data, boolean train) throws IOException, ClusException {
/* 314 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 315 */     ClusSchema schema = data.getSchema();
/*     */     
/* 317 */     wrt.println(":-dynamictype/2,type/3,variable/2,target/1,lrn/1,tst/1.\n");
/*     */     
/* 319 */     wrt.println("type(real,continuous,0.1)."); int i;
/* 320 */     for (i = schema.getNbAttributes() - 1; i >= 0; i--) {
/* 321 */       ClusAttrType type = schema.getAttrType(i);
/* 322 */       if (!type.isDisabled() && type instanceof NominalAttrType) {
/* 323 */         String[] labels = ((NominalAttrType)type).getValues();
/* 324 */         wrt.print("type(type_");
/* 325 */         wrt.print(type.getName());
/* 326 */         wrt.print(",discrete,[");
/* 327 */         for (int j = 0; j < labels.length; j++) {
/* 328 */           String label = new String(labels[j]);
/* 329 */           label = label.replace("^2", "two");
/* 330 */           label = label.replace("<", "le");
/* 331 */           label = label.replace(">", "gt");
/* 332 */           label = label.replace("-", "");
/* 333 */           label = label.replace("_", "");
/* 334 */           label = label.replace("&", "");
/* 335 */           label = label.replace(".", "");
/* 336 */           wrt.print("a" + label);
/* 337 */           if (j < labels.length - 1) {
/* 338 */             wrt.print(",");
/*     */           }
/*     */         } 
/* 341 */         wrt.print("]).\n");
/*     */       } 
/*     */     } 
/* 344 */     wrt.println("");
/*     */     
/* 346 */     for (i = schema.getNbAttributes() - 1; i >= 0; i--) {
/* 347 */       ClusAttrType type = schema.getAttrType(i);
/* 348 */       if (!type.isDisabled()) {
/* 349 */         if (type instanceof NominalAttrType) {
/* 350 */           wrt.print("variable('A");
/* 351 */           wrt.print(type.getName());
/* 352 */           wrt.print("',type_");
/* 353 */           wrt.print(type.getName());
/* 354 */           wrt.print(").\n");
/*     */         } else {
/* 356 */           wrt.print("variable('A");
/* 357 */           wrt.print(type.getName());
/* 358 */           wrt.print("',real).\n");
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 363 */     wrt.print("\ntarget(lr(");
/* 364 */     for (i = schema.getNbAttributes() - 1; i >= 0; i--) {
/* 365 */       ClusAttrType type = schema.getAttrType(i);
/* 366 */       if (!type.isDisabled()) {
/* 367 */         wrt.print("'A");
/* 368 */         wrt.print(type.getName());
/* 369 */         wrt.print("'");
/*     */       } 
/* 371 */       if (i > 0) {
/* 372 */         wrt.print(",");
/*     */       }
/*     */     } 
/* 375 */     wrt.print(")).\n\n");
/* 376 */     wrt.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeRData(String fname, RowData data) throws IOException, ClusException {
/* 381 */     double NUMBER_INF = 9.0E36D;
/* 382 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 383 */     ClusSchema schema = data.getSchema();
/* 384 */     int nbAttr = schema.getNbAttributes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 392 */     for (int jRow = 0; jRow < data.getNbRows(); jRow++) {
/* 393 */       DataTuple tuple = data.getTuple(jRow);
/* 394 */       for (int iAttr = 0; iAttr < nbAttr; iAttr++) {
/* 395 */         ClusAttrType attrType = schema.getAttrType(iAttr);
/*     */         
/* 397 */         if (attrType instanceof NumericAttrType) {
/* 398 */           if (attrType.isMissing(tuple)) {
/* 399 */             if (!Double.isNaN(attrType.getNumeric(tuple)) && !Double.isInfinite(attrType.getNumeric(tuple))) {
/* 400 */               System.err.println("ERROR, isMissing works wrong");
/* 401 */               System.exit(0);
/*     */             } 
/* 403 */             wrt.print(NUMBER_INF);
/*     */           } else {
/*     */             
/* 406 */             wrt.print(attrType.getNumeric(tuple));
/*     */           
/*     */           }
/*     */         
/*     */         }
/* 411 */         else if (attrType.isMissing(tuple)) {
/* 412 */           wrt.print(NUMBER_INF);
/*     */         } else {
/* 414 */           wrt.print(attrType.getNominal(tuple));
/*     */         } 
/*     */         
/* 417 */         wrt.print("\t");
/*     */       } 
/* 419 */       wrt.print("\n");
/*     */     } 
/* 421 */     wrt.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeRDataNominalLabels(String fname, RowData data) throws IOException, ClusException {
/* 426 */     ClusSchema schema = data.getSchema();
/* 427 */     int nbAttr = schema.getNbAttributes();
/*     */ 
/*     */ 
/*     */     
/* 431 */     ArrayList<Integer> nominalAttrs = new ArrayList<>();
/*     */ 
/*     */     
/* 434 */     for (int iColumn = 0; iColumn < nbAttr; iColumn++) {
/* 435 */       ClusAttrType attrType = schema.getAttrType(iColumn);
/* 436 */       if (attrType instanceof NominalAttrType) {
/* 437 */         nominalAttrs.add(Integer.valueOf(iColumn + 1));
/*     */       }
/*     */     } 
/*     */     
/* 441 */     if (nominalAttrs.size() > 0) {
/*     */       
/* 443 */       PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 444 */       for (int i = 0; i < nominalAttrs.size(); i++) {
/* 445 */         wrt.print((new StringBuilder()).append(nominalAttrs.get(i)).append("\t").toString());
/*     */       }
/* 447 */       wrt.print("\n");
/* 448 */       wrt.close();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\io\ARFFFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */