package weka.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JTableHelper {
  private JTable jtable;
  
  public JTableHelper(JTable paramJTable) {
    this.jtable = paramJTable;
  }
  
  public JTable getJTable() {
    return this.jtable;
  }
  
  public int calcColumnWidth(int paramInt) {
    return calcColumnWidth(getJTable(), paramInt);
  }
  
  public static int calcColumnWidth(JTable paramJTable, int paramInt) {
    int i = calcHeaderWidth(paramJTable, paramInt);
    if (i == -1)
      return i; 
    TableColumnModel tableColumnModel = paramJTable.getColumnModel();
    TableModel tableModel = paramJTable.getModel();
    int j = tableModel.getRowCount();
    TableColumn tableColumn = tableColumnModel.getColumn(paramInt);
    try {
      for (int k = j - 1; k >= 0; k--) {
        Component component = paramJTable.prepareRenderer(paramJTable.getCellRenderer(k, paramInt), k, paramInt);
        i = Math.max(i, (component.getPreferredSize()).width + 10);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return i;
  }
  
  public int calcHeaderWidth(int paramInt) {
    return calcHeaderWidth(getJTable(), paramInt);
  }
  
  public static int calcHeaderWidth(JTable paramJTable, int paramInt) {
    if (paramJTable == null)
      return -1; 
    if (paramInt < 0 || paramInt > paramJTable.getColumnCount()) {
      System.out.println("invalid col " + paramInt);
      return -1;
    } 
    JTableHeader jTableHeader = paramJTable.getTableHeader();
    TableCellRenderer tableCellRenderer1 = null;
    if (jTableHeader != null)
      tableCellRenderer1 = jTableHeader.getDefaultRenderer(); 
    TableColumnModel tableColumnModel = paramJTable.getColumnModel();
    TableModel tableModel = paramJTable.getModel();
    TableColumn tableColumn = tableColumnModel.getColumn(paramInt);
    int i = -1;
    TableCellRenderer tableCellRenderer2 = tableColumn.getHeaderRenderer();
    if (tableCellRenderer2 == null)
      tableCellRenderer2 = tableCellRenderer1; 
    if (tableCellRenderer2 != null) {
      Component component = tableCellRenderer2.getTableCellRendererComponent(paramJTable, tableColumn.getHeaderValue(), false, false, -1, paramInt);
      i = (component.getPreferredSize()).width + 5;
    } 
    return i;
  }
  
  public void setOptimalColumnWidth(int paramInt) {
    setOptimalColumnWidth(getJTable(), paramInt);
  }
  
  public static void setOptimalColumnWidth(JTable paramJTable, int paramInt) {
    if (paramInt >= 0 && paramInt < paramJTable.getColumnModel().getColumnCount()) {
      int i = calcColumnWidth(paramJTable, paramInt);
      if (i >= 0) {
        JTableHeader jTableHeader = paramJTable.getTableHeader();
        TableColumn tableColumn = paramJTable.getColumnModel().getColumn(paramInt);
        tableColumn.setPreferredWidth(i);
        paramJTable.sizeColumnsToFit(-1);
        jTableHeader.repaint();
      } 
    } 
  }
  
  public void setOptimalColumnWidth() {
    setOptimalColumnWidth(getJTable());
  }
  
  public static void setOptimalColumnWidth(JTable paramJTable) {
    for (byte b = 0; b < paramJTable.getColumnModel().getColumnCount(); b++)
      setOptimalColumnWidth(paramJTable, b); 
  }
  
  public void setOptimalHeaderWidth(int paramInt) {
    setOptimalHeaderWidth(getJTable(), paramInt);
  }
  
  public static void setOptimalHeaderWidth(JTable paramJTable, int paramInt) {
    if (paramInt >= 0 && paramInt < paramJTable.getColumnModel().getColumnCount()) {
      int i = calcHeaderWidth(paramJTable, paramInt);
      if (i >= 0) {
        JTableHeader jTableHeader = paramJTable.getTableHeader();
        TableColumn tableColumn = paramJTable.getColumnModel().getColumn(paramInt);
        tableColumn.setPreferredWidth(i);
        paramJTable.sizeColumnsToFit(-1);
        jTableHeader.repaint();
      } 
    } 
  }
  
  public void setOptimalHeaderWidth() {
    setOptimalHeaderWidth(getJTable());
  }
  
  public static void setOptimalHeaderWidth(JTable paramJTable) {
    for (byte b = 0; b < paramJTable.getColumnModel().getColumnCount(); b++)
      setOptimalHeaderWidth(paramJTable, b); 
  }
  
  public void scrollToVisible(int paramInt1, int paramInt2) {
    scrollToVisible(getJTable(), paramInt1, paramInt2);
  }
  
  public static void scrollToVisible(JTable paramJTable, int paramInt1, int paramInt2) {
    if (!(paramJTable.getParent() instanceof JViewport))
      return; 
    JViewport jViewport = (JViewport)paramJTable.getParent();
    Rectangle rectangle = paramJTable.getCellRect(paramInt1, paramInt2, true);
    Point point = jViewport.getViewPosition();
    rectangle.setLocation(rectangle.x - point.x, rectangle.y - point.y);
    jViewport.scrollRectToVisible(rectangle);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\JTableHelper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */