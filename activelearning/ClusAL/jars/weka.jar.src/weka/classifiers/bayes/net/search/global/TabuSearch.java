package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class TabuSearch extends HillClimber {
  int m_nRuns = 10;
  
  int m_nTabuList = 5;
  
  HillClimber.Operation[] m_oTabuList = null;
  
  protected void search(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_oTabuList = new HillClimber.Operation[this.m_nTabuList];
    int i = 0;
    double d2 = calcScore(paramBayesNet);
    double d1 = d2;
    BayesNet bayesNet = new BayesNet();
    bayesNet.m_Instances = paramInstances;
    bayesNet.initStructure();
    copyParentSets(bayesNet, paramBayesNet);
    for (byte b = 0; b < this.m_nRuns; b++) {
      HillClimber.Operation operation = getOptimalOperation(paramBayesNet, paramInstances);
      performOperation(paramBayesNet, paramInstances, operation);
      if (operation == null)
        throw new Exception("Panic: could not find any step to make. Tabu list too long?"); 
      this.m_oTabuList[i] = operation;
      i = (i + 1) % this.m_nTabuList;
      d2 += operation.m_fScore;
      if (d2 > d1) {
        d1 = d2;
        copyParentSets(bayesNet, paramBayesNet);
      } 
      if (paramBayesNet.getDebug())
        printTabuList(); 
    } 
    copyParentSets(paramBayesNet, bayesNet);
    bayesNet = null;
  }
  
  void copyParentSets(BayesNet paramBayesNet1, BayesNet paramBayesNet2) {
    int i = paramBayesNet2.getNrOfNodes();
    for (byte b = 0; b < i; b++)
      paramBayesNet1.getParentSet(b).copy(paramBayesNet2.getParentSet(b)); 
  }
  
  boolean isNotTabu(HillClimber.Operation paramOperation) {
    for (byte b = 0; b < this.m_nTabuList; b++) {
      if (paramOperation.equals(this.m_oTabuList[b]))
        return false; 
    } 
    return true;
  }
  
  void printTabuList() {
    for (byte b = 0; b < this.m_nTabuList; b++) {
      HillClimber.Operation operation = this.m_oTabuList[b];
      if (operation != null) {
        if (operation.m_nOperation == 0) {
          System.out.print(" +(");
        } else {
          System.out.print(" -(");
        } 
        System.out.print(operation.m_nTail + "->" + operation.m_nHead + ")");
      } 
    } 
    System.out.println();
  }
  
  public int getRuns() {
    return this.m_nRuns;
  }
  
  public void setRuns(int paramInt) {
    this.m_nRuns = paramInt;
  }
  
  public int getTabuList() {
    return this.m_nTabuList;
  }
  
  public void setTabuList(int paramInt) {
    this.m_nTabuList = paramInt;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tTabu list length\n", "L", 1, "-L <integer>"));
    vector.addElement(new Option("\tNumber of runs\n", "U", 1, "-U <integer>"));
    vector.addElement(new Option("\tMaximum number of parents\n", "P", 1, "-P <nr of parents>"));
    vector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('L', paramArrayOfString);
    if (str1.length() != 0)
      setTabuList(Integer.parseInt(str1)); 
    String str2 = Utils.getOption('U', paramArrayOfString);
    if (str2.length() != 0)
      setRuns(Integer.parseInt(str2)); 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[7 + arrayOfString1.length];
    byte b1 = 0;
    arrayOfString2[b1++] = "-L";
    arrayOfString2[b1++] = "" + getTabuList();
    arrayOfString2[b1++] = "-U";
    arrayOfString2[b1++] = "" + getRuns();
    for (byte b2 = 0; b2 < arrayOfString1.length; b2++)
      arrayOfString2[b1++] = arrayOfString1[b2]; 
    while (b1 < arrayOfString2.length)
      arrayOfString2[b1++] = ""; 
    return arrayOfString2;
  }
  
  public String globalInfo() {
    return "This Bayes Network learning algorithm uses tabu search for finding a well scoring Bayes network structure. Tabu search is hill climbing till an optimum is reached. The following step is the least worst possible step. The last X steps are kept in a list and none of the steps in this so called tabu list is considered in taking the next step. The best network found in this traversal is returned.";
  }
  
  public String runsTipText() {
    return "Sets the number of steps to be performed.";
  }
  
  public String tabuListTipText() {
    return "Sets the length of the tabu list.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\global\TabuSearch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */