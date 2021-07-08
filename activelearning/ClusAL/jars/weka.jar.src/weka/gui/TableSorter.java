package weka.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableSorter extends TableMap {
  protected int[] indexes;
  
  private Vector sortingColumns = new Vector();
  
  private boolean ascending = true;
  
  private int compares;
  
  public TableSorter() {
    this.indexes = new int[0];
  }
  
  public TableSorter(TableModel paramTableModel) {
    setModel(paramTableModel);
  }
  
  public void setModel(TableModel paramTableModel) {
    super.setModel(paramTableModel);
    reallocateIndexes();
  }
  
  public Object getModelValueAt(int paramInt1, int paramInt2) {
    return this.model.getValueAt(paramInt1, paramInt2);
  }
  
  public int compareRowsByColumn(int paramInt1, int paramInt2, int paramInt3) {
    Class clazz = this.model.getColumnClass(paramInt3);
    Object object1 = getModelValueAt(paramInt1, paramInt3);
    Object object2 = getModelValueAt(paramInt2, paramInt3);
    if (object1 == null && object2 == null)
      return 0; 
    if (object1 == null)
      return -1; 
    if (object2 == null)
      return 1; 
    if (clazz.getSuperclass() == Number.class) {
      Number number1 = (Number)getModelValueAt(paramInt1, paramInt3);
      double d1 = number1.doubleValue();
      Number number2 = (Number)getModelValueAt(paramInt2, paramInt3);
      double d2 = number2.doubleValue();
      return (d1 < d2) ? -1 : ((d1 > d2) ? 1 : 0);
    } 
    if (clazz == Date.class) {
      Date date1 = (Date)getModelValueAt(paramInt1, paramInt3);
      long l1 = date1.getTime();
      Date date2 = (Date)getModelValueAt(paramInt2, paramInt3);
      long l2 = date2.getTime();
      return (l1 < l2) ? -1 : ((l1 > l2) ? 1 : 0);
    } 
    if (clazz == String.class) {
      String str3 = (String)getModelValueAt(paramInt1, paramInt3);
      String str4 = (String)getModelValueAt(paramInt2, paramInt3);
      int j = str3.compareTo(str4);
      return (j < 0) ? -1 : ((j > 0) ? 1 : 0);
    } 
    if (clazz == Boolean.class) {
      Boolean bool1 = (Boolean)getModelValueAt(paramInt1, paramInt3);
      boolean bool2 = bool1.booleanValue();
      Boolean bool3 = (Boolean)getModelValueAt(paramInt2, paramInt3);
      boolean bool4 = bool3.booleanValue();
      return (bool2 == bool4) ? 0 : (bool2 ? 1 : -1);
    } 
    Object object3 = getModelValueAt(paramInt1, paramInt3);
    String str1 = object3.toString();
    Object object4 = getModelValueAt(paramInt2, paramInt3);
    String str2 = object4.toString();
    int i = str1.compareTo(str2);
    return (i < 0) ? -1 : ((i > 0) ? 1 : 0);
  }
  
  public int compare(int paramInt1, int paramInt2) {
    this.compares++;
    for (byte b = 0; b < this.sortingColumns.size(); b++) {
      Integer integer = this.sortingColumns.elementAt(b);
      int i = compareRowsByColumn(paramInt1, paramInt2, integer.intValue());
      if (i != 0)
        return this.ascending ? i : -i; 
    } 
    return 0;
  }
  
  public void reallocateIndexes() {
    int i = this.model.getRowCount();
    this.indexes = new int[i];
    for (byte b = 0; b < i; b++)
      this.indexes[b] = b; 
  }
  
  public void tableChanged(TableModelEvent paramTableModelEvent) {
    reallocateIndexes();
    super.tableChanged(paramTableModelEvent);
  }
  
  public void checkModel() {
    if (this.indexes.length != this.model.getRowCount())
      System.err.println("Sorter not informed of a change in model."); 
  }
  
  public void sort(Object paramObject) {
    checkModel();
    this.compares = 0;
    shuttlesort((int[])this.indexes.clone(), this.indexes, 0, this.indexes.length);
  }
  
  public void n2sort() {
    for (byte b = 0; b < getRowCount(); b++) {
      for (int i = b + 1; i < getRowCount(); i++) {
        if (compare(this.indexes[b], this.indexes[i]) == -1)
          swap(b, i); 
      } 
    } 
  }
  
  public void shuttlesort(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int paramInt2) {
    if (paramInt2 - paramInt1 < 2)
      return; 
    int i = (paramInt1 + paramInt2) / 2;
    shuttlesort(paramArrayOfint2, paramArrayOfint1, paramInt1, i);
    shuttlesort(paramArrayOfint2, paramArrayOfint1, i, paramInt2);
    int j = paramInt1;
    int k = i;
    if (paramInt2 - paramInt1 >= 4 && compare(paramArrayOfint1[i - 1], paramArrayOfint1[i]) <= 0) {
      for (int n = paramInt1; n < paramInt2; n++)
        paramArrayOfint2[n] = paramArrayOfint1[n]; 
      return;
    } 
    for (int m = paramInt1; m < paramInt2; m++) {
      if (k >= paramInt2 || (j < i && compare(paramArrayOfint1[j], paramArrayOfint1[k]) <= 0)) {
        paramArrayOfint2[m] = paramArrayOfint1[j++];
      } else {
        paramArrayOfint2[m] = paramArrayOfint1[k++];
      } 
    } 
  }
  
  public void swap(int paramInt1, int paramInt2) {
    int i = this.indexes[paramInt1];
    this.indexes[paramInt1] = this.indexes[paramInt2];
    this.indexes[paramInt2] = i;
  }
  
  public Object getValueAt(int paramInt1, int paramInt2) {
    checkModel();
    return this.model.getValueAt(this.indexes[paramInt1], paramInt2);
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2) {
    checkModel();
    this.model.setValueAt(paramObject, this.indexes[paramInt1], paramInt2);
  }
  
  public void sortByColumn(int paramInt) {
    sortByColumn(paramInt, true);
  }
  
  public void sortByColumn(int paramInt, boolean paramBoolean) {
    this.ascending = paramBoolean;
    this.sortingColumns.removeAllElements();
    this.sortingColumns.addElement(new Integer(paramInt));
    sort(this);
    super.tableChanged(new TableModelEvent(this));
  }
  
  public void addMouseListenerToHeaderInTable(JTable paramJTable) {
    TableSorter tableSorter = this;
    JTable jTable = paramJTable;
    jTable.setColumnSelectionAllowed(false);
    MouseAdapter mouseAdapter = new MouseAdapter(this, jTable, tableSorter) {
        private final JTable val$tableView;
        
        private final TableSorter val$sorter;
        
        private final TableSorter this$0;
        
        public void mouseClicked(MouseEvent param1MouseEvent) {
          TableColumnModel tableColumnModel = this.val$tableView.getColumnModel();
          int i = tableColumnModel.getColumnIndexAtX(param1MouseEvent.getX());
          int j = this.val$tableView.convertColumnIndexToModel(i);
          if (param1MouseEvent.getButton() == 1 && param1MouseEvent.getClickCount() == 1 && j != -1) {
            int k = param1MouseEvent.getModifiers() & 0x1;
            boolean bool = (k == 0) ? true : false;
            this.val$sorter.sortByColumn(j, bool);
          } 
        }
      };
    JTableHeader jTableHeader = jTable.getTableHeader();
    if (jTableHeader != null)
      jTableHeader.addMouseListener(mouseAdapter); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\TableSorter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */