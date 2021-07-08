package weka.classifiers.functions.pace;

import java.text.DecimalFormat;
import java.text.FieldPosition;

public class ExponentialFormat extends DecimalFormat {
  protected DecimalFormat nf;
  
  protected boolean sign;
  
  protected int digits;
  
  protected int exp;
  
  protected boolean trailing = true;
  
  public ExponentialFormat() {
    this(5);
  }
  
  public ExponentialFormat(int paramInt) {
    this(paramInt, false);
  }
  
  public ExponentialFormat(int paramInt, boolean paramBoolean) {
    this(paramInt, 2, true, paramBoolean);
  }
  
  public ExponentialFormat(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    this.digits = paramInt1;
    this.exp = paramInt2;
    this.sign = paramBoolean1;
    this.trailing = paramBoolean2;
    this.nf = new DecimalFormat(pattern());
    this.nf.setPositivePrefix("+");
    this.nf.setNegativePrefix("-");
  }
  
  public int width() {
    if (!this.trailing)
      throw new RuntimeException("flexible width"); 
    return this.sign ? (1 + this.digits + 2 + this.exp) : (this.digits + 2 + this.exp);
  }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    StringBuffer stringBuffer = new StringBuffer(this.nf.format(paramDouble));
    if (this.sign) {
      if (stringBuffer.charAt(0) == '+')
        stringBuffer.setCharAt(0, ' '); 
    } else if (stringBuffer.charAt(0) == '-') {
      stringBuffer.setCharAt(0, '*');
    } else {
      stringBuffer.deleteCharAt(0);
    } 
    return paramStringBuffer.append(stringBuffer);
  }
  
  private String pattern() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("0.");
    byte b;
    for (b = 0; b < this.digits - 1; b++) {
      if (this.trailing) {
        stringBuffer.append('0');
      } else {
        stringBuffer.append('#');
      } 
    } 
    stringBuffer.append('E');
    for (b = 0; b < this.exp; b++)
      stringBuffer.append('0'); 
    return stringBuffer.toString();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\pace\ExponentialFormat.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */