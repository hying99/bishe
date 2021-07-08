package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.WekaTaskMonitor;
import weka.gui.visualize.MatrixPanel;

public class Explorer extends JPanel {
  protected PreprocessPanel m_PreprocessPanel = new PreprocessPanel();
  
  protected ClassifierPanel m_ClassifierPanel = new ClassifierPanel();
  
  protected ClustererPanel m_ClustererPanel = new ClustererPanel();
  
  protected AssociationsPanel m_AssociationPanel = new AssociationsPanel();
  
  protected AttributeSelectionPanel m_AttributeSelectionPanel = new AttributeSelectionPanel();
  
  protected MatrixPanel m_VisualizePanel = new MatrixPanel();
  
  protected JTabbedPane m_TabbedPane = new JTabbedPane();
  
  protected LogPanel m_LogPanel = new LogPanel(new WekaTaskMonitor());
  
  private static Explorer m_explorer;
  
  private static long m_initialJVMSize;
  
  public Explorer() {
    String str = (new SimpleDateFormat("EEEE, d MMMM yyyy")).format(new Date());
    this.m_LogPanel.logMessage("Weka Explorer");
    this.m_LogPanel.logMessage("(c) 1999-2004 The University of Waikato, Hamilton, New Zealand");
    this.m_LogPanel.logMessage("web: http://www.cs.waikato.ac.nz/~ml/");
    this.m_LogPanel.logMessage("Started on " + str);
    this.m_LogPanel.statusMessage("Welcome to the Weka Explorer");
    this.m_PreprocessPanel.setLog((Logger)this.m_LogPanel);
    this.m_ClassifierPanel.setLog((Logger)this.m_LogPanel);
    this.m_AssociationPanel.setLog((Logger)this.m_LogPanel);
    this.m_ClustererPanel.setLog((Logger)this.m_LogPanel);
    this.m_AttributeSelectionPanel.setLog((Logger)this.m_LogPanel);
    this.m_TabbedPane.addTab("Preprocess", null, this.m_PreprocessPanel, "Open/Edit/Save instances");
    this.m_TabbedPane.addTab("Classify", null, this.m_ClassifierPanel, "Classify instances");
    this.m_TabbedPane.addTab("Cluster", null, this.m_ClustererPanel, "Identify instance clusters");
    this.m_TabbedPane.addTab("Associate", null, this.m_AssociationPanel, "Discover association rules");
    this.m_TabbedPane.addTab("Select attributes", null, this.m_AttributeSelectionPanel, "Determine relevance of attributes");
    this.m_TabbedPane.addTab("Visualize", null, (Component)this.m_VisualizePanel, "Explore the data");
    this.m_TabbedPane.setSelectedIndex(0);
    this.m_TabbedPane.setEnabledAt(1, false);
    this.m_TabbedPane.setEnabledAt(2, false);
    this.m_TabbedPane.setEnabledAt(3, false);
    this.m_TabbedPane.setEnabledAt(4, false);
    this.m_TabbedPane.setEnabledAt(5, false);
    this.m_PreprocessPanel.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final Explorer this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.m_ClassifierPanel.setInstances(this.this$0.m_PreprocessPanel.getInstances());
            this.this$0.m_AssociationPanel.setInstances(this.this$0.m_PreprocessPanel.getInstances());
            this.this$0.m_ClustererPanel.setInstances(this.this$0.m_PreprocessPanel.getInstances());
            this.this$0.m_AttributeSelectionPanel.setInstances(this.this$0.m_PreprocessPanel.getInstances());
            this.this$0.m_VisualizePanel.setInstances(this.this$0.m_PreprocessPanel.getInstances());
            this.this$0.m_TabbedPane.setEnabledAt(1, true);
            this.this$0.m_TabbedPane.setEnabledAt(2, true);
            this.this$0.m_TabbedPane.setEnabledAt(3, true);
            this.this$0.m_TabbedPane.setEnabledAt(4, true);
            this.this$0.m_TabbedPane.setEnabledAt(5, true);
          }
        });
    setLayout(new BorderLayout());
    add(this.m_TabbedPane, "Center");
    add((Component)this.m_LogPanel, "South");
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exception) {}
    try {
      m_initialJVMSize = Runtime.getRuntime().totalMemory();
      m_explorer = new Explorer();
      JFrame jFrame = new JFrame("Weka Explorer");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(m_explorer, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
      Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("weka/gui/weka_icon.gif"));
      jFrame.setIconImage(image);
      if (paramArrayOfString.length == 1) {
        System.err.println("Loading instances from " + paramArrayOfString[0]);
        m_explorer.m_PreprocessPanel.setInstancesFromFile(new File(paramArrayOfString[0]));
      } 
      Thread thread = new Thread(jFrame) {
          private final JFrame val$jf;
          
          public void run() {
            while (true) {
              try {
                this;
                sleep(4000L);
                System.gc();
                if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < Explorer.m_initialJVMSize + 200000L) {
                  this.val$jf.dispose();
                  Explorer.m_explorer = null;
                  System.gc();
                  Thread[] arrayOfThread = new Thread[Thread.activeCount()];
                  Thread.enumerate(arrayOfThread);
                  for (byte b = 0; b < arrayOfThread.length; b++) {
                    Thread thread = arrayOfThread[b];
                    if (thread != null && thread != Thread.currentThread())
                      if (thread.getName().startsWith("Thread")) {
                        thread.stop();
                      } else if (thread.getName().startsWith("AWT-EventQueue")) {
                        thread.stop();
                      }  
                  } 
                  arrayOfThread = null;
                  JOptionPane.showMessageDialog(null, "Not enough memory. Please load a smaller dataset or use larger heap size.", "OutOfMemory", 2);
                  System.err.println("displayed message");
                  System.err.println("Not enough memory. Please load a smaller dataset or use larger heap size.");
                  System.err.println("exiting");
                  System.exit(-1);
                } 
              } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
              } 
            } 
          }
        };
      thread.setPriority(10);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\explorer\Explorer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */