package weka.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import javax.swing.JFrame;
import weka.core.SelectedTag;
import weka.core.Tag;

public class SelectedTagEditor extends PropertyEditorSupport {
  public String getJavaInitializationString() {
    SelectedTag selectedTag = (SelectedTag)getValue();
    Tag[] arrayOfTag = selectedTag.getTags();
    String str = "new SelectedTag(" + selectedTag.getSelectedTag().getID() + ", {\n";
    for (byte b = 0; b < arrayOfTag.length; b++) {
      str = str + "new Tag(" + arrayOfTag[b].getID() + ",\"" + arrayOfTag[b].getReadable() + "\")";
      if (b < arrayOfTag.length - 1)
        str = str + ','; 
      str = str + '\n';
    } 
    return str + "})";
  }
  
  public String getAsText() {
    SelectedTag selectedTag = (SelectedTag)getValue();
    return selectedTag.getSelectedTag().getReadable();
  }
  
  public void setAsText(String paramString) {
    SelectedTag selectedTag = (SelectedTag)getValue();
    Tag[] arrayOfTag = selectedTag.getTags();
    try {
      for (byte b = 0; b < arrayOfTag.length; b++) {
        if (paramString.equals(arrayOfTag[b].getReadable())) {
          setValue(new SelectedTag(arrayOfTag[b].getID(), arrayOfTag));
          return;
        } 
      } 
    } catch (Exception exception) {
      throw new IllegalArgumentException(paramString);
    } 
  }
  
  public String[] getTags() {
    SelectedTag selectedTag = (SelectedTag)getValue();
    Tag[] arrayOfTag = selectedTag.getTags();
    String[] arrayOfString = new String[arrayOfTag.length];
    for (byte b = 0; b < arrayOfTag.length; b++)
      arrayOfString[b] = arrayOfTag[b].getReadable(); 
    return arrayOfString;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.err.println("---Registering Weka Editors---");
      PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
      Tag[] arrayOfTag = { new Tag(1, "First option"), new Tag(2, "Second option"), new Tag(3, "Third option"), new Tag(4, "Fourth option"), new Tag(5, "Fifth option") };
      SelectedTag selectedTag = new SelectedTag(1, arrayOfTag);
      SelectedTagEditor selectedTagEditor = new SelectedTagEditor();
      selectedTagEditor.setValue(selectedTag);
      PropertyValueSelector propertyValueSelector = new PropertyValueSelector(selectedTagEditor);
      JFrame jFrame = new JFrame();
      jFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent param1WindowEvent) {
              System.exit(0);
            }
          });
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(propertyValueSelector, "Center");
      jFrame.pack();
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\SelectedTagEditor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */