package weka.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import weka.core.RTSI;
import weka.core.Utils;

public class GenericPropertiesCreator {
  public static final boolean VERBOSE = false;
  
  protected static String CREATOR_FILE = "weka/gui/GenericPropertiesCreator.props";
  
  protected static String PROPERTY_FILE = "weka/gui/GenericObjectEditor.props";
  
  protected String inputFilename;
  
  protected String outputFilename;
  
  protected Properties inputProperties;
  
  protected Properties outputProperties;
  
  public GenericPropertiesCreator() throws Exception {
    this(CREATOR_FILE);
  }
  
  public GenericPropertiesCreator(String paramString) throws Exception {
    this.inputFilename = paramString;
    this.outputFilename = PROPERTY_FILE;
    this.inputProperties = null;
    this.outputProperties = null;
  }
  
  public String getOutputFilename() {
    return this.outputFilename;
  }
  
  public void setOutputFilename(String paramString) {
    this.outputFilename = paramString;
  }
  
  public String getInputFilename() {
    return this.inputFilename;
  }
  
  public void setInputFilename(String paramString) {
    this.inputFilename = paramString;
  }
  
  public Properties getInputProperties() {
    return this.inputProperties;
  }
  
  public Properties getOutputProperties() {
    return this.outputProperties;
  }
  
  protected void loadInputProperties() throws Exception {
    this.inputProperties = new Properties();
    try {
      File file = new File(getInputFilename());
      if (file.exists()) {
        this.inputProperties.load(new FileInputStream(getInputFilename()));
      } else {
        this.inputProperties = Utils.readProperties(getInputFilename());
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  protected void generateOutputProperties() throws Exception {
    this.outputProperties = new Properties();
    Enumeration enumeration = this.inputProperties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = enumeration.nextElement().toString();
      StringTokenizer stringTokenizer = new StringTokenizer(this.inputProperties.getProperty(str1), ",");
      String str2 = "";
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken().trim();
        Vector vector = RTSI.find(str, Class.forName(str1));
        for (byte b = 0; b < vector.size(); b++) {
          if (!str2.equals(""))
            str2 = str2 + ","; 
          str2 = str2 + vector.get(b).toString();
        } 
      } 
      this.outputProperties.setProperty(str1, str2);
    } 
  }
  
  protected void storeOutputProperties() throws Exception {
    this.outputProperties.store(new FileOutputStream(getOutputFilename()), " Customises the list of options given by the GenericObjectEditor\n# for various superclasses.");
  }
  
  public void execute() throws Exception {
    execute(true);
  }
  
  public void execute(boolean paramBoolean) throws Exception {
    loadInputProperties();
    generateOutputProperties();
    if (paramBoolean)
      storeOutputProperties(); 
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    GenericPropertiesCreator genericPropertiesCreator = null;
    if (paramArrayOfString.length == 0) {
      genericPropertiesCreator = new GenericPropertiesCreator();
    } else if (paramArrayOfString.length == 1) {
      genericPropertiesCreator = new GenericPropertiesCreator();
      genericPropertiesCreator.setOutputFilename(paramArrayOfString[0]);
    } else if (paramArrayOfString.length == 2) {
      genericPropertiesCreator = new GenericPropertiesCreator(paramArrayOfString[0]);
      genericPropertiesCreator.setOutputFilename(paramArrayOfString[1]);
    } else {
      System.out.println("usage: " + GenericPropertiesCreator.class.getName() + " [<input.props>] [<output.props>]");
      System.exit(1);
    } 
    genericPropertiesCreator.execute(true);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\GenericPropertiesCreator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */