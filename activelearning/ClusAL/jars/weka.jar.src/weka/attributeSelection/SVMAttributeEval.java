package weka.attributeSelection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.MakeIndicator;
import weka.filters.unsupervised.attribute.Remove;

public class SVMAttributeEval extends AttributeEvaluator implements OptionHandler {
  private double[] m_attScores;
  
  private int m_numToEliminate = 1;
  
  private int m_percentToEliminate = 0;
  
  private int m_percentThreshold = 0;
  
  private double m_smoCParameter = 1.0D;
  
  private double m_smoTParameter = 1.0E-10D;
  
  private double m_smoPParameter = 1.0E-25D;
  
  private int m_smoFilterType = 0;
  
  public String globalInfo() {
    return "SVMAttributeEval :\n\nEvaluates the worth of an attribute by using an SVM classifier.\n";
  }
  
  public SVMAttributeEval() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify the constant rate of attribute\n\telimination per invocation of\n\tthe support vector machine.\n\tDefault = 1.", "X", 1, "-X <constant rate of elimination>"));
    vector.addElement(new Option("\tSpecify the percentage rate of attributes to\n\telimination per invocation of\n\tthe support vector machine.\n\tTrumps constant rate (above threshold).\n\tDefault = 0.", "Y", 1, "-Y <percent rate of elimination>"));
    vector.addElement(new Option("\tSpecify the threshold below which \n\tpercentage attribute elimination\n\treverts to the constant method.\n", "Z", 1, "-Z <threshold for percent elimination>"));
    vector.addElement(new Option("\tSpecify the value of P (epsilon\n\tparameter) to pass on to the\n\tsupport vector machine.\n\tDefault = 1.0e-25", "P", 1, "-P <epsilon>"));
    vector.addElement(new Option("\tSpecify the value of T (tolerance\n\tparameter) to pass on to the\n\tsupport vector machine.\n\tDefault = 1.0e-10", "T", 1, "-T <tolerance>"));
    vector.addElement(new Option("\tSpecify the value of C (complexity\n\tparameter) to pass on to the\n\tsupport vector machine.\n\tDefault = 1.0", "C", 1, "-C <complexity>"));
    vector.addElement(new Option("\tWhether the SVM should 0=normalize/1=standardize/2=neither. (default 0=normalize)", "N", 1, "-N"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('X', paramArrayOfString);
    if (str.length() != 0)
      setAttsToEliminatePerIteration(Integer.parseInt(str)); 
    str = Utils.getOption('Y', paramArrayOfString);
    if (str.length() != 0)
      setPercentToEliminatePerIteration(Integer.parseInt(str)); 
    str = Utils.getOption('Z', paramArrayOfString);
    if (str.length() != 0)
      setPercentThreshold(Integer.parseInt(str)); 
    str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setEpsilonParameter((new Double(str)).doubleValue()); 
    str = Utils.getOption('T', paramArrayOfString);
    if (str.length() != 0)
      setToleranceParameter((new Double(str)).doubleValue()); 
    str = Utils.getOption('C', paramArrayOfString);
    if (str.length() != 0)
      setComplexityParameter((new Double(str)).doubleValue()); 
    str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0) {
      setFilterType(new SelectedTag(Integer.parseInt(str), SMO.TAGS_FILTER));
    } else {
      setFilterType(new SelectedTag(0, SMO.TAGS_FILTER));
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[14];
    byte b = 0;
    arrayOfString[b++] = "-X";
    arrayOfString[b++] = "" + getAttsToEliminatePerIteration();
    arrayOfString[b++] = "-Y";
    arrayOfString[b++] = "" + getPercentToEliminatePerIteration();
    arrayOfString[b++] = "-Z";
    arrayOfString[b++] = "" + getPercentThreshold();
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + getEpsilonParameter();
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + getToleranceParameter();
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + getComplexityParameter();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_smoFilterType;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String attsToEliminatePerIterationTipText() {
    return "Constant rate of attribute elimination.";
  }
  
  public String percentToEliminatePerIterationTipText() {
    return "Percent rate of attribute elimination.";
  }
  
  public String percentThresholdTipText() {
    return "Threshold below which percent elimination reverts to constant elimination.";
  }
  
  public String epsilonParameterTipText() {
    return "P epsilon parameter to pass to the SVM";
  }
  
  public String toleranceParameterTipText() {
    return "T tolerance parameter to pass to the SVM";
  }
  
  public String complexityParameterTipText() {
    return "C complexity parameter to pass to the SVM";
  }
  
  public String filterTypeTipText() {
    return "filtering used by the SVM";
  }
  
  public void setAttsToEliminatePerIteration(int paramInt) {
    this.m_numToEliminate = paramInt;
  }
  
  public int getAttsToEliminatePerIteration() {
    return this.m_numToEliminate;
  }
  
  public void setPercentToEliminatePerIteration(int paramInt) {
    this.m_percentToEliminate = paramInt;
  }
  
  public int getPercentToEliminatePerIteration() {
    return this.m_percentToEliminate;
  }
  
  public void setPercentThreshold(int paramInt) {
    this.m_percentThreshold = paramInt;
  }
  
  public int getPercentThreshold() {
    return this.m_percentThreshold;
  }
  
  public void setEpsilonParameter(double paramDouble) {
    this.m_smoPParameter = paramDouble;
  }
  
  public double getEpsilonParameter() {
    return this.m_smoPParameter;
  }
  
  public void setToleranceParameter(double paramDouble) {
    this.m_smoTParameter = paramDouble;
  }
  
  public double getToleranceParameter() {
    return this.m_smoTParameter;
  }
  
  public void setComplexityParameter(double paramDouble) {
    this.m_smoCParameter = paramDouble;
  }
  
  public double getComplexityParameter() {
    return this.m_smoCParameter;
  }
  
  public void setFilterType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == SMO.TAGS_FILTER)
      this.m_smoFilterType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getFilterType() {
    return new SelectedTag(this.m_smoFilterType, SMO.TAGS_FILTER);
  }
  
  public void buildEvaluator(Instances paramInstances) throws Exception {
    int[][] arrayOfInt;
    if (paramInstances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    if (!paramInstances.classAttribute().isNominal())
      throw new Exception("Class must be nominal!"); 
    for (byte b1 = 0; b1 < paramInstances.numAttributes(); b1++) {
      if (paramInstances.attribute(b1).isNominal() && paramInstances.attribute(b1).numValues() != 2 && b1 != paramInstances.classIndex())
        throw new Exception("All nominal attributes must be binary!"); 
    } 
    System.out.println("Class attribute: " + paramInstances.attribute(paramInstances.classIndex()).name());
    this.m_numToEliminate = (this.m_numToEliminate > 1) ? this.m_numToEliminate : 1;
    this.m_percentToEliminate = (this.m_percentToEliminate < 100) ? this.m_percentToEliminate : 100;
    this.m_percentToEliminate = (this.m_percentToEliminate > 0) ? this.m_percentToEliminate : 0;
    this.m_percentThreshold = (this.m_percentThreshold < paramInstances.numAttributes()) ? this.m_percentThreshold : (paramInstances.numAttributes() - 1);
    this.m_percentThreshold = (this.m_percentThreshold > 0) ? this.m_percentThreshold : 0;
    int i = paramInstances.numAttributes() - 1;
    if (paramInstances.numClasses() > 2) {
      arrayOfInt = new int[paramInstances.numClasses()][i];
      for (byte b = 0; b < paramInstances.numClasses(); b++)
        arrayOfInt[b] = rankBySVM(b, paramInstances); 
    } else {
      arrayOfInt = new int[1][i];
      arrayOfInt[0] = rankBySVM(0, paramInstances);
    } 
    ArrayList arrayList = new ArrayList(i);
    byte b2 = 0;
    while (b2 < i) {
      byte b = 0;
      while (true) {
        if (b < ((paramInstances.numClasses() > 2) ? paramInstances.numClasses() : 1)) {
          Integer integer = new Integer(arrayOfInt[b][b2]);
          if (!arrayList.contains(integer))
            arrayList.add(integer); 
          b++;
          continue;
        } 
        b2++;
      } 
    } 
    this.m_attScores = new double[paramInstances.numAttributes()];
    Iterator iterator = arrayList.iterator();
    double d;
    for (d = i; iterator.hasNext(); d--)
      this.m_attScores[((Integer)iterator.next()).intValue()] = d; 
  }
  
  private int[] rankBySVM(int paramInt, Instances paramInstances) {
    int[] arrayOfInt1 = new int[paramInstances.numAttributes()];
    int i;
    for (i = 0; i < arrayOfInt1.length; i++)
      arrayOfInt1[i] = i; 
    i = paramInstances.numAttributes() - 1;
    int[] arrayOfInt2 = new int[i];
    try {
      MakeIndicator makeIndicator = new MakeIndicator();
      makeIndicator.setAttributeIndex("" + (paramInstances.classIndex() + 1));
      makeIndicator.setNumeric(false);
      makeIndicator.setValueIndex(paramInt);
      makeIndicator.setInputFormat(paramInstances);
      Instances instances = Filter.useFilter(paramInstances, (Filter)makeIndicator);
      double d = this.m_percentToEliminate / 100.0D;
      while (i > 0) {
        int j;
        if (d > 0.0D) {
          j = (int)(instances.numAttributes() * d);
          j = (j > 1) ? j : 1;
          if (i - j <= this.m_percentThreshold) {
            d = 0.0D;
            j = i - this.m_percentThreshold;
          } 
        } else {
          j = (i >= this.m_numToEliminate) ? this.m_numToEliminate : i;
        } 
        SMO sMO = new SMO();
        sMO.setFilterType(new SelectedTag(this.m_smoFilterType, SMO.TAGS_FILTER));
        sMO.setEpsilon(this.m_smoPParameter);
        sMO.setToleranceParameter(this.m_smoTParameter);
        sMO.setC(this.m_smoCParameter);
        sMO.buildClassifier(instances);
        double[] arrayOfDouble1 = sMO.sparseWeights()[0][1];
        int[] arrayOfInt3 = sMO.sparseIndices()[0][1];
        double[] arrayOfDouble2 = new double[instances.numAttributes()];
        int k;
        for (k = 0; k < arrayOfDouble1.length; k++)
          arrayOfDouble2[arrayOfInt3[k]] = arrayOfDouble1[k] * arrayOfDouble1[k]; 
        arrayOfDouble2[instances.classIndex()] = Double.MAX_VALUE;
        int[] arrayOfInt4 = new int[j];
        boolean[] arrayOfBoolean = new boolean[arrayOfInt1.length];
        for (byte b1 = 0; b1 < j; b1++) {
          k = Utils.minIndex(arrayOfDouble2);
          arrayOfInt2[--i] = arrayOfInt1[k];
          arrayOfInt4[b1] = k;
          arrayOfBoolean[k] = true;
          arrayOfDouble2[k] = Double.MAX_VALUE;
        } 
        Remove remove = new Remove();
        remove.setInvertSelection(false);
        remove.setAttributeIndicesArray(arrayOfInt4);
        remove.setInputFormat(instances);
        instances = Filter.useFilter(instances, (Filter)remove);
        int[] arrayOfInt5 = new int[arrayOfInt1.length - j];
        byte b2 = 0;
        for (byte b3 = 0; b3 < arrayOfInt1.length; b3++) {
          if (!arrayOfBoolean[b3])
            arrayOfInt5[b2++] = arrayOfInt1[b3]; 
        } 
        arrayOfInt1 = arrayOfInt5;
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return arrayOfInt2;
  }
  
  protected void resetOptions() {
    this.m_attScores = null;
  }
  
  public double evaluateAttribute(int paramInt) throws Exception {
    return this.m_attScores[paramInt];
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_attScores == null) {
      stringBuffer.append("\tSVM feature evaluator has not been built yet");
    } else {
      stringBuffer.append("\tSVM feature evaluator");
    } 
    stringBuffer.append("\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(AttributeSelection.SelectAttributes(new SVMAttributeEval(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\SVMAttributeEval.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */