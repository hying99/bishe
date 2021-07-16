/*     */ package jeans.util;
/*     */ 
/*     */ import java.util.Enumeration;
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
/*     */ public class MSStringTokenizer
/*     */   implements Enumeration
/*     */ {
/*     */   public static final int NUMBER = 0;
/*     */   public static final int ALPHA = 1;
/*     */   public static final int SPECIAL = 2;
/*     */   public static final int SPACE = 3;
/*     */   public static final int CONTROL = 4;
/*     */   public static final int FLOAT = 5;
/*     */   public static final int GROUP = 6;
/*     */   private String strg;
/*     */   private int pos;
/*     */   private int prevpos;
/*     */   private int len;
/*     */   private int typegroup;
/*     */   
/*     */   public MSStringTokenizer(String strg) {
/*  42 */     this.strg = strg;
/*  43 */     this.len = strg.length();
/*  44 */     skipControlsAndSpaces();
/*     */   }
/*     */   
/*     */   public int getGroup(char ch) {
/*  48 */     if (Character.isDigit(ch)) return 0; 
/*  49 */     if (Character.isLetter(ch)) return 1; 
/*  50 */     if (ch == '_') return 1; 
/*  51 */     if (ch == '(' || ch == ')') return 6; 
/*  52 */     if (Character.isSpaceChar(ch)) return 3; 
/*  53 */     if (Character.isISOControl(ch)) return 4; 
/*  54 */     return 2;
/*     */   }
/*     */   
/*     */   public String nextToken() {
/*  58 */     StringBuffer buffer = new StringBuffer();
/*  59 */     this.prevpos = this.pos;
/*  60 */     char ch = this.strg.charAt(this.pos);
/*  61 */     int group = getGroup(ch);
/*  62 */     int prevgroup = this.typegroup = group;
/*  63 */     while (group == prevgroup && this.pos < this.len) {
/*  64 */       buffer.append(ch); this.pos++;
/*  65 */       if (this.pos < this.len) {
/*  66 */         ch = this.strg.charAt(this.pos);
/*  67 */         prevgroup = group;
/*  68 */         group = getGroup(ch);
/*     */       } 
/*     */     } 
/*  71 */     skipControlsAndSpaces();
/*  72 */     return buffer.toString();
/*     */   }
/*     */   
/*     */   public String nextGroupToken() throws GeneralException {
/*  76 */     StringBuffer buffer = new StringBuffer();
/*  77 */     this.prevpos = this.pos;
/*  78 */     char ch = this.strg.charAt(this.pos);
/*  79 */     int group = getGroup(ch);
/*  80 */     int level = 0;
/*  81 */     int prevgroup = this.typegroup = group;
/*  82 */     while ((level > 0 || group == prevgroup) && this.pos < this.len) {
/*  83 */       if (ch == ')') level--; 
/*  84 */       if (level > 0 || group != 6) buffer.append(ch); 
/*  85 */       if (ch == '(') level++; 
/*  86 */       if (++this.pos < this.len) {
/*  87 */         ch = this.strg.charAt(this.pos);
/*  88 */         prevgroup = group;
/*  89 */         group = getGroup(ch);
/*     */       } 
/*     */     } 
/*  92 */     if (level > 0)
/*  93 */       throw new GeneralException("'(,)' don't match."); 
/*  94 */     skipControlsAndSpaces();
/*  95 */     return buffer.toString();
/*     */   }
/*     */   
/*     */   public String nextNumberToken() {
/*  99 */     StringBuffer buffer = new StringBuffer();
/* 100 */     boolean done = false;
/* 101 */     char prevChar = Character.MIN_VALUE;
/* 102 */     this.prevpos = this.pos;
/* 103 */     this.typegroup = 0;
/*     */     while (true) {
/* 105 */       char ch = Character.toUpperCase(this.strg.charAt(this.pos));
/* 106 */       done = true;
/* 107 */       if (Character.isDigit(ch)) { done = false; }
/* 108 */       else if (ch == 'E' || ch == '.')
/* 109 */       { done = false;
/* 110 */         this.typegroup = 5; }
/* 111 */       else if ((ch == '+' || ch == '-') && (prevChar == '\000' || prevChar == 'E')) { done = false; }
/* 112 */        if (!done) {
/* 113 */         buffer.append(ch);
/* 114 */         this.pos++;
/* 115 */         if (this.pos >= this.len) done = true; 
/* 116 */         if (!done) {
/* 117 */           prevChar = ch;
/* 118 */           ch = Character.toUpperCase(this.strg.charAt(this.pos));
/*     */         } 
/*     */       } 
/* 121 */       if (done) {
/* 122 */         skipControlsAndSpaces();
/* 123 */         return buffer.toString();
/*     */       } 
/*     */     } 
/*     */   } public int getPosition() {
/* 127 */     return this.prevpos;
/*     */   }
/*     */   
/*     */   public boolean hasMoreTokens() {
/* 131 */     return hasMoreElements();
/*     */   }
/*     */   
/*     */   public int getGroup() {
/* 135 */     return this.typegroup;
/*     */   }
/*     */   
/*     */   public void pushBack() {
/* 139 */     this.pos = this.prevpos;
/*     */   }
/*     */   
/*     */   public boolean hasMoreElements() {
/* 143 */     return (this.pos < this.len);
/*     */   }
/*     */   
/*     */   public Object nextElement() {
/* 147 */     return nextToken();
/*     */   }
/*     */   
/*     */   private void skipControlsAndSpaces() {
/* 151 */     if (this.pos < this.len) {
/* 152 */       char ch = this.strg.charAt(this.pos);
/* 153 */       int group = getGroup(ch);
/* 154 */       while ((group == 3 || group == 4) && this.pos < this.len) {
/* 155 */         this.pos++;
/* 156 */         ch = this.strg.charAt(this.pos);
/* 157 */         group = getGroup(ch);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\MSStringTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */