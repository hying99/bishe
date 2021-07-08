package weka.classifiers.meta;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.DecisionStump;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;

public class RacedIncrementalLogitBoost extends Classifier implements OptionHandler, UpdateableClassifier {
  public static final int PRUNETYPE_NONE = 0;
  
  public static final int PRUNETYPE_LOGLIKELIHOOD = 1;
  
  public static final Tag[] TAGS_PRUNETYPE = new Tag[] { new Tag(0, "No pruning"), new Tag(1, "Log likelihood pruning") };
  
  protected Classifier m_Classifier = (Classifier)new DecisionStump();
  
  protected FastVector m_committees;
  
  protected int m_PruningType = 1;
  
  protected boolean m_UseResampling = false;
  
  protected int m_Seed = 1;
  
  protected int m_NumClasses;
  
  protected static final double Z_MAX = 4.0D;
  
  protected Instances m_NumericClassData;
  
  protected Attribute m_ClassAttribute;
  
  protected int m_minChunkSize = 500;
  
  protected int m_maxChunkSize = 8000;
  
  protected int m_validationChunkSize = 5000;
  
  protected int m_numInstancesConsumed;
  
  protected Instances m_validationSet;
  
  protected Instances m_currentSet;
  
  protected Committee m_bestCommittee;
  
  protected ZeroR m_zeroR = null;
  
  protected boolean m_validationSetChanged;
  
  protected int m_maxBatchSizeRequired;
  
  protected boolean m_Debug = false;
  
  protected Random m_RandomInstance = null;
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_RandomInstance = new Random(this.m_Seed);
    int i = paramInstances.classIndex();
    if (paramInstances.classAttribute().isNumeric())
      throw new Exception("LogitBoost can't handle a numeric class!"); 
    if (this.m_Classifier == null)
      throw new Exception("A base classifier has not been specified!"); 
    if (!(this.m_Classifier instanceof weka.core.WeightedInstancesHandler) && !this.m_UseResampling)
      this.m_UseResampling = true; 
    if (paramInstances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    this.m_NumClasses = paramInstances.numClasses();
    this.m_ClassAttribute = paramInstances.classAttribute();
    Instances instances = new Instances(paramInstances);
    instances.deleteWithMissingClass();
    instances.setClassIndex(-1);
    instances.deleteAttributeAt(i);
    instances.insertAttributeAt(new Attribute("'pseudo class'"), i);
    instances.setClassIndex(i);
    this.m_NumericClassData = new Instances(instances, 0);
    paramInstances = new Instances(paramInstances);
    paramInstances.randomize(this.m_RandomInstance);
    int j = this.m_minChunkSize;
    this.m_committees = new FastVector();
    while (j <= this.m_maxChunkSize) {
      this.m_committees.addElement(new Committee(this, j));
      this.m_maxBatchSizeRequired = j;
      j *= 2;
    } 
    this.m_validationSet = new Instances(paramInstances, this.m_validationChunkSize);
    this.m_currentSet = new Instances(paramInstances, this.m_maxBatchSizeRequired);
    this.m_bestCommittee = null;
    this.m_numInstancesConsumed = 0;
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      updateClassifier(paramInstances.instance(b)); 
  }
  
  public void updateClassifier(Instance paramInstance) throws Exception {
    this.m_numInstancesConsumed++;
    if (this.m_validationSet.numInstances() < this.m_validationChunkSize) {
      this.m_validationSet.add(paramInstance);
      this.m_validationSetChanged = true;
    } else {
      this.m_currentSet.add(paramInstance);
      boolean bool = false;
      byte b;
      for (b = 0; b < this.m_committees.size(); b++) {
        Committee committee = (Committee)this.m_committees.elementAt(b);
        if (committee.update()) {
          bool = true;
          if (this.m_PruningType == 1) {
            double d1 = committee.logLikelihood();
            double d2 = committee.logLikelihoodAfter();
            if (d2 >= d1 && committee.committeeSize() > 1) {
              committee.pruneLastModel();
              if (this.m_Debug)
                System.out.println("Pruning " + committee.chunkSize() + " committee (" + d1 + " < " + d2 + ")"); 
            } else {
              committee.keepLastModel();
            } 
          } else {
            committee.keepLastModel();
          } 
        } 
      } 
      if (bool) {
        if (this.m_Debug)
          System.out.println("After consuming " + this.m_numInstancesConsumed + " instances... (" + this.m_validationSet.numInstances() + " + " + this.m_currentSet.numInstances() + " instances currently in memory)"); 
        double d = 1.0D;
        for (byte b1 = 0; b1 < this.m_committees.size(); b1++) {
          Committee committee = (Committee)this.m_committees.elementAt(b1);
          if (committee.committeeSize() > 0) {
            double d1 = committee.validationError();
            double d2 = committee.logLikelihood();
            if (this.m_Debug)
              System.out.println("Chunk size " + committee.chunkSize() + " with " + committee.committeeSize() + " models, has validation error of " + d1 + ", log likelihood of " + d2); 
            if (d1 < d) {
              d = d1;
              this.m_bestCommittee = committee;
            } 
          } 
        } 
      } 
      if (this.m_currentSet.numInstances() >= this.m_maxBatchSizeRequired) {
        this.m_currentSet = new Instances(this.m_currentSet, this.m_maxBatchSizeRequired);
        for (b = 0; b < this.m_committees.size(); b++) {
          Committee committee = (Committee)this.m_committees.elementAt(b);
          committee.resetConsumed();
        } 
      } 
    } 
  }
  
  protected static double RtoP(double[] paramArrayOfdouble, int paramInt) throws Exception {
    double d1 = -1.7976931348623157E308D;
    for (byte b1 = 0; b1 < paramArrayOfdouble.length; b1++) {
      if (paramArrayOfdouble[b1] > d1)
        d1 = paramArrayOfdouble[b1]; 
    } 
    double d2 = 0.0D;
    double[] arrayOfDouble = new double[paramArrayOfdouble.length];
    for (byte b2 = 0; b2 < paramArrayOfdouble.length; b2++) {
      arrayOfDouble[b2] = Math.exp(paramArrayOfdouble[b2] - d1);
      d2 += arrayOfDouble[b2];
    } 
    if (d2 == 0.0D)
      throw new Exception("Can't normalize"); 
    return arrayOfDouble[paramInt] / d2;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_bestCommittee != null)
      return this.m_bestCommittee.distributionForInstance(paramInstance); 
    if (this.m_validationSetChanged || this.m_zeroR == null) {
      this.m_zeroR = new ZeroR();
      this.m_zeroR.buildClassifier(this.m_validationSet);
      this.m_validationSetChanged = false;
    } 
    return this.m_zeroR.distributionForInstance(paramInstance);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(9);
    vector.addElement(new Option("\tTurn on debugging output.", "D", 0, "-D"));
    vector.addElement(new Option("\tMinimum size of chunks.\n\t(default 500)", "C", 1, "-C <num>"));
    vector.addElement(new Option("\tMaximum size of chunks.\n\t(default 20000)", "M", 1, "-M <num>"));
    vector.addElement(new Option("\tSize of validation set.\n\t(default 5000)", "V", 1, "-V <num>"));
    vector.addElement(new Option("\tFull name of 'weak' learner to boost.\n\teg: weka.classifiers.DecisionStump", "W", 1, "-W <learner class name>"));
    vector.addElement(new Option("\tCommittee pruning to perform.\n\t0=none, 1=log likelihood (default)", "P", 1, "-P <pruning type>"));
    vector.addElement(new Option("\tUse resampling for boosting.", "Q", 0, "-Q"));
    vector.addElement(new Option("\tSeed for resampling. (Default 1)", "S", 1, "-S <num>"));
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler) {
      vector.addElement(new Option("", "", 0, "\nOptions specific to weak learner " + this.m_Classifier.getClass().getName() + ":"));
      Enumeration enumeration = this.m_Classifier.listOptions();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('C', paramArrayOfString);
    if (str1.length() != 0) {
      setMinChunkSize(Integer.parseInt(str1));
    } else {
      setMinChunkSize(500);
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      setMaxChunkSize(Integer.parseInt(str2));
    } else {
      setMaxChunkSize(20000);
    } 
    String str3 = Utils.getOption('V', paramArrayOfString);
    if (str3.length() != 0) {
      setValidationChunkSize(Integer.parseInt(str3));
    } else {
      setValidationChunkSize(5000);
    } 
    String str4 = Utils.getOption('P', paramArrayOfString);
    if (str4.length() != 0) {
      setPruningType(new SelectedTag(Integer.parseInt(str4), TAGS_PRUNETYPE));
    } else {
      setPruningType(new SelectedTag(1, TAGS_PRUNETYPE));
    } 
    setUseResampling(Utils.getFlag('Q', paramArrayOfString));
    String str5 = Utils.getOption('S', paramArrayOfString);
    if (str5.length() != 0) {
      setSeed(Integer.parseInt(str5));
    } else {
      setSeed(1);
    } 
    setDebug(Utils.getFlag('D', paramArrayOfString));
    String str6 = Utils.getOption('W', paramArrayOfString);
    if (str6.length() == 0)
      throw new Exception("A classifier must be specified with the -W option."); 
    setClassifier(Classifier.forName(str6, Utils.partitionOptions(paramArrayOfString)));
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = new String[0];
    if (this.m_Classifier != null && this.m_Classifier instanceof OptionHandler)
      arrayOfString1 = this.m_Classifier.getOptions(); 
    String[] arrayOfString2 = new String[arrayOfString1.length + 15];
    int i = 0;
    if (getDebug())
      arrayOfString2[i++] = "-D"; 
    if (getUseResampling())
      arrayOfString2[i++] = "-Q"; 
    if (getSeed() != 1) {
      arrayOfString2[i++] = "-S";
      arrayOfString2[i++] = "" + getSeed();
    } 
    arrayOfString2[i++] = "-C";
    arrayOfString2[i++] = "" + getMinChunkSize();
    arrayOfString2[i++] = "-M";
    arrayOfString2[i++] = "" + getMaxChunkSize();
    arrayOfString2[i++] = "-V";
    arrayOfString2[i++] = "" + getValidationChunkSize();
    arrayOfString2[i++] = "-P";
    arrayOfString2[i++] = "" + this.m_PruningType;
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
    return "Classifier for incremental learning of large datasets by way of racing logit-boosted committees.";
  }
  
  public String classifierTipText() {
    return "Sets the base classifier to boost. If not capable of handling weighted instances then resampling will be used.";
  }
  
  public void setClassifier(Classifier paramClassifier) {
    this.m_Classifier = paramClassifier;
  }
  
  public Classifier getClassifier() {
    return this.m_Classifier;
  }
  
  public String minChunkSizeTipText() {
    return "The minimum number of instances to train the base learner with.";
  }
  
  public void setMinChunkSize(int paramInt) {
    this.m_minChunkSize = paramInt;
  }
  
  public int getMinChunkSize() {
    return this.m_minChunkSize;
  }
  
  public String maxChunkSizeTipText() {
    return "The maximum number of instances to train the base learner with. The chunk sizes used will start at minChunkSize and grow twice as large for as many times as they are less than or equal to the maximum size.";
  }
  
  public void setMaxChunkSize(int paramInt) {
    this.m_maxChunkSize = paramInt;
  }
  
  public int getMaxChunkSize() {
    return this.m_maxChunkSize;
  }
  
  public String validationChunkSizeTipText() {
    return "The number of instances to hold out for validation. These instances will be taken from the beginning of the stream, so learning will not start until these instances have been consumed first.";
  }
  
  public void setValidationChunkSize(int paramInt) {
    this.m_validationChunkSize = paramInt;
  }
  
  public int getValidationChunkSize() {
    return this.m_validationChunkSize;
  }
  
  public String pruningTypeTipText() {
    return "The pruning method to use within each committee. Log likelihood pruning will discard new models if they have a negative effect on the log likelihood of the validation data.";
  }
  
  public void setPruningType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_PRUNETYPE)
      this.m_PruningType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getPruningType() {
    return new SelectedTag(this.m_PruningType, TAGS_PRUNETYPE);
  }
  
  public String debugTipText() {
    return "Whether debugging output should be sent to the terminal.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public String useResamplingTipText() {
    return "Force the use of resampling data rather than using the weight-handling capabilities of the base classifier. Resampling is always used if the base classifier cannot handle weighted instances.";
  }
  
  public void setUseResampling(boolean paramBoolean) {
    this.m_UseResampling = paramBoolean;
  }
  
  public boolean getUseResampling() {
    return this.m_UseResampling;
  }
  
  public String seedTipText() {
    return "Random seed used for resampling the data.";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
  
  public int getBestCommitteeChunkSize() {
    return (this.m_bestCommittee != null) ? this.m_bestCommittee.chunkSize() : 0;
  }
  
  public int getBestCommitteeSize() {
    return (this.m_bestCommittee != null) ? this.m_bestCommittee.committeeSize() : 0;
  }
  
  public double getBestCommitteeErrorEstimate() {
    if (this.m_bestCommittee != null)
      try {
        return this.m_bestCommittee.validationError() * 100.0D;
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
        return 100.0D;
      }  
    return 100.0D;
  }
  
  public double getBestCommitteeLLEstimate() {
    if (this.m_bestCommittee != null)
      try {
        return this.m_bestCommittee.logLikelihood();
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
        return Double.MAX_VALUE;
      }  
    return Double.MAX_VALUE;
  }
  
  public String toString() {
    if (this.m_bestCommittee != null)
      return this.m_bestCommittee.toString(); 
    if ((this.m_validationSetChanged || this.m_zeroR == null) && this.m_validationSet != null && this.m_validationSet.numInstances() > 0) {
      this.m_zeroR = new ZeroR();
      try {
        this.m_zeroR.buildClassifier(this.m_validationSet);
      } catch (Exception exception) {}
      this.m_validationSetChanged = false;
    } 
    return (this.m_zeroR != null) ? ("RacedIncrementalLogitBoost: insufficient data to build model, resorting to ZeroR:\n\n" + this.m_zeroR.toString()) : "RacedIncrementalLogitBoost: no model built yet.";
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new RacedIncrementalLogitBoost(), paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class Committee implements Serializable {
    protected int m_chunkSize;
    
    protected int m_instancesConsumed;
    
    protected FastVector m_models;
    
    protected double m_lastValidationError;
    
    protected double m_lastLogLikelihood;
    
    protected boolean m_modelHasChanged;
    
    protected boolean m_modelHasChangedLL;
    
    protected double[][] m_validationFs;
    
    protected double[][] m_newValidationFs;
    
    private final RacedIncrementalLogitBoost this$0;
    
    public Committee(RacedIncrementalLogitBoost this$0, int param1Int) {
      this.this$0 = this$0;
      this.m_chunkSize = param1Int;
      this.m_instancesConsumed = 0;
      this.m_models = new FastVector();
      this.m_lastValidationError = 1.0D;
      this.m_lastLogLikelihood = Double.MAX_VALUE;
      this.m_modelHasChanged = true;
      this.m_modelHasChangedLL = true;
      this.m_validationFs = new double[this$0.m_validationChunkSize][this$0.m_NumClasses];
      this.m_newValidationFs = new double[this$0.m_validationChunkSize][this$0.m_NumClasses];
    }
    
    public boolean update() throws Exception {
      boolean bool;
      for (bool = false; this.this$0.m_currentSet.numInstances() - this.m_instancesConsumed >= this.m_chunkSize; bool = true) {
        Classifier[] arrayOfClassifier = boost(new Instances(this.this$0.m_currentSet, this.m_instancesConsumed, this.m_chunkSize));
        for (byte b = 0; b < this.this$0.m_validationSet.numInstances(); b++)
          this.m_newValidationFs[b] = updateFS(this.this$0.m_validationSet.instance(b), arrayOfClassifier, this.m_validationFs[b]); 
        this.m_models.addElement(arrayOfClassifier);
        this.m_instancesConsumed += this.m_chunkSize;
      } 
      if (bool) {
        this.m_modelHasChanged = true;
        this.m_modelHasChangedLL = true;
      } 
      return bool;
    }
    
    public void resetConsumed() {
      this.m_instancesConsumed = 0;
    }
    
    public void pruneLastModel() {
      if (this.m_models.size() > 0) {
        this.m_models.removeElementAt(this.m_models.size() - 1);
        this.m_modelHasChanged = true;
        this.m_modelHasChangedLL = true;
      } 
    }
    
    public void keepLastModel() throws Exception {
      this.m_validationFs = this.m_newValidationFs;
      this.m_newValidationFs = new double[this.this$0.m_validationChunkSize][this.this$0.m_NumClasses];
      this.m_modelHasChanged = true;
      this.m_modelHasChangedLL = true;
    }
    
    public double logLikelihood() throws Exception {
      if (this.m_modelHasChangedLL) {
        double d = 0.0D;
        for (byte b = 0; b < this.this$0.m_validationSet.numInstances(); b++) {
          Instance instance = this.this$0.m_validationSet.instance(b);
          d += logLikelihood(this.m_validationFs[b], (int)instance.classValue());
        } 
        this.m_lastLogLikelihood = d / this.this$0.m_validationSet.numInstances();
        this.m_modelHasChangedLL = false;
      } 
      return this.m_lastLogLikelihood;
    }
    
    public double logLikelihoodAfter() throws Exception {
      double d = 0.0D;
      for (byte b = 0; b < this.this$0.m_validationSet.numInstances(); b++) {
        Instance instance = this.this$0.m_validationSet.instance(b);
        d += logLikelihood(this.m_newValidationFs[b], (int)instance.classValue());
      } 
      return d / this.this$0.m_validationSet.numInstances();
    }
    
    private double logLikelihood(double[] param1ArrayOfdouble, int param1Int) throws Exception {
      return -Math.log(distributionForInstance(param1ArrayOfdouble)[param1Int]);
    }
    
    public double validationError() throws Exception {
      if (this.m_modelHasChanged) {
        byte b1 = 0;
        for (byte b2 = 0; b2 < this.this$0.m_validationSet.numInstances(); b2++) {
          Instance instance = this.this$0.m_validationSet.instance(b2);
          if (classifyInstance(this.m_validationFs[b2]) != instance.classValue())
            b1++; 
        } 
        this.m_lastValidationError = b1 / this.this$0.m_validationSet.numInstances();
        this.m_modelHasChanged = false;
      } 
      return this.m_lastValidationError;
    }
    
    public int chunkSize() {
      return this.m_chunkSize;
    }
    
    public int committeeSize() {
      return this.m_models.size();
    }
    
    public double classifyInstance(double[] param1ArrayOfdouble) throws Exception {
      double[] arrayOfDouble = distributionForInstance(param1ArrayOfdouble);
      double d = 0.0D;
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfDouble.length; b2++) {
        if (arrayOfDouble[b2] > d) {
          b1 = b2;
          d = arrayOfDouble[b2];
        } 
      } 
      return (d > 0.0D) ? b1 : Instance.missingValue();
    }
    
    public double classifyInstance(Instance param1Instance) throws Exception {
      double d;
      byte b1;
      byte b2;
      double[] arrayOfDouble = distributionForInstance(param1Instance);
      switch (param1Instance.classAttribute().type()) {
        case 1:
          d = 0.0D;
          b1 = 0;
          for (b2 = 0; b2 < arrayOfDouble.length; b2++) {
            if (arrayOfDouble[b2] > d) {
              b1 = b2;
              d = arrayOfDouble[b2];
            } 
          } 
          return (d > 0.0D) ? b1 : Instance.missingValue();
        case 0:
          return arrayOfDouble[0];
      } 
      return Instance.missingValue();
    }
    
    public double[] distributionForInstance(double[] param1ArrayOfdouble) throws Exception {
      double[] arrayOfDouble = new double[this.this$0.m_NumClasses];
      for (byte b = 0; b < this.this$0.m_NumClasses; b++)
        arrayOfDouble[b] = RacedIncrementalLogitBoost.RtoP(param1ArrayOfdouble, b); 
      return arrayOfDouble;
    }
    
    public double[] updateFS(Instance param1Instance, Classifier[] param1ArrayOfClassifier, double[] param1ArrayOfdouble) throws Exception {
      param1Instance = (Instance)param1Instance.copy();
      param1Instance.setDataset(this.this$0.m_NumericClassData);
      double[] arrayOfDouble1 = new double[this.this$0.m_NumClasses];
      double d = 0.0D;
      for (byte b1 = 0; b1 < this.this$0.m_NumClasses; b1++) {
        arrayOfDouble1[b1] = param1ArrayOfClassifier[b1].classifyInstance(param1Instance);
        d += arrayOfDouble1[b1];
      } 
      d /= this.this$0.m_NumClasses;
      double[] arrayOfDouble2 = new double[param1ArrayOfdouble.length];
      for (byte b2 = 0; b2 < this.this$0.m_NumClasses; b2++)
        arrayOfDouble2[b2] = param1ArrayOfdouble[b2] + (arrayOfDouble1[b2] - d) * (this.this$0.m_NumClasses - 1) / this.this$0.m_NumClasses; 
      return arrayOfDouble2;
    }
    
    public double[] distributionForInstance(Instance param1Instance) throws Exception {
      param1Instance = (Instance)param1Instance.copy();
      param1Instance.setDataset(this.this$0.m_NumericClassData);
      double[] arrayOfDouble1 = new double[this.this$0.m_NumClasses];
      for (byte b1 = 0; b1 < this.m_models.size(); b1++) {
        double[] arrayOfDouble = new double[this.this$0.m_NumClasses];
        double d = 0.0D;
        Classifier[] arrayOfClassifier = (Classifier[])this.m_models.elementAt(b1);
        byte b;
        for (b = 0; b < this.this$0.m_NumClasses; b++) {
          arrayOfDouble[b] = arrayOfClassifier[b].classifyInstance(param1Instance);
          d += arrayOfDouble[b];
        } 
        d /= this.this$0.m_NumClasses;
        for (b = 0; b < this.this$0.m_NumClasses; b++)
          arrayOfDouble1[b] = arrayOfDouble1[b] + (arrayOfDouble[b] - d) * (this.this$0.m_NumClasses - 1) / this.this$0.m_NumClasses; 
      } 
      double[] arrayOfDouble2 = new double[this.this$0.m_NumClasses];
      for (byte b2 = 0; b2 < this.this$0.m_NumClasses; b2++)
        arrayOfDouble2[b2] = RacedIncrementalLogitBoost.RtoP(arrayOfDouble1, b2); 
      return arrayOfDouble2;
    }
    
    protected Classifier[] boost(Instances param1Instances) throws Exception {
      Classifier[] arrayOfClassifier = Classifier.makeCopies(this.this$0.m_Classifier, this.this$0.m_NumClasses);
      Instances instances = new Instances(param1Instances);
      instances.deleteWithMissingClass();
      int i = instances.numInstances();
      int j = param1Instances.classIndex();
      instances.setClassIndex(-1);
      instances.deleteAttributeAt(j);
      instances.insertAttributeAt(new Attribute("'pseudo class'"), j);
      instances.setClassIndex(j);
      double[][] arrayOfDouble1 = new double[i][this.this$0.m_NumClasses];
      double[][] arrayOfDouble2 = new double[i][this.this$0.m_NumClasses];
      byte b;
      for (b = 0; b < this.this$0.m_NumClasses; b++) {
        byte b1 = 0;
        for (byte b2 = 0; b1 < i; b2++) {
          while (param1Instances.instance(b2).classIsMissing())
            b2++; 
          arrayOfDouble2[b1][b] = (param1Instances.instance(b2).classValue() == b) ? 1.0D : 0.0D;
          b1++;
        } 
      } 
      for (b = 0; b < this.m_models.size(); b++) {
        for (byte b1 = 0; b1 < i; b1++) {
          double[] arrayOfDouble = new double[this.this$0.m_NumClasses];
          double d = 0.0D;
          Classifier[] arrayOfClassifier1 = (Classifier[])this.m_models.elementAt(b);
          byte b2;
          for (b2 = 0; b2 < this.this$0.m_NumClasses; b2++) {
            arrayOfDouble[b2] = arrayOfClassifier1[b2].classifyInstance(instances.instance(b1));
            d += arrayOfDouble[b2];
          } 
          d /= this.this$0.m_NumClasses;
          for (b2 = 0; b2 < this.this$0.m_NumClasses; b2++)
            arrayOfDouble1[b1][b2] = arrayOfDouble1[b1][b2] + (arrayOfDouble[b2] - d) * (this.this$0.m_NumClasses - 1) / this.this$0.m_NumClasses; 
        } 
      } 
      for (b = 0; b < this.this$0.m_NumClasses; b++) {
        for (byte b1 = 0; b1 < i; b1++) {
          double d2;
          double d1 = RacedIncrementalLogitBoost.RtoP(arrayOfDouble1[b1], b);
          Instance instance = instances.instance(b1);
          double d3 = arrayOfDouble2[b1][b];
          if (d3 == 1.0D) {
            d2 = 1.0D / d1;
            if (d2 > 4.0D)
              d2 = 4.0D; 
          } else if (d3 == 0.0D) {
            d2 = -1.0D / (1.0D - d1);
            if (d2 < -4.0D)
              d2 = -4.0D; 
          } else {
            d2 = (d3 - d1) / d1 * (1.0D - d1);
          } 
          double d4 = (d3 - d1) / d2;
          instance.setValue(j, d2);
          instance.setWeight(i * d4);
        } 
        Instances instances1 = instances;
        if (this.this$0.m_UseResampling) {
          double[] arrayOfDouble = new double[instances.numInstances()];
          for (byte b2 = 0; b2 < arrayOfDouble.length; b2++)
            arrayOfDouble[b2] = instances.instance(b2).weight(); 
          instances1 = instances.resampleWithWeights(this.this$0.m_RandomInstance, arrayOfDouble);
        } 
        arrayOfClassifier[b].buildClassifier(instances1);
      } 
      return arrayOfClassifier;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("RacedIncrementalLogitBoost: Best committee on validation data\n");
      stringBuffer.append("Base classifiers: \n");
      for (byte b = 0; b < this.m_models.size(); b++) {
        stringBuffer.append("\nModel " + (b + 1));
        Classifier[] arrayOfClassifier = (Classifier[])this.m_models.elementAt(b);
        for (byte b1 = 0; b1 < this.this$0.m_NumClasses; b1++)
          stringBuffer.append("\n\tClass " + (b1 + 1) + " (" + this.this$0.m_ClassAttribute.name() + "=" + this.this$0.m_ClassAttribute.value(b1) + ")\n\n" + arrayOfClassifier[b1].toString() + "\n"); 
      } 
      stringBuffer.append("Number of models: " + this.m_models.size() + "\n");
      stringBuffer.append("Chunk size per model: " + this.m_chunkSize + "\n");
      return stringBuffer.toString();
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\meta\RacedIncrementalLogitBoost.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */