package weka.classifiers.lazy;

import java.io.Serializable;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Statistics;
import weka.core.Utils;

public class LBR extends Classifier {
  protected int[][][] m_Counts;
  
  protected int[][][] m_tCounts;
  
  protected int[] m_Priors;
  
  protected int[] m_tPriors;
  
  protected int m_numAtts;
  
  protected int m_numClasses;
  
  protected int m_numInsts;
  
  protected Instances m_Instances = null;
  
  protected int m_Errors;
  
  protected boolean[] m_ErrorFlags;
  
  protected ArrayList leftHand = new ArrayList();
  
  protected static final double SIGNLOWER = 0.05D;
  
  protected boolean[] m_subOldErrorFlags;
  
  protected int m_RemainderErrors = 0;
  
  protected int m_Number = 0;
  
  protected int m_NumberOfInstances = 0;
  
  protected boolean m_NCV = false;
  
  protected Indexes m_subInstances;
  
  protected Indexes tempSubInstances;
  
  protected double[] posteriorsArray;
  
  protected int bestCnt;
  
  protected int tempCnt;
  
  protected int forCnt;
  
  protected int whileCnt;
  
  public String globalInfo() {
    return "Lazy Bayesian Rules Classifier. The naive Bayesian classifier provides a simple and effective approach to classifier learning, but its attribute independence assumption is often violated in the real world. Lazy Bayesian Rules selectively relaxes the independence assumption, achieving lower error rates over a range of learning tasks.  LBR defers processing to classification time, making it a highly efficient and accurate classification algorithm when small numbers of objects are to be classified.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.bestCnt = 0;
    this.tempCnt = 0;
    this.forCnt = 0;
    this.whileCnt = 0;
    this.m_numAtts = paramInstances.numAttributes();
    byte b1;
    for (b1 = 0; b1 < this.m_numAtts; b1++) {
      Attribute attribute = paramInstances.attribute(b1);
      if (attribute.isNumeric())
        throw new Exception("Can't handle numeric attributes!  Descritize the dataset prior to using Lazy Bayesian Rules or use the Filtered Classifier"); 
    } 
    if (paramInstances.classAttribute().isNumeric())
      throw new Exception("LBR: Class is numeric!"); 
    this.m_numClasses = paramInstances.numClasses();
    if (this.m_numClasses < 0)
      throw new Exception("Dataset has no class attribute"); 
    this.m_numInsts = paramInstances.numInstances();
    this.m_Counts = new int[this.m_numClasses][this.m_numAtts][0];
    this.m_Priors = new int[this.m_numClasses];
    this.m_tCounts = new int[this.m_numClasses][this.m_numAtts][0];
    this.m_tPriors = new int[this.m_numClasses];
    this.m_subOldErrorFlags = new boolean[this.m_numInsts + 1];
    this.m_Instances = paramInstances;
    this.m_subInstances = new Indexes(this, this.m_numInsts, this.m_numAtts, true, this.m_Instances.classIndex());
    this.tempSubInstances = new Indexes(this, this.m_numInsts, this.m_numAtts, true, this.m_Instances.classIndex());
    this.posteriorsArray = new double[this.m_numClasses];
    for (b1 = 0; b1 < this.m_numAtts; b1++) {
      Attribute attribute = paramInstances.attribute(b1);
      for (byte b = 0; b < this.m_numClasses; b++) {
        this.m_Counts[b][b1] = new int[attribute.numValues()];
        this.m_tCounts[b][b1] = new int[attribute.numValues()];
      } 
    } 
    for (byte b2 = 0; b2 < this.m_numInsts; b2++) {
      Instance instance = paramInstances.instance(b2);
      int i = (int)instance.classValue();
      int[][] arrayOfInt = this.m_tCounts[i];
      for (b1 = 0; b1 < this.m_numAtts; b1++)
        arrayOfInt[b1][(int)instance.value(b1)] = arrayOfInt[b1][(int)instance.value(b1)] + 1; 
      this.m_tPriors[i] = this.m_tPriors[i] + 1;
    } 
    this.m_ErrorFlags = new boolean[this.m_numInsts];
    this.m_Errors = leaveOneOut(this.m_subInstances, this.m_tCounts, this.m_tPriors, this.m_ErrorFlags);
    if (this.m_Number == 0) {
      this.m_NumberOfInstances = this.m_Instances.numInstances();
    } else {
      System.out.println(" ");
      System.out.println("N-Fold Cross Validation: ");
      this.m_NCV = true;
    } 
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    int i = 0;
    int j = 0;
    int k = 0;
    boolean bool = false;
    int m = 0;
    int n = 0;
    boolean[] arrayOfBoolean1 = null;
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    Object object = null;
    Indexes indexes = new Indexes(this, this.m_numInsts, this.m_numAtts, true, this.m_Instances.classIndex());
    boolean[] arrayOfBoolean2 = new boolean[indexes.getNumInstances() + 1];
    int i1 = this.m_Errors;
    boolean[] arrayOfBoolean3 = (boolean[])this.m_ErrorFlags.clone();
    byte b1 = 0;
    byte b2 = 0;
    this.leftHand.clear();
    while (i1 >= 5) {
      int i2 = -1;
      this.whileCnt++;
      n = indexes.getNumInstancesSet() + 1;
      indexes.setSequentialDataset(true);
      for (byte b = 0; b < indexes.m_NumSeqAttsSet; b++) {
        this.forCnt++;
        i = indexes.m_SequentialAttIndexes[b];
        this.m_RemainderErrors = 0;
        int i3;
        for (i3 = 0; i3 < this.m_numInsts; i3++)
          this.m_subOldErrorFlags[i3] = true; 
        this.tempSubInstances.resetDatasetBasedOn(indexes);
        for (byte b3 = 0; b3 < indexes.m_NumSeqInstsSet; b3++) {
          j = indexes.m_SequentialInstIndexes[b3];
          if (this.m_Instances.instance(j).value(i) == paramInstance.value(i)) {
            this.tempSubInstances.setInstanceIndex(j, true);
            if (!arrayOfBoolean3[j])
              this.m_subOldErrorFlags[j] = false; 
          } else if (!arrayOfBoolean3[j]) {
            this.m_RemainderErrors++;
          } 
        } 
        if (this.tempSubInstances.m_NumInstsSet < indexes.m_NumInstsSet) {
          this.tempSubInstances.setAttIndex(i, false);
          localNaiveBayes(this.tempSubInstances);
          m = leaveOneOut(this.tempSubInstances, this.m_Counts, this.m_Priors, arrayOfBoolean2);
          b1 = 0;
          b2 = 0;
          this.tempSubInstances.setSequentialDataset(true);
          for (i3 = 0; i3 < this.tempSubInstances.m_NumSeqInstsSet; i3++) {
            k = this.tempSubInstances.m_SequentialInstIndexes[i3];
            if (!arrayOfBoolean2[k]) {
              if (this.m_subOldErrorFlags[k] == true)
                b1++; 
            } else if (!this.m_subOldErrorFlags[k]) {
              b2++;
            } 
          } 
          i3 = m + this.m_RemainderErrors;
          if (i3 < n && binomP(b1, (b1 + b2), 0.5D) < 0.05D) {
            this.tempCnt++;
            this.tempSubInstances.setSequentialDataset(true);
            arrayOfInt1 = (int[])this.tempSubInstances.m_SequentialInstIndexes.clone();
            arrayOfInt2 = (int[])this.tempSubInstances.m_SequentialAttIndexes.clone();
            n = i3;
            arrayOfBoolean1 = (boolean[])arrayOfBoolean2.clone();
            i2 = i;
          } 
        } 
      } 
      if (i2 != -1) {
        this.bestCnt++;
        this.leftHand.add(paramInstance.attribute(i2));
        indexes.setInsts(arrayOfInt1, true);
        indexes.setAtts(arrayOfInt2, true);
        indexes.setAttIndex(i2, false);
        i1 = n;
        arrayOfBoolean3 = arrayOfBoolean1;
      } 
    } 
    localNaiveBayes(indexes);
    return localDistributionForInstance(paramInstance, indexes);
  }
  
  public String toString() {
    if (this.m_Instances == null)
      return "Lazy Bayesian Rule: No model built yet."; 
    try {
      StringBuffer stringBuffer = new StringBuffer("=== LBR Run information ===\n\n");
      stringBuffer.append("Scheme:       weka.classifiers.LBR\n");
      stringBuffer.append("Relation:     " + this.m_Instances.attribute(this.m_Instances.classIndex()).name() + "\n");
      stringBuffer.append("Instances:    " + this.m_Instances.numInstances() + "\n");
      stringBuffer.append("Attributes:   " + this.m_Instances.numAttributes() + "\n");
      return stringBuffer.toString();
    } catch (Exception exception) {
      exception.printStackTrace();
      return "Can't Print Lazy Bayes Rule Classifier!";
    } 
  }
  
  public int leaveOneOut(Indexes paramIndexes, int[][][] paramArrayOfint, int[] paramArrayOfint1, boolean[] paramArrayOfboolean) throws Exception {
    double d = 0.0D;
    byte b1 = 0;
    byte b3 = 0;
    paramIndexes.setSequentialDataset(true);
    int[] arrayOfInt = new int[paramIndexes.m_NumSeqAttsSet + 1];
    for (byte b2 = 0; b2 < paramIndexes.m_NumSeqInstsSet; b2++) {
      int i = paramIndexes.m_SequentialInstIndexes[b2];
      Instance instance = this.m_Instances.instance(i);
      if (!instance.classIsMissing()) {
        int j;
        int k = (int)instance.classValue();
        int[][] arrayOfInt1 = paramArrayOfint[k];
        byte b4;
        for (b4 = 0; b4 < paramIndexes.m_NumSeqAttsSet; b4++) {
          int m = paramIndexes.m_SequentialAttIndexes[b4];
          arrayOfInt[b4] = (int)instance.value(m);
          arrayOfInt1[m][arrayOfInt[b4]] = arrayOfInt1[m][arrayOfInt[b4]] - 1;
        } 
        paramArrayOfint1[k] = paramArrayOfint1[k] - 1;
        d = 0.0D;
        b1 = 0;
        double d1 = Utils.sum(paramArrayOfint1);
        for (byte b5 = 0; b5 < this.m_numClasses; b5++) {
          double d2 = 0.0D;
          d2 = (paramArrayOfint1[b5] + 1) / (d1 + this.m_numClasses);
          arrayOfInt1 = paramArrayOfint[b5];
          for (b4 = 0; b4 < paramIndexes.m_NumSeqAttsSet; b4++) {
            int m = paramIndexes.m_SequentialAttIndexes[b4];
            if (!instance.isMissing(m)) {
              double d3 = Utils.sum(arrayOfInt1[m]);
              d2 *= (arrayOfInt1[m][arrayOfInt[b4]] + 1) / (d3 + instance.attribute(m).numValues());
            } 
          } 
          if (d2 > d) {
            b1 = b5;
            d = d2;
          } 
        } 
        if (d > 0.0D) {
          j = b1;
        } else {
          j = (int)Instance.missingValue();
        } 
        if (j == k) {
          paramArrayOfboolean[i] = true;
        } else {
          paramArrayOfboolean[i] = false;
          b3++;
        } 
        arrayOfInt1 = paramArrayOfint[k];
        for (b4 = 0; b4 < paramIndexes.m_NumSeqAttsSet; b4++) {
          int m = paramIndexes.m_SequentialAttIndexes[b4];
          paramArrayOfint[k][m][arrayOfInt[b4]] = paramArrayOfint[k][m][arrayOfInt[b4]] + 1;
        } 
        paramArrayOfint1[k] = paramArrayOfint1[k] + 1;
      } 
    } 
    return b3;
  }
  
  public void localNaiveBayes(Indexes paramIndexes) throws Exception {
    byte b1 = 0;
    byte b3 = 0;
    byte b4 = 0;
    paramIndexes.setSequentialDataset(true);
    for (b4 = 0; b4 < this.m_numClasses; b4++) {
      int[][] arrayOfInt = this.m_Counts[b4];
      for (b1 = 0; b1 < this.m_numAtts; b1++) {
        Attribute attribute = this.m_Instances.attribute(b1);
        int[] arrayOfInt1 = arrayOfInt[b1];
        for (b3 = 0; b3 < attribute.numValues(); b3++)
          arrayOfInt1[b3] = 0; 
      } 
      this.m_Priors[b4] = 0;
    } 
    for (byte b2 = 0; b2 < paramIndexes.m_NumSeqInstsSet; b2++) {
      Instance instance = this.m_Instances.instance(paramIndexes.m_SequentialInstIndexes[b2]);
      for (b1 = 0; b1 < paramIndexes.m_NumSeqAttsSet; b1++) {
        int i = paramIndexes.m_SequentialAttIndexes[b1];
        this.m_Counts[(int)instance.classValue()][i][(int)instance.value(i)] = this.m_Counts[(int)instance.classValue()][i][(int)instance.value(i)] + 1;
      } 
      this.m_Priors[(int)instance.classValue()] = this.m_Priors[(int)instance.classValue()] + 1;
    } 
  }
  
  public double[] localDistributionForInstance(Instance paramInstance, Indexes paramIndexes) throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    int i = paramInstance.numClasses();
    d1 = 0.0D;
    d2 = 0.0D;
    paramIndexes.setSequentialDataset(true);
    d1 = (Utils.sum(this.m_Priors) + i);
    for (byte b = 0; b < i; b++) {
      int[][] arrayOfInt = this.m_Counts[b];
      this.posteriorsArray[b] = (this.m_Priors[b] + 1) / d1;
      for (byte b1 = 0; b1 < paramIndexes.m_NumSeqAttsSet; b1++) {
        int j = paramIndexes.m_SequentialAttIndexes[b1];
        d2 = Utils.sum(arrayOfInt[j]);
        if (!paramInstance.isMissing(j))
          this.posteriorsArray[b] = this.posteriorsArray[b] * (arrayOfInt[j][(int)paramInstance.value(j)] + 1) / (d2 + paramInstance.attribute(j).numValues()); 
      } 
    } 
    Utils.normalize(this.posteriorsArray);
    return this.posteriorsArray;
  }
  
  public double binomP(double paramDouble1, double paramDouble2, double paramDouble3) throws Exception {
    return (paramDouble2 == paramDouble1) ? 1.0D : Statistics.incompleteBeta(paramDouble2 - paramDouble1, paramDouble1 + 1.0D, 1.0D - paramDouble3);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      LBR lBR = new LBR();
      System.out.println(Evaluation.evaluateModel(lBR, paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  public class Indexes implements Serializable {
    public boolean[] m_InstIndexes;
    
    public boolean[] m_AttIndexes;
    
    private int m_NumInstances;
    
    private int m_NumAtts;
    
    public int[] m_SequentialInstIndexes;
    
    public int[] m_SequentialAttIndexes;
    
    private boolean m_SequentialInstanceIndex_valid;
    
    private boolean m_SequentialAttIndex_valid;
    
    public int m_NumInstsSet;
    
    public int m_NumAttsSet;
    
    public int m_NumSeqInstsSet;
    
    public int m_NumSeqAttsSet;
    
    public int m_ClassIndex;
    
    private final LBR this$0;
    
    public Indexes(LBR this$0, int param1Int1, int param1Int2, boolean param1Boolean, int param1Int3) {
      this.this$0 = this$0;
      this.m_SequentialInstanceIndex_valid = false;
      this.m_SequentialAttIndex_valid = false;
      this.m_NumInstsSet = this.m_NumInstances = param1Int1;
      this.m_NumAttsSet = this.m_NumAtts = param1Int2;
      this.m_InstIndexes = new boolean[param1Int1];
      byte b;
      for (b = 0; b < param1Int1; b++)
        this.m_InstIndexes[b] = param1Boolean; 
      this.m_AttIndexes = new boolean[param1Int2];
      for (b = 0; b < param1Int2; b++)
        this.m_AttIndexes[b] = true; 
      if (!param1Boolean)
        this.m_NumInstsSet = 0; 
      this.m_SequentialInstanceIndex_valid = false;
      this.m_SequentialAttIndex_valid = false;
      if (param1Int3 != -1)
        setAttIndex(param1Int3, false); 
      this.m_ClassIndex = param1Int3;
    }
    
    public Indexes(LBR this$0, Indexes param1Indexes) {
      this.this$0 = this$0;
      this.m_SequentialInstanceIndex_valid = false;
      this.m_SequentialAttIndex_valid = false;
      this.m_NumInstances = param1Indexes.getNumInstances();
      this.m_NumInstsSet = param1Indexes.m_NumInstsSet;
      this.m_NumAtts = param1Indexes.m_NumAtts;
      this.m_NumAttsSet = param1Indexes.m_NumAttsSet;
      this.m_InstIndexes = new boolean[this.m_NumInstances];
      System.arraycopy(param1Indexes.m_InstIndexes, 0, this.m_InstIndexes, 0, this.m_NumInstances);
      this.m_AttIndexes = new boolean[this.m_NumAtts];
      System.arraycopy(param1Indexes.m_AttIndexes, 0, this.m_AttIndexes, 0, this.m_NumAtts);
      this.m_ClassIndex = param1Indexes.m_ClassIndex;
      this.m_SequentialInstanceIndex_valid = false;
      this.m_SequentialAttIndex_valid = false;
    }
    
    public void setInstanceIndex(int param1Int, boolean param1Boolean) {
      if (param1Int < 0 || param1Int >= this.m_NumInstances)
        throw new IllegalArgumentException("Invalid Instance Index value"); 
      if (this.m_InstIndexes[param1Int] != param1Boolean) {
        this.m_InstIndexes[param1Int] = param1Boolean;
        this.m_SequentialInstanceIndex_valid = false;
        if (!param1Boolean) {
          this.m_NumInstsSet--;
        } else {
          this.m_NumInstsSet++;
        } 
      } 
    }
    
    public void setAtts(int[] param1ArrayOfint, boolean param1Boolean) {
      byte b;
      for (b = 0; b < this.m_NumAtts; b++)
        this.m_AttIndexes[b] = !param1Boolean; 
      for (b = 0; b < param1ArrayOfint.length; b++)
        this.m_AttIndexes[param1ArrayOfint[b]] = param1Boolean; 
      this.m_NumAttsSet = param1ArrayOfint.length;
      this.m_SequentialAttIndex_valid = false;
    }
    
    public void setInsts(int[] param1ArrayOfint, boolean param1Boolean) {
      resetInstanceIndex(!param1Boolean);
      for (byte b = 0; b < param1ArrayOfint.length; b++)
        this.m_InstIndexes[param1ArrayOfint[b]] = param1Boolean; 
      this.m_NumInstsSet = param1ArrayOfint.length;
      this.m_SequentialInstanceIndex_valid = false;
    }
    
    public void setAttIndex(int param1Int, boolean param1Boolean) {
      if (param1Int < 0 || param1Int >= this.m_NumAtts)
        throw new IllegalArgumentException("Invalid Attribute Index value"); 
      if (this.m_AttIndexes[param1Int] != param1Boolean) {
        this.m_AttIndexes[param1Int] = param1Boolean;
        this.m_SequentialAttIndex_valid = false;
        if (!param1Boolean) {
          this.m_NumAttsSet--;
        } else {
          this.m_NumAttsSet++;
        } 
      } 
    }
    
    public boolean getInstanceIndex(int param1Int) {
      if (param1Int < 0 || param1Int >= this.m_NumInstances)
        throw new IllegalArgumentException("Invalid index value"); 
      return this.m_InstIndexes[param1Int];
    }
    
    public int getSequentialInstanceIndex(int param1Int) {
      if (param1Int < 0 || param1Int >= this.m_NumInstances)
        throw new IllegalArgumentException("Invalid index value"); 
      return this.m_SequentialInstIndexes[param1Int];
    }
    
    public void resetInstanceIndex(boolean param1Boolean) {
      this.m_NumInstsSet = this.m_NumInstances;
      for (byte b = 0; b < this.m_NumInstances; b++)
        this.m_InstIndexes[b] = param1Boolean; 
      if (!param1Boolean)
        this.m_NumInstsSet = 0; 
      this.m_SequentialInstanceIndex_valid = false;
    }
    
    public void resetDatasetBasedOn(Indexes param1Indexes) {
      resetInstanceIndex(false);
      resetAttIndexTo(param1Indexes);
    }
    
    public void resetAttIndex(boolean param1Boolean) {
      this.m_NumAttsSet = this.m_NumAtts;
      for (byte b = 0; b < this.m_NumAtts; b++)
        this.m_AttIndexes[b] = param1Boolean; 
      if (this.m_ClassIndex != -1)
        setAttIndex(this.m_ClassIndex, false); 
      if (!param1Boolean)
        this.m_NumAttsSet = 0; 
      this.m_SequentialAttIndex_valid = false;
    }
    
    public void resetAttIndexTo(Indexes param1Indexes) {
      System.arraycopy(param1Indexes.m_AttIndexes, 0, this.m_AttIndexes, 0, this.m_NumAtts);
      this.m_NumAttsSet = param1Indexes.getNumAttributesSet();
      this.m_ClassIndex = param1Indexes.m_ClassIndex;
      this.m_SequentialAttIndex_valid = false;
    }
    
    public boolean getAttIndex(int param1Int) {
      if (param1Int < 0 || param1Int >= this.m_NumAtts)
        throw new IllegalArgumentException("Invalid index value"); 
      return this.m_AttIndexes[param1Int];
    }
    
    public int getSequentialAttIndex(int param1Int) {
      if (param1Int < 0 || param1Int >= this.m_NumAtts)
        throw new IllegalArgumentException("Invalid index value"); 
      return this.m_SequentialAttIndexes[param1Int];
    }
    
    public int getNumInstancesSet() {
      return this.m_NumInstsSet;
    }
    
    public int getNumInstances() {
      return this.m_NumInstances;
    }
    
    public int getSequentialNumInstances() {
      return this.m_NumSeqInstsSet;
    }
    
    public int getNumAttributes() {
      return this.m_NumAtts;
    }
    
    public int getNumAttributesSet() {
      return this.m_NumAttsSet;
    }
    
    public int getSequentialNumAttributes() {
      return this.m_NumSeqAttsSet;
    }
    
    public boolean isSequentialInstanceIndexValid() {
      return this.m_SequentialInstanceIndex_valid;
    }
    
    public boolean isSequentialAttIndexValid() {
      return this.m_SequentialAttIndex_valid;
    }
    
    public void setSequentialDataset(boolean param1Boolean) {
      setSequentialInstanceIndex(param1Boolean);
      setSequentialAttIndex(param1Boolean);
    }
    
    public void setSequentialInstanceIndex(boolean param1Boolean) {
      if (this.m_SequentialInstanceIndex_valid == true)
        return; 
      int i = this.m_NumInstsSet;
      this.m_SequentialInstIndexes = new int[i];
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.m_NumInstances; b2++) {
        if (this.m_InstIndexes[b2] == param1Boolean) {
          this.m_SequentialInstIndexes[b1] = b2;
          b1++;
        } 
      } 
      this.m_SequentialInstanceIndex_valid = true;
      this.m_NumSeqInstsSet = b1;
    }
    
    public void setSequentialAttIndex(boolean param1Boolean) {
      if (this.m_SequentialAttIndex_valid == true)
        return; 
      int i = this.m_NumAttsSet;
      this.m_SequentialAttIndexes = new int[i];
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.m_NumAtts; b2++) {
        if (this.m_AttIndexes[b2] == param1Boolean) {
          this.m_SequentialAttIndexes[b1] = b2;
          b1++;
        } 
      } 
      this.m_SequentialAttIndex_valid = true;
      this.m_NumSeqAttsSet = b1;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\lazy\LBR.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */