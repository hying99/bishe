/*     */ package jeans.resource;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletContext;
/*     */ import java.applet.AudioClip;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class MediaInterface
/*     */ {
/*  40 */   private static MediaInterface instance = null;
/*  41 */   protected Applet applet = null;
/*  42 */   protected Class jarBaseClass = null;
/*  43 */   protected URL soundBase = null;
/*  44 */   protected String imageBase = null;
/*  45 */   protected String userDir = null;
/*  46 */   protected Hashtable table = new Hashtable<>();
/*  47 */   protected int javaVersion = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MediaInterface getInstance() {
/*  54 */     if (instance == null)
/*  55 */       instance = new MediaInterface(); 
/*  56 */     return instance;
/*     */   }
/*     */   
/*     */   public int getJavaVersion() {
/*  60 */     if (this.javaVersion == 0) {
/*  61 */       int major = 0, minor = 0;
/*  62 */       String version = System.getProperty("java.version");
/*  63 */       StringTokenizer tokens = new StringTokenizer(version, ".");
/*     */       try {
/*  65 */         if (tokens.hasMoreTokens())
/*  66 */           major = Integer.parseInt(tokens.nextToken()); 
/*  67 */         if (tokens.hasMoreTokens())
/*  68 */           minor = Integer.parseInt(tokens.nextToken()); 
/*  69 */       } catch (NumberFormatException numberFormatException) {}
/*  70 */       this.javaVersion = major * 10000 + minor;
/*     */     } 
/*  72 */     return this.javaVersion;
/*     */   }
/*     */   
/*     */   public boolean supportSounds() {
/*  76 */     return (inApplet() || getJavaVersion() >= 10002);
/*     */   }
/*     */   
/*     */   public AudioClip loadAudioClip(String name) throws MalformedURLException {
/*  80 */     if (this.soundBase == null) initSoundBase(); 
/*  81 */     if (inApplet()) {
/*  82 */       System.out.println("Sound base: " + this.soundBase);
/*  83 */       System.out.println("Name: " + name);
/*  84 */       return this.applet.getAudioClip(this.soundBase, name);
/*     */     } 
/*  86 */     if (getJavaVersion() >= 10002) {
/*  87 */       URL url = new URL(this.soundBase, name);
/*  88 */       return Applet.newAudioClip(url);
/*     */     } 
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream openStream(String fname) throws IOException {
/*     */     try {
/*  97 */       if (inApplet()) {
/*  98 */         URL url = new URL(this.applet.getDocumentBase(), fname);
/*  99 */         return url.openStream();
/* 100 */       }  if (inJar()) {
/* 101 */         URL url = this.jarBaseClass.getResource(fname);
/* 102 */         return url.openStream();
/*     */       } 
/* 104 */       return new FileInputStream(fname);
/*     */     }
/* 106 */     catch (MalformedURLException e) {
/* 107 */       throw new IOException("Can't open URL: " + fname);
/*     */     } 
/*     */   }
/*     */   
/*     */   public OutputStream makeStream(String fname) throws IOException {
/* 112 */     return new FileOutputStream(fname);
/*     */   }
/*     */   
/*     */   public Image loadImage(String name) {
/* 116 */     String fname = name;
/* 117 */     if (this.imageBase != null) fname = this.imageBase + File.separator + name; 
/* 118 */     if (inApplet())
/* 119 */       return this.applet.getImage(this.applet.getCodeBase(), fname); 
/* 120 */     if (inJar()) {
/* 121 */       URL url = this.jarBaseClass.getResource(fname);
/* 122 */       return Toolkit.getDefaultToolkit().getImage(url);
/*     */     } 
/* 124 */     return Toolkit.getDefaultToolkit().getImage(fname);
/*     */   }
/*     */ 
/*     */   
/*     */   public void showImagePath(String name) {
/* 129 */     String fname = name;
/* 130 */     if (this.imageBase != null) fname = this.imageBase + File.separator + name; 
/* 131 */     if (inApplet()) {
/* 132 */       System.out.println("[APPLET] " + fname);
/* 133 */     } else if (inJar()) {
/* 134 */       System.out.println("[JAR] " + fname);
/*     */     } else {
/* 136 */       System.out.println(fname);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showRelativeURL(String relative, String target) throws IOException {
/* 141 */     URL url = null;
/*     */     try {
/* 143 */       url = getRelativeURL(relative);
/* 144 */     } catch (MalformedURLException e) {
/* 145 */       throw new IOException("Malformed URL: " + relative);
/*     */     } 
/* 147 */     if (inApplet()) {
/* 148 */       AppletContext context = this.applet.getAppletContext();
/* 149 */       context.showDocument(url, target);
/*     */     } else {
/* 151 */       Runtime runtime = Runtime.getRuntime();
/* 152 */       String[] args = new String[2];
/* 153 */       args[0] = "start";
/* 154 */       args[1] = url.toExternalForm();
/*     */       try {
/* 156 */         runtime.exec(args);
/* 157 */       } catch (IOException e) {
/* 158 */         args[0] = "netscape";
/* 159 */         runtime.exec(args);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public URL getBaseURL() throws IOException, MalformedURLException {
/* 165 */     if (inApplet()) return this.applet.getCodeBase(); 
/* 166 */     return new URL("file:" + getUserDir() + "/");
/*     */   }
/*     */   
/*     */   public URL getRelativeURL(String dir) throws IOException, MalformedURLException {
/* 170 */     if (dir == null) return getBaseURL(); 
/* 171 */     return new URL(getBaseURL(), dir);
/*     */   }
/*     */   
/*     */   public String getUserDir() {
/* 175 */     if (this.userDir == null) this.userDir = System.getProperty("user.dir"); 
/* 176 */     return this.userDir;
/*     */   }
/*     */   
/*     */   public void setSoundDirectory(String dir) throws IOException, MalformedURLException {
/* 180 */     if (inApplet()) { this.soundBase = getRelativeURL(dir + '/'); }
/* 181 */     else { this.soundBase = getRelativeURL(dir + '/'); }
/*     */   
/*     */   }
/*     */   public void setImageDirectory(String dir) {
/* 185 */     this.imageBase = dir;
/*     */   }
/*     */   
/*     */   public void setDirectory(String dir) throws IOException, MalformedURLException {
/* 189 */     setSoundDirectory(dir);
/* 190 */     setImageDirectory(dir);
/*     */   }
/*     */   
/*     */   public void setJarBase(Class cl) {
/* 194 */     this.jarBaseClass = cl;
/*     */   }
/*     */   
/*     */   public boolean inJar() {
/* 198 */     return (this.jarBaseClass != null);
/*     */   }
/*     */   
/*     */   public void setApplet(Applet applet) {
/* 202 */     this.applet = applet;
/*     */   }
/*     */   
/*     */   public boolean inApplet() {
/* 206 */     return (this.applet != null);
/*     */   }
/*     */   
/*     */   protected void initSoundBase() {
/*     */     try {
/* 211 */       setSoundDirectory(null);
/* 212 */     } catch (IOException iOException) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\resource\MediaInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */