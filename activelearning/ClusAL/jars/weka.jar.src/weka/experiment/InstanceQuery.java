package weka.experiment;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.Utils;

public class InstanceQuery extends DatabaseUtils implements OptionHandler {
  boolean m_CreateSparseData = false;
  
  String m_Query = "SELECT * from ?";
  
  public Enumeration listOptions() {
    Vector vector = new Vector(2);
    vector.addElement(new Option("\tSQL query to execute.", "Q", 1, "-Q <query>"));
    vector.addElement(new Option("\tReturn sparse rather than normal instances.", "S", 0, "-S"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setSparseData(Utils.getFlag('S', paramArrayOfString));
    String str = Utils.getOption('Q', paramArrayOfString);
    if (str.length() != 0)
      setQuery(str); 
  }
  
  public String queryTipText() {
    return "The SQL query to execute against the database.";
  }
  
  public void setQuery(String paramString) {
    this.m_Query = paramString;
  }
  
  public String getQuery() {
    return this.m_Query;
  }
  
  public String sparseDataTipText() {
    return "Encode data as sparse instances.";
  }
  
  public void setSparseData(boolean paramBoolean) {
    this.m_CreateSparseData = paramBoolean;
  }
  
  public boolean getSparseData() {
    return this.m_CreateSparseData;
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[3];
    byte b = 0;
    arrayOfString[b] = "-Q";
    arrayOfString[b++] = getQuery();
    if (getSparseData())
      arrayOfString[b++] = "-S"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public Instances retrieveInstances() throws Exception {
    return retrieveInstances(this.m_Query);
  }
  
  public Instances retrieveInstances(String paramString) throws Exception {
    System.err.println("Executing query: " + paramString);
    connectToDatabase();
    if (!execute(paramString))
      throw new Exception("Query didn't produce results"); 
    ResultSet resultSet = getResultSet();
    System.err.println("Getting metadata...");
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    System.err.println("Completed getting metadata...");
    int i = resultSetMetaData.getColumnCount();
    int[] arrayOfInt = new int[i];
    Hashtable[] arrayOfHashtable = new Hashtable[i];
    FastVector[] arrayOfFastVector = new FastVector[i];
    for (byte b1 = 1; b1 <= i; b1++) {
      switch (translateDBColumnType(resultSetMetaData.getColumnTypeName(b1))) {
        case 0:
          arrayOfInt[b1 - 1] = 1;
          arrayOfHashtable[b1 - 1] = new Hashtable();
          arrayOfFastVector[b1 - 1] = new FastVector();
          break;
        case 1:
          arrayOfInt[b1 - 1] = 1;
          arrayOfHashtable[b1 - 1] = new Hashtable();
          arrayOfHashtable[b1 - 1].put("false", new Double(0.0D));
          arrayOfHashtable[b1 - 1].put("true", new Double(1.0D));
          arrayOfFastVector[b1 - 1] = new FastVector();
          arrayOfFastVector[b1 - 1].addElement("false");
          arrayOfFastVector[b1 - 1].addElement("true");
          break;
        case 2:
          arrayOfInt[b1 - 1] = 0;
          break;
        case 3:
          arrayOfInt[b1 - 1] = 0;
          break;
        case 4:
          arrayOfInt[b1 - 1] = 0;
          break;
        case 5:
          arrayOfInt[b1 - 1] = 0;
          break;
        case 6:
          arrayOfInt[b1 - 1] = 0;
          break;
        case 7:
          arrayOfInt[b1 - 1] = 0;
          break;
        case 8:
          arrayOfInt[b1 - 1] = 3;
          break;
        default:
          arrayOfInt[b1 - 1] = 2;
          break;
      } 
    } 
    System.err.println("Creating instances...");
    FastVector fastVector1 = new FastVector();
    for (byte b2 = 0; resultSet.next(); b2++) {
      Instance instance;
      if (b2 % 100 == 0) {
        System.err.print("read " + b2 + " instances \r");
        System.err.flush();
      } 
      double[] arrayOfDouble = new double[i];
      for (byte b = 1; b <= i; b++) {
        String str;
        Double double_;
        boolean bool;
        double d;
        byte b5;
        short s;
        int j;
        long l;
        float f;
        Date date;
        switch (translateDBColumnType(resultSetMetaData.getColumnTypeName(b))) {
          case 0:
            str = resultSet.getString(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            double_ = arrayOfHashtable[b - 1].get(str);
            if (double_ == null) {
              double_ = new Double(arrayOfFastVector[b - 1].size());
              arrayOfHashtable[b - 1].put(str, double_);
              arrayOfFastVector[b - 1].addElement(str);
            } 
            arrayOfDouble[b - 1] = double_.doubleValue();
            break;
          case 1:
            bool = resultSet.getBoolean(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = bool ? 1.0D : 0.0D;
            break;
          case 2:
            d = resultSet.getDouble(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = d;
            break;
          case 3:
            b5 = resultSet.getByte(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = b5;
            break;
          case 4:
            s = (short)resultSet.getByte(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = s;
            break;
          case 5:
            j = resultSet.getInt(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = j;
            break;
          case 6:
            l = resultSet.getLong(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = l;
            break;
          case 7:
            f = resultSet.getFloat(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = f;
            break;
          case 8:
            date = resultSet.getDate(b);
            if (resultSet.wasNull()) {
              arrayOfDouble[b - 1] = Instance.missingValue();
              break;
            } 
            arrayOfDouble[b - 1] = date.getTime();
            break;
          default:
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
        } 
      } 
      if (this.m_CreateSparseData) {
        SparseInstance sparseInstance = new SparseInstance(1.0D, arrayOfDouble);
      } else {
        instance = new Instance(1.0D, arrayOfDouble);
      } 
      fastVector1.addElement(instance);
    } 
    System.err.println("Creating header...");
    FastVector fastVector2 = new FastVector();
    for (byte b3 = 0; b3 < i; b3++) {
      String str = attributeCaseFix(resultSetMetaData.getColumnName(b3 + 1));
      switch (arrayOfInt[b3]) {
        case 1:
          fastVector2.addElement(new Attribute(str, arrayOfFastVector[b3]));
          break;
        case 0:
          fastVector2.addElement(new Attribute(str));
          break;
        case 2:
          fastVector2.addElement(new Attribute(str, (FastVector)null));
          break;
        case 3:
          fastVector2.addElement(new Attribute(str, (String)null));
          break;
        default:
          throw new Exception("Unknown attribute type");
      } 
    } 
    Instances instances = new Instances("QueryResult", fastVector2, fastVector1.size());
    for (byte b4 = 0; b4 < fastVector1.size(); b4++)
      instances.add((Instance)fastVector1.elementAt(b4)); 
    resultSet.close();
    return instances;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      InstanceQuery instanceQuery = new InstanceQuery();
      String str = Utils.getOption('Q', paramArrayOfString);
      if (str.length() == 0) {
        instanceQuery.setQuery("select * from Experiment_index");
      } else {
        instanceQuery.setQuery(str);
      } 
      instanceQuery.setOptions(paramArrayOfString);
      try {
        Utils.checkForRemainingOptions(paramArrayOfString);
      } catch (Exception exception) {
        System.err.println("Options for weka.experiment.InstanceQuery:\n");
        Enumeration enumeration = instanceQuery.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          System.err.println(option.synopsis() + "\n" + option.description());
        } 
        System.exit(1);
      } 
      Instances instances = instanceQuery.retrieveInstances();
      instanceQuery.disconnectFromDatabase();
      System.out.println(new Instances(instances, 0));
      for (byte b = 0; b < instances.numInstances(); b++)
        System.out.println(instances.instance(b)); 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\InstanceQuery.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */