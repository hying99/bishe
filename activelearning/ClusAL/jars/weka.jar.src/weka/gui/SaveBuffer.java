package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SaveBuffer {
  private Logger m_Log;
  
  private Component m_parentComponent;
  
  private String m_lastvisitedDirectory = null;
  
  public SaveBuffer(Logger paramLogger, Component paramComponent) {
    this.m_Log = paramLogger;
    this.m_parentComponent = paramComponent;
  }
  
  public boolean save(StringBuffer paramStringBuffer) {
    if (paramStringBuffer != null) {
      JFileChooser jFileChooser;
      if (this.m_lastvisitedDirectory == null) {
        jFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
      } else {
        jFileChooser = new JFileChooser(this.m_lastvisitedDirectory);
      } 
      jFileChooser.setFileSelectionMode(0);
      int i = jFileChooser.showSaveDialog(this.m_parentComponent);
      if (i == 0) {
        File file = jFileChooser.getSelectedFile();
        this.m_lastvisitedDirectory = file.getPath();
        if (file.exists()) {
          String[] arrayOfString = new String[4];
          arrayOfString[0] = "Append";
          arrayOfString[1] = "Overwrite";
          arrayOfString[2] = "Choose new name";
          arrayOfString[3] = "Cancel";
          JOptionPane jOptionPane = new JOptionPane("File exists", 3, 1, null, (Object[])arrayOfString);
          JDialog jDialog = jOptionPane.createDialog(this.m_parentComponent, "File query");
          jDialog.setVisible(true);
          Object object = jOptionPane.getValue();
          if (object != null)
            for (byte b = 0; b < 4; b++) {
              if (arrayOfString[b].equals(object))
                switch (b) {
                  case 0:
                    return saveOverwriteAppend(paramStringBuffer, file, true);
                  case 1:
                    return saveOverwriteAppend(paramStringBuffer, file, false);
                  case 2:
                    return save(paramStringBuffer);
                }  
            }  
        } else {
          saveOverwriteAppend(paramStringBuffer, file, false);
        } 
      } else {
        return false;
      } 
    } 
    return false;
  }
  
  private boolean saveOverwriteAppend(StringBuffer paramStringBuffer, File paramFile, boolean paramBoolean) {
    try {
      String str = paramFile.getPath();
      if (this.m_Log != null)
        if (paramBoolean) {
          this.m_Log.statusMessage("Appending to file...");
        } else {
          this.m_Log.statusMessage("Saving to file...");
        }  
      PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(str, paramBoolean)));
      printWriter.write(paramStringBuffer.toString(), 0, paramStringBuffer.toString().length());
      printWriter.close();
      if (this.m_Log != null)
        this.m_Log.statusMessage("OK"); 
    } catch (Exception exception) {
      exception.printStackTrace();
      if (this.m_Log != null)
        this.m_Log.logMessage(exception.getMessage()); 
      return false;
    } 
    return true;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("SaveBuffer test");
      jFrame.getContentPane().setLayout(new BorderLayout());
      LogPanel logPanel = new LogPanel();
      JButton jButton = new JButton("Save");
      jFrame.getContentPane().add(jButton, "South");
      jFrame.getContentPane().add(logPanel, "Center");
      SaveBuffer saveBuffer = new SaveBuffer(logPanel, jFrame);
      jButton.addActionListener(new ActionListener(saveBuffer) {
            private final SaveBuffer val$svb;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.val$svb.save(new StringBuffer("A bit of test text"));
            }
          });
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\SaveBuffer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */