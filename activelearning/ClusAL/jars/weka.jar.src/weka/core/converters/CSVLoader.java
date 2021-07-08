package weka.core.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Enumeration;
import java.util.Hashtable;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class CSVLoader extends AbstractLoader implements FileSourcedConverter, BatchConverter {
  public static String FILE_EXTENSION = ".csv";
  
  protected String m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
  
  protected Instances m_structure = null;
  
  protected File m_sourceFile = null;
  
  private FastVector m_cumulativeStructure;
  
  private FastVector m_cumulativeInstances;
  
  public CSVLoader() {
    setRetrieval(0);
  }
  
  public String getFileExtension() {
    return FILE_EXTENSION;
  }
  
  public String getFileDescription() {
    return "CSV data files";
  }
  
  public File retrieveFile() {
    return new File(this.m_File);
  }
  
  public void setFile(File paramFile) throws IOException {
    this.m_File = paramFile.getAbsolutePath();
    setSource(paramFile);
  }
  
  public String globalInfo() {
    return "Reads a source that is in comma separated or tab separated format. Assumes that the first row in the file determines the number of and names of the attributes.";
  }
  
  public void reset() {
    this.m_structure = null;
    setRetrieval(0);
  }
  
  public void setSource(File paramFile) throws IOException {
    reset();
    if (paramFile == null)
      throw new IOException("Source file object is null!"); 
    this.m_sourceFile = paramFile;
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramFile));
      bufferedReader.close();
    } catch (FileNotFoundException fileNotFoundException) {
      throw new IOException("File not found");
    } 
  }
  
  public Instances getStructure() throws IOException {
    if (this.m_sourceFile == null)
      throw new IOException("No source has been specified"); 
    if (this.m_structure == null)
      try {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(this.m_sourceFile));
        StreamTokenizer streamTokenizer = new StreamTokenizer(bufferedReader);
        initTokenizer(streamTokenizer);
        readStructure(streamTokenizer);
      } catch (FileNotFoundException fileNotFoundException) {} 
    return this.m_structure;
  }
  
  private void readStructure(StreamTokenizer paramStreamTokenizer) throws IOException {
    readHeader(paramStreamTokenizer);
  }
  
  public Instances getDataSet() throws IOException {
    if (this.m_sourceFile == null)
      throw new IOException("No source has been specified"); 
    setSource(this.m_sourceFile);
    BufferedReader bufferedReader = new BufferedReader(new FileReader(this.m_sourceFile));
    StreamTokenizer streamTokenizer = new StreamTokenizer(bufferedReader);
    initTokenizer(streamTokenizer);
    readStructure(streamTokenizer);
    streamTokenizer.ordinaryChar(44);
    streamTokenizer.ordinaryChar(9);
    this.m_cumulativeStructure = new FastVector(this.m_structure.numAttributes());
    for (byte b1 = 0; b1 < this.m_structure.numAttributes(); b1++)
      this.m_cumulativeStructure.addElement(new Hashtable()); 
    this.m_cumulativeInstances = new FastVector();
    FastVector fastVector1;
    while ((fastVector1 = getInstance(streamTokenizer)) != null)
      this.m_cumulativeInstances.addElement(fastVector1); 
    bufferedReader.close();
    FastVector fastVector2 = new FastVector(this.m_structure.numAttributes());
    for (byte b2 = 0; b2 < this.m_structure.numAttributes(); b2++) {
      String str1 = this.m_structure.attribute(b2).name();
      Hashtable hashtable = (Hashtable)this.m_cumulativeStructure.elementAt(b2);
      if (hashtable.size() == 0) {
        fastVector2.addElement(new Attribute(str1));
      } else {
        FastVector fastVector = new FastVector(hashtable.size());
        for (byte b = 0; b < hashtable.size(); b++)
          fastVector.addElement("dummy"); 
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
          Object object = enumeration.nextElement();
          int i = ((Integer)hashtable.get(object)).intValue();
          fastVector.setElementAt(new String(object.toString()), i);
        } 
        fastVector2.addElement(new Attribute(str1, fastVector));
      } 
    } 
    String str = this.m_sourceFile.getName().replaceAll("\\.[cC][sS][vV]$", "");
    Instances instances = new Instances(str, fastVector2, this.m_cumulativeInstances.size());
    for (byte b3 = 0; b3 < this.m_cumulativeInstances.size(); b3++) {
      fastVector1 = (FastVector)this.m_cumulativeInstances.elementAt(b3);
      double[] arrayOfDouble = new double[instances.numAttributes()];
      for (byte b = 0; b < fastVector1.size(); b++) {
        Object object = fastVector1.elementAt(b);
        if (object instanceof String) {
          if (((String)object).compareTo("?") == 0) {
            arrayOfDouble[b] = Instance.missingValue();
          } else {
            if (!instances.attribute(b).isNominal()) {
              System.err.println("Wrong attribute type!!!");
              System.exit(1);
            } 
            Hashtable hashtable = (Hashtable)this.m_cumulativeStructure.elementAt(b);
            int i = ((Integer)hashtable.get(object)).intValue();
            arrayOfDouble[b] = i;
          } 
        } else if (instances.attribute(b).isNominal()) {
          Hashtable hashtable = (Hashtable)this.m_cumulativeStructure.elementAt(b);
          int i = ((Integer)hashtable.get(object)).intValue();
          arrayOfDouble[b] = i;
        } else {
          arrayOfDouble[b] = ((Double)object).doubleValue();
        } 
      } 
      instances.add(new Instance(1.0D, arrayOfDouble));
    } 
    this.m_structure = new Instances(instances, 0);
    setRetrieval(1);
    this.m_cumulativeStructure = null;
    return instances;
  }
  
  public Instance getNextInstance() throws IOException {
    throw new IOException("CSVLoader can't read data sets incrementally.");
  }
  
  private FastVector getInstance(StreamTokenizer paramStreamTokenizer) throws IOException {
    FastVector fastVector = new FastVector();
    ConverterUtils.getFirstToken(paramStreamTokenizer);
    if (paramStreamTokenizer.ttype == -1)
      return null; 
    for (boolean bool = true; paramStreamTokenizer.ttype != 10 && paramStreamTokenizer.ttype != -1; bool = false) {
      boolean bool1;
      if (!bool)
        ConverterUtils.getToken(paramStreamTokenizer); 
      if (paramStreamTokenizer.ttype == 44 || paramStreamTokenizer.ttype == 9 || paramStreamTokenizer.ttype == 10) {
        fastVector.addElement("?");
        bool1 = true;
      } else {
        bool1 = false;
        try {
          double d = Double.valueOf(paramStreamTokenizer.sval).doubleValue();
          fastVector.addElement(new Double(d));
        } catch (NumberFormatException numberFormatException) {
          fastVector.addElement(new String(paramStreamTokenizer.sval.replace(' ', '_')));
        } 
      } 
      if (!bool1)
        ConverterUtils.getToken(paramStreamTokenizer); 
    } 
    if (fastVector.size() != this.m_structure.numAttributes())
      ConverterUtils.errms(paramStreamTokenizer, "wrong number of values. Read " + fastVector.size() + ", expected " + this.m_structure.numAttributes()); 
    try {
      checkStructure(fastVector);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return fastVector;
  }
  
  private void checkStructure(FastVector paramFastVector) throws Exception {
    if (paramFastVector == null)
      throw new Exception("current shouldn't be null in checkStructure"); 
    for (byte b = 0; b < paramFastVector.size(); b++) {
      Object object = paramFastVector.elementAt(b);
      if (object instanceof String) {
        if (((String)object).compareTo("?") != 0) {
          Hashtable hashtable = (Hashtable)this.m_cumulativeStructure.elementAt(b);
          if (!hashtable.containsKey(object)) {
            if (hashtable.size() == 0)
              for (byte b1 = 0; b1 < this.m_cumulativeInstances.size(); b1++) {
                FastVector fastVector = (FastVector)this.m_cumulativeInstances.elementAt(b1);
                Object object1 = fastVector.elementAt(b);
                if (!(object1 instanceof String) && !hashtable.containsKey(object1))
                  hashtable.put(new Double(((Double)object1).doubleValue()), new Integer(hashtable.size())); 
              }  
            int i = hashtable.size();
            hashtable.put(object, new Integer(i));
          } 
        } 
      } else if (object instanceof Double) {
        Hashtable hashtable = (Hashtable)this.m_cumulativeStructure.elementAt(b);
        if (hashtable.size() != 0 && !hashtable.containsKey(object)) {
          int i = hashtable.size();
          hashtable.put(new Double(((Double)object).doubleValue()), new Integer(i));
        } 
      } else {
        throw new Exception("Wrong object type in checkStructure!");
      } 
    } 
  }
  
  private void readHeader(StreamTokenizer paramStreamTokenizer) throws IOException {
    FastVector fastVector = new FastVector();
    ConverterUtils.getFirstToken(paramStreamTokenizer);
    if (paramStreamTokenizer.ttype == -1)
      ConverterUtils.errms(paramStreamTokenizer, "premature end of file"); 
    while (paramStreamTokenizer.ttype != 10) {
      fastVector.addElement(new Attribute(paramStreamTokenizer.sval));
      ConverterUtils.getToken(paramStreamTokenizer);
    } 
    String str = this.m_sourceFile.getName().replaceAll("\\.[cC][sS][vV]$", "");
    this.m_structure = new Instances(str, fastVector, 0);
  }
  
  private void initTokenizer(StreamTokenizer paramStreamTokenizer) {
    paramStreamTokenizer.resetSyntax();
    paramStreamTokenizer.whitespaceChars(0, 31);
    paramStreamTokenizer.wordChars(32, 255);
    paramStreamTokenizer.whitespaceChars(44, 44);
    paramStreamTokenizer.whitespaceChars(9, 9);
    paramStreamTokenizer.commentChar(37);
    paramStreamTokenizer.quoteChar(34);
    paramStreamTokenizer.quoteChar(39);
    paramStreamTokenizer.eolIsSignificant(true);
  }
  
  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length > 0) {
      File file = new File(paramArrayOfString[0]);
      try {
        CSVLoader cSVLoader = new CSVLoader();
        cSVLoader.setSource(file);
        System.out.println(cSVLoader.getDataSet());
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      System.err.println("Usage:\n\tCSVLoader <file.csv>\n");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\CSVLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */