/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.PrintWriter;
/*     */ import jeans.util.StringUtils;
/*     */ import jeans.util.array.StringTable;
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
/*     */ public class HierIO
/*     */ {
/*     */   public static final int TAB_SIZE = 8;
/*  36 */   protected StringTable m_Stab = new StringTable();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassHierarchy readHierarchy(String fname) throws IOException, ClusException {
/*  42 */     HierIOReader rdr = new HierIOReader(fname);
/*  43 */     ClassHierarchy hier = new ClassHierarchy();
/*  44 */     rdr.readHierarchy(hier.getRoot(), 0);
/*  45 */     return hier;
/*     */   }
/*     */   
/*     */   public StringTable getTable() {
/*  49 */     return this.m_Stab;
/*     */   }
/*     */   
/*     */   public static int getDepth(String line) {
/*  53 */     int idx = 0, spc = 0, tab = 0;
/*  54 */     int len = line.length();
/*  55 */     while (idx < len && (line.charAt(idx) == ' ' || line.charAt(idx) == '\t')) {
/*  56 */       if (line.charAt(idx) == ' ') spc++; 
/*  57 */       if (line.charAt(idx) == '\t') tab++; 
/*  58 */       idx++;
/*     */     } 
/*  60 */     return spc / 8 + tab;
/*     */   }
/*     */   
/*     */   public String getID(String line) {
/*  64 */     String id = StringUtils.trimSpacesAndTabs(line);
/*  65 */     return this.m_Stab.get(id);
/*     */   }
/*     */   
/*     */   public static void writePrologTerm(ClassTerm term, PrintWriter writer) {
/*  69 */     String prologID = StringUtils.removeChar(term.getID(), '\'');
/*  70 */     writer.print("node('" + prologID + "',[");
/*  71 */     for (int i = 0; i < term.getNbChildren(); i++) {
/*  72 */       if (i != 0) writer.print(","); 
/*  73 */       ClassTerm subterm = (ClassTerm)term.getChild(i);
/*  74 */       writePrologTerm(subterm, writer);
/*     */     } 
/*  76 */     writer.print("])");
/*     */   }
/*     */   
/*     */   public static boolean hasActiveChild(ClassTerm term, boolean[] bits) {
/*  80 */     for (int i = 0; i < term.getNbChildren(); i++) {
/*  81 */       ClassTerm child = (ClassTerm)term.getChild(i);
/*  82 */       if (bits[child.getIndex()]) return true; 
/*     */     } 
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writePrologTerm(ClassTerm term, boolean[] bits, PrintWriter writer) {
/*  89 */     writer.print("node([");
/*  90 */     for (int i = 0; i < term.getNbChildren(); i++) {
/*  91 */       if (i != 0) writer.print(","); 
/*  92 */       ClassTerm subterm = (ClassTerm)term.getChild(i);
/*  93 */       if (bits[subterm.getIndex()])
/*  94 */       { if (hasActiveChild(subterm, bits)) { writePrologTerm(subterm, bits, writer); }
/*  95 */         else { writer.print("1"); }
/*     */          }
/*  97 */       else { writer.print("0"); }
/*     */     
/*     */     } 
/* 100 */     writer.print("])");
/*     */   }
/*     */   
/*     */   public static void writePrologGraph(String name, ClassTerm term, PrintWriter writer) {
/* 104 */     String prologID = StringUtils.removeChar(term.getID(), '\'');
/* 105 */     writer.println(name + "_name(" + term.getIndex() + ",'" + prologID + "').");
/* 106 */     writer.print(name + "_children(" + term.getIndex() + ",["); int i;
/* 107 */     for (i = 0; i < term.getNbChildren(); i++) {
/* 108 */       if (i != 0) writer.print(","); 
/* 109 */       ClassTerm subterm = (ClassTerm)term.getChild(i);
/* 110 */       writer.print(subterm.getIndex());
/*     */     } 
/* 112 */     writer.println("]).");
/* 113 */     for (i = 0; i < term.getNbChildren(); i++) {
/* 114 */       ClassTerm subterm = (ClassTerm)term.getChild(i);
/* 115 */       writer.println(name + "_child(" + term.getIndex() + "," + subterm.getIndex() + ").");
/*     */     } 
/* 117 */     writer.println();
/* 118 */     for (i = 0; i < term.getNbChildren(); i++) {
/* 119 */       ClassTerm subterm = (ClassTerm)term.getChild(i);
/* 120 */       writePrologGraph(name, subterm, writer);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void writeHierarchy(ClassTerm term) {
/* 125 */     writeHierarchy(term, ClusFormat.OUT_WRITER);
/* 126 */     ClusFormat.OUT_WRITER.flush();
/*     */   }
/*     */   
/*     */   public void writeHierarchy(ClassTerm term, PrintWriter writer) {
/* 130 */     writeHierarchy(0, term, writer);
/* 131 */     writer.flush();
/*     */   }
/*     */   
/*     */   public void writeHierarchy(int tabs, ClassTerm term, PrintWriter writer) {
/* 135 */     for (int i = 0; i < term.getNbChildren(); i++) {
/* 136 */       ClassTerm subterm = (ClassTerm)term.getChild(i);
/* 137 */       writer.print(StringUtils.makeString('\t', tabs) + subterm.getID());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 145 */       writer.println();
/* 146 */       writeHierarchy(tabs + 1, subterm, writer);
/*     */     } 
/*     */   }
/*     */   
/*     */   private class HierIOReader
/*     */   {
/*     */     protected LineNumberReader m_Reader;
/*     */     protected String m_LastLine;
/*     */     
/*     */     public HierIOReader(String fname) throws IOException {
/* 156 */       this.m_Reader = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
/* 157 */       this.m_LastLine = null;
/*     */     }
/*     */     
/*     */     public void readHierarchy(ClassTerm parent, int depth) throws IOException, ClusException {
/* 161 */       ClassTerm child = null;
/* 162 */       String line = getLine();
/* 163 */       while (line != null) {
/* 164 */         int mydepth = HierIO.getDepth(line);
/* 165 */         if (mydepth == depth) {
/* 166 */           String id = HierIO.this.getID(line);
/* 167 */           child = new ClassTerm(id);
/* 168 */           child.addParent(parent);
/* 169 */           parent.addChild(child);
/*     */         } else {
/* 171 */           setLastLine(line);
/* 172 */           if (mydepth < depth) {
/*     */             return;
/*     */           }
/* 175 */           if (mydepth > depth + 1) {
/* 176 */             throw new ClusException("Jump to big in hierarchy");
/*     */           }
/* 178 */           if (child == null) {
/* 179 */             throw new ClusException("Term has no parent");
/*     */           }
/* 181 */           readHierarchy(child, depth + 1);
/*     */         } 
/*     */         
/* 184 */         line = getLine();
/*     */       } 
/*     */     }
/*     */     
/*     */     public String getLine() throws IOException {
/* 189 */       if (this.m_LastLine != null) {
/* 190 */         String result = this.m_LastLine;
/* 191 */         clearLastLine();
/* 192 */         return result;
/*     */       } 
/* 194 */       return this.m_Reader.readLine();
/*     */     }
/*     */     
/*     */     public void setLastLine(String line) {
/* 198 */       this.m_LastLine = line;
/*     */     }
/*     */     
/*     */     public void clearLastLine() {
/* 202 */       this.m_LastLine = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\HierIO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */