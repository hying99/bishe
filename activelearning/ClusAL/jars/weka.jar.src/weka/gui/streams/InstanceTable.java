package weka.gui.streams;

import java.awt.BorderLayout;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceTable extends JPanel implements Serializable, InstanceListener {
  private JTable m_InstanceTable;
  
  private boolean m_Debug;
  
  private boolean m_Clear;
  
  private String m_UpdateString;
  
  private Instances m_Instances;
  
  public void inputFormat(Instances paramInstances) {
    if (this.m_Debug)
      System.err.println("InstanceTable::inputFormat()\n" + paramInstances.toString()); 
    this.m_Instances = paramInstances;
  }
  
  public void input(Instance paramInstance) throws Exception {
    if (this.m_Debug)
      System.err.println("InstanceTable::input(" + paramInstance + ")"); 
    this.m_Instances.add(paramInstance);
  }
  
  public void batchFinished() {
    AbstractTableModel abstractTableModel = new AbstractTableModel(this) {
        private final InstanceTable this$0;
        
        public String getColumnName(int param1Int) {
          return this.this$0.m_Instances.attribute(param1Int).name();
        }
        
        public Class getColumnClass(int param1Int) {
          return "".getClass();
        }
        
        public int getColumnCount() {
          return this.this$0.m_Instances.numAttributes();
        }
        
        public int getRowCount() {
          return this.this$0.m_Instances.numInstances();
        }
        
        public Object getValueAt(int param1Int1, int param1Int2) {
          return new String(this.this$0.m_Instances.instance(param1Int1).toString(param1Int2));
        }
      };
    this.m_InstanceTable.setModel(abstractTableModel);
    if (this.m_Debug)
      System.err.println("InstanceTable::batchFinished()"); 
  }
  
  public InstanceTable() {
    setLayout(new BorderLayout());
    this.m_InstanceTable = new JTable();
    add("Center", new JScrollPane(this.m_InstanceTable));
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void instanceProduced(InstanceEvent paramInstanceEvent) {
    Object object = paramInstanceEvent.getSource();
    if (object instanceof InstanceProducer) {
      try {
        InstanceProducer instanceProducer = (InstanceProducer)object;
        switch (paramInstanceEvent.getID()) {
          case 1:
            inputFormat(instanceProducer.outputFormat());
            return;
          case 2:
            input(instanceProducer.outputPeek());
            return;
          case 3:
            batchFinished();
            return;
        } 
        System.err.println("InstanceTable::instanceProduced() - unknown event type");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else {
      System.err.println("InstanceTable::instanceProduced() - Unknown source object type");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceTable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */