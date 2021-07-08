package weka.attributeSelection;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class GainRatioAttributeEval extends AttributeEvaluator implements OptionHandler {
  private Instances m_trainInstances;
  
  private int m_classIndex;
  
  private int m_numAttribs;
  
  private int m_numInstances;
  
  private int m_numClasses;
  
  private boolean m_missing_merge;
  
  public String globalInfo() {
    return "GainRatioAttributeEval :\n\nEvaluates the worth of an attribute by measuring the gain ratio with respect to the class.\n\nGainR(Class, Attribute) = (H(Class) - H(Class | Attribute)) / H(Attribute).\n";
  }
  
  public GainRatioAttributeEval() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\ttreat missing values as a seperate value.", "M", 0, "-M"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    setMissingMerge(!Utils.getFlag('M', paramArrayOfString));
  }
  
  public String missingMergeTipText() {
    return "Distribute counts for missing values. Counts are distributed across other values in proportion to their frequency. Otherwise, missing is treated as a separate value.";
  }
  
  public void setMissingMerge(boolean paramBoolean) {
    this.m_missing_merge = paramBoolean;
  }
  
  public boolean getMissingMerge() {
    return this.m_missing_merge;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[1];
    byte b = 0;
    if (!getMissingMerge())
      arrayOfString[b++] = "-M"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    this.m_trainInstances = paramInstances;
    this.m_classIndex = this.m_trainInstances.classIndex();
    this.m_numAttribs = this.m_trainInstances.numAttributes();
    this.m_numInstances = this.m_trainInstances.numInstances();
    if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric())
      throw new Exception("Class must be nominal!"); 
    Discretize discretize = new Discretize();
    discretize.setUseBetterEncoding(true);
    discretize.setInputFormat(this.m_trainInstances);
    this.m_trainInstances = Filter.useFilter(this.m_trainInstances, (Filter)discretize);
    this.m_numClasses = this.m_trainInstances.attribute(this.m_classIndex).numValues();
  }
  
  protected void resetOptions() {
    this.m_trainInstances = null;
    this.m_missing_merge = true;
  }
  
  public double evaluateAttribute(int paramInt) throws Exception {
    double d1 = 0.0D;
    int i = this.m_trainInstances.attribute(paramInt).numValues() + 1;
    int j = this.m_numClasses + 1;
    double d2 = 0.0D;
    double[] arrayOfDouble1 = new double[i];
    double[] arrayOfDouble2 = new double[j];
    double[][] arrayOfDouble = new double[i][j];
    arrayOfDouble1 = new double[i];
    arrayOfDouble2 = new double[j];
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      arrayOfDouble1[b1] = 0.0D;
      for (byte b = 0; b < j; b++) {
        arrayOfDouble2[b] = 0.0D;
        arrayOfDouble[b1][b] = 0.0D;
      } 
    } 
    for (b1 = 0; b1 < this.m_numInstances; b1++) {
      int k;
      int m;
      Instance instance = this.m_trainInstances.instance(b1);
      if (instance.isMissing(paramInt)) {
        k = i - 1;
      } else {
        k = (int)instance.value(paramInt);
      } 
      if (instance.isMissing(this.m_classIndex)) {
        m = j - 1;
      } else {
        m = (int)instance.value(this.m_classIndex);
      } 
      arrayOfDouble[k][m] = arrayOfDouble[k][m] + 1.0D;
    } 
    for (b1 = 0; b1 < i; b1++) {
      arrayOfDouble1[b1] = 0.0D;
      for (byte b = 0; b < j; b++) {
        arrayOfDouble1[b1] = arrayOfDouble1[b1] + arrayOfDouble[b1][b];
        d1 += arrayOfDouble[b1][b];
      } 
    } 
    byte b2;
    for (b2 = 0; b2 < j; b2++) {
      arrayOfDouble2[b2] = 0.0D;
      for (b1 = 0; b1 < i; b1++)
        arrayOfDouble2[b2] = arrayOfDouble2[b2] + arrayOfDouble[b1][b2]; 
    } 
    if (this.m_missing_merge && arrayOfDouble1[i - 1] < this.m_numInstances && arrayOfDouble2[j - 1] < this.m_numInstances) {
      double[] arrayOfDouble3 = new double[arrayOfDouble1.length];
      double[] arrayOfDouble4 = new double[arrayOfDouble2.length];
      double[][] arrayOfDouble5 = new double[arrayOfDouble1.length][arrayOfDouble2.length];
      for (b1 = 0; b1 < i; b1++)
        System.arraycopy(arrayOfDouble[b1], 0, arrayOfDouble5[b1], 0, arrayOfDouble2.length); 
      System.arraycopy(arrayOfDouble1, 0, arrayOfDouble3, 0, arrayOfDouble1.length);
      System.arraycopy(arrayOfDouble2, 0, arrayOfDouble4, 0, arrayOfDouble2.length);
      double d = arrayOfDouble1[i - 1] + arrayOfDouble2[j - 1] - arrayOfDouble[i - 1][j - 1];
      if (arrayOfDouble1[i - 1] > 0.0D)
        for (b2 = 0; b2 < j - 1; b2++) {
          if (arrayOfDouble[i - 1][b2] > 0.0D) {
            for (b1 = 0; b1 < i - 1; b1++) {
              d2 = arrayOfDouble3[b1] / (d1 - arrayOfDouble3[i - 1]) * arrayOfDouble[i - 1][b2];
              arrayOfDouble[b1][b2] = arrayOfDouble[b1][b2] + d2;
              arrayOfDouble1[b1] = arrayOfDouble1[b1] + d2;
            } 
            arrayOfDouble[i - 1][b2] = 0.0D;
          } 
        }  
      arrayOfDouble1[i - 1] = 0.0D;
      if (arrayOfDouble2[j - 1] > 0.0D)
        for (b1 = 0; b1 < i - 1; b1++) {
          if (arrayOfDouble[b1][j - 1] > 0.0D) {
            for (b2 = 0; b2 < j - 1; b2++) {
              d2 = arrayOfDouble4[b2] / (d1 - arrayOfDouble4[j - 1]) * arrayOfDouble[b1][j - 1];
              arrayOfDouble[b1][b2] = arrayOfDouble[b1][b2] + d2;
              arrayOfDouble2[b2] = arrayOfDouble2[b2] + d2;
            } 
            arrayOfDouble[b1][j - 1] = 0.0D;
          } 
        }  
      arrayOfDouble2[j - 1] = 0.0D;
      if (arrayOfDouble[i - 1][j - 1] > 0.0D && d != d1) {
        for (b1 = 0; b1 < i - 1; b1++) {
          for (b2 = 0; b2 < j - 1; b2++) {
            d2 = arrayOfDouble5[b1][b2] / (d1 - d) * arrayOfDouble5[i - 1][j - 1];
            arrayOfDouble[b1][b2] = arrayOfDouble[b1][b2] + d2;
            arrayOfDouble1[b1] = arrayOfDouble1[b1] + d2;
            arrayOfDouble2[b2] = arrayOfDouble2[b2] + d2;
          } 
        } 
        arrayOfDouble[i - 1][j - 1] = 0.0D;
      } 
    } 
    return ContingencyTables.gainRatio(arrayOfDouble);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_trainInstances == null) {
      stringBuffer.append("\tGain Ratio evaluator has not been built");
    } else {
      stringBuffer.append("\tGain Ratio feature evaluator");
      if (!this.m_missing_merge)
        stringBuffer.append("\n\tMissing values treated as seperate"); 
    } 
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new GainRatioAttributeEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\GainRatioAttributeEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */