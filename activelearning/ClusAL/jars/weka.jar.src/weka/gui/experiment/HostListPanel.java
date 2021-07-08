package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import weka.experiment.RemoteExperiment;

public class HostListPanel extends JPanel implements ActionListener {
  protected RemoteExperiment m_Exp;
  
  protected JList m_List = new JList();
  
  protected JButton m_DeleteBut = new JButton("Delete selected");
  
  protected JTextField m_HostField = new JTextField(25);
  
  public HostListPanel(RemoteExperiment paramRemoteExperiment) {
    this();
    setExperiment(paramRemoteExperiment);
  }
  
  public HostListPanel() {
    this.m_List.setModel(new DefaultListModel());
    this.m_DeleteBut.setEnabled(false);
    this.m_DeleteBut.addActionListener(this);
    this.m_HostField.addActionListener(this);
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Hosts"));
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
    jPanel.add(this.m_DeleteBut, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel.add(this.m_HostField, gridBagConstraints);
    add(jPanel, "North");
    add(new JScrollPane(this.m_List), "Center");
  }
  
  public void setExperiment(RemoteExperiment paramRemoteExperiment) {
    this.m_Exp = paramRemoteExperiment;
    this.m_List.setModel(this.m_Exp.getRemoteHosts());
    if (((DefaultListModel)this.m_List.getModel()).size() > 0)
      this.m_DeleteBut.setEnabled(true); 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (paramActionEvent.getSource() == this.m_HostField) {
      ((DefaultListModel)this.m_List.getModel()).addElement(this.m_HostField.getText());
      this.m_DeleteBut.setEnabled(true);
    } else if (paramActionEvent.getSource() == this.m_DeleteBut) {
      int[] arrayOfInt = this.m_List.getSelectedIndices();
      if (arrayOfInt != null)
        for (int i = arrayOfInt.length - 1; i >= 0; i--) {
          int j = arrayOfInt[i];
          ((DefaultListModel)this.m_List.getModel()).removeElementAt(j);
          if (((DefaultListModel)this.m_List.getModel()).size() > j) {
            this.m_List.setSelectedIndex(j);
          } else {
            this.m_List.setSelectedIndex(j - 1);
          } 
        }  
      if (((DefaultListModel)this.m_List.getModel()).size() == 0)
        this.m_DeleteBut.setEnabled(false); 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Host List Editor");
      jFrame.getContentPane().setLayout(new BorderLayout());
      HostListPanel hostListPanel = new HostListPanel();
      jFrame.getContentPane().add(hostListPanel, "Center");
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\HostListPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */