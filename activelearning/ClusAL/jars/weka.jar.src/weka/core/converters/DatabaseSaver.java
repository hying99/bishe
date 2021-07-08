package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class DatabaseSaver extends AbstractSaver implements BatchConverter, IncrementalConverter, DatabaseConverter, OptionHandler {
  private DatabaseConnection m_DataBaseConnection;
  
  private String m_tableName;
  
  private String m_inputFile;
  
  private String m_createText;
  
  private String m_createDouble;
  
  private String m_createInt;
  
  private String m_idColumn;
  
  private int m_count;
  
  private boolean m_id;
  
  private boolean m_tabName;
  
  protected static String PROPERTY_FILE = "weka/experiment/DatabaseUtils.props";
  
  protected static Properties PROPERTIES;
  
  public DatabaseSaver() throws Exception {
    resetOptions();
    this.m_createText = PROPERTIES.getProperty("CREATE_STRING");
    this.m_createDouble = PROPERTIES.getProperty("CREATE_DOUBLE");
    this.m_createInt = PROPERTIES.getProperty("CREATE_INT");
    this.m_idColumn = PROPERTIES.getProperty("idColumn");
  }
  
  public void resetOptions() {
    super.resetOptions();
    setRetrieval(0);
    this.m_tableName = "";
    this.m_count = 1;
    this.m_id = false;
    this.m_tabName = true;
    try {
      if (this.m_DataBaseConnection != null && this.m_DataBaseConnection.isConnected())
        this.m_DataBaseConnection.disconnectFromDatabase(); 
      this.m_DataBaseConnection = new DatabaseConnection();
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void cancel() {
    if (getWriteMode() == 2) {
      try {
        this.m_DataBaseConnection.execute("DROP TABLE " + this.m_tableName);
        if (this.m_DataBaseConnection.tableExists(this.m_tableName))
          System.err.println("Table cannot be dropped."); 
      } catch (Exception exception) {
        printException(exception);
      } 
      resetOptions();
    } 
  }
  
  public String globalInfo() {
    return "Writes to a database";
  }
  
  public void setTableName(String paramString) {
    this.m_tableName = paramString;
  }
  
  public String getTableName() {
    return this.m_tableName;
  }
  
  public String tableNameTipText() {
    return "Sets the name of the table.";
  }
  
  public void setAutoKeyGeneration(boolean paramBoolean) {
    this.m_id = paramBoolean;
  }
  
  public boolean getAutoKeyGeneration() {
    return this.m_id;
  }
  
  public String autoKeyGenerationTipText() {
    return "If set to true, a primary key column is generated automatically (containing the row number as INTEGER). The name of the key is read from DatabaseUtils (idColumn) This primary key can be used for incremental loading (requires an unique key). This primary key will not be loaded as an attribute.";
  }
  
  public void setRelationForTableName(boolean paramBoolean) {
    this.m_tabName = paramBoolean;
  }
  
  public boolean getRelationForTableName() {
    return this.m_tabName;
  }
  
  public String relationForTableNameTipText() {
    return "If set to true, the relation name will be used as name for the database table. Otherwise the user has to provide a table name.";
  }
  
  public void setUrl(String paramString) {
    this.m_DataBaseConnection.setDatabaseURL(paramString);
  }
  
  public String getUrl() {
    return this.m_DataBaseConnection.getDatabaseURL();
  }
  
  public String urlTipText() {
    return "The URL of the database";
  }
  
  public void setUser(String paramString) {
    this.m_DataBaseConnection.setUsername(paramString);
  }
  
  public String getUser() {
    return this.m_DataBaseConnection.getUsername();
  }
  
  public String userTipText() {
    return "The user name for the database";
  }
  
  public void setPassword(String paramString) {
    this.m_DataBaseConnection.setPassword(paramString);
  }
  
  public String passwordTipText() {
    return "The database password";
  }
  
  public void setDestination(String paramString1, String paramString2, String paramString3) {
    try {
      this.m_DataBaseConnection = new DatabaseConnection();
      this.m_DataBaseConnection.setDatabaseURL(paramString1);
      this.m_DataBaseConnection.setUsername(paramString2);
      this.m_DataBaseConnection.setPassword(paramString3);
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void setDestination(String paramString) {
    try {
      this.m_DataBaseConnection = new DatabaseConnection();
      this.m_DataBaseConnection.setDatabaseURL(paramString);
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void setDestination() {
    try {
      this.m_DataBaseConnection = new DatabaseConnection();
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void connectToDatabase() {
    try {
      if (!this.m_DataBaseConnection.isConnected())
        this.m_DataBaseConnection.connectToDatabase(); 
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  private void writeStructure() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    Instances instances = getInstances();
    stringBuffer.append("CREATE TABLE ");
    if (this.m_tabName || this.m_tableName.equals(""))
      this.m_tableName = instances.relationName(); 
    if (this.m_DataBaseConnection.getUpperCase()) {
      this.m_tableName = this.m_tableName.toUpperCase();
      this.m_createInt = this.m_createInt.toUpperCase();
      this.m_createDouble = this.m_createDouble.toUpperCase();
      this.m_createText = this.m_createText.toUpperCase();
    } 
    this.m_tableName = this.m_tableName.replaceAll("[^\\w]", "_");
    stringBuffer.append(this.m_tableName);
    if (instances.numAttributes() == 0)
      throw new Exception("Instances have no attribute."); 
    stringBuffer.append(" ( ");
    if (this.m_id) {
      if (this.m_DataBaseConnection.getUpperCase())
        this.m_idColumn = this.m_idColumn.toUpperCase(); 
      stringBuffer.append(this.m_idColumn);
      stringBuffer.append(" ");
      stringBuffer.append(this.m_createInt);
      stringBuffer.append(" PRIMARY KEY,");
    } 
    for (byte b = 0; b < instances.numAttributes(); b++) {
      Attribute attribute = instances.attribute(b);
      String str = attribute.name();
      str = str.replaceAll("[^\\w]", "_");
      if (this.m_DataBaseConnection.getUpperCase()) {
        stringBuffer.append(str.toUpperCase());
      } else {
        stringBuffer.append(str);
      } 
      if (attribute.isDate()) {
        stringBuffer.append(" DATE");
      } else if (attribute.isNumeric()) {
        stringBuffer.append(" " + this.m_createDouble);
      } else {
        stringBuffer.append(" " + this.m_createText);
      } 
      if (b != instances.numAttributes() - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append(" )");
    this.m_DataBaseConnection.execute(stringBuffer.toString());
    if (!this.m_DataBaseConnection.tableExists(this.m_tableName))
      throw new IOException("Table cannot be built."); 
  }
  
  private void writeInstance(Instance paramInstance) throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("INSERT INTO ");
    stringBuffer.append(this.m_tableName);
    stringBuffer.append(" VALUES ( ");
    if (this.m_id) {
      stringBuffer.append(this.m_count);
      stringBuffer.append(", ");
      this.m_count++;
    } 
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (paramInstance.isMissing(b)) {
        stringBuffer.append("NULL");
      } else if (paramInstance.attribute(b).isNumeric()) {
        stringBuffer.append(paramInstance.value(b));
      } else {
        String str = "'" + paramInstance.stringValue(b) + "'";
        str = str.replaceAll("''", "'");
        stringBuffer.append(str);
      } 
      if (b != paramInstance.numAttributes() - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append(" )");
    if (!this.m_DataBaseConnection.execute(stringBuffer.toString()) && this.m_DataBaseConnection.getUpdateCount() < 1)
      throw new IOException("Tuple cannot be inserted."); 
  }
  
  public void writeIncremental(Instance paramInstance) throws IOException {
    int i = getWriteMode();
    Instances instances = getInstances();
    if (this.m_DataBaseConnection == null)
      throw new IOException("No database has been set up."); 
    if (getRetrieval() == 1)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    setRetrieval(2);
    try {
      if (!this.m_DataBaseConnection.isConnected())
        connectToDatabase(); 
      if (i == 1) {
        if (instances == null) {
          setWriteMode(2);
          if (paramInstance != null)
            throw new Exception("Structure(Header Information) has to be set in advance"); 
        } else {
          setWriteMode(3);
        } 
        i = getWriteMode();
      } 
      if (i == 2)
        cancel(); 
      if (i == 3) {
        setWriteMode(0);
        writeStructure();
        i = getWriteMode();
      } 
      if (i == 0) {
        if (instances == null)
          throw new IOException("No instances information available."); 
        if (paramInstance != null) {
          writeInstance(paramInstance);
        } else {
          this.m_DataBaseConnection.disconnectFromDatabase();
          resetStructure();
          this.m_count = 1;
        } 
      } 
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void writeBatch() throws IOException {
    Instances instances = getInstances();
    if (instances == null)
      throw new IOException("No instances to save"); 
    if (getRetrieval() == 2)
      throw new IOException("Batch and incremental saving cannot be mixed."); 
    if (this.m_DataBaseConnection == null)
      throw new IOException("No database has been set up."); 
    setRetrieval(1);
    try {
      if (!this.m_DataBaseConnection.isConnected())
        connectToDatabase(); 
      setWriteMode(0);
      writeStructure();
      for (byte b = 0; b < instances.numInstances(); b++)
        writeInstance(instances.instance(b)); 
      this.m_DataBaseConnection.disconnectFromDatabase();
      setWriteMode(1);
      resetStructure();
      this.m_count = 1;
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  private void printException(Exception paramException) {
    System.out.println("\n--- Exception caught ---\n");
    while (paramException != null) {
      System.out.println("Message:   " + paramException.getMessage());
      if (paramException instanceof SQLException) {
        System.out.println("SQLState:  " + ((SQLException)paramException).getSQLState());
        System.out.println("ErrorCode: " + ((SQLException)paramException).getErrorCode());
        paramException = ((SQLException)paramException).getNextException();
      } else {
        paramException = null;
      } 
      System.out.println("");
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b] = "-T";
    arrayOfString[b++] = this.m_tableName;
    if (this.m_id)
      arrayOfString[b] = "-P"; 
    arrayOfString[b] = "-i";
    arrayOfString[b++] = this.m_inputFile;
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public Enumeration listOptions() {
    FastVector fastVector = new FastVector(3);
    fastVector.addElement(new Option("\tThe name of the table (default: the relation name).", "T", 1, "-T <table name>"));
    fastVector.addElement(new Option("\tAdd an ID column as primary key. The name is specified in the DatabaseUtils file. The DatabaseLoader won't load this column.", "P", 0, "-P"));
    fastVector.addElement(new Option("\tInput file in arff format that should be saved in database.", "i", 1, "-i<input file name>"));
    return fastVector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('T', paramArrayOfString);
    String str2 = Utils.getOption('i', paramArrayOfString);
    resetOptions();
    if (str1.length() != 0) {
      this.m_tableName = str1;
      this.m_tabName = false;
    } 
    this.m_id = Utils.getFlag('P', paramArrayOfString);
    if (str2.length() != 0)
      try {
        this.m_inputFile = str2;
        ArffLoader arffLoader = new ArffLoader();
        File file = new File(str2);
        arffLoader.setSource(file);
        setInstances(arffLoader.getDataSet());
        if (str1.length() == 0)
          this.m_tableName = getInstances().relationName(); 
      } catch (Exception exception) {
        printException(exception);
        exception.printStackTrace();
      }  
  }
  
  public static void main(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("\n\nDatabaseSaver options:\n");
    try {
      DatabaseSaver databaseSaver = new DatabaseSaver();
      try {
        Enumeration enumeration = databaseSaver.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          stringBuffer.append(option.synopsis() + '\n');
          stringBuffer.append(option.description() + '\n');
        } 
        databaseSaver.setOptions(paramArrayOfString);
        databaseSaver.setDestination();
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      databaseSaver.writeBatch();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(stringBuffer);
    } 
  }
  
  static {
    try {
      PROPERTIES = Utils.readProperties(PROPERTY_FILE);
    } catch (Exception exception) {
      System.err.println("Problem reading properties. Fix before continuing.");
      System.err.println(exception);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\DatabaseSaver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */