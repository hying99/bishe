package weka.classifiers.meta;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.MakeIndicator;
import weka.filters.unsupervised.instance.RemoveWithValues;

public class MultiClassClassifier extends Classifier implements OptionHandler {
  private Classifier[] m_Classifiers;
  
  private Filter[] m_ClassFilters;
  
  private Classifier m_Classifier = (Classifier)new ZeroR();
  
  private ZeroR m_ZeroR;
  
  private Attribute m_ClassAttribute;
  
  private Instances m_TwoClassDataset;
  
  protected int m_Seed = 1;
  
  private double m_RandomWidthFactor = 2.0D;
  
  private int m_Method = 0;
  
  public static final int METHOD_1_AGAINST_ALL = 0;
  
  public static final int METHOD_ERROR_RANDOM = 1;
  
  public static final int METHOD_ERROR_EXHAUSTIVE = 2;
  
  public static final int METHOD_1_AGAINST_1 = 3;
  
  public static final Tag[] TAGS_METHOD = new Tag[] { new Tag(0, "1-against-all"), new Tag(1, "Random correction code"), new Tag(2, "Exhaustive correction code"), new Tag(3, "1-against-1") };
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("MultiClassClassifier: class should be nominal!"); 
    if (this.m_Classifier == null)
      throw new Exception("No base classifier has been set!"); 
    this.m_ZeroR = new ZeroR();
    this.m_ZeroR.buildClassifier(paramInstances);
    this.m_TwoClassDataset = null;
    int i = paramInstances.numClasses();
    if (i <= 2) {
      this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, 1);
      this.m_Classifiers[0].buildClassifier(paramInstances);
      this.m_ClassFilters = null;
    } else if (this.m_Method == 3) {
      FastVector fastVector1 = new FastVector();
      int j;
      for (j = 0; j < paramInstances.numClasses(); j++) {
        for (byte b = 0; b < paramInstances.numClasses(); b++) {
          if (b > j) {
            int[] arrayOfInt = new int[2];
            arrayOfInt[0] = j;
            arrayOfInt[1] = b;
            fastVector1.addElement(arrayOfInt);
          } 
        } 
      } 
      i = fastVector1.size();
      this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, i);
      this.m_ClassFilters = new Filter[i];
      for (j = 0; j < i; j++) {
        RemoveWithValues removeWithValues = new RemoveWithValues();
        removeWithValues.setAttributeIndex("" + (paramInstances.classIndex() + 1));
        removeWithValues.setModifyHeader(true);
        removeWithValues.setInvertSelection(true);
        removeWithValues.setNominalIndicesArr((int[])fastVector1.elementAt(j));
        int[] arrayOfInt = (int[])fastVector1.elementAt(j);
        Instances instances2 = new Instances(paramInstances, 0);
        instances2.setClassIndex(-1);
        removeWithValues.setInputFormat(instances2);
        Instances instances1 = Filter.useFilter(paramInstances, (Filter)removeWithValues);
        if (instances1.numInstances() > 0) {
          instances1.setClassIndex(paramInstances.classIndex());
          this.m_Classifiers[j].buildClassifier(instances1);
          this.m_ClassFilters[j] = (Filter)removeWithValues;
        } else {
          this.m_Classifiers[j] = null;
          this.m_ClassFilters[j] = null;
        } 
      } 
      this.m_TwoClassDataset = new Instances(paramInstances, 0);
      j = this.m_TwoClassDataset.classIndex();
      this.m_TwoClassDataset.setClassIndex(-1);
      this.m_TwoClassDataset.deleteAttributeAt(j);
      FastVector fastVector2 = new FastVector();
      fastVector2.addElement("class0");
      fastVector2.addElement("class1");
      this.m_TwoClassDataset.insertAttributeAt(new Attribute("class", fastVector2), j);
      this.m_TwoClassDataset.setClassIndex(j);
    } else {
      RandomCode randomCode;
      StandardCode standardCode;
      ExhaustiveCode exhaustiveCode = null;
      switch (this.m_Method) {
        case 2:
          exhaustiveCode = new ExhaustiveCode(this, i);
          break;
        case 1:
          randomCode = new RandomCode(this, i, (int)(i * this.m_RandomWidthFactor), paramInstances);
          break;
        case 0:
          standardCode = new StandardCode(this, i);
          break;
        default:
          throw new Exception("Unrecognized correction code type");
      } 
      i = standardCode.size();
      this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, i);
      this.m_ClassFilters = (Filter[])new MakeIndicator[i];
      AttributeStats attributeStats = paramInstances.attributeStats(paramInstances.classIndex());
      for (byte b = 0; b < this.m_Classifiers.length; b++) {
        this.m_ClassFilters[b] = (Filter)new MakeIndicator();
        MakeIndicator makeIndicator = (MakeIndicator)this.m_ClassFilters[b];
        makeIndicator.setAttributeIndex("" + (paramInstances.classIndex() + 1));
        makeIndicator.setValueIndices(standardCode.getIndices(b));
        makeIndicator.setNumeric(false);
        makeIndicator.setInputFormat(paramInstances);
        Instances instances = Filter.useFilter(paramInstances, this.m_ClassFilters[b]);
        this.m_Classifiers[b].buildClassifier(instances);
      } 
    } 
    this.m_ClassAttribute = paramInstances.classAttribute();
  }
  
  public double[] individualPredictions(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = null;
    if (this.m_Classifiers.length == 1) {
      arrayOfDouble = new double[1];
      arrayOfDouble[0] = this.m_Classifiers[0].distributionForInstance(paramInstance)[1];
    } else {
      arrayOfDouble = new double[this.m_ClassFilters.length];
      for (byte b = 0; b < this.m_ClassFilters.length; b++) {
        if (this.m_Classifiers[b] != null)
          if (this.m_Method == 3) {
            Instance instance = (Instance)paramInstance.copy();
            instance.setDataset(this.m_TwoClassDataset);
            arrayOfDouble[b] = this.m_Classifiers[b].distributionForInstance(instance)[1];
          } else {
            this.m_ClassFilters[b].input(paramInstance);
            this.m_ClassFilters[b].batchFinished();
            arrayOfDouble[b] = this.m_Classifiers[b].distributionForInstance(this.m_ClassFilters[b].output())[1];
          }  
      } 
    } 
    return arrayOfDouble;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_Classifiers.length == 1)
      return this.m_Classifiers[0].distributionForInstance(paramInstance); 
    double[] arrayOfDouble = new double[paramInstance.numClasses()];
    if (this.m_Method == 3) {
      for (byte b = 0; b < this.m_ClassFilters.length; b++) {
        if (this.m_Classifiers[b] != null) {
          Instance instance = (Instance)paramInstance.copy();
          instance.setDataset(this.m_TwoClassDataset);
          double[] arrayOfDouble1 = this.m_Classifiers[b].distributionForInstance(instance);
          Range range = new Range(((RemoveWithValues)this.m_ClassFilters[b]).getNominalIndices());
          range.setUpper(this.m_ClassAttribute.numValues());
          int[] arrayOfInt = range.getSelection();
          if (arrayOfDouble1[0] > arrayOfDouble1[1]) {
            arrayOfDouble[arrayOfInt[0]] = arrayOfDouble[arrayOfInt[0]] + 1.0D;
          } else {
            arrayOfDouble[arrayOfInt[1]] = arrayOfDouble[arrayOfInt[1]] + 1.0D;
          } 
        } 
      } 
    } else {
      for (byte b = 0; b < this.m_ClassFilters.length; b++) {
        this.m_ClassFilters[b].input(paramInstance);
        this.m_ClassFilters[b].batchFinished();
        double[] arrayOfDouble1 = this.m_Classifiers[b].distributionForInstance(this.m_ClassFilters[b].output());
        for (byte b1 = 0; b1 < this.m_ClassAttribute.numValues(); b1++) {
          if (((MakeIndicator)this.m_ClassFilters[b]).getValueRange().isInRange(b1)) {
            arrayOfDouble[b1] = arrayOfDouble[b1] + arrayOfDouble1[1];
          } else {
            arrayOfDouble[b1] = arrayOfDouble[b1] + arrayOfDouble1[0];
          } 
        } 
      } 
    } 
    if (Utils.gr(Utils.sum(arrayOfDouble), 0.0D)) {
      Utils.normalize(arrayOfDouble);
      return arrayOfDouble;
    } 
    return this.m_ZeroR.distributionForInstance(paramInstance);
  }
  
  public String toString() {
    if (this.m_Classifiers == null)
      return "MultiClassClassifier: No model built yet."; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("MultiClassClassifier\n\n");
    for (byte b = 0; b < this.m_Classifiers.length; b++) {
      stringBuffer.append("Classifier ").append(b + 1);
      if (this.m_Classifiers[b] != null) {
        if (this.m_ClassFilters != null && this.m_ClassFilters[b] != null)
          if (this.m_ClassFilters[b] instanceof RemoveWithValues) {
            Range range = new Range(((RemoveWithValues)this.m_ClassFilters[b]).getNominalIndices());
            range.setUpper(this.m_ClassAttribute.numValues());
            int[] arrayOfInt = range.getSelection();
            stringBuffer.append(", " + (arrayOfInt[0] + 1) + " vs " + (arrayOfInt[1] + 1));
          } else if (this.m_ClassFilters[b] instanceof MakeIndicator) {
            stringBuffer.append(", using indicator values: ");
            stringBuffer.append(((MakeIndicator)this.m_ClassFilters[b]).getValueRange());
          }  
        stringBuffer.append('\n');
        stringBuffer.append(this.m_Classifiers[b].toString() + "\n\n");
      } else {
        stringBuffer.append(" Skipped (no training examples)\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSets the method to use. Valid values are 0 (1-against-all),\n\t1 (random codes), 2 (exhaustive code), and 3 (1-against-1). (default 0)\n", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tSets the multiplier when using random codes. (default 2.0)", "R", 1, "-R <num>"));
    vector.addElement(new Option("\tSets the base classifier.", "W", 1, "-W <base classifier>"));
    vector.addElement(new Option("\tSets the random number seed for random codes.", "Q", 1, "-Q <random number seed>"));
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
    String str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0) {
      setMethod(new SelectedTag(Integer.parseInt(str1), TAGS_METHOD));
    } else {
      setMethod(new SelectedTag(0, TAGS_METHOD));
    } 
    String str2 = Utils.getOption('R', paramArrayOfString);
    if (str2.length() != 0) {
      setRandomWidthFactor((new Double(str2)).doubleValue());
    } else {
      setRandomWidthFactor(2.0D);
    } 
    String str3 = Utils.getOption('Q', paramArrayOfString);
    if (str3.length() != 0) {
      setSeed(Integer.parseInt(str3));
    } else {
      setSeed(1);
    } 
    String str4 = Utils.getOption('W', paramArrayOfString);
    if (str4.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str4, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 9];
    int i = 0;
    arrayOfString2[i++] = "-M";
    arrayOfString2[i++] = "" + this.m_Method;
    arrayOfString2[i++] = "-R";
    arrayOfString2[i++] = "" + this.m_RandomWidthFactor;
    arrayOfString2[i++] = "-Q";
    arrayOfString2[i++] = "" + getSeed();
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
  
  public String globalInfo() {
    return "A metaclassifier for handling multi-class datasets with 2-class classifiers. This classifier is also capable of applying error correcting output codes for increased accuracy.";
  }
  
  public String randomWidthFactorTipText() {
    return "Sets the width multiplier when using random codes. The number of codes generated will be thus number multiplied by the number of classes.";
  }
  
  public double getRandomWidthFactor() {
    return this.m_RandomWidthFactor;
  }
  
  public void setRandomWidthFactor(double paramDouble) {
    this.m_RandomWidthFactor = paramDouble;
  }
  
  public String methodTipText() {
    return "Sets the method to use for transforming the multi-class problem into several 2-class ones.";
  }
  
  public SelectedTag getMethod() {
    return new SelectedTag(this.m_Method, TAGS_METHOD);
  }
  
  public void setMethod(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_METHOD)
      this.m_Method = paramSelectedTag.getSelectedTag().getID(); 
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
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      MultiClassClassifier multiClassClassifier = new MultiClassClassifier();
      System.out.println(Evaluation.evaluateModel(multiClassClassifier, paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
  
  private class ExhaustiveCode extends Code {
    private final MultiClassClassifier this$0;
    
    public ExhaustiveCode(MultiClassClassifier this$0, int param1Int) {
      super(this$0);
      this.this$0 = this$0;
      int i = (int)Math.pow(2.0D, (param1Int - 1)) - 1;
      this.m_Codebits = new boolean[i][param1Int];
      byte b;
      for (b = 0; b < i; b++)
        this.m_Codebits[b][0] = true; 
      for (b = 1; b < param1Int; b++) {
        int j = (int)Math.pow(2.0D, (param1Int - b + 1));
        for (byte b1 = 0; b1 < i; b1++)
          this.m_Codebits[b1][b] = (b1 / j % 2 != 0); 
      } 
    }
  }
  
  private class RandomCode extends Code {
    Random r;
    
    private final MultiClassClassifier this$0;
    
    public RandomCode(MultiClassClassifier this$0, int param1Int1, int param1Int2, Instances param1Instances) {
      super(this$0);
      this.this$0 = this$0;
      this.r = null;
      this.r = param1Instances.getRandomNumberGenerator(this$0.m_Seed);
      param1Int2 = Math.max(param1Int1, param1Int2);
      this.m_Codebits = new boolean[param1Int2][param1Int1];
      byte b = 0;
      do {
        randomize();
      } while (!good() && b++ < 100);
    }
    
    private boolean good() {
      boolean[] arrayOfBoolean1 = new boolean[(this.m_Codebits[0]).length];
      boolean[] arrayOfBoolean2 = new boolean[(this.m_Codebits[0]).length];
      byte b;
      for (b = 0; b < arrayOfBoolean2.length; b++)
        arrayOfBoolean2[b] = true; 
      for (b = 0; b < this.m_Codebits.length; b++) {
        boolean bool1 = false;
        boolean bool2 = true;
        for (byte b1 = 0; b1 < (this.m_Codebits[b]).length; b1++) {
          boolean bool = this.m_Codebits[b][b1];
          bool1 = (bool1 || bool) ? true : false;
          bool2 = (bool2 && bool) ? true : false;
          arrayOfBoolean1[b1] = (arrayOfBoolean1[b1] || bool);
          arrayOfBoolean2[b1] = (arrayOfBoolean2[b1] && bool);
        } 
        if (!bool1 || bool2)
          return false; 
      } 
      for (b = 0; b < arrayOfBoolean1.length; b++) {
        if (!arrayOfBoolean1[b] || arrayOfBoolean2[b])
          return false; 
      } 
      return true;
    }
    
    private void randomize() {
      for (byte b = 0; b < this.m_Codebits.length; b++) {
        for (byte b1 = 0; b1 < (this.m_Codebits[b]).length; b1++) {
          double d = this.r.nextDouble();
          this.m_Codebits[b][b1] = !(d < 0.5D);
        } 
      } 
    }
  }
  
  private class StandardCode extends Code {
    private final MultiClassClassifier this$0;
    
    public StandardCode(MultiClassClassifier this$0, int param1Int) {
      super(this$0);
      this.this$0 = this$0;
      this.m_Codebits = new boolean[param1Int][param1Int];
      for (byte b = 0; b < param1Int; b++)
        this.m_Codebits[b][b] = true; 
    }
  }
  
  private abstract class Code implements Serializable {
    protected boolean[][] m_Codebits;
    
    private final MultiClassClassifier this$0;
    
    private Code(MultiClassClassifier this$0) {
      MultiClassClassifier.this = MultiClassClassifier.this;
    }
    
    public int size() {
      return this.m_Codebits.length;
    }
    
    public String getIndices(int param1Int) {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < (this.m_Codebits[param1Int]).length; b++) {
        if (this.m_Codebits[param1Int][b]) {
          if (stringBuffer.length() != 0)
            stringBuffer.append(','); 
          stringBuffer.append(b + 1);
        } 
      } 
      return stringBuffer.toString();
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < (this.m_Codebits[0]).length; b++) {
        for (byte b1 = 0; b1 < this.m_Codebits.length; b1++)
          stringBuffer.append(this.m_Codebits[b1][b] ? " 1" : " 0"); 
        stringBuffer.append('\n');
      } 
      return stringBuffer.toString();
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\MultiClassClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */