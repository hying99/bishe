package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.SMOset;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.UnsupportedClassTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;

public class SMOreg extends Classifier implements OptionHandler, WeightedInstancesHandler {
  protected boolean m_featureSpaceNormalization = false;
  
  protected boolean m_useRBF = false;
  
  protected int m_cacheSize = 250007;
  
  protected Kernel m_kernel;
  
  protected boolean m_lowerOrder = false;
  
  protected double m_exponent = 1.0D;
  
  protected double m_gamma = 0.01D;
  
  protected int m_classIndex = -1;
  
  protected boolean m_onlyNumeric;
  
  protected NominalToBinary m_NominalToBinary;
  
  public static final int FILTER_NORMALIZE = 0;
  
  public static final int FILTER_STANDARDIZE = 1;
  
  public static final int FILTER_NONE = 2;
  
  public static final Tag[] TAGS_FILTER = new Tag[] { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
  
  protected Filter m_Filter = null;
  
  protected int m_filterType = 0;
  
  protected ReplaceMissingValues m_Missing;
  
  protected boolean m_checksTurnedOff = false;
  
  protected Instances m_data;
  
  protected double m_C = 1.0D;
  
  protected double[] m_alpha;
  
  protected double[] m_alpha_;
  
  protected double m_b;
  
  protected double m_bLow;
  
  protected double m_bUp;
  
  protected int m_iLow;
  
  protected int m_iUp;
  
  protected double[] m_weights;
  
  protected double[] m_fcache;
  
  protected SMOset m_I0;
  
  protected SMOset m_I1;
  
  protected SMOset m_I2;
  
  protected SMOset m_I3;
  
  protected double m_epsilon = 0.001D;
  
  protected double m_tol = 0.001D;
  
  protected double m_eps = 1.0E-12D;
  
  protected static double m_Del = 1.0E-10D;
  
  protected double m_Alin;
  
  protected double m_Blin;
  
  protected double[] m_sparseWeights;
  
  protected int[] m_sparseIndices;
  
  public String globalInfo() {
    return "Implements Alex Smola and Bernhard Scholkopf's sequential minimal optimization algorithm for training a support vector regression model. This implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes by default. (Note that the coefficients in the output are based on the normalized/standardized data, not the original data.) For more information on the SMO algorithm, see\n\nAlex J. Smola, Bernhard Scholkopf (1998). \"A Tutorial on Support Vector Regression\".  NeuroCOLT2 Technical Report Series - NC2-TR-1998-030.\n\nS.K. Shevade, S.S. Keerthi, C. Bhattacharyya, K.R.K. Murthy,  \"Improvements to SMO Algorithm for SVM Regression\".  Technical Report CD-99-16, Control Division Dept of Mechanical and Production Engineering, National University of Singapore. ";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (!this.m_checksTurnedOff) {
      if (paramInstances.checkForStringAttributes())
        throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
      if (paramInstances.classAttribute().isNominal())
        throw new UnsupportedClassTypeException("SMOreg can't handle a nominal class! Use SMO for performing classification."); 
      paramInstances = new Instances(paramInstances);
      paramInstances.deleteWithMissingClass();
      if (paramInstances.numInstances() == 0)
        throw new Exception("No training instances without a missing class!"); 
      Instances instances = new Instances(paramInstances, paramInstances.numInstances());
      for (byte b = 0; b < paramInstances.numInstances(); b++) {
        if (paramInstances.instance(b).weight() > 0.0D)
          instances.add(paramInstances.instance(b)); 
      } 
      if (instances.numInstances() == 0)
        throw new Exception("No training instances left after removing instance with either a weight null or a missing class!"); 
      paramInstances = instances;
    } 
    this.m_onlyNumeric = true;
    if (!this.m_checksTurnedOff)
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        if (b != paramInstances.classIndex() && !paramInstances.attribute(b).isNumeric()) {
          this.m_onlyNumeric = false;
          break;
        } 
      }  
    if (!this.m_checksTurnedOff) {
      this.m_Missing = new ReplaceMissingValues();
      this.m_Missing.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_Missing);
    } else {
      this.m_Missing = null;
    } 
    if (!this.m_onlyNumeric) {
      this.m_NominalToBinary = new NominalToBinary();
      this.m_NominalToBinary.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, (Filter)this.m_NominalToBinary);
    } else {
      this.m_NominalToBinary = null;
    } 
    this.m_classIndex = paramInstances.classIndex();
    if (this.m_filterType == 1) {
      this.m_Filter = (Filter)new Standardize();
      ((Standardize)this.m_Filter).setIgnoreClass(true);
      this.m_Filter.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, this.m_Filter);
    } else if (this.m_filterType == 0) {
      this.m_Filter = (Filter)new Normalize();
      ((Normalize)this.m_Filter).setIgnoreClass(true);
      this.m_Filter.setInputFormat(paramInstances);
      paramInstances = Filter.useFilter(paramInstances, this.m_Filter);
    } else {
      this.m_Filter = null;
    } 
    this.m_data = paramInstances;
    if (this.m_Filter != null) {
      Instance instance1 = (Instance)paramInstances.instance(0).copy();
      instance1.setValue(this.m_classIndex, 0.0D);
      this.m_Filter.input(instance1);
      this.m_Filter.batchFinished();
      Instance instance2 = this.m_Filter.output();
      this.m_Blin = instance2.value(this.m_classIndex);
      instance1.setValue(this.m_classIndex, 1.0D);
      this.m_Filter.input(instance1);
      this.m_Filter.batchFinished();
      instance2 = this.m_Filter.output();
      this.m_Alin = instance2.value(this.m_classIndex) - this.m_Blin;
    } else {
      this.m_Alin = 1.0D;
      this.m_Blin = 0.0D;
    } 
    if (this.m_useRBF) {
      this.m_kernel = (Kernel)new RBFKernel(this.m_data, this.m_cacheSize, this.m_gamma);
    } else if (this.m_featureSpaceNormalization) {
      this.m_kernel = (Kernel)new NormalizedPolyKernel(this.m_data, this.m_cacheSize, this.m_exponent, this.m_lowerOrder);
    } else {
      this.m_kernel = (Kernel)new PolyKernel(this.m_data, this.m_cacheSize, this.m_exponent, this.m_lowerOrder);
    } 
    if (!this.m_useRBF && this.m_exponent == 1.0D) {
      this.m_weights = new double[this.m_data.numAttributes()];
    } else {
      this.m_weights = null;
    } 
    this.m_fcache = new double[this.m_data.numInstances()];
    this.m_I0 = new SMOset(this.m_data.numInstances());
    this.m_I1 = new SMOset(this.m_data.numInstances());
    this.m_I2 = new SMOset(this.m_data.numInstances());
    this.m_I3 = new SMOset(this.m_data.numInstances());
    this.m_alpha = new double[this.m_data.numInstances()];
    this.m_alpha_ = new double[this.m_data.numInstances()];
    int i;
    for (i = 0; i < this.m_data.numInstances(); i++)
      this.m_I1.insert(i); 
    this.m_bUp = this.m_data.instance(0).classValue() + this.m_epsilon;
    this.m_bLow = this.m_data.instance(0).classValue() - this.m_epsilon;
    this.m_iUp = this.m_iLow = 0;
    i = 0;
    boolean bool = true;
    while (true) {
      if (i > 0 || bool) {
        i = 0;
        if (bool) {
          for (byte b = 0; b < this.m_alpha.length; b++)
            i += examineExample(b); 
        } else {
          int j;
          for (j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j)) {
            i += examineExample(j);
            if (this.m_bUp > this.m_bLow - 2.0D * this.m_tol) {
              i = 0;
              break;
            } 
          } 
        } 
        if (bool) {
          bool = false;
          continue;
        } 
        if (i == 0)
          bool = true; 
        continue;
      } 
      this.m_b = (this.m_bLow + this.m_bUp) / 2.0D;
      this.m_kernel.clean();
      this.m_fcache = null;
      this.m_I0 = this.m_I1 = this.m_I2 = this.m_I3 = null;
      if (!this.m_useRBF && this.m_exponent == 1.0D) {
        byte b1;
        for (b1 = 0; b1 < this.m_weights.length; b1++)
          this.m_weights[b1] = 0.0D; 
        for (b1 = 0; b1 < this.m_alpha.length; b1++) {
          for (byte b = 0; b < this.m_weights.length; b++) {
            if (b != this.m_data.classIndex())
              this.m_weights[b] = this.m_weights[b] + (this.m_alpha[b1] - this.m_alpha_[b1]) * this.m_data.instance(b1).value(b); 
          } 
        } 
        double[] arrayOfDouble = new double[this.m_weights.length];
        int[] arrayOfInt = new int[this.m_weights.length];
        byte b2 = 0;
        for (byte b3 = 0; b3 < this.m_weights.length; b3++) {
          if (this.m_weights[b3] != 0.0D) {
            arrayOfDouble[b2] = this.m_weights[b3];
            arrayOfInt[b2] = b3;
            b2++;
          } 
        } 
        this.m_sparseWeights = new double[b2];
        this.m_sparseIndices = new int[b2];
        System.arraycopy(arrayOfDouble, 0, this.m_sparseWeights, 0, b2);
        System.arraycopy(arrayOfInt, 0, this.m_sparseIndices, 0, b2);
        if (!this.m_checksTurnedOff) {
          this.m_data = new Instances(this.m_data, 0);
        } else {
          this.m_data = null;
        } 
        this.m_weights = null;
        this.m_alpha = null;
        this.m_alpha_ = null;
      } 
      return;
    } 
  }
  
  protected int examineExample(int paramInt) throws Exception {
    double d1 = this.m_alpha[paramInt];
    double d2 = this.m_alpha_[paramInt];
    double d3 = 0.0D;
    if (this.m_I0.contains(paramInt)) {
      d3 = this.m_fcache[paramInt];
    } else {
      d3 = this.m_data.instance(paramInt).classValue();
      for (byte b = 0; b < this.m_alpha.length; b++)
        d3 -= (this.m_alpha[b] - this.m_alpha_[b]) * this.m_kernel.eval(paramInt, b, this.m_data.instance(paramInt)); 
      this.m_fcache[paramInt] = d3;
      if (this.m_I1.contains(paramInt)) {
        if (d3 + this.m_epsilon < this.m_bUp) {
          this.m_bUp = d3 + this.m_epsilon;
          this.m_iUp = paramInt;
        } else if (d3 - this.m_epsilon > this.m_bLow) {
          this.m_bLow = d3 - this.m_epsilon;
          this.m_iLow = paramInt;
        } 
      } else if (this.m_I2.contains(paramInt) && d3 + this.m_epsilon > this.m_bLow) {
        this.m_bLow = d3 + this.m_epsilon;
        this.m_iLow = paramInt;
      } else if (this.m_I3.contains(paramInt) && d3 - this.m_epsilon < this.m_bUp) {
        this.m_bUp = d3 - this.m_epsilon;
        this.m_iUp = paramInt;
      } 
    } 
    boolean bool = true;
    int i = -1;
    if (this.m_I0.contains(paramInt) && 0.0D < d1 && d1 < this.m_C * this.m_data.instance(paramInt).weight()) {
      if (this.m_bLow - d3 - this.m_epsilon > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iLow;
        if (d3 - this.m_epsilon - this.m_bUp > this.m_bLow - d3 - this.m_epsilon)
          i = this.m_iUp; 
      } else if (d3 - this.m_epsilon - this.m_bUp > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iUp;
        if (this.m_bLow - d3 - this.m_epsilon > d3 - this.m_epsilon - this.m_bUp)
          i = this.m_iLow; 
      } 
    } else if (this.m_I0.contains(paramInt) && 0.0D < d2 && d2 < this.m_C * this.m_data.instance(paramInt).weight()) {
      if (this.m_bLow - d3 + this.m_epsilon > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iLow;
        if (d3 + this.m_epsilon - this.m_bUp > this.m_bLow - d3 + this.m_epsilon)
          i = this.m_iUp; 
      } else if (d3 + this.m_epsilon - this.m_bUp > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iUp;
        if (this.m_bLow - d3 + this.m_epsilon > d3 + this.m_epsilon - this.m_bUp)
          i = this.m_iLow; 
      } 
    } else if (this.m_I1.contains(paramInt)) {
      if (this.m_bLow - d3 + this.m_epsilon > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iLow;
        if (d3 + this.m_epsilon - this.m_bUp > this.m_bLow - d3 + this.m_epsilon)
          i = this.m_iUp; 
      } else if (d3 - this.m_epsilon - this.m_bUp > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iUp;
        if (this.m_bLow - d3 - this.m_epsilon > d3 - this.m_epsilon - this.m_bUp)
          i = this.m_iLow; 
      } 
    } else if (this.m_I2.contains(paramInt)) {
      if (d3 + this.m_epsilon - this.m_bUp > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iUp;
      } 
    } else if (this.m_I3.contains(paramInt)) {
      if (this.m_bLow - d3 - this.m_epsilon > 2.0D * this.m_tol) {
        bool = false;
        i = this.m_iLow;
      } 
    } else {
      throw new RuntimeException("Inconsistent state ! I0, I1, I2 and I3 must cover the whole set of indices.");
    } 
    return bool ? 0 : (takeStep(i, paramInt) ? 1 : 0);
  }
  
  protected boolean takeStep(int paramInt1, int paramInt2) throws Exception {
    if (paramInt1 == paramInt2)
      return false; 
    double d1 = this.m_alpha[paramInt1];
    double d2 = this.m_alpha_[paramInt1];
    double d3 = this.m_alpha[paramInt2];
    double d4 = this.m_alpha_[paramInt2];
    double d5 = this.m_fcache[paramInt1];
    double d6 = this.m_fcache[paramInt2];
    double d7 = this.m_kernel.eval(paramInt1, paramInt1, this.m_data.instance(paramInt1));
    double d8 = this.m_kernel.eval(paramInt1, paramInt2, this.m_data.instance(paramInt1));
    double d9 = this.m_kernel.eval(paramInt2, paramInt2, this.m_data.instance(paramInt2));
    double d10 = -2.0D * d8 + d7 + d9;
    double d11 = d1 - d2 + d3 - d4;
    if (d10 < 0.0D)
      d10 = 0.0D; 
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    double d12 = d5 - d6;
    boolean bool6 = false;
    while (!bool5) {
      if (!bool1 && (d1 > 0.0D || (d2 == 0.0D && d12 > 0.0D)) && (d3 > 0.0D || (d4 == 0.0D && d12 < 0.0D))) {
        double d13 = Math.max(0.0D, d11 - this.m_C * this.m_data.instance(paramInt1).weight());
        double d14 = Math.min(this.m_C * this.m_data.instance(paramInt2).weight(), d11);
        if (d13 < d14) {
          double d16;
          if (d10 > 0.0D) {
            d16 = d3 - d12 / d10;
            if (d16 > d14) {
              d16 = d14;
            } else if (d16 < d13) {
              d16 = d13;
            } 
          } else {
            double d17 = -d13 * d12;
            double d18 = -d14 * d12;
            if (d17 > d18) {
              d16 = d13;
            } else {
              d16 = d14;
            } 
          } 
          double d15 = d1 - d16 - d3;
          if (Math.abs(d15 - d1) > this.m_eps || Math.abs(d16 - d3) > this.m_eps) {
            d1 = d15;
            d3 = d16;
            bool6 = true;
          } 
        } else {
          bool5 = true;
        } 
        bool1 = true;
      } else if (!bool2 && (d1 > 0.0D || (d2 == 0.0D && d12 > 2.0D * this.m_epsilon)) && (d4 > 0.0D || (d3 == 0.0D && d12 > 2.0D * this.m_epsilon))) {
        double d13 = Math.max(0.0D, -d11);
        double d14 = Math.min(this.m_C * this.m_data.instance(paramInt2).weight(), -d11 + this.m_C * this.m_data.instance(paramInt1).weight());
        if (d13 < d14) {
          double d16;
          if (d10 > 0.0D) {
            d16 = d4 + (d12 - 2.0D * this.m_epsilon) / d10;
            if (d16 > d14) {
              d16 = d14;
            } else if (d16 < d13) {
              d16 = d13;
            } 
          } else {
            double d17 = d13 * (-2.0D * this.m_epsilon + d12);
            double d18 = d14 * (-2.0D * this.m_epsilon + d12);
            if (d17 > d18) {
              d16 = d13;
            } else {
              d16 = d14;
            } 
          } 
          double d15 = d1 + d16 - d4;
          if (Math.abs(d15 - d1) > this.m_eps || Math.abs(d16 - d4) > this.m_eps) {
            d1 = d15;
            d4 = d16;
            bool6 = true;
          } 
        } else {
          bool5 = true;
        } 
        bool2 = true;
      } else if (!bool3 && (d2 > 0.0D || (d1 == 0.0D && d12 < -2.0D * this.m_epsilon)) && (d3 > 0.0D || (d4 == 0.0D && d12 < -2.0D * this.m_epsilon))) {
        double d13 = Math.max(0.0D, d11);
        double d14 = Math.min(this.m_C * this.m_data.instance(paramInt2).weight(), this.m_C * this.m_data.instance(paramInt1).weight() + d11);
        if (d13 < d14) {
          double d16;
          if (d10 > 0.0D) {
            d16 = d3 - (d12 + 2.0D * this.m_epsilon) / d10;
            if (d16 > d14) {
              d16 = d14;
            } else if (d16 < d13) {
              d16 = d13;
            } 
          } else {
            double d17 = -d13 * (2.0D * this.m_epsilon + d12);
            double d18 = -d14 * (2.0D * this.m_epsilon + d12);
            if (d17 > d18) {
              d16 = d13;
            } else {
              d16 = d14;
            } 
          } 
          double d15 = d2 + d16 - d3;
          if (Math.abs(d15 - d2) > this.m_eps || Math.abs(d16 - d3) > this.m_eps) {
            d2 = d15;
            d3 = d16;
            bool6 = true;
          } 
        } else {
          bool5 = true;
        } 
        bool3 = true;
      } else if (!bool4 && (d2 > 0.0D || (d1 == 0.0D && d12 < 0.0D)) && (d4 > 0.0D || (d3 == 0.0D && d12 > 0.0D))) {
        double d13 = Math.max(0.0D, -d11 - this.m_C * this.m_data.instance(paramInt1).weight());
        double d14 = Math.min(this.m_C * this.m_data.instance(paramInt2).weight(), -d11);
        if (d13 < d14) {
          double d16;
          if (d10 > 0.0D) {
            d16 = d4 + d12 / d10;
            if (d16 > d14) {
              d16 = d14;
            } else if (d16 < d13) {
              d16 = d13;
            } 
          } else {
            double d17 = d13 * d12;
            double d18 = d14 * d12;
            if (d17 > d18) {
              d16 = d13;
            } else {
              d16 = d14;
            } 
          } 
          double d15 = d2 - d16 - d4;
          if (Math.abs(d15 - d2) > this.m_eps || Math.abs(d16 - d4) > this.m_eps) {
            d2 = d15;
            d4 = d16;
            bool6 = true;
          } 
        } else {
          bool5 = true;
        } 
        bool4 = true;
      } else {
        bool5 = true;
      } 
      d12 += d10 * (d3 - d4 - this.m_alpha[paramInt2] - this.m_alpha_[paramInt2]);
    } 
    if (bool6) {
      int i;
      for (i = this.m_I0.getNext(-1); i != -1; i = this.m_I0.getNext(i)) {
        if (i != paramInt1 && i != paramInt2)
          this.m_fcache[i] = this.m_fcache[i] + (this.m_alpha[paramInt1] - this.m_alpha_[paramInt1] - d1 - d2) * this.m_kernel.eval(paramInt1, i, this.m_data.instance(paramInt1)) + (this.m_alpha[paramInt2] - this.m_alpha_[paramInt2] - d3 - d4) * this.m_kernel.eval(paramInt2, i, this.m_data.instance(paramInt2)); 
      } 
      this.m_fcache[paramInt1] = this.m_fcache[paramInt1] + (this.m_alpha[paramInt1] - this.m_alpha_[paramInt1] - d1 - d2) * d7 + (this.m_alpha[paramInt2] - this.m_alpha_[paramInt2] - d3 - d4) * d8;
      this.m_fcache[paramInt2] = this.m_fcache[paramInt2] + (this.m_alpha[paramInt1] - this.m_alpha_[paramInt1] - d1 - d2) * d8 + (this.m_alpha[paramInt2] - this.m_alpha_[paramInt2] - d3 - d4) * d9;
      if (d1 > this.m_C * this.m_data.instance(paramInt1).weight() - m_Del * this.m_C * this.m_data.instance(paramInt1).weight()) {
        d1 = this.m_C * this.m_data.instance(paramInt1).weight();
      } else if (d1 <= m_Del * this.m_C * this.m_data.instance(paramInt1).weight()) {
        d1 = 0.0D;
      } 
      if (d2 > this.m_C * this.m_data.instance(paramInt1).weight() - m_Del * this.m_C * this.m_data.instance(paramInt1).weight()) {
        d2 = this.m_C * this.m_data.instance(paramInt1).weight();
      } else if (d2 <= m_Del * this.m_C * this.m_data.instance(paramInt1).weight()) {
        d2 = 0.0D;
      } 
      if (d3 > this.m_C * this.m_data.instance(paramInt2).weight() - m_Del * this.m_C * this.m_data.instance(paramInt2).weight()) {
        d3 = this.m_C * this.m_data.instance(paramInt2).weight();
      } else if (d3 <= m_Del * this.m_C * this.m_data.instance(paramInt2).weight()) {
        d3 = 0.0D;
      } 
      if (d4 > this.m_C * this.m_data.instance(paramInt2).weight() - m_Del * this.m_C * this.m_data.instance(paramInt2).weight()) {
        d4 = this.m_C * this.m_data.instance(paramInt2).weight();
      } else if (d4 <= m_Del * this.m_C * this.m_data.instance(paramInt2).weight()) {
        d4 = 0.0D;
      } 
      this.m_alpha[paramInt1] = d1;
      this.m_alpha_[paramInt1] = d2;
      this.m_alpha[paramInt2] = d3;
      this.m_alpha_[paramInt2] = d4;
      if ((0.0D < d1 && d1 < this.m_C * this.m_data.instance(paramInt1).weight()) || (0.0D < d2 && d2 < this.m_C * this.m_data.instance(paramInt1).weight())) {
        this.m_I0.insert(paramInt1);
      } else {
        this.m_I0.delete(paramInt1);
      } 
      if (d1 == 0.0D && d2 == 0.0D) {
        this.m_I1.insert(paramInt1);
      } else {
        this.m_I1.delete(paramInt1);
      } 
      if (d1 == 0.0D && d2 == this.m_C * this.m_data.instance(paramInt1).weight()) {
        this.m_I2.insert(paramInt1);
      } else {
        this.m_I2.delete(paramInt1);
      } 
      if (d1 == this.m_C * this.m_data.instance(paramInt1).weight() && d2 == 0.0D) {
        this.m_I3.insert(paramInt1);
      } else {
        this.m_I3.delete(paramInt1);
      } 
      if ((0.0D < d3 && d3 < this.m_C * this.m_data.instance(paramInt2).weight()) || (0.0D < d4 && d4 < this.m_C * this.m_data.instance(paramInt2).weight())) {
        this.m_I0.insert(paramInt2);
      } else {
        this.m_I0.delete(paramInt2);
      } 
      if (d3 == 0.0D && d4 == 0.0D) {
        this.m_I1.insert(paramInt2);
      } else {
        this.m_I1.delete(paramInt2);
      } 
      if (d3 == 0.0D && d4 == this.m_C * this.m_data.instance(paramInt2).weight()) {
        this.m_I2.insert(paramInt2);
      } else {
        this.m_I2.delete(paramInt2);
      } 
      if (d3 == this.m_C * this.m_data.instance(paramInt2).weight() && d4 == 0.0D) {
        this.m_I3.insert(paramInt2);
      } else {
        this.m_I3.delete(paramInt2);
      } 
      this.m_bLow = -1.7976931348623157E308D;
      this.m_bUp = Double.MAX_VALUE;
      this.m_iLow = -1;
      this.m_iUp = -1;
      for (i = this.m_I0.getNext(-1); i != -1; i = this.m_I0.getNext(i)) {
        if (0.0D < this.m_alpha_[i] && this.m_alpha_[i] < this.m_C * this.m_data.instance(i).weight() && this.m_fcache[i] + this.m_epsilon > this.m_bLow) {
          this.m_bLow = this.m_fcache[i] + this.m_epsilon;
          this.m_iLow = i;
        } else if (0.0D < this.m_alpha[i] && this.m_alpha[i] < this.m_C * this.m_data.instance(i).weight() && this.m_fcache[i] - this.m_epsilon > this.m_bLow) {
          this.m_bLow = this.m_fcache[i] - this.m_epsilon;
          this.m_iLow = i;
        } 
        if (0.0D < this.m_alpha[i] && this.m_alpha[i] < this.m_C * this.m_data.instance(i).weight() && this.m_fcache[i] - this.m_epsilon < this.m_bUp) {
          this.m_bUp = this.m_fcache[i] - this.m_epsilon;
          this.m_iUp = i;
        } else if (0.0D < this.m_alpha_[i] && this.m_alpha_[i] < this.m_C * this.m_data.instance(i).weight() && this.m_fcache[i] + this.m_epsilon < this.m_bUp) {
          this.m_bUp = this.m_fcache[i] + this.m_epsilon;
          this.m_iUp = i;
        } 
      } 
      if (!this.m_I0.contains(paramInt1)) {
        if (this.m_I2.contains(paramInt1) && this.m_fcache[paramInt1] + this.m_epsilon > this.m_bLow) {
          this.m_bLow = this.m_fcache[paramInt1] + this.m_epsilon;
          this.m_iLow = paramInt1;
        } else if (this.m_I1.contains(paramInt1) && this.m_fcache[paramInt1] - this.m_epsilon > this.m_bLow) {
          this.m_bLow = this.m_fcache[paramInt1] - this.m_epsilon;
          this.m_iLow = paramInt1;
        } 
        if (this.m_I3.contains(paramInt1) && this.m_fcache[paramInt1] - this.m_epsilon < this.m_bUp) {
          this.m_bUp = this.m_fcache[paramInt1] - this.m_epsilon;
          this.m_iUp = paramInt1;
        } else if (this.m_I1.contains(paramInt1) && this.m_fcache[paramInt1] + this.m_epsilon < this.m_bUp) {
          this.m_bUp = this.m_fcache[paramInt1] + this.m_epsilon;
          this.m_iUp = paramInt1;
        } 
      } 
      if (!this.m_I0.contains(paramInt2)) {
        if (this.m_I2.contains(paramInt2) && this.m_fcache[paramInt2] + this.m_epsilon > this.m_bLow) {
          this.m_bLow = this.m_fcache[paramInt2] + this.m_epsilon;
          this.m_iLow = paramInt2;
        } else if (this.m_I1.contains(paramInt2) && this.m_fcache[paramInt2] - this.m_epsilon > this.m_bLow) {
          this.m_bLow = this.m_fcache[paramInt2] - this.m_epsilon;
          this.m_iLow = paramInt2;
        } 
        if (this.m_I3.contains(paramInt2) && this.m_fcache[paramInt2] - this.m_epsilon < this.m_bUp) {
          this.m_bUp = this.m_fcache[paramInt2] - this.m_epsilon;
          this.m_iUp = paramInt2;
        } else if (this.m_I1.contains(paramInt2) && this.m_fcache[paramInt2] + this.m_epsilon < this.m_bUp) {
          this.m_bUp = this.m_fcache[paramInt2] + this.m_epsilon;
          this.m_iUp = paramInt2;
        } 
      } 
      if (this.m_iLow == -1 || this.m_iUp == -1)
        throw new RuntimeException("Fatal error! The program failled to initialize i_Low, iUp."); 
      return true;
    } 
    return false;
  }
  
  public double classifyInstance(Instance paramInstance) throws Exception {
    if (!this.m_checksTurnedOff) {
      this.m_Missing.input(paramInstance);
      this.m_Missing.batchFinished();
      paramInstance = this.m_Missing.output();
    } 
    if (!this.m_onlyNumeric) {
      this.m_NominalToBinary.input(paramInstance);
      this.m_NominalToBinary.batchFinished();
      paramInstance = this.m_NominalToBinary.output();
    } 
    if (this.m_Filter != null) {
      this.m_Filter.input(paramInstance);
      this.m_Filter.batchFinished();
      paramInstance = this.m_Filter.output();
    } 
    double d = this.m_b;
    if (!this.m_useRBF && this.m_exponent == 1.0D) {
      if (this.m_sparseWeights == null) {
        int i = paramInstance.numValues();
        for (byte b = 0; b < i; b++) {
          if (paramInstance.index(b) != this.m_classIndex)
            d += this.m_weights[paramInstance.index(b)] * paramInstance.valueSparse(b); 
        } 
      } else {
        int i = paramInstance.numValues();
        int j = this.m_sparseWeights.length;
        byte b1 = 0;
        byte b2 = 0;
        while (b1 < i && b2 < j) {
          int k = paramInstance.index(b1);
          int m = this.m_sparseIndices[b2];
          if (k == m) {
            if (k != this.m_classIndex)
              d += paramInstance.valueSparse(b1) * this.m_sparseWeights[b2]; 
            b1++;
            b2++;
            continue;
          } 
          if (k > m) {
            b2++;
            continue;
          } 
          b1++;
        } 
      } 
    } else {
      for (byte b = 0; b < this.m_alpha.length; b++)
        d += (this.m_alpha[b] - this.m_alpha_[b]) * this.m_kernel.eval(-1, b, paramInstance); 
    } 
    return (d - this.m_Blin) / this.m_Alin;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(11);
    vector.addElement(new Option("\tThe amount up to which deviations are\n\ttolerated (epsilon). (default 1e-3)", "S", 1, "-S <double>"));
    vector.addElement(new Option("\tThe complexity constant C. (default 1)", "C", 1, "-C <double>"));
    vector.addElement(new Option("\tThe exponent for the polynomial kernel. (default 1)", "E", 1, "-E <double>"));
    vector.addElement(new Option("\tGamma for the RBF kernel. (default 0.01)", "G", 1, "-G <double>"));
    vector.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. (default 0=normalize)", "N", 1, "-N"));
    vector.addElement(new Option("\tFeature-space normalization (only for\n\tnon-linear polynomial kernels).", "F", 0, "-F"));
    vector.addElement(new Option("\tUse lower-order terms (only for non-linear\n\tpolynomial kernels).", "O", 0, "-O"));
    vector.addElement(new Option("\tUse RBF kernel. (default poly)", "R", 0, "-R"));
    vector.addElement(new Option("\tThe size of the kernel cache. (default 250007, use 0 for full cache)", "A", 1, "-A <int>"));
    vector.addElement(new Option("\tThe tolerance parameter. (default 1.0e-3)", "T", 1, "-T <double>"));
    vector.addElement(new Option("\tThe epsilon for round-off error. (default 1.0e-12)", "P", 1, "-P <double>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('S', paramArrayOfString);
    if (str1.length() != 0) {
      this.m_epsilon = (new Double(str1)).doubleValue();
    } else {
      this.m_epsilon = 0.001D;
    } 
    String str2 = Utils.getOption('C', paramArrayOfString);
    if (str2.length() != 0) {
      this.m_C = (new Double(str2)).doubleValue();
    } else {
      this.m_C = 1.0D;
    } 
    String str3 = Utils.getOption('E', paramArrayOfString);
    if (str3.length() != 0) {
      this.m_exponent = (new Double(str3)).doubleValue();
    } else {
      this.m_exponent = 1.0D;
    } 
    String str4 = Utils.getOption('G', paramArrayOfString);
    if (str4.length() != 0) {
      this.m_gamma = (new Double(str4)).doubleValue();
    } else {
      this.m_gamma = 0.01D;
    } 
    String str5 = Utils.getOption('A', paramArrayOfString);
    if (str5.length() != 0) {
      this.m_cacheSize = Integer.parseInt(str5);
    } else {
      this.m_cacheSize = 250007;
    } 
    String str6 = Utils.getOption('T', paramArrayOfString);
    if (str6.length() != 0) {
      this.m_tol = (new Double(str6)).doubleValue();
    } else {
      this.m_tol = 0.001D;
    } 
    String str7 = Utils.getOption('P', paramArrayOfString);
    if (str7.length() != 0) {
      this.m_eps = (new Double(str7)).doubleValue();
    } else {
      this.m_eps = 1.0E-12D;
    } 
    this.m_useRBF = Utils.getFlag('R', paramArrayOfString);
    String str8 = Utils.getOption('N', paramArrayOfString);
    if (str8.length() != 0) {
      setFilterType(new SelectedTag(Integer.parseInt(str8), TAGS_FILTER));
    } else {
      setFilterType(new SelectedTag(0, TAGS_FILTER));
    } 
    this.m_featureSpaceNormalization = Utils.getFlag('F', paramArrayOfString);
    if (this.m_useRBF && this.m_featureSpaceNormalization)
      throw new Exception("RBF machine doesn't require feature-space normalization."); 
    if (this.m_exponent == 1.0D && this.m_featureSpaceNormalization)
      throw new Exception("Can't use feature-space normalization with linear machine."); 
    this.m_lowerOrder = Utils.getFlag('O', paramArrayOfString);
    if (this.m_useRBF && this.m_lowerOrder)
      throw new Exception("Can't use lower-order terms with RBF machine."); 
    if (this.m_exponent == 1.0D && this.m_lowerOrder)
      throw new Exception("Can't use lower-order terms with linear machine."); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[20];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_epsilon;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_C;
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = "" + this.m_exponent;
    arrayOfString[b++] = "-G";
    arrayOfString[b++] = "" + this.m_gamma;
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + this.m_cacheSize;
    arrayOfString[b++] = "-T";
    arrayOfString[b++] = "" + this.m_tol;
    arrayOfString[b++] = "-P";
    arrayOfString[b++] = "" + this.m_eps;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_filterType;
    if (this.m_featureSpaceNormalization)
      arrayOfString[b++] = "-F"; 
    if (this.m_lowerOrder)
      arrayOfString[b++] = "-O"; 
    if (this.m_useRBF)
      arrayOfString[b++] = "-R"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String filterTypeTipText() {
    return "Determines how/if the data will be transformed.";
  }
  
  public SelectedTag getFilterType() {
    return new SelectedTag(this.m_filterType, TAGS_FILTER);
  }
  
  public void setFilterType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_FILTER)
      this.m_filterType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public String exponentTipText() {
    return "The exponent for the polynomial kernel.";
  }
  
  public double getExponent() {
    return this.m_exponent;
  }
  
  public void setExponent(double paramDouble) {
    if (paramDouble == 1.0D) {
      this.m_featureSpaceNormalization = false;
      this.m_lowerOrder = false;
    } 
    this.m_exponent = paramDouble;
  }
  
  public String gammaTipText() {
    return "The value of the gamma parameter for RBF kernels.";
  }
  
  public double getGamma() {
    return this.m_gamma;
  }
  
  public void setGamma(double paramDouble) {
    this.m_gamma = paramDouble;
  }
  
  public String cTipText() {
    return "The complexity parameter C.";
  }
  
  public double getC() {
    return this.m_C;
  }
  
  public void setC(double paramDouble) {
    this.m_C = paramDouble;
  }
  
  public String toleranceParameterTipText() {
    return "The tolerance parameter (shouldn't be changed).";
  }
  
  public double getToleranceParameter() {
    return this.m_tol;
  }
  
  public void setToleranceParameter(double paramDouble) {
    this.m_tol = paramDouble;
  }
  
  public String epsTipText() {
    return "The epsilon for round-off error (shouldn't be changed).";
  }
  
  public double getEps() {
    return this.m_eps;
  }
  
  public void setEps(double paramDouble) {
    this.m_eps = paramDouble;
  }
  
  public String epsilonTipText() {
    return "The amount up to which deviations are tolerated. Watch out, the value of epsilon is used with the (normalized/standardized) data.";
  }
  
  public double getEpsilon() {
    return this.m_epsilon;
  }
  
  public void setEpsilon(double paramDouble) {
    this.m_epsilon = paramDouble;
  }
  
  public String cacheSizeTipText() {
    return "The size of the kernel cache (should be a prime number).";
  }
  
  public int getCacheSize() {
    return this.m_cacheSize;
  }
  
  public void setCacheSize(int paramInt) {
    this.m_cacheSize = paramInt;
  }
  
  public String useRBFTipText() {
    return "Whether to use an RBF kernel instead of a polynomial one.";
  }
  
  public boolean getUseRBF() {
    return this.m_useRBF;
  }
  
  public void setUseRBF(boolean paramBoolean) {
    if (paramBoolean) {
      this.m_featureSpaceNormalization = false;
      this.m_lowerOrder = false;
    } 
    this.m_useRBF = paramBoolean;
  }
  
  public String featureSpaceNormalizationTipText() {
    return "Whether feature-space normalization is performed (only available for non-linear polynomial kernels).";
  }
  
  public boolean getFeatureSpaceNormalization() throws Exception {
    return this.m_featureSpaceNormalization;
  }
  
  public void setFeatureSpaceNormalization(boolean paramBoolean) throws Exception {
    if (this.m_useRBF || this.m_exponent == 1.0D) {
      this.m_featureSpaceNormalization = false;
    } else {
      this.m_featureSpaceNormalization = paramBoolean;
    } 
  }
  
  public String lowerOrderTermsTipText() {
    return "Whether lower order polyomials are also used (only available for non-linear polynomial kernels).";
  }
  
  public boolean getLowerOrderTerms() {
    return this.m_lowerOrder;
  }
  
  public void setLowerOrderTerms(boolean paramBoolean) {
    if (this.m_exponent == 1.0D || this.m_useRBF) {
      this.m_lowerOrder = false;
    } else {
      this.m_lowerOrder = paramBoolean;
    } 
  }
  
  public void turnChecksOff() {
    this.m_checksTurnedOff = true;
  }
  
  public void turnChecksOn() {
    this.m_checksTurnedOff = false;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    if (this.m_alpha == null && this.m_sparseWeights == null)
      return "SMOreg : No model built yet."; 
    try {
      stringBuffer.append("SMOreg\n\n");
      stringBuffer.append("Kernel used : \n");
      if (this.m_useRBF) {
        stringBuffer.append("  RBF kernel : K(x,y) = e^-(" + this.m_gamma + "* <x-y,x-y>^2)");
      } else if (this.m_exponent == 1.0D) {
        stringBuffer.append("  Linear Kernel : K(x,y) = <x,y>");
      } else if (this.m_featureSpaceNormalization) {
        if (this.m_lowerOrder) {
          stringBuffer.append("  Normalized Poly Kernel with lower order : K(x,y) = (<x,y>+1)^" + this.m_exponent + "/" + "((<x,x>+1)^" + this.m_exponent + "*" + "(<y,y>+1)^" + this.m_exponent + ")^(1/2)");
        } else {
          stringBuffer.append("  Normalized Poly Kernel : K(x,y) = <x,y>^" + this.m_exponent + "/" + "(<x,x>^" + this.m_exponent + "*" + "<y,y>^" + this.m_exponent + ")^(1/2)");
        } 
      } else if (this.m_lowerOrder) {
        stringBuffer.append("  Poly Kernel with lower order : K(x,y) = (<x,y> + 1)^" + this.m_exponent);
      } else {
        stringBuffer.append("  Poly Kernel : K(x,y) = <x,y>^" + this.m_exponent);
      } 
      stringBuffer.append("\n\n");
      String str = "";
      if (this.m_filterType == 1) {
        str = "(standardized) ";
      } else if (this.m_filterType == 0) {
        str = "(normalized) ";
      } 
      if (!this.m_useRBF && this.m_exponent == 1.0D) {
        stringBuffer.append("Machine Linear: showing attribute weights, ");
        stringBuffer.append("not support vectors.\n");
        stringBuffer.append(str + this.m_data.classAttribute().name() + " =\n");
        for (byte b1 = 0; b1 < this.m_sparseWeights.length; b1++) {
          if (this.m_sparseIndices[b1] != this.m_classIndex) {
            if (b) {
              stringBuffer.append(" + ");
            } else {
              stringBuffer.append("   ");
            } 
            stringBuffer.append(Utils.doubleToString(this.m_sparseWeights[b1], 12, 4) + " * ");
            if (this.m_filterType == 1) {
              stringBuffer.append("(standardized) ");
            } else if (this.m_filterType == 0) {
              stringBuffer.append("(normalized) ");
            } 
            if (!this.m_checksTurnedOff) {
              stringBuffer.append(this.m_data.attribute(this.m_sparseIndices[b1]).name() + "\n");
            } else {
              stringBuffer.append("attribute with index " + this.m_sparseIndices[b1] + "\n");
            } 
            b++;
          } 
        } 
      } else {
        stringBuffer.append("Support Vector Expansion :\n");
        stringBuffer.append(str + this.m_data.classAttribute().name() + " =\n");
        b = 0;
        for (byte b1 = 0; b1 < this.m_alpha.length; b1++) {
          double d = this.m_alpha[b1] - this.m_alpha_[b1];
          if (Math.abs(d) >= 1.0E-4D) {
            if (b > 0) {
              stringBuffer.append(" + ");
            } else {
              stringBuffer.append("   ");
            } 
            stringBuffer.append(Utils.doubleToString(d, 12, 4) + " * K[X(" + b1 + "), X]\n");
            b++;
          } 
        } 
      } 
      if (this.m_b > 0.0D) {
        stringBuffer.append(" + " + Utils.doubleToString(this.m_b, 12, 4));
      } else {
        stringBuffer.append(" - " + Utils.doubleToString(-this.m_b, 12, 4));
      } 
      if (this.m_useRBF || this.m_exponent != 1.0D)
        stringBuffer.append("\n\nNumber of support vectors: " + b); 
      int i = 0;
      int j = -1;
      if (this.m_kernel != null) {
        i = this.m_kernel.numEvals();
        j = this.m_kernel.numCacheHits();
      } 
      stringBuffer.append("\n\nNumber of kernel evaluations: " + i);
      if (j >= 0 && i > 0) {
        double d = (1 - i / (j + i));
        stringBuffer.append(" (" + Utils.doubleToString(d * 100.0D, 7, 3) + "% cached)");
      } 
    } catch (Exception exception) {
      return "Can't print the classifier.";
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      SMOreg sMOreg = new SMOreg();
      System.out.println(Evaluation.evaluateModel(sMOreg, paramArrayOfString));
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected double objFun() throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    for (byte b = 0; b < this.m_alpha.length; b++) {
      for (byte b1 = 0; b1 < this.m_alpha.length; b1++)
        d2 += (this.m_alpha[b] - this.m_alpha_[b]) * (this.m_alpha[b1] - this.m_alpha_[b1]) * this.m_kernel.eval(b, b1, this.m_data.instance(b)); 
      d3 += this.m_data.instance(b).classValue() * (this.m_alpha[b] - this.m_alpha_[b]) - this.m_epsilon * (this.m_alpha[b] + this.m_alpha_[b]);
    } 
    d1 += -0.5D * d2 + d3;
    return d1;
  }
  
  protected double objFun(int paramInt1, int paramInt2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    for (byte b = 0; b < this.m_alpha.length; b++) {
      double d4;
      double d5;
      if (b == paramInt1) {
        d4 = paramDouble1;
        d5 = paramDouble2;
      } else if (b == paramInt2) {
        d4 = paramDouble3;
        d5 = paramDouble4;
      } else {
        d4 = this.m_alpha[b];
        d5 = this.m_alpha_[b];
      } 
      for (byte b1 = 0; b1 < this.m_alpha.length; b1++) {
        double d6;
        double d7;
        if (b1 == paramInt1) {
          d6 = paramDouble1;
          d7 = paramDouble2;
        } else if (b1 == paramInt2) {
          d6 = paramDouble3;
          d7 = paramDouble4;
        } else {
          d6 = this.m_alpha[b1];
          d7 = this.m_alpha_[b1];
        } 
        d2 += (d4 - d5) * (d6 - d7) * this.m_kernel.eval(b, b1, this.m_data.instance(b));
      } 
      d3 += this.m_data.instance(b).classValue() * (d4 - d5) - this.m_epsilon * (d4 + d5);
    } 
    d1 += -0.5D * d2 + d3;
    return d1;
  }
  
  protected void checkSets() throws Exception {
    boolean[] arrayOfBoolean = new boolean[this.m_data.numInstances()];
    int i;
    for (i = this.m_I0.getNext(-1); i != -1; i = this.m_I0.getNext(i)) {
      if (arrayOfBoolean[i])
        throw new Exception("Fatal error! indice " + i + " appears in two different sets."); 
      arrayOfBoolean[i] = true;
      if ((0.0D >= this.m_alpha[i] || this.m_alpha[i] >= this.m_C * this.m_data.instance(i).weight()) && (0.0D >= this.m_alpha_[i] || this.m_alpha_[i] >= this.m_C * this.m_data.instance(i).weight()))
        throw new Exception("Warning! I0 contains an incorrect indice."); 
    } 
    for (i = this.m_I1.getNext(-1); i != -1; i = this.m_I1.getNext(i)) {
      if (arrayOfBoolean[i])
        throw new Exception("Fatal error! indice " + i + " appears in two different sets."); 
      arrayOfBoolean[i] = true;
      if (this.m_alpha[i] != 0.0D || this.m_alpha_[i] != 0.0D)
        throw new Exception("Fatal error! I1 contains an incorrect indice."); 
    } 
    for (i = this.m_I2.getNext(-1); i != -1; i = this.m_I2.getNext(i)) {
      if (arrayOfBoolean[i])
        throw new Exception("Fatal error! indice " + i + " appears in two different sets."); 
      arrayOfBoolean[i] = true;
      if (this.m_alpha[i] != 0.0D || this.m_alpha_[i] != this.m_C * this.m_data.instance(i).weight())
        throw new Exception("Fatal error! I2 contains an incorrect indice."); 
    } 
    for (i = this.m_I3.getNext(-1); i != -1; i = this.m_I3.getNext(i)) {
      if (arrayOfBoolean[i])
        throw new Exception("Fatal error! indice " + i + " appears in two different sets."); 
      arrayOfBoolean[i] = true;
      if (this.m_alpha_[i] != 0.0D || this.m_alpha[i] != this.m_C * this.m_data.instance(i).weight())
        throw new Exception("Fatal error! I3 contains an incorrect indice."); 
    } 
    for (i = 0; i < arrayOfBoolean.length; i++) {
      if (!arrayOfBoolean[i])
        throw new Exception("Fatal error! indice " + i + " doesn't belong to any set."); 
    } 
  }
  
  protected void checkAlphas() throws Exception {
    double d = 0.0D;
    for (byte b = 0; b < this.m_alpha.length; b++) {
      if (0.0D != this.m_alpha[b] && this.m_alpha_[b] != 0.0D)
        throw new Exception("Fatal error! Inconsistent alphas!"); 
      d += this.m_alpha[b] - this.m_alpha_[b];
    } 
    if (d > 1.0E-10D)
      throw new Exception("Fatal error! Inconsistent alphas' sum = " + d); 
  }
  
  protected void displayStat(int paramInt1, int paramInt2) throws Exception {
    System.err.println("\n-------- Status : ---------");
    System.err.println("\n i, alpha, alpha'\n");
    for (byte b = 0; b < this.m_alpha.length; b++) {
      double d = (this.m_bLow + this.m_bUp) / 2.0D;
      for (byte b1 = 0; b1 < this.m_alpha.length; b1++)
        d += (this.m_alpha[b1] - this.m_alpha_[b1]) * this.m_kernel.eval(b, b1, this.m_data.instance(b)); 
      System.err.print(" " + b + ":  (" + this.m_alpha[b] + ", " + this.m_alpha_[b] + "),       " + (this.m_data.instance(b).classValue() - this.m_epsilon) + " <= " + d + " <= " + (this.m_data.instance(b).classValue() + this.m_epsilon));
      if (b == paramInt1)
        System.err.print(" <-- i1"); 
      if (b == paramInt2)
        System.err.print(" <-- i2"); 
      System.err.println();
    } 
    System.err.println("bLow = " + this.m_bLow + "  bUp = " + this.m_bUp);
    System.err.println("---------------------------\n");
  }
  
  protected void displayB() throws Exception {
    for (byte b = 0; b < this.m_data.numInstances(); b++) {
      double d1 = this.m_data.instance(b).classValue();
      for (byte b1 = 0; b1 < this.m_alpha.length; b1++)
        d1 -= (this.m_alpha[b1] - this.m_alpha_[b1]) * this.m_kernel.eval(b, b1, this.m_data.instance(b)); 
      System.err.print("(" + this.m_alpha[b] + ", " + this.m_alpha_[b] + ") : ");
      System.err.print((d1 - this.m_epsilon) + ",  " + (d1 + this.m_epsilon));
      double d2 = d1 - this.m_epsilon;
      double d3 = d1 + this.m_epsilon;
      String str = "";
      if (this.m_I0.contains(b)) {
        if (0.0D < this.m_alpha[b] && this.m_alpha[b] < this.m_C * this.m_data.instance(b).weight())
          str = str + "(in I0a) bUp = min(bUp, " + d2 + ")   bLow = max(bLow, " + d2 + ")"; 
        if (0.0D < this.m_alpha_[b] && this.m_alpha_[b] < this.m_C * this.m_data.instance(b).weight())
          str = str + "(in I0a) bUp = min(bUp, " + d3 + ")   bLow = max(bLow, " + d3 + ")"; 
      } 
      if (this.m_I1.contains(b))
        str = str + "(in I1) bUp = min(bUp, " + d3 + ")   bLow = max(bLow, " + d2 + ")"; 
      if (this.m_I2.contains(b))
        str = str + "(in I2) bLow = max(bLow, " + d3 + ")"; 
      if (this.m_I3.contains(b))
        str = str + "(in I3) bUp = min(bUp, " + d2 + ")"; 
      System.err.println(" " + str + " {" + (this.m_alpha[b] - 1.0D) + ", " + (this.m_alpha_[b] - 1.0D) + "}");
    } 
    System.err.println("\n\n");
  }
  
  protected void checkOptimality() throws Exception {
    double d1 = Double.POSITIVE_INFINITY;
    double d2 = Double.NEGATIVE_INFINITY;
    byte b1 = -1;
    byte b2 = -1;
    byte b3;
    for (b3 = 0; b3 < this.m_data.numInstances(); b3++) {
      double d3 = this.m_data.instance(b3).classValue();
      for (byte b = 0; b < this.m_alpha.length; b++)
        d3 -= (this.m_alpha[b] - this.m_alpha_[b]) * this.m_kernel.eval(b3, b, this.m_data.instance(b3)); 
      double d4 = 0.0D;
      double d5 = 0.0D;
      if (this.m_I0.contains(b3) && 0.0D < this.m_alpha[b3] && this.m_alpha[b3] < this.m_C * this.m_data.instance(b3).weight()) {
        d4 = d3 - this.m_epsilon;
        d5 = d3 - this.m_epsilon;
      } 
      if (this.m_I0.contains(b3) && 0.0D < this.m_alpha_[b3] && this.m_alpha_[b3] < this.m_C * this.m_data.instance(b3).weight()) {
        d4 = d3 + this.m_epsilon;
        d5 = d3 + this.m_epsilon;
      } 
      if (this.m_I1.contains(b3)) {
        d4 = d3 - this.m_epsilon;
        d5 = d3 + this.m_epsilon;
      } 
      if (this.m_I2.contains(b3)) {
        d4 = d3 + this.m_epsilon;
        d5 = Double.POSITIVE_INFINITY;
      } 
      if (this.m_I3.contains(b3)) {
        d4 = Double.NEGATIVE_INFINITY;
        d5 = d3 - this.m_epsilon;
      } 
      if (d5 < d1) {
        d1 = d5;
        b1 = b3;
      } 
      if (d4 > d2) {
        d2 = d4;
        b2 = b3;
      } 
    } 
    if (d2 > d1 + 2.0D * this.m_tol)
      System.err.println("Warning! Optimality not reached : inequation (6) doesn't hold!"); 
    b3 = 1;
    for (byte b4 = 0; b4 < this.m_data.numInstances(); b4++) {
      double d3 = this.m_data.instance(b4).classValue();
      for (byte b = 0; b < this.m_alpha.length; b++)
        d3 -= (this.m_alpha[b] - this.m_alpha_[b]) * this.m_kernel.eval(b4, b, this.m_data.instance(b4)); 
      double d4 = d3 - (this.m_bUp + this.m_bLow) / 2.0D;
      if (this.m_alpha[b4] > 0.0D && d4 < this.m_epsilon - this.m_tol) {
        System.err.println("Warning! Optimality not reached : inequation (8a) doesn't hold for " + b4);
        b3 = 0;
      } 
      if (this.m_alpha[b4] < this.m_C * this.m_data.instance(b4).weight() && d4 > this.m_epsilon + this.m_tol) {
        System.err.println("Warning! Optimality not reached : inequation (8b) doesn't hold for " + b4);
        b3 = 0;
      } 
      if (this.m_alpha_[b4] > 0.0D && d4 > -this.m_epsilon + this.m_tol) {
        System.err.println("Warning! Optimality not reached : inequation (8c) doesn't hold for " + b4);
        b3 = 0;
      } 
      if (this.m_alpha_[b4] < this.m_C * this.m_data.instance(b4).weight() && d4 < -this.m_epsilon - this.m_tol) {
        System.err.println("Warning! Optimality not reached : inequation (8d) doesn't hold for " + b4);
        b3 = 0;
      } 
    } 
    if (b3 == 0)
      System.err.println(); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\SMOreg.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */