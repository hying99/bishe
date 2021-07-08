/*     */ package jeans.io.filesys;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public class MLocalFileSystem
/*     */   extends MFileSystem
/*     */ {
/*  29 */   protected File m_hDir = new File(".");
/*     */   protected boolean m_bIsRoot;
/*     */   
/*     */   protected void doLoad() {
/*  33 */     int other = getBuffer();
/*  34 */     if (this.m_bIsRoot) {
/*  35 */       File[] files = File.listRoots();
/*  36 */       for (int i = 0; i < files.length; i++) {
/*  37 */         File file = files[i];
/*  38 */         this.m_hDirectories[other].addElement(file.getAbsolutePath());
/*     */       } 
/*     */     } else {
/*  41 */       File[] files = this.m_hDir.listFiles();
/*  42 */       this.m_hDirectories[other].addElement("..");
/*  43 */       for (int i = 0; i < files.length; i++) {
/*  44 */         File file = files[i];
/*  45 */         if (file.isDirectory()) {
/*  46 */           this.m_hDirectories[other].addElement(file.getName());
/*     */         } else {
/*  48 */           MFileEntry entry = new MFileEntry(file.getName(), file.length(), file.lastModified());
/*  49 */           this.m_hFiles[other].addElement(entry);
/*     */         } 
/*     */       } 
/*     */     } 
/*  53 */     sortDirectories(other);
/*  54 */     sortFiles(other);
/*  55 */     toggleCurr();
/*     */   }
/*     */   
/*     */   public InputStream openFile(MFileEntry ent) throws FileNotFoundException, IOException {
/*  59 */     String path = this.m_hDir.getCanonicalPath();
/*  60 */     return new FileInputStream(path + File.separator + ent.getName());
/*     */   }
/*     */   
/*     */   public OutputStream openOutputStream(String name) throws IOException {
/*  64 */     String path = this.m_hDir.getCanonicalPath();
/*  65 */     return new FileOutputStream(path + File.separator + name);
/*     */   }
/*     */   
/*     */   public void doReload() {
/*  69 */     doLoad();
/*     */   }
/*     */   
/*     */   public void doChdir(String str) {
/*     */     try {
/*  74 */       String newPath = null;
/*  75 */       String sep = File.separator;
/*  76 */       String path = this.m_bIsRoot ? "" : this.m_hDir.getCanonicalPath();
/*  77 */       if (str.equals("..")) {
/*  78 */         if (this.m_bIsRoot) {
/*     */           return;
/*     */         }
/*  81 */         int idx = path.lastIndexOf(sep);
/*  82 */         if (idx != -1) {
/*  83 */           int len = path.length();
/*  84 */           if (len <= 3 && len >= 2 && path.charAt(1) == ':') {
/*  85 */             this.m_bIsRoot = true;
/*  86 */             System.out.println("CD: ROOT");
/*  87 */             doLoad();
/*     */             return;
/*     */           } 
/*  90 */           newPath = path.substring(0, idx);
/*     */         } else {
/*  92 */           this.m_bIsRoot = true;
/*  93 */           System.out.println("CD: ROOT");
/*  94 */           doLoad();
/*     */         } 
/*     */       } else {
/*     */         
/*  98 */         int idx = path.lastIndexOf(sep);
/*  99 */         int delta = path.length() - sep.length();
/* 100 */         if (idx != -1 && idx == delta) { newPath = path + str; }
/* 101 */         else { newPath = path + File.separator + str; }
/*     */       
/* 103 */       }  if (newPath != null) {
/* 104 */         File newDir = new File(newPath + sep);
/* 105 */         if (newDir.exists()) {
/* 106 */           this.m_bIsRoot = false;
/* 107 */           this.m_hDir = newDir;
/* 108 */           System.out.println("CD: " + newPath + sep);
/* 109 */           doLoad();
/*     */         } 
/*     */       } 
/* 112 */     } catch (IOException iOException) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\filesys\MLocalFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */