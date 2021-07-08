package weka.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LogPanel extends JPanel implements Logger, TaskLogger {
  protected JLabel m_StatusLab = new JLabel("OK");
  
  protected JTextArea m_LogText = new JTextArea(4, 20);
  
  protected JButton m_logButton = new JButton("Log");
  
  protected boolean m_First = true;
  
  protected WekaTaskMonitor m_TaskMonitor = null;
  
  public LogPanel() {
    this((WekaTaskMonitor)null, false);
  }
  
  public LogPanel(WekaTaskMonitor paramWekaTaskMonitor) {
    this(paramWekaTaskMonitor, true);
  }
  
  public LogPanel(WekaTaskMonitor paramWekaTaskMonitor, boolean paramBoolean) {
    this.m_TaskMonitor = paramWekaTaskMonitor;
    this.m_LogText.setEditable(false);
    this.m_LogText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_StatusLab.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Status"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    JScrollPane jScrollPane = new JScrollPane(this.m_LogText);
    jScrollPane.getViewport().addChangeListener(new ChangeListener(this) {
          private int lastHeight;
          
          private final LogPanel this$0;
          
          public void stateChanged(ChangeEvent param1ChangeEvent) {
            JViewport jViewport = (JViewport)param1ChangeEvent.getSource();
            int i = (jViewport.getViewSize()).height;
            if (i != this.lastHeight) {
              this.lastHeight = i;
              int j = i - (jViewport.getExtentSize()).height;
              jViewport.setViewPosition(new Point(0, j));
            } 
          }
        });
    if (paramBoolean) {
      JFrame jFrame = new JFrame("Log");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final LogPanel this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.setVisible(false);
            }
          });
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(jScrollPane, "Center");
      jFrame.pack();
      jFrame.setSize(450, 350);
      this.m_logButton.addActionListener(new ActionListener(this, jFrame) {
            private final JFrame val$jf;
            
            private final LogPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.val$jf.setVisible(true);
            }
          });
      setLayout(new BorderLayout());
      JPanel jPanel1 = new JPanel();
      jPanel1.setLayout(new BorderLayout());
      jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
      jPanel1.add(this.m_logButton, "Center");
      JPanel jPanel2 = new JPanel();
      jPanel2.setLayout(new BorderLayout());
      jPanel2.add(this.m_StatusLab, "Center");
      jPanel2.add(jPanel1, "East");
      if (paramWekaTaskMonitor == null) {
        add(jPanel2, "South");
      } else {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(jPanel2, "Center");
        jPanel.add(this.m_TaskMonitor, "East");
        add(jPanel, "South");
      } 
    } else {
      JPanel jPanel = new JPanel();
      jPanel.setBorder(BorderFactory.createTitledBorder("Log"));
      jPanel.setLayout(new BorderLayout());
      jPanel.add(jScrollPane, "Center");
      setLayout(new BorderLayout());
      add(jPanel, "Center");
      if (paramWekaTaskMonitor == null) {
        add(this.m_StatusLab, "South");
      } else {
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(this.m_StatusLab, "Center");
        jPanel1.add(this.m_TaskMonitor, "East");
        add(jPanel1, "South");
      } 
    } 
    addPopup();
  }
  
  private void addPopup() {
    addMouseListener(new MouseAdapter(this) {
          private final LogPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16 || param1MouseEvent.isAltDown()) {
              JPopupMenu jPopupMenu = new JPopupMenu();
              JMenuItem jMenuItem1 = new JMenuItem("Available memory");
              jMenuItem1.addActionListener((ActionListener)new Object(this));
              jPopupMenu.add(jMenuItem1);
              JMenuItem jMenuItem2 = new JMenuItem("Run garbage collector");
              jMenuItem2.addActionListener((ActionListener)new Object(this));
              jPopupMenu.add(jMenuItem2);
              jPopupMenu.show(this.this$0, param1MouseEvent.getX(), param1MouseEvent.getY());
            } 
          }
        });
  }
  
  public void taskStarted() {
    if (this.m_TaskMonitor != null)
      this.m_TaskMonitor.taskStarted(); 
  }
  
  public void taskFinished() {
    if (this.m_TaskMonitor != null)
      this.m_TaskMonitor.taskFinished(); 
  }
  
  protected static String getTimestamp() {
    return (new SimpleDateFormat("HH:mm:ss:")).format(new Date());
  }
  
  public void logMessage(String paramString) {
    if (this.m_First) {
      this.m_First = false;
    } else {
      this.m_LogText.append("\n");
    } 
    this.m_LogText.append(getTimestamp() + ' ' + paramString);
  }
  
  public void statusMessage(String paramString) {
    this.m_StatusLab.setText(paramString);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Log Panel");
      jFrame.getContentPane().setLayout(new BorderLayout());
      LogPanel logPanel = new LogPanel();
      jFrame.getContentPane().add(logPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      logPanel.logMessage("Welcome to the generic log panel!");
      logPanel.statusMessage("Hi there");
      logPanel.logMessage("Funky chickens");
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\LogPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */