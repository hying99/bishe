package weka.classifiers.functions.pace;

import java.text.DecimalFormat;
import java.text.FieldPosition;

public class FloatingPointFormat extends DecimalFormat {
  protected DecimalFormat nf;
  
  protected int width;
  
  protected int decimal;
  
  protected boolean trailing = true;
  
  public FloatingPointFormat() {
    this(8, 5);
  }
  
  public FloatingPointFormat(int paramInt) {
    this(8, 2);
  }
  
  public FloatingPointFormat(int paramInt1, int paramInt2) {
    this.width = paramInt1;
    this.decimal = paramInt2;
    this.nf = new DecimalFormat(pattern(paramInt1, paramInt2));
    this.nf.setPositivePrefix(" ");
    this.nf.setNegativePrefix("-");
  }
  
  public FloatingPointFormat(int paramInt1, int paramInt2, boolean paramBoolean) {
    this(paramInt1, paramInt2);
    this.trailing = paramBoolean;
  }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    StringBuffer stringBuffer = new StringBuffer(this.nf.format(paramDouble));
    if (stringBuffer.length() > this.width) {
      if (stringBuffer.charAt(0) == ' ' && stringBuffer.length() == this.width + 1) {
        stringBuffer.deleteCharAt(0);
      } else {
        stringBuffer.setLength(this.width);
        for (byte b = 0; b < this.width; b++)
          stringBuffer.setCharAt(b, '*'); 
      } 
    } else {
      for (byte b = 0; b < this.width - stringBuffer.length(); b++)
        stringBuffer.insert(0, ' '); 
    } 
    if (!this.trailing && this.decimal > 0) {
      while (stringBuffer.charAt(stringBuffer.length() - 1) == '0')
        stringBuffer.deleteCharAt(stringBuffer.length() - 1); 
      if (stringBuffer.charAt(stringBuffer.length() - 1) == '.')
        stringBuffer.deleteCharAt(stringBuffer.length() - 1); 
    } 
    return paramStringBuffer.append(stringBuffer);
  }
  
  public static String pattern(int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(padding(paramInt1 - paramInt2 - 3, '#'));
    if (paramInt2 == 0) {
      stringBuffer.append('0');
    } else {
      stringBuffer.append("0.");
      stringBuffer.append(padding(paramInt2, '0'));
    } 
    return stringBuffer.toString();
  }
  
  private static StringBuffer padding(int paramInt, char paramChar) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramInt; b++)
      stringBuffer.append(paramChar); 
    return stringBuffer;
  }
  
  public int width() {
    if (!this.trailing)
      throw new RuntimeException("flexible width"); 
    return this.width;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\FloatingPointFormat.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */