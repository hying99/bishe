/*    */ package jeans.resource;
/*    */ 
/*    */ import java.applet.Applet;
/*    */ import java.applet.AudioClip;
/*    */ import java.io.File;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SoundList
/*    */ {
/*    */   Applet applet;
/*    */   URL baseURL;
/* 40 */   Hashtable table = new Hashtable<>();
/*    */   
/* 42 */   private static SoundList instance = null;
/*    */   
/*    */   public static SoundList getInstance() {
/* 45 */     if (instance == null)
/* 46 */       instance = new SoundList(); 
/* 47 */     return instance;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setApplet(Applet applet) {
/* 54 */     this.applet = applet;
/*    */   }
/*    */   
/*    */   public void setDirectory(URL codeBase, String dir) {
/*    */     try {
/* 59 */       this.baseURL = new URL(codeBase, dir);
/* 60 */     } catch (MalformedURLException malformedURLException) {}
/*    */   }
/*    */   
/*    */   public void getDirectory(String dir) {
/*    */     try {
/* 65 */       URL crDir = (new File(".")).toURL();
/* 66 */       this.baseURL = new URL(crDir, dir);
/* 67 */     } catch (MalformedURLException malformedURLException) {}
/*    */   }
/*    */ 
/*    */   
/*    */   public void startLoading(String relativeURL) throws MalformedURLException {
/* 72 */     AudioClip audioClip = null;
/* 73 */     if (this.applet == null) {
/* 74 */       audioClip = Applet.newAudioClip(new URL(this.baseURL, relativeURL));
/*    */     } else {
/* 76 */       audioClip = this.applet.getAudioClip(this.baseURL, relativeURL);
/*    */     } 
/* 78 */     if (audioClip == null) {
/* 79 */       System.out.println("Error loading audio clip: " + relativeURL);
/*    */     }
/* 81 */     putClip(audioClip, relativeURL);
/*    */   }
/*    */   
/*    */   public AudioClip getClip(String relativeURL) {
/* 85 */     return (AudioClip)this.table.get(relativeURL);
/*    */   }
/*    */   
/*    */   private void putClip(AudioClip clip, String relativeURL) {
/* 89 */     this.table.put(relativeURL, clip);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\resource\SoundList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */