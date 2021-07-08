package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import weka.classifiers.Classifier;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.filters.Filter;

public class GenericArrayEditor extends JPanel implements PropertyEditor {
  private PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
  
  private JLabel m_Label = new JLabel("Can't edit", 0);
  
  private JList m_ElementList = new JList();
  
  private Class m_ElementClass = String.class;
  
  private DefaultListModel m_ListModel;
  
  private PropertyEditor m_ElementEditor;
  
  private JButton m_DeleteBut = new JButton("Delete");
  
  private JButton m_AddBut = new JButton("Add");
  
  private ActionListener m_InnerActionListener = new ActionListener(this) {
      private final GenericArrayEditor this$0;
      
      public void actionPerformed(ActionEvent param1ActionEvent) {
        if (param1ActionEvent.getSource() == this.this$0.m_DeleteBut) {
          int[] arrayOfInt = this.this$0.m_ElementList.getSelectedIndices();
          if (arrayOfInt != null) {
            for (byte b = 0; b < arrayOfInt.length; b++) {
              int i = arrayOfInt[b];
              this.this$0.m_ListModel.removeElementAt(i);
              if (this.this$0.m_ListModel.size() > i)
                this.this$0.m_ElementList.setSelectedIndex(i); 
            } 
            this.this$0.m_Support.firePropertyChange("", (Object)null, (Object)null);
          } 
          if (this.this$0.m_ElementList.getSelectedIndex() == -1)
            this.this$0.m_DeleteBut.setEnabled(false); 
        } else if (param1ActionEvent.getSource() == this.this$0.m_AddBut) {
          int i = this.this$0.m_ElementList.getSelectedIndex();
          Object object = this.this$0.m_ElementEditor.getValue();
          try {
            SerializedObject serializedObject = new SerializedObject(object);
            object = serializedObject.getObject();
            if (i != -1) {
              this.this$0.m_ListModel.insertElementAt(object, i);
            } else {
              this.this$0.m_ListModel.addElement(object);
            } 
            this.this$0.m_Support.firePropertyChange("", (Object)null, (Object)null);
          } catch (Exception exception) {
            JOptionPane.showMessageDialog(this.this$0, "Could not create an object copy", null, 0);
          } 
        } 
      }
    };
  
  private ListSelectionListener m_InnerSelectionListener = new ListSelectionListener(this) {
      private final GenericArrayEditor this$0;
      
      public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
        if (param1ListSelectionEvent.getSource() == this.this$0.m_ElementList && this.this$0.m_ElementList.getSelectedIndex() != -1)
          this.this$0.m_DeleteBut.setEnabled(true); 
      }
    };
  
  static Class array$Ljava$lang$String;
  
  public GenericArrayEditor() {
    setLayout(new BorderLayout());
    add(this.m_Label, "Center");
    this.m_DeleteBut.addActionListener(this.m_InnerActionListener);
    this.m_AddBut.addActionListener(this.m_InnerActionListener);
    this.m_ElementList.addListSelectionListener(this.m_InnerSelectionListener);
    this.m_AddBut.setToolTipText("Add the current item to the list");
    this.m_DeleteBut.setToolTipText("Delete the selected list item");
  }
  
  private void updateEditorType(Object paramObject) {
    this.m_ElementEditor = null;
    this.m_ListModel = null;
    removeAll();
    if (paramObject != null && paramObject.getClass().isArray()) {
      PropertyText propertyText;
      EditorListCellRenderer editorListCellRenderer;
      Class clazz = paramObject.getClass().getComponentType();
      PropertyEditor propertyEditor = PropertyEditorManager.findEditor(clazz);
      PropertyPanel propertyPanel = null;
      DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
      if (propertyEditor != null) {
        if (propertyEditor instanceof GenericObjectEditor)
          ((GenericObjectEditor)propertyEditor).setClassType(clazz); 
        if (Array.getLength(paramObject) > 0) {
          propertyEditor.setValue(Array.get(paramObject, 0));
        } else if (propertyEditor instanceof GenericObjectEditor) {
          ((GenericObjectEditor)propertyEditor).setDefaultValue();
        } else {
          try {
            propertyEditor.setValue(clazz.newInstance());
          } catch (Exception exception) {
            this.m_ElementEditor = null;
            System.err.println(exception.getMessage());
            add(this.m_Label, "Center");
            this.m_Support.firePropertyChange("", (Object)null, (Object)null);
            validate();
            return;
          } 
        } 
        if (propertyEditor.isPaintable() && propertyEditor.supportsCustomEditor()) {
          propertyPanel = new PropertyPanel(propertyEditor);
          editorListCellRenderer = new EditorListCellRenderer(this, propertyEditor.getClass(), clazz);
        } else if (propertyEditor.getTags() != null) {
          PropertyValueSelector propertyValueSelector = new PropertyValueSelector(propertyEditor);
        } else if (propertyEditor.getAsText() != null) {
          propertyText = new PropertyText(propertyEditor);
        } 
      } 
      if (propertyText == null) {
        System.err.println("No property editor for class: " + clazz.getName());
      } else {
        this.m_ElementEditor = propertyEditor;
        this.m_ListModel = new DefaultListModel();
        this.m_ElementClass = clazz;
        for (byte b = 0; b < Array.getLength(paramObject); b++)
          this.m_ListModel.addElement(Array.get(paramObject, b)); 
        this.m_ElementList.setCellRenderer(editorListCellRenderer);
        this.m_ElementList.setModel(this.m_ListModel);
        if (this.m_ListModel.getSize() > 0) {
          this.m_ElementList.setSelectedIndex(0);
          this.m_DeleteBut.setEnabled(true);
        } else {
          this.m_DeleteBut.setEnabled(false);
        } 
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(propertyText, "Center");
        jPanel.add(this.m_AddBut, "East");
        add(jPanel, "North");
        add(new JScrollPane(this.m_ElementList), "Center");
        add(this.m_DeleteBut, "South");
        this.m_ElementEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
              private final GenericArrayEditor this$0;
              
              public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
                this.this$0.repaint();
              }
            });
      } 
    } 
    if (this.m_ElementEditor == null)
      add(this.m_Label, "Center"); 
    this.m_Support.firePropertyChange("", (Object)null, (Object)null);
    validate();
  }
  
  public void setValue(Object paramObject) {
    updateEditorType(paramObject);
  }
  
  public Object getValue() {
    if (this.m_ListModel == null)
      return null; 
    int i = this.m_ListModel.getSize();
    Object object = Array.newInstance(this.m_ElementClass, i);
    for (byte b = 0; b < i; b++)
      Array.set(object, b, this.m_ListModel.elementAt(b)); 
    return object;
  }
  
  public String getJavaInitializationString() {
    return "null";
  }
  
  public boolean isPaintable() {
    return true;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {
    FontMetrics fontMetrics = paramGraphics.getFontMetrics();
    int i = (paramRectangle.height - fontMetrics.getAscent()) / 2;
    String str = this.m_ListModel.getSize() + " " + this.m_ElementClass.getName();
    paramGraphics.drawString(str, 2, fontMetrics.getHeight() + i);
  }
  
  public String getAsText() {
    return null;
  }
  
  public void setAsText(String paramString) {
    throw new IllegalArgumentException(paramString);
  }
  
  public String[] getTags() {
    return null;
  }
  
  public boolean supportsCustomEditor() {
    return true;
  }
  
  public Component getCustomEditor() {
    return this;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.err.println("---Registering Weka Editors---");
      PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
      PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor((array$Ljava$lang$String == null) ? (array$Ljava$lang$String = class$("[Ljava.lang.String;")) : array$Ljava$lang$String, GenericArrayEditor.class);
      GenericArrayEditor genericArrayEditor = new GenericArrayEditor();
      Filter[] arrayOfFilter = new Filter[0];
      PropertyDialog propertyDialog = new PropertyDialog(genericArrayEditor, 100, 100);
      propertyDialog.setSize(200, 200);
      propertyDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent param1WindowEvent) {
              System.exit(0);
            }
          });
      genericArrayEditor.setValue(arrayOfFilter);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  private class EditorListCellRenderer implements ListCellRenderer {
    private Class m_EditorClass;
    
    private Class m_ValueClass;
    
    private final GenericArrayEditor this$0;
    
    public EditorListCellRenderer(GenericArrayEditor this$0, Class param1Class1, Class param1Class2) {
      this.this$0 = this$0;
      this.m_EditorClass = param1Class1;
      this.m_ValueClass = param1Class2;
    }
    
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      try {
        PropertyEditor propertyEditor = this.m_EditorClass.newInstance();
        if (propertyEditor instanceof GenericObjectEditor)
          ((GenericObjectEditor)propertyEditor).setClassType(this.m_ValueClass); 
        propertyEditor.setValue(param1Object);
        return (Component)new Object(this, param1Boolean1, param1JList, propertyEditor);
      } catch (Exception exception) {
        return null;
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\GenericArrayEditor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */