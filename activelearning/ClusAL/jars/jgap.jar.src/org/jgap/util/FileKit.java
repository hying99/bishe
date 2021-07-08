/*     */ package org.jgap.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Manifest;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileKit
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*  21 */   public static String fileseparator = System.getProperty("file.separator");
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
/*     */   public static void copyFile(String source, String dest) throws FileNotFoundException, IOException {
/*  37 */     copyFile(source, dest, 0);
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
/*     */   public static void copyFile(String source, String dest, int a_offset) throws FileNotFoundException, IOException {
/*  55 */     if (getFilename(dest).length() == 0) {
/*  56 */       dest = dest + getFilename(source);
/*     */     }
/*  58 */     File inputFile = new File(source);
/*  59 */     File outputFile = new File(dest);
/*     */ 
/*     */     
/*  62 */     FileInputStream in = new FileInputStream(inputFile);
/*  63 */     FileOutputStream out = new FileOutputStream(outputFile);
/*     */     
/*  65 */     int currentOffset = 0; int c;
/*  66 */     while ((c = in.read()) != -1) {
/*  67 */       if (currentOffset >= a_offset) {
/*  68 */         out.write(c);
/*     */       }
/*  70 */       currentOffset++;
/*     */     } 
/*  72 */     in.close();
/*  73 */     out.close();
/*     */   }
/*     */   
/*     */   public static String getFilename(String name_and_path) {
/*  77 */     return getFilename(name_and_path, fileseparator);
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
/*     */   public static String getFilename(String name_and_path, String fileseparator) {
/*  93 */     if (name_and_path == null) {
/*  94 */       return "";
/*     */     }
/*  96 */     String s = name_and_path;
/*     */     
/*  98 */     if (fileseparator.equals("/")) {
/*  99 */       s = s.replace('\\', '/');
/*     */     } else {
/*     */       
/* 102 */       s = s.replace('/', '\\');
/*     */     } 
/* 104 */     int p = s.lastIndexOf(fileseparator);
/* 105 */     if (p < 0)
/*     */     {
/* 107 */       return s;
/*     */     }
/*     */     
/* 110 */     s = s.substring(p + 1);
/* 111 */     if (s == null) {
/* 112 */       s = "";
/*     */     }
/* 114 */     return s;
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
/*     */   public static String getCurrentDir() throws IOException {
/* 127 */     File file = new File(".");
/* 128 */     return file.getCanonicalPath();
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
/*     */   public static String addSubDir(String dir, String subDir, boolean makeNice) {
/* 143 */     File f = new File(getConformPath(dir, true), subDir);
/* 144 */     String s = getConformPath(f.getAbsolutePath(), makeNice);
/* 145 */     return s;
/*     */   }
/*     */   
/*     */   public static String getConformPath(String path, boolean makeNice) {
/* 149 */     if (makeNice) {
/* 150 */       return getNiceURL(getConformPath(path), fileseparator);
/*     */     }
/*     */     
/* 153 */     return getConformPath(path);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getConformPath(String path) {
/* 158 */     return getConformPath(path, fileseparator);
/*     */   }
/*     */   
/*     */   public static String getConformPath(String path, String a_fileseparator) {
/* 162 */     String result = path;
/* 163 */     if (a_fileseparator.equals("/")) {
/* 164 */       result = removeDoubleSeparators(path.replace('\\', '/'));
/*     */     } else {
/*     */       
/* 167 */       result = removeDoubleSeparators(path.replace('/', '\\'));
/*     */     } 
/* 169 */     if (!result.endsWith(a_fileseparator)) {
/* 170 */       result = result + a_fileseparator;
/*     */     }
/* 172 */     return result;
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
/*     */   public static String getNiceURL(String url, String separator) {
/* 186 */     if (url == null) {
/* 187 */       return null;
/*     */     }
/* 189 */     if (url.length() == 0) {
/* 190 */       return separator;
/*     */     }
/* 192 */     int p = url.lastIndexOf(separator);
/* 193 */     if (p < url.length() - 1) {
/* 194 */       return removeDoubleSeparators(url + separator);
/*     */     }
/*     */     
/* 197 */     return removeDoubleSeparators(url);
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
/*     */   public static String removeDoubleSeparators(String dir) {
/*     */     while (true) {
/*     */       String sep;
/* 214 */       if (fileseparator.length() > 1) {
/* 215 */         sep = fileseparator;
/*     */       } else {
/*     */         
/* 218 */         sep = fileseparator + fileseparator;
/*     */       } 
/* 220 */       int p = dir.lastIndexOf(sep);
/* 221 */       if (p >= 0) {
/* 222 */         dir = dir.substring(0, p) + dir.substring(p + 1);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 228 */     return dir;
/*     */   }
/*     */   
/*     */   public static boolean directoryExists(String a_dir) {
/* 232 */     File f = new File(a_dir);
/* 233 */     return f.exists();
/*     */   }
/*     */   
/*     */   public static boolean existsFile(String a_filename) {
/* 237 */     File file = new File(getConformPath(a_filename));
/* 238 */     return file.exists();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean deleteFile(String a_filename) {
/* 248 */     File file = new File(getConformPath(a_filename));
/* 249 */     if (file.exists()) {
/* 250 */       return file.delete();
/*     */     }
/*     */     
/* 253 */     return false;
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
/*     */   public static ClassLoader loadJar(String a_filename) throws Exception {
/* 273 */     JarClassLoader cl = new JarClassLoader(a_filename);
/* 274 */     return cl;
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
/*     */   public static Manifest getManifestOfJar(String a_filename) throws Exception {
/* 289 */     URL url = new URL("jar:file:" + a_filename + "!/");
/* 290 */     JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
/* 291 */     Manifest manifest = jarConnection.getManifest();
/*     */ 
/*     */     
/* 294 */     jarConnection.getJarFile().close();
/* 295 */     return manifest;
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
/*     */   public static String getJGAPVersion(Manifest a_JGAPManifest) {
/* 307 */     Attributes attr = a_JGAPManifest.getMainAttributes();
/* 308 */     return attr.getValue("JGAP-Version");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getModuleVersion(Manifest a_JGAPManifest) {
/* 319 */     Attributes attr = a_JGAPManifest.getMainAttributes();
/* 320 */     return attr.getValue("Module-Version");
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
/*     */   public static String getVersionOfModule(String a_filename) throws Exception {
/* 335 */     Manifest mf = getManifestOfJar(a_filename);
/* 336 */     String version = getModuleVersion(mf);
/* 337 */     if (version == null) {
/* 338 */       version = "no version info found!";
/*     */     }
/* 340 */     return version;
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
/*     */   public static String getVersionOfJGAP(String a_filename) throws Exception {
/* 356 */     Manifest mf = getManifestOfJar(a_filename);
/* 357 */     String version = getJGAPVersion(mf);
/* 358 */     if (version == null) {
/* 359 */       version = "no version info found!";
/*     */     }
/* 361 */     return version;
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
/*     */   public static String toJarFileName(String a_filename) {
/* 375 */     String result = a_filename.replace('\\', '/');
/*     */ 
/*     */ 
/*     */     
/* 379 */     return result;
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
/*     */   public static void createDirectory(String a_dirname) throws IOException {
/* 393 */     File file = new File(a_dirname);
/* 394 */     if (file.exists()) {
/*     */       return;
/*     */     }
/* 397 */     if (!file.mkdirs()) {
/* 398 */       throw new IOException("Directory " + a_dirname + " could not be created!");
/*     */     }
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
/*     */   public static Vector readFile(String a_filename) throws IOException {
/* 416 */     Vector<String> v = new Vector();
/*     */     
/* 418 */     FileInputStream fin = new FileInputStream(a_filename);
/* 419 */     BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
/*     */     String thisLine;
/* 421 */     while ((thisLine = myInput.readLine()) != null) {
/* 422 */       v.add(thisLine);
/*     */     }
/* 424 */     return v;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\FileKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */