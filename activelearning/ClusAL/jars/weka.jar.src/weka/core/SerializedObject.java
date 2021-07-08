package weka.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SerializedObject implements Serializable {
  private byte[] m_storedObjectArray;
  
  private boolean m_isCompressed;
  
  public SerializedObject(Object paramObject) throws Exception {
    this(paramObject, false);
  }
  
  public SerializedObject(Object paramObject, boolean paramBoolean) throws Exception {
    ObjectOutputStream objectOutputStream;
    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
    ByteArrayOutputStream byteArrayOutputStream2 = byteArrayOutputStream1;
    if (!paramBoolean) {
      objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream2));
    } else {
      objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(byteArrayOutputStream2)));
    } 
    objectOutputStream.writeObject(paramObject);
    objectOutputStream.flush();
    objectOutputStream.close();
    this.m_storedObjectArray = byteArrayOutputStream1.toByteArray();
    this.m_isCompressed = paramBoolean;
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!paramObject.getClass().equals(getClass()))
      return false; 
    byte[] arrayOfByte = ((SerializedObject)paramObject).m_storedObjectArray;
    if (arrayOfByte.length != this.m_storedObjectArray.length)
      return false; 
    for (byte b = 0; b < arrayOfByte.length; b++) {
      if (arrayOfByte[b] != this.m_storedObjectArray[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    return this.m_storedObjectArray.length;
  }
  
  public Object getObject() {
    try {
      ObjectInputStream objectInputStream;
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.m_storedObjectArray);
      if (!this.m_isCompressed) {
        objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));
      } else {
        objectInputStream = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(byteArrayInputStream)));
      } 
      Object object = objectInputStream.readObject();
      byteArrayInputStream.close();
      return object;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\SerializedObject.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */