package weka.experiment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class OutputZipper {
  File m_destination;
  
  DataOutputStream m_zipOut = null;
  
  ZipOutputStream m_zs = null;
  
  public OutputZipper(File paramFile) throws Exception {
    this.m_destination = paramFile;
    if (!this.m_destination.isDirectory()) {
      this.m_zs = new ZipOutputStream(new FileOutputStream(this.m_destination));
      this.m_zipOut = new DataOutputStream(this.m_zs);
    } 
  }
  
  public void zipit(String paramString1, String paramString2) throws Exception {
    if (this.m_zipOut == null) {
      File file = new File(this.m_destination, paramString2 + ".gz");
      DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
      dataOutputStream.writeBytes(paramString1);
      dataOutputStream.close();
    } else {
      ZipEntry zipEntry = new ZipEntry(paramString2);
      this.m_zs.putNextEntry(zipEntry);
      this.m_zipOut.writeBytes(paramString1);
      this.m_zs.closeEntry();
    } 
  }
  
  public void finished() throws Exception {
    if (this.m_zipOut != null)
      this.m_zipOut.close(); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      File file = new File(new File(System.getProperty("user.dir")), "testOut.zip");
      OutputZipper outputZipper = new OutputZipper(file);
      outputZipper.zipit("Here is some test text to be zipped", "testzip");
      outputZipper.zipit("Here is a second entry to be zipped", "testzip2");
      outputZipper.finished();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\OutputZipper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */