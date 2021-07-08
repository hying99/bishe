package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PropertySheetPanel extends JPanel implements PropertyChangeListener {
  private Object m_Target;
  
  private PropertyDescriptor[] m_Properties;
  
  private MethodDescriptor[] m_Methods;
  
  private PropertyEditor[] m_Editors;
  
  private Object[] m_Values;
  
  private JComponent[] m_Views;
  
  private JLabel[] m_Labels;
  
  private String[] m_TipTexts;
  
  private StringBuffer m_HelpText;
  
  private JFrame m_HelpFrame;
  
  private JButton m_HelpBut;
  
  private int m_NumEditable = 0;
  
  private JPanel m_aboutPanel;
  
  private PropertyChangeSupport support = new PropertyChangeSupport(this);
  
  public PropertySheetPanel() {
    setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
  }
  
  public JPanel getAboutPanel() {
    return this.m_aboutPanel;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    wasModified(paramPropertyChangeEvent);
    this.support.firePropertyChange("", (Object)null, (Object)null);
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.support.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public synchronized void setTarget(Object paramObject) {
    byte b1 = 0;
    removeAll();
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    setVisible(false);
    this.m_NumEditable = 0;
    this.m_Target = paramObject;
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(this.m_Target.getClass());
      this.m_Properties = beanInfo.getPropertyDescriptors();
      this.m_Methods = beanInfo.getMethodDescriptors();
    } catch (IntrospectionException introspectionException) {
      System.err.println("PropertySheet: Couldn't introspect");
      return;
    } 
    byte b2 = 12;
    JTextArea jTextArea = new JTextArea();
    this.m_HelpText = null;
    Object object = null;
    byte b3;
    for (b3 = 0; b3 < this.m_Methods.length; b3++) {
      String str = this.m_Methods[b3].getDisplayName();
      Method method = this.m_Methods[b3].getMethod();
      if (str.equals("globalInfo") && method.getReturnType().equals(String.class))
        try {
          Object[] arrayOfObject = new Object[0];
          String str1 = (String)method.invoke(this.m_Target, arrayOfObject);
          String str2 = str1;
          int i = str1.indexOf('.');
          if (i != -1)
            str2 = str1.substring(0, i + 1); 
          String str3 = paramObject.getClass().getName();
          this.m_HelpText = new StringBuffer("NAME\n");
          this.m_HelpText.append(str3).append("\n\n");
          this.m_HelpText.append("SYNOPSIS\n").append(str1).append("\n\n");
          this.m_HelpBut = new JButton("More");
          this.m_HelpBut.setToolTipText("More information about " + str3);
          this.m_HelpBut.addActionListener(new ActionListener(this) {
                private final PropertySheetPanel this$0;
                
                public void actionPerformed(ActionEvent param1ActionEvent) {
                  this.this$0.openHelpFrame();
                  this.this$0.m_HelpBut.setEnabled(false);
                }
              });
          jTextArea.setColumns(30);
          jTextArea.setFont(new Font("SansSerif", 0, 12));
          jTextArea.setEditable(false);
          jTextArea.setLineWrap(true);
          jTextArea.setWrapStyleWord(true);
          jTextArea.setText(str2);
          jTextArea.setBackground(getBackground());
          b2 = 12;
          JPanel jPanel1 = new JPanel();
          jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
          jPanel1.setLayout(new BorderLayout());
          jPanel1.add(jTextArea, "Center");
          JPanel jPanel2 = new JPanel();
          jPanel2.setLayout(new BorderLayout());
          jPanel2.add(this.m_HelpBut, "North");
          jPanel1.add(jPanel2, "East");
          GridBagConstraints gridBagConstraints = new GridBagConstraints();
          gridBagConstraints.fill = 1;
          gridBagConstraints.gridwidth = 2;
          gridBagConstraints.insets = new Insets(0, 5, 0, 5);
          gridBagLayout.setConstraints(jPanel1, gridBagConstraints);
          this.m_aboutPanel = jPanel1;
          add(this.m_aboutPanel);
          b1 = 1;
          break;
        } catch (Exception exception) {} 
    } 
    this.m_Editors = new PropertyEditor[this.m_Properties.length];
    this.m_Values = new Object[this.m_Properties.length];
    this.m_Views = new JComponent[this.m_Properties.length];
    this.m_Labels = new JLabel[this.m_Properties.length];
    this.m_TipTexts = new String[this.m_Properties.length];
    b3 = 1;
    for (byte b4 = 0;; b4++) {
      if (b4 < this.m_Properties.length) {
        if (!this.m_Properties[b4].isHidden() && !this.m_Properties[b4].isExpert()) {
          String str = this.m_Properties[b4].getDisplayName();
          Class clazz = this.m_Properties[b4].getPropertyType();
          Method method1 = this.m_Properties[b4].getReadMethod();
          Method method2 = this.m_Properties[b4].getWriteMethod();
          if (method1 != null && method2 != null) {
            PropertyPanel propertyPanel = null;
            try {
              Object[] arrayOfObject = new Object[0];
              Object object1 = method1.invoke(this.m_Target, arrayOfObject);
              this.m_Values[b4] = object1;
              PropertyEditor propertyEditor = null;
              Class clazz1 = this.m_Properties[b4].getPropertyEditorClass();
              if (clazz1 != null)
                try {
                  propertyEditor = (PropertyEditor)clazz1.newInstance();
                } catch (Exception exception) {} 
              if (propertyEditor == null)
                propertyEditor = PropertyEditorManager.findEditor(clazz); 
              this.m_Editors[b4] = propertyEditor;
              if (propertyEditor == null) {
                String str1 = this.m_Properties[b4].getReadMethod().getDeclaringClass().getName();
              } else {
                if (propertyEditor instanceof GenericObjectEditor)
                  ((GenericObjectEditor)propertyEditor).setClassType(clazz); 
                if (object1 == null) {
                  String str1 = this.m_Properties[b4].getReadMethod().getDeclaringClass().getName();
                } else {
                  PropertyText propertyText;
                  propertyEditor.setValue(object1);
                  String str1 = str + "TipText";
                  for (byte b = 0; b < this.m_Methods.length; b++) {
                    String str2 = this.m_Methods[b].getDisplayName();
                    Method method = this.m_Methods[b].getMethod();
                    if (str2.equals(str1) && method.getReturnType().equals(String.class)) {
                      try {
                        String str3 = (String)method.invoke(this.m_Target, arrayOfObject);
                        int i = str3.indexOf('.');
                        if (i < 0) {
                          this.m_TipTexts[b4] = str3;
                        } else {
                          this.m_TipTexts[b4] = str3.substring(0, i);
                        } 
                        if (this.m_HelpText != null) {
                          if (b3 != 0) {
                            this.m_HelpText.append("OPTIONS\n");
                            b3 = 0;
                          } 
                          this.m_HelpText.append(str).append(" -- ");
                          this.m_HelpText.append(str3).append("\n\n");
                        } 
                      } catch (Exception exception) {}
                      break;
                    } 
                  } 
                  if (propertyEditor.isPaintable() && propertyEditor.supportsCustomEditor()) {
                    propertyPanel = new PropertyPanel(propertyEditor);
                  } else if (propertyEditor.getTags() != null) {
                    PropertyValueSelector propertyValueSelector = new PropertyValueSelector(propertyEditor);
                  } else if (propertyEditor.getAsText() != null) {
                    propertyText = new PropertyText(propertyEditor);
                  } else {
                    System.err.println("Warning: Property \"" + str + "\" has non-displayabale editor.  Skipping.");
                    b4++;
                  } 
                  propertyEditor.addPropertyChangeListener(this);
                  this.m_Labels[b4] = new JLabel(str, 4);
                  this.m_Labels[b4].setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
                  this.m_Views[b4] = propertyText;
                  GridBagConstraints gridBagConstraints = new GridBagConstraints();
                  gridBagConstraints.anchor = 13;
                  gridBagConstraints.fill = 2;
                  gridBagConstraints.gridy = b4 + b1;
                  gridBagConstraints.gridx = 0;
                  gridBagLayout.setConstraints(this.m_Labels[b4], gridBagConstraints);
                  add(this.m_Labels[b4]);
                  object1 = new JPanel();
                  if (this.m_TipTexts[b4] != null)
                    this.m_Views[b4].setToolTipText(this.m_TipTexts[b4]); 
                  object1.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
                  object1.setLayout(new BorderLayout());
                  object1.add(this.m_Views[b4], "Center");
                  gridBagConstraints = new GridBagConstraints();
                  gridBagConstraints.anchor = 17;
                  gridBagConstraints.fill = 1;
                  gridBagConstraints.gridy = b4 + b1;
                  gridBagConstraints.gridx = 1;
                  gridBagConstraints.weightx = 100.0D;
                  gridBagLayout.setConstraints((Component)object1, gridBagConstraints);
                  add((Component)object1);
                  this.m_NumEditable++;
                } 
              } 
            } catch (InvocationTargetException invocationTargetException) {
              System.err.println("Skipping property " + str + " ; exception on target: " + invocationTargetException.getTargetException());
              invocationTargetException.getTargetException().printStackTrace();
            } catch (Exception exception) {
              System.err.println("Skipping property " + str + " ; exception: " + exception);
              exception.printStackTrace();
            } 
          } 
        } 
      } else {
        break;
      } 
    } 
    if (this.m_NumEditable == 0) {
      JLabel jLabel = new JLabel("No editable properties", 0);
      Dimension dimension = jLabel.getPreferredSize();
      jLabel.setPreferredSize(new Dimension(dimension.width * 2, dimension.height * 2));
      jLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.anchor = 10;
      gridBagConstraints.fill = 2;
      gridBagConstraints.gridy = b1;
      gridBagConstraints.gridx = 0;
      gridBagLayout.setConstraints(jLabel, gridBagConstraints);
      add(jLabel);
    } 
    validate();
    setVisible(true);
  }
  
  protected void openHelpFrame() {
    JTextArea jTextArea = new JTextArea();
    jTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jTextArea.setLineWrap(true);
    jTextArea.setWrapStyleWord(true);
    jTextArea.setEditable(false);
    jTextArea.setText(this.m_HelpText.toString());
    jTextArea.setCaretPosition(0);
    JFrame jFrame = new JFrame("Information");
    jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
          private final JFrame val$jf;
          
          private final PropertySheetPanel this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            this.val$jf.dispose();
            if (this.this$0.m_HelpFrame == this.val$jf)
              this.this$0.m_HelpBut.setEnabled(true); 
          }
        });
    jFrame.getContentPane().setLayout(new BorderLayout());
    jFrame.getContentPane().add(new JScrollPane(jTextArea), "Center");
    jFrame.pack();
    jFrame.setSize(400, 350);
    jFrame.setLocation((this.m_aboutPanel.getTopLevelAncestor().getLocationOnScreen()).x + (this.m_aboutPanel.getTopLevelAncestor().getSize()).width, (this.m_aboutPanel.getTopLevelAncestor().getLocationOnScreen()).y);
    jFrame.setVisible(true);
    this.m_HelpFrame = jFrame;
  }
  
  public int editableProperties() {
    return this.m_NumEditable;
  }
  
  synchronized void wasModified(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getSource() instanceof PropertyEditor) {
      PropertyEditor propertyEditor = (PropertyEditor)paramPropertyChangeEvent.getSource();
      for (byte b1 = 0; b1 < this.m_Editors.length; b1++) {
        if (this.m_Editors[b1] == propertyEditor) {
          PropertyDescriptor propertyDescriptor = this.m_Properties[b1];
          Object object = propertyEditor.getValue();
          this.m_Values[b1] = object;
          Method method = propertyDescriptor.getWriteMethod();
          try {
            Object[] arrayOfObject = { object };
            arrayOfObject[0] = object;
            method.invoke(this.m_Target, arrayOfObject);
          } catch (InvocationTargetException invocationTargetException) {
            if (invocationTargetException.getTargetException() instanceof java.beans.PropertyVetoException) {
              Container container;
              String str = "WARNING: Vetoed; reason is: " + invocationTargetException.getTargetException().getMessage();
              System.err.println(str);
              if (paramPropertyChangeEvent.getSource() instanceof JPanel) {
                container = ((JPanel)paramPropertyChangeEvent.getSource()).getParent();
              } else {
                container = new JFrame();
              } 
              JOptionPane.showMessageDialog(container, str, "error", 2);
              if (container instanceof JFrame)
                ((JFrame)container).dispose(); 
            } else {
              Container container;
              System.err.println(invocationTargetException.getTargetException().getClass().getName() + " while updating " + propertyDescriptor.getName() + ": " + invocationTargetException.getTargetException().getMessage());
              if (paramPropertyChangeEvent.getSource() instanceof JPanel) {
                container = ((JPanel)paramPropertyChangeEvent.getSource()).getParent();
              } else {
                container = new JFrame();
              } 
              JOptionPane.showMessageDialog(container, invocationTargetException.getTargetException().getClass().getName() + " while updating " + propertyDescriptor.getName() + ":\n" + invocationTargetException.getTargetException().getMessage(), "error", 2);
              if (container instanceof JFrame)
                ((JFrame)container).dispose(); 
            } 
          } catch (Exception exception) {
            System.err.println("Unexpected exception while updating " + propertyDescriptor.getName());
          } 
          if (this.m_Views[b1] != null && this.m_Views[b1] instanceof PropertyPanel) {
            this.m_Views[b1].repaint();
            revalidate();
          } 
          break;
        } 
      } 
    } 
    for (byte b = 0; b < this.m_Properties.length; b++) {
      Object object;
      try {
        Method method1 = this.m_Properties[b].getReadMethod();
        Method method2 = this.m_Properties[b].getWriteMethod();
        if (method1 == null || method2 == null)
          continue; 
        Object[] arrayOfObject = new Object[0];
        object = method1.invoke(this.m_Target, arrayOfObject);
      } catch (Exception exception) {
        object = null;
      } 
      if (object != this.m_Values[b] && (object == null || !object.equals(this.m_Values[b]))) {
        this.m_Values[b] = object;
        if (this.m_Editors[b] != null) {
          this.m_Editors[b].removePropertyChangeListener(this);
          this.m_Editors[b].setValue(object);
          this.m_Editors[b].addPropertyChangeListener(this);
          if (this.m_Views[b] != null)
            this.m_Views[b].repaint(); 
        } 
      } 
      continue;
    } 
    if (Beans.isInstanceOf(this.m_Target, Component.class))
      ((Component)Beans.getInstanceOf(this.m_Target, Component.class)).repaint(); 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\PropertySheetPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */