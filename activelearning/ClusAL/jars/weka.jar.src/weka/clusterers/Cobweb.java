package weka.clusterers;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.AttributeStats;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

public class Cobweb extends Clusterer implements OptionHandler, Drawable {
  protected static final double m_normal = 1.0D / 2.0D * Math.sqrt(Math.PI);
  
  protected double m_acuity = 1.0D;
  
  protected double m_cutoff = 0.01D * m_normal;
  
  protected CNode m_cobwebTree = null;
  
  protected int m_numberOfClusters = -1;
  
  protected int m_numberSplits;
  
  protected int m_numberMerges;
  
  protected boolean m_saveInstances = false;
  
  public void buildClusterer(Instances paramInstances) throws Exception {
    this.m_numberOfClusters = -1;
    this.m_cobwebTree = null;
    this.m_numberSplits = 0;
    this.m_numberMerges = 0;
    if (paramInstances.checkForStringAttributes())
      throw new Exception("Can't handle string attributes!"); 
    paramInstances = new Instances(paramInstances);
    paramInstances.randomize(new Random(42L));
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      addInstance(paramInstances.instance(b)); 
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    this.m_cobwebTree.assignClusterNums(arrayOfInt);
    this.m_numberOfClusters = arrayOfInt[0];
  }
  
  public int clusterInstance(Instance paramInstance) throws Exception {
    CNode cNode1 = this.m_cobwebTree;
    CNode cNode2 = null;
    do {
      if (cNode1.m_children == null) {
        cNode2 = null;
        break;
      } 
      cNode1.updateStats(paramInstance, false);
      cNode2 = cNode1.findHost(paramInstance, true);
      cNode1.updateStats(paramInstance, true);
      if (cNode2 == null)
        continue; 
      cNode1 = cNode2;
    } while (cNode2 != null);
    return cNode1.m_clusterNum;
  }
  
  public int numberOfClusters() throws Exception {
    return this.m_numberOfClusters;
  }
  
  public void addInstance(Instance paramInstance) throws Exception {
    if (this.m_cobwebTree == null) {
      this.m_cobwebTree = new CNode(paramInstance.numAttributes(), paramInstance);
    } else {
      this.m_cobwebTree.addInstance(paramInstance);
    } 
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tAcuity.\n\t(default=1.0)", "A", 1, "-A <acuity>"));
    vector.addElement(new Option("\tCutoff.\na\t(default=0.002)", "C", 1, "-C <cutoff>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('A', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = new Double(str);
      setAcuity(double_.doubleValue());
    } else {
      this.m_acuity = 1.0D;
    } 
    str = Utils.getOption('C', paramArrayOfString);
    if (str.length() != 0) {
      Double double_ = new Double(str);
      setCutoff(double_.doubleValue());
    } else {
      this.m_cutoff = 0.01D * m_normal;
    } 
  }
  
  public String acuityTipText() {
    return "set the minimum standard deviation for numeric attributes";
  }
  
  public void setAcuity(double paramDouble) {
    this.m_acuity = paramDouble;
  }
  
  public double getAcuity() {
    return this.m_acuity;
  }
  
  public String cutoffTipText() {
    return "set the category utility threshold by which to prune nodes";
  }
  
  public void setCutoff(double paramDouble) {
    this.m_cutoff = paramDouble;
  }
  
  public double getCutoff() {
    return this.m_cutoff;
  }
  
  public String saveInstanceDataTipText() {
    return "save instance information for visualization purposes";
  }
  
  public boolean getSaveInstanceData() {
    return this.m_saveInstances;
  }
  
  public void setSaveInstanceData(boolean paramBoolean) {
    this.m_saveInstances = paramBoolean;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[4];
    byte b = 0;
    arrayOfString[b++] = "-A";
    arrayOfString[b++] = "" + this.m_acuity;
    arrayOfString[b++] = "-C";
    arrayOfString[b++] = "" + this.m_cutoff;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_cobwebTree == null)
      return "Cobweb hasn't been built yet!"; 
    this.m_cobwebTree.dumpTree(0, stringBuffer);
    return "Number of merges: " + this.m_numberMerges + "\nNumber of splits: " + this.m_numberSplits + "\nNumber of clusters: " + this.m_numberOfClusters + "\n" + stringBuffer.toString() + "\n\n";
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("digraph CobwebTree {\n");
    this.m_cobwebTree.graphTree(stringBuffer);
    stringBuffer.append("}\n");
    return stringBuffer.toString();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(ClusterEvaluation.evaluateClusterer(new Cobweb(), paramArrayOfString));
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    } 
  }
  
  private class CNode implements Serializable {
    private AttributeStats[] m_attStats;
    
    private int m_numAttributes;
    
    protected Instances m_clusterInstances;
    
    private FastVector m_children;
    
    private double m_totalInstances;
    
    private int m_clusterNum;
    
    private final Cobweb this$0;
    
    public CNode(Cobweb this$0, int param1Int) {
      Cobweb.this = Cobweb.this;
      this.m_clusterInstances = null;
      this.m_children = null;
      this.m_totalInstances = 0.0D;
      this.m_clusterNum = -1;
      this.m_numAttributes = param1Int;
    }
    
    public CNode(int param1Int, Instance param1Instance) {
      this(param1Int);
      if (this.m_clusterInstances == null)
        this.m_clusterInstances = new Instances(param1Instance.dataset(), 1); 
      this.m_clusterInstances.add(param1Instance);
      updateStats(param1Instance, false);
    }
    
    protected void addInstance(Instance param1Instance) throws Exception {
      if (this.m_clusterInstances == null) {
        this.m_clusterInstances = new Instances(param1Instance.dataset(), 1);
        this.m_clusterInstances.add(param1Instance);
        updateStats(param1Instance, false);
        return;
      } 
      if (this.m_children == null) {
        this.m_children = new FastVector();
        CNode cNode1 = new CNode(this.m_numAttributes, this.m_clusterInstances.instance(0));
        for (byte b = 1; b < this.m_clusterInstances.numInstances(); b++) {
          cNode1.m_clusterInstances.add(this.m_clusterInstances.instance(b));
          cNode1.updateStats(this.m_clusterInstances.instance(b), false);
        } 
        this.m_children = new FastVector();
        this.m_children.addElement(cNode1);
        this.m_children.addElement(new CNode(this.m_numAttributes, param1Instance));
        this.m_clusterInstances.add(param1Instance);
        updateStats(param1Instance, false);
        if (categoryUtility() < Cobweb.this.m_cutoff)
          this.m_children = null; 
        return;
      } 
      CNode cNode = findHost(param1Instance, false);
      if (cNode != null)
        cNode.addInstance(param1Instance); 
    }
    
    private double[] cuScoresForChildren(Instance param1Instance) throws Exception {
      double[] arrayOfDouble = new double[this.m_children.size()];
      for (byte b = 0; b < this.m_children.size(); b++) {
        CNode cNode = (CNode)this.m_children.elementAt(b);
        cNode.updateStats(param1Instance, false);
        arrayOfDouble[b] = categoryUtility();
        cNode.updateStats(param1Instance, true);
      } 
      return arrayOfDouble;
    }
    
    private double cuScoreForBestTwoMerged(CNode param1CNode1, CNode param1CNode2, CNode param1CNode3, Instance param1Instance) throws Exception {
      double d = -1.7976931348623157E308D;
      param1CNode1.m_clusterInstances = new Instances(this.m_clusterInstances, 1);
      param1CNode1.addChildNode(param1CNode2);
      param1CNode1.addChildNode(param1CNode3);
      param1CNode1.updateStats(param1Instance, false);
      this.m_children.removeElementAt(this.m_children.indexOf(param1CNode2));
      this.m_children.removeElementAt(this.m_children.indexOf(param1CNode3));
      this.m_children.addElement(param1CNode1);
      d = categoryUtility();
      param1CNode1.updateStats(param1Instance, true);
      this.m_children.removeElementAt(this.m_children.indexOf(param1CNode1));
      this.m_children.addElement(param1CNode2);
      this.m_children.addElement(param1CNode3);
      return d;
    }
    
    private CNode findHost(Instance param1Instance, boolean param1Boolean) throws Exception {
      if (!param1Boolean)
        updateStats(param1Instance, false); 
      double[] arrayOfDouble = cuScoresForChildren(param1Instance);
      CNode cNode1 = new CNode(this.m_numAttributes, param1Instance);
      this.m_children.addElement(cNode1);
      double d1 = categoryUtility();
      CNode cNode2 = cNode1;
      this.m_children.removeElementAt(this.m_children.size() - 1);
      byte b1 = 0;
      byte b2 = 0;
      for (byte b3 = 0; b3 < arrayOfDouble.length; b3++) {
        if (arrayOfDouble[b3] > arrayOfDouble[b2])
          if (arrayOfDouble[b3] > arrayOfDouble[b1]) {
            b2 = b1;
            b1 = b3;
          } else {
            b2 = b3;
          }  
      } 
      CNode cNode3 = (CNode)this.m_children.elementAt(b1);
      CNode cNode4 = (CNode)this.m_children.elementAt(b2);
      if (arrayOfDouble[b1] > d1) {
        d1 = arrayOfDouble[b1];
        cNode2 = cNode3;
      } 
      if (param1Boolean)
        return (cNode2 == cNode1) ? null : cNode2; 
      double d2 = -1.7976931348623157E308D;
      CNode cNode5 = new CNode(this.m_numAttributes);
      if (cNode3 != cNode4) {
        d2 = cuScoreForBestTwoMerged(cNode5, cNode3, cNode4, param1Instance);
        if (d2 > d1) {
          d1 = d2;
          cNode2 = cNode5;
        } 
      } 
      double d3 = -1.7976931348623157E308D;
      double d4 = -1.7976931348623157E308D;
      double d5 = -1.7976931348623157E308D;
      double d6 = -1.7976931348623157E308D;
      if (cNode3.m_children != null) {
        FastVector fastVector1 = new FastVector();
        byte b4;
        for (b4 = 0; b4 < this.m_children.size(); b4++) {
          CNode cNode = (CNode)this.m_children.elementAt(b4);
          if (cNode != cNode3)
            fastVector1.addElement(cNode); 
        } 
        for (b4 = 0; b4 < cNode3.m_children.size(); b4++) {
          CNode cNode = (CNode)cNode3.m_children.elementAt(b4);
          fastVector1.addElement(cNode);
        } 
        fastVector1.addElement(cNode1);
        FastVector fastVector2 = this.m_children;
        this.m_children = fastVector1;
        d5 = categoryUtility();
        fastVector1.removeElementAt(fastVector1.size() - 1);
        arrayOfDouble = cuScoresForChildren(param1Instance);
        b1 = 0;
        b2 = 0;
        for (byte b5 = 0; b5 < arrayOfDouble.length; b5++) {
          if (arrayOfDouble[b5] > arrayOfDouble[b2])
            if (arrayOfDouble[b5] > arrayOfDouble[b1]) {
              b2 = b1;
              b1 = b5;
            } else {
              b2 = b5;
            }  
        } 
        CNode cNode6 = (CNode)this.m_children.elementAt(b1);
        CNode cNode7 = (CNode)this.m_children.elementAt(b2);
        d4 = arrayOfDouble[b1];
        CNode cNode8 = new CNode(this.m_numAttributes);
        if (cNode6 != cNode7)
          d6 = cuScoreForBestTwoMerged(cNode8, cNode6, cNode7, param1Instance); 
        d3 = (d4 > d5) ? d4 : d5;
        d3 = (d3 > d6) ? d3 : d6;
        if (d3 > d1) {
          d1 = d3;
          cNode2 = this;
        } else {
          this.m_children = fastVector2;
        } 
      } 
      if (cNode2 != this) {
        this.m_clusterInstances.add(param1Instance);
      } else {
        Cobweb.this.m_numberSplits++;
      } 
      if (cNode2 == cNode5) {
        Cobweb.this.m_numberMerges++;
        this.m_children.removeElementAt(this.m_children.indexOf(cNode3));
        this.m_children.removeElementAt(this.m_children.indexOf(cNode4));
        this.m_children.addElement(cNode5);
      } 
      if (cNode2 == cNode1) {
        cNode2 = new CNode(this.m_numAttributes);
        this.m_children.addElement(cNode2);
      } 
      if (d1 < Cobweb.this.m_cutoff) {
        if (cNode2 == this)
          this.m_clusterInstances.add(param1Instance); 
        this.m_children = null;
        cNode2 = null;
      } 
      if (cNode2 == this)
        updateStats(param1Instance, true); 
      return cNode2;
    }
    
    protected void addChildNode(CNode param1CNode) {
      for (byte b = 0; b < param1CNode.m_clusterInstances.numInstances(); b++) {
        Instance instance = param1CNode.m_clusterInstances.instance(b);
        this.m_clusterInstances.add(instance);
        updateStats(instance, false);
      } 
      if (this.m_children == null)
        this.m_children = new FastVector(); 
      this.m_children.addElement(param1CNode);
    }
    
    protected double categoryUtility() throws Exception {
      if (this.m_children == null)
        throw new Exception("categoryUtility: No children!"); 
      double d = 0.0D;
      for (byte b = 0; b < this.m_children.size(); b++) {
        CNode cNode = (CNode)this.m_children.elementAt(b);
        d += categoryUtilityChild(cNode);
      } 
      d /= this.m_children.size();
      return d;
    }
    
    protected double categoryUtilityChild(CNode param1CNode) throws Exception {
      double d = 0.0D;
      for (byte b = 0; b < this.m_numAttributes; b++) {
        if (this.m_clusterInstances.attribute(b).isNominal()) {
          for (byte b1 = 0; b1 < this.m_clusterInstances.attribute(b).numValues(); b1++) {
            double d1 = param1CNode.getProbability(b, b1);
            double d2 = getProbability(b, b1);
            d += d1 * d1 - d2 * d2;
          } 
        } else {
          d += Cobweb.m_normal / param1CNode.getStandardDev(b) - Cobweb.m_normal / getStandardDev(b);
        } 
      } 
      return param1CNode.m_totalInstances / this.m_totalInstances * d;
    }
    
    protected double getProbability(int param1Int1, int param1Int2) throws Exception {
      if (!this.m_clusterInstances.attribute(param1Int1).isNominal())
        throw new Exception("getProbability: attribute is not nominal"); 
      return ((this.m_attStats[param1Int1]).totalCount <= 0) ? 0.0D : ((this.m_attStats[param1Int1]).nominalCounts[param1Int2] / (this.m_attStats[param1Int1]).totalCount);
    }
    
    protected double getStandardDev(int param1Int) throws Exception {
      if (!this.m_clusterInstances.attribute(param1Int).isNumeric())
        throw new Exception("getStandardDev: attribute is not numeric"); 
      (this.m_attStats[param1Int]).numericStats.calculateDerived();
      double d = (this.m_attStats[param1Int]).numericStats.stdDev;
      return (Double.isNaN(d) || Double.isInfinite(d)) ? Cobweb.this.m_acuity : Math.max(Cobweb.this.m_acuity, d);
    }
    
    protected void updateStats(Instance param1Instance, boolean param1Boolean) {
      if (this.m_attStats == null) {
        this.m_attStats = new AttributeStats[this.m_numAttributes];
        for (byte b1 = 0; b1 < this.m_numAttributes; b1++) {
          this.m_attStats[b1] = new AttributeStats();
          if (this.m_clusterInstances.attribute(b1).isNominal()) {
            (this.m_attStats[b1]).nominalCounts = new int[this.m_clusterInstances.attribute(b1).numValues()];
          } else {
            (this.m_attStats[b1]).numericStats = new Stats();
          } 
        } 
      } 
      for (byte b = 0; b < this.m_numAttributes; b++) {
        if (!param1Instance.isMissing(b)) {
          double d = param1Instance.value(b);
          if (this.m_clusterInstances.attribute(b).isNominal()) {
            (this.m_attStats[b]).nominalCounts[(int)d] = (int)((this.m_attStats[b]).nominalCounts[(int)d] + (param1Boolean ? (-1.0D * param1Instance.weight()) : param1Instance.weight()));
            (this.m_attStats[b]).totalCount = (int)((this.m_attStats[b]).totalCount + (param1Boolean ? (-1.0D * param1Instance.weight()) : param1Instance.weight()));
          } else if (param1Boolean) {
            (this.m_attStats[b]).numericStats.subtract(d, param1Instance.weight());
          } else {
            (this.m_attStats[b]).numericStats.add(d, param1Instance.weight());
          } 
        } 
      } 
      this.m_totalInstances += param1Boolean ? (-1.0D * param1Instance.weight()) : param1Instance.weight();
    }
    
    private void assignClusterNums(int[] param1ArrayOfint) throws Exception {
      if (this.m_children != null && this.m_children.size() < 2)
        throw new Exception("assignClusterNums: tree not built correctly!"); 
      this.m_clusterNum = param1ArrayOfint[0];
      param1ArrayOfint[0] = param1ArrayOfint[0] + 1;
      if (this.m_children != null)
        for (byte b = 0; b < this.m_children.size(); b++) {
          CNode cNode = (CNode)this.m_children.elementAt(b);
          cNode.assignClusterNums(param1ArrayOfint);
        }  
    }
    
    protected void dumpTree(int param1Int, StringBuffer param1StringBuffer) {
      if (this.m_children == null) {
        param1StringBuffer.append("\n");
        for (byte b = 0; b < param1Int; b++)
          param1StringBuffer.append("|   "); 
        param1StringBuffer.append("leaf " + this.m_clusterNum + " [" + this.m_clusterInstances.numInstances() + "]");
      } else {
        for (byte b = 0; b < this.m_children.size(); b++) {
          param1StringBuffer.append("\n");
          for (byte b1 = 0; b1 < param1Int; b1++)
            param1StringBuffer.append("|   "); 
          param1StringBuffer.append("node " + this.m_clusterNum + " [" + this.m_clusterInstances.numInstances() + "]");
          ((CNode)this.m_children.elementAt(b)).dumpTree(param1Int + 1, param1StringBuffer);
        } 
      } 
    }
    
    protected String dumpData() throws Exception {
      if (this.m_children == null)
        return this.m_clusterInstances.toString(); 
      CNode cNode = new CNode(this.m_numAttributes);
      cNode.m_clusterInstances = new Instances(this.m_clusterInstances, 1);
      for (byte b1 = 0; b1 < this.m_children.size(); b1++)
        cNode.addChildNode((CNode)this.m_children.elementAt(b1)); 
      Instances instances = cNode.m_clusterInstances;
      cNode = null;
      StringBuffer stringBuffer = new StringBuffer();
      Add add = new Add();
      add.setAttributeName("Cluster");
      String str = "";
      byte b2;
      for (b2 = 0; b2 < this.m_children.size(); b2++) {
        CNode cNode1 = (CNode)this.m_children.elementAt(b2);
        str = str + "C" + cNode1.m_clusterNum;
        if (b2 < this.m_children.size() - 1)
          str = str + ","; 
      } 
      add.setNominalLabels(str);
      add.setInputFormat(instances);
      instances = Filter.useFilter(instances, (Filter)add);
      instances.setRelationName("Cluster " + this.m_clusterNum);
      b2 = 0;
      for (byte b3 = 0; b3 < this.m_children.size(); b3++) {
        CNode cNode1 = (CNode)this.m_children.elementAt(b3);
        for (byte b = 0; b < cNode1.m_clusterInstances.numInstances(); b++) {
          instances.instance(b2).setValue(this.m_numAttributes, b3);
          b2++;
        } 
      } 
      return instances.toString();
    }
    
    protected void graphTree(StringBuffer param1StringBuffer) throws Exception {
      param1StringBuffer.append("N" + this.m_clusterNum + " [label=\"" + ((this.m_children == null) ? "leaf " : "node ") + this.m_clusterNum + " " + " (" + this.m_clusterInstances.numInstances() + ")\" " + ((this.m_children == null) ? "shape=box style=filled " : "") + (Cobweb.this.m_saveInstances ? ("data =\n" + dumpData() + "\n,\n") : "") + "]\n");
      if (this.m_children != null) {
        byte b;
        for (b = 0; b < this.m_children.size(); b++) {
          CNode cNode = (CNode)this.m_children.elementAt(b);
          param1StringBuffer.append("N" + this.m_clusterNum + "->" + "N" + cNode.m_clusterNum + "\n");
        } 
        for (b = 0; b < this.m_children.size(); b++) {
          CNode cNode = (CNode)this.m_children.elementAt(b);
          cNode.graphTree(param1StringBuffer);
        } 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\clusterers\Cobweb.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */