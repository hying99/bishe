package weka.core.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class C45Loader extends AbstractLoader implements FileSourcedConverter, BatchConverter, IncrementalConverter {
  public static String FILE_EXTENSION = ".names";
  
  protected String m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
  
  protected Instances m_structure = null;
  
  protected File m_sourceFile = null;
  
  private File m_sourceFileData = null;
  
  private transient Reader m_namesReader = null;
  
  private transient Reader m_dataReader = null;
  
  private String m_fileStem;
  
  private int m_numAttribs;
  
  private boolean[] m_ignore;
  
  public String globalInfo() {
    return "Reads a file that is C45 format. Can take a filestem or filestem with .names or .data appended. Assumes that path/<filestem>.names and path/<filestem>.data exist and contain the names and data respectively.";
  }
  
  public void reset() {
    this.m_structure = null;
    setRetrieval(0);
  }
  
  public String getFileExtension() {
    return FILE_EXTENSION;
  }
  
  public String getFileDescription() {
    return "C4.5 data files";
  }
  
  public File retrieveFile() {
    return new File(this.m_File);
  }
  
  public void setFile(File paramFile) throws IOException {
    this.m_File = paramFile.getAbsolutePath();
    setSource(paramFile);
  }
  
  public void setSource(File paramFile) throws IOException {
    String str2;
    reset();
    if (paramFile == null)
      throw new IOException("Source file object is null!"); 
    String str1 = paramFile.getName();
    String str3 = paramFile.getParent();
    if (str3 != null) {
      str3 = str3 + File.separator;
    } else {
      str3 = "";
    } 
    if (str1.indexOf('.') < 0) {
      str2 = str1;
      str1 = str1 + ".names";
    } else {
      str2 = str1.substring(0, str1.lastIndexOf('.'));
      str1 = str2 + ".names";
    } 
    this.m_fileStem = str2;
    paramFile = new File(str3 + str1);
    this.m_sourceFile = paramFile;
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramFile));
      this.m_namesReader = bufferedReader;
    } catch (FileNotFoundException fileNotFoundException) {
      throw new IOException("File not found : " + str3 + str1);
    } 
    this.m_sourceFileData = new File(str3 + str2 + ".data");
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(this.m_sourceFileData));
      this.m_dataReader = bufferedReader;
    } catch (FileNotFoundException fileNotFoundException) {
      throw new IOException("File not found : " + str3 + str1);
    } 
  }
  
  public Instances getStructure() throws IOException {
    if (this.m_sourceFile == null)
      throw new IOException("No source has beenspecified"); 
    if (this.m_structure == null) {
      setSource(this.m_sourceFile);
      StreamTokenizer streamTokenizer = new StreamTokenizer(this.m_namesReader);
      initTokenizer(streamTokenizer);
      readHeader(streamTokenizer);
    } 
    return this.m_structure;
  }
  
  public Instances getDataSet() throws IOException {
    if (this.m_sourceFile == null)
      throw new IOException("No source has been specified"); 
    if (getRetrieval() == 2)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes"); 
    setRetrieval(1);
    if (this.m_structure == null)
      getStructure(); 
    StreamTokenizer streamTokenizer = new StreamTokenizer(this.m_dataReader);
    initTokenizer(streamTokenizer);
    Instances instances = new Instances(this.m_structure);
    for (Instance instance = getInstance(streamTokenizer); instance != null; instance = getInstance(streamTokenizer))
      instances.add(instance); 
    reset();
    return instances;
  }
  
  public Instance getNextInstance() throws IOException {
    if (this.m_sourceFile == null)
      throw new IOException("No source has been specified"); 
    if (getRetrieval() == 1)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes"); 
    setRetrieval(2);
    if (this.m_structure == null)
      getStructure(); 
    StreamTokenizer streamTokenizer = new StreamTokenizer(this.m_dataReader);
    initTokenizer(streamTokenizer);
    Instance instance = getInstance(streamTokenizer);
    if (instance != null) {
      instance.setDataset(this.m_structure);
    } else {
      reset();
    } 
    return instance;
  }
  
  private Instance getInstance(StreamTokenizer paramStreamTokenizer) throws IOException {
    double[] arrayOfDouble = new double[this.m_structure.numAttributes()];
    ConverterUtils.getFirstToken(paramStreamTokenizer);
    if (paramStreamTokenizer.ttype == -1)
      return null; 
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttribs; b2++) {
      if (b2 > 0)
        ConverterUtils.getToken(paramStreamTokenizer); 
      if (!this.m_ignore[b2])
        if (paramStreamTokenizer.ttype == 63) {
          arrayOfDouble[b1++] = Instance.missingValue();
        } else {
          String str = paramStreamTokenizer.sval;
          if (b2 == this.m_numAttribs - 1 && str.charAt(str.length() - 1) == '.')
            str = str.substring(0, str.length() - 1); 
          if (this.m_structure.attribute(b1).isNominal()) {
            int i = this.m_structure.attribute(b1).indexOfValue(str);
            if (i == -1)
              ConverterUtils.errms(paramStreamTokenizer, "nominal value not declared in header :" + str + " column " + b2); 
            arrayOfDouble[b1++] = i;
          } else if (this.m_structure.attribute(b1).isNumeric()) {
            try {
              arrayOfDouble[b1++] = Double.valueOf(str).doubleValue();
            } catch (NumberFormatException numberFormatException) {
              ConverterUtils.errms(paramStreamTokenizer, "number expected");
            } 
          } else {
            System.err.println("Shouldn't get here");
            System.exit(1);
          } 
        }  
    } 
    return new Instance(1.0D, arrayOfDouble);
  }
  
  private String removeTrailingPeriod(String paramString) {
    if (paramString.charAt(paramString.length() - 1) == '.')
      paramString = paramString.substring(0, paramString.length() - 1); 
    return paramString;
  }
  
  private void readHeader(StreamTokenizer paramStreamTokenizer) throws IOException {
    FastVector fastVector1 = new FastVector();
    FastVector fastVector2 = new FastVector();
    ConverterUtils.getFirstToken(paramStreamTokenizer);
    if (paramStreamTokenizer.ttype == -1)
      ConverterUtils.errms(paramStreamTokenizer, "premature end of file"); 
    this.m_numAttribs = 1;
    FastVector fastVector3 = new FastVector();
    while (paramStreamTokenizer.ttype != 10) {
      String str = paramStreamTokenizer.sval.trim();
      if (str.length() > 0) {
        str = removeTrailingPeriod(str);
        fastVector3.addElement(str);
      } 
      ConverterUtils.getToken(paramStreamTokenizer);
    } 
    byte b = 0;
    while (paramStreamTokenizer.ttype != -1) {
      ConverterUtils.getFirstToken(paramStreamTokenizer);
      if (paramStreamTokenizer.ttype != -1) {
        String str1 = paramStreamTokenizer.sval;
        ConverterUtils.getToken(paramStreamTokenizer);
        if (paramStreamTokenizer.ttype == 10)
          ConverterUtils.errms(paramStreamTokenizer, "premature end of line. Expected attribute type."); 
        String str2 = paramStreamTokenizer.sval.toLowerCase().trim();
        if (str2.startsWith("ignore") || str2.startsWith("label")) {
          fastVector2.addElement(new Integer(b));
          b++;
          continue;
        } 
        if (str2.startsWith("continuous")) {
          fastVector1.addElement(new Attribute(str1));
          b++;
          continue;
        } 
        b++;
        FastVector fastVector = new FastVector();
        while (paramStreamTokenizer.ttype != 10 && paramStreamTokenizer.ttype != -1) {
          String str = paramStreamTokenizer.sval.trim();
          if (str.length() > 0) {
            str = removeTrailingPeriod(str);
            fastVector.addElement(str);
          } 
          ConverterUtils.getToken(paramStreamTokenizer);
        } 
        fastVector1.addElement(new Attribute(str1, fastVector));
      } 
    } 
    boolean bool = true;
    byte b1 = -1;
    if (fastVector3.size() == 1)
      for (b1 = 0; b1 < fastVector1.size(); b1++) {
        if (((Attribute)fastVector1.elementAt(b1)).name().compareTo((String)fastVector3.elementAt(0)) == 0) {
          bool = false;
          this.m_numAttribs--;
          break;
        } 
      }  
    if (bool)
      fastVector1.addElement(new Attribute("Class", fastVector3)); 
    this.m_structure = new Instances(this.m_fileStem, fastVector1, 0);
    try {
      if (bool) {
        this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
      } else {
        this.m_structure.setClassIndex(b1);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    this.m_numAttribs = this.m_structure.numAttributes() + fastVector2.size();
    this.m_ignore = new boolean[this.m_numAttribs];
    for (b1 = 0; b1 < fastVector2.size(); b1++)
      this.m_ignore[((Integer)fastVector2.elementAt(b1)).intValue()] = true; 
  }
  
  private void initTokenizer(StreamTokenizer paramStreamTokenizer) {
    paramStreamTokenizer.resetSyntax();
    paramStreamTokenizer.whitespaceChars(0, 31);
    paramStreamTokenizer.wordChars(32, 255);
    paramStreamTokenizer.whitespaceChars(44, 44);
    paramStreamTokenizer.whitespaceChars(58, 58);
    paramStreamTokenizer.commentChar(124);
    paramStreamTokenizer.whitespaceChars(9, 9);
    paramStreamTokenizer.quoteChar(34);
    paramStreamTokenizer.quoteChar(39);
    paramStreamTokenizer.eolIsSignificant(true);
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length > 0) {
      File file = new File(paramArrayOfString[0]);
      try {
        C45Loader c45Loader = new C45Loader();
        c45Loader.setSource(file);
        System.out.println(c45Loader.getStructure());
        for (Instance instance = c45Loader.getNextInstance(); instance != null; instance = c45Loader.getNextInstance())
          System.out.println(instance); 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      System.err.println("Usage:\n\tC45Loader <filestem>[.names | data]\n");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\C45Loader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */