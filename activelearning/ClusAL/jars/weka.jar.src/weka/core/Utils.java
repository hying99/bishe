package weka.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

public final class Utils {
  public static double log2 = Math.log(2.0D);
  
  public static double SMALL = 1.0E-6D;
  
  public static Properties readProperties(String paramString) throws Exception {
    Properties properties1 = new Properties();
    try {
      properties1.load(ClassLoader.getSystemResourceAsStream(paramString));
    } catch (Exception exception) {
      System.err.println("Warning, unable to load properties file from system resource (Utils.java)");
    } 
    int i = paramString.lastIndexOf('/');
    if (i != -1)
      paramString = paramString.substring(i + 1); 
    Properties properties2 = new Properties(properties1);
    File file = new File(System.getProperties().getProperty("user.home") + File.separatorChar + paramString);
    if (file.exists())
      try {
        properties2.load(new FileInputStream(file));
      } catch (Exception exception) {
        throw new Exception("Problem reading user properties: " + file);
      }  
    Properties properties3 = new Properties(properties2);
    file = new File(paramString);
    if (file.exists())
      try {
        properties3.load(new FileInputStream(file));
      } catch (Exception exception) {
        throw new Exception("Problem reading local properties: " + file);
      }  
    return properties3;
  }
  
  public static final double correlation(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt) {
    double d6;
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    if (paramInt <= 1)
      return 1.0D; 
    byte b;
    for (b = 0; b < paramInt; b++) {
      d1 += paramArrayOfdouble1[b];
      d2 += paramArrayOfdouble2[b];
    } 
    d1 /= paramInt;
    d2 /= paramInt;
    for (b = 0; b < paramInt; b++) {
      d3 += (paramArrayOfdouble1[b] - d1) * (paramArrayOfdouble1[b] - d1);
      d4 += (paramArrayOfdouble2[b] - d2) * (paramArrayOfdouble2[b] - d2);
      d5 += (paramArrayOfdouble1[b] - d1) * (paramArrayOfdouble2[b] - d2);
    } 
    if (d3 * d4 == 0.0D) {
      d6 = 1.0D;
    } else {
      d6 = d5 / Math.sqrt(Math.abs(d3 * d4));
    } 
    return d6;
  }
  
  public static String removeSubstring(String paramString1, String paramString2) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    int j = 0;
    while ((j = paramString1.indexOf(paramString2, i)) != -1) {
      stringBuffer.append(paramString1.substring(i, j));
      i = j + paramString2.length();
    } 
    stringBuffer.append(paramString1.substring(i));
    return stringBuffer.toString();
  }
  
  public static String replaceSubstring(String paramString1, String paramString2, String paramString3) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    int j = 0;
    while ((j = paramString1.indexOf(paramString2, i)) != -1) {
      stringBuffer.append(paramString1.substring(i, j));
      stringBuffer.append(paramString3);
      i = j + paramString2.length();
    } 
    stringBuffer.append(paramString1.substring(i));
    return stringBuffer.toString();
  }
  
  public static String padLeft(String paramString, int paramInt) {
    return fixStringLength(paramString, paramInt, false);
  }
  
  public static String padRight(String paramString, int paramInt) {
    return fixStringLength(paramString, paramInt, true);
  }
  
  private static String fixStringLength(String paramString, int paramInt, boolean paramBoolean) {
    if (paramString.length() < paramInt) {
      while (paramString.length() < paramInt)
        paramString = paramBoolean ? paramString.concat(" ") : " ".concat(paramString); 
    } else if (paramString.length() > paramInt) {
      paramString = paramString.substring(0, paramInt);
    } 
    return paramString;
  }
  
  public static String doubleToString(double paramDouble, int paramInt) {
    double d = paramDouble * Math.pow(10.0D, paramInt);
    if (Math.abs(d) < 9.223372036854776E18D) {
      StringBuffer stringBuffer;
      long l = (d > 0.0D) ? (long)(d + 0.5D) : -((long)(Math.abs(d) + 0.5D));
      if (l == 0L) {
        stringBuffer = new StringBuffer(String.valueOf(0));
      } else {
        stringBuffer = new StringBuffer(String.valueOf(l));
      } 
      if (paramInt == 0)
        return stringBuffer.toString(); 
      int i = stringBuffer.length() - paramInt;
      while (true) {
        if ((l < 0L && i < 1) || i < 0) {
          if (l < 0L) {
            stringBuffer.insert(1, '0');
          } else {
            stringBuffer.insert(0, '0');
          } 
          i++;
          continue;
        } 
        stringBuffer.insert(i, '.');
        if (l < 0L && stringBuffer.charAt(1) == '.') {
          stringBuffer.insert(1, '0');
        } else if (stringBuffer.charAt(0) == '.') {
          stringBuffer.insert(0, '0');
        } 
        int j = stringBuffer.length() - 1;
        while (j > i && stringBuffer.charAt(j) == '0')
          stringBuffer.setCharAt(j--, ' '); 
        if (stringBuffer.charAt(j) == '.')
          stringBuffer.setCharAt(j, ' '); 
        return stringBuffer.toString().trim();
      } 
    } 
    return new String("" + paramDouble);
  }
  
  public static String doubleToString(double paramDouble, int paramInt1, int paramInt2) {
    int i;
    String str = doubleToString(paramDouble, paramInt2);
    if (paramInt2 >= paramInt1 || str.indexOf('E') != -1)
      return str; 
    char[] arrayOfChar = new char[paramInt1];
    int j;
    for (j = 0; j < arrayOfChar.length; j++)
      arrayOfChar[j] = ' '; 
    if (paramInt2 > 0) {
      i = str.indexOf('.');
      if (i == -1) {
        i = str.length();
      } else {
        arrayOfChar[paramInt1 - paramInt2 - 1] = '.';
      } 
    } else {
      i = str.length();
    } 
    j = paramInt1 - paramInt2 - i;
    if (paramInt2 > 0)
      j--; 
    if (j < 0)
      return str; 
    int k;
    for (k = 0; k < i; k++)
      arrayOfChar[j + k] = str.charAt(k); 
    for (k = i + 1; k < str.length(); k++)
      arrayOfChar[j + k] = str.charAt(k); 
    return new String(arrayOfChar);
  }
  
  public static boolean eq(double paramDouble1, double paramDouble2) {
    return (paramDouble1 - paramDouble2 < SMALL && paramDouble2 - paramDouble1 < SMALL);
  }
  
  public static void checkForRemainingOptions(String[] paramArrayOfString) throws Exception {
    byte b1 = 0;
    StringBuffer stringBuffer = new StringBuffer();
    if (paramArrayOfString == null)
      return; 
    for (byte b2 = 0; b2 < paramArrayOfString.length; b2++) {
      if (paramArrayOfString[b2].length() > 0) {
        b1++;
        stringBuffer.append(paramArrayOfString[b2] + ' ');
      } 
    } 
    if (b1 > 0)
      throw new Exception("Illegal options: " + stringBuffer); 
  }
  
  public static boolean getFlag(char paramChar, String[] paramArrayOfString) throws Exception {
    return getFlag("" + paramChar, paramArrayOfString);
  }
  
  public static boolean getFlag(String paramString, String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString == null)
      return false; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].length() > 1 && paramArrayOfString[b].charAt(0) == '-')
        try {
          Double double_ = Double.valueOf(paramArrayOfString[b]);
        } catch (NumberFormatException numberFormatException) {
          if (paramArrayOfString[b].equals("-" + paramString)) {
            paramArrayOfString[b] = "";
            return true;
          } 
          if (paramArrayOfString[b].charAt(1) == '-')
            return false; 
        }  
    } 
    return false;
  }
  
  public static String getOption(char paramChar, String[] paramArrayOfString) throws Exception {
    return getOption("" + paramChar, paramArrayOfString);
  }
  
  public static String getOption(String paramString, String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString == null)
      return ""; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].length() > 0 && paramArrayOfString[b].charAt(0) == '-')
        try {
          Double double_ = Double.valueOf(paramArrayOfString[b]);
        } catch (NumberFormatException numberFormatException) {
          if (paramArrayOfString[b].equals("-" + paramString)) {
            if (b + 1 == paramArrayOfString.length)
              throw new Exception("No value given for -" + paramString + " option."); 
            paramArrayOfString[b] = "";
            String str = new String(paramArrayOfString[b + 1]);
            paramArrayOfString[b + 1] = "";
            return str;
          } 
          if (paramArrayOfString[b].charAt(1) == '-')
            return ""; 
        }  
    } 
    return "";
  }
  
  public static String quote(String paramString) {
    boolean bool = false;
    if (paramString.indexOf('\n') != -1 || paramString.indexOf('\r') != -1 || paramString.indexOf('\'') != -1 || paramString.indexOf('"') != -1 || paramString.indexOf('\\') != -1 || paramString.indexOf('\t') != -1 || paramString.indexOf('%') != -1) {
      paramString = backQuoteChars(paramString);
      bool = true;
    } 
    if (bool == true || paramString.indexOf('{') != -1 || paramString.indexOf('}') != -1 || paramString.indexOf(',') != -1 || paramString.equals("?") || paramString.indexOf(' ') != -1 || paramString.equals(""))
      paramString = "'".concat(paramString).concat("'"); 
    return paramString;
  }
  
  public static String backQuoteChars(String paramString) {
    char[] arrayOfChar = { '\\', '\'', '\t', '"', '%' };
    String[] arrayOfString = { "\\\\", "\\'", "\\t", "\\\"", "\\%" };
    for (byte b = 0; b < arrayOfChar.length; b++) {
      if (paramString.indexOf(arrayOfChar[b]) != -1) {
        StringBuffer stringBuffer = new StringBuffer();
        int i;
        while ((i = paramString.indexOf(arrayOfChar[b])) != -1) {
          if (i > 0)
            stringBuffer.append(paramString.substring(0, i)); 
          stringBuffer.append(arrayOfString[b]);
          if (i + 1 < paramString.length()) {
            paramString = paramString.substring(i + 1);
            continue;
          } 
          paramString = "";
        } 
        stringBuffer.append(paramString);
        paramString = stringBuffer.toString();
      } 
    } 
    return convertNewLines(paramString);
  }
  
  public static String convertNewLines(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    int i;
    while ((i = paramString.indexOf('\n')) != -1) {
      if (i > 0)
        stringBuffer.append(paramString.substring(0, i)); 
      stringBuffer.append('\\');
      stringBuffer.append('n');
      if (i + 1 < paramString.length()) {
        paramString = paramString.substring(i + 1);
        continue;
      } 
      paramString = "";
    } 
    stringBuffer.append(paramString);
    paramString = stringBuffer.toString();
    stringBuffer = new StringBuffer();
    while ((i = paramString.indexOf('\r')) != -1) {
      if (i > 0)
        stringBuffer.append(paramString.substring(0, i)); 
      stringBuffer.append('\\');
      stringBuffer.append('r');
      if (i + 1 < paramString.length()) {
        paramString = paramString.substring(i + 1);
        continue;
      } 
      paramString = "";
    } 
    stringBuffer.append(paramString);
    return stringBuffer.toString();
  }
  
  public static String[] partitionOptions(String[] paramArrayOfString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].equals("--")) {
        paramArrayOfString[b++] = "";
        String[] arrayOfString = new String[paramArrayOfString.length - b];
        for (byte b1 = b; b1 < paramArrayOfString.length; b1++) {
          arrayOfString[b1 - b] = paramArrayOfString[b1];
          paramArrayOfString[b1] = "";
        } 
        return arrayOfString;
      } 
    } 
    return new String[0];
  }
  
  public static String unbackQuoteChars(String paramString) {
    String[] arrayOfString = { "\\\\", "\\'", "\\t", "\\\"", "\\%" };
    char[] arrayOfChar = { '\\', '\'', '\t', '"', '%' };
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.indexOf(arrayOfString[b]) != -1) {
        StringBuffer stringBuffer = new StringBuffer();
        int i;
        while ((i = paramString.indexOf(arrayOfString[b])) != -1) {
          if (i > 0)
            stringBuffer.append(paramString.substring(0, i)); 
          stringBuffer.append(arrayOfChar[b]);
          if (i + arrayOfString[b].length() < paramString.length()) {
            paramString = paramString.substring(i + arrayOfString[b].length());
            continue;
          } 
          paramString = "";
        } 
        stringBuffer.append(paramString);
        paramString = stringBuffer.toString();
      } 
    } 
    return convertNewLines(paramString);
  }
  
  public static String[] splitOptions(String paramString) throws Exception {
    FastVector fastVector = new FastVector();
    for (String str = new String(paramString);; str = str.substring(b)) {
      byte b;
      for (b = 0; b < str.length() && Character.isWhitespace(str.charAt(b)); b++);
      str = str.substring(b);
      if (str.length() == 0) {
        String[] arrayOfString = new String[fastVector.size()];
        for (b = 0; b < fastVector.size(); b++)
          arrayOfString[b] = (String)fastVector.elementAt(b); 
        return arrayOfString;
      } 
      if (str.charAt(0) == '"') {
        for (b = 1; b < str.length() && str.charAt(b) != str.charAt(0); b++) {
          if (str.charAt(b) == '\\') {
            if (++b >= str.length())
              throw new Exception("String should not finish with \\"); 
            if (str.charAt(b) != '\\' && str.charAt(b) != '"')
              throw new Exception("Unknow character \\" + str.charAt(b)); 
          } 
        } 
        if (b >= str.length())
          throw new Exception("Quote parse error."); 
        String str2 = str.substring(1, b);
        str2 = unbackQuoteChars(str2);
        fastVector.addElement(str2);
        str = str.substring(b + 1);
        continue;
      } 
      for (b = 0; b < str.length() && !Character.isWhitespace(str.charAt(b)); b++);
      String str1 = str.substring(0, b);
      fastVector.addElement(str1);
    } 
  }
  
  public static String joinOptions(String[] paramArrayOfString) {
    String str = "";
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (!paramArrayOfString[b].equals("")) {
        if (paramArrayOfString[b].indexOf(' ') != -1) {
          str = str + '"' + paramArrayOfString[b] + '"';
        } else {
          str = str + paramArrayOfString[b];
        } 
        str = str + " ";
      } 
    } 
    return str.trim();
  }
  
  public static Object forName(Class paramClass, String paramString, String[] paramArrayOfString) throws Exception {
    Class clazz = null;
    try {
      clazz = Class.forName(paramString);
    } catch (Exception exception) {
      throw new Exception("Can't find class called: " + paramString);
    } 
    if (!paramClass.isAssignableFrom(clazz))
      throw new Exception(paramClass.getName() + " is not assignable from " + paramString); 
    Object object = clazz.newInstance();
    if (object instanceof OptionHandler && paramArrayOfString != null) {
      ((OptionHandler)object).setOptions(paramArrayOfString);
      checkForRemainingOptions(paramArrayOfString);
    } 
    return object;
  }
  
  public static double info(int[] paramArrayOfint) {
    int i = 0;
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      d -= xlogx(paramArrayOfint[b]);
      i += paramArrayOfint[b];
    } 
    return d + xlogx(i);
  }
  
  public static boolean smOrEq(double paramDouble1, double paramDouble2) {
    return (paramDouble1 - paramDouble2 < SMALL);
  }
  
  public static boolean grOrEq(double paramDouble1, double paramDouble2) {
    return (paramDouble2 - paramDouble1 < SMALL);
  }
  
  public static boolean sm(double paramDouble1, double paramDouble2) {
    return (paramDouble2 - paramDouble1 > SMALL);
  }
  
  public static boolean gr(double paramDouble1, double paramDouble2) {
    return (paramDouble1 - paramDouble2 > SMALL);
  }
  
  public static double log2(double paramDouble) {
    return Math.log(paramDouble) / log2;
  }
  
  public static int maxIndex(double[] paramArrayOfdouble) {
    double d = 0.0D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (b2 == 0 || paramArrayOfdouble[b2] > d) {
        b1 = b2;
        d = paramArrayOfdouble[b2];
      } 
    } 
    return b1;
  }
  
  public static int maxIndex(int[] paramArrayOfint) {
    int i = 0;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfint.length; b2++) {
      if (b2 == 0 || paramArrayOfint[b2] > i) {
        b1 = b2;
        i = paramArrayOfint[b2];
      } 
    } 
    return b1;
  }
  
  public static double mean(double[] paramArrayOfdouble) {
    double d = 0.0D;
    if (paramArrayOfdouble.length == 0)
      return 0.0D; 
    for (byte b = 0; b < paramArrayOfdouble.length; b++)
      d += paramArrayOfdouble[b]; 
    return d / paramArrayOfdouble.length;
  }
  
  public static int minIndex(int[] paramArrayOfint) {
    int i = 0;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfint.length; b2++) {
      if (b2 == 0 || paramArrayOfint[b2] < i) {
        b1 = b2;
        i = paramArrayOfint[b2];
      } 
    } 
    return b1;
  }
  
  public static int minIndex(double[] paramArrayOfdouble) {
    double d = 0.0D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      if (b2 == 0 || paramArrayOfdouble[b2] < d) {
        b1 = b2;
        d = paramArrayOfdouble[b2];
      } 
    } 
    return b1;
  }
  
  public static void normalize(double[] paramArrayOfdouble) {
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++)
      d += paramArrayOfdouble[b]; 
    normalize(paramArrayOfdouble, d);
  }
  
  public static void normalize(double[] paramArrayOfdouble, double paramDouble) {
    if (Double.isNaN(paramDouble))
      throw new IllegalArgumentException("Can't normalize array. Sum is NaN."); 
    if (paramDouble == 0.0D)
      throw new IllegalArgumentException("Can't normalize array. Sum is zero."); 
    for (byte b = 0; b < paramArrayOfdouble.length; b++)
      paramArrayOfdouble[b] = paramArrayOfdouble[b] / paramDouble; 
  }
  
  public static double[] logs2probs(double[] paramArrayOfdouble) {
    double d1 = paramArrayOfdouble[maxIndex(paramArrayOfdouble)];
    double d2 = 0.0D;
    double[] arrayOfDouble = new double[paramArrayOfdouble.length];
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      arrayOfDouble[b] = Math.exp(paramArrayOfdouble[b] - d1);
      d2 += arrayOfDouble[b];
    } 
    normalize(arrayOfDouble, d2);
    return arrayOfDouble;
  }
  
  public static int round(double paramDouble) {
    return (paramDouble > 0.0D) ? (int)(paramDouble + 0.5D) : -((int)(Math.abs(paramDouble) + 0.5D));
  }
  
  public static int probRound(double paramDouble, Random paramRandom) {
    if (paramDouble >= 0.0D) {
      double d3 = Math.floor(paramDouble);
      double d4 = paramDouble - d3;
      return (paramRandom.nextDouble() < d4) ? ((int)d3 + 1) : (int)d3;
    } 
    double d1 = Math.floor(Math.abs(paramDouble));
    double d2 = Math.abs(paramDouble) - d1;
    return (paramRandom.nextDouble() < d2) ? -((int)d1 + 1) : -((int)d1);
  }
  
  public static double roundDouble(double paramDouble, int paramInt) {
    double d = Math.pow(10.0D, paramInt);
    return Math.round(paramDouble * d) / d;
  }
  
  public static int[] sort(int[] paramArrayOfint) {
    int[] arrayOfInt1 = new int[paramArrayOfint.length];
    int[] arrayOfInt2 = new int[paramArrayOfint.length];
    int i;
    for (i = 0; i < arrayOfInt1.length; i++)
      arrayOfInt1[i] = i; 
    quickSort(paramArrayOfint, arrayOfInt1, 0, paramArrayOfint.length - 1);
    for (i = 0; i < arrayOfInt1.length; i++) {
      byte b = 1;
      int j;
      for (j = i + 1; j < arrayOfInt1.length && paramArrayOfint[arrayOfInt1[i]] == paramArrayOfint[arrayOfInt1[j]]; j++)
        b++; 
      if (b > 1) {
        int[] arrayOfInt = new int[b];
        for (j = 0; j < b; j++)
          arrayOfInt[j] = i + j; 
        quickSort(arrayOfInt1, arrayOfInt, 0, b - 1);
        for (j = 0; j < b; j++)
          arrayOfInt2[i + j] = arrayOfInt1[arrayOfInt[j]]; 
        i += b;
        continue;
      } 
      arrayOfInt2[i] = arrayOfInt1[i];
    } 
    return arrayOfInt2;
  }
  
  public static int[] sort(double[] paramArrayOfdouble) {
    int[] arrayOfInt = new int[paramArrayOfdouble.length];
    paramArrayOfdouble = (double[])paramArrayOfdouble.clone();
    for (byte b = 0; b < arrayOfInt.length; b++) {
      arrayOfInt[b] = b;
      if (Double.isNaN(paramArrayOfdouble[b]))
        paramArrayOfdouble[b] = Double.MAX_VALUE; 
    } 
    quickSort(paramArrayOfdouble, arrayOfInt, 0, paramArrayOfdouble.length - 1);
    return arrayOfInt;
  }
  
  public static int[] stableSort(double[] paramArrayOfdouble) {
    int[] arrayOfInt1 = new int[paramArrayOfdouble.length];
    int[] arrayOfInt2 = new int[paramArrayOfdouble.length];
    paramArrayOfdouble = (double[])paramArrayOfdouble.clone();
    int i;
    for (i = 0; i < arrayOfInt1.length; i++) {
      arrayOfInt1[i] = i;
      if (Double.isNaN(paramArrayOfdouble[i]))
        paramArrayOfdouble[i] = Double.MAX_VALUE; 
    } 
    quickSort(paramArrayOfdouble, arrayOfInt1, 0, paramArrayOfdouble.length - 1);
    for (i = 0; i < arrayOfInt1.length; i++) {
      byte b = 1;
      int j;
      for (j = i + 1; j < arrayOfInt1.length && eq(paramArrayOfdouble[arrayOfInt1[i]], paramArrayOfdouble[arrayOfInt1[j]]); j++)
        b++; 
      if (b > 1) {
        int[] arrayOfInt = new int[b];
        for (j = 0; j < b; j++)
          arrayOfInt[j] = i + j; 
        quickSort(arrayOfInt1, arrayOfInt, 0, b - 1);
        for (j = 0; j < b; j++)
          arrayOfInt2[i + j] = arrayOfInt1[arrayOfInt[j]]; 
        i += b;
        continue;
      } 
      arrayOfInt2[i] = arrayOfInt1[i];
    } 
    return arrayOfInt2;
  }
  
  public static double variance(double[] paramArrayOfdouble) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    if (paramArrayOfdouble.length <= 1)
      return 0.0D; 
    for (byte b = 0; b < paramArrayOfdouble.length; b++) {
      d1 += paramArrayOfdouble[b];
      d2 += paramArrayOfdouble[b] * paramArrayOfdouble[b];
    } 
    double d3 = (d2 - d1 * d1 / paramArrayOfdouble.length) / (paramArrayOfdouble.length - 1);
    return (d3 < 0.0D) ? 0.0D : d3;
  }
  
  public static double sum(double[] paramArrayOfdouble) {
    double d = 0.0D;
    for (byte b = 0; b < paramArrayOfdouble.length; b++)
      d += paramArrayOfdouble[b]; 
    return d;
  }
  
  public static int sum(int[] paramArrayOfint) {
    int i = 0;
    for (byte b = 0; b < paramArrayOfint.length; b++)
      i += paramArrayOfint[b]; 
    return i;
  }
  
  public static double xlogx(int paramInt) {
    return (paramInt == 0) ? 0.0D : (paramInt * log2(paramInt));
  }
  
  private static void quickSort(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int paramInt2) {
    int i = paramInt1;
    int j = paramInt2;
    if (paramInt2 > paramInt1) {
      int k = paramArrayOfint1[paramArrayOfint2[(paramInt1 + paramInt2) / 2]];
      while (i <= j) {
        while (paramArrayOfint1[paramArrayOfint2[i]] < k && i < paramInt2)
          i++; 
        while (paramArrayOfint1[paramArrayOfint2[j]] > k && j > paramInt1)
          j--; 
        if (i <= j) {
          int m = paramArrayOfint2[i];
          paramArrayOfint2[i] = paramArrayOfint2[j];
          paramArrayOfint2[j] = m;
          i++;
          j--;
        } 
      } 
      if (paramInt1 < j)
        quickSort(paramArrayOfint1, paramArrayOfint2, paramInt1, j); 
      if (i < paramInt2)
        quickSort(paramArrayOfint1, paramArrayOfint2, i, paramInt2); 
    } 
  }
  
  private static void quickSort(double[] paramArrayOfdouble, int[] paramArrayOfint, int paramInt1, int paramInt2) {
    int i = paramInt1;
    int j = paramInt2;
    if (paramInt2 > paramInt1) {
      double d = paramArrayOfdouble[paramArrayOfint[(paramInt1 + paramInt2) / 2]];
      while (i <= j) {
        while (paramArrayOfdouble[paramArrayOfint[i]] < d && i < paramInt2)
          i++; 
        while (paramArrayOfdouble[paramArrayOfint[j]] > d && j > paramInt1)
          j--; 
        if (i <= j) {
          int k = paramArrayOfint[i];
          paramArrayOfint[i] = paramArrayOfint[j];
          paramArrayOfint[j] = k;
          i++;
          j--;
        } 
      } 
      if (paramInt1 < j)
        quickSort(paramArrayOfdouble, paramArrayOfint, paramInt1, j); 
      if (i < paramInt2)
        quickSort(paramArrayOfdouble, paramArrayOfint, i, paramInt2); 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    double[] arrayOfDouble = { 4.5D, 6.7D, Double.NaN, 3.4D, 4.8D, 1.2D, 3.4D };
    int[] arrayOfInt = { 12, 6, 2, 18, 16, 6, 7, 5 };
    try {
      System.out.println("First option split up:");
      if (paramArrayOfString.length > 0) {
        String[] arrayOfString1 = splitOptions(paramArrayOfString[0]);
        for (byte b = 0; b < arrayOfString1.length; b++)
          System.out.println(arrayOfString1[b]); 
      } 
      System.out.println("Partitioned options: ");
      String[] arrayOfString = partitionOptions(paramArrayOfString);
      byte b1;
      for (b1 = 0; b1 < arrayOfString.length; b1++)
        System.out.println(arrayOfString[b1]); 
      System.out.println("Get flag -f: " + getFlag('f', paramArrayOfString));
      System.out.println("Get option -o: " + getOption('o', paramArrayOfString));
      System.out.println("Checking for remaining options... ");
      checkForRemainingOptions(paramArrayOfString);
      System.out.println("Original array (doubles): ");
      for (b1 = 0; b1 < arrayOfDouble.length; b1++)
        System.out.print(arrayOfDouble[b1] + " "); 
      System.out.println();
      System.out.println("Original array (ints): ");
      for (b1 = 0; b1 < arrayOfInt.length; b1++)
        System.out.print(arrayOfInt[b1] + " "); 
      System.out.println();
      System.out.println("Correlation: " + correlation(arrayOfDouble, arrayOfDouble, arrayOfDouble.length));
      System.out.println("Mean: " + mean(arrayOfDouble));
      System.out.println("Variance: " + variance(arrayOfDouble));
      System.out.println("Sum (doubles): " + sum(arrayOfDouble));
      System.out.println("Sum (ints): " + sum(arrayOfInt));
      System.out.println("Max index (doubles): " + maxIndex(arrayOfDouble));
      System.out.println("Max index (ints): " + maxIndex(arrayOfInt));
      System.out.println("Min index (doubles): " + minIndex(arrayOfDouble));
      System.out.println("Min index (ints): " + minIndex(arrayOfInt));
      System.out.println("Sorted array (doubles): ");
      int[] arrayOfInt1 = sort(arrayOfDouble);
      byte b2;
      for (b2 = 0; b2 < arrayOfDouble.length; b2++)
        System.out.print(arrayOfDouble[arrayOfInt1[b2]] + " "); 
      System.out.println();
      System.out.println("Normalized array (doubles): ");
      normalize(arrayOfDouble);
      for (b2 = 0; b2 < arrayOfDouble.length; b2++)
        System.out.print(arrayOfDouble[b2] + " "); 
      System.out.println();
      System.out.println("Normalized again (doubles): ");
      normalize(arrayOfDouble, sum(arrayOfDouble));
      for (b2 = 0; b2 < arrayOfDouble.length; b2++)
        System.out.print(arrayOfDouble[b2] + " "); 
      System.out.println();
      System.out.println("-4.58: " + doubleToString(-4.57826535D, 2));
      System.out.println("-6.78: " + doubleToString(-6.78214234D, 6, 2));
      System.out.println("5.70001 == 5.7 ? " + eq(5.70001D, 5.7D));
      System.out.println("5.70001 > 5.7 ? " + gr(5.70001D, 5.7D));
      System.out.println("5.70001 >= 5.7 ? " + grOrEq(5.70001D, 5.7D));
      System.out.println("5.7 < 5.70001 ? " + sm(5.7D, 5.70001D));
      System.out.println("5.7 <= 5.70001 ? " + smOrEq(5.7D, 5.70001D));
      System.out.println("Info (ints): " + info(arrayOfInt));
      System.out.println("log2(4.6): " + log2(4.6D));
      System.out.println("5 * log(5): " + xlogx(5));
      System.out.println("5.5 rounded: " + round(5.5D));
      System.out.println("5.55555 rounded to 2 decimal places: " + roundDouble(5.55555D, 2));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Utils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */