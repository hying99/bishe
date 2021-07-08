package weka.core;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class Range implements Serializable {
  Vector m_RangeStrings = new Vector();
  
  boolean m_Invert;
  
  boolean[] m_SelectFlags;
  
  int m_Upper = -1;
  
  public Range() {}
  
  public Range(String paramString) {
    setRanges(paramString);
  }
  
  public void setUpper(int paramInt) {
    if (paramInt >= 0) {
      this.m_Upper = paramInt;
      setFlags();
    } 
  }
  
  public boolean getInvert() {
    return this.m_Invert;
  }
  
  public void setInvert(boolean paramBoolean) {
    this.m_Invert = paramBoolean;
  }
  
  public String getRanges() {
    String str = null;
    Enumeration enumeration = this.m_RangeStrings.elements();
    while (enumeration.hasMoreElements()) {
      if (str == null) {
        str = enumeration.nextElement();
        continue;
      } 
      str = str + ',' + (String)enumeration.nextElement();
    } 
    return (str == null) ? "" : str;
  }
  
  public void setRanges(String paramString) {
    Vector vector = new Vector(10);
    while (!paramString.equals("")) {
      String str = paramString.trim();
      int i = paramString.indexOf(',');
      if (i != -1) {
        str = paramString.substring(0, i).trim();
        paramString = paramString.substring(i + 1).trim();
      } else {
        paramString = "";
      } 
      if (!str.equals(""))
        vector.addElement(str); 
    } 
    this.m_RangeStrings = vector;
    this.m_SelectFlags = null;
  }
  
  public boolean isInRange(int paramInt) {
    if (this.m_Upper == -1)
      throw new RuntimeException("No upper limit has been specified for range"); 
    return this.m_Invert ? (!this.m_SelectFlags[paramInt]) : this.m_SelectFlags[paramInt];
  }
  
  public String toString() {
    if (this.m_RangeStrings.size() == 0)
      return "Empty"; 
    String str = "Strings: ";
    Enumeration enumeration = this.m_RangeStrings.elements();
    while (enumeration.hasMoreElements())
      str = str + (String)enumeration.nextElement() + " "; 
    str = str + "\n";
    str = str + "Invert: " + this.m_Invert + "\n";
    try {
      if (this.m_Upper == -1)
        throw new RuntimeException("Upper limit has not been specified"); 
      String str1 = null;
      for (byte b = 0; b < this.m_SelectFlags.length; b++) {
        if (isInRange(b))
          if (str1 == null) {
            str1 = "Cols: " + (b + 1);
          } else {
            str1 = str1 + "," + (b + 1);
          }  
      } 
      if (str1 != null)
        str = str + str1 + "\n"; 
    } catch (Exception exception) {
      str = str + exception.getMessage();
    } 
    return str;
  }
  
  public int[] getSelection() {
    if (this.m_Upper == -1)
      throw new RuntimeException("No upper limit has been specified for range"); 
    int[] arrayOfInt1 = new int[this.m_Upper + 1];
    byte b = 0;
    if (this.m_Invert) {
      for (byte b1 = 0; b1 <= this.m_Upper; b1++) {
        if (!this.m_SelectFlags[b1])
          arrayOfInt1[b++] = b1; 
      } 
    } else {
      Enumeration enumeration = this.m_RangeStrings.elements();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        int i = rangeLower(str);
        int j = rangeUpper(str);
        for (int k = i; k <= this.m_Upper && k <= j; k++) {
          if (this.m_SelectFlags[k])
            arrayOfInt1[b++] = k; 
        } 
      } 
    } 
    int[] arrayOfInt2 = new int[b];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b);
    return arrayOfInt2;
  }
  
  public static String indicesToRangeList(int[] paramArrayOfint) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = -2;
    boolean bool = false;
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (b == 0) {
        stringBuffer.append(paramArrayOfint[b] + 1);
      } else if (paramArrayOfint[b] == i) {
        bool = true;
      } else {
        if (bool) {
          stringBuffer.append('-').append(i);
          bool = false;
        } 
        stringBuffer.append(',').append(paramArrayOfint[b] + 1);
      } 
      i = paramArrayOfint[b] + 1;
    } 
    if (bool)
      stringBuffer.append('-').append(i); 
    return stringBuffer.toString();
  }
  
  protected void setFlags() {
    this.m_SelectFlags = new boolean[this.m_Upper + 1];
    Enumeration enumeration = this.m_RangeStrings.elements();
    while (enumeration.hasMoreElements()) {
      String str = enumeration.nextElement();
      if (!isValidRange(str))
        throw new IllegalArgumentException("Invalid range list at " + str); 
      int i = rangeLower(str);
      int j = rangeUpper(str);
      for (int k = i; k <= this.m_Upper && k <= j; k++)
        this.m_SelectFlags[k] = true; 
    } 
  }
  
  protected int rangeSingle(String paramString) {
    if (paramString.toLowerCase().equals("first"))
      return 0; 
    if (paramString.toLowerCase().equals("last"))
      return this.m_Upper; 
    int i = Integer.parseInt(paramString) - 1;
    if (i < 0)
      i = 0; 
    if (i > this.m_Upper)
      i = this.m_Upper; 
    return i;
  }
  
  protected int rangeLower(String paramString) {
    int i;
    return ((i = paramString.indexOf('-')) >= 0) ? Math.min(rangeLower(paramString.substring(0, i)), rangeLower(paramString.substring(i + 1))) : rangeSingle(paramString);
  }
  
  protected int rangeUpper(String paramString) {
    int i;
    return ((i = paramString.indexOf('-')) >= 0) ? Math.max(rangeUpper(paramString.substring(0, i)), rangeUpper(paramString.substring(i + 1))) : rangeSingle(paramString);
  }
  
  protected boolean isValidRange(String paramString) {
    if (paramString == null)
      return false; 
    int i;
    if ((i = paramString.indexOf('-')) >= 0)
      return (isValidRange(paramString.substring(0, i)) && isValidRange(paramString.substring(i + 1))); 
    if (paramString.toLowerCase().equals("first"))
      return true; 
    if (paramString.toLowerCase().equals("last"))
      return true; 
    try {
      int j = Integer.parseInt(paramString);
      return (j > 0 && j <= this.m_Upper + 1);
    } catch (NumberFormatException numberFormatException) {
      return false;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("Usage: Range <rangespec>"); 
      Range range = new Range();
      range.setRanges(paramArrayOfString[0]);
      range.setUpper(9);
      range.setInvert(false);
      System.out.println("Input: " + paramArrayOfString[0] + "\n" + range.toString());
      int[] arrayOfInt = range.getSelection();
      for (byte b = 0; b < arrayOfInt.length; b++)
        System.out.print(" " + (arrayOfInt[b] + 1)); 
      System.out.println("");
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Range.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */