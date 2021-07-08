package weka.filters.supervised.instance;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class StratifiedRemoveFolds extends Filter implements SupervisedFilter, OptionHandler {
  private boolean m_Inverse = false;
  
  private int m_NumFolds = 10;
  
  private int m_Fold = 1;
  
  private long m_Seed = 0L;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tSpecifies if inverse of selection is to be output.\n", "V", 0, "-V"));
    vector.addElement(new Option("\tSpecifies number of folds dataset is split into. \n\t(default 10)\n", "N", 1, "-N <number of folds>"));
    vector.addElement(new Option("\tSpecifies which fold is selected. (default 1)\n", "F", 1, "-F <fold>"));
    vector.addElement(new Option("\tSpecifies random number seed. (default 0, no randomizing)\n", "S", 1, "-S <seed>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setInvertSelection(Utils.getFlag('V', paramArrayOfString));
    String str1 = Utils.getOption('N', paramArrayOfString);
    if (str1.length() != 0) {
      setNumFolds(Integer.parseInt(str1));
    } else {
      setNumFolds(10);
    } 
    String str2 = Utils.getOption('F', paramArrayOfString);
    if (str2.length() != 0) {
      setFold(Integer.parseInt(str2));
    } else {
      setFold(1);
    } 
    String str3 = Utils.getOption('S', paramArrayOfString);
    if (str3.length() != 0) {
      setSeed(Integer.parseInt(str3));
    } else {
      setSeed(0L);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[8];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSeed();
    if (getInvertSelection())
      arrayOfString[b++] = "-V"; 
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getNumFolds();
    arrayOfString[b++] = "-F";
    arrayOfString[b++] = "" + getFold();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String globalInfo() {
    return "This filter takes a dataset and outputs a specified fold for cross validation. If you do not want the folds to be stratified use the unsupervised version.";
  }
  
  public String invertSelectionTipText() {
    return "Whether to invert the selection.";
  }
  
  public boolean getInvertSelection() {
    return this.m_Inverse;
  }
  
  public void setInvertSelection(boolean paramBoolean) {
    this.m_Inverse = paramBoolean;
  }
  
  public String numFoldsTipText() {
    return "The number of folds to split the dataset into.";
  }
  
  public int getNumFolds() {
    return this.m_NumFolds;
  }
  
  public void setNumFolds(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Number of folds has to be positive or zero."); 
    this.m_NumFolds = paramInt;
  }
  
  public String foldTipText() {
    return "The fold which is selected.";
  }
  
  public int getFold() {
    return this.m_Fold;
  }
  
  public void setFold(int paramInt) {
    if (paramInt < 1)
      throw new IllegalArgumentException("Fold's index has to be greater than 0."); 
    this.m_Fold = paramInt;
  }
  
  public String seedTipText() {
    return "the random number seed for shuffling the dataset. If seed is negative, shuffling will not be performed.";
  }
  
  public long getSeed() {
    return this.m_Seed;
  }
  
  public void setSeed(long paramLong) {
    this.m_Seed = paramLong;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    if (this.m_NumFolds > 0 && this.m_NumFolds < this.m_Fold)
      throw new IllegalArgumentException("Fold has to be smaller or equal to number of folds."); 
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    return true;
  }
  
  public boolean batchFinished() {
    Instances instances;
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_Seed > 0L)
      getInputFormat().randomize(new Random(this.m_Seed)); 
    getInputFormat().stratify(this.m_NumFolds);
    if (!this.m_Inverse) {
      instances = getInputFormat().testCV(this.m_NumFolds, this.m_Fold - 1);
    } else {
      instances = getInputFormat().trainCV(this.m_NumFolds, this.m_Fold - 1);
    } 
    for (byte b = 0; b < instances.numInstances(); b++)
      push(instances.instance(b)); 
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new StratifiedRemoveFolds(), paramArrayOfString);
      } else {
        Filter.filterFile(new StratifiedRemoveFolds(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\supervised\instance\StratifiedRemoveFolds.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */