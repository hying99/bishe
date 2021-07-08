package weka.classifiers.meta;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.MakeIndicator;

public class OrdinalClassClassifier extends Classifier implements OptionHandler {
  private Classifier[] m_Classifiers;
  
  private MakeIndicator[] m_ClassFilters;
  
  private Classifier m_Classifier = (Classifier)new ZeroR();
  
  private Attribute m_ClassAttribute;
  
  private ZeroR m_ZeroR;
  
  public String globalInfo() {
    return " Meta classifier that allows standard classification algorithms to be applied to ordinal class problems.  For more information see: Frank, E. and Hall, M. (in press). A simple approach to ordinal prediction. 12th European Conference on Machine Learning. Freiburg, Germany.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("OrdinalClassClassifier: class should be declared nominal!"); 
    if (this.m_Classifier == null)
      throw new Exception("No base classifier has been set!"); 
    this.m_ZeroR = new ZeroR();
    this.m_ZeroR.buildClassifier(paramInstances);
    int i = paramInstances.numClasses() - 1;
    i = (i == 0) ? 1 : i;
    if (i == 1) {
      this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, 1);
      this.m_Classifiers[0].buildClassifier(paramInstances);
    } else {
      this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, i);
      this.m_ClassFilters = new MakeIndicator[i];
      for (byte b = 0; b < this.m_Classifiers.length; b++) {
        this.m_ClassFilters[b] = new MakeIndicator();
        this.m_ClassFilters[b].setAttributeIndex("" + (paramInstances.classIndex() + 1));
        this.m_ClassFilters[b].setValueIndices("" + (b + 2) + "-last");
        this.m_ClassFilters[b].setNumeric(false);
        this.m_ClassFilters[b].setInputFormat(paramInstances);
        Instances instances = Filter.useFilter(paramInstances, (Filter)this.m_ClassFilters[b]);
        this.m_Classifiers[b].buildClassifier(instances);
      } 
    } 
    this.m_ClassAttribute = paramInstances.classAttribute();
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_Classifiers.length == 1)
      return this.m_Classifiers[0].distributionForInstance(paramInstance); 
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    double[][] arrayOfDouble1 = new double[this.m_ClassFilters.length][0];
    byte b;
    for (b = 0; b < this.m_ClassFilters.length; b++) {
      this.m_ClassFilters[b].input(paramInstance);
      this.m_ClassFilters[b].batchFinished();
      arrayOfDouble1[b] = this.m_Classifiers[b].distributionForInstance(this.m_ClassFilters[b].output());
    } 
    for (b = 0; b < paramInstance.numClasses(); b++) {
      if (b == 0) {
        arrayOfDouble[b] = arrayOfDouble1[0][0];
      } else if (b == paramInstance.numClasses() - 1) {
        arrayOfDouble[b] = arrayOfDouble1[b - 1][1];
      } else {
        arrayOfDouble[b] = arrayOfDouble1[b - 1][1] - arrayOfDouble1[b][1];
        if (arrayOfDouble[b] <= 0.0D) {
          System.err.println("Warning: estimated probability " + arrayOfDouble[b] + ". Rounding to 0.");
          arrayOfDouble[b] = 0.0D;
        } 
      } 
    } 
    if (Utils.gr(Utils.sum(arrayOfDouble), 0.0D)) {
      Utils.normalize(arrayOfDouble);
      return arrayOfDouble;
    } 
    return this.m_ZeroR.distributionForInstance(paramInstance);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSets the base classifier.", "W", 1, "-W <base classifier>"));
    if (this.m_Classifier != null)
      try {
        vector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Classifier.getClass().getName() + ":"));
        Enumeration enumeration = this.m_Classifier.listOptions();
        while (enumeration.hasMoreElements())
          vector.addElement(enumeration.nextElement()); 
      } catch (Exception exception) {} 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('W', paramArrayOfString);
    if (str.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 3];
    int i = 0;
    if (getClassifier() != null) {
      arrayOfString2[i++] = "-W";
      arrayOfString2[i++] = getClassifier().getClass().getName();
    } 
    arrayOfString2[i++] = "--";
    System.arraycopy(arrayOfString1, 0, arrayOfString2, i, arrayOfString1.length);
    i += arrayOfString1.length;
    while (i < arrayOfString2.length)
      arrayOfString2[i++] = ""; 
    return arrayOfString2;
  }
  
  public String classifierTipText() {
    return "Sets the Classifier used as the basis for the multi-class classifier.";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public String toString() {
    if (this.m_Classifiers == null)
      return "OrdinalClassClassifier: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("OrdinalClassClassifier\n\n");
    for (byte b = 0; b < this.m_Classifiers.length; b++) {
      stringBuffer.append("Classifier ").append(b + 1);
      if (this.m_Classifiers[b] != null) {
        if (this.m_ClassFilters != null && this.m_ClassFilters[b] != null) {
          stringBuffer.append(", using indicator values: ");
          stringBuffer.append(this.m_ClassFilters[b].getValueRange());
        } 
        stringBuffer.append('\n');
        stringBuffer.append(this.m_Classifiers[b].toString() + "\n");
      } else {
        stringBuffer.append(" Skipped (no training examples)\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      OrdinalClassClassifier ordinalClassClassifier = new OrdinalClassClassifier();
      System.out.println(Evaluation.evaluateModel(ordinalClassClassifier, paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\OrdinalClassClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */