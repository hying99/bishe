package weka.core.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerialUIDChanger {
  protected static boolean checkKOML() throws Exception {
    if (!KOML.isPresent())
      throw new Exception("KOML is not present!"); 
    return true;
  }
  
  public static boolean isKOML(String paramString) {
    return paramString.toLowerCase().endsWith(".koml");
  }
  
  protected static Object readBinary(String paramString) throws Exception {
    FileInputStream fileInputStream = new FileInputStream(paramString);
    ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
    Object object = objectInputStream.readObject();
    objectInputStream.close();
    return object;
  }
  
  protected static void writeBinary(String paramString, Object paramObject) throws Exception {
    FileOutputStream fileOutputStream = new FileOutputStream(paramString);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(fileOutputStream));
    objectOutputStream.writeObject(paramObject);
    objectOutputStream.close();
  }
  
  public static void binaryToKOML(String paramString1, String paramString2) throws Exception {
    checkKOML();
    Object object = readBinary(paramString1);
    if (object == null)
      throw new Exception("Failed to deserialize object from binary file '" + paramString1 + "'!"); 
    KOML.write(paramString2, object);
  }
  
  public static void komlToBinary(String paramString1, String paramString2) throws Exception {
    checkKOML();
    Object object = KOML.read(paramString1);
    if (object == null)
      throw new Exception("Failed to deserialize object from XML file '" + paramString1 + "'!"); 
    writeBinary(paramString2, object);
  }
  
  public static void changeUID(long paramLong1, long paramLong2, String paramString1, String paramString2) throws Exception {
    String str1;
    if (!isKOML(paramString1)) {
      str1 = paramString1 + ".koml";
      binaryToKOML(paramString1, str1);
    } else {
      str1 = paramString1;
    } 
    BufferedReader bufferedReader = new BufferedReader(new FileReader(str1));
    String str3;
    String str4;
    for (str3 = ""; (str4 = bufferedReader.readLine()) != null; str3 = str3 + str4) {
      if (!str3.equals(""))
        str3 = str3 + "\n"; 
    } 
    bufferedReader.close();
    str3 = str3.replaceAll(" uid='" + Long.toString(paramLong1) + "'", " uid='" + Long.toString(paramLong2) + "'");
    String str2 = str1 + ".temp";
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(str2));
    bufferedWriter.write(str3);
    bufferedWriter.flush();
    bufferedWriter.close();
    if (!isKOML(paramString2)) {
      komlToBinary(str2, paramString2);
    } else {
      bufferedWriter = new BufferedWriter(new FileWriter(paramString2));
      bufferedWriter.write(str3);
      bufferedWriter.flush();
      bufferedWriter.close();
    } 
    File file = new File(str2);
    file.delete();
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length != 4) {
      System.out.println();
      System.out.println("Usage: " + SerialUIDChanger.class.getName() + " <oldUID> <newUID> <oldFilename> <newFilename>");
      System.out.println("       <oldFilename> and <newFilename> have to be different");
      System.out.println();
    } else {
      if (paramArrayOfString[2].equals(paramArrayOfString[3]))
        throw new Exception("Filenames have to be different!"); 
      changeUID(Long.parseLong(paramArrayOfString[0]), Long.parseLong(paramArrayOfString[1]), paramArrayOfString[2], paramArrayOfString[3]);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\xml\SerialUIDChanger.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */