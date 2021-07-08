package weka.gui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class Loader {
  private String dir;
  
  public Loader(String paramString) {
    this.dir = paramString;
  }
  
  public String getDir() {
    return this.dir;
  }
  
  public String processFilename(String paramString) {
    if (!paramString.startsWith(getDir()))
      paramString = getDir() + paramString; 
    return paramString;
  }
  
  public static URL getURL(String paramString1, String paramString2) {
    Loader loader = new Loader(paramString1);
    return loader.getURL(paramString2);
  }
  
  public URL getURL(String paramString) {
    paramString = processFilename(paramString);
    return Loader.class.getClassLoader().getResource(paramString);
  }
  
  public static InputStream getInputStream(String paramString1, String paramString2) {
    Loader loader = new Loader(paramString1);
    return loader.getInputStream(paramString2);
  }
  
  public InputStream getInputStream(String paramString) {
    paramString = processFilename(paramString);
    return Loader.class.getResourceAsStream(paramString);
  }
  
  public static Reader getReader(String paramString1, String paramString2) {
    Loader loader = new Loader(paramString1);
    return loader.getReader(paramString2);
  }
  
  public Reader getReader(String paramString) {
    InputStream inputStream = getInputStream(paramString);
    return (inputStream == null) ? null : new InputStreamReader(inputStream);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\Loader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */