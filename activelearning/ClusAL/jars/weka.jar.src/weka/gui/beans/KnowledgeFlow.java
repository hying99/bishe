package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.Customizer;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.beancontext.BeanContextSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.UIManager;
import weka.core.Utils;
import weka.gui.GenericPropertiesCreator;
import weka.gui.HierarchyPropertyParser;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.visualize.PrintablePanel;

public class KnowledgeFlow extends JPanel implements PropertyChangeListener {
  protected static String PROPERTY_FILE = "weka/gui/beans/Beans.props";
  
  private static Properties BEAN_PROPERTIES;
  
  private static Vector TOOLBARS = new Vector();
  
  FontMetrics m_fontM;
  
  protected static final int NONE = 0;
  
  protected static final int MOVING = 1;
  
  protected static final int CONNECTING = 2;
  
  protected static final int ADDING = 3;
  
  private int m_mode = 0;
  
  private ButtonGroup m_toolBarGroup = new ButtonGroup();
  
  private Object m_toolBarBean;
  
  private BeanLayout m_beanLayout = new BeanLayout(this);
  
  private JTabbedPane m_toolBars = new JTabbedPane();
  
  private JToggleButton m_pointerB;
  
  private JButton m_saveB;
  
  private JButton m_loadB;
  
  private JButton m_stopB;
  
  private JButton m_helpB;
  
  private BeanInstance m_editElement;
  
  private EventSetDescriptor m_sourceEventSetDescriptor;
  
  private int m_oldX;
  
  private int m_oldY;
  
  private int m_startX;
  
  private int m_startY;
  
  protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
  
  protected LogPanel m_logPanel = new LogPanel(null, true);
  
  protected BeanContextSupport m_bcSupport = new BeanContextSupport();
  
  private static KnowledgeFlow m_knowledgeFlow;
  
  private static long m_initialJVMSize;
  
  public KnowledgeFlow() {
    JWindow jWindow = new JWindow();
    jWindow.show();
    jWindow.getGraphics().setFont(new Font("Monospaced", 0, 10));
    this.m_fontM = jWindow.getGraphics().getFontMetrics();
    jWindow.hide();
    this.m_bcSupport.setDesignTime(true);
    this.m_beanLayout.setLayout(null);
    this.m_beanLayout.addMouseListener(new MouseAdapter(this) {
          private final KnowledgeFlow this$0;
          
          public void mousePressed(MouseEvent param1MouseEvent) {
            if (this.this$0.m_toolBarBean == null && (param1MouseEvent.getModifiers() & 0x10) == 16 && this.this$0.m_mode == 0) {
              BeanInstance beanInstance = BeanInstance.findInstance(param1MouseEvent.getPoint());
              JComponent jComponent = null;
              if (beanInstance != null)
                jComponent = (JComponent)beanInstance.getBean(); 
              if (jComponent != null && jComponent instanceof Visible) {
                this.this$0.m_editElement = beanInstance;
                this.this$0.m_oldX = param1MouseEvent.getX();
                this.this$0.m_oldY = param1MouseEvent.getY();
                this.this$0.m_mode = 1;
              } 
            } 
          }
          
          public void mouseReleased(MouseEvent param1MouseEvent) {
            if (this.this$0.m_editElement != null && this.this$0.m_mode == 1) {
              this.this$0.m_editElement = null;
              this.this$0.revalidate();
              this.this$0.m_beanLayout.repaint();
              this.this$0.m_mode = 0;
            } 
          }
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            BeanInstance beanInstance = BeanInstance.findInstance(param1MouseEvent.getPoint());
            if (this.this$0.m_mode == 3 || this.this$0.m_mode == 0)
              if (beanInstance != null) {
                JComponent jComponent = (JComponent)beanInstance.getBean();
                if ((param1MouseEvent.getModifiers() & 0x10) != 16 || param1MouseEvent.isAltDown())
                  this.this$0.doPopup(param1MouseEvent.getPoint(), beanInstance, param1MouseEvent.getX(), param1MouseEvent.getY()); 
              } else if ((param1MouseEvent.getModifiers() & 0x10) != 16 || param1MouseEvent.isAltDown()) {
                byte b = 10;
                this.this$0.deleteConnectionPopup(BeanConnection.getClosestConnections(new Point(param1MouseEvent.getX(), param1MouseEvent.getY()), b), param1MouseEvent.getX(), param1MouseEvent.getY());
              } else if (this.this$0.m_toolBarBean != null) {
                this.this$0.addComponent(param1MouseEvent.getX(), param1MouseEvent.getY());
              }  
            if (this.this$0.m_mode == 2) {
              this.this$0.m_beanLayout.repaint();
              Vector vector = BeanInstance.getBeanInstances();
              byte b;
              for (b = 0; b < vector.size(); b++) {
                JComponent jComponent = (JComponent)((BeanInstance)vector.elementAt(b)).getBean();
                if (jComponent instanceof Visible)
                  ((Visible)jComponent).getVisual().setDisplayConnectors(false); 
              } 
              if (beanInstance != null) {
                b = 0;
                if (!(beanInstance.getBean() instanceof BeanCommon)) {
                  b = 1;
                } else if (((BeanCommon)beanInstance.getBean()).connectionAllowed(this.this$0.m_sourceEventSetDescriptor.getName())) {
                  b = 1;
                } 
                if (b != 0)
                  BeanConnection beanConnection = new BeanConnection(this.this$0.m_editElement, beanInstance, this.this$0.m_sourceEventSetDescriptor); 
                this.this$0.m_beanLayout.repaint();
              } 
              this.this$0.m_mode = 0;
              this.this$0.m_editElement = null;
              this.this$0.m_sourceEventSetDescriptor = null;
            } 
          }
        });
    this.m_beanLayout.addMouseMotionListener(new MouseMotionAdapter(this) {
          private final KnowledgeFlow this$0;
          
          public void mouseDragged(MouseEvent param1MouseEvent) {
            if (this.this$0.m_editElement != null && this.this$0.m_mode == 1) {
              ImageIcon imageIcon = ((Visible)this.this$0.m_editElement.getBean()).getVisual().getStaticIcon();
              int i = imageIcon.getIconWidth() / 2;
              int j = imageIcon.getIconHeight() / 2;
              this.this$0.m_editElement.setX(this.this$0.m_oldX - i);
              this.this$0.m_editElement.setY(this.this$0.m_oldY - j);
              this.this$0.m_beanLayout.repaint();
              this.this$0.m_oldX = param1MouseEvent.getX();
              this.this$0.m_oldY = param1MouseEvent.getY();
            } 
          }
          
          public void mouseMoved(MouseEvent param1MouseEvent) {
            if (this.this$0.m_mode == 2) {
              this.this$0.m_beanLayout.repaint();
              this.this$0.m_oldX = param1MouseEvent.getX();
              this.this$0.m_oldY = param1MouseEvent.getY();
            } 
          }
        });
    String str = (new SimpleDateFormat("EEEE, d MMMM yyyy")).format(new Date());
    this.m_logPanel.logMessage("Weka Knowledge Flow was written by Mark Hall");
    this.m_logPanel.logMessage("Weka Knowledge Flow");
    this.m_logPanel.logMessage("(c) 2002-2004 Mark Hall");
    this.m_logPanel.logMessage("web: http://www.cs.waikato.ac.nz/~ml/");
    this.m_logPanel.logMessage(str);
    this.m_logPanel.statusMessage("Welcome to the Weka Knowledge Flow");
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BorderLayout());
    jPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Knowledge Flow Layout"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    JScrollPane jScrollPane = new JScrollPane((Component)this.m_beanLayout);
    jPanel.add(jScrollPane, "Center");
    setLayout(new BorderLayout());
    add(jPanel, "Center");
    this.m_beanLayout.setSize(1024, 768);
    Dimension dimension = this.m_beanLayout.getPreferredSize();
    this.m_beanLayout.setMinimumSize(dimension);
    this.m_beanLayout.setMaximumSize(dimension);
    this.m_beanLayout.setPreferredSize(dimension);
    add((Component)this.m_logPanel, "South");
    setUpToolBars();
  }
  
  private Image loadImage(String paramString) {
    Image image = null;
    URL uRL = ClassLoader.getSystemResource(paramString);
    if (uRL != null)
      image = Toolkit.getDefaultToolkit().getImage(uRL); 
    return image;
  }
  
  private void setUpToolBars() {
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BorderLayout());
    JToolBar jToolBar1 = new JToolBar();
    jToolBar1.setOrientation(1);
    this.m_saveB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/Save24.gif")));
    this.m_saveB.setToolTipText("Save layout");
    this.m_loadB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/Open24.gif")));
    this.m_stopB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/Stop24.gif")));
    this.m_helpB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/Help24.gif")));
    this.m_stopB.setToolTipText("Stop all execution");
    this.m_loadB.setToolTipText("Load layout");
    this.m_helpB.setToolTipText("Display help");
    Image image = loadImage("weka/gui/beans/icons/Pointer.gif");
    this.m_pointerB = new JToggleButton(new ImageIcon(image));
    this.m_pointerB.addActionListener(new ActionListener(this) {
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_toolBarBean = null;
            this.this$0.m_mode = 0;
            this.this$0.setCursor(Cursor.getPredefinedCursor(0));
          }
        });
    this.m_toolBarGroup.add(this.m_pointerB);
    jToolBar1.add(this.m_pointerB);
    jToolBar1.add(this.m_saveB);
    jToolBar1.add(this.m_loadB);
    jToolBar1.add(this.m_stopB);
    Dimension dimension1 = this.m_saveB.getPreferredSize();
    Dimension dimension2 = this.m_saveB.getMaximumSize();
    jToolBar1.setFloatable(false);
    this.m_pointerB.setPreferredSize(dimension1);
    this.m_pointerB.setMaximumSize(dimension2);
    jPanel.add(jToolBar1, "West");
    JToolBar jToolBar2 = new JToolBar();
    jToolBar2.setOrientation(1);
    jToolBar2.setFloatable(false);
    jToolBar2.add(this.m_helpB);
    this.m_helpB.setPreferredSize(dimension1);
    this.m_helpB.setMaximumSize(dimension1);
    jPanel.add(jToolBar2, "East");
    this.m_saveB.addActionListener(new ActionListener(this) {
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveLayout();
          }
        });
    this.m_loadB.addActionListener(new ActionListener(this) {
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.loadLayout();
          }
        });
    this.m_stopB.addActionListener(new ActionListener(this) {
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            Vector vector = BeanInstance.getBeanInstances();
            for (byte b = 0; b < vector.size(); b++) {
              Object object = ((BeanInstance)vector.elementAt(b)).getBean();
              if (object instanceof BeanCommon)
                ((BeanCommon)object).stop(); 
            } 
          }
        });
    this.m_helpB.addActionListener(new ActionListener(this) {
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.popupHelp();
          }
        });
    boolean bool = false;
    for (byte b = 0; b < TOOLBARS.size(); b++) {
      Vector vector = TOOLBARS.elementAt(b);
      String str1 = vector.elementAt(0);
      Box box = null;
      String str2 = vector.elementAt(1);
      String str3 = "";
      HierarchyPropertyParser hierarchyPropertyParser = null;
      if (str2.compareTo("null") != 0) {
        Object object = null;
        bool = true;
        str3 = vector.elementAt(2);
        hierarchyPropertyParser = (HierarchyPropertyParser)vector.elementAt(3);
        try {
          Beans.instantiate(null, str2);
        } catch (Exception exception) {
          System.err.println("Failed to instantiate: " + str2);
          break;
        } 
      } else {
        bool = false;
      } 
      JToolBar jToolBar = new JToolBar();
      byte b1 = 2;
      if (bool == true) {
        if (!hierarchyPropertyParser.goTo(str3)) {
          System.err.println("**** Failed to locate root package in tree ");
          System.exit(1);
        } 
        String[] arrayOfString = hierarchyPropertyParser.childrenValues();
        for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
          hierarchyPropertyParser.goToChild(arrayOfString[b2]);
          if (hierarchyPropertyParser.isLeafReached()) {
            if (box == null) {
              box = Box.createHorizontalBox();
              box.setBorder(BorderFactory.createTitledBorder(str1));
            } 
            String str = hierarchyPropertyParser.fullValue();
            JPanel jPanel1 = instantiateToolBarBean(true, str2, str);
            if (jPanel1 != null)
              box.add(jPanel1); 
            hierarchyPropertyParser.goToParent();
          } else {
            Box box1 = Box.createHorizontalBox();
            box1.setBorder(BorderFactory.createTitledBorder(arrayOfString[b2]));
            processPackage(box1, str2, hierarchyPropertyParser);
            jToolBar.add(box1);
          } 
        } 
        if (box != null) {
          jToolBar.add(box);
          box = null;
        } 
      } else {
        Box box1 = Box.createHorizontalBox();
        box1.setBorder(BorderFactory.createTitledBorder(str1));
        for (byte b2 = b1; b2 < vector.size(); b2++) {
          JPanel jPanel1 = null;
          str2 = vector.elementAt(b2);
          jPanel1 = instantiateToolBarBean((bool == true), str2, "");
          if (jPanel1 != null)
            box1.add(jPanel1); 
        } 
        jToolBar.add(box1);
      } 
      JScrollPane jScrollPane = new JScrollPane(jToolBar, 21, 32);
      Dimension dimension = jToolBar.getPreferredSize();
      jScrollPane.setMinimumSize(new Dimension((int)dimension.getWidth(), (int)(dimension.getHeight() + 15.0D)));
      jScrollPane.setPreferredSize(new Dimension((int)dimension.getWidth(), (int)(dimension.getHeight() + 15.0D)));
      this.m_toolBars.addTab(str1, null, jScrollPane, str1);
    } 
    jPanel.add(this.m_toolBars, "Center");
    add(jPanel, "North");
  }
  
  private void processPackage(JComponent paramJComponent, String paramString, HierarchyPropertyParser paramHierarchyPropertyParser) {
    if (paramHierarchyPropertyParser.isLeafReached()) {
      String str = paramHierarchyPropertyParser.fullValue();
      JPanel jPanel = instantiateToolBarBean(true, paramString, str);
      if (jPanel != null)
        paramJComponent.add(jPanel); 
      paramHierarchyPropertyParser.goToParent();
      return;
    } 
    String[] arrayOfString = paramHierarchyPropertyParser.childrenValues();
    for (byte b = 0; b < arrayOfString.length; b++) {
      paramHierarchyPropertyParser.goToChild(arrayOfString[b]);
      processPackage(paramJComponent, paramString, paramHierarchyPropertyParser);
    } 
    paramHierarchyPropertyParser.goToParent();
  }
  
  private JPanel instantiateToolBarBean(boolean paramBoolean, String paramString1, String paramString2) {
    Object object1;
    JToggleButton jToggleButton;
    if (paramBoolean) {
      try {
        object1 = Beans.instantiate(null, paramString1);
      } catch (Exception exception) {
        System.err.println("Failed to instantiate :" + paramString1 + "KnowledgeFlow.instantiateToolBarBean()");
        return null;
      } 
      if (object1 instanceof WekaWrapper) {
        Class clazz = null;
        try {
          clazz = Class.forName(paramString2);
        } catch (Exception exception) {
          System.err.println("Can't find class called: " + paramString2);
          return null;
        } 
        try {
          Object object = clazz.newInstance();
          ((WekaWrapper)object1).setWrappedAlgorithm(object);
        } catch (Exception exception) {
          System.err.println("Failed to configure " + paramString1 + " with " + paramString2);
          return null;
        } 
      } 
    } else {
      try {
        object1 = Beans.instantiate(null, paramString1);
      } catch (Exception exception) {
        exception.printStackTrace();
        System.err.println("Failed to instantiate :" + paramString1 + "KnowledgeFlow.setUpToolBars()");
        return null;
      } 
    } 
    if (object1 instanceof java.beans.beancontext.BeanContextChild)
      this.m_bcSupport.add(object1); 
    if (object1 instanceof Visible)
      ((Visible)object1).getVisual().scale(3); 
    JPanel jPanel = new JPanel();
    JLabel jLabel = new JLabel();
    jLabel.setFont(new Font("Monospaced", 0, 10));
    String str1 = (paramBoolean == true) ? paramString2 : paramString1;
    str1 = str1.substring(str1.lastIndexOf('.') + 1, str1.length());
    jLabel.setText(" " + str1 + " ");
    jLabel.setHorizontalAlignment(0);
    jPanel.setLayout(new BorderLayout());
    if (object1 instanceof Visible) {
      BeanVisual beanVisual = ((Visible)object1).getVisual();
      jToggleButton = new JToggleButton(beanVisual.getStaticIcon());
      int i = beanVisual.getStaticIcon().getIconWidth();
      int j = beanVisual.getStaticIcon().getIconHeight();
      JPanel jPanel1 = multiLineLabelPanel(str1, i);
      jPanel.add(jPanel1, "South");
    } else {
      jToggleButton = new JToggleButton();
      jPanel.add(jLabel, "South");
    } 
    jPanel.add(jToggleButton, "North");
    this.m_toolBarGroup.add(jToggleButton);
    String str2 = paramString1;
    Object object2 = object1;
    jToggleButton.addActionListener(new ActionListener(this, str2, object2) {
          private final String val$tempName;
          
          private final Object val$tempBN;
          
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            try {
              this.this$0.m_toolBarBean = null;
              this.this$0.m_toolBarBean = Beans.instantiate(null, this.val$tempName);
              if (this.this$0.m_toolBarBean instanceof WekaWrapper) {
                Object object = ((WekaWrapper)this.val$tempBN).getWrappedAlgorithm();
                ((WekaWrapper)this.this$0.m_toolBarBean).setWrappedAlgorithm(object.getClass().newInstance());
              } 
              this.this$0.setCursor(Cursor.getPredefinedCursor(1));
              this.this$0.m_mode = 3;
            } catch (Exception exception) {
              System.err.println("Problem adding bean to data flow layout");
            } 
          }
        });
    String str3 = getGlobalInfo(object1);
    if (str3 != null) {
      int i = str3.indexOf('.');
      if (i != -1)
        str3 = str3.substring(0, i + 1); 
      jToggleButton.setToolTipText(str3);
    } 
    return jPanel;
  }
  
  private JPanel multiLineLabelPanel(String paramString, int paramInt) {
    JPanel jPanel = new JPanel();
    Vector vector = new Vector();
    int i = this.m_fontM.stringWidth(paramString);
    if (i < paramInt) {
      vector.addElement(paramString);
    } else {
      int j = paramString.length() / 2;
      int k = paramString.length();
      byte b1 = -1;
      for (byte b2 = 0; b2 < paramString.length(); b2++) {
        if (paramString.charAt(b2) < 'a' && Math.abs(j - b2) < k) {
          k = Math.abs(j - b2);
          b1 = b2;
        } 
      } 
      if (b1 != -1) {
        String str1 = paramString.substring(0, b1);
        String str2 = paramString.substring(b1, paramString.length());
        if (str1.length() > 1 && str2.length() > 1) {
          vector.addElement(str1);
          vector.addElement(str2);
        } else {
          vector.addElement(paramString);
        } 
      } else {
        vector.addElement(paramString);
      } 
    } 
    jPanel.setLayout(new GridLayout(vector.size(), 1));
    for (byte b = 0; b < vector.size(); b++) {
      JLabel jLabel = new JLabel();
      jLabel.setFont(new Font("Monospaced", 0, 10));
      jLabel.setText(" " + (String)vector.elementAt(b) + " ");
      jLabel.setHorizontalAlignment(0);
      jPanel.add(jLabel);
    } 
    return jPanel;
  }
  
  private void popupHelp() {
    JButton jButton = this.m_helpB;
    try {
      jButton.setEnabled(false);
      InputStream inputStream = ClassLoader.getSystemResourceAsStream("weka/gui/beans/README_KnowledgeFlow");
      StringBuffer stringBuffer = new StringBuffer();
      LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream));
      String str;
      while ((str = lineNumberReader.readLine()) != null)
        stringBuffer.append(str + "\n"); 
      lineNumberReader.close();
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      JTextArea jTextArea = new JTextArea(stringBuffer.toString());
      jTextArea.setFont(new Font("Monospaced", 0, 12));
      jTextArea.setEditable(false);
      JScrollPane jScrollPane = new JScrollPane(jTextArea);
      jFrame.getContentPane().add(jScrollPane, "Center");
      jFrame.addWindowListener(new WindowAdapter(this, jButton, jFrame) {
            private final JButton val$tempB;
            
            private final JFrame val$jf;
            
            private final KnowledgeFlow this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$tempB.setEnabled(true);
              this.val$jf.dispose();
            }
          });
      jFrame.setSize(600, 600);
      jFrame.setVisible(true);
    } catch (Exception exception) {
      jButton.setEnabled(true);
    } 
  }
  
  private void doPopup(Point paramPoint, BeanInstance paramBeanInstance, int paramInt1, int paramInt2) {
    JComponent jComponent = (JComponent)paramBeanInstance.getBean();
    int i = paramInt1;
    int j = paramInt2;
    byte b = 0;
    JPopupMenu jPopupMenu = new JPopupMenu();
    jPopupMenu.insert(new JLabel("Edit", 0), b);
    b++;
    JMenuItem jMenuItem = new JMenuItem("Delete");
    jMenuItem.addActionListener(new ActionListener(this, paramBeanInstance) {
          private final BeanInstance val$bi;
          
          private final KnowledgeFlow this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            BeanConnection.removeConnections(this.val$bi);
            this.val$bi.removeBean((JComponent)this.this$0.m_beanLayout);
            this.this$0.revalidate();
          }
        });
    jPopupMenu.add(jMenuItem);
    b++;
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(jComponent.getClass());
      if (beanInfo == null) {
        System.err.println("Error");
      } else {
        Class clazz = beanInfo.getBeanDescriptor().getCustomizerClass();
        if (clazz != null) {
          JMenuItem jMenuItem1 = new JMenuItem("Configure...");
          jMenuItem1.addActionListener(new ActionListener(this, clazz, jComponent) {
                private final Class val$custClass;
                
                private final JComponent val$bc;
                
                private final KnowledgeFlow this$0;
                
                public void actionPerformed(ActionEvent param1ActionEvent) {
                  this.this$0.popupCustomizer(this.val$custClass, this.val$bc);
                }
              });
          jPopupMenu.add(jMenuItem1);
          b++;
        } else {
          System.err.println("No customizer class");
        } 
        EventSetDescriptor[] arrayOfEventSetDescriptor = beanInfo.getEventSetDescriptors();
        if (arrayOfEventSetDescriptor != null && arrayOfEventSetDescriptor.length > 0) {
          jPopupMenu.insert(new JLabel("Connections", 0), b);
          b++;
        } 
        for (byte b1 = 0; b1 < arrayOfEventSetDescriptor.length; b1++) {
          JMenuItem jMenuItem1 = new JMenuItem(arrayOfEventSetDescriptor[b1].getName());
          EventSetDescriptor eventSetDescriptor = arrayOfEventSetDescriptor[b1];
          boolean bool = true;
          if (jComponent instanceof EventConstraints)
            bool = ((EventConstraints)jComponent).eventGeneratable(eventSetDescriptor.getName()); 
          if (bool) {
            jMenuItem1.addActionListener(new ActionListener(this, eventSetDescriptor, paramBeanInstance, i, j) {
                  private final EventSetDescriptor val$esd;
                  
                  private final BeanInstance val$bi;
                  
                  private final int val$xx;
                  
                  private final int val$yy;
                  
                  private final KnowledgeFlow this$0;
                  
                  public void actionPerformed(ActionEvent param1ActionEvent) {
                    this.this$0.connectComponents(this.val$esd, this.val$bi, this.val$xx, this.val$yy);
                  }
                });
          } else {
            jMenuItem1.setEnabled(false);
          } 
          jPopupMenu.add(jMenuItem1);
          b++;
        } 
      } 
    } catch (IntrospectionException introspectionException) {
      introspectionException.printStackTrace();
    } 
    if (jComponent instanceof UserRequestAcceptor) {
      Enumeration enumeration = ((UserRequestAcceptor)jComponent).enumerateRequests();
      if (enumeration.hasMoreElements()) {
        jPopupMenu.insert(new JLabel("Actions", 0), b);
        b++;
      } 
      while (enumeration.hasMoreElements()) {
        String str1 = enumeration.nextElement();
        boolean bool = false;
        if (str1.charAt(0) == '$') {
          str1 = str1.substring(1, str1.length());
          bool = true;
        } 
        String str2 = str1;
        JMenuItem jMenuItem1 = new JMenuItem(str2);
        jMenuItem1.addActionListener(new ActionListener(this, jComponent, str2) {
              private final JComponent val$bc;
              
              private final String val$tempS2;
              
              private final KnowledgeFlow this$0;
              
              public void actionPerformed(ActionEvent param1ActionEvent) {
                ((UserRequestAcceptor)this.val$bc).performRequest(this.val$tempS2);
              }
            });
        if (bool)
          jMenuItem1.setEnabled(false); 
        jPopupMenu.add(jMenuItem1);
        b++;
      } 
    } 
    if (b > 0)
      jPopupMenu.show((Component)this.m_beanLayout, paramInt1, paramInt2); 
  }
  
  private void popupCustomizer(Class paramClass, JComponent paramJComponent) {
    try {
      Customizer customizer = (Customizer)paramClass.newInstance();
      ((Customizer)customizer).setObject(paramJComponent);
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add((JComponent)customizer, "Center");
      if (customizer instanceof CustomizerCloseRequester)
        ((CustomizerCloseRequester)customizer).setParentFrame(jFrame); 
      jFrame.addWindowListener(new WindowAdapter(this, customizer, jFrame) {
            private final Object val$customizer;
            
            private final JFrame val$jf;
            
            private final KnowledgeFlow this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              if (this.val$customizer instanceof CustomizerClosingListener)
                ((CustomizerClosingListener)this.val$customizer).customizerClosing(); 
              this.val$jf.dispose();
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  private void deleteConnectionPopup(Vector paramVector, int paramInt1, int paramInt2) {
    if (paramVector.size() > 0) {
      byte b1 = 0;
      JPopupMenu jPopupMenu = new JPopupMenu();
      jPopupMenu.insert(new JLabel("Delete Connection", 0), b1);
      b1++;
      for (byte b2 = 0; b2 < paramVector.size(); b2++) {
        BeanConnection beanConnection = paramVector.elementAt(b2);
        String str = beanConnection.getSourceEventSetDescriptor().getName();
        JMenuItem jMenuItem = new JMenuItem(str);
        jMenuItem.addActionListener(new ActionListener(this, beanConnection) {
              private final BeanConnection val$bc;
              
              private final KnowledgeFlow this$0;
              
              public void actionPerformed(ActionEvent param1ActionEvent) {
                this.val$bc.remove();
                this.this$0.m_beanLayout.revalidate();
                this.this$0.m_beanLayout.repaint();
              }
            });
        jPopupMenu.add(jMenuItem);
        b1++;
      } 
      jPopupMenu.show((Component)this.m_beanLayout, paramInt1, paramInt2);
    } 
  }
  
  private void connectComponents(EventSetDescriptor paramEventSetDescriptor, BeanInstance paramBeanInstance, int paramInt1, int paramInt2) {
    this.m_sourceEventSetDescriptor = paramEventSetDescriptor;
    Class clazz = paramEventSetDescriptor.getListenerType();
    JComponent jComponent = (JComponent)paramBeanInstance.getBean();
    byte b1 = 0;
    Vector vector = BeanInstance.getBeanInstances();
    for (byte b2 = 0; b2 < vector.size(); b2++) {
      JComponent jComponent1 = (JComponent)((BeanInstance)vector.elementAt(b2)).getBean();
      boolean bool = false;
      if (clazz.isInstance(jComponent1) && jComponent1 != jComponent) {
        if (!(jComponent1 instanceof BeanCommon)) {
          bool = true;
        } else if (((BeanCommon)jComponent1).connectionAllowed(paramEventSetDescriptor.getName())) {
          bool = true;
        } 
        if (bool && jComponent1 instanceof Visible) {
          b1++;
          ((Visible)jComponent1).getVisual().setDisplayConnectors(true);
        } 
      } 
    } 
    if (b1 > 0) {
      if (jComponent instanceof Visible)
        ((Visible)jComponent).getVisual().setDisplayConnectors(true); 
      this.m_editElement = paramBeanInstance;
      Point point = ((Visible)jComponent).getVisual().getClosestConnectorPoint(new Point(paramInt1, paramInt2));
      this.m_startX = (int)point.getX();
      this.m_startY = (int)point.getY();
      this.m_oldX = this.m_startX;
      this.m_oldY = this.m_startY;
      Graphics2D graphics2D = (Graphics2D)this.m_beanLayout.getGraphics();
      graphics2D.setXORMode(Color.white);
      graphics2D.drawLine(this.m_startX, this.m_startY, this.m_startX, this.m_startY);
      graphics2D.dispose();
      this.m_mode = 2;
    } 
  }
  
  private void addComponent(int paramInt1, int paramInt2) {
    if (this.m_toolBarBean instanceof java.beans.beancontext.BeanContextChild)
      this.m_bcSupport.add(this.m_toolBarBean); 
    BeanInstance beanInstance = new BeanInstance((JComponent)this.m_beanLayout, this.m_toolBarBean, paramInt1, paramInt2);
    if (this.m_toolBarBean instanceof Visible)
      ((Visible)this.m_toolBarBean).getVisual().addPropertyChangeListener(this); 
    if (this.m_toolBarBean instanceof BeanCommon)
      ((BeanCommon)this.m_toolBarBean).setLog((Logger)this.m_logPanel); 
    this.m_toolBarBean = null;
    setCursor(Cursor.getPredefinedCursor(0));
    this.m_beanLayout.repaint();
    this.m_pointerB.setSelected(true);
    this.m_mode = 0;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    revalidate();
    this.m_beanLayout.repaint();
  }
  
  private void loadLayout() {
    this.m_loadB.setEnabled(false);
    this.m_saveB.setEnabled(false);
    int i = this.m_FileChooser.showOpenDialog(this);
    if (i == 0)
      try {
        File file = this.m_FileChooser.getSelectedFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Vector vector = (Vector)objectInputStream.readObject();
        Vector vector1 = (Vector)objectInputStream.readObject();
        objectInputStream.close();
        Color color = getBackground();
        this.m_bcSupport = new BeanContextSupport();
        this.m_bcSupport.setDesignTime(true);
        for (byte b = 0; b < vector.size(); b++) {
          BeanInstance beanInstance = vector.elementAt(b);
          if (beanInstance.getBean() instanceof Visible) {
            ((Visible)beanInstance.getBean()).getVisual().addPropertyChangeListener(this);
            ((Visible)beanInstance.getBean()).getVisual().setBackground(color);
            ((JComponent)beanInstance.getBean()).setBackground(color);
          } 
          if (beanInstance.getBean() instanceof BeanCommon)
            ((BeanCommon)beanInstance.getBean()).setLog((Logger)this.m_logPanel); 
          if (beanInstance.getBean() instanceof java.beans.beancontext.BeanContextChild)
            this.m_bcSupport.add(beanInstance.getBean()); 
        } 
        BeanInstance.setBeanInstances(vector, (JComponent)this.m_beanLayout);
        BeanConnection.setConnections(vector1);
        this.m_beanLayout.revalidate();
        this.m_beanLayout.repaint();
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
    this.m_loadB.setEnabled(true);
    this.m_saveB.setEnabled(true);
  }
  
  private void saveLayout() {
    this.m_loadB.setEnabled(false);
    this.m_saveB.setEnabled(false);
    int i = this.m_FileChooser.showSaveDialog(this);
    Color color = getBackground();
    if (i == 0) {
      Vector vector = BeanInstance.getBeanInstances();
      for (byte b = 0; b < vector.size(); b++) {
        BeanInstance beanInstance = vector.elementAt(b);
        if (beanInstance.getBean() instanceof Visible) {
          ((Visible)beanInstance.getBean()).getVisual().removePropertyChangeListener(this);
          ((Visible)beanInstance.getBean()).getVisual().setBackground(Color.white);
          ((JComponent)beanInstance.getBean()).setBackground(Color.white);
        } 
      } 
      try {
        File file = this.m_FileChooser.getSelectedFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(vector);
        objectOutputStream.writeObject(BeanConnection.getConnections());
        objectOutputStream.flush();
        objectOutputStream.close();
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        for (byte b1 = 0; b1 < vector.size(); b1++) {
          BeanInstance beanInstance = vector.elementAt(b1);
          if (beanInstance.getBean() instanceof Visible) {
            ((Visible)beanInstance.getBean()).getVisual().addPropertyChangeListener(this);
            ((Visible)beanInstance.getBean()).getVisual().setBackground(color);
            ((JComponent)beanInstance.getBean()).setBackground(color);
          } 
        } 
      } 
    } 
    this.m_saveB.setEnabled(true);
    this.m_loadB.setEnabled(true);
  }
  
  public static String getGlobalInfo(Object paramObject) {
    String str = null;
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(paramObject.getClass());
      MethodDescriptor[] arrayOfMethodDescriptor = beanInfo.getMethodDescriptors();
      for (byte b = 0; b < arrayOfMethodDescriptor.length; b++) {
        String str1 = arrayOfMethodDescriptor[b].getDisplayName();
        Method method = arrayOfMethodDescriptor[b].getMethod();
        if (str1.equals("globalInfo") && method.getReturnType().equals(String.class)) {
          Object[] arrayOfObject = new Object[0];
          String str2 = (String)method.invoke(paramObject, arrayOfObject);
          str = str2;
          break;
        } 
      } 
    } catch (Exception exception) {}
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception exception) {}
    try {
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      m_knowledgeFlow = new KnowledgeFlow();
      jFrame.getContentPane().add(m_knowledgeFlow, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
      Thread thread = new Thread(jFrame) {
          private final JFrame val$jf;
          
          public void run() {
            while (true) {
              try {
                this;
                sleep(4000L);
                System.gc();
                if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() < KnowledgeFlow.m_initialJVMSize + 200000L) {
                  this.val$jf.dispose();
                  KnowledgeFlow.m_knowledgeFlow = null;
                  System.gc();
                  Thread[] arrayOfThread = new Thread[Thread.activeCount()];
                  Thread.enumerate(arrayOfThread);
                  for (byte b = 0; b < arrayOfThread.length; b++) {
                    Thread thread = arrayOfThread[b];
                    if (thread != null && thread != Thread.currentThread())
                      if (thread.getName().startsWith("Thread")) {
                        thread.stop();
                      } else if (thread.getName().startsWith("AWT-EventQueue")) {
                        thread.stop();
                      }  
                  } 
                  arrayOfThread = null;
                  JOptionPane.showMessageDialog(null, "Not enough memory. Please load a smaller dataset or use larger heap size.", "OutOfMemory", 2);
                  System.err.println("displayed message");
                  System.err.println("Not enough memory. Please load a smaller dataset or use larger heap size.");
                  System.err.println("exiting");
                  System.exit(-1);
                } 
              } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
              } 
            } 
          }
        };
      thread.setPriority(5);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    try {
      BEAN_PROPERTIES = Utils.readProperties(PROPERTY_FILE);
      Enumeration enumeration = BEAN_PROPERTIES.propertyNames();
      if (!enumeration.hasMoreElements())
        throw new Exception("Could not read a configuration file for the bean\npanel. An example file is included with the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\" and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n"); 
      String str = BEAN_PROPERTIES.getProperty("weka.gui.beans.KnowledgeFlow.standardToolBars");
      StringTokenizer stringTokenizer = new StringTokenizer(str, ", ");
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken().trim();
        Vector vector = new Vector();
        vector.addElement(str1);
        vector.addElement("null");
        String str2 = BEAN_PROPERTIES.getProperty("weka.gui.beans.KnowledgeFlow." + str1);
        StringTokenizer stringTokenizer1 = new StringTokenizer(str2, ", ");
        while (stringTokenizer1.hasMoreTokens()) {
          String str3 = stringTokenizer1.nextToken().trim();
          vector.addElement(str3);
        } 
        TOOLBARS.addElement(vector);
      } 
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(null, exception.getMessage(), "KnowledgeFlow", 0);
    } 
    try {
      GenericPropertiesCreator genericPropertiesCreator = new GenericPropertiesCreator();
      genericPropertiesCreator.execute(false);
      Properties properties = genericPropertiesCreator.getOutputProperties();
      Enumeration enumeration = properties.propertyNames();
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        String str2 = BEAN_PROPERTIES.getProperty(str1);
        if (str2 != null) {
          Vector vector = new Vector();
          String str3 = BEAN_PROPERTIES.getProperty(str1 + ".alias");
          String str4 = (str3 != null) ? str3 : str1.substring(str1.lastIndexOf('.') + 1, str1.length());
          vector.addElement(str4);
          vector.addElement(str2);
          String str5 = str1.substring(0, str1.lastIndexOf('.'));
          vector.addElement(str5);
          String str6 = properties.getProperty(str1);
          HierarchyPropertyParser hierarchyPropertyParser = new HierarchyPropertyParser();
          hierarchyPropertyParser.build(str6, ", ");
          vector.addElement(hierarchyPropertyParser);
          StringTokenizer stringTokenizer = new StringTokenizer(str6, ", ");
          while (stringTokenizer.hasMoreTokens()) {
            String str = stringTokenizer.nextToken().trim();
            vector.addElement(str);
          } 
          TOOLBARS.addElement(vector);
        } 
      } 
    } catch (Exception exception) {
      JOptionPane.showMessageDialog(null, "Could not read a configuration file for the generic objecte editor. An example file is included with the Weka distribution.\nThis file should be named \"GenericObjectEditor.props\" and\nshould be placed either in your user home (which is set\nto \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "KnowledgeFlow", 0);
    } 
  }
  
  protected class BeanLayout extends PrintablePanel {
    private final KnowledgeFlow this$0;
    
    protected BeanLayout(KnowledgeFlow this$0) {
      this.this$0 = this$0;
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      BeanInstance.paintLabels(param1Graphics);
      BeanConnection.paintConnections(param1Graphics);
      if (this.this$0.m_mode == 2)
        param1Graphics.drawLine(this.this$0.m_startX, this.this$0.m_startY, this.this$0.m_oldX, this.this$0.m_oldY); 
    }
    
    public void doLayout() {
      super.doLayout();
      Vector vector = BeanInstance.getBeanInstances();
      for (byte b = 0; b < vector.size(); b++) {
        BeanInstance beanInstance = vector.elementAt(b);
        JComponent jComponent = (JComponent)beanInstance.getBean();
        Dimension dimension = jComponent.getPreferredSize();
        jComponent.setBounds(beanInstance.getX(), beanInstance.getY(), dimension.width, dimension.height);
        jComponent.revalidate();
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\KnowledgeFlow.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */