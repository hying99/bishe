package weka.gui.arffviewer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class ArffTableCellRenderer extends DefaultTableCellRenderer {
  private Color missingColor;
  
  private Color missingColorSelected;
  
  private Color highlightColor;
  
  private Color highlightColorSelected;
  
  public ArffTableCellRenderer() {
    this(new Color(223, 223, 223), new Color(192, 192, 192));
  }
  
  public ArffTableCellRenderer(Color paramColor1, Color paramColor2) {
    this(paramColor1, paramColor2, Color.RED, Color.RED.darker());
  }
  
  public ArffTableCellRenderer(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4) {
    this.missingColor = paramColor1;
    this.missingColorSelected = paramColor2;
    this.highlightColor = paramColor3;
    this.highlightColorSelected = paramColor4;
  }
  
  public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
    String str;
    boolean bool;
    Component component = super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
    if (paramJTable instanceof ArffTable) {
      str = ((ArffTable)paramJTable).getSearchString();
    } else {
      str = null;
    } 
    if (str != null && !str.equals("")) {
      bool = str.equals(paramObject.toString());
    } else {
      bool = false;
    } 
    if (paramJTable.getModel() instanceof ArffTableSorter) {
      ArffTableSorter arffTableSorter = (ArffTableSorter)paramJTable.getModel();
      if (paramInt1 >= 0) {
        if (arffTableSorter.isMissingAt(paramInt1, paramInt2)) {
          setToolTipText("missing");
          if (bool) {
            if (paramBoolean1) {
              component.setBackground(this.highlightColorSelected);
            } else {
              component.setBackground(this.highlightColor);
            } 
          } else if (paramBoolean1) {
            component.setBackground(this.missingColorSelected);
          } else {
            component.setBackground(this.missingColor);
          } 
        } else {
          setToolTipText(null);
          if (bool) {
            if (paramBoolean1) {
              component.setBackground(this.highlightColorSelected);
            } else {
              component.setBackground(this.highlightColor);
            } 
          } else if (paramBoolean1) {
            component.setBackground(paramJTable.getSelectionBackground());
          } else {
            component.setBackground(Color.WHITE);
          } 
        } 
        if (arffTableSorter.getType(paramInt1, paramInt2) == 0) {
          setHorizontalAlignment(4);
        } else {
          setHorizontalAlignment(2);
        } 
      } else {
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(0);
        if (paramJTable.getColumnModel().getSelectionModel().isSelectedIndex(paramInt2)) {
          component.setBackground(UIManager.getColor("TableHeader.background").darker());
        } else {
          component.setBackground(UIManager.getColor("TableHeader.background"));
        } 
      } 
    } 
    return component;
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\ArffTableCellRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */