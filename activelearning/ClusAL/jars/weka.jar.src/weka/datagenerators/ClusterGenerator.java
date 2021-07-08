package weka.datagenerators;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public abstract class ClusterGenerator implements Serializable {
  private boolean m_Debug = false;
  
  private Instances m_Format = null;
  
  private String m_RelationName = "";
  
  protected int m_NumAttributes = 2;
  
  protected int m_NumClusters = 4;
  
  private boolean m_ClassFlag = false;
  
  private int m_NumExamplesAct = 0;
  
  private PrintWriter m_Output = null;
  
  abstract Instances defineDataFormat() throws Exception;
  
  abstract Instance generateExample() throws Exception;
  
  abstract Instances generateExamples() throws Exception;
  
  abstract String generateStart() throws Exception;
  
  abstract String generateFinished() throws Exception;
  
  abstract boolean getSingleModeFlag() throws Exception;
  
  public void setClassFlag(boolean paramBoolean) {
    this.m_ClassFlag = paramBoolean;
  }
  
  public boolean getClassFlag() {
    boolean bool = this.m_ClassFlag;
    return this.m_ClassFlag;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void setRelationName(String paramString) {
    if (paramString.length() == 0) {
      StringBuffer stringBuffer = new StringBuffer(getClass().getName());
      String[] arrayOfString = getGenericOptions();
      byte b;
      for (b = 0; b < arrayOfString.length; b++)
        stringBuffer = stringBuffer.append(arrayOfString[b].trim()); 
      if (this instanceof OptionHandler) {
        arrayOfString = ((OptionHandler)this).getOptions();
        for (b = 0; b < arrayOfString.length; b++)
          stringBuffer = stringBuffer.append(arrayOfString[b].trim()); 
      } 
      this.m_RelationName = stringBuffer.toString();
    } else {
      this.m_RelationName = paramString;
    } 
  }
  
  public String getRelationName() {
    return this.m_RelationName;
  }
  
  public void setNumClusters(int paramInt) {
    this.m_NumClusters = paramInt;
  }
  
  public int getNumClusters() {
    return this.m_NumClusters;
  }
  
  public void setNumAttributes(int paramInt) {
    this.m_NumAttributes = paramInt;
  }
  
  public int getNumAttributes() {
    return this.m_NumAttributes;
  }
  
  public void setNumExamplesAct(int paramInt) {
    this.m_NumExamplesAct = paramInt;
  }
  
  public int getNumExamplesAct() {
    return this.m_NumExamplesAct;
  }
  
  public void setOutput(PrintWriter paramPrintWriter) {
    this.m_Output = paramPrintWriter;
  }
  
  public PrintWriter getOutput() {
    return this.m_Output;
  }
  
  protected void setFormat(Instances paramInstances) {
    this.m_Format = new Instances(paramInstances, 0);
  }
  
  protected Instances getFormat() {
    return new Instances(this.m_Format, 0);
  }
  
  protected String toStringFormat() {
    return (this.m_Format == null) ? "" : this.m_Format.toString();
  }
  
  public static void makeData(ClusterGenerator paramClusterGenerator, String[] paramArrayOfString) throws Exception {
    PrintWriter printWriter = null;
    try {
      setOptions(paramClusterGenerator, paramArrayOfString);
    } catch (Exception exception) {
      String str3 = "";
      if (paramClusterGenerator instanceof OptionHandler)
        str3 = paramClusterGenerator.listSpecificOptions(paramClusterGenerator); 
      String str4 = listGenericOptions(paramClusterGenerator);
      throw new Exception('\n' + exception.getMessage() + str3 + str4);
    } 
    paramClusterGenerator.setFormat(paramClusterGenerator.defineDataFormat());
    printWriter = paramClusterGenerator.getOutput();
    printWriter.println("% ");
    printWriter.print("% " + paramClusterGenerator.getClass().getName() + " ");
    String[] arrayOfString = paramClusterGenerator.getGenericOptions();
    byte b;
    for (b = 0; b < arrayOfString.length; b++)
      printWriter.print(arrayOfString[b] + " "); 
    arrayOfString = ((OptionHandler)paramClusterGenerator).getOptions();
    for (b = 0; b < arrayOfString.length; b++)
      printWriter.print(arrayOfString[b] + " "); 
    printWriter.println("\n%");
    String str1 = paramClusterGenerator.generateStart();
    if (str1.length() > 0)
      printWriter.println(str1); 
    boolean bool = paramClusterGenerator.getSingleModeFlag();
    if (bool) {
      printWriter.println(paramClusterGenerator.toStringFormat());
      for (byte b1 = 0; b1 < paramClusterGenerator.getNumExamplesAct(); b1++) {
        Instance instance = paramClusterGenerator.generateExample();
        printWriter.println(instance);
      } 
    } else {
      Instances instances = paramClusterGenerator.generateExamples();
      printWriter.println(instances);
    } 
    String str2 = paramClusterGenerator.generateFinished();
    if (str2.length() > 0)
      printWriter.println(str2); 
    if (printWriter != null)
      printWriter.close(); 
  }
  
  private String listSpecificOptions(ClusterGenerator paramClusterGenerator) {
    String str = "";
    if (paramClusterGenerator instanceof OptionHandler) {
      str = str + "\nData Generator options:\n\n";
      Enumeration enumeration = ((OptionHandler)paramClusterGenerator).listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        str = str + option.synopsis() + '\n' + option.description() + "\n";
      } 
    } 
    return str;
  }
  
  private static void setOptions(ClusterGenerator paramClusterGenerator, String[] paramArrayOfString) throws Exception {
    PrintWriter printWriter;
    boolean bool = false;
    String str1 = new String("");
    bool = Utils.getFlag('h', paramArrayOfString);
    if (Utils.getFlag('d', paramArrayOfString))
      paramClusterGenerator.setDebug(true); 
    String str2 = Utils.getOption('r', paramArrayOfString);
    str1 = Utils.getOption('o', paramArrayOfString);
    String str3 = Utils.getOption('k', paramArrayOfString);
    if (str3.length() != 0)
      paramClusterGenerator.setNumClusters(Integer.parseInt(str3)); 
    if (Utils.getFlag('c', paramArrayOfString))
      paramClusterGenerator.setClassFlag(true); 
    String str4 = Utils.getOption('a', paramArrayOfString);
    if (str4.length() != 0)
      paramClusterGenerator.setNumAttributes(Integer.parseInt(str4)); 
    if (paramClusterGenerator instanceof OptionHandler)
      ((OptionHandler)paramClusterGenerator).setOptions(paramArrayOfString); 
    paramClusterGenerator.setRelationName(str2);
    Utils.checkForRemainingOptions(paramArrayOfString);
    if (bool)
      throw new Exception("Help requested.\n"); 
    if (str1.length() != 0) {
      printWriter = new PrintWriter(new FileOutputStream(str1));
    } else {
      printWriter = new PrintWriter(System.out);
    } 
    paramClusterGenerator.setOutput(printWriter);
  }
  
  private static String listGenericOptions(ClusterGenerator paramClusterGenerator) {
    return "\nGeneral options:\n\n-h\n\tGet help on available options.\n-r <relation name>\n\tThe name of the relation for the produced dataset.\n-a <number of attributes>\n\tThe number of attributes for the produced dataset.\n-k <number of clusters>\n\tThe number of clusters the dataset is produced in.\n-c \n\tThe class flag, if set, the cluster is listed in the class attribute.\n-o <file>\n\tThe name of the file output instances will be written to.\n\tIf not supplied the instances will be written to stdout.\n";
  }
  
  private String[] getGenericOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    String str = getRelationName();
    if (str.length() > 0) {
      arrayOfString[b++] = "-r";
      arrayOfString[b++] = "" + getRelationName();
    } 
    arrayOfString[b++] = "-a";
    arrayOfString[b++] = "" + getNumAttributes();
    arrayOfString[b++] = "-k";
    arrayOfString[b++] = "" + getNumClusters();
    if (getClassFlag()) {
      arrayOfString[b++] = "-c";
      arrayOfString[b++] = "";
    } 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\datagenerators\ClusterGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */