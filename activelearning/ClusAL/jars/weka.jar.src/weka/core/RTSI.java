package weka.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class RTSI {
  public static final boolean VERBOSE = false;
  
  public static Vector find(String paramString) {
    Vector vector = new Vector();
    try {
      Class clazz = Class.forName(paramString);
      Package[] arrayOfPackage = Package.getPackages();
      for (byte b = 0; b < arrayOfPackage.length; b++) {
        Vector vector1 = find(arrayOfPackage[b].getName(), clazz);
        vector.addAll(vector1);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("Class " + paramString + " not found!");
    } 
    return vector;
  }
  
  public static Vector find(String paramString1, String paramString2) {
    try {
      Class clazz = Class.forName(paramString2);
      return find(paramString1, clazz);
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("Class " + paramString2 + " not found!");
      return new Vector();
    } 
  }
  
  public static boolean isSubclass(Class paramClass1, Class paramClass2) {
    boolean bool = false;
    Class clazz = paramClass2;
    do {
      bool = clazz.equals(paramClass1);
      if (clazz.equals(Object.class))
        break; 
      if (bool)
        continue; 
      clazz = clazz.getSuperclass();
    } while (!bool);
    return bool;
  }
  
  public static boolean hasInterface(Class paramClass1, Class paramClass2) {
    boolean bool = false;
    Class clazz = paramClass2;
    do {
      Class[] arrayOfClass = clazz.getInterfaces();
      for (byte b = 0; b < arrayOfClass.length; b++) {
        if (arrayOfClass[b].equals(paramClass1)) {
          bool = true;
          break;
        } 
      } 
      if (bool)
        continue; 
      clazz = clazz.getSuperclass();
      if (clazz.equals(Object.class))
        break; 
    } while (!bool);
    return bool;
  }
  
  protected static URL getURL(String paramString1, String paramString2) {
    URL uRL = null;
    String str = null;
    try {
      File file = new File(paramString1);
      if (file.isDirectory()) {
        File file1 = new File(paramString1 + paramString2);
        if (file1.exists())
          str = "file:" + paramString1 + paramString2; 
      } else {
        JarFile jarFile = new JarFile(paramString1);
        Enumeration enumeration = jarFile.entries();
        String str1 = paramString2.substring(1);
        while (enumeration.hasMoreElements()) {
          if (enumeration.nextElement().toString().startsWith(str1)) {
            str = "jar:file:" + paramString1 + "!" + paramString2;
            break;
          } 
        } 
      } 
    } catch (Exception exception) {}
    if (str != null)
      try {
        uRL = new URL(str);
      } catch (Exception exception) {
        System.err.println("Trying to create URL from '" + str + "' generates this exception:\n" + exception);
        uRL = null;
      }  
    return uRL;
  }
  
  public static Vector find(String paramString, Class paramClass) {
    Vector vector = new Vector();
    String str = new String(paramString);
    if (!str.startsWith("/"))
      str = "/" + str; 
    str = str.replace('.', '/');
    StringTokenizer stringTokenizer = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken();
      URL uRL = getURL(str1, str);
      if (uRL == null)
        continue; 
      File file = new File(uRL.getFile());
      if (file.exists()) {
        String[] arrayOfString = file.list();
        for (byte b = 0; b < arrayOfString.length; b++) {
          if (arrayOfString[b].endsWith(".class")) {
            String str2 = arrayOfString[b].substring(0, arrayOfString[b].length() - 6);
            try {
              Class clazz = Class.forName(paramString + "." + str2);
              if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isPrimitive() && ((!paramClass.isInterface() && isSubclass(paramClass, clazz)) || (paramClass.isInterface() && hasInterface(paramClass, clazz))) && !vector.contains(clazz.getName()))
                vector.add(clazz.getName()); 
            } catch (ClassNotFoundException classNotFoundException) {
              System.err.println(classNotFoundException);
            } 
          } 
        } 
        continue;
      } 
      try {
        JarURLConnection jarURLConnection = (JarURLConnection)uRL.openConnection();
        String str2 = jarURLConnection.getEntryName();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
          ZipEntry zipEntry = enumeration.nextElement();
          String str3 = zipEntry.getName();
          if (str3.startsWith(str2) && str3.lastIndexOf('/') <= str2.length() && str3.endsWith(".class")) {
            String str4 = str3.substring(0, str3.length() - 6);
            if (str4.startsWith("/"))
              str4 = str4.substring(1); 
            str4 = str4.replace('/', '.');
            try {
              Class clazz = Class.forName(str4);
              if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isPrimitive() && ((!paramClass.isInterface() && isSubclass(paramClass, clazz)) || (paramClass.isInterface() && hasInterface(paramClass, clazz))) && !vector.contains(clazz.getName()))
                vector.add(clazz.getName()); 
            } catch (ClassNotFoundException classNotFoundException) {
              System.err.println(classNotFoundException);
            } 
          } 
        } 
      } catch (IOException iOException) {
        System.err.println(iOException);
      } 
    } 
    RTSI rTSI = new RTSI();
    rTSI.getClass();
    Collections.sort(vector, new StringCompare(rTSI));
    return vector;
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length == 2) {
      System.out.println(find(paramArrayOfString[0], paramArrayOfString[1]));
    } else if (paramArrayOfString.length == 1) {
      System.out.println(find(paramArrayOfString[0]));
    } else {
      System.out.println("Usage: java " + RTSI.class.getName() + " [<package>] <subclass>");
    } 
  }
  
  public class StringCompare implements Comparator {
    private final RTSI this$0;
    
    public StringCompare(RTSI this$0) {
      this.this$0 = this$0;
    }
    
    private String fillUp(String param1String, int param1Int) {
      while (param1String.length() < param1Int)
        param1String = param1String + " "; 
      return param1String;
    }
    
    private int charGroup(char param1Char) {
      byte b = 0;
      if (param1Char >= 'a' && param1Char <= 'z') {
        b = 2;
      } else if (param1Char >= '0' && param1Char <= '9') {
        b = 1;
      } 
      return b;
    }
    
    public int compare(Object param1Object1, Object param1Object2) {
      byte b1 = 0;
      String str1 = param1Object1.toString().toLowerCase();
      String str2 = param1Object2.toString().toLowerCase();
      str1 = fillUp(str1, str2.length());
      str2 = fillUp(str2, str1.length());
      for (byte b = 0; b < str1.length(); b++) {
        if (str1.charAt(b) == str2.charAt(b)) {
          b1 = 0;
        } else {
          int i = charGroup(str1.charAt(b));
          int j = charGroup(str2.charAt(b));
          if (i != j) {
            if (i < j) {
              b1 = -1;
              break;
            } 
            b1 = 1;
            break;
          } 
          if (str1.charAt(b) < str2.charAt(b)) {
            b1 = -1;
            break;
          } 
          b1 = 1;
          break;
        } 
      } 
      return b1;
    }
    
    public boolean equals(Object param1Object) {
      return param1Object instanceof StringCompare;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\RTSI.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */