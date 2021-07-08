package weka.gui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TableMap extends AbstractTableModel implements TableModelListener {
  protected TableModel model;
  
  public TableModel getModel() {
    return this.model;
  }
  
  public void setModel(TableModel paramTableModel) {
    this.model = paramTableModel;
    paramTableModel.addTableModelListener(this);
  }
  
  public Object getValueAt(int paramInt1, int paramInt2) {
    return this.model.getValueAt(paramInt1, paramInt2);
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2) {
    this.model.setValueAt(paramObject, paramInt1, paramInt2);
  }
  
  public int getRowCount() {
    return (this.model == null) ? 0 : this.model.getRowCount();
  }
  
  public int getColumnCount() {
    return (this.model == null) ? 0 : this.model.getColumnCount();
  }
  
  public String getColumnName(int paramInt) {
    return this.model.getColumnName(paramInt);
  }
  
  public Class getColumnClass(int paramInt) {
    return this.model.getColumnClass(paramInt);
  }
  
  public boolean isCellEditable(int paramInt1, int paramInt2) {
    return this.model.isCellEditable(paramInt1, paramInt2);
  }
  
  public void tableChanged(TableModelEvent paramTableModelEvent) {
    fireTableChanged(paramTableModelEvent);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\TableMap.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */