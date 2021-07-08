package weka.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import weka.gui.beans.KnowledgeFlow;
import weka.gui.experiment.Experimenter;
import weka.gui.explorer.Explorer;

public class GUIChooser extends JFrame {
  protected Button m_SimpleBut = new Button("Simple CLI");
  
  protected Button m_ExplorerBut = new Button("Explorer");
  
  protected Button m_ExperimenterBut = new Button("Experimenter");
  
  protected Button m_KnowledgeFlowBut = new Button("KnowledgeFlow");
  
  protected SimpleCLI m_SimpleCLI;
  
  protected JFrame m_ExplorerFrame;
  
  protected JFrame m_ExperimenterFrame;
  
  protected JFrame m_KnowledgeFlowFrame;
  
  Image m_weka = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("weka/gui/weka3.gif"));
  
  private static GUIChooser m_chooser;
  
  private static long m_initialJVMSize;
  
  public GUIChooser() {
    super("Weka GUI Chooser");
    Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("weka/gui/weka_icon.gif"));
    setIconImage(image);
    getContentPane().setLayout(new BorderLayout());
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createTitledBorder("GUI"));
    jPanel1.setLayout(new GridLayout(2, 2));
    jPanel1.add(this.m_SimpleBut);
    jPanel1.add(this.m_ExplorerBut);
    jPanel1.add(this.m_ExperimenterBut);
    jPanel1.add(this.m_KnowledgeFlowBut);
    getContentPane().add(jPanel1, "South");
    JPanel jPanel2 = new JPanel();
    jPanel2.setToolTipText("Weka, a native bird of New Zealand");
    ImageIcon imageIcon = new ImageIcon(this.m_weka);
    JLabel jLabel = new JLabel(imageIcon);
    jPanel2.add(jLabel);
    getContentPane().add(jPanel2, "Center");
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new GridLayout(8, 1));
    jPanel3.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel3.add(new JLabel("Waikato Environment for", 0));
    jPanel3.add(new JLabel("Knowledge Analysis", 0));
    jPanel3.add(new JLabel(""));
    jPanel3.add(new JLabel("Version 3.4.4", 0));
    jPanel3.add(new JLabel(""));
    jPanel3.add(new JLabel("(c) 1999 - 2005", 0));
    jPanel3.add(new JLabel("University of Waikato", 0));
    jPanel3.add(new JLabel("New Zealand", 0));
    getContentPane().add(jPanel3, "North");
    this.m_SimpleBut.addActionListener(new ActionListener(this) {
          private final GUIChooser this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_SimpleCLI == null) {
              this.this$0.m_SimpleBut.setEnabled(false);
              try {
                this.this$0.m_SimpleCLI = new SimpleCLI();
              } catch (Exception exception) {
                throw new Error("Couldn't start SimpleCLI!");
              } 
              this.this$0.m_SimpleCLI.addWindowListener((WindowListener)new Object(this));
              this.this$0.m_SimpleCLI.setVisible(true);
            } 
          }
        });
    this.m_ExplorerBut.addActionListener(new ActionListener(this) {
          private final GUIChooser this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_ExplorerFrame == null) {
              this.this$0.m_ExplorerBut.setEnabled(false);
              this.this$0.m_ExplorerFrame = new JFrame("Weka Explorer");
              this.this$0.m_ExplorerFrame.getContentPane().setLayout(new BorderLayout());
              this.this$0.m_ExplorerFrame.getContentPane().add((Component)new Explorer(), "Center");
              this.this$0.m_ExplorerFrame.addWindowListener((WindowListener)new Object(this));
              this.this$0.m_ExplorerFrame.pack();
              this.this$0.m_ExplorerFrame.setSize(800, 600);
              this.this$0.m_ExplorerFrame.setVisible(true);
            } 
          }
        });
    this.m_ExperimenterBut.addActionListener(new ActionListener(this) {
          private final GUIChooser this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_ExperimenterFrame == null) {
              this.this$0.m_ExperimenterBut.setEnabled(false);
              this.this$0.m_ExperimenterFrame = new JFrame("Weka Experiment Environment");
              this.this$0.m_ExperimenterFrame.getContentPane().setLayout(new BorderLayout());
              this.this$0.m_ExperimenterFrame.getContentPane().add((Component)new Experimenter(false), "Center");
              this.this$0.m_ExperimenterFrame.addWindowListener((WindowListener)new Object(this));
              this.this$0.m_ExperimenterFrame.pack();
              this.this$0.m_ExperimenterFrame.setSize(800, 600);
              this.this$0.m_ExperimenterFrame.setVisible(true);
            } 
          }
        });
    this.m_KnowledgeFlowBut.addActionListener(new ActionListener(this) {
          private final GUIChooser this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.m_KnowledgeFlowFrame == null) {
              this.this$0.m_KnowledgeFlowBut.setEnabled(false);
              this.this$0.m_KnowledgeFlowFrame = new JFrame("Weka KnowledgeFlow Environment");
              this.this$0.m_KnowledgeFlowFrame.getContentPane().setLayout(new BorderLayout());
              this.this$0.m_KnowledgeFlowFrame.getContentPane().add((Component)new KnowledgeFlow(), "Center");
              this.this$0.m_KnowledgeFlowFrame.addWindowListener((WindowListener)new Object(this));
              this.this$0.m_KnowledgeFlowFrame.pack();
              this.this$0.m_KnowledgeFlowFrame.setSize(800, 600);
              this.this$0.m_KnowledgeFlowFrame.setVisible(true);
            } 
          }
        });
    addWindowListener(new WindowAdapter(this) {
          private final GUIChooser this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.this$0.dispose();
            this.this$0.checkExit();
          }
        });
    pack();
  }
  
  private void checkExit() {
    if (!isVisible() && this.m_SimpleCLI == null && this.m_ExplorerFrame == null && this.m_ExperimenterFrame == null && this.m_KnowledgeFlowFrame == null)
      System.exit(0); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exception) {}
    try {
      m_chooser = new GUIChooser();
      m_chooser.setVisible(true);
      Thread thread = new Thread() {
          public void run() {
            while (true) {
              try {
                this;
                sleep(4000L);
                System.gc();
                if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < GUIChooser.m_initialJVMSize + 200000L) {
                  GUIChooser.m_chooser.dispose();
                  if (GUIChooser.m_chooser.m_ExperimenterFrame != null) {
                    GUIChooser.m_chooser.m_ExperimenterFrame.dispose();
                    GUIChooser.m_chooser.m_ExperimenterFrame = null;
                  } 
                  if (GUIChooser.m_chooser.m_ExplorerFrame != null) {
                    GUIChooser.m_chooser.m_ExplorerFrame.dispose();
                    GUIChooser.m_chooser.m_ExplorerFrame = null;
                  } 
                  if (GUIChooser.m_chooser.m_KnowledgeFlowFrame != null) {
                    GUIChooser.m_chooser.m_KnowledgeFlowFrame.dispose();
                    GUIChooser.m_chooser.m_KnowledgeFlowFrame = null;
                  } 
                  if (GUIChooser.m_chooser.m_SimpleCLI != null) {
                    GUIChooser.m_chooser.m_SimpleCLI.dispose();
                    GUIChooser.m_chooser.m_SimpleCLI = null;
                  } 
                  GUIChooser.m_chooser = null;
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\GUIChooser.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */