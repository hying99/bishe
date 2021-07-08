/*     */ package jeans.io.ini;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ public class INIFileSection
/*     */   extends INIFileNode
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  34 */   protected Hashtable m_hEntries = new Hashtable<>();
/*  35 */   protected Vector m_hEntryList = new Vector();
/*     */   
/*     */   public INIFileSection(String name) {
/*  38 */     super(name);
/*     */   }
/*     */   
/*     */   public INIFileSection() {
/*  42 */     this("");
/*     */   }
/*     */   
/*     */   public boolean isSectionGroup() {
/*  46 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isSection() {
/*  50 */     return true;
/*     */   }
/*     */   
/*     */   public INIFileNode cloneNode() {
/*  54 */     INIFileSection sec = new INIFileSection(getName());
/*  55 */     for (Enumeration<INIFileNode> e = getNodes(); e.hasMoreElements(); ) {
/*  56 */       INIFileNode node = e.nextElement();
/*  57 */       sec.addNode(node.cloneNode());
/*     */     } 
/*  59 */     return sec;
/*     */   }
/*     */   
/*     */   public int getNbNodes() {
/*  63 */     return this.m_hEntryList.size();
/*     */   }
/*     */   
/*     */   public Enumeration getNodes() {
/*  67 */     return this.m_hEntryList.elements();
/*     */   }
/*     */   
/*     */   public INIFileEntry getEntry(String name) {
/*  71 */     return (INIFileEntry)this.m_hEntries.get(name);
/*     */   }
/*     */   
/*     */   public INIFileNode getNode(String name) {
/*  75 */     return (INIFileNode)this.m_hEntries.get(name);
/*     */   }
/*     */   
/*     */   public void addNode(INIFileNode entry) {
/*  79 */     this.m_hEntries.put(entry.getName(), entry);
/*  80 */     this.m_hEntryList.addElement(entry);
/*  81 */     entry.setParent(this);
/*     */   }
/*     */   
/*     */   public INIFileNode getPathNode(String path, Class<?> type) {
/*  85 */     INIFileNode node = getPathNode(path);
/*  86 */     if (node != null && node.getClass() == type) {
/*  87 */       return node;
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public INIFileNode getPathNode(String path, int type) {
/*  94 */     INIFileNode node = getPathNode(path);
/*  95 */     if (node == null) {
/*  96 */       return null;
/*     */     }
/*  98 */     switch (type) {
/*     */       case 0:
/*     */       case 1:
/* 101 */         if (node instanceof INIFileArray && ((INIFileArray)node)
/* 102 */           .getType() == type) {
/* 103 */           return node;
/*     */         }
/*     */         break;
/*     */     } 
/* 107 */     return null;
/*     */   }
/*     */   
/*     */   public INIFileNode getPathNode(String path) {
/* 111 */     String nextNode = null, subNode = null;
/* 112 */     if (path.equals("")) {
/* 113 */       return this;
/*     */     }
/* 115 */     int idx = path.indexOf('.');
/* 116 */     if (idx == -1) {
/* 117 */       nextNode = path;
/* 118 */       subNode = "";
/*     */     } else {
/* 120 */       nextNode = path.substring(0, idx);
/* 121 */       subNode = path.substring(idx + 1);
/*     */     } 
/* 123 */     INIFileNode node = getNode(nextNode);
/* 124 */     if (node != null) {
/* 125 */       if (node.isSection()) {
/* 126 */         return ((INIFileSection)node).getPathNode(subNode);
/*     */       }
/* 128 */       return node;
/*     */     } 
/*     */     
/* 131 */     System.out.println("Can't find node: " + nextNode);
/* 132 */     for (Enumeration<INIFileNode> e = getNodes(); e.hasMoreElements(); ) {
/* 133 */       INIFileNode entry = e.nextElement();
/* 134 */       System.out.println("   " + entry.getName());
/*     */     } 
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void doLoad(String sectionName, String groupName, MStreamTokenizer tokens) throws IOException {
/* 141 */     INIFileNode node = getNode(groupName);
/* 142 */     if (node != null && node instanceof INIFileSectionGroup) {
/* 143 */       INIFileSectionGroup group = (INIFileSectionGroup)node;
/* 144 */       INIFileSection sec = (INIFileSection)group.getPrototype().cloneNode();
/* 145 */       sec.setName(sectionName);
/* 146 */       group.addSection(sec);
/* 147 */       sec.load(tokens);
/*     */     } else {
/* 149 */       throw new IOException("Error in the settings file. Don't know about group '" + groupName + "' at line: " + tokens.getLine());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void doLoad(String name, MStreamTokenizer tokens) throws IOException {
/* 154 */     INIFileNode node = getNode(name);
/* 155 */     if (node != null && node instanceof INIFileSection) {
/* 156 */       INIFileSection section = (INIFileSection)node;
/* 157 */       section.load(tokens);
/*     */     } else {
/*     */       
/* 160 */       throw new IOException("Error in the settings file. Don't know about section '" + name + "' at line: " + tokens.getLine());
/*     */     } 
/*     */   }
/*     */   public void load(MStreamTokenizer tokens) throws IOException {
/*     */     String name;
/* 165 */     setEnabled(true);
/*     */     while (true) {
/* 167 */       String token = tokens.getToken();
/*     */       
/* 169 */       if (token == null) {
/*     */         return;
/*     */       }
/* 172 */       if (token.equals("[")) {
/* 173 */         tokens.pushBackToken(token);
/*     */         return;
/*     */       } 
/* 176 */       if (token.equals("<")) {
/* 177 */         if (getDepth() >= 1) {
/* 178 */           tokens.pushBackToken(token);
/*     */           
/*     */           return;
/*     */         } 
/* 182 */         int saveline = tokens.getLine();
/* 183 */         String str = tokens.readTillEol();
/*     */         
/* 185 */         int idx1 = str.indexOf('>');
/* 186 */         if (idx1 == -1) {
/* 187 */           throw new IOException("Error in the settings file. Character '>' expected at line: " + saveline);
/*     */         }
/*     */         
/* 190 */         int idx2 = str.indexOf(',');
/* 191 */         if (idx2 != -1) {
/* 192 */           String groupName = str.substring(0, idx2).trim();
/* 193 */           String sectionName = str.substring(idx2 + 1, idx1).trim();
/* 194 */           doLoad(sectionName, groupName, tokens); continue;
/*     */         } 
/* 196 */         doLoad(str.substring(0, idx1).trim(), tokens);
/*     */         
/*     */         continue;
/*     */       } 
/* 200 */       name = token.trim();
/* 201 */       tokens.readChar('=');
/*     */       
/* 203 */       INIFileNode entry = getNode(name);
/* 204 */       if (entry != null && entry instanceof INIFileEntry) {
/* 205 */         ((INIFileEntry)entry).build(tokens); continue;
/*     */       }  break;
/* 207 */     }  throw new IOException("Error in the settings file. Don't know about entry '" + name + "' at line: " + tokens.getLine());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void save(String group, PrintWriter writer) throws IOException {
/* 214 */     int depth = getDepth();
/* 215 */     if (!isEnabled()) {
/*     */       return;
/*     */     }
/* 218 */     if (group == null) {
/* 219 */       if (depth == 0) {
/* 220 */         writer.println("[" + getName() + "]");
/*     */       } else {
/* 222 */         writer.println("<" + getName() + ">");
/*     */       } 
/* 224 */     } else if (depth == 0) {
/* 225 */       writer.println("[" + group + ", " + getName() + "]");
/*     */     } else {
/* 227 */       writer.println("<" + group + ", " + getName() + ">");
/*     */     } 
/* 229 */     for (Enumeration<INIFileNode> e = getNodes(); e.hasMoreElements(); ) {
/* 230 */       INIFileNode entry = e.nextElement();
/* 231 */       if (entry.isEnabled()) {
/* 232 */         entry.save(writer);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void save(PrintWriter writer) throws IOException {
/* 238 */     save((String)null, writer);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\ini\INIFileSection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */