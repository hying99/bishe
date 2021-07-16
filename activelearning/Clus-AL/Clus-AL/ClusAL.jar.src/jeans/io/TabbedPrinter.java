/*     */ package jeans.io;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import jeans.util.IntegerStack;
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
/*     */ public class TabbedPrinter
/*     */ {
/*     */   protected PrintWriter m_writer;
/*  33 */   protected IntegerStack m_tabs = new IntegerStack();
/*     */   protected int m_linePos;
/*     */   
/*     */   protected TabbedPrinter(PrintWriter writer) {
/*  37 */     this.m_writer = writer;
/*  38 */     this.m_lineSize = 100;
/*  39 */     this.m_linePos = 0;
/*     */   }
/*     */   protected int m_lineSize;
/*     */   public static TabbedPrinter getSystemOutPrinter() {
/*  43 */     return new TabbedPrinter(new PrintWriter(System.out));
/*     */   }
/*     */   
/*     */   public void clearTabs() {
/*  47 */     this.m_tabs.clear();
/*     */   }
/*     */   
/*     */   public void addTab(int pos) {
/*  51 */     this.m_tabs.push(pos);
/*     */   }
/*     */   
/*     */   public void addTabs(int[] poss) {
/*  55 */     for (int i = 0; i < poss.length; i++)
/*  56 */       addTab(poss[i]); 
/*     */   }
/*     */   
/*     */   public void setLineSize(int lineSize) {
/*  60 */     this.m_lineSize = lineSize;
/*     */   }
/*     */   
/*     */   public int getNextTab(int pos) {
/*  64 */     int idx = 0;
/*  65 */     while (idx < this.m_tabs.getSize()) {
/*  66 */       int tabpos = this.m_tabs.getElementAt(idx++);
/*  67 */       if (pos <= tabpos) return tabpos; 
/*     */     } 
/*  69 */     return -1;
/*     */   }
/*     */   
/*     */   public void print(String strg) {
/*  73 */     while (strg != null)
/*  74 */       strg = printSub(strg); 
/*     */   }
/*     */   
/*     */   public void println(String strg) {
/*  78 */     print(strg + '\n');
/*     */   }
/*     */   
/*     */   public void newLine() {
/*  82 */     this.m_writer.println();
/*  83 */     this.m_writer.flush();
/*  84 */     this.m_linePos = 0;
/*     */   }
/*     */   
/*     */   public void forceNewLine() {
/*  88 */     if (this.m_linePos != 0) newLine(); 
/*     */   }
/*     */   
/*     */   public void printTab(String strg) {
/*  92 */     int tab = getNextTab(this.m_linePos + 1);
/*  93 */     int len = strg.length();
/*     */     
/*  95 */     int max = (tab != -1) ? Math.max(tab - this.m_linePos - 1, 0) : Math.max(this.m_lineSize - this.m_linePos - 1, 0);
/*  96 */     if (max < len) { print(strg.substring(0, max) + "\t"); }
/*  97 */     else { print(strg + "\t"); }
/*     */   
/*     */   }
/*     */   private String printSub(String strg) {
/* 101 */     int idx = strg.indexOf('\t');
/* 102 */     int len = strg.length();
/* 103 */     if (idx != -1) {
/* 104 */       this.m_writer.print(strg.substring(0, idx));
/* 105 */       this.m_linePos += idx;
/* 106 */       int nxtSpc = getNextTab(this.m_linePos);
/* 107 */       if (nxtSpc != -1) {
/* 108 */         int nbSpc = nxtSpc - this.m_linePos;
/* 109 */         this.m_linePos = nxtSpc;
/* 110 */         this.m_writer.print(StringUtils.makeString(' ', nbSpc));
/*     */       } else {
/* 112 */         newLine();
/*     */       } 
/* 114 */       if (idx + 1 >= len) return null; 
/* 115 */       return strg.substring(idx + 1);
/*     */     } 
/* 117 */     idx = strg.indexOf('\n');
/* 118 */     if (idx != -1) {
/* 119 */       this.m_writer.println(strg.substring(0, idx));
/* 120 */       this.m_writer.flush();
/* 121 */       this.m_linePos = 0;
/* 122 */       if (idx + 1 >= len) return null; 
/* 123 */       return strg.substring(idx + 1);
/*     */     } 
/* 125 */     this.m_writer.print(strg);
/* 126 */     this.m_linePos += len;
/* 127 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 133 */     TabbedPrinter result = getSystemOutPrinter();
/* 134 */     int[] tabs = { 10, 20, 30 };
/* 135 */     result.addTabs(tabs);
/* 136 */     result.println("1\t2\t3\t4");
/* 137 */     result.printTab("jakke");
/* 138 */     result.printTab("marsje");
/* 139 */     result.printTab("willy");
/* 140 */     result.printTab("bakker");
/* 141 */     result.printTab("kwak");
/* 142 */     result.printTab("dropje");
/* 143 */     result.printTab("snope");
/* 144 */     result.printTab("qdf");
/* 145 */     result.newLine();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\TabbedPrinter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */