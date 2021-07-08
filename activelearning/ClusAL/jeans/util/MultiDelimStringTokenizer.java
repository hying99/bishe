/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
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
/*     */ public class MultiDelimStringTokenizer
/*     */ {
/*  29 */   public static final String[] $example_delims = new String[] { "&nbsp;", "&", ";" };
/*     */   
/*     */   protected String[] $delims;
/*     */   
/*     */   protected String $line;
/*     */   protected int[] $delimpos;
/*     */   protected int[] $delimlen;
/*     */   protected int $pos;
/*     */   protected int $len;
/*  38 */   protected int $lastdelim = -1;
/*     */   
/*     */   MultiDelimStringTokenizer(String[] delims) {
/*  41 */     this.$delims = delims;
/*  42 */     this.$delimpos = new int[this.$delims.length];
/*  43 */     this.$delimlen = new int[this.$delims.length];
/*  44 */     for (int i = 0; i < this.$delims.length; i++)
/*  45 */       this.$delimlen[i] = this.$delims[i].length(); 
/*     */   }
/*     */   
/*     */   public void setLine(String line) {
/*  49 */     this.$pos = 0;
/*  50 */     this.$line = line;
/*  51 */     this.$len = line.length();
/*  52 */     int delim = -1;
/*  53 */     while ((delim = checkDelim()) != -1) {
/*  54 */       this.$lastdelim = delim;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean hasMoreTokens() {
/*  59 */     return (this.$pos < this.$len);
/*     */   }
/*     */   
/*     */   public String nextToken() {
/*  63 */     int delim = -1;
/*  64 */     boolean notdelim = true;
/*  65 */     StringBuffer token = new StringBuffer();
/*  66 */     while (this.$pos < this.$len && notdelim) {
/*  67 */       token.append(this.$line.charAt(this.$pos++));
/*  68 */       while ((delim = checkDelim()) != -1) {
/*  69 */         notdelim = false;
/*  70 */         this.$lastdelim = delim;
/*     */       } 
/*     */     } 
/*  73 */     return token.toString();
/*     */   }
/*     */   
/*     */   public int lastDelim() {
/*  77 */     return this.$lastdelim;
/*     */   }
/*     */   
/*     */   public int checkDelim() {
/*  81 */     int ch = 0;
/*  82 */     int mypos = this.$pos;
/*  83 */     int nbdelims = this.$delims.length;
/*  84 */     int is_delim = -1; int i;
/*  85 */     for (i = 0; i < this.$delims.length; ) { this.$delimpos[i] = 0; i++; }
/*  86 */      while (nbdelims > 0 && mypos <= this.$len) {
/*  87 */       if (mypos < this.$len) {
/*  88 */         ch = this.$line.charAt(mypos++);
/*     */       } else {
/*  90 */         mypos++;
/*  91 */         is_delim = 0;
/*  92 */         this.$pos = mypos;
/*     */       } 
/*  94 */       for (i = 0; i < this.$delims.length; i++) {
/*  95 */         int delps = this.$delimpos[i];
/*  96 */         if (delps != -1) {
/*  97 */           int delch = this.$delims[i].charAt(delps);
/*  98 */           if (delch == ch) {
/*  99 */             delps = this.$delimpos[i] = this.$delimpos[i] + 1;
/* 100 */             if (delps >= this.$delimlen[i]) {
/* 101 */               is_delim = i + 1;
/* 102 */               this.$pos = mypos;
/* 103 */               this.$delimpos[i] = -1;
/* 104 */               nbdelims--;
/*     */             } 
/*     */           } else {
/* 107 */             this.$delimpos[i] = -1;
/* 108 */             nbdelims--;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 113 */     return is_delim;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 118 */       String line = null;
/* 119 */       MultiDelimStringTokenizer token = new MultiDelimStringTokenizer($example_delims);
/* 120 */       LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(args[0])));
/* 121 */       while ((line = reader.readLine()) != null) {
/* 122 */         token.setLine(line);
/* 123 */         while (token.hasMoreTokens()) {
/* 124 */           System.out.println(token.nextToken());
/*     */         }
/*     */       } 
/* 127 */       reader.close();
/* 128 */     } catch (NullPointerException e) {
/* 129 */       System.out.println("You must supply a file name!");
/* 130 */     } catch (IOException e) {
/* 131 */       System.out.println("IO Error: " + e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\MultiDelimStringTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */