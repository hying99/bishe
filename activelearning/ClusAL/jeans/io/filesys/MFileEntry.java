/*     */ package jeans.io.filesys;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.util.Date;
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
/*     */ public class MFileEntry
/*     */ {
/*     */   public static final int T_ICON = 0;
/*     */   public static final int T_NAME = 1;
/*     */   public static final int T_DATE = 2;
/*     */   public static final int T_TIME = 3;
/*     */   public static final int T_SIZE = 4;
/*  36 */   public static final String[] m_hSizeString = new String[] { "kB", "MB", "GB" };
/*     */   
/*     */   protected String m_sName;
/*     */   protected long m_iLength;
/*     */   protected Date m_hDate;
/*     */   
/*     */   public MFileEntry(String name, long len, long modified) {
/*  43 */     this.m_sName = name;
/*  44 */     this.m_iLength = len;
/*  45 */     this.m_hDate = new Date(modified);
/*     */   }
/*     */   
/*     */   public String getName() {
/*  49 */     return this.m_sName;
/*     */   }
/*     */   
/*     */   public long getLength() {
/*  53 */     return this.m_iLength;
/*     */   }
/*     */   
/*     */   public Date getDate() {
/*  57 */     return this.m_hDate;
/*     */   }
/*     */   
/*     */   public String getExtension() {
/*  61 */     int idx = this.m_sName.lastIndexOf('.');
/*  62 */     if (idx == -1 || idx >= this.m_sName.length() - 1) return ""; 
/*  63 */     return this.m_sName.substring(idx + 1);
/*     */   }
/*     */   
/*     */   public String getTimeString() {
/*  67 */     return DateFormat.getTimeInstance(3).format(this.m_hDate);
/*     */   }
/*     */   
/*     */   public String getDateString() {
/*  71 */     return DateFormat.getDateInstance(3).format(this.m_hDate);
/*     */   }
/*     */   
/*     */   public String getLengthString() {
/*  75 */     int idx = 0;
/*  76 */     long len = this.m_iLength;
/*  77 */     while (idx < 3 && len >= 1024L) {
/*  78 */       idx++;
/*  79 */       len /= 1024L;
/*     */     } 
/*  81 */     if (idx == 0) return String.valueOf(len); 
/*  82 */     return String.valueOf(len) + " " + m_hSizeString[idx - 1];
/*     */   }
/*     */   
/*     */   protected int compareString(String a, String b) {
/*  86 */     return a.compareTo(b);
/*     */   }
/*     */   
/*     */   protected int compareLong(long a, long b) {
/*  90 */     if (a > b) return -1; 
/*  91 */     if (a == b) return 0; 
/*  92 */     return 1;
/*     */   }
/*     */   
/*     */   protected int compareDate(Date a, Date b) {
/*  96 */     if (a.equals(b)) return 0; 
/*  97 */     if (a.after(b)) return -1; 
/*  98 */     return 1;
/*     */   }
/*     */   
/*     */   protected int compareExtention(MFileEntry a, MFileEntry b) {
/* 102 */     int res = compareString(a.getExtension(), b.getExtension());
/* 103 */     if (res == 0) res = compareString(a.getName(), b.getName()); 
/* 104 */     return res;
/*     */   }
/*     */   
/*     */   public int compareTo(MFileEntry ent, int type) {
/* 108 */     switch (type) {
/*     */       case 0:
/* 110 */         return compareExtention(this, ent);
/*     */       case 1:
/* 112 */         return compareString(this.m_sName, ent.getName());
/*     */       case 2:
/*     */       case 3:
/* 115 */         return compareDate(this.m_hDate, ent.getDate());
/*     */       case 4:
/* 117 */         return compareLong(this.m_iLength, ent.getLength());
/*     */     } 
/* 119 */     return 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\filesys\MFileEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */