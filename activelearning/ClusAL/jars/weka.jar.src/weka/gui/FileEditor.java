package weka.gui;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.io.File;
import javax.swing.JFileChooser;

public class FileEditor extends PropertyEditorSupport {
  protected JFileChooser m_FileChooser;
  
  public String getJavaInitializationString() {
    File file = (File)getValue();
    return (file == null) ? "null" : ("new File(\"" + file.getName() + "\")");
  }
  
  public boolean supportsCustomEditor() {
    return true;
  }
  
  public Component getCustomEditor() {
    if (this.m_FileChooser == null) {
      File file = (File)getValue();
      if (file != null) {
        this.m_FileChooser = new JFileChooser();
        this.m_FileChooser.setSelectedFile(file);
      } else {
        this.m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
      } 
      this.m_FileChooser.setApproveButtonText("Select");
      this.m_FileChooser.setApproveButtonMnemonic('S');
      this.m_FileChooser.setFileSelectionMode(2);
      this.m_FileChooser.addActionListener(new ActionListener(this) {
            private final FileEditor this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              String str = param1ActionEvent.getActionCommand();
              if (str.equals("ApproveSelection")) {
                File file = this.this$0.m_FileChooser.getSelectedFile();
                this.this$0.setValue(file);
              } 
            }
          });
    } 
    return this.m_FileChooser;
  }
  
  public boolean isPaintable() {
    return true;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {
    FontMetrics fontMetrics = paramGraphics.getFontMetrics();
    int i = (paramRectangle.height - fontMetrics.getHeight()) / 2;
    File file = (File)getValue();
    String str = "No file";
    if (file != null)
      str = file.getName(); 
    paramGraphics.drawString(str, 2, fontMetrics.getHeight() + i);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\FileEditor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */