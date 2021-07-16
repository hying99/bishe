/*    */ package jeans.resource;
/*    */ 
/*    */ import java.applet.AudioClip;
/*    */ import java.awt.Image;
/*    */ import java.net.MalformedURLException;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MediaCache
/*    */ {
/* 32 */   private Hashtable sounds = null;
/* 33 */   private Hashtable images = null;
/*    */   private boolean m_SoundEnabled = true;
/*    */   private boolean m_SoundLoaded = false;
/* 36 */   private static MediaCache instance = null;
/*    */   
/*    */   public static MediaCache getInstance() {
/* 39 */     if (instance == null)
/* 40 */       instance = new MediaCache(); 
/* 41 */     return instance;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void showImagePaths() {
/* 48 */     MediaInterface interf = MediaInterface.getInstance();
/* 49 */     for (Enumeration<String> e = this.images.keys(); e.hasMoreElements(); ) {
/* 50 */       String name = e.nextElement();
/* 51 */       interf.showImagePath(name);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Image getImage(String fname) {
/* 56 */     if (this.images == null) this.images = new Hashtable<>(); 
/* 57 */     if (this.images.containsKey(fname)) {
/* 58 */       return (Image)this.images.get(fname);
/*    */     }
/* 60 */     return loadImage(fname, fname);
/*    */   }
/*    */ 
/*    */   
/*    */   public Image loadImage(String fname, String key) {
/* 65 */     if (this.images == null) this.images = new Hashtable<>(); 
/* 66 */     Image image = MediaInterface.getInstance().loadImage(fname);
/* 67 */     this.images.put(key, image);
/* 68 */     return image;
/*    */   }
/*    */   
/*    */   public void setSoundEnabled(boolean ena) {
/* 72 */     this.m_SoundEnabled = ena;
/*    */   }
/*    */   
/*    */   public boolean isSoundEnabled() {
/* 76 */     return this.m_SoundEnabled;
/*    */   }
/*    */   
/*    */   public void setSoundsLoaded(boolean load) {
/* 80 */     this.m_SoundLoaded = load;
/*    */   }
/*    */   
/*    */   public boolean isSoundsLoaded() {
/* 84 */     return this.m_SoundLoaded;
/*    */   }
/*    */   
/*    */   public void playSound(String key) {
/* 88 */     if (isSoundEnabled()) getSound(key).play(); 
/*    */   }
/*    */   
/*    */   public AudioClip getSound(String key) {
/* 92 */     return (AudioClip)this.sounds.get(key);
/*    */   }
/*    */   
/*    */   public AudioClip loadSound(String fname, String key) throws MalformedURLException {
/* 96 */     if (this.sounds == null) this.sounds = new Hashtable<>(); 
/* 97 */     AudioClip sound = MediaInterface.getInstance().loadAudioClip(fname);
/* 98 */     if (sound != null) this.sounds.put(key, sound); 
/* 99 */     return sound;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\resource\MediaCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */