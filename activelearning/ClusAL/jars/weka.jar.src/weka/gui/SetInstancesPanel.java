package weka.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import weka.core.Instances;

public class SetInstancesPanel extends JPanel {
  protected JButton m_OpenFileBut = new JButton("Open file...");
  
  protected JButton m_OpenURLBut = new JButton("Open URL...");
  
  protected InstancesSummaryPanel m_Summary = new InstancesSummaryPanel();
  
  protected FileFilter m_ArffFilter = new ExtensionFileFilter(Instances.FILE_EXTENSION, "Arff data files");
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected String m_LastURL = "http://";
  
  protected Thread m_IOThread;
  
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
  
  protected Instances m_Instances;
  
  public SetInstancesPanel() {
    this.m_OpenFileBut.setToolTipText("Open a set of instances from a file");
    this.m_OpenURLBut.setToolTipText("Open a set of instances from a URL");
    this.m_FileChooser.setFileFilter(this.m_ArffFilter);
    this.m_FileChooser.setFileSelectionMode(0);
    this.m_OpenURLBut.addActionListener(new ActionListener(this) {
          private final SetInstancesPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setInstancesFromURLQ();
          }
        });
    this.m_OpenFileBut.addActionListener(new ActionListener(this) {
          private final SetInstancesPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setInstancesFromFileQ();
          }
        });
    this.m_Summary.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridLayout(1, 2));
    jPanel.add(this.m_OpenFileBut);
    jPanel.add(this.m_OpenURLBut);
    setLayout(new BorderLayout());
    add(this.m_Summary, "Center");
    add(jPanel, "South");
  }
  
  public void setInstancesFromFileQ() {
    if (this.m_IOThread == null) {
      int i = this.m_FileChooser.showOpenDialog(this);
      if (i == 0) {
        File file = this.m_FileChooser.getSelectedFile();
        this.m_IOThread = new Thread(this, file) {
            private final File val$selected;
            
            private final SetInstancesPanel this$0;
            
            public void run() {
              this.this$0.setInstancesFromFile(this.val$selected);
              this.this$0.m_IOThread = null;
            }
          };
        this.m_IOThread.setPriority(1);
        this.m_IOThread.start();
      } 
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  public void setInstancesFromURLQ() {
    if (this.m_IOThread == null) {
      try {
        String str = (String)JOptionPane.showInputDialog(this, "Enter the source URL", "Load Instances", 3, null, null, this.m_LastURL);
        if (str != null) {
          this.m_LastURL = str;
          URL uRL = new URL(str);
          this.m_IOThread = new Thread(this, uRL) {
              private final URL val$url;
              
              private final SetInstancesPanel this$0;
              
              public void run() {
                this.this$0.setInstancesFromURL(this.val$url);
                this.this$0.m_IOThread = null;
              }
            };
          this.m_IOThread.setPriority(1);
          this.m_IOThread.start();
        } 
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(this, "Problem with URL:\n" + exception.getMessage(), "Load Instances", 0);
      } 
    } else {
      JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
    } 
  }
  
  protected void setInstancesFromFile(File paramFile) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(paramFile));
      setInstances(new Instances(bufferedReader));
      bufferedReader.close();
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(this, "Couldn't read from file:\n" + paramFile.getName(), "Load Instances", 0);
    } 
  }
  
  protected void setInstancesFromURL(URL paramURL) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramURL.openStream()));
      setInstances(new Instances(bufferedReader));
      bufferedReader.close();
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(this, "Couldn't read from URL:\n" + paramURL, "Load Instances", 0);
    } 
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    this.m_Summary.setInstances(this.m_Instances);
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  public Instances getInstances() {
    return this.m_Instances;
  }
  
  public InstancesSummaryPanel getSummary() {
    return this.m_Summary;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.removePropertyChangeListener(paramPropertyChangeListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\SetInstancesPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */