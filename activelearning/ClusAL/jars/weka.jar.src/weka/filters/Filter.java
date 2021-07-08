package weka.filters;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Queue;
import weka.core.Utils;

public abstract class Filter implements Serializable {
  private boolean m_Debug = false;
  
  private Instances m_OutputFormat = null;
  
  private Queue m_OutputQueue = null;
  
  private int[] m_OutputStringAtts = null;
  
  private int[] m_InputStringAtts = null;
  
  private Instances m_InputFormat = null;
  
  protected boolean m_NewBatch = true;
  
  protected void setOutputFormat(Instances paramInstances) {
    if (paramInstances != null) {
      this.m_OutputFormat = paramInstances.stringFreeStructure();
      this.m_OutputStringAtts = getStringIndices(this.m_OutputFormat);
      String str = paramInstances.relationName() + "-" + getClass().getName();
      if (this instanceof OptionHandler) {
        String[] arrayOfString = ((OptionHandler)this).getOptions();
        for (byte b = 0; b < arrayOfString.length; b++)
          str = str + arrayOfString[b].trim(); 
      } 
      this.m_OutputFormat.setRelationName(str);
    } else {
      this.m_OutputFormat = null;
    } 
    this.m_OutputQueue = new Queue();
  }
  
  protected Instances getInputFormat() {
    return this.m_InputFormat;
  }
  
  protected Instances inputFormatPeek() {
    return this.m_InputFormat;
  }
  
  protected Instances outputFormatPeek() {
    return this.m_OutputFormat;
  }
  
  protected void push(Instance paramInstance) {
    if (paramInstance != null) {
      copyStringValues(paramInstance, this.m_OutputFormat, this.m_OutputStringAtts);
      paramInstance.setDataset(this.m_OutputFormat);
      this.m_OutputQueue.push(paramInstance);
    } 
  }
  
  protected void resetQueue() {
    this.m_OutputQueue = new Queue();
  }
  
  protected void bufferInput(Instance paramInstance) {
    if (paramInstance != null) {
      copyStringValues(paramInstance, this.m_InputFormat, this.m_InputStringAtts);
      this.m_InputFormat.add(paramInstance);
    } 
  }
  
  protected int[] getInputStringIndex() {
    return this.m_InputStringAtts;
  }
  
  protected int[] getOutputStringIndex() {
    return this.m_OutputStringAtts;
  }
  
  private void copyStringValues(Instance paramInstance, Instances paramInstances, int[] paramArrayOfint) {
    if (paramArrayOfint.length == 0)
      return; 
    if (paramInstance.dataset() == null)
      throw new IllegalArgumentException("Instance has no dataset assigned!!"); 
    if (paramInstance.dataset().numAttributes() != paramInstances.numAttributes())
      throw new IllegalArgumentException("Src and Dest differ in # of attributes!!"); 
    copyStringValues(paramInstance, true, paramInstance.dataset(), paramArrayOfint, paramInstances, paramArrayOfint);
  }
  
  protected void copyStringValues(Instance paramInstance, boolean paramBoolean, Instances paramInstances1, Instances paramInstances2) {
    copyStringValues(paramInstance, paramBoolean, paramInstances1, this.m_InputStringAtts, paramInstances2, this.m_OutputStringAtts);
  }
  
  protected void copyStringValues(Instance paramInstance, boolean paramBoolean, Instances paramInstances1, int[] paramArrayOfint1, Instances paramInstances2, int[] paramArrayOfint2) {
    if (paramInstances1 == paramInstances2)
      return; 
    if (paramArrayOfint1.length != paramArrayOfint2.length)
      throw new IllegalArgumentException("Src and Dest string indices differ in length!!"); 
    for (byte b = 0; b < paramArrayOfint1.length; b++) {
      int i = paramBoolean ? paramArrayOfint1[b] : paramArrayOfint2[b];
      Attribute attribute1 = paramInstances1.attribute(paramArrayOfint1[b]);
      Attribute attribute2 = paramInstances2.attribute(paramArrayOfint2[b]);
      if (!paramInstance.isMissing(i)) {
        int j = attribute2.addStringValue(attribute1, (int)paramInstance.value(i));
        paramInstance.setValue(i, j);
      } 
    } 
  }
  
  protected void flushInput() {
    if (this.m_InputStringAtts.length > 0) {
      this.m_InputFormat = this.m_InputFormat.stringFreeStructure();
    } else {
      this.m_InputFormat.delete();
    } 
  }
  
  public boolean inputFormat(Instances paramInstances) throws Exception {
    return setInputFormat(paramInstances);
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    this.m_InputFormat = paramInstances.stringFreeStructure();
    this.m_InputStringAtts = getStringIndices(paramInstances);
    this.m_OutputFormat = null;
    this.m_OutputQueue = new Queue();
    this.m_NewBatch = true;
    return false;
  }
  
  public Instances outputFormat() {
    return getOutputFormat();
  }
  
  public Instances getOutputFormat() {
    if (this.m_OutputFormat == null)
      throw new NullPointerException("No output format defined."); 
    return new Instances(this.m_OutputFormat, 0);
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    if (this.m_InputFormat == null)
      throw new NullPointerException("No input instance format defined"); 
    if (this.m_NewBatch) {
      this.m_OutputQueue = new Queue();
      this.m_NewBatch = false;
    } 
    bufferInput(paramInstance);
    return false;
  }
  
  public boolean batchFinished() throws Exception {
    if (this.m_InputFormat == null)
      throw new NullPointerException("No input instance format defined"); 
    flushInput();
    this.m_NewBatch = true;
    return (numPendingOutput() != 0);
  }
  
  public Instance output() {
    if (this.m_OutputFormat == null)
      throw new NullPointerException("No output instance format defined"); 
    if (this.m_OutputQueue.empty())
      return null; 
    Instance instance = (Instance)this.m_OutputQueue.pop();
    if (this.m_OutputQueue.empty() && this.m_NewBatch && this.m_OutputStringAtts.length > 0)
      this.m_OutputFormat = this.m_OutputFormat.stringFreeStructure(); 
    return instance;
  }
  
  public Instance outputPeek() {
    if (this.m_OutputFormat == null)
      throw new NullPointerException("No output instance format defined"); 
    return this.m_OutputQueue.empty() ? null : (Instance)this.m_OutputQueue.peek();
  }
  
  public int numPendingOutput() {
    if (this.m_OutputFormat == null)
      throw new NullPointerException("No output instance format defined"); 
    return this.m_OutputQueue.size();
  }
  
  public boolean isOutputFormatDefined() {
    return (this.m_OutputFormat != null);
  }
  
  protected int[] getStringIndices(Instances paramInstances) {
    int[] arrayOfInt1 = new int[paramInstances.numAttributes()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInstances.numAttributes(); b2++) {
      if (paramInstances.attribute(b2).type() == 2)
        arrayOfInt1[b1++] = b2; 
    } 
    int[] arrayOfInt2 = new int[b1];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b1);
    return arrayOfInt2;
  }
  
  public static Instances useFilter(Instances paramInstances, Filter paramFilter) throws Exception {
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      paramFilter.input(paramInstances.instance(b)); 
    paramFilter.batchFinished();
    Instances instances = paramFilter.getOutputFormat();
    Instance instance;
    while ((instance = paramFilter.output()) != null)
      instances.add(instance); 
    return instances;
  }
  
  public static void filterFile(Filter paramFilter, String[] paramArrayOfString) throws Exception {
    boolean bool1 = false;
    Instances instances = null;
    BufferedReader bufferedReader = null;
    PrintWriter printWriter = null;
    try {
      boolean bool = Utils.getFlag('h', paramArrayOfString);
      if (Utils.getFlag('d', paramArrayOfString))
        bool1 = true; 
      String str1 = Utils.getOption('i', paramArrayOfString);
      String str2 = Utils.getOption('o', paramArrayOfString);
      String str3 = Utils.getOption('c', paramArrayOfString);
      if (paramFilter instanceof OptionHandler)
        ((OptionHandler)paramFilter).setOptions(paramArrayOfString); 
      Utils.checkForRemainingOptions(paramArrayOfString);
      if (bool)
        throw new Exception("Help requested.\n"); 
      if (str1.length() != 0) {
        bufferedReader = new BufferedReader(new FileReader(str1));
      } else {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      } 
      if (str2.length() != 0) {
        printWriter = new PrintWriter(new FileOutputStream(str2));
      } else {
        printWriter = new PrintWriter(System.out);
      } 
      instances = new Instances(bufferedReader, 1);
      if (str3.length() != 0)
        if (str3.equals("first")) {
          instances.setClassIndex(0);
        } else if (str3.equals("last")) {
          instances.setClassIndex(instances.numAttributes() - 1);
        } else {
          instances.setClassIndex(Integer.parseInt(str3) - 1);
        }  
    } catch (Exception exception) {
      String str1 = "";
      if (paramFilter instanceof OptionHandler) {
        str1 = str1 + "\nFilter options:\n\n";
        Enumeration enumeration = ((OptionHandler)paramFilter).listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str1 = str1 + option.synopsis() + '\n' + option.description() + "\n";
        } 
      } 
      String str2 = "\nGeneral options:\n\n-h\n\tGet help on available options.\n\t(use -b -h for help on batch mode.)\n-i <file>\n\tThe name of the file containing input instances.\n\tIf not supplied then instances will be read from stdin.\n-o <file>\n\tThe name of the file output instances will be written to.\n\tIf not supplied then instances will be written to stdout.\n-c <class index>\n\tThe number of the attribute to use as the class.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then no class is assigned.\n";
      throw new Exception('\n' + exception.getMessage() + str1 + str2);
    } 
    if (bool1)
      System.err.println("Setting input format"); 
    boolean bool2 = false;
    if (paramFilter.setInputFormat(instances)) {
      if (bool1)
        System.err.println("Getting output format"); 
      printWriter.println(paramFilter.getOutputFormat().toString());
      bool2 = true;
    } 
    while (instances.readInstance(bufferedReader)) {
      if (bool1)
        System.err.println("Input instance to filter"); 
      if (paramFilter.input(instances.instance(0))) {
        if (bool1)
          System.err.println("Filter said collect immediately"); 
        if (!bool2)
          throw new Error("Filter didn't return true from setInputFormat() earlier!"); 
        if (bool1)
          System.err.println("Getting output instance"); 
        printWriter.println(paramFilter.output().toString());
      } 
      instances.delete(0);
    } 
    if (bool1)
      System.err.println("Setting end of batch"); 
    if (paramFilter.batchFinished()) {
      if (bool1)
        System.err.println("Filter said collect output"); 
      if (!bool2) {
        if (bool1)
          System.err.println("Getting output format"); 
        printWriter.println(paramFilter.getOutputFormat().toString());
      } 
      if (bool1)
        System.err.println("Getting output instance"); 
      while (paramFilter.numPendingOutput() > 0) {
        printWriter.println(paramFilter.output().toString());
        if (bool1)
          System.err.println("Getting output instance"); 
      } 
    } 
    if (bool1)
      System.err.println("Done"); 
    if (printWriter != null)
      printWriter.close(); 
  }
  
  public static void batchFilterFile(Filter paramFilter, String[] paramArrayOfString) throws Exception {
    Instances instances1 = null;
    Instances instances2 = null;
    BufferedReader bufferedReader1 = null;
    BufferedReader bufferedReader2 = null;
    PrintWriter printWriter1 = null;
    PrintWriter printWriter2 = null;
    try {
      boolean bool1 = Utils.getFlag('h', paramArrayOfString);
      String str1 = Utils.getOption('i', paramArrayOfString);
      if (str1.length() != 0) {
        bufferedReader1 = new BufferedReader(new FileReader(str1));
      } else {
        throw new Exception("No first input file given.\n");
      } 
      str1 = Utils.getOption('r', paramArrayOfString);
      if (str1.length() != 0) {
        bufferedReader2 = new BufferedReader(new FileReader(str1));
      } else {
        throw new Exception("No second input file given.\n");
      } 
      str1 = Utils.getOption('o', paramArrayOfString);
      if (str1.length() != 0) {
        printWriter1 = new PrintWriter(new FileOutputStream(str1));
      } else {
        printWriter1 = new PrintWriter(System.out);
      } 
      str1 = Utils.getOption('s', paramArrayOfString);
      if (str1.length() != 0) {
        printWriter2 = new PrintWriter(new FileOutputStream(str1));
      } else {
        printWriter2 = new PrintWriter(System.out);
      } 
      String str2 = Utils.getOption('c', paramArrayOfString);
      if (paramFilter instanceof OptionHandler)
        ((OptionHandler)paramFilter).setOptions(paramArrayOfString); 
      Utils.checkForRemainingOptions(paramArrayOfString);
      if (bool1)
        throw new Exception("Help requested.\n"); 
      instances1 = new Instances(bufferedReader1, 1);
      instances2 = new Instances(bufferedReader2, 1);
      if (!instances2.equalHeaders(instances1))
        throw new Exception("Input file formats differ.\n"); 
      if (str2.length() != 0)
        if (str2.equals("first")) {
          instances1.setClassIndex(0);
          instances2.setClassIndex(0);
        } else if (str2.equals("last")) {
          instances1.setClassIndex(instances1.numAttributes() - 1);
          instances2.setClassIndex(instances2.numAttributes() - 1);
        } else {
          instances1.setClassIndex(Integer.parseInt(str2) - 1);
          instances2.setClassIndex(Integer.parseInt(str2) - 1);
        }  
    } catch (Exception exception) {
      String str1 = "";
      if (paramFilter instanceof OptionHandler) {
        str1 = str1 + "\nFilter options:\n\n";
        Enumeration enumeration = ((OptionHandler)paramFilter).listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str1 = str1 + option.synopsis() + '\n' + option.description() + "\n";
        } 
      } 
      String str2 = "\nGeneral options:\n\n-h\n\tGet help on available options.\n-i <filename>\n\tThe file containing first input instances.\n-o <filename>\n\tThe file first output instances will be written to.\n-r <filename>\n\tThe file containing second input instances.\n-s <filename>\n\tThe file second output instances will be written to.\n-c <class index>\n\tThe number of the attribute to use as the class.\n\t\"first\" and \"last\" are also valid entries.\n\tIf not supplied then no class is assigned.\n";
      throw new Exception('\n' + exception.getMessage() + str1 + str2);
    } 
    boolean bool = false;
    if (paramFilter.setInputFormat(instances1)) {
      printWriter1.println(paramFilter.getOutputFormat().toString());
      bool = true;
    } 
    while (instances1.readInstance(bufferedReader1)) {
      if (paramFilter.input(instances1.instance(0))) {
        if (!bool)
          throw new Error("Filter didn't return true from setInputFormat() earlier!"); 
        printWriter1.println(paramFilter.output().toString());
      } 
      instances1.delete(0);
    } 
    if (paramFilter.batchFinished()) {
      if (!bool)
        printWriter1.println(paramFilter.getOutputFormat().toString()); 
      while (paramFilter.numPendingOutput() > 0)
        printWriter1.println(paramFilter.output().toString()); 
    } 
    if (printWriter1 != null)
      printWriter1.close(); 
    bool = false;
    if (paramFilter.isOutputFormatDefined()) {
      printWriter2.println(paramFilter.getOutputFormat().toString());
      bool = true;
    } 
    while (instances2.readInstance(bufferedReader2)) {
      if (paramFilter.input(instances2.instance(0))) {
        if (!bool)
          throw new Error("Filter didn't return true from isOutputFormatDefined() earlier!"); 
        printWriter2.println(paramFilter.output().toString());
      } 
      instances2.delete(0);
    } 
    if (paramFilter.batchFinished()) {
      if (!bool)
        printWriter2.println(paramFilter.getOutputFormat().toString()); 
      while (paramFilter.numPendingOutput() > 0)
        printWriter2.println(paramFilter.output().toString()); 
    } 
    if (printWriter2 != null)
      printWriter2.close(); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("First argument must be the class name of a Filter"); 
      String str = paramArrayOfString[0];
      Filter filter = (Filter)Class.forName(str).newInstance();
      paramArrayOfString[0] = "";
      if (Utils.getFlag('b', paramArrayOfString)) {
        batchFilterFile(filter, paramArrayOfString);
      } else {
        filterFile(filter, paramArrayOfString);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filters\Filter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */