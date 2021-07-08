/*     */ package org.jgap.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
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
/*     */ public class PluginDiscoverer
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private static final boolean DEBUG = false;
/*     */   private List m_classpathFolders;
/*     */   private List m_classpathJars;
/*     */   private String m_jarFile;
/*     */   
/*     */   public PluginDiscoverer() {
/*  50 */     init();
/*  51 */     String classpath = System.getProperty("java.class.path");
/*  52 */     StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
/*  53 */     while (st.hasMoreTokens()) {
/*  54 */       String item = st.nextToken();
/*  55 */       File f = new File(item);
/*  56 */       if (item.toLowerCase().endsWith(".jar") && f.isFile()) {
/*  57 */         this.m_classpathJars.add(item); continue;
/*     */       } 
/*  59 */       if (f.isDirectory()) {
/*  60 */         this.m_classpathFolders.add(item);
/*     */       }
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
/*     */   public PluginDiscoverer(String a_jarFile) {
/*  73 */     init();
/*  74 */     this.m_jarFile = a_jarFile;
/*  75 */     this.m_classpathJars.add(this.m_jarFile);
/*     */   }
/*     */   
/*     */   private void init() {
/*  79 */     this.m_classpathFolders = new Vector();
/*  80 */     this.m_classpathJars = new Vector();
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
/*     */   private String checkIfClassMatches(String a_jarFilename, Class a_interfaceClass, String a_testClass) {
/*  95 */     if (a_testClass.toLowerCase().endsWith(".class")) {
/*  96 */       a_testClass = a_testClass.substring(0, a_testClass.length() - 6);
/*     */     }
/*     */     
/*  99 */     a_testClass = a_testClass.replace('\\', '.').replace('/', '.');
/*     */     
/* 101 */     while (a_testClass.startsWith(".")) {
/* 102 */       a_testClass = a_testClass.substring(1);
/*     */     }
/* 104 */     if (a_testClass.indexOf('$') != -1)
/*     */     {
/* 106 */       return null;
/*     */     }
/*     */     try {
/*     */       ClassLoader cl;
/* 110 */       if (a_jarFilename == null) {
/* 111 */         cl = getClass().getClassLoader();
/*     */       } else {
/*     */         
/* 114 */         cl = new JarClassLoader(a_jarFilename);
/*     */       } 
/* 116 */       Class<?> testClassObj = Class.forName(a_testClass, false, cl);
/*     */       
/* 118 */       if (a_interfaceClass.isAssignableFrom(testClassObj)) {
/* 119 */         if (testClassObj.isInterface())
/*     */         {
/* 121 */           return null;
/*     */         }
/* 123 */         if ((testClassObj.getModifiers() & 0x400) > 0)
/*     */         {
/*     */           
/* 126 */           return null;
/*     */         }
/* 128 */         return a_testClass;
/*     */       } 
/* 130 */     } catch (UnsatisfiedLinkError ule) {
/*     */ 
/*     */     
/*     */     }
/* 134 */     catch (IllegalAccessError e) {
/*     */ 
/*     */     
/*     */     }
/* 138 */     catch (ClassNotFoundException cnfe) {
/*     */ 
/*     */     
/*     */     }
/* 142 */     catch (NoClassDefFoundError nex) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 147 */     return null;
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
/*     */   public List findImplementingClasses(String a_fullInterfaceName) throws ClassNotFoundException {
/* 163 */     Class<?> interfaceToLookFor = Class.forName(a_fullInterfaceName);
/* 164 */     return findImplementingClasses(interfaceToLookFor);
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
/*     */   public List findImplementingClasses(Class a_intrface) {
/* 176 */     List<String> result = new Vector();
/*     */     
/* 178 */     String s = null;
/*     */     
/*     */     try {
/* 181 */       File f = new File(".");
/* 182 */       s = f.getCanonicalPath();
/* 183 */       s = FileKit.getConformPath(s, true);
/* 184 */     } catch (IOException iex) {
/* 185 */       throw new RuntimeException("Unable to determine current directory", iex);
/*     */     } 
/* 187 */     Iterator<String> i = this.m_classpathJars.iterator();
/* 188 */     while (i.hasNext()) {
/* 189 */       String filename = i.next();
/* 190 */       filename = FileKit.getConformPath(filename, true);
/*     */ 
/*     */ 
/*     */       
/* 194 */       if (filename.startsWith(s)) {
/*     */         try {
/* 196 */           JarFile jar = new JarFile(filename);
/* 197 */           Enumeration<JarEntry> item = jar.entries();
/* 198 */           while (item.hasMoreElements()) {
/* 199 */             JarEntry entry = item.nextElement();
/* 200 */             String name = entry.getName();
/* 201 */             if (name.toLowerCase().endsWith(".class")) {
/* 202 */               String classname = checkIfClassMatches(filename, a_intrface, name);
/* 203 */               if (classname != null) {
/* 204 */                 result.add(classname);
/*     */               }
/*     */             } 
/*     */           } 
/* 208 */         } catch (IOException e) {
/* 209 */           System.out.println("Unable to open jar " + filename);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 214 */     i = this.m_classpathFolders.iterator();
/* 215 */     while (i.hasNext()) {
/* 216 */       String folder = i.next();
/* 217 */       System.err.println(folder);
/* 218 */       findImplementingClasses0(a_intrface, result, folder, "");
/*     */     } 
/* 220 */     return result;
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
/*     */   private void findImplementingClasses0(Class a_intrface, List a_result, String a_base, String a_path) {
/* 238 */     a_result.addAll(findImplementingClasses(a_intrface, a_base, a_path));
/* 239 */     File f = new File(a_base + File.separator + a_path);
/* 240 */     if (!f.isDirectory()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 251 */     File[] matches = f.listFiles(new DirectoryFilter());
/* 252 */     for (int i = 0; i < matches.length; i++) {
/* 253 */       String folder = a_path + File.separator + matches[i].getName();
/* 254 */       findImplementingClasses0(a_intrface, a_result, a_base, folder);
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
/*     */   public List findImplementingClasses(Class a_intrface, String a_base, String a_path) {
/* 270 */     List<String> result = new Vector();
/* 271 */     File f = new File(a_base + File.separator + a_path);
/* 272 */     if (!f.isDirectory()) {
/* 273 */       return result;
/*     */     }
/* 275 */     File[] matches = f.listFiles(new ClassFilter());
/* 276 */     for (int i = 0; i < matches.length; i++) {
/* 277 */       String classname = a_path + File.separator + matches[i].getName();
/* 278 */       classname = checkIfClassMatches(null, a_intrface, classname);
/* 279 */       if (classname != null) {
/* 280 */         result.add(classname);
/*     */       }
/*     */     } 
/* 283 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class ClassFilter
/*     */     implements FilenameFilter
/*     */   {
/*     */     public boolean accept(File a_dir, String a_name) {
/* 292 */       return (a_name != null && a_name.toLowerCase().endsWith(".class"));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public class DirectoryFilter
/*     */     implements FilenameFilter
/*     */   {
/*     */     public boolean accept(File a_dir, String a_name) {
/* 301 */       return (a_dir != null && (new File(a_dir.getPath() + File.separator + a_name)).isDirectory());
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
/*     */   public static void main(String[] args) throws Exception {
/* 316 */     PluginDiscoverer discoverer = new PluginDiscoverer();
/*     */ 
/*     */ 
/*     */     
/* 320 */     List plugins = discoverer.findImplementingClasses("org.jgap.INaturalSelector");
/*     */     
/* 322 */     System.out.println();
/* 323 */     int size = plugins.size();
/* 324 */     System.out.println("" + size + " plugin" + ((size == 1) ? "" : "s") + " discovered" + ((size == 0) ? "" : ":"));
/*     */ 
/*     */     
/* 327 */     for (int i = 0; i < size; i++) {
/* 328 */       System.out.println(plugins.get(i));
/*     */     }
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
/* 341 */     System.exit(0);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\PluginDiscoverer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */