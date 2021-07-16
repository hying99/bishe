/*    */ package jeans.graph.swing;
/*    */ 
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.LayoutManager;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
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
/*    */ 
/*    */ public class JIntegerPropertyControl
/*    */   extends JPanel
/*    */   implements ActionListener
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   private PropertyInterface props;
/*    */   private int prop;
/*    */   private int delta;
/*    */   private Object backup;
/*    */   private JTextField field;
/*    */   
/*    */   public JIntegerPropertyControl(PropertyInterface props, int prop, String label, int wd, int delta, int align) {
/* 41 */     this.props = props;
/* 42 */     this.prop = prop;
/* 43 */     this.delta = delta;
/* 44 */     this.backup = props.getProperty(prop);
/* 45 */     setLayout((LayoutManager)new PercentLayout("100% p p", 3, 0, false));
/* 46 */     add(new JLabel(label, align));
/* 47 */     add(this.field = new JTextField(wd));
/* 48 */     add(makeSpins());
/* 49 */     updateField();
/*    */   }
/*    */   
/*    */   public void updateField() {
/* 53 */     this.field.setText(this.props.getProperty(this.prop).toString());
/*    */   }
/*    */   
/*    */   public void updateProperty() throws NumberFormatException {
/* 57 */     this.props.setIntegerProperty(this.prop, Integer.parseInt(this.field.getText()));
/*    */   }
/*    */   
/*    */   public void restoreProperty() {
/* 61 */     this.props.setProperty(this.prop, this.backup);
/*    */   }
/*    */   
/*    */   public void increase(int delta) {
/*    */     try {
/* 66 */       int value = Integer.parseInt(this.field.getText()) + delta;
/* 67 */       this.props.setIntegerProperty(this.prop, value);
/* 68 */     } catch (NumberFormatException numberFormatException) {}
/* 69 */     updateField();
/*    */   }
/*    */ 
/*    */   
/*    */   private JPanel makeSpins() {
/* 74 */     JPanel spin = new JPanel();
/* 75 */     spin.setLayout(new GridLayout(1, 2, 3, 3));
/* 76 */     JButton button = new JButton("<");
/* 77 */     button.addActionListener(this);
/* 78 */     spin.add(button);
/* 79 */     button = new JButton(">");
/* 80 */     button.addActionListener(this);
/* 81 */     spin.add(button);
/* 82 */     return spin;
/*    */   }
/*    */   
/*    */   public void actionPerformed(ActionEvent event) {
/* 86 */     JButton source = (JButton)event.getSource();
/* 87 */     if (source.getText().equals("<")) {
/* 88 */       increase(-this.delta);
/*    */     } else {
/* 90 */       increase(this.delta);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\JIntegerPropertyControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */