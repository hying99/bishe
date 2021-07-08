package weka.gui.arffviewer;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import weka.core.Instances;
import weka.core.Undoable;
import weka.core.Utils;
import weka.gui.ComponentHelper;
import weka.gui.ListSelectorDialog;

public class ArffPanel extends JPanel implements ActionListener, ChangeListener, MouseListener, Undoable {
  public static final String TAB_INSTANCES = "Instances";
  
  private ArffTable tableArff;
  
  private JPopupMenu popupHeader;
  
  private JPopupMenu popupRows;
  
  private JLabel labelName;
  
  private JMenuItem menuItemMean;
  
  private JMenuItem menuItemSetAllValues;
  
  private JMenuItem menuItemSetMissingValues;
  
  private JMenuItem menuItemReplaceValues;
  
  private JMenuItem menuItemRenameAttribute;
  
  private JMenuItem menuItemDeleteAttribute;
  
  private JMenuItem menuItemDeleteAttributes;
  
  private JMenuItem menuItemSortInstances;
  
  private JMenuItem menuItemDeleteSelectedInstance;
  
  private JMenuItem menuItemDeleteAllSelectedInstances;
  
  private JMenuItem menuItemSearch;
  
  private JMenuItem menuItemClearSearch;
  
  private JMenuItem menuItemUndo;
  
  private JMenuItem menuItemCopy;
  
  private String filename;
  
  private String title;
  
  private int currentCol;
  
  private boolean changed;
  
  private HashSet changeListeners;
  
  private String lastSearch;
  
  private String lastReplace;
  
  public ArffPanel() {
    initialize();
    createPanel();
  }
  
  public ArffPanel(String paramString) {
    this();
    loadFile(paramString);
  }
  
  public ArffPanel(Instances paramInstances) {
    this();
    this.filename = "";
    setInstances(paramInstances);
  }
  
  protected void initialize() {
    this.filename = "";
    this.title = "";
    this.currentCol = -1;
    this.lastSearch = "";
    this.lastReplace = "";
    this.changed = false;
    this.changeListeners = new HashSet();
  }
  
  protected void createPanel() {
    setLayout(new BorderLayout());
    this.popupHeader = new JPopupMenu();
    this.popupHeader.addMouseListener(this);
    this.menuItemMean = new JMenuItem("Get mean...");
    this.menuItemMean.addActionListener(this);
    this.popupHeader.add(this.menuItemMean);
    this.popupHeader.addSeparator();
    this.menuItemSetAllValues = new JMenuItem("Set all values to...");
    this.menuItemSetAllValues.addActionListener(this);
    this.popupHeader.add(this.menuItemSetAllValues);
    this.menuItemSetMissingValues = new JMenuItem("Set missing values to...");
    this.menuItemSetMissingValues.addActionListener(this);
    this.popupHeader.add(this.menuItemSetMissingValues);
    this.menuItemReplaceValues = new JMenuItem("Replace values with...");
    this.menuItemReplaceValues.addActionListener(this);
    this.popupHeader.add(this.menuItemReplaceValues);
    this.popupHeader.addSeparator();
    this.menuItemRenameAttribute = new JMenuItem("Rename attribute...");
    this.menuItemRenameAttribute.addActionListener(this);
    this.popupHeader.add(this.menuItemRenameAttribute);
    this.menuItemDeleteAttribute = new JMenuItem("Delete attribute");
    this.menuItemDeleteAttribute.addActionListener(this);
    this.popupHeader.add(this.menuItemDeleteAttribute);
    this.menuItemDeleteAttributes = new JMenuItem("Delete attributes...");
    this.menuItemDeleteAttributes.addActionListener(this);
    this.popupHeader.add(this.menuItemDeleteAttributes);
    this.menuItemSortInstances = new JMenuItem("Sort data (ascending)");
    this.menuItemSortInstances.addActionListener(this);
    this.popupHeader.add(this.menuItemSortInstances);
    this.popupRows = new JPopupMenu();
    this.popupRows.addMouseListener(this);
    this.menuItemUndo = new JMenuItem("Undo");
    this.menuItemUndo.addActionListener(this);
    this.popupRows.add(this.menuItemUndo);
    this.popupRows.addSeparator();
    this.menuItemCopy = new JMenuItem("Copy");
    this.menuItemCopy.addActionListener(this);
    this.popupRows.add(this.menuItemCopy);
    this.popupRows.addSeparator();
    this.menuItemSearch = new JMenuItem("Search...");
    this.menuItemSearch.addActionListener(this);
    this.popupRows.add(this.menuItemSearch);
    this.menuItemClearSearch = new JMenuItem("Clear search");
    this.menuItemClearSearch.addActionListener(this);
    this.popupRows.add(this.menuItemClearSearch);
    this.popupRows.addSeparator();
    this.menuItemDeleteSelectedInstance = new JMenuItem("Delete selected instance");
    this.menuItemDeleteSelectedInstance.addActionListener(this);
    this.popupRows.add(this.menuItemDeleteSelectedInstance);
    this.menuItemDeleteAllSelectedInstances = new JMenuItem("Delete ALL selected instances");
    this.menuItemDeleteAllSelectedInstances.addActionListener(this);
    this.popupRows.add(this.menuItemDeleteAllSelectedInstances);
    this.tableArff = new ArffTable();
    this.tableArff.setToolTipText("Right click for context menu");
    this.tableArff.getTableHeader().addMouseListener(this);
    this.tableArff.getTableHeader().setToolTipText("<html><b>Sort view:</b> left click = ascending / Shift + left click = descending<br><b>Menu:</b> right click</html>");
    this.tableArff.getTableHeader().setDefaultRenderer(new ArffTableCellRenderer());
    this.tableArff.addChangeListener(this);
    this.tableArff.addMouseListener(this);
    JScrollPane jScrollPane = new JScrollPane(this.tableArff);
    add(jScrollPane, "Center");
    this.labelName = new JLabel();
    add(this.labelName, "North");
  }
  
  private void setMenu() {
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    boolean bool2 = (arffTableSorter.getInstances().numAttributes() > 0) ? true : false;
    boolean bool3 = (arffTableSorter.getInstances().numInstances() > 0) ? true : false;
    boolean bool4 = (bool2 && this.currentCol > 0) ? true : false;
    boolean bool1 = (bool4 && arffTableSorter.getAttributeAt(this.currentCol).isNumeric()) ? true : false;
    this.menuItemUndo.setEnabled(canUndo());
    this.menuItemCopy.setEnabled(true);
    this.menuItemSearch.setEnabled(true);
    this.menuItemClearSearch.setEnabled(true);
    this.menuItemMean.setEnabled(bool1);
    this.menuItemSetAllValues.setEnabled(bool4);
    this.menuItemSetMissingValues.setEnabled(bool4);
    this.menuItemReplaceValues.setEnabled(bool4);
    this.menuItemRenameAttribute.setEnabled(bool4);
    this.menuItemDeleteAttribute.setEnabled(bool4);
    this.menuItemDeleteAttributes.setEnabled(bool4);
    this.menuItemSortInstances.setEnabled((bool3 && bool4));
    this.menuItemDeleteSelectedInstance.setEnabled((bool3 && this.tableArff.getSelectedRow() > -1));
    this.menuItemDeleteAllSelectedInstances.setEnabled((bool3 && (this.tableArff.getSelectedRows()).length > 0));
  }
  
  public ArffTable getTable() {
    return this.tableArff;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public String getFilename() {
    return this.filename;
  }
  
  public void setFilename(String paramString) {
    this.filename = paramString;
    createTitle();
  }
  
  public Instances getInstances() {
    Instances instances = null;
    if (this.tableArff.getModel() != null)
      instances = ((ArffTableSorter)this.tableArff.getModel()).getInstances(); 
    return instances;
  }
  
  public void setInstances(Instances paramInstances) {
    ArffTableSorter arffTableSorter;
    this.filename = "Instances";
    createTitle();
    if (paramInstances == null) {
      arffTableSorter = null;
    } else {
      arffTableSorter = new ArffTableSorter(paramInstances);
    } 
    this.tableArff.setModel((TableModel)arffTableSorter);
    clearUndo();
    setChanged(false);
    createName();
  }
  
  public Vector getAttributes() {
    Vector vector = new Vector();
    for (byte b = 0; b < getInstances().numAttributes(); b++)
      vector.add(getInstances().attribute(b).name()); 
    Collections.sort(vector);
    return vector;
  }
  
  public void setChanged(boolean paramBoolean) {
    if (!paramBoolean) {
      this.changed = paramBoolean;
      createTitle();
    } 
  }
  
  public boolean isChanged() {
    return this.changed;
  }
  
  public boolean isUndoEnabled() {
    return ((ArffTableSorter)this.tableArff.getModel()).isUndoEnabled();
  }
  
  public void setUndoEnabled(boolean paramBoolean) {
    ((ArffTableSorter)this.tableArff.getModel()).setUndoEnabled(paramBoolean);
  }
  
  public void clearUndo() {
    ((ArffTableSorter)this.tableArff.getModel()).clearUndo();
  }
  
  public boolean canUndo() {
    return ((ArffTableSorter)this.tableArff.getModel()).canUndo();
  }
  
  public void undo() {
    if (canUndo()) {
      ((ArffTableSorter)this.tableArff.getModel()).undo();
      notifyListener();
    } 
  }
  
  public void addUndoPoint() {
    ((ArffTableSorter)this.tableArff.getModel()).addUndoPoint();
    setMenu();
  }
  
  private void createTitle() {
    if (this.filename.equals("")) {
      this.title = "-none-";
    } else if (this.filename.equals("Instances")) {
      this.title = "Instances";
    } else {
      try {
        File file = new File(this.filename);
        this.title = file.getName();
      } catch (Exception exception) {
        this.title = "-none-";
      } 
    } 
    if (isChanged())
      this.title += " *"; 
  }
  
  private void createName() {
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    if (arffTableSorter != null) {
      this.labelName.setText("Relation: " + arffTableSorter.getInstances().relationName());
    } else {
      this.labelName.setText("");
    } 
  }
  
  private void loadFile(String paramString) {
    ArffTableSorter arffTableSorter;
    this.filename = paramString;
    createTitle();
    if (paramString.equals("")) {
      arffTableSorter = null;
    } else {
      arffTableSorter = new ArffTableSorter(paramString);
    } 
    this.tableArff.setModel((TableModel)arffTableSorter);
    setChanged(false);
    createName();
  }
  
  private void calcMean() {
    if (this.currentCol == -1)
      return; 
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    if (!arffTableSorter.getAttributeAt(this.currentCol).isNumeric())
      return; 
    double d = 0.0D;
    for (byte b = 0; b < arffTableSorter.getRowCount(); b++)
      d += arffTableSorter.getInstances().instance(b).value(this.currentCol - 1); 
    d /= arffTableSorter.getRowCount();
    ComponentHelper.showMessageBox(getParent(), "Mean for attribute...", "Mean for attribute '" + this.tableArff.getPlainColumnName(this.currentCol) + "':\n\t" + Utils.doubleToString(d, 3), 2, -1);
  }
  
  private void setValues(Object paramObject) {
    String str1;
    String str2;
    String str3 = "";
    String str4 = "";
    if (paramObject == this.menuItemSetMissingValues) {
      str2 = "Replace missing values...";
      str1 = "New value for MISSING values";
    } else if (paramObject == this.menuItemSetAllValues) {
      str2 = "Set all values...";
      str1 = "New value for ALL values";
    } else if (paramObject == this.menuItemReplaceValues) {
      str2 = "Replace values...";
      str1 = "Old value";
    } else {
      return;
    } 
    str3 = ComponentHelper.showInputBox(this.tableArff.getParent(), str2, str1, this.lastSearch);
    if (str3 == null)
      return; 
    this.lastSearch = str3;
    if (paramObject == this.menuItemReplaceValues) {
      str4 = ComponentHelper.showInputBox(this.tableArff.getParent(), str2, "New value", this.lastReplace);
      if (str4 == null)
        return; 
      this.lastReplace = str4;
    } 
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    arffTableSorter.setNotificationEnabled(false);
    addUndoPoint();
    arffTableSorter.setUndoEnabled(false);
    for (byte b = 0; b < this.tableArff.getRowCount(); b++) {
      if (paramObject == this.menuItemSetAllValues) {
        arffTableSorter.setValueAt(str3, b, this.currentCol);
      } else if (paramObject == this.menuItemSetMissingValues && arffTableSorter.isMissingAt(b, this.currentCol)) {
        arffTableSorter.setValueAt(str3, b, this.currentCol);
      } else if (paramObject == this.menuItemReplaceValues && arffTableSorter.getValueAt(b, this.currentCol).toString().equals(str3)) {
        arffTableSorter.setValueAt(str4, b, this.currentCol);
      } 
    } 
    arffTableSorter.setUndoEnabled(true);
    arffTableSorter.setNotificationEnabled(true);
    arffTableSorter.notifyListener(new TableModelEvent((TableModel)arffTableSorter, 0, arffTableSorter.getRowCount(), this.currentCol, 0));
    this.tableArff.repaint();
  }
  
  public void deleteAttribute() {
    if (this.currentCol == -1)
      return; 
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    if (ComponentHelper.showMessageBox(getParent(), "Confirm...", "Do you really want to delete the attribute '" + arffTableSorter.getAttributeAt(this.currentCol).name() + "'?", 0, 3) != 0)
      return; 
    setCursor(Cursor.getPredefinedCursor(3));
    arffTableSorter.deleteAttributeAt(this.currentCol);
    setCursor(Cursor.getPredefinedCursor(0));
  }
  
  public void deleteAttributes() {
    JList jList = new JList(getAttributes());
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, jList);
    int i = listSelectorDialog.showDialog();
    if (i != 0)
      return; 
    Object[] arrayOfObject = jList.getSelectedValues();
    if (ComponentHelper.showMessageBox(getParent(), "Confirm...", "Do you really want to delete these " + arrayOfObject.length + " attributes?", 0, 3) != 0)
      return; 
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    int[] arrayOfInt = new int[arrayOfObject.length];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfInt[b] = arffTableSorter.getAttributeColumn(arrayOfObject[b].toString()); 
    setCursor(Cursor.getPredefinedCursor(3));
    arffTableSorter.deleteAttributes(arrayOfInt);
    setCursor(Cursor.getPredefinedCursor(0));
  }
  
  private void renameAttribute() {
    if (this.currentCol == -1)
      return; 
    ArffTableSorter arffTableSorter = (ArffTableSorter)this.tableArff.getModel();
    String str = ComponentHelper.showInputBox(getParent(), "Rename attribute...", "Enter new Attribute name", arffTableSorter.getAttributeAt(this.currentCol).name());
    if (str == null)
      return; 
    setCursor(Cursor.getPredefinedCursor(3));
    arffTableSorter.renameAttributeAt(this.currentCol, str);
    setCursor(Cursor.getPredefinedCursor(0));
  }
  
  public void deleteInstance() {
    int i = this.tableArff.getSelectedRow();
    if (i == -1)
      return; 
    ((ArffTableSorter)this.tableArff.getModel()).deleteInstanceAt(i);
  }
  
  public void deleteInstances() {
    if (this.tableArff.getSelectedRow() == -1)
      return; 
    int[] arrayOfInt = this.tableArff.getSelectedRows();
    ((ArffTableSorter)this.tableArff.getModel()).deleteInstances(arrayOfInt);
  }
  
  public void sortInstances() {
    if (this.currentCol == -1)
      return; 
    ((ArffTableSorter)this.tableArff.getModel()).sortInstances(this.currentCol);
  }
  
  public void copyContent() {
    StringSelection stringSelection = getTable().getStringSelection();
    if (stringSelection == null)
      return; 
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, stringSelection);
  }
  
  public void search() {
    String str = ComponentHelper.showInputBox(getParent(), "Search...", "Enter the string to search for", this.lastSearch);
    if (str != null)
      this.lastSearch = str; 
    getTable().setSearchString(str);
  }
  
  public void clearSearch() {
    getTable().setSearchString("");
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object == this.menuItemMean) {
      calcMean();
    } else if (object == this.menuItemSetAllValues) {
      setValues(this.menuItemSetAllValues);
    } else if (object == this.menuItemSetMissingValues) {
      setValues(this.menuItemSetMissingValues);
    } else if (object == this.menuItemReplaceValues) {
      setValues(this.menuItemReplaceValues);
    } else if (object == this.menuItemRenameAttribute) {
      renameAttribute();
    } else if (object == this.menuItemDeleteAttribute) {
      deleteAttribute();
    } else if (object == this.menuItemDeleteAttributes) {
      deleteAttributes();
    } else if (object == this.menuItemDeleteSelectedInstance) {
      deleteInstance();
    } else if (object == this.menuItemDeleteAllSelectedInstances) {
      deleteInstances();
    } else if (object == this.menuItemSortInstances) {
      sortInstances();
    } else if (object == this.menuItemSearch) {
      search();
    } else if (object == this.menuItemClearSearch) {
      clearSearch();
    } else if (object == this.menuItemUndo) {
      undo();
    } else if (object == this.menuItemCopy) {
      copyContent();
    } 
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    int i = this.tableArff.columnAtPoint(paramMouseEvent.getPoint());
    if (paramMouseEvent.getSource() == this.tableArff.getTableHeader()) {
      this.currentCol = i;
      if (paramMouseEvent.getButton() == 3 && paramMouseEvent.getClickCount() == 1) {
        paramMouseEvent.consume();
        setMenu();
        this.popupHeader.show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
      } 
    } else if (paramMouseEvent.getSource() == this.tableArff && paramMouseEvent.getButton() == 3 && paramMouseEvent.getClickCount() == 1) {
      paramMouseEvent.consume();
      setMenu();
      this.popupRows.show(paramMouseEvent.getComponent(), paramMouseEvent.getX(), paramMouseEvent.getY());
    } 
    if (paramMouseEvent.getButton() == 1 && paramMouseEvent.getClickCount() == 1 && i > -1)
      this.tableArff.setSelectedColumn(i); 
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {}
  
  public void mouseReleased(MouseEvent paramMouseEvent) {}
  
  public void stateChanged(ChangeEvent paramChangeEvent) {
    this.changed = true;
    createTitle();
    notifyListener();
  }
  
  public void notifyListener() {
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


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\ArffPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */