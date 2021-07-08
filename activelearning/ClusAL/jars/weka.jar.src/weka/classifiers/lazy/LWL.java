package weka.classifiers.lazy;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class LWL extends SingleClassifierEnhancer implements UpdateableClassifier, WeightedInstancesHandler {
  protected Instances m_Train;
  
  protected double[] m_Min;
  
  protected double[] m_Max;
  
  protected boolean m_NoAttribNorm = false;
  
  protected int m_kNN = -1;
  
  protected int m_WeightKernel = 0;
  
  protected boolean m_UseAllK = true;
  
  protected static final int LINEAR = 0;
  
  protected static final int EPANECHNIKOV = 1;
  
  protected static final int TRICUBE = 2;
  
  protected static final int INVERSE = 3;
  
  protected static final int GAUSS = 4;
  
  protected static final int CONSTANT = 5;
  
  public String globalInfo() {
    return "Class for performing locally weighted learning. Can do classification (e.g. using naive Bayes) or regression (e.g. using linear regression). The base learner needs to implement WeightedInstancesHandler. For more info, see\n\nEibe Frank, Mark Hall, and Bernhard Pfahringer (2003). \"Locally Weighted Naive Bayes\". Conference on Uncertainty in AI.\n\nAtkeson, C., A. Moore, and S. Schaal (1996) \"Locally weighted learning\" AI Reviews.";
  }
  
  protected String defaultClassifierString() {
    return "weka.classifiers.trees.DecisionStump";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tDo not normalize numeric attributes' values in distance calculation.\n\t(default DO normalization)", "N", 0, "-N"));
    vector.addElement(new Option("\tSet the number of neighbours used to set the kernel bandwidth.\n\t(default all)", "K", 1, "-K <number of neighbours>"));
    vector.addElement(new Option("\tSet the weighting kernel shape to use. 0=Linear, 1=Epanechnikov,\n\t2=Tricube, 3=Inverse, 4=Gaussian.\n\t(default 0 = Linear)", "U", 1, "-U <number of weighting method>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('K', paramArrayOfString);
    if (str1.length() != 0) {
      setKNN(Integer.parseInt(str1));
    } else {
      setKNN(0);
    } 
    String str2 = Utils.getOption('U', paramArrayOfString);
    if (str2.length() != 0) {
      setWeightingKernel(Integer.parseInt(str2));
    } else {
      setWeightingKernel(0);
    } 
    setDontNormalize(Utils.getFlag('N', paramArrayOfString));
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 5];
    byte b = 0;
    arrayOfString2[b++] = "-U";
    arrayOfString2[b++] = "" + getWeightingKernel();
    arrayOfString2[b++] = "-K";
    arrayOfString2[b++] = "" + getKNN();
    if (getDontNormalize()) {
      arrayOfString2[b++] = "-N";
    } else {
      arrayOfString2[b++] = "";
    } 
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String KNNTipText() {
    return "How many neighbours are used to determine the width of the weighting function (<= 0 means all neighbours).";
  }
  
  public void setKNN(int paramInt) {
    this.m_kNN = paramInt;
    if (paramInt <= 0) {
      this.m_kNN = 0;
      this.m_UseAllK = true;
    } else {
      this.m_UseAllK = false;
    } 
  }
  
  public int getKNN() {
    return this.m_kNN;
  }
  
  public String weightingKernelTipText() {
    return "Determines weighting function. [0 = Linear, 1 = Epnechnikov,2 = Tricube, 3 = Inverse, 4 = Gaussian and 5 = Constant. (default 0 = Linear)].";
  }
  
  public void setWeightingKernel(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2 && paramInt != 3 && paramInt != 4 && paramInt != 5)
      return; 
    this.m_WeightKernel = paramInt;
  }
  
  public int getWeightingKernel() {
    return this.m_WeightKernel;
  }
  
  public String dontNormalizeTipText() {
    return "Turns off normalization for attribute values in distance calculation.";
  }
  
  public boolean getDontNormalize() {
    return this.m_NoAttribNorm;
  }
  
  public void setDontNormalize(boolean paramBoolean) {
    this.m_NoAttribNorm = paramBoolean;
  }
  
  protected double getAttributeMin(int paramInt) {
    return this.m_Min[paramInt];
  }
  
  protected double getAttributeMax(int paramInt) {
    return this.m_Max[paramInt];
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    // Byte code:
    //   0: aload_0
    //   1: getfield m_Classifier : Lweka/classifiers/Classifier;
    //   4: instanceof weka/core/WeightedInstancesHandler
    //   7: ifne -> 20
    //   10: new java/lang/IllegalArgumentException
    //   13: dup
    //   14: ldc 'Classifier must be a WeightedInstancesHandler!'
    //   16: invokespecial <init> : (Ljava/lang/String;)V
    //   19: athrow
    //   20: aload_1
    //   21: invokevirtual classIndex : ()I
    //   24: ifge -> 37
    //   27: new java/lang/Exception
    //   30: dup
    //   31: ldc 'No class attribute assigned to instances'
    //   33: invokespecial <init> : (Ljava/lang/String;)V
    //   36: athrow
    //   37: aload_1
    //   38: invokevirtual checkForStringAttributes : ()Z
    //   41: ifeq -> 54
    //   44: new weka/core/UnsupportedAttributeTypeException
    //   47: dup
    //   48: ldc 'Cannot handle string attributes!'
    //   50: invokespecial <init> : (Ljava/lang/String;)V
    //   53: athrow
    //   54: aload_0
    //   55: new weka/core/Instances
    //   58: dup
    //   59: aload_1
    //   60: iconst_0
    //   61: aload_1
    //   62: invokevirtual numInstances : ()I
    //   65: invokespecial <init> : (Lweka/core/Instances;II)V
    //   68: putfield m_Train : Lweka/core/Instances;
    //   71: aload_0
    //   72: getfield m_Train : Lweka/core/Instances;
    //   75: invokevirtual deleteWithMissingClass : ()V
    //   78: aload_0
    //   79: aload_0
    //   80: getfield m_Train : Lweka/core/Instances;
    //   83: invokevirtual numAttributes : ()I
    //   86: newarray double
    //   88: putfield m_Min : [D
    //   91: aload_0
    //   92: aload_0
    //   93: getfield m_Train : Lweka/core/Instances;
    //   96: invokevirtual numAttributes : ()I
    //   99: newarray double
    //   101: putfield m_Max : [D
    //   104: iconst_0
    //   105: istore_2
    //   106: iload_2
    //   107: aload_0
    //   108: getfield m_Train : Lweka/core/Instances;
    //   111: invokevirtual numAttributes : ()I
    //   114: if_icmpge -> 139
    //   117: aload_0
    //   118: getfield m_Min : [D
    //   121: iload_2
    //   122: aload_0
    //   123: getfield m_Max : [D
    //   126: iload_2
    //   127: ldc2_w NaN
    //   130: dup2_x2
    //   131: dastore
    //   132: dastore
    //   133: iinc #2, 1
    //   136: goto -> 106
    //   139: iconst_0
    //   140: istore_2
    //   141: iload_2
    //   142: aload_0
    //   143: getfield m_Train : Lweka/core/Instances;
    //   146: invokevirtual numInstances : ()I
    //   149: if_icmpge -> 170
    //   152: aload_0
    //   153: aload_0
    //   154: getfield m_Train : Lweka/core/Instances;
    //   157: iload_2
    //   158: invokevirtual instance : (I)Lweka/core/Instance;
    //   161: invokespecial updateMinMax : (Lweka/core/Instance;)V
    //   164: iinc #2, 1
    //   167: goto -> 141
    //   170: return
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    if (!this.m_Train.equalHeaders(paramInstance.dataset()))
      throw new Exception("Incompatible instance types"); 
    if (!paramInstance.classIsMissing()) {
      updateMinMax(paramInstance);
      this.m_Train.add(paramInstance);
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    MyHeap myHeap;
    if (this.m_Train.numInstances() == 0)
      throw new Exception("No training instances!"); 
    updateMinMax(paramInstance);
    double[] arrayOfDouble = new double[this.m_Train.numInstances()];
    int i = arrayOfDouble.length - 1;
    if (!this.m_UseAllK && this.m_kNN < i) {
      i = this.m_kNN;
      myHeap = new MyHeap(this, i);
    } else {
      myHeap = new MyHeap(this, arrayOfDouble.length);
    } 
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < this.m_Train.numInstances()) {
      MyHeapElement myHeapElement;
      switch (this.m_WeightKernel) {
        case 0:
        case 1:
        case 2:
          if (b2 < i) {
            arrayOfDouble[b1] = distance(paramInstance, this.m_Train.instance(b1));
            myHeap.put(b1, arrayOfDouble[b1]);
            break;
          } 
          myHeapElement = myHeap.peek();
          arrayOfDouble[b1] = distance(paramInstance, this.m_Train.instance(b1), myHeapElement.distance);
          if (arrayOfDouble[b1] < myHeapElement.distance) {
            myHeap.get();
            myHeap.put(b1, arrayOfDouble[b1]);
          } 
          break;
        default:
          arrayOfDouble[b1] = distance(paramInstance, this.m_Train.instance(b1));
          break;
      } 
      b2++;
      b1++;
    } 
    int[] arrayOfInt = Utils.sort(arrayOfDouble);
    if (this.m_Debug) {
      System.out.println("Instance Distances");
      for (b2 = 0; b2 < arrayOfInt.length; b2++)
        System.out.println("" + arrayOfDouble[arrayOfInt[b2]]); 
    } 
    double d1 = arrayOfDouble[arrayOfInt[i - 1]];
    if (d1 <= 0.0D) {
      for (int j = i; j < arrayOfInt.length; j++) {
        if (arrayOfDouble[arrayOfInt[j]] > d1) {
          d1 = arrayOfDouble[arrayOfInt[j]];
          break;
        } 
      } 
      if (d1 <= 0.0D)
        throw new Exception("All training instances coincide with test instance!"); 
    } 
    byte b3;
    for (b3 = 0; b3 < arrayOfDouble.length; b3++)
      arrayOfDouble[b3] = arrayOfDouble[b3] / d1; 
    for (b3 = 0; b3 < arrayOfDouble.length; b3++) {
      switch (this.m_WeightKernel) {
        case 0:
          arrayOfDouble[b3] = Math.max(1.0001D - arrayOfDouble[b3], 0.0D);
          break;
        case 1:
          if (arrayOfDouble[b3] <= 1.0D) {
            arrayOfDouble[b3] = 0.75D * (1.0001D - arrayOfDouble[b3] * arrayOfDouble[b3]);
            break;
          } 
          arrayOfDouble[b3] = 0.0D;
          break;
        case 2:
          if (arrayOfDouble[b3] <= 1.0D) {
            arrayOfDouble[b3] = Math.pow(1.0001D - Math.pow(arrayOfDouble[b3], 3.0D), 3.0D);
            break;
          } 
          arrayOfDouble[b3] = 0.0D;
          break;
        case 5:
          if (arrayOfDouble[b3] <= 1.0D) {
            arrayOfDouble[b3] = 1.0D;
            break;
          } 
          arrayOfDouble[b3] = 0.0D;
          break;
        case 3:
          arrayOfDouble[b3] = 1.0D / (1.0D + arrayOfDouble[b3]);
          break;
        case 4:
          arrayOfDouble[b3] = Math.exp(-arrayOfDouble[b3] * arrayOfDouble[b3]);
          break;
      } 
    } 
    if (this.m_Debug) {
      System.out.println("Instance Weights");
      for (b3 = 0; b3 < arrayOfInt.length; b3++)
        System.out.println("" + arrayOfDouble[arrayOfInt[b3]]); 
    } 
    Instances instances = new Instances(this.m_Train, 0);
    double d2 = 0.0D;
    double d3 = 0.0D;
    byte b4;
    for (b4 = 0; b4 < arrayOfInt.length; b4++) {
      double d = arrayOfDouble[arrayOfInt[b4]];
      if (d < 1.0E-20D)
        break; 
      Instance instance = (Instance)this.m_Train.instance(arrayOfInt[b4]).copy();
      d2 += instance.weight();
      d3 += instance.weight() * d;
      instance.setWeight(instance.weight() * d);
      instances.add(instance);
    } 
    if (this.m_Debug)
      System.out.println("Kept " + instances.numInstances() + " out of " + this.m_Train.numInstances() + " instances"); 
    for (b4 = 0; b4 < instances.numInstances(); b4++) {
      Instance instance = instances.instance(b4);
      instance.setWeight(instance.weight() * d2 / d3);
    } 
    this.m_Classifier.buildClassifier(instances);
    if (this.m_Debug) {
      System.out.println("Classifying test instance: " + paramInstance);
      System.out.println("Built base classifier:\n" + this.m_Classifier.toString());
    } 
    return this.m_Classifier.distributionForInstance(paramInstance);
  }
  
  public String toString() {
    if (this.m_Train == null)
      return "Locally weighted learning: No model built yet."; 
    null = "Locally weighted learning\n===========================\n";
    null = null + "Using classifier: " + this.m_Classifier.getClass().getName() + "\n";
    switch (this.m_WeightKernel) {
      case 0:
        null = null + "Using linear weighting kernels\n";
        break;
      case 1:
        null = null + "Using epanechnikov weighting kernels\n";
        break;
      case 2:
        null = null + "Using tricube weighting kernels\n";
        break;
      case 3:
        null = null + "Using inverse-distance weighting kernels\n";
        break;
      case 4:
        null = null + "Using gaussian weighting kernels\n";
        break;
      case 5:
        null = null + "Using constant weighting kernels\n";
        break;
    } 
    return null + "Using " + (this.m_UseAllK ? "all" : ("" + this.m_kNN)) + " neighbours";
  }
  
  private double distance(Instance paramInstance1, Instance paramInstance2) throws Exception {
    return distance(paramInstance1, paramInstance2, Math.sqrt(Double.MAX_VALUE));
  }
  
  private double distance(Instance paramInstance1, Instance paramInstance2, double paramDouble) throws Exception {
    return euclideanDistance(paramInstance1, paramInstance2, paramDouble);
  }
  
  private double euclideanDistance(Instance paramInstance1, Instance paramInstance2, double paramDouble) {
    double d = 0.0D;
    paramDouble *= paramDouble;
    byte b1 = 0;
    byte b2 = 0;
    while (true) {
      if (b1 < paramInstance1.numValues() || b2 < paramInstance2.numValues()) {
        int i;
        int j;
        double d1;
        if (b1 >= paramInstance1.numValues()) {
          i = this.m_Train.numAttributes();
        } else {
          i = paramInstance1.index(b1);
        } 
        if (b2 >= paramInstance2.numValues()) {
          j = this.m_Train.numAttributes();
        } else {
          j = paramInstance2.index(b2);
        } 
        if (i == this.m_Train.classIndex()) {
          b1++;
          continue;
        } 
        if (j == this.m_Train.classIndex()) {
          b2++;
          continue;
        } 
        if (i == j) {
          d1 = difference(i, paramInstance1.valueSparse(b1), paramInstance2.valueSparse(b2));
          b1++;
          b2++;
        } else if (i > j) {
          d1 = difference(j, 0.0D, paramInstance2.valueSparse(b2));
          b2++;
        } else {
          d1 = difference(i, paramInstance1.valueSparse(b1), 0.0D);
          b1++;
        } 
        d += d1 * d1;
        if (d > paramDouble)
          return Double.MAX_VALUE; 
        continue;
      } 
      return Math.sqrt(d);
    } 
  }
  
  private double difference(int paramInt, double paramDouble1, double paramDouble2) {
    switch (this.m_Train.attribute(paramInt).type()) {
      case 1:
        return (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2) || (int)paramDouble1 != (int)paramDouble2) ? 1.0D : 0.0D;
      case 0:
        if (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2)) {
          double d;
          if (Instance.isMissingValue(paramDouble1) && Instance.isMissingValue(paramDouble2))
            return !this.m_NoAttribNorm ? 1.0D : (this.m_Max[paramInt] - this.m_Min[paramInt]); 
          if (Instance.isMissingValue(paramDouble2)) {
            d = !this.m_NoAttribNorm ? norm(paramDouble1, paramInt) : paramDouble1;
          } else {
            d = !this.m_NoAttribNorm ? norm(paramDouble2, paramInt) : paramDouble2;
          } 
          if (!this.m_NoAttribNorm && d < 0.5D) {
            d = 1.0D - d;
          } else if (this.m_NoAttribNorm == true) {
            return (this.m_Max[paramInt] - d > d - this.m_Min[paramInt]) ? (this.m_Max[paramInt] - d) : (d - this.m_Min[paramInt]);
          } 
          return d;
        } 
        return !this.m_NoAttribNorm ? (norm(paramDouble1, paramInt) - norm(paramDouble2, paramInt)) : (paramDouble1 - paramDouble2);
    } 
    return 0.0D;
  }
  
  private double norm(double paramDouble, int paramInt) {
    return (Double.isNaN(this.m_Min[paramInt]) || Utils.eq(this.m_Max[paramInt], this.m_Min[paramInt])) ? 0.0D : ((paramDouble - this.m_Min[paramInt]) / (this.m_Max[paramInt] - this.m_Min[paramInt]));
  }
  
  private void updateMinMax(Instance paramInstance) {
    for (byte b = 0; b < this.m_Train.numAttributes(); b++) {
      if (!paramInstance.isMissing(b))
        if (Double.isNaN(this.m_Min[b])) {
          this.m_Min[b] = paramInstance.value(b);
          this.m_Max[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) < this.m_Min[b]) {
          this.m_Min[b] = paramInstance.value(b);
        } else if (paramInstance.value(b) > this.m_Max[b]) {
          this.m_Max[b] = paramInstance.value(b);
        }  
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel((Classifier)new LWL(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  private class MyHeapElement {
    int index;
    
    double distance;
    
    private final LWL this$0;
    
    public MyHeapElement(LWL this$0, int param1Int, double param1Double) {
      this.this$0 = this$0;
      this.distance = param1Double;
      this.index = param1Int;
    }
  }
  
  private class MyHeap {
    LWL.MyHeapElement[] m_heap;
    
    private final LWL this$0;
    
    public MyHeap(LWL this$0, int param1Int) {
      this.this$0 = this$0;
      this.m_heap = null;
      if (param1Int % 2 == 0)
        param1Int++; 
      this.m_heap = new LWL.MyHeapElement[param1Int + 1];
      this.m_heap[0] = new LWL.MyHeapElement(this$0, 0, 0.0D);
    }
    
    public int size() {
      return (this.m_heap[0]).index;
    }
    
    public LWL.MyHeapElement peek() {
      return this.m_heap[1];
    }
    
    public LWL.MyHeapElement get() throws Exception {
      if ((this.m_heap[0]).index == 0)
        throw new Exception("No elements present in the heap"); 
      LWL.MyHeapElement myHeapElement = this.m_heap[1];
      this.m_heap[1] = this.m_heap[(this.m_heap[0]).index];
      (this.m_heap[0]).index--;
      downheap();
      return myHeapElement;
    }
    
    public void put(int param1Int, double param1Double) throws Exception {
      if ((this.m_heap[0]).index + 1 > this.m_heap.length - 1)
        throw new Exception("the number of elements cannot exceed the initially set maximum limit"); 
      (this.m_heap[0]).index++;
      this.m_heap[(this.m_heap[0]).index] = new LWL.MyHeapElement(this.this$0, param1Int, param1Double);
      upheap();
    }
    
    private void upheap() {
      int i = (this.m_heap[0]).index;
      while (i > 1 && (this.m_heap[i]).distance > (this.m_heap[i / 2]).distance) {
        LWL.MyHeapElement myHeapElement = this.m_heap[i];
        this.m_heap[i] = this.m_heap[i / 2];
        i /= 2;
        this.m_heap[i] = myHeapElement;
      } 
    }
    
    private void downheap() {
      int i = 1;
      while (2 * i <= (this.m_heap[0]).index && ((this.m_heap[i]).distance < (this.m_heap[2 * i]).distance || (this.m_heap[i]).distance < (this.m_heap[2 * i + 1]).distance)) {
        if (2 * i + 1 <= (this.m_heap[0]).index) {
          if ((this.m_heap[2 * i]).distance > (this.m_heap[2 * i + 1]).distance) {
            LWL.MyHeapElement myHeapElement2 = this.m_heap[i];
            this.m_heap[i] = this.m_heap[2 * i];
            i = 2 * i;
            this.m_heap[i] = myHeapElement2;
            continue;
          } 
          LWL.MyHeapElement myHeapElement1 = this.m_heap[i];
          this.m_heap[i] = this.m_heap[2 * i + 1];
          i = 2 * i + 1;
          this.m_heap[i] = myHeapElement1;
          continue;
        } 
        LWL.MyHeapElement myHeapElement = this.m_heap[i];
        this.m_heap[i] = this.m_heap[2 * i];
        i = 2 * i;
        this.m_heap[i] = myHeapElement;
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\LWL.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */