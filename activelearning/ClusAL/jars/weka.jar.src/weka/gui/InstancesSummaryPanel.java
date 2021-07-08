package weka.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import weka.core.Instances;

public class InstancesSummaryPanel extends JPanel {
  protected static final String NO_SOURCE = "None";
  
  protected JLabel m_RelationNameLab = new JLabel("None");
  
  protected JLabel m_NumInstancesLab = new JLabel("None");
  
  protected JLabel m_NumAttributesLab = new JLabel("None");
  
  protected Instances m_Instances;
  
  public InstancesSummaryPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    JLabel jLabel = new JLabel("Relation:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.gridwidth = 3;
    gridBagLayout.setConstraints(this.m_RelationNameLab, gridBagConstraints);
    add(this.m_RelationNameLab);
    this.m_RelationNameLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
    jLabel = new JLabel("Instances:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.weightx = 100.0D;
    gridBagLayout.setConstraints(this.m_NumInstancesLab, gridBagConstraints);
    add(this.m_NumInstancesLab);
    this.m_NumInstancesLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
    jLabel = new JLabel("Attributes:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 2;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 3;
    gridBagConstraints.weightx = 100.0D;
    gridBagLayout.setConstraints(this.m_NumAttributesLab, gridBagConstraints);
    add(this.m_NumAttributesLab);
    this.m_NumAttributesLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    this.m_RelationNameLab.setText(this.m_Instances.relationName());
    this.m_NumInstancesLab.setText("" + this.m_Instances.numInstances());
    this.m_NumAttributesLab.setText("" + this.m_Instances.numAttributes());
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Instances Panel");
      jFrame.getContentPane().setLayout(new BorderLayout());
      InstancesSummaryPanel instancesSummaryPanel = new InstancesSummaryPanel();
      instancesSummaryPanel.setBorder(BorderFactory.createTitledBorder("Relation"));
      jFrame.getContentPane().add(instancesSummaryPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      if (paramArrayOfString.length == 1) {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
        Instances instances = new Instances(bufferedReader);
        instancesSummaryPanel.setInstances(instances);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\InstancesSummaryPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */