/*     */ package org.jgap.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import java.util.zip.ZipInputStream;
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
/*     */ public final class JarResources
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*     */   public boolean debugOn = false;
/*  33 */   private Hashtable htSizes = new Hashtable<Object, Object>();
/*     */   
/*  35 */   private Hashtable htJarContents = new Hashtable<Object, Object>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String jarFileName;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JarResources(String jarFileName) {
/*  46 */     this.jarFileName = jarFileName;
/*  47 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getResource(String name) {
/*  55 */     return (byte[])this.htJarContents.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init() {
/*     */     try {
/*  64 */       ZipFile zf = new ZipFile(this.jarFileName);
/*  65 */       Enumeration<? extends ZipEntry> e = zf.entries();
/*  66 */       while (e.hasMoreElements()) {
/*  67 */         ZipEntry zipEntry = e.nextElement();
/*  68 */         if (this.debugOn) {
/*  69 */           System.out.println(dumpZipEntry(zipEntry));
/*     */         }
/*  71 */         this.htSizes.put(zipEntry.getName(), new Integer((int)zipEntry.getSize()));
/*     */       } 
/*  73 */       zf.close();
/*     */       
/*  75 */       FileInputStream fis = new FileInputStream(this.jarFileName);
/*  76 */       BufferedInputStream bis = new BufferedInputStream(fis);
/*  77 */       ZipInputStream zis = new ZipInputStream(bis);
/*  78 */       ZipEntry ze = null;
/*  79 */       while ((ze = zis.getNextEntry()) != null) {
/*  80 */         if (ze.isDirectory()) {
/*     */           continue;
/*     */         }
/*  83 */         if (this.debugOn) {
/*  84 */           System.out.println("ze.getName()=" + ze.getName() + "," + "getSize()=" + ze.getSize());
/*     */         }
/*     */ 
/*     */         
/*  88 */         int size = (int)ze.getSize();
/*     */         
/*  90 */         if (size == -1) {
/*  91 */           size = ((Integer)this.htSizes.get(ze.getName())).intValue();
/*     */         }
/*  93 */         byte[] b = new byte[size];
/*  94 */         int rb = 0;
/*  95 */         int chunk = 0;
/*  96 */         while (size - rb > 0) {
/*  97 */           chunk = zis.read(b, rb, size - rb);
/*  98 */           if (chunk == -1) {
/*     */             break;
/*     */           }
/* 101 */           rb += chunk;
/*     */         } 
/*     */         
/* 104 */         this.htJarContents.put(ze.getName(), b);
/* 105 */         if (this.debugOn) {
/* 106 */           System.out.println(ze.getName() + "  rb=" + rb + ",size=" + size + ",csize=" + ze.getCompressedSize());
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 113 */     catch (NullPointerException e) {
/* 114 */       System.out.println("done.");
/* 115 */     } catch (FileNotFoundException e) {
/* 116 */       e.printStackTrace();
/* 117 */     } catch (IOException e) {
/* 118 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String dumpZipEntry(ZipEntry ze) {
/* 127 */     StringBuffer sb = new StringBuffer();
/* 128 */     if (ze.isDirectory()) {
/* 129 */       sb.append("d ");
/*     */     } else {
/*     */       
/* 132 */       sb.append("f ");
/*     */     } 
/* 134 */     if (ze.getMethod() == 0) {
/* 135 */       sb.append("stored   ");
/*     */     } else {
/*     */       
/* 138 */       sb.append("defalted ");
/*     */     } 
/* 140 */     sb.append(ze.getName());
/* 141 */     sb.append("\t");
/* 142 */     sb.append("" + ze.getSize());
/* 143 */     if (ze.getMethod() == 8) {
/* 144 */       sb.append("/" + ze.getCompressedSize());
/*     */     }
/* 146 */     return sb.toString();
/*     */   }
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
/*     */   public static void main(String[] args) throws IOException {
/* 169 */     if (args.length != 2) {
/* 170 */       System.err.println("usage: java JarResources <jar file name> <resource name>");
/*     */ 
/*     */       
/* 173 */       System.exit(1);
/*     */     } 
/* 175 */     JarResources jr = new JarResources(args[0]);
/* 176 */     byte[] buff = jr.getResource(args[1]);
/* 177 */     if (buff == null) {
/* 178 */       System.out.println("Could not find " + args[1] + ".");
/*     */     } else {
/*     */       
/* 181 */       System.out.println("Found " + args[1] + " (length=" + buff.length + ").");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\JarResources.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */