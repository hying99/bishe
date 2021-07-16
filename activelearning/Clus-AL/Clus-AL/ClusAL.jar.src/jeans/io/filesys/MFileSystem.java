/*     */ package jeans.io.filesys;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Vector;
/*     */ import jeans.util.sort.MComparator;
/*     */ import jeans.util.sort.MStringSorter;
/*     */ import jeans.util.sort.MVectorSorter;
/*     */ import jeans.util.thread.MCallback;
/*     */ import jeans.util.thread.MWorkerThread;
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
/*     */ public abstract class MFileSystem
/*     */ {
/*     */   public static final int OP_RELOAD = 0;
/*     */   public static final int OP_CHDIR = 1;
/*     */   protected MCallback m_hCallback;
/*     */   protected MWorkerThread m_hWorker;
/*  40 */   protected Vector[] m_hDirectories = new Vector[2];
/*  41 */   protected Vector[] m_hFiles = new Vector[2];
/*     */   protected int m_iCurr;
/*  43 */   protected MStringSorter m_hStringSorter = new MStringSorter();
/*  44 */   protected MFileSorter m_hFileSorter = new MFileSorter();
/*     */   
/*     */   public MFileSystem() {
/*  47 */     for (int i = 0; i < 2; i++) {
/*  48 */       this.m_hDirectories[i] = new Vector();
/*  49 */       this.m_hFiles[i] = new Vector();
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNbDirectories() {
/*  54 */     return this.m_hDirectories[this.m_iCurr].size();
/*     */   }
/*     */   
/*     */   public String getDirectoryAt(int idx) {
/*  58 */     Vector<String> curr = this.m_hDirectories[this.m_iCurr];
/*  59 */     if (idx < curr.size()) return curr.elementAt(idx); 
/*  60 */     return "";
/*     */   }
/*     */   
/*     */   public int getNbFiles() {
/*  64 */     return this.m_hFiles[this.m_iCurr].size();
/*     */   }
/*     */   
/*     */   public MFileEntry getFileAt(int idx) {
/*  68 */     Vector<MFileEntry> curr = this.m_hFiles[this.m_iCurr];
/*  69 */     if (idx < curr.size()) return curr.elementAt(idx); 
/*  70 */     return null;
/*     */   }
/*     */   
/*     */   public MFileEntry getEntry(String name) {
/*  74 */     Vector<MFileEntry> curr = this.m_hFiles[this.m_iCurr];
/*  75 */     for (int i = 0; i < curr.size(); i++) {
/*  76 */       MFileEntry ent = curr.elementAt(i);
/*  77 */       if (ent.getName().equals(name)) return ent; 
/*     */     } 
/*  79 */     return null;
/*     */   }
/*     */   
/*     */   public void clearLists(int idx) {
/*  83 */     this.m_hFiles[idx].removeAllElements();
/*  84 */     this.m_hDirectories[idx].removeAllElements();
/*     */   }
/*     */   
/*     */   public void setCallback(MCallback call) {
/*  88 */     this.m_hCallback = call;
/*     */   }
/*     */   
/*     */   public void setWorker(MWorkerThread worker) {
/*  92 */     this.m_hWorker = worker;
/*     */   }
/*     */   
/*     */   public void toggleCurr() {
/*  96 */     clearLists(this.m_iCurr);
/*  97 */     this.m_iCurr = 1 - this.m_iCurr;
/*     */   }
/*     */   
/*     */   public int getCurrent() {
/* 101 */     return this.m_iCurr;
/*     */   }
/*     */   
/*     */   public int getBuffer() {
/* 105 */     return 1 - this.m_iCurr;
/*     */   }
/*     */   
/*     */   public void sortFiles(int idx) {
/* 109 */     MVectorSorter.quickSort(this.m_hFiles[idx], this.m_hFileSorter);
/*     */   }
/*     */   
/*     */   public void sortDirectories(int idx) {
/* 113 */     MVectorSorter.quickSort(this.m_hDirectories[idx], (MComparator)this.m_hStringSorter);
/*     */   }
/*     */   
/*     */   public boolean reload(int type) {
/* 117 */     MyWorker job = new MyWorker(null, type, 0);
/* 118 */     if (this.m_hWorker == null) this.m_hWorker = MWorkerThread.getInstance(); 
/* 119 */     this.m_hWorker.execute(job);
/* 120 */     return true;
/*     */   }
/*     */   
/*     */   public boolean chdir(String dir, int type) {
/* 124 */     MyWorker job = new MyWorker(dir, type, 1);
/* 125 */     if (this.m_hWorker == null) this.m_hWorker = MWorkerThread.getInstance(); 
/* 126 */     this.m_hWorker.execute(job);
/* 127 */     return true;
/*     */   }
/*     */   
/*     */   public abstract InputStream openFile(MFileEntry paramMFileEntry) throws FileNotFoundException, IOException;
/*     */   
/*     */   public abstract OutputStream openOutputStream(String paramString) throws IOException;
/*     */   
/*     */   public abstract void doReload();
/*     */   
/*     */   public abstract void doChdir(String paramString);
/*     */   
/*     */   public class MyWorker implements Runnable {
/*     */     protected Object m_hArg;
/*     */     protected int m_iType;
/*     */     protected int m_iOperator;
/*     */     
/*     */     public MyWorker(Object arg, int type, int operator) {
/* 144 */       this.m_hArg = arg;
/* 145 */       this.m_iType = type;
/* 146 */       this.m_iOperator = operator;
/*     */     }
/*     */     
/*     */     public void run() {
/* 150 */       Object result = null;
/* 151 */       switch (this.m_iOperator) {
/*     */         case 0:
/* 153 */           MFileSystem.this.doReload();
/*     */           break;
/*     */         case 1:
/* 156 */           MFileSystem.this.doChdir((String)this.m_hArg);
/*     */           break;
/*     */       } 
/*     */       
/* 160 */       if (MFileSystem.this.m_hCallback != null) MFileSystem.this.m_hCallback.callBack(result, this.m_iType); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\filesys\MFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */