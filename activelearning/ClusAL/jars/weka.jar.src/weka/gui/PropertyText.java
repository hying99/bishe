package weka.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import javax.swing.JTextField;

class PropertyText extends JTextField {
  private PropertyEditor m_Editor;
  
  PropertyText(PropertyEditor paramPropertyEditor) {
    super(paramPropertyEditor.getAsText().equals("null") ? "" : paramPropertyEditor.getAsText());
    this.m_Editor = paramPropertyEditor;
    addKeyListener(new KeyAdapter(this) {
          private final PropertyText this$0;
          
          public void keyReleased(KeyEvent param1KeyEvent) {
            this.this$0.updateEditor();
          }
        });
    addFocusListener(new FocusAdapter(this) {
          private final PropertyText this$0;
          
          public void focusLost(FocusEvent param1FocusEvent) {
            this.this$0.updateEditor();
          }
        });
  }
  
  protected void updateUs() {
    try {
      setText(this.m_Editor.getAsText());
    } catch (IllegalArgumentException illegalArgumentException) {}
  }
  
  protected void updateEditor() {
    try {
      this.m_Editor.setAsText(getText());
    } catch (IllegalArgumentException illegalArgumentException) {}
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\PropertyText.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */