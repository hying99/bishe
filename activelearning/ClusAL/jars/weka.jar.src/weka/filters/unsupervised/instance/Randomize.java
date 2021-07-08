package weka.filters.unsupervised.instance;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.UnsupervisedFilter;

public class Randomize extends Filter implements UnsupervisedFilter, OptionHandler {
  protected int m_Seed = 42;
  
  protected Random m_Random;
  
  public String globalInfo() {
    return "Randomly shuffles the order of instances passed through it. The random number generator is reset with the seed value whenever a new set of instances is passed in.";
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(1);
    vector.addElement(new Option("\tSpecify the random number seed (default 42)", "S", 1, "-S <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0) {
      setRandomSeed(Integer.parseInt(str));
    } else {
      setRandomSeed(42);
    } 
    if (getInputFormat() != null)
      setInputFormat(getInputFormat()); 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[2];
    byte b = 0;
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getRandomSeed();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String randomSeedTipText() {
    return "Seed for the random number generator.";
  }
  
  public int getRandomSeed() {
    return this.m_Seed;
  }
  
  public void setRandomSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    super.setInputFormat(paramInstances);
    setOutputFormat(paramInstances);
    this.m_Random = new Random(this.m_Seed);
    return true;
  }
  
  public boolean batchFinished() {
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    getInputFormat().randomize(this.m_Random);
    for (byte b = 0; b < getInputFormat().numInstances(); b++)
      push(getInputFormat().instance(b)); 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new Randomize(), paramArrayOfString);
      } else {
        Filter.filterFile(new Randomize(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\instance\Randomize.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */