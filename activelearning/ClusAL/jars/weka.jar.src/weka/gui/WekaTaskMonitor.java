package weka.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WekaTaskMonitor extends JPanel implements TaskLogger {
  private int m_ActiveTasks = 0;
  
  private JLabel m_MonitorLabel;
  
  private ImageIcon m_iconStationary;
  
  private ImageIcon m_iconAnimated;
  
  private boolean m_animating = false;
  
  public WekaTaskMonitor() {
    Image image1 = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("weka/gui/weka_stationary.gif"));
    Image image2 = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("weka/gui/weka_animated.gif"));
    this.m_iconStationary = new ImageIcon(image1);
    this.m_iconAnimated = new ImageIcon(image2);
    this.m_MonitorLabel = new JLabel(" x " + this.m_ActiveTasks, this.m_iconStationary, 0);
    setLayout(new BorderLayout());
    Dimension dimension = this.m_MonitorLabel.getPreferredSize();
    this.m_MonitorLabel.setPreferredSize(new Dimension(dimension.width + 15, dimension.height));
    this.m_MonitorLabel.setMinimumSize(new Dimension(dimension.width + 15, dimension.height));
    add(this.m_MonitorLabel, "Center");
  }
  
  public void taskStarted() {
    this.m_ActiveTasks++;
    updateMonitor();
  }
  
  public void taskFinished() {
    this.m_ActiveTasks--;
    if (this.m_ActiveTasks < 0)
      this.m_ActiveTasks = 0; 
    updateMonitor();
  }
  
  private void updateMonitor() {
    this.m_MonitorLabel.setText(" x " + this.m_ActiveTasks);
    if (this.m_ActiveTasks > 0 && !this.m_animating) {
      this.m_MonitorLabel.setIcon(this.m_iconAnimated);
      this.m_animating = true;
    } 
    if (this.m_ActiveTasks == 0 && this.m_animating) {
      this.m_MonitorLabel.setIcon(this.m_iconStationary);
      this.m_animating = false;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      WekaTaskMonitor wekaTaskMonitor = new WekaTaskMonitor();
      wekaTaskMonitor.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Weka Tasks"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
      jFrame.getContentPane().add(wekaTaskMonitor, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      wekaTaskMonitor.taskStarted();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\WekaTaskMonitor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */