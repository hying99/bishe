package weka.experiment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class InstancesResultListener extends CSVResultListener {
  protected transient FastVector m_Instances;
  
  protected transient int[] m_AttributeTypes;
  
  protected transient Hashtable[] m_NominalIndexes;
  
  protected transient FastVector[] m_NominalStrings;
  
  public InstancesResultListener() {
    File file;
    try {
      file = File.createTempFile("weka_experiment", ".arff");
      file.deleteOnExit();
    } catch (Exception exception) {
      System.err.println("Cannot create temp file, writing to standard out.");
      file = new File("-");
    } 
    setOutputFile(file);
    setOutputFileName("");
  }
  
  public String globalInfo() {
    return "Takes results from a result producer and assembles them into a set of instances.";
  }
  
  public void preProcess(ResultProducer paramResultProducer) throws Exception {
    this.m_RP = paramResultProducer;
    if (this.m_OutputFile == null || this.m_OutputFile.getName().equals("-")) {
      this.m_Out = new PrintWriter(System.out, true);
    } else {
      this.m_Out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(this.m_OutputFile)), true);
    } 
    Object[] arrayOfObject1 = this.m_RP.getKeyTypes();
    Object[] arrayOfObject2 = this.m_RP.getResultTypes();
    this.m_AttributeTypes = new int[arrayOfObject1.length + arrayOfObject2.length];
    this.m_NominalIndexes = new Hashtable[this.m_AttributeTypes.length];
    this.m_NominalStrings = new FastVector[this.m_AttributeTypes.length];
    this.m_Instances = new FastVector();
    for (byte b = 0; b < this.m_AttributeTypes.length; b++) {
      Object object = null;
      if (b < arrayOfObject1.length) {
        object = arrayOfObject1[b];
      } else {
        object = arrayOfObject2[b - arrayOfObject1.length];
      } 
      if (object instanceof String) {
        this.m_AttributeTypes[b] = 1;
        this.m_NominalIndexes[b] = new Hashtable();
        this.m_NominalStrings[b] = new FastVector();
      } else if (object instanceof Double) {
        this.m_AttributeTypes[b] = 0;
      } else {
        throw new Exception("Unknown attribute type in column " + (b + 1));
      } 
    } 
  }
  
  public void postProcess(ResultProducer paramResultProducer) throws Exception {
    if (this.m_RP != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    String[] arrayOfString1 = this.m_RP.getKeyNames();
    String[] arrayOfString2 = this.m_RP.getResultNames();
    FastVector fastVector = new FastVector();
    for (byte b1 = 0; b1 < this.m_AttributeTypes.length; b1++) {
      String str = "Unknown";
      if (b1 < arrayOfString1.length) {
        str = "Key_" + arrayOfString1[b1];
      } else {
        str = arrayOfString2[b1 - arrayOfString1.length];
      } 
      switch (this.m_AttributeTypes[b1]) {
        case 1:
          if (this.m_NominalStrings[b1].size() > 0) {
            fastVector.addElement(new Attribute(str, this.m_NominalStrings[b1]));
            break;
          } 
          fastVector.addElement(new Attribute(str, (FastVector)null));
          break;
        case 0:
          fastVector.addElement(new Attribute(str));
          break;
        case 2:
          fastVector.addElement(new Attribute(str, (FastVector)null));
          break;
        default:
          throw new Exception("Unknown attribute type");
      } 
    } 
    Instances instances = new Instances("InstanceResultListener", fastVector, this.m_Instances.size());
    byte b2;
    for (b2 = 0; b2 < this.m_Instances.size(); b2++)
      instances.add((Instance)this.m_Instances.elementAt(b2)); 
    this.m_Out.println(new Instances(instances, 0));
    for (b2 = 0; b2 < instances.numInstances(); b2++)
      this.m_Out.println(instances.instance(b2)); 
    if (this.m_OutputFile != null && !this.m_OutputFile.getName().equals("-"))
      this.m_Out.close(); 
  }
  
  public void acceptResult(ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception {
    if (this.m_RP != paramResultProducer)
      throw new Error("Unrecognized ResultProducer sending results!!"); 
    Instance instance = new Instance(this.m_AttributeTypes.length);
    for (byte b = 0; b < this.m_AttributeTypes.length; b++) {
      Object object = null;
      if (b < paramArrayOfObject1.length) {
        object = paramArrayOfObject1[b];
      } else {
        object = paramArrayOfObject2[b - paramArrayOfObject1.length];
      } 
      if (object == null) {
        instance.setValue(b, Instance.missingValue());
      } else {
        String str;
        Double double_;
        double d;
        switch (this.m_AttributeTypes[b]) {
          case 1:
            str = (String)object;
            double_ = this.m_NominalIndexes[b].get(str);
            if (double_ == null) {
              double_ = new Double(this.m_NominalStrings[b].size());
              this.m_NominalIndexes[b].put(str, double_);
              this.m_NominalStrings[b].addElement(str);
            } 
            instance.setValue(b, double_.doubleValue());
            break;
          case 0:
            d = ((Double)object).doubleValue();
            instance.setValue(b, d);
            break;
          default:
            instance.setValue(b, Instance.missingValue());
            break;
        } 
      } 
    } 
    this.m_Instances.addElement(instance);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\InstancesResultListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */