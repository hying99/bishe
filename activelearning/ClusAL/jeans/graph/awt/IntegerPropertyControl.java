/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.Button;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.Label;
/*    */ import java.awt.LayoutManager;
/*    */ import java.awt.Panel;
/*    */ import java.awt.TextField;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import jeans.graph.PercentLayout;
/*    */ import jeans.util.PropertyInterface;
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
/*    */ public class IntegerPropertyControl
/*    */   extends Panel
/*    */   implements ActionListener
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   private PropertyInterface props;
/*    */   private int prop;
/*    */   private int delta;
/*    */   private Object backup;
/*    */   private TextField field;
/*    */   
/*    */   public IntegerPropertyControl(PropertyInterface props, int prop, String label, int wd, int delta) {
/* 40 */     this.props = props;
/* 41 */     this.prop = prop;
/* 42 */     this.delta = delta;
/* 43 */     this.backup = props.getProperty(prop);
/* 44 */     setLayout((LayoutManager)new PercentLayout("100% p p", 3, 0, false));
/* 45 */     add(new Label(label));
/* 46 */     add(this.field = new TextField(wd));
/* 47 */     add(makeSpins());
/* 48 */     updateField();
/*    */   }
/*    */   
/*    */   public void updateField() {
/* 52 */     this.field.setText(this.props.getProperty(this.prop).toString());
/*    */   }
/*    */   
/*    */   public void updateProperty() throws NumberFormatException {
/* 56 */     this.props.setIntegerProperty(this.prop, Integer.parseInt(this.field.getText()));
/*    */   }
/*    */   
/*    */   public void restoreProperty() {
/* 60 */     this.props.setProperty(this.prop, this.backup);
/*    */   }
/*    */   
/*    */   public void increase(int delta) {
/*    */     try {
/* 65 */       int value = Integer.parseInt(this.field.getText()) + delta;
/* 66 */       this.props.setIntegerProperty(this.prop, value);
/* 67 */     } catch (NumberFormatException numberFormatException) {}
/* 68 */     updateField();
/*    */   }
/*    */ 
/*    */   
/*    */   private Panel makeSpins() {
/* 73 */     Panel spin = new Panel();
/* 74 */     spin.setLayout(new GridLayout(1, 2, 3, 3));
/* 75 */     Button button = new Button("<");
/* 76 */     button.addActionListener(this);
/* 77 */     spin.add(button);
/* 78 */     button = new Button(">");
/* 79 */     button.addActionListener(this);
/* 80 */     spin.add(button);
/* 81 */     return spin;
/*    */   }
/*    */   
/*    */   public void actionPerformed(ActionEvent event) {
/* 85 */     Button source = (Button)event.getSource();
/* 86 */     if (source.getLabel().equals("<")) {
/* 87 */       increase(-this.delta);
/*    */     } else {
/* 89 */       increase(this.delta);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\awt\IntegerPropertyControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */