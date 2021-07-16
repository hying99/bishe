/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleHighlighter
/*     */   extends SyntaxHighlighter
/*     */ {
/*     */   protected String m_ops;
/*     */   protected String m_token;
/*     */   protected MStringTokenizer m_tokens;
/*  34 */   protected Vector m_blockColors = new Vector();
/*  35 */   protected Vector m_funcColors = new Vector();
/*     */   protected Color m_color;
/*     */   protected Color m_newColor;
/*  38 */   protected Color m_ops_color = Color.blue;
/*  39 */   protected Color m_default_color = Color.black;
/*     */   
/*     */   public void setOperators(String ops, Color opcolor) {
/*  42 */     this.m_ops = ops;
/*  43 */     this.m_ops_color = opcolor;
/*     */   }
/*     */   
/*     */   public void addFunctions(String[] fcts, Color color) {
/*  47 */     FuncColor fc = new FuncColor(fcts, color);
/*  48 */     this.m_funcColors.addElement(fc);
/*     */   }
/*     */   
/*     */   public void initialize() {
/*  52 */     this.m_tokens = new MStringTokenizer();
/*  53 */     this.m_tokens.setCharTokens(this.m_ops + " \t");
/*  54 */     String stBlock = "";
/*  55 */     String edBlock = "";
/*  56 */     for (int ctr = 0; ctr < this.m_blockColors.size(); ctr++) {
/*  57 */       BlockColor bc = this.m_blockColors.elementAt(ctr);
/*  58 */       stBlock = stBlock + String.valueOf((char)bc.ch1);
/*  59 */       edBlock = edBlock + String.valueOf((char)bc.ch2);
/*     */     } 
/*  61 */     this.m_tokens.setBlockChars(stBlock, edBlock);
/*     */   }
/*     */   
/*     */   public void addBlockColor(int ch1, int ch2, Color color) {
/*  65 */     BlockColor bc = new BlockColor(ch1, ch2, color);
/*  66 */     this.m_blockColors.addElement(bc);
/*     */   }
/*     */   
/*     */   public Color getColor(String token) {
/*  70 */     if (token.length() > 0) {
/*  71 */       int ch = token.charAt(0);
/*  72 */       if (this.m_ops.indexOf(ch) != -1) return this.m_ops_color;  int ctr;
/*  73 */       for (ctr = 0; ctr < this.m_blockColors.size(); ctr++) {
/*  74 */         BlockColor bc = this.m_blockColors.elementAt(ctr);
/*  75 */         if (ch == bc.ch1) return bc.color; 
/*     */       } 
/*  77 */       for (ctr = 0; ctr < this.m_funcColors.size(); ctr++) {
/*  78 */         FuncColor fc = this.m_funcColors.elementAt(ctr);
/*  79 */         for (int n = 0; n < fc.func.length; n++) {
/*  80 */           if (fc.func[n].equals(token))
/*  81 */             return fc.color; 
/*     */         } 
/*     */       } 
/*     */     } 
/*  85 */     return this.m_default_color;
/*     */   }
/*     */   
/*     */   public Color getColor() {
/*  89 */     return this.m_color;
/*     */   }
/*     */   
/*     */   public void parseString(String strg) {
/*  93 */     this.m_tokens.setString(strg);
/*  94 */     this.m_newColor = this.m_default_color;
/*  95 */     this.m_token = "";
/*     */   }
/*     */   
/*     */   public String getColorToken() {
/*  99 */     this.m_color = this.m_newColor;
/*     */     while (true) {
/* 101 */       String token = this.m_tokens.getToken();
/* 102 */       if (token == null) {
/* 103 */         if (this.m_token.length() > 0) {
/* 104 */           String output = this.m_token;
/* 105 */           this.m_token = "";
/* 106 */           return output;
/*     */         } 
/* 108 */         return null;
/*     */       } 
/*     */       
/* 111 */       if (token.equals(" ") || token.equals("\t")) {
/* 112 */         this.m_token += token; continue;
/*     */       } 
/* 114 */       Color newColor = getColor(token);
/* 115 */       if (this.m_color.equals(newColor)) {
/* 116 */         this.m_token += token; continue;
/*     */       } 
/* 118 */       this.m_newColor = newColor;
/* 119 */       if (this.m_token.length() > 0) {
/* 120 */         String output = this.m_token;
/* 121 */         this.m_token = token;
/* 122 */         return output;
/*     */       } 
/* 124 */       this.m_token = token;
/* 125 */       this.m_color = newColor;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\SimpleHighlighter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */