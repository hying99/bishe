package weka.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import weka.core.Instances;

public class AttributeListPanel extends JPanel {
  protected JTable m_Table = new JTable();
  
  protected AttributeTableModel m_Model;
  
  public AttributeListPanel() {
    this.m_Table.setSelectionMode(0);
    this.m_Table.setColumnSelectionAllowed(false);
    this.m_Table.setPreferredScrollableViewportSize(new Dimension(250, 150));
    setLayout(new BorderLayout());
    add(new JScrollPane(this.m_Table), "Center");
  }
  
  public void setInstances(Instances paramInstances) {
    if (this.m_Model == null) {
      this.m_Model = new AttributeTableModel(this, paramInstances);
      this.m_Table.setModel(this.m_Model);
      TableColumnModel tableColumnModel = this.m_Table.getColumnModel();
      tableColumnModel.getColumn(0).setMaxWidth(60);
      tableColumnModel.getColumn(1).setMinWidth(100);
    } else {
      this.m_Model.setInstances(paramInstances);
    } 
    this.m_Table.sizeColumnsToFit(-1);
    this.m_Table.revalidate();
    this.m_Table.repaint();
  }
  
  public ListSelectionModel getSelectionModel() {
    return this.m_Table.getSelectionModel();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("supply the name of an arff file"); 
      Instances instances = new Instances(new BufferedReader(new FileReader(paramArrayOfString[0])));
      AttributeListPanel attributeListPanel = new AttributeListPanel();
      JFrame jFrame = new JFrame("Attribute List Panel");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(attributeListPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      attributeListPanel.setInstances(instances);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  class AttributeTableModel extends AbstractTableModel {
    protected Instances m_Instances;
    
    private final AttributeListPanel this$0;
    
    public AttributeTableModel(AttributeListPanel this$0, Instances param1Instances) {
      this.this$0 = this$0;
      setInstances(param1Instances);
    }
    
    public void setInstances(Instances param1Instances) {
      this.m_Instances = param1Instances;
    }
    
    public int getRowCount() {
      return this.m_Instances.numAttributes();
    }
    
    public int getColumnCount() {
      return 2;
    }
    
    public Object getValueAt(int param1Int1, int param1Int2) {
      switch (param1Int2) {
        case 0:
          return new Integer(param1Int1 + 1);
        case 1:
          return this.m_Instances.attribute(param1Int1).name();
      } 
      return null;
    }
    
    public String getColumnName(int param1Int) {
      switch (param1Int) {
        case 0:
          return new String("No.");
        case 1:
          return new String("Name");
      } 
      return null;
    }
    
    public Class getColumnClass(int param1Int) {
      return getValueAt(0, param1Int).getClass();
    }
    
    public boolean isCellEditable(int param1Int1, int param1Int2) {
      return false;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\AttributeListPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */