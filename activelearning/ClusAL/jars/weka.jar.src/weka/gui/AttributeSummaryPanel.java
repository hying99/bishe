package weka.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.Utils;

public class AttributeSummaryPanel extends JPanel {
  protected static final String NO_SOURCE = "None";
  
  protected JLabel m_AttributeNameLab = new JLabel("None");
  
  protected JLabel m_AttributeTypeLab = new JLabel("None");
  
  protected JLabel m_MissingLab = new JLabel("None");
  
  protected JLabel m_UniqueLab = new JLabel("None");
  
  protected JLabel m_DistinctLab = new JLabel("None");
  
  protected JTable m_StatsTable = new JTable();
  
  protected Instances m_Instances;
  
  protected AttributeStats[] m_AttributeStats;
  
  public AttributeSummaryPanel() {
    JPanel jPanel = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    jPanel.setLayout(gridBagLayout);
    JLabel jLabel = new JLabel("Name:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    jPanel.add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.gridwidth = 3;
    gridBagLayout.setConstraints(this.m_AttributeNameLab, gridBagConstraints);
    jPanel.add(this.m_AttributeNameLab);
    this.m_AttributeNameLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
    jLabel = new JLabel("Type:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 4;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    jPanel.add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 5;
    gridBagConstraints.weightx = 100.0D;
    gridBagLayout.setConstraints(this.m_AttributeTypeLab, gridBagConstraints);
    jPanel.add(this.m_AttributeTypeLab);
    this.m_AttributeTypeLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
    jLabel = new JLabel("Missing:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    jPanel.add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.weightx = 100.0D;
    gridBagLayout.setConstraints(this.m_MissingLab, gridBagConstraints);
    jPanel.add(this.m_MissingLab);
    this.m_MissingLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
    jLabel = new JLabel("Distinct:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 2;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    jPanel.add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 3;
    gridBagConstraints.weightx = 100.0D;
    gridBagLayout.setConstraints(this.m_DistinctLab, gridBagConstraints);
    jPanel.add(this.m_DistinctLab);
    this.m_DistinctLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
    jLabel = new JLabel("Unique:", 4);
    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 13;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 4;
    gridBagLayout.setConstraints(jLabel, gridBagConstraints);
    jPanel.add(jLabel);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 17;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 5;
    gridBagConstraints.weightx = 100.0D;
    gridBagLayout.setConstraints(this.m_UniqueLab, gridBagConstraints);
    jPanel.add(this.m_UniqueLab);
    this.m_UniqueLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
    setLayout(new BorderLayout());
    add(jPanel, "North");
    add(new JScrollPane(this.m_StatsTable), "Center");
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    this.m_AttributeStats = new AttributeStats[paramInstances.numAttributes()];
    this.m_AttributeNameLab.setText("None");
    this.m_AttributeTypeLab.setText("None");
    this.m_MissingLab.setText("None");
    this.m_UniqueLab.setText("None");
    this.m_DistinctLab.setText("None");
    this.m_StatsTable.setModel(new DefaultTableModel());
  }
  
  public void setAttribute(int paramInt) {
    setHeader(paramInt);
    if (this.m_AttributeStats[paramInt] == null) {
      Thread thread = new Thread(this, paramInt) {
          private final int val$index;
          
          private final AttributeSummaryPanel this$0;
          
          public void run() {
            this.this$0.m_AttributeStats[this.val$index] = this.this$0.m_Instances.attributeStats(this.val$index);
            SwingUtilities.invokeLater((Runnable)new Object(this));
          }
        };
      thread.setPriority(1);
      thread.start();
    } else {
      setDerived(paramInt);
    } 
  }
  
  protected void setDerived(int paramInt) {
    AttributeStats attributeStats = this.m_AttributeStats[paramInt];
    long l = Math.round(100.0D * attributeStats.missingCount / attributeStats.totalCount);
    this.m_MissingLab.setText("" + attributeStats.missingCount + " (" + l + "%)");
    l = Math.round(100.0D * attributeStats.uniqueCount / attributeStats.totalCount);
    this.m_UniqueLab.setText("" + attributeStats.uniqueCount + " (" + l + "%)");
    this.m_DistinctLab.setText("" + attributeStats.distinctCount);
    setTable(attributeStats, paramInt);
  }
  
  protected void setTable(AttributeStats paramAttributeStats, int paramInt) {
    if (paramAttributeStats.nominalCounts != null) {
      Attribute attribute = this.m_Instances.attribute(paramInt);
      Object[] arrayOfObject = { "Label", "Count" };
      Object[][] arrayOfObject1 = new Object[paramAttributeStats.nominalCounts.length][2];
      for (byte b = 0; b < paramAttributeStats.nominalCounts.length; b++) {
        arrayOfObject1[b][0] = attribute.value(b);
        arrayOfObject1[b][1] = new Integer(paramAttributeStats.nominalCounts[b]);
      } 
      this.m_StatsTable.setModel(new DefaultTableModel(arrayOfObject1, arrayOfObject));
    } else if (paramAttributeStats.numericStats != null) {
      Object[] arrayOfObject = { "Statistic", "Value" };
      Object[][] arrayOfObject1 = new Object[4][2];
      arrayOfObject1[0][0] = "Minimum";
      arrayOfObject1[0][1] = Utils.doubleToString(paramAttributeStats.numericStats.min, 3);
      arrayOfObject1[1][0] = "Maximum";
      arrayOfObject1[1][1] = Utils.doubleToString(paramAttributeStats.numericStats.max, 3);
      arrayOfObject1[2][0] = "Mean";
      arrayOfObject1[2][1] = Utils.doubleToString(paramAttributeStats.numericStats.mean, 3);
      arrayOfObject1[3][0] = "StdDev";
      arrayOfObject1[3][1] = Utils.doubleToString(paramAttributeStats.numericStats.stdDev, 3);
      this.m_StatsTable.setModel(new DefaultTableModel(arrayOfObject1, arrayOfObject));
    } else {
      this.m_StatsTable.setModel(new DefaultTableModel());
    } 
  }
  
  protected void setHeader(int paramInt) {
    Attribute attribute = this.m_Instances.attribute(paramInt);
    this.m_AttributeNameLab.setText(attribute.name());
    switch (attribute.type()) {
      case 1:
        this.m_AttributeTypeLab.setText("Nominal");
        break;
      case 0:
        this.m_AttributeTypeLab.setText("Numeric");
        break;
      case 2:
        this.m_AttributeTypeLab.setText("String");
        break;
      case 3:
        this.m_AttributeTypeLab.setText("Date");
        break;
      default:
        this.m_AttributeTypeLab.setText("Unknown");
        break;
    } 
    this.m_MissingLab.setText("...");
    this.m_UniqueLab.setText("...");
    this.m_DistinctLab.setText("...");
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Attribute Panel");
      jFrame.getContentPane().setLayout(new BorderLayout());
      AttributeSummaryPanel attributeSummaryPanel = new AttributeSummaryPanel();
      attributeSummaryPanel.setBorder(BorderFactory.createTitledBorder("Attribute"));
      jFrame.getContentPane().add(attributeSummaryPanel, "Center");
      JComboBox jComboBox = new JComboBox();
      jComboBox.setEnabled(false);
      jComboBox.addActionListener(new ActionListener(attributeSummaryPanel, jComboBox) {
            private final AttributeSummaryPanel val$p;
            
            private final JComboBox val$j;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.val$p.setAttribute(this.val$j.getSelectedIndex());
            }
          });
      jFrame.getContentPane().add(jComboBox, "North");
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
        attributeSummaryPanel.setInstances(instances);
        attributeSummaryPanel.setAttribute(0);
        String[] arrayOfString = new String[instances.numAttributes()];
        for (byte b = 0; b < arrayOfString.length; b++)
          arrayOfString[b] = instances.attribute(b).name(); 
        jComboBox.setModel(new DefaultComboBoxModel(arrayOfString));
        jComboBox.setEnabled(true);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\AttributeSummaryPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */