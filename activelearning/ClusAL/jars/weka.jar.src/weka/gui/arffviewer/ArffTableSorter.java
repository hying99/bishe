package weka.gui.arffviewer;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Undoable;
import weka.gui.TableSorter;

public class ArffTableSorter extends TableSorter implements Undoable {
  public ArffTableSorter(String paramString) {
    this(new ArffTableModel(paramString));
  }
  
  public ArffTableSorter(Instances paramInstances) {
    this(new ArffTableModel(paramInstances));
  }
  
  public ArffTableSorter(TableModel paramTableModel) {
    super(paramTableModel);
  }
  
  public boolean isNotificationEnabled() {
    return ((ArffTableModel)this.model).isNotificationEnabled();
  }
  
  public void setNotificationEnabled(boolean paramBoolean) {
    ((ArffTableModel)this.model).setNotificationEnabled(paramBoolean);
  }
  
  public boolean isUndoEnabled() {
    return ((ArffTableModel)this.model).isUndoEnabled();
  }
  
  public void setUndoEnabled(boolean paramBoolean) {
    ((ArffTableModel)this.model).setUndoEnabled(paramBoolean);
  }
  
  public Object getModelValueAt(int paramInt1, int paramInt2) {
    Object object = this.model.getValueAt(paramInt1, paramInt2);
    if (((ArffTableModel)this.model).isMissingAt(paramInt1, paramInt2))
      object = null; 
    return object;
  }
  
  public int getType(int paramInt) {
    return ((ArffTableModel)this.model).getType(this.indexes[0], paramInt);
  }
  
  public int getType(int paramInt1, int paramInt2) {
    return ((ArffTableModel)this.model).getType(this.indexes[paramInt1], paramInt2);
  }
  
  public void deleteAttributeAt(int paramInt) {
    ((ArffTableModel)this.model).deleteAttributeAt(paramInt);
  }
  
  public void deleteAttributes(int[] paramArrayOfint) {
    ((ArffTableModel)this.model).deleteAttributes(paramArrayOfint);
  }
  
  public void renameAttributeAt(int paramInt, String paramString) {
    ((ArffTableModel)this.model).renameAttributeAt(paramInt, paramString);
  }
  
  public void deleteInstanceAt(int paramInt) {
    ((ArffTableModel)this.model).deleteInstanceAt(paramInt);
  }
  
  public void deleteInstances(int[] paramArrayOfint) {
    ((ArffTableModel)this.model).deleteInstances(paramArrayOfint);
  }
  
  public void sortInstances(int paramInt) {
    ((ArffTableModel)this.model).sortInstances(paramInt);
  }
  
  public int getAttributeColumn(String paramString) {
    return ((ArffTableModel)this.model).getAttributeColumn(paramString);
  }
  
  public boolean isMissingAt(int paramInt1, int paramInt2) {
    return ((ArffTableModel)this.model).isMissingAt(this.indexes[paramInt1], paramInt2);
  }
  
  public void setInstances(Instances paramInstances) {
    ((ArffTableModel)this.model).setInstances(paramInstances);
  }
  
  public Instances getInstances() {
    return ((ArffTableModel)this.model).getInstances();
  }
  
  public Attribute getAttributeAt(int paramInt) {
    return ((ArffTableModel)this.model).getAttributeAt(paramInt);
  }
  
  public void addTableModelListener(TableModelListener paramTableModelListener) {
    if (getModel() != null)
      ((ArffTableModel)getModel()).addTableModelListener(paramTableModelListener); 
  }
  
  public void removeTableModelListener(TableModelListener paramTableModelListener) {
    if (getModel() != null)
      ((ArffTableModel)getModel()).removeTableModelListener(paramTableModelListener); 
  }
  
  public void notifyListener(TableModelEvent paramTableModelEvent) {
    ((ArffTableModel)getModel()).notifyListener(paramTableModelEvent);
  }
  
  public void clearUndo() {
    ((ArffTableModel)getModel()).clearUndo();
  }
  
  public boolean canUndo() {
    return ((ArffTableModel)getModel()).canUndo();
  }
  
  public void undo() {
    ((ArffTableModel)getModel()).undo();
  }
  
  public void addUndoPoint() {
    ((ArffTableModel)getModel()).addUndoPoint();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\ArffTableSorter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */