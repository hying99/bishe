package weka.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import weka.core.Instances;

public class AttributeSelectionPanel extends JPanel {
  protected JButton m_IncludeAll = new JButton("All");
  
  protected JButton m_RemoveAll = new JButton("None");
  
  protected JButton m_Invert = new JButton("Invert");
  
  protected JTable m_Table = new JTable();
  
  protected AttributeTableModel m_Model;
  
  public AttributeSelectionPanel() {
    this.m_IncludeAll.setToolTipText("Selects all attributes");
    this.m_IncludeAll.setEnabled(false);
    this.m_IncludeAll.addActionListener(new ActionListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Model.includeAll();
          }
        });
    this.m_RemoveAll.setToolTipText("Unselects all attributes");
    this.m_RemoveAll.setEnabled(false);
    this.m_RemoveAll.addActionListener(new ActionListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Model.removeAll();
          }
        });
    this.m_Invert.setToolTipText("Inverts the current attribute selection");
    this.m_Invert.setEnabled(false);
    this.m_Invert.addActionListener(new ActionListener(this) {
          private final AttributeSelectionPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Model.invert();
          }
        });
    this.m_Table.setSelectionMode(0);
    this.m_Table.setColumnSelectionAllowed(false);
    this.m_Table.setPreferredScrollableViewportSize(new Dimension(250, 150));
    JPanel jPanel = new JPanel();
    jPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel.setLayout(new GridLayout(1, 3, 5, 5));
    jPanel.add(this.m_IncludeAll);
    jPanel.add(this.m_RemoveAll);
    jPanel.add(this.m_Invert);
    setLayout(new BorderLayout());
    add(jPanel, "North");
    add(new JScrollPane(this.m_Table), "Center");
  }
  
  public void setInstances(Instances paramInstances) {
    if (this.m_Model == null) {
      this.m_Model = new AttributeTableModel(this, paramInstances);
      this.m_Table.setModel(this.m_Model);
      TableColumnModel tableColumnModel = this.m_Table.getColumnModel();
      tableColumnModel.getColumn(0).setMaxWidth(60);
      tableColumnModel.getColumn(1).setMaxWidth(tableColumnModel.getColumn(1).getMinWidth());
      tableColumnModel.getColumn(2).setMinWidth(100);
    } else {
      this.m_Model.setInstances(paramInstances);
      this.m_Table.clearSelection();
    } 
    this.m_IncludeAll.setEnabled(true);
    this.m_RemoveAll.setEnabled(true);
    this.m_Invert.setEnabled(true);
    this.m_Table.sizeColumnsToFit(2);
    this.m_Table.revalidate();
    this.m_Table.repaint();
  }
  
  public int[] getSelectedAttributes() {
    return this.m_Model.getSelectedAttributes();
  }
  
  public ListSelectionModel getSelectionModel() {
    return this.m_Table.getSelectionModel();
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0)
        throw new Exception("supply the name of an arff file"); 
      Instances instances = new Instances(new BufferedReader(new FileReader(paramArrayOfString[0])));
      AttributeSelectionPanel attributeSelectionPanel = new AttributeSelectionPanel();
      JFrame jFrame = new JFrame("Attribute Selection Panel");
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(attributeSelectionPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      attributeSelectionPanel.setInstances(instances);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  class AttributeTableModel extends AbstractTableModel {
    protected Instances m_Instances;
    
    protected boolean[] m_Selected;
    
    private final AttributeSelectionPanel this$0;
    
    public AttributeTableModel(AttributeSelectionPanel this$0, Instances param1Instances) {
      this.this$0 = this$0;
      setInstances(param1Instances);
    }
    
    public void setInstances(Instances param1Instances) {
      this.m_Instances = param1Instances;
      this.m_Selected = new boolean[this.m_Instances.numAttributes()];
    }
    
    public int getRowCount() {
      return this.m_Selected.length;
    }
    
    public int getColumnCount() {
      return 3;
    }
    
    public Object getValueAt(int param1Int1, int param1Int2) {
      switch (param1Int2) {
        case 0:
          return new Integer(param1Int1 + 1);
        case 1:
          return new Boolean(this.m_Selected[param1Int1]);
        case 2:
          return this.m_Instances.attribute(param1Int1).name();
      } 
      return null;
    }
    
    public String getColumnName(int param1Int) {
      switch (param1Int) {
        case 0:
          return new String("No.");
        case 1:
          return new String("");
        case 2:
          return new String("Name");
      } 
      return null;
    }
    
    public void setValueAt(Object param1Object, int param1Int1, int param1Int2) {
      if (param1Int2 == 1)
        this.m_Selected[param1Int1] = ((Boolean)param1Object).booleanValue(); 
    }
    
    public Class getColumnClass(int param1Int) {
      return getValueAt(0, param1Int).getClass();
    }
    
    public boolean isCellEditable(int param1Int1, int param1Int2) {
      return (param1Int2 == 1);
    }
    
    public int[] getSelectedAttributes() {
      int[] arrayOfInt1 = new int[getRowCount()];
      byte b1 = 0;
      for (byte b2 = 0; b2 < getRowCount(); b2++) {
        if (this.m_Selected[b2])
          arrayOfInt1[b1++] = b2; 
      } 
      int[] arrayOfInt2 = new int[b1];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b1);
      return arrayOfInt2;
    }
    
    public void includeAll() {
      for (byte b = 0; b < this.m_Selected.length; b++)
        this.m_Selected[b] = true; 
      fireTableRowsUpdated(0, this.m_Selected.length);
    }
    
    public void removeAll() {
      for (byte b = 0; b < this.m_Selected.length; b++)
        this.m_Selected[b] = false; 
      fireTableRowsUpdated(0, this.m_Selected.length);
    }
    
    public void invert() {
      for (byte b = 0; b < this.m_Selected.length; b++)
        this.m_Selected[b] = !this.m_Selected[b]; 
      fireTableRowsUpdated(0, this.m_Selected.length);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\AttributeSelectionPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */