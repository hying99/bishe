package weka.attributeSelection;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;

public class BestFirst extends ASSearch implements OptionHandler, StartSetHandler {
  protected int m_maxStale;
  
  protected int m_searchDirection;
  
  protected static final int SELECTION_BACKWARD = 0;
  
  protected static final int SELECTION_FORWARD = 1;
  
  protected static final int SELECTION_BIDIRECTIONAL = 2;
  
  public static final Tag[] TAGS_SELECTION = new Tag[] { new Tag(0, "Backward"), new Tag(1, "Forward"), new Tag(2, "Bi-directional") };
  
  protected int[] m_starting;
  
  protected Range m_startRange;
  
  protected boolean m_hasClass;
  
  protected int m_classIndex;
  
  protected int m_numAttribs;
  
  protected int m_totalEvals;
  
  protected boolean m_debug;
  
  protected double m_bestMerit;
  
  protected int m_cacheSize;
  
  public String globalInfo() {
    return "BestFirst:\n\nSearches the space of attribute subsets by greedy hillclimbing augmented with a backtracking facility. Setting the number of consecutive non-improving nodes allowed controls the level of backtracking done. Best first may start with the empty set of attributes and search forward, or start with the full set of attributes and search backward, or start at any point and search in both directions (by considering all possible single attribute additions and deletions at a given point).\n";
  }
  
  public BestFirst() {
    resetOptions();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(4);
    vector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.", "P", 1, "-P <start set>"));
    vector.addElement(new Option("\tDirection of search. (default = 1).", "D", 1, "-D <0 = backward | 1 = forward | 2 = bi-directional>"));
    vector.addElement(new Option("\tNumber of non-improving nodes to\n\tconsider before terminating search.", "N", 1, "-N <num>"));
    vector.addElement(new Option("\tSize of lookup cache for evaluated subsets.\n\tExpressed as a multiple of the number of\n\tattributes in the data set. (default = 1)", "S", 1, "-S <num>"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    resetOptions();
    String str = Utils.getOption('P', paramArrayOfString);
    if (str.length() != 0)
      setStartSet(str); 
    str = Utils.getOption('D', paramArrayOfString);
    if (str.length() != 0) {
      setDirection(new SelectedTag(Integer.parseInt(str), TAGS_SELECTION));
    } else {
      setDirection(new SelectedTag(1, TAGS_SELECTION));
    } 
    str = Utils.getOption('N', paramArrayOfString);
    if (str.length() != 0)
      setSearchTermination(Integer.parseInt(str)); 
    str = Utils.getOption('S', paramArrayOfString);
    if (str.length() != 0)
      setLookupCacheSize(Integer.parseInt(str)); 
    this.m_debug = Utils.getFlag('Z', paramArrayOfString);
  }
  
  public void setLookupCacheSize(int paramInt) {
    if (paramInt >= 0)
      this.m_cacheSize = paramInt; 
  }
  
  public int getLookupCacheSize() {
    return this.m_cacheSize;
  }
  
  public String lookupCacheSizeTipText() {
    return "Set the maximum size of the lookup cache of evaluated subsets. This is expressed as a multiplier of the number of attributes in the data set. (default = 1).";
  }
  
  public String startSetTipText() {
    return "Set the start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17.";
  }
  
  public void setStartSet(String paramString) throws Exception {
    this.m_startRange.setRanges(paramString);
  }
  
  public String getStartSet() {
    return this.m_startRange.getRanges();
  }
  
  public String searchTerminationTipText() {
    return "Set the amount of backtracking. Specify the number of ";
  }
  
  public void setSearchTermination(int paramInt) throws Exception {
    if (paramInt < 1)
      throw new Exception("Value of -N must be > 0."); 
    this.m_maxStale = paramInt;
  }
  
  public int getSearchTermination() {
    return this.m_maxStale;
  }
  
  public String directionTipText() {
    return "Set the direction of the search.";
  }
  
  public void setDirection(SelectedTag paramSelectedTag) {
    if (paramSelectedTag.getTags() == TAGS_SELECTION)
      this.m_searchDirection = paramSelectedTag.getSelectedTag().getID(); 
  }
  
  public SelectedTag getDirection() {
    return new SelectedTag(this.m_searchDirection, TAGS_SELECTION);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[6];
    byte b = 0;
    if (!getStartSet().equals("")) {
      arrayOfString[b++] = "-P";
      arrayOfString[b++] = "" + startSetToString();
    } 
    arrayOfString[b++] = "-D";
    arrayOfString[b++] = "" + this.m_searchDirection;
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + this.m_maxStale;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  private String startSetToString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.m_starting == null)
      return getStartSet(); 
    for (byte b = 0; b < this.m_starting.length; b++) {
      boolean bool = false;
      if (!this.m_hasClass || (this.m_hasClass == true && b != this.m_classIndex)) {
        stringBuffer.append(this.m_starting[b] + 1);
        bool = true;
      } 
      if (b == this.m_starting.length - 1) {
        stringBuffer.append("");
      } else if (bool) {
        stringBuffer.append(",");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\tBest first.\n\tStart set: ");
    if (this.m_starting == null) {
      stringBuffer.append("no attributes\n");
    } else {
      stringBuffer.append(startSetToString() + "\n");
    } 
    stringBuffer.append("\tSearch direction: ");
    if (this.m_searchDirection == 0) {
      stringBuffer.append("backward\n");
    } else if (this.m_searchDirection == 1) {
      stringBuffer.append("forward\n");
    } else {
      stringBuffer.append("bi-directional\n");
    } 
    stringBuffer.append("\tStale search after " + this.m_maxStale + " node expansions\n");
    stringBuffer.append("\tTotal number of subsets evaluated: " + this.m_totalEvals + "\n");
    stringBuffer.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
    return stringBuffer.toString();
  }
  
  protected void printGroup(BitSet paramBitSet, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      if (paramBitSet.get(b) == true)
        System.out.print((b + 1) + " "); 
    } 
    System.out.println();
  }
  
  public int[] search(ASEvaluation paramASEvaluation, Instances paramInstances) throws Exception {
    this.m_totalEvals = 0;
    if (!(paramASEvaluation instanceof SubsetEvaluator))
      throw new Exception(paramASEvaluation.getClass().getName() + " is not a " + "Subset evaluator!"); 
    if (paramASEvaluation instanceof UnsupervisedSubsetEvaluator) {
      this.m_hasClass = false;
    } else {
      this.m_hasClass = true;
      this.m_classIndex = paramInstances.classIndex();
    } 
    SubsetEvaluator subsetEvaluator = (SubsetEvaluator)paramASEvaluation;
    this.m_numAttribs = paramInstances.numAttributes();
    int i = 0;
    byte b1 = 0;
    int j = this.m_searchDirection;
    boolean bool1 = false;
    boolean bool2 = true;
    Hashtable hashtable = new Hashtable(this.m_cacheSize * this.m_numAttribs);
    byte b2 = 0;
    byte b3 = 0;
    LinkedList2 linkedList2 = new LinkedList2(this, this.m_maxStale);
    double d = -1.7976931348623157E308D;
    int k = 0;
    BitSet bitSet1 = new BitSet(this.m_numAttribs);
    this.m_startRange.setUpper(this.m_numAttribs - 1);
    if (!getStartSet().equals(""))
      this.m_starting = this.m_startRange.getSelection(); 
    if (this.m_starting != null) {
      for (byte b = 0; b < this.m_starting.length; b++) {
        if (this.m_starting[b] != this.m_classIndex)
          bitSet1.set(this.m_starting[b]); 
      } 
      i = this.m_starting.length;
      this.m_totalEvals++;
    } else if (this.m_searchDirection == 0) {
      setStartSet("1-last");
      this.m_starting = new int[this.m_numAttribs];
      byte b4 = 0;
      byte b5 = 0;
      while (b4 < this.m_numAttribs) {
        if (b4 != this.m_classIndex) {
          bitSet1.set(b4);
          this.m_starting[b5++] = b4;
        } 
        b4++;
      } 
      i = this.m_numAttribs - 1;
      this.m_totalEvals++;
    } 
    d = subsetEvaluator.evaluateSubset(bitSet1);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = bitSet1.clone();
    linkedList2.addToList(arrayOfObject, d);
    BitSet bitSet2 = (BitSet)bitSet1.clone();
    String str = bitSet2.toString();
    hashtable.put(str, "");
    while (k < this.m_maxStale) {
      byte b4;
      boolean bool = false;
      if (this.m_searchDirection == 2) {
        b4 = 2;
        j = 1;
      } else {
        b4 = 1;
      } 
      if (linkedList2.size() == 0) {
        k = this.m_maxStale;
        break;
      } 
      Link2 link2 = linkedList2.getLinkAt(0);
      BitSet bitSet = (BitSet)link2.getData()[0];
      bitSet = (BitSet)bitSet.clone();
      linkedList2.removeLinkAt(0);
      byte b5 = 0;
      b1 = 0;
      while (b5 < this.m_numAttribs) {
        if (bitSet.get(b5))
          b1++; 
        b5++;
      } 
      while (true) {
        for (byte b = 0; b < this.m_numAttribs; b++) {
          boolean bool3;
          if (j == 1) {
            bool3 = (b != this.m_classIndex && !bitSet.get(b)) ? true : false;
          } else {
            bool3 = (b != this.m_classIndex && bitSet.get(b)) ? true : false;
          } 
          if (bool3) {
            if (j == 1) {
              bitSet.set(b);
              b1++;
            } else {
              bitSet.clear(b);
              b1--;
            } 
            bitSet2 = (BitSet)bitSet.clone();
            str = bitSet2.toString();
            if (!hashtable.containsKey(str)) {
              double d1 = subsetEvaluator.evaluateSubset(bitSet);
              this.m_totalEvals++;
              if (this.m_debug) {
                System.out.print("Group: ");
                printGroup(bitSet2, this.m_numAttribs);
                System.out.println("Merit: " + d1);
              } 
              if (j == 1) {
                bool3 = (d1 - d > 1.0E-5D) ? true : false;
              } else if (d1 == d) {
                bool3 = (b1 < i) ? true : false;
              } else {
                bool3 = (d1 > d) ? true : false;
              } 
              if (bool3) {
                bool = true;
                k = 0;
                d = d1;
                i = b1;
                bitSet1 = (BitSet)bitSet.clone();
              } 
              if (b2 > this.m_cacheSize * this.m_numAttribs) {
                hashtable = new Hashtable(this.m_cacheSize * this.m_numAttribs);
                b2 = 0;
              } 
              Object[] arrayOfObject1 = new Object[1];
              arrayOfObject1[0] = bitSet2.clone();
              linkedList2.addToList(arrayOfObject1, d1);
              str = bitSet2.toString();
              hashtable.put(str, "");
              b2++;
            } else {
              b3++;
            } 
            if (j == 1) {
              bitSet.clear(b);
              b1--;
            } else {
              bitSet.set(b);
              b1++;
            } 
          } 
        } 
        if (b4 == 2)
          j = 0; 
        if (--b4 <= 0 && !bool)
          k++; 
      } 
    } 
    this.m_bestMerit = d;
    return attributeList(bitSet1);
  }
  
  protected void resetOptions() {
    this.m_maxStale = 5;
    this.m_searchDirection = 1;
    this.m_starting = null;
    this.m_startRange = new Range();
    this.m_classIndex = -1;
    this.m_totalEvals = 0;
    this.m_cacheSize = 1;
    this.m_debug = false;
  }
  
  protected int[] attributeList(BitSet paramBitSet) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (paramBitSet.get(b2))
        b1++; 
    } 
    int[] arrayOfInt = new int[b1];
    b1 = 0;
    for (byte b3 = 0; b3 < this.m_numAttribs; b3++) {
      if (paramBitSet.get(b3))
        arrayOfInt[b1++] = b3; 
    } 
    return arrayOfInt;
  }
  
  public class LinkedList2 extends FastVector {
    int m_MaxSize;
    
    private final BestFirst this$0;
    
    public LinkedList2(BestFirst this$0, int param1Int) {
      this.this$0 = this$0;
      this.m_MaxSize = param1Int;
    }
    
    public void removeLinkAt(int param1Int) throws Exception {
      if (param1Int >= 0 && param1Int < size()) {
        removeElementAt(param1Int);
      } else {
        throw new Exception("index out of range (removeLinkAt)");
      } 
    }
    
    public BestFirst.Link2 getLinkAt(int param1Int) throws Exception {
      if (size() == 0)
        throw new Exception("List is empty (getLinkAt)"); 
      if (param1Int >= 0 && param1Int < size())
        return (BestFirst.Link2)elementAt(param1Int); 
      throw new Exception("index out of range (getLinkAt)");
    }
    
    public void addToList(Object[] param1ArrayOfObject, double param1Double) throws Exception {
      BestFirst.Link2 link2 = new BestFirst.Link2(this.this$0, param1ArrayOfObject, param1Double);
      if (size() == 0) {
        addElement(link2);
      } else if (param1Double > ((BestFirst.Link2)firstElement()).m_merit) {
        if (size() == this.m_MaxSize)
          removeLinkAt(this.m_MaxSize - 1); 
        insertElementAt(link2, 0);
      } else {
        byte b = 0;
        int i = size();
        boolean bool = false;
        if (i != this.m_MaxSize || param1Double > ((BestFirst.Link2)lastElement()).m_merit)
          while (!bool && b < i) {
            if (param1Double > ((BestFirst.Link2)elementAt(b)).m_merit) {
              if (i == this.m_MaxSize)
                removeLinkAt(this.m_MaxSize - 1); 
              insertElementAt(link2, b);
              bool = true;
              continue;
            } 
            if (b == i - 1) {
              addElement(link2);
              bool = true;
              continue;
            } 
            b++;
          }  
      } 
    }
  }
  
  public class Link2 implements Serializable {
    Object[] m_data;
    
    double m_merit;
    
    private final BestFirst this$0;
    
    public Link2(BestFirst this$0, Object[] param1ArrayOfObject, double param1Double) {
      this.this$0 = this$0;
      this.m_data = param1ArrayOfObject;
      this.m_merit = param1Double;
    }
    
    public Object[] getData() {
      return this.m_data;
    }
    
    public String toString() {
      return "Node: " + this.m_data.toString() + "  " + this.m_merit;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\attributeSelection\BestFirst.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */