package weka.gui.visualize;

import java.awt.Color;
import java.util.Properties;
import javax.swing.JOptionPane;
import weka.core.Utils;

public class VisualizeUtils {
  protected static String PROPERTY_FILE = "weka/gui/visualize/Visualize.props";
  
  protected static Properties VISUALIZE_PROPERTIES;
  
  protected static int MAX_PRECISION = 10;
  
  protected static Color processColour(String paramString, Color paramColor) {
    String str = new String(paramString);
    Color color = paramColor;
    if (paramString.indexOf(",") >= 0) {
      try {
        int i = paramString.indexOf(",");
        int j = Integer.parseInt(paramString.substring(0, i));
        paramString = paramString.substring(i + 1, paramString.length());
        i = paramString.indexOf(",");
        int k = Integer.parseInt(paramString.substring(0, i));
        paramString = paramString.substring(i + 1, paramString.length());
        int m = Integer.parseInt(paramString);
        color = new Color(j, k, m);
      } catch (Exception exception) {
        System.err.println("VisualizeUtils: Problem parsing colour property value (" + str + ").");
      } 
    } else if (paramString.compareTo("black") == 0) {
      color = Color.black;
    } else if (paramString.compareTo("blue") == 0) {
      color = Color.blue;
    } else if (paramString.compareTo("cyan") == 0) {
      color = Color.cyan;
    } else if (paramString.compareTo("darkGray") == 0) {
      color = Color.darkGray;
    } else if (paramString.compareTo("gray") == 0) {
      color = Color.gray;
    } else if (paramString.compareTo("green") == 0) {
      color = Color.green;
    } else if (paramString.compareTo("lightGray") == 0) {
      color = Color.lightGray;
    } else if (paramString.compareTo("magenta") == 0) {
      color = Color.magenta;
    } else if (paramString.compareTo("orange") == 0) {
      color = Color.orange;
    } else if (paramString.compareTo("pink") == 0) {
      color = Color.pink;
    } else if (paramString.compareTo("red") == 0) {
      color = Color.red;
    } else if (paramString.compareTo("white") == 0) {
      color = Color.white;
    } else if (paramString.compareTo("yellow") == 0) {
      color = Color.yellow;
    } else {
      System.err.println("VisualizeUtils: colour property name not recognized (" + str + ").");
    } 
    return color;
  }
  
  static {
    try {
      VISUALIZE_PROPERTIES = Utils.readProperties(PROPERTY_FILE);
      String str = VISUALIZE_PROPERTIES.getProperty("weka.gui.visualize.precision");
      if (str != null)
        MAX_PRECISION = Integer.parseInt(str); 
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(null, "VisualizeUtils: Could not read a visualization configuration file.\nAn example file is included in the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\"  and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "Plot2D", 0);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\VisualizeUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */