package weka.core;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

public class Attribute implements Copyable, Serializable {
  public static final int NUMERIC = 0;
  
  public static final int NOMINAL = 1;
  
  public static final int STRING = 2;
  
  public static final int DATE = 3;
  
  public static final int ORDERING_SYMBOLIC = 0;
  
  public static final int ORDERING_ORDERED = 1;
  
  public static final int ORDERING_MODULO = 2;
  
  static String ARFF_ATTRIBUTE = "@attribute";
  
  static String ARFF_ATTRIBUTE_INTEGER = "integer";
  
  static String ARFF_ATTRIBUTE_REAL = "real";
  
  static String ARFF_ATTRIBUTE_NUMERIC = "numeric";
  
  static String ARFF_ATTRIBUTE_STRING = "string";
  
  static String ARFF_ATTRIBUTE_DATE = "date";
  
  private static final int STRING_COMPRESS_THRESHOLD = 200;
  
  private String m_Name;
  
  private int m_Type;
  
  private FastVector m_Values;
  
  private Hashtable m_Hashtable;
  
  private SimpleDateFormat m_DateFormat;
  
  private int m_Index;
  
  private ProtectedProperties m_Metadata;
  
  private int m_Ordering;
  
  private boolean m_IsRegular;
  
  private boolean m_IsAveragable;
  
  private boolean m_HasZeropoint;
  
  private double m_Weight;
  
  private double m_LowerBound;
  
  private boolean m_LowerBoundIsOpen;
  
  private double m_UpperBound;
  
  private boolean m_UpperBoundIsOpen;
  
  public Attribute(String paramString) {
    this(paramString, new ProtectedProperties(new Properties()));
  }
  
  public Attribute(String paramString, ProtectedProperties paramProtectedProperties) {
    this.m_Name = paramString;
    this.m_Index = -1;
    this.m_Values = null;
    this.m_Hashtable = null;
    this.m_Type = 0;
    setMetadata(paramProtectedProperties);
  }
  
  public Attribute(String paramString1, String paramString2) {
    this(paramString1, paramString2, new ProtectedProperties(new Properties()));
  }
  
  public Attribute(String paramString1, String paramString2, ProtectedProperties paramProtectedProperties) {
    this.m_Name = paramString1;
    this.m_Index = -1;
    this.m_Values = null;
    this.m_Hashtable = null;
    this.m_Type = 3;
    if (paramString2 != null) {
      this.m_DateFormat = new SimpleDateFormat(paramString2);
    } else {
      this.m_DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    } 
    this.m_DateFormat.setLenient(false);
    setMetadata(paramProtectedProperties);
  }
  
  public Attribute(String paramString, FastVector paramFastVector) {
    this(paramString, paramFastVector, new ProtectedProperties(new Properties()));
  }
  
  public Attribute(String paramString, FastVector paramFastVector, ProtectedProperties paramProtectedProperties) {
    this.m_Name = paramString;
    this.m_Index = -1;
    if (paramFastVector == null) {
      this.m_Values = new FastVector();
      this.m_Hashtable = new Hashtable();
      this.m_Type = 2;
    } else {
      this.m_Values = new FastVector(paramFastVector.size());
      this.m_Hashtable = new Hashtable(paramFastVector.size());
      for (byte b = 0; b < paramFastVector.size(); b++) {
        Object object = paramFastVector.elementAt(b);
        if (((String)object).length() > 200)
          try {
            object = new SerializedObject(paramFastVector.elementAt(b), true);
          } catch (Exception exception) {
            System.err.println("Couldn't compress nominal attribute value - storing uncompressed.");
          }  
        if (this.m_Values.indexOf(object) >= 0)
          throw new IllegalArgumentException("A nominal attribute (" + paramString + ") cannot" + " have duplicate labels (" + object + ")."); 
        this.m_Values.addElement(object);
        this.m_Hashtable.put(object, new Integer(b));
      } 
      this.m_Type = 1;
    } 
    setMetadata(paramProtectedProperties);
  }
  
  public Object copy() {
    Attribute attribute = new Attribute(this.m_Name);
    attribute.m_Index = this.m_Index;
    attribute.m_Type = this.m_Type;
    attribute.m_Values = this.m_Values;
    attribute.m_Hashtable = this.m_Hashtable;
    attribute.m_DateFormat = this.m_DateFormat;
    attribute.setMetadata(this.m_Metadata);
    return attribute;
  }
  
  public final Enumeration enumerateValues() {
    if (isNominal() || isString()) {
      Enumeration enumeration = this.m_Values.elements();
      return new Enumeration(this, enumeration) {
          private final Enumeration val$ee;
          
          private final Attribute this$0;
          
          public boolean hasMoreElements() {
            return this.val$ee.hasMoreElements();
          }
          
          public Object nextElement() {
            SerializedObject serializedObject = (SerializedObject)this.val$ee.nextElement();
            return (serializedObject instanceof SerializedObject) ? ((SerializedObject)serializedObject).getObject() : serializedObject;
          }
        };
    } 
    return null;
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null || !paramObject.getClass().equals(getClass()))
      return false; 
    Attribute attribute = (Attribute)paramObject;
    if (!this.m_Name.equals(attribute.m_Name))
      return false; 
    if (isNominal() && attribute.isNominal()) {
      if (this.m_Values.size() != attribute.m_Values.size())
        return false; 
      for (byte b = 0; b < this.m_Values.size(); b++) {
        if (!this.m_Values.elementAt(b).equals(attribute.m_Values.elementAt(b)))
          return false; 
      } 
      return true;
    } 
    return (type() == attribute.type());
  }
  
  public final int index() {
    return this.m_Index;
  }
  
  public final int indexOfValue(String paramString) {
    SerializedObject serializedObject;
    if (!isNominal() && !isString())
      return -1; 
    String str = paramString;
    if (paramString.length() > 200)
      try {
        serializedObject = new SerializedObject(paramString, true);
      } catch (Exception exception) {
        System.err.println("Couldn't compress string attribute value - searching uncompressed.");
      }  
    Integer integer = (Integer)this.m_Hashtable.get(serializedObject);
    return (integer == null) ? -1 : integer.intValue();
  }
  
  public final boolean isNominal() {
    return (this.m_Type == 1);
  }
  
  public final boolean isNumeric() {
    return (this.m_Type == 0 || this.m_Type == 3);
  }
  
  public final boolean isString() {
    return (this.m_Type == 2);
  }
  
  public final boolean isDate() {
    return (this.m_Type == 3);
  }
  
  public final String name() {
    return this.m_Name;
  }
  
  public final int numValues() {
    return (!isNominal() && !isString()) ? 0 : this.m_Values.size();
  }
  
  public final String toString() {
    Enumeration enumeration;
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(ARFF_ATTRIBUTE).append(" ").append(Utils.quote(this.m_Name)).append(" ");
    switch (this.m_Type) {
      case 1:
        stringBuffer.append('{');
        enumeration = enumerateValues();
        while (enumeration.hasMoreElements()) {
          stringBuffer.append(Utils.quote(enumeration.nextElement()));
          if (enumeration.hasMoreElements())
            stringBuffer.append(','); 
        } 
        stringBuffer.append('}');
        return stringBuffer.toString();
      case 0:
        stringBuffer.append(ARFF_ATTRIBUTE_NUMERIC);
        return stringBuffer.toString();
      case 2:
        stringBuffer.append(ARFF_ATTRIBUTE_STRING);
        return stringBuffer.toString();
      case 3:
        stringBuffer.append(ARFF_ATTRIBUTE_DATE).append(" ").append(Utils.quote(this.m_DateFormat.toPattern()));
        return stringBuffer.toString();
    } 
    stringBuffer.append("UNKNOWN");
    return stringBuffer.toString();
  }
  
  public final int type() {
    return this.m_Type;
  }
  
  public final String value(int paramInt) {
    if (!isNominal() && !isString())
      return ""; 
    Object object = this.m_Values.elementAt(paramInt);
    if (object instanceof SerializedObject)
      object = ((SerializedObject)object).getObject(); 
    return (String)object;
  }
  
  Attribute(String paramString, int paramInt) {
    this(paramString);
    this.m_Index = paramInt;
  }
  
  Attribute(String paramString1, String paramString2, int paramInt) {
    this(paramString1, paramString2);
    this.m_Index = paramInt;
  }
  
  Attribute(String paramString, FastVector paramFastVector, int paramInt) {
    this(paramString, paramFastVector);
    this.m_Index = paramInt;
  }
  
  public int addStringValue(String paramString) {
    SerializedObject serializedObject;
    if (!isString())
      return -1; 
    String str = paramString;
    if (paramString.length() > 200)
      try {
        serializedObject = new SerializedObject(paramString, true);
      } catch (Exception exception) {
        System.err.println("Couldn't compress string attribute value - storing uncompressed.");
      }  
    Integer integer = (Integer)this.m_Hashtable.get(serializedObject);
    if (integer != null)
      return integer.intValue(); 
    int i = this.m_Values.size();
    this.m_Values.addElement(serializedObject);
    this.m_Hashtable.put(serializedObject, new Integer(i));
    return i;
  }
  
  public int addStringValue(Attribute paramAttribute, int paramInt) {
    if (!isString())
      return -1; 
    Object object = paramAttribute.m_Values.elementAt(paramInt);
    Integer integer = (Integer)this.m_Hashtable.get(object);
    if (integer != null)
      return integer.intValue(); 
    int i = this.m_Values.size();
    this.m_Values.addElement(object);
    this.m_Hashtable.put(object, new Integer(i));
    return i;
  }
  
  final void addValue(String paramString) {
    this.m_Values = (FastVector)this.m_Values.copy();
    this.m_Hashtable = (Hashtable)this.m_Hashtable.clone();
    forceAddValue(paramString);
  }
  
  final Attribute copy(String paramString) {
    Attribute attribute = new Attribute(paramString);
    attribute.m_Index = this.m_Index;
    attribute.m_DateFormat = this.m_DateFormat;
    attribute.m_Type = this.m_Type;
    attribute.m_Values = this.m_Values;
    attribute.m_Hashtable = this.m_Hashtable;
    attribute.setMetadata(this.m_Metadata);
    return attribute;
  }
  
  final void delete(int paramInt) {
    if (!isNominal() && !isString())
      throw new IllegalArgumentException("Can only remove value ofnominal or string attribute!"); 
    this.m_Values = (FastVector)this.m_Values.copy();
    this.m_Values.removeElementAt(paramInt);
    Hashtable hashtable = new Hashtable(this.m_Hashtable.size());
    Enumeration enumeration = this.m_Hashtable.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      Integer integer = (Integer)this.m_Hashtable.get(object);
      int i = integer.intValue();
      if (i > paramInt) {
        hashtable.put(object, new Integer(i - 1));
        continue;
      } 
      if (i < paramInt)
        hashtable.put(object, integer); 
    } 
    this.m_Hashtable = hashtable;
  }
  
  final void forceAddValue(String paramString) {
    SerializedObject serializedObject;
    String str = paramString;
    if (paramString.length() > 200)
      try {
        serializedObject = new SerializedObject(paramString, true);
      } catch (Exception exception) {
        System.err.println("Couldn't compress string attribute value - storing uncompressed.");
      }  
    this.m_Values.addElement(serializedObject);
    this.m_Hashtable.put(serializedObject, new Integer(this.m_Values.size() - 1));
  }
  
  final void setIndex(int paramInt) {
    this.m_Index = paramInt;
  }
  
  final void setValue(int paramInt, String paramString) {
    String str;
    SerializedObject serializedObject;
    switch (this.m_Type) {
      case 1:
      case 2:
        this.m_Values = (FastVector)this.m_Values.copy();
        this.m_Hashtable = (Hashtable)this.m_Hashtable.clone();
        str = paramString;
        if (paramString.length() > 200)
          try {
            serializedObject = new SerializedObject(paramString, true);
          } catch (Exception exception) {
            System.err.println("Couldn't compress string attribute value - storing uncompressed.");
          }  
        this.m_Hashtable.remove(this.m_Values.elementAt(paramInt));
        this.m_Values.setElementAt(serializedObject, paramInt);
        this.m_Hashtable.put(serializedObject, new Integer(paramInt));
        return;
    } 
    throw new IllegalArgumentException("Can only set values for nominal or string attributes!");
  }
  
  public String formatDate(double paramDouble) {
    switch (this.m_Type) {
      case 3:
        return this.m_DateFormat.format(new Date((long)paramDouble));
    } 
    throw new IllegalArgumentException("Can only format date values for date attributes!");
  }
  
  public double parseDate(String paramString) throws ParseException {
    long l;
    switch (this.m_Type) {
      case 3:
        l = this.m_DateFormat.parse(paramString).getTime();
        return l;
    } 
    throw new IllegalArgumentException("Can only parse date values for date attributes!");
  }
  
  public final ProtectedProperties getMetadata() {
    return this.m_Metadata;
  }
  
  public final int ordering() {
    return this.m_Ordering;
  }
  
  public final boolean isRegular() {
    return this.m_IsRegular;
  }
  
  public final boolean isAveragable() {
    return this.m_IsAveragable;
  }
  
  public final boolean hasZeropoint() {
    return this.m_HasZeropoint;
  }
  
  public final double weight() {
    return this.m_Weight;
  }
  
  public final double getLowerNumericBound() {
    return this.m_LowerBound;
  }
  
  public final boolean lowerNumericBoundIsOpen() {
    return this.m_LowerBoundIsOpen;
  }
  
  public final double getUpperNumericBound() {
    return this.m_UpperBound;
  }
  
  public final boolean upperNumericBoundIsOpen() {
    return this.m_UpperBoundIsOpen;
  }
  
  public final boolean isInRange(double paramDouble) {
    if (this.m_Type == 3 || paramDouble == Instance.missingValue())
      return true; 
    if (this.m_Type != 0) {
      int i = (int)paramDouble;
      if (i < 0 || i >= this.m_Hashtable.size())
        return false; 
    } else {
      if (this.m_LowerBoundIsOpen) {
        if (paramDouble <= this.m_LowerBound)
          return false; 
      } else if (paramDouble < this.m_LowerBound) {
        return false;
      } 
      if (this.m_UpperBoundIsOpen) {
        if (paramDouble >= this.m_UpperBound)
          return false; 
      } else if (paramDouble > this.m_UpperBound) {
        return false;
      } 
    } 
    return true;
  }
  
  private void setMetadata(ProtectedProperties paramProtectedProperties) {
    this.m_Metadata = paramProtectedProperties;
    if (this.m_Type == 3) {
      this.m_Ordering = 1;
      this.m_IsRegular = true;
      this.m_IsAveragable = false;
      this.m_HasZeropoint = false;
    } else {
      String str2;
      String str1 = this.m_Metadata.getProperty("ordering", "");
      if (this.m_Type == 0 && str1.compareTo("modulo") != 0 && str1.compareTo("symbolic") != 0) {
        str2 = "true";
      } else {
        str2 = "false";
      } 
      this.m_IsAveragable = (this.m_Metadata.getProperty("averageable", str2).compareTo("true") == 0);
      this.m_HasZeropoint = (this.m_Metadata.getProperty("zeropoint", str2).compareTo("true") == 0);
      if (this.m_IsAveragable || this.m_HasZeropoint)
        str2 = "true"; 
      this.m_IsRegular = (this.m_Metadata.getProperty("regular", str2).compareTo("true") == 0);
      if (str1.compareTo("symbolic") == 0) {
        this.m_Ordering = 0;
      } else if (str1.compareTo("ordered") == 0) {
        this.m_Ordering = 1;
      } else if (str1.compareTo("modulo") == 0) {
        this.m_Ordering = 2;
      } else if (this.m_Type == 0 || this.m_IsAveragable || this.m_HasZeropoint) {
        this.m_Ordering = 1;
      } else {
        this.m_Ordering = 0;
      } 
    } 
    if (this.m_IsAveragable && !this.m_IsRegular)
      throw new IllegalArgumentException("An averagable attribute must be regular"); 
    if (this.m_HasZeropoint && !this.m_IsRegular)
      throw new IllegalArgumentException("A zeropoint attribute must be regular"); 
    if (this.m_IsRegular && this.m_Ordering == 0)
      throw new IllegalArgumentException("A symbolic attribute cannot be regular"); 
    if (this.m_IsAveragable && this.m_Ordering != 1)
      throw new IllegalArgumentException("An averagable attribute must be ordered"); 
    if (this.m_HasZeropoint && this.m_Ordering != 1)
      throw new IllegalArgumentException("A zeropoint attribute must be ordered"); 
    this.m_Weight = 1.0D;
    String str = this.m_Metadata.getProperty("weight");
    if (str != null)
      try {
        this.m_Weight = Double.valueOf(str).doubleValue();
      } catch (NumberFormatException numberFormatException) {
        throw new IllegalArgumentException("Not a valid attribute weight: '" + str + "'");
      }  
    if (this.m_Type == 0)
      setNumericRange(this.m_Metadata.getProperty("range")); 
  }
  
  private void setNumericRange(String paramString) {
    this.m_LowerBound = Double.NEGATIVE_INFINITY;
    this.m_LowerBoundIsOpen = false;
    this.m_UpperBound = Double.POSITIVE_INFINITY;
    this.m_UpperBoundIsOpen = false;
    if (paramString == null)
      return; 
    StreamTokenizer streamTokenizer = new StreamTokenizer(new StringReader(paramString));
    streamTokenizer.resetSyntax();
    streamTokenizer.whitespaceChars(0, 32);
    streamTokenizer.wordChars(33, 255);
    streamTokenizer.ordinaryChar(91);
    streamTokenizer.ordinaryChar(40);
    streamTokenizer.ordinaryChar(44);
    streamTokenizer.ordinaryChar(93);
    streamTokenizer.ordinaryChar(41);
    try {
      streamTokenizer.nextToken();
      if (streamTokenizer.ttype == 91) {
        this.m_LowerBoundIsOpen = false;
      } else if (streamTokenizer.ttype == 40) {
        this.m_LowerBoundIsOpen = true;
      } else {
        throw new IllegalArgumentException("Expected opening brace on range, found: " + streamTokenizer.toString());
      } 
      streamTokenizer.nextToken();
      if (streamTokenizer.ttype != -3)
        throw new IllegalArgumentException("Expected lower bound in range, found: " + streamTokenizer.toString()); 
      if (streamTokenizer.sval.compareToIgnoreCase("-inf") == 0) {
        this.m_LowerBound = Double.NEGATIVE_INFINITY;
      } else if (streamTokenizer.sval.compareToIgnoreCase("+inf") == 0) {
        this.m_LowerBound = Double.POSITIVE_INFINITY;
      } else if (streamTokenizer.sval.compareToIgnoreCase("inf") == 0) {
        this.m_LowerBound = Double.NEGATIVE_INFINITY;
      } else {
        try {
          this.m_LowerBound = Double.valueOf(streamTokenizer.sval).doubleValue();
        } catch (NumberFormatException numberFormatException) {
          throw new IllegalArgumentException("Expected lower bound in range, found: '" + streamTokenizer.sval + "'");
        } 
      } 
      if (streamTokenizer.nextToken() != 44)
        throw new IllegalArgumentException("Expected comma in range, found: " + streamTokenizer.toString()); 
      streamTokenizer.nextToken();
      if (streamTokenizer.ttype != -3)
        throw new IllegalArgumentException("Expected upper bound in range, found: " + streamTokenizer.toString()); 
      if (streamTokenizer.sval.compareToIgnoreCase("-inf") == 0) {
        this.m_UpperBound = Double.NEGATIVE_INFINITY;
      } else if (streamTokenizer.sval.compareToIgnoreCase("+inf") == 0) {
        this.m_UpperBound = Double.POSITIVE_INFINITY;
      } else if (streamTokenizer.sval.compareToIgnoreCase("inf") == 0) {
        this.m_UpperBound = Double.POSITIVE_INFINITY;
      } else {
        try {
          this.m_UpperBound = Double.valueOf(streamTokenizer.sval).doubleValue();
        } catch (NumberFormatException numberFormatException) {
          throw new IllegalArgumentException("Expected upper bound in range, found: '" + streamTokenizer.sval + "'");
        } 
      } 
      streamTokenizer.nextToken();
      if (streamTokenizer.ttype == 93) {
        this.m_UpperBoundIsOpen = false;
      } else if (streamTokenizer.ttype == 41) {
        this.m_UpperBoundIsOpen = true;
      } else {
        throw new IllegalArgumentException("Expected closing brace on range, found: " + streamTokenizer.toString());
      } 
      if (streamTokenizer.nextToken() != -1)
        throw new IllegalArgumentException("Expected end of range string, found: " + streamTokenizer.toString()); 
    } catch (IOException iOException) {
      throw new IllegalArgumentException("IOException reading attribute range string: " + iOException.getMessage());
    } 
    if (this.m_UpperBound < this.m_LowerBound)
      throw new IllegalArgumentException("Upper bound (" + this.m_UpperBound + ") on numeric range is" + " less than lower bound (" + this.m_LowerBound + ")!"); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Attribute attribute1 = new Attribute("length");
      Attribute attribute2 = new Attribute("weight");
      Attribute attribute3 = new Attribute("date", "yyyy-MM-dd HH:mm:ss");
      System.out.println(attribute3);
      double d = attribute3.parseDate("2001-04-04 14:13:55");
      System.out.println("Test date = " + d);
      System.out.println(attribute3.formatDate(d));
      d = (new Date()).getTime();
      System.out.println("Date now = " + d);
      System.out.println(attribute3.formatDate(d));
      FastVector fastVector = new FastVector(3);
      fastVector.addElement("first");
      fastVector.addElement("second");
      fastVector.addElement("third");
      Attribute attribute4 = new Attribute("position", fastVector);
      System.out.println("Name of \"position\": " + attribute4.name());
      Enumeration enumeration = attribute4.enumerateValues();
      while (enumeration.hasMoreElements()) {
        String str = enumeration.nextElement();
        System.out.println("Value of \"position\": " + str);
      } 
      Attribute attribute5 = (Attribute)attribute4.copy();
      System.out.println("Copy is the same as original: " + attribute5.equals(attribute4));
      System.out.println("Index of attribute \"weight\" (should be -1): " + attribute2.index());
      System.out.println("Index of value \"first\" of \"position\" (should be 0): " + attribute4.indexOfValue("first"));
      System.out.println("\"position\" is numeric: " + attribute4.isNumeric());
      System.out.println("\"position\" is nominal: " + attribute4.isNominal());
      System.out.println("\"position\" is string: " + attribute4.isString());
      System.out.println("Name of \"position\": " + attribute4.name());
      System.out.println("Number of values for \"position\": " + attribute4.numValues());
      for (byte b = 0; b < attribute4.numValues(); b++)
        System.out.println("Value " + b + ": " + attribute4.value(b)); 
      System.out.println(attribute4);
      switch (attribute4.type()) {
        case 0:
          System.out.println("\"position\" is numeric");
          return;
        case 1:
          System.out.println("\"position\" is nominal");
          return;
        case 2:
          System.out.println("\"position\" is string");
          return;
        case 3:
          System.out.println("\"position\" is date");
          return;
      } 
      System.out.println("\"position\" has unknown type");
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Attribute.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */