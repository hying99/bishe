/*     */ package jeans.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileUtil
/*     */ {
/*     */   public static String getExtension(File f) {
/*  44 */     return getExtension(f.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File getCurrentDir() {
/*  53 */     return new File(".");
/*     */   }
/*     */   
/*     */   public static String cmbPath(String path, String fname) {
/*  57 */     if (path.endsWith("/") || path.endsWith("\\")) {
/*  58 */       return normPath(path + fname);
/*     */     }
/*  60 */     return normPath(path + File.separator + fname);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String normPath(String path) {
/*  65 */     if (File.separatorChar == '/') {
/*  66 */       return path.replace('\\', File.separatorChar);
/*     */     }
/*  68 */     return path.replace('/', File.separatorChar);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getCurrentDirectory() throws IOException {
/*  73 */     return getCurrentDir().getCanonicalPath();
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
/*     */   public static String addExtension(String fname, String ext) {
/*  85 */     String myext = getExtension(fname);
/*  86 */     if (myext == null || !myext.equals(ext)) return fname + "." + ext; 
/*  87 */     return fname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getExtension(String s) {
/*  98 */     String ext = null;
/*  99 */     int i = s.lastIndexOf('.');
/* 100 */     if (i > 0 && i < s.length() - 1) {
/* 101 */       ext = s.substring(i + 1).toLowerCase();
/*     */     }
/* 103 */     return ext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getName(String s) {
/* 114 */     String name = s;
/* 115 */     int i = s.lastIndexOf('.');
/* 116 */     if (i > 0 && i < s.length() - 1) {
/* 117 */       name = s.substring(0, i);
/*     */     }
/* 119 */     return name;
/*     */   }
/*     */   
/*     */   public static void delete(String fname) {
/* 123 */     File file = new File(fname);
/* 124 */     file.delete();
/*     */   }
/*     */   
/*     */   public static void mkdir(String fname) {
/* 128 */     File file = new File(fname);
/* 129 */     file.mkdirs();
/*     */   }
/*     */   
/*     */   public static boolean fileExists(String fname) {
/* 133 */     File file = new File(fname);
/* 134 */     return file.exists();
/*     */   }
/*     */   
/*     */   public static boolean isNewerOrEqual(String newFile, String oldFile) {
/* 138 */     File newF = new File(newFile);
/* 139 */     File oldF = new File(oldFile);
/* 140 */     if (!newF.exists()) return false; 
/* 141 */     if (!oldF.exists()) return true; 
/* 142 */     return (newF.lastModified() >= oldF.lastModified());
/*     */   }
/*     */   
/*     */   public static String removePath(String s) {
/* 146 */     String name = s;
/* 147 */     int i = s.lastIndexOf(File.separatorChar);
/* 148 */     if (i >= 0 && i < s.length() - 1) {
/* 149 */       name = s.substring(i + 1);
/*     */     }
/* 151 */     return name;
/*     */   }
/*     */   
/*     */   public static String getPath(String s) {
/* 155 */     int i = s.lastIndexOf(File.separatorChar);
/* 156 */     if (i >= 0 && i < s.length() - 1) {
/* 157 */       return trimDirSeparator(s.substring(0, i));
/*     */     }
/* 159 */     return null;
/*     */   }
/*     */   
/*     */   public static String trimDirSeparator(String s) {
/* 163 */     int len = s.length();
/* 164 */     if (len > 0 && s.charAt(len - 1) == File.separatorChar)
/* 165 */       return s.substring(0, len - 1); 
/* 166 */     return s;
/*     */   }
/*     */   
/*     */   public static String relativePath(String abs, String prefix) {
/* 170 */     int pos = abs.indexOf(prefix);
/* 171 */     int from = prefix.length() + 1;
/* 172 */     if (pos == 0 && from < abs.length()) return abs.substring(from); 
/* 173 */     return abs;
/*     */   }
/*     */   
/*     */   public static boolean isAbsolutePath(String fname) {
/* 177 */     if (fname.length() > 0) {
/* 178 */       if (fname.charAt(0) == File.separatorChar) return true; 
/* 179 */       if (fname.length() >= 3 && fname.charAt(1) == ':' && (fname
/* 180 */         .charAt(2) == File.separatorChar || fname.charAt(2) == '/')) return true; 
/* 181 */       return false;
/*     */     } 
/* 183 */     return false;
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
/*     */   public static String[] dirList(String dirname) throws FileNotFoundException {
/* 197 */     File directory = new File(dirname);
/* 198 */     if (directory != null && directory.isDirectory()) {
/* 199 */       return directory.list();
/*     */     }
/* 201 */     throw new FileNotFoundException();
/*     */   }
/*     */   
/*     */   public static ArrayList recursiveFind(File dir, String pattern) throws IOException {
/* 205 */     ArrayList res = new ArrayList();
/* 206 */     if (dir.isDirectory()) recursiveFindAll(dir, pattern, res); 
/* 207 */     return res;
/*     */   }
/*     */   
/*     */   public static void recursiveFindAll(File dir, String pattern, ArrayList<String> res) throws IOException {
/* 211 */     String[] list = dir.list();
/* 212 */     if (list == null)
/* 213 */       return;  for (int i = 0; i < list.length; i++) {
/* 214 */       String full = cmbPath(dir.getCanonicalPath(), list[i]);
/* 215 */       if (list[i].endsWith(pattern)) {
/* 216 */         res.add(full);
/*     */       }
/* 218 */       File file = new File(full);
/* 219 */       if (file.isDirectory()) recursiveFindAll(file, pattern, res);
/*     */     
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
/*     */   public static String[] dirList(String dirname, String extension) throws FileNotFoundException {
/* 234 */     File directory = new File(dirname);
/* 235 */     if (directory != null && directory.isDirectory()) {
/* 236 */       return directory.list(new ExtensionFilter(extension));
/*     */     }
/* 238 */     throw new FileNotFoundException();
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
/*     */   public static String[] readTextFile(String filename) throws FileNotFoundException, IOException {
/* 252 */     Vector<String> lines = new Vector();
/*     */ 
/*     */     
/* 255 */     BufferedReader in = new BufferedReader(new FileReader(filename));
/*     */     while (true) {
/* 257 */       String line = in.readLine();
/* 258 */       if (line != null) lines.addElement(line); 
/* 259 */       if (line == null) {
/* 260 */         in.close();
/* 261 */         String[] text = new String[lines.size()];
/* 262 */         int idx = 0;
/* 263 */         for (Enumeration<String> e = lines.elements(); e.hasMoreElements();) {
/* 264 */           text[idx++] = e.nextElement();
/*     */         }
/* 266 */         return text;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\FileUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */