package weka.classifiers.evaluation;

import weka.classifiers.CostMatrix;
import weka.core.FastVector;
import weka.core.Matrix;
import weka.core.Utils;

public class ConfusionMatrix extends Matrix {
  protected String[] m_ClassNames;
  
  public ConfusionMatrix(String[] paramArrayOfString) {
    super(paramArrayOfString.length, paramArrayOfString.length);
    this.m_ClassNames = (String[])paramArrayOfString.clone();
  }
  
  public ConfusionMatrix makeWeighted(CostMatrix paramCostMatrix) throws Exception {
    if (paramCostMatrix.size() != size())
      throw new Exception("Cost and confusion matrices must be the same size"); 
    ConfusionMatrix confusionMatrix = new ConfusionMatrix(this.m_ClassNames);
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++)
        confusionMatrix.setElement(b, b1, getElement(b, b1) * paramCostMatrix.getElement(b, b1)); 
    } 
    return confusionMatrix;
  }
  
  public Object clone() throws CloneNotSupportedException {
    ConfusionMatrix confusionMatrix = (ConfusionMatrix)super.clone();
    confusionMatrix.m_ClassNames = (String[])this.m_ClassNames.clone();
    return confusionMatrix;
  }
  
  public int size() {
    return this.m_ClassNames.length;
  }
  
  public String className(int paramInt) {
    return this.m_ClassNames[paramInt];
  }
  
  public void addPrediction(NominalPrediction paramNominalPrediction) throws Exception {
    if (paramNominalPrediction.predicted() == Prediction.MISSING_VALUE)
      throw new Exception("No predicted value given."); 
    if (paramNominalPrediction.actual() == Prediction.MISSING_VALUE)
      throw new Exception("No actual value given."); 
    addElement((int)paramNominalPrediction.actual(), (int)paramNominalPrediction.predicted(), paramNominalPrediction.weight());
  }
  
  public void addPredictions(FastVector paramFastVector) throws Exception {
    for (byte b = 0; b < paramFastVector.size(); b++)
      addPrediction((NominalPrediction)paramFastVector.elementAt(b)); 
  }
  
  public TwoClassStats getTwoClassStats(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++) {
        if (b == paramInt) {
          if (b1 == paramInt) {
            d2 += getElement(b, b1);
          } else {
            d3 += getElement(b, b1);
          } 
        } else if (b1 == paramInt) {
          d1 += getElement(b, b1);
        } else {
          d4 += getElement(b, b1);
        } 
      } 
    } 
    return new TwoClassStats(d2, d1, d4, d3);
  }
  
  public double correct() {
    double d = 0.0D;
    for (byte b = 0; b < size(); b++)
      d += getElement(b, b); 
    return d;
  }
  
  public double incorrect() {
    double d = 0.0D;
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++) {
        if (b != b1)
          d += getElement(b, b1); 
      } 
    } 
    return d;
  }
  
  public double total() {
    double d = 0.0D;
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++)
        d += getElement(b, b1); 
    } 
    return d;
  }
  
  public double errorRate() {
    return incorrect() / total();
  }
  
  public String toString() {
    return toString("=== Confusion Matrix ===\n");
  }
  
  public String toString(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    char[] arrayOfChar = { 
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
        'u', 'v', 'w', 'x', 'y', 'z' };
    boolean bool = false;
    double d = 0.0D;
    byte b;
    for (b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++) {
        double d1 = getElement(b, b1);
        if (d1 < 0.0D)
          d1 *= -10.0D; 
        if (d1 > d)
          d = d1; 
        double d2 = d1 - Math.rint(d1);
        if (!bool && Math.log(d2) / Math.log(10.0D) >= -2.0D)
          bool = true; 
      } 
    } 
    int i = 1 + Math.max((int)(Math.log(d) / Math.log(10.0D) + (bool ? 3 : false)), (int)(Math.log(size()) / Math.log(arrayOfChar.length)));
    stringBuffer.append(paramString).append("\n");
    for (b = 0; b < size(); b++) {
      if (bool) {
        stringBuffer.append(" ").append(num2ShortID(b, arrayOfChar, i - 3)).append("   ");
      } else {
        stringBuffer.append(" ").append(num2ShortID(b, arrayOfChar, i));
      } 
    } 
    stringBuffer.append("     actual class\n");
    for (b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < size(); b1++)
        stringBuffer.append(" ").append(Utils.doubleToString(getElement(b, b1), i, bool ? 2 : 0)); 
      stringBuffer.append(" | ").append(num2ShortID(b, arrayOfChar, i)).append(" = ").append(this.m_ClassNames[b]).append("\n");
    } 
    return stringBuffer.toString();
  }
  
  private static String num2ShortID(int paramInt1, char[] paramArrayOfchar, int paramInt2) {
    char[] arrayOfChar = new char[paramInt2];
    int i;
    for (i = paramInt2 - 1; i >= 0; i--) {
      arrayOfChar[i] = paramArrayOfchar[paramInt1 % paramArrayOfchar.length];
      paramInt1 = paramInt1 / paramArrayOfchar.length - 1;
      if (paramInt1 < 0)
        break; 
    } 
    while (--i >= 0) {
      arrayOfChar[i] = ' ';
      i--;
    } 
    return new String(arrayOfChar);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\evaluation\ConfusionMatrix.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */