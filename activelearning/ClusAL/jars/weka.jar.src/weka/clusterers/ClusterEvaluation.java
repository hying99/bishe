package weka.clusterers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ClusterEvaluation implements Serializable {
  private Instances m_trainInstances;
  
  private Clusterer m_Clusterer;
  
  private StringBuffer m_clusteringResults;
  
  private int m_numClusters;
  
  private double[] m_clusterAssignments;
  
  private double m_logL;
  
  private int[] m_classToCluster = null;
  
  public void setClusterer(Clusterer paramClusterer) {
    this.m_Clusterer = paramClusterer;
  }
  
  public String clusterResultsToString() {
    return this.m_clusteringResults.toString();
  }
  
  public int getNumClusters() {
    return this.m_numClusters;
  }
  
  public double[] getClusterAssignments() {
    return this.m_clusterAssignments;
  }
  
  public int[] getClassesToClusters() {
    return this.m_classToCluster;
  }
  
  public double getLogLikelihood() {
    return this.m_logL;
  }
  
  public ClusterEvaluation() {
    setClusterer(new EM());
    this.m_trainInstances = null;
    this.m_clusteringResults = new StringBuffer();
    this.m_clusterAssignments = null;
  }
  
  public void evaluateClusterer(Instances paramInstances) throws Exception {
    byte b1 = 0;
    double d1 = 0.0D;
    int i = this.m_Clusterer.numberOfClusters();
    this.m_numClusters = i;
    int j = (int)(Math.log(paramInstances.numInstances()) / Math.log(10.0D) + 1.0D);
    double[] arrayOfDouble = new double[i];
    this.m_clusterAssignments = new double[paramInstances.numInstances()];
    Instances instances = paramInstances;
    boolean bool = (instances.classIndex() >= 0) ? true : false;
    byte b2 = 0;
    if (bool) {
      if (instances.classAttribute().isNumeric())
        throw new Exception("ClusterEvaluation: Class must be nominal!"); 
      Remove remove = new Remove();
      remove.setAttributeIndices("" + (instances.classIndex() + 1));
      remove.setInvertSelection(false);
      remove.setInputFormat(instances);
      instances = Filter.useFilter(instances, (Filter)remove);
    } 
    for (b1 = 0; b1 < instances.numInstances(); b1++) {
      int m = -1;
      try {
        if (this.m_Clusterer instanceof DensityBasedClusterer) {
          d1 += ((DensityBasedClusterer)this.m_Clusterer).logDensityForInstance(instances.instance(b1));
          m = this.m_Clusterer.clusterInstance(instances.instance(b1));
          this.m_clusterAssignments[b1] = m;
        } else {
          m = this.m_Clusterer.clusterInstance(instances.instance(b1));
          this.m_clusterAssignments[b1] = m;
        } 
      } catch (Exception exception) {
        b2++;
      } 
      if (m != -1)
        arrayOfDouble[m] = arrayOfDouble[m] + 1.0D; 
    } 
    double d2 = Utils.sum(arrayOfDouble);
    d1 /= d2;
    this.m_logL = d1;
    this.m_clusteringResults.append(this.m_Clusterer.toString());
    this.m_clusteringResults.append("Clustered Instances\n\n");
    int k = (int)(Math.log(i) / Math.log(10.0D) + 1.0D);
    for (b1 = 0; b1 < i; b1++) {
      if (arrayOfDouble[b1] > 0.0D)
        this.m_clusteringResults.append(Utils.doubleToString(b1, k, 0) + "      " + Utils.doubleToString(arrayOfDouble[b1], j, 0) + " (" + Utils.doubleToString(arrayOfDouble[b1] / d2 * 100.0D, 3, 0) + "%)\n"); 
    } 
    if (b2 > 0)
      this.m_clusteringResults.append("\nUnclustered instances : " + b2); 
    if (this.m_Clusterer instanceof DensityBasedClusterer)
      this.m_clusteringResults.append("\n\nLog likelihood: " + Utils.doubleToString(d1, 1, 5) + "\n"); 
    if (bool)
      evaluateClustersWithRespectToClass(paramInstances); 
  }
  
  private void evaluateClustersWithRespectToClass(Instances paramInstances) throws Exception {
    int i = paramInstances.classAttribute().numValues();
    int[][] arrayOfInt = new int[this.m_numClusters][i];
    int[] arrayOfInt1 = new int[this.m_numClusters];
    double[] arrayOfDouble1 = new double[this.m_numClusters + 1];
    double[] arrayOfDouble2 = new double[this.m_numClusters + 1];
    for (byte b1 = 0; b1 < paramInstances.numInstances(); b1++) {
      arrayOfInt[(int)this.m_clusterAssignments[b1]][(int)paramInstances.instance(b1).classValue()] = arrayOfInt[(int)this.m_clusterAssignments[b1]][(int)paramInstances.instance(b1).classValue()] + 1;
      arrayOfInt1[(int)this.m_clusterAssignments[b1]] = arrayOfInt1[(int)this.m_clusterAssignments[b1]] + 1;
    } 
    arrayOfDouble1[this.m_numClusters] = Double.MAX_VALUE;
    mapClasses(0, arrayOfInt, arrayOfInt1, arrayOfDouble2, arrayOfDouble1, 0);
    this.m_clusteringResults.append("\n\nClass attribute: " + paramInstances.classAttribute().name() + "\n");
    this.m_clusteringResults.append("Classes to Clusters:\n");
    String str = toMatrixString(arrayOfInt, arrayOfInt1, paramInstances);
    this.m_clusteringResults.append(str).append("\n");
    int j = 1 + (int)(Math.log(this.m_numClusters) / Math.log(10.0D));
    byte b2;
    for (b2 = 0; b2 < this.m_numClusters; b2++) {
      if (arrayOfInt1[b2] > 0) {
        this.m_clusteringResults.append("Cluster " + Utils.doubleToString(b2, j, 0));
        this.m_clusteringResults.append(" <-- ");
        if (arrayOfDouble1[b2] < 0.0D) {
          this.m_clusteringResults.append("No class\n");
        } else {
          this.m_clusteringResults.append(paramInstances.classAttribute().value((int)arrayOfDouble1[b2])).append("\n");
        } 
      } 
    } 
    this.m_clusteringResults.append("\nIncorrectly clustered instances :\t" + arrayOfDouble1[this.m_numClusters] + "\t" + Utils.doubleToString(arrayOfDouble1[this.m_numClusters] / paramInstances.numInstances() * 100.0D, 8, 4) + " %\n");
    this.m_classToCluster = new int[this.m_numClusters];
    for (b2 = 0; b2 < this.m_numClusters; b2++)
      this.m_classToCluster[b2] = (int)arrayOfDouble1[b2]; 
  }
  
  private String toMatrixString(int[][] paramArrayOfint, int[] paramArrayOfint1, Instances paramInstances) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    int j;
    for (j = 0; j < this.m_numClusters; j++) {
      for (byte b1 = 0; b1 < (paramArrayOfint[j]).length; b1++) {
        if (paramArrayOfint[j][b1] > i)
          i = paramArrayOfint[j][b1]; 
      } 
    } 
    j = 1 + Math.max((int)(Math.log(i) / Math.log(10.0D)), (int)(Math.log(this.m_numClusters) / Math.log(10.0D)));
    stringBuffer.append("\n");
    byte b;
    for (b = 0; b < this.m_numClusters; b++) {
      if (paramArrayOfint1[b] > 0)
        stringBuffer.append(" ").append(Utils.doubleToString(b, j, 0)); 
    } 
    stringBuffer.append("  <-- assigned to cluster\n");
    for (b = 0; b < (paramArrayOfint[0]).length; b++) {
      for (byte b1 = 0; b1 < this.m_numClusters; b1++) {
        if (paramArrayOfint1[b1] > 0)
          stringBuffer.append(" ").append(Utils.doubleToString(paramArrayOfint[b1][b], j, 0)); 
      } 
      stringBuffer.append(" | ").append(paramInstances.classAttribute().value(b)).append("\n");
    } 
    return stringBuffer.toString();
  }
  
  private void mapClasses(int paramInt1, int[][] paramArrayOfint, int[] paramArrayOfint1, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt2) {
    if (paramInt1 == this.m_numClusters) {
      if (paramInt2 < paramArrayOfdouble2[this.m_numClusters]) {
        paramArrayOfdouble2[this.m_numClusters] = paramInt2;
        for (byte b = 0; b < this.m_numClusters; b++)
          paramArrayOfdouble2[b] = paramArrayOfdouble1[b]; 
      } 
    } else if (paramArrayOfint1[paramInt1] == 0) {
      paramArrayOfdouble1[paramInt1] = -1.0D;
      mapClasses(paramInt1 + 1, paramArrayOfint, paramArrayOfint1, paramArrayOfdouble1, paramArrayOfdouble2, paramInt2);
    } else {
      paramArrayOfdouble1[paramInt1] = -1.0D;
      mapClasses(paramInt1 + 1, paramArrayOfint, paramArrayOfint1, paramArrayOfdouble1, paramArrayOfdouble2, paramInt2 + paramArrayOfint1[paramInt1]);
      for (byte b = 0; b < (paramArrayOfint[0]).length; b++) {
        if (paramArrayOfint[paramInt1][b] > 0) {
          boolean bool = true;
          for (byte b1 = 0; b1 < paramInt1; b1++) {
            if ((int)paramArrayOfdouble1[b1] == b) {
              bool = false;
              break;
            } 
          } 
          if (bool) {
            paramArrayOfdouble1[paramInt1] = b;
            mapClasses(paramInt1 + 1, paramArrayOfint, paramArrayOfint1, paramArrayOfdouble1, paramArrayOfdouble2, paramInt2 + paramArrayOfint1[paramInt1] - paramArrayOfint[paramInt1][b]);
          } 
        } 
      } 
    } 
  }
  
  public static String evaluateClusterer(Clusterer paramClusterer, String[] paramArrayOfString) throws Exception {
    String str1;
    String str2;
    String str3;
    String str4;
    int i = 1;
    int j = 10;
    boolean bool1 = false;
    Instances instances = null;
    Object object = null;
    String[] arrayOfString = null;
    boolean bool2 = false;
    Range range = null;
    ObjectInputStream objectInputStream = null;
    ObjectOutputStream objectOutputStream = null;
    StringBuffer stringBuffer = new StringBuffer();
    int k = -1;
    try {
      String str7;
      if (Utils.getFlag('h', paramArrayOfString))
        throw new Exception("Help requested."); 
      str3 = Utils.getOption('l', paramArrayOfString);
      str4 = Utils.getOption('d', paramArrayOfString);
      str1 = Utils.getOption('t', paramArrayOfString);
      str2 = Utils.getOption('T', paramArrayOfString);
      try {
        str7 = Utils.getOption('p', paramArrayOfString);
      } catch (Exception exception) {
        throw new Exception(exception.getMessage() + "\nNOTE: the -p option has changed. " + "It now expects a parameter specifying a range of attributes " + "to list with the predictions. Use '-p 0' for none.");
      } 
      if (str7.length() != 0) {
        bool2 = true;
        if (!str7.equals("0"))
          range = new Range(str7); 
      } 
      if (str1.length() == 0) {
        if (str3.length() == 0)
          throw new Exception("No training file and no object input file given."); 
        if (str2.length() == 0)
          throw new Exception("No training file and no test file given."); 
      } else if (str3.length() != 0 && !bool2) {
        throw new Exception("Can't use both train and model file unless -p specified.");
      } 
      String str5 = Utils.getOption('s', paramArrayOfString);
      if (str5.length() != 0)
        i = Integer.parseInt(str5); 
      String str6 = Utils.getOption('x', paramArrayOfString);
      if (str6.length() != 0) {
        j = Integer.parseInt(str6);
        bool1 = true;
      } 
    } catch (Exception exception) {
      throw new Exception('\n' + exception.getMessage() + makeOptionString(paramClusterer));
    } 
    try {
      if (str1.length() != 0) {
        instances = new Instances(new BufferedReader(new FileReader(str1)));
        String str = Utils.getOption('c', paramArrayOfString);
        if (str.length() != 0) {
          if (str.compareTo("last") == 0) {
            k = instances.numAttributes();
          } else if (str.compareTo("first") == 0) {
            k = 1;
          } else {
            k = Integer.parseInt(str);
          } 
          if (bool1 || str2.length() != 0)
            throw new Exception("Can only do class based evaluation on the training data"); 
          if (str3.length() != 0)
            throw new Exception("Can't load a clusterer and do class based evaluation"); 
        } 
        if (k != -1) {
          if (k < 1 || k > instances.numAttributes())
            throw new Exception("Class is out of range!"); 
          if (!instances.attribute(k - 1).isNominal())
            throw new Exception("Class must be nominal!"); 
          instances.setClassIndex(k - 1);
        } 
      } 
      if (str3.length() != 0)
        objectInputStream = new ObjectInputStream(new FileInputStream(str3)); 
      if (str4.length() != 0)
        objectOutputStream = new ObjectOutputStream(new FileOutputStream(str4)); 
    } catch (Exception exception) {
      throw new Exception("ClusterEvaluation: " + exception.getMessage() + '.');
    } 
    if (paramArrayOfString != null) {
      arrayOfString = new String[paramArrayOfString.length];
      System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
    } 
    if (str3.length() != 0)
      Utils.checkForRemainingOptions(paramArrayOfString); 
    if (paramClusterer instanceof OptionHandler)
      ((OptionHandler)paramClusterer).setOptions(paramArrayOfString); 
    Utils.checkForRemainingOptions(paramArrayOfString);
    if (str3.length() != 0) {
      paramClusterer = (Clusterer)objectInputStream.readObject();
      objectInputStream.close();
    } else if (k == -1) {
      paramClusterer.buildClusterer(instances);
    } else {
      Remove remove = new Remove();
      remove.setAttributeIndices("" + k);
      remove.setInvertSelection(false);
      remove.setInputFormat(instances);
      Instances instances1 = Filter.useFilter(instances, (Filter)remove);
      paramClusterer.buildClusterer(instances1);
      ClusterEvaluation clusterEvaluation = new ClusterEvaluation();
      clusterEvaluation.setClusterer(paramClusterer);
      clusterEvaluation.evaluateClusterer(instances);
      return "\n\n=== Clustering stats for training data ===\n\n" + clusterEvaluation.clusterResultsToString();
    } 
    if (bool2)
      return printClusterings(paramClusterer, instances, str2, range); 
    stringBuffer.append(paramClusterer.toString());
    stringBuffer.append("\n\n=== Clustering stats for training data ===\n\n" + printClusterStats(paramClusterer, str1));
    if (str2.length() != 0)
      stringBuffer.append("\n\n=== Clustering stats for testing data ===\n\n" + printClusterStats(paramClusterer, str2)); 
    if (paramClusterer instanceof DensityBasedClusterer && bool1 == true && str2.length() == 0 && str3.length() == 0) {
      Random random = new Random(i);
      random.setSeed(i);
      instances.randomize(random);
      stringBuffer.append(crossValidateModel(paramClusterer.getClass().getName(), instances, j, arrayOfString, random));
    } 
    if (str4.length() != 0) {
      objectOutputStream.writeObject(paramClusterer);
      objectOutputStream.flush();
      objectOutputStream.close();
    } 
    return stringBuffer.toString();
  }
  
  public static double crossValidateModel(DensityBasedClusterer paramDensityBasedClusterer, Instances paramInstances, int paramInt, Random paramRandom) throws Exception {
    double d = 0.0D;
    paramInstances = new Instances(paramInstances);
    paramInstances.randomize(paramRandom);
    for (byte b = 0; b < paramInt; b++) {
      Instances instances1 = paramInstances.trainCV(paramInt, b, paramRandom);
      paramDensityBasedClusterer.buildClusterer(instances1);
      Instances instances2 = paramInstances.testCV(paramInt, b);
      for (byte b1 = 0; b1 < instances2.numInstances(); b1++) {
        try {
          d += paramDensityBasedClusterer.logDensityForInstance(instances2.instance(b1));
        } catch (Exception exception) {}
      } 
    } 
    return d / paramInstances.numInstances();
  }
  
  public static String crossValidateModel(String paramString, Instances paramInstances, int paramInt, String[] paramArrayOfString, Random paramRandom) throws Exception {
    Clusterer clusterer = null;
    String[] arrayOfString = null;
    double d = 0.0D;
    StringBuffer stringBuffer = new StringBuffer();
    if (paramArrayOfString != null)
      arrayOfString = new String[paramArrayOfString.length]; 
    paramInstances = new Instances(paramInstances);
    try {
      clusterer = (Clusterer)Class.forName(paramString).newInstance();
    } catch (Exception exception) {
      throw new Exception("Can't find class with name " + paramString + '.');
    } 
    if (!(clusterer instanceof DensityBasedClusterer))
      throw new Exception(paramString + " must be a distrinbution " + "clusterer."); 
    if (paramArrayOfString != null)
      System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length); 
    if (clusterer instanceof OptionHandler)
      try {
        ((OptionHandler)clusterer).setOptions(arrayOfString);
        Utils.checkForRemainingOptions(arrayOfString);
      } catch (Exception exception) {
        throw new Exception("Can't parse given options in cross-validation!");
      }  
    d = crossValidateModel((DensityBasedClusterer)clusterer, paramInstances, paramInt, paramRandom);
    stringBuffer.append("\n" + paramInt + " fold CV Log Likelihood: " + Utils.doubleToString(d, 6, 4) + "\n");
    return stringBuffer.toString();
  }
  
  private static String printClusterStats(Clusterer paramClusterer, String paramString) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    byte b1 = 0;
    double d = 0.0D;
    int i = paramClusterer.numberOfClusters();
    double[] arrayOfDouble = new double[i];
    byte b2 = 0;
    if (paramString.length() != 0) {
      BufferedReader bufferedReader = null;
      try {
        bufferedReader = new BufferedReader(new FileReader(paramString));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + '.');
      } 
      Instances instances = new Instances(bufferedReader, 1);
      while (instances.readInstance(bufferedReader)) {
        try {
          int m = paramClusterer.clusterInstance(instances.instance(0));
          if (paramClusterer instanceof DensityBasedClusterer)
            d += ((DensityBasedClusterer)paramClusterer).logDensityForInstance(instances.instance(0)); 
          arrayOfDouble[m] = arrayOfDouble[m] + 1.0D;
        } catch (Exception exception) {
          b2++;
        } 
        instances.delete(0);
        b1++;
      } 
      int j = (int)(Math.log(i) / Math.log(10.0D) + 1.0D);
      int k = (int)(Math.log(b1) / Math.log(10.0D) + 1.0D);
      double d1 = Utils.sum(arrayOfDouble);
      d /= d1;
      stringBuffer.append("Clustered Instances\n");
      for (b1 = 0; b1 < i; b1++) {
        if (arrayOfDouble[b1] > 0.0D)
          stringBuffer.append(Utils.doubleToString(b1, j, 0) + "      " + Utils.doubleToString(arrayOfDouble[b1], k, 0) + " (" + Utils.doubleToString(arrayOfDouble[b1] / d1 * 100.0D, 3, 0) + "%)\n"); 
      } 
      if (b2 > 0)
        stringBuffer.append("\nUnclustered Instances : " + b2); 
      if (paramClusterer instanceof DensityBasedClusterer)
        stringBuffer.append("\n\nLog likelihood: " + Utils.doubleToString(d, 1, 5) + "\n"); 
    } 
    return stringBuffer.toString();
  }
  
  private static String printClusterings(Clusterer paramClusterer, Instances paramInstances, String paramString, Range paramRange) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    if (paramString.length() != 0) {
      BufferedReader bufferedReader = null;
      try {
        bufferedReader = new BufferedReader(new FileReader(paramString));
      } catch (Exception exception) {
        throw new Exception("Can't open file " + exception.getMessage() + '.');
      } 
      Instances instances = new Instances(bufferedReader, 1);
      while (instances.readInstance(bufferedReader)) {
        try {
          int i = paramClusterer.clusterInstance(instances.instance(0));
          stringBuffer.append(b + " " + i + " " + attributeValuesString(instances.instance(0), paramRange) + "\n");
        } catch (Exception exception) {
          stringBuffer.append(b + " Unclustered " + attributeValuesString(instances.instance(0), paramRange) + "\n");
        } 
        instances.delete(0);
        b++;
      } 
    } else {
      for (b = 0; b < paramInstances.numInstances(); b++) {
        try {
          int i = paramClusterer.clusterInstance(paramInstances.instance(b));
          stringBuffer.append(b + " " + i + " " + attributeValuesString(paramInstances.instance(b), paramRange) + "\n");
        } catch (Exception exception) {
          stringBuffer.append(b + " Unclustered " + attributeValuesString(paramInstances.instance(b), paramRange) + "\n");
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static String attributeValuesString(Instance paramInstance, Range paramRange) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramRange != null) {
      boolean bool = true;
      paramRange.setUpper(paramInstance.numAttributes() - 1);
      for (byte b = 0; b < paramInstance.numAttributes(); b++) {
        if (paramRange.isInRange(b)) {
          if (bool) {
            stringBuffer.append("(");
          } else {
            stringBuffer.append(",");
          } 
          stringBuffer.append(paramInstance.toString(b));
          bool = false;
        } 
      } 
      if (!bool)
        stringBuffer.append(")"); 
    } 
    return stringBuffer.toString();
  }
  
  private static String makeOptionString(Clusterer paramClusterer) {
    StringBuffer stringBuffer = new StringBuffer("");
    stringBuffer.append("\n\nGeneral options:\n\n");
    stringBuffer.append("-t <name of training file>\n");
    stringBuffer.append("\tSets training file.\n");
    stringBuffer.append("-T <name of test file>\n");
    stringBuffer.append("-l <name of input file>\n");
    stringBuffer.append("\tSets model input file.\n");
    stringBuffer.append("-d <name of output file>\n");
    stringBuffer.append("\tSets model output file.\n");
    stringBuffer.append("-p <attribute range>\n");
    stringBuffer.append("\tOutput predictions. Predictions are for training file\n\tif only training file is specified,\n\totherwise predictions are for the test file.\n\tThe range specifies attribute values to be output\n\twith the predictions. Use '-p 0' for none.\n");
    stringBuffer.append("-x <number of folds>\n");
    stringBuffer.append("\tOnly Distribution Clusterers can be cross validated.\n");
    stringBuffer.append("-s <random number seed>\n");
    stringBuffer.append("-c <class index>\n");
    stringBuffer.append("\tSet class attribute. If supplied, class is ignored");
    stringBuffer.append("\n\tduring clustering but is used in a classes to");
    stringBuffer.append("\n\tclusters evaluation.\n");
    if (paramClusterer instanceof OptionHandler) {
      stringBuffer.append("\nOptions specific to " + paramClusterer.getClass().getName() + ":\n\n");
      Enumeration enumeration = ((OptionHandler)paramClusterer).listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        stringBuffer.append(option.synopsis() + '\n');
        stringBuffer.append(option.description() + "\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("The first argument must be the name of a clusterer"); 
      String str = paramArrayOfString[0];
      paramArrayOfString[0] = "";
      Clusterer clusterer = Clusterer.forName(str, null);
      System.out.println(evaluateClusterer(clusterer, paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\ClusterEvaluation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */