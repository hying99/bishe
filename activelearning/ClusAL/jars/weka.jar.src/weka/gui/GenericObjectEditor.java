package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.Classifier;
import weka.classifiers.rules.ZeroR;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;

public class GenericObjectEditor implements PropertyEditor, CustomPanelSupplier {
  protected Object m_Object;
  
  protected Object m_Backup;
  
  protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
  
  protected Class m_ClassType;
  
  protected Hashtable m_ObjectNames;
  
  protected GOEPanel m_EditorComponent;
  
  protected boolean m_Enabled = true;
  
  protected static String PROPERTY_FILE = "weka/gui/GenericObjectEditor.props";
  
  protected static Properties EDITOR_PROPERTIES;
  
  protected DefaultMutableTreeNode m_treeNodeOfCurrentObject;
  
  protected PropertyPanel m_ObjectPropertyPanel;
  
  protected boolean m_canChangeClassInDialog;
  
  protected static final boolean USE_DYNAMIC = true;
  
  public GenericObjectEditor() {
    this(false);
  }
  
  public GenericObjectEditor(boolean paramBoolean) {
    this.m_canChangeClassInDialog = paramBoolean;
  }
  
  protected String getRootFromClass(String paramString1, String paramString2) {
    return (paramString1.indexOf(paramString2) > -1) ? paramString1.substring(0, paramString1.indexOf(paramString2)) : null;
  }
  
  protected Hashtable sortClassesByRoot(String paramString) {
    if (paramString == null)
      return null; 
    Hashtable hashtable1 = new Hashtable();
    HierarchyPropertyParser hierarchyPropertyParser = new HierarchyPropertyParser();
    String str = hierarchyPropertyParser.getSeperator();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ", ");
    while (stringTokenizer.hasMoreElements()) {
      Vector vector;
      String str1 = stringTokenizer.nextToken();
      String str2 = getRootFromClass(str1, str);
      if (str2 == null)
        continue; 
      if (!hashtable1.containsKey(str2)) {
        vector = new Vector();
        hashtable1.put(str2, vector);
      } else {
        vector = (Vector)hashtable1.get(str2);
      } 
      vector.add(str1);
    } 
    Hashtable hashtable2 = new Hashtable();
    Enumeration enumeration = hashtable1.keys();
    while (enumeration.hasMoreElements()) {
      String str1 = enumeration.nextElement();
      Vector vector = (Vector)hashtable1.get(str1);
      String str2 = "";
      for (byte b = 0; b < vector.size(); b++) {
        if (b > 0)
          str2 = str2 + ","; 
        str2 = str2 + (String)vector.get(b);
      } 
      hashtable2.put(str1, str2);
    } 
    return hashtable2;
  }
  
  protected Hashtable getClassesFromProperties() {
    Hashtable hashtable = new Hashtable();
    String str = this.m_ClassType.getName();
    Hashtable hashtable1 = sortClassesByRoot(EDITOR_PROPERTIES.getProperty(str));
    if (hashtable1 != null)
      try {
        Enumeration enumeration = hashtable1.keys();
        while (enumeration.hasMoreElements()) {
          String str1 = enumeration.nextElement();
          String str2 = (String)hashtable1.get(str1);
          HierarchyPropertyParser hierarchyPropertyParser = new HierarchyPropertyParser();
          hierarchyPropertyParser.build(str2, ", ");
          hashtable.put(str1, hierarchyPropertyParser);
        } 
      } catch (Exception exception) {
        System.err.println("Invalid property: " + hashtable1);
      }  
    return hashtable;
  }
  
  protected void updateObjectNames() {
    if (this.m_ObjectNames == null)
      this.m_ObjectNames = getClassesFromProperties(); 
    if (this.m_Object != null) {
      String str1 = this.m_Object.getClass().getName();
      String str2 = getRootFromClass(str1, (new HierarchyPropertyParser()).getSeperator());
      HierarchyPropertyParser hierarchyPropertyParser = (HierarchyPropertyParser)this.m_ObjectNames.get(str2);
      if (hierarchyPropertyParser != null && !hierarchyPropertyParser.contains(str1))
        hierarchyPropertyParser.add(str1); 
    } 
  }
  
  public void setEnabled(boolean paramBoolean) {
    if (paramBoolean != this.m_Enabled)
      this.m_Enabled = paramBoolean; 
  }
  
  public void setClassType(Class paramClass) {
    this.m_ClassType = paramClass;
    this.m_ObjectNames = getClassesFromProperties();
  }
  
  public void setDefaultValue() {
    if (this.m_ClassType == null) {
      System.err.println("No ClassType set up for GenericObjectEditor!!");
      return;
    } 
    Hashtable hashtable = getClassesFromProperties();
    HierarchyPropertyParser hierarchyPropertyParser = null;
    Enumeration enumeration = hashtable.elements();
    try {
      while (enumeration.hasMoreElements()) {
        hierarchyPropertyParser = enumeration.nextElement();
        if (hierarchyPropertyParser.depth() > 0) {
          hierarchyPropertyParser.goToRoot();
          while (!hierarchyPropertyParser.isLeafReached())
            hierarchyPropertyParser.goToChild(0); 
          String str = hierarchyPropertyParser.fullValue();
          setValue(Class.forName(str).newInstance());
        } 
      } 
    } catch (Exception exception) {
      System.err.println("Problem loading the first class: " + hierarchyPropertyParser.fullValue());
      exception.printStackTrace();
    } 
  }
  
  public void setValue(Object paramObject) {
    if (this.m_ClassType == null) {
      System.err.println("No ClassType set up for GenericObjectEditor!!");
      return;
    } 
    if (!this.m_ClassType.isAssignableFrom(paramObject.getClass())) {
      System.err.println("setValue object not of correct type!");
      return;
    } 
    setObject(paramObject);
    if (this.m_EditorComponent != null)
      this.m_EditorComponent.repaint(); 
    updateObjectNames();
  }
  
  protected void setObject(Object paramObject) {
    boolean bool;
    if (getValue() != null) {
      bool = !paramObject.equals(getValue()) ? true : false;
    } else {
      bool = true;
    } 
    this.m_Backup = this.m_Object;
    this.m_Object = paramObject;
    if (this.m_EditorComponent != null)
      this.m_EditorComponent.updateChildPropertySheet(); 
    if (bool)
      this.m_Support.firePropertyChange("", (Object)null, (Object)null); 
  }
  
  public Object getValue() {
    return this.m_Object;
  }
  
  public String getJavaInitializationString() {
    return "new " + this.m_Object.getClass().getName() + "()";
  }
  
  public boolean isPaintable() {
    return true;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {
    if (this.m_Enabled) {
      String str;
      if (this.m_Object != null) {
        str = this.m_Object.getClass().getName();
      } else {
        str = "None";
      } 
      int i = str.lastIndexOf('.');
      if (i != -1)
        str = str.substring(i + 1); 
      Font font = paramGraphics.getFont();
      paramGraphics.setFont(font.deriveFont(1));
      FontMetrics fontMetrics = paramGraphics.getFontMetrics();
      int j = (paramRectangle.height - fontMetrics.getHeight()) / 2;
      paramGraphics.drawString(str, 2, fontMetrics.getHeight() + j);
      int k = fontMetrics.stringWidth(str);
      paramGraphics.setFont(font);
      if (this.m_Object instanceof OptionHandler)
        paramGraphics.drawString(" " + Utils.joinOptions(((OptionHandler)this.m_Object).getOptions()), k + 2, fontMetrics.getHeight() + j); 
    } 
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
    if (this.m_EditorComponent == null)
      this.m_EditorComponent = new GOEPanel(this); 
    return this.m_EditorComponent;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_Support.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public JPanel getCustomPanel() {
    JButton jButton = createChooseClassButton();
    this.m_ObjectPropertyPanel = new PropertyPanel(this, true);
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BorderLayout());
    jPanel.add(jButton, "West");
    jPanel.add(this.m_ObjectPropertyPanel, "Center");
    return jPanel;
  }
  
  protected JButton createChooseClassButton() {
    JButton jButton = new JButton("Choose");
    jButton.addActionListener(new ActionListener(this) {
          private final GenericObjectEditor this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JPopupMenu jPopupMenu = this.this$0.getChooseClassPopupMenu();
            if (param1ActionEvent.getSource() instanceof Component) {
              Component component = (Component)param1ActionEvent.getSource();
              jPopupMenu.show(component, component.getX(), component.getY());
              jPopupMenu.pack();
            } 
          }
        });
    return jButton;
  }
  
  public JPopupMenu getChooseClassPopupMenu() {
    updateObjectNames();
    this.m_treeNodeOfCurrentObject = null;
    JTree jTree = createTree(this.m_ObjectNames);
    if (this.m_treeNodeOfCurrentObject != null)
      jTree.setSelectionPath(new TreePath((Object[])this.m_treeNodeOfCurrentObject.getPath())); 
    jTree.getSelectionModel().setSelectionMode(1);
    JTreePopupMenu jTreePopupMenu = new JTreePopupMenu(this, jTree);
    jTree.addTreeSelectionListener(new TreeSelectionListener(this, jTree, jTreePopupMenu) {
          private final JTree val$tree;
          
          private final JPopupMenu val$popup;
          
          private final GenericObjectEditor this$0;
          
          public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.val$tree.getLastSelectedPathComponent();
            if (defaultMutableTreeNode == null)
              return; 
            Object object = defaultMutableTreeNode.getUserObject();
            if (defaultMutableTreeNode.isLeaf()) {
              TreePath treePath = this.val$tree.getSelectionPath();
              StringBuffer stringBuffer = new StringBuffer();
              byte b1 = 0;
              if (this.this$0.m_ObjectNames.size() > 1)
                b1 = 1; 
              for (byte b2 = b1; b2 < treePath.getPathCount(); b2++) {
                if (b2 > b1)
                  stringBuffer.append("."); 
                stringBuffer.append((String)((DefaultMutableTreeNode)treePath.getPathComponent(b2)).getUserObject());
              } 
              this.this$0.classSelected(stringBuffer.toString());
              this.val$popup.setVisible(false);
            } 
          }
        });
    return jTreePopupMenu;
  }
  
  protected JTree createTree(Hashtable paramHashtable) {
    DefaultMutableTreeNode defaultMutableTreeNode;
    if (paramHashtable.size() > 1) {
      defaultMutableTreeNode = new DefaultMutableTreeNode("root");
    } else {
      defaultMutableTreeNode = null;
    } 
    Enumeration enumeration = paramHashtable.elements();
    while (enumeration.hasMoreElements()) {
      HierarchyPropertyParser hierarchyPropertyParser = enumeration.nextElement();
      hierarchyPropertyParser.goToRoot();
      DefaultMutableTreeNode defaultMutableTreeNode1 = new DefaultMutableTreeNode(hierarchyPropertyParser.getValue());
      addChildrenToTree(defaultMutableTreeNode1, hierarchyPropertyParser);
      if (defaultMutableTreeNode == null) {
        defaultMutableTreeNode = defaultMutableTreeNode1;
        continue;
      } 
      defaultMutableTreeNode.add(defaultMutableTreeNode1);
    } 
    return new JTree(defaultMutableTreeNode);
  }
  
  protected void addChildrenToTree(DefaultMutableTreeNode paramDefaultMutableTreeNode, HierarchyPropertyParser paramHierarchyPropertyParser) {
    try {
      for (byte b = 0; b < paramHierarchyPropertyParser.numChildren(); b++) {
        paramHierarchyPropertyParser.goToChild(b);
        DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(paramHierarchyPropertyParser.getValue());
        if (this.m_Object != null && this.m_Object.getClass().getName().equals(paramHierarchyPropertyParser.fullValue()))
          this.m_treeNodeOfCurrentObject = defaultMutableTreeNode; 
        paramDefaultMutableTreeNode.add(defaultMutableTreeNode);
        addChildrenToTree(defaultMutableTreeNode, paramHierarchyPropertyParser);
        paramHierarchyPropertyParser.goToParent();
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  protected void classSelected(String paramString) {
    try {
      if (this.m_Object != null && this.m_Object.getClass().getName().equals(paramString))
        return; 
      setValue(Class.forName(paramString).newInstance());
      if (this.m_EditorComponent != null)
        this.m_EditorComponent.updateChildPropertySheet(); 
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(null, "Could not create an example of\n" + paramString + "\n" + "from the current classpath", "Class load failed", 0);
      exception.printStackTrace();
      try {
        if (this.m_Backup != null) {
          setValue(this.m_Backup);
        } else {
          setDefaultValue();
        } 
      } catch (Exception exception1) {
        System.err.println(exception.getMessage());
        exception.printStackTrace();
      } 
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.err.println("---Registering Weka Editors---");
      PropertyEditorManager.registerEditor(ResultProducer.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(SplitEvaluator.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(ASEvaluation.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(ASSearch.class, GenericObjectEditor.class);
      PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
      PropertyEditorManager.registerEditor(File.class, FileEditor.class);
      GenericObjectEditor genericObjectEditor = new GenericObjectEditor(true);
      genericObjectEditor.setClassType(Classifier.class);
      ZeroR zeroR = new ZeroR();
      if (paramArrayOfString.length > 0) {
        genericObjectEditor.setClassType(Class.forName(paramArrayOfString[0]));
        if (paramArrayOfString.length > 1) {
          zeroR = (ZeroR)Class.forName(paramArrayOfString[1]).newInstance();
          genericObjectEditor.setValue(zeroR);
        } else {
          genericObjectEditor.setDefaultValue();
        } 
      } else {
        genericObjectEditor.setValue(zeroR);
      } 
      PropertyDialog propertyDialog = new PropertyDialog(genericObjectEditor, 100, 100);
      propertyDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent param1WindowEvent) {
              PropertyEditor propertyEditor = ((PropertyDialog)param1WindowEvent.getSource()).getEditor();
              Object object = propertyEditor.getValue();
              String str = "";
              if (object instanceof OptionHandler)
                str = Utils.joinOptions(((OptionHandler)object).getOptions()); 
              System.out.println(object.getClass().getName() + " " + str);
              System.exit(0);
            }
          });
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    try {
      GenericPropertiesCreator genericPropertiesCreator = new GenericPropertiesCreator();
      genericPropertiesCreator.execute(false);
      EDITOR_PROPERTIES = genericPropertiesCreator.getOutputProperties();
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(null, "Could not determine the properties for the generic object\neditor. This exception was produced:\n" + exception.toString(), "GenericObjectEditor", 0);
    } 
  }
  
  public class GOEPanel extends JPanel {
    protected PropertySheetPanel m_ChildPropertySheet;
    
    protected JLabel m_ClassNameLabel;
    
    protected JButton m_OpenBut;
    
    protected JButton m_SaveBut;
    
    protected JButton m_okBut;
    
    protected JButton m_cancelBut;
    
    protected JFileChooser m_FileChooser;
    
    private final GenericObjectEditor this$0;
    
    public GOEPanel(GenericObjectEditor this$0) {
      this.this$0 = this$0;
      this$0.m_Backup = copyObject(this$0.m_Object);
      this.m_ClassNameLabel = new JLabel("None");
      this.m_ClassNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.m_ChildPropertySheet = new PropertySheetPanel();
      this.m_ChildPropertySheet.addPropertyChangeListener((PropertyChangeListener)new Object(this));
      this.m_OpenBut = new JButton("Open...");
      this.m_OpenBut.setToolTipText("Load a configured object");
      this.m_OpenBut.setEnabled(true);
      this.m_OpenBut.addActionListener((ActionListener)new Object(this));
      this.m_SaveBut = new JButton("Save...");
      this.m_SaveBut.setToolTipText("Save the current configured object");
      this.m_SaveBut.setEnabled(true);
      this.m_SaveBut.addActionListener((ActionListener)new Object(this));
      this.m_okBut = new JButton("OK");
      this.m_okBut.setEnabled(true);
      this.m_okBut.addActionListener((ActionListener)new Object(this));
      this.m_cancelBut = new JButton("Cancel");
      this.m_cancelBut.setEnabled(true);
      this.m_cancelBut.addActionListener((ActionListener)new Object(this));
      setLayout(new BorderLayout());
      if (this$0.m_canChangeClassInDialog) {
        JButton jButton = this$0.createChooseClassButton();
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.add(jButton, "West");
        jPanel1.add(this.m_ClassNameLabel, "Center");
        add(jPanel1, "North");
      } else {
        add(this.m_ClassNameLabel, "North");
      } 
      add(this.m_ChildPropertySheet, "Center");
      JPanel jPanel = new JPanel();
      jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      jPanel.setLayout(new GridLayout(1, 4, 5, 5));
      jPanel.add(this.m_OpenBut);
      jPanel.add(this.m_SaveBut);
      jPanel.add(this.m_okBut);
      jPanel.add(this.m_cancelBut);
      add(jPanel, "South");
      if (this$0.m_ClassType != null) {
        this$0.m_ObjectNames = this$0.getClassesFromProperties();
        if (this$0.m_Object != null) {
          this$0.updateObjectNames();
          updateChildPropertySheet();
        } 
      } 
    }
    
    protected void setCancelButton(boolean param1Boolean) {
      if (this.m_cancelBut != null)
        this.m_cancelBut.setEnabled(param1Boolean); 
    }
    
    protected Object openObject() {
      if (this.m_FileChooser == null)
        createFileChooser(); 
      int i = this.m_FileChooser.showOpenDialog(this);
      if (i == 0) {
        File file = this.m_FileChooser.getSelectedFile();
        try {
          ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
          Object object = objectInputStream.readObject();
          objectInputStream.close();
          if (!this.this$0.m_ClassType.isAssignableFrom(object.getClass()))
            throw new Exception("Object not of type: " + this.this$0.m_ClassType.getName()); 
          return object;
        } catch (Exception exception) {
          JOptionPane.showMessageDialog(this, "Couldn't read object: " + file.getName() + "\n" + exception.getMessage(), "Open object file", 0);
        } 
      } 
      return null;
    }
    
    protected void saveObject(Object param1Object) {
      if (this.m_FileChooser == null)
        createFileChooser(); 
      int i = this.m_FileChooser.showSaveDialog(this);
      if (i == 0) {
        File file = this.m_FileChooser.getSelectedFile();
        try {
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
          objectOutputStream.writeObject(param1Object);
          objectOutputStream.close();
        } catch (Exception exception) {
          JOptionPane.showMessageDialog(this, "Couldn't write to file: " + file.getName() + "\n" + exception.getMessage(), "Save object", 0);
        } 
      } 
    }
    
    protected void createFileChooser() {
      this.m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
      this.m_FileChooser.setFileSelectionMode(0);
    }
    
    protected Object copyObject(Object param1Object) {
      Object object = null;
      try {
        SerializedObject serializedObject = new SerializedObject(param1Object);
        object = serializedObject.getObject();
        setCancelButton(true);
      } catch (Exception exception) {
        setCancelButton(false);
        System.err.println("GenericObjectEditor: Problem making backup object");
        System.err.println(exception);
      } 
      return object;
    }
    
    public void setOkButtonText(String param1String) {
      this.m_okBut.setText(param1String);
    }
    
    public void addOkListener(ActionListener param1ActionListener) {
      this.m_okBut.addActionListener(param1ActionListener);
    }
    
    public void addCancelListener(ActionListener param1ActionListener) {
      this.m_cancelBut.addActionListener(param1ActionListener);
    }
    
    public void removeOkListener(ActionListener param1ActionListener) {
      this.m_okBut.removeActionListener(param1ActionListener);
    }
    
    public void removeCancelListener(ActionListener param1ActionListener) {
      this.m_cancelBut.removeActionListener(param1ActionListener);
    }
    
    public void updateChildPropertySheet() {
      String str = "None";
      if (this.this$0.m_Object != null)
        str = this.this$0.m_Object.getClass().getName(); 
      this.m_ClassNameLabel.setText(str);
      this.m_ChildPropertySheet.setTarget(this.this$0.m_Object);
      if (getTopLevelAncestor() != null && getTopLevelAncestor() instanceof Window)
        ((Window)getTopLevelAncestor()).pack(); 
    }
  }
  
  public class JTreePopupMenu extends JPopupMenu {
    JTree m_tree;
    
    JScrollPane m_scroller;
    
    private final GenericObjectEditor this$0;
    
    public JTreePopupMenu(GenericObjectEditor this$0, JTree param1JTree) {
      this.this$0 = this$0;
      this.m_tree = param1JTree;
      JPanel jPanel = new JPanel();
      jPanel.setLayout(new BorderLayout());
      jPanel.add(this.m_tree, "North");
      jPanel.setBackground(this.m_tree.getBackground());
      this.m_scroller = new JScrollPane(jPanel);
      this.m_scroller.setPreferredSize(new Dimension(300, 400));
      add(this.m_scroller);
    }
    
    public void show(Component param1Component, int param1Int1, int param1Int2) {
      super.show(param1Component, param1Int1, param1Int2);
      Point point = getLocationOnScreen();
      Dimension dimension1 = getToolkit().getScreenSize();
      int i = (int)(dimension1.getWidth() - point.getX());
      int j = (int)(dimension1.getHeight() - point.getY());
      Dimension dimension2 = this.m_scroller.getPreferredSize();
      int k = (int)dimension2.getHeight();
      int m = (int)dimension2.getWidth();
      if (m > i)
        m = i; 
      if (k > j)
        k = j; 
      this.m_scroller.setPreferredSize(new Dimension(m, k));
      revalidate();
      pack();
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\GenericObjectEditor.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */