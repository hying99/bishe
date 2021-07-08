package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import weka.experiment.Experiment;

public class Experimenter extends JPanel {
  protected SetupModePanel m_SetupPanel = new SetupModePanel();
  
  protected RunPanel m_RunPanel = new RunPanel();
  
  protected ResultsPanel m_ResultsPanel = new ResultsPanel();
  
  protected JTabbedPane m_TabbedPane = new JTabbedPane();
  
  protected boolean m_ClassFirst = false;
  
  private static Experimenter m_experimenter;
  
  private static long m_initialJVMSize;
  
  public Experimenter(boolean paramBoolean) {
    this.m_RunPanel.setResultsPanel(this.m_ResultsPanel);
    this.m_ClassFirst = paramBoolean;
    this.m_TabbedPane.addTab("Setup", null, this.m_SetupPanel, "Set up the experiment");
    this.m_TabbedPane.addTab("Run", null, this.m_RunPanel, "Run the experiment");
    this.m_TabbedPane.addTab("Analyse", null, this.m_ResultsPanel, "Analyse experiment results");
    this.m_TabbedPane.setSelectedIndex(0);
    this.m_TabbedPane.setEnabledAt(1, false);
    this.m_SetupPanel.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final Experimenter this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            Experiment experiment = this.this$0.m_SetupPanel.getExperiment();
            experiment.classFirst(this.this$0.m_ClassFirst);
            this.this$0.m_RunPanel.setExperiment(experiment);
            this.this$0.m_TabbedPane.setEnabledAt(1, true);
          }
        });
    setLayout(new BorderLayout());
    add(this.m_TabbedPane, "Center");
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exception) {}
    try {
      m_initialJVMSize = Runtime.getRuntime().totalMemory();
      boolean bool = false;
      if (paramArrayOfString.length > 0)
        bool = paramArrayOfString[0].equals("CLASS_FIRST"); 
      m_experimenter = new Experimenter(bool);
      JFrame jFrame = new JFrame("Weka Experiment Environment");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(m_experimenter, "Center");
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
      Thread thread = new Thread(jFrame) {
          private final JFrame val$jf;
          
          public void run() {
            while (true) {
              try {
                this;
                sleep(4000L);
                System.gc();
                if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < Experimenter.m_initialJVMSize + 200000L) {
                  this.val$jf.dispose();
                  Experimenter.m_experimenter = null;
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
      thread.setPriority(5);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\Experimenter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */