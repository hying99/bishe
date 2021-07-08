package weka.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import weka.classifiers.Classifier;
import weka.core.SelectedTag;
import weka.experiment.AveragingResultProducer;
import weka.experiment.PropertyNode;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;

public class PropertySelectorDialog extends JDialog {
  protected JButton m_SelectBut = new JButton("Select");
  
  protected JButton m_CancelBut = new JButton("Cancel");
  
  protected DefaultMutableTreeNode m_Root;
  
  protected Object m_RootObject;
  
  protected int m_Result;
  
  protected Object[] m_ResultPath;
  
  protected JTree m_Tree;
  
  public static final int APPROVE_OPTION = 0;
  
  public static final int CANCEL_OPTION = 1;
  
  public PropertySelectorDialog(Frame paramFrame, Object paramObject) {
    super(paramFrame, "Select a property", true);
    this.m_CancelBut.addActionListener(new ActionListener(this) {
          private final PropertySelectorDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Result = 1;
            this.this$0.setVisible(false);
          }
        });
    this.m_SelectBut.addActionListener(new ActionListener(this) {
          private final PropertySelectorDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            TreePath treePath = this.this$0.m_Tree.getSelectionPath();
            if (treePath == null) {
              this.this$0.m_Result = 1;
            } else {
              this.this$0.m_ResultPath = treePath.getPath();
              if (this.this$0.m_ResultPath == null || this.this$0.m_ResultPath.length < 2) {
                this.this$0.m_Result = 1;
              } else {
                this.this$0.m_Result = 0;
              } 
            } 
            this.this$0.setVisible(false);
          }
        });
    this.m_RootObject = paramObject;
    this.m_Root = new DefaultMutableTreeNode(new PropertyNode(this.m_RootObject));
    createNodes(this.m_Root);
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    Box box = new Box(0);
    box.add(this.m_SelectBut);
    box.add(Box.createHorizontalStrut(10));
    box.add(this.m_CancelBut);
    container.add(box, "South");
    this.m_Tree = new JTree(this.m_Root);
    this.m_Tree.getSelectionModel().setSelectionMode(1);
    container.add(new JScrollPane(this.m_Tree), "Center");
    pack();
  }
  
  public int showDialog() {
    this.m_Result = 1;
    setVisible(true);
    return this.m_Result;
  }
  
  public PropertyNode[] getPath() {
    PropertyNode[] arrayOfPropertyNode = new PropertyNode[this.m_ResultPath.length - 1];
    for (byte b = 0; b < arrayOfPropertyNode.length; b++)
      arrayOfPropertyNode[b] = (PropertyNode)((DefaultMutableTreeNode)this.m_ResultPath[b + 1]).getUserObject(); 
    return arrayOfPropertyNode;
  }
  
  protected void createNodes(DefaultMutableTreeNode paramDefaultMutableTreeNode) {
    PropertyDescriptor[] arrayOfPropertyDescriptor;
    PropertyNode propertyNode = (PropertyNode)paramDefaultMutableTreeNode.getUserObject();
    Object object = propertyNode.value;
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
      arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
    } catch (IntrospectionException introspectionException) {
      System.err.println("PropertySelectorDialog: Couldn't introspect");
      return;
    } 
    for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
      if (!arrayOfPropertyDescriptor[b].isHidden() && !arrayOfPropertyDescriptor[b].isExpert()) {
        String str = arrayOfPropertyDescriptor[b].getDisplayName();
        Class clazz = arrayOfPropertyDescriptor[b].getPropertyType();
        Method method1 = arrayOfPropertyDescriptor[b].getReadMethod();
        Method method2 = arrayOfPropertyDescriptor[b].getWriteMethod();
        Object object1 = null;
        if (method1 != null && method2 != null)
          try {
            Object[] arrayOfObject = new Object[0];
            object1 = method1.invoke(object, arrayOfObject);
            PropertyEditor propertyEditor = null;
            Class clazz1 = arrayOfPropertyDescriptor[b].getPropertyEditorClass();
            if (clazz1 != null)
              try {
                propertyEditor = (PropertyEditor)clazz1.newInstance();
              } catch (Exception exception) {} 
            if (propertyEditor == null)
              propertyEditor = PropertyEditorManager.findEditor(clazz); 
            if (propertyEditor != null && object1 != null) {
              DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(new PropertyNode(object1, arrayOfPropertyDescriptor[b], object.getClass()));
              paramDefaultMutableTreeNode.add(defaultMutableTreeNode);
              createNodes(defaultMutableTreeNode);
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
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.err.println("---Registering Weka Editors---");
      PropertyEditorManager.registerEditor(ResultProducer.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(SplitEvaluator.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
      AveragingResultProducer averagingResultProducer = new AveragingResultProducer();
      PropertySelectorDialog propertySelectorDialog = new PropertySelectorDialog(null, averagingResultProducer);
      int i = propertySelectorDialog.showDialog();
      if (i == 0) {
        System.err.println("Property Selected");
        PropertyNode[] arrayOfPropertyNode = propertySelectorDialog.getPath();
        for (byte b = 0; b < arrayOfPropertyNode.length; b++) {
          PropertyNode propertyNode = arrayOfPropertyNode[b];
          System.err.println("" + (b + 1) + "  " + propertyNode.toString() + " " + propertyNode.value.toString());
        } 
      } else {
        System.err.println("Cancelled");
      } 
      System.exit(0);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\PropertySelectorDialog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */