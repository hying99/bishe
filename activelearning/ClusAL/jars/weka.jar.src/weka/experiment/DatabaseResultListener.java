package weka.experiment;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import weka.core.FastVector;

public class DatabaseResultListener extends DatabaseUtils implements ResultListener {
  protected ResultProducer m_ResultProducer;
  
  protected String m_ResultsTableName;
  
  protected boolean m_Debug = true;
  
  protected String m_CacheKeyName = "";
  
  protected int m_CacheKeyIndex;
  
  protected Object[] m_CacheKey;
  
  protected FastVector m_Cache = new FastVector();
  
  public String globalInfo() {
    return "Takes results from a result producer and sends them to a database.";
  }
  
  public void preProcess(ResultProducer paramResultProducer) throws Exception {
    this.m_ResultProducer = paramResultProducer;
    updateResultsTableName(this.m_ResultProducer);
  }
  
  public void postProcess(ResultProducer paramResultProducer) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer calling postProcess!!"); 
    disconnectFromDatabase();
  }
  
  public String[] determineColumnConstraints(ResultProducer paramResultProducer) throws Exception {
    ResultSet resultSet;
    FastVector fastVector = new FastVector();
    updateResultsTableName(paramResultProducer);
    DatabaseMetaData databaseMetaData = this.m_Connection.getMetaData();
    if (this.m_checkForUpperCaseNames) {
      resultSet = databaseMetaData.getColumns(null, null, this.m_ResultsTableName.toUpperCase(), null);
    } else {
      resultSet = databaseMetaData.getColumns(null, null, this.m_ResultsTableName, null);
    } 
    boolean bool = false;
    byte b1 = 0;
    while (resultSet.next()) {
      bool = true;
      if (resultSet.getString(4).toLowerCase().startsWith("measure")) {
        b1++;
        fastVector.addElement(resultSet.getString(4));
      } 
    } 
    if (!bool)
      return null; 
    String[] arrayOfString = new String[b1];
    for (byte b2 = 0; b2 < b1; b2++)
      arrayOfString[b2] = (String)fastVector.elementAt(b2); 
    return arrayOfString;
  }
  
  public void acceptResult(ResultProducer paramResultProducer, Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer calling acceptResult!!"); 
    if (paramArrayOfObject2 != null)
      putResultInTable(this.m_ResultsTableName, paramResultProducer, paramArrayOfObject1, paramArrayOfObject2); 
  }
  
  public boolean isResultRequired(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    if (this.m_ResultProducer != paramResultProducer)
      throw new Error("Unrecognized ResultProducer calling isResultRequired!"); 
    if (this.m_Debug) {
      System.err.print("Is result required...");
      for (byte b = 0; b < paramArrayOfObject.length; b++)
        System.err.print(" " + paramArrayOfObject[b]); 
      System.err.flush();
    } 
    boolean bool = false;
    if (!this.m_CacheKeyName.equals("")) {
      if (!isCacheValid(paramArrayOfObject))
        loadCache(paramResultProducer, paramArrayOfObject); 
      bool = !isKeyInCache(paramResultProducer, paramArrayOfObject) ? true : false;
    } else {
      bool = !isKeyInTable(this.m_ResultsTableName, paramResultProducer, paramArrayOfObject) ? true : false;
    } 
    if (this.m_Debug) {
      System.err.println(" ..." + (bool ? "required" : "not required") + (this.m_CacheKeyName.equals("") ? "" : " (cache)"));
      System.err.flush();
    } 
    return bool;
  }
  
  protected void updateResultsTableName(ResultProducer paramResultProducer) throws Exception {
    if (!isConnected())
      connectToDatabase(); 
    if (!experimentIndexExists())
      createExperimentIndex(); 
    String str = getResultsTableName(paramResultProducer);
    if (str == null)
      str = createExperimentIndexEntry(paramResultProducer); 
    if (!tableExists(str))
      createResultsTable(paramResultProducer, str); 
    this.m_ResultsTableName = str;
  }
  
  public String cacheKeyNameTipText() {
    return "Set the name of the key field by which to cache.";
  }
  
  public String getCacheKeyName() {
    return this.m_CacheKeyName;
  }
  
  public void setCacheKeyName(String paramString) {
    this.m_CacheKeyName = paramString;
  }
  
  protected boolean isCacheValid(Object[] paramArrayOfObject) {
    if (this.m_CacheKey == null)
      return false; 
    if (this.m_CacheKey.length != paramArrayOfObject.length)
      return false; 
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (b != this.m_CacheKeyIndex && !this.m_CacheKey[b].equals(paramArrayOfObject[b]))
        return false; 
    } 
    return true;
  }
  
  protected boolean isKeyInCache(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    for (byte b = 0; b < this.m_Cache.size(); b++) {
      if (this.m_Cache.elementAt(b).equals(paramArrayOfObject[this.m_CacheKeyIndex]))
        return true; 
    } 
    return false;
  }
  
  protected void loadCache(ResultProducer paramResultProducer, Object[] paramArrayOfObject) throws Exception {
    System.err.print(" (updating cache)");
    System.err.flush();
    this.m_Cache.removeAllElements();
    this.m_CacheKey = null;
    String str = "SELECT Key_" + this.m_CacheKeyName + " FROM " + this.m_ResultsTableName;
    String[] arrayOfString = paramResultProducer.getKeyNames();
    if (arrayOfString.length != paramArrayOfObject.length)
      throw new Exception("Key names and key values of different lengths"); 
    this.m_CacheKeyIndex = -1;
    byte b1;
    for (b1 = 0; b1 < arrayOfString.length; b1++) {
      if (arrayOfString[b1].equalsIgnoreCase(this.m_CacheKeyName)) {
        this.m_CacheKeyIndex = b1;
        break;
      } 
    } 
    if (this.m_CacheKeyIndex == -1)
      throw new Exception("No key field named " + this.m_CacheKeyName + " (as specified for caching)"); 
    b1 = 1;
    for (byte b2 = 0; b2 < paramArrayOfObject.length; b2++) {
      if (paramArrayOfObject[b2] != null && b2 != this.m_CacheKeyIndex) {
        if (b1 != 0) {
          str = str + " WHERE ";
          b1 = 0;
        } else {
          str = str + " AND ";
        } 
        str = str + "Key_" + arrayOfString[b2] + '=';
        if (paramArrayOfObject[b2] instanceof String) {
          str = str + '\'' + paramArrayOfObject[b2].toString() + '\'';
        } else {
          str = str + paramArrayOfObject[b2].toString();
        } 
      } 
    } 
    if (execute(str)) {
      ResultSet resultSet = getResultSet();
      while (resultSet.next()) {
        String str1 = resultSet.getString(1);
        if (!resultSet.wasNull())
          this.m_Cache.addElement(str1); 
      } 
      resultSet.close();
    } 
    this.m_CacheKey = (Object[])paramArrayOfObject.clone();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\DatabaseResultListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */