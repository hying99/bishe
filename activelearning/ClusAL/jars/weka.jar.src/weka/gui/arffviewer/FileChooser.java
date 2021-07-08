package weka.gui.arffviewer;

import java.awt.Component;
import javax.swing.JFileChooser;

public class FileChooser extends JFileChooser {
  public int showSaveDialog(Component paramComponent) {
    while (true) {
      int i = super.showSaveDialog(paramComponent);
      if (i != 0)
        return i; 
      if (getSelectedFile() != null)
        return i; 
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\FileChooser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */