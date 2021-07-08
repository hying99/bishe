package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import weka.experiment.Experiment;

public class RunNumberPanel extends JPanel {
  protected JTextField m_LowerText = new JTextField("1");
  
  protected JTextField m_UpperText = new JTextField("10");
  
  protected Experiment m_Exp;
  
  public RunNumberPanel() {
    this.m_LowerText.addKeyListener(new KeyAdapter(this) {
          private final RunNumberPanel this$0;
          
          public void keyReleased(KeyEvent param1KeyEvent) {
            this.this$0.m_Exp.setRunLower(this.this$0.getLower());
          }
        });
    this.m_LowerText.addFocusListener(new FocusAdapter(this) {
          private final RunNumberPanel this$0;
          
          public void focusLost(FocusEvent param1FocusEvent) {
            this.this$0.m_Exp.setRunLower(this.this$0.getLower());
          }
        });
    this.m_UpperText.addKeyListener(new KeyAdapter(this) {
          private final RunNumberPanel this$0;
          
          public void keyReleased(KeyEvent param1KeyEvent) {
            this.this$0.m_Exp.setRunUpper(this.this$0.getUpper());
          }
        });
    this.m_UpperText.addFocusListener(new FocusAdapter(this) {
          private final RunNumberPanel this$0;
          
          public void focusLost(FocusEvent param1FocusEvent) {
            this.this$0.m_Exp.setRunUpper(this.this$0.getUpper());
          }
        });
    this.m_LowerText.setEnabled(false);
    this.m_UpperText.setEnabled(false);
    setLayout(new GridLayout(1, 2));
    setBorder(BorderFactory.createTitledBorder("Runs"));
    Box box1 = new Box(0);
    box1.add(Box.createHorizontalStrut(10));
    box1.add(new JLabel("From:", 4));
    box1.add(Box.createHorizontalStrut(5));
    box1.add(this.m_LowerText);
    add(box1);
    Box box2 = new Box(0);
    box2.add(Box.createHorizontalStrut(10));
    box2.add(new JLabel("To:", 4));
    box2.add(Box.createHorizontalStrut(5));
    box2.add(this.m_UpperText);
    add(box2);
  }
  
  public RunNumberPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_Exp = paramExperiment;
    this.m_LowerText.setText("" + this.m_Exp.getRunLower());
    this.m_UpperText.setText("" + this.m_Exp.getRunUpper());
    this.m_LowerText.setEnabled(true);
    this.m_UpperText.setEnabled(true);
  }
  
  public int getLower() {
    int i = 1;
    try {
      i = Integer.parseInt(this.m_LowerText.getText());
    } catch (Exception exception) {}
    return Math.max(1, i);
  }
  
  public int getUpper() {
    int i = 1;
    try {
      i = Integer.parseInt(this.m_UpperText.getText());
    } catch (Exception exception) {}
    return Math.max(1, i);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Dataset List Editor");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(new RunNumberPanel(new Experiment()), "Center");
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\RunNumberPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */