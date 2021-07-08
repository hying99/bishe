package weka.classifiers.functions.pace;

import java.text.DecimalFormat;
import java.text.FieldPosition;

public class FlexibleDecimalFormat extends DecimalFormat {
  private DecimalFormat nf = null;
  
  private int digits = 7;
  
  private boolean exp = false;
  
  private int intDigits = 1;
  
  private int decimalDigits = 0;
  
  private int expIntDigits = 1;
  
  private int expDecimalDigits = 0;
  
  private int power = 2;
  
  private boolean trailing = false;
  
  private boolean grouping = false;
  
  private boolean sign = false;
  
  public FlexibleDecimalFormat() {
    this(5);
  }
  
  public FlexibleDecimalFormat(int paramInt) {
    if (paramInt < 1)
      throw new IllegalArgumentException("digits < 1"); 
    this.digits = paramInt;
    this.intDigits = 1;
  }
  
  public FlexibleDecimalFormat(int paramInt, boolean paramBoolean) {
    this(paramInt);
    this.trailing = paramBoolean;
  }
  
  public FlexibleDecimalFormat(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    this.trailing = paramBoolean2;
    this.exp = paramBoolean1;
    this.digits = paramInt;
    this.grouping = paramBoolean3;
    if (paramBoolean1) {
      this.intDigits = 1;
      this.decimalDigits = paramInt - this.intDigits;
    } else {
      this.decimalDigits = this.decimalDigits;
      this.intDigits = Math.max(1, paramInt - this.decimalDigits);
    } 
  }
  
  public FlexibleDecimalFormat(double paramDouble) {
    newFormat(paramDouble);
  }
  
  private void newFormat(double paramDouble) {
    if (needExponentialFormat(paramDouble)) {
      this.exp = true;
      this.intDigits = 1;
      this.expDecimalDigits = decimalDigits(paramDouble, true);
      if (paramDouble < 0.0D) {
        this.sign = true;
      } else {
        this.sign = false;
      } 
    } else {
      this.exp = false;
      this.intDigits = Math.max(1, intDigits(paramDouble));
      this.decimalDigits = decimalDigits(paramDouble, false);
      if (paramDouble < 0.0D) {
        this.sign = true;
      } else {
        this.sign = false;
      } 
    } 
  }
  
  public void update(double paramDouble) {
    if (Math.abs(intDigits(paramDouble) - 1) > 99)
      this.power = 3; 
    this.expIntDigits = 1;
    this.expDecimalDigits = Math.max(this.expDecimalDigits, decimalDigits(paramDouble, true));
    if (paramDouble < 0.0D)
      this.sign = true; 
    if (needExponentialFormat(paramDouble) || this.exp) {
      this.exp = true;
    } else {
      this.intDigits = Math.max(this.intDigits, intDigits(paramDouble));
      this.decimalDigits = Math.max(this.decimalDigits, decimalDigits(paramDouble, false));
      if (paramDouble < 0.0D)
        this.sign = true; 
    } 
  }
  
  private static int intDigits(double paramDouble) {
    return (int)Math.floor(Math.log(Math.abs(paramDouble * 1.00000000000001D)) / Math.log(10.0D)) + 1;
  }
  
  private int decimalDigits(double paramDouble, boolean paramBoolean) {
    if (paramDouble == 0.0D)
      return 0; 
    paramDouble = Math.abs(paramDouble);
    int i = intDigits(paramDouble);
    if (paramBoolean) {
      paramDouble /= Math.pow(10.0D, (i - 1));
      i = 1;
    } 
    if (i >= this.digits)
      return 0; 
    int j = Math.max(1, i);
    int k = this.digits - i;
    if (!this.trailing && k > 0) {
      FloatingPointFormat floatingPointFormat = new FloatingPointFormat(j + 1 + k, k, true);
      String str = floatingPointFormat.format(paramDouble);
      while (k > 0 && str.charAt(j + 1 + k - 1) == '0')
        k--; 
    } 
    return k;
  }
  
  public boolean needExponentialFormat(double paramDouble) {
    if (paramDouble == 0.0D)
      return false; 
    int i = intDigits(paramDouble);
    return (i > this.digits + 5 || i < -3);
  }
  
  public void grouping(boolean paramBoolean) {
    this.grouping = paramBoolean;
  }
  
  private static void println(Object paramObject) {
    System.out.println(paramObject);
  }
  
  private void setFormat() {
    byte b = 1;
    if (this.decimalDigits == 0)
      b = 0; 
    if (this.exp) {
      this.nf = new ExponentialFormat(1 + this.expDecimalDigits, this.power, this.sign, (this.grouping || this.trailing));
    } else {
      byte b1 = this.sign ? 1 : 0;
      this.nf = new FloatingPointFormat(b1 + this.intDigits + b + this.decimalDigits, this.decimalDigits, (this.grouping || this.trailing));
    } 
  }
  
  private void setFormat(double paramDouble) {
    newFormat(paramDouble);
    setFormat();
  }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    if (this.grouping) {
      if (this.nf == null)
        setFormat(); 
    } else {
      setFormat(paramDouble);
    } 
    return paramStringBuffer.append(this.nf.format(paramDouble));
  }
  
  public int width() {
    if (!this.trailing && !this.grouping)
      throw new RuntimeException("flexible width"); 
    return format(0.0D).length();
  }
  
  public StringBuffer formatString(String paramString) {
    int i = width();
    int j = (i - paramString.length()) / 2;
    StringBuffer stringBuffer = new StringBuffer();
    byte b;
    for (b = 0; b < j; b++)
      stringBuffer.append(' '); 
    stringBuffer.append(paramString);
    for (b = 0; b < i - j - paramString.length(); b++)
      stringBuffer.append(' '); 
    return stringBuffer;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\FlexibleDecimalFormat.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */