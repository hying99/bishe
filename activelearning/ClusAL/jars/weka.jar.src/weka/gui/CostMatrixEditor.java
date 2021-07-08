package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import weka.classifiers.CostMatrix;

public class CostMatrixEditor implements PropertyEditor {
  private CostMatrix m_matrix = new CostMatrix(2);
  
  private PropertyChangeSupport m_propSupport = new PropertyChangeSupport(this);
  
  private CustomEditor m_customEditor = new CustomEditor(this);
  
  private JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  static Class class$java$lang$Double;
  
  public void setValue(Object paramObject) {
    this.m_matrix = (CostMatrix)paramObject;
    this.m_customEditor.matrixChanged();
  }
  
  public Object getValue() {
    return this.m_matrix;
  }
  
  public boolean isPaintable() {
    return true;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {
    paramGraphics.drawString(this.m_matrix.size() + " x " + this.m_matrix.size() + " cost matrix", paramRectangle.x, paramRectangle.y + paramRectangle.height);
  }
  
  public String getJavaInitializationString() {
    return "new CostMatrix(" + this.m_matrix.size() + ")";
  }
  
  public String getAsText() {
    return null;
  }
  
  public void setAsText(String paramString) {
    throw new IllegalArgumentException("CostMatrixEditor: CostMatrix properties cannot be expressed as text");
  }
  
  public String[] getTags() {
    return null;
  }
  
  public Component getCustomEditor() {
    return this.m_customEditor;
  }
  
  public boolean supportsCustomEditor() {
    return true;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_propSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_propSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  static Class class$(String paramString) {
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
  
  private class CustomEditor extends JPanel implements ActionListener, TableModelListener {
    private CostMatrixEditor.CostMatrixTableModel m_tableModel;
    
    private JButton m_defaultButton;
    
    private JButton m_openButton;
    
    private JButton m_saveButton;
    
    private JTextField m_classesField;
    
    private final CostMatrixEditor this$0;
    
    public CustomEditor(CostMatrixEditor this$0) {
      this.this$0 = this$0;
      this$0.m_fileChooser.setFileFilter(new ExtensionFileFilter(CostMatrix.FILE_EXTENSION, "Cost files"));
      this$0.m_fileChooser.setFileSelectionMode(0);
      this.m_defaultButton = new JButton("Defaults");
      this.m_openButton = new JButton("Open...");
      this.m_saveButton = new JButton("Save...");
      this.m_classesField = new JTextField("" + this$0.m_matrix.size());
      this.m_defaultButton.addActionListener(this);
      this.m_openButton.addActionListener(this);
      this.m_saveButton.addActionListener(this);
      this.m_classesField.addActionListener(this);
      JPanel jPanel1 = new JPanel();
      jPanel1.setLayout(new GridLayout(1, 2, 0, 0));
      jPanel1.add(new JLabel("Classes:", 4));
      jPanel1.add(this.m_classesField);
      JPanel jPanel2 = new JPanel();
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      jPanel2.setLayout(gridBagLayout);
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = -1;
      gridBagConstraints.insets = new Insets(2, 10, 2, 10);
      gridBagConstraints.fill = 2;
      gridBagLayout.setConstraints(this.m_defaultButton, gridBagConstraints);
      jPanel2.add(this.m_defaultButton);
      gridBagLayout.setConstraints(this.m_openButton, gridBagConstraints);
      jPanel2.add(this.m_openButton);
      gridBagLayout.setConstraints(this.m_saveButton, gridBagConstraints);
      jPanel2.add(this.m_saveButton);
      gridBagLayout.setConstraints(jPanel1, gridBagConstraints);
      jPanel2.add(jPanel1);
      JPanel jPanel3 = new JPanel();
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.fill = 1;
      gridBagLayout.setConstraints(jPanel3, gridBagConstraints);
      jPanel2.add(jPanel3);
      this.m_tableModel = new CostMatrixEditor.CostMatrixTableModel();
      this.m_tableModel.addTableModelListener(this);
      JTable jTable = new JTable(this.m_tableModel);
      setLayout(new BorderLayout());
      add(jTable, "Center");
      add(jPanel2, "East");
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (param1ActionEvent.getSource() == this.m_defaultButton) {
        this.this$0.m_matrix.initialize();
        matrixChanged();
      } else if (param1ActionEvent.getSource() == this.m_openButton) {
        openMatrix();
      } else if (param1ActionEvent.getSource() == this.m_saveButton) {
        saveMatrix();
      } else if (param1ActionEvent.getSource() == this.m_classesField) {
        try {
          int i = Integer.parseInt(this.m_classesField.getText());
          if (i > 0 && i != this.this$0.m_matrix.size())
            this.this$0.setValue(new CostMatrix(i)); 
        } catch (Exception exception) {}
      } 
    }
    
    public void tableChanged(TableModelEvent param1TableModelEvent) {
      this.this$0.m_propSupport.firePropertyChange((String)null, (Object)null, (Object)null);
    }
    
    public void matrixChanged() {
      this.m_tableModel.fireTableStructureChanged();
      this.m_classesField.setText("" + this.this$0.m_matrix.size());
    }
    
    private void openMatrix() {
      int i = this.this$0.m_fileChooser.showOpenDialog(this);
      if (i == 0) {
        File file = this.this$0.m_fileChooser.getSelectedFile();
        BufferedReader bufferedReader = null;
        try {
          bufferedReader = new BufferedReader(new FileReader(file));
          this.this$0.m_matrix = new CostMatrix(bufferedReader);
          bufferedReader.close();
          matrixChanged();
        } catch (Exception exception) {
          JOptionPane.showMessageDialog(this, "Error reading file '" + file.getName() + "':\n" + exception.getMessage(), "Load failed", 0);
          System.out.println(exception.getMessage());
        } 
      } 
    }
    
    private void saveMatrix() {
      int i = this.this$0.m_fileChooser.showSaveDialog(this);
      if (i == 0) {
        File file = this.this$0.m_fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(CostMatrix.FILE_EXTENSION))
          file = new File(file.getParent(), file.getName() + CostMatrix.FILE_EXTENSION); 
        BufferedWriter bufferedWriter = null;
        try {
          bufferedWriter = new BufferedWriter(new FileWriter(file));
          this.this$0.m_matrix.write(bufferedWriter);
          bufferedWriter.close();
        } catch (Exception exception) {
          JOptionPane.showMessageDialog(this, "Error writing file '" + file.getName() + "':\n" + exception.getMessage(), "Save failed", 0);
          System.out.println(exception.getMessage());
        } 
      } 
    }
  }
  
  private class CostMatrixTableModel extends AbstractTableModel {
    private final CostMatrixEditor this$0;
    
    private CostMatrixTableModel(CostMatrixEditor this$0) {
      CostMatrixEditor.this = CostMatrixEditor.this;
    }
    
    public int getRowCount() {
      return CostMatrixEditor.this.m_matrix.size();
    }
    
    public int getColumnCount() {
      return CostMatrixEditor.this.m_matrix.size();
    }
    
    public Object getValueAt(int param1Int1, int param1Int2) {
      return new Double(CostMatrixEditor.this.m_matrix.getElement(param1Int1, param1Int2));
    }
    
    public void setValueAt(Object param1Object, int param1Int1, int param1Int2) {
      double d = ((Double)param1Object).doubleValue();
      CostMatrixEditor.this.m_matrix.setElement(param1Int1, param1Int2, d);
      fireTableCellUpdated(param1Int1, param1Int2);
    }
    
    public boolean isCellEditable(int param1Int1, int param1Int2) {
      return true;
    }
    
    public Class getColumnClass(int param1Int) {
      return (CostMatrixEditor.class$java$lang$Double == null) ? (CostMatrixEditor.class$java$lang$Double = CostMatrixEditor.class$("java.lang.Double")) : CostMatrixEditor.class$java$lang$Double;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\CostMatrixEditor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */