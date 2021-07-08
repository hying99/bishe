package weka.classifiers.trees;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.Sourcable;
import weka.core.AdditionalMeasureProducer;
import weka.core.Attribute;
import weka.core.ContingencyTables;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class REPTree extends Classifier implements OptionHandler, WeightedInstancesHandler, Drawable, AdditionalMeasureProducer, Sourcable {
  protected Tree m_Tree = null;
  
  protected int m_NumFolds = 3;
  
  protected int m_Seed = 1;
  
  protected boolean m_NoPruning = false;
  
  protected double m_MinNum = 2.0D;
  
  protected double m_MinVarianceProp = 0.001D;
  
  protected int m_MaxDepth = -1;
  
  private static long PRINTED_NODES = 0L;
  
  public String globalInfo() {
    return "Fast decision tree learner. Builds a decision/regression tree using information gain/variance and prunes it using reduced-error pruning (with backfitting).  Only sorts values for numeric attributes once. Missing values are dealt with by splitting the corresponding instances into pieces (i.e. as in C4.5).";
  }
  
  public String noPruningTipText() {
    return "Whether pruning is performed.";
  }
  
  public boolean getNoPruning() {
    return this.m_NoPruning;
  }
  
  public void setNoPruning(boolean paramBoolean) {
    this.m_NoPruning = paramBoolean;
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
  
  public String minVariancePropTipText() {
    return "The minimum proportion of the variance on all the data that needs to be present at a node in order for splitting to be performed in regression trees.";
  }
  
  public double getMinVarianceProp() {
    return this.m_MinVarianceProp;
  }
  
  public void setMinVarianceProp(double paramDouble) {
    this.m_MinVarianceProp = paramDouble;
  }
  
  public String seedTipText() {
    return "The seed used for randomizing the data.";
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public String numFoldsTipText() {
    return "Determines the amount of data used for pruning. One fold is used for pruning, the rest for growing the rules.";
  }
  
  public int getNumFolds() {
    return this.m_NumFolds;
  }
  
  public void setNumFolds(int paramInt) {
    this.m_NumFolds = paramInt;
  }
  
  public String maxDepthTipText() {
    return "The maximum tree depth (-1 for no restriction).";
  }
  
  public int getMaxDepth() {
    return this.m_MaxDepth;
  }
  
  public void setMaxDepth(int paramInt) {
    this.m_MaxDepth = paramInt;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(5);
    vector.addElement(new Option("\tSet minimum number of instances per leaf (default 2).", "M", 1, "-M <minimum number of instances>"));
    vector.addElement(new Option("\tSet minimum numeric class variance proportion\n\tof train variance for split (default 1e-3).", "V", 1, "-V <minimum variance for split>"));
    vector.addElement(new Option("\tNumber of folds for reduced error pruning (default 3).", "N", 1, "-N <number of folds>"));
    vector.addElement(new Option("\tSeed for random data shuffling (default 1).", "S", 1, "-S <seed>"));
    vector.addElement(new Option("\tNo pruning.", "P", 0, "-P"));
    vector.addElement(new Option("\tMaximum tree depth (default -1, no maximum)", "L", 1, "-L"));
    return vector.elements();
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[12];
    byte b = 0;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + (int)getMinNum();
    arrayOfString[b++] = "-V";
    arrayOfString[b++] = "" + getMinVarianceProp();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumFolds();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    arrayOfString[b++] = "-L";
    arrayOfString[b++] = "" + getMaxDepth();
    if (getNoPruning())
      arrayOfString[b++] = "-P"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('M', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_MinNum = Integer.parseInt(str1);
    } else {
      this.m_MinNum = 2.0D;
    } 
    String str2 = Utils.getOption('V', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_MinVarianceProp = Double.parseDouble(str2);
    } else {
      this.m_MinVarianceProp = 0.001D;
    } 
    String str3 = Utils.getOption('N', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_NumFolds = Integer.parseInt(str3);
    } else {
      this.m_NumFolds = 3;
    } 
    String str4 = Utils.getOption('S', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_Seed = Integer.parseInt(str4);
    } else {
      this.m_Seed = 1;
    } 
    this.m_NoPruning = Utils.getFlag('P', paramArrayOfString);
    String str5 = Utils.getOption('L', paramArrayOfString);
    if (str5.length() != 0) {
      this.m_MaxDepth = Integer.parseInt(str5);
    } else {
      this.m_MaxDepth = -1;
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public int numNodes() {
    return this.m_Tree.numNodes();
  }
  
  public Enumeration enumerateMeasures() {
    Vector vector = new Vector(1);
    vector.addElement("measureTreeSize");
    return vector.elements();
  }
  
  public double getMeasure(String paramString) {
    if (paramString.equalsIgnoreCase("measureTreeSize"))
      return numNodes(); 
    throw new IllegalArgumentException(paramString + " not supported (REPTree)");
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    Random random = new Random(this.m_Seed);
    if (!paramInstances.classAttribute().isNominal() && !paramInstances.classAttribute().isNumeric())
      throw new UnsupportedClassTypeException("REPTree: nominal or numeric class!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.deleteWithMissingClass();
    if (paramInstances.numInstances() == 0)
      throw new IllegalArgumentException("REPTree: zero training instances or all instances have missing class!"); 
    if (paramInstances.numAttributes() == 1)
      throw new IllegalArgumentException("REPTree: Attribute missing. Need at least one attribute other than class attribute!"); 
    paramInstances.randomize(random);
    if (paramInstances.classAttribute().isNominal())
      paramInstances.stratify(this.m_NumFolds); 
    Instances instances1 = null;
    Instances instances2 = null;
    if (!this.m_NoPruning) {
      instances1 = paramInstances.trainCV(this.m_NumFolds, 0, random);
      instances2 = paramInstances.testCV(this.m_NumFolds, 0);
    } else {
      instances1 = paramInstances;
    } 
    int[][] arrayOfInt = new int[instances1.numAttributes()][0];
    double[][] arrayOfDouble = new double[instances1.numAttributes()][0];
    double[] arrayOfDouble1 = new double[instances1.numInstances()];
    for (byte b1 = 0; b1 < instances1.numAttributes(); b1++) {
      if (b1 != instances1.classIndex()) {
        arrayOfDouble[b1] = new double[instances1.numInstances()];
        if (instances1.attribute(b1).isNominal()) {
          arrayOfInt[b1] = new int[instances1.numInstances()];
          byte b3 = 0;
          byte b4;
          for (b4 = 0; b4 < instances1.numInstances(); b4++) {
            Instance instance = instances1.instance(b4);
            if (!instance.isMissing(b1)) {
              arrayOfInt[b1][b3] = b4;
              arrayOfDouble[b1][b3] = instance.weight();
              b3++;
            } 
          } 
          for (b4 = 0; b4 < instances1.numInstances(); b4++) {
            Instance instance = instances1.instance(b4);
            if (instance.isMissing(b1)) {
              arrayOfInt[b1][b3] = b4;
              arrayOfDouble[b1][b3] = instance.weight();
              b3++;
            } 
          } 
        } else {
          byte b;
          for (b = 0; b < instances1.numInstances(); b++) {
            Instance instance = instances1.instance(b);
            arrayOfDouble1[b] = instance.value(b1);
          } 
          arrayOfInt[b1] = Utils.sort(arrayOfDouble1);
          for (b = 0; b < instances1.numInstances(); b++)
            arrayOfDouble[b1][b] = instances1.instance(arrayOfInt[b1][b]).weight(); 
        } 
      } 
    } 
    double[] arrayOfDouble2 = new double[instances1.numClasses()];
    double d1 = 0.0D;
    double d2 = 0.0D;
    for (byte b2 = 0; b2 < instances1.numInstances(); b2++) {
      Instance instance = instances1.instance(b2);
      if (paramInstances.classAttribute().isNominal()) {
        arrayOfDouble2[(int)instance.classValue()] = arrayOfDouble2[(int)instance.classValue()] + instance.weight();
        d1 += instance.weight();
      } else {
        arrayOfDouble2[0] = arrayOfDouble2[0] + instance.classValue() * instance.weight();
        d2 += instance.classValue() * instance.classValue() * instance.weight();
        d1 += instance.weight();
      } 
    } 
    this.m_Tree = new Tree(this);
    double d3 = 0.0D;
    if (paramInstances.classAttribute().isNumeric()) {
      d3 = this.m_Tree.singleVariance(arrayOfDouble2[0], d2, d1) / d1;
      arrayOfDouble2[0] = arrayOfDouble2[0] / d1;
    } 
    this.m_Tree.buildTree(arrayOfInt, arrayOfDouble, instances1, d1, arrayOfDouble2, new Instances(instances1, 0), this.m_MinNum, this.m_MinVarianceProp * d3, 0, this.m_MaxDepth);
    if (!this.m_NoPruning) {
      this.m_Tree.insertHoldOutSet(instances2);
      this.m_Tree.reducedErrorPrune();
      this.m_Tree.backfitHoldOutSet(instances2);
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    return this.m_Tree.distributionForInstance(paramInstance);
  }
  
  protected static long nextID() {
    return PRINTED_NODES++;
  }
  
  protected static void resetID() {
    PRINTED_NODES = 0L;
  }
  
  public String toSource(String paramString) throws Exception {
    StringBuffer[] arrayOfStringBuffer = this.m_Tree.toSource(paramString, this.m_Tree);
    return "class " + paramString + " {\n\n" + "  public static double classify(Object [] i)\n" + "    throws Exception {\n\n" + "    double p = Double.NaN;\n" + arrayOfStringBuffer[0] + "    return p;\n" + "  }\n" + arrayOfStringBuffer[1] + "}\n";
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    this.m_Tree.toGraph(stringBuffer, 0, null);
    return "digraph Tree {\nedge [style=bold]\n" + stringBuffer.toString() + "\n}\n";
  }
  
  public String toString() {
    return (this.m_Tree == null) ? "REPTree: No model built yet." : ("\nREPTree\n============\n" + this.m_Tree.toString(0, null) + "\n" + "\nSize of the tree : " + numNodes());
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new REPTree(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class Tree implements Serializable {
    protected Instances m_Info;
    
    protected Tree[] m_Successors;
    
    protected int m_Attribute;
    
    protected double m_SplitPoint;
    
    protected double[] m_Prop;
    
    protected double[] m_ClassProbs;
    
    protected double[] m_Distribution;
    
    protected double[] m_HoldOutDist;
    
    protected double m_HoldOutError;
    
    private final REPTree this$0;
    
    protected Tree(REPTree this$0) {
      this.this$0 = this$0;
      this.m_Info = null;
      this.m_Attribute = -1;
      this.m_SplitPoint = Double.NaN;
      this.m_Prop = null;
      this.m_ClassProbs = null;
      this.m_Distribution = null;
      this.m_HoldOutDist = null;
      this.m_HoldOutError = 0.0D;
    }
    
    protected double[] distributionForInstance(Instance param1Instance) throws Exception {
      double[] arrayOfDouble = null;
      if (this.m_Attribute > -1)
        if (param1Instance.isMissing(this.m_Attribute)) {
          arrayOfDouble = new double[this.m_Info.numClasses()];
          for (byte b = 0; b < this.m_Successors.length; b++) {
            double[] arrayOfDouble1 = this.m_Successors[b].distributionForInstance(param1Instance);
            if (arrayOfDouble1 != null)
              for (byte b1 = 0; b1 < arrayOfDouble1.length; b1++)
                arrayOfDouble[b1] = arrayOfDouble[b1] + this.m_Prop[b] * arrayOfDouble1[b1];  
          } 
        } else if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
          arrayOfDouble = this.m_Successors[(int)param1Instance.value(this.m_Attribute)].distributionForInstance(param1Instance);
        } else if (param1Instance.value(this.m_Attribute) < this.m_SplitPoint) {
          arrayOfDouble = this.m_Successors[0].distributionForInstance(param1Instance);
        } else {
          arrayOfDouble = this.m_Successors[1].distributionForInstance(param1Instance);
        }  
      return (this.m_Attribute == -1 || arrayOfDouble == null) ? this.m_ClassProbs : arrayOfDouble;
    }
    
    public final String sourceExpression(int param1Int) {
      StringBuffer stringBuffer = null;
      if (param1Int < 0)
        return "i[" + this.m_Attribute + "] == null"; 
      if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
        stringBuffer = new StringBuffer("i[");
        stringBuffer.append(this.m_Attribute).append("]");
        stringBuffer.append(".equals(\"").append(this.m_Info.attribute(this.m_Attribute).value(param1Int)).append("\")");
      } else {
        stringBuffer = new StringBuffer("");
        if (param1Int == 0) {
          stringBuffer.append("((Double)i[").append(this.m_Attribute).append("]).doubleValue() < ").append(this.m_SplitPoint);
        } else {
          stringBuffer.append("true");
        } 
      } 
      return stringBuffer.toString();
    }
    
    public StringBuffer[] toSource(String param1String, Tree param1Tree) throws Exception {
      double[] arrayOfDouble;
      StringBuffer[] arrayOfStringBuffer = new StringBuffer[2];
      if (this.m_ClassProbs == null) {
        arrayOfDouble = param1Tree.m_ClassProbs;
      } else {
        arrayOfDouble = this.m_ClassProbs;
      } 
      long l = REPTree.nextID();
      if (this.m_Attribute == -1) {
        arrayOfStringBuffer[0] = new StringBuffer("\tp = ");
        if (this.m_Info.classAttribute().isNumeric()) {
          arrayOfStringBuffer[0].append(arrayOfDouble[0]);
        } else {
          arrayOfStringBuffer[0].append(Utils.maxIndex(arrayOfDouble));
        } 
        arrayOfStringBuffer[0].append(";\n");
        arrayOfStringBuffer[1] = new StringBuffer("");
      } else {
        StringBuffer stringBuffer1 = new StringBuffer("");
        String str = "      ";
        StringBuffer stringBuffer2 = new StringBuffer("");
        stringBuffer1.append("  static double N").append(Integer.toHexString(hashCode()) + l).append("(Object []i) {\n").append("    double p = Double.NaN;\n");
        stringBuffer1.append("    /* " + this.m_Info.attribute(this.m_Attribute).name() + " */\n");
        stringBuffer1.append("    if (" + sourceExpression(-1) + ") {\n").append("      p = ");
        if (this.m_Info.classAttribute().isNumeric()) {
          stringBuffer1.append(arrayOfDouble[0] + ";\n");
        } else {
          stringBuffer1.append(Utils.maxIndex(arrayOfDouble) + ";\n");
        } 
        stringBuffer1.append("    } ");
        for (byte b = 0; b < this.m_Successors.length; b++) {
          stringBuffer1.append("else if (" + sourceExpression(b) + ") {\n");
          if ((this.m_Successors[b]).m_Attribute == -1) {
            double[] arrayOfDouble1 = (this.m_Successors[b]).m_ClassProbs;
            if (arrayOfDouble1 == null)
              arrayOfDouble1 = this.m_ClassProbs; 
            stringBuffer1.append("      p = ");
            if (this.m_Info.classAttribute().isNumeric()) {
              stringBuffer1.append(arrayOfDouble1[0] + ";\n");
            } else {
              stringBuffer1.append(Utils.maxIndex(arrayOfDouble1) + ";\n");
            } 
          } else {
            StringBuffer[] arrayOfStringBuffer1 = this.m_Successors[b].toSource(param1String, this);
            stringBuffer1.append("" + arrayOfStringBuffer1[0]);
            stringBuffer2.append("" + arrayOfStringBuffer1[1]);
          } 
          stringBuffer1.append("    } ");
          if (b == this.m_Successors.length - 1)
            stringBuffer1.append("\n"); 
        } 
        stringBuffer1.append("    return p;\n  }\n");
        arrayOfStringBuffer[0] = new StringBuffer("    p = " + param1String + ".N");
        arrayOfStringBuffer[0].append(Integer.toHexString(hashCode()) + l).append("(i);\n");
        arrayOfStringBuffer[1] = stringBuffer1.append("" + stringBuffer2);
      } 
      return arrayOfStringBuffer;
    }
    
    protected int toGraph(StringBuffer param1StringBuffer, int param1Int, Tree param1Tree) throws Exception {
      param1Int++;
      if (this.m_Attribute == -1) {
        param1StringBuffer.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + param1Int + leafString(param1Tree) + "\"" + "shape=box]\n");
      } else {
        param1StringBuffer.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + param1Int + ": " + this.m_Info.attribute(this.m_Attribute).name() + "\"]\n");
        for (byte b = 0; b < this.m_Successors.length; b++) {
          param1StringBuffer.append("N" + Integer.toHexString(hashCode()) + "->" + "N" + Integer.toHexString(this.m_Successors[b].hashCode()) + " [label=\"");
          if (this.m_Info.attribute(this.m_Attribute).isNumeric()) {
            if (b == 0) {
              param1StringBuffer.append(" < " + Utils.doubleToString(this.m_SplitPoint, 2));
            } else {
              param1StringBuffer.append(" >= " + Utils.doubleToString(this.m_SplitPoint, 2));
            } 
          } else {
            param1StringBuffer.append(" = " + this.m_Info.attribute(this.m_Attribute).value(b));
          } 
          param1StringBuffer.append("\"]\n");
          param1Int = this.m_Successors[b].toGraph(param1StringBuffer, param1Int, this);
        } 
      } 
      return param1Int;
    }
    
    protected String leafString(Tree param1Tree) throws Exception {
      int i;
      if (this.m_Info.classAttribute().isNumeric()) {
        double d1;
        if (this.m_ClassProbs == null) {
          d1 = param1Tree.m_ClassProbs[0];
        } else {
          d1 = this.m_ClassProbs[0];
        } 
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" : " + Utils.doubleToString(d1, 2));
        double d2 = 0.0D;
        if (this.m_Distribution[1] > 0.0D)
          d2 = this.m_Distribution[0] / this.m_Distribution[1]; 
        stringBuffer.append(" (" + Utils.doubleToString(this.m_Distribution[1], 2) + "/" + Utils.doubleToString(d2, 2) + ")");
        d2 = 0.0D;
        if (this.m_HoldOutDist[0] > 0.0D)
          d2 = this.m_HoldOutError / this.m_HoldOutDist[0]; 
        stringBuffer.append(" [" + Utils.doubleToString(this.m_HoldOutDist[0], 2) + "/" + Utils.doubleToString(d2, 2) + "]");
        return stringBuffer.toString();
      } 
      if (this.m_ClassProbs == null) {
        i = Utils.maxIndex(param1Tree.m_ClassProbs);
      } else {
        i = Utils.maxIndex(this.m_ClassProbs);
      } 
      return " : " + this.m_Info.classAttribute().value(i) + " (" + Utils.doubleToString(Utils.sum(this.m_Distribution), 2) + "/" + Utils.doubleToString(Utils.sum(this.m_Distribution) - this.m_Distribution[i], 2) + ")" + " [" + Utils.doubleToString(Utils.sum(this.m_HoldOutDist), 2) + "/" + Utils.doubleToString(Utils.sum(this.m_HoldOutDist) - this.m_HoldOutDist[i], 2) + "]";
    }
    
    protected String toString(int param1Int, Tree param1Tree) {
      try {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.m_Attribute == -1)
          return leafString(param1Tree); 
        if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
          for (byte b = 0; b < this.m_Successors.length; b++) {
            stringBuffer.append("\n");
            for (byte b1 = 0; b1 < param1Int; b1++)
              stringBuffer.append("|   "); 
            stringBuffer.append(this.m_Info.attribute(this.m_Attribute).name() + " = " + this.m_Info.attribute(this.m_Attribute).value(b));
            stringBuffer.append(this.m_Successors[b].toString(param1Int + 1, this));
          } 
        } else {
          stringBuffer.append("\n");
          byte b;
          for (b = 0; b < param1Int; b++)
            stringBuffer.append("|   "); 
          stringBuffer.append(this.m_Info.attribute(this.m_Attribute).name() + " < " + Utils.doubleToString(this.m_SplitPoint, 2));
          stringBuffer.append(this.m_Successors[0].toString(param1Int + 1, this));
          stringBuffer.append("\n");
          for (b = 0; b < param1Int; b++)
            stringBuffer.append("|   "); 
          stringBuffer.append(this.m_Info.attribute(this.m_Attribute).name() + " >= " + Utils.doubleToString(this.m_SplitPoint, 2));
          stringBuffer.append(this.m_Successors[1].toString(param1Int + 1, this));
        } 
        return stringBuffer.toString();
      } catch (Exception exception) {
        exception.printStackTrace();
        return "Decision tree: tree can't be printed";
      } 
    }
    
    protected void buildTree(int[][] param1ArrayOfint, double[][] param1ArrayOfdouble, Instances param1Instances1, double param1Double1, double[] param1ArrayOfdouble1, Instances param1Instances2, double param1Double2, double param1Double3, int param1Int1, int param1Int2) throws Exception {
      this.m_Info = param1Instances2;
      this.m_HoldOutDist = new double[param1Instances1.numClasses()];
      boolean bool = false;
      if (param1Instances1.classIndex() == 0)
        bool = true; 
      if ((param1ArrayOfint[bool]).length == 0) {
        if (param1Instances1.classAttribute().isNumeric()) {
          this.m_Distribution = new double[2];
        } else {
          this.m_Distribution = new double[param1Instances1.numClasses()];
        } 
        this.m_ClassProbs = null;
        return;
      } 
      double d = 0.0D;
      if (param1Instances1.classAttribute().isNumeric()) {
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        for (byte b = 0; b < (param1ArrayOfint[bool]).length; b++) {
          Instance instance = param1Instances1.instance(param1ArrayOfint[bool][b]);
          d1 += instance.classValue() * param1ArrayOfdouble[bool][b];
          d2 += instance.classValue() * instance.classValue() * param1ArrayOfdouble[bool][b];
          d3 += param1ArrayOfdouble[bool][b];
        } 
        d = singleVariance(d1, d2, d3);
      } 
      this.m_ClassProbs = new double[param1ArrayOfdouble1.length];
      System.arraycopy(param1ArrayOfdouble1, 0, this.m_ClassProbs, 0, param1ArrayOfdouble1.length);
      if (param1Double1 < 2.0D * param1Double2 || (param1Instances1.classAttribute().isNominal() && Utils.eq(this.m_ClassProbs[Utils.maxIndex(this.m_ClassProbs)], Utils.sum(this.m_ClassProbs))) || (param1Instances1.classAttribute().isNumeric() && d / param1Double1 < param1Double3) || (this.this$0.m_MaxDepth >= 0 && param1Int1 >= param1Int2)) {
        this.m_Attribute = -1;
        if (param1Instances1.classAttribute().isNominal()) {
          this.m_Distribution = new double[this.m_ClassProbs.length];
          for (byte b = 0; b < this.m_ClassProbs.length; b++)
            this.m_Distribution[b] = this.m_ClassProbs[b]; 
          Utils.normalize(this.m_ClassProbs);
        } else {
          this.m_Distribution = new double[2];
          this.m_Distribution[0] = d;
          this.m_Distribution[1] = param1Double1;
        } 
        return;
      } 
      double[] arrayOfDouble1 = new double[param1Instances1.numAttributes()];
      double[][][] arrayOfDouble = new double[param1Instances1.numAttributes()][0][0];
      double[][] arrayOfDouble2 = new double[param1Instances1.numAttributes()][0];
      double[][] arrayOfDouble3 = new double[param1Instances1.numAttributes()][0];
      double[] arrayOfDouble4 = new double[param1Instances1.numAttributes()];
      if (param1Instances1.classAttribute().isNominal()) {
        for (byte b = 0; b < param1Instances1.numAttributes(); b++) {
          if (b != param1Instances1.classIndex()) {
            arrayOfDouble4[b] = distribution(arrayOfDouble2, arrayOfDouble, b, param1ArrayOfint[b], param1ArrayOfdouble[b], arrayOfDouble3, param1Instances1);
            arrayOfDouble1[b] = gain(arrayOfDouble[b], priorVal(arrayOfDouble[b]));
          } 
        } 
      } else {
        for (byte b = 0; b < param1Instances1.numAttributes(); b++) {
          if (b != param1Instances1.classIndex())
            arrayOfDouble4[b] = numericDistribution(arrayOfDouble2, arrayOfDouble, b, param1ArrayOfint[b], param1ArrayOfdouble[b], arrayOfDouble3, param1Instances1, arrayOfDouble1); 
        } 
      } 
      this.m_Attribute = Utils.maxIndex(arrayOfDouble1);
      int i = (arrayOfDouble[this.m_Attribute]).length;
      byte b1 = 0;
      byte b2;
      for (b2 = 0; b2 < i; b2++) {
        if (arrayOfDouble3[this.m_Attribute][b2] >= param1Double2)
          b1++; 
        if (b1 > 1)
          break; 
      } 
      if (arrayOfDouble1[this.m_Attribute] > 0.0D && b1 > 1) {
        this.m_SplitPoint = arrayOfDouble4[this.m_Attribute];
        this.m_Prop = arrayOfDouble2[this.m_Attribute];
        int[][][] arrayOfInt = new int[i][param1Instances1.numAttributes()][0];
        double[][][] arrayOfDouble5 = new double[i][param1Instances1.numAttributes()][0];
        splitData(arrayOfInt, arrayOfDouble5, this.m_Attribute, this.m_SplitPoint, param1ArrayOfint, param1ArrayOfdouble, param1Instances1);
        this.m_Successors = new Tree[i];
        for (byte b = 0; b < i; b++) {
          this.m_Successors[b] = new Tree(this.this$0);
          this.m_Successors[b].buildTree(arrayOfInt[b], arrayOfDouble5[b], param1Instances1, arrayOfDouble3[this.m_Attribute][b], arrayOfDouble[this.m_Attribute][b], param1Instances2, param1Double2, param1Double3, param1Int1 + 1, param1Int2);
        } 
      } else {
        this.m_Attribute = -1;
      } 
      if (param1Instances1.classAttribute().isNominal()) {
        this.m_Distribution = new double[this.m_ClassProbs.length];
        for (b2 = 0; b2 < this.m_ClassProbs.length; b2++)
          this.m_Distribution[b2] = this.m_ClassProbs[b2]; 
        Utils.normalize(this.m_ClassProbs);
      } else {
        this.m_Distribution = new double[2];
        this.m_Distribution[0] = d;
        this.m_Distribution[1] = param1Double1;
      } 
    }
    
    protected int numNodes() {
      if (this.m_Attribute == -1)
        return 1; 
      int i = 1;
      for (byte b = 0; b < this.m_Successors.length; b++)
        i += this.m_Successors[b].numNodes(); 
      return i;
    }
    
    protected void splitData(int[][][] param1ArrayOfint, double[][][] param1ArrayOfdouble, int param1Int, double param1Double, int[][] param1ArrayOfint1, double[][] param1ArrayOfdouble1, Instances param1Instances) throws Exception {
      for (byte b = 0; b < param1Instances.numAttributes(); b++) {
        if (b != param1Instances.classIndex()) {
          int[] arrayOfInt;
          if (param1Instances.attribute(param1Int).isNominal()) {
            arrayOfInt = new int[param1Instances.attribute(param1Int).numValues()];
            for (byte b3 = 0; b3 < arrayOfInt.length; b3++) {
              param1ArrayOfint[b3][b] = new int[(param1ArrayOfint1[b]).length];
              param1ArrayOfdouble[b3][b] = new double[(param1ArrayOfint1[b]).length];
            } 
            for (byte b2 = 0; b2 < (param1ArrayOfint1[b]).length; b2++) {
              Instance instance = param1Instances.instance(param1ArrayOfint1[b][b2]);
              if (instance.isMissing(param1Int)) {
                for (byte b4 = 0; b4 < arrayOfInt.length; b4++) {
                  if (this.m_Prop[b4] > 0.0D) {
                    param1ArrayOfint[b4][b][arrayOfInt[b4]] = param1ArrayOfint1[b][b2];
                    param1ArrayOfdouble[b4][b][arrayOfInt[b4]] = this.m_Prop[b4] * param1ArrayOfdouble1[b][b2];
                    arrayOfInt[b4] = arrayOfInt[b4] + 1;
                  } 
                } 
              } else {
                int i = (int)instance.value(param1Int);
                param1ArrayOfint[i][b][arrayOfInt[i]] = param1ArrayOfint1[b][b2];
                param1ArrayOfdouble[i][b][arrayOfInt[i]] = param1ArrayOfdouble1[b][b2];
                arrayOfInt[i] = arrayOfInt[i] + 1;
              } 
            } 
          } else {
            arrayOfInt = new int[2];
            for (byte b3 = 0; b3 < 2; b3++) {
              param1ArrayOfint[b3][b] = new int[(param1ArrayOfint1[b]).length];
              param1ArrayOfdouble[b3][b] = new double[(param1ArrayOfdouble1[b]).length];
            } 
            for (byte b2 = 0; b2 < (param1ArrayOfint1[b]).length; b2++) {
              Instance instance = param1Instances.instance(param1ArrayOfint1[b][b2]);
              if (instance.isMissing(param1Int)) {
                for (byte b4 = 0; b4 < arrayOfInt.length; b4++) {
                  if (this.m_Prop[b4] > 0.0D) {
                    param1ArrayOfint[b4][b][arrayOfInt[b4]] = param1ArrayOfint1[b][b2];
                    param1ArrayOfdouble[b4][b][arrayOfInt[b4]] = this.m_Prop[b4] * param1ArrayOfdouble1[b][b2];
                    arrayOfInt[b4] = arrayOfInt[b4] + 1;
                  } 
                } 
              } else {
                boolean bool = (instance.value(param1Int) < param1Double) ? false : true;
                param1ArrayOfint[bool][b][arrayOfInt[bool]] = param1ArrayOfint1[b][b2];
                param1ArrayOfdouble[bool][b][arrayOfInt[bool]] = param1ArrayOfdouble1[b][b2];
                arrayOfInt[bool] = arrayOfInt[bool] + 1;
              } 
            } 
          } 
          for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
            int[] arrayOfInt1 = new int[arrayOfInt[b1]];
            System.arraycopy(param1ArrayOfint[b1][b], 0, arrayOfInt1, 0, arrayOfInt[b1]);
            param1ArrayOfint[b1][b] = arrayOfInt1;
            double[] arrayOfDouble = new double[arrayOfInt[b1]];
            System.arraycopy(param1ArrayOfdouble[b1][b], 0, arrayOfDouble, 0, arrayOfInt[b1]);
            param1ArrayOfdouble[b1][b] = arrayOfDouble;
          } 
        } 
      } 
    }
    
    protected double distribution(double[][] param1ArrayOfdouble1, double[][][] param1ArrayOfdouble, int param1Int, int[] param1ArrayOfint, double[] param1ArrayOfdouble2, double[][] param1ArrayOfdouble3, Instances param1Instances) throws Exception {
      byte b1;
      double d = Double.NaN;
      Attribute attribute = param1Instances.attribute(param1Int);
      double[][] arrayOfDouble = (double[][])null;
      if (attribute.isNominal()) {
        arrayOfDouble = new double[attribute.numValues()][param1Instances.numClasses()];
        for (b1 = 0; b1 < param1ArrayOfint.length; b1++) {
          Instance instance = param1Instances.instance(param1ArrayOfint[b1]);
          if (instance.isMissing(param1Int))
            break; 
          arrayOfDouble[(int)instance.value(param1Int)][(int)instance.classValue()] = arrayOfDouble[(int)instance.value(param1Int)][(int)instance.classValue()] + param1ArrayOfdouble2[b1];
        } 
      } else {
        double[][] arrayOfDouble1 = new double[2][param1Instances.numClasses()];
        arrayOfDouble = new double[2][param1Instances.numClasses()];
        for (byte b = 0; b < param1ArrayOfint.length; b++) {
          Instance instance = param1Instances.instance(param1ArrayOfint[b]);
          if (instance.isMissing(param1Int))
            break; 
          arrayOfDouble1[1][(int)instance.classValue()] = arrayOfDouble1[1][(int)instance.classValue()] + param1ArrayOfdouble2[b];
        } 
        double d1 = priorVal(arrayOfDouble1);
        System.arraycopy(arrayOfDouble1[1], 0, arrayOfDouble[1], 0, (arrayOfDouble[1]).length);
        double d2 = param1Instances.instance(param1ArrayOfint[0]).value(param1Int);
        double d3 = -1.7976931348623157E308D;
        for (b1 = 0; b1 < param1ArrayOfint.length; b1++) {
          Instance instance = param1Instances.instance(param1ArrayOfint[b1]);
          if (instance.isMissing(param1Int))
            break; 
          if (instance.value(param1Int) > d2) {
            double d4 = gain(arrayOfDouble1, d1);
            if (d4 > d3) {
              d3 = d4;
              d = (instance.value(param1Int) + d2) / 2.0D;
              for (byte b3 = 0; b3 < arrayOfDouble1.length; b3++)
                System.arraycopy(arrayOfDouble1[b3], 0, arrayOfDouble[b3], 0, (arrayOfDouble[b3]).length); 
            } 
          } 
          d2 = instance.value(param1Int);
          arrayOfDouble1[0][(int)instance.classValue()] = arrayOfDouble1[0][(int)instance.classValue()] + param1ArrayOfdouble2[b1];
          arrayOfDouble1[1][(int)instance.classValue()] = arrayOfDouble1[1][(int)instance.classValue()] - param1ArrayOfdouble2[b1];
        } 
      } 
      param1ArrayOfdouble1[param1Int] = new double[arrayOfDouble.length];
      byte b2;
      for (b2 = 0; b2 < (param1ArrayOfdouble1[param1Int]).length; b2++)
        param1ArrayOfdouble1[param1Int][b2] = Utils.sum(arrayOfDouble[b2]); 
      if (Utils.sum(param1ArrayOfdouble1[param1Int]) <= 0.0D) {
        for (b2 = 0; b2 < (param1ArrayOfdouble1[param1Int]).length; b2++)
          param1ArrayOfdouble1[param1Int][b2] = 1.0D / (param1ArrayOfdouble1[param1Int]).length; 
      } else {
        Utils.normalize(param1ArrayOfdouble1[param1Int]);
      } 
      while (b1 < param1ArrayOfint.length) {
        Instance instance = param1Instances.instance(param1ArrayOfint[b1]);
        for (byte b = 0; b < arrayOfDouble.length; b++)
          arrayOfDouble[b][(int)instance.classValue()] = arrayOfDouble[b][(int)instance.classValue()] + param1ArrayOfdouble1[param1Int][b] * param1ArrayOfdouble2[b1]; 
        b1++;
      } 
      param1ArrayOfdouble3[param1Int] = new double[arrayOfDouble.length];
      for (b2 = 0; b2 < arrayOfDouble.length; b2++)
        param1ArrayOfdouble3[param1Int][b2] = param1ArrayOfdouble3[param1Int][b2] + Utils.sum(arrayOfDouble[b2]); 
      param1ArrayOfdouble[param1Int] = arrayOfDouble;
      return d;
    }
    
    protected double numericDistribution(double[][] param1ArrayOfdouble1, double[][][] param1ArrayOfdouble, int param1Int, int[] param1ArrayOfint, double[] param1ArrayOfdouble2, double[][] param1ArrayOfdouble3, Instances param1Instances, double[] param1ArrayOfdouble4) throws Exception {
      byte b1;
      double d1 = Double.NaN;
      Attribute attribute = param1Instances.attribute(param1Int);
      double[][] arrayOfDouble = (double[][])null;
      double[] arrayOfDouble1 = null;
      double[] arrayOfDouble2 = null;
      double[] arrayOfDouble3 = null;
      double d2 = 0.0D;
      double d3 = 0.0D;
      double d4 = 0.0D;
      if (attribute.isNominal()) {
        arrayOfDouble1 = new double[attribute.numValues()];
        arrayOfDouble2 = new double[attribute.numValues()];
        arrayOfDouble3 = new double[attribute.numValues()];
        for (b1 = 0; b1 < param1ArrayOfint.length; b1++) {
          Instance instance = param1Instances.instance(param1ArrayOfint[b1]);
          if (instance.isMissing(param1Int))
            break; 
          int i = (int)instance.value(param1Int);
          arrayOfDouble1[i] = arrayOfDouble1[i] + instance.classValue() * param1ArrayOfdouble2[b1];
          arrayOfDouble2[i] = arrayOfDouble2[i] + instance.classValue() * instance.classValue() * param1ArrayOfdouble2[b1];
          arrayOfDouble3[i] = arrayOfDouble3[i] + param1ArrayOfdouble2[b1];
        } 
        d2 = Utils.sum(arrayOfDouble1);
        d3 = Utils.sum(arrayOfDouble2);
        d4 = Utils.sum(arrayOfDouble3);
      } else {
        arrayOfDouble1 = new double[2];
        arrayOfDouble2 = new double[2];
        arrayOfDouble3 = new double[2];
        double[] arrayOfDouble4 = new double[2];
        double[] arrayOfDouble5 = new double[2];
        double[] arrayOfDouble6 = new double[2];
        for (byte b = 0; b < param1ArrayOfint.length; b++) {
          Instance instance = param1Instances.instance(param1ArrayOfint[b]);
          if (instance.isMissing(param1Int))
            break; 
          arrayOfDouble4[1] = arrayOfDouble4[1] + instance.classValue() * param1ArrayOfdouble2[b];
          arrayOfDouble5[1] = arrayOfDouble5[1] + instance.classValue() * instance.classValue() * param1ArrayOfdouble2[b];
          arrayOfDouble6[1] = arrayOfDouble6[1] + param1ArrayOfdouble2[b];
        } 
        d2 = arrayOfDouble4[1];
        d3 = arrayOfDouble5[1];
        d4 = arrayOfDouble6[1];
        arrayOfDouble1[1] = arrayOfDouble4[1];
        arrayOfDouble2[1] = arrayOfDouble5[1];
        arrayOfDouble3[1] = arrayOfDouble6[1];
        double d8 = param1Instances.instance(param1ArrayOfint[0]).value(param1Int);
        double d9 = Double.MAX_VALUE;
        for (b1 = 0; b1 < param1ArrayOfint.length; b1++) {
          Instance instance = param1Instances.instance(param1ArrayOfint[b1]);
          if (instance.isMissing(param1Int))
            break; 
          if (instance.value(param1Int) > d8) {
            double d = variance(arrayOfDouble4, arrayOfDouble5, arrayOfDouble6);
            if (d < d9) {
              d9 = d;
              d1 = (instance.value(param1Int) + d8) / 2.0D;
              for (byte b3 = 0; b3 < 2; b3++) {
                arrayOfDouble1[b3] = arrayOfDouble4[b3];
                arrayOfDouble2[b3] = arrayOfDouble5[b3];
                arrayOfDouble3[b3] = arrayOfDouble6[b3];
              } 
            } 
          } 
          d8 = instance.value(param1Int);
          double d10 = instance.classValue() * param1ArrayOfdouble2[b1];
          double d11 = instance.classValue() * d10;
          arrayOfDouble4[0] = arrayOfDouble4[0] + d10;
          arrayOfDouble5[0] = arrayOfDouble5[0] + d11;
          arrayOfDouble6[0] = arrayOfDouble6[0] + param1ArrayOfdouble2[b1];
          arrayOfDouble4[1] = arrayOfDouble4[1] - d10;
          arrayOfDouble5[1] = arrayOfDouble5[1] - d11;
          arrayOfDouble6[1] = arrayOfDouble6[1] - param1ArrayOfdouble2[b1];
        } 
      } 
      param1ArrayOfdouble1[param1Int] = new double[arrayOfDouble1.length];
      byte b2;
      for (b2 = 0; b2 < (param1ArrayOfdouble1[param1Int]).length; b2++)
        param1ArrayOfdouble1[param1Int][b2] = arrayOfDouble3[b2]; 
      if (Utils.sum(param1ArrayOfdouble1[param1Int]) <= 0.0D) {
        for (b2 = 0; b2 < (param1ArrayOfdouble1[param1Int]).length; b2++)
          param1ArrayOfdouble1[param1Int][b2] = 1.0D / (param1ArrayOfdouble1[param1Int]).length; 
      } else {
        Utils.normalize(param1ArrayOfdouble1[param1Int]);
      } 
      while (b1 < param1ArrayOfint.length) {
        Instance instance = param1Instances.instance(param1ArrayOfint[b1]);
        for (byte b = 0; b < arrayOfDouble1.length; b++) {
          arrayOfDouble1[b] = arrayOfDouble1[b] + param1ArrayOfdouble1[param1Int][b] * instance.classValue() * param1ArrayOfdouble2[b1];
          arrayOfDouble2[b] = arrayOfDouble2[b] + param1ArrayOfdouble1[param1Int][b] * instance.classValue() * instance.classValue() * param1ArrayOfdouble2[b1];
          arrayOfDouble3[b] = arrayOfDouble3[b] + param1ArrayOfdouble1[param1Int][b] * param1ArrayOfdouble2[b1];
        } 
        d2 += instance.classValue() * param1ArrayOfdouble2[b1];
        d3 += instance.classValue() * instance.classValue() * param1ArrayOfdouble2[b1];
        d4 += param1ArrayOfdouble2[b1];
        b1++;
      } 
      arrayOfDouble = new double[arrayOfDouble1.length][param1Instances.numClasses()];
      for (b2 = 0; b2 < arrayOfDouble1.length; b2++) {
        if (arrayOfDouble3[b2] > 0.0D) {
          arrayOfDouble[b2][0] = arrayOfDouble1[b2] / arrayOfDouble3[b2];
        } else {
          arrayOfDouble[b2][0] = d2 / d4;
        } 
      } 
      double d5 = singleVariance(d2, d3, d4);
      double d6 = variance(arrayOfDouble1, arrayOfDouble2, arrayOfDouble3);
      double d7 = d5 - d6;
      param1ArrayOfdouble3[param1Int] = arrayOfDouble3;
      param1ArrayOfdouble[param1Int] = arrayOfDouble;
      param1ArrayOfdouble4[param1Int] = d7;
      return d1;
    }
    
    protected double variance(double[] param1ArrayOfdouble1, double[] param1ArrayOfdouble2, double[] param1ArrayOfdouble3) {
      double d = 0.0D;
      for (byte b = 0; b < param1ArrayOfdouble1.length; b++) {
        if (param1ArrayOfdouble3[b] > 0.0D)
          d += singleVariance(param1ArrayOfdouble1[b], param1ArrayOfdouble2[b], param1ArrayOfdouble3[b]); 
      } 
      return d;
    }
    
    protected double singleVariance(double param1Double1, double param1Double2, double param1Double3) {
      return param1Double2 - param1Double1 * param1Double1 / param1Double3;
    }
    
    protected double priorVal(double[][] param1ArrayOfdouble) {
      return ContingencyTables.entropyOverColumns(param1ArrayOfdouble);
    }
    
    protected double gain(double[][] param1ArrayOfdouble, double param1Double) {
      return param1Double - ContingencyTables.entropyConditionedOnRows(param1ArrayOfdouble);
    }
    
    protected double reducedErrorPrune() throws Exception {
      if (this.m_Attribute == -1)
        return this.m_HoldOutError; 
      double d = 0.0D;
      for (byte b = 0; b < this.m_Successors.length; b++)
        d += this.m_Successors[b].reducedErrorPrune(); 
      if (d >= this.m_HoldOutError) {
        this.m_Attribute = -1;
        this.m_Successors = null;
        return this.m_HoldOutError;
      } 
      return d;
    }
    
    protected void insertHoldOutSet(Instances param1Instances) throws Exception {
      for (byte b = 0; b < param1Instances.numInstances(); b++)
        insertHoldOutInstance(param1Instances.instance(b), param1Instances.instance(b).weight(), this); 
    }
    
    protected void insertHoldOutInstance(Instance param1Instance, double param1Double, Tree param1Tree) throws Exception {
      if (param1Instance.classAttribute().isNominal()) {
        this.m_HoldOutDist[(int)param1Instance.classValue()] = this.m_HoldOutDist[(int)param1Instance.classValue()] + param1Double;
        int i = 0;
        if (this.m_ClassProbs == null) {
          i = Utils.maxIndex(param1Tree.m_ClassProbs);
        } else {
          i = Utils.maxIndex(this.m_ClassProbs);
        } 
        if (i != (int)param1Instance.classValue())
          this.m_HoldOutError += param1Double; 
      } else {
        this.m_HoldOutDist[0] = this.m_HoldOutDist[0] + param1Double;
        double d = 0.0D;
        if (this.m_ClassProbs == null) {
          d = param1Tree.m_ClassProbs[0] - param1Instance.classValue();
        } else {
          d = this.m_ClassProbs[0] - param1Instance.classValue();
        } 
        this.m_HoldOutError += d * d * param1Double;
      } 
      if (this.m_Attribute != -1)
        if (param1Instance.isMissing(this.m_Attribute)) {
          for (byte b = 0; b < this.m_Successors.length; b++) {
            if (this.m_Prop[b] > 0.0D)
              this.m_Successors[b].insertHoldOutInstance(param1Instance, param1Double * this.m_Prop[b], this); 
          } 
        } else if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
          this.m_Successors[(int)param1Instance.value(this.m_Attribute)].insertHoldOutInstance(param1Instance, param1Double, this);
        } else if (param1Instance.value(this.m_Attribute) < this.m_SplitPoint) {
          this.m_Successors[0].insertHoldOutInstance(param1Instance, param1Double, this);
        } else {
          this.m_Successors[1].insertHoldOutInstance(param1Instance, param1Double, this);
        }  
    }
    
    protected void backfitHoldOutSet(Instances param1Instances) throws Exception {
      for (byte b = 0; b < param1Instances.numInstances(); b++)
        backfitHoldOutInstance(param1Instances.instance(b), param1Instances.instance(b).weight(), this); 
    }
    
    protected void backfitHoldOutInstance(Instance param1Instance, double param1Double, Tree param1Tree) throws Exception {
      if (param1Instance.classAttribute().isNominal()) {
        if (this.m_ClassProbs == null)
          this.m_ClassProbs = new double[param1Instance.numClasses()]; 
        System.arraycopy(this.m_Distribution, 0, this.m_ClassProbs, 0, param1Instance.numClasses());
        this.m_ClassProbs[(int)param1Instance.classValue()] = this.m_ClassProbs[(int)param1Instance.classValue()] + param1Double;
        Utils.normalize(this.m_ClassProbs);
      } else {
        if (this.m_ClassProbs == null)
          this.m_ClassProbs = new double[1]; 
        this.m_ClassProbs[0] = this.m_ClassProbs[0] * this.m_Distribution[1];
        this.m_ClassProbs[0] = this.m_ClassProbs[0] + param1Double * param1Instance.classValue();
        this.m_ClassProbs[0] = this.m_ClassProbs[0] / (this.m_Distribution[1] + param1Double);
      } 
      if (this.m_Attribute != -1)
        if (param1Instance.isMissing(this.m_Attribute)) {
          for (byte b = 0; b < this.m_Successors.length; b++) {
            if (this.m_Prop[b] > 0.0D)
              this.m_Successors[b].backfitHoldOutInstance(param1Instance, param1Double * this.m_Prop[b], this); 
          } 
        } else if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
          this.m_Successors[(int)param1Instance.value(this.m_Attribute)].backfitHoldOutInstance(param1Instance, param1Double, this);
        } else if (param1Instance.value(this.m_Attribute) < this.m_SplitPoint) {
          this.m_Successors[0].backfitHoldOutInstance(param1Instance, param1Double, this);
        } else {
          this.m_Successors[1].backfitHoldOutInstance(param1Instance, param1Double, this);
        }  
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\REPTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */