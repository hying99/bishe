package weka.classifiers.bayes.net;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.estimators.Estimator;

public class BayesNetGenerator extends BayesNet {
  int m_nSeed = 1;
  
  Random random;
  
  boolean m_bGenerateNet = false;
  
  int m_nNrOfNodes = 10;
  
  int m_nNrOfArcs = 10;
  
  int m_nNrOfInstances = 10;
  
  int m_nCardinality = 2;
  
  String m_sBIFFile = "";
  
  public void generateRandomNetwork() throws Exception {
    if (this.m_otherBayesNet == null) {
      Init(this.m_nNrOfNodes, this.m_nCardinality);
      generateRandomNetworkStructure(this.m_nNrOfNodes, this.m_nNrOfArcs);
      generateRandomDistributions(this.m_nNrOfNodes, this.m_nCardinality);
    } else {
      this.m_nNrOfNodes = this.m_otherBayesNet.getNrOfNodes();
      this.m_ParentSets = this.m_otherBayesNet.getParentSets();
      this.m_Distributions = this.m_otherBayesNet.getDistributions();
      this.random = new Random(this.m_nSeed);
      FastVector fastVector = new FastVector(this.m_nNrOfNodes);
      for (byte b = 0; b < this.m_nNrOfNodes; b++) {
        int i = this.m_otherBayesNet.getCardinality(b);
        FastVector fastVector1 = new FastVector(i + 1);
        for (byte b1 = 0; b1 < i; b1++)
          fastVector1.addElement(this.m_otherBayesNet.getNodeValue(b, b1)); 
        Attribute attribute = new Attribute(this.m_otherBayesNet.getNodeName(b), fastVector1);
        fastVector.addElement(attribute);
      } 
      this.m_Instances = new Instances(this.m_otherBayesNet.getName(), fastVector, 100);
      this.m_Instances.setClassIndex(this.m_nNrOfNodes - 1);
    } 
  }
  
  public void Init(int paramInt1, int paramInt2) throws Exception {
    this.random = new Random(this.m_nSeed);
    FastVector fastVector1 = new FastVector(paramInt1);
    FastVector fastVector2 = new FastVector(paramInt2 + 1);
    byte b;
    for (b = 0; b < paramInt2; b++)
      fastVector2.addElement("Value" + (b + 1)); 
    for (b = 0; b < paramInt1; b++) {
      Attribute attribute = new Attribute("Node" + (b + 1), fastVector2);
      fastVector1.addElement(attribute);
    } 
    this.m_Instances = new Instances("RandomNet", fastVector1, 100);
    this.m_Instances.setClassIndex(paramInt1 - 1);
    setUseADTree(false);
    initStructure();
    this.m_Distributions = new Estimator[paramInt1][1];
    for (b = 0; b < paramInt1; b++)
      this.m_Distributions[b][0] = (Estimator)new DiscreteEstimatorBayes(paramInt2, getEstimator().getAlpha()); 
  }
  
  public void generateRandomNetworkStructure(int paramInt1, int paramInt2) throws Exception {
    if (paramInt2 < paramInt1 - 1)
      throw new Exception("Number of arcs should be at least (nNodes - 1) = " + (paramInt1 - 1) + " instead of " + paramInt2); 
    if (paramInt2 > paramInt1 * (paramInt1 - 1) / 2)
      throw new Exception("Number of arcs should be at most nNodes * (nNodes - 1) / 2 = " + (paramInt1 * (paramInt1 - 1) / 2) + " instead of " + paramInt2); 
    if (paramInt2 == 0)
      return; 
    generateTree(paramInt1);
    for (int i = paramInt1 - 1; i < paramInt2; i++) {
      boolean bool = false;
      while (!bool) {
        int j = this.random.nextInt(paramInt1);
        int k = this.random.nextInt(paramInt1);
        if (j == k)
          k = (j + 1) % paramInt1; 
        if (k < j) {
          int m = j;
          j = k;
          k = m;
        } 
        if (!this.m_ParentSets[k].contains(j)) {
          this.m_ParentSets[k].addParent(j, this.m_Instances);
          bool = true;
        } 
      } 
    } 
  }
  
  void generateTree(int paramInt) {
    boolean[] arrayOfBoolean = new boolean[paramInt];
    int i = this.random.nextInt(paramInt);
    int j = this.random.nextInt(paramInt);
    if (i == j)
      j = (i + 1) % paramInt; 
    if (j < i) {
      int k = i;
      i = j;
      j = k;
    } 
    this.m_ParentSets[j].addParent(i, this.m_Instances);
    arrayOfBoolean[i] = true;
    arrayOfBoolean[j] = true;
    for (byte b = 2; b < paramInt; b++) {
      int k = this.random.nextInt(paramInt);
      i = 0;
      while (k >= 0) {
        for (i = (i + 1) % paramInt; !arrayOfBoolean[i]; i = (i + 1) % paramInt);
        k--;
      } 
      k = this.random.nextInt(paramInt);
      j = 0;
      while (k >= 0) {
        for (j = (j + 1) % paramInt; arrayOfBoolean[j]; j = (j + 1) % paramInt);
        k--;
      } 
      if (j < i) {
        int m = i;
        i = j;
        j = m;
      } 
      this.m_ParentSets[j].addParent(i, this.m_Instances);
      arrayOfBoolean[i] = true;
      arrayOfBoolean[j] = true;
    } 
  }
  
  void generateRandomDistributions(int paramInt1, int paramInt2) {
    int i = 1;
    byte b;
    for (b = 0; b < paramInt1; b++) {
      if (this.m_ParentSets[b].getCardinalityOfParents() > i)
        i = this.m_ParentSets[b].getCardinalityOfParents(); 
    } 
    this.m_Distributions = new Estimator[this.m_Instances.numAttributes()][i];
    for (b = 0; b < paramInt1; b++) {
      int[] arrayOfInt = new int[paramInt2 + 1];
      arrayOfInt[0] = 0;
      arrayOfInt[paramInt2] = 1000;
      for (byte b1 = 0; b1 < this.m_ParentSets[b].getCardinalityOfParents(); b1++) {
        byte b2;
        for (b2 = 1; b2 < paramInt2; b2++)
          arrayOfInt[b2] = this.random.nextInt(1000); 
        for (b2 = 1; b2 < paramInt2; b2++) {
          for (int j = b2 + 1; j < paramInt2; j++) {
            if (arrayOfInt[j] < arrayOfInt[b2]) {
              int k = arrayOfInt[j];
              arrayOfInt[j] = arrayOfInt[b2];
              arrayOfInt[b2] = k;
            } 
          } 
        } 
        DiscreteEstimatorBayes discreteEstimatorBayes = new DiscreteEstimatorBayes(paramInt2, getEstimator().getAlpha());
        for (byte b3 = 0; b3 < paramInt2; b3++)
          discreteEstimatorBayes.addValue(b3, (arrayOfInt[b3 + 1] - arrayOfInt[b3])); 
        this.m_Distributions[b][b1] = (Estimator)discreteEstimatorBayes;
      } 
    } 
  }
  
  public void generateInstances() {
    for (byte b = 0; b < this.m_nNrOfInstances; b++) {
      int i = this.m_Instances.numAttributes();
      Instance instance = new Instance(i);
      instance.setDataset(this.m_Instances);
      for (byte b1 = 0; b1 < i; b1++) {
        double d1 = 0.0D;
        for (byte b2 = 0; b2 < this.m_ParentSets[b1].getNrOfParents(); b2++) {
          int j = this.m_ParentSets[b1].getParent(b2);
          d1 = d1 * this.m_Instances.attribute(j).numValues() + instance.value(j);
        } 
        double d2 = (this.random.nextInt(1000) / 1000.0F);
        byte b3;
        for (b3 = 0; d2 > this.m_Distributions[b1][(int)d1].getProbability(b3); b3++)
          d2 -= this.m_Distributions[b1][(int)d1].getProbability(b3); 
        instance.setValue(b1, b3);
      } 
      this.m_Instances.add(instance);
    } 
  }
  
  public String toString() {
    if (this.m_bGenerateNet)
      return toXMLBIF03(); 
    StringBuffer stringBuffer = new StringBuffer();
    return this.m_Instances.toString();
  }
  
  void setNrOfNodes(int paramInt) {
    this.m_nNrOfNodes = paramInt;
  }
  
  void setNrOfArcs(int paramInt) {
    this.m_nNrOfArcs = paramInt;
  }
  
  void setNrOfInstances(int paramInt) {
    this.m_nNrOfInstances = paramInt;
  }
  
  void setCardinality(int paramInt) {
    this.m_nCardinality = paramInt;
  }
  
  void setSeed(int paramInt) {
    this.m_nSeed = paramInt;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tGenerate network (instead of instances)\n", "B", 0, "-B"));
    vector.addElement(new Option("\tNr of nodes\n", "N", 1, "-N <integer>"));
    vector.addElement(new Option("\tNr of arcs\n", "A", 1, "-A <integer>"));
    vector.addElement(new Option("\tNr of instances\n", "I", 1, "-I <integer>"));
    vector.addElement(new Option("\tCardinality of the variables\n", "C", 1, "-C <integer>"));
    vector.addElement(new Option("\tSeed for random number generator\n", "S", 1, "-S <integer>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    this.m_bGenerateNet = Utils.getFlag('B', paramArrayOfString);
    String str1 = Utils.getOption('N', paramArrayOfString);
    if (str1.length() != 0) {
      setNrOfNodes(Integer.parseInt(str1));
    } else {
      setNrOfNodes(10);
    } 
    String str2 = Utils.getOption('A', paramArrayOfString);
    if (str2.length() != 0) {
      setNrOfArcs(Integer.parseInt(str2));
    } else {
      setNrOfArcs(10);
    } 
    String str3 = Utils.getOption('M', paramArrayOfString);
    if (str3.length() != 0) {
      setNrOfInstances(Integer.parseInt(str3));
    } else {
      setNrOfInstances(10);
    } 
    String str4 = Utils.getOption('C', paramArrayOfString);
    if (str4.length() != 0) {
      setCardinality(Integer.parseInt(str4));
    } else {
      setCardinality(2);
    } 
    String str5 = Utils.getOption('S', paramArrayOfString);
    if (str5.length() != 0) {
      setSeed(Integer.parseInt(str5));
    } else {
      setSeed(1);
    } 
    String str6 = Utils.getOption('F', paramArrayOfString);
    if (str6 != null && str6 != "")
      setBIFFile(str6); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[13];
    byte b = 0;
    if (!this.m_bGenerateNet)
      arrayOfString[b++] = "-B"; 
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_nNrOfNodes;
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + this.m_nNrOfArcs;
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + this.m_nNrOfInstances;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_nCardinality;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + this.m_nSeed;
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + this.m_sBIFFile;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public static void main(String[] paramArrayOfString) {
    BayesNetGenerator bayesNetGenerator = new BayesNetGenerator();
    if (paramArrayOfString.length == 0) {
      System.err.println(bayesNetGenerator.listOptions().toString());
      return;
    } 
    try {
      bayesNetGenerator.setOptions(paramArrayOfString);
      bayesNetGenerator.generateRandomNetwork();
      if (!bayesNetGenerator.m_bGenerateNet)
        bayesNetGenerator.generateInstances(); 
      System.out.println(bayesNetGenerator.toString());
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(bayesNetGenerator.listOptions().toString());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\BayesNetGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */