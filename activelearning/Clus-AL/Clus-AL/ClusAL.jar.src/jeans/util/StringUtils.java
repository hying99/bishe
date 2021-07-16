/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.PrintWriter;
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
/*     */ public class StringUtils
/*     */ {
/*     */   public static final String DOUBLE_CHARS = "+-eE.";
/*     */   
/*     */   public static String printInt(int nb, int tabs) {
/*  32 */     String out = String.valueOf(nb);
/*  33 */     int len = out.length();
/*  34 */     return out + makeString(' ', tabs - len);
/*     */   }
/*     */   
/*     */   public static String trimSpacesAndTabs(String line) {
/*  38 */     int start = 0, end = line.length() - 1;
/*  39 */     while (start <= end && (line.charAt(start) == ' ' || line.charAt(start) == '\t')) {
/*  40 */       start++;
/*     */     }
/*  42 */     while (end >= start && (line.charAt(end) == ' ' || line.charAt(end) == '\t')) {
/*  43 */       end--;
/*     */     }
/*  45 */     return line.substring(start, end + 1);
/*     */   }
/*     */   
/*     */   public static String printStrMax(String out, int tabs) {
/*  49 */     int len = out.length();
/*  50 */     if (len > tabs) {
/*  51 */       return out.substring(0, tabs);
/*     */     }
/*  53 */     return out + makeString(' ', tabs - len);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String printStr(String out, int tabs) {
/*  58 */     int len = out.length();
/*  59 */     return out + makeString(' ', tabs - len);
/*     */   }
/*     */   
/*     */   public static void printTabs(PrintWriter out, int tabs) {
/*  63 */     for (int i = 0; i < tabs; i++) {
/*  64 */       out.print("\t");
/*     */     }
/*     */   }
/*     */   
/*     */   public static String replaceChars(String strg, char ch1, char ch2) {
/*  69 */     StringBuffer res = new StringBuffer();
/*  70 */     for (int ctr = 0; ctr < strg.length(); ctr++) {
/*  71 */       char ch = strg.charAt(ctr);
/*  72 */       if (ch == ch1) ch = ch2; 
/*  73 */       res.append(ch);
/*     */     } 
/*  75 */     return res.toString();
/*     */   }
/*     */   
/*     */   public static String removeChar(String strg, char ch1) {
/*  79 */     StringBuffer res = new StringBuffer();
/*  80 */     for (int ctr = 0; ctr < strg.length(); ctr++) {
/*  81 */       char ch = strg.charAt(ctr);
/*  82 */       if (ch != ch1) res.append(ch); 
/*     */     } 
/*  84 */     return res.toString();
/*     */   }
/*     */   
/*     */   public static String makeString(char ch, int cnt) {
/*  88 */     StringBuffer res = new StringBuffer();
/*  89 */     for (int ctr = 0; ctr < cnt; ) { res.append(ch); ctr++; }
/*  90 */      return res.toString();
/*     */   }
/*     */   
/*     */   public static boolean isInteger(String str) {
/*  94 */     for (int ctr = 0; ctr < str.length(); ctr++) {
/*  95 */       char ch = str.charAt(ctr);
/*  96 */       if ((ch < '0' || ch > '9') && ch != '+' && ch != '-') return false; 
/*     */     } 
/*  98 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean isDouble(String str) {
/* 102 */     for (int ctr = 0; ctr < str.length(); ctr++) {
/* 103 */       char ch = str.charAt(ctr);
/* 104 */       if ((ch < '0' || ch > '9') && "+-eE.".indexOf(ch) == -1) return false; 
/*     */     } 
/* 106 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean unCaseCompare(String a, String b) {
/* 110 */     int l1 = a.length();
/* 111 */     int l2 = b.length();
/* 112 */     if (l1 != l2) return false; 
/* 113 */     for (int i = 0; i < l1; i++) {
/* 114 */       if (Character.toUpperCase(a.charAt(i)) != Character.toUpperCase(b.charAt(i)))
/* 115 */         return false; 
/* 116 */     }  return true;
/*     */   }
/*     */   
/*     */   public static int getBoolean(String str) {
/* 120 */     String upper = str.toUpperCase();
/* 121 */     if (upper.equals("TRUE")) return 1; 
/* 122 */     if (upper.equals("FALSE")) return 0; 
/* 123 */     if (upper.equals("ON")) return 1; 
/* 124 */     if (upper.equals("OFF")) return 0; 
/* 125 */     if (upper.equals("YES")) return 1; 
/* 126 */     if (upper.equals("NO")) return 0; 
/* 127 */     return -1;
/*     */   }
/*     */   
/*     */   public static String roundDouble(double d, int place) {
/* 131 */     if (place <= 0) return String.valueOf((int)(d + ((d > 0.0D) ? 0.5D : -0.5D))); 
/* 132 */     String s = "";
/* 133 */     if (d < 0.0D) {
/* 134 */       s = s + "-";
/* 135 */       d = -d;
/*     */     } 
/* 137 */     d += 0.5D * Math.pow(10.0D, -place);
/* 138 */     if (d > 1.0D) {
/* 139 */       int i = (int)d;
/* 140 */       s = s + i;
/* 141 */       d -= i;
/*     */     } else {
/* 143 */       s = s + "0";
/*     */     } 
/* 145 */     if (d > 0.0D) {
/* 146 */       d++;
/* 147 */       String f = "" + (int)(d * Math.pow(10.0D, place));
/* 148 */       s = s + "." + f.substring(1);
/*     */     } 
/* 150 */     return s;
/*     */   }
/*     */   
/*     */   public static int occurs(char ch, String str) {
/* 154 */     int nb = 0;
/* 155 */     int len = str.length();
/* 156 */     for (int i = 0; i < len; i++) {
/* 157 */       if (str.charAt(i) == ch) nb++; 
/* 158 */     }  return nb;
/*     */   }
/*     */   
/*     */   public static String removeSuffix(String str, String suffix) {
/* 162 */     if (str.endsWith(suffix)) {
/* 163 */       return str.substring(0, str.length() - suffix.length());
/*     */     }
/* 165 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String removeSingleQuote(String str) {
/* 170 */     int len = str.length();
/* 171 */     if (len >= 2 && str.charAt(0) == '\'' && str.charAt(len - 1) == '\'') {
/* 172 */       return str.substring(1, len - 1);
/*     */     }
/* 174 */     return str;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */