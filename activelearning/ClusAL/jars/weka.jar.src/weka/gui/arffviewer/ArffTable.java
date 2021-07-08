package weka.gui.arffviewer;

import java.awt.datatransfer.StringSelection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import weka.gui.ComponentHelper;
import weka.gui.JTableHelper;

public class ArffTable extends JTable {
  private String searchString;
  
  private HashSet changeListeners;
  
  public ArffTable() {
    this((TableModel)new ArffTableSorter(""));
  }
  
  public ArffTable(TableModel paramTableModel) {
    super(paramTableModel);
    setAutoResizeMode(0);
  }
  
  public void setModel(TableModel paramTableModel) {
    this.searchString = null;
    if (this.changeListeners == null)
      this.changeListeners = new HashSet(); 
    super.setModel(paramTableModel);
    if (paramTableModel == null)
      return; 
    if (!(paramTableModel instanceof ArffTableSorter))
      return; 
    ArffTableSorter arffTableSorter = (ArffTableSorter)paramTableModel;
    arffTableSorter.addMouseListenerToHeaderInTable(this);
    arffTableSorter.addTableModelListener(this);
    arffTableSorter.sortByColumn(0);
    setLayout();
    setSelectedColumn(0);
  }
  
  private void setLayout() {
    ArffTableSorter arffTableSorter = (ArffTableSorter)getModel();
    for (byte b = 0; b < getColumnCount(); b++) {
      JTableHelper.setOptimalHeaderWidth(this, b);
      getColumnModel().getColumn(b).setCellRenderer(new ArffTableCellRenderer());
      if (b > 0)
        if (arffTableSorter.getType(b) == 1) {
          JComboBox jComboBox = new JComboBox();
          jComboBox.addItem("?");
          Enumeration enumeration = arffTableSorter.getInstances().attribute(b - 1).enumerateValues();
          while (enumeration.hasMoreElements())
            jComboBox.addItem(enumeration.nextElement()); 
          getColumnModel().getColumn(b).setCellEditor(new DefaultCellEditor(jComboBox));
        } else {
          getColumnModel().getColumn(b).setCellEditor(null);
        }  
    } 
  }
  
  public String getPlainColumnName(int paramInt) {
    String str = "";
    if (getModel() == null)
      return str; 
    if (!(getModel() instanceof ArffTableSorter))
      return str; 
    ArffTableSorter arffTableSorter = (ArffTableSorter)getModel();
    if (paramInt >= 0 && paramInt < getColumnCount())
      if (paramInt == 0) {
        str = "No.";
      } else {
        str = arffTableSorter.getAttributeAt(paramInt).name();
      }  
    return str;
  }
  
  public StringSelection getStringSelection() {
    int[] arrayOfInt;
    null = null;
    if (getSelectedRow() == -1) {
      if (ComponentHelper.showMessageBox(getParent(), "Question...", "Do you really want to copy the whole table?", 0, 3) != 0)
        return null; 
      arrayOfInt = new int[getRowCount()];
      for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
        arrayOfInt[b1] = b1; 
    } else {
      arrayOfInt = getSelectedRows();
    } 
    StringBuffer stringBuffer = new StringBuffer();
    byte b;
    for (b = 0; b < getColumnCount(); b++) {
      if (b > 0)
        stringBuffer.append("\t"); 
      stringBuffer.append(getPlainColumnName(b));
    } 
    stringBuffer.append("\n");
    for (b = 0; b < arrayOfInt.length; b++) {
      for (byte b1 = 0; b1 < getColumnCount(); b1++) {
        if (b1 > 0)
          stringBuffer.append("\t"); 
        stringBuffer.append(getValueAt(arrayOfInt[b], b1).toString());
      } 
      stringBuffer.append("\n");
    } 
    return new StringSelection(stringBuffer.toString());
  }
  
  public void setSearchString(String paramString) {
    this.searchString = paramString;
    repaint();
  }
  
  public String getSearchString() {
    return this.searchString;
  }
  
  public void setSelectedColumn(int paramInt) {
    getColumnModel().getSelectionModel().clearSelection();
    getColumnModel().getSelectionModel().setSelectionInterval(paramInt, paramInt);
    resizeAndRepaint();
    if (getTableHeader() != null)
      getTableHeader().resizeAndRepaint(); 
  }
  
  public void tableChanged(TableModelEvent paramTableModelEvent) {
    super.tableChanged(paramTableModelEvent);
    setLayout();
    notifyListener();
  }
  
  private void notifyListener() {
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((ChangeListener)iterator.next()).stateChanged(new ChangeEvent(this)); 
  }
  
  public void addChangeListener(ChangeListener paramChangeListener) {
    this.changeListeners.add(paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener) {
    this.changeListeners.remove(paramChangeListener);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\ArffTable.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */