package weka.gui;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ComponentHelper {
  public static final String[] IMAGES = new String[] { "weka/gui/", "weka/gui/images/" };
  
  public static ImageIcon getImageIcon(String paramString1, String paramString2) {
    ImageIcon imageIcon = null;
    URL uRL = Loader.getURL(paramString1, paramString2);
    if (uRL == null)
      for (byte b = 0; b < IMAGES.length; b++) {
        uRL = Loader.getURL(IMAGES[b], paramString2);
        if (uRL != null)
          break; 
      }  
    if (uRL != null)
      imageIcon = new ImageIcon(uRL); 
    return imageIcon;
  }
  
  public static ImageIcon getImageIcon(String paramString) {
    return getImageIcon("", paramString);
  }
  
  public static Image getImage(String paramString1, String paramString2) {
    Image image = null;
    ImageIcon imageIcon = getImageIcon(paramString1, paramString2);
    if (imageIcon != null)
      image = imageIcon.getImage(); 
    return image;
  }
  
  public static Image getImage(String paramString) {
    Image image = null;
    ImageIcon imageIcon = getImageIcon(paramString);
    if (imageIcon != null)
      image = imageIcon.getImage(); 
    return image;
  }
  
  public static int showMessageBox(Component paramComponent, String paramString1, String paramString2, int paramInt1, int paramInt2) {
    switch (paramInt2) {
      case 0:
        str = "weka/gui/images/error.gif";
        return JOptionPane.showConfirmDialog(paramComponent, paramString2, paramString1, paramInt1, paramInt2, getImageIcon(str));
      case 1:
        str = "weka/gui/images/information.gif";
        return JOptionPane.showConfirmDialog(paramComponent, paramString2, paramString1, paramInt1, paramInt2, getImageIcon(str));
      case 2:
        str = "weka/gui/images/information.gif";
        return JOptionPane.showConfirmDialog(paramComponent, paramString2, paramString1, paramInt1, paramInt2, getImageIcon(str));
      case 3:
        str = "weka/gui/images/question.gif";
        return JOptionPane.showConfirmDialog(paramComponent, paramString2, paramString1, paramInt1, paramInt2, getImageIcon(str));
    } 
    String str = "weka/gui/images/information.gif";
    return JOptionPane.showConfirmDialog(paramComponent, paramString2, paramString1, paramInt1, paramInt2, getImageIcon(str));
  }
  
  public static String showInputBox(Component paramComponent, String paramString1, String paramString2, Object paramObject) {
    if (paramString1 == null)
      paramString1 = "Input..."; 
    Object object = JOptionPane.showInputDialog(paramComponent, paramString2, paramString1, 3, getImageIcon("question.gif"), null, paramObject);
    return (object != null) ? object.toString() : null;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\ComponentHelper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */