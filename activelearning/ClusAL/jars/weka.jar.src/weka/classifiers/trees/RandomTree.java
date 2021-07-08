package weka.classifiers.trees;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class RandomTree extends Classifier implements OptionHandler, WeightedInstancesHandler, Randomizable {
  protected RandomTree[] m_Successors;
  
  protected int m_Attribute = -1;
  
  protected double m_SplitPoint = Double.NaN;
  
  protected double[][] m_Distribution = (double[][])null;
  
  protected Instances m_Info = null;
  
  protected double[] m_Prop = null;
  
  protected double[] m_ClassProbs = null;
  
  protected double m_MinNum = 1.0D;
  
  protected boolean m_Debug = false;
  
  protected int m_KValue = 1;
  
  protected int m_randomSeed = 1;
  
  public String globalInfo() {
    return "Class for constructing a tree that considers K randomly  chosen attributes at each node. Performs no pruning.";
  }
  
  public String minNumTipText() {
    return "The minimum total weight of the instances in a leaf.";
  }
  
  public double getMinNum() {
    return this.m_MinNum;
  }
  
  public void setMinNum(double paramDouble) {
    this.m_MinNum = paramDouble;
  }
  
  public String KValueTipText() {
    return "Sets the number of randomly chosen attributes.";
  }
  
  public int getKValue() {
    return this.m_KValue;
  }
  
  public void setKValue(int paramInt) {
    this.m_KValue = paramInt;
  }
  
  public String debugTipText() {
    return "Whether debug information is output to the console.";
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public String seedTipText() {
    return "The random number seed used for selecting attributes.";
  }
  
  public void setSeed(int paramInt) {
    this.m_randomSeed = paramInt;
  }
  
  public int getSeed() {
    return this.m_randomSeed;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tNumber of attributes to randomly investigate.", "K", 1, "-K <number of attributes>"));
    vector.addElement(new Option("\tSet minimum number of instances per leaf.", "M", 1, "-M <minimum number of instances>"));
    vector.addElement(new Option("\tTurns debugging info on.", "D", 0, "-D"));
    vector.addElement(new Option("\tSeed for random number generator.\n\t(default 1)", "S", 1, "-S"));
    return vector.elements();
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    arrayOfString[b++] = "-K";
    arrayOfString[b++] = "" + getKValue();
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMinNum();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    if (getDebug())
      arrayOfString[b++] = "-D"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('K', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_KValue = Integer.parseInt(str1);
    } else {
      this.m_KValue = 1;
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_MinNum = Integer.parseInt(str2);
    } else {
      this.m_MinNum = 1.0D;
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      setSeed(Integer.parseInt(str3));
    } else {
      setSeed(1);
    } 
    this.m_Debug = Utils.getFlag('D', paramArrayOfString);
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (this.m_KValue > paramInstances.numAttributes() - 1)
      this.m_KValue = paramInstances.numAttributes() - 1; 
    if (!paramInstances.classAttribute().isNominal())
      throw new UnsupportedClassTypeException("RandomTree: Nominal class, please."); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new IllegalArgumentException("RandomTree: zero training instances or all instances have missing class!"); 
    if (paramInstances.numAttributes() == 1)
      throw new IllegalArgumentException("RandomTree: Attribute missing. Need at least one attribute other than class attribute!"); 
    Instances instances = paramInstances;
    int[][] arrayOfInt = new int[instances.numAttributes()][0];
    double[][] arrayOfDouble = new double[instances.numAttributes()][0];
    double[] arrayOfDouble1 = new double[instances.numInstances()];
    for (byte b1 = 0; b1 < instances.numAttributes(); b1++) {
      if (b1 != instances.classIndex()) {
        arrayOfDouble[b1] = new double[instances.numInstances()];
        if (instances.attribute(b1).isNominal()) {
          arrayOfInt[b1] = new int[instances.numInstances()];
          byte b5 = 0;
          byte b6;
          for (b6 = 0; b6 < instances.numInstances(); b6++) {
            Instance instance = instances.instance(b6);
            if (!instance.isMissing(b1)) {
              arrayOfInt[b1][b5] = b6;
              arrayOfDouble[b1][b5] = instance.weight();
              b5++;
            } 
          } 
          for (b6 = 0; b6 < instances.numInstances(); b6++) {
            Instance instance = instances.instance(b6);
            if (instance.isMissing(b1)) {
              arrayOfInt[b1][b5] = b6;
              arrayOfDouble[b1][b5] = instance.weight();
              b5++;
            } 
          } 
        } else {
          byte b;
          for (b = 0; b < instances.numInstances(); b++) {
            Instance instance = instances.instance(b);
            arrayOfDouble1[b] = instance.value(b1);
          } 
          arrayOfInt[b1] = Utils.sort(arrayOfDouble1);
          for (b = 0; b < instances.numInstances(); b++)
            arrayOfDouble[b1][b] = instances.instance(arrayOfInt[b1][b]).weight(); 
        } 
      } 
    } 
    double[] arrayOfDouble2 = new double[instances.numClasses()];
    for (byte b2 = 0; b2 < instances.numInstances(); b2++) {
      Instance instance = instances.instance(b2);
      arrayOfDouble2[(int)instance.classValue()] = arrayOfDouble2[(int)instance.classValue()] + instance.weight();
    } 
    int[] arrayOfInt1 = new int[paramInstances.numAttributes() - 1];
    byte b3 = 0;
    for (byte b4 = 0; b4 < arrayOfInt1.length; b4++) {
      if (b3 == paramInstances.classIndex())
        b3++; 
      arrayOfInt1[b4] = b3++;
    } 
    buildTree(arrayOfInt, arrayOfDouble, instances, arrayOfDouble2, new Instances(instances, 0), this.m_MinNum, this.m_Debug, arrayOfInt1, paramInstances.getRandomNumberGenerator(this.m_randomSeed));
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    double[] arrayOfDouble = null;
    if (this.m_Attribute > -1)
      if (paramInstance.isMissing(this.m_Attribute)) {
        arrayOfDouble = new double[this.m_Info.numClasses()];
        for (byte b = 0; b < this.m_Successors.length; b++) {
          double[] arrayOfDouble1 = this.m_Successors[b].distributionForInstance(paramInstance);
          if (arrayOfDouble1 != null)
            for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
              arrayOfDouble[b1] = arrayOfDouble[b1] + this.m_Prop[b] * arrayOfDouble1[b1];  
        } 
      } else if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
        arrayOfDouble = this.m_Successors[(int)paramInstance.value(this.m_Attribute)].distributionForInstance(paramInstance);
      } else if (Utils.sm(paramInstance.value(this.m_Attribute), this.m_SplitPoint)) {
        arrayOfDouble = this.m_Successors[0].distributionForInstance(paramInstance);
      } else {
        arrayOfDouble = this.m_Successors[1].distributionForInstance(paramInstance);
      }  
    return (this.m_Attribute == -1 || arrayOfDouble == null) ? this.m_ClassProbs : arrayOfDouble;
  }
  
  public String toGraph() {
    try {
      StringBuffer stringBuffer = new StringBuffer();
      toGraph(stringBuffer, 0);
      return "digraph Tree {\nedge [style=bold]\n" + stringBuffer.toString() + "\n}\n";
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public int toGraph(StringBuffer paramStringBuffer, int paramInt) throws Exception {
    int i = Utils.maxIndex(this.m_ClassProbs);
    String str = this.m_Info.classAttribute().value(i);
    paramInt++;
    if (this.m_Attribute == -1) {
      paramStringBuffer.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + paramInt + ": " + str + "\"" + "shape=box]\n");
    } else {
      paramStringBuffer.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + paramInt + ": " + str + "\"]\n");
      for (byte b = 0; b < this.m_Successors.length; b++) {
        paramStringBuffer.append("N" + Integer.toHexString(hashCode()) + "->" + "N" + Integer.toHexString(this.m_Successors[b].hashCode()) + " [label=\"" + this.m_Info.attribute(this.m_Attribute).name());
        if (this.m_Info.attribute(this.m_Attribute).isNumeric()) {
          if (b == 0) {
            paramStringBuffer.append(" < " + Utils.doubleToString(this.m_SplitPoint, 2));
          } else {
            paramStringBuffer.append(" >= " + Utils.doubleToString(this.m_SplitPoint, 2));
          } 
        } else {
          paramStringBuffer.append(" = " + this.m_Info.attribute(this.m_Attribute).value(b));
        } 
        paramStringBuffer.append("\"]\n");
        paramInt = this.m_Successors[b].toGraph(paramStringBuffer, paramInt);
      } 
    } 
    return paramInt;
  }
  
  public String toString() {
    return (this.m_Successors == null) ? "RandomTree: no model has been built yet." : ("\nRandomTree\n==========\n" + toString(0) + "\n" + "\nSize of the tree : " + numNodes());
  }
  
  protected String leafString() throws Exception {
    int i = Utils.maxIndex(this.m_Distribution[0]);
    return " : " + this.m_Info.classAttribute().value(i) + " (" + Utils.doubleToString(Utils.sum(this.m_Distribution[0]), 2) + "/" + Utils.doubleToString(Utils.sum(this.m_Distribution[0]) - this.m_Distribution[0][i], 2) + ")";
  }
  
  protected String toString(int paramInt) {
    try {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_Attribute == -1)
        return leafString(); 
      if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
        for (byte b = 0; b < this.m_Successors.length; b++) {
          stringBuffer.append("\n");
          for (byte b1 = 0; b1 < paramInt; b1++)
            stringBuffer.append("|   "); 
          stringBuffer.append(this.m_Info.attribute(this.m_Attribute).name() + " = " + this.m_Info.attribute(this.m_Attribute).value(b));
          stringBuffer.append(this.m_Successors[b].toString(paramInt + 1));
        } 
      } else {
        stringBuffer.append("\n");
        byte b;
        for (b = 0; b < paramInt; b++)
          stringBuffer.append("|   "); 
        stringBuffer.append(this.m_Info.attribute(this.m_Attribute).name() + " < " + Utils.doubleToString(this.m_SplitPoint, 2));
        stringBuffer.append(this.m_Successors[0].toString(paramInt + 1));
        stringBuffer.append("\n");
        for (b = 0; b < paramInt; b++)
          stringBuffer.append("|   "); 
        stringBuffer.append(this.m_Info.attribute(this.m_Attribute).name() + " >= " + Utils.doubleToString(this.m_SplitPoint, 2));
        stringBuffer.append(this.m_Successors[1].toString(paramInt + 1));
      } 
      return stringBuffer.toString();
    } catch (Exception exception) {
      exception.printStackTrace();
      return "RandomTree: tree can't be printed";
    } 
  }
  
  protected void buildTree(int[][] paramArrayOfint, double[][] paramArrayOfdouble, Instances paramInstances1, double[] paramArrayOfdouble1, Instances paramInstances2, double paramDouble, boolean paramBoolean, int[] paramArrayOfint1, Random paramRandom) throws Exception {
    this.m_Info = paramInstances2;
    this.m_Debug = paramBoolean;
    this.m_MinNum = paramDouble;
    if ((paramInstances1.classIndex() > 0 && (paramArrayOfint[0]).length == 0) || (paramArrayOfint[1]).length == 0) {
      this.m_Distribution = new double[1][paramInstances1.numClasses()];
      this.m_ClassProbs = null;
      return;
    } 
    this.m_ClassProbs = new double[paramArrayOfdouble1.length];
    System.arraycopy(paramArrayOfdouble1, 0, this.m_ClassProbs, 0, paramArrayOfdouble1.length);
    if (Utils.sm(Utils.sum(this.m_ClassProbs), 2.0D * this.m_MinNum) || Utils.eq(this.m_ClassProbs[Utils.maxIndex(this.m_ClassProbs)], Utils.sum(this.m_ClassProbs))) {
      this.m_Attribute = -1;
      this.m_Distribution = new double[1][this.m_ClassProbs.length];
      for (byte b = 0; b < this.m_ClassProbs.length; b++)
        this.m_Distribution[0][b] = this.m_ClassProbs[b]; 
      Utils.normalize(this.m_ClassProbs);
      return;
    } 
    double[] arrayOfDouble1 = new double[paramInstances1.numAttributes()];
    double[][][] arrayOfDouble = new double[paramInstances1.numAttributes()][0][0];
    double[][] arrayOfDouble2 = new double[paramInstances1.numAttributes()][0];
    double[] arrayOfDouble3 = new double[paramInstances1.numAttributes()];
    int i = 0;
    int j = paramArrayOfint1.length;
    int k = this.m_KValue;
    boolean bool = false;
    while (j > 0 && (k-- > 0 || !bool)) {
      int m = paramRandom.nextInt(j);
      i = paramArrayOfint1[m];
      paramArrayOfint1[m] = paramArrayOfint1[j - 1];
      paramArrayOfint1[j - 1] = i;
      j--;
      arrayOfDouble3[i] = distribution(arrayOfDouble2, arrayOfDouble, i, paramArrayOfint[i], paramArrayOfdouble[i], paramInstances1);
      arrayOfDouble1[i] = gain(arrayOfDouble[i], priorVal(arrayOfDouble[i]));
      if (Utils.gr(arrayOfDouble1[i], 0.0D))
        bool = true; 
    } 
    this.m_Attribute = Utils.maxIndex(arrayOfDouble1);
    this.m_Distribution = arrayOfDouble[this.m_Attribute];
    if (Utils.gr(arrayOfDouble1[this.m_Attribute], 0.0D)) {
      this.m_SplitPoint = arrayOfDouble3[this.m_Attribute];
      this.m_Prop = arrayOfDouble2[this.m_Attribute];
      int[][][] arrayOfInt = new int[this.m_Distribution.length][paramInstances1.numAttributes()][0];
      double[][][] arrayOfDouble4 = new double[this.m_Distribution.length][paramInstances1.numAttributes()][0];
      splitData(arrayOfInt, arrayOfDouble4, this.m_Attribute, this.m_SplitPoint, paramArrayOfint, paramArrayOfdouble, this.m_Distribution, paramInstances1);
      this.m_Successors = new RandomTree[this.m_Distribution.length];
      for (byte b = 0; b < this.m_Distribution.length; b++) {
        this.m_Successors[b] = new RandomTree();
        this.m_Successors[b].setKValue(this.m_KValue);
        this.m_Successors[b].buildTree(arrayOfInt[b], arrayOfDouble4[b], paramInstances1, this.m_Distribution[b], paramInstances2, this.m_MinNum, this.m_Debug, paramArrayOfint1, paramRandom);
      } 
    } else {
      this.m_Attribute = -1;
      this.m_Distribution = new double[1][this.m_ClassProbs.length];
      for (byte b = 0; b < this.m_ClassProbs.length; b++)
        this.m_Distribution[0][b] = this.m_ClassProbs[b]; 
    } 
    Utils.normalize(this.m_ClassProbs);
  }
  
  public int numNodes() {
    if (this.m_Attribute == -1)
      return 1; 
    int i = 1;
    for (byte b = 0; b < this.m_Successors.length; b++)
      i += this.m_Successors[b].numNodes(); 
    return i;
  }
  
  protected void splitData(int[][][] paramArrayOfint, double[][][] paramArrayOfdouble, int paramInt, double paramDouble, int[][] paramArrayOfint1, double[][] paramArrayOfdouble1, double[][] paramArrayOfdouble2, Instances paramInstances) throws Exception {
    for (byte b = 0; b < paramInstances.numAttributes(); b++) {
      if (b != paramInstances.classIndex()) {
        int[] arrayOfInt;
        if (paramInstances.attribute(paramInt).isNominal()) {
          arrayOfInt = new int[paramInstances.attribute(paramInt).numValues()];
          for (byte b3 = 0; b3 < arrayOfInt.length; b3++) {
            paramArrayOfint[b3][b] = new int[(paramArrayOfint1[b]).length];
            paramArrayOfdouble[b3][b] = new double[(paramArrayOfint1[b]).length];
          } 
          for (byte b2 = 0; b2 < (paramArrayOfint1[b]).length; b2++) {
            Instance instance = paramInstances.instance(paramArrayOfint1[b][b2]);
            if (instance.isMissing(paramInt)) {
              for (byte b4 = 0; b4 < arrayOfInt.length; b4++) {
                if (Utils.gr(this.m_Prop[b4], 0.0D)) {
                  paramArrayOfint[b4][b][arrayOfInt[b4]] = paramArrayOfint1[b][b2];
                  paramArrayOfdouble[b4][b][arrayOfInt[b4]] = this.m_Prop[b4] * paramArrayOfdouble1[b][b2];
                  arrayOfInt[b4] = arrayOfInt[b4] + 1;
                } 
              } 
            } else {
              int i = (int)instance.value(paramInt);
              paramArrayOfint[i][b][arrayOfInt[i]] = paramArrayOfint1[b][b2];
              paramArrayOfdouble[i][b][arrayOfInt[i]] = paramArrayOfdouble1[b][b2];
              arrayOfInt[i] = arrayOfInt[i] + 1;
            } 
          } 
        } else {
          arrayOfInt = new int[2];
          for (byte b3 = 0; b3 < 2; b3++) {
            paramArrayOfint[b3][b] = new int[(paramArrayOfint1[b]).length];
            paramArrayOfdouble[b3][b] = new double[(paramArrayOfdouble1[b]).length];
          } 
          for (byte b2 = 0; b2 < (paramArrayOfint1[b]).length; b2++) {
            Instance instance = paramInstances.instance(paramArrayOfint1[b][b2]);
            if (instance.isMissing(paramInt)) {
              for (byte b4 = 0; b4 < arrayOfInt.length; b4++) {
                if (Utils.gr(this.m_Prop[b4], 0.0D)) {
                  paramArrayOfint[b4][b][arrayOfInt[b4]] = paramArrayOfint1[b][b2];
                  paramArrayOfdouble[b4][b][arrayOfInt[b4]] = this.m_Prop[b4] * paramArrayOfdouble1[b][b2];
                  arrayOfInt[b4] = arrayOfInt[b4] + 1;
                } 
              } 
            } else {
              boolean bool = Utils.sm(instance.value(paramInt), paramDouble) ? false : true;
              paramArrayOfint[bool][b][arrayOfInt[bool]] = paramArrayOfint1[b][b2];
              paramArrayOfdouble[bool][b][arrayOfInt[bool]] = paramArrayOfdouble1[b][b2];
              arrayOfInt[bool] = arrayOfInt[bool] + 1;
            } 
          } 
        } 
        for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
          int[] arrayOfInt1 = new int[arrayOfInt[b1]];
          System.arraycopy(paramArrayOfint[b1][b], 0, arrayOfInt1, 0, arrayOfInt[b1]);
          paramArrayOfint[b1][b] = arrayOfInt1;
          double[] arrayOfDouble = new double[arrayOfInt[b1]];
          System.arraycopy(paramArrayOfdouble[b1][b], 0, arrayOfDouble, 0, arrayOfInt[b1]);
          paramArrayOfdouble[b1][b] = arrayOfDouble;
        } 
      } 
    } 
  }
  
  protected double distribution(double[][] paramArrayOfdouble, double[][][] paramArrayOfdouble1, int paramInt, int[] paramArrayOfint, double[] paramArrayOfdouble2, Instances paramInstances) throws Exception {
    byte b1;
    double d = Double.NaN;
    Attribute attribute = paramInstances.attribute(paramInt);
    double[][] arrayOfDouble = (double[][])null;
    if (attribute.isNominal()) {
      arrayOfDouble = new double[attribute.numValues()][paramInstances.numClasses()];
      for (b1 = 0; b1 < paramArrayOfint.length; b1++) {
        Instance instance = paramInstances.instance(paramArrayOfint[b1]);
        if (instance.isMissing(paramInt))
          break; 
        arrayOfDouble[(int)instance.value(paramInt)][(int)instance.classValue()] = arrayOfDouble[(int)instance.value(paramInt)][(int)instance.classValue()] + paramArrayOfdouble2[b1];
      } 
    } else {
      double[][] arrayOfDouble1 = new double[2][paramInstances.numClasses()];
      arrayOfDouble = new double[2][paramInstances.numClasses()];
      for (byte b3 = 0; b3 < paramArrayOfint.length; b3++) {
        Instance instance = paramInstances.instance(paramArrayOfint[b3]);
        if (instance.isMissing(paramInt))
          break; 
        arrayOfDouble1[1][(int)instance.classValue()] = arrayOfDouble1[1][(int)instance.classValue()] + paramArrayOfdouble2[b3];
      } 
      double d1 = priorVal(arrayOfDouble1);
      for (byte b4 = 0; b4 < arrayOfDouble1.length; b4++)
        System.arraycopy(arrayOfDouble1[b4], 0, arrayOfDouble[b4], 0, (arrayOfDouble[b4]).length); 
      double d2 = paramInstances.instance(paramArrayOfint[0]).value(paramInt);
      double d3 = -1.7976931348623157E308D;
      for (b1 = 0; b1 < paramArrayOfint.length; b1++) {
        Instance instance = paramInstances.instance(paramArrayOfint[b1]);
        if (instance.isMissing(paramInt))
          break; 
        if (Utils.gr(instance.value(paramInt), d2)) {
          double d4 = gain(arrayOfDouble1, d1);
          if (Utils.gr(d4, d3)) {
            d3 = d4;
            d = (instance.value(paramInt) + d2) / 2.0D;
            for (byte b = 0; b < arrayOfDouble1.length; b++)
              System.arraycopy(arrayOfDouble1[b], 0, arrayOfDouble[b], 0, (arrayOfDouble[b]).length); 
          } 
        } 
        d2 = instance.value(paramInt);
        arrayOfDouble1[0][(int)instance.classValue()] = arrayOfDouble1[0][(int)instance.classValue()] + paramArrayOfdouble2[b1];
        arrayOfDouble1[1][(int)instance.classValue()] = arrayOfDouble1[1][(int)instance.classValue()] - paramArrayOfdouble2[b1];
      } 
    } 
    paramArrayOfdouble[paramInt] = new double[arrayOfDouble.length];
    byte b2;
    for (b2 = 0; b2 < (paramArrayOfdouble[paramInt]).length; b2++)
      paramArrayOfdouble[paramInt][b2] = Utils.sum(arrayOfDouble[b2]); 
    if (Utils.eq(Utils.sum(paramArrayOfdouble[paramInt]), 0.0D)) {
      for (b2 = 0; b2 < (paramArrayOfdouble[paramInt]).length; b2++)
        paramArrayOfdouble[paramInt][b2] = 1.0D / (paramArrayOfdouble[paramInt]).length; 
    } else {
      Utils.normalize(paramArrayOfdouble[paramInt]);
    } 
    if (b1 < paramArrayOfint.length)
      while (b1 < paramArrayOfint.length) {
        Instance instance = paramInstances.instance(paramArrayOfint[b1]);
        for (byte b = 0; b < arrayOfDouble.length; b++)
          arrayOfDouble[b][(int)instance.classValue()] = arrayOfDouble[b][(int)instance.classValue()] + paramArrayOfdouble[paramInt][b] * paramArrayOfdouble2[b1]; 
        b1++;
      }  
    paramArrayOfdouble1[paramInt] = arrayOfDouble;
    return d;
  }
  
  protected double priorVal(double[][] paramArrayOfdouble) {
    return ContingencyTables.entropyOverColumns(paramArrayOfdouble);
  }
  
  protected double gain(double[][] paramArrayOfdouble, double paramDouble) {
    return paramDouble - ContingencyTables.entropyConditionedOnRows(paramArrayOfdouble);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new RandomTree(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\RandomTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */