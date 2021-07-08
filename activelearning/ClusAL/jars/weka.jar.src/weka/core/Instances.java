package weka.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Random;
import weka.experiment.Stats;

public class Instances implements Serializable {
  public static String FILE_EXTENSION = ".arff";
  
  public static String SERIALIZED_OBJ_FILE_EXTENSION = ".bsi";
  
  static String ARFF_RELATION = "@relation";
  
  static String ARFF_DATA = "@data";
  
  protected String m_RelationName;
  
  protected FastVector m_Attributes;
  
  protected FastVector m_Instances;
  
  protected int m_ClassIndex;
  
  protected double[] m_ValueBuffer;
  
  protected int[] m_IndicesBuffer;
  
  public Instances(Reader paramReader) throws IOException {
    StreamTokenizer streamTokenizer = new StreamTokenizer(paramReader);
    initTokenizer(streamTokenizer);
    readHeader(streamTokenizer);
    this.m_ClassIndex = -1;
    this.m_Instances = new FastVector(1000);
    while (getInstance(streamTokenizer, true));
    compactify();
  }
  
  public Instances(Reader paramReader, int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IllegalArgumentException("Capacity has to be positive!"); 
    StreamTokenizer streamTokenizer = new StreamTokenizer(paramReader);
    initTokenizer(streamTokenizer);
    readHeader(streamTokenizer);
    this.m_ClassIndex = -1;
    this.m_Instances = new FastVector(paramInt);
  }
  
  public Instances(Instances paramInstances) {
    this(paramInstances, paramInstances.numInstances());
    paramInstances.copyInstances(0, this, paramInstances.numInstances());
  }
  
  public Instances(Instances paramInstances, int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.m_ClassIndex = paramInstances.m_ClassIndex;
    this.m_RelationName = paramInstances.m_RelationName;
    this.m_Attributes = paramInstances.m_Attributes;
    this.m_Instances = new FastVector(paramInt);
  }
  
  public Instances(Instances paramInstances, int paramInt1, int paramInt2) {
    this(paramInstances, paramInt2);
    if (paramInt1 < 0 || paramInt1 + paramInt2 > paramInstances.numInstances())
      throw new IllegalArgumentException("Parameters first and/or toCopy out of range"); 
    paramInstances.copyInstances(paramInt1, this, paramInt2);
  }
  
  public Instances(String paramString, FastVector paramFastVector, int paramInt) {
    this.m_RelationName = paramString;
    this.m_ClassIndex = -1;
    this.m_Attributes = paramFastVector;
    for (byte b = 0; b < numAttributes(); b++)
      attribute(b).setIndex(b); 
    this.m_Instances = new FastVector(paramInt);
  }
  
  public Instances stringFreeStructure() {
    FastVector fastVector = (FastVector)this.m_Attributes.copy();
    for (byte b = 0; b < fastVector.size(); b++) {
      Attribute attribute = (Attribute)fastVector.elementAt(b);
      if (attribute.type() == 2)
        fastVector.setElementAt(new Attribute(attribute.name(), (FastVector)null), b); 
    } 
    Instances instances = new Instances(relationName(), fastVector, 0);
    instances.m_ClassIndex = this.m_ClassIndex;
    return instances;
  }
  
  public void add(Instance paramInstance) {
    Instance instance = (Instance)paramInstance.copy();
    instance.setDataset(this);
    this.m_Instances.addElement(instance);
  }
  
  public Attribute attribute(int paramInt) {
    return (Attribute)this.m_Attributes.elementAt(paramInt);
  }
  
  public Attribute attribute(String paramString) {
    for (byte b = 0; b < numAttributes(); b++) {
      if (attribute(b).name().equals(paramString))
        return attribute(b); 
    } 
    return null;
  }
  
  public boolean checkForStringAttributes() {
    byte b = 0;
    while (b < this.m_Attributes.size()) {
      if (attribute(b++).isString())
        return true; 
    } 
    return false;
  }
  
  public boolean checkInstance(Instance paramInstance) {
    if (paramInstance.numAttributes() != numAttributes())
      return false; 
    for (byte b = 0; b < numAttributes(); b++) {
      if (!paramInstance.isMissing(b) && (attribute(b).isNominal() || attribute(b).isString())) {
        if (!Utils.eq(paramInstance.value(b), (int)paramInstance.value(b)))
          return false; 
        if (Utils.sm(paramInstance.value(b), 0.0D) || Utils.gr(paramInstance.value(b), attribute(b).numValues()))
          return false; 
      } 
    } 
    return true;
  }
  
  public Attribute classAttribute() {
    if (this.m_ClassIndex < 0)
      throw new UnassignedClassException("Class index is negative (not set)!"); 
    return attribute(this.m_ClassIndex);
  }
  
  public int classIndex() {
    return this.m_ClassIndex;
  }
  
  public void compactify() {
    this.m_Instances.trimToSize();
  }
  
  public void delete() {
    this.m_Instances = new FastVector();
  }
  
  public void delete(int paramInt) {
    this.m_Instances.removeElementAt(paramInt);
  }
  
  public void deleteAttributeAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.m_Attributes.size())
      throw new IllegalArgumentException("Index out of range"); 
    if (paramInt == this.m_ClassIndex)
      throw new IllegalArgumentException("Can't delete class attribute"); 
    freshAttributeInfo();
    if (this.m_ClassIndex > paramInt)
      this.m_ClassIndex--; 
    this.m_Attributes.removeElementAt(paramInt);
    int i;
    for (i = paramInt; i < this.m_Attributes.size(); i++) {
      Attribute attribute = (Attribute)this.m_Attributes.elementAt(i);
      attribute.setIndex(attribute.index() - 1);
    } 
    for (i = 0; i < numInstances(); i++)
      instance(i).forceDeleteAttributeAt(paramInt); 
  }
  
  public void deleteStringAttributes() {
    for (byte b = 0; b < this.m_Attributes.size(); b++) {
      if (attribute(b).isString()) {
        deleteAttributeAt(b);
        continue;
      } 
    } 
  }
  
  public void deleteWithMissing(int paramInt) {
    FastVector fastVector = new FastVector(numInstances());
    for (byte b = 0; b < numInstances(); b++) {
      if (!instance(b).isMissing(paramInt))
        fastVector.addElement(instance(b)); 
    } 
    this.m_Instances = fastVector;
  }
  
  public void deleteWithMissing(Attribute paramAttribute) {
    deleteWithMissing(paramAttribute.index());
  }
  
  public void deleteWithMissingClass() {
    if (this.m_ClassIndex < 0)
      throw new UnassignedClassException("Class index is negative (not set)!"); 
    deleteWithMissing(this.m_ClassIndex);
  }
  
  public Enumeration enumerateAttributes() {
    return this.m_Attributes.elements(this.m_ClassIndex);
  }
  
  public Enumeration enumerateInstances() {
    return this.m_Instances.elements();
  }
  
  public boolean equalHeaders(Instances paramInstances) {
    if (this.m_ClassIndex != paramInstances.m_ClassIndex)
      return false; 
    if (this.m_Attributes.size() != paramInstances.m_Attributes.size())
      return false; 
    for (byte b = 0; b < this.m_Attributes.size(); b++) {
      if (!attribute(b).equals(paramInstances.attribute(b)))
        return false; 
    } 
    return true;
  }
  
  public Instance firstInstance() {
    return (Instance)this.m_Instances.firstElement();
  }
  
  public Random getRandomNumberGenerator(long paramLong) {
    Random random = new Random(paramLong);
    random.setSeed(instance(random.nextInt(numInstances())).toString().hashCode() + paramLong);
    return random;
  }
  
  public void insertAttributeAt(Attribute paramAttribute, int paramInt) {
    if (paramInt < 0 || paramInt > this.m_Attributes.size())
      throw new IllegalArgumentException("Index out of range"); 
    paramAttribute = (Attribute)paramAttribute.copy();
    freshAttributeInfo();
    paramAttribute.setIndex(paramInt);
    this.m_Attributes.insertElementAt(paramAttribute, paramInt);
    int i;
    for (i = paramInt + 1; i < this.m_Attributes.size(); i++) {
      Attribute attribute = (Attribute)this.m_Attributes.elementAt(i);
      attribute.setIndex(attribute.index() + 1);
    } 
    for (i = 0; i < numInstances(); i++)
      instance(i).forceInsertAttributeAt(paramInt); 
    if (this.m_ClassIndex >= paramInt)
      this.m_ClassIndex++; 
  }
  
  public Instance instance(int paramInt) {
    return (Instance)this.m_Instances.elementAt(paramInt);
  }
  
  public Instance lastInstance() {
    return (Instance)this.m_Instances.lastElement();
  }
  
  public double meanOrMode(int paramInt) {
    if (attribute(paramInt).isNumeric()) {
      double d2 = 0.0D;
      double d1 = d2;
      for (byte b = 0; b < numInstances(); b++) {
        if (!instance(b).isMissing(paramInt)) {
          d2 += instance(b).weight();
          d1 += instance(b).weight() * instance(b).value(paramInt);
        } 
      } 
      return (d2 <= 0.0D) ? 0.0D : (d1 / d2);
    } 
    if (attribute(paramInt).isNominal()) {
      int[] arrayOfInt = new int[attribute(paramInt).numValues()];
      for (byte b = 0; b < numInstances(); b++) {
        if (!instance(b).isMissing(paramInt))
          arrayOfInt[(int)instance(b).value(paramInt)] = (int)(arrayOfInt[(int)instance(b).value(paramInt)] + instance(b).weight()); 
      } 
      return Utils.maxIndex(arrayOfInt);
    } 
    return 0.0D;
  }
  
  public double meanOrMode(Attribute paramAttribute) {
    return meanOrMode(paramAttribute.index());
  }
  
  public int numAttributes() {
    return this.m_Attributes.size();
  }
  
  public int numClasses() {
    if (this.m_ClassIndex < 0)
      throw new UnassignedClassException("Class index is negative (not set)!"); 
    return !classAttribute().isNominal() ? 1 : classAttribute().numValues();
  }
  
  public int numDistinctValues(int paramInt) {
    if (attribute(paramInt).isNumeric()) {
      double[] arrayOfDouble = attributeToDoubleArray(paramInt);
      int[] arrayOfInt = Utils.sort(arrayOfDouble);
      double d = 0.0D;
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
        Instance instance = instance(arrayOfInt[b2]);
        if (instance.isMissing(paramInt))
          break; 
        if (b2 == 0 || instance.value(paramInt) > d) {
          d = instance.value(paramInt);
          b1++;
        } 
      } 
      return b1;
    } 
    return attribute(paramInt).numValues();
  }
  
  public int numDistinctValues(Attribute paramAttribute) {
    return numDistinctValues(paramAttribute.index());
  }
  
  public int numInstances() {
    return this.m_Instances.size();
  }
  
  public void randomize(Random paramRandom) {
    for (int i = numInstances() - 1; i > 0; i--)
      swap(i, paramRandom.nextInt(i + 1)); 
  }
  
  public boolean readInstance(Reader paramReader) throws IOException {
    StreamTokenizer streamTokenizer = new StreamTokenizer(paramReader);
    initTokenizer(streamTokenizer);
    return getInstance(streamTokenizer, false);
  }
  
  public String relationName() {
    return this.m_RelationName;
  }
  
  public void renameAttribute(int paramInt, String paramString) {
    Attribute attribute = attribute(paramInt).copy(paramString);
    FastVector fastVector = new FastVector(numAttributes());
    for (byte b = 0; b < numAttributes(); b++) {
      if (b == paramInt) {
        fastVector.addElement(attribute);
      } else {
        fastVector.addElement(attribute(b));
      } 
    } 
    this.m_Attributes = fastVector;
  }
  
  public void renameAttribute(Attribute paramAttribute, String paramString) {
    renameAttribute(paramAttribute.index(), paramString);
  }
  
  public void renameAttributeValue(int paramInt1, int paramInt2, String paramString) {
    Attribute attribute = (Attribute)attribute(paramInt1).copy();
    FastVector fastVector = new FastVector(numAttributes());
    attribute.setValue(paramInt2, paramString);
    for (byte b = 0; b < numAttributes(); b++) {
      if (b == paramInt1) {
        fastVector.addElement(attribute);
      } else {
        fastVector.addElement(attribute(b));
      } 
    } 
    this.m_Attributes = fastVector;
  }
  
  public void renameAttributeValue(Attribute paramAttribute, String paramString1, String paramString2) {
    int i = paramAttribute.indexOfValue(paramString1);
    if (i == -1)
      throw new IllegalArgumentException(paramString1 + " not found"); 
    renameAttributeValue(paramAttribute.index(), i, paramString2);
  }
  
  public Instances resample(Random paramRandom) {
    Instances instances = new Instances(this, numInstances());
    while (instances.numInstances() < numInstances())
      instances.add(instance(paramRandom.nextInt(numInstances()))); 
    return instances;
  }
  
  public Instances resampleWithWeights(Random paramRandom) {
    double[] arrayOfDouble = new double[numInstances()];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = instance(b).weight(); 
    return resampleWithWeights(paramRandom, arrayOfDouble);
  }
  
  public Instances resampleWithWeights(Random paramRandom, double[] paramArrayOfdouble) {
    if (paramArrayOfdouble.length != numInstances())
      throw new IllegalArgumentException("weights.length != numInstances."); 
    Instances instances = new Instances(this, numInstances());
    if (numInstances() == 0)
      return instances; 
    double[] arrayOfDouble = new double[numInstances()];
    double d1 = 0.0D;
    double d2 = Utils.sum(paramArrayOfdouble);
    byte b1;
    for (b1 = 0; b1 < numInstances(); b1++) {
      d1 += paramRandom.nextDouble();
      arrayOfDouble[b1] = d1;
    } 
    Utils.normalize(arrayOfDouble, d1 / d2);
    arrayOfDouble[numInstances() - 1] = d2;
    b1 = 0;
    byte b2 = 0;
    d1 = 0.0D;
    while (b1 < numInstances() && b2 < numInstances()) {
      if (paramArrayOfdouble[b2] < 0.0D)
        throw new IllegalArgumentException("Weights have to be positive."); 
      d1 += paramArrayOfdouble[b2];
      while (b1 < numInstances() && arrayOfDouble[b1] <= d1) {
        instances.add(instance(b2));
        instances.instance(b1).setWeight(1.0D);
        b1++;
      } 
      b2++;
    } 
    return instances;
  }
  
  public void setClass(Attribute paramAttribute) {
    this.m_ClassIndex = paramAttribute.index();
  }
  
  public void setClassIndex(int paramInt) {
    if (paramInt >= numAttributes())
      throw new IllegalArgumentException("Invalid class index: " + paramInt); 
    this.m_ClassIndex = paramInt;
  }
  
  public void setRelationName(String paramString) {
    this.m_RelationName = paramString;
  }
  
  public void sort(int paramInt) {
    int i = numInstances() - 1;
    for (byte b = 0; b <= i; b++) {
      if (instance(i).isMissing(paramInt)) {
        i--;
        continue;
      } 
      if (instance(b).isMissing(paramInt)) {
        swap(b, i);
        i--;
      } 
    } 
    quickSort(paramInt, 0, i);
  }
  
  public void sort(Attribute paramAttribute) {
    sort(paramAttribute.index());
  }
  
  public void stratify(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Number of folds must be greater than 1"); 
    if (this.m_ClassIndex < 0)
      throw new UnassignedClassException("Class index is negative (not set)!"); 
    if (classAttribute().isNominal()) {
      for (byte b = 1; b < numInstances(); b++) {
        Instance instance = instance(b - 1);
        for (byte b1 = b; b1 < numInstances(); b1++) {
          Instance instance1 = instance(b1);
          if (instance.classValue() == instance1.classValue() || (instance.classIsMissing() && instance1.classIsMissing())) {
            swap(b, b1);
            b++;
          } 
        } 
      } 
      stratStep(paramInt);
    } 
  }
  
  public double sumOfWeights() {
    double d = 0.0D;
    for (byte b = 0; b < numInstances(); b++)
      d += instance(b).weight(); 
    return d;
  }
  
  public Instances testCV(int paramInt1, int paramInt2) {
    int k;
    if (paramInt1 < 2)
      throw new IllegalArgumentException("Number of folds must be at least 2!"); 
    if (paramInt1 > numInstances())
      throw new IllegalArgumentException("Can't have more folds than instances!"); 
    int i = numInstances() / paramInt1;
    if (paramInt2 < numInstances() % paramInt1) {
      i++;
      k = paramInt2;
    } else {
      k = numInstances() % paramInt1;
    } 
    Instances instances = new Instances(this, i);
    int j = paramInt2 * numInstances() / paramInt1 + k;
    copyInstances(j, instances, i);
    return instances;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(ARFF_RELATION).append(" ").append(Utils.quote(this.m_RelationName)).append("\n\n");
    byte b;
    for (b = 0; b < numAttributes(); b++)
      stringBuffer.append(attribute(b)).append("\n"); 
    stringBuffer.append("\n").append(ARFF_DATA).append("\n");
    for (b = 0; b < numInstances(); b++) {
      stringBuffer.append(instance(b));
      if (b < numInstances() - 1)
        stringBuffer.append('\n'); 
    } 
    return stringBuffer.toString();
  }
  
  public Instances trainCV(int paramInt1, int paramInt2) {
    int k;
    if (paramInt1 < 2)
      throw new IllegalArgumentException("Number of folds must be at least 2!"); 
    if (paramInt1 > numInstances())
      throw new IllegalArgumentException("Can't have more folds than instances!"); 
    int i = numInstances() / paramInt1;
    if (paramInt2 < numInstances() % paramInt1) {
      i++;
      k = paramInt2;
    } else {
      k = numInstances() % paramInt1;
    } 
    Instances instances = new Instances(this, numInstances() - i);
    int j = paramInt2 * numInstances() / paramInt1 + k;
    copyInstances(0, instances, j);
    copyInstances(j + i, instances, numInstances() - j - i);
    return instances;
  }
  
  public Instances trainCV(int paramInt1, int paramInt2, Random paramRandom) {
    Instances instances = trainCV(paramInt1, paramInt2);
    instances.randomize(paramRandom);
    return instances;
  }
  
  public double variance(int paramInt) {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    if (!attribute(paramInt).isNumeric())
      throw new IllegalArgumentException("Can't compute variance because attribute is not numeric!"); 
    for (byte b = 0; b < numInstances(); b++) {
      if (!instance(b).isMissing(paramInt)) {
        d1 += instance(b).weight() * instance(b).value(paramInt);
        d2 += instance(b).weight() * instance(b).value(paramInt) * instance(b).value(paramInt);
        d3 += instance(b).weight();
      } 
    } 
    if (d3 <= 1.0D)
      return 0.0D; 
    double d4 = (d2 - d1 * d1 / d3) / (d3 - 1.0D);
    return (d4 < 0.0D) ? 0.0D : d4;
  }
  
  public double variance(Attribute paramAttribute) {
    return variance(paramAttribute.index());
  }
  
  public AttributeStats attributeStats(int paramInt) {
    AttributeStats attributeStats = new AttributeStats();
    if (attribute(paramInt).isNominal())
      attributeStats.nominalCounts = new int[attribute(paramInt).numValues()]; 
    if (attribute(paramInt).isNumeric())
      attributeStats.numericStats = new Stats(); 
    attributeStats.totalCount = numInstances();
    double[] arrayOfDouble = attributeToDoubleArray(paramInt);
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    byte b1 = 0;
    double d = Instance.missingValue();
    for (byte b2 = 0; b2 < numInstances(); b2++) {
      Instance instance = instance(arrayOfInt[b2]);
      if (instance.isMissing(paramInt)) {
        attributeStats.missingCount = numInstances() - b2;
        break;
      } 
      if (instance.value(paramInt) == d) {
        b1++;
      } else {
        attributeStats.addDistinct(d, b1);
        b1 = 1;
        d = instance.value(paramInt);
      } 
    } 
    attributeStats.addDistinct(d, b1);
    attributeStats.distinctCount--;
    return attributeStats;
  }
  
  public double[] attributeToDoubleArray(int paramInt) {
    double[] arrayOfDouble = new double[numInstances()];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = instance(b).value(paramInt); 
    return arrayOfDouble;
  }
  
  public String toSummaryString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Relation Name:  ").append(relationName()).append('\n');
    stringBuffer.append("Num Instances:  ").append(numInstances()).append('\n');
    stringBuffer.append("Num Attributes: ").append(numAttributes()).append('\n');
    stringBuffer.append('\n');
    stringBuffer.append(Utils.padLeft("", 5)).append(Utils.padRight("Name", 25));
    stringBuffer.append(Utils.padLeft("Type", 5)).append(Utils.padLeft("Nom", 5));
    stringBuffer.append(Utils.padLeft("Int", 5)).append(Utils.padLeft("Real", 5));
    stringBuffer.append(Utils.padLeft("Missing", 12));
    stringBuffer.append(Utils.padLeft("Unique", 12));
    stringBuffer.append(Utils.padLeft("Dist", 6)).append('\n');
    for (byte b = 0; b < numAttributes(); b++) {
      Attribute attribute = attribute(b);
      AttributeStats attributeStats = attributeStats(b);
      stringBuffer.append(Utils.padLeft("" + (b + 1), 4)).append(' ');
      stringBuffer.append(Utils.padRight(attribute.name(), 25)).append(' ');
      switch (attribute.type()) {
        case 1:
          stringBuffer.append(Utils.padLeft("Nom", 4)).append(' ');
          l = Math.round(100.0D * attributeStats.intCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
          l = Math.round(100.0D * attributeStats.realCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          break;
        case 0:
          stringBuffer.append(Utils.padLeft("Num", 4)).append(' ');
          stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
          l = Math.round(100.0D * attributeStats.intCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          l = Math.round(100.0D * attributeStats.realCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          break;
        case 3:
          stringBuffer.append(Utils.padLeft("Dat", 4)).append(' ');
          stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
          l = Math.round(100.0D * attributeStats.intCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          l = Math.round(100.0D * attributeStats.realCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          break;
        case 2:
          stringBuffer.append(Utils.padLeft("Str", 4)).append(' ');
          l = Math.round(100.0D * attributeStats.intCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
          l = Math.round(100.0D * attributeStats.realCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          break;
        default:
          stringBuffer.append(Utils.padLeft("???", 4)).append(' ');
          stringBuffer.append(Utils.padLeft("0", 3)).append("% ");
          l = Math.round(100.0D * attributeStats.intCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          l = Math.round(100.0D * attributeStats.realCount / attributeStats.totalCount);
          stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
          break;
      } 
      stringBuffer.append(Utils.padLeft("" + attributeStats.missingCount, 5)).append(" /");
      long l = Math.round(100.0D * attributeStats.missingCount / attributeStats.totalCount);
      stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
      stringBuffer.append(Utils.padLeft("" + attributeStats.uniqueCount, 5)).append(" /");
      l = Math.round(100.0D * attributeStats.uniqueCount / attributeStats.totalCount);
      stringBuffer.append(Utils.padLeft("" + l, 3)).append("% ");
      stringBuffer.append(Utils.padLeft("" + attributeStats.distinctCount, 5)).append(' ');
      stringBuffer.append('\n');
    } 
    return stringBuffer.toString();
  }
  
  protected boolean getInstance(StreamTokenizer paramStreamTokenizer, boolean paramBoolean) throws IOException {
    if (this.m_Attributes.size() == 0)
      errms(paramStreamTokenizer, "no header information available"); 
    getFirstToken(paramStreamTokenizer);
    return (paramStreamTokenizer.ttype == -1) ? false : ((paramStreamTokenizer.ttype == 123) ? getInstanceSparse(paramStreamTokenizer, paramBoolean) : getInstanceFull(paramStreamTokenizer, paramBoolean));
  }
  
  protected boolean getInstanceSparse(StreamTokenizer paramStreamTokenizer, boolean paramBoolean) throws IOException {
    byte b = 0;
    int i = -1;
    while (true) {
      getIndex(paramStreamTokenizer);
      if (paramStreamTokenizer.ttype == 125) {
        if (paramBoolean)
          getLastToken(paramStreamTokenizer, true); 
        double[] arrayOfDouble = new double[b];
        int[] arrayOfInt = new int[b];
        System.arraycopy(this.m_ValueBuffer, 0, arrayOfDouble, 0, b);
        System.arraycopy(this.m_IndicesBuffer, 0, arrayOfInt, 0, b);
        add(new SparseInstance(1.0D, arrayOfDouble, arrayOfInt, numAttributes()));
        return true;
      } 
      try {
        this.m_IndicesBuffer[b] = Integer.valueOf(paramStreamTokenizer.sval).intValue();
      } catch (NumberFormatException numberFormatException) {
        errms(paramStreamTokenizer, "index number expected");
      } 
      if (this.m_IndicesBuffer[b] <= i)
        errms(paramStreamTokenizer, "indices have to be ordered"); 
      if (this.m_IndicesBuffer[b] < 0 || this.m_IndicesBuffer[b] >= numAttributes())
        errms(paramStreamTokenizer, "index out of bounds"); 
      i = this.m_IndicesBuffer[b];
      getNextToken(paramStreamTokenizer);
      if (paramStreamTokenizer.ttype == 63) {
        this.m_ValueBuffer[b] = Instance.missingValue();
      } else {
        int j;
        if (paramStreamTokenizer.ttype != -3)
          errms(paramStreamTokenizer, "not a valid value"); 
        switch (attribute(this.m_IndicesBuffer[b]).type()) {
          case 1:
            j = attribute(this.m_IndicesBuffer[b]).indexOfValue(paramStreamTokenizer.sval);
            if (j == -1)
              errms(paramStreamTokenizer, "nominal value not declared in header"); 
            this.m_ValueBuffer[b] = j;
            break;
          case 0:
            try {
              this.m_ValueBuffer[b] = Double.valueOf(paramStreamTokenizer.sval).doubleValue();
            } catch (NumberFormatException numberFormatException) {
              errms(paramStreamTokenizer, "number expected");
            } 
            break;
          case 2:
            this.m_ValueBuffer[b] = attribute(this.m_IndicesBuffer[b]).addStringValue(paramStreamTokenizer.sval);
            break;
          case 3:
            try {
              this.m_ValueBuffer[b] = attribute(this.m_IndicesBuffer[b]).parseDate(paramStreamTokenizer.sval);
            } catch (ParseException parseException) {
              errms(paramStreamTokenizer, "unparseable date: " + paramStreamTokenizer.sval);
            } 
            break;
          default:
            errms(paramStreamTokenizer, "unknown attribute type in column " + this.m_IndicesBuffer[b]);
            break;
        } 
      } 
      b++;
    } 
  }
  
  protected boolean getInstanceFull(StreamTokenizer paramStreamTokenizer, boolean paramBoolean) throws IOException {
    double[] arrayOfDouble = new double[numAttributes()];
    for (byte b = 0; b < numAttributes(); b++) {
      if (b > 0)
        getNextToken(paramStreamTokenizer); 
      if (paramStreamTokenizer.ttype == 63) {
        arrayOfDouble[b] = Instance.missingValue();
      } else {
        int i;
        if (paramStreamTokenizer.ttype != -3)
          errms(paramStreamTokenizer, "not a valid value"); 
        switch (attribute(b).type()) {
          case 1:
            i = attribute(b).indexOfValue(paramStreamTokenizer.sval);
            if (i == -1)
              errms(paramStreamTokenizer, "nominal value not declared in header"); 
            arrayOfDouble[b] = i;
            break;
          case 0:
            try {
              arrayOfDouble[b] = Double.valueOf(paramStreamTokenizer.sval).doubleValue();
            } catch (NumberFormatException numberFormatException) {
              errms(paramStreamTokenizer, "number expected");
            } 
            break;
          case 2:
            arrayOfDouble[b] = attribute(b).addStringValue(paramStreamTokenizer.sval);
            break;
          case 3:
            try {
              arrayOfDouble[b] = attribute(b).parseDate(paramStreamTokenizer.sval);
            } catch (ParseException parseException) {
              errms(paramStreamTokenizer, "unparseable date: " + paramStreamTokenizer.sval);
            } 
            break;
          default:
            errms(paramStreamTokenizer, "unknown attribute type in column " + b);
            break;
        } 
      } 
    } 
    if (paramBoolean)
      getLastToken(paramStreamTokenizer, true); 
    add(new Instance(1.0D, arrayOfDouble));
    return true;
  }
  
  protected void readHeader(StreamTokenizer paramStreamTokenizer) throws IOException {
    getFirstToken(paramStreamTokenizer);
    if (paramStreamTokenizer.ttype == -1)
      errms(paramStreamTokenizer, "premature end of file"); 
    if (ARFF_RELATION.equalsIgnoreCase(paramStreamTokenizer.sval)) {
      getNextToken(paramStreamTokenizer);
      this.m_RelationName = paramStreamTokenizer.sval;
      getLastToken(paramStreamTokenizer, false);
    } else {
      errms(paramStreamTokenizer, "keyword " + ARFF_RELATION + " expected");
    } 
    this.m_Attributes = new FastVector();
    getFirstToken(paramStreamTokenizer);
    if (paramStreamTokenizer.ttype == -1)
      errms(paramStreamTokenizer, "premature end of file"); 
    while (Attribute.ARFF_ATTRIBUTE.equalsIgnoreCase(paramStreamTokenizer.sval)) {
      getNextToken(paramStreamTokenizer);
      String str = paramStreamTokenizer.sval;
      getNextToken(paramStreamTokenizer);
      if (paramStreamTokenizer.ttype == -3) {
        if (paramStreamTokenizer.sval.equalsIgnoreCase(Attribute.ARFF_ATTRIBUTE_REAL) || paramStreamTokenizer.sval.equalsIgnoreCase(Attribute.ARFF_ATTRIBUTE_INTEGER) || paramStreamTokenizer.sval.equalsIgnoreCase(Attribute.ARFF_ATTRIBUTE_NUMERIC)) {
          this.m_Attributes.addElement(new Attribute(str, numAttributes()));
          readTillEOL(paramStreamTokenizer);
        } else if (paramStreamTokenizer.sval.equalsIgnoreCase(Attribute.ARFF_ATTRIBUTE_STRING)) {
          this.m_Attributes.addElement(new Attribute(str, (FastVector)null, numAttributes()));
          readTillEOL(paramStreamTokenizer);
        } else if (paramStreamTokenizer.sval.equalsIgnoreCase(Attribute.ARFF_ATTRIBUTE_DATE)) {
          String str1 = null;
          if (paramStreamTokenizer.nextToken() != 10) {
            if (paramStreamTokenizer.ttype != -3 && paramStreamTokenizer.ttype != 39 && paramStreamTokenizer.ttype != 34)
              errms(paramStreamTokenizer, "not a valid date format"); 
            str1 = paramStreamTokenizer.sval;
            readTillEOL(paramStreamTokenizer);
          } else {
            paramStreamTokenizer.pushBack();
          } 
          this.m_Attributes.addElement(new Attribute(str, str1, numAttributes()));
        } else {
          errms(paramStreamTokenizer, "no valid attribute type or invalid enumeration");
        } 
      } else {
        FastVector fastVector = new FastVector();
        paramStreamTokenizer.pushBack();
        if (paramStreamTokenizer.nextToken() != 123)
          errms(paramStreamTokenizer, "{ expected at beginning of enumeration"); 
        while (paramStreamTokenizer.nextToken() != 125) {
          if (paramStreamTokenizer.ttype == 10) {
            errms(paramStreamTokenizer, "} expected at end of enumeration");
            continue;
          } 
          fastVector.addElement(paramStreamTokenizer.sval);
        } 
        if (fastVector.size() == 0)
          errms(paramStreamTokenizer, "no nominal values found"); 
        this.m_Attributes.addElement(new Attribute(str, fastVector, numAttributes()));
      } 
      getLastToken(paramStreamTokenizer, false);
      getFirstToken(paramStreamTokenizer);
      if (paramStreamTokenizer.ttype == -1)
        errms(paramStreamTokenizer, "premature end of file"); 
    } 
    if (!ARFF_DATA.equalsIgnoreCase(paramStreamTokenizer.sval))
      errms(paramStreamTokenizer, "keyword " + ARFF_DATA + " expected"); 
    if (this.m_Attributes.size() == 0)
      errms(paramStreamTokenizer, "no attributes declared"); 
    this.m_ValueBuffer = new double[numAttributes()];
    this.m_IndicesBuffer = new int[numAttributes()];
  }
  
  protected void copyInstances(int paramInt1, Instances paramInstances, int paramInt2) {
    for (byte b = 0; b < paramInt2; b++)
      paramInstances.add(instance(paramInt1 + b)); 
  }
  
  protected void errms(StreamTokenizer paramStreamTokenizer, String paramString) throws IOException {
    throw new IOException(paramString + ", read " + paramStreamTokenizer.toString());
  }
  
  protected void freshAttributeInfo() {
    this.m_Attributes = (FastVector)this.m_Attributes.copyElements();
  }
  
  protected void getFirstToken(StreamTokenizer paramStreamTokenizer) throws IOException {
    while (paramStreamTokenizer.nextToken() == 10);
    if (paramStreamTokenizer.ttype == 39 || paramStreamTokenizer.ttype == 34) {
      paramStreamTokenizer.ttype = -3;
    } else if (paramStreamTokenizer.ttype == -3 && paramStreamTokenizer.sval.equals("?")) {
      paramStreamTokenizer.ttype = 63;
    } 
  }
  
  protected void getIndex(StreamTokenizer paramStreamTokenizer) throws IOException {
    if (paramStreamTokenizer.nextToken() == 10)
      errms(paramStreamTokenizer, "premature end of line"); 
    if (paramStreamTokenizer.ttype == -1)
      errms(paramStreamTokenizer, "premature end of file"); 
  }
  
  protected void getLastToken(StreamTokenizer paramStreamTokenizer, boolean paramBoolean) throws IOException {
    if (paramStreamTokenizer.nextToken() != 10 && (paramStreamTokenizer.ttype != -1 || !paramBoolean))
      errms(paramStreamTokenizer, "end of line expected"); 
  }
  
  protected void getNextToken(StreamTokenizer paramStreamTokenizer) throws IOException {
    if (paramStreamTokenizer.nextToken() == 10)
      errms(paramStreamTokenizer, "premature end of line"); 
    if (paramStreamTokenizer.ttype == -1) {
      errms(paramStreamTokenizer, "premature end of file");
    } else if (paramStreamTokenizer.ttype == 39 || paramStreamTokenizer.ttype == 34) {
      paramStreamTokenizer.ttype = -3;
    } else if (paramStreamTokenizer.ttype == -3 && paramStreamTokenizer.sval.equals("?")) {
      paramStreamTokenizer.ttype = 63;
    } 
  }
  
  protected void initTokenizer(StreamTokenizer paramStreamTokenizer) {
    paramStreamTokenizer.resetSyntax();
    paramStreamTokenizer.whitespaceChars(0, 32);
    paramStreamTokenizer.wordChars(33, 255);
    paramStreamTokenizer.whitespaceChars(44, 44);
    paramStreamTokenizer.commentChar(37);
    paramStreamTokenizer.quoteChar(34);
    paramStreamTokenizer.quoteChar(39);
    paramStreamTokenizer.ordinaryChar(123);
    paramStreamTokenizer.ordinaryChar(125);
    paramStreamTokenizer.eolIsSignificant(true);
  }
  
  protected String instancesAndWeights() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < numInstances(); b++) {
      stringBuffer.append(instance(b) + " " + instance(b).weight());
      if (b < numInstances() - 1)
        stringBuffer.append("\n"); 
    } 
    return stringBuffer.toString();
  }
  
  protected void quickSort(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt2;
    int j = paramInt3;
    if (paramInt3 > paramInt2) {
      double d = instance((paramInt2 + paramInt3) / 2).value(paramInt1);
      while (i <= j) {
        while (instance(i).value(paramInt1) < d && i < paramInt3)
          i++; 
        while (instance(j).value(paramInt1) > d && j > paramInt2)
          j--; 
        if (i <= j) {
          swap(i, j);
          i++;
          j--;
        } 
      } 
      if (paramInt2 < j)
        quickSort(paramInt1, paramInt2, j); 
      if (i < paramInt3)
        quickSort(paramInt1, i, paramInt3); 
    } 
  }
  
  protected void readTillEOL(StreamTokenizer paramStreamTokenizer) throws IOException {
    while (paramStreamTokenizer.nextToken() != 10);
    paramStreamTokenizer.pushBack();
  }
  
  protected void stratStep(int paramInt) {
    FastVector fastVector = new FastVector(this.m_Instances.capacity());
    for (byte b = 0; fastVector.size() < numInstances(); b++) {
      int i;
      for (i = b; i < numInstances(); i += paramInt)
        fastVector.addElement(instance(i)); 
    } 
    this.m_Instances = fastVector;
  }
  
  public void swap(int paramInt1, int paramInt2) {
    this.m_Instances.swap(paramInt1, paramInt2);
  }
  
  public static Instances mergeInstances(Instances paramInstances1, Instances paramInstances2) {
    if (paramInstances1.numInstances() != paramInstances2.numInstances())
      throw new IllegalArgumentException("Instance sets must be of the same size"); 
    FastVector fastVector = new FastVector();
    byte b1;
    for (b1 = 0; b1 < paramInstances1.numAttributes(); b1++)
      fastVector.addElement(paramInstances1.attribute(b1)); 
    for (b1 = 0; b1 < paramInstances2.numAttributes(); b1++)
      fastVector.addElement(paramInstances2.attribute(b1)); 
    Instances instances = new Instances(paramInstances1.relationName() + '_' + paramInstances2.relationName(), fastVector, paramInstances1.numInstances());
    for (byte b2 = 0; b2 < paramInstances1.numInstances(); b2++)
      instances.add(paramInstances1.instance(b2).mergeInstance(paramInstances2.instance(b2))); 
    return instances;
  }
  
  public static void test(String[] paramArrayOfString) {
    Random random = new Random(2L);
    try {
      if (paramArrayOfString.length > 1)
        throw new Exception("Usage: Instances [<filename>]"); 
      FastVector fastVector2 = new FastVector(2);
      fastVector2.addElement("first_value");
      fastVector2.addElement("second_value");
      FastVector fastVector1 = new FastVector(2);
      fastVector1.addElement(new Attribute("nominal_attribute", fastVector2));
      fastVector1.addElement(new Attribute("numeric_attribute"));
      Instances instances1 = new Instances("test_set", fastVector1, 10);
      instances1.add(new Instance(instances1.numAttributes()));
      instances1.add(new Instance(instances1.numAttributes()));
      instances1.add(new Instance(instances1.numAttributes()));
      instances1.setClassIndex(0);
      System.out.println("\nSet of instances created from scratch:\n");
      System.out.println(instances1);
      if (paramArrayOfString.length == 1) {
        String str = paramArrayOfString[0];
        FileReader fileReader = new FileReader(str);
        System.out.println("\nFirst five instances from file:\n");
        instances1 = new Instances(fileReader, 1);
        instances1.setClassIndex(instances1.numAttributes() - 1);
        for (byte b = 0; b < 5 && instances1.readInstance(fileReader); b++);
        System.out.println(instances1);
        fileReader = new FileReader(str);
        instances1 = new Instances(fileReader);
        instances1.setClassIndex(instances1.numAttributes() - 1);
        System.out.println("\nDataset:\n");
        System.out.println(instances1);
        System.out.println("\nClass index: " + instances1.classIndex());
      } 
      System.out.println("\nClass name: " + instances1.classAttribute().name());
      System.out.println("\nClass index: " + instances1.classIndex());
      System.out.println("\nClass is nominal: " + instances1.classAttribute().isNominal());
      System.out.println("\nClass is numeric: " + instances1.classAttribute().isNumeric());
      System.out.println("\nClasses:\n");
      byte b1;
      for (b1 = 0; b1 < instances1.numClasses(); b1++)
        System.out.println(instances1.classAttribute().value(b1)); 
      System.out.println("\nClass values and labels of instances:\n");
      for (b1 = 0; b1 < instances1.numInstances(); b1++) {
        Instance instance = instances1.instance(b1);
        System.out.print(instance.classValue() + "\t");
        System.out.print(instance.toString(instance.classIndex()));
        if (instances1.instance(b1).classIsMissing()) {
          System.out.println("\tis missing");
        } else {
          System.out.println();
        } 
      } 
      System.out.println("\nCreating random weights for instances.");
      for (b1 = 0; b1 < instances1.numInstances(); b1++)
        instances1.instance(b1).setWeight(random.nextDouble()); 
      System.out.println("\nInstances and their weights:\n");
      System.out.println(instances1.instancesAndWeights());
      System.out.print("\nSum of weights: ");
      System.out.println(instances1.sumOfWeights());
      Instances instances2 = new Instances(instances1);
      Attribute attribute = new Attribute("Inserted");
      instances2.insertAttributeAt(attribute, 0);
      System.out.println("\nSet with inserted attribute:\n");
      System.out.println(instances2);
      System.out.println("\nClass name: " + instances2.classAttribute().name());
      instances2.deleteAttributeAt(0);
      System.out.println("\nSet with attribute deleted:\n");
      System.out.println(instances2);
      System.out.println("\nClass name: " + instances2.classAttribute().name());
      System.out.println("\nHeaders equal: " + instances1.equalHeaders(instances2) + "\n");
      System.out.println("\nData (internal values):\n");
      for (b1 = 0; b1 < instances1.numInstances(); b1++) {
        for (byte b = 0; b < instances1.numAttributes(); b++) {
          if (instances1.instance(b1).isMissing(b)) {
            System.out.print("? ");
          } else {
            System.out.print(instances1.instance(b1).value(b) + " ");
          } 
        } 
        System.out.println();
      } 
      System.out.println("\nEmpty dataset:\n");
      Instances instances3 = new Instances(instances1, 0);
      System.out.println(instances3);
      System.out.println("\nClass name: " + instances3.classAttribute().name());
      if (instances3.classAttribute().isNominal()) {
        Instances instances = new Instances(instances3, 0);
        instances.renameAttribute(instances.classAttribute(), "new_name");
        instances.renameAttributeValue(instances.classAttribute(), instances.classAttribute().value(0), "new_val_name");
        System.out.println("\nDataset with names changed:\n" + instances);
        System.out.println("\nOriginal dataset:\n" + instances3);
      } 
      int i = instances1.numInstances() / 4;
      int j = instances1.numInstances() / 2;
      System.out.print("\nSubset of dataset: ");
      System.out.println(j + " instances from " + (i + 1) + ". instance");
      instances2 = new Instances(instances1, i, j);
      System.out.println("\nClass name: " + instances2.classAttribute().name());
      System.out.println("\nInstances and their weights:\n");
      System.out.println(instances2.instancesAndWeights());
      System.out.print("\nSum of weights: ");
      System.out.println(instances2.sumOfWeights());
      System.out.println("\nTrain and test folds for 3-fold CV:");
      if (instances1.classAttribute().isNominal())
        instances1.stratify(3); 
      for (byte b2 = 0; b2 < 3; b2++) {
        Instances instances4 = instances1.trainCV(3, b2, new Random(1L));
        Instances instances5 = instances1.testCV(3, b2);
        System.out.println("\nTrain: ");
        System.out.println("\nInstances and their weights:\n");
        System.out.println(instances4.instancesAndWeights());
        System.out.print("\nSum of weights: ");
        System.out.println(instances4.sumOfWeights());
        System.out.println("\nClass name: " + instances4.classAttribute().name());
        System.out.println("\nTest: ");
        System.out.println("\nInstances and their weights:\n");
        System.out.println(instances5.instancesAndWeights());
        System.out.print("\nSum of weights: ");
        System.out.println(instances5.sumOfWeights());
        System.out.println("\nClass name: " + instances5.classAttribute().name());
      } 
      System.out.println("\nRandomized dataset:");
      instances1.randomize(random);
      System.out.println("\nInstances and their weights:\n");
      System.out.println(instances1.instancesAndWeights());
      System.out.print("\nSum of weights: ");
      System.out.println(instances1.sumOfWeights());
      System.out.print("\nInstances sorted according to first attribute:\n ");
      instances1.sort(0);
      System.out.println("\nInstances and their weights:\n");
      System.out.println(instances1.instancesAndWeights());
      System.out.print("\nSum of weights: ");
      System.out.println(instances1.sumOfWeights());
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      BufferedReader bufferedReader = null;
      if (paramArrayOfString.length > 1)
        throw new Exception("Usage: Instances <filename>"); 
      if (paramArrayOfString.length == 0) {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      } else {
        bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
      } 
      Instances instances = new Instances(bufferedReader);
      System.out.println(instances.toSummaryString());
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Instances.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */