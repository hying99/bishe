package weka.experiment;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import weka.core.Utils;

public class DatabaseUtils implements Serializable {
  public static final String EXP_INDEX_TABLE = "Experiment_index";
  
  public static final String EXP_TYPE_COL = "Experiment_type";
  
  public static final String EXP_SETUP_COL = "Experiment_setup";
  
  public static final String EXP_RESULT_COL = "Result_table";
  
  public static final String EXP_RESULT_PREFIX = "Results";
  
  protected static String PROPERTY_FILE = "weka/experiment/DatabaseUtils.props";
  
  protected Vector DRIVERS = new Vector();
  
  protected Properties PROPERTIES;
  
  public static final int STRING = 0;
  
  public static final int BOOL = 1;
  
  public static final int DOUBLE = 2;
  
  public static final int BYTE = 3;
  
  public static final int SHORT = 4;
  
  public static final int INTEGER = 5;
  
  public static final int LONG = 6;
  
  public static final int FLOAT = 7;
  
  public static final int DATE = 8;
  
  protected String m_DatabaseURL;
  
  protected PreparedStatement m_PreparedStatement;
  
  protected Connection m_Connection;
  
  protected boolean m_Debug = true;
  
  protected String m_userName = "";
  
  protected String m_password = "";
  
  protected String m_stringType = "LONGVARCHAR";
  
  protected String m_intType = "INT";
  
  protected String m_doubleType = "DOUBLE";
  
  protected boolean m_checkForUpperCaseNames = false;
  
  protected boolean m_setAutoCommit = true;
  
  protected boolean m_createIndex = false;
  
  protected String attributeCaseFix(String paramString) {
    if (!this.m_checkForUpperCaseNames)
      return paramString; 
    String str = paramString.toUpperCase();
    return str.equals("Experiment_type".toUpperCase()) ? "Experiment_type" : (str.equals("Experiment_setup".toUpperCase()) ? "Experiment_setup" : (str.equals("Result_table".toUpperCase()) ? "Result_table" : paramString));
  }
  
  public void setUsername(String paramString) {
    this.m_userName = paramString;
  }
  
  public String getUsername() {
    return this.m_userName;
  }
  
  public void setPassword(String paramString) {
    this.m_password = paramString;
  }
  
  public String getPassword() {
    return this.m_password;
  }
  
  int translateDBColumnType(String paramString) {
    try {
      return Integer.parseInt(this.PROPERTIES.getProperty(paramString));
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("Unknown data type: " + paramString + ". Add entry " + "in weka/experiment/DatabaseUtils.props.");
    } 
  }
  
  public DatabaseUtils() throws Exception {
    try {
      this.PROPERTIES = Utils.readProperties(PROPERTY_FILE);
      String str1 = this.PROPERTIES.getProperty("jdbcDriver", "jdbc.idbDriver");
      if (str1 == null)
        throw new Exception("No jdbc drivers specified"); 
      StringTokenizer stringTokenizer = new StringTokenizer(str1, ", ");
      while (stringTokenizer.hasMoreTokens()) {
        String str2 = stringTokenizer.nextToken();
        this.DRIVERS.addElement(str2);
        System.err.println("Added driver: " + str2);
      } 
    } catch (Exception exception) {
      System.err.println("Problem reading properties. Fix before continuing.");
      System.err.println(exception);
    } 
    this.m_DatabaseURL = this.PROPERTIES.getProperty("jdbcURL", "jdbc:idb=experiments.prp");
    this.m_stringType = this.PROPERTIES.getProperty("CREATE_STRING");
    this.m_intType = this.PROPERTIES.getProperty("CREATE_INT");
    this.m_doubleType = this.PROPERTIES.getProperty("CREATE_DOUBLE");
    String str = this.PROPERTIES.getProperty("checkUpperCaseNames");
    if (str.equals("true")) {
      this.m_checkForUpperCaseNames = true;
    } else {
      this.m_checkForUpperCaseNames = false;
    } 
    str = this.PROPERTIES.getProperty("setAutoCommit");
    if (str.equals("true")) {
      this.m_setAutoCommit = true;
    } else {
      this.m_setAutoCommit = false;
    } 
    str = this.PROPERTIES.getProperty("createIndex");
    if (str.equals("true")) {
      this.m_createIndex = true;
    } else {
      this.m_createIndex = false;
    } 
  }
  
  public static String arrayToString(Object[] paramArrayOfObject) {
    String str = "";
    if (paramArrayOfObject == null) {
      str = "<null>";
    } else {
      for (byte b = 0; b < paramArrayOfObject.length; b++) {
        if (paramArrayOfObject[b] == null) {
          str = str + " ?";
        } else {
          str = str + " " + paramArrayOfObject[b];
        } 
      } 
    } 
    return str;
  }
  
  public static String typeName(int paramInt) {
    switch (paramInt) {
      case -5:
        return "BIGINT ";
      case -2:
        return "BINARY";
      case -7:
        return "BIT";
      case 1:
        return "CHAR";
      case 91:
        return "DATE";
      case 3:
        return "DECIMAL";
      case 8:
        return "DOUBLE";
      case 6:
        return "FLOAT";
      case 4:
        return "INTEGER";
      case -4:
        return "LONGVARBINARY";
      case -1:
        return "LONGVARCHAR";
      case 0:
        return "NULL";
      case 2:
        return "NUMERIC";
      case 1111:
        return "OTHER";
      case 7:
        return "REAL";
      case 5:
        return "SMALLINT";
      case 92:
        return "TIME";
      case 93:
        return "TIMESTAMP";
      case -6:
        return "TINYINT";
      case -3:
        return "VARBINARY";
      case 12:
        return "VARCHAR";
    } 
    return "Unknown";
  }
  
  public String databaseURLTipText() {
    return "Set the URL to the database.";
  }
  
  public String getDatabaseURL() {
    return this.m_DatabaseURL;
  }
  
  public void setDatabaseURL(String paramString) {
    this.m_DatabaseURL = paramString;
  }
  
  public void connectToDatabase() throws Exception {
    if (this.m_Debug)
      System.err.println("Connecting to " + this.m_DatabaseURL); 
    if (this.m_Connection == null)
      if (this.m_userName.equals("")) {
        try {
          this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL);
        } catch (SQLException sQLException) {
          for (byte b = 0; b < this.DRIVERS.size(); b++) {
            try {
              Class.forName(this.DRIVERS.elementAt(b));
            } catch (Exception exception) {}
          } 
          this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL);
        } 
      } else {
        try {
          this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL, this.m_userName, this.m_password);
        } catch (SQLException sQLException) {
          for (byte b = 0; b < this.DRIVERS.size(); b++) {
            try {
              Class.forName(this.DRIVERS.elementAt(b));
            } catch (Exception exception) {}
          } 
          this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL, this.m_userName, this.m_password);
        } 
      }  
    if (this.m_setAutoCommit) {
      this.m_Connection.setAutoCommit(true);
    } else {
      this.m_Connection.setAutoCommit(false);
    } 
  }
  
  public void disconnectFromDatabase() throws Exception {
    if (this.m_Debug)
      System.err.println("Disconnecting from " + this.m_DatabaseURL); 
    if (this.m_Connection != null) {
      this.m_Connection.close();
      this.m_Connection = null;
    } 
  }
  
  public boolean isConnected() {
    return (this.m_Connection != null);
  }
  
  public boolean execute(String paramString) throws SQLException {
    this.m_PreparedStatement = this.m_Connection.prepareStatement(paramString);
    return this.m_PreparedStatement.execute();
  }
  
  public ResultSet getResultSet() throws SQLException {
    return this.m_PreparedStatement.getResultSet();
  }
  
  public boolean tableExists(String paramString) throws Exception {
    ResultSet resultSet;
    if (this.m_Debug)
      System.err.println("Checking if table " + paramString + " exists..."); 
    DatabaseMetaData databaseMetaData = this.m_Connection.getMetaData();
    if (this.m_checkForUpperCaseNames == true) {
      resultSet = databaseMetaData.getTables(null, null, paramString.toUpperCase(), null);
    } else {
      resultSet = databaseMetaData.getTables(null, null, paramString, null);
    } 
    boolean bool = resultSet.next();
    if (resultSet.next())
      throw new Exception("This table seems to exist more than once!"); 
    resultSet.close();
    if (this.m_Debug)
      if (bool) {
        System.err.println("... " + paramString + " exists");
      } else {
        System.err.println("... " + paramString + " does not exist");
      }  
    return bool;
  }
  
  protected boolean isKeyInTable(String paramString, ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    String str = "SELECT Key_Run FROM " + paramString;
    String[] arrayOfString = paramResultProducer.getKeyNames();
    if (arrayOfString.length != paramArrayOfObject.length)
      throw new Exception("Key names and key values of different lengths"); 
    boolean bool = true;
    byte b;
    for (b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] != null) {
        if (bool) {
          str = str + " WHERE ";
          bool = false;
        } else {
          str = str + " AND ";
        } 
        str = str + "Key_" + arrayOfString[b] + '=';
        if (paramArrayOfObject[b] instanceof String) {
          str = str + '\'' + paramArrayOfObject[b].toString() + '\'';
        } else {
          str = str + paramArrayOfObject[b].toString();
        } 
      } 
    } 
    b = 0;
    if (execute(str)) {
      ResultSet resultSet = this.m_PreparedStatement.getResultSet();
      int i = resultSet.getMetaData().getColumnCount();
      if (resultSet.next()) {
        b = 1;
        if (resultSet.next())
          throw new Exception("More than one result entry for result key: " + str); 
      } 
      resultSet.close();
    } 
    return b;
  }
  
  public Object[] getResultFromTable(String paramString, ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    String str = "SELECT ";
    String[] arrayOfString1 = paramResultProducer.getResultNames();
    for (byte b1 = 0; b1 < arrayOfString1.length; b1++) {
      if (b1 != 0)
        str = str + ", "; 
      str = str + arrayOfString1[b1];
    } 
    str = str + " FROM " + paramString;
    String[] arrayOfString2 = paramResultProducer.getKeyNames();
    if (arrayOfString2.length != paramArrayOfObject.length)
      throw new Exception("Key names and key values of different lengths"); 
    boolean bool = true;
    for (byte b2 = 0; b2 < paramArrayOfObject.length; b2++) {
      if (paramArrayOfObject[b2] != null) {
        if (bool) {
          str = str + " WHERE ";
          bool = false;
        } else {
          str = str + " AND ";
        } 
        str = str + "Key_" + arrayOfString2[b2] + '=';
        if (paramArrayOfObject[b2] instanceof String) {
          str = str + "'" + paramArrayOfObject[b2].toString() + "'";
        } else {
          str = str + paramArrayOfObject[b2].toString();
        } 
      } 
    } 
    if (!execute(str))
      throw new Exception("Couldn't execute query: " + str); 
    ResultSet resultSet = this.m_PreparedStatement.getResultSet();
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    int i = resultSetMetaData.getColumnCount();
    if (!resultSet.next())
      throw new Exception("No result for query: " + str); 
    Object[] arrayOfObject = new Object[i];
    for (byte b3 = 1; b3 <= i; b3++) {
      switch (translateDBColumnType(resultSetMetaData.getColumnTypeName(b3))) {
        case 0:
          arrayOfObject[b3 - 1] = resultSet.getString(b3);
          if (resultSet.wasNull())
            arrayOfObject[b3 - 1] = null; 
          break;
        case 2:
        case 7:
          arrayOfObject[b3 - 1] = new Double(resultSet.getDouble(b3));
          if (resultSet.wasNull())
            arrayOfObject[b3 - 1] = null; 
          break;
        default:
          throw new Exception("Unhandled SQL result type (field " + (b3 + 1) + "): " + typeName(resultSetMetaData.getColumnType(b3)));
      } 
    } 
    if (resultSet.next())
      throw new Exception("More than one result entry for result key: " + str); 
    resultSet.close();
    return arrayOfObject;
  }
  
  public void putResultInTable(String paramString, ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception {
    String str = "INSERT INTO " + paramString + " VALUES ( ";
    byte b;
    for (b = 0; b < paramArrayOfObject1.length; b++) {
      if (b != 0)
        str = str + ','; 
      if (paramArrayOfObject1[b] != null) {
        if (paramArrayOfObject1[b] instanceof String) {
          str = str + '\'' + paramArrayOfObject1[b].toString() + '\'';
        } else if (paramArrayOfObject1[b] instanceof Double) {
          str = str + safeDoubleToString((Double)paramArrayOfObject1[b]);
        } else {
          str = str + paramArrayOfObject1[b].toString();
        } 
      } else {
        str = str + "NULL";
      } 
    } 
    for (b = 0; b < paramArrayOfObject2.length; b++) {
      str = str + ',';
      if (paramArrayOfObject2[b] != null) {
        if (paramArrayOfObject2[b] instanceof String) {
          str = str + "'" + paramArrayOfObject2[b].toString() + "'";
        } else if (paramArrayOfObject2[b] instanceof Double) {
          str = str + safeDoubleToString((Double)paramArrayOfObject2[b]);
        } else {
          str = str + paramArrayOfObject2[b].toString();
        } 
      } else {
        str = str + "NULL";
      } 
    } 
    str = str + ')';
    if (this.m_Debug)
      System.err.println("Submitting result: " + str); 
    if (execute(str) && this.m_Debug)
      System.err.println("...acceptResult returned resultset"); 
  }
  
  private String safeDoubleToString(Double paramDouble) {
    String str = paramDouble.toString();
    int i = str.indexOf('E');
    if (i == -1 || str.charAt(i + 1) == '-')
      return str; 
    StringBuffer stringBuffer = new StringBuffer(str);
    stringBuffer.insert(i + 1, '+');
    return new String(stringBuffer);
  }
  
  public boolean experimentIndexExists() throws Exception {
    return tableExists("Experiment_index");
  }
  
  public void createExperimentIndex() throws Exception {
    if (this.m_Debug)
      System.err.println("Creating experiment index table..."); 
    String str = "CREATE TABLE Experiment_index ( Experiment_type " + this.m_stringType + "," + "  " + "Experiment_setup" + " " + this.m_stringType + "," + "  " + "Result_table" + " " + this.m_intType + " )";
    if (execute(str) && this.m_Debug)
      System.err.println("...create returned resultset"); 
  }
  
  public String createExperimentIndexEntry(ResultProducer paramResultProducer) throws Exception {
    if (this.m_Debug)
      System.err.println("Creating experiment index entry..."); 
    int i = 0;
    String str1 = "SELECT COUNT(*) FROM Experiment_index";
    if (execute(str1)) {
      if (this.m_Debug)
        System.err.println("...getting number of rows"); 
      ResultSet resultSet = this.m_PreparedStatement.getResultSet();
      if (resultSet.next())
        i = resultSet.getInt(1); 
      resultSet.close();
    } 
    String str2 = paramResultProducer.getClass().getName();
    String str3 = paramResultProducer.getCompatibilityState();
    str1 = "INSERT INTO Experiment_index VALUES ('" + str2 + "', '" + str3 + "', " + i + " )";
    if (execute(str1) && this.m_Debug)
      System.err.println("...create returned resultset"); 
    if (!this.m_setAutoCommit) {
      this.m_Connection.commit();
      this.m_Connection.setAutoCommit(true);
    } 
    String str4 = getResultsTableName(paramResultProducer);
    if (str4 == null)
      throw new Exception("Problem adding experiment index entry"); 
    try {
      str1 = "DROP TABLE " + str4;
      if (this.m_Debug)
        System.err.println(str1); 
      execute(str1);
    } catch (SQLException sQLException) {
      System.err.println(sQLException.getMessage());
    } 
    return str4;
  }
  
  public String getResultsTableName(ResultProducer paramResultProducer) throws Exception {
    if (this.m_Debug)
      System.err.println("Getting results table name..."); 
    String str1 = paramResultProducer.getClass().getName();
    String str2 = paramResultProducer.getCompatibilityState();
    String str3 = "SELECT Result_table FROM Experiment_index WHERE Experiment_type='" + str1 + "' AND " + "Experiment_setup" + "='" + str2 + "'";
    String str4 = null;
    if (execute(str3)) {
      ResultSet resultSet = this.m_PreparedStatement.getResultSet();
      int i = resultSet.getMetaData().getColumnCount();
      if (resultSet.next()) {
        str4 = resultSet.getString(1);
        if (resultSet.next())
          throw new Exception("More than one index entry for experiment config: " + str3); 
      } 
      resultSet.close();
    } 
    if (this.m_Debug)
      System.err.println("...results table = " + ((str4 == null) ? "<null>" : ("Results" + str4))); 
    return (str4 == null) ? str4 : ("Results" + str4);
  }
  
  public String createResultsTable(ResultProducer paramResultProducer, String paramString) throws Exception {
    if (this.m_Debug)
      System.err.println("Creating results table " + paramString + "..."); 
    String str = "CREATE TABLE " + paramString + " ( ";
    String[] arrayOfString = paramResultProducer.getKeyNames();
    Object[] arrayOfObject = paramResultProducer.getKeyTypes();
    if (arrayOfString.length != arrayOfObject.length)
      throw new Exception("key names types differ in length"); 
    byte b;
    for (b = 0; b < arrayOfString.length; b++) {
      str = str + "Key_" + arrayOfString[b] + " ";
      if (arrayOfObject[b] instanceof Double) {
        str = str + this.m_doubleType;
      } else if (arrayOfObject[b] instanceof String) {
        str = str + this.m_stringType + " ";
      } else {
        throw new Exception("Unknown/unsupported field type in key");
      } 
      str = str + ", ";
    } 
    arrayOfString = paramResultProducer.getResultNames();
    arrayOfObject = paramResultProducer.getResultTypes();
    if (arrayOfString.length != arrayOfObject.length)
      throw new Exception("result names and types differ in length"); 
    for (b = 0; b < arrayOfString.length; b++) {
      str = str + arrayOfString[b] + " ";
      if (arrayOfObject[b] instanceof Double) {
        str = str + this.m_doubleType;
      } else if (arrayOfObject[b] instanceof String) {
        str = str + this.m_stringType + " ";
      } else {
        throw new Exception("Unknown/unsupported field type in key");
      } 
      if (b < arrayOfString.length - 1)
        str = str + ", "; 
    } 
    str = str + " )";
    if (execute(str) && this.m_Debug)
      System.err.println("...create returned resultset"); 
    System.err.println("table created");
    if (this.m_createIndex) {
      str = "CREATE UNIQUE INDEX Key_IDX ON " + paramString + " (";
      String[] arrayOfString1 = paramResultProducer.getKeyNames();
      boolean bool = true;
      for (byte b1 = 0; b1 < arrayOfString1.length; b1++) {
        if (arrayOfString1[b1] != null)
          if (bool) {
            bool = false;
            str = str + "Key_" + arrayOfString1[b1];
          } else {
            str = str + ",Key_" + arrayOfString1[b1];
          }  
      } 
      str = str + ")";
      if (execute(str) && this.m_Debug)
        System.err.println("...create index returned resultset"); 
    } 
    return paramString;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\DatabaseUtils.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */