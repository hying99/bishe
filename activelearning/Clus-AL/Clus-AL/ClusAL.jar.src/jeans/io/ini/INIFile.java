/*     */ package jeans.io.ini;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.util.Enumeration;
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
/*     */ public class INIFile
/*     */   extends INIFileSection
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int TYPE_DOUBLE_ARRAY = 0;
/*     */   public static final int TYPE_STRING_ARRAY = 1;
/*     */   public static final int TYPE_INT_ARRAY = 2;
/*     */   
/*     */   public INIFile() {
/*  39 */     super("");
/*     */   }
/*     */   
/*     */   public void addNode(INIFileNode entry) {
/*  43 */     this.m_hEntries.put(entry.getName(), entry);
/*  44 */     this.m_hEntryList.addElement(entry);
/*     */   }
/*     */   
/*     */   public void load(String fname) throws FileNotFoundException, IOException {
/*  48 */     load(fname, '#');
/*     */   }
/*     */   
/*     */   public void load(String fname, char comment) throws FileNotFoundException, IOException {
/*  52 */     MStreamTokenizer tokens = new MStreamTokenizer(fname);
/*  53 */     load(tokens, comment);
/*  54 */     tokens.close();
/*     */   }
/*     */   
/*     */   public void load(Reader reader, char comment) throws IOException {
/*  58 */     MStreamTokenizer tokens = new MStreamTokenizer(reader);
/*  59 */     load(tokens, comment);
/*  60 */     tokens.close();
/*     */   }
/*     */   
/*     */   public void save(String fname) throws IOException {
/*  64 */     PrintWriter writer = new PrintWriter(new FileOutputStream(fname));
/*  65 */     save(writer);
/*  66 */     writer.close();
/*     */   }
/*     */   
/*     */   public void load(MStreamTokenizer tokens, char comment) throws IOException {
/*  70 */     tokens.setCharTokens("[]=<>,{}");
/*  71 */     tokens.setCommentChar(comment);
/*     */     
/*     */     while (true) {
/*  74 */       String token = "";
/*  75 */       while (token != null && !token.equals("[")) {
/*  76 */         token = tokens.getToken();
/*     */       }
/*  78 */       if (token == null) {
/*     */         break;
/*     */       }
/*  81 */       int saveline = tokens.getLine();
/*  82 */       String name = tokens.readTillEol();
/*     */ 
/*     */       
/*  85 */       int idx1 = name.indexOf(']');
/*  86 */       if (idx1 == -1) {
/*  87 */         throw new IOException("Error in the settings file. Character ']' expected at line: " + saveline);
/*     */       }
/*     */       
/*  90 */       int idx2 = name.indexOf(',');
/*  91 */       if (idx2 != -1) {
/*  92 */         String groupName = name.substring(0, idx2).trim();
/*  93 */         String sectionName = name.substring(idx2 + 1, idx1).trim();
/*  94 */         doLoad(sectionName, groupName, tokens);
/*     */         
/*     */         continue;
/*     */       } 
/*  98 */       doLoad(name.substring(0, idx1).trim(), tokens);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void save(PrintWriter writer) throws IOException {
/* 105 */     for (Enumeration<INIFileNode> e = getNodes(); e.hasMoreElements(); ) {
/* 106 */       INIFileNode section = e.nextElement();
/* 107 */       if (section.isEnabled()) {
/* 108 */         section.save(writer);
/* 109 */         writer.println();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ini\INIFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */