package weka.classifiers.bayes.net.search.local;

import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.Statistics;
import weka.core.Tag;
import weka.core.Utils;

public class LocalScoreSearchAlgorithm extends SearchAlgorithm {
  BayesNet m_BayesNet;
  
  double m_fAlpha = 0.5D;
  
  public static final Tag[] TAGS_SCORE_TYPE = new Tag[] { new Tag(0, "BAYES"), new Tag(1, "BDeu"), new Tag(2, "MDL"), new Tag(3, "ENTROPY"), new Tag(4, "AIC") };
  
  int m_nScoreType = 0;
  
  public LocalScoreSearchAlgorithm() {}
  
  public LocalScoreSearchAlgorithm(BayesNet paramBayesNet, Instances paramInstances) {
    this.m_BayesNet = paramBayesNet;
  }
  
  public double logScore(int paramInt) {
    if (paramInt < 0)
      paramInt = this.m_nScoreType; 
    double d = 0.0D;
    Instances instances = this.m_BayesNet.m_Instances;
    for (byte b = 0; b < instances.numAttributes(); b++) {
      for (byte b1 = 0; b1 < this.m_BayesNet.getParentSet(b).getCardinalityOfParents(); b1++)
        d += ((Scoreable)this.m_BayesNet.m_Distributions[b][b1]).logScore(paramInt); 
      switch (paramInt) {
        case 2:
          d -= 0.5D * this.m_BayesNet.getParentSet(b).getCardinalityOfParents() * (instances.attribute(b).numValues() - 1) * Math.log(instances.numInstances());
          break;
        case 4:
          d -= (this.m_BayesNet.getParentSet(b).getCardinalityOfParents() * (instances.attribute(b).numValues() - 1));
          break;
      } 
    } 
    return d;
  }
  
  public void buildStructure(BayesNet paramBayesNet, Instances paramInstances) throws Exception {
    this.m_BayesNet = paramBayesNet;
    super.buildStructure(paramBayesNet, paramInstances);
  }
  
  public double calcNodeScore(int paramInt) {
    return (this.m_BayesNet.getUseADTree() && this.m_BayesNet.getADTree() != null) ? calcNodeScoreADTree(paramInt) : calcNodeScorePlain(paramInt);
  }
  
  private double calcNodeScoreADTree(int paramInt) {
    Instances instances = this.m_BayesNet.m_Instances;
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt);
    int i = parentSet.getNrOfParents();
    int[] arrayOfInt1 = new int[i + 1];
    for (byte b = 0; b < i; b++)
      arrayOfInt1[b] = parentSet.getParent(b); 
    arrayOfInt1[i] = paramInt;
    int[] arrayOfInt2 = new int[i + 1];
    int j = 1;
    arrayOfInt2[i] = 1;
    j *= instances.attribute(paramInt).numValues();
    int k;
    for (k = i - 1; k >= 0; k--) {
      arrayOfInt2[k] = j;
      j *= instances.attribute(arrayOfInt1[k]).numValues();
    } 
    for (k = 1; k < arrayOfInt1.length; k++) {
      for (int n = k; n > 0 && arrayOfInt1[n] < arrayOfInt1[n - 1]; n--) {
        int i1 = arrayOfInt1[n];
        arrayOfInt1[n] = arrayOfInt1[n - 1];
        arrayOfInt1[n - 1] = i1;
        i1 = arrayOfInt2[n];
        arrayOfInt2[n] = arrayOfInt2[n - 1];
        arrayOfInt2[n - 1] = i1;
      } 
    } 
    k = parentSet.getCardinalityOfParents();
    int m = instances.attribute(paramInt).numValues();
    int[] arrayOfInt3 = new int[k * m];
    this.m_BayesNet.getADTree().getCounts(arrayOfInt3, arrayOfInt1, arrayOfInt2, 0, 0, false);
    return calcScoreOfCounts(arrayOfInt3, k, m, instances);
  }
  
  private double calcNodeScorePlain(int paramInt) {
    Instances instances = this.m_BayesNet.m_Instances;
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt);
    int i = parentSet.getCardinalityOfParents();
    int j = instances.attribute(paramInt).numValues();
    int[] arrayOfInt = new int[i * j];
    for (byte b = 0; b < i * j; b++)
      arrayOfInt[b] = 0; 
    Enumeration enumeration = instances.enumerateInstances();
    while (enumeration.hasMoreElements()) {
      Instance instance = enumeration.nextElement();
      double d = 0.0D;
      for (byte b1 = 0; b1 < parentSet.getNrOfParents(); b1++) {
        int k = parentSet.getParent(b1);
        d = d * instances.attribute(k).numValues() + instance.value(k);
      } 
      arrayOfInt[j * (int)d + (int)instance.value(paramInt)] = arrayOfInt[j * (int)d + (int)instance.value(paramInt)] + 1;
    } 
    return calcScoreOfCounts(arrayOfInt, i, j, instances);
  }
  
  protected double calcScoreOfCounts(int[] paramArrayOfint, int paramInt1, int paramInt2, Instances paramInstances) {
    double d = 0.0D;
    for (byte b = 0; b < paramInt1; b++) {
      double d1;
      byte b1;
      switch (this.m_nScoreType) {
        case 0:
          d1 = 0.0D;
          for (b1 = 0; b1 < paramInt2; b1++) {
            if (this.m_fAlpha + paramArrayOfint[b * paramInt2 + b1] != 0.0D) {
              d += Statistics.lnGamma(this.m_fAlpha + paramArrayOfint[b * paramInt2 + b1]);
              d1 += this.m_fAlpha + paramArrayOfint[b * paramInt2 + b1];
            } 
          } 
          if (d1 != 0.0D)
            d -= Statistics.lnGamma(d1); 
          if (this.m_fAlpha != 0.0D) {
            d -= paramInt2 * Statistics.lnGamma(this.m_fAlpha);
            d += Statistics.lnGamma(paramInt2 * this.m_fAlpha);
          } 
          break;
        case 1:
          d1 = 0.0D;
          for (b1 = 0; b1 < paramInt2; b1++) {
            if (this.m_fAlpha + paramArrayOfint[b * paramInt2 + b1] != 0.0D) {
              d += Statistics.lnGamma(1.0D / (paramInt2 * paramInt1) + paramArrayOfint[b * paramInt2 + b1]);
              d1 += 1.0D / (paramInt2 * paramInt1) + paramArrayOfint[b * paramInt2 + b1];
            } 
          } 
          d -= Statistics.lnGamma(d1);
          d -= paramInt2 * Statistics.lnGamma(1.0D);
          d += Statistics.lnGamma(paramInt2 * 1.0D);
          break;
        case 2:
        case 3:
        case 4:
          d1 = 0.0D;
          for (b1 = 0; b1 < paramInt2; b1++)
            d1 += paramArrayOfint[b * paramInt2 + b1]; 
          for (b1 = 0; b1 < paramInt2; b1++) {
            if (paramArrayOfint[b * paramInt2 + b1] > 0)
              d += paramArrayOfint[b * paramInt2 + b1] * Math.log(paramArrayOfint[b * paramInt2 + b1] / d1); 
          } 
          break;
      } 
    } 
    switch (this.m_nScoreType) {
      case 2:
        d -= 0.5D * paramInt1 * (paramInt2 - 1) * Math.log(paramInstances.numInstances());
        break;
      case 4:
        d -= (paramInt1 * (paramInt2 - 1));
        break;
    } 
    return d;
  }
  
  protected double calcScoreOfCounts2(int[][] paramArrayOfint, int paramInt1, int paramInt2, Instances paramInstances) {
    double d = 0.0D;
    for (byte b = 0; b < paramInt1; b++) {
      double d1;
      byte b1;
      switch (this.m_nScoreType) {
        case 0:
          d1 = 0.0D;
          for (b1 = 0; b1 < paramInt2; b1++) {
            if (this.m_fAlpha + paramArrayOfint[b][b1] != 0.0D) {
              d += Statistics.lnGamma(this.m_fAlpha + paramArrayOfint[b][b1]);
              d1 += this.m_fAlpha + paramArrayOfint[b][b1];
            } 
          } 
          if (d1 != 0.0D)
            d -= Statistics.lnGamma(d1); 
          if (this.m_fAlpha != 0.0D) {
            d -= paramInt2 * Statistics.lnGamma(this.m_fAlpha);
            d += Statistics.lnGamma(paramInt2 * this.m_fAlpha);
          } 
          break;
        case 1:
          d1 = 0.0D;
          for (b1 = 0; b1 < paramInt2; b1++) {
            if (this.m_fAlpha + paramArrayOfint[b * paramInt2][b1] != 0.0D) {
              d += Statistics.lnGamma(1.0D / (paramInt2 * paramInt1) + paramArrayOfint[b * paramInt2][b1]);
              d1 += 1.0D / (paramInt2 * paramInt1) + paramArrayOfint[b * paramInt2][b1];
            } 
          } 
          d -= Statistics.lnGamma(d1);
          d -= paramInt2 * Statistics.lnGamma(1.0D);
          d += Statistics.lnGamma(paramInt2 * 1.0D);
          break;
        case 2:
        case 3:
        case 4:
          d1 = 0.0D;
          for (b1 = 0; b1 < paramInt2; b1++)
            d1 += paramArrayOfint[b][b1]; 
          for (b1 = 0; b1 < paramInt2; b1++) {
            if (paramArrayOfint[b][b1] > 0)
              d += paramArrayOfint[b][b1] * Math.log(paramArrayOfint[b][b1] / d1); 
          } 
          break;
      } 
    } 
    switch (this.m_nScoreType) {
      case 2:
        d -= 0.5D * paramInt1 * (paramInt2 - 1) * Math.log(paramInstances.numInstances());
        break;
      case 4:
        d -= (paramInt1 * (paramInt2 - 1));
        break;
    } 
    return d;
  }
  
  public double calcScoreWithExtraParent(int paramInt1, int paramInt2) {
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt1);
    if (parentSet.contains(paramInt2))
      return -1.0E100D; 
    parentSet.addParent(paramInt2, this.m_BayesNet.m_Instances);
    int i = parentSet.getCardinalityOfParents();
    int j = this.m_BayesNet.m_Instances.attribute(paramInt1).numValues();
    int[][] arrayOfInt = new int[i][j];
    double d = calcNodeScore(paramInt1);
    parentSet.deleteLastParent(this.m_BayesNet.m_Instances);
    return d;
  }
  
  public double calcScoreWithMissingParent(int paramInt1, int paramInt2) {
    ParentSet parentSet = this.m_BayesNet.getParentSet(paramInt1);
    if (!parentSet.contains(paramInt2))
      return -1.0E100D; 
    int i = parentSet.deleteParent(paramInt2, this.m_BayesNet.m_Instances);
    int j = parentSet.getCardinalityOfParents();
    int k = this.m_BayesNet.m_Instances.attribute(paramInt1).numValues();
    int[][] arrayOfInt = new int[j][k];
    double d = calcNodeScore(paramInt1);
    parentSet.addParent(paramInt2, i, this.m_BayesNet.m_Instances);
    return d;
  }
  
  public void setScoreType(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_SCORE_TYPE)
      this.m_nScoreType = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getScoreType() {
    return new SelectedTag(this.m_nScoreType, TAGS_SCORE_TYPE);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tScore type (BAYES, BDeu, MDL, ENTROPY and AIC)\n", "S", 1, "-S [BAYES|MDL|ENTROPY|AIC|CROSS_CLASSIC|CROSS_BAYES]"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.compareTo("BAYES") == 0)
      setScoreType(new SelectedTag(0, TAGS_SCORE_TYPE)); 
    if (str.compareTo("BDeu") == 0)
      setScoreType(new SelectedTag(1, TAGS_SCORE_TYPE)); 
    if (str.compareTo("MDL") == 0)
      setScoreType(new SelectedTag(2, TAGS_SCORE_TYPE)); 
    if (str.compareTo("ENTROPY") == 0)
      setScoreType(new SelectedTag(3, TAGS_SCORE_TYPE)); 
    if (str.compareTo("AIC") == 0)
      setScoreType(new SelectedTag(4, TAGS_SCORE_TYPE)); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-S";
    switch (this.m_nScoreType) {
      case 0:
        arrayOfString[b++] = "BAYES";
        break;
      case 1:
        arrayOfString[b++] = "BDeu";
        break;
      case 2:
        arrayOfString[b++] = "MDL";
        break;
      case 3:
        arrayOfString[b++] = "ENTROPY";
        break;
      case 4:
        arrayOfString[b++] = "AIC";
        break;
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String scoreTypeTipText() {
    return "The score type determines the measure used to judge the quality of a network structure. It can be one of Bayes, BDeu, Minimum Description Length (MDL), Akaike Information Criterion (AIC), and Entropy.";
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\search\local\LocalScoreSearchAlgorithm.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */