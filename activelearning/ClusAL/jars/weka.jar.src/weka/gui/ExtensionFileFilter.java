package weka.gui;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter implements FilenameFilter {
  protected String m_Description;
  
  protected String[] m_Extension = new String[1];
  
  public ExtensionFileFilter(String paramString1, String paramString2) {
    this.m_Extension[0] = paramString1;
    this.m_Description = paramString2;
  }
  
  public ExtensionFileFilter(String[] paramArrayOfString, String paramString) {
    this.m_Description = paramString;
  }
  
  public String getDescription() {
    return this.m_Description;
  }
  
  public boolean accept(File paramFile) {
    String str = paramFile.getName().toLowerCase();
    if (paramFile.isDirectory())
      return true; 
    for (byte b = 0; b < this.m_Extension.length; b++) {
      if (str.endsWith(this.m_Extension[b]))
        return true; 
    } 
    return false;
  }
  
  public boolean accept(File paramFile, String paramString) {
    return accept(new File(paramFile, paramString));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\ExtensionFileFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */