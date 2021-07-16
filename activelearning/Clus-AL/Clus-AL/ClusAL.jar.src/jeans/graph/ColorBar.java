/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import jeans.util.CallBackFunction;
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
/*     */ public class ColorBar
/*     */   extends Panel
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   Scrollbar sbCol;
/*     */   CallBackFunction chvalue;
/*  40 */   Label laCol = new Label();
/*  41 */   TextField tfDec = new TextField("FF");
/*  42 */   TextField tfHex = new TextField("255");
/*     */   
/*     */   public ColorBar(CallBackFunction modified, String label, boolean horiz) {
/*  45 */     this.chvalue = modified;
/*  46 */     this.laCol.setText(label);
/*  47 */     Panel sPanel = new Panel();
/*  48 */     sPanel.setLayout(new PercentLayout("50%d p 50%d", 2, 0, false));
/*  49 */     if (horiz) {
/*  50 */       this.sbCol = new Scrollbar(0, 255, 8, 0, 255);
/*  51 */       sPanel.add(this.sbCol);
/*  52 */       setLayout(new PercentLayout("p 100% p p", 2, 0, false));
/*  53 */       add(this.laCol);
/*  54 */       add(sPanel);
/*  55 */       add(this.tfHex);
/*  56 */       add(this.tfDec);
/*     */     } else {
/*  58 */       this.sbCol = new Scrollbar(1, 255, 8, 0, 255);
/*  59 */       sPanel.add(this.sbCol);
/*  60 */       setLayout(new PercentLayout("p p p 100%", 2, 0, true));
/*  61 */       add(this.laCol);
/*  62 */       add(this.tfHex);
/*  63 */       add(this.tfDec);
/*  64 */       add(sPanel);
/*     */     } 
/*  66 */     this.sbCol.addAdjustmentListener(new CAdjustmentListener());
/*  67 */     ActionListener action_listener = new CActionListener();
/*  68 */     this.tfDec.addActionListener(action_listener);
/*  69 */     this.tfHex.addActionListener(action_listener);
/*     */   }
/*     */   
/*     */   public void setColor(Color col) {
/*  73 */     this.sbCol.setBackground(col);
/*     */   }
/*     */   
/*     */   public void saveValue(int val) {
/*  77 */     this.tfHex.setText(convHex(val));
/*  78 */     this.tfDec.setText(Integer.toString(val));
/*     */   }
/*     */   
/*     */   public int getValue() {
/*  82 */     return this.sbCol.getValue();
/*     */   }
/*     */   
/*     */   public void setValue(int value) {
/*  86 */     this.sbCol.setValue(value);
/*  87 */     saveValue(value);
/*     */   }
/*     */   
/*     */   public static String convHex(int value) {
/*  91 */     String aux = Integer.toString(value, 16).toUpperCase();
/*  92 */     if (aux.length() == 1) aux = "0" + aux; 
/*  93 */     return aux;
/*     */   }
/*     */   
/*     */   private class CAdjustmentListener implements AdjustmentListener { private CAdjustmentListener() {}
/*     */     
/*     */     public void adjustmentValueChanged(AdjustmentEvent e) {
/*  99 */       int val = ColorBar.this.sbCol.getValue();
/* 100 */       ColorBar.this.saveValue(val);
/* 101 */       ColorBar.this.chvalue.callBackFunction(this);
/*     */     } }
/*     */ 
/*     */   
/*     */   private class CActionListener implements ActionListener {
/*     */     private CActionListener() {}
/*     */     
/*     */     public void actionPerformed(ActionEvent ev) {
/* 109 */       TextField fld = (TextField)ev.getSource();
/*     */       try {
/* 111 */         int value = Integer.parseInt(fld.getText(), (fld == ColorBar.this.tfDec) ? 10 : 16);
/* 112 */         if (value >= 0 && value <= 255) ColorBar.this.sbCol.setValue(value); 
/* 113 */         ColorBar.this.chvalue.callBackFunction(this);
/* 114 */       } catch (NumberFormatException e) {
/* 115 */         ColorBar.this.saveValue(ColorBar.this.getValue());
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\ColorBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */