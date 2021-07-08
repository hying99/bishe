/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Image;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class IconMessageBox
/*     */   extends Dialog
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int MB_OK = 1;
/*     */   public static final int MB_CANCEL = 2;
/*  36 */   protected int m_action = 2;
/*  37 */   protected ActionListener m_listener = null;
/*     */   
/*     */   public IconMessageBox(Frame parent, String title, String lines, int buttons) {
/*  40 */     this(parent, title, lines, (Image)null, buttons);
/*     */   }
/*     */   
/*     */   public IconMessageBox(Frame parent, String title, String lines, Image icon, int buttons) {
/*  44 */     super(parent, title, true);
/*  45 */     add(makePanel(icon, lines, buttons));
/*  46 */     addWindowListener(new WindowClosingListener(this, false));
/*  47 */     pack();
/*     */   }
/*     */   
/*     */   public IconMessageBox(Frame parent, String title) {
/*  51 */     super(parent, title, true);
/*     */   }
/*     */   
/*     */   public void setCenter() {
/*  55 */     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
/*  56 */     Dimension mySize = getSize();
/*  57 */     setLocation(screenSize.width / 2 - mySize.width / 2, screenSize.height / 2 - mySize.height / 2);
/*     */   }
/*     */   
/*     */   public void setActionListener(ActionListener listener) {
/*  61 */     this.m_listener = listener;
/*     */   }
/*     */   
/*     */   public void performAction(int action) {
/*  65 */     this.m_action = action;
/*  66 */     if (this.m_listener != null)
/*  67 */       this.m_listener.actionPerformed(new ActionEvent(this, this.m_action, "")); 
/*     */   }
/*     */   
/*     */   public int getAction() {
/*  71 */     return this.m_action;
/*     */   }
/*     */   
/*     */   protected int getNbButtons(int buttons) {
/*  75 */     int nb = 0;
/*  76 */     int ps = 0;
/*  77 */     while (ps < 5 && nb < 5) {
/*  78 */       int and = 1 << ps;
/*  79 */       if ((buttons & and) != 0) nb++; 
/*  80 */       ps++;
/*     */     } 
/*  82 */     return nb;
/*     */   }
/*     */   
/*     */   protected Vector getLines(String lines) {
/*  86 */     Vector<String> res = new Vector();
/*  87 */     StringTokenizer tokens = new StringTokenizer(lines, "\n");
/*  88 */     while (tokens.hasMoreTokens()) {
/*  89 */       String token = tokens.nextToken();
/*  90 */       res.addElement(token);
/*     */     } 
/*  92 */     return res;
/*     */   }
/*     */   
/*     */   protected Panel makePanel(Image icon, String lines, int buttons) {
/*  96 */     Panel panel = new Panel();
/*  97 */     panel.setBackground(SystemColor.control);
/*  98 */     if (icon == null) {
/*  99 */       panel.setLayout(new PercentLayout("100% p", 3, 15, false));
/* 100 */       panel.add(makeStringPanel(lines));
/* 101 */       panel.add(makeButtonPanel(buttons));
/*     */     } else {
/* 103 */       panel.setLayout(new PercentLayout("p 100% p", 3, 15, false));
/* 104 */       panel.add(new ImageViewer(icon, 1));
/* 105 */       panel.add(makeStringPanel(lines));
/* 106 */       panel.add(makeButtonPanel(buttons));
/*     */     } 
/* 108 */     return panel;
/*     */   }
/*     */   
/*     */   protected Panel makeStringPanel(String lines) {
/* 112 */     Panel panel = new Panel();
/* 113 */     String layout = "";
/* 114 */     Vector<String> msgs = getLines(lines);
/* 115 */     int nb = msgs.size(); int ctr;
/* 116 */     for (ctr = 0; ctr < nb; ctr++)
/* 117 */       layout = layout + "p "; 
/* 118 */     panel.setLayout(new PercentLayout(layout + "100%d", 3, 0, true));
/* 119 */     for (ctr = 0; ctr < nb; ctr++) {
/* 120 */       String string = msgs.elementAt(ctr);
/* 121 */       Label label = new Label(string);
/* 122 */       panel.add(label);
/*     */     } 
/* 124 */     return panel;
/*     */   }
/*     */   
/*     */   protected Panel makeButtonPanel(int buttons) {
/* 128 */     Panel panel = new Panel();
/* 129 */     String layout = "";
/* 130 */     int nb = getNbButtons(buttons);
/* 131 */     for (int ctr = 0; ctr < nb; ctr++)
/* 132 */       layout = layout + "p "; 
/* 133 */     panel.setLayout(new PercentLayout(layout + "100%d", 3, 0, true));
/* 134 */     if ((buttons & 0x1) != 0) {
/* 135 */       Button m_okButton = new Button("OK");
/* 136 */       ButtonListener listener = new ButtonListener(1);
/* 137 */       m_okButton.addActionListener(listener);
/* 138 */       panel.add(m_okButton);
/*     */     } 
/* 140 */     if ((buttons & 0x2) != 0) {
/* 141 */       Button m_cancelButton = new Button("CANCEL");
/* 142 */       ButtonListener listener = new ButtonListener(2);
/* 143 */       m_cancelButton.addActionListener(listener);
/* 144 */       panel.add(m_cancelButton);
/*     */     } 
/* 146 */     return panel;
/*     */   }
/*     */   
/*     */   protected class ButtonListener
/*     */     implements ActionListener {
/* 151 */     protected int m_id = 0;
/*     */     
/*     */     protected ButtonListener(int id) {
/* 154 */       this.m_id = id;
/*     */     }
/*     */     
/*     */     public void actionPerformed(ActionEvent evt) {
/* 158 */       IconMessageBox.this.performAction(this.m_id);
/* 159 */       IconMessageBox.this.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\IconMessageBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */