package weka.classifiers.lazy;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.lazy.kstar.KStarCache;
import weka.classifiers.lazy.kstar.KStarConstants;
import weka.classifiers.lazy.kstar.KStarNominalAttribute;
import weka.classifiers.lazy.kstar.KStarNumericAttribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;

public class KStar extends Classifier implements KStarConstants, UpdateableClassifier {
  protected Instances m_Train;
  
  protected int m_NumInstances;
  
  protected int m_NumClasses;
  
  protected int m_NumAttributes;
  
  protected int m_ClassType;
  
  protected int[][] m_RandClassCols;
  
  protected int m_ComputeRandomCols = 1;
  
  protected int m_InitFlag = 1;
  
  protected KStarCache[] m_Cache;
  
  protected int m_MissingMode = 4;
  
  protected int m_BlendMethod = 1;
  
  protected int m_GlobalBlend = 20;
  
  public static final Tag[] TAGS_MISSING = new Tag[] { new Tag(1, "Ignore the instances with missing values"), new Tag(2, "Treat missing values as maximally different"), new Tag(3, "Normalize over the attributes"), new Tag(4, "Average column entropy curves") };
  
  public String globalInfo() {
    return "K* is an instance-based classifier, that is the class of a test instance is based upon the class of those training instances similar to it, as determined by some similarity function.  It differs from other instance-based learners in that it uses an entropy-based distance function. For more information on K*, see\n\nJohn, G. Cleary and Leonard, E. Trigg (1995) \"K*: An Instance- based Learner Using an Entropic Distance Measure\", Proceedings of the 12th International Conference on Machine learning, pp. 108-114.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    String str = "(KStar.buildClassifier) ";
    if (paramInstances.classIndex() < 0)
      throw new Exception("No class attribute assigned to instances"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    this.m_Train = new Instances(paramInstances, 0, paramInstances.numInstances());
    this.m_Train.deleteWithMissingClass();
    init_m_Attributes();
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    String str = "(KStar.updateClassifier) ";
    if (!this.m_Train.equalHeaders(paramInstance.dataset()))
      throw new Exception("Incompatible instance types"); 
    if (paramInstance.classIsMissing())
      return; 
    this.m_Train.add(paramInstance);
    update_m_Attributes();
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    String str = "(KStar.distributionForInstance) ";
    double d1 = 0.0D;
    double d2 = 0.0D;
    double[] arrayOfDouble1 = new double[this.m_NumClasses];
    double[] arrayOfDouble2 = new double[1];
    byte b;
    for (b = 0; b < arrayOfDouble1.length; b++)
      arrayOfDouble1[b] = 0.0D; 
    arrayOfDouble2[0] = 0.0D;
    if (this.m_InitFlag == 1) {
      if (this.m_BlendMethod == 2)
        generateRandomClassColomns(); 
      this.m_Cache = new KStarCache[this.m_NumAttributes];
      for (b = 0; b < this.m_NumAttributes; b++)
        this.m_Cache[b] = new KStarCache(); 
      this.m_InitFlag = 0;
    } 
    Enumeration enumeration = this.m_Train.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      d1 = instanceTransformationProbability(paramInstance, instance);
      switch (this.m_ClassType) {
        case 1:
          arrayOfDouble1[(int)instance.classValue()] = arrayOfDouble1[(int)instance.classValue()] + d1;
        case 0:
          arrayOfDouble2[0] = arrayOfDouble2[0] + d1 * instance.classValue();
          d2 += d1;
      } 
    } 
    if (this.m_ClassType == 1) {
      double d = Utils.sum(arrayOfDouble1);
      if (d <= 0.0D) {
        for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
          arrayOfDouble1[b1] = (1 / this.m_NumClasses); 
      } else {
        Utils.normalize(arrayOfDouble1, d);
      } 
      return arrayOfDouble1;
    } 
    arrayOfDouble2[0] = (d2 != 0.0D) ? (arrayOfDouble2[0] / d2) : 0.0D;
    return arrayOfDouble2;
  }
  
  private double instanceTransformationProbability(Instance paramInstance1, Instance paramInstance2) {
    String str = "(KStar.instanceTransformationProbability) ";
    double d = 1.0D;
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_NumAttributes; b2++) {
      if (b2 != this.m_Train.classIndex())
        if (paramInstance1.isMissing(b2)) {
          b1++;
        } else {
          d *= attrTransProb(paramInstance1, paramInstance2, b2);
          if (b1 != this.m_NumAttributes) {
            d = Math.pow(d, this.m_NumAttributes / (this.m_NumAttributes - b1));
          } else {
            d = 0.0D;
          } 
        }  
    } 
    return d / this.m_NumInstances;
  }
  
  private double attrTransProb(Instance paramInstance1, Instance paramInstance2, int paramInt) {
    KStarNominalAttribute kStarNominalAttribute;
    KStarNumericAttribute kStarNumericAttribute;
    String str = "(KStar.attrTransProb)";
    double d = 0.0D;
    switch (this.m_Train.attribute(paramInt).type()) {
      case 1:
        kStarNominalAttribute = new KStarNominalAttribute(paramInstance1, paramInstance2, paramInt, this.m_Train, this.m_RandClassCols, this.m_Cache[paramInt]);
        kStarNominalAttribute.setOptions(this.m_MissingMode, this.m_BlendMethod, this.m_GlobalBlend);
        d = kStarNominalAttribute.transProb();
        kStarNominalAttribute = null;
        break;
      case 0:
        kStarNumericAttribute = new KStarNumericAttribute(paramInstance1, paramInstance2, paramInt, this.m_Train, this.m_RandClassCols, this.m_Cache[paramInt]);
        kStarNumericAttribute.setOptions(this.m_MissingMode, this.m_BlendMethod, this.m_GlobalBlend);
        d = kStarNumericAttribute.transProb();
        kStarNumericAttribute = null;
        break;
    } 
    return d;
  }
  
  public String missingModeTipText() {
    return "Determines how missing attribute values are treated.";
  }
  
  public SelectedTag getMissingMode() {
    return new SelectedTag(this.m_MissingMode, TAGS_MISSING);
  }
  
  public void setMissingMode(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_MISSING)
      this.m_MissingMode = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tManual blend setting (default 20%)\n", "B", 1, "-B <num>"));
    vector.addElement(new Option("\tEnable entropic auto-blend setting (symbolic class only)\n", "E", 0, "-E"));
    vector.addElement(new Option("\tSpecify the missing value treatment mode (default a)\n\tValid options are: a(verage), d(elete), m(axdiff), n(ormal)\n", "M", 1, "-M <char>"));
    return vector.elements();
  }
  
  public String globalBlendTipText() {
    return "The parameter for global blending. Values are restricted to [0,100].";
  }
  
  public void setGlobalBlend(int paramInt) {
    this.m_GlobalBlend = paramInt;
    if (this.m_GlobalBlend > 100)
      this.m_GlobalBlend = 100; 
    if (this.m_GlobalBlend < 0)
      this.m_GlobalBlend = 0; 
  }
  
  public int getGlobalBlend() {
    return this.m_GlobalBlend;
  }
  
  public String entropicAutoBlendTipText() {
    return "Whether entropy-based blending is to be used.";
  }
  
  public void setEntropicAutoBlend(boolean paramBoolean) {
    if (paramBoolean) {
      this.m_BlendMethod = 2;
    } else {
      this.m_BlendMethod = 1;
    } 
  }
  
  public boolean getEntropicAutoBlend() {
    return (this.m_BlendMethod == 2);
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = "(KStar.setOptions)";
    String str2 = Utils.getOption('B', paramArrayOfString);
    if (str2.length() != 0)
      setGlobalBlend(Integer.parseInt(str2)); 
    setEntropicAutoBlend(Utils.getFlag('E', paramArrayOfString));
    String str3 = Utils.getOption('M', paramArrayOfString);
    if (str3.length() != 0)
      switch (str3.charAt(0)) {
        case 'a':
          setMissingMode(new SelectedTag(4, TAGS_MISSING));
          break;
        case 'd':
          setMissingMode(new SelectedTag(1, TAGS_MISSING));
          break;
        case 'm':
          setMissingMode(new SelectedTag(2, TAGS_MISSING));
          break;
        case 'n':
          setMissingMode(new SelectedTag(3, TAGS_MISSING));
          break;
        default:
          setMissingMode(new SelectedTag(4, TAGS_MISSING));
          break;
      }  
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b++] = "-B";
    arrayOfString[b++] = "" + this.m_GlobalBlend;
    if (getEntropicAutoBlend())
      arrayOfString[b++] = "-E"; 
    arrayOfString[b++] = "-M";
    if (this.m_MissingMode == 4) {
      arrayOfString[b++] = "a";
    } else if (this.m_MissingMode == 1) {
      arrayOfString[b++] = "d";
    } else if (this.m_MissingMode == 2) {
      arrayOfString[b++] = "m";
    } else if (this.m_MissingMode == 3) {
      arrayOfString[b++] = "n";
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("KStar Beta Verion (0.1b).\nCopyright (c) 1995-97 by Len Trigg (trigg@cs.waikato.ac.nz).\nJava port to Weka by Abdelaziz Mahoui (am14@cs.waikato.ac.nz).\n\nKStar options : ");
    String[] arrayOfString = getOptions();
    for (byte b = 0; b < arrayOfString.length; b++)
      stringBuffer.append(arrayOfString[b] + ' '); 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new KStar(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void init_m_Attributes() {
    try {
      this.m_NumInstances = this.m_Train.numInstances();
      this.m_NumClasses = this.m_Train.numClasses();
      this.m_NumAttributes = this.m_Train.numAttributes();
      this.m_ClassType = this.m_Train.classAttribute().type();
      this.m_InitFlag = 1;
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void update_m_Attributes() {
    this.m_NumInstances = this.m_Train.numInstances();
    this.m_InitFlag = 1;
  }
  
  private void generateRandomClassColomns() {
    String str = "(KStar.generateRandomClassColomns)";
    Random random = new Random(42L);
    this.m_RandClassCols = new int[6][];
    int[] arrayOfInt = classValues();
    for (byte b = 0; b < 5; b++)
      this.m_RandClassCols[b] = randomize(arrayOfInt, random); 
    this.m_RandClassCols[5] = arrayOfInt;
  }
  
  private int[] classValues() {
    String str = "(KStar.classValues)";
    int[] arrayOfInt = new int[this.m_NumInstances];
    for (byte b = 0; b < this.m_NumInstances; b++) {
      try {
        arrayOfInt[b] = (int)this.m_Train.instance(b).classValue();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } 
    return arrayOfInt;
  }
  
  private int[] randomize(int[] paramArrayOfint, Random paramRandom) {
    String str = "(KStar.randomize)";
    int[] arrayOfInt = new int[paramArrayOfint.length];
    System.arraycopy(paramArrayOfint, 0, arrayOfInt, 0, paramArrayOfint.length);
    for (int i = arrayOfInt.length - 1; i > 0; i--) {
      int j = (int)(paramRandom.nextDouble() * i);
      int k = arrayOfInt[i];
      arrayOfInt[i] = arrayOfInt[j];
      arrayOfInt[j] = k;
    } 
    return arrayOfInt;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\KStar.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */