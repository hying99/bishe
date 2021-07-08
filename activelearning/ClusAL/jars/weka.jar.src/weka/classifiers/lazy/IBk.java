package weka.classifiers.lazy;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

public class IBk extends Classifier implements OptionHandler, UpdateableClassifier, WeightedInstancesHandler {
  protected Instances m_Train;
  
  protected int m_NumClasses;
  
  protected int m_ClassType;
  
  protected double[] m_Min;
  
  protected double[] m_Max;
  
  protected int m_kNN;
  
  protected int m_kNNUpper;
  
  protected boolean m_kNNValid;
  
  protected int m_WindowSize;
  
  protected int m_DistanceWeighting;
  
  protected boolean m_CrossValidate;
  
  protected boolean m_MeanSquared;
  
  protected boolean m_DontNormalize;
  
  public static final int WEIGHT_NONE = 1;
  
  public static final int WEIGHT_INVERSE = 2;
  
  public static final int WEIGHT_SIMILARITY = 4;
  
  public static final Tag[] TAGS_WEIGHTING = new Tag[] { new Tag(1, "No distance weighting"), new Tag(2, "Weight by 1/distance"), new Tag(4, "Weight by 1-distance") };
  
  protected double m_NumAttributesUsed;
  
  public String globalInfo() {
    return "K-nearest neighbours classifier. Normalizes attributes by default. Can select appropriate value of K based on cross-validation. Can also do distance weighting. For more information, see\n\nAha, D., and D. Kibler (1991) \"Instance-based learning algorithms\", Machine Learning, vol.6, pp. 37-66.";
  }
  
  public IBk(int paramInt) {
    init();
    setKNN(paramInt);
  }
  
  public IBk() {
    init();
  }
  
  public String KNNTipText() {
    return "The number of neighbours to use.";
  }
  
  public void setKNN(int paramInt) {
    this.m_kNN = paramInt;
    this.m_kNNUpper = paramInt;
    this.m_kNNValid = false;
  }
  
  public int getKNN() {
    return this.m_kNN;
  }
  
  public String windowSizeTipText() {
    return "Gets the maximum number of instances allowed in the training pool. The addition of new instances above this value will result in old instances being removed. A value of 0 signifies no limit to the number of training instances.";
  }
  
  public int getWindowSize() {
    return this.m_WindowSize;
  }
  
  public void setWindowSize(int paramInt) {
    this.m_WindowSize = paramInt;
  }
  
  public String distanceWeightingTipText() {
    return "Gets the distance weighting method used.";
  }
  
  public SelectedTag getDistanceWeighting() {
    return new SelectedTag(this.m_DistanceWeighting, TAGS_WEIGHTING);
  }
  
  public void setDistanceWeighting(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_WEIGHTING)
      this.m_DistanceWeighting = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String meanSquaredTipText() {
    return "Whether the mean squared error is used rather than mean absolute error when doing cross-validation for regression problems.";
  }
  
  public boolean getMeanSquared() {
    return this.m_MeanSquared;
  }
  
  public void setMeanSquared(boolean paramBoolean) {
    this.m_MeanSquared = paramBoolean;
  }
  
  public String crossValidateTipText() {
    return "Whether hold-one-out cross-validation will be used to select the best k value.";
  }
  
  public boolean getCrossValidate() {
    return this.m_CrossValidate;
  }
  
  public void setCrossValidate(boolean paramBoolean) {
    this.m_CrossValidate = paramBoolean;
  }
  
  public int getNumTraining() {
    return this.m_Train.numInstances();
  }
  
  public double getAttributeMin(int paramInt) throws Exception {
    if (this.m_Min == null)
      throw new Exception("Minimum value for attribute not available!"); 
    return this.m_Min[paramInt];
  }
  
  public double getAttributeMax(int paramInt) throws Exception {
    if (this.m_Max == null)
      throw new Exception("Maximum value for attribute not available!"); 
    return this.m_Max[paramInt];
  }
  
  public String noNormalizationTipText() {
    return "Whether attribute normalization is turned off.";
  }
  
  public boolean getNoNormalization() {
    return this.m_DontNormalize;
  }
  
  public void setNoNormalization(boolean paramBoolean) {
    this.m_DontNormalize = paramBoolean;
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.classIndex() < 0)
      throw new Exception("No class attribute assigned to instances"); 
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    try {
      this.m_NumClasses = paramInstances.numClasses();
      this.m_ClassType = paramInstances.classAttribute().type();
    } catch (Exception exception) {
      throw new Error("This should never be reached");
    } 
    this.m_Train = new Instances(paramInstances, 0, paramInstances.numInstances());
    this.m_Train.deleteWithMissingClass();
    if (this.m_WindowSize > 0 && paramInstances.numInstances() > this.m_WindowSize)
      this.m_Train = new Instances(this.m_Train, this.m_Train.numInstances() - this.m_WindowSize, this.m_WindowSize); 
    if (this.m_DontNormalize) {
      this.m_Min = null;
      this.m_Max = null;
    } else {
      this.m_Min = new double[this.m_Train.numAttributes()];
      this.m_Max = new double[this.m_Train.numAttributes()];
      byte b = 0;
      if (b < this.m_Train.numAttributes()) {
        this.m_Max[b] = Double.NaN;
        this.m_Min[b] = Double.NaN;
        b++;
      } else {
        Enumeration enumeration = this.m_Train.enumerateInstances();
        while (enumeration.hasMoreElements())
          updateMinMax(enumeration.nextElement()); 
        this.m_NumAttributesUsed = 0.0D;
        boolean bool1 = false;
      } 
    } 
    this.m_NumAttributesUsed = 0.0D;
    boolean bool = false;
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    if (!this.m_Train.equalHeaders(paramInstance.dataset()))
      throw new Exception("Incompatible instance types"); 
    if (paramInstance.classIsMissing())
      return; 
    if (!this.m_DontNormalize)
      updateMinMax(paramInstance); 
    this.m_Train.add(paramInstance);
    this.m_kNNValid = false;
    if (this.m_WindowSize > 0 && this.m_Train.numInstances() > this.m_WindowSize)
      while (this.m_Train.numInstances() > this.m_WindowSize)
        this.m_Train.delete(0);  
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_Train.numInstances() == 0)
      throw new Exception("No training instances!"); 
    if (this.m_WindowSize > 0 && this.m_Train.numInstances() > this.m_WindowSize) {
      this.m_kNNValid = false;
      while (this.m_Train.numInstances() > this.m_WindowSize)
        this.m_Train.delete(0); 
    } 
    if (!this.m_kNNValid && this.m_CrossValidate && this.m_kNNUpper >= 1)
      crossValidate(); 
    if (!this.m_DontNormalize)
      updateMinMax(paramInstance); 
    NeighborList neighborList = findNeighbors(paramInstance);
    return makeDistribution(neighborList);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(8);
    vector.addElement(new Option("\tWeight neighbours by the inverse of their distance\n\t(use when k > 1)", "I", 0, "-I"));
    vector.addElement(new Option("\tWeight neighbours by 1 - their distance\n\t(use when k > 1)", "F", 0, "-F"));
    vector.addElement(new Option("\tNumber of nearest neighbours (k) used in classification.\n\t(Default = 1)", "K", 1, "-K <number of neighbors>"));
    vector.addElement(new Option("\tMinimise mean squared error rather than mean absolute\n\terror when using -X option with numeric prediction.", "E", 0, "-E"));
    vector.addElement(new Option("\tMaximum number of training instances maintained.\n\tTraining instances are dropped FIFO. (Default = no window)", "W", 1, "-W <window size>"));
    vector.addElement(new Option("\tSelect the number of nearest neighbours between 1\n\tand the k value specified using hold-one-out evaluation\n\ton the training data (use when k > 1)", "X", 0, "-X"));
    vector.addElement(new Option("\tDon't normalize the data.\n", "N", 0, "-N"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('K', paramArrayOfString);
    if (str1.length() != 0) {
      setKNN(Integer.parseInt(str1));
    } else {
      setKNN(1);
    } 
    String str2 = Utils.getOption('W', paramArrayOfString);
    if (str2.length() != 0) {
      setWindowSize(Integer.parseInt(str2));
    } else {
      setWindowSize(0);
    } 
    if (Utils.getFlag('I', paramArrayOfString)) {
      setDistanceWeighting(new SelectedTag(2, TAGS_WEIGHTING));
    } else if (Utils.getFlag('F', paramArrayOfString)) {
      setDistanceWeighting(new SelectedTag(4, TAGS_WEIGHTING));
    } else {
      setDistanceWeighting(new SelectedTag(1, TAGS_WEIGHTING));
    } 
    setCrossValidate(Utils.getFlag('X', paramArrayOfString));
    setMeanSquared(Utils.getFlag('E', paramArrayOfString));
    setNoNormalization(Utils.getFlag('N', paramArrayOfString));
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[11];
    byte b = 0;
    arrayOfString[b++] = "-K";
    arrayOfString[b++] = "" + getKNN();
    arrayOfString[b++] = "-W";
    arrayOfString[b++] = "" + this.m_WindowSize;
    if (getCrossValidate())
      arrayOfString[b++] = "-X"; 
    if (getMeanSquared())
      arrayOfString[b++] = "-E"; 
    if (this.m_DistanceWeighting == 2) {
      arrayOfString[b++] = "-I";
    } else if (this.m_DistanceWeighting == 4) {
      arrayOfString[b++] = "-F";
    } 
    if (this.m_DontNormalize)
      arrayOfString[b++] = "-N"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    if (this.m_Train == null)
      return "IBk: No model built yet."; 
    if (!this.m_kNNValid && this.m_CrossValidate)
      crossValidate(); 
    String str = "IB1 instance-based classifier\nusing " + this.m_kNN;
    switch (this.m_DistanceWeighting) {
      case 2:
        str = str + " inverse-distance-weighted";
        break;
      case 4:
        str = str + " similarity-weighted";
        break;
    } 
    str = str + " nearest neighbour(s) for classification\n";
    if (this.m_WindowSize != 0)
      str = str + "using a maximum of " + this.m_WindowSize + " (windowed) training instances\n"; 
    return str;
  }
  
  protected void init() {
    setKNN(1);
    this.m_WindowSize = 0;
    this.m_DistanceWeighting = 1;
    this.m_CrossValidate = false;
    this.m_MeanSquared = false;
    this.m_DontNormalize = false;
  }
  
  protected double distance(Instance paramInstance1, Instance paramInstance2) {
    double d = 0.0D;
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
        continue;
      } 
      return Math.sqrt(d / this.m_NumAttributesUsed);
    } 
  }
  
  protected double difference(int paramInt, double paramDouble1, double paramDouble2) {
    switch (this.m_Train.attribute(paramInt).type()) {
      case 1:
        return (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2) || (int)paramDouble1 != (int)paramDouble2) ? 1.0D : 0.0D;
      case 0:
        if (Instance.isMissingValue(paramDouble1) || Instance.isMissingValue(paramDouble2)) {
          double d;
          if (Instance.isMissingValue(paramDouble1) && Instance.isMissingValue(paramDouble2))
            return 1.0D; 
          if (Instance.isMissingValue(paramDouble2)) {
            d = norm(paramDouble1, paramInt);
          } else {
            d = norm(paramDouble2, paramInt);
          } 
          if (d < 0.5D)
            d = 1.0D - d; 
          return d;
        } 
        return norm(paramDouble1, paramInt) - norm(paramDouble2, paramInt);
    } 
    return 0.0D;
  }
  
  protected double norm(double paramDouble, int paramInt) {
    return this.m_DontNormalize ? paramDouble : ((Double.isNaN(this.m_Min[paramInt]) || Utils.eq(this.m_Max[paramInt], this.m_Min[paramInt])) ? 0.0D : ((paramDouble - this.m_Min[paramInt]) / (this.m_Max[paramInt] - this.m_Min[paramInt])));
  }
  
  protected void updateMinMax(Instance paramInstance) {
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
  
  protected NeighborList findNeighbors(Instance paramInstance) {
    NeighborList neighborList = new NeighborList(this, this.m_kNN);
    Enumeration enumeration = this.m_Train.enumerateInstances();
    byte b = 0;
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      if (paramInstance != instance) {
        double d = distance(paramInstance, instance);
        if (neighborList.isEmpty() || b < this.m_kNN || d <= neighborList.m_Last.m_Distance)
          neighborList.insertSorted(d, instance); 
        b++;
      } 
    } 
    return neighborList;
  }
  
  protected double[] makeDistribution(NeighborList paramNeighborList) throws Exception {
    double d = 0.0D;
    double[] arrayOfDouble = new double[this.m_NumClasses];
    if (this.m_ClassType == 1) {
      for (byte b = 0; b < this.m_NumClasses; b++)
        arrayOfDouble[b] = 1.0D / Math.max(1, this.m_Train.numInstances()); 
      d = this.m_NumClasses / Math.max(1, this.m_Train.numInstances());
    } 
    if (!paramNeighborList.isEmpty())
      for (NeighborNode neighborNode = paramNeighborList.m_First; neighborNode != null; neighborNode = neighborNode.m_Next) {
        double d1;
        switch (this.m_DistanceWeighting) {
          case 2:
            d1 = 1.0D / (neighborNode.m_Distance + 0.001D);
            break;
          case 4:
            d1 = 1.0D - neighborNode.m_Distance;
            break;
          default:
            d1 = 1.0D;
            break;
        } 
        d1 *= neighborNode.m_Instance.weight();
        try {
          switch (this.m_ClassType) {
            case 1:
              arrayOfDouble[(int)neighborNode.m_Instance.classValue()] = arrayOfDouble[(int)neighborNode.m_Instance.classValue()] + d1;
              break;
            case 0:
              arrayOfDouble[0] = arrayOfDouble[0] + neighborNode.m_Instance.classValue() * d1;
              break;
          } 
        } catch (Exception exception) {
          throw new Error("Data has no class attribute!");
        } 
        d += d1;
      }  
    if (d > 0.0D)
      Utils.normalize(arrayOfDouble, d); 
    return arrayOfDouble;
  }
  
  protected void crossValidate() {
    try {
      double[] arrayOfDouble1 = new double[this.m_kNNUpper];
      double[] arrayOfDouble2 = new double[this.m_kNNUpper];
      for (byte b1 = 0; b1 < this.m_kNNUpper; b1++) {
        arrayOfDouble1[b1] = 0.0D;
        arrayOfDouble2[b1] = 0.0D;
      } 
      this.m_kNN = this.m_kNNUpper;
      byte b2;
      for (b2 = 0; b2 < this.m_Train.numInstances(); b2++) {
        if (this.m_Debug && b2 % 50 == 0)
          System.err.print("Cross validating " + b2 + "/" + this.m_Train.numInstances() + "\r"); 
        Instance instance = this.m_Train.instance(b2);
        NeighborList neighborList = findNeighbors(instance);
        for (int j = this.m_kNNUpper - 1; j >= 0; j--) {
          double[] arrayOfDouble = makeDistribution(neighborList);
          double d1 = Utils.maxIndex(arrayOfDouble);
          if (this.m_Train.classAttribute().isNumeric()) {
            d1 = arrayOfDouble[0];
            double d2 = d1 - instance.classValue();
            arrayOfDouble2[j] = arrayOfDouble2[j] + d2 * d2;
            arrayOfDouble1[j] = arrayOfDouble1[j] + Math.abs(d2);
          } else if (d1 != instance.classValue()) {
            arrayOfDouble1[j] = arrayOfDouble1[j] + 1.0D;
          } 
          if (j >= 1)
            neighborList.pruneToK(j); 
        } 
      } 
      for (b2 = 0; b2 < this.m_kNNUpper; b2++) {
        if (this.m_Debug)
          System.err.print("Hold-one-out performance of " + (b2 + 1) + " neighbors "); 
        if (this.m_Train.classAttribute().isNumeric()) {
          if (this.m_Debug)
            if (this.m_MeanSquared) {
              System.err.println("(RMSE) = " + Math.sqrt(arrayOfDouble2[b2] / this.m_Train.numInstances()));
            } else {
              System.err.println("(MAE) = " + (arrayOfDouble1[b2] / this.m_Train.numInstances()));
            }  
        } else if (this.m_Debug) {
          System.err.println("(%ERR) = " + (100.0D * arrayOfDouble1[b2] / this.m_Train.numInstances()));
        } 
      } 
      double[] arrayOfDouble3 = arrayOfDouble1;
      if (this.m_Train.classAttribute().isNumeric() && this.m_MeanSquared)
        arrayOfDouble3 = arrayOfDouble2; 
      double d = Double.NaN;
      int i = 1;
      for (byte b3 = 0; b3 < this.m_kNNUpper; b3++) {
        if (Double.isNaN(d) || d > arrayOfDouble3[b3]) {
          d = arrayOfDouble3[b3];
          i = b3 + 1;
        } 
      } 
      this.m_kNN = i;
      if (this.m_Debug)
        System.err.println("Selected k = " + i); 
      this.m_kNNValid = true;
    } catch (Exception exception) {
      throw new Error("Couldn't optimize by cross-validation: " + exception.getMessage());
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new IBk(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class NeighborList {
    protected IBk.NeighborNode m_First;
    
    protected IBk.NeighborNode m_Last;
    
    protected int m_Length;
    
    private final IBk this$0;
    
    public NeighborList(IBk this$0, int param1Int) {
      this.this$0 = this$0;
      this.m_Length = 1;
      this.m_Length = param1Int;
    }
    
    public boolean isEmpty() {
      return (this.m_First == null);
    }
    
    public int currentLength() {
      byte b = 0;
      for (IBk.NeighborNode neighborNode = this.m_First; neighborNode != null; neighborNode = neighborNode.m_Next)
        b++; 
      return b;
    }
    
    public void insertSorted(double param1Double, Instance param1Instance) {
      if (isEmpty()) {
        this.m_First = this.m_Last = new IBk.NeighborNode(param1Double, param1Instance);
      } else {
        IBk.NeighborNode neighborNode = this.m_First;
        if (param1Double < this.m_First.m_Distance) {
          this.m_First = new IBk.NeighborNode(param1Double, param1Instance, this.m_First);
        } else {
          while (neighborNode.m_Next != null && neighborNode.m_Next.m_Distance < param1Double)
            neighborNode = neighborNode.m_Next; 
          neighborNode.m_Next = new IBk.NeighborNode(param1Double, param1Instance, neighborNode.m_Next);
          if (neighborNode.equals(this.m_Last))
            this.m_Last = neighborNode.m_Next; 
        } 
        byte b = 0;
        for (neighborNode = this.m_First; neighborNode.m_Next != null; neighborNode = neighborNode.m_Next) {
          if (++b >= this.m_Length && neighborNode.m_Distance != neighborNode.m_Next.m_Distance) {
            this.m_Last = neighborNode;
            neighborNode.m_Next = null;
            break;
          } 
        } 
      } 
    }
    
    public void pruneToK(int param1Int) {
      if (isEmpty())
        return; 
      if (param1Int < 1)
        param1Int = 1; 
      byte b = 0;
      double d = this.m_First.m_Distance;
      for (IBk.NeighborNode neighborNode = this.m_First; neighborNode.m_Next != null; neighborNode = neighborNode.m_Next) {
        b++;
        d = neighborNode.m_Distance;
        if (b >= param1Int && d != neighborNode.m_Next.m_Distance) {
          this.m_Last = neighborNode;
          neighborNode.m_Next = null;
          break;
        } 
      } 
    }
    
    public void printList() {
      if (isEmpty()) {
        System.out.println("Empty list");
      } else {
        for (IBk.NeighborNode neighborNode = this.m_First; neighborNode != null; neighborNode = neighborNode.m_Next)
          System.out.println("Node: instance " + neighborNode.m_Instance + ", distance " + neighborNode.m_Distance); 
        System.out.println();
      } 
    }
  }
  
  protected class NeighborNode {
    protected Instance m_Instance;
    
    protected double m_Distance;
    
    protected NeighborNode m_Next;
    
    private final IBk this$0;
    
    public NeighborNode(IBk this$0, double param1Double, Instance param1Instance, NeighborNode param1NeighborNode) {
      IBk.this = IBk.this;
      this.m_Distance = param1Double;
      this.m_Instance = param1Instance;
      this.m_Next = param1NeighborNode;
    }
    
    public NeighborNode(double param1Double, Instance param1Instance) {
      this(param1Double, param1Instance, null);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\IBk.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */