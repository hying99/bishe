package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditor;
import javax.swing.JFrame;

public class PropertyDialog extends JFrame {
  private PropertyEditor m_Editor;
  
  private Component m_EditorComponent;
  
  public PropertyDialog(PropertyEditor paramPropertyEditor, int paramInt1, int paramInt2) {
    super(paramPropertyEditor.getClass().getName());
    addWindowListener(new WindowAdapter(this) {
          private final PropertyDialog this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            param1WindowEvent.getWindow().dispose();
          }
        });
    getContentPane().setLayout(new BorderLayout());
    this.m_Editor = paramPropertyEditor;
    this.m_EditorComponent = paramPropertyEditor.getCustomEditor();
    getContentPane().add(this.m_EditorComponent, "Center");
    pack();
    setLocation(paramInt1, paramInt2);
    setVisible(true);
  }
  
  public PropertyEditor getEditor() {
    return this.m_Editor;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\PropertyDialog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */