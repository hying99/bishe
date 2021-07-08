package weka.experiment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PropertyNode implements Serializable {
  public Object value;
  
  public Class parentClass;
  
  public PropertyDescriptor property;
  
  public PropertyNode(Object paramObject) {
    this(paramObject, null, null);
  }
  
  public PropertyNode(Object paramObject, PropertyDescriptor paramPropertyDescriptor, Class paramClass) {
    this.value = paramObject;
    this.property = paramPropertyDescriptor;
    this.parentClass = paramClass;
  }
  
  public String toString() {
    return (this.property == null) ? "Available properties" : this.property.getDisplayName();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    try {
      paramObjectOutputStream.writeObject(this.value);
    } catch (Exception exception) {
      throw new IOException("Can't serialize object: " + exception.getMessage());
    } 
    paramObjectOutputStream.writeObject(this.parentClass);
    paramObjectOutputStream.writeObject(this.property.getDisplayName());
    paramObjectOutputStream.writeObject(this.property.getReadMethod().getName());
    paramObjectOutputStream.writeObject(this.property.getWriteMethod().getName());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.value = paramObjectInputStream.readObject();
    this.parentClass = (Class)paramObjectInputStream.readObject();
    String str1 = (String)paramObjectInputStream.readObject();
    String str2 = (String)paramObjectInputStream.readObject();
    String str3 = (String)paramObjectInputStream.readObject();
    try {
      this.property = new PropertyDescriptor(str1, this.parentClass, str2, str3);
    } catch (IntrospectionException introspectionException) {
      throw new ClassNotFoundException("Couldn't create property descriptor: " + this.parentClass.getName() + "::" + str1);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\PropertyNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */