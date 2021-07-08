/*     */ package org.jgap.distr.grid.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.SocketException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import org.jgap.distr.grid.request.Status;
/*     */ import org.jgap.distr.grid.request.VersionInfo;
/*     */ import org.jgap.util.FileKit;
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
/*     */ public class GridKit
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   
/*     */   public static String ensureDirectory(String a_currentDir, String a_subDir, String a_descr) throws IOException {
/*  31 */     if (a_currentDir == null || a_currentDir.length() < 1) {
/*  32 */       String currentDir = FileKit.getCurrentDir();
/*     */ 
/*     */       
/*  35 */       String workDir = FileKit.addSubDir(currentDir, a_subDir, true);
/*  36 */       File f = new File(workDir);
/*  37 */       if (!f.exists() && 
/*  38 */         !f.mkdirs()) {
/*  39 */         throw new RuntimeException("Creation of " + a_descr + " " + workDir + " failed!");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  45 */       return workDir;
/*     */     } 
/*     */     
/*  48 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static URLConnection getConnection(String a_url) throws Exception {
/*  54 */     URL url1 = new URL(a_url);
/*  55 */     URL url = new URL(url1.toExternalForm());
/*  56 */     URLConnection con = url.openConnection();
/*  57 */     con.setUseCaches(false);
/*  58 */     con.setDoInput(true);
/*  59 */     con.setDoOutput(true);
/*  60 */     return con;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VersionInfo isUpdateAvailable(String BASE_URL, String a_moduleName, String currentVersion) throws Exception {
/*  68 */     String url = BASE_URL + "getVersion=" + a_moduleName;
/*  69 */     addURLParameter(url, "version", currentVersion);
/*  70 */     URLConnection con = getConnection(url);
/*  71 */     ObjectInputStream ois = new ObjectInputStream(con.getInputStream());
/*  72 */     VersionInfo result = (VersionInfo)ois.readObject();
/*  73 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String addURLParameter(String a_requestURL, String a_key, long a_value) {
/*  78 */     String result = a_requestURL + "&" + a_key + "=" + a_value;
/*  79 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static String addURLParameter(String a_requestURL, String a_key, String a_value) {
/*  84 */     String result = a_requestURL + "&" + a_key + "=" + a_value;
/*  85 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String retrieveModule(String BASE_URL, VersionInfo a_versionInfo, String a_destDir) throws Exception {
/*  92 */     String filename = a_versionInfo.filenameOfLib;
/*  93 */     if (!getFile(BASE_URL, filename, a_destDir)) {
/*  94 */       return null;
/*     */     }
/*  96 */     return filename;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void updateModule(String a_filename, String a_workDir, String a_libDir) throws Exception {
/* 102 */     String libDir = a_libDir;
/*     */ 
/*     */     
/* 105 */     String sourceFileName = a_workDir + a_filename;
/* 106 */     FileKit.copyFile(sourceFileName, libDir);
/*     */ 
/*     */     
/* 109 */     if (!FileKit.deleteFile(sourceFileName));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean getFile(String BASE_URL, String a_sourceFilename, String a_targetDir) throws Exception {
/*     */     long offset;
/*     */     boolean append;
/* 117 */     String filename = FileKit.getFilename(a_sourceFilename);
/* 118 */     String destFilename = a_targetDir + filename;
/*     */ 
/*     */     
/* 121 */     File f = new File(destFilename);
/* 122 */     if (f.exists()) {
/* 123 */       offset = f.length();
/*     */     } else {
/*     */       
/* 126 */       offset = 0L;
/*     */     } 
/* 128 */     String a_url = BASE_URL + "download=";
/* 129 */     String requestURL = a_url + a_sourceFilename;
/*     */     
/* 131 */     requestURL = addURLParameter(requestURL, "offset", offset);
/* 132 */     URL url1 = new URL(requestURL);
/* 133 */     URL url = new URL(url1.toExternalForm());
/*     */     
/* 135 */     HttpURLConnection con = (HttpURLConnection)url.openConnection();
/*     */     
/* 137 */     con.setUseCaches(false);
/*     */     
/* 139 */     con.setDoOutput(true);
/*     */ 
/*     */     
/* 142 */     if (offset == 0L) {
/* 143 */       append = false;
/*     */     } else {
/*     */       
/* 146 */       append = true;
/*     */     } 
/* 148 */     InputStream in = con.getInputStream();
/* 149 */     ObjectInputStream sis = new ObjectInputStream(new BufferedInputStream(in, 5120));
/*     */ 
/*     */ 
/*     */     
/* 153 */     FileOutputStream fos = new FileOutputStream(destFilename, append);
/* 154 */     long currentOffset = 0L; try {
/*     */       Status s;
/* 156 */       int loopIndex = 0;
/*     */       do {
/* 158 */         s = (Status)sis.readObject();
/* 159 */         if (s.code == 0) {
/* 160 */           if (loopIndex == 0 && (
/* 161 */             s.buffer == null || s.buffer.length < 1)) {
/* 162 */             System.out.println("File already exists");
/* 163 */             return true;
/*     */           }
/*     */         
/*     */         }
/* 167 */         else if (s.code < 0) {
/* 168 */           throw new IOException(s.description);
/*     */         } 
/* 170 */         loopIndex++;
/* 171 */         fos.write(s.buffer);
/* 172 */         currentOffset += s.buffer.length;
/* 173 */       } while (s.code != 0);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 178 */     catch (SocketException sex) {
/* 179 */       System.err.println("Connection to server lost - file transfer interrupted (resum possible)");
/*     */       
/* 181 */       sex.printStackTrace();
/*     */       
/* 183 */       fos.close();
/* 184 */       return false;
/*     */     } 
/* 186 */     fos.close();
/* 187 */     System.out.println("File received: " + destFilename);
/* 188 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void updateModuleLibrary(String BASE_URL, String a_moduleName, String a_libDir, String a_workDir) throws Exception {
/*     */     String currentVersion;
/*     */     boolean isCoreModule;
/* 199 */     if (a_moduleName.equals("evolutionDistributed")) {
/*     */       
/* 201 */       filename = "evdistr.jar";
/* 202 */       isCoreModule = false;
/*     */     }
/* 204 */     else if (a_moduleName.equals("jgap")) {
/*     */       
/* 206 */       filename = "jgap.jar";
/* 207 */       isCoreModule = true;
/*     */     } else {
/*     */       
/* 210 */       throw new IllegalArgumentException("Unknown module " + a_moduleName);
/*     */     } 
/* 212 */     String filename = a_libDir + filename;
/*     */     
/* 214 */     if (!FileKit.existsFile(filename)) {
/* 215 */       currentVersion = "none";
/* 216 */       System.out.println(" File not found: " + filename);
/*     */ 
/*     */     
/*     */     }
/* 220 */     else if (isCoreModule) {
/* 221 */       currentVersion = FileKit.getVersionOfJGAP(filename);
/*     */     } else {
/*     */       
/* 224 */       currentVersion = FileKit.getVersionOfModule(filename);
/*     */ 
/*     */ 
/*     */       
/* 228 */       String str = FileKit.getVersionOfJGAP(filename);
/*     */     } 
/*     */ 
/*     */     
/* 232 */     VersionInfo versionInfo = isUpdateAvailable(BASE_URL, a_moduleName, currentVersion);
/*     */     
/* 234 */     if (versionInfo.currentVersion == null) {
/* 235 */       System.out.println("Module " + a_moduleName + " is unknown");
/*     */       return;
/*     */     } 
/* 238 */     if (!currentVersion.equals(versionInfo.currentVersion)) {
/* 239 */       System.out.println("Newer module " + a_moduleName + " with version " + versionInfo.currentVersion + " available");
/*     */ 
/*     */       
/* 242 */       String workDir = a_workDir;
/*     */       
/* 244 */       filename = retrieveModule(BASE_URL, versionInfo, workDir);
/* 245 */       if (filename != null)
/*     */       {
/* 247 */         updateModule(filename, workDir, a_libDir);
/*     */       }
/*     */     } else {
/*     */       
/* 251 */       System.out.println("Module " + a_moduleName + " with version " + currentVersion + " is up-to-date");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void updateJGAPLibrary(String BASE_URL, String a_libDir, String a_workDir) throws Exception {
/* 259 */     updateModuleLibrary(BASE_URL, "jgap", a_libDir, a_workDir);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\gri\\util\GridKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */