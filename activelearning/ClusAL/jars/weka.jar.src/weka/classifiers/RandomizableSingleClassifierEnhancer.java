package weka.classifiers;

import java.util.Enumeration;
import java.util.Vector;
import weka.core.Option;
import weka.core.Randomizable;
import weka.core.Utils;

public abstract class RandomizableSingleClassifierEnhancer extends SingleClassifierEnhancer implements Randomizable {
  protected int m_Seed = 1;
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tRandom number seed.\n\t(default 1)", "S", 1, "-S <num>"));
    Enumeration enumeration = super.listOptions();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0) {
      setSeed(Integer.parseInt(str));
    } else {
      setSeed(1);
    } 
    super.setOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString1 = super.getOptions();
    String[] arrayOfString2 = new String[arrayOfString1.length + 2];
    byte b = 0;
    arrayOfString2[b++] = "-S";
    arrayOfString2[b++] = "" + getSeed();
    System.arraycopy(arrayOfString1, 0, arrayOfString2, b, arrayOfString1.length);
    return arrayOfString2;
  }
  
  public String seedTipText() {
    return "The random number seed to be used.";
  }
  
  public void setSeed(int paramInt) {
    this.m_Seed = paramInt;
  }
  
  public int getSeed() {
    return this.m_Seed;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\RandomizableSingleClassifierEnhancer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */