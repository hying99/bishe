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

public abstract class Generator implements Serializable {
  private boolean m_Debug = false;
  
  private Instances m_Format = null;
  
  private String m_RelationName = "";
  
  private int m_NumAttributes = 10;
  
  private int m_NumClasses = 2;
  
  private int m_NumExamples = 100;
  
  private int m_NumExamplesAct = 0;
  
  private PrintWriter m_Output = null;
  
  abstract Instances defineDataFormat() throws Exception;
  
  abstract Instance generateExample() throws Exception;
  
  abstract Instances generateExamples() throws Exception;
  
  abstract String generateFinished() throws Exception;
  
  abstract boolean getSingleModeFlag() throws Exception;
  
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
  
  public void setNumClasses(int paramInt) {
    this.m_NumClasses = paramInt;
  }
  
  public int getNumClasses() {
    return this.m_NumClasses;
  }
  
  public void setNumExamples(int paramInt) {
    this.m_NumExamples = paramInt;
  }
  
  public int getNumExamples() {
    return this.m_NumExamples;
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
  
  public static void makeData(Generator paramGenerator, String[] paramArrayOfString) throws Exception {
    PrintWriter printWriter = null;
    try {
      setOptions(paramGenerator, paramArrayOfString);
    } catch (Exception exception) {
      String str1 = "";
      if (paramGenerator instanceof OptionHandler)
        str1 = paramGenerator.listSpecificOptions(paramGenerator); 
      String str2 = listGenericOptions(paramGenerator);
      throw new Exception('\n' + exception.getMessage() + str1 + str2);
    } 
    paramGenerator.setFormat(paramGenerator.defineDataFormat());
    printWriter = paramGenerator.getOutput();
    printWriter.println("% ");
    printWriter.print("% " + paramGenerator.getClass().getName() + " ");
    String[] arrayOfString = paramGenerator.getGenericOptions();
    byte b;
    for (b = 0; b < arrayOfString.length; b++)
      printWriter.print(arrayOfString[b] + " "); 
    arrayOfString = ((OptionHandler)paramGenerator).getOptions();
    for (b = 0; b < arrayOfString.length; b++)
      printWriter.print(arrayOfString[b] + " "); 
    printWriter.println("\n%");
    boolean bool = paramGenerator.getSingleModeFlag();
    if (bool) {
      printWriter.println(paramGenerator.toStringFormat());
      for (byte b1 = 0; b1 < paramGenerator.getNumExamplesAct(); b1++) {
        Instance instance = paramGenerator.generateExample();
        printWriter.println(instance);
      } 
    } else {
      Instances instances = paramGenerator.generateExamples();
      printWriter.println(instances);
    } 
    String str = paramGenerator.generateFinished();
    if (str.length() > 0)
      printWriter.println(str); 
    if (printWriter != null)
      printWriter.close(); 
  }
  
  private String listSpecificOptions(Generator paramGenerator) {
    String str = "";
    if (paramGenerator instanceof OptionHandler) {
      str = str + "\nData Generator options:\n\n";
      Enumeration enumeration = ((OptionHandler)paramGenerator).listOptions();
      while (enumeration.hasMoreElements()) {
        Option option = enumeration.nextElement();
        str = str + option.synopsis() + '\n' + option.description() + "\n";
      } 
    } 
    return str;
  }
  
  private static void setOptions(Generator paramGenerator, String[] paramArrayOfString) throws Exception {
    PrintWriter printWriter;
    boolean bool = false;
    String str1 = new String("");
    bool = Utils.getFlag('h', paramArrayOfString);
    if (Utils.getFlag('d', paramArrayOfString))
      paramGenerator.setDebug(true); 
    String str2 = Utils.getOption('r', paramArrayOfString);
    str1 = Utils.getOption('o', paramArrayOfString);
    String str3 = Utils.getOption('c', paramArrayOfString);
    if (str3.length() != 0)
      paramGenerator.setNumClasses(Integer.parseInt(str3)); 
    String str4 = Utils.getOption('n', paramArrayOfString);
    if (str4.length() != 0) {
      paramGenerator.setNumExamples(Integer.parseInt(str4));
      paramGenerator.setNumExamplesAct(Integer.parseInt(str4));
    } 
    String str5 = Utils.getOption('a', paramArrayOfString);
    if (str5.length() != 0)
      paramGenerator.setNumAttributes(Integer.parseInt(str5)); 
    if (paramGenerator instanceof OptionHandler)
      ((OptionHandler)paramGenerator).setOptions(paramArrayOfString); 
    paramGenerator.setRelationName(str2);
    Utils.checkForRemainingOptions(paramArrayOfString);
    if (bool)
      throw new Exception("Help requested.\n"); 
    if (str1.length() != 0) {
      printWriter = new PrintWriter(new FileOutputStream(str1));
    } else {
      printWriter = new PrintWriter(System.out);
    } 
    paramGenerator.setOutput(printWriter);
  }
  
  private static String listGenericOptions(Generator paramGenerator) {
    return "\nGeneral options:\n\n-h\n\tGet help on available options.\n-a <number of attributes>\n\tThe number of attributes the produced dataset should have.\n-c <number of classes>\n\tThe number of classes the produced dataset should have.\n-n <number of examples>\n\tThe number of examples the produced dataset should have.\n-o <file>\n\tThe name of the file output instances will be written to.\n\tIf not supplied the instances will be written to stdout.\n";
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
    arrayOfString[b++] = "-c";
    arrayOfString[b++] = "" + getNumClasses();
    arrayOfString[b++] = "-n";
    arrayOfString[b++] = "" + getNumExamples();
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\datagenerators\Generator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */