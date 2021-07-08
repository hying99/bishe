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
import weka.filters.unsupervised.attribute.NumericToBinary;

public class InfoGainAttributeEval extends AttributeEvaluator implements OptionHandler {
  private boolean m_missing_merge;
  
  private boolean m_Binarize;
  
  private double[] m_InfoGains;
  
  public String globalInfo() {
    return "InfoGainAttributeEval :\n\nEvaluates the worth of an attribute by measuring the information gain with respect to the class.\n\nInfoGain(Class,Attribute) = H(Class) - H(Class | Attribute).\n";
  }
  
  public InfoGainAttributeEval() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\ttreat missing values as a seperate value.", "M", 0, "-M"));
    vector.addElement(new Option("\tjust binarize numeric attributes instead\n \tof properly discretizing them.", "B", 0, "-B"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    setMissingMerge(!Utils.getFlag('M', paramArrayOfString));
    setBinarizeNumericAttributes(Utils.getFlag('B', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    if (!getMissingMerge())
      arrayOfString[b++] = "-M"; 
    if (getBinarizeNumericAttributes())
      arrayOfString[b++] = "-B"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String binarizeNumericAttributesTipText() {
    return "Just binarize numeric attributes instead of properly discretizing them.";
  }
  
  public void setBinarizeNumericAttributes(boolean paramBoolean) {
    this.m_Binarize = paramBoolean;
  }
  
  public boolean getBinarizeNumericAttributes() {
    return this.m_Binarize;
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
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Can't handle string attributes!"); 
    int i = paramInstances.classIndex();
    if (paramInstances.attribute(i).isNumeric())
      throw new Exception("Class must be nominal!"); 
    int j = paramInstances.numInstances();
    if (!this.m_Binarize) {
      Discretize discretize = new Discretize();
      discretize.setUseBetterEncoding(true);
      discretize.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)discretize);
    } else {
      NumericToBinary numericToBinary = new NumericToBinary();
      numericToBinary.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)numericToBinary);
    } 
    int k = paramInstances.attribute(i).numValues();
    double[][][] arrayOfDouble = new double[paramInstances.numAttributes()][][];
    for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
      if (b1 != i) {
        int m = paramInstances.attribute(b1).numValues();
        arrayOfDouble[b1] = new double[m + 1][k + 1];
      } 
    } 
    double[] arrayOfDouble1 = new double[k + 1];
    byte b2;
    for (b2 = 0; b2 < j; b2++) {
      Instance instance = paramInstances.instance(b2);
      if (instance.classIsMissing()) {
        arrayOfDouble1[k] = arrayOfDouble1[k] + instance.weight();
      } else {
        arrayOfDouble1[(int)instance.classValue()] = arrayOfDouble1[(int)instance.classValue()] + instance.weight();
      } 
    } 
    for (b2 = 0; b2 < arrayOfDouble.length; b2++) {
      if (b2 != i)
        for (byte b = 0; b < arrayOfDouble1.length; b++)
          arrayOfDouble[b2][0][b] = arrayOfDouble1[b];  
    } 
    for (b2 = 0; b2 < j; b2++) {
      Instance instance = paramInstances.instance(b2);
      for (byte b = 0; b < instance.numValues(); b++) {
        if (instance.index(b) != i)
          if (instance.isMissingSparse(b) || instance.classIsMissing()) {
            if (!instance.isMissingSparse(b)) {
              arrayOfDouble[instance.index(b)][(int)instance.valueSparse(b)][k] = arrayOfDouble[instance.index(b)][(int)instance.valueSparse(b)][k] + instance.weight();
              arrayOfDouble[instance.index(b)][0][k] = arrayOfDouble[instance.index(b)][0][k] - instance.weight();
            } else if (!instance.classIsMissing()) {
              arrayOfDouble[instance.index(b)][paramInstances.attribute(instance.index(b)).numValues()][(int)instance.classValue()] = arrayOfDouble[instance.index(b)][paramInstances.attribute(instance.index(b)).numValues()][(int)instance.classValue()] + instance.weight();
              arrayOfDouble[instance.index(b)][0][(int)instance.classValue()] = arrayOfDouble[instance.index(b)][0][(int)instance.classValue()] - instance.weight();
            } else {
              arrayOfDouble[instance.index(b)][paramInstances.attribute(instance.index(b)).numValues()][k] = arrayOfDouble[instance.index(b)][paramInstances.attribute(instance.index(b)).numValues()][k] + instance.weight();
              arrayOfDouble[instance.index(b)][0][k] = arrayOfDouble[instance.index(b)][0][k] - instance.weight();
            } 
          } else {
            arrayOfDouble[instance.index(b)][(int)instance.valueSparse(b)][(int)instance.classValue()] = arrayOfDouble[instance.index(b)][(int)instance.valueSparse(b)][(int)instance.classValue()] + instance.weight();
            arrayOfDouble[instance.index(b)][0][(int)instance.classValue()] = arrayOfDouble[instance.index(b)][0][(int)instance.classValue()] - instance.weight();
          }  
      } 
    } 
    if (this.m_missing_merge)
      for (b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
        if (b2 != i) {
          int m = paramInstances.attribute(b2).numValues();
          double[] arrayOfDouble2 = new double[m];
          double[] arrayOfDouble3 = new double[k];
          double d = 0.0D;
          for (byte b = 0; b < m; b++) {
            for (byte b3 = 0; b3 < k; b3++) {
              arrayOfDouble2[b] = arrayOfDouble2[b] + arrayOfDouble[b2][b][b3];
              arrayOfDouble3[b3] = arrayOfDouble3[b3] + arrayOfDouble[b2][b][b3];
            } 
            d += arrayOfDouble2[b];
          } 
          if (Utils.gr(d, 0.0D)) {
            double[][] arrayOfDouble4 = new double[m][k];
            byte b3;
            for (b3 = 0; b3 < m; b3++) {
              for (byte b5 = 0; b5 < k; b5++)
                arrayOfDouble4[b3][b5] = arrayOfDouble2[b3] / d * arrayOfDouble[b2][m][b5]; 
            } 
            for (b3 = 0; b3 < k; b3++) {
              for (byte b5 = 0; b5 < m; b5++)
                arrayOfDouble4[b5][b3] = arrayOfDouble4[b5][b3] + arrayOfDouble3[b3] / d * arrayOfDouble[b2][b5][k]; 
            } 
            for (b3 = 0; b3 < k; b3++) {
              for (byte b5 = 0; b5 < m; b5++)
                arrayOfDouble4[b5][b3] = arrayOfDouble4[b5][b3] + arrayOfDouble[b2][b5][b3] / d * arrayOfDouble[b2][m][k]; 
            } 
            double[][] arrayOfDouble5 = new double[m][k];
            for (byte b4 = 0; b4 < m; b4++) {
              for (byte b5 = 0; b5 < k; b5++)
                arrayOfDouble5[b4][b5] = arrayOfDouble[b2][b4][b5] + arrayOfDouble4[b4][b5]; 
            } 
            arrayOfDouble[b2] = arrayOfDouble5;
          } 
        } 
      }  
    this.m_InfoGains = new double[paramInstances.numAttributes()];
    for (b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      if (b2 != i)
        this.m_InfoGains[b2] = ContingencyTables.entropyOverColumns(arrayOfDouble[b2]) - ContingencyTables.entropyConditionedOnRows(arrayOfDouble[b2]); 
    } 
  }
  
  protected void resetOptions() {
    this.m_InfoGains = null;
    this.m_missing_merge = true;
    this.m_Binarize = false;
  }
  
  public double evaluateAttribute(int paramInt) throws Exception {
    return this.m_InfoGains[paramInt];
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_InfoGains == null) {
      stringBuffer.append("Information Gain attribute evaluator has not been built");
    } else {
      stringBuffer.append("\tInformation Gain Ranking Filter");
      if (!this.m_missing_merge)
        stringBuffer.append("\n\tMissing values treated as seperate"); 
      if (this.m_Binarize)
        stringBuffer.append("\n\tNumeric attributes are just binarized"); 
    } 
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new InfoGainAttributeEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\InfoGainAttributeEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */