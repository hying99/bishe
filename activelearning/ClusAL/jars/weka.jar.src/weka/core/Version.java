package weka.core;

public class Version implements Comparable {
  public static final int MAJOR = 3;
  
  public static final int MINOR = 4;
  
  public static final int REVISION = 4;
  
  public static final String VERSION = "3.4.4";
  
  public int compareTo(Object paramObject) {
    boolean bool;
    int i = 0;
    int j = 0;
    int k = 0;
    if (paramObject instanceof String) {
      try {
        String str = paramObject.toString();
        if (str.indexOf(".") > -1) {
          i = Integer.parseInt(str.substring(0, str.indexOf(".")));
          str = str.substring(str.indexOf(".") + 1);
          if (str.indexOf(".") > -1) {
            j = Integer.parseInt(str.substring(0, str.indexOf(".")));
            str = str.substring(str.indexOf(".") + 1);
            if (!str.equals("")) {
              k = Integer.parseInt(str);
            } else {
              k = 0;
            } 
          } else if (!str.equals("")) {
            j = Integer.parseInt(str);
          } else {
            j = 0;
          } 
        } else if (!str.equals("")) {
          i = Integer.parseInt(str);
        } else {
          i = 0;
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
        i = -1;
        j = -1;
        k = -1;
      } 
    } else {
      System.out.println(getClass().getName() + ": no version-string for comparTo povided!");
      i = -1;
      j = -1;
      k = -1;
    } 
    if (3 < i) {
      bool = true;
    } else if (3 == i) {
      if (4 < j) {
        bool = true;
      } else if (4 == j) {
        if (4 < k) {
          bool = true;
        } else if (4 == k) {
          bool = false;
        } else {
          bool = true;
        } 
      } else {
        bool = true;
      } 
    } else {
      bool = true;
    } 
    return bool;
  }
  
  public boolean equals(Object paramObject) {
    return (compareTo(paramObject) == 0);
  }
  
  public boolean isOlder(Object paramObject) {
    return (compareTo(paramObject) == -1);
  }
  
  public boolean isNewer(Object paramObject) {
    return (compareTo(paramObject) == 1);
  }
  
  public static void main(String[] paramArrayOfString) {
    System.out.println("3.4.4\n");
    Version version = new Version();
    System.out.println("-1? " + version.compareTo("5.0.1"));
    System.out.println(" 0? " + version.compareTo("3.4.4"));
    System.out.println("+1? " + version.compareTo("3.4.0"));
    String str = "5.0.1";
    System.out.println("\ncomparing with " + str);
    System.out.println("isOlder? " + version.isOlder(str));
    System.out.println("equals ? " + version.equals(str));
    System.out.println("isNewer? " + version.isNewer(str));
    str = "3.4.4";
    System.out.println("\ncomparing with " + str);
    System.out.println("isOlder? " + version.isOlder(str));
    System.out.println("equals ? " + version.equals(str));
    System.out.println("isNewer? " + version.isNewer(str));
    str = "3.4.0";
    System.out.println("\ncomparing with " + str);
    System.out.println("isOlder? " + version.isOlder(str));
    System.out.println("equals ? " + version.equals(str));
    System.out.println("isNewer? " + version.isNewer(str));
    str = "3.4";
    System.out.println("\ncomparing with " + str);
    System.out.println("isOlder? " + version.isOlder(str));
    System.out.println("equals ? " + version.equals(str));
    System.out.println("isNewer? " + version.isNewer(str));
    str = "5";
    System.out.println("\ncomparing with " + str);
    System.out.println("isOlder? " + version.isOlder(str));
    System.out.println("equals ? " + version.equals(str));
    System.out.println("isNewer? " + version.isNewer(str));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Version.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */