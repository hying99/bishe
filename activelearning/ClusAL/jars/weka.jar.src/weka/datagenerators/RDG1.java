package weka.datagenerators;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class RDG1 extends Generator implements OptionHandler, Serializable {
  private int m_MaxRuleSize = 10;
  
  private int m_MinRuleSize = 1;
  
  private int m_NumIrrelevant = 0;
  
  private int m_NumNumeric = 0;
  
  private int m_Seed = 1;
  
  private boolean m_VoteFlag = false;
  
  private Instances m_DatasetFormat = null;
  
  private Random m_Random = null;
  
  private FastVector m_DecisionList = null;
  
  boolean[] m_AttList_Irr;
  
  private int m_Debug = 0;
  
  public String globalInfo() {
    return "A data generator that produces data randomly with 'boolean' (nominal with values {false,true}) andnumeric attributes by producing a decisionlist.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tmaximum size for rules (default 10) ", "R", 1, "-R <num>"));
    vector.addElement(new Option("\tminimum size for rules (default 1) ", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tnumber of irrelevant attributes (default 0)", "I", 1, "-I <num>"));
    vector.addElement(new Option("\tnumber of numeric attributes (default 0)", "N", 1, "-N"));
    vector.addElement(new Option("\tseed for random function (default 1)", "S", 1, "-S"));
    vector.addElement(new Option("\tswitch on voting (default is no voting)", "V", 1, "-V"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    boolean bool = false;
    String str1 = Utils.getOption('R', paramArrayOfString);
    if (str1.length() != 0) {
      setMaxRuleSize((int)Double.valueOf(str1).doubleValue());
    } else {
      setMaxRuleSize(10);
    } 
    str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0) {
      setMinRuleSize((int)Double.valueOf(str1).doubleValue());
    } else {
      setMinRuleSize(1);
    } 
    String str2 = Utils.getOption('I', paramArrayOfString);
    if (str2.length() != 0) {
      setNumIrrelevant((int)Double.valueOf(str2).doubleValue());
    } else {
      setNumIrrelevant(0);
    } 
    if (getNumAttributes() - getNumIrrelevant() < getMinRuleSize())
      throw new Exception("Possible rule size is below minimal rule size."); 
    String str3 = Utils.getOption('N', paramArrayOfString);
    if (str3.length() != 0) {
      setNumNumeric((int)Double.valueOf(str3).doubleValue());
    } else {
      setNumNumeric(0);
    } 
    String str4 = Utils.getOption('S', paramArrayOfString);
    if (str4.length() != 0) {
      setSeed(Integer.parseInt(str4));
    } else {
      setSeed(1);
    } 
    bool = Utils.getFlag('V', paramArrayOfString);
    setVoteFlag(bool);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[12];
    byte b = 0;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumNumeric();
    arrayOfString[b++] = "-I";
    arrayOfString[b++] = "" + getNumIrrelevant();
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMinRuleSize();
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + getMaxRuleSize();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    if (getVoteFlag()) {
      arrayOfString[b++] = "-V";
      arrayOfString[b++] = "";
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public Random getRandom() {
    if (this.m_Random == null)
      this.m_Random = new Random(getSeed()); 
    return this.m_Random;
  }
  
  public void setRandom(Random paramRandom) {
    this.m_Random = paramRandom;
  }
  
  public int getMaxRuleSize() {
    return this.m_MaxRuleSize;
  }
  
  public void setMaxRuleSize(int paramInt) {
    this.m_MaxRuleSize = paramInt;
  }
  
  public int getMinRuleSize() {
    return this.m_MinRuleSize;
  }
  
  public void setMinRuleSize(int paramInt) {
    this.m_MinRuleSize = paramInt;
  }
  
  public int getNumIrrelevant() {
    return this.m_NumIrrelevant;
  }
  
  public void setNumIrrelevant(int paramInt) {
    this.m_NumIrrelevant = paramInt;
  }
  
  public int getNumNumeric() {
    return this.m_NumNumeric;
  }
  
  public void setNumNumeric(int paramInt) {
    this.m_NumNumeric = paramInt;
  }
  
  public boolean getVoteFlag() {
    return this.m_VoteFlag;
  }
  
  public void setVoteFlag(boolean paramBoolean) {
    this.m_VoteFlag = paramBoolean;
  }
  
  public boolean getSingleModeFlag() {
    return !getVoteFlag();
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public Instances getDatasetFormat() {
    return this.m_DatasetFormat;
  }
  
  public void setDatasetFormat(Instances paramInstances) {
    this.m_DatasetFormat = paramInstances;
  }
  
  public boolean[] getAttList_Irr() {
    return this.m_AttList_Irr;
  }
  
  public void setAttList_Irr(boolean[] paramArrayOfboolean) {
    this.m_AttList_Irr = paramArrayOfboolean;
  }
  
  public Instances defineDataFormat() throws Exception {
    Random random = new Random(getSeed());
    setRandom(random);
    this.m_DecisionList = new FastVector();
    setNumExamplesAct(getNumExamples());
    return defineDataset(random);
  }
  
  public Instance generateExample() throws Exception {
    Random random = getRandom();
    Instances instances = getDatasetFormat();
    if (instances == null)
      throw new Exception("Dataset format not defined."); 
    if (getVoteFlag())
      throw new Exception("Examples cannot be generated one by one."); 
    instances = generateExamples(1, random, instances);
    return instances.lastInstance();
  }
  
  public Instances generateExamples() throws Exception {
    Random random = getRandom();
    Instances instances = getDatasetFormat();
    if (instances == null)
      throw new Exception("Dataset format not defined."); 
    instances = generateExamples(getNumExamplesAct(), random, instances);
    if (getVoteFlag())
      instances = voteDataset(instances); 
    return instances;
  }
  
  public Instances generateExamples(int paramInt, Random paramRandom, Instances paramInstances) throws Exception {
    if (paramInstances == null)
      throw new Exception("Dataset format not defined."); 
    for (byte b = 0; b < paramInt; b++) {
      Instance instance = generateExample(paramRandom, paramInstances);
      boolean bool = classifyExample(instance);
      if (!bool)
        instance = updateDecisionList(paramRandom, instance); 
      instance.setDataset(paramInstances);
      paramInstances.add(instance);
    } 
    return paramInstances;
  }
  
  private Instance updateDecisionList(Random paramRandom, Instance paramInstance) throws Exception {
    Instances instances = getDatasetFormat();
    if (instances == null)
      throw new Exception("Dataset format not defined."); 
    FastVector fastVector = generateTestList(paramRandom, paramInstance);
    int i = (getMaxRuleSize() < fastVector.size()) ? getMaxRuleSize() : fastVector.size();
    int j = (int)(paramRandom.nextDouble() * (i - getMinRuleSize())) + getMinRuleSize();
    RuleList ruleList = new RuleList();
    for (byte b = 0; b < j; b++) {
      int k = (int)(paramRandom.nextDouble() * fastVector.size());
      Test test = (Test)fastVector.elementAt(k);
      ruleList.addTest(test);
      fastVector.removeElementAt(k);
    } 
    double d = 0.0D;
    if (this.m_DecisionList.size() > 0) {
      RuleList ruleList1 = (RuleList)this.m_DecisionList.lastElement();
      double d1 = ruleList1.getClassValue();
      d = ((int)d1 + 1) % getNumClasses();
    } 
    ruleList.setClassValue(d);
    this.m_DecisionList.addElement(ruleList);
    paramInstance = (Instance)paramInstance.copy();
    paramInstance.setDataset(instances);
    paramInstance.setClassValue(d);
    return paramInstance;
  }
  
  private FastVector generateTestList(Random paramRandom, Instance paramInstance) throws Exception {
    Instances instances = getDatasetFormat();
    if (instances == null)
      throw new Exception("Dataset format not defined."); 
    int i = getNumAttributes() - getNumIrrelevant();
    FastVector fastVector = new FastVector(i);
    boolean[] arrayOfBoolean = getAttList_Irr();
    for (byte b = 0; b < getNumAttributes(); b++) {
      if (!arrayOfBoolean[b]) {
        Test test = null;
        Attribute attribute = paramInstance.attribute(b);
        if (attribute.isNumeric()) {
          double d = paramRandom.nextDouble();
          boolean bool = (d < paramInstance.value(b)) ? true : false;
          test = new Test(b, d, instances, bool);
        } else {
          test = new Test(b, paramInstance.value(b), instances, false);
        } 
        fastVector.addElement(test);
      } 
    } 
    return fastVector;
  }
  
  private Instance generateExample(Random paramRandom, Instances paramInstances) throws Exception {
    double[] arrayOfDouble = new double[getNumAttributes() + 1];
    for (byte b = 0; b < getNumAttributes(); b++) {
      double d = paramRandom.nextDouble();
      if (paramInstances.attribute(b).isNumeric()) {
        arrayOfDouble[b] = d;
      } else if (paramInstances.attribute(b).isNominal()) {
        arrayOfDouble[b] = (d > 0.5D) ? 1.0D : 0.0D;
      } else {
        throw new Exception("Attribute type is not supported.");
      } 
    } 
    Instance instance = new Instance(0.0D, arrayOfDouble);
    instance.setDataset(paramInstances);
    instance.setClassMissing();
    return instance;
  }
  
  private boolean classifyExample(Instance paramInstance) throws Exception {
    double d = -1.0D;
    Enumeration enumeration = this.m_DecisionList.elements();
    while (enumeration.hasMoreElements() && d < 0.0D) {
      RuleList ruleList = enumeration.nextElement();
      d = ruleList.classifyInstance(paramInstance);
    } 
    if (d >= 0.0D) {
      paramInstance.setClassValue(d);
      return true;
    } 
    return false;
  }
  
  private Instance votedReclassifyExample(Instance paramInstance) throws Exception {
    boolean bool = false;
    int[] arrayOfInt = new int[getNumClasses()];
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
      arrayOfInt[b1] = 0; 
    Enumeration enumeration = this.m_DecisionList.elements();
    while (enumeration.hasMoreElements()) {
      RuleList ruleList = enumeration.nextElement();
      int j = (int)ruleList.classifyInstance(paramInstance);
      if (j >= 0)
        arrayOfInt[j] = arrayOfInt[j] + 1; 
    } 
    int i = 0;
    byte b = -1;
    for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
      if (arrayOfInt[b2] > i) {
        i = arrayOfInt[b2];
        b = b2;
      } 
    } 
    if (b >= 0) {
      paramInstance.setClassValue(b);
    } else {
      throw new Exception("Error in instance classification.");
    } 
    return paramInstance;
  }
  
  private Instances defineDataset(Random paramRandom) throws Exception {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector(2);
    fastVector2.addElement("false");
    fastVector2.addElement("true");
    FastVector fastVector3 = new FastVector(getNumClasses());
    boolean[] arrayOfBoolean = defineIrrelevant(paramRandom);
    setAttList_Irr(arrayOfBoolean);
    int[] arrayOfInt = defineNumeric(paramRandom);
    int i;
    for (i = 0; i < getNumAttributes(); i++) {
      Attribute attribute1;
      if (arrayOfInt[i] == 0) {
        attribute1 = new Attribute("a" + i);
      } else {
        attribute1 = new Attribute("a" + i, fastVector2);
      } 
      fastVector1.addElement(attribute1);
    } 
    i = fastVector3.capacity();
    for (byte b = 0; b < fastVector3.capacity(); b++)
      fastVector3.addElement("c" + b); 
    Attribute attribute = new Attribute("class", fastVector3);
    fastVector1.addElement(attribute);
    Instances instances1 = new Instances(getRelationName(), fastVector1, getNumExamplesAct());
    instances1.setClassIndex(getNumAttributes());
    Instances instances2 = new Instances(instances1, 0);
    setDatasetFormat(instances2);
    return instances1;
  }
  
  private boolean[] defineIrrelevant(Random paramRandom) {
    boolean[] arrayOfBoolean = new boolean[getNumAttributes()];
    byte b1;
    for (b1 = 0; b1 < arrayOfBoolean.length; b1++)
      arrayOfBoolean[b1] = false; 
    b1 = 0;
    for (byte b2 = 0; b1 < getNumIrrelevant() && b2 < getNumAttributes() * 5; b2++) {
      int i = (int)(paramRandom.nextDouble() * arrayOfBoolean.length);
      if (!arrayOfBoolean[i]) {
        arrayOfBoolean[i] = true;
        b1++;
      } 
    } 
    return arrayOfBoolean;
  }
  
  private int[] defineNumeric(Random paramRandom) {
    int[] arrayOfInt = new int[getNumAttributes()];
    byte b1;
    for (b1 = 0; b1 < arrayOfInt.length; b1++)
      arrayOfInt[b1] = 1; 
    b1 = 0;
    for (byte b2 = 0; b1 < getNumNumeric() && b2 < getNumAttributes() * 5; b2++) {
      int i = (int)(paramRandom.nextDouble() * arrayOfInt.length);
      if (arrayOfInt[i] != 0) {
        arrayOfInt[i] = 0;
        b1++;
      } 
    } 
    return arrayOfInt;
  }
  
  public String generateFinished() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    boolean[] arrayOfBoolean = getAttList_Irr();
    Instances instances = getDatasetFormat();
    stringBuffer.append("\n%\n% Number of attributes chosen as irrelevant = " + getNumIrrelevant() + "\n");
    byte b;
    for (b = 0; b < arrayOfBoolean.length; b++) {
      if (arrayOfBoolean[b])
        stringBuffer.append("% " + instances.attribute(b).name() + "\n"); 
    } 
    stringBuffer.append("%\n% DECISIONLIST (number of rules = " + this.m_DecisionList.size() + "):\n");
    for (b = 0; b < this.m_DecisionList.size(); b++) {
      RuleList ruleList = (RuleList)this.m_DecisionList.elementAt(b);
      stringBuffer.append("% RULE " + b + ": " + ruleList.toString() + "\n");
    } 
    return stringBuffer.toString();
  }
  
  private Instances voteDataset(Instances paramInstances) throws Exception {
    for (byte b = 0; b < paramInstances.numInstances(); b++) {
      Instance instance = paramInstances.firstInstance();
      instance = votedReclassifyExample(instance);
      paramInstances.add(instance);
      paramInstances.delete(0);
    } 
    return paramInstances;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Generator.makeData(new RDG1(), paramArrayOfString);
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
  
  private class RuleList implements Serializable {
    private FastVector m_RuleList;
    
    double m_ClassValue;
    
    private final RDG1 this$0;
    
    private RuleList(RDG1 this$0) {
      RDG1.this = RDG1.this;
      this.m_RuleList = null;
      this.m_ClassValue = 0.0D;
    }
    
    public double getClassValue() {
      return this.m_ClassValue;
    }
    
    public void setClassValue(double param1Double) {
      this.m_ClassValue = param1Double;
    }
    
    private void addTest(Test param1Test) {
      if (this.m_RuleList == null)
        this.m_RuleList = new FastVector(); 
      this.m_RuleList.addElement(param1Test);
    }
    
    private double classifyInstance(Instance param1Instance) throws Exception {
      boolean bool = true;
      Enumeration enumeration = this.m_RuleList.elements();
      while (bool && enumeration.hasMoreElements()) {
        Test test = enumeration.nextElement();
        bool = test.passesTest(param1Instance);
      } 
      return bool ? this.m_ClassValue : -1.0D;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer = stringBuffer.append("  c" + (int)this.m_ClassValue + " := ");
      Enumeration enumeration = this.m_RuleList.elements();
      if (enumeration.hasMoreElements()) {
        Test test = enumeration.nextElement();
        stringBuffer = stringBuffer.append(test.toPrologString());
      } 
      while (enumeration.hasMoreElements()) {
        Test test = enumeration.nextElement();
        stringBuffer = stringBuffer.append(", " + test.toPrologString());
      } 
      return stringBuffer.toString();
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\datagenerators\RDG1.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */