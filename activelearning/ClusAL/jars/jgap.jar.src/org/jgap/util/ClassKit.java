/*     */ package org.jgap.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.zip.ZipEntry;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassKit
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.10 $";
/*  23 */   private static transient Logger LOGGER = Logger.getLogger(ClassKit.class);
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  27 */     File f = new File(".");
/*     */     
/*  29 */     getPlugins(f.getCanonicalPath() + "\\lib");
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
/*     */   public static List find(String a_tosubclassname) {
/*     */     try {
/*  53 */       List result = new Vector();
/*  54 */       Class<?> tosubclass = Class.forName(a_tosubclassname);
/*  55 */       Package[] pcks = Package.getPackages();
/*     */       
/*  57 */       for (int i = 0; i < pcks.length; i++) {
/*  58 */         List subresult = find(pcks[i].getName(), tosubclass);
/*  59 */         result.addAll(subresult);
/*     */       } 
/*     */ 
/*     */       
/*  63 */       return result;
/*     */     }
/*  65 */     catch (ClassNotFoundException ex) {
/*  66 */       LOGGER.warn("Class " + a_tosubclassname + " not found!");
/*  67 */       return null;
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
/*     */   public static List find(String a_pckname, String a_tosubclassname) throws ClassNotFoundException {
/*  84 */     Class<?> tosubclass = Class.forName(a_tosubclassname);
/*  85 */     return find(a_pckname, tosubclass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List find(String a_pckgname, Class a_tosubclass) {
/*  96 */     List result = new Vector();
/*     */     
/*  98 */     String name = a_pckgname;
/*  99 */     if (!name.startsWith("/")) {
/* 100 */       name = "/" + name;
/*     */     }
/* 102 */     name = name.replace('.', '/');
/*     */     
/* 104 */     URL url = ClassKit.class.getResource(name);
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
/* 119 */     if (url == null) {
/* 120 */       return result;
/*     */     }
/* 122 */     return find(url, a_pckgname, a_tosubclass);
/*     */   }
/*     */ 
/*     */   
/*     */   public static List find(URL a_url, String a_pckgname, Class a_tosubclass) {
/* 127 */     List<String> result = new Vector();
/* 128 */     File directory = new File(a_url.getFile());
/* 129 */     if (directory.exists()) {
/*     */       
/* 131 */       String[] files = directory.list();
/* 132 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 134 */         if (files[i].endsWith(".class"))
/*     */         {
/* 136 */           String classname = files[i].substring(0, files[i].length() - 6);
/*     */           
/*     */           try {
/* 139 */             Class<?> c = Class.forName(a_pckgname + "." + classname);
/* 140 */             if (implementsInterface(c, a_tosubclass) || extendsClass(c, a_tosubclass))
/*     */             {
/* 142 */               result.add(a_pckgname + "." + classname);
/*     */             }
/*     */           }
/* 145 */           catch (ClassNotFoundException cnfex) {
/* 146 */             LOGGER.error(cnfex);
/*     */           
/*     */           }
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 159 */       findInJar(result, a_url, a_tosubclass);
/*     */     } 
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void findInJar(List<String> a_result, URL a_url, Class a_tosubclass) {
/*     */     try {
/* 169 */       JarURLConnection conn = (JarURLConnection)a_url.openConnection();
/* 170 */       String starts = conn.getEntryName();
/* 171 */       JarFile jfile = conn.getJarFile();
/* 172 */       Enumeration<JarEntry> e = jfile.entries();
/* 173 */       while (e.hasMoreElements()) {
/* 174 */         ZipEntry entry = e.nextElement();
/* 175 */         String entryname = entry.getName();
/* 176 */         if (entryname.startsWith(starts) && entryname.lastIndexOf('/') <= starts.length() && entryname.endsWith(".class")) {
/*     */ 
/*     */           
/* 179 */           String classname = entryname.substring(0, entryname.length() - 6);
/* 180 */           if (classname.startsWith("/")) {
/* 181 */             classname = classname.substring(1);
/*     */           }
/* 183 */           classname = classname.replace('/', '.');
/*     */           
/*     */           try {
/* 186 */             Class<?> c = Class.forName(classname);
/* 187 */             if (implementsInterface(c, a_tosubclass) || extendsClass(c, a_tosubclass))
/*     */             {
/*     */ 
/*     */               
/* 191 */               a_result.add(classname);
/*     */             }
/*     */           }
/* 194 */           catch (ClassNotFoundException cnfex) {
/* 195 */             LOGGER.error(cnfex);
/*     */           }
/*     */         
/*     */         } 
/*     */       } 
/* 200 */     } catch (IOException ioex) {
/* 201 */       LOGGER.error(ioex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean implementsInterface(Class a_o, Class a_clazz) {
/* 207 */     Class[] interfaces = a_o.getInterfaces();
/* 208 */     for (int i = 0; i < interfaces.length; i++) {
/* 209 */       Class c = interfaces[i];
/* 210 */       if (c.equals(a_clazz)) {
/* 211 */         return true;
/*     */       }
/*     */     } 
/* 214 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean extendsClass(Class<?> a_o, Class a_clazz) {
/* 218 */     if (a_clazz.getName().equals(a_o.getName())) {
/* 219 */       return false;
/*     */     }
/* 221 */     if (a_clazz.isAssignableFrom(a_o)) {
/* 222 */       return true;
/*     */     }
/*     */     
/* 225 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void getPlugins(String a_directory) {
/* 231 */     File modulePath = new File(a_directory);
/*     */     
/* 233 */     File[] jarFiles = modulePath.listFiles(new ExtensionsFilter("jar", false));
/* 234 */     URL[] urls = new URL[jarFiles.length + 1];
/* 235 */     int i = 0;
/* 236 */     for (; i < jarFiles.length; i++) {
/*     */       try {
/* 238 */         urls[i] = jarFiles[i].toURL();
/*     */       }
/* 240 */       catch (Exception ex) {}
/*     */     } 
/*     */     
/*     */     try {
/* 244 */       urls[i] = modulePath.toURL();
/*     */     }
/* 246 */     catch (Exception ex) {}
/*     */ 
/*     */     
/* 249 */     ClassLoader ucl = new URLClassLoader(urls);
/*     */     
/* 251 */     Vector classes = new Vector();
/* 252 */     long startTime = System.currentTimeMillis();
/* 253 */     addClasses(classes, modulePath, "");
/* 254 */     LOGGER.info("Found plugin classes in: " + (System.currentTimeMillis() - startTime) + " milliseconds");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 259 */     Enumeration<E> e = classes.elements();
/* 260 */     while (e.hasMoreElements()) {
/*     */       try {
/* 262 */         String name = e.nextElement().toString();
/*     */         
/* 264 */         LOGGER.info("Found plugin class: " + name);
/*     */       }
/* 266 */       catch (Throwable ex) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addClasses(Vector a_v, File a_path, String a_name) {
/* 274 */     addClassesFile(a_v, a_path, a_name);
/* 275 */     addClassesJar(a_v, a_path);
/*     */   }
/*     */   
/*     */   public static void addClassesJar(Vector<String> a_v, File a_path) {
/* 279 */     File[] files = a_path.listFiles(new ExtensionsFilter("jar", false));
/* 280 */     for (int i = 0; i < files.length; i++) {
/*     */       try {
/* 282 */         JarFile jar = new JarFile(files[i]);
/*     */         
/* 284 */         Enumeration<JarEntry> e = jar.entries();
/* 285 */         while (e.hasMoreElements()) {
/* 286 */           String wa = e.nextElement().toString();
/* 287 */           if (wa.endsWith(".class") && wa.indexOf("$") == -1) {
/* 288 */             a_v.add(wa.substring(0, wa.length() - 6).replace('/', '.'));
/*     */           }
/*     */         }
/*     */       
/* 292 */       } catch (Exception ex) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void addClassesFile(Vector<String> a_v, File a_path, String a_name) {
/* 301 */     File[] files = a_path.listFiles(new ExtensionsFilter("class", true));
/* 302 */     for (int i = 0; i < files.length; i++) {
/* 303 */       if (files[i].isDirectory()) {
/* 304 */         addClassesFile(a_v, files[i], a_name + files[i].getName() + ".");
/*     */       }
/* 306 */       else if (files[i].getName().indexOf("$") == -1) {
/* 307 */         a_v.add(a_name + files[i].getName().substring(0, files[i].getName().length() - 6));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static class ExtensionsFilter
/*     */     implements FilenameFilter
/*     */   {
/*     */     private String m_ext;
/*     */     
/*     */     public ExtensionsFilter(String a_extension, boolean a_dummy) {
/* 319 */       this.m_ext = a_extension;
/*     */     }
/*     */     
/*     */     public boolean accept(File a_dir, String a_name) {
/* 323 */       return (a_name != null && a_name.endsWith("." + this.m_ext));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\ClassKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */