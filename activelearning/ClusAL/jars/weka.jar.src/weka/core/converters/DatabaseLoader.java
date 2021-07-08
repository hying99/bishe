package weka.core.converters;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

public class DatabaseLoader extends AbstractLoader implements BatchConverter, IncrementalConverter, DatabaseConverter, OptionHandler {
  protected Instances m_structure;
  
  private Instances m_datasetPseudoInc;
  
  private Instances m_oldStructure;
  
  private DatabaseConnection m_DataBaseConnection;
  
  private String m_query;
  
  private boolean m_pseudoIncremental;
  
  private int m_nominalToStringLimit;
  
  private int m_rowCount;
  
  private int m_counter;
  
  private int m_choice;
  
  private boolean m_firstTime;
  
  private boolean m_inc;
  
  private FastVector m_orderBy;
  
  private Hashtable[] m_nominalIndexes;
  
  private FastVector[] m_nominalStrings;
  
  private String m_idColumn;
  
  public static final int STRING = 0;
  
  public static final int BOOL = 1;
  
  public static final int DOUBLE = 2;
  
  public static final int BYTE = 3;
  
  public static final int SHORT = 4;
  
  public static final int INTEGER = 5;
  
  public static final int LONG = 6;
  
  public static final int FLOAT = 7;
  
  public static final int DATE = 8;
  
  protected static String PROPERTY_FILE = "weka/experiment/DatabaseUtils.props";
  
  protected static Properties PROPERTIES;
  
  public DatabaseLoader() throws Exception {
    reset();
    this.m_pseudoIncremental = false;
    String str = PROPERTIES.getProperty("nominalToStringLimit");
    this.m_nominalToStringLimit = Integer.parseInt(str);
    this.m_idColumn = PROPERTIES.getProperty("idColumn");
  }
  
  public String globalInfo() {
    return "Reads Instances from a Database";
  }
  
  public void reset() throws Exception {
    resetStructure();
    if (this.m_DataBaseConnection != null && this.m_DataBaseConnection.isConnected())
      this.m_DataBaseConnection.disconnectFromDatabase(); 
    this.m_DataBaseConnection = new DatabaseConnection();
    this.m_orderBy = new FastVector();
    this.m_query = "Select * from Results0";
    this.m_inc = false;
  }
  
  public void resetStructure() {
    this.m_structure = null;
    this.m_datasetPseudoInc = null;
    this.m_oldStructure = null;
    this.m_rowCount = 0;
    this.m_counter = 0;
    this.m_choice = 0;
    this.m_firstTime = true;
    setRetrieval(0);
  }
  
  public void setQuery(String paramString) {
    paramString = paramString.replaceAll("[fF][rR][oO][mM]", "FROM");
    paramString = paramString.replaceFirst("[sS][eE][lL][eE][cC][tT]", "SELECT");
    this.m_query = paramString;
  }
  
  public String getQuery() {
    return this.m_query;
  }
  
  public String queryTipText() {
    return "The query that should load the instances.\n The query has to be of the form SELECT <column-list>|* FROM <table> [WHERE <conditions>]";
  }
  
  public void setKeys(String paramString) {
    this.m_orderBy.removeAllElements();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      str = str.replaceAll(" ", "");
      this.m_orderBy.addElement(str);
    } 
  }
  
  public String getKeys() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.m_orderBy.size(); b++) {
      stringBuffer.append((String)this.m_orderBy.elementAt(b));
      if (b != this.m_orderBy.size() - 1)
        stringBuffer.append(", "); 
    } 
    return stringBuffer.toString();
  }
  
  public String keysTipText() {
    return "For incremental loading a unique identiefer has to be specified.\nIf the query includes all columns of a table (SELECT *...) a primary key\ncan be detected automatically depending on the JDBC driver. If that is not possible\nspecify the key columns here in a comma separated list.";
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
  
  public void setSource(String paramString1, String paramString2, String paramString3) {
    try {
      this.m_DataBaseConnection = new DatabaseConnection();
      this.m_DataBaseConnection.setDatabaseURL(paramString1);
      this.m_DataBaseConnection.setUsername(paramString2);
      this.m_DataBaseConnection.setPassword(paramString3);
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void setSource(String paramString) {
    try {
      this.m_DataBaseConnection = new DatabaseConnection();
      this.m_DataBaseConnection.setDatabaseURL(paramString);
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  public void setSource() throws Exception {
    this.m_DataBaseConnection = new DatabaseConnection();
  }
  
  public void connectToDatabase() {
    try {
      if (!this.m_DataBaseConnection.isConnected())
        this.m_DataBaseConnection.connectToDatabase(); 
    } catch (Exception exception) {
      printException(exception);
    } 
  }
  
  private String endOfQuery(boolean paramBoolean) {
    String str;
    int i;
    for (i = this.m_query.indexOf("FROM ") + 5; this.m_query.charAt(i) == ' '; i++);
    int j = this.m_query.indexOf(" ", i);
    if (j != -1 && paramBoolean) {
      str = this.m_query.substring(i, j);
    } else {
      str = this.m_query.substring(i);
    } 
    if (this.m_DataBaseConnection.getUpperCase())
      str = str.toUpperCase(); 
    return str;
  }
  
  private boolean checkForKey() throws Exception {
    String str1 = this.m_query;
    str1 = str1.replaceAll(" +", " ");
    if (!str1.startsWith("SELECT *"))
      return false; 
    this.m_orderBy.removeAllElements();
    if (!this.m_DataBaseConnection.isConnected())
      this.m_DataBaseConnection.connectToDatabase(); 
    DatabaseMetaData databaseMetaData = this.m_DataBaseConnection.getMetaData();
    String str2 = endOfQuery(true);
    ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, str2);
    while (resultSet.next())
      this.m_orderBy.addElement(resultSet.getString(4)); 
    resultSet.close();
    if (this.m_orderBy.size() != 0)
      return true; 
    resultSet = databaseMetaData.getBestRowIdentifier(null, null, str2, 2, false);
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    byte b;
    for (b = 0; resultSet.next(); b++)
      this.m_orderBy.addElement(resultSet.getString(2)); 
    resultSet.close();
    if (b == resultSetMetaData.getColumnCount())
      this.m_orderBy.removeAllElements(); 
    return (this.m_orderBy.size() != 0);
  }
  
  private void stringToNominal(ResultSet paramResultSet, int paramInt) throws Exception {
    while (paramResultSet.next()) {
      String str = paramResultSet.getString(1);
      if (!paramResultSet.wasNull()) {
        Double double_ = this.m_nominalIndexes[paramInt - 1].get(str);
        if (double_ == null) {
          double_ = new Double(this.m_nominalStrings[paramInt - 1].size());
          this.m_nominalIndexes[paramInt - 1].put(str, double_);
          this.m_nominalStrings[paramInt - 1].addElement(str);
        } 
      } 
    } 
  }
  
  private String limitQuery(String paramString, int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer();
    String str = "";
    if (this.m_orderBy.size() != 0) {
      stringBuffer.append(" ORDER BY ");
      for (byte b = 0; b < this.m_orderBy.size() - 1; b++) {
        if (this.m_DataBaseConnection.getUpperCase()) {
          stringBuffer.append(((String)this.m_orderBy.elementAt(b)).toUpperCase());
        } else {
          stringBuffer.append((String)this.m_orderBy.elementAt(b));
        } 
        stringBuffer.append(", ");
      } 
      if (this.m_DataBaseConnection.getUpperCase()) {
        stringBuffer.append(((String)this.m_orderBy.elementAt(this.m_orderBy.size() - 1)).toUpperCase());
      } else {
        stringBuffer.append((String)this.m_orderBy.elementAt(this.m_orderBy.size() - 1));
      } 
      str = stringBuffer.toString();
    } 
    if (paramInt2 == 0) {
      null = paramString.replaceFirst("SELECT", "SELECT LIMIT " + paramInt1 + " 1");
      return null.concat(str);
    } 
    return (paramInt2 == 1) ? paramString.concat(str + " LIMIT 1 OFFSET " + paramInt1) : paramString.concat(str + " LIMIT " + paramInt1 + ", 1");
  }
  
  private int getRowCount() throws Exception {
    String str = "SELECT COUNT(*) FROM " + endOfQuery(false);
    if (!this.m_DataBaseConnection.execute(str))
      throw new Exception("Cannot count results tuples."); 
    ResultSet resultSet = this.m_DataBaseConnection.getResultSet();
    resultSet.next();
    int i = resultSet.getInt(1);
    resultSet.close();
    return i;
  }
  
  public Instances getStructure() throws IOException {
    if (this.m_DataBaseConnection == null)
      throw new IOException("No source database has been specified"); 
    connectToDatabase();
    try {
      if (this.m_pseudoIncremental && this.m_structure == null) {
        if (getRetrieval() == 1)
          throw new IOException("Cannot mix getting instances in both incremental and batch modes"); 
        setRetrieval(0);
        this.m_datasetPseudoInc = getDataSet();
        this.m_structure = new Instances(this.m_datasetPseudoInc, 0);
        setRetrieval(0);
        return this.m_structure;
      } 
      if (this.m_structure == null) {
        if (!this.m_DataBaseConnection.tableExists(endOfQuery(true)))
          throw new IOException("Table does not exist."); 
        byte b1 = 0;
        boolean bool = false;
        while (!bool) {
          try {
            if (!this.m_DataBaseConnection.execute(limitQuery(this.m_query, 0, b1)))
              throw new IOException("Query didn't produce results"); 
            this.m_choice = b1;
            bool = true;
          } catch (SQLException sQLException) {
            if (++b1 == 3) {
              System.out.println("Incremental loading not supported for that DBMS. Pseudoincremental mode is used if you use incremental loading.\nAll rows are loaded into memory once and retrieved incrementally from memory instead of from the database.");
              this.m_pseudoIncremental = true;
              return this.m_oldStructure;
            } 
          } 
        } 
        String str = endOfQuery(false);
        ResultSet resultSet = this.m_DataBaseConnection.getResultSet();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        resultSet.close();
        int i = resultSetMetaData.getColumnCount();
        int[] arrayOfInt = new int[i];
        this.m_nominalIndexes = new Hashtable[i];
        this.m_nominalStrings = new FastVector[i];
        for (byte b2 = 1; b2 <= i; b2++) {
          ResultSet resultSet1;
          String str1;
          String str2;
          switch (this.m_DataBaseConnection.translateDBColumnType(resultSetMetaData.getColumnTypeName(b2))) {
            case 0:
              str1 = resultSetMetaData.getColumnName(b2);
              if (this.m_DataBaseConnection.getUpperCase())
                str1 = str1.toUpperCase(); 
              this.m_nominalIndexes[b2 - 1] = new Hashtable();
              this.m_nominalStrings[b2 - 1] = new FastVector();
              str2 = "SELECT COUNT(DISTINCT( " + str1 + " )) FROM " + str;
              if (this.m_DataBaseConnection.execute(str2) == true) {
                resultSet1 = this.m_DataBaseConnection.getResultSet();
                resultSet1.next();
                int j = resultSet1.getInt(1);
                resultSet1.close();
                if (j > this.m_nominalToStringLimit || !this.m_DataBaseConnection.execute("SELECT DISTINCT ( " + str1 + " ) FROM " + str)) {
                  arrayOfInt[b2 - 1] = 2;
                  break;
                } 
                resultSet1 = this.m_DataBaseConnection.getResultSet();
              } else {
                arrayOfInt[b2 - 1] = 2;
                break;
              } 
              arrayOfInt[b2 - 1] = 1;
              stringToNominal(resultSet1, b2);
              resultSet1.close();
              break;
            case 1:
              arrayOfInt[b2 - 1] = 1;
              this.m_nominalIndexes[b2 - 1] = new Hashtable();
              this.m_nominalIndexes[b2 - 1].put("false", new Double(0.0D));
              this.m_nominalIndexes[b2 - 1].put("true", new Double(1.0D));
              this.m_nominalStrings[b2 - 1] = new FastVector();
              this.m_nominalStrings[b2 - 1].addElement("false");
              this.m_nominalStrings[b2 - 1].addElement("true");
              break;
            case 2:
              arrayOfInt[b2 - 1] = 0;
              break;
            case 3:
              arrayOfInt[b2 - 1] = 0;
              break;
            case 4:
              arrayOfInt[b2 - 1] = 0;
              break;
            case 5:
              arrayOfInt[b2 - 1] = 0;
              break;
            case 6:
              arrayOfInt[b2 - 1] = 0;
              break;
            case 7:
              arrayOfInt[b2 - 1] = 0;
              break;
            case 8:
              arrayOfInt[b2 - 1] = 3;
              break;
            default:
              arrayOfInt[b2 - 1] = 2;
              break;
          } 
        } 
        FastVector fastVector = new FastVector();
        for (byte b3 = 0; b3 < i; b3++) {
          String str1 = resultSetMetaData.getColumnName(b3 + 1);
          switch (arrayOfInt[b3]) {
            case 1:
              fastVector.addElement(new Attribute(str1, this.m_nominalStrings[b3]));
              break;
            case 0:
              fastVector.addElement(new Attribute(str1));
              break;
            case 2:
              fastVector.addElement(new Attribute(str1, (FastVector)null));
              break;
            case 3:
              fastVector.addElement(new Attribute(str1, (String)null));
              break;
            default:
              throw new IOException("Unknown attribute type");
          } 
        } 
        this.m_structure = new Instances(endOfQuery(true), fastVector, 0);
        if (this.m_DataBaseConnection.getUpperCase())
          this.m_idColumn = this.m_idColumn.toUpperCase(); 
        if (this.m_structure.attribute(0).name().equals(this.m_idColumn)) {
          this.m_oldStructure = new Instances(this.m_structure, 0);
          this.m_oldStructure.deleteAttributeAt(0);
        } else {
          this.m_oldStructure = new Instances(this.m_structure, 0);
        } 
      } else if (this.m_oldStructure == null) {
        this.m_oldStructure = new Instances(this.m_structure, 0);
      } 
      this.m_DataBaseConnection.disconnectFromDatabase();
    } catch (Exception exception) {
      exception.printStackTrace();
      printException(exception);
    } 
    return this.m_oldStructure;
  }
  
  public Instances getDataSet() throws IOException {
    if (this.m_DataBaseConnection == null)
      throw new IOException("No source database has been specified"); 
    if (getRetrieval() == 2)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes"); 
    setRetrieval(1);
    connectToDatabase();
    Instances instances = null;
    try {
      if (!this.m_DataBaseConnection.execute(this.m_query))
        throw new Exception("Query didn't produce results"); 
      ResultSet resultSet = this.m_DataBaseConnection.getResultSet();
      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
      int i = resultSetMetaData.getColumnCount();
      int[] arrayOfInt = new int[i];
      this.m_nominalIndexes = new Hashtable[i];
      this.m_nominalStrings = new FastVector[i];
      for (byte b1 = 1; b1 <= i; b1++) {
        ResultSet resultSet1;
        String str1;
        String str2;
        switch (this.m_DataBaseConnection.translateDBColumnType(resultSetMetaData.getColumnTypeName(b1))) {
          case 0:
            str1 = resultSetMetaData.getColumnName(b1);
            if (this.m_DataBaseConnection.getUpperCase())
              str1 = str1.toUpperCase(); 
            str2 = endOfQuery(false);
            this.m_nominalIndexes[b1 - 1] = new Hashtable();
            this.m_nominalStrings[b1 - 1] = new FastVector();
            if (!this.m_DataBaseConnection.execute("SELECT DISTINCT ( " + str1 + " ) FROM " + str2))
              throw new Exception("Nominal values cannot be retrieved"); 
            resultSet1 = this.m_DataBaseConnection.getResultSet();
            arrayOfInt[b1 - 1] = 1;
            stringToNominal(resultSet1, b1);
            resultSet1.close();
            break;
          case 1:
            arrayOfInt[b1 - 1] = 1;
            this.m_nominalIndexes[b1 - 1] = new Hashtable();
            this.m_nominalIndexes[b1 - 1].put("false", new Double(0.0D));
            this.m_nominalIndexes[b1 - 1].put("true", new Double(1.0D));
            this.m_nominalStrings[b1 - 1] = new FastVector();
            this.m_nominalStrings[b1 - 1].addElement("false");
            this.m_nominalStrings[b1 - 1].addElement("true");
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
      FastVector fastVector1 = new FastVector();
      while (resultSet.next()) {
        double[] arrayOfDouble = new double[i];
        for (byte b = 1; b <= i; b++) {
          String str;
          Double double_;
          boolean bool;
          double d;
          byte b3;
          short s;
          int j;
          long l;
          float f;
          Date date;
          switch (this.m_DataBaseConnection.translateDBColumnType(resultSetMetaData.getColumnTypeName(b))) {
            case 0:
              str = resultSet.getString(b);
              if (resultSet.wasNull()) {
                arrayOfDouble[b - 1] = Instance.missingValue();
                break;
              } 
              double_ = this.m_nominalIndexes[b - 1].get(str);
              if (double_ == null)
                double_ = new Double(this.m_structure.attribute(b - 1).addStringValue(str)); 
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
              b3 = resultSet.getByte(b);
              if (resultSet.wasNull()) {
                arrayOfDouble[b - 1] = Instance.missingValue();
                break;
              } 
              arrayOfDouble[b - 1] = b3;
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
        Instance instance = new Instance(1.0D, arrayOfDouble);
        fastVector1.addElement(instance);
      } 
      FastVector fastVector2 = new FastVector();
      byte b2;
      for (b2 = 0; b2 < i; b2++) {
        String str = resultSetMetaData.getColumnName(b2 + 1);
        switch (arrayOfInt[b2]) {
          case 1:
            fastVector2.addElement(new Attribute(str, this.m_nominalStrings[b2]));
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
            throw new IOException("Unknown attribute type");
        } 
      } 
      instances = new Instances(endOfQuery(true), fastVector2, fastVector1.size());
      for (b2 = 0; b2 < fastVector1.size(); b2++)
        instances.add((Instance)fastVector1.elementAt(b2)); 
      resultSet.close();
      this.m_DataBaseConnection.disconnectFromDatabase();
      if (this.m_DataBaseConnection.getUpperCase())
        this.m_idColumn = this.m_idColumn.toUpperCase(); 
      if (instances.attribute(0).name().equals(this.m_idColumn))
        instances.deleteAttributeAt(0); 
      this.m_structure = new Instances(instances, 0);
    } catch (Exception exception) {
      printException(exception);
      StringBuffer stringBuffer = new StringBuffer();
      if (this.m_query.equals("Select * from Results0")) {
        stringBuffer.append("\n\nDatabaseLoader options:\n");
        Enumeration enumeration = listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          stringBuffer.append(option.synopsis() + '\n');
          stringBuffer.append(option.description() + '\n');
        } 
        System.out.println(stringBuffer);
      } 
    } 
    return instances;
  }
  
  private Instance readInstance(ResultSet paramResultSet) throws Exception {
    FastVector fastVector = new FastVector();
    ResultSetMetaData resultSetMetaData = paramResultSet.getMetaData();
    int i = resultSetMetaData.getColumnCount();
    double[] arrayOfDouble = new double[i];
    this.m_structure.delete();
    for (byte b = 1; b <= i; b++) {
      String str;
      Double double_;
      boolean bool;
      double d;
      byte b1;
      short s;
      int j;
      long l;
      float f;
      Date date;
      switch (this.m_DataBaseConnection.translateDBColumnType(resultSetMetaData.getColumnTypeName(b))) {
        case 0:
          str = paramResultSet.getString(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          double_ = this.m_nominalIndexes[b - 1].get(str);
          if (double_ == null)
            double_ = new Double(this.m_structure.attribute(b - 1).addStringValue(str)); 
          arrayOfDouble[b - 1] = double_.doubleValue();
          break;
        case 1:
          bool = paramResultSet.getBoolean(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = bool ? 1.0D : 0.0D;
          break;
        case 2:
          d = paramResultSet.getDouble(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = d;
          break;
        case 3:
          b1 = paramResultSet.getByte(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = b1;
          break;
        case 4:
          s = (short)paramResultSet.getByte(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = s;
          break;
        case 5:
          j = paramResultSet.getInt(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = j;
          break;
        case 6:
          l = paramResultSet.getLong(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = l;
          break;
        case 7:
          f = paramResultSet.getFloat(b);
          if (paramResultSet.wasNull()) {
            arrayOfDouble[b - 1] = Instance.missingValue();
            break;
          } 
          arrayOfDouble[b - 1] = f;
          break;
        case 8:
          date = paramResultSet.getDate(b);
          if (paramResultSet.wasNull()) {
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
    Instance instance = new Instance(1.0D, arrayOfDouble);
    if (this.m_DataBaseConnection.getUpperCase())
      this.m_idColumn = this.m_idColumn.toUpperCase(); 
    if (this.m_structure.attribute(0).name().equals(this.m_idColumn)) {
      instance.deleteAttributeAt(0);
      this.m_oldStructure.add(instance);
      instance = this.m_oldStructure.instance(0);
      this.m_oldStructure.delete(0);
    } else {
      this.m_structure.add(instance);
      instance = this.m_structure.instance(0);
      this.m_structure.delete(0);
    } 
    return instance;
  }
  
  public Instance getNextInstance() throws IOException {
    if (this.m_DataBaseConnection == null)
      throw new IOException("No source database has been specified"); 
    if (getRetrieval() == 1)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes"); 
    if (this.m_pseudoIncremental) {
      if (this.m_structure == null) {
        setRetrieval(0);
        getStructure();
      } 
      setRetrieval(2);
      if (this.m_datasetPseudoInc.numInstances() > 0) {
        Instance instance = this.m_datasetPseudoInc.instance(0);
        this.m_datasetPseudoInc.delete(0);
        return instance;
      } 
      resetStructure();
      return null;
    } 
    setRetrieval(2);
    try {
      if (!this.m_DataBaseConnection.isConnected())
        connectToDatabase(); 
      if (this.m_structure == null)
        this.m_structure = getStructure(); 
      if (this.m_firstTime && this.m_orderBy.size() == 0 && !checkForKey())
        throw new Exception("A unique order cannot be detected automatically.\nYou have to use SELECT * in your query to enable this feature.\nMaybe JDBC driver is not able to detect key.\nDefine primary key in your database or use -P option (command line) or enter key columns in the GUI."); 
      if (this.m_firstTime) {
        this.m_firstTime = false;
        this.m_rowCount = getRowCount();
      } 
      if (this.m_counter < this.m_rowCount) {
        if (!this.m_DataBaseConnection.execute(limitQuery(this.m_query, this.m_counter, this.m_choice)))
          throw new Exception("Tuple could not be retrieved."); 
        this.m_counter++;
        ResultSet resultSet = this.m_DataBaseConnection.getResultSet();
        resultSet.next();
        Instance instance = readInstance(resultSet);
        resultSet.close();
        return instance;
      } 
      this.m_DataBaseConnection.disconnectFromDatabase();
      resetStructure();
      return null;
    } catch (Exception exception) {
      printException(exception);
      return null;
    } 
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b1 = 0;
    arrayOfString[b1] = "-Q";
    arrayOfString[b1++] = getQuery();
    StringBuffer stringBuffer = new StringBuffer();
    byte b2;
    for (b2 = 0; b2 < this.m_orderBy.size() - 1; b2++) {
      stringBuffer.append((String)this.m_orderBy.elementAt(b2));
      stringBuffer.append(", ");
    } 
    if (b2 == this.m_orderBy.size() - 1) {
      stringBuffer.append((String)this.m_orderBy.elementAt(b2));
      arrayOfString[b1] = "-P";
      arrayOfString[b1++] = stringBuffer.toString();
    } 
    if (this.m_inc)
      arrayOfString[b1++] = "-I"; 
    while (b1 < arrayOfString.length)
      arrayOfString[b1++] = ""; 
    return arrayOfString;
  }
  
  public Enumeration listOptions() {
    FastVector fastVector = new FastVector(3);
    fastVector.addElement(new Option("\tSQL query of the form SELECT <list of columns>|* FROM <table> [WHERE] to execute (default Select * From Results0).", "Q", 1, "-Q <query>"));
    fastVector.addElement(new Option("\tList of column names uniquely defining a DB row (separated by ', ').\n\tUsed for incremental loading.\n\tIf not specified, the key will be determined automatically, if possible with the used JDBC driver.\n\tThe auto ID column created by the DatabaseSaver won't be loaded.", "P", 1, "-P<list of column names>"));
    fastVector.addElement(new Option("\tSets incremental loading", "I", 0, "-I"));
    return fastVector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('Q', paramArrayOfString);
    String str2 = Utils.getOption('P', paramArrayOfString);
    reset();
    if (str1.length() != 0)
      setQuery(str1); 
    this.m_orderBy.removeAllElements();
    this.m_inc = Utils.getFlag('I', paramArrayOfString);
    if (this.m_inc) {
      StringTokenizer stringTokenizer = new StringTokenizer(str2, ",");
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        str = str.replaceAll(" ", "");
        this.m_orderBy.addElement(str);
      } 
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
  
  public static void main(String[] paramArrayOfString) {
    try {
      DatabaseLoader databaseLoader = new DatabaseLoader();
      databaseLoader.setOptions(paramArrayOfString);
      databaseLoader.setSource();
      if (!databaseLoader.m_inc) {
        System.out.println(databaseLoader.getDataSet());
      } else {
        Instance instance;
        System.out.println(databaseLoader.getStructure());
        do {
          instance = databaseLoader.getNextInstance();
          if (instance == null)
            continue; 
          System.out.println(instance);
        } while (instance != null);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("\n" + exception.getMessage());
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\converters\DatabaseLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */