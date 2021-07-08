package weka.core.converters;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import weka.core.Utils;

public class DatabaseConnection implements Serializable {
  protected static String PROPERTY_FILE = "weka/experiment/DatabaseUtils.props";
  
  protected static Vector DRIVERS = new Vector();
  
  protected static Properties PROPERTIES;
  
  protected String m_DatabaseURL = PROPERTIES.getProperty("jdbcURL", "jdbc:idb=experiments.prp");
  
  protected PreparedStatement m_PreparedStatement;
  
  protected Connection m_Connection;
  
  protected boolean m_Debug = false;
  
  protected boolean m_checkForUpperCaseNames = false;
  
  protected boolean m_setAutoCommit = true;
  
  protected boolean m_createIndex = false;
  
  protected String m_userName = "";
  
  protected String m_password = "";
  
  public static final int STRING = 0;
  
  public static final int BOOL = 1;
  
  public static final int DOUBLE = 2;
  
  public static final int BYTE = 3;
  
  public static final int SHORT = 4;
  
  public static final int INTEGER = 5;
  
  public static final int LONG = 6;
  
  public static final int FLOAT = 7;
  
  public static final int DATE = 8;
  
  public DatabaseConnection() throws Exception {
    String str = PROPERTIES.getProperty("checkUpperCaseNames");
    if (str.equals("true")) {
      this.m_checkForUpperCaseNames = true;
    } else {
      this.m_checkForUpperCaseNames = false;
    } 
    str = PROPERTIES.getProperty("setAutoCommit");
    if (str.equals("true")) {
      this.m_setAutoCommit = true;
    } else {
      this.m_setAutoCommit = false;
    } 
    str = PROPERTIES.getProperty("createIndex");
    if (str.equals("true")) {
      this.m_createIndex = true;
    } else {
      this.m_createIndex = false;
    } 
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
  
  public String databaseURLTipText() {
    return "Set the URL to the database.";
  }
  
  public String getDatabaseURL() {
    return this.m_DatabaseURL;
  }
  
  public void setDatabaseURL(String paramString) {
    this.m_DatabaseURL = paramString;
  }
  
  public boolean getUpperCase() {
    return this.m_checkForUpperCaseNames;
  }
  
  int translateDBColumnType(String paramString) {
    return Integer.parseInt(PROPERTIES.getProperty(paramString));
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
  
  public void connectToDatabase() throws Exception {
    if (this.m_Debug)
      System.err.println("Connecting to " + this.m_DatabaseURL); 
    if (this.m_Connection == null)
      if (this.m_userName.equals("")) {
        this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL);
      } else {
        this.m_Connection = DriverManager.getConnection(this.m_DatabaseURL, this.m_userName, this.m_password);
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
  
  public DatabaseMetaData getMetaData() throws Exception {
    return this.m_Connection.getMetaData();
  }
  
  public boolean isConnected() {
    return (this.m_Connection != null);
  }
  
  public boolean execute(String paramString) throws SQLException {
    this.m_PreparedStatement = this.m_Connection.prepareStatement(paramString, 1003, 1007);
    return this.m_PreparedStatement.execute();
  }
  
  public ResultSet getResultSet() throws SQLException {
    return this.m_PreparedStatement.getResultSet();
  }
  
  public int getUpdateCount() throws SQLException {
    return this.m_PreparedStatement.getUpdateCount();
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
  
  private String safeDoubleToString(Double paramDouble) {
    String str = paramDouble.toString();
    int i = str.indexOf('E');
    if (i == -1 || str.charAt(i + 1) == '-')
      return str; 
    StringBuffer stringBuffer = new StringBuffer(str);
    stringBuffer.insert(i + 1, '+');
    return new String(stringBuffer);
  }
  
  static {
    try {
      PROPERTIES = Utils.readProperties(PROPERTY_FILE);
      String str = PROPERTIES.getProperty("jdbcDriver", "jdbc.idbDriver");
      if (str == null)
        throw new Exception("No jdbc drivers specified"); 
      StringTokenizer stringTokenizer = new StringTokenizer(str, ", ");
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        try {
          DRIVERS.addElement(Class.forName(str1).newInstance());
        } catch (Exception exception) {}
      } 
    } catch (Exception exception) {
      System.err.println("Problem reading properties. Fix before continuing.");
      System.err.println(exception);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\DatabaseConnection.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */