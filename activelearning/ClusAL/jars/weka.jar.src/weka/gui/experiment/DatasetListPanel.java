package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import weka.core.Instances;
import weka.core.RTSI;
import weka.experiment.Experiment;
import weka.gui.ExtensionFileFilter;

public class DatasetListPanel extends JPanel implements ActionListener {
  protected Experiment m_Exp;
  
  protected JList m_List = new JList();
  
  protected JButton m_AddBut = new JButton("Add new...");
  
  protected JButton m_DeleteBut = new JButton("Delete selected");
  
  protected JCheckBox m_relativeCheck = new JCheckBox("Use relative paths");
  
  protected FileFilter m_ArffFilter = (FileFilter)new ExtensionFileFilter(Instances.FILE_EXTENSION, "Arff data files");
  
  protected File m_UserDir = new File(System.getProperty("user.dir"));
  
  protected JFileChooser m_FileChooser = new JFileChooser(this.m_UserDir);
  
  public DatasetListPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public DatasetListPanel() {
    this.m_FileChooser.setFileFilter(this.m_ArffFilter);
    this.m_FileChooser.setFileSelectionMode(2);
    this.m_DeleteBut.setEnabled(false);
    this.m_DeleteBut.addActionListener(this);
    this.m_AddBut.setEnabled(false);
    this.m_AddBut.addActionListener(this);
    this.m_relativeCheck.setSelected(false);
    this.m_relativeCheck.setToolTipText("Store file paths relative to the start directory");
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Datasets"));
    JPanel jPanel = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    jPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel.setLayout(gridBagLayout);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel.add(this.m_AddBut, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel.add(this.m_DeleteBut, gridBagConstraints);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel.add(this.m_relativeCheck, gridBagConstraints);
    add(jPanel, "North");
    add(new JScrollPane(this.m_List), "Center");
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_Exp = paramExperiment;
    this.m_AddBut.setEnabled(true);
    this.m_List.setModel(this.m_Exp.getDatasets());
    if (this.m_Exp.getDatasets().size() > 0)
      this.m_DeleteBut.setEnabled(true); 
  }
  
  protected void getFilesRecursively(File paramFile, Vector paramVector) {
    try {
      String[] arrayOfString = paramFile.list();
      for (byte b = 0; b < arrayOfString.length; b++) {
        arrayOfString[b] = paramFile.getCanonicalPath() + File.separator + arrayOfString[b];
        File file = new File(arrayOfString[b]);
        if (this.m_FileChooser.getFileFilter().accept(file))
          if (file.isDirectory()) {
            getFilesRecursively(file, paramVector);
          } else {
            paramVector.addElement(file);
          }  
      } 
    } catch (Exception exception) {
      System.err.println("IOError occured when reading list of files");
    } 
  }
  
  protected File convertToRelativePath(File paramFile) throws Exception {
    String str1 = this.m_UserDir.getAbsolutePath() + File.separator;
    String str2 = (new File(paramFile.getParent())).getPath() + File.separator;
    String str3 = paramFile.getName();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("." + File.separator);
    int i = str2.indexOf(str1);
    if (i == 0) {
      if (str1.length() == str2.length()) {
        stringBuffer.append(str3);
      } else {
        int j = str1.length();
        stringBuffer.append(str2.substring(j));
        stringBuffer.append(str3);
      } 
    } else {
      byte b1 = 0;
      for (String str4 = new String(str1); str4.indexOf(File.separator) != -1; str4 = str4.substring(j + 1, str4.length())) {
        int j = str4.indexOf(File.separator);
        b1++;
      } 
      String str5 = new String(str2);
      String str6 = new String(str1);
      byte b = 0;
      while (str5.indexOf(File.separator) != -1) {
        int j = str5.indexOf(File.separator);
        int k = str6.indexOf(File.separator);
        String str7 = str5.substring(0, j + 1);
        String str8 = str6.substring(0, k + 1);
        if (str7.compareTo(str8) != 0) {
          if (!b)
            b = -1; 
          break;
        } 
        b++;
        str5 = str5.substring(j + 1, str5.length());
        str6 = str6.substring(k + 1, str6.length());
      } 
      if (b == -1)
        throw new Exception("Can't construct a path to file relative to user dir."); 
      if (str5.indexOf(File.separator) == -1)
        str5 = ""; 
      for (byte b2 = 0; b2 < b1 - b; b2++)
        stringBuffer.append(".." + File.separator); 
      stringBuffer.append(str5 + str3);
    } 
    return new File(stringBuffer.toString());
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    boolean bool = this.m_relativeCheck.isSelected();
    if (paramActionEvent.getSource() == this.m_AddBut) {
      int i = this.m_FileChooser.showOpenDialog(this);
      if (i == 0)
        if (this.m_FileChooser.isMultiSelectionEnabled()) {
          File[] arrayOfFile = this.m_FileChooser.getSelectedFiles();
          for (byte b = 0; b < arrayOfFile.length; b++) {
            if (arrayOfFile[b].isDirectory()) {
              Vector vector = new Vector();
              getFilesRecursively(arrayOfFile[b], vector);
              RTSI rTSI = new RTSI();
              rTSI.getClass();
              Collections.sort(vector, (Comparator)new RTSI.StringCompare(rTSI));
              for (byte b1 = 0; b1 < vector.size(); b1++) {
                File file = (File)vector.elementAt(b1);
                if (bool)
                  try {
                    file = convertToRelativePath(file);
                  } catch (Exception exception) {
                    exception.printStackTrace();
                  }  
                this.m_Exp.getDatasets().addElement(file);
              } 
            } else {
              File file = arrayOfFile[b];
              if (bool)
                try {
                  file = convertToRelativePath(file);
                } catch (Exception exception) {
                  exception.printStackTrace();
                }  
              this.m_Exp.getDatasets().addElement(file);
            } 
          } 
          this.m_DeleteBut.setEnabled(true);
        } else {
          if (this.m_FileChooser.getSelectedFile().isDirectory()) {
            Vector vector = new Vector();
            getFilesRecursively(this.m_FileChooser.getSelectedFile(), vector);
            RTSI rTSI = new RTSI();
            rTSI.getClass();
            Collections.sort(vector, (Comparator)new RTSI.StringCompare(rTSI));
            for (byte b = 0; b < vector.size(); b++) {
              File file = (File)vector.elementAt(b);
              if (bool)
                try {
                  file = convertToRelativePath(file);
                } catch (Exception exception) {
                  exception.printStackTrace();
                }  
              this.m_Exp.getDatasets().addElement(file);
            } 
          } else {
            File file = this.m_FileChooser.getSelectedFile();
            if (bool)
              try {
                file = convertToRelativePath(file);
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
            this.m_Exp.getDatasets().addElement(file);
          } 
          this.m_DeleteBut.setEnabled(true);
        }  
    } else if (paramActionEvent.getSource() == this.m_DeleteBut) {
      int[] arrayOfInt = this.m_List.getSelectedIndices();
      if (arrayOfInt != null)
        for (int i = arrayOfInt.length - 1; i >= 0; i--) {
          int j = arrayOfInt[i];
          this.m_Exp.getDatasets().removeElementAt(j);
          if (this.m_Exp.getDatasets().size() > j) {
            this.m_List.setSelectedIndex(j);
          } else {
            this.m_List.setSelectedIndex(j - 1);
          } 
        }  
      if (this.m_List.getSelectedIndex() == -1)
        this.m_DeleteBut.setEnabled(false); 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Dataset List Editor");
      jFrame.getContentPane().setLayout(new BorderLayout());
      DatasetListPanel datasetListPanel = new DatasetListPanel();
      jFrame.getContentPane().add(datasetListPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      System.err.println("Short nap");
      Thread.currentThread();
      Thread.sleep(3000L);
      System.err.println("Done");
      datasetListPanel.setExperiment(new Experiment());
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\DatasetListPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */