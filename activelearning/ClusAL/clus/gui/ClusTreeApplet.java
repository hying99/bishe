/*    */ package clus.gui;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import javax.swing.JApplet;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.border.EtchedBorder;
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
/*    */ public class ClusTreeApplet
/*    */   extends JApplet
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected JFrame m_Frame;
/*    */   protected JButton m_Launch;
/*    */   
/*    */   public void init() {
/* 42 */     JPanel panel = new JPanel();
/* 43 */     panel.setLayout(new BorderLayout());
/* 44 */     panel.setBorder(new EtchedBorder());
/* 45 */     String name = getParameter("Label");
/* 46 */     panel.add(this.m_Launch = new JButton((name == null) ? "Show" : name));
/* 47 */     this.m_Launch.addActionListener(new MyClick());
/* 48 */     setContentPane(panel);
/*    */   }
/*    */   
/*    */   private class MyClick
/*    */     implements ActionListener {
/*    */     public void actionPerformed(ActionEvent evt) {
/* 54 */       if (ClusTreeApplet.this.m_Frame == null) {
/* 55 */         ClusTreeApplet.this.m_Launch.setText("Loading Tree...");
/* 56 */         ClusTreeApplet.this.m_Launch.setEnabled(false);
/* 57 */         ClusTreeApplet.MyThread thread = new ClusTreeApplet.MyThread();
/* 58 */         thread.start();
/*    */       } else {
/* 60 */         ClusTreeApplet.this.m_Frame.setVisible(true);
/*    */       } 
/*    */     }
/*    */     private MyClick() {} }
/*    */   
/*    */   private class MyWindowListener extends WindowAdapter { private MyWindowListener() {}
/*    */     
/*    */     public void windowClosing(WindowEvent e) {
/* 68 */       ClusTreeApplet.this.m_Frame.setVisible(false);
/* 69 */       ClusTreeApplet.this.m_Launch.setEnabled(true);
/* 70 */       String name = ClusTreeApplet.this.getParameter("Label");
/* 71 */       ClusTreeApplet.this.m_Launch.setText((name == null) ? "Show" : name);
/*    */     } }
/*    */   
/*    */   private class MyThread extends Thread {
/*    */     private MyThread() {}
/*    */     
/*    */     public void run() {
/*    */       try {
/* 79 */         URL url = new URL(ClusTreeApplet.this.getDocumentBase(), ClusTreeApplet.this.getParameter("Tree"));
/*    */ 
/*    */         
/* 82 */         ClusTreeApplet.this.m_Frame = SimpleTreeFrame.loadTildeTree(url.openStream());
/*    */ 
/*    */ 
/*    */         
/* 86 */         ClusTreeApplet.this.m_Frame.addWindowListener(new ClusTreeApplet.MyWindowListener());
/* 87 */         ClusTreeApplet.this.m_Frame.setVisible(true);
/* 88 */         ClusTreeApplet.this.m_Launch.setText("Done !");
/* 89 */       } catch (MalformedURLException e) {
/* 90 */         ClusTreeApplet.this.m_Launch.setText("URL Error: " + e.getMessage());
/* 91 */       } catch (IOException e) {
/* 92 */         ClusTreeApplet.this.m_Launch.setText("IO Error: " + e);
/* 93 */       } catch (ClassNotFoundException e) {
/* 94 */         ClusTreeApplet.this.m_Launch.setText("Class Not Found Error: " + e.getMessage());
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\gui\ClusTreeApplet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */