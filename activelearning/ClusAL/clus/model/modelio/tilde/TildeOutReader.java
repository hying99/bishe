/*     */ package clus.model.modelio.tilde;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Vector;
/*     */ import jeans.util.MStreamTokenizer;
/*     */ import jeans.util.MyArray;
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
/*     */ public class TildeOutReader
/*     */ {
/*     */   public static final int SINGLE_CLASSIFY = 0;
/*     */   public static final int MULTI_CLASSIFY = 1;
/*     */   public static final int REGRESSION = 2;
/*     */   protected static final boolean m_Debug = true;
/*  42 */   protected int m_Dim = 1;
/*  43 */   protected int m_Mode = 1;
/*     */   protected ClusNode m_Root;
/*     */   protected MStreamTokenizer m_Tokens;
/*  46 */   protected MyArray m_Info = new MyArray();
/*  47 */   protected ClusSchema m_Schema = new ClusSchema("TildeTree");
/*     */   protected NominalAttrType m_Target;
/*     */   protected ClusStatManager m_StatMgr;
/*     */   
/*     */   public TildeOutReader(InputStream strm) {
/*  52 */     this.m_Tokens = new MStreamTokenizer(strm);
/*  53 */     this.m_Tokens.setCharTokens(":,+-()|[]");
/*     */   }
/*     */   
/*     */   public void doParse() throws IOException {
/*  57 */     toSettings();
/*  58 */     readSettings();
/*  59 */     skipHeader();
/*     */     try {
/*  61 */       this.m_Schema.addIndices(0);
/*  62 */       this.m_StatMgr = new ClusStatManager(this.m_Schema, new Settings());
/*  63 */       this.m_StatMgr.initStatisticAndStatManager();
/*  64 */       TildeTreeReader treer = new TildeTreeReader(this);
/*  65 */       this.m_Root = treer.readTree();
/*  66 */       this.m_Root.addChildStats();
/*  67 */       this.m_Schema.showDebug();
/*  68 */       System.out.println("Total Root Node: " + this.m_Root.getClusteringStat());
/*  69 */     } catch (ClusException e) {
/*  70 */       throw new IOException(e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setDim(int dim) {
/*  75 */     this.m_Dim = dim;
/*     */   }
/*     */   
/*     */   public int getDim() {
/*  79 */     return this.m_Dim;
/*     */   }
/*     */   
/*     */   public boolean getDebug() {
/*  83 */     return true;
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatMgr() {
/*  87 */     return this.m_StatMgr;
/*     */   }
/*     */   
/*     */   public ClusNode getTree() {
/*  91 */     return this.m_Root;
/*     */   }
/*     */   
/*     */   public int getMode() {
/*  95 */     return this.m_Mode;
/*     */   }
/*     */   
/*     */   public void readTargetSchema(String fname) throws IOException {
/*  99 */     MStreamTokenizer tokens = new MStreamTokenizer(fname);
/* 100 */     int nb = Math.min(1, getDim());
/* 101 */     for (int i = 0; i < nb; i++) {
/* 102 */       NominalAttrType nom = null;
/* 103 */       nom.setName(tokens.readToken());
/* 104 */       tokens.readChar('{');
/* 105 */       for (int j = 0; j < nom.getNbValues(); j++) {
/* 106 */         if (j != 0) tokens.readChar(','); 
/* 107 */         nom.setValue(j, tokens.readToken());
/*     */       } 
/* 109 */       tokens.readChar('}');
/*     */     } 
/*     */   }
/*     */   
/*     */   public void skip(String skip) throws IOException {
/* 114 */     boolean done = false;
/* 115 */     while (!done) {
/* 116 */       String line = this.m_Tokens.readTillEol();
/* 117 */       if (line.indexOf(skip) != -1) done = true; 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void skipHeader() throws IOException {
/* 122 */     skip("notation of pruned tree");
/*     */   }
/*     */   
/*     */   public void toSettings() throws IOException {
/* 126 */     skip("ettings");
/*     */   }
/*     */ 
/*     */   
/*     */   public void readInfo() throws IOException {
/* 131 */     skip("--------------------------");
/* 132 */     boolean done = false;
/* 133 */     while (!done) {
/* 134 */       String line = this.m_Tokens.readTillEol();
/* 135 */       if (line.indexOf("Compact notation") != -1) { done = true; continue; }
/* 136 */        this.m_Info.addElement(line);
/*     */     } 
/* 138 */     skip("Compact notation of pruned tree");
/*     */   }
/*     */   
/*     */   public void readSetting(String setting) throws IOException {
/* 142 */     System.out.println("Setting: " + setting);
/* 143 */     if (setting.equals("classes")) {
/* 144 */       System.out.println("Do classes ***********");
/* 145 */       int count = 0;
/* 146 */       Vector<String> values = new Vector();
/* 147 */       this.m_Tokens.readChar('[');
/* 148 */       while (!this.m_Tokens.isNextToken(']')) {
/* 149 */         if (count > 0) this.m_Tokens.readChar(','); 
/* 150 */         values.addElement(this.m_Tokens.readToken());
/* 151 */         count++;
/*     */       } 
/* 153 */       this.m_Target = new NominalAttrType("Target", values.size());
/* 154 */       this.m_Target.setStatus(1);
/* 155 */       for (int i = 0; i < values.size(); i++) {
/* 156 */         String value = values.elementAt(i);
/* 157 */         this.m_Target.setValue(i, value);
/*     */       } 
/* 159 */       this.m_Tokens.readTillEol();
/* 160 */     } else if (setting.equals("tilde_mode")) {
/* 161 */       String mode = this.m_Tokens.readTillEol();
/* 162 */       if (mode.indexOf("classify") != -1) { this.m_Mode = 1; }
/* 163 */       else { this.m_Mode = 2; }
/* 164 */        System.out.println("Tilde mode: " + mode);
/*     */     } else {
/* 166 */       this.m_Tokens.readTillEol();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void readSettings() throws IOException {
/* 171 */     boolean done = false;
/* 172 */     while (!done) {
/* 173 */       String token = this.m_Tokens.readToken();
/* 174 */       if (token.equals("*")) {
/* 175 */         String setting = this.m_Tokens.readToken();
/* 176 */         this.m_Tokens.readChar(':');
/* 177 */         readSetting(setting); continue;
/* 178 */       }  if (token.equals("**")) {
/* 179 */         this.m_Tokens.readTillEol(); continue;
/*     */       } 
/* 181 */       System.out.println("Error: " + token);
/* 182 */       done = true;
/*     */     } 
/*     */     
/* 185 */     if (this.m_Mode == 2) {
/* 186 */       System.out.println("Dimension = " + getDim());
/* 187 */       for (int i = 0; i < getDim(); i++) {
/* 188 */         NominalAttrType target = new NominalAttrType("Target", 2);
/* 189 */         target.setStatus(1);
/* 190 */         target.setValue(0, "pos");
/* 191 */         target.setValue(1, "neg");
/* 192 */         this.m_Schema.addAttrType((ClusAttrType)target);
/*     */       } 
/*     */     } else {
/* 195 */       this.m_Schema.addAttrType((ClusAttrType)this.m_Target);
/*     */     } 
/*     */   }
/*     */   
/*     */   public MStreamTokenizer getStream() {
/* 200 */     return this.m_Tokens;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 204 */     this.m_Tokens.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\modelio\tilde\TildeOutReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */