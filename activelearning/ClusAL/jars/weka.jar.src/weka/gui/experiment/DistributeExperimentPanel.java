package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import weka.experiment.Experiment;
import weka.experiment.RemoteExperiment;

public class DistributeExperimentPanel extends JPanel {
  RemoteExperiment m_Exp = null;
  
  protected JCheckBox m_enableDistributedExperiment = new JCheckBox();
  
  protected JButton m_configureHostNames = new JButton("Hosts");
  
  protected HostListPanel m_hostList = new HostListPanel();
  
  protected JRadioButton m_splitByDataSet = new JRadioButton("By data set");
  
  protected JRadioButton m_splitByRun = new JRadioButton("By run");
  
  ActionListener m_radioListener = new ActionListener(this) {
      private final DistributeExperimentPanel this$0;
      
      public void actionPerformed(ActionEvent param1ActionEvent) {
        this.this$0.updateRadioLinks();
      }
    };
  
  public DistributeExperimentPanel() {
    this.m_enableDistributedExperiment.setSelected(false);
    this.m_enableDistributedExperiment.setToolTipText("Allow this experiment to be distributed to remote hosts");
    this.m_enableDistributedExperiment.setEnabled(false);
    this.m_configureHostNames.setEnabled(false);
    this.m_configureHostNames.setToolTipText("Edit the list of remote hosts");
    this.m_enableDistributedExperiment.addActionListener(new ActionListener(this) {
          private final DistributeExperimentPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_configureHostNames.setEnabled(this.this$0.m_enableDistributedExperiment.isSelected());
            this.this$0.m_splitByDataSet.setEnabled(this.this$0.m_enableDistributedExperiment.isSelected());
            this.this$0.m_splitByRun.setEnabled(this.this$0.m_enableDistributedExperiment.isSelected());
          }
        });
    this.m_configureHostNames.addActionListener(new ActionListener(this) {
          private final DistributeExperimentPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.popupHostPanel();
          }
        });
    this.m_splitByDataSet.setToolTipText("Distribute experiment by data set");
    this.m_splitByRun.setToolTipText("Distribute experiment by run number");
    this.m_splitByDataSet.setSelected(true);
    this.m_splitByDataSet.setEnabled(false);
    this.m_splitByRun.setEnabled(false);
    this.m_splitByDataSet.addActionListener(this.m_radioListener);
    this.m_splitByRun.addActionListener(this.m_radioListener);
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_splitByDataSet);
    buttonGroup.add(this.m_splitByRun);
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridLayout(1, 2));
    jPanel.add(this.m_splitByDataSet);
    jPanel.add(this.m_splitByRun);
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Distribute experiment"));
    add(this.m_enableDistributedExperiment, "West");
    add(this.m_configureHostNames, "Center");
    add(jPanel, "South");
  }
  
  public DistributeExperimentPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_enableDistributedExperiment.setEnabled(true);
    this.m_Exp = null;
    if (paramExperiment instanceof RemoteExperiment) {
      this.m_Exp = (RemoteExperiment)paramExperiment;
      this.m_enableDistributedExperiment.setSelected(true);
      this.m_configureHostNames.setEnabled(true);
      this.m_hostList.setExperiment(this.m_Exp);
      this.m_splitByDataSet.setEnabled(true);
      this.m_splitByRun.setEnabled(true);
      this.m_splitByDataSet.setSelected(this.m_Exp.getSplitByDataSet());
      this.m_splitByRun.setSelected(!this.m_Exp.getSplitByDataSet());
    } 
  }
  
  private void popupHostPanel() {
    try {
      JFrame jFrame = new JFrame("Edit host names");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(this.m_hostList, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final DistributeExperimentPanel this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  public boolean distributedExperimentSelected() {
    return this.m_enableDistributedExperiment.isSelected();
  }
  
  public void addCheckBoxActionListener(ActionListener paramActionListener) {
    this.m_enableDistributedExperiment.addActionListener(paramActionListener);
  }
  
  private void updateRadioLinks() {
    if (this.m_Exp != null)
      this.m_Exp.setSplitByDataSet(this.m_splitByDataSet.isSelected()); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("DistributeExperiment");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(new DistributeExperimentPanel(new Experiment()), "Center");
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\DistributeExperimentPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */