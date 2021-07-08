package weka.datagenerators;

import java.io.Serializable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Test implements Serializable {
  int m_AttIndex;
  
  double m_Split;
  
  boolean m_Not;
  
  Instances m_Dataset;
  
  Test(int paramInt, double paramDouble, Instances paramInstances) {
    this.m_AttIndex = paramInt;
    this.m_Split = paramDouble;
    this.m_Dataset = paramInstances;
    this.m_Not = false;
  }
  
  Test(int paramInt, double paramDouble, Instances paramInstances, boolean paramBoolean) {
    this.m_AttIndex = paramInt;
    this.m_Split = paramDouble;
    this.m_Dataset = paramInstances;
    this.m_Not = paramBoolean;
  }
  
  public Test getNot() {
    return new Test(this.m_AttIndex, this.m_Split, this.m_Dataset, !this.m_Not);
  }
  
  public boolean passesTest(Instance paramInstance) throws Exception {
    if (paramInstance.isMissing(this.m_AttIndex))
      return false; 
    boolean bool = paramInstance.attribute(this.m_AttIndex).isNominal();
    double d = paramInstance.value(this.m_AttIndex);
    if (!this.m_Not) {
      if (bool) {
        if ((int)d != (int)this.m_Split)
          return false; 
      } else if (d >= this.m_Split) {
        return false;
      } 
    } else if (bool) {
      if ((int)d == (int)this.m_Split)
        return false; 
    } else if (d < this.m_Split) {
      return false;
    } 
    return true;
  }
  
  public String toString() {
    return this.m_Dataset.attribute(this.m_AttIndex).name() + " " + testComparisonString();
  }
  
  public String toPrologString() {
    Attribute attribute = this.m_Dataset.attribute(this.m_AttIndex);
    StringBuffer stringBuffer = new StringBuffer();
    String str = this.m_Dataset.attribute(this.m_AttIndex).name();
    if (attribute.isNumeric()) {
      stringBuffer = stringBuffer.append(str + " ");
      if (this.m_Not) {
        stringBuffer = stringBuffer.append(">= " + Utils.doubleToString(this.m_Split, 3));
      } else {
        stringBuffer = stringBuffer.append("< " + Utils.doubleToString(this.m_Split, 3));
      } 
    } else {
      String str1 = attribute.value((int)this.m_Split);
      if (str1 == "false") {
        stringBuffer = stringBuffer.append("not(" + str + ")");
      } else {
        stringBuffer = stringBuffer.append(str);
      } 
    } 
    return stringBuffer.toString();
  }
  
  private String testComparisonString() {
    Attribute attribute = this.m_Dataset.attribute(this.m_AttIndex);
    return attribute.isNumeric() ? ((this.m_Not ? ">= " : "< ") + Utils.doubleToString(this.m_Split, 3)) : ((attribute.numValues() != 2) ? ((this.m_Not ? "!= " : "= ") + attribute.value((int)this.m_Split)) : ("= " + (this.m_Not ? attribute.value(((int)this.m_Split == 0) ? 1 : 0) : attribute.value((int)this.m_Split))));
  }
  
  private String testPrologComparisonString() {
    Attribute attribute = this.m_Dataset.attribute(this.m_AttIndex);
    return attribute.isNumeric() ? ((this.m_Not ? ">= " : "< ") + Utils.doubleToString(this.m_Split, 3)) : ((attribute.numValues() != 2) ? ((this.m_Not ? "!= " : "= ") + attribute.value((int)this.m_Split)) : ("= " + (this.m_Not ? attribute.value(((int)this.m_Split == 0) ? 1 : 0) : attribute.value((int)this.m_Split))));
  }
  
  public boolean equalTo(Test paramTest) {
    return (this.m_AttIndex == paramTest.m_AttIndex && this.m_Split == paramTest.m_Split && this.m_Not == paramTest.m_Not);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\datagenerators\Test.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */