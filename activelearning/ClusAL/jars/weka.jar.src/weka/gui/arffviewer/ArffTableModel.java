package weka.gui.arffviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Undoable;
import weka.core.converters.AbstractLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.gui.ComponentHelper;

public class ArffTableModel implements TableModel, Undoable {
  private HashSet listeners = new HashSet();
  
  private Instances data = null;
  
  private boolean notificationEnabled = true;
  
  private boolean undoEnabled = true;
  
  private boolean ignoreChanges = false;
  
  private Vector undoList = new Vector();
  
  private ArffTableModel() {}
  
  public ArffTableModel(String paramString) {
    this();
    if (paramString != null && !paramString.equals(""))
      loadFile(paramString); 
  }
  
  public ArffTableModel(Instances paramInstances) {
    this();
    this.data = paramInstances;
  }
  
  public boolean isNotificationEnabled() {
    return this.notificationEnabled;
  }
  
  public void setNotificationEnabled(boolean paramBoolean) {
    this.notificationEnabled = paramBoolean;
  }
  
  public boolean isUndoEnabled() {
    return this.undoEnabled;
  }
  
  public void setUndoEnabled(boolean paramBoolean) {
    this.undoEnabled = paramBoolean;
  }
  
  private void loadFile(String paramString) {
    AbstractLoader abstractLoader;
    if (paramString.toLowerCase().endsWith(".arff")) {
      abstractLoader = (AbstractLoader)new ArffLoader();
    } else if (paramString.toLowerCase().endsWith(".csv")) {
      abstractLoader = (AbstractLoader)new CSVLoader();
    } else {
      abstractLoader = null;
    } 
    if (abstractLoader != null)
      try {
        abstractLoader.setSource(new File(paramString));
        this.data = abstractLoader.getDataSet();
      } catch (Exception exception) {
        ComponentHelper.showMessageBox(null, "Error loading file...", exception.toString(), 2, 0);
        System.out.println(exception);
        this.data = null;
      }  
  }
  
  public void setInstances(Instances paramInstances) {
    this.data = paramInstances;
  }
  
  public Instances getInstances() {
    return this.data;
  }
  
  public Attribute getAttributeAt(int paramInt) {
    return (paramInt > 0 && paramInt < getColumnCount()) ? this.data.attribute(paramInt - 1) : null;
  }
  
  public int getType(int paramInt) {
    return getType(0, paramInt);
  }
  
  public int getType(int paramInt1, int paramInt2) {
    int i = 2;
    if (paramInt1 >= 0 && paramInt1 < getRowCount())
      if ((((paramInt2 > 0) ? 1 : 0) & ((paramInt2 < getColumnCount()) ? 1 : 0)) != 0)
        i = this.data.instance(paramInt1).attribute(paramInt2 - 1).type();  
    return i;
  }
  
  public void deleteAttributeAt(int paramInt) {
    deleteAttributeAt(paramInt, true);
  }
  
  public void deleteAttributeAt(int paramInt, boolean paramBoolean) {
    if (paramInt > 0 && paramInt < getColumnCount()) {
      if (!this.ignoreChanges)
        addUndoPoint(); 
      this.data.deleteAttributeAt(paramInt - 1);
      if (paramBoolean)
        notifyListener(new TableModelEvent(this, -1)); 
    } 
  }
  
  public void deleteAttributes(int[] paramArrayOfint) {
    Arrays.sort(paramArrayOfint);
    addUndoPoint();
    this.ignoreChanges = true;
    for (int i = paramArrayOfint.length - 1; i >= 0; i--)
      deleteAttributeAt(paramArrayOfint[i], false); 
    this.ignoreChanges = false;
    notifyListener(new TableModelEvent(this, -1));
  }
  
  public void renameAttributeAt(int paramInt, String paramString) {
    if (paramInt > 0 && paramInt < getColumnCount()) {
      addUndoPoint();
      this.data.renameAttribute(paramInt - 1, paramString);
      notifyListener(new TableModelEvent(this, -1));
    } 
  }
  
  public void deleteInstanceAt(int paramInt) {
    deleteInstanceAt(paramInt, true);
  }
  
  public void deleteInstanceAt(int paramInt, boolean paramBoolean) {
    if (paramInt >= 0 && paramInt < getRowCount()) {
      if (!this.ignoreChanges)
        addUndoPoint(); 
      this.data.delete(paramInt);
      if (paramBoolean)
        notifyListener(new TableModelEvent(this, paramInt, paramInt, -1, -1)); 
    } 
  }
  
  public void deleteInstances(int[] paramArrayOfint) {
    Arrays.sort(paramArrayOfint);
    addUndoPoint();
    this.ignoreChanges = true;
    for (int i = paramArrayOfint.length - 1; i >= 0; i--)
      deleteInstanceAt(paramArrayOfint[i], false); 
    this.ignoreChanges = false;
    notifyListener(new TableModelEvent(this, paramArrayOfint[0], paramArrayOfint[paramArrayOfint.length - 1], -1, -1));
  }
  
  public void sortInstances(int paramInt) {
    if (paramInt > 0 && paramInt < getColumnCount()) {
      addUndoPoint();
      this.data.sort(paramInt - 1);
      notifyListener(new TableModelEvent(this));
    } 
  }
  
  public int getAttributeColumn(String paramString) {
    int i = -1;
    for (byte b = 0; b < this.data.numAttributes(); b++) {
      if (this.data.attribute(b).name().equals(paramString)) {
        i = b + 1;
        break;
      } 
    } 
    return i;
  }
  
  public Class getColumnClass(int paramInt) {
    Class clazz;
    Class clazz1 = null;
    if (paramInt >= 0 && paramInt < getColumnCount())
      if (paramInt == 0) {
        clazz1 = Integer.class;
      } else {
        clazz = String.class;
      }  
    return clazz;
  }
  
  public int getColumnCount() {
    int i = 1;
    if (this.data != null)
      i += this.data.numAttributes(); 
    return i;
  }
  
  private boolean isClassIndex(int paramInt) {
    int i = this.data.classIndex();
    return ((i == -1 && this.data.numAttributes() == paramInt) || i == paramInt - 1);
  }
  
  public String getColumnName(int paramInt) {
    String str = "";
    if (paramInt >= 0 && paramInt < getColumnCount())
      if (paramInt == 0) {
        str = "<html><center>No.<br><font size=\"-2\">&nbsp;</font></center></html>";
      } else if (this.data != null && paramInt - 1 < this.data.numAttributes()) {
        str = "<html><center>";
        if (isClassIndex(paramInt)) {
          str = str + "<b>" + this.data.attribute(paramInt - 1).name() + "</b>";
        } else {
          str = str + this.data.attribute(paramInt - 1).name();
        } 
        switch (getType(paramInt)) {
          case 3:
            str = str + "<br><font size=\"-2\">Date</font>";
            break;
          case 1:
            str = str + "<br><font size=\"-2\">Nominal</font>";
            break;
          case 2:
            str = str + "<br><font size=\"-2\">String</font>";
            break;
          case 0:
            str = str + "<br><font size=\"-2\">Numeric</font>";
            break;
        } 
        str = str + "</center></html>";
      }  
    return str;
  }
  
  public int getRowCount() {
    return (this.data == null) ? 0 : this.data.numInstances();
  }
  
  public boolean isMissingAt(int paramInt1, int paramInt2) {
    boolean bool = false;
    if (paramInt1 >= 0 && paramInt1 < getRowCount())
      if ((((paramInt2 > 0) ? 1 : 0) & ((paramInt2 < getColumnCount()) ? 1 : 0)) != 0)
        bool = this.data.instance(paramInt1).isMissing(paramInt2 - 1);  
    return bool;
  }
  
  public Object getValueAt(int paramInt1, int paramInt2) {
    String str1;
    Integer integer = null;
    if (paramInt1 >= 0 && paramInt1 < getRowCount())
      if ((((paramInt2 >= 0) ? 1 : 0) & ((paramInt2 < getColumnCount()) ? 1 : 0)) != 0)
        if (paramInt2 == 0) {
          integer = new Integer(paramInt1 + 1);
        } else if (isMissingAt(paramInt1, paramInt2)) {
          str1 = "?";
        } else {
          switch (getType(paramInt2)) {
            case 1:
            case 2:
            case 3:
              str1 = this.data.instance(paramInt1).stringValue(paramInt2 - 1);
              break;
            case 0:
              str1 = (new Double(this.data.instance(paramInt1).value(paramInt2 - 1))).toString();
              break;
          } 
        }   
    String str2 = str1.toString();
    if (str2.indexOf("\n") > -1 || str2.indexOf("\r") > -1) {
      str2 = str2.replaceAll("\\r\\n", ", ");
      str2 = str2.replaceAll("\\r", ", ").replaceAll("\\n", ", ");
      str2 = str2.replaceAll(", $", "");
      str1 = str2;
    } 
    return str1;
  }
  
  public boolean isCellEditable(int paramInt1, int paramInt2) {
    return (paramInt2 > 0);
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2) {
    setValueAt(paramObject, paramInt1, paramInt2, true);
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean) {
    if (!this.ignoreChanges)
      addUndoPoint(); 
    Object object = getValueAt(paramInt1, paramInt2);
    int i = getType(paramInt1, paramInt2);
    int j = paramInt2 - 1;
    Instance instance = this.data.instance(paramInt1);
    Attribute attribute = instance.attribute(j);
    if (paramObject == null || paramObject.toString().equals("?")) {
      instance.setValue(j, Instance.missingValue());
    } else {
      String str = paramObject.toString();
      switch (i) {
        case 3:
          try {
            attribute.parseDate(str);
            instance.setValue(j, attribute.parseDate(str));
          } catch (Exception exception) {}
          break;
        case 1:
          if (attribute.indexOfValue(str) > -1)
            instance.setValue(j, attribute.indexOfValue(str)); 
          break;
        case 2:
          instance.setValue(j, str);
          break;
        case 0:
          try {
            Double.parseDouble(str);
            instance.setValue(j, Double.parseDouble(str));
          } catch (Exception exception) {}
          break;
      } 
    } 
    if (!object.toString().equals(paramObject.toString()) && paramBoolean)
      notifyListener(new TableModelEvent(this, paramInt1, paramInt2)); 
  }
  
  public void addTableModelListener(TableModelListener paramTableModelListener) {
    this.listeners.add(paramTableModelListener);
  }
  
  public void removeTableModelListener(TableModelListener paramTableModelListener) {
    this.listeners.remove(paramTableModelListener);
  }
  
  public void notifyListener(TableModelEvent paramTableModelEvent) {
    if (!isNotificationEnabled())
      return; 
    for (TableModelListener tableModelListener : this.listeners)
      tableModelListener.tableChanged(paramTableModelEvent); 
  }
  
  public void clearUndo() {
    this.undoList = new Vector();
  }
  
  public boolean canUndo() {
    return !this.undoList.isEmpty();
  }
  
  public void undo() {
    if (canUndo()) {
      File file = this.undoList.get(this.undoList.size() - 1);
      try {
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        Instances instances = (Instances)objectInputStream.readObject();
        objectInputStream.close();
        setInstances(instances);
        notifyListener(new TableModelEvent(this, -1));
        notifyListener(new TableModelEvent(this));
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      file.delete();
      this.undoList.remove(this.undoList.size() - 1);
    } 
  }
  
  public void addUndoPoint() {
    if (!isUndoEnabled())
      return; 
    if (getInstances() != null)
      try {
        File file = File.createTempFile("arffviewer", null);
        file.deleteOnExit();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        objectOutputStream.writeObject(getInstances());
        objectOutputStream.flush();
        objectOutputStream.close();
        this.undoList.add(file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\ArffTableModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */